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


import hivis.data.AbstractUnmodifiableDataSeries;
import hivis.data.Data;
import hivis.data.DataEvent;
import hivis.data.DataSeries;

/**
 * Base class for creating {@link DataSeries} that are based on some other {@link Data} set. 
 * At minimum sub-classes must implement {@link #get(int)}, {@link #length()}, and {@link #update(DataEvent)}. 
 * {@link CalcSeries} provides the infrastructure for a cached view that
 * simplifies implementations of calculated views. Also see {@link AbstractSeriesViewMultiple} which 
 * provides more infrastructure than this class but less than {@link CalcSeries}.
 * 
 * @author O. J. Coleman
 *
 */
public abstract class AbstractSeriesView<V> extends AbstractUnmodifiableDataSeries<V> implements SeriesView<V> {
	/**
	 * The primary data source, to synchronise on when updating.
	 */
	private Data primarySource = null;
	
	
	/**
	 * Create an AbstractSeriesView that has the given (primary) data source.
	 */
	public AbstractSeriesView(Data source) {
		primarySource = source;
	}

	/**
	 * Create an AbstractSeriesView that is not based on any input.
	 */
	public AbstractSeriesView() {
	}

	
	@Override
	public void lock() {
		if (primarySource != null) {
			primarySource.lock();
		}
	}
	@Override
	public void unlock() {
		if (primarySource != null) {
			primarySource.unlock();
		}
	}
}
