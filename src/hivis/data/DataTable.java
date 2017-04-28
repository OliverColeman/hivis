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

package hivis.data;

import java.util.regex.Pattern;

import hivis.common.ListMap;
import hivis.common.ListSet;
import hivis.data.view.Function;
import hivis.data.view.RowFilter;
import hivis.data.view.TableFunction;
import hivis.data.view.TableView;

/**
 * <p>
 * Represents a table of data in which the columns are represented as
 * {@link DataSeries}, each of which has a unique String label. The label/series
 * pairs have a fixed order (either the order they have been added or an
 * ordering defined by a view).
 * </p>
 * 
 * <p>
 * DataTable extends {@link DataMap}, it's a mapping from labels to DataSeries,
 * this means that it inherits several methods which are awkwardly named in the
 * context of a table, such as {@link DataMap#keys()} (series labels) and
 * {@link DataMap#values()} (corresponding data series). On the plus side these
 * methods provide the set of labels and series as view DataSeries that will
 * update when labels/series are added, removed or rearranged in the table.
 * </p>
 * 
 * @author O. J. Coleman
 */
public interface DataTable extends DataMap<String, DataSeries<?>>, Iterable<DataRow> {

	/**
	 * Get the number of series.
	 */
	int seriesCount();

	/**
	 * Get the length of the (longest) series.
	 */
	int length();

	/**
	 * Returns true iff this table contains a series with the specified label.
	 */
	boolean hasSeries(String label);

	/**
	 * Get the series contained in this table, keyed by their label in this
	 * table, as an unmodifiable ListMap.
	 */
	ListMap<String, DataSeries<?>> getLabelledSeries();

	/**
	 * Get the series contained in this table.
	 */
	ListSet<DataSeries<?>> getAll();

	/**
	 * Get the specified series.
	 * @throws IndexOutOfBoundsException if the index is out of range ( index < 0 || index >= seriesCount())
	 */
	DataSeries<?> get(int index);

	/**
	 * Get the specified series, or null if there is no series with the given label.
	 */
	DataSeries<?> get(String label);

	/**
	 * Get the specified series.
	 * @deprecated As of 2.0. Replaced by {@link #get(int)}.
	 */
	DataSeries<?> getSeries(int index);

	/**
	 * Get the specified series.
	 * @deprecated As of 2.0. Replaced by {@link #get(String)}.
	 */
	DataSeries<?> getSeries(String label);
	
	/**
	 * Get a view of the specified row.
	 */
	DataRow getRow(int index);

	/**
	 * Returns the label associated with the specified series.
	 */
	String getSeriesLabel(int index);

	/**
	 * Returns the set of labels associated with each series.
	 */
	ListSet<String> getSeriesLabels();

	/**
	 * Returns true iff this table has a series that represents the row keys.
	 */
	boolean hasRowKeys();

	/**
	 * Returns the index of the series that represents the row keys, or -1 if
	 * there is no such series. By default this is set to the first series added
	 * that contains String values.
	 */
	int getRowKeyIndex();

	/**
	 * Returns the label of the series that represents the row keys, or null if
	 * there is no such series. By default this is set to the first series added
	 * that contains String values.
	 */
	String getRowKeyLabel();

	/**
	 * Set the series that represents the row keys. By default this is set to
	 * the first series added that contains String values.
	 * 
	 * @param index
	 *            The index of the series to use for the row keys, or -1 to
	 *            indicate no such series.
	 */
	void setRowKey(int index);

	/**
	 * Add the given series to this table with the given label
	 * 
	 * @return This DataTable.
	 * 
	 * @throws IllegalArgumentException
	 *             If this DataTable already contains a DataSeries with the same
	 *             label.
	 */
	DataTable addSeries(String label, DataSeries<?> newSeries);

	/**
	 * Add the series from the given table to this table.
	 * 
	 * @return This DataTable.
	 * 
	 * @throws IllegalArgumentException
	 *             If this DataTable already contains a DataSeries with the same
	 *             label as one of the series in the given table.
	 */
	DataTable addSeries(DataTable table);

