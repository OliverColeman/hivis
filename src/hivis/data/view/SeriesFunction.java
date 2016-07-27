package hivis.data.view;

import java.util.List;

import hivis.common.ListMap;
import hivis.common.ListSet;
import hivis.data.DataSeries;
import hivis.data.DataTable;

/**
 * Interface for functions that produce a DataSeries, optionally derived from input DataSeries.
 * 
 * @author O. J. Coleman
 */
public interface SeriesFunction<O, I> {
	/**
	 * For the given DataSeries return the generated series.
	 * 
	 * @param input The input DataSeries. If the series this function generates are not derived from other series this may be null.
	 */
	public DataSeries<O> apply(List<DataSeries<I>> input);
}
