package hivis.common;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author O. J. Coleman
 */
public class UnmodifiableListMap<K, V> implements ListMap<K, V> {
	ListMap<K, V> lm;
	
	public UnmodifiableListMap(ListMap<K, V> listMap) {
		lm = listMap;
	}
	
	@Override
	public boolean isEmpty() {
		return lm.isEmpty();
	}

	@Override
	public int size() {
		return lm.size();
	}

	@Override
	public java.util.Map.Entry<K, V> remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
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
	public Map.Entry<K, V> get(int index) {
		return lm.get(index);
	}

	@Override
	public V get(Object key) {
		return lm.get(key);
	}

	@Override
	public V put(K key, V value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public V remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListSet<K> keySet() {
		return lm.keySet();
	}

	@Override
	public ListSet<V> values() {
		return lm.values();
	}

	@Override
	public ListSet<Map.Entry<K, V>> entrySet() {
		return lm.entrySet();
	}

	@Override
	public List<Map.Entry<K, V>> asList() {
		return lm.asList();
	}

	@Override
	public Map<K, V> asMap() {
		return this;
	}

	@Override
	public ListMap<K, V> unmodifiableView() {
		return this;
	}

}
