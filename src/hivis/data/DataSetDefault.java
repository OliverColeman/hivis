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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Default base class for any object representing a set of data. For example a vector, table or graph.
 * It provides some basic data storage (name, container DataSet) as well as a framework for handling
 * changes to DataSets (see {@link #beginChanges(Object)}, {@link #setDataChanged(Object)} and 
 * {@link #finishChanges(Object)}.
 * 
 * @author O. J. Coleman
 */
public class DataSetDefault implements DataSet {
	private Set<DataSet> containers = new HashSet<>();
	private Set<DataSet> containersUnmod = Collections.unmodifiableSet(containers);
	
	private List<DataListener> changeListeners = new ArrayList<>();
	
	// A list is used for currentChangers so that duplicates may occur.
	// This in order to handle cases such as a process changing a DataTable 
	// calling beginChanges() on the DataTable, and then beginChanges() on the
	// individual DataSeries of the DataTable (resulting in more calls to
	// beginChanges on the container DataTable) before calling finishChanges on the
	// DataSeries and then the DataTable. In each call to finishChanges only one
	// of the occurrences of the DataTable will be removed.
	private List<Object> currentChangers = new ArrayList<>();
	private Set<Object> changeTypes = new HashSet<>();
	
	
	public DataSetDefault() {
	}
	
	public DataSetDefault(DataSet container) {
		addContainer(container);
	}

	@Override
	public Set<DataSet> getContainers() {
		return containersUnmod;
	}
	
	@Override
	public void addContainer(DataSet container) {
		if (container == null) throw new IllegalArgumentException("Container to add may not be null.");
		containers.add(container);
	}
	
	@Override
	public void removeContainer(DataSet container) {
		containers.remove(container);
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
		for (DataListener listener : changeListeners) {
			listener.dataChanged(event);
		}
	}
	
	
	/**
	 * All subclasses should call this method whenever the data is modified in some way.
	 * This method should not generally be called by non-subclasses.
	 * If a container is set then the change will be registered with it too.
	 * If no object has registered that it is making multiple changes via {@link DataSetDefault#beginChanges(Object)}
	 * then a DataChangeEvent will be fired (see {@link #addChangeListener(DataListener)}).  
	 */
	@Override
	public synchronized void setDataChanged(Object changeType) {
		changeTypes.add(changeType);
		
		for (DataSet c : containers) {
			if (c == null) System.out.println("null");

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
	public void beginChanges(Object changer) {
		currentChangers.add(changer);
		
		for (DataSet c : containers) {
			c.beginChanges(changer);
		}
	}
	
	@Override
	public void finishChanges(Object changer) {
		if (!currentChangers.contains(changer)) {
			throw new IllegalStateException("The given object, of type '" + changer.getClass().getCanonicalName() + "', is not listed as making changes to this DataSet ('" + this.getClass().getCanonicalName() + "').");
		}
		
		// Note: if the changer is listed multiple times in currentChangers 
		// then only the first instance will be removed. This is what we want.
		// For example a process changing a DataTable may call beginChanges() 
		// on the DataTable, and then beginChanges() on the individual DataSeries 
		// of the DataTable before calling finishChanges on the DataTable.
		currentChangers.remove(changer);
		if (currentChangers.isEmpty()) {
			fireChangeEvent();
		}
		
		for (DataSet c : containers) {
			c.finishChanges(changer);
		}
	}
	
	@Override
	public List<Object> getCurrentChangers() {
		return Collections.unmodifiableList(currentChangers);
	}
}
