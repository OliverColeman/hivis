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

import java.util.List;

import hivis.common.HV;
import hivis.data.DataEvent;
import hivis.data.DataListener;
import hivis.data.DataSeries;
import hivis.data.DataSeriesGeneric;
import hivis.data.DataSeriesInteger;
import hivis.data.DataSeriesLong;
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
	 * Create a DataSeries function with the given length.
	 */
	public CalcSeries(int length) {
		super(length);
		setupCache();
	}
	
	
	private void setupCache() {
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
		
		
		protected static class Func extends FloatSeries {
			protected final DataSeries<?> series;
			protected final DataSeries<?> seriesOther;
			protected final float value;
			public Func(DataSeries<?> series, float value) {
				super(series);
				this.series = series;
				this.seriesOther = null;
				this.value = value;
			}
			public Func(DataSeries<?> series, DataSeries<?> seriesOther) {
				super(series, seriesOther);
				this.series = series;
				this.seriesOther = seriesOther;
				this.value = 0;
			}
			public int length() {
				return ((DataSeries<?>) inputSeries.get(0)).length();
			}
		}
		
		public static class Add extends Func {
			public Add(DataSeries<?> series, float value) {
				super(series, value);
			}
			public float calcFloat(int index) {
				return series.getFloat(index) + value;
			}
		}
		public static class AddSeries extends Func {
			public AddSeries(DataSeries<?> series, DataSeries<?> seriesOther) {
				super(series, seriesOther);
			}
			public float calcFloat(int index) {
				return series.getFloat(index) + seriesOther.getFloat(index);
			}
		}
		public static class Subtract extends Func {
			public Subtract(DataSeries<?> series, float value) {
				super(series, value);
			}
			public float calcFloat(int index) {
				return series.getFloat(index) - value;
			}
		}
		public static class SubtractSeries extends Func {
			public SubtractSeries(DataSeries<?> series, DataSeries<?> seriesOther) {
				super(series, seriesOther);
			}
			public float calcFloat(int index) {
				return series.getFloat(index) - seriesOther.getFloat(index);
			}
		}
		public static class Multiply extends Func {
			public Multiply(DataSeries<?> series, float value) {
				super(series, value);
			}
			public float calcFloat(int index) {
				return series.getFloat(index) * value;
			}
		}
		public static class MultiplySeries extends Func {
			public MultiplySeries(DataSeries<?> series, DataSeries<?> seriesOther) {
				super(series, seriesOther);
			}
			public float calcFloat(int index) {
				return series.getFloat(index) * seriesOther.getFloat(index);
			}
		}
		public static class Divide extends Func {
			public Divide(DataSeries<?> series, float value) {
				super(series, value);
			}
			public float calcFloat(int index) {
				return series.getFloat(index) / value;
			}
		}
		public static class DivideSeries extends Func {
			public DivideSeries(DataSeries<?> series, DataSeries<?> seriesOther) {
				super(series, seriesOther);
			}
			public float calcFloat(int index) {
				return series.getFloat(index) / seriesOther.getFloat(index);
			}
		}
	}
	
	
	
	
	public static class DoubleSeries<I extends Object> extends CalcSeries<I, Double> {
		public DoubleSeries(int length) {
			super(length);
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
		
		
		protected static class Func extends DoubleSeries {
			protected final DataSeries<?> series;
			protected final DataSeries<?> seriesOther;
			protected final double value;
			public Func(DataSeries<?> series, double value) {
				super(series);
				this.series = series;
				this.seriesOther = null;
				this.value = value;
			}
			public Func(DataSeries<?> series, DataSeries<?> seriesOther) {
				super(series, seriesOther);
				this.series = series;
				this.seriesOther = seriesOther;
				this.value = 0;
			}
			public int length() {
				return ((DataSeries<?>) inputSeries.get(0)).length();
			}
		}
		
		public static class Add extends Func {
			public Add(DataSeries<?> series, double value) {
				super(series, value);
			}
			public double calcDouble(int index) {
				return series.getDouble(index) + value;
			}
		}
		public static class AddSeries extends Func {
			public AddSeries(DataSeries<?> series, DataSeries<?> seriesOther) {
				super(series, seriesOther);
			}
			public double calcDouble(int index) {
				return series.getDouble(index) + seriesOther.getDouble(index);
			}
		}
		public static class Subtract extends Func {
			public Subtract(DataSeries<?> series, double value) {
				super(series, value);
			}
			public double calcDouble(int index) {
				return series.getDouble(index) - value;
			}
		}
		public static class SubtractSeries extends Func {
			public SubtractSeries(DataSeries<?> series, DataSeries<?> seriesOther) {
				super(series, seriesOther);
			}
			public double calcDouble(int index) {
				return series.getDouble(index) - seriesOther.getDouble(index);
			}
		}
		public static class Multiply extends Func {
			public Multiply(DataSeries<?> series, double value) {
				super(series, value);
			}
			public double calcDouble(int index) {
				return series.getDouble(index) * value;
			}
		}
		public static class MultiplySeries extends Func {
			public MultiplySeries(DataSeries<?> series, DataSeries<?> seriesOther) {
				super(series, seriesOther);
			}
			public double calcDouble(int index) {
				return series.getDouble(index) * seriesOther.getDouble(index);
			}
		}
		public static class Divide extends Func {
			public Divide(DataSeries<?> series, double value) {
				super(series, value);
			}
			public double calcDouble(int index) {
				return series.getDouble(index) / value;
			}
		}
		public static class DivideSeries extends Func {
			public DivideSeries(DataSeries<?> series, DataSeries<?> seriesOther) {
				super(series, seriesOther);
			}
			public double calcDouble(int index) {
				return series.getDouble(index) / seriesOther.getDouble(index);
			}
		}
	}
	
	
	

	public static class IntSeries<I extends Object> extends CalcSeries<I, Integer> {
		public IntSeries(int length) {
			super(length);
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
		
		
		protected static class Func extends IntSeries {
			protected final DataSeries<?> series;
			protected final DataSeries<?> seriesOther;
			protected final int value;
			public Func(DataSeries<?> series, int value) {
				super(series);
				this.series = series;
				this.seriesOther = null;
				this.value = value;
			}
			public Func(DataSeries<?> series, DataSeries<?> seriesOther) {
				super(series, seriesOther);
				this.series = series;
				this.seriesOther = seriesOther;
				this.value = 0;
			}
			public int length() {
				return series.length();
			}
		}
		
		public static class Add extends Func {
			public Add(DataSeries<?> series, int value) {
				super(series, value);
			}
			public int calcInteger(int index) {
				return series.getInt(index) + value;
			}
		}
		public static class AddSeries extends Func {
			public AddSeries(DataSeries<?> series, DataSeries<?> seriesOther) {
				super(series, seriesOther);
			}
			public int calcInteger(int index) {
				return series.getInt(index) + seriesOther.getInt(index);
			}
		}
		public static class Subtract extends Func {
			public Subtract(DataSeries<?> series, int value) {
				super(series, value);
			}
			public int calcInteger(int index) {
				return series.getInt(index) - value;
			}
		}
		public static class SubtractSeries extends Func {
			public SubtractSeries(DataSeries<?> series, DataSeries<?> seriesOther) {
				super(series, seriesOther);
			}
			public int calcInteger(int index) {
				return series.getInt(index) - seriesOther.getInt(index);
			}
		}
		public static class Multiply extends Func {
			public Multiply(DataSeries<?> series, int value) {
				super(series, value);
			}
			public int calcInteger(int index) {
				return series.getInt(index) * value;
			}
		}
		public static class MultiplySeries extends Func {
			public MultiplySeries(DataSeries<?> series, DataSeries<?> seriesOther) {
				super(series, seriesOther);
			}
			public int calcInteger(int index) {
				return series.getInt(index) * seriesOther.getInt(index);
			}
		}
		public static class Divide extends Func {
			public Divide(DataSeries<?> series, int value) {
				super(series, value);
			}
			public int calcInteger(int index) {
				return series.getInt(index) / value;
			}
		}
		public static class DivideSeries extends Func {
			public DivideSeries(DataSeries<?> series, DataSeries<?> seriesOther) {
				super(series, seriesOther);
			}
			public int calcInteger(int index) {
				return series.getInt(index) / seriesOther.getInt(index);
			}
		}
	}
	
	
	
	public static class LongSeries<I extends Object> extends CalcSeries<I, Long> {
		public LongSeries(int length) {
			super(length);
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
		
		
		protected static class Func extends LongSeries {
			protected final DataSeries<?> series;
			protected final DataSeries<?> seriesOther;
			protected final long value;
			public Func(DataSeries<?> series, long value) {
				super(series);
				this.series = series;
				this.seriesOther = null;
				this.value = value;
			}
			public Func(DataSeries<?> series, DataSeries<?> seriesOther) {
				super(series, seriesOther);
				this.series = series;
				this.seriesOther = seriesOther;
				this.value = 0;
			}
			public int length() {
				return series.length();
			}
		}
		
		public static class Add extends Func {
			public Add(DataSeries<?> series, long value) {
				super(series, value);
			}
			public long calcLong(int index) {
				return series.getLong(index) + value;
			}
		}
		public static class AddSeries extends Func {
			public AddSeries(DataSeries<?> series, DataSeries<?> seriesOther) {
				super(series, seriesOther);
			}
			public long calcLong(int index) {
				return series.getLong(index) + seriesOther.getLong(index);
			}
		}
		public static class Subtract extends Func {
			public Subtract(DataSeries<?> series, long value) {
				super(series, value);
			}
			public long calcLong(int index) {
				return series.getLong(index) - value;
			}
		}
		public static class SubtractSeries extends Func {
			public SubtractSeries(DataSeries<?> series, DataSeries<?> seriesOther) {
				super(series, seriesOther);
			}
			public long calcLong(int index) {
				return series.getLong(index) - seriesOther.getLong(index);
			}
		}
		public static class Multiply extends Func {
			public Multiply(DataSeries<?> series, long value) {
				super(series, value);
			}
			public long calcLong(int index) {
				return series.getLong(index) * value;
			}
		}
		public static class MultiplySeries extends Func {
			public MultiplySeries(DataSeries<?> series, DataSeries<?> seriesOther) {
				super(series, seriesOther);
			}
			public long calcLong(int index) {
				return series.getLong(index) * seriesOther.getLong(index);
			}
		}
		public static class Divide extends Func {
			public Divide(DataSeries<?> series, long value) {
				super(series, value);
			}
			public long calcLong(int index) {
				return series.getLong(index) / value;
			}
		}
		public static class DivideSeries extends Func {
			public DivideSeries(DataSeries<?> series, DataSeries<?> seriesOther) {
				super(series, seriesOther);
			}
			public long calcLong(int index) {
				return series.getLong(index) / seriesOther.getLong(index);
			}
		}
	}
}

























