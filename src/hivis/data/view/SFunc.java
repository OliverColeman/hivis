/**
 * 
 */
package hivis.data.view;

import hivis.data.DataSeries;

/**
 * Base class for creating {@link DataSeries} that are calculated from zero, one or more other DataSeries.
 * If input series are provided then change events on those series are forwarded to this series.
 * 
 * @author O. J. Coleman
 *
 */
public abstract class SFunc<I, O> extends ViewSeries<I, O> {
	private int length = Integer.MIN_VALUE;
	
	
	/**
	 * Create a DataSeries function of the given input series, with length equal to the (first) input series.
	 */
	public SFunc(DataSeries<I>... input) {
		super(input);
	}
	
	/**
	 * Create a DataSeries function with the given length.
	 */
	public SFunc(int length) {
		super();
		this.length = length;
	}
	
	
	@Override
	public int length() {
		if (length >= 0) {
			return length;
		}
		if (inputSeries.isEmpty()) {
			throw new RuntimeException("If an SFunc has no input series then either the length() method must be overridden or the length field set to provide the length of the calculated series.");
		}
		return inputSeries.get(0).length();
	}

	@Override
	public O getEmptyValue() {
		return getNewSeries().getEmptyValue();
	}
}

