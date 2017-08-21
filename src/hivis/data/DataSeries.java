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

import java.util.Comparator;
import java.util.List;

import hivis.data.view.Function;
import hivis.data.view.RowFilter;
import hivis.data.view.SeriesView;
import hivis.data.view.TableFunction;

/**
 * Represents a series or vector of values that are all of the same type.
 * 
 * @author O. J. Coleman
 */
public interface DataSeries<V> extends DataSequence, Iterable<V> {
	/**
	 * Get the number of elements in this series.
	 */
	int length();

	/**
	 * Get the element at the specified index, or the empty value (see
	 * {@link #getEmptyValue()} if the bounds are out of range.
	 * 
	 * @param index
	 *            The index of the value to get, counting from 0.
	 */
	V get(int index);

	/**
	 * Set the value of the element at the specified index. This method should
	 * only be used when the new value is exactly the same type represented by
	 * this series. In general it is best to use {@link #set(int, Object)}.
	 * 
	 * @param index
	 *            The index of the value to set, counting from 0.
	 * @param value
	 *            The new value.
	 * @throws IndexOutOfBoundsException
	 *             if the index is invalid.
	 * @throws UnsupportedOperationException
	 *             if this DataSeries is not externally modifiable, for example
	 *             because it is (a view) derived from some other data.
	 */
	void setValue(int index, V value);

	/**
	 * Set the value of the element at the specified index. Attempts to
	 * cast/convert the given value to the type represented by this series. For
	 * typed value setting use {@link DataSeries#setValue(int, Object)}.
	 * 
	 * @param index
	 *            The index of the value to set, counting from 0.
	 * @param value
	 *            The new value.
	 * @throws IndexOutOfBoundsException
	 *             if the index is invalid.
	 * @throws IllegalArgumentException
	 *             if the given object cannot be cast to the type represented by
	 *             this series.
	 * @throws UnsupportedOperationException
	 *             if this DataSeries is not externally modifiable, for example
	 *             because it is (a view) derived from some other data.
	 */
	void set(int index, Object value);

	/**
	 * Adds the given element to the end of this series. Attempts to cast the
	 * given object to the type represented by this series.
	 * 
	 * @param value
	 *            The new value.
	 * @throws IllegalArgumentException
	 *             if the given object cannot be cast to the type represented by
	 *             this series.
	 * @throws UnsupportedOperationException
	 *             if this DataSeries is not externally modifiable, for example
	 *             because it is (a view) derived from some other data.
	 */
	void append(Object value);

	/**
	 * Adds the given element to the end of the series.
	 * 
	 * @param value
	 *            The new value.
	 * @throws UnsupportedOperationException
	 *             if this DataSeries is not externally modifiable, for example
	 *             because it is (a view) derived from some other data.
	 */
	void appendValue(V value);

	/**
	 * Adds the given elements to the end of the series.
	 * 
	 * @param values
	 *            The new values.
	 * @throws UnsupportedOperationException
	 *             if this DataSeries is not externally modifiable, for example
	 *             because it is (a view) derived from some other data.
	 */
	void appendAllValues(V... values);

	/**
	 * Adds the given element(s) to the end of the series. Attempts to cast the
	 * given objects to the type represented by this series.
	 * 
	 * @param values
	 *            The new values.
	 * @throws IllegalArgumentException
	 *             if the given objects cannot be cast to the type represented
	 *             by this series.
	 * @throws UnsupportedOperationException
	 *             if this DataSeries is not externally modifiable, for example
	 *             because it is (a view) derived from some other data.
	 */
	void appendAll(Object... values);

	/**
	 * Removes the element at the given index. After this operation all elements
	 * after the removed element will have their indexes decremented by 1 and
	 * the total length of the series will be decreased by 1.
	 * 
	 * @param index
	 *            The index of the value to remove, counting from 0.
	 * @throws IndexOutOfBoundsException
	 *             if the index is invalid.
	 * @throws UnsupportedOperationException
	 *             if this DataSeries is not externally modifiable, for example
	 *             because it is (a view) derived from some other data.
	 */
	void remove(int index);

