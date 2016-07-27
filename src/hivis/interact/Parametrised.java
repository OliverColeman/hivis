package hivis.interact;

import hivis.common.ListSet;

/**
 * Interface to an object or process that may be controlled via a set of 
 * {@link Parameter}s.  Note that it is up 
 * to the implementation of Parameter and Parametrised to handle communication
 * between these. The typical pattern is to create an inner class for the 
 * Parametrised object that extends Parameter.
 * 
 * @author O. J. Coleman
 */
public interface Parametrised {
	/**
	 * Get the set of parameter labels. This should usually be an unmodifiable view.
	 */
	public ListSet<String> getParameterLabels();
	
	/**
	 * Get the set of parameters. This should usually be an unmodifiable view.
	 */
	public ListSet<Parameter<?>> getParameters();
	
	/**
	 * Get the parameter with the specified label.
	 */
	public Parameter<?> getParameter(String label);
	
	/**
	 * Add the given ParametersChangeListener to this Parametrised object. The
	 * listener will receive {@link ParameterListener#parametersChanged(ParametersChangeEvent)}
	 * notifications when a Parameters value changes or Parameters are added or removed.
	 */
	public void addParameterChangeListener(ParameterListener listener);
	
	/**
	 * Remove the given ParametersChangeListener from this Parametrised object.
	 */
	public void removeParameterChangeListener(ParameterListener listener);
}
