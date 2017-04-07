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
import hivis.data.DataSeriesChange;

/**
 * A DataSeries view that allows filtering out and/or rearranging the rows in an input series.
 *
 * @author O. J. Coleman
 */
public class SeriesViewRow<V> extends AbstractSeriesView<V, V> {
	/**
	 * Mapping from the row indices in this Series to the input series.
	 * <code>rowMap[view_series_index] = input_series_index<code>.
	 */
	protected int[] rowMap;

	
	/**
	 * Create a new SeriesViewRow with no rows selected.
	 * @param input The input DataSeries.
	 */
	public SeriesViewRow(DataSeries<V> input) {
		super(input);
		rowMap = new int[0];
	}
	
	/**
	 * Create a new SeriesViewRow with the given row mapping.
	 * @param input The input DataSeries.
	 * @param map The mapping from the row indices in this View to the input series:
	 * 			<code>map[view_series_index] = input_series_index<code>.
	 * 			The given array should not be changed after calling this constructor.
	 */
	public SeriesViewRow(DataSeries<V> input, int... map) {
		super(input);
		rowMap = new int[0];
		setRowMap(map);
	}
	
	/**
	 * Set the row mapping.
	 * @param map The mapping from the row indices in this View to the input series:
	 * 			<code>map[view_series_index] = input_series_index<code>. 
	 * 			The given array should not be changed after calling this method.
	 */
	public synchronized void setRowMap(int... map) {
		// See if any value changes result from the change, and check the new indices.
		boolean changedValues = false;
		
		for (int i = 0; i < map.length; i++) {
			if (map[i] < 0 || map[i] >= inputSeries().length()) {
				//throw new IllegalArgumentException("New row map for SeriesViewRow contains an index less than 0 or greater than the length of the input series (" + inputSeries().length() + "): " + map[i]);
				continue;
			}
			
			// See if a value has changed (if we haven't already established this).
			if (!changedValues && i < rowMap.length) {
				if (!get(map[i]).equals(get(rowMap[i]))) {
					changedValues = true;
				}
			}
		}
		
		boolean valuesAdded = map.length > rowMap.length;
		boolean valuesRemoved = map.length < rowMap.length;
		
		rowMap = map;
		
		if (changedValues) {
			setDataChanged(DataSeriesChange.ValuesChanged);
		}
		
		if (valuesAdded) {
			setDataChanged(DataSeriesChange.ValuesAdded);
		}
		else if (valuesRemoved) {
			setDataChanged(DataSeriesChange.ValuesRemoved);
		}
	}
	
	
	/**
	 * Convenience method to get a reference to the input series.
	 */
	public DataSeries<V> inputSeries() {
		return inputSeries.get(0);
	}
	
	@Override
	public int length() {
		return rowMap.length;
	}

	@Override
	public V getEmptyValue() {
		return inputSeries().getEmptyValue();
	}

	@Override
	public V get(int index) {
		if (index < 0 || index >= rowMap.length) {
			return getEmptyValue();
		}
		return inputSeries().get(rowMap[index]);
	}
	

	@Override
	public boolean getBoolean(int index) {
		if (index < 0 || index >= rowMap.length) {
			return (Boolean) getEmptyValue();
		}
		return inputSeries().getBoolean(rowMap[index]);
	}

	@Override
	public int getInt(int index) {
		if (index < 0 || index >= rowMap.length) {
			return (Integer) getEmptyValue();
		}
		return inputSeries().getInt(rowMap[index]);
	}

	@Override
	public long getLong(int index) {
		if (index < 0 || index >= rowMap.length) {
			return (Long) getEmptyValue();
		}
		return inputSeries().getLong(rowMap[index]);
	}

	@Override
	public double getDouble(int index) {
		if (index < 0 || index >= rowMap.length) {
			return (Double) getEmptyValue();
		}
		return inputSeries().getDouble(rowMap[index]);
	}
	
	
	@Override
	public void dataChanged(DataEvent event) {
		// Override dataChanged as we don't want to blindly forward on the change events from the input series.
		// Regardless of how the input series was changed we just forward a ValuesChanged event. This is because
		// the length of this series will not change as a result of any change to the input series;
		// if values were removed from the input series then get(int) will return getEmptyValue() for those indices.
		// (If values were added then it may be the case that it does not affect this view, but only if values were 
		// not previously removed, and which values were previously removed).
		
		if (inputSeries() == event.affected) {
			this.fireChangeEvent(new DataEvent(this, event, DataSeriesChange.ValuesChanged));
		}
	}

	@Override
	public void update(DataEvent cause) {
		// Nothing to do, view is not cached.
	}
}
