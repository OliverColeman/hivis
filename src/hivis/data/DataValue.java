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


import hivis.data.view.Function;

/**
 * Represents a single value. 
 * 
 * @author O. J. Coleman
 */
public interface DataValue<V> extends Data, Comparable<V> {
	/**
	 * Get the value, if set, otherwise returns {@link #getEmptyValue()}.
	 */
	V get();

	/**
	 * Set the value. This method should
	 * only be used when the new value is exactly the same type represented by this DataValue. In general it is best to use {@link #set(Object)}.
	 */
	void setValue(V value);

	/**
	 * Set the value. Attempts to cast the given object to the type represented by this DataValue.
	 * For typed value setting use {@link DataValue#setValue(Object)}.
	 * @throws IllegalArgumentException if the given object cannot be cast to the type represented by this DataValue.
	 */
	void set(Object value);

	/**
	 * Get the value corresponding to an empty value. For floating-point numbers this is either Double.NaN or Float.NaN.
	 * For integer types it is Long.MIN_VALUE, Integer.MIN_VALUE, etc. For all other types this is usually null.
	 */
	V getEmptyValue();

	/**
	 * Returns true iff the value is considered empty
	 * @see #getEmptyValue()
	 */
	boolean isEmpty();
	
	/**
	 * Return the Class of the type represented by this DataValue.
	 */
	Class<?> getType();

	/**
	 * Get the value as the primitive type 'boolean'. 
	 * This is an optional operation, not all DataValue implementations support
	 * it, in which case an {@link UnsupportedOperationException} will be
	 * thrown. It is provided to allow more efficient access to DataValues that
	 * store boolean values as a primitive type (to avoid auto-boxing).
	 */
	boolean getBoolean();

	/**
	 * Get the value as the primitive type 'int'. This
	 * is an optional operation, not all DataValue implementations support it,
	 * in which case an {@link UnsupportedOperationException} will be thrown. It
	 * is provided to allow more efficient access to DataValues that store int
	 * values as a primitive type (to avoid auto-boxing).
	 */
	int getInt();

	/**
	 * Get the value as the primitive type 'long'. This
	 * is an optional operation, not all DataValue implementations support it,
	 * in which case an {@link UnsupportedOperationException} will be thrown. It
	 * is provided to allow more efficient access to DataValues that store long
	 * values as a primitive type (to avoid auto-boxing).
	 */
	long getLong();

	/**
	 * Get the value as the primitive type 'float'.
	 * This is an optional operation, not all DataValue implementations support
	 * it, in which case an {@link UnsupportedOperationException} will be
	 * thrown. It is provided to allow more efficient access to DataValues that
	 * store float values as a primitive type (to avoid auto-boxing) and to 
	 * simplify working with APIs that use float by default.
	 */
	float getFloat();
	
	/**
	 * Get the value as the primitive type 'double'.
	 * This is an optional operation, not all DataValue implementations support
	 * it, in which case an {@link UnsupportedOperationException} will be
	 * thrown. It is provided to allow more efficient access to DataValues that
	 * store double values as a primitive type (to avoid auto-boxing).
	 */
	double getDouble();
	
	
	/**
	 * Get a view of this DataValue representing the value as a single-precision floating point number.
	 */
	DataValue<Float> asFloat();
	
	/**
	 * Get a view of this DataValue representing the value as a double-precision floating point number.
	 */
	DataValue<Double> asDouble();

	/**
	 * Get a view of this DataValue representing the value as an integer.
	 */
	DataValue<Integer> asInt();

	/**
	 * Get a view of this DataValue representing the value as a long integer.
	 */
	DataValue<Long> asLong();
	
	
	/**
	 * Get an empty DataValue of the same type as this DataValue.
	 */
	public DataValue<V> getNewDataValue();
	
	/**
	 * Get an independent and immutable copy of this DataValue (the returned value cannot be
	 * modified, and changes to this value will not be reflected in the returned value).
	 */
	public DataValue<V> immutableCopy();
	
	/**
	 * Returns true iff this DataValue represents a numeric value.
	 */
	public boolean isNumeric();
	
	
	// View/functional operations. 
	
		
	/**
	 * Get a view of this DataValue that is calculated using the given function applied to this value.
	 * 
	 * @param function
	 *            The function to calculate the new value from the original value.
	 */
	public <O> DataValue<O> apply(Function<V, O> function);
	
	
	/**
	 * Create a view of this DataValue in which the value is the result of
	 * adding the given value to this value.
	 * 
	 * @param value The value to add.
	 * @throws UnsupportedOperationException if this value is not numeric.
	 */
	public DataValue<?> add(Number value);
	
	/**
	 * Create a view of this DataValue in which the value is the result of
	 * adding the given value to this value.
	 * 
	 * @param value The value to add. Changes to the DataValue will be
	 *            reflected in the returned value.
	 * @throws UnsupportedOperationException if this or the given value are not numeric.
	 */
	public DataValue<?> add(DataValue<?> value);
	
	
	/**
	 * Create a view of this DataValue in which the value is the result of
	 * subtracting the given value from this value.
	 * 
	 * @param value The value to subtract.
	 * @throws UnsupportedOperationException if this value is not numeric.
	 */
	public DataValue<?> subtract(Number value);
	
	/**
	 * Create a view of this DataValue in which the value is the result of
	 * subtracting the given value from this value.
	 * 
	 * @param value The value to subtract. Changes to the DataValue will be
	 *            reflected in the returned value.
	 * @throws UnsupportedOperationException if this or the given value are not numeric.
	 */
	public DataValue<?> subtract(DataValue<?> value);
	
	/**
	 * Create a view of this DataValue in which the value is the result of
	 * multiplying the given value by this value.
	 * 
	 * @param value The value to multiply by.
	 * @throws UnsupportedOperationException if this value is not numeric.
	 */
	public DataValue<?> multiply(Number value);
	
	/**
	 * Create a view of this DataValue in which the value is the result of
	 * multiplying the given value by this value.
	 * 
	 * @param value The value to multiply by. Changes to the DataValue will be
	 *            reflected in the returned value.
	 * @throws UnsupportedOperationException if this or the given value are not numeric.
	 */
	public DataValue<?> multiply(DataValue<?> value);
	
	
	/**
	 * Create a view of this DataValue in which the value is the result of
	 * dividing this value by the given value.
	 * 
	 * @param value The value to divide by.
	 * @throws UnsupportedOperationException if this value is not numeric.
	 */
	public DataValue<?> divide(Number value);
	
	/**
	 * Create a view of this DataValue in which the value is the result of
	 * dividing this value by the given value.
	 * 
	 * @param value The value to divide by. Changes to the DataValue will be
	 *            reflected in the returned value.
	 * @throws UnsupportedOperationException if this or the given value are not numeric.
	 */
	public DataValue<?> divide(DataValue<?> value);
}