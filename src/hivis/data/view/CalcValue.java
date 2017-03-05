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


import hivis.data.DataEvent;
import hivis.data.DataSeries;
import hivis.data.DataValue;
import hivis.data.DataValueInteger;
import hivis.data.DataValueLong;
import hivis.data.DataValueDouble;
import hivis.data.DataValueFloat;

/**
 * Base class for creating {@link DataValue}s that are calculated from zero, one
 * or more other DataValues. If input values are provided then change events on
 * those values are forwarded to this DataValue. The calculated value is
 * cached. Cached values are lazily (re)calculated on the first call to
 * {@link #get()}, {@link #getBoolean()} etc.
 * 
 * @author O. J. Coleman
 */
public abstract class CalcValue<I, O> extends AbstractValueView<I, O> {
	boolean recalc = true;
	
	/**
	 * The cached value. This is lazily calculated on the first call to
	 * {@link #get()}, {@link #getBoolean()} etc.
	 */
	protected DataValue<O> cache;
	
	
	/**
	 * Create a DataValue function of the given input values.
	 */
	public CalcValue(DataValue<I>... input) {
		super(input);
		cache = getNewDataValue();
	}
	
	
	/**
	 * Create a DataValue function of the given input series.
	 */
	public CalcValue(DataSeries<I>... input) {
		super(input);
		cache = getNewDataValue();
	}
	
	
	/**
	 * Update the {@link #cache} value. The default implementation calls
	 * {@link #calc()}. Sub-classes may override
	 * this to provide a more efficient implementation, for example avoiding
	 * autoboxing if a primitive type is stored.
	 */
	public void updateView(Object cause) {
		cache.setValue(calc());
	}
	
	
	/**
	 * This is the method where you implement the function. Implementations must calculate and return the value that a call to
	 * {@link DataValue#get()} should return (based on the {@link #inputValues} if applicable). 
	 * The value returned will be cached and returned by calls to {@link #get()}.
	 */
	public abstract O calc();
	
