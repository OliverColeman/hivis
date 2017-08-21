---
layout: post
title: HiVis
---

# HiVis

HiVis is a Java library for importing, manipulating, shaping, processing and otherwise working with various kinds of data. It is designed to be simple to use for novice programmers while being highly flexible and extensible. It is also designed with the programming environment [Processing](http://www.processing.org) in mind, but may be used outside of Processing. 

HiVis is developed as part of a collaboration between the School of Medical Sciences at the University of Sydney and the Interactive Media Lab in Art and Design at the University of New South Wales.

## Version 2 is here!

Check out the [upgrade page](https://olivercoleman.github.io/hivis/upgrade2) for info on what's new.


## HiVis in 30 seconds

Typically raw data is loaded from a data source, for example a spreadsheet, and then ***views*** of that data are derived from it via transformation operations or functions. Some example views are mathematical and statistical operations, filtering and rearranging elements of the data, sorting and grouping data, or applying a function to each element of the data. Custom views can easily be created too. 

If the original data source changes then the derived views are updated in real-time. Thus HiVis represents a flexible event-driven data processing framework, with which complex data processing pipelines may be constructed.

For example:
```java

    // Load some data from a spread sheet.
    DataTable myTable = HV.loadSpreadSheet(
        HV.loadSSConfig().sourceFile("/path/to/data/myData.xlsx")
    );

    // Get a couple of columns - called series - of data from the table.
    // You can use the column index or header label to refer to it.
    DataSeries mySeries1 = myTable.get(0); //        = [ 1,    2,    5,    2,    5 ]
    DataSeries mySeries2 = myTable.get("Second"); // = [ 0.1,  2.3,  4.5,  6.7,  8.9 ]

    // Get a view of the raw data that sums the corresponding values from our series.
    DataSeries summed = mySeries1.add(mySeries2); // = [ 1.1,  4.3,  9.5,  8.7,  13.9 ]

    // Get a view of the sum that represents the mean, or average, of the values.
    DataValue mean = summed.mean(); //               = 7.5

    // Get a view of the second series with the mean, calculated above, subtracted.
    DataSeries result = mySeries2.subtract(mean); // = [-7.4, -5.2, -3.0, -0.8,  1.4 ]

    // Get the result as a simple array of floats (which Processing likes).
    float[] resultAsFloats = result.asFloatArray();
    
    // Group the original table on the first series/column.
    GroupedTable grouped = myTable.group(0);
    // = GroupedTable (3) [ 
    //     1 => | First |   Second | 
    //              --------------------
    //          |     1 |  0.10000 | 
    //          --------------------,
    //  
    //     2 => | First |   Second | 
    //          --------------------
    //          |     2 |  2.30000 | 
    //          |     2 |  6.70000 | 
    //          --------------------,
    //     
    //     5 => | First |   Second | 
    //          --------------------
    //          |     5 |  4.50000 | 
    //          |     5 |  8.90000 | 
    //          --------------------
    //   ]
    
    // Iterate over the groups. We tell HiVis to interpret the group keys as integers.
    for (int groupKey : grouped.keys().asInt()) {
    	DataTable group = grouped.get(groupKey);
        
        // Iterate over the rows in this group.
        for (DataRow row : group) {
            // Get the value of the Second column for this row.
            double secondValue = row.getDouble("Second");
        }
    }
    
```

There are some cool things to note in this example:
- Any number of operations and resulting views may be chained, allowing the creation of complex data processing pipelines.
- If the original raw data source - in this case a spreadsheet file - changes then these changes will be reflected in the derived views. Of course, if your data is static then HiVis simply represents a flexible and user-friendly data processing library for Java.
- The First and Second series represent integers and decimal values, and we didn't have to care. HiVis will do its best to accommodate the interchange of data of different types when it makes sense to do so (emitting warnings if the numerical accuracy of the result cannot be guaranteed). 
- Data sets such as series, tables and groups can easily be iterated over using standard Java/Processing syntax, or converted to arrays to interface with other libraries or sotware.
- For advanced Java developers: we didn't use Java generics anywhere. However most Dataset types (eg DataSeries, DataValue, DataMap, GroupedTable) are generic, so you can explicitly specify the type of data they store via generics if desired.


## Documentation

### Examples

For data manipulation see the examples (akin to tutorials) in [src/hivis/example](https://github.com/OliverColeman/hivis/tree/latest/src/hivis/example).

For data import and interactive visualisation see the Processing sketches in [examples](https://github.com/OliverColeman/hivis/tree/latest/examples/examples). The first six of these are identical to the data manipulation examples but formatted as Processing sketches.

### API Reference

The API reference is [here](https://olivercoleman.github.io/hivis/reference/).


## Installation

### For Processing

The latest version can be installed via the Contribution Manager in Processing (Sketch > Import Library > Add Library).

### Via a build tool

Instructions for Maven and Gradle coming soon.


## Reporting Problems and Issues

If you run into problems or issues, please use the GitHub project [issue tracker](https://github.com/OliverColeman/hivis/issues).


## Credits

Concept: Philip Poronnik (University of Sydney) and Oliver Bown (University of New South Wales).  
Development: Oliver Coleman, Phillip Gough, Narayan Sankaran.

This library was developed in collaboration between the School of Medical Sciences at the University of Sydney and the Interactive Media Lab in Art and Design at the University of New South Wales. The library was funded  by a University of Sydney Large Education Innovation grant. 
