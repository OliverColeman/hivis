# HiVis 2 is here! 

We've made a number of changes to make HiVis even simpler to use, and added some exciting new functionality. We've minimised changes to the existing API so that your existing code should continue to work.


# Working With Data

## Generics

For most use cases we've removed the need to explicitly specify the type of data stored by a Data set via [Java generics](https://docs.oracle.com/javase/tutorial/java/generics/types.html). So instead of doing something like:
```java
    DataSeries<Double> mySeries = HV.newSeries(1.1, 2.2, 3.3).asDouble();
    double hi = mySeries.get(index);
```
You could do:
```java
    DataSeries mySeries = HV.newSeries(1.1, 2.2, 3.3);
    double hi = mySeries.getDouble(index);
```
You can still explicitly specify the type of data for most types of Data set via generics if desired.

## New DataValue data type

We've added a new data type called `DataValue`. This type stores a single data item or value. It's utility comes from the fact that it's a fully-fledged `DataSet` with all the attendent functionality (for advanced developers, it represents an observable object, for which you may monitor for changes to the data it represents). Thus the value it represents may be based on other data sources - we refer to this as a ___View___ of the underlying data source - such as a DataSeries. When the underlying data source(s) change the value represented by the DataValue will be recalculated automatically. This comes in especially handy when the data in, say, a DataSeries changes and you need an up-to-date value based on that series to pipe into another DataSet or data consumer. More on this, and examples, in the _Arithmetic operations_ section below.

A DataValue may represent numeric values or any other type of object. For DataValues representing numeric values there are methods defined on the DataValue class for performing the basic arithmetic operations _add_, _subtract_, _multiply_, and _divide_. See the _Arithmetic operations_ section below for more information.

If a DataValue represents a numeric type then it's value can be obtained as a _primitive_ java numeric type using methods such as `getFloat()` and `getInt()`.

