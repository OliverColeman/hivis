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

import java.math.BigDecimal;
import java.util.AbstractList;
import java.util.Date;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.reflect.TypeToken;

import hivis.data.view.CalcValue;
import hivis.data.view.CalcValue;
import hivis.data.view.Function;
import hivis.data.view.CalcValue.Op;
import hivis.data.view.CalcValue.SeriesOp;
import hivis.common.Util;
import hivis.data.view.AbstractValueView;

/**
 * Default base implementation of {@link DataValue}.
 * 
 * @author O. J. Coleman
 */
public abstract class AbstractDataValue<V> extends DataDefault implements DataValue<V> {
	private TypeToken<V> typeToken = new TypeToken<V>(getClass()) {};
	private Class<?> type = typeToken.getRawType();
	
	Map<Op, DataValue<V>> dvOp;
	Map<Op, DataValue<V>> rawOp;
	
	
	public AbstractDataValue() {
		super();
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public void set(Object value) {
		try {
			if (isNumeric()) {
				setValue(castToNumericType(value));
			}
			else {
				setValue((V) value);
			}
		}
		catch (ClassCastException ex) {
			throw new IllegalArgumentException("The given value of type " + value.getClass().getSimpleName() + " can not be cast to the type stored by " + this.getClass().getSimpleName());
		}
	}
	
	
	@Override
	public boolean isEmpty() {
		if (get() == null) {
			return getEmptyValue() == null;
		}
		return get().equals(getEmptyValue());
	}
	
	@Override
	public DataValue<V> immutableCopy() {
		V v = get();
		final V value = (v instanceof Data) ?  (V) ((Data) v).immutableCopy() : v;
		return new AbstractUnmodifiableDataValue<V>() {
			@Override
			public V get() {
				return value;
			}
			@Override
			public boolean isMutable() {
				return false;
			}
		};
	}
	
	@Override
	public boolean equalTo(Data o) {
		if (o == this) return true;
		if (!(o instanceof DataValue)) return false;
		DataValue<?> v = (DataValue<?>) o;
		if (!Util.equalsIncData(v.getType(), this.getType())) return false;
		return Util.equalsIncData(this.get(), v.get());
	}
	
	@Override
	public int equalToHashCode() {
		if (isMutable()) {
			throw new IllegalStateException("equalToHashCode() called on a mutable Data set.");
		}
		V val = get();
		if (val == null) return 0;
		return val.hashCode();
	}
	
	
	@Override
	public Class<?> getType() {
		// If the type info seems to be available, use it.
		if (type != null && !type.isAssignableFrom(Object.class)) return type;
		// Otherwise try to get type from instance.
		V e = get();
		if (e != null) {
			type = e.getClass();
			return type;
		}
		return null;
	}
	

	@Override
	public V getEmptyValue() {
		Class<?> type = getType();
		if (Double.class.isAssignableFrom(type)) {
			return (V) (Double) Double.NaN;
		}
		if (Float.class.isAssignableFrom(type)) {
			return (V) (Float) Float.NaN;
		}
		if (Long.class.isAssignableFrom(type)) {
			return (V) (Long) Long.MIN_VALUE;
		}
		if (Integer.class.isAssignableFrom(type)) {
			return (V) (Integer) Integer.MIN_VALUE;
		}
		if (Short.class.isAssignableFrom(type)) {
			return (V) (Short) Short.MIN_VALUE;
		}
		if (Byte.class.isAssignableFrom(type)) {
			return (V) (Byte) Byte.MIN_VALUE;
		}
		return null;
	}
	
	
	/**
	 * Get the value stored as a boolean.
	 * This default implementation casts the value given by {@link #get()}.
	 * Sub-classes may override this to improve efficiency.
	 * @see hivis.data.DataValue#getBoolean()
	 */
	@Override
	public boolean getBoolean() {
		return (boolean) (Boolean) get();
	}

	/**
	 * Get the value stored as an int.
	 * This default implementation casts the value given by {@link #get()}.
	 * Sub-classes may override this to improve efficiency.
	 * @see hivis.data.DataValue#getInt()
	 */
	@Override
	public int getInt() {
		return ((Number) get()).intValue();
	}

	/**
	 * Get the value stored as a long.
	 * This default implementation casts the value given by {@link #get()}.
	 * Sub-classes may override this to improve efficiency.
	 * @see hivis.data.DataValue#getLong()
	 */
	@Override
	public long getLong() {
		return ((Number) get()).longValue();
	}
	
	/**
	 * Get the value stored as a float.
	 * This default implementation casts the value given by {@link #get()}.
	 * Sub-classes may override this to improve efficiency.
	 * @see hivis.data.DataValue#getFloat()
	 */
	@Override
	public float getFloat() {
		return ((Number) get()).floatValue();
	}

	/**
	 * Get the value stored as a double.
	 * This default implementation casts the value given by {@link #get()}.
	 * Sub-classes may override this to improve efficiency.
	 * @see hivis.data.DataValue#getDouble()
	 */
	@Override
	public double getDouble() {
		return ((Number) get()).doubleValue();
	}
	

	/**
	 * Get a view of this DataValue, representing the value as a single-precision floating point number.
	 * This default implementation first checks if the type of this DataValue is Float and returns it if so,
	 * otherwise creates an {@link AbstractValueView} that casts the value to the correct type.
	 */
	@Override
	public DataValue<Float> asFloat() {
		if (Float.class.isAssignableFrom(getType())) {
			return (DataValue<Float>) this;
		}
		final DataValue<V> me = this;
		return new AbstractValueView<V, Float>(this) {
			public Float get() {
				return me.getFloat();
			}
			public float getFloat() {
				return me.getFloat();
			}
			public void updateView(DataEvent cause) {
			}
		};
	}
	
	/**
	 * Get a view of this DataValue representing the value as double-precision floating point number.
	 * This default implementation first checks if the type of this DataValue is Double and returns it if so,
	 * otherwise creates an {@link AbstractValueView} that casts the value to the correct type.
	 */
	@Override
	public DataValue<Double> asDouble() {
		if (Double.class.isAssignableFrom(getType())) {
			return (DataValue<Double>) this;
		}
		final DataValue<V> me = this;
		return new AbstractValueView<V, Double>(this) {
			public Double get() {
				return me.getDouble();
			}
			public double getDouble() {
				return me.getDouble();
			}
			public void updateView(DataEvent cause) {
			}
		};
	}
	
	/**
	 * Get a view of this DataValue representing the values as integers.
	 * This default implementation first checks if the type of this DataValue is Integer and returns it if so,
	 * otherwise creates an {@link AbstractValueView} that casts the value to the correct type.
	 */
	@Override
	public DataValue<Integer> asInt() {
		if (Integer.class.isAssignableFrom(getType())) {
			return (DataValue<Integer>) this;
		}
		final DataValue<V> me = this;
		return new AbstractValueView<V, Integer>(this) {
			public Integer get() {
				return me.getInt();
			}
			public int getInteger() {
				return me.getInt();
			}
			public void updateView(DataEvent cause) {
			}
		};
	}
	
	/**
	 * Get a view of this DataValue representing the values as long integers.
	 * This default implementation first checks if the type of this DataValue is Long and returns it if so,
	 * otherwise creates an {@link AbstractValueView} that casts the value to the correct type.
	 */
	@Override
	public DataValue<Long> asLong(){
		if (Long.class.isAssignableFrom(getType())) {
			return (DataValue<Long>) this;
		}
		final DataValue<V> me = this;
		return new AbstractValueView<V, Long>(this) {
			public Long get() {
				return me.getLong();
			}
			public long getLong() {
				return me.getLong();
			}
			public void updateView(DataEvent cause) {
			}
		};
	}
	
	@Override
	public DataValue<V> getNewDataValue() {
		return (DataValue<V>) getNewDataValue(getType());
	}
	
	
	public static <V> DataValue<V> getNewDataValue(Class<V> type) {
		if (type.equals(Float.class) || type.equals(float.class)) {
			return (DataValue<V>) new DataValueFloat();
		}
		if (type.equals(Double.class) || type.equals(double.class)) {
			return (DataValue<V>) new DataValueDouble();
		}
		if (type.equals(Integer.class) || type.equals(int.class)) {
			return (DataValue<V>) new DataValueInteger();
		}
		if (type.equals(Long.class) || type.equals(long.class)) {
			return (DataValue<V>) new DataValueLong();
		}
		return (DataValue<V>) new DataValueGeneric();
	}
	
	
	@Override
	public boolean isNumeric() {
		return Number.class.isAssignableFrom(getType());
	}
	
	
	@Override
	public <O> DataValue<O> apply(final Function<V, O> function) {
		final DataValue<V> me = this;
		
		// Determine function output type. This is used to set that data type of the CalcValue.
		Class<?> outputType = (new TypeToken<O>(getClass()) {}).getRawType();
		// If the type doesn't appear to have been provided via generics, then get the type from an example.
		if (outputType == null || outputType.equals(Object.class)) {
			outputType = function.apply(get()).getClass();
		}
		final Class<?> outputTypeFinal = outputType;
		
		return new CalcValue<V,O> (this) {
			@Override
			public O calc() {
				return function.apply(me.get());
			}
			@Override
			public Class<?> getType() {
				return outputTypeFinal;
			}
		};
	}
	
	
	private DataValue<V> op(Op op, Number value) {
		if (!isNumeric()) {
			throw new RuntimeException("Can not perform " + op.toString().toLowerCase() + " operation on non-numeric DataValue");
		}
		
		Class<?> envelopeClass = Util.getEnvelopeNumberType((Class<Number>) getType(), (Class<Number>) value.getClass(), true);
		
		if (envelopeClass.equals(Float.class)) {
			return new CalcValue.FloatValue.FuncRaw(op, this, value.floatValue());
		}
		if (envelopeClass.equals(Double.class)) {
			return new CalcValue.DoubleValue.FuncRaw(op, this, value.doubleValue());
		}
		if (envelopeClass.equals(Integer.class)) {
			return new CalcValue.IntValue.FuncRaw(op, this, value.intValue());
		}
		if (envelopeClass.equals(Long.class)) {
			return new CalcValue.LongValue.FuncRaw(op, this, value.longValue());
		}
		throw new UnsupportedOperationException("Can not determine numeric type for " + op.toString().toLowerCase() + " operation on DataValue.");
	}
	
	private DataValue<V> op(Op op, DataValue value) {
		if (!isNumeric() || !value.isNumeric()) {
			throw new RuntimeException("Can not perform " + op.toString().toLowerCase() + " operation on non-numeric DataValue");
		}
		
		Class<?> envelopeClass = Util.getEnvelopeNumberType((Class<Number>) getType(), (Class<Number>) value.getType(), true);
		
		if (envelopeClass.equals(Float.class)) {
			return new CalcValue.FloatValue.FuncDV(op, this, value);
		}
		if (envelopeClass.equals(Double.class)) {
			return new CalcValue.DoubleValue.FuncDV(op, this, value);
		}
		if (envelopeClass.equals(Integer.class)) {
			return new CalcValue.IntValue.FuncDV(op, this, value);
		}
		if (envelopeClass.equals(Long.class)) {
			return new CalcValue.LongValue.FuncDV(op, this, value);
		}
		throw new UnsupportedOperationException("Can not determine numeric type for " + op.toString().toLowerCase() + " operation on DataValue.");
	}
	
	@Override
	public DataValue<?> add(final Number value) {
		return op(Op.ADD, value);
	}
//	@Override
//	public DataValue<?> add(double value) {
//		return add((Double) value);
//	}
//	@Override
//	public DataValue<?> add(long value) {
//		return add((Long) value);
//	}
	@Override
	public DataValue<?> add(final DataValue<?> value) {
		return op(Op.ADD, value);
	}
	
	@Override
	public DataValue<?> subtract(final Number value) {
		return op(Op.SUBTRACT, value);
	}
//	@Override
//	public DataValue<?> subtract(double value) {
//		return subtract((Double) value);
//	}
//	@Override
//	public DataValue<?> subtract(long value) {
//		return subtract((Long) value);
//	}
	@Override
	public DataValue<?> subtract(final DataValue<?> value) {
		return op(Op.SUBTRACT, value);
	}
	
	@Override
	public DataValue<?> multiply(final Number value) {
		return op(Op.MULTIPLY, value);
	}
//	@Override
//	public DataValue<?> multiply(double value) {
//		return multiply((Double) value);
//	}
//	@Override
//	public DataValue<?> multiply(long value) {
//		return multiply((Long) value);
//	}
	@Override
	public DataValue<?> multiply(final DataValue<?> value) {
		return op(Op.MULTIPLY, value);
	}
	
	@Override
	public DataValue<?> divide(final Number value) {
		return op(Op.DIVIDE, value);
	}
//	@Override
//	public DataValue<?> divide(double value) {
//		return divide((Double) value);
//	}
//	@Override
//	public DataValue<?> divide(long value) {
//		return divide((Long) value);
//	}
	@Override
	public DataValue<?> divide(final DataValue<?> value) {
		return op(Op.DIVIDE, value);
	}
	
	
	private V castToNumericType(Object v) {
		if (!(v instanceof Number)) {
			throw new IllegalArgumentException("Argument non-numeric: " + v);
		}
		
		Number n = (Number) v;
		
		if (getType().equals(Double.class)) {
			return (V) ((Double) n.doubleValue());
		}
		if (getType().equals(Float.class)) {
			return (V) ((Float) n.floatValue());
		}
		
		// We're storing an integer type. Make sure we're passed an integer value.
		if (v instanceof Float || v instanceof Double) {
			if (n.doubleValue() != (int) n.doubleValue()) {
				throw new IllegalArgumentException("Value stores integer numbers but argument does not represent an integer: " + v);
			}
		}
		
		if (getType().equals(Long.class)) {
			return (V) ((Long) n.longValue());
		}
		if (getType().equals(Integer.class)) {
			return (V) ((Integer) n.intValue());
		}
		if (getType().equals(Short.class)) {
			return (V) ((Short) n.shortValue());
		}
		if (getType().equals(Byte.class)) {
			return (V) ((Byte) n.byteValue());
		}
		return null;
	}


	@Override
	public String toString() {
		return get().toString();
	}
	
	@Override
	public int compareTo(V o) {
		if (this instanceof Comparable) {
			Comparable<V> thisC = (Comparable<V>) this;
			return thisC.compareTo(o);
		}
		return 0;
	}
}
