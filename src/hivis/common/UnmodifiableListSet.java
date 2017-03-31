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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.function.UnaryOperator;

/**
 * An unmodifiable view of a ListSet.
 * 
 * @author O. J. Coleman
 */
class UnmodifiableListSet<T> implements ListSet<T> {
	List<T> ls;
	Set<T> set;
	
	public UnmodifiableListSet(ListSet<T> ls) {
		this.ls = Collections.unmodifiableList(ls);
		this.set = Collections.unmodifiableSet(new SetFace<T>(ls));
	}

	@Override
	public boolean equals(Object o) {
        return ls.equals(o);
    }

	@Override
	public boolean add(T e) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
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
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
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

	@Override
	public T get(int index) {
		return ls.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return ls.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return ls.lastIndexOf(o);
	}

	@Override
	public T set(int index, T element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(int index, T element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public T remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return ls.listIterator(index);
	}

	@Override
	public ListIterator<T> listIterator() {
		return ls.listIterator();
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return ls.subList(fromIndex, toIndex);
	}

	@Override
	public List<T> asList() {
		return this;
	}

	@Override
	public Set<T> asSet() {
		return set;
	}

	@Override
	public ListSet<T> unmodifiableView() {
		return this;
	}
	
	@Override
	public String toString() {
		return ls.toString();
	}

	@Override
	public void replaceAll(UnaryOperator<T> operator) {
		throw new UnsupportedOperationException();
	}
}