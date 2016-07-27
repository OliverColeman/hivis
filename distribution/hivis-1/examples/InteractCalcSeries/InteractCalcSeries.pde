import hivis.common.*;
import hivis.data.*;
import hivis.data.reader.*;
import hivis.chart.*;
import hivis.data.view.*;
import hivis.interact.*;
import controlP5.*;
import org.gicentre.utils.stat.*;

// Displays a scatter plot of the selected series, optionally thresholding the data using ControlP5 sliders.

// Provides example of selecting series via ControlP5 scrollableLists.
// Provides example of creating calculated series (CalcSeriesP5) that are controlled via ControlP5 controllers.

// The source data from which to select series to plot.
DataTable cars; 

// Table containing basic stats over the cars table.
DataTable labelledStats;

// The plot.
LineChartGI scatterPlot;

// ControlP5 elements used to select the series to plot.
ScrollableList selectX;
ScrollableList selectY;

// ControlP5 elements that allow controlling the thresholding of the series being plotted.
Slider thresholdX;
Slider thresholdY;

// Set up the data tables and the ControlP5 elements.
void setup() {
  size(1000, 500);
  textSize(15);
 
  // Get the "MT Cars" dataset.
  cars = HV.mtCars();
  
  // Add an "index" series to it so we can plot against it if desired.
  cars.addSeries("index", HV.integerSequence(cars.length(), 0, 1));
  
  println(cars);
  
  // Get a transposed view of the cars table.
  DataTable carsT = cars.transpose();
  
  // Get stats (min, max, mean, median, std. dev.) over each series in the cars table.
  DataTable stats = carsT.apply(new SeriesStats(), false);
  
  // Make a table containing a series containing the variables labels from the cars table
  // and the stats series.
  labelledStats = HV.newTable()
      .addSeries("Variable", carsT.get(0))
      .addSeries(stats);
  
  println("stats:\n" + labelledStats);
  
  ControlP5 cp5 = new ControlP5(this);
  
  // Get the list of series that may be plotted as an array of strings.
  // the first series in carsT contains the series to plot.
  String[] series = carsT.get(0).asStringArray();
  
  selectX = cp5.addScrollableList("selectSeriesX")
      .setPosition(width-210, 5)
      .setSize(100, 100)
      .setBarHeight(15)
      .setItemHeight(15)
      .addItems(series);
  selectY = cp5.addScrollableList("selectSeriesY")
      .setPosition(width-105, 5)
      .setSize(100, 100)
      .setBarHeight(15)
      .setItemHeight(15)
      .addItems(series);
  
  thresholdX = cp5.addSlider("thresholdX")
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
  // Plus 1 because we ignore the "model" series in the cars table.
  int seriesXIndex = (int) selectX.getValue() + 1;
  int seriesYIndex = (int) selectY.getValue() + 1;
  
  // Reset min and max for threshold X slider.
  // First get the index of the row in the stats table containing the stats for the first series to plot (which may be thresholded).
  double minX = labelledStats.get("min").getDouble(seriesXIndex-1);
  double maxX = labelledStats.get("max").getDouble(seriesXIndex-1);
  // Set the min and max values for the slider. We have to cast to float because most processing APIs expect the lower precision float number type rather than the higher precision double number type.
  thresholdX.setRange((float) minX, (float) maxX);
  thresholdX.setValue((float) maxX);
  
  // Reset min and max for threshold Y slider.
  double minY = labelledStats.get("min").getDouble(seriesYIndex-1);
  double maxY = labelledStats.get("max").getDouble(seriesYIndex-1);
  thresholdY.setRange((float) minY, (float) maxY);
  thresholdY.setValue((float) maxY);
  
  // Make a series that thresholds the series selected for the X values based on the slider position.
  // When the slider value changes the series values will be recalculated using the apply methods.
  DataSeries thresholdedSeriesX = new CalcSeriesP5(cars.get(seriesXIndex), thresholdX) {
    // We need double and int apply methods because some of the series store real (double) values and some store integer (int) values. 
    public double apply(double input) {
      return Math.min(input, thresholdX.getValue());
    }
    public int apply(int input) {
      return (int) Math.min(input, thresholdX.getValue());
    }
  };
  
  // Make a series that thresholds the series selected for the Y values based on the slider position.
  DataSeries thresholdedSeriesY = new CalcSeriesP5(cars.get(seriesYIndex), thresholdY) {
    public double apply(double input) {
      return Math.min(input, thresholdY.getValue());
    }
    public int apply(int input) {
      return (int) Math.min(input, thresholdY.getValue());
    }
  };
  
  // Make a table containing the series to plot, using the thresholded views of the selected series.
  DataTable toPlot = HV.newTable()
      .addSeries(cars.getSeriesLabel(seriesXIndex), thresholdedSeriesX)
      .addSeries(cars.getSeriesLabel(seriesYIndex), thresholdedSeriesY);
  
  // Make a scatter plot of the selected thresholded series.
  scatterPlot = new LineChartGI(this, toPlot);
}



// Draws the chart and a title.
void draw() {
  background(0, 0, 0);
  
  // Check that the plot has been created before trying to draw it.
  if (scatterPlot != null) {
    scatterPlot.draw(15,15,width-110,height-30);
  }
}
