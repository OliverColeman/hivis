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

import java.util.List;
import java.util.Set;

/**
 * Interface for any object representing data. For example a series, table or graph.
 * It provides some basic data storage (name, container DataSet) as well as a framework for handling
 * changes to DataSets (see {@link #beginChanges(Object)}, {@link #setDataChanged(Object)} and 
 * {@link #finishChanges(Object)}.
 * 
 * @author O. J. Coleman
 */
public interface Data {
	/**
	 * Returns true iff the data represented by this Data set is mutable (either directly via setter methods or indirectly via other processes).
	 * <strong>The result of this method should not change over time</strong>.
	 */
	boolean isMutable();
	
	/**
	 * Return true iff this Data set represents the same data as the given Data set.
	 */
	boolean equalTo(Data data);
	
	/**
	 * Return a hash code consistent with {@link #equalTo(Data)}.
	 */
	int equalToHashCode();
	
	/**
	 * Get the DataSets that "contain" this DataSet as part of their own data. For example a {@link DataTable} contains {@link DataSeries}.
	 */
	Set<Data> getContainers();

	/**
	 * Adds the given DataSet as a container for this DataSet, or if it has already been added makes no changes.
	 * Containers are notified of changes that occur to this DataSet via the {@link #setDataChanged(Object)} 
	 * method.
	 */
	void addContainer(Data container);
	
	/**
	 * Removes the given DataSet as a container for this DataSet.
	 */
	void removeContainer(Data container);

	/**
	 * Registers an object for notification of changes to the DataSet.
	 *
	 * @param listener the object to register.
	 */
	void addChangeListener(DataListener listener);

	/**
	 * De-registers an object for notification of changes to the DataSet.
	 *
	 * @param listener the object to de-register.
	 */
	void removeChangeListener(DataListener listener);
	
	/**
	 * Notify this DataSet that changes are about to be made to it.
	 * {@link #lock()} should generally be called at the beginning of this method.
	 * 
	 * @param changer The object making the changes.
	 */
	void beginChanges(Object changer);
	
	/**
	 * All implementations should call this method whenever the data is modified in some way.
	 * If any containers are set then the change will be registered with them too (see {@link #addContainer(Data)}).
	 * If no object has registered that it is making multiple changes via {@link Data#beginChanges(Object)}
	 * then a DataChangeEvent will be fired (see {@link #addChangeListener(DataListener)}).  
	 */
	void setDataChanged(Object changeType);

	/**
	 * Returns true iff the data has been changed but a change event has not yet been fired. 
	 */
	boolean hasDataChanged();

	/**
	 * Returns true iff any <em>changers</em> have indicated that they will modify the data.
	 * 
	 * @see #beginChanges(Object)
	 */
	boolean changeInProgress();

	/**
	 * Notify this DataSet that a set of changes to the data have been completed. 
	 * If the data was changed and no other objects are modifying the data then a
	 * DataChangeEvent will be fired.
	 * {@link #unlock()} should generally be called at the end of this method.
	 */
	void finishChanges(Object changer);

	/**
	 * Get the list of objects that are currently changing the data, if any.
	 * A list is used for getCurrentChangers() so that duplicates may occur.
	 * This in order to handle cases such as a process changing a DataTable 
	 * calling beginChanges() on the DataTable, and then beginChanges() on the
	 * individual DataSeries of the DataTable (resulting in more calls to
	 * beginChanges on the container DataTable) before calling finishChanges on the
	 * DataSeries and then the DataTable. In each call to finishChanges only one
	 * of the occurrences of the DataTable will be removed.
	 * @see #beginChanges(Object)
	 */
	List<Object> getCurrentChangers();
	
	void lock();
	
	void unlock();
	
	Data immutableCopy();
}