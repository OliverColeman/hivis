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

import hivis.data.Data;
import hivis.data.DataEvent;
import hivis.data.DataListener;
import hivis.data.DataMap;
import hivis.data.DataMapDefault;
import hivis.data.DataSeries;


/**
 * Base class for creating {@link DataMap}s that are optionally calculated from
 * another Data source. If input data is provided then change events on the data
 * trigger a recalculation of this map. Subclasses must implement {@link #update())}.
 * 
 * @author O. J. Coleman
 */
public abstract class CalcMap<K, V, D extends Data> extends AbstractMapView<K, V, D> {
	boolean recalc = true;

	/**
	 * The cached map. Subclasses should update this in
	 * {@link #update()} (and generally never outside of that method).
	 */
	protected DataMap<K, V> cache;

	/**
	 * Create a DataMap view of the given input data.
	 */
	public CalcMap(D input) {
		super(input);
	}

	/**
	 * Create a DataMap view.
	 */
	public CalcMap() {
	}

	protected void setupCache() {
		cache = new DataMapDefault<>();
		// Collect change events that originate as a result of modifying the
		// cache.
		// This way we can detect when the cache values are actually changed.
		final CalcMap<K, V, D> me = this;
		cache.addChangeListener(new DataListener() {
			@Override
			public void dataChanged(DataEvent event) {
				for (Object changeType : event.getTypes()) {
					me.setDataChanged(changeType);
				}
			}
		});
	}
	
	/**
	 * Internal use only. 
	 * Indicates that the view needs to be updated. Typically this will involve
	 * updating the values in the {@link #cache}.
	 */
	public void update(DataEvent cause) {
		recalc = false;
		if (cache == null) {
			setupCache();
		}
		// The cache has a change listener attached to it that will call
		// dataChanged on this CalcMap if the data in it (the cache) actually 
		// changes, so we use beginChanges and finishChanges to avoid firing 
		// multiple change events.
		this.beginChanges(this);
		update();
		this.finishChanges(this);
	}
	
	/**
	 * Update the values in the {@link #cache}.
	 */
	public abstract void update();

	@Override
	public V get(K key) {
		if (recalc) update(null);
		return cache.get(key);
	}

	@Override
	public boolean containsKey(K key) {
		if (recalc) update(null);
		return cache.containsKey(key);
	}

	@Override
	public DataSeries<K> keys() {
		if (recalc) update(null);
		return cache.keys();
	}

	@Override
	public DataSeries<V> values() {
		if (recalc) update(null);
		return cache.values();
	}
	
	public boolean shouldChangeEventsBeForwarded() {
		// Don't forward change events from input data, 
		// as we forward (accurate) change events from the cache.
		return false;
	}
}
