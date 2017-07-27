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

import java.util.Arrays;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import hivis.common.HV;
import hivis.common.LSListMap;
import hivis.common.ListMap;
import hivis.common.ListSet;
import hivis.common.Util;
import hivis.data.view.CalcSeries;
import hivis.data.view.Function;
import hivis.data.view.GroupedTable;
import hivis.data.view.DefaultGroupedTable;
import hivis.data.view.RowFilter;
import hivis.data.view.SeriesFunction;
import hivis.data.view.SeriesView;
import hivis.data.view.SortedTable;
import hivis.data.view.TableFunction;
import hivis.data.view.TableView;
import hivis.data.view.TableViewAppend;
import hivis.data.view.TableViewFilterRows;
import hivis.data.view.TableViewFunction;
import hivis.data.view.TableViewSeries;
import hivis.data.view.TableViewTranspose;

public abstract class AbstractDataTable extends DataDefault implements DataTable {
	private int equalToHashCode = 0; // cached hashcode for equalToHashCode().
	
	private AbstractDataTable immutableCopy = null;
	
	public AbstractDataTable() {
		super();
	}

	public AbstractDataTable(Data container) {
		super(container);
	}

	@Override
	public int seriesCount() {
		return getLabelledSeries().size();
	}

	@Override
	public int length() {
		int l = 0;
		lock();
		try {
			for (DataSeries<?> s : getLabelledSeries().values()) {
				if (s.length() > l) {
					l = s.length();
				}
			}
		}
		finally {
			unlock();
		}
		return l;
	}
	

	@Override
	public List<DataSeries<?>> getAll() {
		return getLabelledSeries().values();
	}

	@Override
	public boolean hasSeries(String label) {
		return getLabelledSeries().containsKey(label);
	}

	@Override
	public DataSeries<?> get(int index) {
		if (index >= seriesCount()) throw new IllegalArgumentException("Series " + index + " does not exist.");
		return getLabelledSeries().get(index).getValue();
	}

	@Override
	public DataSeries<?> get(String label) {
		if (!hasSeries(label)) throw new IllegalArgumentException("Series " + label + " does not exist.");
		return getLabelledSeries().get(label);
	}

	@Override
	public DataSeries<?> getSeries(int index) {
		return get(index);
	}

	@Override
	public DataSeries<?> getSeries(String label) {
		return get(label);
	}
	
	@Override
	public DataRow getRow(int index) {
		return new Row(index);
	}

	@Override
	public ListSet<String> getSeriesLabels() {
		return getLabelledSeries().keySet();
	}

	@Override
	public String getSeriesLabel(int index) {
		return getLabelledSeries().get(index).getKey();
	}
	

	@Override
	public boolean hasRowKeys() {
		return getRowKeyIndex() >= 0;
	}

	@Override
	public String getRowKeyLabel() {
		return getSeriesLabel(getRowKeyIndex());
	}
	
	@Override
	public DataTable copy() {
		lock();
		try {
			DataTable copy = new DataTableDefault();
			for (Entry<String, DataSeries<?>> entry : this.getLabelledSeries().entrySet()) {
				copy.addSeries(entry.getKey(), entry.getValue().copy());
			}
			return copy;
		}
		finally {
			unlock();
		}
	}
	
	
	@Override
	public DataTable immutableCopy() {
		if (immutableCopy != null) return immutableCopy;
		
		lock();
		try {
			ListMap<String, DataSeries<?>> seriesCopies = new LSListMap<>();
			for (Map.Entry<String, DataSeries<?>> e : this.getLabelledSeries().entrySet()) {
				seriesCopies.put(e.getKey(), e.getValue().immutableCopy());
			}
			final ListMap<String, DataSeries<?>> seriesCopiesFinal = seriesCopies.unmodifiableView();
			final int rowKeyIndex = this.getRowKeyIndex();
			immutableCopy = new AbstractUnmodifiableDataTable<DataSeries<?>>() {
				@Override
				public boolean isMutable() {
					return false;
				}
				@Override
				public ListMap<String, DataSeries<?>> getLabelledSeries() {
					return seriesCopiesFinal;
				}
				@Override
				public int getRowKeyIndex() {
					return rowKeyIndex;
				}
			};
			return immutableCopy;
		}
		finally {
			unlock();
		}	
	}
	
	
	@Override
	public TableView selectSeries(int... series) {
		return (new TableViewSeries(this)).setSeries(series);
	}

