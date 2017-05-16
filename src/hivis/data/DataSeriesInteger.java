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
import java.util.Comparator;
import java.util.Random;

import hivis.data.view.AbstractSeriesViewMultiple;
import hivis.data.view.SeriesView;
import hivis.data.view.SortedSeries;

/**
 * Data series storing integer numbers (represented as int for efficiency).
 * 
 * @author O. J. Coleman
 */
public class DataSeriesInteger extends AbstractModifiableDataSeries<Integer> implements DataSeries.IntSeries {
	protected int[] elements;
	int size;
	
	
	public DataSeriesInteger() {
        this(10);
	}
	
	public DataSeriesInteger(int capacity) {
		super();
        elements = new int[capacity];
		size = 0;
	}
	
	public DataSeriesInteger(int[] data) {
		super();
        elements = Arrays.copyOf(data, data.length);
		size = data.length;
	}
	
	public DataSeriesInteger(DataSeriesInteger series) {
		super();
        elements = Arrays.copyOf(series.elements, series.size);
		size = series.size;
	}
	
	
	@Override
	public Integer get(int index) {
		return getInt(index);
	}
	
	@Override
	public int getInt(int index) {
		if (index < 0 || index >= size) {
			return 0;
		}
		return elements[index];
	}
	
	@Override
	public int length() {
		return size;
	}

	@Override
	public void setValue(int index, Integer value) {
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
	public void appendValue(Integer value) {
		if (elements.length == size) {
			elements = Arrays.copyOf(elements, (int) (size * 1.5) + 1);
		}
		elements[size] = value;
		size++;
		this.setDataChanged(DataSeriesChange.ValuesAdded);
	}

	@Override
	public void remove(int index) {
		try {
			for (int i = index; i < size-1; i++) {
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
	public Integer getEmptyValue() {
		return Integer.MIN_VALUE;
	}
	
	@Override
	public int[] asIntArray(int[] data) {
		if (data == null || data.length < size) {
			return Arrays.copyOf(elements, size);
		}
		System.arraycopy(elements, 0, data, 0, size);
		return data;
	}
	
	@Override
	public void resize(int newLength) {
		resize(newLength, getEmptyValue());
	}
	
	@Override
	public void resize(int newLength, Integer padValue) {
		if (newLength < size) {
			size = newLength;
			this.setDataChanged(DataSeriesChange.ValuesRemoved);
		}
		else if (newLength > size) {
			elements = Arrays.copyOf(elements, newLength);
			Arrays.fill(elements, size, newLength, padValue);
			size = newLength;
			this.setDataChanged(DataSeriesChange.ValuesAdded);
		}
	}

	@Override
	public DataSeriesInteger getNewSeries() {
		return new DataSeriesInteger();
	}
	
	@Override
	public DataSeriesInteger copy() {
		return new DataSeriesInteger(this);
	}
	
	@Override
	public SeriesView<Integer> sort() {
		return new Sorted(this);
	}
	
	@Override
	public SeriesView<Integer> sort(Comparator<Integer> comparator) {
		return new Sorted(this, comparator);
	}
	
	/**
	 * Subclass of SortedSeries optimised for ints.
	 */
	public static class Sorted extends SortedSeries<Integer> {
		DataSeriesInteger cache = new DataSeriesInteger();
		public Sorted(DataSeries<Integer> source) { super(source); }
		public Sorted(DataSeries<Integer> source, Comparator<Integer> comp) { super(source, comp); }
		@Override
		public void update(DataEvent cause) {
			super.update(cause);
			cache.elements = Arrays.stream(elements).mapToInt(i->i).toArray();
			cache.size = cache.elements.length;
		}
		@Override
		public int getInt(int index) {
			if (recalc) update(null);
			return cache.getInt(index);
		}
		@Override
		public int[] asIntArray() {
			if (recalc) update(null);
			return cache.asIntArray();
		}
	}
}
