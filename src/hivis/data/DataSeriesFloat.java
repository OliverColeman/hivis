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
import java.util.List;
import java.util.Random;

import hivis.data.view.CalcSeries;

/**
 * Data series storing single-precision floating-point numbers (represented as float for efficiency).
 * 
 * @author O. J. Coleman
 */
public class DataSeriesFloat extends AbstractDataSeries<Float> {
	protected float[] elements;
	int size;
	
	
	public DataSeriesFloat() {
        this(10);
	}
	
	public DataSeriesFloat(int capacity) {
		super();
        elements = new float[capacity];
		size = 0;
	}
	
	public DataSeriesFloat(float... data) {
		super();
        elements = Arrays.copyOf(data, data.length);
		size = data.length;
	}
	
	public DataSeriesFloat(DataSeriesFloat series) {
		super();
        elements = Arrays.copyOf(series.elements, series.size);
		size = series.size;
	}
	
	
	@Override
	public Float get(int index) {
		return getFloat(index);
	}
	
	@Override
	public float getFloat(int index) {
		if (index < 0 || index >= size) {
			return Float.NaN;
		}
		return elements[index];
	}
	
	@Override
	public synchronized int length() {
		return size;
	}

	@Override
	public synchronized void setValue(int index, Float value) {
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
	public synchronized void appendValue(Float value) {
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
	public Float getEmptyValue() {
		return Float.NaN;
	}
	
	/**
	 * Set the data for this series. This replaces all previous data.
	 * @param data The new data for the series. Copied by reference (thus the data should NOT be modified externally after calling this method).
	 */
	public void setData(float[] data) {
		elements = data;
		size = data.length;
		this.setDataChanged(DataSeriesChange.ValuesChanged);
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
	public float[] asFloatArray(float[] data) {
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
	public float[] getDataRef() {
		return elements;
	}

	@Override
	public DataSeriesFloat getNewSeries() {
		return new DataSeriesFloat();
	}

//	@Override
//	public List<Double> asList() {
//		return Arrays.asList(elements);
//	}
}
