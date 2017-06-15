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

import java.lang.reflect.Array;
import java.time.temporal.TemporalAccessor;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.reflect.TypeToken;

import hivis.common.HV;
import hivis.common.Util;
import hivis.data.view.AbstractSeriesView;
import hivis.data.view.AbstractSeriesViewMultiple;
import hivis.data.view.CalcSeries;
import hivis.data.view.CalcSeries.Op;
import hivis.data.view.CalcValue;
import hivis.data.view.CalcValue.SeriesOp;
import hivis.data.view.Function;
import hivis.data.view.GroupedSeries;
import hivis.data.view.SeriesView;
import hivis.data.view.SeriesViewAppend;
import hivis.data.view.SeriesViewRow;
import hivis.data.view.SortedSeries;

/**
 * Default base implementation of {@link DataSeries}.
 * 
 * @author O. J. Coleman
 */
public abstract class AbstractDataSeries<V> extends DataDefault implements DataSeries<V> {
	private TypeToken<V> typeToken = new TypeToken<V>(getClass()) {};
	
	/**
	 * The data type represented by this series, if available.
	 */
	protected Class<?> type = typeToken.getRawType();
	
	Map<SeriesOp, DataValue<V>> dataValueOp;
	
	private DataSeries.FloatSeries floatSeriesView;
	private DataSeries.DoubleSeries doubleSeriesView;
	private DataSeries.IntSeries intSeriesView;
	private DataSeries.LongSeries longSeriesView;
	private SeriesView<V> unmodifiableView;
	private int equalToHashCode = 0; // cached hashcode for equalToHashCode().
	

	public AbstractDataSeries() {
	}

	public AbstractDataSeries(Data container) {
		super(container);
	}

