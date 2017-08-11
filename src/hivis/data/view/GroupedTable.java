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

import hivis.data.AggregateFunction;
import hivis.data.DataMap;
import hivis.data.DataTable;

/**
 * <p>
 * Represents a view of a DataTable containing the rows collected into groups.
 * The grouping is represented as a {@link DataMap} where the map keys represent
 * the group identifier or key and the map values are DataTables containing the
 * rows belonging to that group.
 * </p>
 * <p>
 * The rows in the group tables appear in the same order as their order in the
 * original table.
 * </p>
 * <p>
 * The group tables returned by {@link #get(Object)} and {@link #values()} are
 * emptied (set to length 0) if the group size becomes zero (and calls to
 * {@link #get(Object)} for groups that do not yet exist will return empty group
 * tables, but not add them to the list of group tables returned by
 * {@link #values()} ). If the group size subsequently becomes non-zero this
 * same group table will be reused. This allows external observers to monitor
 * the size of a group even if it may become empty, and before it has existed in
 * the input table.
 * </p>
 * 
 * @see DataTable#group(int)
 * @see DataTable#group(String)
 * 
 * @author O. J. Coleman
 */
public interface GroupedTable<K> extends DataMap<K, TableView> {
	/**
	 * Get the group table for the specified group identifier/key. If the key
	 * does not currently exist then an empty table will be returned. If the
	 * group for the key subsequently becomes non-empty then the previously
	 * returned table will be populated accordingly.
	 */
	TableView get(K key);

	/**
	 * <p>
	 * Creates a view of the table groups in which each group table is
	 * aggregated into a single row using the specified aggregation function.
	 * </p>
	 * <p>
	 * The {@link AggregateFunction#apply(String, hivis.data.DataSeries)} method
	 * of the given AggregateFunction will be called for each series in each
	 * group table, and the resulting value used for the corresponding series 
	 * and row for the group table in the returned table.
	 * </p>
	 * 
	 * @param function
	 *            The aggregation function.
	 */
	TableView aggregate(AggregateFunction function);

	/**
	 * Convenience aggregation method (see {@link #aggregate(AggregateFunction)}
	 * . The aggregation function returns the minimum value of each series in a
	 * given group table, or the first value of the series if it's not numeric.
	 */
	public TableView aggregateMin();

	/**
	 * Convenience aggregation method (see {@link #aggregate(AggregateFunction)}
	 * . The aggregation function returns the maximum of each series in a given
	 * group table, or the first value of the series if it's not numeric.
	 */
	public TableView aggregateMax();

	/**
	 * Convenience aggregation method (see {@link #aggregate(AggregateFunction)}
	 * . The aggregation function returns the sum of each series in a given
	 * group table, or the first value of the series if it's not numeric.
	 */
	public TableView aggregateSum();

	/**
	 * Convenience aggregation method (see {@link #aggregate(AggregateFunction)}
	 * . The aggregation function returns the product of each series in a given
	 * group table, or the first value of the series if it's not numeric.
	 */
	public TableView aggregateProduct();

	/**
	 * Convenience aggregation method (see {@link #aggregate(AggregateFunction)}
	 * . The aggregation function returns the mean of each series in a given
	 * group table, or the first value of the series if it's not numeric.
	 */
	public TableView aggregateMean();

	/**
	 * Convenience aggregation method (see {@link #aggregate(AggregateFunction)}
	 * . The aggregation function returns the variance of each series in a given
	 * group table, or the first value of the series if it's not numeric.
	 */
	public TableView aggregateVariance();

	/**
	 * Convenience aggregation method (see {@link #aggregate(AggregateFunction)}
	 * . The aggregation function returns the standard deviation of each series
	 * in a given group table, or the first value of the series if it's not
	 * numeric.
	 */
	public TableView aggregateStdDev();

}