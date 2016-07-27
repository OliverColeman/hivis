package hivis.common;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Represent a ListSet as a Set.
 * 
 * @see ListSet#asSet()
 * 
 * @author O. J. Coleman
 */
class SetFace<T> implements Set<T> {
	ListSet<T> ls;
	
	public SetFace(ListSet<T> ls) {
		this.ls = ls;
	}

	@Override
	public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof Set))
            return false;
        Collection c = (Collection) o;
        if (c.size() != size())
            return false;
        try {
            return containsAll(c);
        } catch (ClassCastException unused)   {
            return false;
        } catch (NullPointerException unused) {
            return false;
        }
    }

	@Override
	public boolean add(T e) {
		return ls.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return ls.addAll(c);
	}

	@Override
	public void clear() {
		ls.clear();
	}

	@Override
	public boolean contains(Object e) {
		return ls.contains(e);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return ls.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		return ls.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		return ls.iterator();
	}

	@Override
	public boolean remove(Object e) {
		return ls.remove(e);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return ls.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return ls.retainAll(c);
	}

	@Override
	public int size() {
		return ls.size();
	}

	@Override
	public Object[] toArray() {
		return ls.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return ls.toArray(a);
	}
}