	/**
	 * Remove the specified series from this table.
	 * 
	 * @return This DataTable.
	 */
	DataTable removeSeries(String label);

	/**
	 * Remove the specified series from this table.
	 * 
	 * @return This DataTable.
	 */
	DataTable removeSeries(int index);

	// Views

	/**
	 * Get a view of this table containing the specified series in the specified
	 * order.
	 * 
	 * @param series
	 *            Indices of series to select in the order to select them.
	 * @return a view of this table containing the specified series in the
	 *         specified order.
	 */
	TableView selectSeries(int... series);

	/**
	 * Get a view of this table containing the specified range of series from
	 * this table.
	 * 
	 * @param begin
	 *            The index of the first series to include.
	 * @param end
	 *            The index of the last series to include.
	 * 
	 * @return a view of this table containing the specified series in the order
	 *         they appear in this table.
	 */
	TableView selectSeriesRange(int begin, int end);

	/**
	 * Get a view of this table containing the specified series in the specified
	 * order.
	 * 
	 * @param series
	 *            Labels of series to select in the order to select them.
	 * @return a view of this table containing the specified series in the
	 *         specified order.
	 */
	TableView selectSeries(String... series);

	/**
	 * Get a view of this table containing the series for which the label
	 * matches the given "glob" pattern.
	 * 
	 * @param pattern
	 *            The glob pattern to use to match series labels.
	 * @return a view of this table containing the specified series in the order
	 *         they appear in this table.
	 */
	TableView selectSeriesGlob(String pattern);

	/**
	 * Get a view of this table containing the series for which the label
	 * matches the given regular expression.
	 * 
	 * @param pattern
	 *            The regular expression pattern to use to match series labels.
	 * @return a view of this table containing the specified series in the order
	 *         they appear in this table.
	 */
	TableView selectSeriesRE(Pattern pattern);

	/**
	 * Get a view of this table containing the series whose labels match the
	 * given regular expression, renaming the series with the given rename
	 * pattern.
	 * 
	 * @param pattern
	 *            The regular expression pattern to use to match series labels.
	 * @param renamePattern
	 *            The replacement pattern (with $N referring to capture groups
	 *            in 'pattern'). If renamePattern contains '\\oi' or '\\ni' it
	 *            will be replaced by the original or new index of the series
	 *            respectively. An '\\oi' or '\\ni' may be followed by numerals
	 *            to indicate an offset, for example "my\\ni5pf" will give
	 *            strings "my5pf", my6pf", "my7pf" and so on.
	 * @return a view of this table containing the specified series in the order
	 *         they appear in this table, renamed according the renamePattern.
	 */
	TableView selectSeriesRE(Pattern pattern, String renamePattern);

	/**
	 * Get a view of this table containing the series in this table renamed 
	 * with the specified labels. The number of labels given must match
	 * the numbers of series in the table.
	 * 
	 * @param labels The new labels.
	 */
	TableView relabelSeries(String... labels);
	
	/**
	 * Get a view of this table containing the series in this table renamed by
	 * prepending and appending the specified prefix and postfix to the labels.
	 * If the prefix or postfix contain '\\oi' this will be replaced by the
	 * (original) index of the series. An '\\oi' may be followed by numerals to
	 * indicate an offset, for example "my\\oi5pf" will give strings "my5pf",
	 * my6pf", "my7pf" and so on.
	 * 
	 * @param prefix
	 *            The string to prepend to each label. May be null or an empty
	 *            string.
	 * @param postfix
	 *            The string to append to each label. May be null or an empty
	 *            string.
	 */
	TableView relabelSeriesPP(String prefix, String postfix);
	
	
	/**
	 * <p>
	 * Create a view of this table containing the rows in the table grouped
	 * according to the values in the specified series. The grouping is
	 * represented as a {@link DataMap} where the map keys represent the group
	 * identifier and the map values are DataTable views containing the rows
	 * belonging to that group. Groups are formed by placing all rows for which
	 * getRow(x).get(groupingSeries).equals(getRow(y).get(groupingSeries)), for
	 * all rows x and y, into the same group (and rows where this is false into
	 * different groups). The key for each group is a value such that
	 * key.equals(group.getRow(z).get(groupingSeries)) for all rows z in the
	 * group.
	 * </p>
	 * <p>
	 * The rows in the group table views appear in the same order as their order
	 * in this table.
	 * </p>
	 * <p>
	 * See {@link hivis.data.view.GroupedTable} for more information.
	 * </p>
	 */
	<K> DataMap<K, TableView> group(int groupingSeries);

