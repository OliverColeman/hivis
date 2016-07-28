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

import hivis.common.Event;

/**
 * Stores data about events that occur for a set of data. 
 * 
 * @author O. J. Coleman
 */
public class DataEvent extends Event<DataSet, Object, DataEvent> {
	public DataEvent(DataSet affected, DataEvent sourceEvent, Object... types) {
		super(affected, sourceEvent, types);
	}
	
	public DataEvent(DataSet affected, Object... types) {
		super(affected, types);
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " " + types;
	}
}
