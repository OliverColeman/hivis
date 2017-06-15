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
import java.util.Set;
import java.util.Collections;
import java.util.HashMap;

import com.google.common.reflect.TypeToken;

import hivis.data.DataValue;

/**
 * Provides a flexible way of specifying the parameters for an operation.
 * The parameters are stored as {@link DataValue}s. If the parameter values are given as
 * DataValues then this is stored directly, otherwise the given value is wrapped as a
 * DataValue. Parameters cannot be changed via {@link #set(String, DataValue)} or {@link #set(String, Object) 
 * once they have been set (this is to allow caching the result of operations based on a configuration). 
 * However a parameter value set via {@link #set(String, DataValue)}
 * may be changed by changing the value represented by the passed DataValue, assuming that it
 * supports being modified (this can be supported because changes to a DataValue can be monitored
 * and the results of a cached operation updated accordingly). 
 * 
 * @author O. J. Coleman
 */
public class Config implements Cloneable {
	private Map<String, DataValue<?>> values = new HashMap<>();
	
	
	public <V> Config set(String label, V value) {
		if (values.containsKey(label)) {
			throw new ParameterAlreadySetException("The parameter \"" + label + "\" has already been set.");
		}
		values.put(label, HV.newValue(value).immutableCopy());
		return this;
	}
	
	public <V> Config set(String label, DataValue<V> value) {
		if (values.containsKey(label)) {
			throw new ParameterAlreadySetException("The parameter \"" + label + "\" has already been set.");
		}
		values.put(label, value);
		return this;
	}
	
	public boolean hasParameter(String label) {
		return values.containsKey(label);
	}
	
	
	public Set<String> getParameterLabels() {
		return Collections.unmodifiableSet(values.keySet());
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
	public <T> T get(String label, T defaultValue) {
		if (hasParameter(label)) {
			return (T) get(label);
		}
		return defaultValue;
	}
	
	private boolean getBoolean(String label) {
		return (Boolean) get(label); 
	}
	private boolean getBoolean(String label, boolean defaultValue) {
		if (!hasParameter(label)) {
			return defaultValue;
		}
		return getBoolean(label); 
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
	
	
	public DataValue<?> getDataValue(String label) {
		if (!hasParameter(label)) {
			throw new MissingParameterException("You must specify the parameter " + label);
		}
		return values.get(label);
	}
	public DataValue<?> getDataValue(String label, DataValue<?> defaultValue) {
		if (hasParameter(label)) {
			return values.get(label);
		}
		return defaultValue;
	}
	
	public DataValue<?> getNumericDataValue(String label) {
		DataValue<?> dv = getDataValue(label);
		if (!dv.isNumeric()) {
			throw new WrongParameterTypeException("The parameter " + label + " must be of type Number.");
		}
		return dv;
	}
	public DataValue<?> getNumericDataValue(String label, DataValue<?> defaultValue) {
		if (hasParameter(label)) {
			return getNumericDataValue(label);
		}
		return defaultValue;
	}
	

	/**
	 * Returns true iff the passed object is a Config object which represents
	 * the same mappings as this Config object (according to the contract
	 * defined by java.util.Map.equals(Object).
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Config) {
			return this.values.equals(((Config) o).values);
		}
		return false;
	}

	/**
	 * Returns a hashCode consistent with that of the contract defined by
	 * java.util.Map.equals(Object).
	 */
	@Override
	public int hashCode() {
		return values.hashCode();
	}
	
	
	public class ParameterAlreadySetException extends RuntimeException {
		public ParameterAlreadySetException(String msg) {
			super(msg);
		}
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
