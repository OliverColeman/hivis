import hivis.common.*;
import hivis.data.*;
import hivis.data.reader.*;
import hivis.data.view.*;

// This skecth animates an abstract data visualisation

// The source data
DataTable data;

// some DataSeries to store the data
DataSeries heightSeries;
DataSeries weightSeries;
DataSeries waistSeries;
DataSeries BMISeries;
String[] smokerSeries;


// size of the grid
int rectSize = 25;


void setup() {
  size(1000, 700);
  // setup the visual
  colorMode(HSB, 360, 255, 255, 255);
  noStroke();
  background(0);

  // Load the data
  fileSelected(sketchFile("NCHS_dataset.xlsx"));
}

void fileSelected(File selection) {

  // Get data from spread sheet. The SpreadSheetReader will automatically update the DataTable it provides.
  println("loading data");
  data = HV.loadSpreadSheet(HV.loadSSConfig().sourceFile(selection));

  println("\nLoaded data:\n" + data);
  // add the data to our data structures
  heightSeries = data.get("height");
  weightSeries = data.get("weight");
  waistSeries = data.get("waist");
  BMISeries = data.get("bmi");
  smokerSeries = data.get("smoker").asStringArray();
}




void draw() {
  // a counter to remember which row of data we are looking at
  int row = 0;
  // draw a grid
  for (int x = 0; x < width; x+= rectSize) {
    for (int y = 0; y < height; y+= rectSize) {
      // use pushMatrix to draw at the origin, but translate the coordinates to our x and y position
      pushMatrix();
      translate(x, y);
      
      // slowly change the colour of our background rectangles
      fill(frameCount/12.5%360, 100, 100, 20);
      // draw the background rectangles
      rect(0, 0, rectSize-1, rectSize-1);

      // grab the data from our DataSeries
      // (the weight is too high to use below, so scale it)
      float we = weightSeries.getFloat(row) / 50;
      float wa = waistSeries.getFloat(row);
      float hi = heightSeries.getFloat(row);
      
      // we will draw dot, make it white
      fill(360, 0, 255, 200);
      

      // is the person a smoker? slow down their dot a little
      float smok = 1.5;
      if (smokerSeries[row].equals("no")) {
        smok = 1.2;
      }
      // we could do the next part in one step, but it's broken down here...
      
      // the dot starts at the centre of our rectangle
      float cx = rectSize/2.0;
      float cy = rectSize/2.0;
      
      // we will use sin() and cos() to make our dot move, 
      // but since sin() and cos() is from 0 to 1
      // we need an offset to scale them by, 
      // and scale it by their waist size, bigger waist = bigger movement
      float offset = rectSize/4 * wa;
            
      // x will be affected by height of the person
      cx += sin(pow(radians(frameCount*smok), hi)) * offset;
      // y will be affected by their weight
      cy += cos(pow(radians(frameCount*smok), we)) * offset;
      // draw the dot
      ellipse(cx, cy, 1, 1);
      // make sure to run popMatrix() after we finish drawing this box
      popMatrix();
      // move to the next row
      row ++;
      // if we get to the of our data, we will just move to the next one
      row %= data.length();
    }
  }
}