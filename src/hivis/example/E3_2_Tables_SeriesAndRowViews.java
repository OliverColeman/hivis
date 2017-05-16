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
import hivis.data.view.RowFilter;
import hivis.data.view.SeriesFunction;

/**
 * Examples of working with Views over the rows and series of a {@link DataTable}s.
 * 
 * @author O. J. Coleman
 */
public class E3_2_Tables_SeriesAndRowViews {
	public static void main(String[] args) {
		// We can get Views of the series and rows in a table in various ways.

		// First let's get a nice table with several kinds of data in it. 
		// HV.mtCars() provides a DataTable containing data for ten variables over 32 automobiles extracted from the 1974 Motor Trend US magazine.
		// (Seems like a sensible de facto standard data set to use in this modern day and age.)
		DataTable mtCars = HV.mtCars();
		
		// ROW VIEWS.
		
		// Create a View of the cars table containing only row 5 to 29, inclusive.
		DataTable mtRowsRange = mtCars.selectRowRange(5, 29);
		
		// Then get a View of that table containing the rows from the specified 
		// indices, in the specified order.
		DataTable mtRowsRangeSelect = mtRowsRange.selectRows(24, 22, 20, 19, 18, 0, 1, 2, 3, 5, 7);
		
		// Finally get a View of that table with some rows filtered out based on custom criteria.
		DataTable mtRowsRangeSelectFilter = mtRowsRangeSelect.selectRows(new RowFilter() {
			public boolean excludeRow(DataTable input, int index) {
				// Exclude cars with 6 cylinders whose horsepower is less than 120.
				return input.get("cyl").getInt(index) == 6 && input.get("hp").getInt(index) < 120;
			}
		});
		System.out.println("mtRowsRangeSelectFilter = mtCars\n\t.selectRowRange(5, 29)\n\t.selectRows(24, 22, 20, 19, 18, 17, 16, 15, 0, 1, 2, 3, 4, 6 , 8, 10)\n\t.selectRows(new RowFilter() { [exclude cars with 6 cylinders whose horsepower is less than 120] })\n\t=>\n" + mtRowsRangeSelectFilter);
		
		
		// SERIES VIEWS.
		
		// Get a View of the above table containing only the first 7 series.
		DataTable seriesFirst7 = mtRowsRangeSelectFilter.selectSeriesRange(0, 6);
		
		// Get a View of the above table containing 6 of the series in a new order. Note: the indices of the series may be used instead.
		DataTable seriesReordered = seriesFirst7.selectSeries("model", "hp", "disp", "wt", "mpg", "cyl");
		
		// Now get a View that has the series relabelled (from model, mpg, cyl, disp, hp).
		DataTable seriesRelabelled = seriesReordered.relabelSeries("Model", "# Steeds Equivalent", "Displacement (cu.in.)", "Weight (x1000 lbs.)", "Miles per Gallon", "# Cylinders");
		
		// Then add a prefix and postfix to the series labels.
		// If the prefix or postfix Strings contain '\\oi' this will be replaced by the index of the series. 
		// An '\\oi' may be followed by numerals to indicate an offset.
		DataTable seriesRelabelledPrePost = seriesRelabelled.relabelSeriesPP("mtCars ", " \\oi1");
		System.out.println("\nseriesRelabelledPrePost = mtRowsRangeSelectFilter\n\t.selectSeriesRange(0, 7)\n\t.selectSeries(\"model\", \"hp\", \"disp\", \"wt\", \"mpg\", \"cyl\")\n\t.relabelSeries(\"Model\", \"# Steeds Equivalent\", \"Displacement (cu.in.)\", \"Weight (x1000 lbs.)\", \"Miles per Gallon\", \"# Cylinders\")\n\t.relabelSeriesPP(\"mtCars \", \" \\\\oi1\")\n\t=>\n" + seriesRelabelledPrePost);
		
		
		// SOME MORE VIEWS.
		
		// First let's make another table with the same length as the final table View created above.
		int length = seriesRelabelledPrePost.length();
		DataTable myRandomTable = HV.newTable().addSeries("my ints 1", HV.randomIntegerSeries(length, 100, 200)).addSeries("my reals 2", HV.randomUniformSeries(length, -1.234, -5.678));
		
		// Get a View of a table containing the series from two tables.
		DataTable twoTablesBecomeOne = seriesRelabelledPrePost.combine(myRandomTable);
		
		// Get a View of the twoTablesBecomeOne table containing some custom car data.
		// First create the custom car data.
		DataTable customCars = HV.newTable();
		// For each series label in our combined table...
		for (String label : twoTablesBecomeOne.getSeriesLabels()) {
			// If the series is numeric...
			if (twoTablesBecomeOne.get(label).isNumeric()) {
				customCars.addSeries(label, HV.randomIntegerSeries(2, 0, 10));
			}
			else {
				customCars.addSeries(label, HV.newSeries("my car 1", "my car 2"));
			}
		}
		// Then create the View containing the original car data and the custom car data.
		DataTable allCars = twoTablesBecomeOne.append(customCars);
		
		
		// We can get a view of a table that contains each series in the original table
		// with a function applied to it. For example we can get each series in a table
		// converted to unit range with:
		DataTable unitRange = allCars.apply(new SeriesFunction() {
			public DataSeries apply(DataSeries input) {
				// If series is numeric then convert it to unit range.
				return input.isNumeric() ? input.toUnitRange() : input;
			}
		});
		System.out.println("\nunitRange = seriesRelabelledPrePost.combine(myRandomTable).append(customCars).apply([unit range function]) => \n" + unitRange);
		// Note that there is a built-in method for tables called toUnitRange(), 
		// so the above could also be achieved with allCars.toUnitRange().  
		
		
		// Tables can be transposed. If the table contains a "row key" series containing 
		// no duplicate values it will be used for the series labels in the transposed table.
		// By default the row key series for a table is set to the first series containing 
		// String values that are unique, in our case this will be the "model" series.
		// The "row key" can also be set manually.
		DataTable transposed = unitRange.transpose();
		
		System.out.println("\ntransposed = unitRange.transpose() =>\n" + transposed);
		
		
		// Changes to the values in the original series/table are reflected in the Views.
		// If we change a value in the original mtCars table the changes will "bubble up"
		// the chain of Views we've created above:
		mtCars.get("cyl").set(29, 12); // Retrofit the Ferrari Dino with a 12 cylinder engine. Oh boy.
		myRandomTable.get(0).set(9, 999999); // Change a value in the "my ints" series in myRandomTable.
		// If we look in any of the Views we created above we should see the change we made to the original "source" tables:
		System.out.println("\nseriesRelabelled table reflecting modified values:\n" + seriesRelabelled);
		System.out.println("\nunitRange table reflecting modified values:\n" + unitRange);
		System.out.println("\ntransposed table reflecting modified values:\n" + transposed);
	}
}
