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

import java.util.List;

import hivis.data.view.Function;
import hivis.data.view.RowFilter;
import hivis.data.view.SeriesView;
import hivis.data.view.TableFunction;

/**
 * Represents a series or vector of values. 
 * Implementations typically define the type of value to store. 
 * This may be numeric or any kind of object.
 * 
 * @author O. J. Coleman
 */
public interface DataSeries<V> extends DataSet, Iterable<V> {
	/**
	 * Get the number of elements in this series.
	 */
	int length();

	/**
	 * Get the element at the specified index, or the empty value 
	 * (see {@link #getEmptyValue()} if the bounds are out of range.
	 */
	V get(int index);

	/**
	 * Set the element at the specified index. 
	 *@throws IndexOutOfBoundsException if the index is invalid.
	 */
	void setValue(int index, V value);

	/**
	 * Adds the given element to the end of the series.
	 */
	void appendValue(V value);

	/**
	 * Set the element at the specified index. Attempts to cast the given object to the type stored by this series.
	 * For typed value setting use {@link DataSeries#setValue(int, Object)}.
	 * @throws IndexOutOfBoundsException if the index is invalid.
	 * @throws IllegalArgumentException if the given object cannot be cast to the type stored by this series.
	 */
	void set(int index, Object value);

	/**
	 * Adds the given element to the end of the series. Attempts to cast the given object to the type stored by this series.
	 * @throws IllegalArgumentException if the given object cannot be cast to the type stored by this series.
	 */
	void append(Object value);

	/**
	 * Removes the element at the given index. 
	 * @throws IndexOutOfBoundsException if the index is invalid.
	 */
	void remove(int index);

	/**
	 * Get the value corresponding to an empty value. For floating-point numbers this is either Double.NaN or Float.NaN.
	 * For integer types it is Long.MIN_VALUE, Integer.MIN_VALUE, etc. For all other types this is usually null.
	 */
	V getEmptyValue();

	/**
	 * Resize this DataSeries, removing values from the end or padding with {@link #getEmptyValue()} as necessary;
	 * @param newLength The new length for the series.
	 */
	void resize(int newLength);

	/**
	 * Returns true iff the value at the given index is considered empty
	 * @see #getEmptyValue()
	 */
	boolean isEmpty(int index);
	
	/**
	 * Return the type stored by this series.
	 */
	Class<?> getType();

	/**
	 * Get the element at the specified index as the primitive type 'boolean'.
	 * This is an optional operation, not all DataSeries implementations support
	 * it, in which case an {@link UnsupportedOperationException} will be
	 * thrown. It is provided to allow more efficient access to series that
	 * store boolean values as a primitive type (to avoid auto-boxing).
	 */
	boolean getBoolean(int index);

	/**
	 * Get the element at the specified index as the primitive type 'int'. This
	 * is an optional operation, not all DataSeries implementations support it,
	 * in which case an {@link UnsupportedOperationException} will be thrown. It
	 * is provided to allow more efficient access to series that store int
	 * values as a primitive type (to avoid auto-boxing).
	 */
	int getInt(int index);

	/**
	 * Get the element at the specified index as the primitive type 'long'. This
	 * is an optional operation, not all DataSeries implementations support it,
	 * in which case an {@link UnsupportedOperationException} will be thrown. It
	 * is provided to allow more efficient access to series that store long
	 * values as a primitive type (to avoid auto-boxing).
	 */
	long getLong(int index);

	/**
	 * Get the element at the specified index as the primitive type 'float'.
	 * This is an optional operation, not all DataSeries implementations support
	 * it, in which case an {@link UnsupportedOperationException} will be
	 * thrown. It is provided to allow more efficient access to series that
	 * store float values as a primitive type (to avoid auto-boxing) and to 
	 * simplify working with APIs that use float by default.
	 */
	float getFloat(int index);
	
	/**
	 * Get the element at the specified index as the primitive type 'double'.
	 * This is an optional operation, not all DataSeries implementations support
	 * it, in which case an {@link UnsupportedOperationException} will be
	 * thrown. It is provided to allow more efficient access to series that
	 * store double values as a primitive type (to avoid auto-boxing).
	 */
	double getDouble(int index);
	
	
	/**
	 * Get a view of this series representing the values as single-precision floating point numbers.
	 */
	DataSeries<Float> asFloat();
	
	/**
	 * Get a view of this series representing the values as double-precision floating point numbers.
	 */
	DataSeries<Double> asDouble();

	/**
	 * Get a view of this series representing the values as integers.
	 */
	DataSeries<Integer> asInt();

	/**
	 * Get a view of this series representing the values as long integers.
	 */
	DataSeries<Long> asLong();

	/**
	 * Get the values in this series as an array of values.
	 * @return An array containing the values in this series. 
	 */
	V[] asArray();

	/**
	 * Get the values in this series as an array of boolean values.
	 * @return An array containing the values in this series. 
	 */
	boolean[] asBooleanArray();

	/**
	 * Get the values in this series as an array of integer values.
	 * @return An array containing the values in this series. 
	 */
	int[] asIntArray();

