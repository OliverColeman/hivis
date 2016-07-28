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
import java.util.Set;

import com.google.common.collect.Sets;

import hivis.common.BMListSet;
import hivis.common.ListSet;
import hivis.common.Util;
import hivis.data.AbstractDataSeries;
import hivis.data.DataSeries;
import hivis.data.DataSetDefault;
import hivis.data.DataTable;
import hivis.data.DataTableChange;
import hivis.data.DataTableDefault;


/**
 * Base class for {@link DataTable} views that select and/or reorder {@link AbstractDataSeries}.
 * Provides a convenient interface for doing so and handles and produces the necessary events. 
 * 
 * @author O. J. Coleman
 */
public abstract class TableViewSeriesBase extends AbstractTableView<DataSeries<?>> {
	public TableViewSeriesBase(DataTable source) {
		super(source);
	}
	
	/**
	 * Implementations must return the list/set of selected series labels and, 
	 * optionally, the corresponding new labels. The ListSet at index is the 
	 * label in the input table, the ListSet at index 1 is the new label.
	 * If a label is not available in the input table it will be ignored.
	 */
	protected abstract ListSet<String>[] getSelected();
	
	
	/**
	 * If and when an implementation changes the list of series that should be 
	 * selected according to {@link #getSelected(String)} then this method 
	 * should be called to update the selected series data.
	 * This method should also be called at the end of a constructor if a 
	 * call to getSelected would return a non-empty list.
	 */
	@Override
	protected void updateSeries() {
		super.updateSeries();
	}
	
	@Override
	protected synchronized void updateSeries(List<Object> eventTypes) {
		// If this was called manually or as a result of a series event, update the list of selected series
		// (which will trigger a change event if necessary).
		if (eventTypes.isEmpty() || eventTypes.contains(DataTableChange.SeriesAdded) || eventTypes.contains(DataTableChange.SeriesRemoved) || eventTypes.contains(DataTableChange.SeriesReordered)) {
			series.clear();
			ListSet<String>[] newSelected = getSelected();
			for (int idx = 0; idx < newSelected[0].size(); idx++) {
				String srcLabel = newSelected[0].get(idx);
				String newLabel = newSelected[1].get(idx);
				
				if (inputTables.get(0).hasSeries(srcLabel)) {
					series.put(newLabel, inputTables.get(0).getSeries(srcLabel));
					inputTables.get(0).getSeries(srcLabel).addContainer(this);
				}
			}
		}
	}
}
