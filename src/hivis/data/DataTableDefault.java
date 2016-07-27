/**
 * 
 */
package hivis.data;

import java.util.Map.Entry;

import hivis.common.BMListSet;
import hivis.common.LSListMap;
import hivis.common.ListMap;
import hivis.common.ListSet;


/**
 * Default implementation of {@link DataTable}.
 * 
 * @author O. J. Coleman
 */
public class DataTableDefault extends AbstractDataTable {
	protected ListMap<String, DataSeries<?>> series;
	protected int rowKeySeries = Integer.MIN_VALUE;
	
	public DataTableDefault() {
		super();
		series = new LSListMap<>();
	}
	
	@Override
	public ListMap<String, DataSeries<?>> getLabelledSeries() {
		return series.unmodifiableView();
	}
	
	@Override
	public synchronized DataTable addSeries(String label, DataSeries<?> newSeries) {
		if (hasSeries(label)) {
			throw new IllegalArgumentException("There is an existing DataSeries in this DataTable with the label " + label);
		}
		series.put(label, newSeries);
		newSeries.addContainer(this);
		
		if (rowKeySeries == Integer.MIN_VALUE && newSeries.length() > 0 && newSeries.get(0) instanceof String) {
			rowKeySeries = series.size() - 1;
		}
		
		this.setDataChanged(DataTableChange.SeriesAdded);
		
		return this;
	}
	
	@Override
	public synchronized DataTable addSeries(DataTable table) {
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
			newSeries.addContainer(this);
		
			if (rowKeySeries == Integer.MIN_VALUE && newSeries.length() > 0 && newSeries.get(0) instanceof String) {
				rowKeySeries = series.size() - 1;
			}
		}
		this.setDataChanged(DataTableChange.SeriesAdded);
		
		return this;
	}
	
	@Override
	public synchronized DataTable removeSeries(String label) {
		if (!hasSeries(label)) {
			throw new IllegalArgumentException("The specified DataSeries, " + label + ", does not exist in this DataTable.");
		}
		DataSeries<?> s = series.remove(label);
		s.removeContainer(this);
		this.setDataChanged(DataTableChange.SeriesRemoved);
		
		return this;
	}
	
	@Override
	public synchronized DataTable removeSeries(int index) {
		DataSeries<?> s = series.remove(index).getValue();
		s.removeContainer(this);
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
}
