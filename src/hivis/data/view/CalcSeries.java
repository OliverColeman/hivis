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

package hivis.data.view;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

import com.google.common.primitives.Primitives;

import hivis.data.DataEvent;
import hivis.data.DataListener;
import hivis.data.DataSeries;
import hivis.data.DataSeriesInteger;
import hivis.data.DataSeriesLong;
import hivis.data.DataValue;
import hivis.data.DataSeriesDouble;
import hivis.data.DataSeriesFloat;

/**
 * Base class for creating {@link DataSeries} that are calculated from zero, one
 * or more other DataSeries. If input series are provided then change events on
 * those series are forwarded to this series. The values in the series are
 * cached. Cached values are lazily (re)calculated on the first call to
 * {@link #get(int)}, {@link #getBoolean(int)} etc, or after the input
 * DataSeries have changed (all values in the cache are recalculated at once).
 * 
 * @author O. J. Coleman
 */
public abstract class CalcSeries<I, O> extends SeriesViewFunction<I, O> {
	boolean recalc = true;
	/**
	 * The cache of values. This is lazily calculated on the first call to
	 * {@link #get(int)}, {@link #getBoolean(int)} etc.
	 */
	protected DataSeries<O> cache;
	

	/**
	 * Create a DataSeries function of the given input series, with length equal
	 * to the (first) input series.
	 */
	public CalcSeries(DataSeries<I>... input) {
		super(input);
		setupCache();
	}

	/**
	 * Create a DataSeries function of the given input value and series, with length equal
	 * to the (first) input series.
	 */
	public CalcSeries(DataValue<?> dv, DataSeries<I>... input) {
		super(dv, input);
		setupCache();
	}

	/**
	 * Create a DataSeries function with the given length.
	 */
	public CalcSeries(int length) {
		super(length);
		setupCache();
	}
	
	
	protected void setupCache() {
		cache = getNewSeries();
	
		// Collect change events that originate as a result of modifying the cache.
		// This way we can detect when the cache values are actually changed.
		final CalcSeries<I, O> me = this;
		cache.addChangeListener(new DataListener() {
			@Override
			public void dataChanged(DataEvent event) {
				for (Object changeType : event.getTypes()) {
					me.setDataChanged(changeType);
				}
			}
		});
	}


	/**
	 * Update the values in {@link #cache}. The default implementation calls
	 * {@link #calc(int)} for every value in the series. Sub-classes may override
	 * this to provide a more efficient implementation, for example avoiding
	 * autoboxing if a primitive type is stored.
	 */
	public void updateView(Object cause) {
		if (cache == null) {
			setupCache();
		}
		this.beginChanges(this);
		// Make sure cache series is the right length.
		int length = length();
		cache.resize(length);
		for (int i = 0; i < length; i++) {
			cache.setValue(i, calc(i));
		}
		this.finishChanges(this);
	}

	
	/**
	 * This is the method where you implement the function. Implementations must calculate and return the value that a call to
	 * {@link DataSeries#get(int)} should return (based on {@link #inputSeries} if applicable). 
	 * The value returned will be cached and returned by calls to {@link #get}.
	 */
	public abstract O calc(int index);

