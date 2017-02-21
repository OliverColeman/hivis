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

int xSeriesIndex = 3;
int ySeriesIndex = 1;
int hueSeriesIndex = 4;

// The series containing the data we want to plot.
DataSeries xSeries;
DataSeries ySeries;
DataSeries hueSeries;


// Method containing one-off setup code.
void setup() {
  // Make a canvas that is 1000 pixels wide by 500 pixels high.
  size(1000, 500);
  
  // Allow specifying colours using the Hue, Saturation, Brightness colour space, with unit [0, 1] ranges.
  colorMode(HSB, 1, 1, 1, 1);
  
  // Set text size.
  textSize(15);
  
  // Get a data set
  data = HV.mtCars();
  println(data);
  
  // Get the series containing the data we want to plot. 
  xSeries = data.getSeries(xSeriesIndex);
  ySeries = data.getSeries(ySeriesIndex);
  
  // For the series used for hue we scale the values in the series to be in the unit range [0, 1] with the method toUnitRange().
  // This will make it easier to work with (note that we set the colour space to accept unit range values).
  hueSeries = data.getSeries(hueSeriesIndex).toUnitRange();
}


// Draws the plot.
void draw() {
  background(0, 0, 1);
  noStroke();
  
  // Draw a dot for each data point.
  for (int row = 0; row < data.length(); row++) {
    // Get values from series. Multiply x and y by constant factors to scale to canvas size.
    // (We could also have converted the series to unit ranges and multiplied with canvas width and height.)
    // The getFloat method gets the value at the given index as a "float" number, which is what Processing likes to work with.
    float x = xSeries.getFloat(row) * 2.4;
    float y = ySeries.getFloat(row) * 9;
    float hue = hueSeries.getFloat(row);
    
    // Draw a dot. 
    fill(hue, 1, 0.5, 0.6);
    ellipse(x, y, 10, 10);
    
    // Write some text to indicate what is being plotted.
    fill(0, 0, 0.3);
    text("x: " + data.getSeriesLabel(xSeriesIndex), 10, 20);
    text("y: " + data.getSeriesLabel(ySeriesIndex), 10, 40);
    text("hue: " + data.getSeriesLabel(hueSeriesIndex), 10, 60);
  }
}