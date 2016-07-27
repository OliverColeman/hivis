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