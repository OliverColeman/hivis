import hivis.common.*;
import hivis.data.*;
import hivis.data.reader.*;
import hivis.data.view.*;

// Plots the ratio of the corresponding values stored in two columns, 
// separated into sub plots according to the integer values stored in a third column.
// Hovering over a point will provide more information.
// Exercises:
// 1. Try plotting ratio or other functions over other series/columns.
// 3. Change the shape that is drawn depending on the value of a series with integer values.
// 2. Currently when hovering over two data points that are close to each other they 
//    may both have labels shown for them simultaneously and rather messily. Modify the code 
//    so that only the data point closest to the mouse pointer has its label shown (hint: only 
//    draw the label after iterating over all points and recording the closest).


// Stores the data to plot.
DataTable data;

// Series containing data we want to plot.
DataSeries<Integer> cyl; // number of cylinders
DataSeries<Float> disp; // displacement (engine capacity)
DataSeries<Float> hp; // horse power
DataSeries<Float> hpOnDisp; //hp divided by the corresponding values in disp.

// This is a flag to indicate that data is being (re)loaded and so the plot should not be drawn yet.
boolean settingUp = true;


void setup() {
  size(1000, 300);
  textSize(15);
  colorMode(HSB, 1, 1, 1, 1);
  
  // Ask the user to select a spreadsheet to visualise.
  selectInput("Select an excel file to visualise:", "fileSelected", sketchFile("mtcars.xlsx"));
}


void fileSelected(File selection) {
  if (selection == null) {
    println("No file selected.");
  } 
  else {
    // Get data from spread sheet. 
    // The SpreadSheetReader will automatically update the DataTable it provides if the source file is changed.
    SpreadSheetReader reader = new SpreadSheetReader(selection.getAbsolutePath(), 0, 0, 1, 0);
    data = reader.getData();
    println("\nLoaded data:\n" + data);
    
    // Get series from the data table.
    cyl = data.getSeries("cyl").asInt(); // number of cylinders
    // For the displacement and horse-power series we get the double (note the <Double> and asDouble()) 
    // precision floating-point numbers so that numeric operations performed on them have higher accuracy.
    DataSeries<Double> dispOrig = data.getSeries("disp").asDouble();
    DataSeries<Double> hpOrig = data.getSeries("hp").asDouble();
    
    // Get a series containing the values in hp divided by the corresponding 
    // values in disp (and then convert to float values).
    hpOnDisp = hpOrig.divide(dispOrig).asFloat();
    
    // Scale the series used for x and y coorindates (disp and hp) to unit range 
    // (and convert to float) to make them easier to work with when plotting.
    disp = dispOrig.toUnitRange().asFloat();
    hp = hpOrig.toUnitRange().asFloat();
    
    settingUp = false;
  }
}


// Draws the plot.
void draw() {
  background(0, 0, 0);
  
  if (!settingUp) {
    int xScale = 1000;
    int xOffset = 60;
    int yScale = 50;
    int yOffset = 40;
    
    // Draw row labels (for 4, 6 and 8 cylinders).
    fill(1);
    textAlign(LEFT, CENTER);
    for (int c = 4; c <= 8; c += 2) {
      text(c, 10, (c - 4) * yScale + yOffset);
    }
    
    for (int row = 0; row < data.length(); row++) {
      int c = cyl.get(row);
      
      int y = (c - 4) * yScale + yOffset;
      float x = (hpOnDisp.get(row) - hpOnDisp.minValue()) * xScale + xOffset;
      
      float w = disp.get(row) * 100;
      float h = hp.get(row) * 100;
      
      // Determine if we're hovering over a data point by testing if the distance from 
      // the centre of it to the mouse is less than the average/approximate radius of the point. 
      float approxRadius = (w + h) / (2 * 2);
      boolean isHovering = distanceToMouse(x, y) < approxRadius;
      
      float hue = c / 10.0;
      float saturationAndBrightness = isHovering ? 1 : 0.8;
      float alpha = isHovering ? 1 : 0.6; // make semi-transparent if mouse is not hovering over this point.
      
      fill(hue, saturationAndBrightness, saturationAndBrightness, alpha);
      ellipse(x, y, w, h);
      
      if (isHovering) {
        fill(1, 0, 1, 1);
        text("  hp: " + hp.get(row) + "\ndisp: " + disp.get(row), x, y-40);
      }
    }
  }
}

// Returns the distance to the mouse pointer for the given coordinates.
float distanceToMouse(float x, float y) {
  float dx = x - mouseX;
  float dy = y - mouseY;
  return sqrt(dx*dx + dy*dy);
}