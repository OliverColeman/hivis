package hivis.interact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import hivis.common.BMListSet;
import hivis.common.ListSet;
import hivis.data.DataEvent;
import hivis.data.DataTable;
import hivis.data.DataTableChange;
import hivis.data.view.ViewTableSeries;

/**
 * A view that filters a DataTable to a specified set of columns and/or rows. 
 * The view is backed by the input DataTable. Changes to the input DataTable 
 * are reflected in the view, and change events on the input DataTable are 
 * forwarded to the view DataTable.
 * 
 * The parameters (see {@link Parametrised}) are the data series names of the input table,
 * with parameter values being Boolean.TRUE or Boolean.FALSE.
 * 
 * @author O. J. Coleman
 */
public class ViewTableSeriesParametrisedSelect extends ViewTableSeries implements Parametrised {
	ListSet<Parameter<?>> params;
	Set<ParameterListener> changeListeners;
	
	public ViewTableSeriesParametrisedSelect(DataTable source) {
		super(source);
		source.addChangeListener(this);
		params = new BMListSet<>();
		updateParams();
		changeListeners = new HashSet<>();
		updateSeries();
	}
	
	
	@Override
	public ListSet<String> getParameterLabels() {
		return source.get(0).getSeriesLabels();
	}
	

	@Override
	public ListSet<Parameter<?>> getParameters() {
		return params.unmodifiableView();
	}
	
	@Override
	public Parameter<?> getParameter(String label) {
		return params.get(getParameterLabels().indexOf(label));
	}
	
	
	@Override
	public synchronized void dataChanged(DataEvent event) {
		super.dataChanged(event);
		List<ParameterEvent.Type> types = new ArrayList<>(3);
		if (event.isType(DataTableChange.SeriesAdded)) {
			types.add(ParameterEvent.Type.Added);
		}
		if (event.isType(DataTableChange.SeriesRemoved)) {
			types.add(ParameterEvent.Type.Removed);
		}
		if (event.isType(DataTableChange.SeriesReordered)) {
			types.add(ParameterEvent.Type.Reordered);
		}
		
		if (!types.isEmpty()) {
			updateParams();
			ParameterEvent.Type[] ta = new ParameterEvent.Type[types.size()];
			fireParameterChange(new ParameterEvent(ViewTableSeriesParametrisedSelect.this, types.toArray(ta)));
		}
	}

	private synchronized void updateParams() {
		// Get current set of Param objects mapped to their label.
		HashMap<String, Parameter<?>> cp = new HashMap<>();
		for (Parameter<?> p : params) {
			cp.put(p.getLabel(), p);
		}
		params.clear();
		// Re-add available params in correct order, adding new ones as necessary.
		for (String s : source.get(0).getSeriesLabels()) {
			params.add(cp.containsKey(s) ? cp.get(s) : new Param(s));
		}
	}


	@Override
	public void addParameterChangeListener(ParameterListener listener) {
		changeListeners.add(listener);
	}


	@Override
	public void removeParameterChangeListener(ParameterListener listener) {
		changeListeners.remove(listener);
	}
	
	
	@Override
	public synchronized DataTable setSeries(int[] series) {
		super.setSeries(series);
		fireParameterChange(new ParameterEvent(this, ParameterEvent.Type.ValueChanged));
		return this;
	}
	
	@Override
	public synchronized DataTable setSeries(String[] series) {
		super.setSeries(series);
		fireParameterChange(new ParameterEvent(this, ParameterEvent.Type.ValueChanged));
		return this;
	}
	
	protected void fireParameterChange(ParameterEvent event) {
		for (ParameterListener l : changeListeners) {
			l.parametersChanged(event);
		}
	}
	
	
	private class Param extends Parameter<Boolean> {
		String label;
		boolean selected = false;
		
		public Param(String l) {
			label = l;
		}
		
		@Override
		public Boolean getValue() {
			return ViewTableSeriesParametrisedSelect.this.hasSeries(label);
		}
		
		@Override
		public synchronized void setValue(Boolean value) {
			String[] labels = null;
			
			if ((Boolean) value && !selected) {
				selected = true;
				labels = new String[seriesCount() + 1];
			}
			else if (!((Boolean) value) && selected) {
				selected = false;
				labels = new String[seriesCount() - 1];
			}
			
			if (labels != null) {
				int idx = 0;
				for (Parameter<?> p : params) {
					if (((Param) p).selected) {
						labels[idx++] = p.getLabel();
					}
				}
				setSeries(labels);
			}
		}
		
		@Override
		public String getLabel() {
			return label;
		}

		@Override
		public boolean isValid() {
			return source.get(0).hasSeries(label);
		}
	}
}
