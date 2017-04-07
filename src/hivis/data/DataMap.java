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

import hivis.data.view.SeriesView;

/**
 * Represents a mapping from keys to (collections of) items. This is primarily
 * used for the series and table grouping methods.
 * 
 * @author O. J. Coleman
 */
public interface DataMap<K, V> extends Data {
	/**
	 * Get the value for the given key if present otherwise the equivalent of calling {@link DataSeries#getEmptyValue()} on the series returned by {@link #values()}. 
	 */
	V get(K key);

	/**
	 * Returns true iff the given key is present in the set of keys.
	 */
	boolean containsKey(K key);

	/**
	 * Assigns the given value to the given key (if there is not already a value assigned to the given key which {@link Object#equals(Object)) the given value), 
	 * replacing the existing value if present. Optional operation.
	 * 
	 * @return the existing value, or null if not present.
	 */
	V put(K key, V value);

	/**
	 * Remove the given key and associated value from this map. Optional
	 * operation.
	 * 
	 * @return The value removed, or null if the key was not present.
	 */
	V remove(K key);

	/**
	 * Get the number of key-value pairs in this map.
	 */
	int size();

	/**
	 * Get a view of the keys in this map. The keys will have the same order as
	 * the values provided by {@link #values()}.
	 */
	SeriesView<K> keys();

	/**
	 * Get a view of the values in this map. The values will have the same order
	 * as the keys provided by {@link #keys()}.
	 */
	SeriesView<V> values();
}
