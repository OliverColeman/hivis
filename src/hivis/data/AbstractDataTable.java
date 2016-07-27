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

import java.util.regex.Pattern;

import hivis.common.ListMap;
import hivis.common.ListSet;
import hivis.common.Util;
import hivis.data.view.TableFunction;
import hivis.data.view.ViewTableAppend;
import hivis.data.view.ViewTableFunction;
import hivis.data.view.ViewTableSeries;
import hivis.data.view.ViewTableTranspose;

public abstract class AbstractDataTable extends DataSetDefault implements DataTable {
	public AbstractDataTable() {
		super();
	}

	public AbstractDataTable(DataSet container) {
		super(container);
	}

	@Override
	public int seriesCount() {
		return  getLabelledSeries().size();
	}

	@Override
	public int length() {
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
	public DataTable select(int... series) {
		return (new ViewTableSeries(this)).setSeries(series);
	}

	@Override
	public DataTable selectRange(int begin, int end) {
		return (new ViewTableSeries(this)).setSeriesRange(begin, end);
	}

	@Override
	public DataTable select(String... series) {
		return (new ViewTableSeries(this)).setSeries(series);
	}

	@Override
	public DataTable selectGlob(String pattern) {
		return (new ViewTableSeries(this)).setSeriesGlob(pattern);
	}

	@Override
	public DataTable selectRE(Pattern pattern) {
		return (new ViewTableSeries(this)).setSeriesRE(pattern);
	}

	@Override
	public DataTable selectRE(Pattern pattern, String renamePattern) {
		return (new ViewTableSeries(this)).setSeriesRE(pattern, renamePattern);
	}

	@Override
	public DataTable relabel(String... labels) {
		return (new ViewTableSeries(this)).renameSeries(labels);
	}

	@Override
	public DataTable relabelPP(String prefix, String postfix) {
		return (new ViewTableSeries(this)).renameSeriesPP(prefix, postfix);
	}

	@Override
	public DataTable apply(TableFunction function, boolean includeOriginalSeries) {
		return new ViewTableFunction(function, includeOriginalSeries, this);
	}
	
	@Override 
	public DataTable transpose() {
		return new ViewTableTranspose(this);
	}

	@Override 
	public DataTable append(DataTable table) {
		return new ViewTableAppend(this, table);
	}

	/**
	 * Returns a human-readable tabulated view of this DataTable.
	 */
	@Override
	public String toString() {
		return Util.dataTableToString(this);
	}
}
