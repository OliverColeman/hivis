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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import hivis.data.DataSeries;
import hivis.data.DataSeriesInteger;
import hivis.data.DataEvent;
import hivis.data.DataSeries;

/**
 * Create a view of a DataSeries containing the values in the DataSeries
 * sorted either according to the natural (ascending) ordering of the values if
 * no comparator is provided, or the order induced by the specified comparator.
 * If no comparator is provided all values must implement the Comparable interface and
 * must be mutually comparable (that is, v1.compareTo(v2) must not throw a
 * ClassCastException for any values v1 and v2). This sort is guaranteed to be
 * stable: equal values will not be reordered as a result of the sort.
 *
 * @author O. J. Coleman
 */
public class SortedSeries<V> extends AbstractSeriesViewMultiple<V, V> {
	protected DataSeries<V> source;
	protected Comparator<V> comparator;
	protected boolean recalc = true;
	protected V[] elements;
	
	public SortedSeries(DataSeries<V> source) {
		this.source = source;
		source.addChangeListener(this);
	}

	public SortedSeries(DataSeries<V> source, Comparator<V> comparator) {
		this.source = source;
		source.addChangeListener(this);
		this.comparator = comparator;
	}

	@Override
	public Class<?> getType() {
		return source.getType();
	}

	@Override
	public int length() {
		return source.length();
	}

	@Override
	public V get(int index) {
		if (recalc) {
			update(null);
		}
		if (index < 0 || index >= length()) return source.getEmptyValue();
		return elements[index];
	}
	
	@Override
	public void update(DataEvent cause) {
		this.beginChanges(this);
		
		if (elements == null) {
			elements = (V[]) Array.newInstance(this.getType(), length());
		}
		elements = source.asArray(elements);
		
		if (comparator == null) {
			Arrays.sort(elements);
		} else {
			Arrays.sort(elements, comparator);
		}
		recalc = false;
		
		this.finishChanges(this);
	}

	@Override
	public void dataChanged(DataEvent event) {
		if (source == event.affected) {
			recalc = true;
		}
		super.dataChanged(event);
	}
};
