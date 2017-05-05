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

import java.util.HashMap;
import java.util.List;

import hivis.data.DataSeries;
import hivis.data.DataTable;
import hivis.data.DataTableChange;

/**
 * A view over two or more tables appended one after the other.
 *  
 * @author O. J. Coleman
 */
public class TableViewAppend extends AbstractTableView<SeriesViewAppend<?>> {
	public TableViewAppend(DataTable... input) {
		super(input);
		updateSeries();
	}
	
	@Override
	protected void updateSeries(List<Object> eventTypes) {
		// If this was called manually or as a result of a series event, update the list of series
		// (which will trigger a change event if necessary).
		if (eventTypes.isEmpty() || eventTypes.contains(DataTableChange.SeriesAdded) || eventTypes.contains(DataTableChange.SeriesRemoved) || eventTypes.contains(DataTableChange.SeriesReordered)) {
			// Check series in input tables match.
			DataTable first = inputTables.get(0);
			for (int tableIndex = 1; tableIndex < inputTables.size(); tableIndex++) {
				DataTable table = inputTables.get(tableIndex);
				
				if (first.seriesCount() != table.seriesCount()) {
					throw new IllegalArgumentException("Cannot append tables with differing numbers of series.");
				}
				
				for (int si = 0; si < first.seriesCount(); si++) {
					if (!first.getSeriesLabel(si).equals(table.getSeriesLabel(si))) {
						throw new IllegalArgumentException("Cannot append tables with differing series labels.");
					}
					
					// Check series types are compatible.
					DataSeries<?> s1 = first.get(si);
					DataSeries<?> s2 = table.get(si);
					// If they're not (exactly) the same type and not both numeric.
					if (!s1.getType().equals(s2.getType()) && !(s1.isNumeric() && s2.isNumeric())) {
						throw new IllegalArgumentException("Cannot append tables with incompatible series types.");
					}
				}
			}
			
			// Reuse appended series views if possible.
			HashMap<String, SeriesViewAppend<?>> oldSeries = new HashMap<>(series);
			series.clear();
			
			// For each series.
			for (String label : first.getSeriesLabels()) {
				if (oldSeries.containsKey(label)) {
					// Reuse old series.
					series.put(label, oldSeries.get(label));
				}
				else {
					
					
					// Get the corresponding series from each table.
					DataSeries<?>[] seriesToAppend = new DataSeries[inputTables.size()];
					for (int ti = 0; ti < inputTables.size(); ti++) {
						seriesToAppend[ti] = inputTables.get(ti).get(label);
					}
					// Create new series appender and add to series in this view.
					SeriesViewAppend<?> appendedSeries = new SeriesViewAppend(seriesToAppend);
					series.put(label, appendedSeries);
				}
			}
		}
	}
}
