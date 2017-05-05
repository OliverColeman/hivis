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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import hivis.common.ListMap;
import hivis.common.ListSet;
import hivis.common.Util;
import hivis.data.view.CalcSeries;
import hivis.data.view.Function;
import hivis.data.view.GroupedTable;
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
	public synchronized int length() {
		int l = 0;
		for (DataSeries<?> s : getLabelledSeries().values()) {
			if (s.length() > l) {
				l = s.length();
			}
		}
		return l;
	}
	

	@Override
	public ListSet<DataSeries<?>> getAll() {
		return getLabelledSeries().values();
	}

	@Override
	public boolean hasSeries(String label) {
		return getLabelledSeries().containsKey(label);
	}

	@Override
	public DataSeries<?> get(int index) {
		return getLabelledSeries().get(index).getValue();
	}

	@Override
	public DataSeries<?> get(String label) {
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
		DataTable copy = new DataTableDefault();
		for (Entry<String, DataSeries<?>> series : this.getLabelledSeries().entrySet()) {
			copy.addSeries(series.getKey(), series.getValue().copy());
		}
		return copy;
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
	public <K> DataMap<K, TableView> group(int groupingSeries) {
		return new GroupedTable<>(this, groupingSeries);
	}

	@Override
	public <K> DataMap<K, TableView> group(String groupingSeries) {
		return new GroupedTable<>(this, groupingSeries);
	}

	@Override
	public <K> DataMap<K, TableView> group(Function<DataRow, K> keyFunction) {
		return new GroupedTable<>(this, keyFunction);
	}

	
	/**
	 * Returns a human-readable tabulated view of this DataTable.
	 */
	@Override
	public String toString() {
		return Util.dataTableToString(this);
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
	
	
	/**
	 * Returns an iterator that presents a row-based ({@link DataRow}) view of the table.
	 * The iterator will throw a ConcurrentModificationException if the table 
	 * is structurally modified while iteration is in progress. 
	 */
	@Override
	public Iterator<DataRow> iterator() {
		final AbstractDataTable me = this;
		
		// Current length of table.
		final int originalLength = me.length();
		
		// Ensure the table isn't modified structurally between iterations.
		AtomicBoolean modified = new AtomicBoolean(false);
		final DataListener listener = new DataListener() {
			@Override
			public void dataChanged(DataEvent event) {
				// If series were added, removed or reordered, or the length has changed.
				if (event.isType(DataTableChange.SeriesAdded) || 
						event.isType(DataTableChange.SeriesRemoved) || 
						event.isType(DataTableChange.SeriesReordered) || 
						me.length() != originalLength) {
					modified.set(true);
				}
				
			}
		};
		this.addChangeListener(listener);
		for (DataSeries<?> s : getAll()) {
			s.addChangeListener(listener);
		}
		
		// If the table is empty then we've already finished.
		AtomicBoolean finished = new AtomicBoolean(me.length() == 0);
		
		// Current index into table.
		AtomicInteger rowIndex = new AtomicInteger(0);
		
		return new Iterator<DataRow>() {
			@Override
			public boolean hasNext() {
				synchronized (me) {
					if (modified.get()) {
						throw new ConcurrentModificationException("The table has been structurally modified, cannot continue iteration.");
					}
					return !finished.get();
				}
			}

			@Override
			public DataRow next() {
				synchronized (me) {
					if (modified.get()) {
						throw new ConcurrentModificationException("The table has been structurally modified, cannot continue iteration.");
					}
					if (finished.get()) {
						return null;
					}
					Row row = new Row(rowIndex.getAndIncrement());
					if (rowIndex.get() == me.length()) {
						finished.set(true);
					}
					return row;
				}
			}
		};
	}
	
	private class Row extends DataDefault implements DataRow, DataListener {
		int rowIndex;
		int hash;
		
		public Row(int index) {
			rowIndex = index;
			updateHash();
			AbstractDataTable.this.addChangeListener(this);
		}
		
		private void checkValid() {
			if (rowIndex >= AbstractDataTable.this.length()) {
				throw new IllegalStateException("The DataRow for index " + rowIndex + " no longer exists in the DataTable. DataTable length is " + AbstractDataTable.this.length() + ".");
			}
		}

		@Override
		public int length() {
			checkValid();
			return seriesCount();
		}
		
		@Override
		public boolean isNumeric(int index) {
			checkValid();
			return getSeries(index).isNumeric();
		}

		@Override
		public boolean isNumeric(String label) {
			checkValid();
			return getSeries(label).isNumeric();
		}

		@Override
		public int getRowIndex() {
			return rowIndex;
		}

		@Override
		public Class<?> getType(int index) {
			checkValid();
			return getSeries(index).getType();
		}

		@Override
		public Class<?> getType(String label) {
			checkValid();
			return getSeries(label).getType();
		}
		
		@Override
		public Object get(String label) {
			checkValid();
			return getSeries(label).get(rowIndex);
		}

		@Override
		public boolean getBoolean(String label) {
			checkValid();
			return getSeries(label).getBoolean(rowIndex);
		}

		@Override
		public int getInt(String label) {
			checkValid();
			return getSeries(label).getInt(rowIndex);
		}

		@Override
		public long getLong(String label) {
			checkValid();
			return getSeries(label).getLong(rowIndex);
		}

		@Override
		public float getFloat(String label) {
			checkValid();
			return getSeries(label).getFloat(rowIndex);
		}

		@Override
		public double getDouble(String label) {
			checkValid();
			return getSeries(label).getDouble(rowIndex);
		}
		
		@Override
		public String getString(String label) {
			checkValid();
			return "" + getSeries(label).get(rowIndex);
		}

		@Override
		public Object get(int index) {
			checkValid();
			return getSeries(index).get(rowIndex);
		}

		@Override
		public boolean getBoolean(int index) {
			checkValid();
			return getSeries(index).getBoolean(rowIndex);
		}

		@Override
		public int getInt(int index) {
			checkValid();
			return getSeries(index).getInt(rowIndex);
		}

		@Override
		public long getLong(int index) {
			checkValid();
			return getSeries(index).getLong(rowIndex);
		}

		@Override
		public float getFloat(int index) {
			checkValid();
			return getSeries(index).getFloat(rowIndex);
		}

		@Override
		public double getDouble(int index) {
			checkValid();
			return getSeries(index).getDouble(rowIndex);
		}
		
		@Override
		public String getString(int index) {
			checkValid();
			return "" + getSeries(index).get(rowIndex);
		}

		@Override
		public void dataChanged(DataEvent event) {
			if (rowIndex < AbstractDataTable.this.length()) {
				int oldHash = hash;
				updateHash();
				if (oldHash != hash) {
					this.fireChangeEvent(new DataEvent(this, event));
				}
			}
		}
		
		@Override
		public int hashCode() {
			return hash;
		}
		
		private void updateHash() {
			hash = 1;
			for (int i = 0; i < length(); i++) {
				Object element = get(i);
				hash = 31 * hash + (element == null ? 0 : element.hashCode());
			}
		}
	}
}
