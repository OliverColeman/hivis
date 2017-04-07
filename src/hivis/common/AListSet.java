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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.UnaryOperator;

/**
 * <p>A {link ListSet} backed by an {@link ArrayList}.
 * Addition (appending to the end of the list) and contains operations take 
 * O(1) time. Insertion and removal take O(n) time (excepting at the end of 
 * the list, in which case it is O(1)). Index-of operations take O(n) time.
 * 
 * <p><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access an instance concurrently,
 * and at least one of the threads modifies it structurally, it
 * <i>must</i> be synchronized externally.  (A structural modification is
 * any operation that adds or deletes one or more elements, or explicitly
 * resizes the backing array; merely setting the value of an element is not
 * a structural modification.)  This is typically accomplished by
 * synchronizing on some object that naturally encapsulates the list.
 *
 * If no such object exists, the list should be "wrapped" using the
 * {@link Collections#synchronizedList}
 * method.  This is best done at creation time, to prevent accidental
 * unsynchronized access to the list:<pre>
 *   List list = Collections.synchronizedList(new ArrayList(...));</pre></p>
 *
 * <p><a name="fail-fast"/>
 * The iterators returned by this class's {@link #iterator() iterator} and
 * {@link #listIterator(int) listIterator} methods are <em>fail-fast</em>:
 * if the list is structurally modified at any time after the iterator is
 * created, in any way except through the iterator's own
 * {@link ListIterator#remove()} or
 * {@link ListIterator#add(Object)} methods, the iterator will throw a
 * {@link ConcurrentModificationException}.  Thus, in the face of
 * concurrent modification, the iterator fails quickly and cleanly, rather
 * than risking arbitrary, non-deterministic behaviour at an undetermined
 * time in the future.</p>
 *
 * <p>Note that the fail-fast behaviour of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw {@code ConcurrentModificationException} on a best-effort basis.
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness:  <i>the fail-fast behaviour of iterators
 * should be used only to detect bugs.</i></p>
 *  
 * @author O. J. Coleman
 */
public class AListSet<E> extends ArrayList<E> implements ListSet<E> {
	private static final long serialVersionUID = 1L;
	
	private HashSet<E> set = new HashSet<E>();
	
	
	/**
     * Constructs an empty list with the specified initial capacity.
     *
     * @param   initialCapacity   the initial capacity of the list
     * @exception IllegalArgumentException if the specified initial capacity
     *            is negative
     */
    public AListSet(int initialCapacity) {
        super(initialCapacity);
    }

    
    /**
     * Constructs an empty list with an initial capacity of ten.
     */
    public AListSet() {
        super(10);
    }
	
    
    /**
     * Constructs a list containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator. If any items are duplicated, according to their equals
     * method, then only the first item is included.
     *
     * @param c the collection whose elements are to be placed into this list
     * @throws NullPointerException if the specified collection is null
     */
    public AListSet(Collection<? extends E> c) {
    	this.addAll(c);
    }

    
    /**
	 * {@inheritDoc}
	 */
	@Override
    public boolean contains(Object o) {
        return set.contains(o);
    }


    /**
     * Returns a shallow copy of this <tt>ArrayListSet</tt> instance.  (The
     * elements themselves are not copied.)
     *
     * @return a clone of this <tt>ArrayListSet</tt> instance
     */
    public Object clone() {
    	return new AListSet<>(this);
    }

    
    /**
	 * {@inheritDoc}
	 */
	@Override
    public E set(int index, E element) {
		rangeCheck(index);
		E current = get(index);
		if (element == null ? current == null : element.equals(current)) {
			return current;
		}
		checkNotContains(element);
    	set.add(element);
    	return super.set(index, element);
    }

    /**
	 * {@inheritDoc}
	 */
	@Override
    public boolean add(E e) {
    	if (!contains(e)) {
    		set.add(e);
    		return super.add(e);
    	}
        return false;
    }

    /**
	 * {@inheritDoc}
	 */
	@Override
    public void add(int index, E element) {
		checkNotContains(element);
    	super.add(index, element);
    	set.add(element);
    }
	
    /**
	 * {@inheritDoc}
	 */
	@Override
	public void replaceAll(UnaryOperator<E> operator) {
		ListIterator<E> li = listIterator();
		while (li.hasNext()) {
			li.set(operator.apply(li.next()));
		}
	}

    /**
	 * {@inheritDoc}
	 */
	@Override
    public E remove(int index) {
    	E element = super.remove(index);
    	set.remove(element);
    	return element;
    }

    /**
	 * {@inheritDoc}
	 */
	@Override
    public boolean remove(Object o) {
    	if (contains(o)) {
    		super.remove(o);
    		set.remove(o);
    		return true;
    	}
        return false;
    }

    /**
	 * {@inheritDoc}
	 */
	@Override
    public void clear() {
    	super.clear();
    	set.clear();
    }

    /**
	 * {@inheritDoc}
	 */
	@Override
    public boolean addAll(Collection<? extends E> c) {
    	int origSize = size();
    	for (E element : c) {
    		if (set.add(element)) {
    			super.add(element);
    		}
    	}
    	return origSize != size();
    }