	/**
	 * Primitive equivalent to {@link #calc()}. This allows for more
	 * efficient implementations of {@link #updateView(Object)} if a primitive type
	 * is stored. This implementation throws an UnsupportedOperationException.
	 */
	public boolean calcBoolean() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Primitive equivalent to {@link #calc()}. This allows for more
	 * efficient implementations of {@link #updateView(Object)} if a primitive type
	 * is stored. This implementation throws an UnsupportedOperationException.
	 */
	public int calcInteger() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Primitive equivalent to {@link #calc()}. This allows for more
	 * efficient implementations of {@link #updateView(Object)} if a primitive type
	 * is stored. This implementation throws an UnsupportedOperationException.
	 */
	public long calcLong() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Primitive equivalent to {@link #calc()}. This allows for more
	 * efficient implementations of {@link #updateView(Object)} if a primitive type
	 * is stored. This implementation throws an UnsupportedOperationException.
	 */
	public float calcFloat() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Primitive equivalent to {@link #calc()}. This allows for more
	 * efficient implementations of {@link #updateView(Object)} if a primitive type
	 * is stored. This implementation throws an UnsupportedOperationException.
	 */
	public double calcDouble() {
		throw new UnsupportedOperationException();
	}

	
	
	@Override
	public void dataChanged(DataEvent event) {
		if (inputValues != null && inputValues.contains(event.affected) ||
				inputSeries != null && inputSeries.contains(event.affected)) {
			recalc = true;
		}
		super.dataChanged(event);
	}
	

	@Override
	public final O get() {
		if (recalc) {
			updateView(null);
			recalc = false;
		}
		return cache.get();
	}

	@Override
	public final boolean getBoolean() {
		if (recalc) {
			updateView(null);
			recalc = false;
		}
		return cache.getBoolean();
	}

	@Override
	public final int getInt() {
		if (recalc) {
			updateView(null);
			recalc = false;
		}
		return cache.getInt();
	}

	@Override
	public final long getLong() {
		if (recalc) {
			updateView(null);
			recalc = false;
		}
		return cache.getLong();
	}
	
	@Override
	public final float getFloat() {
		if (recalc) {
			updateView(null);
			recalc = false;
		}
		return cache.getFloat();
	}

	@Override
	public final double getDouble() {
		if (recalc) {
			updateView(null);
			recalc = false;
		}
		return cache.getDouble();
	}
	
	
	
	
	public static class FloatValue<I> extends CalcValue<I, Float> {
		public FloatValue(DataValue<I>... input) {
			super(input);
		}
		
		public FloatValue(DataSeries<I>... input) {
			super(input);
		}
		
		@Override
		public void updateView(Object cause) {
			cache.setValue(calcFloat());
		}
		
		@Override 
		public DataValue<Float> getNewDataValue() {
			return new DataValueFloat();
		}

		@Override
		public Float calc() {
			return calcFloat();
		}
		
		@Override
		public float calcFloat() {
			throw new RuntimeException("Implementations of CalcValue.FloatValue must override calcFloat().");
		}
		
		
		public static class FuncDV<I> extends FloatValue<I> {
			protected final Op op;
			public FuncDV(Op op, DataValue<I> dv1, DataValue<I> dv2) {
				super(dv1, dv2);
				this.op = op;
			}
			public float calcFloat() {
				DataValue dv1 = getInputValue(0);
				DataValue dv2 = getInputValue(1);
				switch (op) {
				case ADD: return dv1.getFloat() + dv2.getFloat(); 
				case SUBTRACT: return dv1.getFloat() - dv2.getFloat(); 
				case MULTIPLY: return dv1.getFloat() * dv2.getFloat(); 
				case DIVIDE: return dv1.getFloat() / dv2.getFloat(); 
				}
				throw new UnsupportedOperationException(op + " is not supported by " + this.getClass().getCanonicalName());
			}
		}
		
		public static class FuncRaw<I> extends FloatValue<I> {
			protected final Op op;
			protected final float value;
			public FuncRaw(Op op, DataValue<I> dv1, float value) {
				super(dv1);
				this.op = op;
				this.value = value;
			}
			public float calcFloat() {
				DataValue dv1 = getInputValue(0);
				switch (op) {
				case ADD: return dv1.getFloat() + value; 
				case SUBTRACT: return dv1.getFloat() - value; 
				case MULTIPLY: return dv1.getFloat() * value; 
				case DIVIDE: return dv1.getFloat() / value; 
				}
				throw new UnsupportedOperationException(op + " is not supported by " + this.getClass().getCanonicalName());
			}
		}
		
		public static class SeriesFunc extends FloatValue {
			SeriesOp op;
			public SeriesFunc(DataSeries series, SeriesOp op) {
				super(series);
				this.op = op;
			}
			public float calcFloat() {
				DataSeries s = getInputSeries(0);
				int len = s.length();
				if (len == 0 && op.undefinedForEmpty) return Float.NaN;
				float val = s.getFloat(0);
				switch (op) {
				case MIN:
					for (int i = 1; i < len; i++) {
						if (val > s.getFloat(i)) val = s.getFloat(i);
					}
					return val;
				case MAX:
					for (int i = 1; i < len; i++) {
						if (val < s.getFloat(i)) val = s.getFloat(i);
					}
					return val;
				case SUM:
				case MEAN:
					for (int i = 1; i < len; i++) {
						val += s.getFloat(i);
					}
					return op == SeriesOp.MEAN ? val / len : val;
				case PRODUCT:
					for (int i = 1; i < len; i++) {
						val *= s.getFloat(i);
					}
					return val;
				}
				throw new UnsupportedOperationException(op + " is not supported by " + this.getClass().getCanonicalName());
			}
		}
	}
	
	
	
	public static class DoubleValue<I extends Object> extends CalcValue<I, Double> {
		public DoubleValue(DataValue<I>... input) {
			super(input);
		}
		
		public DoubleValue(DataSeries<I>... input) {
			super(input);
		}
		
		@Override
		public void updateView(Object cause) {
			cache.setValue(calcDouble());
		}
		
		@Override 
		public DataValue<Double> getNewDataValue() {
			return new DataValueDouble();
		}

		@Override
		public Double calc() {
			return calcDouble();
		}
		
		@Override
		public double calcDouble() {
			throw new RuntimeException("Implementations of CalcValue.DoubleValue must override calcDouble().");
		}
		
		
		public static class FuncDV<I> extends DoubleValue<I> {
			protected final Op op;
			public FuncDV(Op op, DataValue<I> dv1, DataValue<I> dv2) {
				super(dv1, dv2);
				this.op = op;
			}
			public double calcDouble() {
				DataValue dv1 = getInputValue(0);
				DataValue dv2 = getInputValue(1);
				switch (op) {
					case ADD: return dv1.getDouble() + dv2.getDouble(); 
					case SUBTRACT: return dv1.getDouble() - dv2.getDouble(); 
					case MULTIPLY: return dv1.getDouble() * dv2.getDouble(); 
					case DIVIDE: return dv1.getDouble() / dv2.getDouble(); 
				}
				throw new UnsupportedOperationException(op + " is not supported by " + this.getClass().getCanonicalName());
			}
		}
		
		public static class FuncRaw<I> extends DoubleValue<I> {
			protected final Op op;
			protected final double value;
			public FuncRaw(Op op, DataValue<I> dv1, double value) {
				super(dv1);
				this.op = op;
				this.value = value;
			}
			public double calcDouble() {
				DataValue dv1 = getInputValue(0);
				switch (op) {
					case ADD: return dv1.getDouble() + value; 
					case SUBTRACT: return dv1.getDouble() - value; 
					case MULTIPLY: return dv1.getDouble() * value; 
					case DIVIDE: return dv1.getDouble() / value; 
				}
				throw new UnsupportedOperationException(op + " is not supported by " + this.getClass().getCanonicalName());
			}
		}
		
		public static class SeriesFunc extends DoubleValue {
			SeriesOp op;
			public SeriesFunc(DataSeries series, SeriesOp op) {
				super(series);
				this.op = op;
			}
			public double calcDouble() {
				DataSeries s = getInputSeries(0);
				int len = s.length();
				if (len == 0 && op.undefinedForEmpty) return Double.NaN;
				double val = s.getDouble(0);
				switch (op) {
				case MIN:
					for (int i = 1; i < len; i++) {
						if (val > s.getDouble(i)) val = s.getDouble(i);
					}
					return val;
				case MAX:
					for (int i = 1; i < len; i++) {
						if (val < s.getDouble(i)) val = s.getDouble(i);
					}
					return val;
				case SUM:
					for (int i = 1; i < len; i++) {
						val += s.getDouble(i);
					}
					return val;
				case PRODUCT:
					for (int i = 1; i < len; i++) {
						val *= s.getDouble(i);
					}
					return val;
				case MEAN:
					// If s is derived from AbstractDataSeries (very likely), then we'll be reusing the same CalcValue object.
					// This means that if s.sum() is called elsewhere we won't recalculate it unnecessarily. 
					return s.sum().getDouble() / len;
				case VARIANCE:
					// If s is derived from AbstractDataSeries (very likely), then we'll be reusing the same CalcValue object.
					// This means that if s.mean() is called elsewhere we won't recalculate it unnecessarily.
					double mean = s.mean().getDouble();
					val = 0;
					for (int i = 0; i < len; i++) {
						double v = s.getDouble(i) - mean;
						val += v * v;
					}
					return val / len;
				case STD_DEV:
					// If s is derived from AbstractDataSeries (very likely), then we'll be reusing the same CalcValue object.
					// This means that if s.variance() is called elsewhere we won't recalculate it unnecessarily. 
					return Math.sqrt(s.variance().getDouble());
				}
				throw new UnsupportedOperationException(op + " is not supported by " + this.getClass().getCanonicalName());
			}
		}
	}
	
	
	

	public static class IntValue<I extends Object> extends CalcValue<I, Integer> {
		public IntValue(DataValue<I>... input) {
			super(input);
		}
				
		public IntValue(DataSeries<I>... input) {
			super(input);
		}
		
		@Override
		public void updateView(Object cause) {
			cache.setValue(calcInteger());
		}
		
		@Override 
		public DataValue<Integer> getNewDataValue() {
			return new DataValueInteger();
		}

		@Override
		public Integer calc() {
			return calcInteger();
		}
		
		@Override
		public int calcInteger() {
			throw new RuntimeException("Implementations of CalcValue.Integer must override calcInteger().");
		}
		

		public static class FuncDV<I> extends IntValue<I> {
			protected final Op op;
			public FuncDV(Op op, DataValue<I> dv1, DataValue<I> dv2) {
				super(dv1, dv2);
				this.op = op;
			}
			public int calcInteger() {
				DataValue dv1 = getInputValue(0);
				DataValue dv2 = getInputValue(1);
				switch (op) {
				case ADD: return dv1.getInt() + dv2.getInt(); 
				case SUBTRACT: return dv1.getInt() - dv2.getInt(); 
				case MULTIPLY: return dv1.getInt() * dv2.getInt(); 
				case DIVIDE: return dv1.getInt() / dv2.getInt(); 
				}
				throw new UnsupportedOperationException(op + " is not supported by " + this.getClass().getCanonicalName());
			}
		}
		
		public static class FuncRaw<I> extends IntValue<I> {
			protected final Op op;
			protected final int value;
			public FuncRaw(Op op, DataValue<I> dv1, int value) {
				super(dv1);
				this.op = op;
				this.value = value;
			}
			public int calcInteger() {
				DataValue dv1 = getInputValue(0);
				switch (op) {
				case ADD: return dv1.getInt() + value; 
				case SUBTRACT: return dv1.getInt() - value; 
				case MULTIPLY: return dv1.getInt() * value; 
				case DIVIDE: return dv1.getInt() / value; 
				}
				throw new UnsupportedOperationException(op + " is not supported by " + this.getClass().getCanonicalName());
			}
		}
		
		public static class SeriesFunc extends IntValue {
			SeriesOp op;
			public SeriesFunc(DataSeries series, SeriesOp op) {
				super(series);
				this.op = op;
			}
			public int calcInteger() {
				DataSeries s = getInputSeries(0);
				int len = s.length();
				if (len == 0 && op.undefinedForEmpty) return Integer.MIN_VALUE;
				int val = s.getInt(0);
				switch (op) {
				case MIN:
					for (int i = 1; i < len; i++) {
						if (val > s.getInt(i)) val = s.getInt(i);
					}
					return val;
				case MAX:
					for (int i = 1; i < len; i++) {
						if (val < s.getInt(i)) val = s.getInt(i);
					}
					return val;
				case SUM:
				case MEAN:
					for (int i = 1; i < len; i++) {
						val += s.getInt(i);
					}
					return op == SeriesOp.MEAN ? val / len : val;
				case PRODUCT:
					for (int i = 1; i < len; i++) {
						val *= s.getInt(i);
					}
					return val;
				}
				throw new UnsupportedOperationException(op + " is not supported by " + this.getClass().getCanonicalName());
			}
		}
	}
	
	
	
	public static class LongValue<I extends Object> extends CalcValue<I, Long> {
		public LongValue(DataValue<I>... input) {
			super(input);
		}
		
		public LongValue(DataSeries<I>... input) {
			super(input);
		}
		
		@Override
		public void updateView(Object cause) {
			cache.setValue(calcLong());
		}
		
		@Override 
		public DataValue<Long> getNewDataValue() {
			return new DataValueLong();
		}

		@Override
		public Long calc() {
			return calcLong();
		}
		
		@Override
		public long calcLong() {
			throw new RuntimeException("Implementations of CalcValue.LongValue must override calcLong().");
		}
		

		public static class FuncDV<I> extends LongValue<I> {
			protected final Op op;
			public FuncDV(Op op, DataValue<I> dv1, DataValue<I> dv2) {
				super(dv1, dv2);
				this.op = op;
			}
			public long calcLong() {
				DataValue dv1 = getInputValue(0);
				DataValue dv2 = getInputValue(1);
				switch (op) {
				case ADD: return dv1.getLong() + dv2.getLong(); 
				case SUBTRACT: return dv1.getLong() - dv2.getLong(); 
				case MULTIPLY: return dv1.getLong() * dv2.getLong(); 
				case DIVIDE: return dv1.getLong() / dv2.getLong(); 
				}
				throw new UnsupportedOperationException(op + " is not supported by " + this.getClass().getCanonicalName());
			}
		}
		
		public static class FuncRaw<I> extends LongValue<I> {
			protected final Op op;
			protected final long value;
			public FuncRaw(Op op, DataValue<I> dv1, long value) {
				super(dv1);
				this.op = op;
				this.value = value;
			}
			public long calcLong() {
				DataValue dv1 = getInputValue(0);
				switch (op) {
				case ADD: return dv1.getLong() + value; 
				case SUBTRACT: return dv1.getLong() - value; 
				case MULTIPLY: return dv1.getLong() * value; 
				case DIVIDE: return dv1.getLong() / value; 
				}
				throw new UnsupportedOperationException(op + " is not supported by " + this.getClass().getCanonicalName());
			}
		}
		
		public static class SeriesFunc extends LongValue {
			SeriesOp op;
			public SeriesFunc(DataSeries series, SeriesOp op) {
				super(series);
				this.op = op;
			}
			public long calcLong() {
				DataSeries s = getInputSeries(0);
				long len = s.length();
				if (len == 0 && op.undefinedForEmpty) return Long.MIN_VALUE;
				long val = s.getLong(0);
				switch (op) {
				case MIN:
					for (int i = 1; i < len; i++) {
						if (val > s.getLong(i)) val = s.getLong(i);
					}
					return val;
				case MAX:
					for (int i = 1; i < len; i++) {
						if (val < s.getLong(i)) val = s.getLong(i);
					}
					return val;
				case SUM:
				case MEAN:
					for (int i = 1; i < len; i++) {
						val += s.getLong(i);
					}
					return op == SeriesOp.MEAN ? val / len : val;
				case PRODUCT:
					for (int i = 1; i < len; i++) {
						val *= s.getLong(i);
					}
					return val;
				}
				throw new UnsupportedOperationException(op + " is not supported by " + this.getClass().getCanonicalName());
			}
		}
	}
	
	public enum Op {
		ADD, SUBTRACT, MULTIPLY, DIVIDE
	}
	
	public enum SeriesOp {
		MIN(true, false), 
		MAX(true, false), 
		SUM(false, false), 
		PRODUCT(true, false), 
		MEAN(true, true), 
		VARIANCE(true, true),
		STD_DEV(true, true);
		
		SeriesOp(boolean ufe, boolean ro) {
			undefinedForEmpty = ufe;
			realOutput = ro;
		}
		
		public final boolean undefinedForEmpty;
		public final boolean realOutput;
	}
}
