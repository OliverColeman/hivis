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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.AbstractMap;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;


/**
 * <p>A {@link ListMap} backed by {@link ListSet}s.
 * Getting, putting and containsKey operations take O(1) time. 
 * Removal and containsValue take O(n) time.
 * 
 * <p><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access an instance concurrently,
 * and at least one of the threads modifies it structurally, it
 * <i>must</i> be synchronized externally.  (A structural modification is
 * any operation that adds or deletes one or more entries.)  
 * This is typically accomplished by
 * synchronizing on some object that naturally encapsulates the list.<br/>
 * TODO add synchronized wrapper for ListMa.</p>
 *
 * <p><a name="fail-fast"/>
 * The iterators returned by the Sets and Collections returned by methods 
 * of this class are <em>fail-fast</em>:
 * if the ListMap is structurally modified at any time after the iterator is
 * created, the iterator will throw a
 * {@link ConcurrentModificationException}.  Thus, in the face of
 * concurrent modification, the iterator fails quickly and cleanly, rather
 * than risking arbitrary, non-deterministic behavior at an undetermined
 * time in the future.</p>
 *
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw {@code ConcurrentModificationException} on a best-effort basis.
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness:  <i>the fail-fast behavior of iterators
 * should be used only to detect bugs.</i></p>
 * 
 * @author O. J. Coleman
 */
public class LSListMap<K, V> implements ListMap<K, V> {
	private ListSet<K> keys;
	private ListSet<Map.Entry<K, V>> list;
	// This is maintained simply to support the values() method without 
	// having to rebuild it every time. It is lazily constructed.
	private List<V> values;
	
	private ListSet<K> keysUnmod;
	private ListSet<Map.Entry<K, V>> listUnmod;
	private List<V> valuesUnmod;
	private ListMap<K, V> unmodView;
	
	/**
	 * Creates an empty LSListMap.
	 */
	public LSListMap() {
		keys = new BMListSet<>();
		keysUnmod = keys.unmodifiableView();
		list = new BMListSet<>();
		listUnmod = list.unmodifiableView();
		unmodView = new UnmodifiableListMap<>(this);
	}
	
	/**
	 * Creates a HashMapListMap containing the elements in the specified
     * map, in the order they are returned by the map's
     * iterator.
     *
     * @param c the map whose entries are to be placed into this list map
     * @throws NullPointerException if the specified collection is null
	 */
	public LSListMap(Map<? extends K, ? extends V> c) {
		keys = new BMListSet<>();
		keysUnmod = keys.unmodifiableView();
		list = new BMListSet<>();
		listUnmod = list.unmodifiableView();
    	this.putAll(c);
    }

	@Override
	public void clear() {
		keys.clear();
		list.clear();
		if (values != null) values.clear();
	}

	@Override
	public Map.Entry<K, V> get(int index) {
		return list.get(index);
	}

	@Override
	public boolean isEmpty() {
		return keys.isEmpty();
	}

	@Override
	public V remove(Object key) {
		if (!keys.contains(key)) {
			return null;
		}
		int index = keys.indexOf(key);
		keys.remove(index);
		values = null;
		return list.remove(index).getValue();
	}
	
	@Override
	public Map.Entry<K, V> remove(int index) {
		keys.remove(index);
		values = null;
		return list.remove(index);
	}

	@Override
	public boolean containsKey(Object key) {
		return keys.contains(key);
	}