	/**
	 * Get the value corresponding to an empty value. For floating-point numbers
	 * this is either Double.NaN or Float.NaN. For integer types it is
	 * Long.MIN_VALUE, Integer.MIN_VALUE, etc. For all other types this is
	 * usually null.
	 */
	V getEmptyValue();

	/**
	 * Get an independent immutable copy of this DataSeries (the returned series cannot be
	 * modified, and changes to this series will not be reflected in the returned series).
	 */
	public DataSeries<V> immutableCopy();

	/**
	 * Resize this DataSeries, removing values from the end or padding with
	 * {@link #getEmptyValue()} as necessary.
	 * 
	 * @param newLength
	 *            The new length for the series.
	 * @throws UnsupportedOperationException
	 *             if this DataSeries is not externally modifiable, for example
	 *             because it is (a view) derived from some other data.
	 */
	void resize(int newLength);

	/**
	 * Resize this DataSeries, removing values from the end or padding with the
	 * given value as necessary.
	 * 
	 * @param newLength
	 *            The new length for the series.
	 * @param padValue
	 *            The value to set additional elements at the end to, if
	 *            necessary.
	 * @throws UnsupportedOperationException
	 *             if this DataSeries is not externally modifiable, for example
	 *             because it is (a view) derived from some other data.
	 */
	void resize(int newLength, V padValue);

	/**
	 * Returns true iff the value at the given index is considered empty
	 * 
	 * @param index
	 *            The index of the value to check, counting from 0.
	 * @see #getEmptyValue()
	 */
	boolean isEmpty(int index);

	/**
	 * Return the type represented by this series.
	 */
	Class<?> getType();

	/**
	 * Get the element at the specified index as the primitive type 'boolean'.
	 * This is an optional operation, not all DataSeries implementations support
	 * it, in which case an {@link UnsupportedOperationException} will be
	 * thrown. It is provided to allow more efficient access to series that
	 * store boolean values as a primitive type (to avoid auto-boxing).
	 * 
	 * @param index
	 *            The index of the value to retrieve, counting from 0.
	 * @throws IndexOutOfBoundsException
	 *             if the index is invalid.
	 */
	boolean getBoolean(int index);

	/**
	 * Get the element at the specified index as the primitive type 'int'. This
	 * is an optional operation, not all DataSeries implementations support it,
	 * in which case an {@link UnsupportedOperationException} will be thrown. It
	 * is provided to allow more efficient access to series that store int
	 * values as a primitive type (to avoid auto-boxing).
	 * 
	 * @param index
	 *            The index of the value to retrieve, counting from 0.
	 * @throws IndexOutOfBoundsException
	 *             if the index is invalid.
	 */
	int getInt(int index);

	/**
	 * Get the element at the specified index as the primitive type 'long'. This
	 * is an optional operation, not all DataSeries implementations support it,
	 * in which case an {@link UnsupportedOperationException} will be thrown. It
	 * is provided to allow more efficient access to series that store long
	 * values as a primitive type (to avoid auto-boxing).
	 * 
	 * @param index
	 *            The index of the value to retrieve, counting from 0.
	 */
	long getLong(int index);

	/**
	 * Get the element at the specified index as the primitive type 'float'.
	 * This is an optional operation, not all DataSeries implementations support
	 * it, in which case an {@link UnsupportedOperationException} will be
	 * thrown. It is provided to allow more efficient access to series that
	 * store float values as a primitive type (to avoid auto-boxing) and to
	 * simplify working with APIs that use float by default.
	 * 
	 * @param index
	 *            The index of the value to retrieve, counting from 0.
	 * @throws IndexOutOfBoundsException
	 *             if the index is invalid.
	 */
	float getFloat(int index);

