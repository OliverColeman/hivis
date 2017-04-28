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
 * Represents a series or vector of values that are all of the same type (Java Class).
 * Implementations typically define the type of value to store. 
 * This may be numeric or any kind of object.
 * 
 * @author O. J. Coleman
 */
public interface DataSeries<V> extends DataSequence, Iterable<V> {
	/**
	 * Get the number of elements in this series.
	 */
	int length();
	
	/**
	 * Returns true iff the given object is a DataSeries storing the same type of data, 
	 * is of the same length, and every element in this series {@link Object#equals(Object)}
	 * the corresponding value in the given series (or both values are null). 
	 */
	boolean equals(Object o);

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
	 * Adds the given elements to the end of the series.
	 */
	void appendAllValues(V... values);

	/**
	 * Set the element at the specified index. Attempts to cast the given object to the type stored by this series.
	 * For typed value setting use {@link DataSeries#setValue(int, Object)}.
	 * @throws IndexOutOfBoundsException if the index is invalid.
	 * @throws IllegalArgumentException if the given object cannot be cast to the type stored by this series.
	 */
	void set(int index, Object value);

	/**
	 * Adds the given element to the end of the series. Attempts to cast the given object to the type stored by this series.
	 * @throws IllegalArgumentException if the given objects cannot be cast to the type stored by this series.
	 */
	void append(Object value);

