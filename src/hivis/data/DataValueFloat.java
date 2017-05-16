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
 * Data series storing single-precision floating-point numbers (represented as primitive float for efficiency).
 * 
 * @author O. J. Coleman
 */
public class DataValueFloat extends AbstractModifiableDataValue<Float> {
	protected float value;
	
	
	public DataValueFloat() {
	}
	
	public DataValueFloat(float value) {
		this.value = value;
	}
	
	
	@Override
	public Float get() {
		return getFloat();
	}
	
	@Override
	public float getFloat() {
		return value;
	}
	
	@Override
	public void setValue(Float value) {
		if (this.value != value) {
			this.value = value;
			this.setDataChanged(null);
		}
	}

	@Override
	public Float getEmptyValue() {
		return Float.NaN;
	}
	
	@Override
	public DataValueFloat getNewDataValue() {
		return new DataValueFloat();
	}
}
