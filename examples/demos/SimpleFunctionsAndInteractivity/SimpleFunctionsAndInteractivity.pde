import hivis.common.*;
import hivis.data.*;
import hivis.data.reader.*;
import hivis.data.view.*;

// Plots the ratio of the corresponding values stored in two columns, 
// separated into sub plots according to the integer values stored in a third 
// column. Hovering over a point will provide more information.


// Stores the data to plot.
DataTable data;

void setup() {
  size(1000, 300);
  textSize(15);
  colorMode(HSB, 1, 1, 1, 1);
  
  // Get a data set.
  DataTable cars = HV.mtCars();
  
  // Make a table to contain the data to plot.
  data = HV.newTable();
  // Add some series unchanged from the original data set.
  data.addSeries(cars.selectSeries("cyl", "disp", "hp"));
  
  // Add a series containing the values in hp divided by the corresponding 
  // values in disp.
  data.addSeries("hp / disp", data.get("hp").divide(data.get("disp")));
  
  // Add versions of disp and hp that are scaled to unit range [0, 1], used 
  // for x and y coorindates.
  data.addSeries("disp unit", data.get("disp").toUnitRange());
  data.addSeries("hp unit", data.get("hp").toUnitRange());
}


// Draws the plot.
void draw() {
  background(0, 0, 0);
  
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
  
  // Get the minimum value in the "hp / disp" series.
  float hpOnDispMin = data.get("hp / disp").min().getFloat();
  
  for (DataRow row : data) {
    int c = row.getInt("cyl");
    
    int y = (c - 4) * yScale + yOffset;
    float x = (row.getFloat("hp / disp") - hpOnDispMin) * xScale + xOffset;
    
    float w = row.getFloat("disp unit") * 100;
    float h = row.getFloat("hp unit") * 100;
    
    // Determine if we're hovering over a data point by testing if the distance
    // from the centre of it to the mouse is less than the average/approximate 
    // radius of the point. 
    float approxRadius = (w + h) / (2 * 2);
    boolean isHovering = distanceToMouse(x, y) < approxRadius;
    
    float hue = c / 10.0;
    float saturationAndBrightness = isHovering ? 1 : 0.8;
    // make semi-transparent if mouse is not hovering over this point.
    float alpha = isHovering ? 1 : 0.6; 
    
    fill(hue, saturationAndBrightness, saturationAndBrightness, alpha);
    ellipse(x, y, w, h);
    
    if (isHovering) {
      fill(1, 0, 1, 1);
      text("  hp: " + row.get("hp") + "\ndisp: " + row.get("disp"), x, y-40);
    }
  }
}

// Returns the distance to the mouse pointer for the given coordinates.
float distanceToMouse(float x, float y) {
  float dx = x - mouseX;
  float dy = y - mouseY;
  return sqrt(dx*dx + dy*dy);
}