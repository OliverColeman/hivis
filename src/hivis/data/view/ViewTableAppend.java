/**
 * 
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
public class ViewTableAppend extends ViewTable {
	public ViewTableAppend(DataTable... input) {
		super(input);
		updateSeries();
	}
	
	@Override
	protected void updateSeries(List<Object> eventTypes) {
		// If this was called manually or as a result of a series event, update the list of series
		// (which will trigger a change event if necessary).
		if (eventTypes.isEmpty() || eventTypes.contains(DataTableChange.SeriesAdded) || eventTypes.contains(DataTableChange.SeriesRemoved) || eventTypes.contains(DataTableChange.SeriesReordered)) {
			// Check series in input tables match.
			DataTable first = source.get(0);
			for (int tableIndex = 1; tableIndex < source.size(); tableIndex++) {
				DataTable table = source.get(tableIndex);
				
				if (first.seriesCount() != table.seriesCount()) {
					throw new IllegalArgumentException("Cannot append tables with differing numbers of series.");
				}
				
				for (int si = 0; si < first.seriesCount(); si++) {
					if (!first.getSeriesLabel(si).equals(table.getSeriesLabel(si))) {
						throw new IllegalArgumentException("Cannot append tables with differing series labels.");
					}
					
					if (!first.get(si).getType().equals(table.get(si).getType())) {
						throw new IllegalArgumentException("Cannot append tables with differing series types.");
					}
				}
			}
			
			// Reuse appended series views if possible.
			HashMap<String, DataSeries<?>> oldSeries = new HashMap<>(series);
			series.clear();
			
			// For each series.
			for (String label : first.getSeriesLabels()) {
				if (oldSeries.containsKey(label)) {
					// Reuse old series.
					series.put(label, oldSeries.get(label));
				}
				else {
					
					
					// Get the corresponding series from each table.
					DataSeries<?>[] seriesToAppend = new DataSeries[source.size()];
					for (int ti = 0; ti < source.size(); ti++) {
						seriesToAppend[ti] = source.get(ti).get(label);
					}
					// Create new series appender and add to series in this view.
					DataSeries<?> appendedSeries = new ViewSeriesAppend(seriesToAppend);
					series.put(label, appendedSeries);
				}
			}
		}
	}
}
