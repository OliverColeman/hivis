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

package hivis.interact;

import controlP5.ControlEvent;
import controlP5.ControlListener;
import controlP5.ControllerInterface;
import hivis.data.AbstractDataSeries;
import hivis.data.DataSeries;
import hivis.data.view.CalcSeries;

/**
 * Base class for a {@link CalcSeries} whose values are affected by the value of a P5 Controller.
 * The series will be {@link #update()}d whenever the value for the controllers changes.
 * This class also provides a simplified way of creating calculated series via the apply methods.
 * 
 * @author O. J. Coleman
 */
public class CalcSeriesP5<V> extends CalcSeries<V, V> implements ControlListener {
	DataSeries<V> input;
	
	/**
	 * Create a DataSeries function of the given input series, with length equal
	 * to the (first) input series.
	 */
	public CalcSeriesP5(int length, ControllerInterface<?>... p5Controllers) {
		super(length);
		
		for (ControllerInterface<?> con : p5Controllers) {
			con.addListener(this);
		}
	}
	
	/**
	 * Create a DataSeries function of the given input series, with length equal
	 * to the (first) input series.
	 */
	public CalcSeriesP5(DataSeries<V> input, ControllerInterface<?>... p5Controllers) {
		super(input);
		this.input = input;
		
		for (ControllerInterface<?> con : p5Controllers) {
			con.addListener(this);
		}
	}
	
	@Override
	public void controlEvent(ControlEvent theEvent) {
		updateView(theEvent);
	}
	
	@Override
	public V calc(int index) {
		if (input != null) {
			if (input.get(index) instanceof  Double) {
				return (V) (Double) apply(((Number) input.get(index)).doubleValue());
			}
			if (input.get(index) instanceof  Integer) {
				return (V) (Integer) apply(((Number) input.get(index)).intValue());
			}
			if (input.get(index) instanceof  Long) {
				return (V) (Long) apply(((Number) input.get(index)).longValue());
			}
		}
		throw new IllegalStateException("Please implement the method CalcSeriesP5.calc(int index).");
	}
	
	public double apply(double input) {
		throw new IllegalStateException("Please implement the method CalcSeriesP5.apply(double input).");
	}
	public int apply(int input) {
		throw new IllegalStateException("Please implement the method CalcSeriesP5.apply(int input).");
	}
	public long apply(long input) {
		throw new IllegalStateException("Please implement the method CalcSeriesP5.apply(long input).");
	}
	
	@Override
	public DataSeries<V> getNewSeries() {
		if (inputSeries == null || inputSeries.isEmpty()) return super.getNewSeries();
		
		if (inputSeries.get(0).length() > 0) {
			return (DataSeries<V>) AbstractDataSeries.getNewSeries(inputSeries.get(0).get(0).getClass());
		}
		return (DataSeries<V>) AbstractDataSeries.getNewSeries(inputSeries.get(0).getType());
	}
}
