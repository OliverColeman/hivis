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
 * Data series storing longeger numbers (represented as primitive long for efficiency).
 * 
 * @author O. J. Coleman
 */
public class DataValueLong extends AbstractModifiableDataValue<Long> {
	protected long value;
	
	
	public DataValueLong() {
	}
	
	public DataValueLong(long value) {
		this.value = value;
	}
	
	
	@Override
	public Long get() {
		return getLong();
	}
	
	@Override
	public long getLong() {
		return value;
	}
	
	@Override
	public void setValue(Long value) {
		if (this.value != value) {
			this.value = value;
			this.setDataChanged(null);
		}
	}

	@Override
	public Long getEmptyValue() {
		return Long.MIN_VALUE;
	}
	
	@Override
	public DataValueLong getNewDataValue() {
		return new DataValueLong();
	}
}
