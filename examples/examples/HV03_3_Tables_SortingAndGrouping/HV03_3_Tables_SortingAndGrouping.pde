import hivis.common.*;
import hivis.data.*;
import hivis.data.view.*;
import java.util.*;

// Examples of sorting and grouping over HiVis DataTables.

void setup() {
  // Get a subset of the MT Cars dataset. We get a copy of 
  // the row range View so we can modify the values later. 
  DataTable mtCars = HV.mtCars().selectRowRange(0, 9).copy();
  println(mtCars);
  
  // SORTING.
  
  // We can get a View of a table in which the rows are sorted according to the values in one of the series.
  // For example sorting the cars by their "mpg" values:
  DataTable mtCarsSortedByMPG = mtCars.sort("mpg");
  println("\nmtCarsSortedByMPG = mtCars.sort(\"mpg\") => \n" + mtCarsSortedByMPG);
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
  println("\nmtCars sorted by \"cyl\" then \"hp\" => \n" + mtCarsSortedByCylThenHP);
  
  
  // GROUPING.
  // We can obtain "grouped" views over a table. A grouping over a table is represented
  // as a DataMap (which represents a mapping from keys to values, see the Maps example). 
  // The values of the DataMap are DataTables containing the rows belonging to that group.
  
  // We can group by the values in a series, rows that have equal values in this series 
  // will be grouped together. The key for each group is a value such that 
  // key.equals(seriesValue) for all values in the grouping series:
  DataMap mtCarsGroupedByCyl = mtCars.group("cyl");
  println("\nmtCars grouped by \"cyl\" series => \n" + mtCarsGroupedByCyl);
  // Note: the index of a series may also be used to specify the grouping series.
  
  // Or we can group using a custom "key function" which takes a row of the 
  // table and produces a key representing the group that row belongs to).
  // The key for each group is then a value such that 
  // key.equals(keyFunction(row)) for all table rows in the group:
  DataMap mtCarsGroupedCustom = mtCars.group(new Function<DataRow, String>() {
    public String apply(DataRow row) {
      // Group by first word of the model series.
      String[] words = row.getString(0).split(" ", 2);
      return words[0];
    }
  });
  println("\nmtCars grouped by first word in \"model\" series => \n" + mtCarsGroupedCustom);
  
  // We can get a view of a table group in which the table for each
  // group is aggregated into a single row of the returned table view.
  // Note that the built-in statistical aggregation functions return 
  // the first value of non-numeric series. Custom aggregation functions 
  // may be specified with GroupedTable.aggregate(AggregationFunction).
  DataTable mtCarsGroupedByCylAggregated = mtCarsGroupedByCyl.aggregateMean();
  println("\nmtCarsGroupedByCyl aggregated (mean of each series) => \n" + mtCarsGroupedByCylAggregated);
  
  
  // Changes in the source table are reflected in the sorted and grouped views:
  for (int row = 0; row < mtCars.length(); row++) {
    int cyl = mtCars.get("cyl").getInt(row);
    if (cyl == 6) {
      mtCars.get("cyl").set(row, cyl * 2);
    }
  }
  
  println("\nmtCarsSortedByMPG (reflecting conversion of 6 cylinder to 12 cylinder, woah) => \n" + mtCarsSortedByMPG);
  println("\nmtCarsSortedByCylThenHP (reflecting conversion of 6 cylinder to 12 cylinder, woah) => \n" + mtCarsSortedByCylThenHP);
  
  println("\nmtCars grouped by \"cyl\" series (reflecting conversion of 6 cylinder to 12 cylinder, woah) => \n" + mtCarsGroupedByCyl);
  println("\nmtCars grouped by first word in \"model\" series (reflecting conversion of 6 cylinder to 12 cylinder, woah) => \n" + mtCarsGroupedCustom);
  
  println("\nmtCarsGroupedByCyl aggregated (reflecting conversion of 6 cylinder to 12 cylinder, woah) => \n" + mtCarsGroupedByCylAggregated);
}


void draw() {
  
}