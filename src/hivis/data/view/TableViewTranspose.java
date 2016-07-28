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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hivis.data.AbstractDataSeries;
import hivis.data.DataSeries;
import hivis.data.DataSeriesGeneric;
import hivis.data.DataTable;

/**
 * 
 * @author O. J. Coleman
 */
public class TableViewTranspose extends AbstractTableView<DataSeries<?>> {
	public TableViewTranspose(DataTable inputTable) {
		super(inputTable);
		updateSeries();
	}
	
	@Override
	protected void updateSeries(List<Object> eventTypes) {
		HashMap<String, DataSeries<?>> oldSeries = new HashMap<>(series);
		series.clear();
		
		DataTable table = inputTables.get(0);
		
		if (table.seriesCount() == 0 || table.length() == 0) {
			return;
		}
		
		if (table.hasRowKeys() && table.seriesCount() == 1) {
			throw new IllegalArgumentException("Cannot transpose a table containing only a key row.");
		}
		
		// Get the row keys series from the table, if set. They will be used as the series labels in the transposed table (if possible).
		DataSeries<?> origRowKeys = table.hasRowKeys() ? table.getSeries(table.getRowKeyIndex()) : null;
		// If a row key was set, make sure it contains unique values (series in a table must have unique labels).
		if (origRowKeys != null) {
			Set<Object> uniqueRowKeys = new HashSet<>();
			uniqueRowKeys.addAll(origRowKeys.asList());
			if (uniqueRowKeys.size() != origRowKeys.length()) {
				origRowKeys = null;
			}
		}
		
		String origRowKeysLabel = origRowKeys == null ? "Key" : table.getSeriesLabel(table.getRowKeyIndex());
		
		int newLength = table.seriesCount() - (table.hasRowKeys() ? 1 : 0); 
		
		// Create row key series from series labels in original table.
		DataSeries<String> newRowKeys = (DataSeries<String>) (oldSeries.containsKey(origRowKeysLabel) ? oldSeries.get(origRowKeysLabel) : new DataSeriesGeneric<String>());
		newRowKeys.resize(newLength);
		// Populate row key series and determine series type.
		Class<?> type = null;
		DataSeries<?> seriesExample = null;
		for (int osi = 0, nri = 0; osi < table.seriesCount(); osi++) {
			if (osi != table.getRowKeyIndex()) {
				String newKey = table.getSeriesLabel(osi);
				newRowKeys.set(nri, newKey);
				nri++;
				
				type = getType(type, table.getSeries(osi));
				// If this series has the right type use it as an example to generate new series.
				if (type.equals(table.getSeries(osi).get(0).getClass())) {
					seriesExample = table.getSeries(osi);
				}
			}
		}
		series.put(origRowKeysLabel, newRowKeys);
		
		if (seriesExample == null) {
			seriesExample = AbstractDataSeries.getNewSeries(type);
		}
		
		for (int ori = 0; ori < table.length(); ori++) {
			String key = origRowKeys == null ? (ori + "") : origRowKeys.get(ori).toString();

			series.put(key, oldSeries.containsKey(key) ? oldSeries.get(key) : seriesExample.getNewSeries());
			DataSeries<?> newSeries = series.get(key);
			
			newSeries.resize(newLength);
			
			for (int osi = 0, nri = 0; osi < table.seriesCount(); osi++) {
				if (osi != table.getRowKeyIndex()) {
					newSeries.set(nri, table.getSeries(osi).get(ori));
					nri++;
				}
			}
		}
	}
	
	private Class<?> getType(Class<?> current, DataSeries<?> series) {
		Class<?> other = series.getType();
		
		if (current == null) return other;
		
		if (other.isAssignableFrom(current)) return other;
		if (current.isAssignableFrom(other)) return current;
		
		if (Number.class.isAssignableFrom(current) && Number.class.isAssignableFrom(other)) {
			// The series don't store the same type. If either of them is double then we need double.
			if (current.equals(Double.class) || other.equals(Double.class)) {
				if (current.equals(Long.class) || other.equals(Long.class)) {
					System.err.println("Warning: transposing a table with long integers and real numbers, possible data loss on long data.");
				}
				return Double.class;
			}
			
			// If one is float and the other long we need double (double can't store the entire range of long, but can store most).
			if (current.equals(Float.class) || other.equals(Float.class)) {
				if (current.equals(Long.class) || other.equals(Long.class)) {
					System.err.println("Warning: transposing a table with long integers and real numbers, possible data loss on long data.");
					return Double.class;
				}
				
				if (current.equals(Integer.class) || other.equals(Integer.class)) return Double.class;
				
				return Float.class;				
			}
			
			if (current.equals(Long.class) || other.equals(Long.class)) return Double.class;
			if (current.equals(Integer.class) || other.equals(Integer.class)) return Integer.class;
			if (current.equals(Short.class) || other.equals(Short.class)) return Short.class;
			return Byte.class;
		}
				
		throw new IllegalArgumentException("Cannot transpose a table with incompatible series types.");
	}
}
