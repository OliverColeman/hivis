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
