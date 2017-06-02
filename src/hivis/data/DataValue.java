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
 * Changes to the value may be monitored via event listeners.
 * Views of the value may be obtained via various functions.
 * Implementations typically define the type of value to store. 
 * This may be numeric or any other kind of object.
 * 
 * @author O. J. Coleman
 */
public interface DataValue<V> extends Data, Comparable<V> {
	/**
	 * Get the value, if set, otherwise returns {@link #getEmptyValue()}.
	 */
	V get();

	/**
	 * Set the value.
	 */
	void setValue(V value);

	/**
	 * Set the value. Attempts to cast the given object to the type stored.
	 * For typed value setting use {@link DataValue#setValue(Object)}.
	 * @throws IllegalArgumentException if the given object cannot be cast to the type stored.
	 */
	void set(Object value);

	/**
	 * Get the value corresponding to an empty value. For floating-point numbers this is either Double.NaN or Float.NaN.
	 * For integer types it is Long.MIN_VALUE, Integer.MIN_VALUE, etc. For all other types this is usually null.
	 */
	V getEmptyValue();

	/**
	 * Returns true iff the stored value is considered empty
	 * @see #getEmptyValue()
	 */
	boolean isEmpty();
	
	/**
	 * Return the Class of the type stored.
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
	 * Get an empty DataVale of the same type as this DataValue.
	 */
	public DataValue<V> getNewDataValue();
	
	/**
	 * Get an immutable copy of this DataValue.
	 */
	public DataValue<V> immutableCopy();
	
	/**
	 * Returns true iff this DataValue stores a numeric value.
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
	
	
	public DataValue<?> add(Number value);
	//public DataValue<?> add(double value);
	//public DataValue<?> add(long value);
	public DataValue<?> add(DataValue<?> value);
	
	public DataValue<?> subtract(Number value);
	//public DataValue<?> subtract(double value);
	//public DataValue<?> subtract(long value);
	public DataValue<?> subtract(DataValue<?> value);
	
	public DataValue<?> multiply(Number value);
	//public DataValue<?> multiply(double value);
	//public DataValue<?> multiply(long value);
	public DataValue<?> multiply(DataValue<?> value);
	
	public DataValue<?> divide(Number value);
	//public DataValue<?> divide(double value);
	//public DataValue<?> divide(long value);
	public DataValue<?> divide(DataValue<?> value);
}