import hivis.common.*;
import hivis.data.*;
import hivis.data.reader.*;
import hivis.data.view.*;

// This skecth animates an abstract data visualisation

// The source data
DataTable data;


// This is a flag to indicate that data is being (re)loaded and so the plot should not be drawn yet.
boolean noDataLoaded = true;

DataSeries heightSeries;
DataSeries weightSeries;
DataSeries waistSeries;
DataSeries BMISeries;
String[] smokerSeries;

int alpha = 15;
int[] randomRows;
int numRows = 10;
void setup() {
  size(1000, 700);
  colorMode(HSB, 360, 255, 255, 255);
  // make the background slightly off-white
  background(350);
  noFill();
  strokeWeight(3);

  // load the data
  fileSelected(sketchFile("NCHS_dataset.xlsx"));
}

void fileSelected(File selection) {
  // Get data from spread sheet. The SpreadSheetReader will automatically update the DataTable it provides.
  println("loading data");
  data = HV.loadSpreadSheet(HV.loadSSConfig().sourceFile(selection));

  println("\nLoaded data:\n" + data);
  // populate our data series
  heightSeries = data.get("height");
  weightSeries = data.get("weight");
  waistSeries = data.get("waist");
  BMISeries = data.get("bmi");
  smokerSeries = data.get("smoker").asStringArray();
  noDataLoaded = false;
  
  // start by selecting some random rows to draw
  randomRows = new int[numRows];
  for (int i = 0; i < numRows; i++) {
    randomRows[i] = int(random(data.length()));
  }

}




void draw() {
  // blur and fade the background
  fill(360, alpha);
  noStroke();
  rect(0, 0, width, height);
  filter(BLUR, .7);

  // update the rows from the data frame that we are visualising every 30 frames
  if (frameCount % 30 == 0) {
    println("updating rows");
    updateRows();
  }
  // draw the rows we have selected as a lines from the centre
  for (int i = 0; i < randomRows.length; i++) {
    int row = randomRows[i];
    // starting point for lines
    float cx = width/2.0;
    float cy = height/2.0;
    // angle for this data point
    float r = row * 360/data.length();
    randomSeed(row);
    // direction to draw the line
    float dx = sin(radians(r  + random(-3.1, 3.1) * frameCount%90)) * 20;
    float dy = cos(radians(r)) * 20;
    // store the previous position of the line
    float px = 0;
    float py = 0;
    // select the colour for this line
    stroke(getColour(row, 50, 400));

    // get the data from this row
    float we = weightSeries.getFloat(row);
    float wa = waistSeries.getFloat(row);
    float hi = heightSeries.getFloat(row);

    // create a counter for our while loop
    float count = 0;
    while (onScreen(cx, cy)) {
      // increase the count, to be used in the noise function
      count += (row % 9)/10 + .1;
      noise(px/10);
      // update the previous position
      px = cx;
      py = cy;
      // update the new position
      noiseSeed(int(we * count * 10));
      cx += dx + noise(frameCount + count)/8 * we;
      noiseSeed(int(wa * hi * count * 5));
      cy += dy + noise(frameCount + count)/8 * we;
      // draw circles if they are smoker
      if (isSmoker(row)) {
        ellipse(cx, cy, wa*10, hi*5);
      }
      // draw lines if not a smoker
      else {  
        line(cx, cy, px, py);
      }
    }
  }
}

// returns true if the positions given are within the boundary of the screen
boolean onScreen(float x, float y) {
  if (0 < x && x < width) {
    if (0 < y && y < height) {
      return true;
    }
  }
  return false;
}

// returns a colour based on the data for BMI 
color getColour(int r, int hue, int hueRange) {
  float min = BMISeries.min().getFloat();
  float max = BMISeries.max().getFloat();
  float bmi = (BMISeries.getFloat(r) - min)/max * hueRange + hue;
  color c = color(bmi, 200, 220, alpha * 2);
  return c;
}

// return whether the person smokes or not
boolean isSmoker(int r) {
  if (smokerSeries[r].equals("no")) {
    return false;
  }
  return true;
}

// update the data being drawn
void updateRows() {
  for (int i = 0; i < randomRows.length-1; i++) {
    randomRows[i] = randomRows[i+1];
  }
  randomRows[randomRows.length-1] = int(random(data.length()));
}