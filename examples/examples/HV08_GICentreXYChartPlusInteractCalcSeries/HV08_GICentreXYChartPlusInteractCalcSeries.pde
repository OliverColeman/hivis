import hivis.common.*;
import hivis.data.*;
import hivis.data.reader.*;
import hivis.data.view.*;
import controlP5.*;
import org.gicentre.utils.stat.*;

// Example of creating scatter or line chart using the XYChart object provided by giCentre (see http://www.gicentre.net/utils/chart)
// The ChartGI sketch file integrates the XYChart object with HiVis such that when the DataTable to chart is modified the 
// chart will automatically be updated.
// Also provides examples of:
//  * selecting series via ControlP5 scrollableLists.
//  * creating calculated series (CalcSeries) that are controlled via ControlP5 controllers.
//  * creating a table with filtered rows controlled via ControlP5 controllers.


// The source data from which to select series to plot.
DataTable cars; 

// The plot.
ChartGI scatterPlot;

// ControlP5 elements used to select the series to plot.
ScrollableList selectX;
ScrollableList selectY;

// ControlP5 elements that allow controlling the thresholding of the series being plotted.
Slider sliderX;
Slider thresholdY;

// A view of the series supplying the X/Y values with a function applied to them.
SeriesView calcSeriesX;

// The table containing the values for x and y.
TableView toPlot;

// Set up the data tables and the ControlP5 elements.
void setup() {
  size(1000, 500);
  textSize(15);
 
  // Get the "MT Cars" dataset.
  cars = HV.mtCars().removeSeries(0);
  
  // Add an "index" series to it so we can plot against it if desired.
  cars.addSeries("index", HV.integerSequence(cars.length(), 0, 1));
  
  println(cars);
  
  ControlP5 cp5 = new ControlP5(this);
  
  String[] seriesLabels = cars.getSeriesLabels().toArray(new String[0]);
  
  selectX = cp5.addScrollableList("selectSeriesX")
      .setPosition(width-210, 5)
      .setSize(100, 100)
      .setBarHeight(15)
      .setItemHeight(15)
      .addItems(seriesLabels);
  selectY = cp5.addScrollableList("selectSeriesY")
      .setPosition(width-105, 5)
      .setSize(100, 100)
      .setBarHeight(15)
      .setItemHeight(15)
      .addItems(seriesLabels);
  
  sliderX = cp5.addSlider("sliderX")
      .setRange(0.5, 2)
      .setValue(2)
      .setPosition(15, height-15)
      .setSize(width-30, 10)
      .setLabelVisible(false);
      
  thresholdY = cp5.addSlider("thresholdY")
      .setPosition(5, 5)
      .setSize(10, height-30)
      .setLabelVisible(false);
  
}

// Called when a selection is made via the selectSeriesX select box.
void selectSeriesX(int series) {
  selectSeries();
}
// Called when a selection is made via the selectSeriesY select box.
void selectSeriesY(int series) {
  selectSeries();
}

// Update the series to plot.
void selectSeries() {
  int seriesXIndex = (int) selectX.getValue();
  int seriesYIndex = (int) selectY.getValue();
  
  // Reset min and max values for threshold Y slider.
  float minY = cars.getSeries(seriesYIndex).min().getFloat();
  float maxY = cars.getSeries(seriesYIndex).max().getFloat();
  thresholdY.setRange(minY + 0.0001, maxY);
  thresholdY.setValue(maxY);
  
  // Get the series for the x values, with values represented as real (double) numbers.
  // Must be final so we can access it in the anonymous inner class below.
  final DataSeries seriesX = cars.getSeries(seriesXIndex);
  
  // Make a series that raises the values in the x series to the exponent given by the x slider.
  // CalcSeries.DoubleSeries produces a DataSeries<Double> from one or more input series.
  calcSeriesX = new CalcSeries.FloatSeries(seriesX) {
    public float calcFloat(int index) {
      // Math.pow returns a 'double' float, have to cast back to float.
      return (float) Math.pow(seriesX.getFloat(index), sliderX.getValue());
    }
  };
  
  // Make a table containing the series to plot, using the thresholded views of the selected series.
  String xLabel = cars.getSeriesLabel(seriesXIndex);
  String yLabel = cars.getSeriesLabel(seriesYIndex);
  // A DataTable cannot have series with the same label, so add a . after the y label if it's the same as the x label.
  if (yLabel.equals(xLabel)) {
    yLabel = yLabel + ".";
  }
  
  DataTable toPlotUnfiltered = HV.newTable()
      .addSeries(xLabel, calcSeriesX)
      .addSeries(yLabel, cars.getSeries(seriesYIndex));
  
  // Get a view of the table to plot which filters out rows which contain a Y value outside the threshold value set by the Y slider.
  toPlot = toPlotUnfiltered.selectRows(new RowFilter() {
    public boolean excludeRow(DataTable input, int index) {
      return input.getSeries(1).getFloat(index) > thresholdY.getValue();
    }
  });
  
  // Make a scatter plot of the selected thresholded series.
  scatterPlot = new ChartGI(this, toPlot);
  
  // Get the maximum value for the x series.
  float maxX = cars.getSeries(seriesXIndex).max().getFloat();
  // Set the max possible value for the x series after applying exponential function.
  scatterPlot.chart.setMaxX((float) Math.pow(maxX, sliderX.getMax()));
  
  // Set the chart x axis label.
  scatterPlot.chart.setXAxisLabel(toPlot.getSeriesLabel(0) + "^" + sliderX.getValue());
}


void sliderX(float value) {
  // Update the calculated values for the x series when the slider value changes.
  // (only if the calc series has been initialised).
  if (calcSeriesX != null) {
    calcSeriesX.updateView(null);
    
    // Also update the chart x axis label
    scatterPlot.chart.setXAxisLabel(toPlot.getSeriesLabel(0) + "^" + sliderX.getValue());
  }
}

void thresholdY(float value) {
  // Update the filtered table to plot.
  // (only if it has been initialised).
  if (calcSeriesX != null) {
    toPlot.updateView(null);
  }
}

// Draws the chart and a title.
void draw() {
  background(0, 0, 0);
  
  // Check that the plot has been created before trying to draw it.
  if (scatterPlot != null) {
    scatterPlot.draw(15,15,width-110,height-30);
  }
}