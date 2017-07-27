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
import java.time.temporal.TemporalAccessor;
import java.util.Date;

import com.google.common.reflect.TypeToken;

/**
 * Base class for functions. Sub-classes must implement at least one of the apply methods.
 * 
 * @author O. J. Coleman
 */
public abstract class Function<I, O> {
	public final Class<?> inputType = (new TypeToken<I>(getClass()) {}).getRawType();
	public final Class<?> outputType = (new TypeToken<O>(getClass()) {}).getRawType();
	
	Method method;
	
	public O apply(I input) {
		try {
			// If we've previously determined the method to use.
			if (method != null) {
				return (O) method.invoke(this, input);
			}
			if (input instanceof Float) {
				return apply((Float) input);
			}
			if (input instanceof Double) {
				return apply((Double) input);
			}
			if (input instanceof Integer) {
				return apply((Integer) input);
			}
			if (input instanceof Long) {
				return apply((Long) input);
			}
			// If we weren't given a type at run time (and the input 
			// isn't a type for which we supply a method to override),
			// see if there's a method matching the given input type.
			if (!inputType.equals(Object.class) || !input.getClass().equals(Object.class)) {
				Class<?> type = inputType.equals(Object.class) ? input.getClass() : inputType;
				try {
					method = this.getClass().getMethod("apply", type);
					method.setAccessible(true);
					return (O) method.invoke(this, input);
				} catch (NoSuchMethodException | SecurityException e) {}
			}
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException("Something went terribly wrong: ", e);
		}
		
		throw new IllegalStateException("Please implement the method '" + outputType.getSimpleName() + " apply(" + inputType.getSimpleName() + ")' in your Function.");
	}
	
	public O apply(float input) {
		return apply(input);
	}
	
	public O apply(double input) {
		return apply(input);
	}
	
	public O apply(int input) {
		return apply(input);
	}
	
	public O apply(long input) {
		return apply(input);
	}
	
	public O apply(String input) {
		return apply(input);
	}
	
	public O apply(Date input) {
		return apply(input);
	}
	
	public O apply(TemporalAccessor input) {
		return apply(input);
	}
}
