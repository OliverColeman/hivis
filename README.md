# HiVis

HiVis is a Java library for importing, manipulating and working with various kinds of data. 
It was designed with the programming environment [Processing](http://www.processing.org) in mind, but may be used outside of Processing.


## Overview / Features

A collection of data is a DataSet. Built-in DataSet types are:
- DataSeries (storing sequential data elements that may be numeric, Date, String or any other kind of object);
- DataTable, which combine a set of Series with associated labels;
- Graphs (coming soon).

DataSets may be manipulated using various filters and transformations. New filters and transformations may be created easily.

Typically when a DataSet is manipulated a "View" of the DataSet is created, leaving the original DataSet unchanged.

When the underlying DataSet changes the View of it will also be updated, via a tightly integrated event framework.

Data may be imported from external sources, for example a spreadsheet. By default if the data changes in the file then the imported DataSet will also be updated (and the Views based on it) in real time.

HiVis will do its best to accommodate the interchange of data of different types when it makes sense to do so, 
for example a floating-point value with no fractional part may be assigned to a Series that stores integer values, or when transposing a Table comprising Series that store differing types (various precision floating-point or integer values) then the generated Series in the transposed Table will automatically assume the necessary data type to accurately store the data (and a warning will be given if this is not possible).

Integration with popular Processing libraries such as ControlP5 and giCentre simplify the (interactive) visualisation of data.


## Examples

For data import and manipulation see the examples (akin to tutorials) in [src/hivis/example](https://github.com/OliverColeman/hivis/tree/master/src/hivis/example).

For interactive visualisation see the Processing sketches in [examples](https://github.com/OliverColeman/hivis/tree/master/examples).


## API Reference

The API reference is [here](http://htmlpreview.github.io/?https://github.com/OliverColeman/hivis/blob/master/distribution/HiVis-1/reference/index.html).
 

## Reporting Problems and Issues

If you run into problems or issues, please use the issues section that comes with this repository.


## Supporting Organisations

The initial work on this library is funded by the University of Sydney Medical School, Faculty of Science.
