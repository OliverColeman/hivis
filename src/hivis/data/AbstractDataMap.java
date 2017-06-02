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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import com.google.common.base.Strings;

import hivis.common.LSListMap;
import hivis.common.ListMap;
import hivis.data.view.SeriesView;

/**
 * Base class for {@link DataMap} implementations.
 * 
 * @author O. J. Coleman
 */
public abstract class AbstractDataMap<K, V> extends DataDefault implements DataMap<K, V> {
	private int equalToHashCode = 0; // cached hashcode for equalToHashCode().
	
	@Override
	public boolean equalTo(Data data) {
		if (data == this) return true;
		if (!(data instanceof DataMap)) return false;
		DataMap<?, ?> map = (DataMap<?, ?>) data;
		try {
			this.lock();
			try {
				map.lock();
				return this.keys().equalTo(map.keys()) && this.values().equalTo(map.values());
			}
			finally {
				map.unlock();
			}
		}
		finally {
			this.unlock();
		}
	}

	@Override
	public int equalToHashCode() {
		if (isMutable()) {
			throw new IllegalStateException("equalToHashCode() called on a mutable Data set.");
		}
		if (equalToHashCode == 0) {
			equalToHashCode = 31 * this.keys().equalToHashCode() + this.values().equalToHashCode();
		}
		return equalToHashCode;
	}
	
	
	@Override
	public String toString() {
		lock();
		try {
			ListMap<String, String[]> values = new LSListMap<>();
			
			boolean multiLineValues = false;
			for (K key : keys()) {
				String[] valLines = get(key).toString().split("\n");
				values.put(key.toString(), valLines);
				multiLineValues |= valLines.length > 1;
			}
			
			String sep = multiLineValues ? ",\n\n\t" : ",\n\t";
			
			StringJoiner sj = new StringJoiner(sep, "DataMap (" + size() + ") [ " + (size() > 1 ? "\n\t" : ""), (size() > 1 ? "\n" : "") + " ]");
			
			for (String key : values.keySet()) {
				String[] valLines = values.get(key);
				String keyArrow = key + " => ";
				StringJoiner valSJ = new StringJoiner("\n\t");
				valSJ.add(keyArrow + valLines[0]);
				String keyArrowPad = Strings.repeat(" ", keyArrow.length());
				for (int i = 1; i < valLines.length; i++) {
					valSJ.add(keyArrowPad + valLines[i]);
				}
				sj.add(valSJ.toString());
			}
			
			return sj.toString();
		}
		finally {
			unlock();
		}
	}
	

	@Override
	public DataMap<K, V> immutableCopy() {
		lock();
		try {
			// Get immutable copies of the key and value series so 
			// that we get immutable copies of the elements therein,
			// and can also return immutable series in the keys() and values() methods.
			final DataSeries<K> keys = keys().immutableCopy();
			final DataSeries<V> values = values().immutableCopy();
			final Map<K, V> map = new HashMap<>();
			for (int i = 0; i < keys.length(); i++) {
				map.put(keys.get(i), values.get(i));
			}
			return new AbstractUnmodifiableDataMap<K, V>() {
				@Override
				public boolean isMutable() {
					return false;
				}
				@Override
				public V get(K key) {
					return map.get(key);
				}
				@Override
				public boolean containsKey(K key) {
					return map.containsKey(key);
				}
				@Override
				public int size() {
					return keys.length();
				}
				@Override
				public DataSeries<K> keys() {
					return keys;
				}
				@Override
				public DataSeries<V> values() {
					return values;
				}
			};
		}
		finally {
			unlock();
		}	
	}
}
