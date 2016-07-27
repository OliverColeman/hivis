package hivis.data;

import java.util.List;

import hivis.data.view.Function;
import hivis.data.view.SeriesFunction;
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
	 * Get the value corresponding to an empty value. This will typically be 
	 * null or a 'NaN' (Not a Number) value, for example Double.NaN for a 
	 * series storing double values.
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
	 * Get the element at the specified index as the primitive type 'double'.
	 * This is an optional operation, not all DataSeries implementations support
	 * it, in which case an {@link UnsupportedOperationException} will be
	 * thrown. It is provided to allow more efficient access to series that
	 * store double values as a primitive type (to avoid auto-boxing).
	 */
	double getDouble(int index);
	
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
	
	
	// View/functional operations. 
	

	/**
	 * Get a series that is calculated using the given function over each element in this series.
	 * 
	 * @param function
	 *            The function to generate the series for the new table.
	 */
	public <O> DataSeries<O> apply(Function<V, O> function);
	
	/**
	 * Create a view of this series that contains the values of 
	 * this series followed by the values in the given series.
	 * @param series The series to append.
	 * @return A view of this series with the given series appended.
	 */
	public DataSeries<V> append(DataSeries<V> series);
	
	public DataSeries<V> add(V value);
	public DataSeries<V> add(double value);
	public DataSeries<V> add(long value);
	public DataSeries<V> add(DataSeries<?> series);
	
	public DataSeries<V> subtract(V value);
	public DataSeries<V> subtract(double value);
	public DataSeries<V> subtract(long value);
	public DataSeries<V> subtract(DataSeries<?> series);
	
	public DataSeries<V> multiply(V value);
	public DataSeries<V> multiply(double value);
	public DataSeries<V> multiply(long value);
	public DataSeries<V> multiply(DataSeries<?> series);
	
	public DataSeries<V> divide(V value);
	public DataSeries<V> divide(double value);
	public DataSeries<V> divide(long value);
	public DataSeries<V> divide(DataSeries<?> series);
	
	
}