	/**
	 * Get the element at the specified index as the primitive type 'double'.
	 * This is an optional operation, not all DataSeries implementations support
	 * it, in which case an {@link UnsupportedOperationException} will be
	 * thrown. It is provided to allow more efficient access to series that
	 * store double values as a primitive type (to avoid auto-boxing).
	 * 
	 * @param index
	 *            The index of the value to retrieve, counting from 0.
	 * @throws IndexOutOfBoundsException
	 *             if the index is invalid.
	 */
	double getDouble(int index);

	/**
	 * Get the element at the specified index as a DataValue. The returned
	 * DataValue is a view of whatever element is at the specified index: the
	 * DataValue's value will reflect changes made to the elements in this
	 * series (including insertion and removal of elements at preceding indices
	 * and value changes to the element at the specified index). If the given
	 * index becomes out of range of the length of the series the empty value
	 * will be returned by the DataValue ({@link DataSeries#getEmptyValue()}).
	 * 
	 * @param index
	 *            The index of the value to retrieve, counting from 0.
	 * @throws IndexOutOfBoundsException
	 *             if the index is invalid.
	 */
	DataValue<V> getDataValue(int index);

	/**
	 * Get a view of this series representing the values as single-precision
	 * floating point numbers.
	 */
	FloatSeries asFloat();

	/**
	 * Get a view of this series representing the values as double-precision
	 * floating point numbers.
	 */
	DoubleSeries asDouble();

	/**
	 * Get a view of this series representing the values as integers.
	 */
	IntSeries asInt();

	/**
	 * Get a view of this series representing the values as long integers.
	 */
	LongSeries asLong();

	/**
	 * Get a view of this series representing the values as Strings.
	 */
	StringSeries asString();

	/**
	 * Get the values in this series as an array of values.
	 * 
	 * @return An array containing the values in this series.
	 */
	V[] asArray();

	/**
	 * Get the values in this series as an array of boolean values.
	 * 
	 * @return An array containing the values in this series.
	 */
	boolean[] asBooleanArray();

	/**
	 * Get the values in this series as an array of integer values.
	 * 
	 * @return An array containing the values in this series.
	 */
	int[] asIntArray();

	/**
	 * Get the values in this series as an array of long values.
	 * 
	 * @return An array containing the values in this series.
	 */
	long[] asLongArray();

	/**
	 * Get the values in this series as an array of float (real) values.
	 * 
	 * @return An array containing the values in this series.
	 */
	float[] asFloatArray();

	/**
	 * Get the values in this series as an array of double (real) values.
	 * 
	 * @return An array containing the values in this series.
	 */
	double[] asDoubleArray();

	/**
	 * Get the values in this series as an array of String values.
	 * 
	 * @return An array containing the values in this series.
	 */
	String[] asStringArray();

	/**
	 * Get the values in this series as an array of values.
	 * 
	 * @param data
	 *            An array to put the values in, starting at index 0. If this is
	 *            null or is not long enough to fit all the values a new array
	 *            will be created.
	 * @return An array containing the values in this series (the given array if
	 *         possible).
	 */
	V[] asArray(V[] data);

	/**
	 * Get the values in this series as an array of boolean values.
	 * 
	 * @param data
	 *            An array to put the values in, starting at index 0. If this is
	 *            null or is not long enough to fit all the values a new array
	 *            will be created.
	 * @return An array containing the values in this series (the given array if
	 *         possible).
	 */
	boolean[] asBooleanArray(boolean[] data);

	/**
	 * Get the values in this series as an array of integer values.
	 * 
	 * @param data
	 *            An array to put the values in, starting at index 0. If this is
	 *            null or is not long enough to fit all the values a new array
	 *            will be created.
	 * @return An array containing the values in this series (the given array if
	 *         possible).
	 */
	int[] asIntArray(int[] data);

	/**
	 * Get the values in this series as an array of long values.
	 * 
	 * @param data
	 *            An array to put the values in, starting at index 0. If this is
	 *            null or is not long enough to fit all the values a new array
	 *            will be created.
	 * @return An array containing the values in this series (the given array if
	 *         possible).
	 */
	long[] asLongArray(long[] data);

