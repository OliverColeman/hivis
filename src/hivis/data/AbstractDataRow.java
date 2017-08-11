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


import hivis.common.LSListMap;
import hivis.common.ListMap;
import hivis.common.Util;

/**
 * Base class for implementations of DataRow.
 * 
 * @author O. J. Coleman
 */
public abstract class AbstractDataRow extends DataDefault implements DataRow {
	// Cache for equalToHashCode().
	protected int equalToHashCode = 0;
	
	
	@Override
	public Class<?> getType(int index) {
		Object v = get(index);
		if (v == null) return Object.class;
		return v.getClass();
	}

	@Override
	public Class<?> getType(String label) {
		Object v = get(label);
		if (v == null) return Object.class;
		return v.getClass();
	}

	@Override
	public boolean isNumeric(int index) {
		return Number.class.isAssignableFrom(getType(index));
	}

	@Override
	public boolean isNumeric(String label) {
		return Number.class.isAssignableFrom(getType(label));
	}

	@Override
	public boolean getBoolean(String label) {
		return (boolean) (Boolean) get(label);
	}

	@Override
	public int getInt(String label) {
		return ((Number) get(label)).intValue();
	}

	@Override
	public long getLong(String label) {
		return ((Number) get(label)).longValue();
	}

	@Override
	public float getFloat(String label) {
		return ((Number) get(label)).floatValue();
	}

	@Override
	public double getDouble(String label) {
		return ((Number) get(label)).doubleValue();
	}

	@Override
	public String getString(String label) {
		return get(label) + "";
	}

	@Override
	public boolean getBoolean(int index) {
		return (boolean) (Boolean) get(index);
	}

	@Override
	public int getInt(int index) {
		return ((Number) get(index)).intValue();
	}

	@Override
	public long getLong(int index) {
		return ((Number) get(index)).longValue();
	}

	@Override
	public float getFloat(int index) {
		return ((Number) get(index)).floatValue();
	}

	@Override
	public double getDouble(int index) {
		return ((Number) get(index)).doubleValue();
	}

	@Override
	public String getString(int index) {
		return get(index) + "";
	}

	@Override
	public DataRow immutableCopy() {
		if (!this.isMutable()) return this;
		
		try {
			lock();
			final int rowIndex = this.getRowIndex();
			final ListMap<String, Object> data = new LSListMap<>();
			for (int i = 0; i < length(); i++) {
				data.put(getLabel(i), get(i));
			}
			
			return new AbstractDataRow() {
				@Override
				public String getLabel(int index) {
					return data.keySet().get(index);
				}
				
				@Override
				public int getRowIndex() {
					return rowIndex;
				}
	
				@Override
				public Object get(String label) {
					if (!data.containsKey(label)) throw new IllegalArgumentException("The given column, " + label + ", does not exist in the DataRow.");
					return data.get(label);
				}
	
				@Override
				public Object get(int index) {
					if (index < 0 || index >= data.size()) throw new IndexOutOfBoundsException("The given index, " + index + ", is out of bounds (row length is " + data.size() + ").");
					return data.get(index);
				}
	
				@Override
				public int length() {
					return data.size();
				}
			};
		}
		finally {
			unlock();
		}
	}


	@Override
	public boolean equalTo(Data data) {
		if (data == this) return true;
		if (!(data instanceof DataRow)) return false;
		DataRow row = (DataRow) data;
		try {
			this.lock();
			try {
				row.lock();
				if (this.length() != row.length()) return false;
				for (int i = 0; i < length(); i++) {
					if (!Util.equalsIncData(get(i), row.get(i))) {
						return false;
					}
				}
				return true;
			}
			finally {
				row.unlock();
			}
		}
		finally {
			this.unlock();
		}
	}
	
	@Override
	public int equalToHashCode() {
		if (isMutable()) {
			throw new IllegalStateException("equalToHashCode() called on a mutable DataRow.");
		}
		if (equalToHashCode == 0) {
			equalToHashCode = 1;
			for (int s = 0; s < length(); s++) {
				equalToHashCode = 31 * equalToHashCode + get(s).hashCode();
			}
		}
		return equalToHashCode;
	}
}
