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

import hivis.data.DataSeries;

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
public class GroupedSeries<K, V> extends CalcMap<K, SeriesView<V>, DataSeries<V>> {
	/**
	 * The function used to generate group keys from values.
	 */
	protected Function<V, K> keyFunction;
	
	/**
	 * A map of all groups ever produced. Allows re-use of groups.
	 */
	protected Map<K, DataSeries<V>> allGroups = new HashMap<>();
	
	/**
	 * A map of all groups ever produced, as an unmodifiable view. Allows re-use of groups.
	 */
	protected Map<K, SeriesView<V>> allGroupsUnmod = new HashMap<>();
	
	public GroupedSeries(DataSeries<V> input) {
		super(input);
		keyFunction = new Function<V, K>() {
			@Override
			public K apply(V input) {
				return (K) input;
			}
		};
	}
	
	public GroupedSeries(DataSeries<V> input, Function<V, K> keyFuntion) {
		super(input);
		this.keyFunction = keyFuntion;
	}
	
	@Override
	public SeriesView<V> get(K key) {
		if (!allGroups.containsKey(key)) {
			DataSeries<V> newSeries = input.getNewSeries();
			allGroups.put(key, newSeries);
			allGroupsUnmod.put(key, newSeries.unmodifiableView());
		}
		return allGroupsUnmod.get(key);
	}
	
	@Override
	public void update() {
		// Build a new grouping.
		Map<K, List<V>> newGroups = new HashMap<>();
		
		for (V value : input) {
			K key = keyFunction.apply(value);
			
			List<V> group = null;
			if (newGroups.containsKey(key)) {
				group = newGroups.get(key);
			}
			else {
				group = new ArrayList<>();
				newGroups.put(key, group);
			}
			
			group.add(value);
		}
				
		// First, remove groups that no longer exist.
		for (K key : cache.keys().asArray()) {
			if (!newGroups.containsKey(key)) {
				// Clear it so that external observers of the group have 
				// up-to-date data (and get the correct change events). 
				allGroups.get(key).resize(0);
				cache.remove(key);
			}
		}
		
		// Second, update or add existing groups.
		for (K key : newGroups.keySet()) {
			List<V> newValues = newGroups.get(key);
			
			if (cache.containsKey(key)) {
				DataSeries<V> existingSeries = allGroups.get(key);
				
				// Collate change events (beginChanges).
				existingSeries.beginChanges(this);
				
				// If the new series is smaller/bigger then resize will trigger the values removed/added change event.
				existingSeries.resize(newValues.size());
				
				// Setting each value will produce change events iff the values are not equal.
				for (int i = 0; i < newValues.size(); i++) {
					existingSeries.set(i, newValues.get(i));
				}
				
				existingSeries.finishChanges(this);
			}
			else {
				DataSeries<V> newSeries;
				
				if (allGroups.containsKey(key)) {
					newSeries = allGroups.get(key);
					for (V value : newValues) {
						newSeries.append(value);
					}
				}
				else {
					newSeries = input.getNewSeries(newValues);
					allGroups.put(key, newSeries);
					allGroupsUnmod.put(key, newSeries.unmodifiableView());
				}
				
				cache.put(key, allGroupsUnmod.get(key));
			}
		}
	}
}
