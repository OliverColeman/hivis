import hivis.common.*;
import hivis.data.*;
import hivis.data.reader.*;
import hivis.visual.*;
import hivis.data.view.*;
import hivis.interact.*;
import controlP5.*;
import java.util.*;

// Example of visualising data in a 3D plot.
// Exercises:
// 1. Play around with different functions.
// 2. Add more sliders or other control elements to allow interactively setting the values for other parameters or control the animated rotation.


// Data series associated with x and y coordinates and a series to contain values based on the x and y series.
DataSeries<Double> x;
DataSeries<Double> y; 
CalcSeries.Real func; 

// Minimum and maximum values for the x and y series.
double minValueX = 0;
double maxValueX = PI*2;
double minValueY = 0;
double maxValueY = PI*2;

// Length of the series (number of data points).
int dataLength = 100000;

// A ControlP5 slider to control one of the parameters for the calculated series.
Slider sliderParam;


void setup() {
  size(1000, 700, P3D);
  // Set frame rate so we can animate the visualiation.
  frameRate(24);
  colorMode(HSB, 1, 1, 1);
  noStroke();
  
  // Add a slider to control one of the parameters for the function.
  ControlP5 cp5 = new ControlP5(this);
  sliderParam = cp5.addSlider("sliderParam")
      .setRange(1, 3)
      .setValue(1.5)
      .setPosition(15, height-15)
      .setSize(width-30, 10)
      .setLabelVisible(false);

  // Populate the x and y series with some random "data".
  x = HV.randomUniformSeries(dataLength, minValueX, maxValueX);
  y = HV.randomUniformSeries(dataLength, minValueY, maxValueY);
  
  // Make a series that is a function of the x and y series.
  // CalcSeries.Real produces a DataSeries<Double> from one or more input series. CalcSeries.Real<Double> indicates that the CalcSeries.Real takes DataSeries<Double> as the input series. 
  func = new CalcSeries.Real<Double>(x, y) {
    public double calcReal(int index) {
      // A CalcSeries may be calculated from one or more input series. We've supplied two series (x and y). inputSeries.get(0) and inputSeries.get(1) gets a reference to these series respectively.
      double x = inputSeries.get(0).get(index);
      double y = inputSeries.get(1).get(index);
      
      // A function that will make for pretty patterns.
      double xPow = Math.pow(x, sliderParam.getValue());
      double yPow = Math.pow(y, sliderParam.getValue());
      double value = 1 / (Math.sin(xPow) + Math.cos(yPow) + 1);
      
      return value;
    }
  };
}

// Update the calculated values for the function when the slider value changes.
void sliderParam(float value) {
  // (only if the calc series has been initialised).
  if (func != null) {
    func.updateView(null);
  }
}

// Current view rotation.
float rotateX = 0;
float rotateY = 0;

// Draws the chart and a title.
void draw() {
  background(0, 0, 0);
  
  pushMatrix();
  
  // Scale and translate to x and y series coordinate space.
  scale(width / (float) (maxValueX-minValueX), height / (float) (maxValueY-minValueY));
  translate((float) -minValueX, (float) -minValueY);
  
  // Animated rotation around centre of x and y series coordinate space.
  translate((float) (maxValueX-minValueX) / 2, (float) (maxValueY-minValueY) / 2);
  rotateX += PI/180;
  rotateX(rotateX % (PI*2));
  rotateY += PI/360;
  rotateY(rotateY % (PI*2));
  translate((float) -maxValueX / 2, (float) -maxValueY / 2);

  // Plot each data point.
  for (int row = 0; row < dataLength; row++) {
    float xCoord = (float) x.getDouble(row);
    float yCoord = (float) y.getDouble(row);
    float value = (float) func.getDouble(row);
    
    // Color is associated with function value
    fill(Math.abs(value % 1), 1, 1);
    
    pushMatrix();
    
    // Draw rect at x and y according to this data point, with function value associated with z axis.
    translate(xCoord, yCoord, value);
    rect(0, 0, 0.05, 0.05);
    
    popMatrix();
  }
  
  popMatrix();
}

