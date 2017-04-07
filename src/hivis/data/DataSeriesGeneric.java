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

package hivis.data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import hivis.common.Util;
import hivis.data.view.AbstractSeriesView;
import hivis.data.view.SeriesView;


/**
 * Generic data series implementation. If the data is numeric then the specialised 
 * series classes, for example {@link DataSeriesDouble} should be used for efficiency.
 * 
 * @author O. J. Coleman
 */
public class DataSeriesGeneric<V> extends AbstractModifiableDataSeries<V> {
	protected List<V> elements;
	
	/**
	 * Create a new empty DataSeries.
	 */
	public DataSeriesGeneric() {
		super();
		elements = new ArrayList<V>();
    }
	
	/**
	 * Create a new empty DataSeries.
	 */
	public DataSeriesGeneric(V... items) {
		super();
		elements = new ArrayList<V>();
		elements.addAll(Arrays.asList(items));
	}
    
	/**
	 * Create a copy of the given DataSeries.
	 */
    public DataSeriesGeneric(DataSeriesGeneric<V> data) {
    	super();
    	this.elements = new ArrayList<V>(data.elements);
    }
    
    
	@Override
	public int length() {
		return elements.size();
	}
	
	@Override
	public V get(int index) {
		if (index < 0 || index >= elements.size()) {
			return null;
		}
		return elements.get(index);
	}
	
	@Override
	public void setValue(int index, V value) {
		if (!Util.equalsIncNull(elements.get(index), value)) {
			elements.set(index, value);
			this.setDataChanged(DataSeriesChange.ValuesChanged);
		}
	}
	
	@Override
	public void appendValue(V value) {
		elements.add(value);
		this.setDataChanged(DataSeriesChange.ValuesAdded);
	}
	
	@Override
	public void remove(int index) {
		elements.remove(index);
		this.setDataChanged(DataSeriesChange.ValuesRemoved);
	}

	@Override
	public void resize(int newLength) {
		resize(newLength, getEmptyValue());
	}
	
	@Override
	public void resize(int newLength, V padValue) {
		if (newLength < elements.size()) {
			while (newLength < elements.size()) {
				elements.remove(elements.size() - 1);
			}
			this.setDataChanged(DataSeriesChange.ValuesRemoved);
		}
		else if (newLength > elements.size()) {
			while (newLength > elements.size()) {
				elements.add(padValue);
			}
			this.setDataChanged(DataSeriesChange.ValuesAdded);
		}
	}

	@Override
	public DataSeries<V> getNewSeries() {
		return new DataSeriesGeneric<V>();
	}
}