You can see [examples of working with DataValues](https://github.com/OliverColeman/hivis/blob/latest/src/hivis/example/E1_Values.java).

## New DataMap data type

`DataMap`s represent a mapping from keys to values. Keys are always unique with respect to each other, values may be duplicated. 

The keys and values may be any type of Object, however typically a key will be "simple", such as numbers, strings, and dates, but values may be simple or more complex, for example other data structures such as DataSeries. Note that a DataTable is a specialised kind of DataMap where the keys (labels) are Strings and the values are DataSeries.

At the moment DataMaps are primarily used to support the grouping functions over DataSeries and DataTables (more info on this below), so do not have much in the way of built-in functionality.

Here are some [examples of working with DataMaps](https://github.com/OliverColeman/hivis/blob/latest/src/hivis/example/E4_Maps.java).

## Statistical operations on DataSeries

As well as the `min()` and `max()` methods, there are several new statistical methods for DataSeries representing numerical values: `mean()` (arithmetic mean, average), `sum()` (sum of all values in the DataSeries), `product()` (all values in the DataSeries multiplied together), `variance()` and `stdDev()` (standard deviation).

All of these statistical operations return the result as a `DataValue`, which will be automatically updated when the DataSeries it's derived from changes.

Note that the `minValue()` and `maxValue()` methods have been deprecated in favour of `min()` and `max()`, so you should update your code to use these new methods. `minValue()` and `maxValue()` will be removed in a future version of HiVis.

## Arithmetic operations

Arithmetic methods on DataSeries and DataValues (now) create new DataSeries or DataValues that will accurately represent the results of the arithmetic function given the type of data represented by the operands as well as the type of arithmetic function being performed (in the past the data type of a calculated series was always the same as that represented by the source series). For example: 
* The `divide` operation will always return a series or value representing floating-point (double) values.
* For `add`, `subtract` and `multiply` the calculated series or value will represent integers if both the series/value and the second operand are integers, or floating-point otherwise (taking into account the various possible sizes or precisions of the numeric type).
* The methods to calculate statistical values over a series return a numeric type based on the numeric type of the series as well as the statistical operation. For example the `min()` and `max()` methods will be of the same numeric type as the series, however the `variance()` and `stdDev()` methods return floating-point (double) values.

The end result of these changes is that you can now be blissfully ignorant of the numeric type a series or value represents when performing arithmetic operations on or with it to derive new series or values. HiVis will automatically determine the appropriate numeric type, and, as in the past, will do it's best to accommodate the interchange of different numeric types when necessary (for example happily appending a floating-point value to an integer DataSeries if that floating-point value happens to represent an integer). In the rare instance that a calculated value or series might result in lost precision - for example, dealing with calculations involving 'long' and 'double' numbers - HiVis will emit a warning.

DataValues can be provided as an argument, or operand, to arithmetic methods (or classes used to derive DataSeries or DataValues based on a custom operation). The resulting DataSeries or DataValues will be automatically updated if the underlying DataValue is changed. This makes it possible to derive a _View_ of an original data set from a chain of operations involving series and values. A simple example is recreating the `toUnitRange()` method of a DataSeries or calculating the dot product of two vectors:
```java
DataSeries myData = HV.newIntegerSeries(1, 1, 2, 3, 5, 8);
DataSeries myDataUnitRange = myData.subtract(myData.min()).divide(myData.max().subtract(myData.min()));

DataSeries vector1 = HV.randomUniformSeries(3, 0, 1);
DataSeries vector2 = HV.randomUniformSeries(3, 0, 1);
DataSeries dotProduct = vector1.multiply(vector2).sum();
```
There are several things to note about the above code:
* The source data series, `myData`, represents integers, however `myDataUnitRange` is a series representing floating-point (double) numbers.
* If the `myData` series changes the DataValues provided by the `min()` and `max()` methods will be updated automatically, and the `myDataUnitRange` series accordingly. Similarly if `vector1` or `vector2` change, the calculated DataSeries returned by the `multiply` method will be updated automatically, and then the `sum()` method as a result of that.
* we are calling `min()` twice, however the  minimum is not actually calculated twice as the same _min_ DataValue is cached and reused by subsequent calls to the `min()` method. The same is true for the other statistical methods - and even within the methods where possible, eg `mean()` will create a cache of the _sum_ DataValue as well as the _mean_ DataValue.

You can see more [examples of working with DataSeries](https://github.com/OliverColeman/hivis/blob/latest/src/hivis/example/E2_Series.java).

## Sorting and grouping series and tables

There is now built-in support for obtaining Views of DataSeries and DataTables that sort the values/rows, or group the values/rows. A quick synopsis is provided below, but you can see [more in depth examples and information about sorting and grouping DataSeries](https://github.com/OliverColeman/hivis/blob/latest/src/hivis/example/E2_Series.java) and [more in depth examples and information about sorting and grouping DataTables](https://github.com/OliverColeman/hivis/blob/latest/src/hivis/example/E3_Tables.java).

### Sorting

```java
    // Sort a series according to the "natural" ordering of the values, in ascending order.
    DataSeries sortedSeriesNatural = mySeries.sort();
    // Sort a series with a custom Comparator (see examples linked above for more info on Comparator usage).
    DataSeries sortedSeriesCustom = mySeries.sort(new Comparator() { ...  } );

    // Sort the rows of a table according to the "natural" ordering of the values in the "name" series in the table.
    DataTable sortedTableNaturalName = myTable.sort("name");
    // Sort the rows of a table according to a custom Comparator (see examples linked above for more info on Comparator usage).
    DataTable sortedTableCustom = myTable.sort(new Comparator<DataRow>() { ... });
```

### Grouping Series

A grouping over a series is represented as a DataMap (see above). The values of the DataMap are DataSeries containing the values for a group. 

We can group by the values' own equality (values that are equal are grouped together), in which case the key for each group is a value such that key.equals(v) for all values in the group:
```java
    DataMap randomIntsGrouped = randomInts.group();
```
Will produce a grouping something like (depending on the actual values in the series):
```
    1 => DataSeries (1) [  1 ],
    2 => DataSeries (3) [  2 ; 2 ; 2 ],
    3 => DataSeries (2) [  3 ; 3 ],
    ...
```
The numbers on the left are the keys of the map, and the DataSeries are the groups (showing the values they contain). If we called `group()` on a series containing Strings or dates then the keys would be Strings or dates.

We can also group using a custom "key function", in which case the key for each group is a value such that `key.equals(keyFunction(v))` for all values `v` in the group:
```java
    DataMap randomIntsGroupedCustom = randomInts.group(new Function<[SeriesType], [TypeOfTheKey]>() { });
```
The output of the key function can be any kind of value. 

The ordering of the values in the groups matches the ordering of those values in the original series. Note that because the grouping is a View it will be updated when the source series changes.

### Grouping Tables

A grouping over a table is represented as a GroupedTable (an extension of DataMap). The values of the DataMap are DataTables containing the rows for a group. 

We can group by the values in a series, in which case the key for each group is a value such that `key.equals(v)` for all values `v` in the grouping series. For example:
```java
    // Make a table of peoples' names and their ages.
    DataSeries name = HV.newSeries("Genevieve", "Charlotte", "Roberto", "Stefan", "Franklin", "Amelia");
    DataSeries age = HV.newSeries(7, 8, 7, 6, 8, 7);
    DataTable nameAge = HV.newTable().addSeries("name", name).addSeries("age", age);
    // Group by "age" series.
    DataMap ageGrouping = nameAge.group("age");
    System.out.println(ageGrouping);
```
Will output:
```
DataMap (3) [ 
    6 => |   name | age | 
         ----------------
         | Stefan |   6 | 
         ----------------,

    7 => |      name | age | 
         -------------------
         | Genevieve |   7 | 
         |   Roberto |   7 | 
         |    Amelia |   7 | 
         -------------------,

    8 => |      name | age | 
         -------------------
         | Charlotte |   8 | 
         |  Franklin |   8 | 
         -------------------
 ]

```

Or we can group using a custom "key function" which, given a row of the table, produces a key representing the group that row belongs to.
Then the key for each group is a value such that `key.equals(keyFunction(row))` for all table rows in the group:
```java
    DataMap groupedTableByNameCustom = myTable.group(new Function<DataRow, [TypeOfTheKey]>() { ... });
```
One cool thing to do with GroupedTables is aggregate them. You can try an example of this in the second tutorial at examples/tutorials/T02_pie, from which a snippet:
```java
    DataTable rawData = HV.loadSpreadSheet(HV.loadSSConfig().sourceFile("iris.csv").columnIndex(1));

    // Group on the species.
    GroupedTable groupedData = rawData.group("Species");

    // Get a table in which the table for each group/sub-table is aggregated into a 
    // single row using an aggregation function (in this case the mean of the series 
    // in the sub-table, or the first value of the series if it's not numeric). 
    meanValuesBySpecies = groupedData.aggregateMean();
```
You can create custom aggregation functions as well, see the API docs for GroupedTable.

# Loading data from spreadsheets

## CSV support

Data may now be loaded from CSV (Comma Separated Values) and similar text files. You can specify the separator and quote characters if necessary. See below for configuration options.

## Configuring how data is read

There are now many more configuration options available when loading data from spreadsheets. To facilitate this a new configuration object has been introduced. An example usage:

```java
    DataTable data = HV.loadSpreadSheet(
        HV.loadSSConfig().sourceFile("myspreadsheet.csv").headerRowIndex(2).rowIndex(3).columnIndex(2)
    );
```

For all configuration options see [here](https://olivercoleman.github.io/hivis/reference/hivis/data/reader/SpreadSheetReader.Config.html).

You can call as many of the configuration option methods as necessary, in any order. Note that the various 'index' options start counting at 0, not 1.

As part of this change the method `HV.loadSpreadSheet(File file, int sheet, int headerRow, int firstDataRow, int firstDataColumn)` has been deprecated and will be removed in a future release.

## Date/time handling

Date handling has been upgraded to the new Java 8 date/time API (which overcomes a lot of the issues with the old Java Date API). For columns formatted as dates/times (Excel), or text that looks like an ISO-like date or time (CSV/text), dates are now created as [TemporalAccessor](https://docs.oracle.com/javase/8/docs/api/java/time/temporal/TemporalAccessor.html) objects. This means that dates and times that include time zones and/or offsets are now supported. If you use some other date/time format in your CSV-like file then you can specify the format, see the SpreadSheetReader.Config options linked above for more information. You can also force use of the old date API with the `useDeprecatedDates` option. Some example ISO-like date/time formats that work out-of-the-box:
* 2011-12-03
* 20111203
* 10:15:30
* 10:15:30+01:00
* 2011-12-03 10:15:30.123
* 2011-12-03T10:15:30+01:00
* 2011-12-03T10:15:30[Europe/Paris]
* 2011-12-03T10:15:30+01:00[Europe/Paris]
* 2011-12-03 10:15:30Europe/London
* 2011-12-03 10:15:30Z[Europe/London]

