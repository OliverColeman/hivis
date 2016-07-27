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

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.google.common.reflect.TypeToken;

import hivis.data.view.CalcSeries;
import hivis.data.view.Function;
import hivis.data.view.SFunc;
import hivis.data.view.SeriesFunction;
import hivis.data.view.ViewSeriesAppend;

/**
 * Default base implementation of {@link DataSeries}.
 * 
 * @author O. J. Coleman
 */
public abstract class AbstractDataSeries<V> extends DataSetDefault implements DataSeries<V> {
	private TypeToken<V> typeToken = new TypeToken<V>(getClass()) {};
	private Class<?> type = typeToken.getRawType();
	
	
	public AbstractDataSeries() {
		super();
	}
	
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
		return type;
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
	 * @see hivis.data.DataSeries#getBoolean(int)
	 */
	@Override
	public int getInt(int index) {
		return ((Number) get(index)).intValue();
	}

	/**
	 * Get the value stored at the given index as a long.
	 * This default implementation casts the value given by {@link #get(int)}.
	 * Sub-classes may override this to improve efficiency.
	 * @see hivis.data.DataSeries#getBoolean(int)
	 */
	@Override
	public long getLong(int index) {
		return ((Number) get(index)).longValue();
	}

	/**
	 * Get the value stored at the given index as a double.
	 * This default implementation casts the value given by {@link #get(int)}.
	 * Sub-classes may override this to improve efficiency.
	 * @see hivis.data.DataSeries#getBoolean(int)
	 */
	@Override
	public double getDouble(int index) {
		return ((Number) get(index)).doubleValue();
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
	 * @see hivis.data.DataSeries#asArray(V[])
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
		if (type.equals(Double.class) || type.equals(double.class)) {
			return (DataSeries<V>) new DataSeriesReal();
		}
		if (type.equals(Integer.class) || type.equals(int.class)) {
			return (DataSeries<V>) new DataSeriesInteger();
		}
		throw new UnsupportedOperationException("Don't know how to create a DataSeries containing type " + type);
	}
	
	
	@Override
	public boolean isNumeric() {
		return Number.class.isAssignableFrom(getType());
	}
	
	
	@Override
	public <O> DataSeries<O> apply(final Function<V, O> function) {
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
	public DataSeries<V> append(DataSeries<V> otherSeries) {
		return new ViewSeriesAppend<V>(this, otherSeries);
	}
	
	
	@Override
	public DataSeries<V> add(final V value) {
		if (getType().equals(Double.class)) {
			return (DataSeries<V>) new CalcSeries.Real.Add(this, (Double) castToNumericType(value));
		}
		if (getType().equals(Integer.class)) {
			return (DataSeries<V>) new CalcSeries.Int.Add(this, (Integer) castToNumericType(value));
		}
		throw new UnsupportedOperationException();
	}
	
	@Override
	public DataSeries<V> add(double value) {
		return add(castToType(value));
	}
	
	@Override
	public DataSeries<V> add(long value) {
		return add(castToType(value));
	}
	
	@Override
	public DataSeries<V> add(final DataSeries<?> series) {
		if (this.length() != series.length()) {
			throw new IllegalArgumentException("Can not add two DataSeries with differing lengths.");
		}
		if (getType().equals(Double.class)) {
			return (DataSeries<V>) new CalcSeries.Real.AddSeries(this, series);
		}
		if (getType().equals(Integer.class)) {
			return (DataSeries<V>) new CalcSeries.Int.AddSeries(this, series);
		}
		throw new UnsupportedOperationException();
	}
	

	@Override
	public DataSeries<V> subtract(V value) {
		if (getType().equals(Double.class)) {
			return (DataSeries<V>) new CalcSeries.Real.Subtract(this, (Double) castToNumericType(value));
		}
		if (getType().equals(Integer.class)) {
			return (DataSeries<V>) new CalcSeries.Int.Subtract(this, (Integer) castToNumericType(value));
		}
		throw new UnsupportedOperationException();
	}
	
	@Override
	public DataSeries<V> subtract(double value) {
		return subtract(castToType(value));
	}
	
	@Override
	public DataSeries<V> subtract(long value) {
		return subtract(castToType(value));
	}
	
	@Override
	public DataSeries<V> subtract(DataSeries<?> series) {
		if (this.length() != series.length()) {
			throw new IllegalArgumentException("Can not subtract two DataSeries with differing lengths.");
		}
		if (getType().equals(Double.class)) {
			return (DataSeries<V>) new CalcSeries.Real.SubtractSeries(this, series);
		}
		if (getType().equals(Integer.class)) {
			return (DataSeries<V>) new CalcSeries.Int.SubtractSeries(this, series);
		}
		throw new UnsupportedOperationException();
	}
	
	
	@Override
	public DataSeries<V> multiply(V value) {
		if (getType().equals(Double.class)) {
			return (DataSeries<V>) new CalcSeries.Real.Multiply(this, (Double) castToNumericType(value));
		}
		if (getType().equals(Integer.class)) {
			return (DataSeries<V>) new CalcSeries.Int.Multiply(this, (Integer) castToNumericType(value));
		}
		throw new UnsupportedOperationException();
	}
	
	@Override
	public DataSeries<V> multiply(double value) {
		return multiply(castToType(value));
	}
	
	@Override
	public DataSeries<V> multiply(long value) {
		return multiply(castToType(value));
	}
	
	@Override
	public DataSeries<V> multiply(DataSeries<?> series) {
		if (this.length() != series.length()) {
			throw new IllegalArgumentException("Can not multiply two DataSeries with differing lengths.");
		}
		if (getType().equals(Double.class)) {
			return (DataSeries<V>) new CalcSeries.Real.MultiplySeries(this, series);
		}
		if (getType().equals(Integer.class)) {
			return (DataSeries<V>) new CalcSeries.Int.MultiplySeries(this, series);
		}
		throw new UnsupportedOperationException();
	}
	
	
	@Override
	public DataSeries<V> divide(V value) {
		if (getType().equals(Double.class)) {
			return (DataSeries<V>) new CalcSeries.Real.Divide(this, (Double) castToNumericType(value));
		}
		if (getType().equals(Integer.class)) {
			return (DataSeries<V>) new CalcSeries.Int.Divide(this, (Integer) castToNumericType(value));
		}
		throw new UnsupportedOperationException();
	}
	
	@Override
	public DataSeries<V> divide(double value) {
		return divide(castToType(value));
	}
	
	@Override
	public DataSeries<V> divide(long value) {
		return divide(castToType(value));
	}

	@Override
	public DataSeries<V> divide(DataSeries<?> series) {
		if (this.length() != series.length()) {
			throw new IllegalArgumentException("Can not divide two DataSeries with differing lengths.");
		}
		if (getType().equals(Double.class)) {
			return (DataSeries<V>) new CalcSeries.Real.DivideSeries(this, series);
		}
		if (getType().equals(Integer.class)) {
			return (DataSeries<V>) new CalcSeries.Int.DivideSeries(this, series);
		}
		throw new UnsupportedOperationException();
	}
	
	
	private V castToType(double v) {
		if (getType().equals(Float.class)) {
			return (V) ((Float) ((float) v));
		}
		if (getType().equals(BigDecimal.class)) {
			return (V) new BigDecimal(v);
		}
		return (V) ((Double) v);
	}
	private V castToType(long v) {
		if (getType().equals(Float.class)) {
			return (V) ((Float) ((float) v));
		}
		if (getType().equals(Double.class)) {
			return (V) ((Double) ((double) v));
		}
		if (getType().equals(Integer.class)) {
			return (V) ((Integer) ((int) v));
		}
		if (getType().equals(Short.class)) {
			return (V) ((Short) ((short) v));
		}
		if (getType().equals(Byte.class)) {
			return (V) ((Byte) ((byte) v));
		}
		return (V) ((Long) v);
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
		
		// We're storing an integer getType(). Make sure we're passed an integer value.
		if (v instanceof Float || v instanceof Double) {
			if (n.doubleValue() != (int) n.doubleValue()) {
				throw new IllegalArgumentException("Series stores integer numbers but argument does not represent an integer: " + v);
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
}

