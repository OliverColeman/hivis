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
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import hivis.common.ListMap;
import hivis.common.ListSet;
import hivis.common.Util;
import hivis.data.view.RowFilter;
import hivis.data.view.TableFunction;
import hivis.data.view.TableView;
import hivis.data.view.TableViewAppend;
import hivis.data.view.TableViewFilterRows;
import hivis.data.view.TableViewFunction;
import hivis.data.view.TableViewSeries;
import hivis.data.view.TableViewTranspose;

public abstract class AbstractDataTable extends DataSetDefault implements DataTable {
	public AbstractDataTable() {
		super();
	}

	public AbstractDataTable(DataSet container) {
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
	public DataSeries<?> getSeries(int index) {
		return getLabelledSeries().get(index).getValue();
	}

	@Override
	public DataSeries<?> getSeries(String label) {
		return getLabelledSeries().get(label);
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
	
	@Override 
	public TableView transpose() {
		return new TableViewTranspose(this);
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

	/**
	 * Returns a human-readable tabulated view of this DataTable.
	 */
	@Override
	public String toString() {
		return Util.dataTableToString(this);
	}
	
	/**
	 * Returns an iterator that presents a row-based ({@link DataRow}) view of the table.
	 * The iterator will throw a ConcurrentModificationException if the table 
	 * is structurally modified while iteration is in progress. 
	 */
	@Override
	public Iterator<DataRow> iterator() {
		final AbstractDataTable me = this;
		
		// Ensure the table isn't modified structurally between iterations.
		AtomicBoolean modified = new AtomicBoolean(false);
		final DataListener listener = new DataListener() {
			@Override
			public void dataChanged(DataEvent event) {
				// If the affected DataSet is this table, then series were added, removed or reordered.
				if (event.affected == me) {
					modified.set(true);
				}
				else if (event.isType(DataSeriesChange.ValuesAdded) || event.isType(DataSeriesChange.ValuesRemoved)) {
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
	
	private class Row implements DataRow {
		int rowIndex;
		
		public Row(int index) {
			rowIndex = index;
		}

		@Override
		public int size() {
			return seriesCount();
		}

		@Override
		public int getRowIndex() {
			return rowIndex;
		}

		@Override
		public Class<?> getType(int index) {
			return getSeries(index).getType();
		}

		@Override
		public Class<?> getType(String label) {
			return getSeries(label).getType();
		}
		
		@Override
		public Object get(String label) {
			return getSeries(label).get(rowIndex);
		}

		@Override
		public boolean getBoolean(String label) {
			return getSeries(label).getBoolean(rowIndex);
		}

		@Override
		public int getInt(String label) {
			return getSeries(label).getInt(rowIndex);
		}

		@Override
		public long getLong(String label) {
			return getSeries(label).getLong(rowIndex);
		}

		@Override
		public float getFloat(String label) {
			return getSeries(label).getFloat(rowIndex);
		}

		@Override
		public double getDouble(String label) {
			return getSeries(label).getDouble(rowIndex);
		}

		@Override
		public Object get(int index) {
			return getSeries(index).get(rowIndex);
		}

		@Override
		public boolean getBoolean(int index) {
			return getSeries(index).getBoolean(rowIndex);
		}

		@Override
		public int getInt(int index) {
			return getSeries(index).getInt(rowIndex);
		}

		@Override
		public long getLong(int index) {
			return getSeries(index).getLong(rowIndex);
		}

		@Override
		public float getFloat(int index) {
			return getSeries(index).getFloat(rowIndex);
		}

		@Override
		public double getDouble(int index) {
			return getSeries(index).getDouble(rowIndex);
		}
	}
}
