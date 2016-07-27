/**
 * 
 */
package hivis.data.view;

import com.google.common.reflect.TypeToken;

import hivis.data.DataSeries;

/**
 * A view over two or more series appended one after the other.
 * 
 * @author O. J. Coleman
 */
public class ViewSeriesAppend<V> extends ViewSeries<V, V> {
	/**
	 * Create a new ViewSeriesAppend that appends the given input series in the order given.
	 */
	public ViewSeriesAppend(DataSeries<V>... input) {
		super(input);
	}
	
	@Override
	public int length() {
		int length = 0;
		for (DataSeries<V> s : inputSeries) {
			length += s.length();
		}
		return length;
	}
	@Override
	public V getEmptyValue() {
		return inputSeries.get(0).getEmptyValue();
	}
	@Override
	public Class<?> getType() {
		return inputSeries.get(0).getType();
	}
	@Override
	public synchronized V get(int index) {
		for (int seriesIndex = 0; seriesIndex < inputSeries.size(); seriesIndex++) {
			int len = inputSeries.get(seriesIndex).length();
			if (index < len) return inputSeries.get(seriesIndex).get(index);
			index -= len;
		}
		return getEmptyValue();
	}
	
	@Override
	public void setValue(int index, V value) {
		throw new UnsupportedOperationException("Can not set values in a series view.");
	}
	@Override
	public void appendValue(V value) {
		throw new UnsupportedOperationException("Can not append values to a series view.");
	}
	@Override
	public void remove(int index) {
		throw new UnsupportedOperationException("Can not remove values from a series view.");
	}
	@Override
	public void resize(int newLength) {
		throw new UnsupportedOperationException("Can not resize a series view.");
	}
}