	/**
	 * Get the values in this series as an array of long values.
	 * @return An array containing the values in this series. 
	 */
	long[] asLongArray();

	/**
	 * Get the values in this series as an array of float (real) values.
	 * @return An array containing the values in this series.
	 */
	float[] asFloatArray();
	
	/**
	 * Get the values in this series as an array of double (real) values.
	 * @return An array containing the values in this series.
	 */
	double[] asDoubleArray();
	
	/**
	 * Get the values in this series as an array of String values.
	 * @return An array containing the values in this series.
	 */
	String[] asStringArray();

	/**
	 * Get the values in this series as an array of values.
	 * @param data An array to put the values in, starting at index 0. If this 
	 * is null or is not long enough to fit all the values a new array will be created.
	 * @return An array containing the values in this series. 
	 */
	V[] asArray(V[] data);

	/**
	 * Get the values in this series as an array of boolean values.
	 * @param data An array to put the values in, starting at index 0. If this 
	 * is null or is not long enough to fit all the values a new array will be created.
	 * @return An array containing the values in this series. 
	 */
	boolean[] asBooleanArray(boolean[] data);

	/**
	 * Get the values in this series as an array of integer values.
	 * @param data An array to put the values in, starting at index 0. If this 
	 * is null or is not long enough to fit all the values a new array will be created.
	 * @return An array containing the values in this series. 
	 */
	int[] asIntArray(int[] data);

	/**
	 * Get the values in this series as an array of long values.
	 * @param data An array to put the values in, starting at index 0. If this 
	 * is null or is not long enough to fit all the values a new array will be created.
	 * @return An array containing the values in this series. 
	 */
	long[] asLongArray(long[] data);

	/**
	 * Get the values in this series as an array of float (real) values.
	 * @param data An array to put the values in, starting at index 0. If this 
	 * is null or is not long enough to fit all the values a new array will be created.
	 * @return An array containing the values in this series (the passed in array if possible).
	 */
	float[] asFloatArray(float[] data);
	
	/**
	 * Get the values in this series as an array of double (real) values.
	 * @param data An array to put the values in, starting at index 0. If this 
	 * is null or is not long enough to fit all the values a new array will be created.
	 * @return An array containing the values in this series (the passed in array if possible).
	 */
	double[] asDoubleArray(double[] data);
	
	/**
	 * Get the values in this series as an array of String values.
	 * @param data An array to put the values in, starting at index 0. If this 
	 * is null or is not long enough to fit all the values a new array will be created.
	 * @return An array containing the values in this series (the passed in array if possible).
	 */
	String[] asStringArray(String[] data);
	
	
	/**
	 * Get an empty series of the same type as this series.
	 */
	public DataSeries<V> getNewSeries();
	
	
	/**
	 * Get the values in this series as an (unmodifiable) List.
	 */
	public List<V> asList();
	
	/**
	 * Returns true iff this series stores numeric values.
	 */
	public boolean isNumeric();
	
	
	/**
	 * Returns the minimum value contained in this series, 
	 * or {@link #getEmptyValue()} if the series is empty.
	 */
	public V minValue();

	/**
	 * Returns the maximum value contained in this series.
	 * or {@link #getEmptyValue()} if the series is empty.
	 */
	public V maxValue();

	
	// View/functional operations. 
	
	
	/**
	 * Create a view of this series containing the values in this 
	 * series scaled to the unit range [0, 1]. Equivalent to
	 * <code>this.asDouble().subtract(this.minValue()).divide(this.maxValue() - this.minValue())</code>,
	 * however the values in the view will be updated when the vales in the
	 * series change.
	 * 
	 * @throws UnsupportedOperationException if this series is non-numeric.
	 */
	public DataSeries<Double> toUnitRange();
	

	/**
	 * Create a view of this series that contains the values of 
	 * this series followed by the values in the given series.
	 * @param series The series to append.
	 * @return A view of this series with the given series appended.
	 */
	public SeriesView<V> append(DataSeries<V> series);
	
	/**
	 * Get a view of this series that is calculated using the given function over each element in this series.
	 * 
	 * @param function
	 *            The function to generate calculate the new values from the original values.
	 */
	public <O> SeriesView<O> apply(Function<V, O> function);
	
	/**
	 * Get a view of this series that filters and/or rearranges the elements.
	 * @param indices The indices of the elements to include, in the order to include them.
	 */
	SeriesView<V> select(int... indices);
	
	
	public SeriesView<V> add(V value);
	public SeriesView<V> add(double value);
	public SeriesView<V> add(long value);
	public SeriesView<V> add(DataSeries<?> series);
	
	public SeriesView<V> subtract(V value);
	public SeriesView<V> subtract(double value);
	public SeriesView<V> subtract(long value);
	public SeriesView<V> subtract(DataSeries<?> series);
	
	public SeriesView<V> multiply(V value);
	public SeriesView<V> multiply(double value);
	public SeriesView<V> multiply(long value);
	public SeriesView<V> multiply(DataSeries<?> series);
	
	public SeriesView<V> divide(V value);
	public SeriesView<V> divide(double value);
	public SeriesView<V> divide(long value);
	public SeriesView<V> divide(DataSeries<?> series);
}