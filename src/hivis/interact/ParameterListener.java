package hivis.interact;

/**
 * The interface that must be supported by classes that wish to receive notification of changes to a {@link Parametrised} object.
 * 
 * @author O. J. Coleman
 */
public interface ParameterListener {
	/**
	 * Receives notification of a Parameters change event.
	 */
	public void parametersChanged(ParameterEvent event);
}
