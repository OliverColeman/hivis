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

import hivis.common.Util;

/**
 * Data series storing generic Objects.
 * 
 * @author O. J. Coleman
 */
public class DataValueGeneric<V> extends AbstractModifiableDataValue<V> {
	protected V value;
	
	
	public DataValueGeneric() {
	}
	
	public DataValueGeneric(V value) {
		this.value = value;
	}
	
	
	@Override
	public V get() {
		return value;
	}
	
	@Override
	public void setValue(V value) {
		if (!Util.equalsIncNull(this.value, value)) {
			this.value = value;
			this.setDataChanged(null);
		}
	}

	@Override
	public V getEmptyValue() {
		return null;
	}
	
	@Override
	public DataValueGeneric<V> getNewDataValue() {
		return new DataValueGeneric<V>();
	}
}
