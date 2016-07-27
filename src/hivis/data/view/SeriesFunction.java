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

import java.util.List;

import hivis.common.ListMap;
import hivis.common.ListSet;
import hivis.data.DataSeries;
import hivis.data.DataTable;

/**
 * Interface for functions that produce a DataSeries, optionally derived from input DataSeries.
 * 
 * @author O. J. Coleman
 */
public interface SeriesFunction<O, I> {
	/**
	 * For the given DataSeries return the generated series.
	 * 
	 * @param input The input DataSeries. If the series this function generates are not derived from other series this may be null.
	 */
	public DataSeries<O> apply(List<DataSeries<I>> input);
}