	/**
	 * Get the values in this series as an array of float (real) values.
	 * 
	 * @param data
	 *            An array to put the values in, starting at index 0. If this is
	 *            null or is not long enough to fit all the values a new array
	 *            will be created.
	 * @return An array containing the values in this series (the given array if
	 *         possible).
	 */
	float[] asFloatArray(float[] data);

	/**
	 * Get the values in this series as an array of double (real) values.
	 * 
	 * @param data
	 *            An array to put the values in, starting at index 0. If this is
	 *            null or is not long enough to fit all the values a new array
	 *            will be created.
	 * @return An array containing the values in this series (the given array if
	 *         possible).
	 */
	double[] asDoubleArray(double[] data);

	/**
	 * Get the values in this series as an array of String values.
	 * 
	 * @param data
	 *            An array to put the values in, starting at index 0. If this is
	 *            null or is not long enough to fit all the values a new array
	 *            will be created.
	 * @return An array containing the values in this series (the given array if
	 *         possible).
	 */
	String[] asStringArray(String[] data);

	/**
	 * Get a copy of this DataSeries. The returned series will contain the same
	 * values as this series, copied by reference. The returned series may be
	 * modified, even if this series is not modifiable. Changes to this series
	 * will not be reflected in the returned series, or vice versa.
	 * 
	 */
	public DataSeries<V> copy();

	/**
	 * Get an unmodifiable view of this series.
	 */
	public SeriesView<V> unmodifiableView();

	/**
	 * Get an empty modifiable series representing the same type as this series.
	 */
	public DataSeries<V> getNewSeries();

	/**
	 * Get a series representing the same type as this series and containing the
	 * given values. Attempts to cast the given values to the type represented
	 * by this series.
	 * 
	 * @param values
	 *            A sequence of values (may be an array, List, DataSeries or any
	 *            other object that implements Iterable) to populate the new
	 *            DataSeries.
	 */
	public DataSeries<V> getNewSeries(Iterable<?> values);

	/**
	 * Get a {@link DataValue} representing the same type as this series.
	 */
	public DataValue<V> getNewDataValue();

	/**
	 * Get the values in this series as an (unmodifiable) List.
	 */
	public List<V> asList();

	/**
	 * Returns true iff this series stores numeric values.
	 */
	public boolean isNumeric();

	/**
	 * Returns the minimum value contained in this series, or
	 * {@link #getEmptyValue()} if the series is empty, as a DataValue.
	 * 
	 * @throws UnsupportedOperationException
	 *             if this series is not numeric.
	 */
	public DataValue<V> min();

	/**
	 * Returns the maximum value contained in this series. or
	 * {@link #getEmptyValue()} if the series is empty, as a DataValue.
	 * 
	 * @throws UnsupportedOperationException
	 *             if this series is not numeric.
	 */
	public DataValue<V> max();

	/**
	 * Returns the sum of the values contained in this series, or 0 if the
	 * series is empty, as a DataValue.
	 * 
	 * @throws UnsupportedOperationException
	 *             if this series is not numeric.
	 */
	public DataValue<V> sum();

	/**
	 * Returns the product of the values contained in this series, or
	 * {@link #getEmptyValue()} if the series is empty, as a DataValue.
	 * 
	 * @throws UnsupportedOperationException
	 *             if this series is not numeric.
	 */
	public DataValue<V> product();

	/**
	 * Returns the arithmetic mean of the values contained in this series, or
	 * {@link #getEmptyValue()} if the series is empty, as a DataValue.
	 * 
	 * @throws UnsupportedOperationException
	 *             if this series is not numeric.
	 */
	public DataValue<Double> mean();

	/**
	 * Returns the variance of the values contained in this series, or
	 * {@link #getEmptyValue()} if the series is empty, as a DataValue.
	 * 
	 * @throws UnsupportedOperationException
	 *             if this series is not numeric.
	 */
	public DataValue<Double> variance();

