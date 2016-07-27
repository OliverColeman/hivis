/**
 * 
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
