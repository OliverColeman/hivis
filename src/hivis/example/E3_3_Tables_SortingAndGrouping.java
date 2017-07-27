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

package hivis.example;


import java.util.Comparator;

import hivis.common.HV;
import hivis.data.DataMap;
import hivis.data.DataRow;
import hivis.data.DataSeries;
import hivis.data.DataTable;
import hivis.data.view.Function;
import hivis.data.view.GroupedTable;
import hivis.data.view.RowFilter;
import hivis.data.view.SeriesFunction;

/**
 * Examples of sorting and grouping over {@link DataTable}s.
 * 
 * @author O. J. Coleman
 */
public class E3_3_Tables_SortingAndGrouping {
	public static void main(String[] args) {
		// Get a subset of the MT Cars dataset. We get a copy of 
		// the row range View so we can modify the values later.
		DataTable mtCars = HV.mtCars().selectRowRange(0, 9).copy();
		System.out.println(mtCars);
		
		// SORTING.
		
		// We can get a View of a table in which the rows are sorted according to the values in one of the series.
		// For example sorting the cars by their "mpg" values:
		DataTable mtCarsSortedByMPG = mtCars.sort("mpg");
		System.out.println("\nmtCarsSortedByMPG = mtCars.sort(\"mpg\") => \n" + mtCarsSortedByMPG);
		// Note: the index of a series may also be used to specify the sorting series.
		
		
		// Or we can sort using a custom Comparator (see https://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html)
		// The Comparator compares the rows of the table to determine their ordering. Here we sort the cars
		// first by number of cylinders and then by their horsepower.
		DataTable mtCarsSortedByCylThenHP = mtCars.sort(new Comparator<DataRow>() {
			public int compare(DataRow row1, DataRow row2) {
				int row1Cyl = row1.getInt("cyl");
				int row2Cyl = row2.getInt("cyl");
				
				if (row1Cyl < row2Cyl) return -1;
				if (row1Cyl > row2Cyl) return 1;
				
				// Same number of cylinders, compare by hp.
				double row1HP = row1.getDouble("hp");
				double row2HP = row2.getDouble("hp");
				return Double.compare(row1HP, row2HP); // Use utility method provided by Java standard library.
				
			}
		});
		System.out.println("\nmtCars sorted by \"cyl\" then \"hp\" => \n" + mtCarsSortedByCylThenHP);
		
		
		// GROUPING.
		// We can obtain "grouped" views over a table. A grouping over a table is represented
		// as a DataMap (which represents a mapping from keys to values, see the Maps example). 
		// The values of the DataMap are DataTables containing the rows belonging to that group.
		
		// We can group by the values in a series, rows that have equal values in this series 
		// will be grouped together. The key for each group is a value such that 
		// key.equals(seriesValue) for all values in the grouping series:
		GroupedTable mtCarsGroupedByCyl = mtCars.group("cyl");
		System.out.println("\nmtCars grouped by \"cyl\" series => \n" + mtCarsGroupedByCyl);
		// Note: the index of a series may also be used to specify the grouping series.
		
		// Or we can group using a custom "key function" which takes a row of the 
		// table and produces a key representing the group that row belongs to).
		// The key for each group is then a value such that 
		// key.equals(keyFunction(row)) for all table rows in the group:
		GroupedTable mtCarsGroupedCustom = mtCars.group(new Function<DataRow, String>() {
			public String apply(DataRow row) {
				// Group by first word of the model series.
				String[] words = row.getString(0).split(" ", 2);
				return words[0];
			}
		});
		System.out.println("\nmtCars grouped by first word in \"model\" series => \n" + mtCarsGroupedCustom);
		
		// We can get a view of a table group in which the table for each
		// group is aggregated into a single row of the returned table view.
		// Note that the built-in statistical aggregation functions return 
		// the first value of non-numeric series. Custom aggregation functions 
		// may be specified with GroupedTable.aggregate(AggregationFunction).
		DataTable mtCarsGroupedByCylAggregated = mtCarsGroupedByCyl.aggregateMean();
		System.out.println("\nmtCarsGroupedByCyl aggregated (mean of each series) => \n" + mtCarsGroupedByCylAggregated);
		
		
		// Changes in the source table are reflected in the sorted and grouped views:
		for (int row = 0; row < mtCars.length(); row++) {
			int cyl = mtCars.get("cyl").getInt(row);
			if (cyl == 6) {
				mtCars.get("cyl").set(row, cyl * 2);
			}
		}
		
		System.out.println("\nmtCarsSortedByMPG (reflecting conversion of 6 cylinder to 12 cylinder, woah) => \n" + mtCarsSortedByMPG);
		System.out.println("\nmtCarsSortedByCylThenHP (reflecting conversion of 6 cylinder to 12 cylinder, woah) => \n" + mtCarsSortedByCylThenHP);
		
		System.out.println("\nmtCars grouped by \"cyl\" series (reflecting conversion of 6 cylinder to 12 cylinder, woah) => \n" + mtCarsGroupedByCyl);
		System.out.println("\nmtCars grouped by first word in \"model\" series (reflecting conversion of 6 cylinder to 12 cylinder, woah) => \n" + mtCarsGroupedCustom);
		
		System.out.println("\nmtCarsGroupedByCyl aggregated (reflecting conversion of 6 cylinder to 12 cylinder, woah) => \n" + mtCarsGroupedByCylAggregated);
		
	}
}
