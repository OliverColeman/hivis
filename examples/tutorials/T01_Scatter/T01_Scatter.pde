import hivis.common.*;
import hivis.data.*;
import java.util.*;

// Example of drawing a scatter plot of two columns in a spreadsheet. 
// Exercises:
//  1. Try plotting different columns/series.
//  2. Change the brightness of the data points based on the values in a third column/series.

// Stores the data to plot.
DataTable data;

// The size of the plot points.
float pointDiameter = 30;

// The labels of the series to plot.
String xSeries = "Sepal.Length";
String ySeries = "Sepal.Width";

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
  	DataTable rawData = HV.loadSpreadSheet(
  	  HV.loadSSConfig().sourceFile(selection)
  	);
    
    // Convert the numeric series to the unit range [0, 1]. This will make it easier to work with when we plot it.
    // We also add " (UR)" to the series names to distinguish them from the original series (PP stands for Prefix/Postfix).
    DataTable scaledData = rawData.toUnitRange().relabelSeriesPP("", " (UR)");
    
    // Get a table containing the original data and the scaled data.
    data = rawData.combine(scaledData);
    
    // Print out the data to plot to make sure it looks right.
    println(data);
  }
}


// Draws the plot.
void draw() {
  background(255);
  noStroke();
  fill(0, 0, 255);
  
  // If the data is ready to plot.
  if (data != null) {
    // Calculate the dimensions of the plot.
    // We're using the whole window, but have to account for the size of the points.
    // We substract twice the point diameter to allow for a margin.
    float plotWidth = width - pointDiameter * 2;
    float plotHeight = height - pointDiameter * 2;
    
    // Draw a dot for each data point/row in the table.
    for (DataRow row : data) {
      // Get values from the data point. The method getFloat(label) returns the value for 
      // the series with the label as a 'float' number, which is what Processing works with.
      // We append " (UR)" to the series labels to get the unit-range series created in fileSelected().
      float x = row.getFloat(xSeries + " (UR)");
      float y = row.getFloat(ySeries + " (UR)");
      
      // Translate and scale the coordinates to allow for the size of the points and the margin.
      x = x * plotWidth + pointDiameter;
      y = height - (y * plotHeight + pointDiameter); // Subtract from height to flip the y-axis.
      
      // Draw the dot.
      ellipse(x, y, pointDiameter, pointDiameter);
    }
    
    // Draw axes.
    stroke(0, 127, 0);
    fill(0, 127, 0);
    // x axis.
    line(pointDiameter, height - pointDiameter, width - pointDiameter, height - pointDiameter);
    textAlign(CENTER, TOP);
    text("" + data.get(xSeries).min().getFloat(), pointDiameter, height - pointDiameter + 5);
    text("" + data.get(xSeries).max().getFloat(), width-pointDiameter, height - pointDiameter + 5);
    // y axis.
    line(pointDiameter, height - pointDiameter, pointDiameter, pointDiameter);
    textAlign(LEFT, CENTER);
    text("" + data.get(ySeries).min().getFloat(), 2, height - pointDiameter);
    text("" + data.get(ySeries).max().getFloat(), 2, pointDiameter);
  }
}