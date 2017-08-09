/**
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA
 */

package hivis.data;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Default base class for any object representing data via a {@link Data}. For example a series, table or graph.
 * It provides some basic data storage (name, container DataSet) as well as a framework for handling
 * changes to DataSets (see {@link #beginChanges(Object)}, {@link #setDataChanged(Object)} and 
 * {@link #finishChanges(Object)}.
 * 
 * @author O. J. Coleman
 */
public abstract class DataDefault implements Data {
	private Set<Data> containers = new HashSet<>();
	private Set<Data> containersUnmod = Collections.unmodifiableSet(containers);
	
	private Set<Data> contained = new HashSet<>();
	
	private List<DataListener> changeListeners = new ArrayList<>();
	
	// A list is used for currentChangers so that duplicates may occur.
	// This in order to handle cases such as a process changing a DataTable 
	// calling beginChanges() on the DataTable, and then beginChanges() on the
	// individual DataSeries of the DataTable (resulting in more calls to
	// beginChanges on the container DataTable) before calling finishChanges on the
	// DataSeries and then the DataTable. In each call to finishChanges only one
	// of the occurrences of the DataTable will be removed.
	private Deque<Object> currentChangers = new ArrayDeque<>();
	private Deque<Collection<Data>> containersAtChanger = new ArrayDeque<>();
	
	private Set<Object> changeTypes = new HashSet<>();
	
	
	public DataDefault() {
	}
	
	public DataDefault(Data container) {
		addContainer(container);
	}
	
	
	@Override
	public Set<Data> getContainers() {
		return containersUnmod;
	}
	
	@Override
	public void addContainer(Data container) {
		if (container == null) throw new IllegalArgumentException("Container to add may not be null.");
		containers.add(container);
		if (container instanceof DataDefault) {
			((DataDefault) container).contained.add(this);
		}
	}
	
	@Override
	public void removeContainer(Data container) {
		containers.remove(container);
		if (container instanceof DataDefault) {
			((DataDefault) container).contained.remove(this);
		}
	}
	
	
	@Override
	public void addChangeListener(DataListener listener) {
		changeListeners.add(listener);
	}

	
	@Override
	public void removeChangeListener(DataListener listener) {
		changeListeners.remove(listener);
	}

	
	/**
	 * Notifies all change listeners of a change event.
	 */
	private synchronized void fireChangeEvent() {
		if (!changeTypes.isEmpty()) {
			DataEvent event = new DataEvent(this, changeTypes.toArray());
			fireChangeEvent(event);
			changeTypes.clear();
		}
	}
	
	/**
	 * Notifies all change listeners of a change event.
	 */
	protected synchronized void fireChangeEvent(DataEvent event) {
		for (int i = 0; i < changeListeners.size(); i++) {
			changeListeners.get(i).dataChanged(event);
		}
	}
	
	
	/**
	 * All subclasses should call this method whenever the data is modified in some way.
	 * This method should not generally be called by non-subclasses.
	 * If a container is set then the change will be registered with it too.
	 * If no object has registered that it is making multiple changes via {@link DataDefault#beginChanges(Object)}
	 * then a DataChangeEvent will be fired (see {@link #addChangeListener(DataListener)}).  
	 */
	@Override
	public void setDataChanged(Object changeType) {
		changeTypes.add(changeType);
		
		// In the process of notifying containers that data has changed, 
		// containers may be added/removed to/from the set of containers. 
		// These  new containers do not need to be notified that data has changed. 
		// To avoid ConcurrentModificationExceptions while iterating over the 
		// containers collection we iterate over an array copy of the current set.
		for (Data c : containers.toArray(new Data[containers.size()])) {
			c.setDataChanged(changeType);
		}
		
		if (currentChangers.isEmpty()) {
			fireChangeEvent();
		}
	}
	
	@Override
	public boolean hasDataChanged() {
		return !changeTypes.isEmpty();
	}
	
	@Override
	public boolean changeInProgress() {
		return !currentChangers.isEmpty();
	}
	
	@Override
	public void beginChanges(Object changer) {
		lock();

		currentChangers.push(changer);
		containersAtChanger.push(new ArrayList<>(containers)); 
		
		for (Data c : containersAtChanger.peek()) {
			c.beginChanges(changer);
		}
	}
	
	@Override
	public void finishChanges(Object changer) {
		if (currentChangers.peek() != changer) {
			throw new IllegalStateException("The given object, of type '" + changer.getClass().getCanonicalName() + "', is not listed as the most recent to be making changes to this DataSet ('" + this.getClass().getCanonicalName() + "').");
		}
		
		// Note: if the changer is listed multiple times in currentChangers 
		// then only the last instance will be removed. This is what we want.
		// For example a process changing a DataTable may call beginChanges() 
		// on the DataTable, and then beginChanges() on the individual DataSeries 
		// of the DataTable before calling finishChanges on the DataTable.
		currentChangers.pop();
		Collection<Data> containersAtThisChanger = containersAtChanger.pop();
		
		if (currentChangers.isEmpty()) {
			fireChangeEvent();
		}
		
		for (Data c : containersAtThisChanger) {
			c.finishChanges(changer);
		}
		
//		// Unlock the contained Data sets, if any.
//		for (Data c : contained) {
//			c.unlock();
//		}
		unlock();
	}
	
	@Override
	public List<Object> getCurrentChangers() {
		return Collections.unmodifiableList(new ArrayList<>(currentChangers));
	}
	
	@Override
	public void lock() {
		throw new UnsupportedOperationException("This Data set (" + getClassName() + ") does not implement lock! This probably means you found a bug.");
	}
	
	@Override
	public void unlock() {
		throw new UnsupportedOperationException("This Data set (" + getClassName() + ") does not implement unlock. This probably means you found a bug.");
	}
	
	private String getClassName() {
		Class<?> clazz = this.getClass();
		String out = clazz.getName();
		if (clazz.isAnonymousClass()) {
			out += " (super class is " + clazz.getSuperclass().getName() + ")";
		}
		return out;
	}
	
	/**
	 * Default implementation that always returns true. Immutable Data sets must override this to return false;
	 */
	@Override
	public boolean isMutable() {
		return true;
	}

	/**
	 * <p>
	 * Final implementation of equals that returns true if the given object is
	 * this object (the default Object.equals method behaviour), or returns true
	 * if this Data set is immutable and the given object is an immutable Data
	 * set (see {@link #isMutable()}) and this.equalTo(o) returns true,
	 * otherwise returns false.
	 * </p>
	 * <p>
	 * Rationale: in most cases the equals method would be overridden to be
	 * based on the values stored by the Data set. When overriding the equals
	 * method the hashCode method should also be overridden to be consistent
	 * with it. However if the hash code for a Data set changes over time and it
	 * is used as the key object in a hash table-based data structure (which is
	 * the case in numerous locations throughout the HiVis library) then
	 * subsequent accesses of the hash table with the same Data set will not
	 * behave consistently. Thus - to avoid the likely introduction of bugs
	 * caused by either a hash code method inconsistent with the equals method
	 * or a hash code that changes over time - we prevent using the equals
	 * method for testing Data set value equality.
	 * </p>
	 * <p>
	 * Use {@link #equalTo(Data)} instead.
	 * </p>
	 */
	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o instanceof Data && !this.isMutable() && !((Data) o).isMutable()) {
			return this.equalTo((Data) o);
		}
		return false;
	}
	
	/**
	 * Final implementation of hashCode() that returns the same value as the
	 * default Object.hashCode() method if this Data set {@link #isMutable()}
	 * otherwise returns {@link #equalToHashCode(Data)}. See
	 * {@link #equals(Object)} for rationale.
	 */
	@Override
	public final int hashCode() {
		if (isMutable())
			return System.identityHashCode(this);
		return this.equalToHashCode();
	}
}
