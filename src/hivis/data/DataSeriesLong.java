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

import java.util.Arrays;
import java.util.Random;

/**
 * Data series storing long numbers (represented as long for efficiency).
 * 
 * @author O. J. Coleman
 */
public class DataSeriesLong extends AbstractDataSeries<Long> {
	protected long[] elements;
	int size;
	
	
	public DataSeriesLong() {
        this(10);
	}
	
	public DataSeriesLong(int capacity) {
		super();
        elements = new long[capacity];
		size = 0;
	}
	
	public DataSeriesLong(long[] data) {
		super();
        elements = Arrays.copyOf(data, data.length);
		size = data.length;
	}
	
	public DataSeriesLong(DataSeriesLong series) {
		super();
        elements = Arrays.copyOf(series.elements, series.size);
		size = series.size;
	}
	
	
	@Override
	public Long get(int index) {
		return getLong(index);
	}
	
	@Override
	public long getLong(int index) {
		if (index < 0 || index >= size) {
			return 0;
		}
		return elements[index];
	}
	
	@Override
	public synchronized int length() {
		return size;
	}

	@Override
	public synchronized void setValue(int index, Long value) {
		try {
			if (elements[index] != value) {
				elements[index] = value;
				this.setDataChanged(DataSeriesChange.ValuesChanged);
			}
		}
		catch (ArrayIndexOutOfBoundsException ex) {
			throw new IndexOutOfBoundsException();
		}
	}

	@Override
	public synchronized void appendValue(Long value) {
		if (elements.length == size) {
			elements = Arrays.copyOf(elements, (int) (size * 1.5) + 1);
		}
		elements[size] = value;
		size++;
		this.setDataChanged(DataSeriesChange.ValuesAdded);
	}

	@Override
	public synchronized void remove(int index) {
		try {
			for (int i = index; i < size; i++) {
				elements[i] = elements[i+1];
			}
		}
		catch (ArrayIndexOutOfBoundsException ex) {
			throw new IndexOutOfBoundsException();
		}
		size--;
		this.setDataChanged(DataSeriesChange.ValuesRemoved);
	}

	@Override
	public Long getEmptyValue() {
		return Long.MIN_VALUE;
	}
	
	@Override
	public long[] asLongArray(long[] data) {
		if (data == null || data.length < size) {
			return Arrays.copyOf(elements, size);
		}
		System.arraycopy(elements, 0, data, 0, size);
		return data;
	}
	
	@Override
	public void resize(int newLength) {
		if (newLength < size) {
			size = newLength;
			this.setDataChanged(DataSeriesChange.ValuesRemoved);
		}
		else if (newLength > size) {
			elements = Arrays.copyOf(elements, newLength);
			size = newLength;
			this.setDataChanged(DataSeriesChange.ValuesAdded);
		}
	}

	@Override
	public DataSeriesLong getNewSeries() {
		return new DataSeriesLong();
	}
}
