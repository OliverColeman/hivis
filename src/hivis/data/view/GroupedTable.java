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
 * the group identifier and the map values are DataTable views containing the
 * rows belonging to that group.
 * </p>
 * <p>
 * The rows in the group table views should appear in the same order as their
 * order in the input table.
 * </p>
 * <p>
 * The group table views returned by {@link #get(Object)} and {@link #values()}
 * should be emptied (set to length 0) if the group size becomes zero (and calls
 * to {@link #get(Object)} for groups that do not yet exist will return empty
 * group table views, but not add them to the list of groups returned by
 * {@link #values()} ). If the group size subsequently becomes non-zero this
 * same group table view will be reused. This allows external observers to
 * monitor the size of a group (even before it has existed in the input table).
 * </p>
 * 
 * @see DataTable#group(int)
 * @see DataTable#group(String)
 * 
 * @author O. J. Coleman
 */
public interface GroupedTable<K> extends DataMap<K, TableView> {
	/**
	 * Get the group table view for the specified key. If the key does not
	 * currently exist then an empty group table view will be returned. If the
	 * group for that key subsequently becomes non-empty then the previously
	 * returned table view will be populated accordingly.
	 */
	TableView get(K key);

	/**
	 * Returns a view of this table group in which the table for each
	 * group/sub-table is aggregated into a row of the returned table view.
	 * 
	 * @param function
	 *            The aggregation function.
	 */
	TableView aggregate(AggregateFunction function);

	/**
	 * Convenience aggregation method (see {@link #aggregate(AggregateFunction)}
	 * . The aggregation function returns the minimum value of each series in
	 * the group tables, or the first value of the series if it's not numeric.
	 */
	public TableView aggregateMin();

	/**
	 * Convenience aggregation method (see {@link #aggregate(AggregateFunction)}
	 * . The aggregation function returns the maximum of each series in the
	 * group tables, or the first value of the series if it's not numeric.
	 */
	public TableView aggregateMax();

	/**
	 * Convenience aggregation method (see {@link #aggregate(AggregateFunction)}
	 * . The aggregation function returns the sum of each series in the group
	 * tables, or the first value of the series if it's not numeric.
	 */
	public TableView aggregateSum();

	/**
	 * Convenience aggregation method (see {@link #aggregate(AggregateFunction)}
	 * . The aggregation function returns the product of each series in the
	 * group tables, or the first value of the series if it's not numeric.
	 */
	public TableView aggregateProduct();

	/**
	 * Convenience aggregation method (see {@link #aggregate(AggregateFunction)}
	 * . The aggregation function returns the mean of each series in the group
	 * tables, or the first value of the series if it's not numeric.
	 */
	public TableView aggregateMean();

	/**
	 * Convenience aggregation method (see {@link #aggregate(AggregateFunction)}
	 * . The aggregation function returns the variance of each series in the
	 * group tables, or the first value of the series if it's not numeric.
	 */
	public TableView aggregateVariance();

	/**
	 * Convenience aggregation method (see {@link #aggregate(AggregateFunction)}
	 * . The aggregation function returns the standard deviation of each series
	 * in the group tables, or the first value of the series if it's not
	 * numeric.
	 */
	public TableView aggregateStdDev();

}