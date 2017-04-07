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

import hivis.common.LSListMap;
import hivis.common.ListMap;
import hivis.data.view.CalcSeries;
import hivis.data.view.SeriesView;

/**
 * Default implementation of {@link DataTable}.
 * 
 * @author O. J. Coleman
 */
public class DataMapDefault<K, V> extends AbstractDataMap<K, V> {
	private ListMap<K, V> map;
	private SeriesView<K> keys;
	private SeriesView<V> values;

	public DataMapDefault() {
		map = new LSListMap<>();
		keys = new KeySeries();
		values = new ValueSeries();
	}
	
	@Override
	public void beginChanges(Object changer) {
		super.beginChanges(changer);
		keys.beginChanges(changer);
		values.beginChanges(changer);
	}

	@Override
	public void finishChanges(Object changer) {
		super.finishChanges(changer);
		keys.finishChanges(changer);
		values.finishChanges(changer);
	}

	@Override
	public synchronized V put(K key, V value) {
		boolean keyExists = map.containsKey(key);

		V existing = map.get(key);

		if (keyExists && (existing == null ? value == null : existing.equals(value))) {
			return existing;
		}

		map.put(key, value);
		if (keyExists) {
			this.setDataChanged(MapChange.Changed);
			values.setDataChanged(DataSeriesChange.ValuesChanged);
		} else {
			this.setDataChanged(MapChange.Added);
			keys.setDataChanged(DataSeriesChange.ValuesAdded);
			values.setDataChanged(DataSeriesChange.ValuesAdded);
		}
		return existing;
	}

	@Override
	public synchronized V remove(K key) {
		if (map.containsKey(key)) {
			V existing = map.get(key);
			map.remove(key);
			this.setDataChanged(MapChange.Removed);
			keys.setDataChanged(DataSeriesChange.ValuesRemoved);
			values.setDataChanged(DataSeriesChange.ValuesRemoved);
			return existing;
		}
		return null;
	}

	@Override
	public V get(K key) {
		if (map.containsKey(key)) {
			return map.get(key);
		}
		return values.getEmptyValue();
	}

	@Override
	public boolean containsKey(K key) {
		return map.containsKey(key);
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public SeriesView<K> keys() {
		return keys;
	}

	@Override
	public SeriesView<V> values() {
		return values;
	}

	private class KeySeries extends AbstractUnmodifiableDataSeries<K> {
		@Override
		public int length() {
			return DataMapDefault.this.map.size();
		}
		@Override
		public K get(int index) {
			return DataMapDefault.this.map.get(index).getKey();
		}
		@Override
		public void update(DataEvent cause) {
			// Nothing to update.
		}
	};

	private class ValueSeries extends AbstractUnmodifiableDataSeries<V> {
		@Override
		public int length() {
			return DataMapDefault.this.map.size();
		}
		@Override
		public V get(int index) {
			return DataMapDefault.this.map.get(index).getValue();
		}
		@Override
		public void update(DataEvent cause) {
			// Nothing to update.
		}
	};

}
