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

/**
 * Data series storing integer numbers (represented as primitive int for efficiency).
 * 
 * @author O. J. Coleman
 */
public class DataValueInteger extends AbstractModifiableDataValue<Integer> {
	protected int value;
	
	
	public DataValueInteger() {
	}
	
	public DataValueInteger(int value) {
		this.value = value;
	}
	
	
	@Override
	public Integer get() {
		return getInt();
	}
	
	@Override
	public int getInt() {
		return value;
	}
	
	@Override
	public void setValue(Integer value) {
		if (this.value != value) {
			this.value = value;
			this.setDataChanged(null);
		}
	}

	@Override
	public Integer getEmptyValue() {
		return Integer.MIN_VALUE;
	}
	
	@Override
	public DataValueInteger getNewDataValue() {
		return new DataValueInteger();
	}
}
