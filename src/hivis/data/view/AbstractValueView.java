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

import com.google.common.reflect.TypeToken;

import hivis.data.AbstractDataValue;
import hivis.data.DataEvent;
import hivis.data.DataListener;
import hivis.data.DataSeries;
import hivis.data.DataValue;

/**
 * Base class for creating {@link DataValue}s that are optionally based on one or more other DataValues or DataSeries.
 * If input DataValues or DataSeries are provided then change events on those DataValues or DataSeries are forwarded to this DataValue.
 *
 * @author O. J. Coleman
 */
public abstract class AbstractValueView<I, O> extends AbstractDataValue<O> implements DataListener {
	/**
	 * The (optional) input values on which this view is based. Null if no input values are used.
	 */
	protected List<DataValue<I>> inputValues;

	/**
	 * The (optional) input series on which this view is based. Null if no input series are used.
	 */
	protected List<DataSeries<I>> inputSeries;

	/**
	 * Create a DataValue for the given input value(s).
	 */
	public AbstractValueView(DataValue<I>... input) {
		inputValues = Collections.unmodifiableList(Arrays.asList(Arrays.copyOf(input, input.length)));
		for (DataValue<I> s : inputValues) {
			s.addChangeListener(this);
		}
	}
	
	/**
	 * Create a DataValue for the given input series.
	 */
	public AbstractValueView(DataSeries<I>... input) {
		inputSeries = Collections.unmodifiableList(Arrays.asList(Arrays.copyOf(input, input.length)));
		for (DataSeries<I> s : inputSeries) {
			s.addChangeListener(this);
		}
	}
	
	/**
	 * Create a DataValue that is not based on input values.
	 */
	public AbstractValueView() {
	}
	
	/**
	 * Get the list of input values for this view.
	 */
	public List<DataValue<I>> getInputValues() {
		return inputValues;
	}
	
	/**
	 * Get the list of input series for this view.
	 */
	public List<DataSeries<I>> getInputSeries() {
		return inputSeries;
	}
	
	/**
	 * Get the list of input values for this view.
	 */
	public DataValue<I> getInputValue(int index) {
		return inputValues.get(index);
	}
	
	/**
	 * Get the list of input series for this view.
	 */
	public DataSeries<I> getInputSeries(int index) {
		return inputSeries.get(index);
	}
	
	
	@Override
	public void setValue(O value) {
		throw new UnsupportedOperationException("Can not set value of a calculated DataValue.");
	}
	
	@Override
	public void dataChanged(DataEvent event) {
		if (inputValues != null && inputValues.contains(event.affected) ||
				inputSeries != null && inputSeries.contains(event.affected)) {
			// Forward the change event.
			this.fireChangeEvent(new DataEvent(this, event, event.getTypes().toArray()));
		}
	}

}