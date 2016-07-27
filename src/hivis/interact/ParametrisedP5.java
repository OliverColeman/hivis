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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import controlP5.ControlEvent;
import controlP5.ControlListener;
import controlP5.ControlP5;
import controlP5.Controller;
import controlP5.ControllerInterface;
import controlP5.Group;
import controlP5.Toggle;
import processing.core.PApplet;

/**
 * Automatically creates P5 control elements for a {@link Parametrised} object.
 * 
 * @author O. J. Coleman
 */
public class ParametrisedP5 {
	/**
	 * The ControlP5.
	 */
	public final ControlP5 cp5;
	
	/**
	 * The ControlP5 Group. Use this to set the position and other properties of the group of control elements as a whole.
	 */
	public final Group group;
	
	/**
	 * The Parametrised object to control.
	 */
	public final Parametrised controlled;
	
	/**
	 * List of ControlP5 controller elements.
	 */
	protected Map<String, Controller<?>> elements;
	
	
	/**
	 * Create a new set of ControlP5 elements linked to the given {@link Parametrised} object.
	 * @param applet The Processing PApplet to create the ControlP5 elements in.
	 * @param controlled The Parametrised object to create the ControlP5 elements for.
	 * @param name The name to assign to the ControlP5 Group that will contain the control elements.
	 */
	public ParametrisedP5(PApplet applet, Parametrised controlled, String name) {
		cp5 = new ControlP5(applet);
		group = cp5.addGroup(name);
		this.controlled = controlled;
		elements = new HashMap<>();
		
		rebuild();
		
		cp5.addListener(new CP5Listener());
		controlled.addParameterChangeListener(new ParamListener());
	}
	
	
	protected void rebuild() {
		Map<String, Controller<?>> elementsToRemove = new HashMap<>(elements);
		
		int y = 5;
		for (Parameter<?> param : controlled.getParameters()) {
			Class<?> type = param.getType();
			
			if (type.equals(Boolean.class)) {
				Toggle toggle;
				if (elements.containsKey(param.getLabel())) {
					toggle = (Toggle) elements.get(param.getLabel());
				}
				else {
					toggle = cp5.addToggle(param.getLabel()).setGroup(group);
					toggle.setSize(15, 15);
					toggle.setLabel(param.getLabel());
					controlP5.Label l = toggle.getCaptionLabel();
					l.getStyle().marginTop = -toggle.getHeight();
					l.getStyle().marginLeft = toggle.getWidth() + 3;
					elements.put(param.getLabel(), toggle);
				}
				
				toggle.setPosition(5, y);
				toggle.setValue((Boolean) param.getValue());
				
				y += toggle.getHeight() + 5;
			}
			
			elementsToRemove.remove(param.getLabel());
		}
		
		// Remove defunct elements.
		for (Controller<?> c : elementsToRemove.values()) {
			cp5.remove(c);
			group.remove((ControllerInterface<?>) c);
		}
	}
	
	
	protected class CP5Listener implements ControlListener {
		@Override
		public void controlEvent(ControlEvent event) {
			Controller<?> con = event.getController();
			if (con instanceof Toggle) {
				controlled.getParameter(con.getName()).setBooleanValue(((Toggle) con).getState());
			}
		}
	}
	
	
	protected class ParamListener implements ParameterListener {
		@Override
		public void parametersChanged(ParameterEvent event) {
			rebuild();
		}		
	}
}