	@Override
	public TableView selectSeriesRange(int begin, int end) {
		return (new TableViewSeries(this)).setSeriesRange(begin, end);
	}

	@Override
	public TableView selectSeries(String... series) {
		return (new TableViewSeries(this)).setSeries(series);
	}

	@Override
	public TableView selectSeriesGlob(String pattern) {
		return (new TableViewSeries(this)).setSeriesGlob(pattern);
	}

	@Override
	public TableView selectSeriesRE(Pattern pattern) {
		return (new TableViewSeries(this)).setSeriesRE(pattern);
	}

	@Override
	public TableView selectSeriesRE(Pattern pattern, String renamePattern) {
		return (new TableViewSeries(this)).setSeriesRE(pattern, renamePattern);
	}

	@Override
	public TableView relabelSeries(String... labels) {
		return (new TableViewSeries(this)).renameSeries(labels);
	}

	@Override
	public TableView relabelSeriesPP(String prefix, String postfix) {
		return (new TableViewSeries(this)).renameSeriesPP(prefix, postfix);
	}

	@Override
	public TableView apply(TableFunction function, boolean includeOriginalSeries) {
		return new TableViewFunction(function, includeOriginalSeries, this);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public TableView apply(final SeriesFunction function) {
		return new TableViewFunction(new TableFunction() {
			@SuppressWarnings("unchecked")
			@Override
			public void getSeries(List<DataTable> input, ListMap<String, DataSeries<?>> outputSeries) {
				for (Map.Entry<String, DataSeries<?>> series : input.get(0).getLabelledSeries().entrySet()) {
					outputSeries.put(series.getKey(), function.apply(series.getValue()));
				}
			}
		}, this);
	}
	
	
	private Map<String, TableView> rangeViews = null;
	@Override
	public TableView toUnitRange() {
		return toRange(0, 1);
	}
	@Override
	public TableView toRange(double min, double max) {
		return toRange(new DataValueDouble(min), new DataValueDouble(max));
	}
	@Override
	public TableView toRange(DataValue<?> min, DataValue<?> max) {
		String key = min.getDouble() + ":" + max.getDouble();
		TableView rangeView = null;
		if (rangeViews == null || !rangeViews.containsKey(key)) {
			if (rangeViews == null) {
				rangeViews = new HashMap<>();
			}
			rangeView = apply(new SeriesFunction() {
				public DataSeries apply(DataSeries input) {
					return input.isNumeric() ? input.toRange(min, max) : input;
				}
			});
		}
		else {
			rangeView = rangeViews.get(key);
		}
		return rangeView;
	}
	
	
	@Override 
	public TableView transpose() {
		return new TableViewTranspose(this);
	}
	
	@Override
	public TableView combine(DataTable table) {
		return new TableViewFunction(new TableFunction() {
			@Override
			public void getSeries(List<DataTable> input, ListMap<String, DataSeries<?>> outputSeries) {
				outputSeries.putAll(input.get(0).getLabelledSeries());
				outputSeries.putAll(input.get(1).getLabelledSeries());
			}
		}, true, this, table);
	}

	@Override 
	public TableView append(DataTable table) {
		return new TableViewAppend(this, table);
	}
	
	@Override
	public TableView selectRowRange(final int beginIndex, final int endIndex) {
		return new TableViewFilterRows(this, new RowFilter() {
			@Override
			public boolean excludeRow(DataTable input, int index) {
				return index < beginIndex || index > endIndex;
			}
		});
	}

	@Override
	public TableView selectRows(final int... rows) {
		final int[] rowsSorted = Arrays.copyOf(rows, rows.length);
		Arrays.sort(rowsSorted);
		return new TableViewFilterRows(this, new RowFilter() {
			@Override
			public boolean excludeRow(DataTable input, int index) {
				return Arrays.binarySearch(rowsSorted, index) < 0;
			}
		});
	}

	@Override
	public TableView selectRows(RowFilter filter) {
		return new TableViewFilterRows(this, filter);
	}
	
	
	@Override
	public TableView sort(int sortingSeries) {
		return new SortedTable(this, sortingSeries);
	}

	@Override
	public TableView sort(String sortingSeries) {
		return new SortedTable(this, sortingSeries);
	}

	@Override
	public TableView sort(Comparator<DataRow> comparator) {
		return new SortedTable(this, comparator);
	}
	
	
	@Override
	public <K> GroupedTable<K> group(int groupingSeries) {
		return new DefaultGroupedTable<K>(this, groupingSeries);
	}

	@Override
	public <K> GroupedTable<K> group(String groupingSeries) {
		return new DefaultGroupedTable<K>(this, groupingSeries);
	}

	@Override
	public <K> GroupedTable<K> group(Function<DataRow, K> keyFunction) {
		return new DefaultGroupedTable<K>(this, keyFunction);
	}

	
	/**
	 * Returns a human-readable tabulated view of this DataTable.
	 */
	@Override
	public String toString() {
		lock();
		String out = Util.dataTableToString(this);
		unlock();
		return out;
	}
	

	@Override
	public boolean containsKey(String key) {
		return this.hasSeries(key);
	}

	@Override
	public DataSeries<?> put(String key, DataSeries<?> value) {
		this.addSeries(key, value);
		return null;
	}
	
	@Override
	public DataSeries<?> remove(String key) {
		DataSeries<?> existing = this.getSeries(key);
		this.removeSeries(key);
		return existing;
	}
	
	@Override
	public int size() {
		return this.seriesCount();
	}
	
	@Override
	public SeriesView<String> keys() {
		return new CalcSeries<Object, String> (this) {
			@Override 
			public void update(DataEvent cause) {
				if (cache == null) {
					setupCache();
				}
				// The cache has a change listener attached to it that will call dataChanged  
				// on this CalcSeries if the data in it (the cache) actually changes, so we 
				// use beginChanges and finishChanges to avoid firing multiple change events.
				this.beginChanges(this);
				// Make sure cache series is the right length.
				int length = length();
				cache.resize(length);
				for (int i = 0; i < length; i++) {
					cache.setValue(i, inputTable.getSeriesLabel(i));
				}
				this.finishChanges(this);
			}
			@Override 
			public int length() {
				return inputTable.seriesCount();
			}
			@Override
			public String calc(int index) {
				return null;
			}
		};
	}
	
	@Override
	public SeriesView<DataSeries<?>> values() {
		return new CalcSeries<Object, DataSeries<?>> (this) {
			@Override 
			public void update(DataEvent cause) {
				if (cache == null) {
					setupCache();
				}
				// The cache has a change listener attached to it that will call dataChanged  
				// on this CalcSeries if the data in it (the cache) actually changes, so we 
				// use beginChanges and finishChanges to avoid firing multiple change events.
				this.beginChanges(this);
				// Make sure cache series is the right length.
				int length = length();
				cache.resize(length);
				for (int i = 0; i < length; i++) {
					cache.setValue(i, inputTable.get(i));
				}
				this.finishChanges(this);
			}
			@Override 
			public int length() {
				return inputTable.seriesCount();
			}
			@Override
			public DataSeries<?> calc(int index) {
				return null;
			}
		};
	}
	
	@Override
	public boolean equalTo(Data data) {
		if (data == this) return true;
		if (!(data instanceof DataTable)) return false;
		DataTable otherTable = (DataTable) data;
		// Check series labels are the same.
		if (!this.getSeriesLabels().equals(otherTable.getSeriesLabels())) return false;
		// Check series values are the same.
		try {
			this.lock();
			try {
				otherTable.lock();
				for (int s = 0; s < this.seriesCount(); s++) {
					if (!this.get(s).equals(otherTable.get(s))) return false;
				}
				return true;
			}
			finally {
				otherTable.unlock();
			}
		}
		finally {
			this.unlock();
		}
	}
	

	@Override
	public int equalToHashCode() {
		if (isMutable()) {
			throw new IllegalStateException("equalToHashCode() called on a mutable Data set.");
		}
		if (equalToHashCode == 0) {
			// Hash code of list is based on contained elements.
			equalToHashCode = this.getSeriesLabels().hashCode();
			for (int s = 0; s < seriesCount(); s++) {
				if (get(s).isMutable()) {
					throw new IllegalStateException("equalToHashCode() called on a DataTable with mutable series.");
				}
				equalToHashCode = 31 * equalToHashCode + get(s).equalToHashCode();
			}
		}
		return equalToHashCode;
	}
	
	
	/**
	 * Returns an iterator that presents a row-based (see {@link DataRow}) 
	 * view of this table. If the table is modified while iteration
	 * is in progress it will generally not cause an error or exception. The 
	 * DataRows provided by this iterator store a row index that does not change;
	 * calls to {@link DataRow#get(String)} and similar use the stored row index to 
	 * retrieve the data from this table. Thus if the data changes in the table
	 * it will be reflected on subsequent calls to the <code>get</code> methods
	 * on the DataRow objects. If rows are added or removed then the iterator will continue
	 * on to the last available row even if it was not present before iteration began (unless the
	 * iterator is exhausted before the new rows are added). If the iterator has
	 * progressed past the point of row removal then {@link Iterator#hasNext()} will 
	 * begin returning false and calls to {@link Iterator#next()} will begin returning null,
	 * and for any DataRows that have been provided for rows that no longer exist calls
	 * to the <code>get</code> methods will begin returning the empty values for the 
	 * requested column/series using the {@link DataSeries#getEmptyValue()} method
	 * for the underlying series.
	 */
	@Override
	public Iterator<DataRow> iterator() {
		final DataTable me = this;
		// Current index into table.
		AtomicInteger rowIndex = new AtomicInteger(0);
		
		return new Iterator<DataRow>() {
			// When hasNext() is called retrieve and store the next item
			// (subsequent calls before next() is called have no effect).
			DataRow next = null;
			boolean nextObtained = false;
			
			@Override
			public synchronized boolean hasNext() {
				me.lock();
				try {
					if (nextObtained == false && rowIndex.get() < me.length()) {
						next = new Row(rowIndex.getAndIncrement());
						nextObtained = true;
					}
				}
				finally {
					me.unlock();
				}
				return nextObtained;
			}

			@Override
			public synchronized DataRow next() {
				if (hasNext()) {
					DataRow ret = next;
					next = null;
					nextObtained = false;
					return ret;
				}
				return null;
			}
		};
	}
	
	private class Row extends DataDefault implements DataRow {
		private final int rowIndex;
		private int equalToHashCode = 0;
		
		public Row(int index) {
			rowIndex = index;
		}

		@Override
		public int length() {
			return seriesCount();
		}
		
		@Override
		public boolean isNumeric(int index) {
			return AbstractDataTable.this.get(index).isNumeric();
		}

		@Override
		public boolean isNumeric(String label) {
			return AbstractDataTable.this.get(label).isNumeric();
		}

		@Override
		public int getRowIndex() {
			return rowIndex;
		}

		@Override
		public Class<?> getType(int index) {
			return AbstractDataTable.this.get(index).getType();
		}

		@Override
		public Class<?> getType(String label) {
			return AbstractDataTable.this.get(label).getType();
		}
		
		@Override
		public Object get(String label) {
			return AbstractDataTable.this.get(label).get(rowIndex);
		}

		@Override
		public boolean getBoolean(String label) {
			return AbstractDataTable.this.get(label).getBoolean(rowIndex);
		}

		@Override
		public int getInt(String label) {
			return AbstractDataTable.this.get(label).getInt(rowIndex);
		}

		@Override
		public long getLong(String label) {
			return AbstractDataTable.this.get(label).getLong(rowIndex);
		}

		@Override
		public float getFloat(String label) {
			return AbstractDataTable.this.get(label).getFloat(rowIndex);
		}

		@Override
		public double getDouble(String label) {
			return AbstractDataTable.this.get(label).getDouble(rowIndex);
		}
		
		@Override
		public String getString(String label) {
			return "" + AbstractDataTable.this.get(label).get(rowIndex);
		}

		@Override
		public Object get(int index) {
			return AbstractDataTable.this.get(index).get(rowIndex);
		}

		@Override
		public boolean getBoolean(int index) {
			return AbstractDataTable.this.get(index).getBoolean(rowIndex);
		}

		@Override
		public int getInt(int index) {
			return AbstractDataTable.this.get(index).getInt(rowIndex);
		}

		@Override
		public long getLong(int index) {
			return AbstractDataTable.this.get(index).getLong(rowIndex);
		}

		@Override
		public float getFloat(int index) {
			return AbstractDataTable.this.get(index).getFloat(rowIndex);
		}

		@Override
		public double getDouble(int index) {
			return AbstractDataTable.this.get(index).getDouble(rowIndex);
		}
		
		@Override
		public String getString(int index) {
			return "" + AbstractDataTable.this.get(index).get(rowIndex);
		}
		
		@Override
		public void lock() {
			AbstractDataTable.this.lock();
		}

		@Override
		public void unlock() {
			AbstractDataTable.this.unlock();
		}

		@Override
		public boolean equalTo(Data data) {
			if (data == this) return true;
			if (!(data instanceof DataRow)) return false;
			DataRow row = (DataRow) data;
			try {
				this.lock();
				try {
					row.lock();
					if (this.length() != row.length()) return false;
					for (int i = 0; i < length(); i++) {
						if (!Util.equalsIncData(get(i), row.get(i))) {
							return false;
						}
					}
					return true;
				}
				finally {
					row.unlock();
				}
			}
			finally {
				this.unlock();
			}
		}
		
		@Override
		public int equalToHashCode() {
			if (AbstractDataTable.this.isMutable()) {
				throw new IllegalStateException("equalToHashCode() called on a DataRow for a mutable DataTable.");
			}
			if (equalToHashCode == 0) {
				equalToHashCode = 1;
				for (int s = 0; s < seriesCount(); s++) {
					if (AbstractDataTable.this.get(s).isMutable()) {
						throw new IllegalStateException("equalToHashCode() called on a DataRow for a DataTable containing mutable DataSeries.");
					}
					equalToHashCode = 31 * equalToHashCode + get(s).hashCode();
				}
			}
			return equalToHashCode;
		}

		@Override
		public DataRow immutableCopy() {
			// To create immutable rows we re-use the immutable copy of the table.
			AbstractDataTable.this.immutableCopy();
			return immutableCopy.getImmutableRow(rowIndex);
		}
	}
	
	private ImmutableRow getImmutableRow(int index) {
		return new ImmutableRow(index);
	}
	private class ImmutableRow extends Row {
		public ImmutableRow(int rowIndex) {
			super(rowIndex);
			if (AbstractDataTable.this.isMutable()) {
				throw new IllegalStateException("Created an ImmutableRow for a mutable table.");
			}
		}
		@Override
		public boolean isMutable() {
			return false;
		}
	}
}
