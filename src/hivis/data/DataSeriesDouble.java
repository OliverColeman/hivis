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
import java.util.List;
import java.util.Random;

import hivis.data.DataSeriesInteger.Sorted;
import hivis.data.view.AbstractSeriesViewMultiple;
import hivis.data.view.CalcSeries;
import hivis.data.view.SeriesView;
import hivis.data.view.SortedSeries;

/**
 * Data series storing double-precision floating-point numbers (represented as double for efficiency).
 * 
 * @author O. J. Coleman
 */
public class DataSeriesDouble extends AbstractModifiableDataSeries<Double> {
	protected double[] elements;
	int size;
	
	
	public DataSeriesDouble() {
        this(10);
	}
	
	public DataSeriesDouble(int capacity) {
		super();
        elements = new double[capacity];
		size = 0;
	}
	
	public DataSeriesDouble(double... data) {
		super();
        elements = Arrays.copyOf(data, data.length);
		size = data.length;
	}
	
	public DataSeriesDouble(DataSeriesDouble series) {
		super();
        elements = Arrays.copyOf(series.elements, series.size);
		size = series.size;
	}
	
	
	@Override
	public Double get(int index) {
		return getDouble(index);
	}
	
	@Override
	public double getDouble(int index) {
		if (index < 0 || index >= size) {
			return Double.NaN;
		}
		return elements[index];
	}
	
	@Override
	public int length() {
		return size;
	}

	@Override
	public void setValue(int index, Double value) {
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
	public void appendValue(Double value) {
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
	public Double getEmptyValue() {
		return Double.NaN;
	}
	
	/**
	 * Set the data for this series. This replaces all previous data.
	 * @param data The new data for the series. Copied by reference (thus the data should NOT be modified externally after calling this method).
	 */
	public void setData(double[] data) {
		elements = data;
		size = data.length;
		this.setDataChanged(DataSeriesChange.ValuesChanged);
	}
	
	@Override
	public void resize(int newLength) {
		resize(newLength, getEmptyValue());
	}
	
	@Override
	public void resize(int newLength, Double padValue) {
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
	public double[] asDoubleArray(double[] data) {
		if (data == null || data.length < size) {
			return Arrays.copyOf(elements, size);
		}
		System.arraycopy(elements, 0, data, 0, size);
		return data;
	}

	/**
	 * Returns a reference to the underlying data array. 
	 * This method is provided for improved efficiency. 
	 * <strong>The array should never be modified.</strong>
	 */
	public double[] getDataRef() {
		return elements;
	}

	@Override
	public DataSeriesDouble getNewSeries() {
		return new DataSeriesDouble();
	}
	
	
	@Override
	public DataSeriesDouble copy() {
		return new DataSeriesDouble(this);
	}
	

	@Override
	public SeriesView<Double> sort() {
		return new Sorted(this);
	}
	
	@Override
	public SeriesView<Double> sort(Comparator<Double> comparator) {
		return new Sorted(this, comparator);
	}
	
	/**
	 * Subclass of SortedSeries optimised for doubles.
	 */
	public static class Sorted extends SortedSeries<Double> {
		DataSeriesDouble cache = new DataSeriesDouble();
		public Sorted(DataSeries<Double> source) { super(source); }
		public Sorted(DataSeries<Double> source, Comparator<Double> comp) { super(source, comp); }
		@Override
		public void update(DataEvent cause) {
			super.update(cause);
			cache.elements = Arrays.stream(elements).mapToDouble(i->i).toArray();
			cache.size = cache.elements.length;
		}
		@Override
		public double getDouble(int index) {
			if (recalc) update(null);
			return cache.getDouble(index);
		}
		@Override
		public double[] asDoubleArray() {
			if (recalc) update(null);
			return cache.asDoubleArray();
		}
	}

//	@Override
//	public List<Double> asList() {
//		return Arrays.asList(elements);
//	}
}
