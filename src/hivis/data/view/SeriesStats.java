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

import java.util.Arrays;
import java.util.List;

import hivis.common.BMListSet;
import hivis.common.HV;
import hivis.common.LSListMap;
import hivis.common.ListMap;
import hivis.common.ListSet;
import hivis.data.DataSeries;
import hivis.data.DataSeriesReal;
import hivis.data.DataTable;
import hivis.data.DataTableDefault;

/**
 * Function to generate some basic statistics for a data table. 
 * The series generated are "min", "max", "mean", "median", "std. dev.".
 * The statistics are generated "across" the series in the table, not 
 * for/within each series, thus this function always produces 5 series.
 * Non-numeric data series are ignored.
 * 
 * @author O. J. Coleman
 */
public class SeriesStats implements TableFunction {
	ListSet<String> labels = new BMListSet<String>(new String[] {"min", "max", "mean", "median", "std. dev."});
	ListMap<String, DataSeriesReal> series = new LSListMap<>();
	
	public SeriesStats() {
		for (String label : labels) {
			series.put(label, new DataSeriesReal());
		}
	}
	
	@Override
	public void getSeries(List<DataTable> inputTables, ListMap<String, DataSeries<?>> output) {
		if (inputTables.size() != 1) {
			throw new IllegalArgumentException("SeriesStats may only be applied to a single input table.");
		}
		DataTable input = inputTables.get(0);
		
		int seriesCount = input.seriesCount();
		int numericSeriesCount = 0;
		int length = input.length();
		
		double[][] data = new double[length][seriesCount];
		boolean[] numeric = new boolean[seriesCount];
		
		for (int si = 0; si < seriesCount; si++) {
			DataSeries<?> s = input.get(si);
			
			if (s.isNumeric()) {
				numeric[si] = true;
				numericSeriesCount++;
				
				double[] d = (s instanceof DataSeriesReal) ? ((DataSeriesReal) s).getDataRef() : s.asDoubleArray(new double[length]);
				for (int ri = 0; ri < length; ri++) {
					data[ri][si] = d[ri];
				}
			}
		}
		
		if (numericSeriesCount == 0) {
			throw new IllegalArgumentException("Cannot create SeriesStats for table containing no numeric series.");
		}
		
		for (int si = 0; si < series.size(); si++) {
			DataSeriesReal s = series.get(si).getValue();
			
			if (s.length() != length) {
				s.resize(length);
			}
		}
		
		double[] statsMin = new double[length];
		double[] statsMax = new double[length];
		double[] statsMean = new double[length];
		double[] statsMedian = new double[length];
		double[] statsStdDev = new double[length];
		
		for (int i = 0; i < length; i++) {
			double min = Double.MAX_VALUE;
			double max = -Double.MAX_VALUE;
			double sum = 0;
			double median = 0;
			
			Arrays.sort(data[i]);
			
			for (int s = 0, ns = 0; s < seriesCount; s++) {
				if (numeric[s]) {
					double val = data[i][s];
					min = Math.min(min, val);
					max = Math.max(max, val);
					sum += val;
					if (ns == numericSeriesCount / 2) {
						median = val;
					}
					ns++;
				}
			}
			
			double mean = sum / numericSeriesCount;
			
			double sd = 0;
			for (int s = 0; s < seriesCount; s++) {
				if (numeric[s]) {
					sd += Math.pow(data[i][s] - mean, 2);
				}
			}
			sd = Math.sqrt(sd / numericSeriesCount);
			
			statsMin[i] = min;
			statsMax[i] = max;
			statsMean[i] = mean;
			statsMedian[i] = median;
			statsStdDev[i] = sd;
		}
		
		series.get("min").setData(statsMin);
		series.get("max").setData(statsMax);
		series.get("mean").setData(statsMean);
		series.get("median").setData(statsMedian);
		series.get("std. dev.").setData(statsStdDev);
		
		output.putAll(series);
	}
	
	
	public static void main(String[] args) {
		for (int sd = 1; sd <= 1000000; sd *= 10) {
			DataTable table = new DataTableDefault();
			for (int s = 0; s < 100000; s++) {
				table.addSeries(""+s, HV.randomNormalSeries(5, 0, sd));
			}
	
			DataTable stats = table.apply(new SeriesStats(), false);
			System.out.println(stats);
		}
	}
}
