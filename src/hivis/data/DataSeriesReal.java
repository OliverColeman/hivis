/**
 * 
 */
package hivis.data;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import hivis.data.view.CalcSeries;

/**
 * Data series storing real numbers (represented as double for efficiency).
 * 
 * @author O. J. Coleman
 */
public class DataSeriesReal extends AbstractDataSeries<Double> {
	protected double[] elements;
	int size;
	
	
	public DataSeriesReal() {
        this(10);
	}
	
	public DataSeriesReal(int capacity) {
		super();
        elements = new double[capacity];
		size = 0;
	}
	
	public DataSeriesReal(double... data) {
		super();
        elements = Arrays.copyOf(data, data.length);
		size = data.length;
	}
	
	public DataSeriesReal(DataSeriesReal series) {
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
	public synchronized int length() {
		return size;
	}

	@Override
	public synchronized void setValue(int index, Double value) {
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
	public synchronized void appendValue(Double value) {
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
	public DataSeriesReal getNewSeries() {
		return new DataSeriesReal();
	}

//	@Override
//	public List<Double> asList() {
//		return Arrays.asList(elements);
//	}
}
