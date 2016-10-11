import hivis.common.*;
import hivis.data.*;
import hivis.data.reader.*;
import hivis.data.view.*;

// Example of producing a colour coded scatter plot of data in a spreadsheet to visualise 
// three series/columns of data. Exercises:
// 1. Try plotting different columns/series.
// 2. Change the size or shape of the circles according to the values in a fourth series/column.
// 3. Automatically scale the x and y values to fit the canvas.


// Stores the data to plot.
DataTable data;

int xSeriesIndex = 2;
int ySeriesIndex = 1;
int hueSeriesIndex = 3;

// The series containing the data we want to plot.
// <Float> indicates that the series stores floating point (real) values.
DataSeries<Float> xSeries;
DataSeries<Float> ySeries;
DataSeries<Float> hueSeries;

// This is a flag to indicate that data is being (re)loaded and so the plot should not be drawn yet.
boolean settingUp = true;


// Method containing one-off setup code.
void setup() {
  // Make a canvas that is 1000 pixels wide by 500 pixels high.
  size(1000, 500);
  
  // Allow specifying colurs using the Hue, Saturation, Brightness colour space, with unit [0, 1] ranges.
  colorMode(HSB, 1, 1, 1, 1);
  
  // Set text size.
  textSize(15);
  
  // Ask the user to select a spreadsheet to visualise (suggesting the supplied mtcars file).
  selectInput("Select an excel file to visualise:", "fileSelected", sketchFile("mtcars.xlsx"));
}


// Method that gets called when a file is selected.
void fileSelected(File selection) {
  // If no file was selected.
  if (selection == null) {
    println("No file selected.");
  } 
  else {
    // Get data from spread sheet. 
    // The SpreadSheetReader will automatically update the DataTable it provides if the source file is changed.
    int sheetIndex = 0;
    int headerRow = 0;
    int firstDataRow = 1;
    int firstDataColumn = 0;
    SpreadSheetReader reader = new SpreadSheetReader(selection.getAbsolutePath(),  sheetIndex, headerRow, firstDataRow, firstDataColumn);
    data = reader.getData();
    println("\nLoaded data:\n" + data);
    
    
    // Get the series containing the data we want to plot. 
    // The asFloat() method converts, if necessary, and returns the series as a series representing floating point values.
    xSeries = data.getSeries(xSeriesIndex).asFloat();
    ySeries = data.getSeries(ySeriesIndex).asFloat();
    
    // For the series used for hue we scale the values in the series to be in the unit range [0, 1] with the method toUnitRange().
    // This will make it easier to work with (note that we set the colour space to accept unit range values).
    hueSeries = data.getSeries(hueSeriesIndex).toUnitRange().asFloat();
    
    // Set our flag to indicate we've finished loading and setting up the data to plot.
    settingUp = false;
  }
}


// Draws the plot.
void draw() {
  background(0, 0, 1);
  noStroke();
  
  // If the data is ready to plot.
  if (!settingUp) {
    // Draw a dot for each data point.
    for (int row = 0; row < data.length(); row++) {
      // Get values from series. Multiply x and y by a constant factor to scale to canvas size.
      float x = xSeries.get(row) * 2.4;
      float y = ySeries.get(row) * 9;
      float hue = hueSeries.get(row);
      
      // Draw a dot. 
      fill(hue, 1, 0.5, 0.6);
      ellipse(x, y, 10, 10);
    }
    
    // Write some text to indicate what is being plotted.
    fill(0, 0, 0.3);
    text("x: " + data.getSeriesLabel(xSeriesIndex), 10, 20);
    text("y: " + data.getSeriesLabel(ySeriesIndex), 10, 40);
    text("hue: " + data.getSeriesLabel(hueSeriesIndex), 10, 60);
  }
}