import hivis.common.*;
import hivis.data.*;
import hivis.data.view.*;


// Stores the data to plot
DataTable meanValuesBySpecies;

// A set of colours to render the pie charts in.
color[] palette;

// Dimensions of the pie chart
int diameter = 180;
int radius = diameter/2;

// Margin from the window edge. 
int margin = 20;

// Size of the legend items.
int legendItemSize = 150;

// Method containing one-off setup code.
void setup() {
  // Set canvas size to match pie chart size.
  size(1000, 260);
  
  // We automatically load the supplied data file by default. 
  // If you want to load your own data then comment the below line and uncomment the line beginning "selectInput(..." 
  fileSelected(sketchFile("iris.csv"));
  //selectInput("Select an xlsx or CSV file to visualise:", "fileSelected");
}


// Method that is called when a file is selected.
void fileSelected(File selection) {
  // If no file was selected.
  if (selection == null) {
    println("No file selected.");
  } 
  else {
    // Get data from the spread sheet. We start at the second column (using zero-based indexing).
    // The spread sheet reader will automatically update the DataTable if the source file is changed.
    DataTable rawData = HV.loadSpreadSheet(
      HV.loadSSConfig().sourceFile(selection).columnIndex(1)
    );
    
    // Split up the original table into sub-tables containing the rows belonging to the same species. 
    GroupedTable groupedData = rawData.group("Species");
    println("groupedData:");
    println(groupedData);
    
    // Get a table in which the table for each group/sub-table is aggregated into a 
    // single row using an aggregation function (in this case the mean of the series 
    // in the sub-table, or the first value of the series if it's not numeric). 
    meanValuesBySpecies = groupedData.aggregateMean();
    println();
    println("meanValuesBySpecies:");
    println(meanValuesBySpecies);

    // Make a colour palette based on the number of species.
    palette = HVDraw.makeRainbowPalette(meanValuesBySpecies.length(), 0.9, 0.8);
  }
}


// Draws the plot.
void draw() {
  background(0);
  noFill();
  stroke(255);
  textSize(17);
  
  // If we've loaded some data.
  if (meanValuesBySpecies != null) {
    // Get the series to plot.
    DataSeries petalLength = meanValuesBySpecies.get("Petal.Length");
    DataSeries petalWidth = meanValuesBySpecies.get("Petal.Width");
    
    textAlign(CENTER, TOP);
    
    // Draw a pie chart showing the average petal length for each species.
    float x = margin + radius;
    HVDraw.pie(this, petalLength, diameter, x, radius+margin, palette, 0);
    fill(255, 255, 255);
    text("Petal Length", x, margin+diameter + 10);
    
    // Draw a pie chart showing the average petal width for each species.
    x += margin + diameter;
    HVDraw.pie(this, petalWidth, diameter, x, radius+margin, palette, 0);
    fill(255, 255, 255);
    text("Petal Width", x, margin+diameter + 10);
    
    // Draw a pie chart showing the average ratio of petal length to width for each species.
    x += margin + diameter;
    HVDraw.pie(this, petalLength.divide(petalWidth), diameter, x, radius+margin, palette, 0);
    fill(255, 255, 255);
    text("Petal Length / Width", x, margin+diameter + 10);
    
    
    // Make a fancy legend.
    float maxPetalLength = petalLength.max().getFloat();
    x += radius + margin + legendItemSize / 2;
    float y = margin;
    textAlign(CENTER, CENTER);
    noStroke();
    for (DataRow row: meanValuesBySpecies) {
      // Get width and height proportional to maximum petal length (then scale to legend item size).
      float width = (row.getFloat("Petal.Length") / maxPetalLength) * legendItemSize;
      float height = (row.getFloat("Petal.Width") / maxPetalLength) * legendItemSize;
      y += height / 2;
      
      fill(palette[row.getRowIndex()]);
      ellipse(x, y, width, height);
      
      fill(255, 255, 255);
      text(row.getString("Species"), x, y-3);
      
      y += height / 2 + 20;
    }
  }
}