	/**
	 * <p>
	 * Create a view of this table containing the rows in the table grouped
	 * according to the values in the specified series. The grouping is
	 * represented as a {@link DataMap} where the map keys represent the group
	 * identifier and the map values are DataTable views containing the rows
	 * belonging to that group. Groups are formed by placing all rows for which
	 * getRow(x).get(groupingSeries).equals(getRow(y).get(groupingSeries)), for
	 * all rows x and y, into the same group (and rows where this is false into
	 * different groups). The key for each group is a value such that
	 * key.equals(group.getRow(z).get(groupingSeries)) for all rows z in the
	 * group.
	 * </p>
	 * <p>
	 * The rows in the group table views appear in the same order as their order
	 * in this table.
	 * </p>
	 * <p>
	 * See {@link hivis.data.view.GroupedTable} for more information.
	 * </p>
	 */
	<K> DataMap<K, TableView> group(String groupingSeries);
	
	/**
	 * <p>
	 * Create a view of this table containing the rows in the table grouped
	 * according to the specified row key function. The grouping is represented
	 * as a {@link DataMap} where the map keys represent the group identifier
	 * and the map values are DataTable views containing the rows belonging to
	 * that group. The key for a row is calculated using the given row key
	 * function, which should accept a row from the table and return a group key
	 * (identifier) for that row. Groups are formed by placing all rows for
	 * which rowKeyFunction(getRow(x)).equals(rowKeyFunction(getRow(y)), for all
	 * rows x and y, into the same group (and rows where this is false into
	 * different groups). The key for each group is a value such that
	 * key.equals(rowKeyFunction(group.getRow(z)) for all rows z in the group
	 * table.
	 * </p>
	 * <p>
	 * The rows in the group table views appear in the same order as their order
	 * in this table.
	 * </p>
	 * <p>
	 * See {@link hivis.data.view.GroupedTable} for more information.
	 * </p>
	 */
	<K> DataMap<K, TableView> group(Function<DataRow, K> rowKeyFunction);

	
	/**
	 * Get a table containing series generated by applying the given function to
	 * this table.
	 * 
	 * @param function
	 *            The function to generate the series for the new table.
	 * @param includeOriginalSeries
	 *            Whether to include the DataSeries from this DataTable in the
	 *            new table.
	 * @return a table containing series generated by applying the given
	 *         function to this table, optionally including the series from this
	 *         table.
	 * 
	 */
	TableView apply(TableFunction function, boolean includeOriginalSeries);

	/**
	 * Get a transposed view of this table. If a row key series is set in this
	 * table and it contains no duplicate values it will be used for the series
	 * labels in the transposed table, otherwise the row index will be used for
	 * the series label in the transposed table (see {@link #setRowKey(int)}).
	 * The series labels for this table will become the row key in the
	 * transposed table.
	 */
	TableView transpose();
	
	/**
	 * Get a table view comprising the series in the given table appended to the series in this table.
	 * If the series labels (both presence and order) or types do not match then an error will occur.
	 */
	TableView append(DataTable table);
	
	/**
	 * Get a view of this table containing the specified contiguous range of rows.
	 * @param beginIndex The index of the first row to include, inclusive.
	 * @param endIndex The index of the last row to include, inclusive.
	 */
	TableView selectRowRange(int beginIndex, final int endIndex);

	/**
	 * Get a view of this table containing the specified rows.
	 * @param rows The indices of the rows to include, in any order. May be an array or list of ints.
	 */
	TableView selectRows(int... rows);
	
	/**
	 * Get a view of this table with some rows filtered out.
	 * @param filter The filter specifying which rows should be included in the view.
	 */
	TableView selectRows(RowFilter filter);
}
