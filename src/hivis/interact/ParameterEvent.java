/**
 * 
 */
package hivis.interact;

import hivis.common.Event;

/**
 * Contains information about a parameter change to a  {@link Parametrised} object.
 * 
 * @author O. J. Coleman
 */
public class ParameterEvent extends Event<Parametrised, ParameterEvent.Type, Event<?, ?, ?>> {
	public ParameterEvent(Parametrised affected, Event<?, ?, ?> sourceEvent, Type... types) {
		super(affected, sourceEvent, types);
	}
	
	public ParameterEvent(Parametrised affected, Type... valuechanged) {
		super(affected, valuechanged);
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " " + types.toString();
	}
	
	
	public enum Type {
		Added, Removed, Reordered, ValueChanged
	}
}
