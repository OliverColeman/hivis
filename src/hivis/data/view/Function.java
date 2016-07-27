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

import com.google.common.reflect.TypeToken;

/**
 * Base class for functions. Sub-classes must implement at least one of the apply methods.
 * 
 * @author O. J. Coleman
 */
public abstract class Function<I, O> {
	public O apply(I input) {
		if (input instanceof Double) {
			return (O) (Double) apply(((Number) input).doubleValue());
		}
		if (input instanceof Integer) {
			return (O) (Integer) apply(((Number) input).intValue());
		}
		if (input instanceof Double) {
			return (O) (Long) apply(((Number) input).longValue());
		}
		throw new IllegalStateException("Please implement the method '" + outputTypeToken.getRawType().getSimpleName() + " apply(" + input.getClass().getSimpleName() + ").");
	}
	
	public double apply(double input) {
		return ((Number) apply(input)).doubleValue();
	}
	
	public int apply(int input) {
		return ((Number) apply(input)).intValue();
	}
	
	public long apply(long input) {
		return ((Number) apply(input)).longValue();
	}
	
	public final TypeToken<O> outputTypeToken = new TypeToken<O>(getClass()) {};
}
