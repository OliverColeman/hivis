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

package hivis.common;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Interface for a List of mappings from keys to values. Or a Map in which
 * entries are associated with contiguous, predictable indices. Iterators and
 * returned Sets and Collections iterate over the elements in the order defined
 * by the ListMap.
 * </p>
 * 
 * @author O. J. Coleman
 */
public interface ListMap<K, V> extends Map<K, V> {
	/**
	 * Removes the entry at the specified position in this list map. Shifts any
	 * subsequent entries to the left (subtracts one from their indices).
	 *
	 * @param index
	 *            the index of the entry to be removed
	 * @return the entry that was removed from the list
	 * @throws IndexOutOfBoundsException
	 *             {@inheritDoc}
	 */
	public Map.Entry<K, V> remove(int index);

	/**
	 * Removes all of the entries from this list map. The list map will be empty
	 * after this call returns.
	 */
	public void clear();

	/**
	 * Compares the specified object with this list map for equality. Returns
	 * {@code true} if and only if the specified object is also a ListMap, both
	 * ListMaps have the same size, and all corresponding pairs of entries in
	 * the two ListMaps are <i>equal</i>. (Two entries {@code e1} and {@code e2}
	 * are <i>equal</i> if {@code (e1==null ? e2==null :
	 * e1.equals(e2))}.) In other words, two lists are defined to be equal if
	 * they contain the same entries in the same order.
	 * <p>
	 *
	 * @param o
	 *            the object to be compared for equality with this list
	 * @return {@code true} if the specified object is equal to this list
	 */
	public boolean equals(Object o);

	/**
	 * Returns <tt>true</tt> if this list map contains a mapping for the
	 * specified key. More formally, returns <tt>true</tt> if and only if this
	 * map contains a mapping for a key <tt>k</tt> such that
	 * <tt>(key==null ? k==null : key.equals(k))</tt>. (There can be at most one
	 * such mapping.)
	 *
	 * @param key
	 *            key whose presence in this map is to be tested
	 * @return <tt>true</tt> if this map contains a mapping for the specified
	 *         key
	 * @throws ClassCastException
	 *             if the key is of an inappropriate type for this map (
	 *             <a href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException
	 *             if the specified key is null and this map does not permit
	 *             null keys (
	 *             <a href="Collection.html#optional-restrictions">optional</a>)
	 */
	boolean containsKey(Object key);

	/**
	 * Returns <tt>true</tt> if this list map maps one or more keys to the
	 * specified value. More formally, returns <tt>true</tt> if and only if this
	 * map contains at least one mapping to a value <tt>v</tt> such that
	 * <tt>(value==null ? v==null : value.equals(v))</tt>. This operation will
	 * probably require time linear in the map size for most implementations of
	 * the <tt>Map</tt> interface.
	 *
	 * @param value
	 *            value whose presence in this map is to be tested
	 * @return <tt>true</tt> if this map maps one or more keys to the specified
	 *         value
	 * @throws ClassCastException
	 *             if the value is of an inappropriate type for this map (
	 *             <a href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException
	 *             if the specified value is null and this map does not permit
	 *             null values (
	 *             <a href="Collection.html#optional-restrictions">optional</a>)
	 */
	boolean containsValue(Object value);

	/**
	 * Returns the entry at the specified position in this list.
	 *
	 * @param index
	 *            index of the entry to return
	 * @return the element at the specified position in this list
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range (
	 *             <tt>index &lt; 0 || index &gt;= size()</tt>)
	 */
	Map.Entry<K, V> get(int index);

	/**
	 * Returns the value to which the specified key is mapped, or {@code null}
	 * if this map contains no mapping for the key.
	 *
	 * <p>
	 * More formally, if this map contains a mapping from a key {@code k} to a
	 * value {@code v} such that {@code (key==null ? k==null :
	 * key.equals(k))}, then this method returns {@code v}; otherwise it returns
	 * {@code null}. (There can be at most one such mapping.)
	 *
	 * <p>
	 * If this map permits null values, then a return value of {@code null} does
	 * not <i>necessarily</i> indicate that the map contains no mapping for the
	 * key; it's also possible that the map explicitly maps the key to
	 * {@code null}. The {@link #containsKey containsKey} operation may be used
	 * to distinguish these two cases.
	 *
	 * @param key
	 *            the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or {@code null}
	 *         if this map contains no mapping for the key
	 * @throws ClassCastException
	 *             if the key is of an inappropriate type for this map (
	 *             <a href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException
	 *             if the specified key is null and this map does not permit
	 *             null keys (
	 *             <a href="Collection.html#optional-restrictions">optional</a>)
	 */
	V get(Object key);

	// Modification Operations

