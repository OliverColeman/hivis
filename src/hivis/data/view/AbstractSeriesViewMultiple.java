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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import hivis.data.AbstractUnmodifiableDataSeries;
import hivis.data.Data;
import hivis.data.DataEvent;
import hivis.data.DataListener;
import hivis.data.DataMap;
import hivis.data.DataSeries;
import hivis.data.DataTable;
import hivis.data.DataValue;

/**
 * Base class for creating {@link DataSeries} that are based on other
 * DataSeries, and/or a DataValue, or a DataTable, or a DataMap. At minimum
 * sub-classes must implement {@link #get(int)}, and {@link #update(DataEvent)},
 * which will be called whenever one of the input data sets change.
 * {@link CalcSeries} provides the infrastructure for a cached view that
 * simplifies implementations of calculated views.
 * 
 * @author O. J. Coleman
 *
 */
public abstract class AbstractSeriesViewMultiple<I, O> extends AbstractSeriesView<O> implements DataListener {
	private int length = Integer.MIN_VALUE;

	/**
	 * The (optional) input series on which this view is based. Null if no input
	 * series are used.
	 */
	public final List<DataSeries<I>> inputSeries;

	/**
	 * The (optional) input value on which this view is based. Null if no input
	 * value is used.
	 */
	public final DataValue<?> inputValue;

	/**
	 * The (optional) input table on which this view is based. Null if no input
	 * table is used.
	 */
	public final DataTable inputTable;

	/**
	 * The (optional) input map on which this view is based. Null if no input
	 * map is used.
	 */
	public final DataMap<?, ?> inputMap;

	
	
	/**
	 * Create a ViewSeries for the given input series.
	 */
	public AbstractSeriesViewMultiple(DataSeries<I>... input) {
		super(input[0]);
		inputSeries = Collections.unmodifiableList(Arrays.asList(Arrays.copyOf(input, input.length)));
		for (DataSeries<I> s : inputSeries) {
			s.addChangeListener(this);
		}
		inputValue = null;
		inputTable = null;
		inputMap = null;
	}

	/**
	 * Create a ViewSeries for the given value (may be null) and input series,
	 * with length equal to the (first) input series.
	 */
	public AbstractSeriesViewMultiple(DataValue<?> value, DataSeries<I>... input) {
		super(input[0]);
		inputSeries = Collections.unmodifiableList(Arrays.asList(Arrays.copyOf(input, input.length)));
		for (DataSeries<I> s : inputSeries) {
			s.addChangeListener(this);
		}
		inputValue = value;
		inputTable = null;
		inputMap = null;
		value.addChangeListener(this);
	}

	/**
	 * Create a ViewSeries for the given table.
	 */
	public AbstractSeriesViewMultiple(DataTable input) {
		super(input);
		input.addChangeListener(this);
		inputTable = input;
		inputSeries = null;
		inputValue = null;
		inputMap = null;
	}

	/**
	 * Create a ViewSeries for the given map.
	 */
	public AbstractSeriesViewMultiple(DataMap<?, ?> input) {
		super(input);
		input.addChangeListener(this);
		inputMap = input;
		inputSeries = null;
		inputValue = null;
		inputTable = null;
	}

	/**
	 * Create a SeriesViewFunction function with the given length.
	 */
	public AbstractSeriesViewMultiple(int length) {
		this.length = length;
		inputSeries = null;
		inputValue = null;
		inputTable = null;
		inputMap = null;
	}

	/**
	 * Create a ViewSeries that is not based on any input and with the length manually calculated.
	 */
	public AbstractSeriesViewMultiple() {
		inputSeries = null;
		inputValue = null;
		inputTable = null;
		inputMap = null;
	}
	

	/**
	 * Get the list of input series for this view, or null if no input series
	 * are used.
	 */
	public List<DataSeries<I>> getInputSeries() {
		return inputSeries;
	}

	/**
	 * Get the specified input series for this view.
	 */
	public DataSeries<I> getInputSeries(int index) {
		return inputSeries.get(index);
	}

	@Override
	public int length() {
		if (length >= 0) {
			return length;
		}
		if (inputSeries != null && !inputSeries.isEmpty()) {
			return inputSeries.get(0).length();
		}
		if (inputTable != null) {
			return inputTable.length();
		}
		if (inputMap != null) {
			return inputMap.size();
		}

		throw new RuntimeException("If a SeriesViewFunction has no input series, table or map then either the length() method must be overridden or the length field set to provide the length of the calculated series.");
	}

	@Override
	public O getEmptyValue() {
		return getNewSeries().getEmptyValue();
	}
	
	/**
	 * Sub-classes may override this to return false if change
	 * events in the input data should not forwarded from this
	 * view.
	 */
	public boolean shouldChangeEventsBeForwarded() {
		return true;
	}

	@Override
	public void dataChanged(DataEvent event) {
		if (inputSeries != null && inputSeries.contains(event.affected)
				|| inputValue != null && inputValue == event.affected
				|| inputTable != null && inputTable == event.affected) {
			
			update(event);
			
			if (shouldChangeEventsBeForwarded()) {
				this.fireChangeEvent(new DataEvent(this, event, event.getTypes().toArray()));
			}
		}
	}
}