	@Override
	public boolean containsValue(Object value) {
		for (Map.Entry<K, V> entry : list) {
			if (entry.getValue().equals(value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int indexOfKey(Object key) {
		return keys.indexOf(key);
	}

	@Override
	public int indexOfValue(Object value) {
		return values.indexOf(value);
	}


	@Override
	public V get(Object key) {
		if (!keys.contains(key)) return null;
		return list.get(keys.indexOf(key)).getValue();
	}

	@Override
	public V put(K key, V value) {
		if (containsKey(key)) {
			int index = keys.indexOf(key);
			if (values != null) {
				values.set(index, value);
			}
			return list.set(index, new AbstractMap.SimpleEntry<>(key, value)).getValue();
		}
		else {
			keys.add(key);
			list.add(new AbstractMap.SimpleEntry<>(key, value));
			if (values != null) {
				values.add(value);
			}
			return null;
		}
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public ListSet<K> keySet() {
		return keysUnmod;
	}

	@Override
	public List<V> values() {
		if (values == null) {
			values = new ArrayList<>();
			for (Map.Entry<K, V> entry : list) {
				values.add(entry.getValue());
			}
			valuesUnmod = Collections.unmodifiableList(values);
		}
		return valuesUnmod;
	}

	@Override
	public ListSet<Map.Entry<K, V>> entrySet() {
		return listUnmod;
	}

	@Override
	public int size() {
		return keys.size();
	}
	
	@Override
	public List<Map.Entry<K, V>> asList() {
		return list.unmodifiableView();
	}

	@Override
	public Map<K, V> asMap() {
		return new MapFace<>(this);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ListMap))
            return false;
        
        // Use iterators because they'll fail fast in the face of 
        // structural modifications while we're checking for equality.
        ListIterator<Map.Entry<K, V>> e1 = list.listIterator();
        ListIterator e2 = ((ListMap) o).asList().listIterator();
        while (e1.hasNext() && e2.hasNext()) {
        	Map.Entry<K, V> o1 = e1.next();
            Object o2 = e2.next();
            if (!(o1==null ? o2==null : o1.equals(o2)))
                return false;
        }
        return !(e1.hasNext() || e2.hasNext());
    }


	@Override
	public ListMap<K, V> unmodifiableView() {
		return unmodView;
	}
	
	
	/**
     * Returns a string representation of this list map.  The string representation
     * consists of a list of key-value mappings in the order returned by the
     * list map's <tt>entrySet</tt> view's iterator, enclosed in braces
     * (<tt>"{}"</tt>).  Adjacent mappings are separated by the characters
     * <tt>", "</tt> (comma and space).  Each key-value mapping is rendered as
     * the key followed by an equals sign (<tt>"="</tt>) followed by the
     * associated value.  Keys and values are converted to strings as by
     * {@link String#valueOf(Object)}.
     *
     * @return a string representation of this map
     */
    public String toString() {
        Iterator<Entry<K,V>> i = entrySet().iterator();
        if (! i.hasNext())
            return "{}";

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (;;) {
            Entry<K,V> e = i.next();
            K key = e.getKey();
            V value = e.getValue();
            sb.append(key   == this ? "(this Map)" : key);
            sb.append('=');
            sb.append(value == this ? "(this Map)" : value);
            if (! i.hasNext())
                return sb.append('}').toString();
            sb.append(',').append(' ');
        }
    }
	
	
	public static class MapFace<K, V> implements Map<K, V> {
		ListMap<K, V> lm;
		
		public MapFace(ListMap<K, V> lm) {
			this.lm = lm;
		}

		@Override
		public void clear() {
			lm.clear();
		}

		@Override
		public boolean containsKey(Object key) {
			return lm.containsKey(key);
		}

		@Override
		public boolean containsValue(Object value) {
			return lm.containsValue(value);
		}

		@Override
		public Set<java.util.Map.Entry<K, V>> entrySet() {
			return lm.entrySet();
		}

		@Override
		public V get(Object key) {
			return lm.get(key);
		}

		@Override
		public boolean isEmpty() {
			return lm.isEmpty();
		}

		@Override
		public Set<K> keySet() {
			return lm.keySet();
		}
		
		@Override
		public V put(K key, V value) {
			return lm.put(key, value);
		}

		@Override
		public void putAll(Map<? extends K, ? extends V> m) {
			lm.putAll(m);
		}

		@Override
		public V remove(Object key) {
			return lm.remove(key);
		}

		@Override
		public int size() {
			return lm.size();
		}

		@Override
		public Collection<V> values() {
			return lm.values();
		}
		
		@Override
		public boolean equals(Object o) {
			if (o == this)
	            return true;

	        if (!(o instanceof Map)) {
	            return false;
	        }
	        Map<K,V> m = (Map<K,V>) o;
	        if (m.size() != size()) {
	            return false;
	        }
	        try {
	            Iterator<Entry<K,V>> i = entrySet().iterator();
	            while (i.hasNext()) {
	                Entry<K,V> e = i.next();
	                K key = e.getKey();
	                V value = e.getValue();
	                if (value == null) {
	                    if (!(m.get(key)==null && m.containsKey(key)))
	                        return false;
	                } else {
	                    if (!value.equals(m.get(key)))
	                        return false;
	                }
	            }
	        } catch (ClassCastException unused) {
	            return false;
	        } catch (NullPointerException unused) {
	            return false;
	        }

	        return true;
		}
	}
	
	
	// Quick and dirty testing for ListSet implementations.
	public static void main(String[] args) {
		Random r = new Random();
		
		
		// Test as map.
		for (int t = 0; t < 100; t++) {
			Map<Long, Double> s1 = new HashMap<>();
			LSListMap<Long, Double> s2 = new LSListMap<>();
			
			Double valD;
			Long valL;
			Map<Long, Double> col;
			for (int i = 1; i < 1000; i++) {
				long c = r.nextInt(i)+2;
				
				switch (r.nextInt(12)) {
				case 0: 
					s1.clear();
					s2.clear();
					if (!s1.equals(s2)) throw new RuntimeException("clear");
					break;
				case 1: 
					if (s1.containsKey(c*2) != s2.containsKey(c*2)) throw new RuntimeException("containsKey");
					break;
				case 2: 
					valD = r.nextBoolean() ? r.nextDouble() : (s1.isEmpty() ? 0.0 : s1.values().iterator().next());
					if (s1.containsValue(valD) != s2.containsValue(valD)) throw new RuntimeException("containsValue");
					break;
				case 3: 
					if (!s1.entrySet().equals(s2.entrySet())) throw new RuntimeException("entrySet");
				case 4: 
					if (!s2.asMap().equals(s1)) throw new RuntimeException("equals\n" + s1.toString() + "\n" + s2.toString());
					break;
				case 5:
					valL = (long) (r.nextBoolean() ? (double) r.nextInt() : (s1.isEmpty() ? 0L : s1.keySet().iterator().next()));
					if (!Util.equalsIncNull(s1.get(valL), s2.get(valL))) throw new RuntimeException("get");
				case 6: 
					if (s1.isEmpty() != s2.isEmpty()) throw new RuntimeException("isEmpty");
				case 7: 
					if (!s2.keySet().asSet().equals(s1.keySet())) throw new RuntimeException("keySet");
					break;
				case 8: 
					valD = Math.random();
					s1.put(c, valD);
					s2.put(c, valD);
					if (!s1.equals(s2.asMap())) throw new RuntimeException("put");
					break;
				case 9:
					col = newrl(r, 10);
					s1.putAll(col);
					s2.putAll(col);
					if (!s1.equals(s2.asMap())) throw new RuntimeException("putAll");
					break;
				case 10:
					s1.remove(c/2);
					s2.remove(c/2);
					if (!s1.equals(s2)) throw new RuntimeException("remove");
					break;
				case 11:
					if (!s1.values().containsAll(s2.values())) throw new RuntimeException("values\n" + s1.values().toString() + "\n" + s2.values().toString());
				}
			}
		}
		
		System.out.println("done, no errors");
	}
	
	private static Map<Long, Double> newrs(Random r, long size, long offset) {
		Map<Long, Double> c = new HashMap<>();
		int actSize = r.nextInt((int) size) + 1;
		for (int i = 0; i < actSize; i++) {
			c.put((long) r.nextInt((int) size) + offset, r.nextDouble());
		}
		return c;
	}
	private static Map<Long, Double> newrl(Random r, long size) {
		Map<Long, Double> c = new HashMap<>();
		int actSize = r.nextInt((int) size) + 1;
		for (int i = 0; i < actSize; i++) {
			c.put((long) r.nextInt((int) size), r.nextDouble());
		}
		return c;
	}}
