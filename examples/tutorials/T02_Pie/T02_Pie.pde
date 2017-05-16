import hivis.common.*;
import hivis.data.*;

// Example of drawing pie charts from columns in a spreadsheet.
// Exercises:
//  1. Lay the pie charts side-by-side, all at the same size, instead of nested inside each other.
//  2. Label the (now side-by-side) pie charts with the labels for the series in the data table.


// Stores the data to plot.
DataTable data;

// A set of colours to render the pie charts in.
color[] palette;


// Method containing one-off setup code.
void setup() {
  // Set canvas size to match pie chart size.
  size(1000, 1000);
  
  data = HV.loadSpreadSheet(
    HV.loadSSConfig().sourceFile(sketchFile("Employee Diversity Social Media.csv"))
  );
  
  // Make a colour palette based on the length of the series in the table.
  palette = HVDraw.makeRainbowPalette(data.length(), 0.9, 0.8);
}


// Draws the plot.
void draw() {
  background(0);
  noFill();
  stroke(255);
  
  // If we've loaded some data.
  if (data != null) {
    // Draw pie charts from each series in the spreadsheet, one inside the other.
    
    // The first pie chart will have the maximum size we can fit in the canvas.
    int size = min(width, height);
    float d = size;
    
    // For each series in the table.
    for (int s = 0; s < data.seriesCount(); s++) {
      // Get the series from table.
      DataSeries series = data.get(s);
      
      // If this series contains numeric data.
      if (series.isNumeric()) {
        // Draw the pie chart for series s, in the middle of the canvas, using our colour palette (starting with the first colour in the palette).
        HVDraw.pie(this, series, d, width/2, height/2, palette, 0);
        
        // Draw a border circle around the pie.
        ellipse(width/2, height/2, d, d);
        
        // Make the next pie half the size of the previous one.
        d = d / 2;
      }
    }
  }
}