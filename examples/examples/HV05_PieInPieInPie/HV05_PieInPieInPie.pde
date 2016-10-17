import hivis.common.*;
import hivis.data.*;
import hivis.data.reader.*;
import hivis.data.view.*;

// Uses pie charts to represent proportional data over several groups under several classifications.


// Stores the data to plot. MF = male/female; Eth = ethnicity.
DataTable data;
DataTable popMF;
DataTable popEth;
DataTable socialMF;
DataTable socialEth;
DataTable techMF;
DataTable techEth;

// This is a flag to indicate that data is being (re)loaded and so the plot should not be drawn yet.
boolean settingUp = true;

// A nice colour palette to use.
color[] palette = HVDraw.PASTEL8;

void setup() {
  size(1000, 1000);
  textSize(14);
  
  // Get data from spread sheet. 
  int sheet = 0;
  int headerRow = 1;
  int firstDataRow = 2;
  int firstDataColumn = 0;
  data = HV.loadSpreadSheet(sketchFile("Employee Diversity in tech.xlsx"), sheet, headerRow, firstDataRow, firstDataColumn);
  
  // Get male/female data (with names as first series).
  DataTable mf = HV.newTable().addSeries(data.selectSeries(0)).addSeries(data.selectSeriesRange(1, 2));
  // Get ethnicity data (with names as first series).
  DataTable eth = HV.newTable().addSeries(data.selectSeries(0)).addSeries(data.selectSeriesRange(3, 8));
  
  
  // Split into population, social media and tech company subsets.
  popMF = mf.selectRows(0);
  popEth = eth.selectRows(0);
  println("\npopMF:\n" + popMF);
  println("\npopEth:\n" + popEth);
  
  socialMF = mf.selectRowRange(3, 12);
  socialEth = eth.selectRowRange(3, 12);
  println("\nsocialMF:\n" + socialMF);
  println("\nsocialEth:\n" + socialEth);
  
  techMF = mf.selectRowRange(16, 33);
  techEth = eth.selectRowRange(16, 33);
  println("\ntechMF:\n" + techMF);
  println("\ntechEth:\n" + techEth);
  
  settingUp = false;
}


// Draws the plot.
void draw() {
  background(255);
  noStroke();
  
  if (!settingUp) {
    // Make the legend.
    textAlign(LEFT, TOP);
    textSize(14);
    for (int c = 0; c < 8; c++) {
      fill(palette[c]);
      
      int y = c * 20;
      // Split male/female and ethnicity by shifting down ethnicity 20 pixels.
      if (c >= 2) y += 20;
      
      rect(0, y, 40, 20);
      
      // Get the label from the original data table.
      String label = data.getSeriesLabel(c+1);
      label = label.replace("%", "").trim(); // Get rid of the % sign and remaining spaces.
      text(label, 45, y);
    }
    
    // Size of the biggest pie chart (for population).
    int diameter = round(height * 0.24);
    
    int xCentre = width / 2;
    int yCentre = height / 2;
    
    // Draw population pie chart.
    makePieInPie(popMF, popEth, 0, diameter, xCentre, yCentre);
    
    // Draw pie chart for each row of the social data.
    float ringRadius = diameter; // The radius of the circle of pie charts.
    float pieSize = diameter * 0.5;
    for (int p = 0; p < socialMF.length(); p++) {
      // The social pie charts are distributed evenly in a circle around the centre.
      
      // First determine the angle (relative to horizontal, in radians) from the centre of window to where we want to put social pie chart p.
      float angle = (p * TWO_PI) / socialMF.length();
      
      // We use trigonometry to determine the position of pie chart p, relative to the centre of the window. 
      float x = xCentre + sin(angle) * ringRadius;
      float y = yCentre + cos(angle) * ringRadius;
      
      makePieInPie(socialMF, socialEth, p, pieSize, x, y);
    }
    fill(0, 0, 0, 95);
    textSize(50);
    text("S o c i a l", xCentre, yCentre - ringRadius - pieSize + 40);
    textSize(14);
    
    // Draw pie chart for each row of the tech data.
    ringRadius = diameter * 1.725; // The radius of the circle of pie charts.
    for (int p = 0; p < techMF.length(); p++) {
      // Spin it around 3 spots so that "Apple (excluding undeclared)" is at the bottom.
      float angle = ((p - 3) * TWO_PI) / techMF.length();
      float x = xCentre + sin(angle) * ringRadius;
      float y = yCentre + cos(angle) * ringRadius;
      makePieInPie(techMF, techEth, p, pieSize, x, y);
    }
    fill(0, 0, 0, 95);
    textSize(50);
    text("T e c h n o l o g y", xCentre, yCentre - ringRadius - pieSize + 40);
  }
}

// Draws a double pie chart using the specified row of the two given data tables.
// diameter, x and y specify the size and center of the pie chart.
void makePieInPie(DataTable mf, DataTable eth, int row, float diameter, float x, float y) {
  HVDraw.pie(this, eth, row, diameter, x, y, palette, 2);
    
  fill(255);
  ellipse(x, y, diameter * 0.6, diameter * 0.6);

  HVDraw.pie(this, mf, row, diameter * 0.5, x, y, palette, 0);
  
  fill(255);
  ellipse(x, y, diameter * 0.2, diameter * 0.2);
  
  // Draw label.
  fill(64);
  textAlign(CENTER, TOP);
  text(mf.getSeries(0).get(row).toString(), x, y + diameter / 2);
}