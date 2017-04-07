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

import hivis.data.DataMap;
import hivis.data.AbstractDataMap;
import hivis.data.Data;
import hivis.data.DataDefault;
import hivis.data.DataEvent;
import hivis.data.DataListener;

/**
 * Base class for DataMap views, optionally based on another {@link Data} object.
 * 
 * @author O. J. Coleman
 */
public abstract class AbstractMapView<K, V, D extends Data> extends AbstractDataMap<K, V> implements DataListener, View {
	/**
	 * The input Data object, or null if none used.
	 */
	protected final D input;
	
	/**
	 * Create a ViewSeries for the given data.
	 */
	public AbstractMapView(D input) {
		this.input = input;
		input.addChangeListener(this);
	}
	
	/**
	 * Create a ViewSeries.
	 */
	public AbstractMapView() {
		this.input = null;
	}
	
	@Override
	public V put(K key, V value) {
		throw new UnsupportedOperationException("Can not set values in a calculated map.");
	}

	@Override
	public V remove(K key) {
		throw new UnsupportedOperationException("Can not remove values in a calculated map.");
	}

	@Override
	public int size() {
		return keys().length();
	}
	
	/**
	 * Sub-classes may override this to return false if change
	 * events in the input data should not forwarded from this
	 * view.
	 */
	public boolean shouldChangeEventsBeForwarded() {
		return true;
	}

	@Override
	public void dataChanged(DataEvent event) {
		if (input != null && input == event.affected) {
			update(event);
			
			if (shouldChangeEventsBeForwarded()) {
				this.fireChangeEvent(new DataEvent(this, event, event.getTypes().toArray()));
			}
		}
	}
}
