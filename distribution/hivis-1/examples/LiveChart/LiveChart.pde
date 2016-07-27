import hivis.data.*;
import hivis.data.reader.*;
import hivis.chart.*;
import hivis.data.view.*;
import hivis.interact.*;
import controlP5.*;
import org.gicentre.utils.stat.*;
import java.util.regex.Pattern;

// Displays a simple line chart using the selected spreadsheet.
 
LineChartGI lineChart;
 
// Loads data into the chart and customises its appearance.
void setup() {
  size(1000, 500);
 
  selectInput("Select an excel file to plot:", "fileSelected", sketchFile("LiveChart"));
  
  //fileSelected(new File("mtcars.xlsx"));
}


void fileSelected(File selection) {
  if (selection == null) {
    println("No file selected.");
  } 
  else {
    // Get data from spread sheet. The SpreadSheetReader will automatically update the DataTable it provides.
    SpreadSheetReader reader = new SpreadSheetReader(selection.getAbsolutePath());
    DataTable data = reader.getData();
    println(data);
    
    DataTable stats = data.apply(new SeriesStats(), true);
    println("stats:\n" + stats);
    
    ViewTableSeriesParametrised dataSelect = new ViewTableSeriesParametrised(stats);
    ParametrisedP5 controller = new ParametrisedP5(this, dataSelect, "select");
    
    lineChart = new LineChartGI(this, dataSelect);    
  }
}


// Draws the chart and a title.
void draw() {
  background(0, 0, 0);
  textSize(12);
  if (lineChart != null) {
    lineChart.draw(100,15,width-130,height-30);
  }
}