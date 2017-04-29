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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import com.google.common.collect.Streams;
import com.google.common.primitives.Ints;

import hivis.data.AbstractModifiableDataSeries;
import hivis.data.DataRow;
import hivis.data.DataSeries;
import hivis.data.DataSeriesGeneric;
import hivis.data.DataTable;

/**
 * Creates a view of a table containing the rows in the table sorted according to the natural ordering
 * of the values in a specified series or according to a provided Comparator that defines
 * an ordering over the {@link DataRow}s in the table.
 * 
 * @author O. J. Coleman
 */
public class SortedTable extends AbstractTableView<SeriesViewRow<?>> {
	private Comparator<DataRow> comparator;
	
	/**
	 * <p>
	 * Create a view of the given table containing the rows in the table sorted into
	 * ascending order, according to the natural ordering of the values in the
	 * specified series.
	 * <p>
	 * <p>
	 * All values in the specified series must implement the Comparable
	 * interface. Furthermore, all values must be mutually comparable (that is,
	 * v1.compareTo(v2) must not throw a ClassCastException for any values v1
	 * and v2).
	 * <p>
	 * <p>
	 * This sort is guaranteed to be stable: equal values will not be reordered
	 * as a result of the sort.
	 * </p>
	 */
	public SortedTable(DataTable input, int sortingSeries) {
		super(input);
		comparator = new SeriesComparator(sortingSeries);
		updateSeries();
	}
	
	/**
	 * <p>
	 * Create a view of the given table containing the rows in the table sorted into
	 * ascending order, according to the natural ordering of the values in the
	 * specified series.
	 * <p>
	 * <p>
	 * All values in the specified series must implement the Comparable
	 * interface. Furthermore, all values must be mutually comparable (that is,
	 * v1.compareTo(v2) must not throw a ClassCastException for any values v1
	 * and v2).
	 * <p>
	 * <p>
	 * This sort is guaranteed to be stable: equal values will not be reordered
	 * as a result of the sort.
	 * </p>
	 */
	public SortedTable(DataTable input, String sortingSeries) {
		super(input);
		comparator = new SeriesComparator(sortingSeries);
		updateSeries();
	}

	/**
	 * <p>
	 * Create a view of this table containing the rows in this table sorted
	 * according to the order induced by the specified comparator. The
	 * comparator must accept any two {@link DataRow}s from this table and
	 * determine their relative ordering according to the contract specified by
	 * <a href=
	 * "https://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html">
	 * Comparator</a>
	 * </p>
	 * </p>
	 * This sort is guaranteed to be stable: equal rows, according to the
	 * comparator, will not be reordered as a result of the sort.
	 * </p>
	 */
	public SortedTable(DataTable input, Comparator<DataRow> comparator) {
		super(input);
		this.comparator = comparator;
		updateSeries();
	}
	

	@Override
	protected void updateSeries(List<Object> eventTypes) {
		// Maintain old list of SeriesViewRow wrappers so we can reuse them. 
		HashMap<String, SeriesViewRow<?>> oldSeries = new HashMap<>(series);
		
		series.clear();
		
		DataTable inputTable = inputTables.get(0);
		int inputTableLength = inputTable.length();
		
		// Create/reuse a SeriesViewRow for each series in the input table. 
		for (Entry<String, DataSeries<?>> inputS : inputTable.getLabelledSeries().entrySet()) {
			String label = inputS.getKey();
			DataSeries<?> s = inputS.getValue();
			
			// Get the existing SeriesViewRow for this label if it exists.
			SeriesViewRow<?> oldWrapper = oldSeries.get(label);
			
			// If there was an existing SeriesViewRow and it has the input series set as its input, then reuse it.
			if (oldWrapper != null && s == oldWrapper.getInputSeries().get(0)) {
				series.put(label, oldWrapper);
			}
			else {
				// Otherwise create a new one.
				series.put(label, new SeriesViewRow<>(inputS.getValue()));
			}
		}
		
		// Get rows ordered by comparator, and then get the original indices of the rows.
		int[] rowMap = Streams.stream(inputTable).sorted(comparator).mapToInt(row -> row.getRowIndex()).toArray();;
		
		// Update/set row map for each SeriesViewRow wrapper.
		for (SeriesViewRow<?> s : series.values()) {
			s.setRowMap(rowMap);
		}
	}
	
	
	private class SeriesComparator implements Comparator<DataRow> {
		int seriesIndex;
		String seriesLabel;
		public SeriesComparator(int series) {
			seriesIndex = series;
		}
		public SeriesComparator(String series) {
			seriesLabel = series;
		}
		@Override
		public int compare(DataRow row0, DataRow row1) {
			if (seriesLabel != null) {
				Comparable v0 = (Comparable) row0.get(seriesLabel);
				Comparable v1 = (Comparable) row1.get(seriesLabel);
				return v0.compareTo(v1);
			}
			else {
				Comparable v0 = (Comparable) row0.get(seriesIndex);
				Comparable v1 = (Comparable) row1.get(seriesIndex);
				return v0.compareTo(v1);
			}
		}
	}
}
