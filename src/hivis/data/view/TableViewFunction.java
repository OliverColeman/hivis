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
import java.util.Map;

import hivis.common.LSListMap;
import hivis.common.ListMap;
import hivis.data.DataSeries;
import hivis.data.DataTable;

/**
 * A view that presents the {@link DataSeries} generated by a {@link TableFunction} 
 * as a {@link DataTable}, optionally including the DataSeries from the 
 * input DataTable.
 * 
 * @author O. J. Coleman
 *
 */
public class TableViewFunction extends AbstractTableView<DataSeries<?>, DataTable> {
	TableFunction function;
	boolean includeInputSeries;
	
	/**
	 * Create a new ViewTableFunction that does not include the DataSeries from the input DataTable in this DataTable view.
	 * 
	 * @param source The input DataTable.
	 * @param function The function to generate the new DataSeries.
	 */
	public TableViewFunction(TableFunction function, DataTable... source) {
		this(function, false, source);
	}
	
	/**
	 * Create a new ViewTableFunction that optionally includes the DataSeries from the input DataTable.
	 * 
	 * @param source The input DataTable.
	 * @param function The function to generate the new DataSeries.
	 * @param includeInputSeries Whether to include the DataSeries from the input DataTable in this DataTable view.
	 */
	public TableViewFunction(TableFunction function, boolean includeInputSeries, DataTable... source) {
		super(source);
		this.function = function;
		this.includeInputSeries = includeInputSeries;
		updateSeries();
	}

	
	@Override
	protected void updateSeries(List<Object> eventTypes) {
		series.clear();
		
		if (includeInputSeries) {
			for (DataTable dt : input) {
				series.putAll(dt.getLabelledSeries());
			}
		}
		
		ListMap<String, DataSeries<?>> map = new LSListMap<>();
		function.getSeries(input, map);
		
		for (Map.Entry<String, DataSeries<?>> s : map.entrySet()) {
			series.put(s.getKey(), s.getValue());
			s.getValue().addContainer(this);
		}
	}
}