	public static <V> DataSeries<V> getNewSeries(Class<V> type) {
		if (type != null) {
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
		}
		return (DataSeries<V>) new DataSeriesGeneric<V>();
		//throw new UnsupportedOperationException("Don't know how to create a DataSeries containing type " + type);
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
	public boolean isEmpty(int index) {
		if (get(index) == null) {
			return getEmptyValue() == null;
		}
		return get(index).equals(getEmptyValue());
	}
	
	
	@Override
	public DataSeries<V> immutableCopy() {
		V[] values = asArray();
		for (int i = 0; i < values.length; i++) {
			if (values[i] instanceof Data) {
				values[i] = (V) ((Data) values[i]).immutableCopy();
			}
		}
		return new AbstractImmutableDataSeries<V>() {
			@Override
			public int length() {
				return values.length;
			}
			@Override
			public V get(int index) {
				if (index < 0 || index >= values.length) return getEmptyValue();
				return values[index];
			}
			@Override
			public void update(DataEvent cause) {}
		};
	}
	
	@Override
	public boolean isNumeric(int index) {
		return isNumeric();
	}
	
	@Override
	public boolean equalTo(Data o) {
		if (o == this) return true;
		if (!(o instanceof DataSeries)) return false;
		DataSeries<?> s = (DataSeries<?>) o;
		if (s.length() != this.length()) return false;
		if (!Util.equalsIncData(s.getType(), this.getType())) return false;
		DataSeries<V> st = (DataSeries<V>) o;
		try {
			this.lock();
			try {
				st.lock();
				Iterator<V> thisItr = this.iterator();
				Iterator<V> stItr = st.iterator();
				while (thisItr.hasNext()) {
					if (!Util.equalsIncData(thisItr.next(), stItr.next())) return false;
				}
				return true;
			}
			finally {
				st.unlock();
			}
		}
		finally {
			this.unlock();
		}
	}
	
	@Override
	public int equalToHashCode() {
		if (isMutable()) {
			throw new IllegalStateException("equalToHashCode() called on a mutable Data set.");
		}
		if (equalToHashCode == 0) {
			equalToHashCode = 1;
			for (V v : this) {
				equalToHashCode = 31 * equalToHashCode + (v==null ? 0 : v.hashCode());
			}
		}
		return equalToHashCode;
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
		if (type != null) {
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

	/**
	 * This default implementation uses {@link #get(int)} to populate the array.
	 * @see hivis.data.DataSeries#asArray()
	 */
	@Override
	public V[] asArray() {
		Class<?> type = getType();
		if (type == null) type = Object.class;
		return asArray((V[]) Array.newInstance(type, length()));
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
		lock();
		try {
			if (data == null || data.length < length()) {
				data = (V[]) Array.newInstance(this.getType(), length());
			}
			for (int i = 0; i < length(); i++) {
				data[i] = get(i);
			}
			return data;
		}
		finally {
			unlock();
		}
	}

	/**
	 * This default implementation uses {@link #getBoolean(int)} to populate the array.
	 * @see hivis.data.DataSeries#asBooleanArray(boolean[])
	 */
	@Override
	public boolean[] asBooleanArray(boolean[] data) {
		lock();
		try {
			if (data == null || data.length < length()) {
				data = new boolean[length()];
			}
			for (int i = 0; i < length(); i++) {
				data[i] = getBoolean(i);
			}
			return data;
		}
		finally {
			unlock();
		}
	}

	/**
	 * This default implementation uses {@link #getInt(int)} to populate the array.
	 * @see hivis.data.DataSeries#asIntArray(int[])
	 */
	@Override
	public int[] asIntArray(int[] data) {
		lock();
		try {
			if (data == null || data.length < length()) {
				data = new int[length()];
			}
			for (int i = 0; i < length(); i++) {
				data[i] = getInt(i);
			}
			return data;
		}
		finally {
			unlock();
		}
	}

	/**
	 * This default implementation uses {@link #getLong(int)} to populate the array.
	 * @see hivis.data.DataSeries#asLongArray(long[])
	 */
	@Override
	public long[] asLongArray(long[] data) {
		lock();
		try {
			if (data == null || data.length < length()) {
				data = new long[length()];
			}
			for (int i = 0; i < length(); i++) {
				data[i] = getLong(i);
			}
			return data;
		}
		finally {
			unlock();
		}
	}

	/**
	 * This default implementation uses {@link #getFloat(int)} to populate the array.
	 * @see hivis.data.DataSeries#asFloatArray(float[])
	 */
	@Override
	public float[] asFloatArray(float[] data) {
		lock();
		try {
			if (data == null || data.length < length()) {
				data = new float[length()];
			}
			for (int i = 0; i < length(); i++) {
				data[i] = getFloat(i);
			}
			return data;
		}
		finally {
			unlock();
		}
	}

	/**
	 * This default implementation uses {@link #getDouble(int)} to populate the array.
	 * @see hivis.data.DataSeries#asDoubleArray(double[])
	 */
	@Override
	public double[] asDoubleArray(double[] data) {
		lock();
		try {
			if (data == null || data.length < length()) {
				data = new double[length()];
			}
			for (int i = 0; i < length(); i++) {
				data[i] = getDouble(i);
			}
			return data;
		}
		finally {
			unlock();
		}
	}

	/**
	 * This default implementation uses {@link #get(int)}.toString() to populate the array.
	 * @see hivis.data.DataSeries#asStringArray(String[])
	 */
	@Override
	public String[] asStringArray(String[] data) {
		lock();
		try {
			if (data == null || data.length < length()) {
				data = new String[length()];
			}
			for (int i = 0; i < length(); i++) {
				data[i] = get(i).toString();
			}
			return data;
		}
		finally {
			unlock();
		}
	}
	
	@Override
	public DataSeries<V> copy(){
		DataSeries<V> copy = getNewSeries();
		copy.appendAllValues(this.asArray());
		return copy;
	}

	public SeriesView<V> unmodifiableView() {
		if (unmodifiableView == null) {
			final DataSeries<V> me = this;
			unmodifiableView = new AbstractSeriesView<V>(this) {
				@Override
				public int length() {
					return me.length();
				}
				@Override
				public V get(int index) {
					return me.get(index);
				}
				@Override
				public int getInt(int index) {
					return me.getInt(index);
				}
				@Override
				public long getLong(int index) {
					return me.getLong(index);
				}
				@Override
				public float getFloat(int index) {
					return me.getFloat(index);
				}
				@Override
				public double getDouble(int index) {
					return me.getDouble(index);
				}
				@Override
				public void update(DataEvent cause) {
				}
			};
		}
		return unmodifiableView;
	}

	@Override
	public DataSeries<V> getNewSeries(Iterable<?> values) {
		DataSeries<V> copy = getNewSeries();
		for (Object value : values) {
			copy.append(value);
		}
		return copy;
	}

	@Override
	public DataSeries<V> getNewSeries() {
		return (DataSeries<V>) getNewSeries(getType());
	}

	@Override
	public DataValue<V> getNewDataValue() {
		return (DataValue<V>) getNewDataValue(getType());
	}

	@Override
	public boolean isNumeric() {
		Class<?> type = getType();
		if (type == null) return false;
		return Number.class.isAssignableFrom(getType());
	}

//	private DataValue<?> op(SeriesOp op) {
//		if (dataValueOp == null || !dataValueOp.containsKey(op)) {
//			if (dataValueOp == null) dataValueOp = new EnumMap<>(SeriesOp.class);
//			if (op.realOutput) {
//				if (getType().equals(Float.class)) {
//					dataValueOp.put(op, new CalcValue.DoubleValue.SeriesFunc(this, op));
//				}
//				if (getType().equals(Double.class)) {
//					dataValueOp.put(op, new CalcValue.DoubleValue.SeriesFunc(this, op));
//				}
//				if (getType().equals(Integer.class)) {
//					dataValueOp.put(op, new CalcValue.DoubleValue.SeriesFunc(this, op));
//				}
//				if (getType().equals(Long.class)) {
//					dataValueOp.put(op, new CalcValue.DoubleValue.SeriesFunc(this, op));
//				}
//			}
//			else {
//				if (getType().equals(Float.class)) {
//					dataValueOp.put(op, new CalcValue.FloatValue.SeriesFunc(this, op));
//				}
//				if (getType().equals(Double.class)) {
//					dataValueOp.put(op, new CalcValue.DoubleValue.SeriesFunc(this, op));
//				}
//				if (getType().equals(Integer.class)) {
//					dataValueOp.put(op, new CalcValue.IntValue.SeriesFunc(this, op));
//				}
//				if (getType().equals(Long.class)) {
//					dataValueOp.put(op, new CalcValue.LongValue.SeriesFunc(this, op));
//				}
//			}
//		}
//		return dataValueOp.get(op);
//	}
	private DataValue<?> op(SeriesOp op) {
		if (dataValueOp == null || !dataValueOp.containsKey(op)) {
			if (dataValueOp == null) dataValueOp = new EnumMap<>(SeriesOp.class);
			if (op.realOutput || getType().equals(Double.class)) {
				dataValueOp.put(op, new CalcValue.DoubleValue.SeriesFunc(this, op));
			}
			else if (getType().equals(Float.class)) {
				dataValueOp.put(op, new CalcValue.FloatValue.SeriesFunc(this, op));
			}
			else if (getType().equals(Integer.class)) {
				dataValueOp.put(op, new CalcValue.IntValue.SeriesFunc(this, op));
			}
			else if (getType().equals(Long.class)) {
				dataValueOp.put(op, new CalcValue.LongValue.SeriesFunc(this, op));
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
	public void appendAllValues(V... values) {
		for (V v : values) {
			appendValue(v);
		}
	}
	
	@Override
	public void appendAll(Object... values) {
		for (Object v : values) {
			append(v);
		}
	}
	
	@Override
	public SeriesView<V> append(DataSeries<V> otherSeries) {
		return new SeriesViewAppend<V>(this, otherSeries);
	}

	@Override
	public SeriesView<V> select(int... indices) {
		return new SeriesViewRow<>(this, indices);
	}

	@Override
	public SeriesView<V> sort() {
		return new SortedSeries<V>(this);
	}

	@Override
	public SeriesView<V> sort(Comparator<V> comparator) {
		return new SortedSeries<V>(this, comparator);
	}

	@Override
	public DataMap<V, SeriesView<V>> group() {
		return new GroupedSeries<V, V>(this);
	}

	@Override
	public <K> DataMap<K, SeriesView<V>> group(Function<V, K> keyFuntion) {
		return new GroupedSeries<K, V>(this, keyFuntion);
	}

	@Override
	public <O> SeriesView<O> apply(final Function<V, O> function) {
		final DataSeries<V> me = this;
		
		// Determine function output type. This is used to create the cache series in CalcSeries.
		Class<?> outputType = (new TypeToken<O>(getClass()) {}).getRawType();
		// If the type doesn't appear to have been provided via generics (and if the length is 0),
		// then get the type from an example.
		if (length() > 0 && (outputType == null || outputType.equals(Object.class))) {
			outputType = function.apply(get(0)).getClass();
		}
		final Class<?> outputTypeFinal = outputType;
		
		// See if we can determine the (numeric) input type for the function,
		// in which case we can use a CalcSeries sub-class that calls the method
		// accepting the relevant primitive type.
		Class<?> type = getType();
		if (type != null) {
			if (type.equals(Float.class)) {
				return new CalcSeries<V, O>(me) {
					@Override
					public O calc(int index) {
						return function.apply(me.getFloat(index));
					}
					@Override
					public DataSeries<O> getNewSeries() {
						return (DataSeries<O>) getNewSeries(outputTypeFinal);
					}
				};
			}
			if (type.equals(Double.class)) {
				return new CalcSeries<V, O>(me) {
					@Override
					public O calc(int index) {
						return function.apply(me.getDouble(index));
					}
					@Override
					public DataSeries<O> getNewSeries() {
						return (DataSeries<O>) getNewSeries(outputTypeFinal);
					}
				};
			}
			if (type.equals(Integer.class)) {
				return new CalcSeries<V, O>(me) {
					@Override
					public O calc(int index) {
						return function.apply(me.getInt(index));
					}
					@Override
					public DataSeries<O> getNewSeries() {
						return (DataSeries<O>) getNewSeries(outputTypeFinal);
					}
				};
			}
			if (type.equals(Long.class)) {
				return new CalcSeries<V, O>(me) {
					@Override
					public O calc(int index) {
						return function.apply(me.getLong(index));
					}
					@Override
					public DataSeries<O> getNewSeries() {
						return (DataSeries<O>) getNewSeries(outputTypeFinal);
					}
				};
			}
			if (type.equals(String.class)) {
				return new CalcSeries<V, O>(me) {
					@Override
					public O calc(int index) {
						return function.apply((String) me.get(index));
					}
					@Override
					public DataSeries<O> getNewSeries() {
						return (DataSeries<O>) getNewSeries(outputTypeFinal);
					}
				};
			}
			if (type.equals(Date.class)) {
				return new CalcSeries<V, O>(me) {
					@Override
					public O calc(int index) {
						return function.apply((Date) me.get(index));
					}
					@Override
					public DataSeries<O> getNewSeries() {
						return (DataSeries<O>) getNewSeries(outputTypeFinal);
					}
				};
			}
			if (type.equals(TemporalAccessor.class)) {
				return new CalcSeries<V, O>(me) {
					@Override
					public O calc(int index) {
						return function.apply((TemporalAccessor) me.get(index));
					}
					@Override
					public DataSeries<O> getNewSeries() {
						return (DataSeries<O>) getNewSeries(outputTypeFinal);
					}
				};
			}
		}
		// Fallback to creating a CalcSeries that uses the 
		// (possibly typed) object-based apply method.
		return new CalcSeries<V, O>(me) {
			@Override
			public O calc(int index) {
				return function.apply(me.get(index));
			}
			@Override
			public DataSeries<O> getNewSeries() {
				return (DataSeries<O>) getNewSeries(outputTypeFinal);
			}
		};
	}

	@Override
	public SeriesView<?> applyMathMethod(String mathMethod) {
		return new CalcSeries.Maths<V>(mathMethod, this);
	}

	private SeriesView<?> op(Op op, Number value) {
		Class<?> envelopeClass = envelopeClassForOp(op, value);
		
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
		Class<?> envelopeClass = envelopeClassForOp(op, value);
		
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
		
		Class<?> envelopeClass = envelopeClassForOp(op, series);
		
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

	private Class<?> envelopeClassForOp(Op op, Object arg) {
		Class<?> type = getType();
		// Only check for numeric type if we can determine the type or if the series is not empty.
		if ((type != null || length() > 0) && !isNumeric()) {
			throw new RuntimeException("Can not perform " + op.toString().toLowerCase() + " operation on non-numeric DataSeries (type is " + type + ").");
		}
		if (type == null) {
			System.err.println("Warning: cannot determine type of source series for " + op.toString().toLowerCase() + " operation, assuming real (double) numbers.");
		}
		
		Class<Number> numericType = null;
		if (arg instanceof Number) {
			numericType = (Class<Number>) arg.getClass();
		}
		else if (arg instanceof DataValue) {
			numericType = (Class<Number>) ((DataValue<?>) arg).getType();
		}
		else { //series
			DataSeries<?> s = (DataSeries<?>) arg;
			Class<?> sType = s.getType();
			if ((sType != null || s.length() > 0) && !s.isNumeric()) {
				throw new RuntimeException("Can not perform " + op.toString().toLowerCase() + " operation with provided non-numeric DataSeries (type is " + sType + ").");
			}
			
			if (sType == null) {
				System.err.println("Warning: cannot determine type of series provided to " + op.toString().toLowerCase() + " operation, assuming real (double) numbers.");
			}
			else {
				numericType = (Class<Number>) sType;
			}
		}
		
		return (op.realOutput || type == null || numericType == null) ? Double.class : Util.getEnvelopeNumberType((Class<Number>) type, numericType, true);
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

	/**
	 *  Utility function to cast the given value to the numeric type stored by this series.
	 */
	protected V castToNumericType(Object v) {
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
		lock();
		try {
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
		finally {
			unlock();
		}
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
	
	
	private Map<String, DataSeries<Double>> rangeViews = null;
	@Override
	public DataSeries<Double> toUnitRange() {
		return toRange(0, 1);
	}
	@Override
	public DataSeries<Double> toRange(double min, double max) {
		return toRange(new DataValueDouble(min), new DataValueDouble(max));
	}
	@Override
	public DataSeries<Double> toRange(DataValue<?> min, DataValue<?> max) {
		if (!isNumeric()) {
			throw new UnsupportedOperationException("Cannot perform Range operation on non-numeric DataSeries containing " + getType().getSimpleName());
		}
		String key = min.getDouble() + ":" + max.getDouble();
		DataSeries<Double> rangeView = null;
		if (rangeViews == null || !rangeViews.containsKey(key)) {
			if (rangeViews == null) {
				rangeViews = new HashMap<>();
			}
			
			DataValue<?> originalRange = max().subtract(min());
			if (min.getDouble() == 0 && max.getDouble() == 1) {
				rangeView = (DataSeries<Double>) subtract(min()).divide(originalRange);
			}
			else {
				DataValue<?> specRange = max.subtract(min);
				rangeView = (DataSeries<Double>) (((subtract(min())).multiply(specRange)).divide(originalRange)).add(min);
			}
		}
		else {
			rangeView = rangeViews.get(key);
		}
		return rangeView;
	}

	
	class DataValueView extends AbstractDataValue<V> implements DataListener {
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
			if (!Util.equalsIncData(currentValue, AbstractDataSeries.this.get(index))) {
				currentValue = AbstractDataSeries.this.get(index);
				this.fireChangeEvent(new DataEvent(this, event));
			}
		}
	}
	

	class FloatSeriesView extends AbstractSeriesView<Float> implements DataSeries.FloatSeries {
		@Override public int length() {	return AbstractDataSeries.this.length(); }
		@Override public Float get(int index) { return AbstractDataSeries.this.getFloat(index); }
		@Override public float getFloat(int index) { return AbstractDataSeries.this.getFloat(index); }
		@Override public void update(DataEvent cause) {}
	}
	
	class DoubleSeriesView extends AbstractSeriesView<Double> implements DataSeries.DoubleSeries {
		@Override public int length() {	return AbstractDataSeries.this.length(); }
		@Override public Double get(int index) { return AbstractDataSeries.this.getDouble(index); }
		@Override public double getDouble(int index) { return AbstractDataSeries.this.getDouble(index); }
		@Override public void update(DataEvent cause) {}
	}
	
	class IntSeriesView extends AbstractSeriesView<Integer> implements DataSeries.IntSeries {
		@Override public int length() {	return AbstractDataSeries.this.length(); }
		@Override public Integer get(int index) { return AbstractDataSeries.this.getInt(index); }
		@Override public int getInt(int index) { return AbstractDataSeries.this.getInt(index); }
		@Override public void update(DataEvent cause) {}
	}
	
	class LongSeriesView extends AbstractSeriesView<Long> implements DataSeries.LongSeries {
		@Override public int length() {	return AbstractDataSeries.this.length(); }
		@Override public Long get(int index) { return AbstractDataSeries.this.getLong(index); }
		@Override public long getLong(int index) { return AbstractDataSeries.this.getLong(index); }
		@Override public void update(DataEvent cause) {}
	}
	

	class Lst extends AbstractList<V> {
		@Override
		public V get(int arg0) {
			return AbstractDataSeries.this.get(arg0);
		}

		@Override
		public int size() {
			return length();
		}
		
	}
	
	
	
	public static void main(String[] args) {
		DataSeries<?> s = HV.randomUniformSeries(5, 1, 10);
		System.out.println(s);
		DataSeries<?> r = s.toRange(0, 5);
		System.out.println(r);
		
	}
}