	/**
	 * Associates the specified value with the specified key in this list map
	 * (optional operation). If the map previously contained a mapping for the
	 * key, the old value is replaced by the specified value at the same index,
	 * otherwise a new mapping is added to the end of the list. (A map
	 * <tt>m</tt> is said to contain a mapping for a key <tt>k</tt> if and only
	 * if {@link #containsKey(Object) m.containsKey(k)} would return
	 * <tt>true</tt>.)
	 *
	 * @param key
	 *            key with which the specified value is to be associated
	 * @param value
	 *            value to be associated with the specified key
	 * @return the previous value associated with <tt>key</tt>, or <tt>null</tt>
	 *         if there was no mapping for <tt>key</tt>. (A <tt>null</tt> return
	 *         can also indicate that the map previously associated
	 *         <tt>null</tt> with <tt>key</tt>, if the implementation supports
	 *         <tt>null</tt> values.)
	 * @throws UnsupportedOperationException
	 *             if the <tt>put</tt> operation is not supported by this map
	 * @throws ClassCastException
	 *             if the class of the specified key or value prevents it from
	 *             being stored in this map
	 * @throws NullPointerException
	 *             if the specified key or value is null and this map does not
	 *             permit null keys or values
	 * @throws IllegalArgumentException
	 *             if some property of the specified key or value prevents it
	 *             from being stored in this map
	 */
	V put(K key, V value);

	/**
	 * Removes the mapping for a key from this map if it is present (optional
	 * operation). Shifts any subsequent entries to the left (subtracts one from
	 * their indices). More formally, if this map contains a mapping from key
	 * <tt>k</tt> to value <tt>v</tt> such that
	 * <code>(key==null ?  k==null : key.equals(k))</code>, that mapping is
	 * removed. (The map can contain at most one such mapping.)
	 *
	 * <p>
	 * Returns the value to which this map previously associated the key, or
	 * <tt>null</tt> if the map contained no mapping for the key.
	 *
	 * <p>
	 * If this map permits null values, then a return value of <tt>null</tt>
	 * does not <i>necessarily</i> indicate that the map contained no mapping
	 * for the key; it's also possible that the map explicitly mapped the key to
	 * <tt>null</tt>.
	 *
	 * <p>
	 * The map will not contain a mapping for the specified key once the call
	 * returns.
	 *
	 * @param key
	 *            key whose mapping is to be removed from the map
	 * @return the previous value associated with <tt>key</tt>, or <tt>null</tt>
	 *         if there was no mapping for <tt>key</tt>.
	 * @throws UnsupportedOperationException
	 *             if the <tt>remove</tt> operation is not supported by this map
	 * @throws ClassCastException
	 *             if the key is of an inappropriate type for this map (
	 *             <a href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException
	 *             if the specified key is null and this map does not permit
	 *             null keys (
	 *             <a href="Collection.html#optional-restrictions">optional</a>)
	 */
	V remove(Object key);

	/**
	 * Copies all of the mappings from the specified map to this map (optional
	 * operation). The effect of this call is equivalent to that of calling
	 * {@link #put(Object,Object) put(k, v)} on this map once for each mapping
	 * from key <tt>k</tt> to value <tt>v</tt> in the specified map. The
	 * behavior of this operation is undefined if the specified map is modified
	 * while the operation is in progress.
	 *
	 * @param m
	 *            mappings to be stored in this map
	 * @throws UnsupportedOperationException
	 *             if the <tt>putAll</tt> operation is not supported by this map
	 * @throws ClassCastException
	 *             if the class of a key or value in the specified map prevents
	 *             it from being stored in this map
	 * @throws NullPointerException
	 *             if the specified map is null, or if this map does not permit
	 *             null keys or values, and the specified map contains null keys
	 *             or values
	 * @throws IllegalArgumentException
	 *             if some property of a key or value in the specified map
	 *             prevents it from being stored in this map
	 */
	void putAll(Map<? extends K, ? extends V> m);

	/**
	 * Returns an unmodifiable {@link Set} view of the keys contained in this
	 * list map. The set is backed by the list map, so changes to the list map
	 * are reflected in the set, and vice-versa. If the list map is modified
	 * while an iteration over the set is in progress, the results of the
	 * iteration are undefined. The iterator for the Set returns keys in the
	 * order defined by this list map.
	 *
	 * @return a set view of the keys contained in this list map
	 */
	ListSet<K> keySet();

	/**
	 * Returns an unmodifiable {@link Collection} view of the values contained
	 * in this list map. The collection is backed by the list map, so changes to
	 * the list map are reflected in the collection, and vice-versa. If the list
	 * map is modified while an iteration over the collection is in progress the
	 * results of the iteration are undefined. The iterator for the Collection
	 * returns values in the order defined by this list map.
	 * 
	 * @return a collection view of the values contained in this list map
	 */
	ListSet<V> values();

	/**
	 * Returns an unmodifiable {@link Set} view of the mappings contained in
	 * this list map. The set is backed by the list map, so changes to the list
	 * map are reflected in the set, and vice-versa. If the list map is modified
	 * while an iteration over the set is in progress the results of the
	 * iteration are undefined. The iterator for the Set returns mappings in the
	 * order defined by this list map.
	 *
	 * @return a set view of the mappings contained in this list map
	 */
	ListSet<Map.Entry<K, V>> entrySet();

	/**
	 * Returns an (unmodifiable) List view of this ListMap.
	 */
	public List<Map.Entry<K, V>> asList();

	/**
	 * Returns a Map view of this ListMap, with the property that the equals
	 * method will return true according to the usual contract of
	 * {@link java.util.Map#equals(Object)}
	 */
	public Map<K, V> asMap();

	/**
	 * Returns an unmodifiable view of this ListMap.
	 */
	public ListMap<K, V> unmodifiableView();
}
