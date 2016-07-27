package hivis.data.view;

import java.util.List;

import hivis.common.ListMap;
import hivis.common.ListSet;
import hivis.data.DataSeries;
import hivis.data.DataTable;

/**
 * Interface for functions that produce a set of DataSeries derived from an input DataTable.
 * 
 * @author O. J. Coleman
 */
public interface TableFunction {
	/**
	 * For the given DataTable produce the series this function generates and add them to the given output list.
	 * 
	 * @param input The input DataTable(s). If the series this function generates are not derived from an input table this may be null.
	 * @param output The ListMap to add the output DataSeries to, key is a label suggestion. One or more DataSeries may be added.
	 */
	public void getSeries(List<DataTable> input, ListMap<String, DataSeries<?>> outputSeries);
}
