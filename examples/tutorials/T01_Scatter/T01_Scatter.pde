import hivis.common.*;
import hivis.data.*;

// Example of drawing a scatter plot of two columns in a spreadsheet. 
// Exercises:
//  1. Try plotting different columns/series.
//  2. Change the brightness of the data points based on the values in a third column/series.

// Stores the data to plot.
DataTable data;

// The series containing the data we want to plot.
DataSeries xSeries;
DataSeries ySeries;

// Method containing one-off setup code.
void setup() {
  // Make a canvas that is 1000 pixels wide by 500 pixels high.
  size(1000, 500);
  
  // Ask the user to select a spreadsheet to visualise.
  selectInput("Select an excel file to visualise:", "fileSelected");
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
    data = HV.loadSpreadSheet(selection);
    
    // Get the series containing the data we want to plot. 
    // Note that the first series starts at index 0 (column A in the spreadsheet).
    // We scale the values in the series to be in the unit range [0, 1] with the method toUnitRange(). This will make it easier to work with.
    xSeries = data.getSeries(1).toUnitRange();
    ySeries = data.getSeries(2).toUnitRange();
  }
}


// Draws the plot.
void draw() {
  background(255);
  
  // If the data is ready to plot.
  if (ySeries != null) {
    noStroke();
    fill(0, 0, 255);
    
  	// Draw a dot for each data point.
    for (int row = 0; row < data.length(); row++) {
      
      // Get values from the series. The method getFloat(x) returns the value stored at index x as a 'float', which is what Processing works with. 
      // We multiply x and y by width and height respectively to convert the values to the canvas pixel size (the data was scaled to unit range).
      float x = xSeries.getFloat(row) * width;
      float y = ySeries.getFloat(row) * height;
      
      // Draw a dot. 
      ellipse(x, y, 30, 30);
    }
  }
}