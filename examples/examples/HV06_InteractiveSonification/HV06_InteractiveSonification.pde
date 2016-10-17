import hivis.common.*;
import hivis.data.*;
import hivis.data.reader.*;
import hivis.data.view.*;
import beads.*;

// Example of using sonification to "render" high-dimensional data. 
// A scatter plot is produced, with two of the dimensions used for 
// the coordinates, and two more dimensions are used to control the 
// volume/gain and frequency of a tone when hovering over a data point.
// THIS EXAMPLE REQUIRES the Beads Library (Sketch -> Import Library -> Add Library ...) 

// Stores the data to plot.
DataTable data;

// The series/columns we're interested in.
DataSeries<Float> retailMarkup;
DataSeries<Float> healthRating;
DataSeries<Float> retailCost;
DataSeries<Integer> tasteStrength;

// Used to control the frequency of the audio when hovering over a data point.
Envelope freqEnv;
// Used to silence the audio when not hovering over a data point.
Gain gainControl;


void setup() {
  size(1000, 1000);
  
  // Get data from spread sheet. 
  // The SpreadSheetReader will automatically update the DataTable it provides if the source file is changed.
  int sheetIndex = 1;
  int headerRowIndex = 0;
  int firstDataRow = 3;
  int firstDataColumn = 0;
  data = HV.loadSpreadSheet(sketchFile("KIB - Oil Well.xlsx"), sheetIndex, headerRowIndex, firstDataRow, firstDataColumn);
  
  // Get the series/columns we're interested in.
  // Transform some to unit range [0, 1] to make them easier to work with.
  retailMarkup = data.getSeries("retail markup").toUnitRange().asFloat();
  healthRating = data.getSeries("health rating").toUnitRange().asFloat();
  retailCost = data.getSeries("UK retail cost per 100ml ($)").asFloat();
  tasteStrength = data.getSeries("taste strength index").asInt();
  
  // Set-up audio.
  AudioContext ac = new AudioContext();
  freqEnv = new Envelope(ac);
  WavePlayer wp = new WavePlayer(ac, freqEnv, Buffer.SINE);
  //freqEnv.addSegment(1000, 1000);
  gainControl = new Gain(ac, 1, 0);
  gainControl.addInput(wp);
  ac.out.addInput(gainControl);
  ac.start();
}


// Draws the plot.
void draw() {
  float widthScaled = width * 0.96;
  float heightScaled = height * 0.96;
  float marginTop = width * 0.01;
  float marginLeft = width * 0.03;
  
  textSize(14);
  
  background(255);
  
  // Draw axis.
  fill(0, 0, 127);
  stroke(0, 0, 127);
  textAlign(CENTER, BOTTOM);
  
  text("Retail Markup", width/2, height-5);
  line(marginLeft, marginTop+heightScaled, marginLeft + widthScaled, marginTop+heightScaled);
  
  
  rotate(HALF_PI);
  text("Health Rating", height / 2, -10);
  rotate(-HALF_PI);
  line(marginLeft, marginTop, marginLeft, marginTop+heightScaled);
  
  // Flag to indicate that the audio output should be silenced when no point is being hovered over.
  boolean silenceAudio = true;

  // Plot data points as scatter plot, using "retail markup" and "health rating" as x and y coordinates respectively.
  noStroke();
  for (int row = 0; row < data.length(); row++) {
    
    // If data exists for this row.
    if (data.getSeries(0).get(row) != null) {
      // Get values from series. Multiply x and y by a constant factor to scale to canvas size. See exercise 2.
      float x = retailMarkup.get(row) * widthScaled + marginLeft;
      float y = healthRating.get(row) * heightScaled + marginTop;
      
      fill(0, 0, 0, 127);
      ellipse(x, y, 10, 10);
      
      if (isHovering(x, y)) {
        // Text labels.
        textAlign(LEFT, CENTER);
        String label = data.getSeries(0).get(row).toString();
        if (data.getSeries(1).get(row) != null) {
          label += " - " + data.getSeries(1).get(row).toString();
        }
        label += "\nretail cost: " + retailCost.get(row);
        label += "\ntaste strength: " + tasteStrength.get(row);
        fill(0, 127, 0);
        text(label, x+7, y);
        
        // Set audio frequency and gain based on non-visible data.
        silenceAudio = false;
        // Take log of cost to account for logarithmic tone sensitivity of human hearing (?). Multiply by 1000 to get frequencies into khz range.
        // Add 100 hertz so even the lowest data values will produce audible tone on speakers that don't reproduce low frequencies very well. 
        float freq = log(retailCost.get(row)) * 1000 + 100;
        // Scale gain from 2/5 to 4/5 (data range is [0, 2]).
        float gain = (tasteStrength.getInt(row) + 2) / 5.0;
        freqEnv.addSegment(freq, 200);
        gainControl.setGain(gain);
        
        // Draw audio legend.
        textAlign(LEFT, TOP);
        text("Freqency: retail cost\nVolume:   taste strength", width - 200, marginTop);
      }
    }
  }
  
  if (silenceAudio) {
    gainControl.setGain(0);
    freqEnv.setValue(20);
  }
}

// Returns true if the mouse is hovering near the given coordinates (less than 5 pixels distance).
boolean isHovering(float x, float y) {
  float dx = x - mouseX;
  float dy = y - mouseY;
  return Math.sqrt(dx*dx + dy*dy) < 5;
}