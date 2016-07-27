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

package hivis.common;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Stores data about events that occur for a set of data. 
 * 
 * @author O. J. Coleman
 */
public class Event<O, T, S extends Event<?, ?, ?>> {
	/**
	 * The object affected by the change.
	 */
	public final O affected;
	
	/**
	 * The underlying event, if applicable.
	 */
	public final S sourceEvent;
	
	protected Set<T> types;
	
	public Event(O affected, S sourceEvent, T... types) {
		this.affected = affected;
		this.sourceEvent = sourceEvent;
		this.types = Collections.unmodifiableSet(new HashSet<T>(Arrays.asList(types)));
	}

	public Event(O affected, T... types) {
		this(affected, null, types);
	}

	/**
	 * Returns true iff this event represents the given type. Note that an event may represent multiple types.
	 * An event type is typically a dataset-type-specific enum.
	 */
	public boolean isType(Object type) {
		return types.contains(type);
	}
	
	/**
	 * Get the list of change types represented by this event.
	 * @return
	 */
	public Set<T> getTypes() {
		return types;
	}
}
