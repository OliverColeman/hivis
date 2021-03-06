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

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hivis.common.BMListSet;
import hivis.common.HV;
import hivis.common.ListSet;
import hivis.common.Util;
import hivis.data.DataSeriesDouble;
import hivis.data.DataTable;
import hivis.data.DataTableDefault;

/**
 * A view that allows selecting, reordering and renaming the {@link hivis.data.DataSeries}
 * from a {@link DataTable} based on a list of series indexes or labels.
 * 
 * @author O. J. Coleman
 */
public class TableViewSeries extends TableViewSeriesBase {
	ListSet<String> selected;
	ListSet<String> selectedRename;

	public TableViewSeries(DataTable source) {
		super(source);

		selected = new BMListSet<>();
		selectedRename = selected;
	}

	@Override
	public ListSet<String>[] getSelected() {
		return new ListSet[] { selected, selectedRename };
	}
	

	/**
	 * Relabels the series in the input table with the given labels.
	 * @param labels The new labels. The number of labels must match the number of series in the input table.
	 * @return This data table view.
	 */
	public TableViewSeries renameSeries(String...labels) {
		if (labels.length != input.get(0).seriesCount()) {
			throw new IllegalArgumentException("The number of labels given does not match the number of series in the input table.");
		}
		
		selected.clear();
		selected.addAll(input.get(0).getSeriesLabels());
		selectedRename = new BMListSet<>(labels);

		if (selectedRename.size() != input.get(0).seriesCount()) {
			throw new IllegalArgumentException("The labels given are not unique.");
		}
		
		updateSeries();
		return this;
	}

	/**
	 * Relabels the series in the input table by prepending and appending the
	 * specified prefix and postfix to their labels. If the prefix or postfix
	 * contain '\\oi' this will be replaced by the (original) index of the
	 * series. An '\\oi' may be followed by numerals to indicate an offset, for
	 * example "my\\oi5pf" will give strings "my5pf", my6pf", "my7pf" and so on.
	 * 
	 * @param prefix
	 *            The string to prepend to each label. May be null or an empty
	 *            string.
	 * @param postfix
	 *            The string to append to each label. May be null or an empty
	 *            string.
	 * @return This data table view.
	 */
	public TableViewSeries renameSeriesPP(String prefix, String postfix) {
		if (prefix == null)
			prefix = "";
		if (postfix == null)
			postfix = "";

		selected.clear();
		selected.addAll(input.get(0).getSeriesLabels());

		selectedRename = new BMListSet<>();
		for (int i = 0; i < selected.size(); i++) {
			String preRepl = processReplacements(prefix, i, i);
			String postRepl = processReplacements(postfix, i, i);
			selectedRename.add(preRepl + selected.get(i) + postRepl);
		}

		updateSeries();
		return this;
	}

	private String processReplacements(String s, int oi, int ni) {
		Matcher m = Pattern.compile("(\\\\[on]i)(\\d*)").matcher(s);
		while (m.find()) {
			MatchResult mr = m.toMatchResult();
			int offset = (mr.group(2) == null || mr.group(2).length() == 0) ? 0 : Integer.parseInt(mr.group(2));
			String replacement = "" + (offset + (mr.group(1).equals("\\oi") ? oi : ni));
			s = s.substring(0, mr.start()) + replacement + s.substring(mr.end());
			m.reset(s);
		}
		return s;
	}

	/**
	 * Selects the specified series from the input table in the order given.
	 * 
	 * @param series
	 *            Indices of series to select in the order to select them.
	 * @return This data table view.
	 */
	public TableViewSeries setSeries(int... series) {
		selected.clear();
		for (int index : series) {
			if (index >= input.get(0).seriesCount()) {
				throw new IllegalArgumentException(
						"Error setting series for ViewTableSeries: no series with index " + index + " in input table.");
			}
			selected.add(input.get(0).getSeriesLabel(index));
		}
		// No rename.
		selectedRename = selected;
		updateSeries();
		return this;
	}
	
	/**
	 * Selects the specified range of series from the input table.
	 * 
	 * @param begin The index of the first series to include.
	 * @param end The index of the last series to include.
	 * 
	 * @return This data table view.
	 */
	public TableViewSeries setSeriesRange(int begin, int end) {
		if (begin > end) {
			throw new IllegalArgumentException("Error setting series for ViewTableSeries: first index must be less than or equal to end index.");
		}
		if (begin < 0) {
			throw new IllegalArgumentException("Error setting series for ViewTableSeries: first index less than 0.");
		}
		if (end >= input.get(0).seriesCount()) {
			throw new IllegalArgumentException("Error setting series for ViewTableSeries: last index greater than last series index in input table.");
		}
		
		selected.clear();
		for (int index = begin; index <= end; index++) {
			selected.add(input.get(0).getSeriesLabel(index));
		}
		// No rename.
		selectedRename = selected;
		updateSeries();
		return this;
	}

	/**
	 * Selects the specified series from the input table in the order given.
	 * 
	 * @param series
	 *            Labels of series to select in the order to select them.
	 * @return This data table view.
	 */
	public TableViewSeries setSeries(String... series) {
		selected.clear();
		for (String label : series) {
			if (!input.get(0).hasSeries(label)) {
				label = label.trim();
				if (!input.get(0).hasSeries(label)) {
					// System.err.println("No series with label " + label + " in input table, ignoring.");
					continue;
				}
			}
			selected.add(label);
		}
		// No rename.
		selectedRename = selected;
		updateSeries();
		return this;
	}

	/**
	 * Selects series from the input table whose labels match the given "glob"
	 * pattern.
	 * 
	 * @param pattern
	 *            The glob pattern to use to match series labels.
	 * @return This data table view.
	 */
	public TableViewSeries setSeriesGlob(String pattern) {
		String regex = pattern.replaceAll("([^a-zA-z*?0-9])", "\\\\$1");
		regex = regex.replace("?", ".");
		regex = regex.replace("*", ".*");

		setSeriesRE(Pattern.compile(regex));
		return this;
	}

	/**
	 * Selects series from the input table whose labels match the given regular
	 * expression pattern.
	 * 
	 * @param pattern
	 *            The regular expression pattern to use to match series labels.
	 * @return This data table view.
	 */
	public TableViewSeries setSeriesRE(Pattern pattern) {
		setSeriesRE(pattern, null);
		return this;
	}

	/**
	 * Selects series from the input table whose labels match the given regular
	 * expression pattern.
	 * 
	 * @param pattern
	 *            The regular expression pattern to use to match series labels.
	 * @param renamePattern
	 *            The replacement pattern (with $N referring to capture groups
	 *            in 'pattern'). If renamePattern contains '\\oi' or '\\ni' it
	 *            will be replaced by the original or new index of the series
	 *            respectively. An '\\oi' or '\\ni' may be followed by numerals
	 *            to indicate an offset, for example "my\\ni5pf" will give
	 *            strings "my5pf", my6pf", "my7pf" and so on.
	 * @return This data table view.
	 */
	public TableViewSeries setSeriesRE(Pattern pattern, String renamePattern) {
		selected.clear();
		selectedRename = new BMListSet<>();
		for (int oi = 0; oi < input.get(0).getAll().size(); oi++) {
			String label = input.get(0).getSeriesLabels().get(oi);
			Matcher m = pattern.matcher(label);
			if (m.matches()) {
				if (renamePattern != null) {
					String renamePatternRepl = processReplacements(renamePattern, oi, selected.size());
					selectedRename.add(m.replaceAll(renamePatternRepl));
				} else {
					selectedRename.add(label);
				}
				selected.add(label);
			}
		}
		updateSeries();
		return this;
	}
}