	/**
	 * Returns the standard deviation of the values contained in this series, or
	 * {@link #getEmptyValue()} if the series is empty, as a DataValue.
	 * 
	 * @throws UnsupportedOperationException
	 *             if this series is not numeric.
	 */
	public DataValue<Double> stdDev();

	/**
	 * Returns the maximum value contained in this series, or getEmptyValue() if
	 * the series is empty.
	 * 
	 * @deprecated Superseded by {@link #max()}.
	 */
	public V maxValue();

	/**
	 * Returns the minimum value contained in this series, or getEmptyValue() if
	 * the series is empty.
	 * 
	 * @deprecated Superseded by {@link #min()}.
	 */
	public V minValue();

	// View/functional operations.

	/**
	 * <p>
	 * Create a view of this series containing the values in this series sorted
	 * into ascending order, according to the natural ordering of the values.
	 * </p>
	 * <p>
	 * All values in the series must implement the Comparable interface. All
	 * numeric types, strings, and date types implement Comparable, so this is
	 * only of concern if the series represents some other less common type of
	 * data). All values must be mutually comparable (that is, v1.compareTo(v2)
	 * must not throw a ClassCastException for any values v1 and v2). This sort
	 * is guaranteed to be stable: equal values will not be reordered as a
	 * result of the sort.
	 * </p>
	 */
	public SeriesView<V> sort();

	/**
	 * Create a view of this series containing the values in this series sorted
	 * according to the order induced by the specified comparator. All values in
	 * the series must implement the Comparable interface. Furthermore, all
	 * values must be mutually comparable (that is, v1.compareTo(v2) must not
	 * throw a ClassCastException for any values v1 and v2). This sort is
	 * guaranteed to be stable: equal values will not be reordered as a result
	 * of the sort.
	 * 
	 * @param comparator
	 *            The comparator that will be used to determine the ordering of
	 *            the elements.
	 */
	public SeriesView<V> sort(Comparator<V> comparator);

	/**
	 * Create a view of this DataSeries containing the values in the DataSeries
	 * grouped according to their mutual equality. Groups are formed by placing
	 * all values for which <code>v1.equals(v2)</code> into the same group (and
	 * values where <code>!v1.equals(v2)</code> into different groups). The key
	 * for each group is a value such that <code>key.equals(v)</code> for all
	 * values in the group.
	 */
	public DataMap<V, SeriesView<V>> group();

	/**
	 * Create a view of this DataSeries containing the values in the DataSeries
	 * grouped according to the given key function. A key is generated for each
	 * value in the series with the given key function. Groups are then formed
	 * such that the keys for all values in a group satisfy
	 * <code>k1.equals(k2)</code>. The key for each group is a value such that
	 * <code>key.equals(keyFuntion(v))</code> for any value, v, in the group.
	 * 
	 * @param keyFunction
	 *            The function used to determine the key for each value in the
	 *            series.
	 */
	public <K> DataMap<K, SeriesView<V>> group(Function<V, K> keyFuntion);

	/**
	 * Create a view of this series containing the values in this series scaled
	 * to the unit range [0, 1]. Equivalent to
	 * <code>this.subtract(this.min()).divide(this.max().subtract(this.min()))</code>
	 * .
	 * 
	 * @throws UnsupportedOperationException
	 *             if this series is non-numeric.
	 */
	public DataSeries<Double> toUnitRange();

	/**
	 * Create a view of this series containing the values in this series scaled
	 * to the specified range. For example if a series contains the values [5,
	 * 25, 11, 16] and toRange(0, 10) is called on it the resulting series will
	 * contain (0, 10, 3, 5.5).
	 * 
	 * @param min
	 *            The minimum value of the range.
	 * @param max
	 *            The maximum value of the range.
	 * @throws UnsupportedOperationException
	 *             if this series is non-numeric.
	 */
	public DataSeries<Double> toRange(double min, double max);

