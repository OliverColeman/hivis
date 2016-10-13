---
title: Document Center
---

# HiVis

HiVis is a Java library for importing, manipulating and working with various kinds of data. 
It was designed with the programming environment [Processing](http://www.processing.org) (version 3) in mind, but may be used outside of Processing.


## Overview / Features

A collection of data is a DataSet. Built-in DataSet types are:
- DataSeries (storing sequential data elements that may be numeric, Date, String or any other kind of object);
- DataTable, which combine a set of DataSeries with associated labels;
- Graphs (coming soon).

DataSets may be manipulated using various filters and transformations. New filters and transformations may be created easily.

Typically when a DataSet is manipulated a "View" of the DataSet is created, leaving the original DataSet unchanged.

When the underlying DataSet changes the View of it will also be updated, via a tightly integrated event framework.

Data may be imported from external sources, for example a spreadsheet. By default if the source data changes then the imported DataSet will also be updated (and the Views based on it) in real time.

HiVis will do its best to accommodate the interchange of data of different types when it makes sense to do so, 
for example a floating-point value with no fractional part may be assigned to a DataSeries that stores integer values, or when transposing a DataTable comprising series that store differing types (various precision floating-point or integer values) then the generated series in the transposed table will automatically assume the necessary data type to accurately store the data (and a warning will be given if this is not possible).

Includes many examples of loading, manipulating, and visualising data in Processing.


## Examples

For data manipulation see the examples (akin to tutorials) in [src/hivis/example](https://github.com/OliverColeman/hivis/tree/latest/src/hivis/example).

For data import and interactive visualisation see the Processing sketches in [examples](https://github.com/OliverColeman/hivis/tree/latest/examples). The first two of these are identical to the data manipulation examples but formatted as Processing sketches. 


## API Reference

The API reference is [here](http://olivercoleman.github.io/hivis/).


## Download and Install for Processing

Download the latest [zip](https://github.com/OliverColeman/hivis/releases/download/latest/HiVis.zip) file and unzip it into the `libraries` folder in the Processing sketchbook folder. You will need to create the libraries folder if it does not exist.
 

## Reporting Problems and Issues

If you run into problems or issues, please use the issues section that comes with this repository.


## Supporting Organisations

This library is developed by the Interactive Media Lab at the University of New South Wales, Australia with funding from the University of Sydney Medical School, Faculty of Science.
