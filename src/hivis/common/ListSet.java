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
import java.util.Set;
import java.util.Spliterator;

/**
 * <p>Interface for a List that enforces uniqueness amongst its members. 
 * Or a Set in which items are associated with contiguous, predictable indices.</p>
 * 
 * @author O. J. Coleman
 */
public interface ListSet<E> extends List<E>, Set<E> {
    /**
     * Returns <tt>true</tt> if this list contains the specified element.
     * More formally, returns <tt>true</tt> if and only if this list contains
     * at least one element <tt>e</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
     *
     * @param o element whose presence in this list is to be tested
     * @return <tt>true</tt> if this list contains the specified element
     */
    public boolean contains(Object o);
    
    /**
     * Replaces the element at the specified position in this list with
     * the specified element. An exception is thrown if the list set already
     * contains the element.
     *
     * @param index index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws IllegalArgumentException If the ListSet already contains the given element.
     */
    public E set(int index, E element);

    /**
     * Appends the specified element to the end of this list set, 
     * if the element is not already contained by it.
     *
     * @param e element to be appended to this list
     * @return true if the item was added, false if not.
     */
    public boolean add(E e);

    /**
     * Inserts the specified element at the specified position in this
     * list. Shifts the element currently at that position (if any) and
     * any subsequent elements to the right (adds one to their indices).
     * An exception is thrown if the list already contains the element.
     *
     * @param index index at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws IllegalArgumentException If the ListSet already contains the given element.
     */
    public void add(int index, E element);

    /**
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left (subtracts one from their
     * indices).
     *
     * @param index the index of the element to be removed
     * @return the element that was removed from the list
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E remove(int index);

    /**
     * Removes the the specified element from this list, if it is present.  
     * If the list set does not contain the element, it is unchanged.
     *
     * @param o element to be removed from this list, if present
     * @return <tt>true</tt> if this list set contained the specified element
     */
    public boolean remove(Object o);

    /**
     * Removes all of the elements from this list set. The list set will
     * be empty after this call returns.
     */
    public void clear();

    /**
     * Appends all of the elements in the specified collection to the end of
     * this list set, in the order that they are returned by the
     * specified collection's Iterator, excepting elements that are already 
     * present in this list set. The behavior of this operation is
     * undefined if the specified collection is modified while the operation
     * is in progress.  (This implies that the behavior of this call is
     * undefined if the specified collection is this list, and this
     * list is nonempty.)
     *
     * @param c collection containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws NullPointerException if the specified collection is null
     */
    public boolean addAll(Collection<? extends E> c);

    /**
     * Inserts all of the elements in the specified collection into this
     * list, starting at the specified position, excepting elements that 
     * are already present in this list set.  Shifts the element
     * currently at that position (if any) and any subsequent elements to
     * the right (increases their indices).  The new elements will appear
     * in the list in the order that they are returned by the
     * specified collection's iterator.
     *
     * @param index index at which to insert the first element from the
     *              specified collection
     * @param c collection containing elements to be added to this list
     * @return <tt>true</tt> if this list changed as a result of the call
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws NullPointerException if the specified collection is null
     */
    public boolean addAll(int index, Collection<? extends E> c);

    /**
     * Removes from this list all of its elements that are contained in the
     * specified collection.
     *
     * @param c collection containing elements to be removed from this list
     * @return {@code true} if this list changed as a result of the call
     * @throws ClassCastException if the class of an element of this list
     *         is incompatible with the specified collection (optional)
     * @throws NullPointerException if this list contains a null element and the
     *         specified collection does not permit null elements (optional),
     *         or if the specified collection is null
     * @see Collection#contains(Object)
     */
    public boolean removeAll(Collection<?> c);

    /**
     * Retains only the elements in this list that are contained in the
     * specified collection.  In other words, removes from this list all
     * of its elements that are not contained in the specified collection.
     *
     * @param c collection containing elements to be retained in this list
     * @return {@code true} if this list changed as a result of the call
     * @throws ClassCastException if the class of an element of this list
     *         is incompatible with the specified collection (optional)
     * @throws NullPointerException if this list contains a null element and the
     *         specified collection does not permit null elements (optional),
     *         or if the specified collection is null
     * @see Collection#contains(Object)
     */
    public boolean retainAll(Collection<?> c);

 
    /**
     * Returns a list iterator over the elements in this list set (in proper
     * sequence), starting at the specified position in the list.
     * The specified index indicates the first element that would be
     * returned by an initial call to {@link ListIterator#next next}.
     * An initial call to {@link ListIterator#previous previous} would
     * return the element with the specified index minus one.
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public ListIterator<E> listIterator(int index);

    /**
     * Returns a list iterator over the elements in this list set (in proper
     * sequence).
     *
     * @see #listIterator(int)
     */
    public ListIterator<E> listIterator();

    /**
     * Returns an iterator over the elements in this list set in proper sequence.
     *
     * @return an iterator over the elements in this list set in proper sequence
     */
    public Iterator<E> iterator();

    /**
     * Returns a view of the portion of this list between the specified
     * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive.  (If
     * {@code fromIndex} and {@code toIndex} are equal, the returned list is
     * empty.)  The returned list is backed by this list, so non-structural
     * changes in the returned list are reflected in this list, and vice-versa.
     * The returned list supports all of the optional list operations.
     *
     * <p>This method eliminates the need for explicit range operations (of
     * the sort that commonly exist for arrays).  Any operation that expects
     * a list can be used as a range operation by passing a subList view
     * instead of a whole list.  For example, the following idiom
     * removes a range of elements from a list:
     * <pre>
     *      list.subList(from, to).clear();
     * </pre>
     * Similar idioms may be constructed for {@link #indexOf(Object)} and
     * {@link #lastIndexOf(Object)}, and all of the algorithms in the
     * {@link Collections} class can be applied to a subList.
     *
     * <p>The semantics of the list returned by this method become undefined if
     * the backing list (i.e., this list) is <i>structurally modified</i> in
     * any way other than via the returned list.  (Structural modifications are
     * those that change the size of this list, or otherwise perturb it in such
     * a fashion that iterations in progress may yield incorrect results.)
     *
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws IllegalArgumentException {@inheritDoc}
     */
    public List<E> subList(int fromIndex, int toIndex);
    
    /**
     * Compares the specified object with this list set for equality.  Returns
     * {@code true} if and only if the specified object is also a ListSet, both
     * ListSets have the same size, and all corresponding pairs of elements in
     * the two ListSets are <i>equal</i>.  (Two elements {@code e1} and
     * {@code e2} are <i>equal</i> if {@code (e1==null ? e2==null :
     * e1.equals(e2))}.)  In other words, two lists are defined to be
     * equal if they contain the same elements in the same order.<p>
     *
     * @param o the object to be compared for equality with this list
     * @return {@code true} if the specified object is equal to this list
     */
    public boolean equals(Object o);
    
    /**
     * Returns a List view of this ListSet, with the property that the equals 
     * method will return true according to the usual contract of 
     * {@link java.util.List#equals(Object)} 
     */
    public List<E> asList();
    
    /**
     * Returns a Set view of this ListSet, with the property that the equals 
     * method will return true according to the usual contract of 
     * {@link java.util.Set#equals(Object)} 
     */
    public Set<E> asSet();
    
    /**
     * Returns an unmodifiable view of this ListSet.
     */
    public ListSet<E> unmodifiableView();

	@Override
	default Spliterator<E> spliterator() {
		return List.super.spliterator();
	}
}
