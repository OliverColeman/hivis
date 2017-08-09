import hivis.common.*;
import hivis.data.*;
import hivis.data.reader.*;
import hivis.data.view.*;
import controlP5.*;
import java.util.*;

// Example of visualising data in a 3D plot, transforming the data with a 
// custom function, and using an interactive control to modify the behaviour
// of the function. A ControlP5 slider (shown at bottom of the window) controls
//  a parameter to the function. Try sliding it. 


// Data series associated with x and y coordinates and a series to contain 
// values based on the x and y series.
DataSeries xSeries;
DataSeries ySeries; 
CalcSeries.DoubleSeries func; 

// Minimum and maximum values for the x and y series.
float minValueX = 0;
float maxValueX = PI*2;
float minValueY = 0;
float maxValueY = PI*2;

// Length of the series (number of data points).
int dataLength = 100000;

// A ControlP5 slider to control one of the parameters for the calculated series.
Slider sliderParam;


void setup() {
  size(1000, 700, P3D);
  // Set frame rate so we can animate the visualisation.
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
  xSeries = HV.randomUniformSeries(dataLength, minValueX, maxValueX);
  ySeries = HV.randomUniformSeries(dataLength, minValueY, maxValueY);
  
  // Make a series that is a function of the x and y series. By supplying the 
  // series to the constructor we ensure the calculated series is updated if 
  // the underlying series change. A CalcSeries.DoubleSeries is a generic class 
  // for creating a DataSeries, representing double values, derived from zero, 
  // one or more other series. You just have to override the calcDouble method. 
  // There is also CalcSeries.FloatSeries, CalcSeries.IntSeries and CalcSeries.LongSeries.
  func = new CalcSeries.DoubleSeries(xSeries, ySeries) {
    public double calcDouble(int index) {
      double x = xSeries.getDouble(index);
      double y = ySeries.getDouble(index);
      
      // A function that will make for pretty patterns.
      double xPow = Math.pow(x, sliderParam.getValue());
      double yPow = Math.pow(y, sliderParam.getValue());
      double value = 1 / (Math.sin(xPow) + Math.cos(yPow) + 1);
      
      return value;
    }
  };
}

// Update the calculated values for the function when the slider value changes.
// This method is called by ControlP5 when the slider is adjusted. It must 
// match the name given to the slider.
void sliderParam(float value) {
  // (only if the calc series has been initialised).
  if (func != null) {
    func.update();
  }
}

// Current view rotation.
float rotateX = 0;
float rotateY = 0;

// Draw the visualisation.
void draw() {
  background(0, 0, 0);
  
  pushMatrix();
  
  // Scale and translate to x and y series coordinate space.
  scale(width / (maxValueX-minValueX), height / (maxValueY-minValueY));
  translate(-minValueX, -minValueY);
  
  // Animated rotation around centre of x and y series coordinate space.
  translate((maxValueX-minValueX) / 2, (maxValueY-minValueY) / 2);
  rotateX += PI/180;
  rotateX(rotateX % (PI*2));
  rotateY += PI/360;
  rotateY(rotateY % (PI*2));
  translate(-maxValueX / 2, -maxValueY / 2);

  // Plot each data point.
  for (int row = 0; row < dataLength; row++) {
    float xCoord = xSeries.getFloat(row);
    float yCoord = ySeries.getFloat(row);
    float value = func.getFloat(row);
    
    // Color is associated with function value
    fill(abs(value % 1), 1, 1);
    
    pushMatrix();
    
    // Draw rect at x and y according to this data point, with function 
    // value associated with z axis.
    translate(xCoord, yCoord, value);
    rect(0, 0, 0.05, 0.05);
    
    popMatrix();
  }
  
  popMatrix();
}