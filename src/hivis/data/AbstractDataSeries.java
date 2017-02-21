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

import hivis.data.view.CalcSeries;
import hivis.data.view.CalcSeries.Op;
import hivis.data.view.CalcValue;
import hivis.data.view.CalcValue.SeriesOp;
import hivis.data.view.Function;
import hivis.data.view.SeriesView;
import hivis.data.view.AbstractValueView;
import hivis.common.Util;
import hivis.data.view.AbstractSeriesView;
import hivis.data.view.SeriesViewRow;
import hivis.data.view.SeriesViewAppend;

/**
 * Default base implementation of {@link DataSeries}.
 * 
 * @author O. J. Coleman
 */
public abstract class AbstractDataSeries<V> extends DataSetDefault implements DataSeries<V> {
	private TypeToken<V> typeToken = new TypeToken<V>(getClass()) {};
	private Class<?> type = typeToken.getRawType();
	
	Map<SeriesOp, DataValue<V>> dataValueOp;
	
	private DataSeries.FloatSeries floatSeriesView;
	private DataSeries.DoubleSeries doubleSeriesView;
	private DataSeries.IntSeries intSeriesView;
	private DataSeries.LongSeries longSeriesView;
	
	@Override
	@SuppressWarnings("unchecked")
	public void set(int index, Object value) {
		try {
			if (isNumeric()) {
				setValue(index, castToNumericType(value));
			}
			else {
				setValue(index, (V) value);
			}
		}
		catch (ClassCastException ex) {
			throw new IllegalArgumentException("The given value of type " + value.getClass().getSimpleName() + " can not be cast to the type stored by " + this.getClass().getSimpleName());
		}
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public void append(Object value) {
		try {
			if (isNumeric()) {
				appendValue(castToNumericType(value));
			}
			else {
				appendValue((V) value);
			}
		}
		catch (ClassCastException ex) {
			throw new IllegalArgumentException("The given value of type " + value.getClass().getSimpleName() + " can not be cast to the type stored by " + this.getClass().getSimpleName());
		}
	}
	
	
	@Override
	public boolean isEmpty(int index) {
		if (get(index) == null) {
			return getEmptyValue() == null;
		}
		return get(index).equals(getEmptyValue());
	}
	
	@Override
	public Class<?> getType() {
		// If the type info seems to be available, use it.
		if (type != null && !type.isAssignableFrom(Object.class)) return type;
		// Otherwise try to get type from an instance.
		V e = length() > 0 ? get(0) : null;
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
	 * Get the value stored at the given index as a boolean.
	 * This default implementation casts the value given by {@link #get(int)}.
	 * Sub-classes may override this to improve efficiency.
	 * @see hivis.data.DataSeries#getBoolean(int)
	 */
	@Override
	public boolean getBoolean(int index) {
		return (boolean) (Boolean) get(index);
	}

	/**
	 * Get the value stored at the given index as an int.
	 * This default implementation casts the value given by {@link #get(int)}.
	 * Sub-classes may override this to improve efficiency.
	 * @see hivis.data.DataSeries#getInt(int)
	 */
	@Override
	public int getInt(int index) {
		return ((Number) get(index)).intValue();
	}

	/**
	 * Get the value stored at the given index as a long.
	 * This default implementation casts the value given by {@link #get(int)}.
	 * Sub-classes may override this to improve efficiency.
	 * @see hivis.data.DataSeries#getLong(int)
	 */
	@Override
	public long getLong(int index) {
		return ((Number) get(index)).longValue();
	}
	
	/**
	 * Get the value stored at the given index as a float.
	 * This default implementation casts the value given by {@link #get(int)}.
	 * Sub-classes may override this to improve efficiency.
	 * @see hivis.data.DataSeries#getFloat(int)
	 */
	@Override
	public float getFloat(int index) {
		return ((Number) get(index)).floatValue();
	}

	/**
	 * Get the value stored at the given index as a double.
	 * This default implementation casts the value given by {@link #get(int)}.
	 * Sub-classes may override this to improve efficiency.
	 * @see hivis.data.DataSeries#getDouble(int)
	 */
	@Override
	public double getDouble(int index) {
		return ((Number) get(index)).doubleValue();
	}
	
	
	@Override
	public DataValue<V> getDataValue(int index) {
		return new DataValueView(index);
	}
	private class DataValueView extends AbstractDataValue<V> implements DataListener {
		int index;
		// We keep track of the current value via dataChanged, so we only fire a change event if the data actually changes.
		V currentValue;
		public DataValueView(int index) {
			AbstractDataSeries.this.addChangeListener(this);
			this.index = index;
			currentValue = AbstractDataSeries.this.get(index);
		}
		@Override
		public V get() {
			return currentValue;
		}
		@Override
		public void setValue(V value) {
			throw new UnsupportedOperationException("Can not set value of DataValue that is a view of an element in a DataSeries.");
		}
		@Override
		public void dataChanged(DataEvent event) {
			if (!Util.equalsIncNull(currentValue, AbstractDataSeries.this.get(index))) {
				currentValue = AbstractDataSeries.this.get(index);
				this.fireChangeEvent(new DataEvent(this, event));
			}
		}
	}
	

	/**
	 * Get a view of this series representing the values as single-precision floating point numbers.
	 * This default implementation first checks if the type of this series is {@link DataSeries.FloatSeries} 
	 * and returns it if so, otherwise creates a wrapper that represent the values as floats.
	 */
	@Override
	public DataSeries.FloatSeries asFloat() {
		if (floatSeriesView == null) {
			if (this instanceof DataSeries.FloatSeries) {
				floatSeriesView = (DataSeries.FloatSeries) this;
			}
			else {
				floatSeriesView = new FloatSeriesView();
			}
		}
		return floatSeriesView;
	}
	private class FloatSeriesView extends AbstractSeriesView<V, Float> implements DataSeries.FloatSeries {
		@Override public int length() {	return AbstractDataSeries.this.length(); }
		@Override public Float get(int index) { return AbstractDataSeries.this.getFloat(index); }
		@Override public float getFloat(int index) { return AbstractDataSeries.this.getFloat(index); }
		@Override public void updateView(Object cause) {}
	}
	
	/**
	 * Get a view of this series representing the values as double-precision floating point numbers.
	 * This default implementation first checks if the type of this series is {@link DataSeries.DoubleSeries} 
	 * and returns it if so, otherwise creates a wrapper that represent the values as doubles.
	 */
	@Override
	public DataSeries.DoubleSeries asDouble() {
		if (doubleSeriesView == null) {
			if (this instanceof DataSeries.DoubleSeries) {
				doubleSeriesView = (DataSeries.DoubleSeries) this;
			}
			else {
				doubleSeriesView = new DoubleSeriesView();
			}
		}
		return doubleSeriesView;
	}
	private class DoubleSeriesView extends AbstractSeriesView<V, Double> implements DataSeries.DoubleSeries {
		@Override public int length() {	return AbstractDataSeries.this.length(); }
		@Override public Double get(int index) { return AbstractDataSeries.this.getDouble(index); }
		@Override public double getDouble(int index) { return AbstractDataSeries.this.getDouble(index); }
		@Override public void updateView(Object cause) {}
	}
	
	/**
	 * Get a view of this series representing the values as integers.
	 * This default implementation first checks if the type of this series is {@link DataSeries.IntSeries} 
	 * and returns it if so, otherwise creates a wrapper that represent the values as integers.
	 */
	@Override
	public DataSeries.IntSeries asInt() {
		if (intSeriesView == null) {
			if (this instanceof DataSeries.IntSeries) {
				intSeriesView = (DataSeries.IntSeries) this;
			}
			else {
				intSeriesView = new IntSeriesView();
			}
		}
		return intSeriesView;
	}
	private class IntSeriesView extends AbstractSeriesView<V, Integer> implements DataSeries.IntSeries {
		@Override public int length() {	return AbstractDataSeries.this.length(); }
		@Override public Integer get(int index) { return AbstractDataSeries.this.getInt(index); }
		@Override public int getInt(int index) { return AbstractDataSeries.this.getInt(index); }
		@Override public void updateView(Object cause) {}
	}
	
	/**
	 * Get a view of this series representing the values as long integers.
	 * This default implementation first checks if the type of this series is {@link DataSeries.LongSeries} 
	 * and returns it if so, otherwise creates a wrapper that represent the values as longs.
	 */
	@Override
	public DataSeries.LongSeries asLong() {
		if (longSeriesView == null) {
			if (this instanceof DataSeries.LongSeries) {
				longSeriesView = (DataSeries.LongSeries) this;
			}
			else {
				longSeriesView = new LongSeriesView();
			}
		}
		return longSeriesView;
	}
	private class LongSeriesView extends AbstractSeriesView<V, Long> implements DataSeries.LongSeries {
		@Override public int length() {	return AbstractDataSeries.this.length(); }
		@Override public Long get(int index) { return AbstractDataSeries.this.getLong(index); }
		@Override public long getLong(int index) { return AbstractDataSeries.this.getLong(index); }
		@Override public void updateView(Object cause) {}
	}
	
	/**
	 * This default implementation uses {@link #get(int)} to populate the array.
	 * @see hivis.data.DataSeries#asArray()
	 */
	@Override
	public V[] asArray() {
		return asArray((V[]) new Object[length()]);
	}
	

	/**
	 * This default implementation uses {@link #getBoolean(int)} to populate the array.
	 * @see hivis.data.DataSeries#asBooleanArray()
	 */
	@Override
	public boolean[] asBooleanArray() {
		return asBooleanArray(new boolean[length()]);
	}
	
	/**
	 * This default implementation uses {@link #getInt(int)} to populate the array.
	 * @see hivis.data.DataSeries#asIntArray()
	 */
	@Override
	public int[] asIntArray() {
		return asIntArray(new int[length()]);
	}
	
	/**
	 * This default implementation uses {@link #getLong(int)} to populate the array.
	 * @see hivis.data.DataSeries#asLongArray()
	 */
	@Override
	public long[] asLongArray() {
		return asLongArray(new long[length()]);
	}
	
	/**
	 * This default implementation uses {@link #getFloat(int)} to populate the array.
	 * @see hivis.data.DataSeries#asDoubleArray()
	 */
	@Override
	public float[] asFloatArray() {
		return asFloatArray(new float[length()]);
	}
	
	/**
	 * This default implementation uses {@link #getDouble(int)} to populate the array.
	 * @see hivis.data.DataSeries#asDoubleArray()
	 */
	@Override
	public double[] asDoubleArray() {
		return asDoubleArray(new double[length()]);
	}
	
	/**
	 * This default implementation uses {@link #get(int)}.toString() to populate the array.
	 * @see hivis.data.DataSeries#asStringArray()
	 */
	@Override
	public String[] asStringArray() {
		return asStringArray(new String[length()]);
	}
	
	
	/**
	 * This default implementation uses {@link #get(int)} to populate the array.
	 * @see hivis.data.DataSeries#asArray(Object[])
	 */
	@Override
	public V[] asArray(V[] data) {
		if (data == null || data.length < length()) {
			data = (V[]) new Object[length()];
		}
		for (int i = 0; i < length(); i++) {
			data[i] = get(i);
		}
		return data;
	}
	

	/**
	 * This default implementation uses {@link #getBoolean(int)} to populate the array.
	 * @see hivis.data.DataSeries#asBooleanArray(boolean[])
	 */
	@Override
	public boolean[] asBooleanArray(boolean[] data) {
		if (data == null || data.length < length()) {
			data = new boolean[length()];
		}
		for (int i = 0; i < length(); i++) {
			data[i] = getBoolean(i);
		}
		return data;
	}
	
	/**
	 * This default implementation uses {@link #getInt(int)} to populate the array.
	 * @see hivis.data.DataSeries#asIntArray(int[])
	 */
	@Override
	public int[] asIntArray(int[] data) {
		if (data == null || data.length < length()) {
			data = new int[length()];
		}
		for (int i = 0; i < length(); i++) {
			data[i] = getInt(i);
		}
		return data;
	}
	
	/**
	 * This default implementation uses {@link #getLong(int)} to populate the array.
	 * @see hivis.data.DataSeries#asLongArray(long[])
	 */
	@Override
	public long[] asLongArray(long[] data) {
		if (data == null || data.length < length()) {
			data = new long[length()];
		}
		for (int i = 0; i < length(); i++) {
			data[i] = getLong(i);
		}
		return data;
	}
	
	/**
	 * This default implementation uses {@link #getFloat(int)} to populate the array.
	 * @see hivis.data.DataSeries#asFloatArray(double[])
	 */
	@Override
	public float[] asFloatArray(float[] data) {
		if (data == null || data.length < length()) {
			data = new float[length()];
		}
		for (int i = 0; i < length(); i++) {
			data[i] = getFloat(i);
		}
		return data;
	}
	
	/**
	 * This default implementation uses {@link #getDouble(int)} to populate the array.
	 * @see hivis.data.DataSeries#asDoubleArray(double[])
	 */
	@Override
	public double[] asDoubleArray(double[] data) {
		if (data == null || data.length < length()) {
			data = new double[length()];
		}
		for (int i = 0; i < length(); i++) {
			data[i] = getDouble(i);
		}
		return data;
	}
	
	/**
	 * This default implementation uses {@link #get(int)}.toString() to populate the array.
	 * @see hivis.data.DataSeries#asStringArray(String[])
	 */
	@Override
	public String[] asStringArray(String[] data) {
		if (data == null || data.length < length()) {
			data = new String[length()];
		}
		for (int i = 0; i < length(); i++) {
			data[i] = get(i).toString();
		}
		return data;
	}
	
	
	@Override
	public DataSeries<V> getNewSeries() {
		return (DataSeries<V>) getNewSeries(getType());
	}
	
	
	public static <V> DataSeries<V> getNewSeries(Class<V> type) {
		if (type.equals(Float.class) || type.equals(float.class)) {
			return (DataSeries<V>) new DataSeriesFloat();
		}
		if (type.equals(Double.class) || type.equals(double.class)) {
			return (DataSeries<V>) new DataSeriesDouble();
		}
		if (type.equals(Integer.class) || type.equals(int.class)) {
			return (DataSeries<V>) new DataSeriesInteger();
		}
		if (type.equals(Long.class) || type.equals(long.class)) {
			return (DataSeries<V>) new DataSeriesLong();
		}
		throw new UnsupportedOperationException("Don't know how to create a DataSeries containing type " + type);
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
		throw new UnsupportedOperationException("Don't know how to create a DataSeries containing type " + type);
	}
	
	
	@Override
	public boolean isNumeric() {
		return Number.class.isAssignableFrom(getType());
	}
	
	
	private DataValue<?> op(SeriesOp op) {
		if (dataValueOp == null || !dataValueOp.containsKey(op)) {
			if (dataValueOp == null) dataValueOp = new EnumMap<>(SeriesOp.class);
			if (op.realOutput) {
				if (getType().equals(Float.class)) {
					dataValueOp.put(op, new CalcValue.DoubleValue.SeriesFunc(this, op));
				}
				if (getType().equals(Double.class)) {
					dataValueOp.put(op, new CalcValue.DoubleValue.SeriesFunc(this, op));
				}
				if (getType().equals(Integer.class)) {
					dataValueOp.put(op, new CalcValue.DoubleValue.SeriesFunc(this, op));
				}
				if (getType().equals(Long.class)) {
					dataValueOp.put(op, new CalcValue.DoubleValue.SeriesFunc(this, op));
				}
			}
			else {
				if (getType().equals(Float.class)) {
					dataValueOp.put(op, new CalcValue.FloatValue.SeriesFunc(this, op));
				}
				if (getType().equals(Double.class)) {
					dataValueOp.put(op, new CalcValue.DoubleValue.SeriesFunc(this, op));
				}
				if (getType().equals(Integer.class)) {
					dataValueOp.put(op, new CalcValue.IntValue.SeriesFunc(this, op));
				}
				if (getType().equals(Long.class)) {
					dataValueOp.put(op, new CalcValue.LongValue.SeriesFunc(this, op));
				}
			}
		}
		return dataValueOp.get(op);
	}
	
	@Override
	public DataValue<V> min() {
		return (DataValue<V>) op(SeriesOp.MIN);
	}
	@Override
	public DataValue<V> max() {
		return (DataValue<V>) op(SeriesOp.MAX);
	}
	@Override
	public DataValue<V> sum() {
		return (DataValue<V>) op(SeriesOp.SUM);
	}
	@Override
	public DataValue<V> product() {
		return (DataValue<V>) op(SeriesOp.PRODUCT);
	}
	@Override
	public DataValue<Double> mean() {
		return (DataValue<Double>) op(SeriesOp.MEAN);
	}
	@Override
	public DataValue<Double> variance() {
		return (DataValue<Double>) op(SeriesOp.VARIANCE);
	}
	@Override
	public DataValue<Double> stdDev() {
		return (DataValue<Double>) op(SeriesOp.STD_DEV);
	}
	
	@Override
	public V maxValue() {
		return max().get();
	}
	@Override
	public V minValue() {
		return min().get();
	}
	
	@Override
	public SeriesView<V> select(int... indices) {
		return new SeriesViewRow<>(this, indices);
	}
	
	
	@Override
	public <O> SeriesView<O> apply(final Function<V, O> function) {
		final DataSeries<V> me = this;
		
		return new CalcSeries<V,O> (this) {
			@Override
			public O calc(int index) {
				return function.apply(me.get(index));
			}
			
			@Override
			public DataSeries<O> getNewSeries() {
				return (DataSeries<O>) getNewSeries(function.outputTypeToken.getRawType());
			}
		};
	}
	
	
	@Override
	public SeriesView<V> append(DataSeries<V> otherSeries) {
		return new SeriesViewAppend<V>(this, otherSeries);
	}
	
	
	
	private SeriesView<?> op(Op op, Number value) {
		if (!isNumeric()) {
			throw new RuntimeException("Can not perform " + op.toString().toLowerCase() + " operation on non-numeric DataSeries.");
		}
		
		Class<?> envelopeClass = op.realOutput ? Double.class : Util.getEnvelopeNumberType((Class<Number>) getType(), (Class<Number>) value.getClass(), true);
		
		if (envelopeClass.equals(Float.class)) {
			return new CalcSeries.FloatSeries.FuncValue(op, this, value.floatValue());
		}
		if (envelopeClass.equals(Double.class)) {
			return new CalcSeries.DoubleSeries.FuncValue(op, this, value.doubleValue());
		}
		if (envelopeClass.equals(Integer.class)) {
			return new CalcSeries.IntSeries.FuncValue(op, this, value.intValue());
		}
		if (envelopeClass.equals(Long.class)) {
			return new CalcSeries.LongSeries.FuncValue(op, this, value.longValue());
		}
		throw new UnsupportedOperationException();
	}
	
	private SeriesView<?> op(Op op, DataValue<?> value) {
		if (!isNumeric()) {
			throw new RuntimeException("Can not perform " + op.toString().toLowerCase() + " operation on non-numeric DataSeries.");
		}
		if (!value.isNumeric()) {
			throw new RuntimeException("Can not perform " + op.toString().toLowerCase() + " operation with non-numeric DataValue argument.");
		}
		
		Class<?> envelopeClass = op.realOutput ? Double.class : Util.getEnvelopeNumberType((Class<Number>) getType(), (Class<Number>) value.getType(), true);
		
		if (envelopeClass.equals(Float.class)) {
			return new CalcSeries.FloatSeries.FuncValue(op, this, value);
		}
		if (envelopeClass.equals(Double.class)) {
			return new CalcSeries.DoubleSeries.FuncValue(op, this, value);
		}
		if (envelopeClass.equals(Integer.class)) {
			return new CalcSeries.IntSeries.FuncValue(op, this, value);
		}
		if (envelopeClass.equals(Long.class)) {
			return new CalcSeries.LongSeries.FuncValue(op, this, value);
		}
		throw new UnsupportedOperationException();
	}
	
	private SeriesView<?> op(Op op, DataSeries<?> series) {
		if (this.length() != series.length()) {
			throw new IllegalArgumentException("Can not " + op.toString().toLowerCase() + " two DataSeries with differing lengths.");
		}
		
		if (!isNumeric()) {
			throw new RuntimeException("Can not perform " + op.toString().toLowerCase() + " operation on non-numeric DataSeries.");
		}
		if (!series.isNumeric()) {
			throw new RuntimeException("Can not perform " + op.toString().toLowerCase() + " operation with non-numeric DataSeries argument.");
		}
		
		Class<?> envelopeClass = op.realOutput ? Double.class : Util.getEnvelopeNumberType((Class<Number>) getType(), (Class<Number>) series.getType(), true);
		
		if (envelopeClass.equals(Float.class)) {
			return new CalcSeries.FloatSeries.FuncSeries(op, this, series);
		}
		if (envelopeClass.equals(Double.class)) {
			return new CalcSeries.DoubleSeries.FuncSeries(op, this, series);
		}
		if (envelopeClass.equals(Integer.class)) {
			return new CalcSeries.IntSeries.FuncSeries(op, this, series);
		}
		if (envelopeClass.equals(Long.class)) {
			return new CalcSeries.LongSeries.FuncSeries(op, this, series);
		}
		throw new UnsupportedOperationException();
	}
	
	
	@Override
	public SeriesView<?> add(Number value) {
		return op(Op.ADD, value);
	}
	@Override
	public SeriesView<?> add(DataSeries<?> series) {
		return op(Op.ADD, series);
	}
	@Override
	public SeriesView<?> add(DataValue<?> value) {
		return op(Op.ADD, value);
	}
	
	@Override
	public SeriesView<?> subtract(Number value) {
		return op(Op.SUBTRACT, value);
	}
	@Override
	public SeriesView<?> subtract(DataSeries<?> series) {
		return op(Op.SUBTRACT, series);
	}
	@Override
	public SeriesView<?> subtract(DataValue<?> value) {
		return op(Op.SUBTRACT, value);
	}
	
	@Override
	public SeriesView<?> multiply(Number value) {
		return op(Op.MULTIPLY, value);
	}
	@Override
	public SeriesView<?> multiply(DataSeries<?> series) {
		return op(Op.MULTIPLY, series);
	}
	@Override
	public SeriesView<?> multiply(DataValue<?> value) {
		return op(Op.MULTIPLY, value);
	}
	
	@Override
	public SeriesView<?> divide(Number value) {
		return op(Op.DIVIDE, value);
	}
	@Override
	public SeriesView<?> divide(DataSeries<?> series) {
		return op(Op.DIVIDE, series);
	}
	@Override
	public SeriesView<?> divide(DataValue<?> value) {
		return op(Op.DIVIDE, value);
	}
	
	
	private V castToNumericType(Object v) {
		if (!(v instanceof Number)) {
			throw new IllegalArgumentException("value is non-numeric: \"" + v + "\"");
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
				throw new IllegalArgumentException("Series stores integer numbers but argument does not represent an integer: \"" + v + "\"");
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
		int len = length();
		
		StringBuilder out = new StringBuilder();
		out.append("DataSeries (").append(len).append(") [ ");
		
		if (len == 0) {
			return out.append(" ]").toString();
		}
		
		// Get minimum and maximum values for numeric series.
		boolean numeric = false;
		double magnitude = 0;
		if ((Object) get(0) instanceof Number) {
			numeric = true;
			for (int r = 0; r < len; r++) {
				magnitude = Math.max(magnitude, Math.abs(((Number) get(r)).doubleValue()));
			}
		}
		
		String defaultFormat = numeric ? getFormat(magnitude, true) : getFormat(get(0), false);
		int width = 0;
		
		if (numeric || (Object) get(0) instanceof Date) {
			width = String.format(defaultFormat, get(0)).length();
		}
		else {
			int s = Math.min(len, 25);
			for (int i = 1; i < s; i++) {
				int l = get(i).toString().length();
				if (l > width) {
					width = l;
				}
			}
			int e = Math.max(s, len-25);
			if (e < len) {
				for (int i = e; i < len; i++) {
					int l = get(i).toString().length();
					if (l > width) {
						width = l;
					}
				}
			}
		}
		
		boolean wrap = width > 15;
		
		if (len > 0) {
			if (wrap) {
				out.append("\n\t").append(String.format(defaultFormat, get(0)));
			}
			else {
				out.append(" ").append(String.format(getFormat(get(0), numeric), get(0)).trim());
			}
			
			int s = Math.min(len, 25);
			for (int i = 1; i < s; i++) {
				if (wrap) {
					out.append(" ;").append("\n\t").append(String.format(defaultFormat, get(i)));
				}
				else {
					out.append(" ;").append(" ").append(String.format(getFormat(get(i), numeric), get(i)).trim());
				}
			}
			int e = Math.max(s, len-25);
			if (e < len) {
				if (e > s)
					out.append(",").append(wrap ? "\n\t" : " ").append("...");
				for (int i = e; i < len; i++) {
					if (wrap) {
						out.append(" ;").append("\n\t").append(String.format(defaultFormat, get(i)));
					}
					else {
						out.append(" ;").append(" ").append(String.format(getFormat(get(i), numeric), get(i)).trim());
					}
				}
			}
		}
		return out.append(" ]").toString();
	}
	
	private String getFormat(Object v, boolean numeric) {
		if (numeric) {
			double d = ((Number) v).doubleValue();
			boolean big = d >= 1000000000;
			if ((Object) get(0) instanceof Float || (Object) get(0) instanceof Double) {
				String grouping = big ? "" : ", ";
				int precision = (int) Math.max(0, Math.min(6, 6-("" + ((int) Math.ceil(d))).length()));
				String subFrm = "." + precision + (big ? "e" : "f");
				String str = String.format("%1$" + grouping + "1" + subFrm, -d);
				int width = str.length();
				return "%1$" + grouping + "" + width + subFrm;
			}
			else {
				String str = String.format("%1$,1d", -((int) Math.ceil(d)));
				int width = str.length();
				return "%1$," + width + "d";
			}
		}
		else if ((Object) get(0) instanceof Date) {
			return "%1$tF %1$tT";
		}
		else {
			return "%1$s";
		}
	}
	
	@Override
	public List<V> asList() {
		return new Lst();
	}

	@Override
	public Iterator<V> iterator() {
		return asList().iterator();
	}
	
	
	protected class Lst extends AbstractList<V> {
		@Override
		public V get(int arg0) {
			return AbstractDataSeries.this.get(arg0);
		}

		@Override
		public int size() {
			return length();
		}
		
	}
	
	
	@Override
	public DataSeries<Double> toUnitRange() {
		if (!isNumeric()) {
			throw new UnsupportedOperationException("Cannot perform toUnitRange operation on non-numeric DataSeries containing " + getType().getSimpleName());
		}
		return new UnitSeries(this);
	}

	// A CalcSeries that scales the values in a given input series to unit
	// values [0, 1].
	// When the input series changes the scaled values are also updated.
	private class UnitSeries extends CalcSeries<Object, Double> {
		public UnitSeries(DataSeries input) {
			super(input);
		}

		// Updates the cache field in CalcSeries. This gets called whenever a
		// change to the input DataSeries occurs.
		// We override this rather than calc() because we need to know the min
		// and max over the series before we can convert values to unit range.
		@Override
		public void updateView(Object cause) {
			// Suppress change events occurring until we've finished updating
			// the values.
			this.beginChanges(this);
			
			// Make sure cache series is the right length.
			cache.resize(length());

			DataSeries<?> input = inputSeries.get(0);
			Number minObj = (Number) input.min().get();
			Number maxObj = (Number) input.max().get();

			if (minObj instanceof Float || minObj instanceof Double) {
				double min = ((Number) minObj).doubleValue();
				double max = ((Number) maxObj).doubleValue();
				double range = max - min;

				// Then set values.
				for (int i = 0; i < length(); i++) {
					// Convert to unit range.
					double value = (input.getDouble(i) - min) / range;
					cache.setValue(i, value);
				}
			} else {
				long min = ((Number) minObj).longValue();
				long max = ((Number) maxObj).longValue();
				double range = max - min;

				// Then set values.
				for (int i = 0; i < length(); i++) {
					// Convert to unit range.
					double value = (input.getLong(i) - min) / range;
					cache.setValue(i, value);
				}
			}

			this.finishChanges(this);
		}

		// Not used but must implement from abstract class.
		@Override
		public Double calc(int index) {
			return 0.0d;
		}
	}
}

