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

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import hivis.common.LSListMap;
import hivis.common.ListMap;
import hivis.data.view.SeriesView;
import hivis.data.view.TableView;


/**
 * Default implementation of {@link DataTable}.
 * 
 * @author O. J. Coleman
 */
public class DataTableDefault extends AbstractDataTable {
	private int length; // Cached length, -1 indicates it needs recalculating.
	private ListMap<String, DataSeries<?>> series;
	private Multimap<DataSeries<?>, String> seriesToKeys;
	private int rowKeySeries = Integer.MIN_VALUE;
	private LengthChangeListener lengthChangeListener;
	
	public DataTableDefault() {
		super();
		series = new LSListMap<>();
		seriesToKeys = HashMultimap.create();
		lengthChangeListener = new LengthChangeListener();
	}
	
	@Override
	public ListMap<String, DataSeries<?>> getLabelledSeries() {
		return series.unmodifiableView();
	}
	
	@Override
	public DataTable addSeries(String label, DataSeries<?> newSeries) {
		if (hasSeries(label)) {
			throw new IllegalArgumentException("There is an existing DataSeries in this DataTable with the label " + label);
		}
		
		series.put(label, newSeries);
		// If there are not yet other instances of the series in this table.
		if (!seriesToKeys.containsKey(newSeries)) {
			newSeries.addContainer(this);
			newSeries.addChangeListener(lengthChangeListener);
			
			if (rowKeySeries == Integer.MIN_VALUE && newSeries.length() > 0 && newSeries.get(0) instanceof String) {
				rowKeySeries = series.size() - 1;
			}
		}
		seriesToKeys.put(newSeries, label);
		
		length = -1;
		
		this.setDataChanged(DataTableChange.SeriesAdded);
		
		return this;
	}
	
	
	@Override
	public DataTable addSeries(DataTable table) {
		if (table.seriesCount() == 0) {
			return this;
		}
		
		for (Entry<String, DataSeries<?>> s : table.getLabelledSeries().entrySet()) {
			String label = s.getKey();
			if (hasSeries(s.getKey())) {
				throw new IllegalArgumentException("There is an existing DataSeries in this DataTable with the label " + s.getKey());
			}
			
			DataSeries<?> newSeries = s.getValue();
			
			series.put(label, newSeries);
			// If there are not yet other instances of the series in this table.
			if (!seriesToKeys.containsKey(newSeries)) {
				newSeries.addContainer(this);
				newSeries.addChangeListener(lengthChangeListener);
				
				if (rowKeySeries == Integer.MIN_VALUE && newSeries.length() > 0 && newSeries.get(0) instanceof String) {
					rowKeySeries = series.size() - 1;
				}
			}
			seriesToKeys.put(newSeries, label);
		}
		
		length = -1;
		
		this.setDataChanged(DataTableChange.SeriesAdded);
		
		return this;
	}
	
	@Override
	public DataTable removeSeries(String label) {
		if (!hasSeries(label)) {
			throw new IllegalArgumentException("The specified DataSeries, " + label + ", does not exist in this DataTable.");
		}
		DataSeries<?> s = series.remove(label);
		seriesToKeys.remove(s, label);
		
		// If there are no other instances of the removed series in this table.
		if (!seriesToKeys.containsKey(s)) {
			s.removeContainer(this);
			s.removeChangeListener(lengthChangeListener);
			length = -1;
		}
		
		this.setDataChanged(DataTableChange.SeriesRemoved);
		return this;
	}
	
	@Override
	public DataTable removeSeries(int index) {
		Entry<String, DataSeries<?>> labelSeries = series.remove(index);
		String label = labelSeries.getKey();
		DataSeries<?> s = labelSeries.getValue();
		
		seriesToKeys.remove(s, label);
		// If there are no other instances of the removed series in this table.
		if (!seriesToKeys.containsKey(s)) {
			s.removeContainer(this);
			s.removeChangeListener(lengthChangeListener);
			length = -1;
		}
		this.setDataChanged(DataTableChange.SeriesRemoved);
		return this;
	}
	
	@Override
	public void setRowKey(int index) {
		rowKeySeries = index;
	}
	
	@Override
	public int getRowKeyIndex() {
		return rowKeySeries;
	}
	
	@Override
	public int length() {
		lock();
		try {
			if (length == -1) {
				length = 0;
				for (DataSeries<?> s : getLabelledSeries().values()) {
					if (s.length() > length) {
						length = s.length();
					}
				}
			}
			return length;
		}
		finally {
			unlock();
		}
	}
	
	private class LengthChangeListener implements DataListener {
		@Override
		public void dataChanged(DataEvent event) {
			if (event.isType(DataSeriesChange.ValuesAdded) || event.isType(DataSeriesChange.ValuesRemoved)) {
				length = -1;
			}
		}
	}
	
	
	private ReentrantLock lock = new ReentrantLock();
	@Override
	public void lock() {
		lock.lock();
	}
	@Override
	public void unlock() {
		lock.unlock();
	}
}
