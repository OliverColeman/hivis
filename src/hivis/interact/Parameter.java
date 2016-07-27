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

package hivis.interact;

import java.util.Collections;
import java.util.Set;

import hivis.common.Util;

/**
 * Represents a parameter in a {@link Parametrised} object. Note that it is up 
 * to the implementation of Parameter and Parametrised to handle communication
 * between these. The typical pattern is to create an inner class of the 
 * Parametrised object that implements Parameter.
 * 
 * @author O. J. Coleman
 */
public abstract class Parameter<T> {
	/**
	 * Get the value of this parameter.
	 */
	public abstract T getValue();
	
	/**
	 * Set the value of this parameter.
	 */
	public abstract void setValue(T newValue);
	
	/**
	 * Set the value of this parameter as a boolean.
	 */
	@SuppressWarnings("unchecked")
	public void setBooleanValue(Boolean newValue) {
		setValue((T) newValue);
	}
	
	/**
	 * Set the value of this parameter as an integer.
	 */
	@SuppressWarnings("unchecked")
	public void setIntegerValue(Integer newValue) {
		setValue((T) newValue);
	}
	
	/**
	 * Set the value of this parameter as a double.
	 */
	@SuppressWarnings("unchecked")
	public void setDoubleValue(Double newValue) {
		setValue((T) newValue);
	}
	
	/**
	 * Implementations may override this method to provide the minimum allowable value for this parameter, if applicable.
	 */
	public T getMin() {
		return null;
	}
	
	/**
	 * Implementations may override this method to provide the maximum allowable value for this parameter, if applicable.
	 */
	public T getMax() {
		return null;
	}
	
	/**
	 * Implementations may override this method to provide the set of allowable values for this parameter, if applicable and sensible.
	 */
	public Set<T> getAllowableValues() {
		return Collections.emptySet();
	}
	
	/**
	 * Get the label or name for this parameter.
	 */
	public abstract String getLabel();
	
	/**
	 * Implementations may override this method to provide a description for the parameter. 
	 * Return null if a description is not available.
	 */
	public String getDescription() {
		return null;
	}
	
	/**
	 * Returns true iff this parameter is still valid for the parametrised object that created it. If a call to
	 * {@link Parametrised#getParameters()} would no longer return this parameter then isValid() should return
	 * false. This default implementation always returns true.
	 */
	public boolean isValid() {
		return true;
	}
	
	
	/**
	 * Get the class of the value stored by this Parameter. This implementation calls getClass() on the value returned by getValue().
	 * Subclasses should override this method if getValue() may return null.
	 */
	public Class<?> getType() {
		return getValue().getClass();
	}
	

	@Override
	public int hashCode() {
		return getLabel().hashCode();
	}
	
	/**
	 * Returns true iff the given object is a Parameter with the same label and same value. 
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Parameter)) return false;
		try {
			@SuppressWarnings("unchecked")
			Parameter<T> p = (Parameter<T>) o;
			return getLabel().equals(p.getLabel()) && Util.equalsIncNull(getValue(), p.getValue());
		}
		catch (ClassCastException e) {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "Parameter " + getLabel() + " = " + getValue();
	}
}
