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
package hivis.common;

import java.util.Map;

import com.google.common.reflect.TypeToken;

import hivis.data.DataValue;

/**
 * Provides a flexible, user-friendly way of specifying the parameters for an operation.
 * The parameters are stored as {@link DataValue}s. If the parameter values are given as
 * DataValues then this is stored directly, otherwise the given value is wrapped as a
 * DataValue.
 * 
 * @author O. J. Coleman
 */
public class Config implements Cloneable {
	private Map<String, DataValue<?>> values;
	
	
	public <V> Config set(String label, V value) {
		values.put(label, HV.newValue(value));
		return this;
	}
	
	public <V> Config set(String label, DataValue<V> value) {
		values.put(label, value);
		return this;
	}
	
	public Config unset(String label) {
		values.remove(label);
		return this;
	}
	
	public boolean hasParameter(String label) {
		return values.containsKey(label);
	}
	
	
	@SuppressWarnings("unchecked")
	public <V> V get(String label) {
		if (!hasParameter(label)) {
			throw new MissingParameterException("You must specify the parameter " + label);
		}
		try {
			return (V) values.get(label).get();
		}
		catch (ClassCastException e) {
			@SuppressWarnings("serial")
			TypeToken<V> typeToken = new TypeToken<V>(getClass()) {};
			throw new WrongParameterTypeException("The parameter " + label + " must be of type " + typeToken.getRawType().getSimpleName());
		}
	}
	
	@SuppressWarnings("unchecked")
	public <V> V get(String label, Class<V> type) {
		return get(label);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(String label, T defaultValue) {
		if (hasParameter(label)) {
			return (T) get(label, defaultValue.getClass());
		}
		return defaultValue;
	}
	
	
	private Number getNumber(String label) {
		return get(label); 
	}
	private Number getNumber(String label, Number defaultValue) {
		if (!hasParameter(label)) {
			return defaultValue;
		}
		return get(label); 
	}
	
	public int getInt(String label) {
		return getNumber(label).intValue();
	}
	public int getInt(String label, int defaultValue) {
		return getNumber(label, defaultValue).intValue();
	}
	
	public long getLong(String label) {
		return getNumber(label).longValue();
	}
	public long getLong(String label, long defaultValue) {
		return getNumber(label, defaultValue).longValue();
	}
	
	public float getFloat(String label) {
		return getNumber(label).floatValue();
	}
	public float getFloat(String label, float defaultValue) {
		return getNumber(label, defaultValue).floatValue();
	}
	
	public double getDouble(String label) {
		return getNumber(label).doubleValue();
	}
	public double getDouble(String label, double defaultValue) {
		return getNumber(label, defaultValue).doubleValue();
	}
	
	
	public class MissingParameterException extends RuntimeException {
		public MissingParameterException(String msg) {
			super(msg);
		}
	}
	
	public class WrongParameterTypeException extends RuntimeException {
		public WrongParameterTypeException(String msg) {
			super(msg);
		}
	}
}
