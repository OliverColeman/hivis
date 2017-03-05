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

// Used to control the frequency of the audio when hovering over a data point.
Envelope freqEnv;
// Used to silence the audio when not hovering over a data point.
Gain gainControl;


void setup() {
  size(1000, 1000);
  
  println(sketchFile("KIB - Oil Well.xlsx"));
  
  // Get data from spread sheet. 
  // The SpreadSheetReader will automatically update the DataTable it provides if the source file is changed.
  DataTable dataRaw = HV.loadSpreadSheet(
    HV.loadSSConfig().sourceFile(sketchFile("KIB - Oil Well.xlsx")).sheetIndex(1).headerRowIndex(0).rowIndex(3)
  );
  
  println("Raw data:\n" + dataRaw);
  
  // Get the series/columns we're interested in.
  data = HV.newTable();
  data.addSeries(dataRaw.selectSeries("oil / fat", "type", "UK retail cost per 100ml ($)", "taste strength index"));
  // Transform some to unit range [0, 1] to make them easier to work with.
  data.addSeries("retail markup", dataRaw.get("retail markup").toUnitRange());
  data.addSeries("health rating", dataRaw.get("health rating").toUnitRange());
  
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
  for (DataRow row : data) {
    // If data exists for this row.
    if (row.get("oil / fat") != null) {
      // Get values from series. Multiply x and y by a constant factor to scale to canvas size. See exercise 2.
      float x = row.getFloat("retail markup") * widthScaled + marginLeft;
      float y = row.getFloat("health rating") * heightScaled + marginTop;
      
      fill(0, 0, 0, 127);
      ellipse(x, y, 10, 10);
      
      if (isHovering(x, y)) {
        // Text labels.
        textAlign(LEFT, CENTER);
        String label = row.get("oil / fat").toString();
        if (row.get("type") != null) {
          label += " - " + row.get("type").toString();
        }
        
        float retailCost = row.getFloat("UK retail cost per 100ml ($)");
        int tasteStrength = row.getInt("taste strength index");
        
        label += "\nretail cost: " + retailCost;
        label += "\ntaste strength: " + tasteStrength;
        fill(0, 127, 0);
        text(label, x+7, y);
        
        // Set audio frequency and gain based on non-visible data.
        silenceAudio = false;
        // Take log of cost to account for logarithmic tone sensitivity of human hearing (?). Multiply by 1000 to get frequencies into khz range.
        // Add 100 hertz so even the lowest data values will produce audible tone on speakers that don't reproduce low frequencies very well. 
        float freq = log(retailCost) * 1000 + 100;
        // Scale gain from 2/5 to 4/5 (data range is [0, 2]).
        float gain = (tasteStrength + 2) / 5.0;
        freqEnv.addSegment(freq, 200);
        gainControl.setGain(gain);
        
        // Draw audio legend.
        textAlign(LEFT, TOP);
        text("Freqency: retail cost\nVolume:   taste strength", width - 200, marginTop);
      }
    }
    data.addSeries("blah", HV.newIntegerSeries(1));
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