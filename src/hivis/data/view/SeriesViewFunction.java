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

import hivis.data.DataSeries;

/**
 * Base class for creating {@link DataSeries} that are calculated from zero, one or more other DataSeries.
 * If input series are provided then change events on those series are forwarded to this series.
 * 
 * @author O. J. Coleman
 *
 */
public abstract class SeriesViewFunction<I, O> extends SeriesView<I, O> {
	private int length = Integer.MIN_VALUE;
	
	
	/**
	 * Create a DataSeries function of the given input series, with length equal to the (first) input series.
	 */
	public SeriesViewFunction(DataSeries<I>... input) {
		super(input);
	}
	
	/**
	 * Create a DataSeries function with the given length.
	 */
	public SeriesViewFunction(int length) {
		super();
		this.length = length;
	}
	
	
	@Override
	public int length() {
		if (length >= 0) {
			return length;
		}
		if (inputSeries.isEmpty()) {
			throw new RuntimeException("If an SFunc has no input series then either the length() method must be overridden or the length field set to provide the length of the calculated series.");
		}
		return inputSeries.get(0).length();
	}

	@Override
	public O getEmptyValue() {
		return getNewSeries().getEmptyValue();
	}
}

