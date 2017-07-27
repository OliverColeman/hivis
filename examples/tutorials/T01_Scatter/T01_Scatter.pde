import hivis.common.*;
import hivis.data.*;
import java.util.*;


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


// Method that is called when a file is selected.
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
  
    // Scale the series/columns we wish to plot to the window pixel coordinates. 
    // We're using the whole window, but have to account for the size of the 
    // points and we also allow for a margin the same size as the points.
    // The DataSeries.toRange method accepts two values: the minimum and maximum 
    // values to scale the data in the series to.
    DataTable scaledData = HV.newTable(); 
    scaledData.addSeries("x", rawData.get(xSeries).toRange(pointDiameter, width - pointDiameter));
    // Note that we're swapping the "top" and "bottom" of the y-axis so that the smallest values appear 
    // at the bottom of the window (the (0, 0) coordinates are at the top-left of the window in Processing).
    scaledData.addSeries("y", rawData.get(ySeries).toRange(height - pointDiameter, pointDiameter));
    
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
    
    // Draw a dot for each data point/row in the table.
    for (DataRow row : data) {
      // Draw the dot.
      ellipse(row.getFloat("x"), row.getFloat("y"), pointDiameter, pointDiameter);
    }
    
    // Draw axes.
    fill(0, 127, 0);
    stroke(0, 127, 0);
    HVDraw.xAxis(this, data.get(xSeries), pointDiameter, height-pointDiameter, width-pointDiameter*2);
    HVDraw.yAxis(this, data.get(ySeries), pointDiameter, pointDiameter, height-pointDiameter*2);
  }
}