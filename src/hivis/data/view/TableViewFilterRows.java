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
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.primitives.Ints;

import hivis.data.DataSeries;
import hivis.data.DataTable;

/**
 * Class for creating table views that filter out some rows from an input table.
 * 
 * @author O. J. Coleman
 */
public class TableViewFilterRows extends AbstractTableView<SeriesViewRow<?>> {
	private RowFilter filter;
	
	public TableViewFilterRows(DataTable input, RowFilter filter) {
		super(input);
		this.filter = filter;
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
		
		// Determine which rows should be included.
		List<Integer> include = new ArrayList<>(inputTable.length());
		for (int i = 0; i < inputTableLength; i++) {
			if (!filter.excludeRow(inputTable, i)) {
				include.add(i);
			}
		}
		
		int[] rowMap = Ints.toArray(include);
		
		// Update/set row map for each SeriesViewRow wrapper.
		for (SeriesViewRow<?> s : series.values()) {
			s.setRowMap(rowMap);
		}
	}
}
