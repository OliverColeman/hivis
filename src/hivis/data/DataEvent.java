/**
 * 
 */
package hivis.data;

import hivis.common.Event;

/**
 * Stores data about events that occur for a set of data. 
 * 
 * @author O. J. Coleman
 */
public class DataEvent extends Event<DataSet, Object, DataEvent> {
	public DataEvent(DataSet affected, DataEvent sourceEvent, Object[] types) {
		super(affected, sourceEvent, types);
	}
	
	public DataEvent(DataSet affected, Object[] types) {
		super(affected, types);
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " " + types;
	}
}
