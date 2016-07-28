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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.collect.Sets;

import hivis.common.LSListMap;
import hivis.common.ListMap;
import hivis.data.AbstractDataTable;
import hivis.data.DataEvent;
import hivis.data.DataListener;
import hivis.data.DataSeries;
import hivis.data.DataTable;
import hivis.data.DataTableChange;

/**
 * Base class for {@link DataTable} views. Provides a convenient interface for
 * doing so and handles and produces the necessary events.
 * 
 * @author O. J. Coleman
 */
public abstract class AbstractTableView<S extends DataSeries<?>> extends AbstractDataTable implements DataListener, TableView {
	protected int rowKeySeries = Integer.MIN_VALUE;
	
	
	/**
	 * The source data table for this view.
	 */
	protected final List<DataTable> inputTables;

	/**
	 * The data series this view presents, keyed by label.
	 */
	protected ListMap<String, S> series;

	/**
	 * Create a ViewTable that is not derived from a source DataTable.
	 */
	public AbstractTableView() {
		this(null);
	}

	/**
	 * Create a ViewTable that is derived from the given source DataTable.
	 */
	public AbstractTableView(DataTable... inputTables) {
		super();
		
		if (inputTables == null) {
			this.inputTables = new ArrayList<>();
		}
		else {
			this.inputTables = Arrays.asList(inputTables);
		}
		series = new LSListMap<>();
		for (DataTable s : inputTables) {
			s.addChangeListener(this);
		}
	}
	
	@Override
	public ListMap<String, DataSeries<?>> getLabelledSeries() {
		return (ListMap<String, DataSeries<?>>) series.unmodifiableView();
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
	public DataTable addSeries(String label, DataSeries<?> newSeries) {
		throw new UnsupportedOperationException("Can not add a series to a view.");
	}

	@Override
	public DataTable addSeries(DataTable table) {
		throw new UnsupportedOperationException("Can not add series to a view.");
	}

	@Override
	public DataTable removeSeries(String label) {
		throw new UnsupportedOperationException("Can not remove a series from a view.");
	}

	@Override
	public DataTable removeSeries(int index) {
		throw new UnsupportedOperationException("Can not remove a series from a view.");
	}

	@Override
	public void dataChanged(DataEvent event) {
		if (inputTables.contains(event.affected)) {
			updateSeriesWrapper(new ArrayList<>(event.getTypes()));
		}
	}

	/**
	 * If and when an implementation changes the list of series that it presents
	 * then this method should be called to update the selected series data.
	 * This method should also be called at the end of a constructor.
	 */
	protected void updateSeries() {
		updateSeriesWrapper(new ArrayList<>());
	}

	/**
	 * Implementations must update the {@link #series}, and/or update the values
	 * in the series. This is called when the source DataTable has changed.
	 * After the first call it is preferable, especially if the data for a
	 * series is changed, to reuse previously generated series rather than than
	 * creating new ones. The relevant change events will be generated as
	 * necessary; it is NOT necessary to call {@link #beginChanges(Object)},
	 * etc.
	 * 
	 * @param eventTypes
	 *            The type of event that occurred on the source table in case
	 *            this is useful.
	 */
	protected abstract void updateSeries(List<Object> eventTypes);

	private void updateSeriesWrapper(List<Object> eventTypes) {
		ListMap<String, S> origSeries = new LSListMap<>(series);

		this.beginChanges(this);

		updateSeries(eventTypes);

		// Remove this view as a container for removed series.
		for (DataSeries<?> s : Sets.difference(origSeries.values(), series.values())) {
			s.removeContainer(this);
		}

		// Make sure new series have this view set as the container,
		// and assign default row key.
		for (int si = 0; si < series.size(); si++) {
			DataSeries<?> s = series.get(si).getValue();
			s.addContainer(this);
			
			if (rowKeySeries == Integer.MIN_VALUE && s.length() > 0 && s.get(0) instanceof String) {
				rowKeySeries = si;
			}
		}

		Set<String> allSeriesLabels = Sets.union(origSeries.keySet(), series.keySet());
		int allSeriesLabelsSize = allSeriesLabels.size();

		// If one or more series were added.
		if (origSeries.size() < allSeriesLabelsSize) {
			this.setDataChanged(DataTableChange.SeriesAdded);
		}
		// If one or more series were removed.
		if (series.size() < allSeriesLabelsSize) {
			this.setDataChanged(DataTableChange.SeriesRemoved);
		}
		// If no series were added or removed and the ordering changed.
		if (series.size() == allSeriesLabelsSize && !series.keySet().equals(origSeries.keySet())) {
			this.setDataChanged(DataTableChange.SeriesReordered);
		}

		// Forward any other non-series events.
		for (Object et : eventTypes) {
			if (et != DataTableChange.SeriesAdded && et != DataTableChange.SeriesRemoved
					&& et != DataTableChange.SeriesReordered) {
				this.setDataChanged(et);
			}
		}

		this.finishChanges(this);
	}
	
	@Override
	public void updateView(Object cause) {
		List<Object> events = new ArrayList<>();
		events.add(cause);
		updateSeriesWrapper(events);
	}
}