	/**
	 * Create a view of this series containing the values in this series scaled
	 * to the specified range. For example if a series contains the values [5,
	 * 25, 11, 16] and toRange(0, 10) is called on it the resulting series will
	 * contain (0, 10, 3, 5.5).
	 * 
	 * @param min
	 *            The minimum value of the range. Changes to this DataValue will
	 *            be reflected in the returned series.
	 * @param max
	 *            The maximum value of the range. Changes to this DataValue will
	 *            be reflected in the returned series.
	 * 
	 * @throws UnsupportedOperationException
	 *             if this series is non-numeric.
	 */
	public DataSeries<Double> toRange(DataValue<?> min, DataValue<?> max);

	/**
	 * Create a view of this series that contains the values of this series
	 * followed by the values in the given series.
	 * 
	 * @param series
	 *            The series to append.
	 * @return A view of this series with the given series appended.
	 */
	public SeriesView<V> append(DataSeries<V> series);

	/**
	 * Create a view of this series that is calculated by applying the given
	 * function to each element in this series.
	 * 
	 * @param function
	 *            The function to calculate the new values from the original
	 *            values.
	 */
	public <O> SeriesView<O> apply(final Function<V, O> function);

	/**
	 * Create a view of this series that is calculated by applying the given
	 * method from the java.lang.Math class to each element in this series. Only
	 * methods accepting a single argument/parameter may be used.
	 * 
	 * @param mathMethod
	 *            The name of the method in the Math class to be used to
	 *            calculate the new values from the original values.
	 */
	public SeriesView<?> applyMathMethod(String mathMethod);

	/**
	 * Create a view of this series that filters and/or rearranges the elements.
	 * 
	 * @param indices
	 *            The indices of the elements to include, in the order to
	 *            include them.
	 * @throws IndexOutOfBoundsException
	 *             if any of the indices are invalid.
	 */
	SeriesView<V> select(int... indices);

	/**
	 * Create a view of this series in which the values are the result of adding
	 * the given value to each value in this series.
	 * 
	 * @param value
	 *            The value to add.
	 * @throws UnsupportedOperationException
	 *             if this series does not represent numeric values.
	 */
	public <O> SeriesView<O> add(Number value);

	/**
	 * Create a view of this series in which the values are the result of adding
	 * the given value to each value in this series.
	 * 
	 * @param value
	 *            The value to add. Changes to the DataValue will be reflected
	 *            in the returned series.
	 * @throws UnsupportedOperationException
	 *             if this series or the given value do not represent numeric
	 *             values.
	 */
	public <O> SeriesView<O> add(DataValue<?> value);

	/**
	 * Create a view of this series in which the values are the result of adding
	 * the values in the given series to the corresponding values in this
	 * series.
	 * 
	 * @param series
	 *            The series to add.
	 * @throws IllegalArgumentException
	 *             if the this series and the given series have different
	 *             lengths.
	 * @throws UnsupportedOperationException
	 *             if this series or the given series do not represent numeric
	 *             values.
	 */
	public <O> SeriesView<O> add(DataSeries<?> series);

	/**
	 * Create a view of this series in which the values are the result of
	 * subtracting the given value from each value in this series.
	 * 
	 * @param value
	 *            The value to subtract.
	 * @throws UnsupportedOperationException
	 *             if this series does not represent numeric values.
	 */
	public <O> SeriesView<O> subtract(Number value);

	/**
	 * Create a view of this series in which the values are the result of
	 * subtracting the given value from each value in this series.
	 * 
	 * @param value
	 *            The value to subtract. Changes to the DataValue will be
	 *            reflected in the returned series.
	 * @throws UnsupportedOperationException
	 *             if this series or the given value do not represent numeric
	 *             values.
	 */
	public <O> SeriesView<O> subtract(DataValue<?> value);

