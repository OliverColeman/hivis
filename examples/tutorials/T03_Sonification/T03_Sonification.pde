import hivis.common.*;
import hivis.data.*;
import hivis.data.reader.*;
import hivis.data.view.*;
import beads.WavePlayer;
import beads.Envelope;
import beads.Gain;
import beads.AudioContext;
import beads.Buffer;


// Stores the data to plot.
DataTable data;

// The series/columns containing data to plot.
DataSeries xSeries;
DataSeries ySeries;
DataSeries freqSeries;

// Used to control the frequency of the audio when hovering over a data point.
Envelope freqEnv;
WavePlayer wp;
Gain gainControl;

// This is a flag to indicate that data has not been loaded and so the plot may not be drawn yet.
boolean settingUp = true;


void setup() {
  size(1000, 1000);
  
  // Allow specifying colours using the Hue, Saturation, Brightness colour space, with unit [0, 1] ranges.
  colorMode(HSB, 1, 1, 1, 1);
  
  // We automatically load the supplied data file by default. 
  // If you want to load your own data then comment the below line and uncomment the line beginning "selectInput(..." 
  fileSelected(sketchFile("iris.csv"));
  //selectInput("Select an xlsx or CSV file to visualise:", "fileSelected");
}


// Method that gets called when a file is selected.
void fileSelected(File selection) {
  // If no file was selected.
  if (selection == null) {
    println("No file selected.");
  } 
  else {
    // Get data from spread sheet. 
    // The SpreadSheetReader will automatically update the DataTable it provides if the source file is changed.
    data = HV.loadSpreadSheet(selection);
    println(data);
    
    // Get the series/columns we're interested in.
    // Transform to unit range [0, 1] to make them easier to work with.
    xSeries = data.get(0).toUnitRange();
    ySeries = data.get(1).toUnitRange();
    freqSeries = data.get(2).toUnitRange();
    
    // Set-up audio.
    AudioContext ac = new AudioContext();
    freqEnv = new Envelope(ac);
    wp = new WavePlayer(ac, freqEnv, Buffer.SINE);
    gainControl = new Gain(ac, 1, 0);
    gainControl.addInput(wp);
    ac.out.addInput(gainControl);
    ac.start();
    
    // Finished setting up.
    settingUp = false;
  }
}


// Draws the plot.
void draw() {
  textSize(14);
  noStroke();
  background(0, 0, 1);
  
  if (!settingUp) {
    // Flag to indicate whether the audio output should be silenced.
    boolean silenceAudio = true;
    
    // Plot data points as scatter plot.
    for (int row = 0; row < data.length(); row++) {
      // Get coordinate values from series. Multiply by width/height - 30 and then add 15 to allow 15 pixel margin on all sides.
      float x = xSeries.getFloat(row) * (width - 30) + 15;
      float y = ySeries.getFloat(row) * (height - 30) + 15;
      
      // Also set the colour based on the same series as we use to set the frequency, and set the hue based on the species.
      float hue = 0;
      float brightness = freqSeries.getFloat(row);
      fill(hue, 1, brightness, 0.7);
      
      // Draw the data point.
      ellipse(x, y, 30, 30);
      
      // If the mouse is hovering over this data point.
      if (isHovering(x, y)) {
        // Set audio frequency based on non-visible data.
        // Multiply by 900 and add 100 to get frequency range [100, 1000] Hertz. 
        float freq = freqSeries.getFloat(row) * 900 + 100;
        freqEnv.addSegment(freq, 10);
        
        // Set maximum volume.
        gainControl.setGain(1);
        
        // Set flag to indicate the audio should not be silenced below.
        silenceAudio = false;
      }
    }
    
    // If we're not hovering over any data point set the volume/gain to zero.
    if (silenceAudio) {
      gainControl.setGain(0);
    }
  }
}

// Returns true if the mouse is hovering near the given coordinates (less than 10 pixels distance).
boolean isHovering(float x, float y) {
  float dx = x - mouseX;
  float dy = y - mouseY;
  return Math.sqrt(dx*dx + dy*dy) < 15;
}