	/**
	 * Primitive equivalent to {@link #calc(int)}. This allows for more
	 * efficient implementations of {@link #updateView(Object)} if a primitive type
	 * is stored. This implementation throws an UnsupportedOperationException.
	 */
	public boolean calcBoolean(int index) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Primitive equivalent to {@link #calc(int)}. This allows for more
	 * efficient implementations of {@link #updateView(Object)} if a primitive type
	 * is stored. This implementation throws an UnsupportedOperationException.
	 */
	public int calcInteger(int index) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Primitive equivalent to {@link #calc(int)}. This allows for more
	 * efficient implementations of {@link #updateView(Object)} if a primitive type
	 * is stored. This implementation throws an UnsupportedOperationException.
	 */
	public long calcLong(int index) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Primitive equivalent to {@link #calc(int)}. This allows for more
	 * efficient implementations of {@link #updateView(Object)} if a primitive type
	 * is stored. This implementation throws an UnsupportedOperationException.
	 */
	public float calcFloat(int index) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Primitive equivalent to {@link #calc(int)}. This allows for more
	 * efficient implementations of {@link #updateView(Object)} if a primitive type
	 * is stored. This implementation throws an UnsupportedOperationException.
	 */
	public double calcDouble(int index) {
		throw new UnsupportedOperationException();
	}

	
	
	@Override
	public void dataChanged(DataEvent event) {
		if (inputSeries.contains(event.affected)) {
			recalc = true;
		}
		super.dataChanged(event);
	}
	

	@Override
	public final O get(int index) {
		if (recalc) {
			updateView(null);
			recalc = false;
		}
		return cache.get(index);
	}

	@Override
	public final boolean getBoolean(int index) {
		if (recalc) {
			updateView(null);
			recalc = false;
		}
		return cache.getBoolean(index);
	}

	@Override
	public final int getInt(int index) {
		if (recalc) {
			updateView(null);
			recalc = false;
		}
		return cache.getInt(index);
	}

	@Override
	public final long getLong(int index) {
		if (recalc) {
			updateView(null);
			recalc = false;
		}
		return cache.getLong(index);
	}
	
	@Override
	public final float getFloat(int index) {
		if (recalc) {
			updateView(null);
			recalc = false;
		}
		return cache.getFloat(index);
	}

	@Override
	public final double getDouble(int index) {
		if (recalc) {
			updateView(null);
			recalc = false;
		}
		return cache.getDouble(index);
	}
	
	
	
	
	public static class FloatSeries<I extends Object> extends CalcSeries<I, Float> {
		public FloatSeries(int length) {
			super(length);
		}
		public FloatSeries(DataValue<?> dv, DataSeries<I>... input) {
			super(dv, input);
		}
		public FloatSeries(DataSeries<I>... input) {
			super(input);
		}
		
		@Override
		public void updateView(Object cause) {
			cache.resize(length());
			for (int i = 0; i < length(); i++) {
				cache.setValue(i, calcFloat(i));
			}
		}
		
		@Override 
		public DataSeries<Float> getNewSeries() {
			return new DataSeriesFloat();
		}

		@Override
		public Float calc(int index) {
			return calcFloat(index);
		}
		
		@Override
		public float calcFloat(int index) {
			throw new RuntimeException("Implementations of CalcSeries.Float must override calcFloat(int).");
		}
		
		
		public static class FuncValue<I> extends FloatSeries<I> {
			protected final Op op;
			protected final float value;
			public FuncValue(Op op, DataSeries<I> series, DataValue<I> dv) {
				super(dv, series);
				this.op = op;
				this.value = 0;
			}
			public FuncValue(Op op, DataSeries<I> series, float value) {
				super(series);
				this.op = op;
				this.value = value;
			}
			@Override
			public void updateView(Object cause) {
				cache.resize(length());
				DataSeries<I> series = getInputSeries(0);
				float v = inputValue != null ? inputValue.getFloat() : this.value;
				switch (op) {
				case ADD:
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, series.getFloat(i) + v);
					}
					return; 
				case SUBTRACT:
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, series.getFloat(i) - v);
					}
					return; 
				case MULTIPLY:
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, series.getFloat(i) * v);
					}
					return; 
				case DIVIDE:
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, series.getFloat(i) / v);
					}
					return; 
				}
				throw new UnsupportedOperationException(op + " is not supported by " + this.getClass().getCanonicalName());
			}
		}
		
		public static class FuncSeries<I> extends FloatSeries<I> {
			protected final Op op;
			public FuncSeries(Op op, DataSeries<I> series1, DataSeries<I> series2) {
				super(series1, series2);
				this.op = op;
			}
			@Override
			public void updateView(Object cause) {
				cache.resize(length());
				DataSeries<I> series1 = getInputSeries(0);
				DataSeries<I> series2 = getInputSeries(1);
				switch (op) {
				case ADD:
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, series1.getFloat(i) + series2.getFloat(i));
					}
					return; 
				case SUBTRACT:
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, series1.getFloat(i) - series2.getFloat(i));
					}
					return; 
				case MULTIPLY:
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, series1.getFloat(i) * series2.getFloat(i));
					}
					return; 
				case DIVIDE:
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, series1.getFloat(i) / series2.getFloat(i));
					}
					return; 
				}
				throw new UnsupportedOperationException(op + " is not supported by " + this.getClass().getCanonicalName());
			}
		}
	}
	
	
	
	
	public static class DoubleSeries<I extends Object> extends CalcSeries<I, Double> {
		public DoubleSeries(int length) {
			super(length);
		}
		public DoubleSeries(DataValue<?> dv, DataSeries<I>... input) {
			super(dv, input);
		}
		public DoubleSeries(DataSeries<I>... input) {
			super(input);
		}
		
		@Override
		public void updateView(Object cause) {
			cache.resize(length());
			for (int i = 0; i < length(); i++) {
				cache.setValue(i, calcDouble(i));
			}
		}
		
		@Override 
		public DataSeries<Double> getNewSeries() {
			return new DataSeriesDouble();
		}

		@Override
		public Double calc(int index) {
			return calcDouble(index);
		}
		
		@Override
		public double calcDouble(int index) {
			throw new RuntimeException("Implementations of CalcSeries.Real must override calcDouble(int).");
		}
		
		
		public static class FuncValue<I> extends DoubleSeries<I> {
			protected final Op op;
			protected final double value;
			public FuncValue(Op op, DataSeries<I> series, DataValue<I> dv) {
				super(dv, series);
				this.op = op;
				this.value = 0;
			}
			public FuncValue(Op op, DataSeries<I> series, double value) {
				super(series);
				this.op = op;
				this.value = value;
			}
			@Override
			public void updateView(Object cause) {
				cache.resize(length());
				DataSeries<I> series = getInputSeries(0);
				double v = inputValue != null ? inputValue.getDouble() : this.value;
				switch (op) {
				case ADD:
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, series.getDouble(i) + v);
					}
					return; 
				case SUBTRACT:
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, series.getDouble(i) - v);
					}
					return; 
				case MULTIPLY:
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, series.getDouble(i) * v);
					}
					return; 
				case DIVIDE:
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, series.getDouble(i) / v);
					}
					return; 
				}
				throw new UnsupportedOperationException(op + " is not supported by " + this.getClass().getCanonicalName());
			}
		}
		
		public static class FuncSeries<I> extends DoubleSeries<I> {
			protected final Op op;
			public FuncSeries(Op op, DataSeries<I> series1, DataSeries<I> series2) {
				super(series1, series2);
				this.op = op;
			}
			@Override
			public void updateView(Object cause) {
				cache.resize(length());
				DataSeries<I> series1 = getInputSeries(0);
				DataSeries<I> series2 = getInputSeries(1);
				switch (op) {
				case ADD:
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, series1.getDouble(i) + series2.getDouble(i));
					}
					return; 
				case SUBTRACT:
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, series1.getDouble(i) - series2.getDouble(i));
					}
					return; 
				case MULTIPLY:
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, series1.getDouble(i) * series2.getDouble(i));
					}
					return; 
				case DIVIDE:
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, series1.getDouble(i) / series2.getDouble(i));
					}
					return; 
				}
				throw new UnsupportedOperationException(op + " is not supported by " + this.getClass().getCanonicalName());
			}
		}
	}
	
	
	

	public static class IntSeries<I extends Object> extends CalcSeries<I, Integer> {
		public IntSeries(int length) {
			super(length);
		}
		public IntSeries(DataValue<?> dv, DataSeries<I>... input) {
			super(dv, input);
		}
		public IntSeries(DataSeries<I>... input) {
			super(input);
		}
		
		@Override
		public void updateView(Object cause) {
			cache.resize(length());
			for (int i = 0; i < length(); i++) {
				cache.setValue(i, calcInteger(i));
			}
		}
		
		@Override 
		public DataSeries<Integer> getNewSeries() {
			return new DataSeriesInteger();
		}

		@Override
		public Integer calc(int index) {
			return calcInteger(index);
		}
		
		@Override
		public int calcInteger(int index) {
			throw new RuntimeException("Implementations of CalcSeries.Int must override calcInteger(int).");
		}
		
		
		public static class FuncValue<I> extends IntSeries<I> {
			protected final Op op;
			protected final int value;
			public FuncValue(Op op, DataSeries<I> series, DataValue<I> dv) {
				super(dv, series);
				this.op = op;
				this.value = 0;
			}
			public FuncValue(Op op, DataSeries<I> series, int value) {
				super(series);
				this.op = op;
				this.value = value;
			}
			@Override
			public void updateView(Object cause) {
				cache.resize(length());
				DataSeries<I> series = getInputSeries(0);
				int v = inputValue != null ? inputValue.getInt() : this.value;
				switch (op) {
				case ADD:
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, series.getInt(i) + v);
					}
					return; 
				case SUBTRACT:
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, series.getInt(i) - v);
					}
					return; 
				case MULTIPLY:
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, series.getInt(i) * v);
					}
					return; 
				case DIVIDE:
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, series.getInt(i) / v);
					}
					return; 
				}
				throw new UnsupportedOperationException(op + " is not supported by " + this.getClass().getCanonicalName());
			}
		}
		
		public static class FuncSeries<I> extends IntSeries<I> {
			protected final Op op;
			public FuncSeries(Op op, DataSeries<I> series1, DataSeries<I> series2) {
				super(series1, series2);
				this.op = op;
			}
			@Override
			public void updateView(Object cause) {
				cache.resize(length());
				DataSeries<I> series1 = getInputSeries(0);
				DataSeries<I> series2 = getInputSeries(1);
				switch (op) {
				case ADD:
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, series1.getInt(i) + series2.getInt(i));
					}
					return; 
				case SUBTRACT:
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, series1.getInt(i) - series2.getInt(i));
					}
					return; 
				case MULTIPLY:
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, series1.getInt(i) * series2.getInt(i));
					}
					return; 
				case DIVIDE:
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, series1.getInt(i) / series2.getInt(i));
					}
					return; 
				}
				throw new UnsupportedOperationException(op + " is not supported by " + this.getClass().getCanonicalName());
			}
		}
	}
	
	
	
	public static class LongSeries<I extends Object> extends CalcSeries<I, Long> {
		public LongSeries(int length) {
			super(length);
		}
		public LongSeries(DataValue<?> dv, DataSeries<I>... input) {
			super(dv, input);
		}
		public LongSeries(DataSeries<I>... input) {
			super(input);
		}
		
		@Override
		public void updateView(Object cause) {
			cache.resize(length());
			for (int i = 0; i < length(); i++) {
				cache.setValue(i, calcLong(i));
			}
		}
		
		@Override 
		public DataSeries<Long> getNewSeries() {
			return new DataSeriesLong();
		}

		@Override
		public Long calc(int index) {
			return calcLong(index);
		}
		
		@Override
		public long calcLong(int index) {
			throw new RuntimeException("Implementations of CalcSeries.Long must override calcLong(long).");
		}
		

		public static class FuncValue<I> extends LongSeries<I> {
			protected final Op op;
			protected final long value;
			public FuncValue(Op op, DataSeries<I> series, DataValue<I> dv) {
				super(dv, series);
				this.op = op;
				this.value = 0;
			}
			public FuncValue(Op op, DataSeries<I> series, long value) {
				super(series);
				this.op = op;
				this.value = value;
			}
			@Override
			public void updateView(Object cause) {
				cache.resize(length());
				DataSeries<I> series = getInputSeries(0);
				long v = inputValue != null ? inputValue.getLong() : this.value;
				switch (op) {
				case ADD:
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, series.getLong(i) + v);
					}
					return; 
				case SUBTRACT:
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, series.getLong(i) - v);
					}
					return; 
				case MULTIPLY:
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, series.getLong(i) * v);
					}
					return; 
				case DIVIDE:
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, series.getLong(i) / v);
					}
					return; 
				}
				throw new UnsupportedOperationException(op + " is not supported by " + this.getClass().getCanonicalName());
			}
		}
		
		public static class FuncSeries<I> extends LongSeries<I> {
			protected final Op op;
			public FuncSeries(Op op, DataSeries<I> series1, DataSeries<I> series2) {
				super(series1, series2);
				this.op = op;
			}
			@Override
			public void updateView(Object cause) {
				cache.resize(length());
				DataSeries<I> series1 = getInputSeries(0);
				DataSeries<I> series2 = getInputSeries(1);
				switch (op) {
				case ADD:
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, series1.getLong(i) + series2.getLong(i));
					}
					return; 
				case SUBTRACT:
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, series1.getLong(i) - series2.getLong(i));
					}
					return; 
				case MULTIPLY:
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, series1.getLong(i) * series2.getLong(i));
					}
					return; 
				case DIVIDE:
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, series1.getLong(i) / series2.getLong(i));
					}
					return; 
				}
				throw new UnsupportedOperationException(op + " is not supported by " + this.getClass().getCanonicalName());
			}
		}
	}
	
	public enum Op {
		ADD(false), 
		SUBTRACT(false), 
		MULTIPLY(false), 
		DIVIDE(true);
		
		Op(boolean ro) {
			realOutput = ro;
		}
		
		/**
		 * Indicates that the output of this operation should be assumed to be a real value.
		 */
		public final boolean realOutput;
	}
	
	
	public static class Maths<I> extends CalcSeries<I, Object> {
		protected Method method;
		
		public Maths(String func, DataSeries<I> input) {
			super (input);
			
			Class<?> primType = Primitives.unwrap(input.getType());
			
			try {
				method = Math.class.getMethod(func, primType);
				method.setAccessible(true);
			} catch (NoSuchMethodException | SecurityException e) {
				if (primType.equals(int.class) || primType.equals(long.class) || primType.equals(float.class)) {
					try {
						method = Math.class.getMethod(func, double.class);
						method.setAccessible(true);
					} catch (NoSuchMethodException | SecurityException e2) {
						throw new IllegalArgumentException("Could not find the specified Math method, " + func + ", accepting argument type " + input.getType().getSimpleName() + " or double.", e2);
					}
				}
				else {
					throw new IllegalArgumentException("Could not find the specified Math method, " + func + ", accepting argument type " + input.getType().getSimpleName() + ".", e);
				}
			}
		}
		
		@Override
		public Class<?> getType() {
			return method == null ? null : method.getReturnType();
		}
		
		public void updateView(Object cause) {
			if (cache == null) {
				setupCache();
			}
			this.beginChanges(this);
			cache.resize(length());
			DataSeries<I> series = getInputSeries(0);
			
			try {
				if (method.getParameterTypes()[0].equals(float.class)) {
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, method.invoke(null, series.getFloat(i)));
					}
				}
				else if (method.getParameterTypes()[0].equals(double.class)) {
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, method.invoke(null, series.getDouble(i)));
					}
				}
				else if (method.getParameterTypes()[0].equals(int.class)) {
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, method.invoke(null, series.getInt(i)));
					}
				}
				else if (method.getParameterTypes()[0].equals(long.class)) {
					for (int i = 0; i < length(); i++) {
						cache.setValue(i, method.invoke(null, series.getLong(i)));
					}
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException("Error calling " + method, e);
			}
		}
		
		@Override 
		protected void setupCache() {
			// Create cache if we know the type to create at this point.
			if (getType() != null) {
				cache = getNewSeries();
			
				// Collect change events that originate as a result of modifying the cache.
				// This way we can detect when the cache values are actually changed.
				final Maths<I> me = this;
				cache.addChangeListener(new DataListener() {
					@Override
					public void dataChanged(DataEvent event) {
						for (Object changeType : event.getTypes()) {
							me.setDataChanged(changeType);
						}
					}
				});
			}
		}
	
		@Override
		public Object calc(int index) {
			return null;
		}
	}
}
