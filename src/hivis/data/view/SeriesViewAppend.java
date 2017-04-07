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

package hivis.data.view;

import hivis.data.DataEvent;
import hivis.data.DataSeries;

/**
 * A view over two or more series appended one after the other.
 * 
 * @author O. J. Coleman
 */
public class SeriesViewAppend<V> extends AbstractSeriesView<V, V> {
	/**
	 * Create a new ViewSeriesAppend that appends the given input series in the order given.
	 */
	public SeriesViewAppend(DataSeries<V>... input) {
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
	public synchronized boolean getBoolean(int index) {
		for (int seriesIndex = 0; seriesIndex < inputSeries.size(); seriesIndex++) {
			int len = inputSeries.get(seriesIndex).length();
			if (index < len) return inputSeries.get(seriesIndex).getBoolean(index);
			index -= len;
		}
		return (boolean) (Boolean) getEmptyValue();
	}

	@Override
	public synchronized int getInt(int index) {
		for (int seriesIndex = 0; seriesIndex < inputSeries.size(); seriesIndex++) {
			int len = inputSeries.get(seriesIndex).length();
			if (index < len) return inputSeries.get(seriesIndex).getInt(index);
			index -= len;
		}
		return (int) (Integer) getEmptyValue();
	}

	@Override
	public synchronized long getLong(int index) {
		for (int seriesIndex = 0; seriesIndex < inputSeries.size(); seriesIndex++) {
			int len = inputSeries.get(seriesIndex).length();
			if (index < len) return inputSeries.get(seriesIndex).getLong(index);
			index -= len;
		}
		return (long) (Long) getEmptyValue();
	}

	@Override
	public synchronized double getDouble(int index) {
		for (int seriesIndex = 0; seriesIndex < inputSeries.size(); seriesIndex++) {
			int len = inputSeries.get(seriesIndex).length();
			if (index < len) return inputSeries.get(seriesIndex).getDouble(index);
			index -= len;
		}
		return (double) (Double) getEmptyValue();
	}

	@Override
	public void update(DataEvent cause) {
		// Nothing to do, view is not cached.
	}
}
