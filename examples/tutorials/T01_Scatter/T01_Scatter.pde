import hivis.common.*;
import hivis.data.*;
import java.util.*;

// Example of drawing a scatter plot of two columns in a spreadsheet. 
// Exercises:
//  1. Try plotting different columns/series.
//  2. Change the brightness of the data points based on the values in a third column/series.

// Stores the data to plot.
DataTable data;

// Method containing one-off setup code.
void setup() {
  // Make a canvas that is 1000 pixels wide by 500 pixels high.
  size(1000, 500);
  
  // We automatically load the supplied data file by default. 
  // If you want to load your own data then comment the below line and uncomment the line beginning "selectInput(..." 
  fileSelected(sketchFile("iris.csv"));
  //selectInput("Select an xlsx or CSV file to visualise:", "fileSelected");
}


// Method that gets called when a file is selected.
void fileSelected(File selection) {
  // If no file was selected.
  if (selection == null) {
    println("No file selected.");
  } 
  else {
    // Get data from the spread sheet. 
    // The spread sheet reader will automatically update the DataTable if the source file is changed.
  	data = HV.loadSpreadSheet(
  	  HV.loadSSConfig().sourceFile(selection)
  	);
  }
  
  // Convert the numeric series to the unit range [0, 1] with the method toUnitRange(). 
  // This will make it easier to work with when we plot it.
  data = data.toUnitRange();
}


// Draws the plot.
void draw() {
  background(255);
  noStroke();
  fill(0, 0, 255);
  
  // If the data is ready to plot.
  if (data != null) {
    // Draw a dot for each data point/row in the table.
    for (DataRow row : data) {
      // Get values from the data point. The method getFloat(x) returns the value for 
      // the series with index x as a 'float' number, which is what Processing works with. 
      // We multiply x and y by the canvas width and height respectively to convert the 
      // values to the canvas pixel size (remember the data was scaled to unit range).
      float x = row.getFloat(1) * width;
      float y = row.getFloat(2) * height;
      // Note that we could also have used the labels/headings of the series/columns instead 
      // of their indexes with the getFloat method, eg row.getFloat("Sepal.Length")
      
      // Draw a dot. 
      ellipse(x, y, 30, 30);
    }
  }
}