	/**
	 * Adds the given element(s) to the end of the series. Attempts to cast the given objects to the type stored by this series.
	 * @throws IllegalArgumentException if the given objects cannot be cast to the type stored by this series.
	 */
	void appendAll(Object... values);

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
	 * Resize this DataSeries, removing values from the end or padding with the given value as necessary;
	 * @param newLength The new length for the series.
	 * @param padValue
	 */
	void resize(int newLength, V padValue);

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
	 * Get the element at the specified index as a DataValue. The returned 
	 * DataValue is a view of the specified element value: DataValue's value 
	 * will reflect changes made to the referenced element in this series.
	 * If the given index becomes out of range of the length of the series
	 * The empty value will be returned by DataValue ({@link DataSeries#getEmptyValue()}).
	 */
	DataValue<V> getDataValue(int index);
	
	
	/**
	 * Get a view of this series representing the values as single-precision floating point numbers.
	 */
	FloatSeries asFloat();
	
	/**
	 * Get a view of this series representing the values as double-precision floating point numbers.
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
	 * Get an unmodifiable view of this series.
	 */
	public SeriesView<V> unmodifiableView();
	
	/**
	 * Get an empty series of the same type as this series.
	 */
	public DataSeries<V> getNewSeries();

	/**
	 * Get a series of the same type as this series containing the given values.
	 * Attempts to cast the given values to the type stored by this series.
	 * 
	 * @param values
	 *            A sequence of values (may be an array, List, DataSeries or any
	 *            other object that implements Iterable) to populate the new
	 *            DataSeries.
	 */
	public DataSeries<V> getNewSeries(Iterable<?> values);

	/**
	 * Get a {@link DataValue} storing the same type as this series.
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
	 * Returns the minimum value contained in this series, 
	 * or {@link #getEmptyValue()} if the series is empty,
	 * as a DataValue.
	 */
	public DataValue<V> min();

	/**
	 * Returns the maximum value contained in this series.
	 * or {@link #getEmptyValue()} if the series is empty,
	 * as a DataValue.
	 */
	public DataValue<V> max();

	/**
	 * Returns the sum of the values contained in this series, 
	 * or 0 if the series is empty,
	 * as a DataValue.
	 */
	public DataValue<V> sum();

	/**
	 * Returns the product of the values contained in this series, 
	 * or {@link #getEmptyValue()} if the series is empty,
	 * as a DataValue.
	 */
	public DataValue<V> product();

	/**
	 * Returns the arithmetic mean of the values contained in this series, 
	 * or {@link #getEmptyValue()} if the series is empty,
	 * as a DataValue.
	 */
	public DataValue<Double> mean();

	/**
	 * Returns the variance of the values contained in this series, 
	 * or {@link #getEmptyValue()} if the series is empty,
	 * as a DataValue.
	 */
	public DataValue<Double> variance();
	
	/**
	 * Returns the standard deviation of the values contained in this series, 
	 * or {@link #getEmptyValue()} if the series is empty,
	 * as a DataValue.
	 */
	public DataValue<Double> stdDev();
	
	/**
	 * Returns the maximum value contained in this series, or getEmptyValue() if the series is empty.
	 * @deprecated Superseded by {@link #max()}. 
	 */
	public V maxValue();
	
	/**
	 * Returns the minimum value contained in this series, or getEmptyValue() if the series is empty.
	 * @deprecated Superseded by {@link #min()}.
	 */
	public V minValue();
	
	
	// View/functional operations. 
	
	
	/**
	 * Create a view of this series containing the values in this series sorted
	 * into ascending order, according to the natural ordering of the values.
	 * All values in the series must implement the Comparable interface.
	 * Furthermore, all values must be mutually comparable (that is,
	 * v1.compareTo(v2) must not throw a ClassCastException for any values v1
	 * and v2). This sort is guaranteed to be stable: equal values will not be
	 * reordered as a result of the sort.
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
	 */
	public SeriesView<V> sort(Comparator<V> comparator);
	
	/**
	 * Create a view of this DataSeries containing the values in the DataSeries
	 * grouped according to their mutual equality. Groups are formed by placing all values
	 * for which v1.equals(v2) into the same group (and values where !v1.equals(v2)
	 * into different groups). The key for each group is a value such that
	 * key.equals(v) for all values in the group.
	 */
	public DataMap<V, SeriesView<V>> group();

	/**
	 * Create a view of this DataSeries containing the values in the DataSeries
	 * grouped according to the given key function. A key is generated for each value in the
	 * series with the given key function. Groups are then formed such that the keys for all values in a group
	 * satisfy k1.equals(k2). The key for each group is a value such that
	 * key.equals(keyFuntion(v)) for any value, v, in the group.
	 */
	public <K> DataMap<K, SeriesView<V>> group(Function<V, K> keyFuntion);
	
	/**
	 * Create a view of this series containing the values in this 
	 * series scaled to the unit range [0, 1]. Equivalent to
	 * <code>this.subtract(this.min()).divide(this.max().subtract(this.min()))</code>.
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
	 * Get a view of this series that is calculated by applying the given function to each element in this series.
	 * 
	 * @param function
	 *            The function to generate calculate the new values from the original values.
	 */
	public <O> SeriesView<O> apply(final Function<V, O> function);
	
	/**
	 * Get a view of this series that is calculated by applying the given method from the java.lang.Math class to each 
	 * element in this series. Only methods accepting a single argument/parameter may be used.
	 * 
	 * @param mathMethod The name of the method in the Math class to be used to calculate the new values from the 
	 * original values.
	 */
	public SeriesView<?> applyMathMethod(String mathMethod);
	
	/**
	 * Get a view of this series that filters and/or rearranges the elements.
	 * @param indices The indices of the elements to include, in the order to include them.
	 */
	SeriesView<V> select(int... indices);
	
	
	public SeriesView<?> add(Number value);
	public SeriesView<?> add(DataSeries<?> series);
	public SeriesView<?> add(DataValue<?> value);
	
	public SeriesView<?> subtract(Number value);
	public SeriesView<?> subtract(DataSeries<?> series);
	public SeriesView<?> subtract(DataValue<?> value);
	
	public SeriesView<?> multiply(Number value);
	public SeriesView<?> multiply(DataSeries<?> series);
	public SeriesView<?> multiply(DataValue<?> value);
	
	public SeriesView<?> divide(Number value);
	public SeriesView<?> divide(DataSeries<?> series);
	public SeriesView<?> divide(DataValue<?> value);
	
	
	/**
	 * Interface for DataSeries representing float values.
	 * Allows {@link #asFloat()} to return series that do not lose their generic type due to type erasure.
	 */
	public interface FloatSeries extends DataSeries<Float> {}
	/**
	 * Interface for DataSeries representing double values.
	 * Allows {@link #asDouble()} to return series that do not lose their generic type due to type erasure.
	 */
	public interface DoubleSeries extends DataSeries<Double> {}
	/**
	 * Interface for DataSeries representing integer values.
	 * Allows {@link #asInt()} to return series that do not lose their generic type due to type erasure.
	 */
	public interface IntSeries extends DataSeries<Integer> {}
	/**
	 * Interface for DataSeries representing long integer values.
	 * Allows {@link #asLong()} to return series that do not lose their generic type due to type erasure.
	 */
	public interface LongSeries extends DataSeries<Long> {}
}