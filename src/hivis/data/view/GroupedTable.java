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
import java.util.Map;

import hivis.common.HV;
import hivis.data.DataRow;
import hivis.data.DataSeries;
import hivis.data.DataTable;

/**
 * <p>
 * Create a view of a DataSeries containing the values in the DataSeries
 * collected into groups.
 * </p>
 * <p>
 * If no key function is provided then groups are formed by placing all values
 * for which v1.equals(v2) into the same group (and values where !v1.equals(v2)
 * into different groups). The key for each group is a value such that
 * key.equals(v) for all values in the group.
 * </p>
 * <p>
 * If a key function is provided then a key is generated for each value in the
 * series, and groups formed such that the keys for all values in a group
 * satisfy k1.equals(k2).
 * </p>
 * <p>
 * The values in the groups appear in the same order as their order in the
 * series.
 * </p>
 * <p>
 * The series returned by {@link #get(Object)} and {@link #values()} will be
 * emptied (set to length 0) if the group size becomes zero (and calls to
 * {@link #get(Object)} for groups that do not yet exist will create empty
 * series, but not add them to the list of groups returned by {@link #values()}
 * ). If the group size subsequently becomes non-zero this same series will be
 * reused. This allows external observers to monitor the size of a group (even
 * before it has existed in the input series).
 * </p>
 *
 * @author O. J. Coleman
 */
public class GroupedTable<K> extends CalcMap<K, TableView, DataTable> {
	/**
	 * The function used to generate group keys from values.
	 */
	protected Function<DataRow, K> keyFunction;
	
	/**
	 * A map of all groups ever produced. Allows re-use of groups.
	 */
	protected Map<K, TableViewFilterRows> allGroups = new HashMap<>();
	
	public GroupedTable(DataTable input, int groupingSeries) {
		super(input);
		keyFunction = new Function<DataRow, K>() {
			@Override
			public K apply(DataRow input) {
				return (K) input.get(groupingSeries);
			}
		};
	}
	
	public GroupedTable(DataTable input, String groupingSeries) {
		super(input);
		keyFunction = new Function<DataRow, K>() {
			@Override
			public K apply(DataRow input) {
				return (K) input.get(groupingSeries);
			}
		};
	}
	
	public GroupedTable(DataTable input, Function<DataRow, K> keyFuntion) {
		super(input);
		this.keyFunction = keyFuntion;
	}
	
	@Override
	public TableView get(K key) {
		if (!allGroups.containsKey(key)) {
			TableViewFilterRows newGroup = new TableViewFilterRows(input, new GroupRowFilter(key));
			allGroups.put(key, newGroup);
		}
		return allGroups.get(key);
	}
	
	@Override
	public void update() {
		// Determine current groups.
		Map<K, TableView> newGroups = new HashMap<>();
		
		for (DataRow row : input) {
			K key = keyFunction.apply(row);
			if (!newGroups.containsKey(key)) {
				TableViewFilterRows group = (TableViewFilterRows) get(key);
				newGroups.put(key, group);
			}
		}
				
		// Remove groups that no longer exist.
		for (K key : cache.keys().asArray()) {
			if (!newGroups.containsKey(key)) {
				cache.remove(key);
			}
		}
		
		// Add new groups.
		for (K key : newGroups.keySet()) {
			if (!cache.containsKey(key)) {
				cache.put(key, newGroups.get(key));
			}
		}
	}
	
	private class GroupRowFilter implements RowFilter {
		K key;
		GroupRowFilter(K key) {
			this.key = key;
		}
		@Override
		public boolean excludeRow(DataTable input, int index) {
			return !keyFunction.apply(input.getRow(index)).equals(key);
		}
	}
}
