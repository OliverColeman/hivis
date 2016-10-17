import hivis.common.*;
import hivis.data.*;
import hivis.data.reader.*;
import hivis.data.view.*;
import beads.*;

// THIS EXAMPLE REQUIRES the Beads Library (Sketch -> Import Library -> Add Library ...)
// Example of using sonification to "render" additional dimensions of the data. 
// A scatter plot is produced, with two of the dimensions used for  the coordinates, and 
// another dimension used to control the frequency of a tone when hovering over a data point.
// Exercises:
//  1. Set the volume/gain of the tone based on another series in the data table.
//  2. When hovering over a data point, display text that lists the values for that point in each series.


// Stores the data to plot.
DataTable data;

// The series/columns containing data to plot.
DataSeries xSeries;
DataSeries ySeries;
DataSeries freqSeries;

// Used to control the frequency of the audio when hovering over a data point.
Envelope freqEnv;
Gain gainControl;

// This is a flag to indicate that data has not been loaded and so the plot may not be drawn yet.
boolean settingUp = true;


void setup() {
  size(1000, 1000);
  
  // Allow specifying colours using the Hue, Saturation, Brightness colour space, with unit [0, 1] ranges.
  colorMode(HSB, 1, 1, 1, 1);

  // Ask the user to select a spreadsheet to visualise.
  selectInput("Select an excel file to visualise:", "fileSelected");
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
    
    // Get the series/columns we're interested in.
    // Transform to unit range [0, 1] to make them easier to work with.
    xSeries = data.getSeries(0).toUnitRange();
    ySeries = data.getSeries(1).toUnitRange();
    freqSeries = data.getSeries(2).toUnitRange();
    
    // Set-up audio.
    AudioContext ac = new AudioContext();
    freqEnv = new Envelope(ac);
    WavePlayer wp = new WavePlayer(ac, freqEnv, Buffer.SINE);
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
      
      // Also set the colour based on the same series as we use to set the frequency.
      float brightness = freqSeries.getFloat(row);
      fill(0.8, 1, brightness, 0.7);
      
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