	/**
	 * Create a view of this series in which the values are the result of
	 * subtracting the values in the given series from the corresponding values
	 * in this series.
	 * 
	 * @param series
	 *            The series to subtract.
	 * @throws IllegalArgumentException
	 *             if the this series and the given series have different
	 *             lengths.
	 * @throws UnsupportedOperationException
	 *             if this series or the given series do not represent numeric
	 *             values.
	 */
	public <O> SeriesView<O> subtract(DataSeries<?> series);

	/**
	 * Create a view of this series in which the values are the result of
	 * multiplying each value in this series by the given value.
	 * 
	 * @param value
	 *            The value to multiply by.
	 * @throws UnsupportedOperationException
	 *             if this series does not represent numeric values.
	 */
	public <O> SeriesView<O> multiply(Number value);

	/**
	 * Create a view of this series in which the values are the result of
	 * multiplying each value in this series by the given value.
	 * 
	 * @param value
	 *            The value to multiply by. Changes to the DataValue will be
	 *            reflected in the returned series.
	 * @throws UnsupportedOperationException
	 *             if this series or the given value do not represent numeric
	 *             values.
	 */
	public <O> SeriesView<O> multiply(DataValue<?> value);

	/**
	 * Create a view of this series in which the values are the result of
	 * multiplying the values in this series by the corresponding values in the
	 * given series.
	 * 
	 * @param series
	 *            The series to multiply by.
	 * @throws IllegalArgumentException
	 *             if the this series and the given series have different
	 *             lengths.
	 * @throws UnsupportedOperationException
	 *             if this series or the given series do not represent numeric
	 *             values.
	 */
	public <O> SeriesView<O>  multiply(DataSeries<?> series);

	/**
	 * Create a view of this series in which the values are the result of
	 * dividing each value in this series by the given value.
	 * 
	 * @param value
	 *            The value to divide by.
	 * @throws UnsupportedOperationException
	 *             if this series does not represent numeric values.
	 */
	public <O> SeriesView<O>  divide(Number value);

	/**
	 * Create a view of this series in which the values are the result of
	 * dividing each value in this series by the given value.
	 * 
	 * @param value
	 *            The value to divide by. Changes to the DataValue will be
	 *            reflected in the returned series.
	 * @throws UnsupportedOperationException
	 *             if this series or the given value do not represent numeric
	 *             values.
	 */
	public <O> SeriesView<O>  divide(DataValue<?> value);

	/**
	 * Create a view of this series in which the values are the result of
	 * dividing the values in this series by the corresponding values in the
	 * given series.
	 * 
	 * @param series
	 *            The series to divide by.
	 * @throws IllegalArgumentException
	 *             if the this series and the given series have different
	 *             lengths.
	 * @throws UnsupportedOperationException
	 *             if this series or the given series do not represent numeric
	 *             values.
	 */
	public <O> SeriesView<O>  divide(DataSeries<?> series);

	/**
	 * Internal use. Interface for DataSeries representing float values. Allows
	 * {@link #asFloat()} to return series that do not lose their generic type
	 * due to type erasure.
	 */
	public interface FloatSeries extends DataSeries<Float> {
	}

	/**
	 * Internal use. Interface for DataSeries representing double values. Allows
	 * {@link #asDouble()} to return series that do not lose their generic type
	 * due to type erasure.
	 */
	public interface DoubleSeries extends DataSeries<Double> {
	}

	/**
	 * Internal use. Interface for DataSeries representing integer values.
	 * Allows {@link #asInt()} to return series that do not lose their generic
	 * type due to type erasure.
	 */
	public interface IntSeries extends DataSeries<Integer> {
	}

	/**
	 * Internal use. Interface for DataSeries representing long integer values.
	 * Allows {@link #asLong()} to return series that do not lose their generic
	 * type due to type erasure.
	 */
	public interface LongSeries extends DataSeries<Long> {
	}

	/**
	 * Internal use. Interface for DataSeries representing String values. Allows
	 * {@link #asString()} to return series that do not lose their generic type
	 * due to type erasure.
	 */
	public interface StringSeries extends DataSeries<String> {
	}
}