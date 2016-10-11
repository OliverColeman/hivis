import hivis.common.*;
import hivis.data.*;
import hivis.data.reader.*;
import hivis.data.view.*;

// Example of drawing a pie chart of a series from a DataTable.

// Stores the data to plot.
DataTable data;

void setup() {
  size(500, 500);
  textSize(14);
  // Allow specifying colours using the Hue, Saturation, Brightness colour space, with unit [0, 1] ranges.
  colorMode(HSB, 1, 1, 1, 1);
  
  data = HV.mtCars();
}


// Draws the plot.
void draw() {
  background(1, 0, 1);
  noStroke();
  
  // Make pie chart from first numerical series in the table.
  makePie(data.getSeries(1), height, width/2, height/2);
}

// Draws a pie chart based on a series from a d
void makePie(DataSeries series, float diameter, float x, float y) {
  // First calculate the total of the given values. We start at 1 because the first series is the name.
  float total = 0;
  for (int row = 0; row < series.length(); row++) {
    // Get the value for this series from the row to chart.
    float value = series.getFloat(row);
    total += value;
  }
  
  // For remembering angle we're up to as we draw each slice.
  float lastAngle = 0;
  
  // Draw a slice for each data point/column value.
  for (int row = 0; row < series.length(); row++) {
    // Get the value for this series from the row to plot.
    float value = series.getFloat(row);
    
    // Set colour, just go round the colour wheel via the hue component.
    fill((float) row / series.length(), 0.9, 0.7);
    
    // Angle is the proportional magnitude of the data point.
    float angle = (value / total) * TWO_PI; 

    // Draw a slice of the chart.
    arc(x, y, diameter, diameter, lastAngle, lastAngle + angle);
    
    // Remember angle we're up to.
    lastAngle += angle;
  }
}