    /**
	 * {@inheritDoc}
	 */
	@Override
    public boolean addAll(int index, Collection<? extends E> c) {
		rangeCheckForAdd(index);
    	ArrayList<E> added = new ArrayList<>(c.size());
    	for (E element : c) {
    		if (set.add(element)) {
    			added.add(element);
    		}
    	}
    	if (!added.isEmpty()) {
    		super.addAll(index, added);
    		return true;
    	}
    	return false;
    }

    /**
	 * {@inheritDoc}
	 */
	@Override
    protected void removeRange(int fromIndex, int toIndex) {
		rangeCheck(fromIndex);
		rangeCheckForAdd(toIndex);
		set.removeAll(super.subList(fromIndex, toIndex));
    	super.removeRange(fromIndex, toIndex);
    }

    /**
	 * {@inheritDoc}
	 */
	@Override
    public boolean removeAll(Collection<?> c) {
    	super.removeAll(c);
    	return set.removeAll(c);
    }

    /**
	 * {@inheritDoc}
	 */
	@Override
    public boolean retainAll(Collection<?> c) {
		super.retainAll(c);
		return set.retainAll(c);
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<E> asList() {
		return this;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<E> asSet() {
		return new SetFace<E>(this);
	}


	@Override
	public ListSet<E> unmodifiableView() {
		return new UnmodifiableListSet<>(this);
	}
	
	private void rangeCheck(int index) {
		if (index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException("Index is out of range: " + index + " (size = " + size());
		}
	}

	private void rangeCheckForAdd(int index) {
		if (index < 0 || index > size()) {
			throw new IndexOutOfBoundsException("Index is out of range: " + index + " (size = " + size());
		}
	}
	
	/**
	 * Throws an IllegalArgumentException if this list set contains the given item.
	 */
	private void checkNotContains(E e) {
		if (contains(e)) {
			throw new IllegalArgumentException("Value already present: " + e);
		}
	}
	


    // Iterators, from OpenJDK, below license header applies to below code only. 
	
	/*
	 * Copyright (c) 1997, 2012, Oracle and/or its affiliates. All rights reserved.
	 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
	 *
	 * This code is free software; you can redistribute it and/or modify it
	 * under the terms of the GNU General Public License version 2 only, as
	 * published by the Free Software Foundation.  Oracle designates this
	 * particular file as subject to the "Classpath" exception as provided
	 * by Oracle in the LICENSE file that accompanied this code.
	 *
	 * This code is distributed in the hope that it will be useful, but WITHOUT
	 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
	 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
	 * version 2 for more details (a copy is included in the LICENSE file that
	 * accompanied this code).
	 *
	 * You should have received a copy of the GNU General Public License version
	 * 2 along with this work; if not, write to the Free Software Foundation,
	 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
	 *
	 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
	 * or visit www.oracle.com if you need additional information or have any
	 * questions.
	 */

	/**
	 * {@inheritDoc}
	 */
    public Iterator<E> iterator() {
        return new Itr();
    }

    /**
	 * {@inheritDoc}
	 */
    public ListIterator<E> listIterator() {
        return listIterator(0);
    }

    /**
	 * {@inheritDoc}
	 */
    public ListIterator<E> listIterator(final int index) {
        rangeCheckForAdd(index);

        return new ListItr(index);
    }

    private class Itr implements Iterator<E> {
        /**
         * Index of element to be returned by subsequent call to next.
         */
        int cursor = 0;

        /**
         * Index of element returned by most recent call to next or
         * previous.  Reset to -1 if this element is deleted by a call
         * to remove.
         */
        int lastRet = -1;

        /**
         * The modCount value that the iterator believes that the backing
         * List should have.  If this expectation is violated, the iterator
         * has detected concurrent modification.
         */
        int expectedModCount = modCount;

        public boolean hasNext() {
            return cursor != size();
        }

        public E next() {
            checkForComodification();
            try {
                int i = cursor;
                E next = get(i);
                lastRet = i;
                cursor = i + 1;
                return next;
            } catch (IndexOutOfBoundsException e) {
                checkForComodification();
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();
            checkForComodification();

            try {
                AListSet.this.remove(lastRet);
                if (lastRet < cursor)
                    cursor--;
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    private class ListItr extends Itr implements ListIterator<E> {
        ListItr(int index) {
            cursor = index;
        }

        public boolean hasPrevious() {
            return cursor != 0;
        }

        public E previous() {
            checkForComodification();
            try {
                int i = cursor - 1;
                E previous = get(i);
                lastRet = cursor = i;
                return previous;
            } catch (IndexOutOfBoundsException e) {
                checkForComodification();
                throw new NoSuchElementException();
            }
        }

        public int nextIndex() {
            return cursor;
        }

        public int previousIndex() {
            return cursor-1;
        }

        public void set(E e) {
            if (lastRet < 0)
                throw new IllegalStateException();
            checkForComodification();

            try {
            	AListSet.this.set(lastRet, e);
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        public void add(E e) {
            checkForComodification();

            try {
                int i = cursor;
                AListSet.this.add(i, e);
                lastRet = -1;
                cursor = i + 1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }

}
