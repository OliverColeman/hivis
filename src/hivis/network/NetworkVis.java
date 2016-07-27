/**
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA
 */

package hivis.network;

import processing.core.*;
import controlP5.*;

import java.awt.Toolkit;
import java.io.*;
import java.util.*;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;


public class NetworkVis extends PApplet {
	
	java.awt.Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	double screenWidth = screenSize.getWidth();
	double screenHeight = screenSize.getHeight();
	
	// Main Variables:
	
	public static NetworkVis _instance;
	//NetworkVis () {};
	public String path;
	int sizeX;
	int sizeY;
    float f1;
    float f2;
	int[][] dataType;
	String[][] xlsData;
	protected String[] names;	// 1-d array to store the nodes
	protected boolean[][] Adjacency;	// 2-d array to store adjacencies between vertices
	protected int numVertices;
	protected int[] axisNumber; // assignment of node to axis
	int[][] nodeDegree;
	float[] clustCoeff;
	protected boolean drawFlg = false;
	Edge[] allEdges = new Edge[13500]; // store curves
	ControlP5 cp5;
	Textarea myTextarea;
	Sheet sheet=null;
	InputStream inp=null;
	Workbook wb=null;
	int baseEdgeClr; // color of edges
	int selectEdgeClr;
	String selectedNode;
	String selectedMethod;
	String textInput;
	int selNodeinput;
	int highlightedRow;
	//int textNodeidx;
	int edgecount = 0;
		
	  public static void main(String args[]) {
		    PApplet.main(new String[] {"interactiveNetworkVisualization.NetworkVis"});
		    
		  }
	  
	  public void settings() {
		  size((int) (screenWidth/2), (int) (screenHeight - 50));
		  f1 = (float) (height*0.05);
		  f2 =  (float) (height*0.4);
		}
	  
	  public void setup() {	
		  selectInput("Select excel file","fileSelected");
		  baseEdgeClr = getIntFromColor(130,130,130);
		  selectEdgeClr = getIntFromColor(0,0,130);
		  smooth();
		  background(0);
		  frameRate(10);
	//	  font = createFont("Arial",14,true);
		  //noLoop();
		  
		  
		  cp5 = new ControlP5(this);

		    		   
		  }

		  public void draw() {
			  background(0);
		    // If Node information known:		   		    	
		    	if (drawFlg) {
		    		pushMatrix();
				    stroke(255);
				    translate((float)(width/2), (float) (height*0.6));		    		 
				    line(0, -f1, 0, -f2); // axis 1 - source
				    rotate(PI/2);
				    line(0, -f1, 0, -f2); // axis 2 - manager
				    rotate(PI/3);
				    line(0, -f1, 0, -f2); // duplicate manager
				    rotate(PI/2);
				    line(0, -f1, 0, -f2); // axis 3 - sink
				    rotate(2*PI/3); // get back to starting frame
				   
		    		for (int i = 0; i < allEdges.length; i++) {
		    			if (allEdges[i] != null) {
		    				if (selectedNode != null) {
		    					int thisNode = Integer.parseInt(selectedNode);
	    						if (xlsData[1][i] == names[thisNode] || xlsData[0][i] == names[thisNode]) {
	    							allEdges[i].display();	    							
	    						}
		    				} else if (textInput!= null) {
		    					if (xlsData[1][i] == names[selNodeinput] || xlsData[0][i] == names[selNodeinput]) {
	    							allEdges[i].display();
	    						}	    				
		    				} else {allEdges[i].display();	
		    				}
		    			} 
		    		}		    		
		    		popMatrix();
		    		//drawFlg = false;
		    	   } 
		    			   
		  } // end draw
		  	  
		  public void fileSelected(File selection) {

			  if (selection == null) {
			    println("No file selected.");
			  } else {
			    println("User selected:  " + selection.getAbsolutePath());
			    path = selection.getAbsolutePath();
			  }
			  
			  readExcel(path);
			  getAdjacency(xlsData);
			  getNetworkParams(names);	
			  
			  List<String> coordMethods = Arrays.asList("degree", "clusteringcoefficient");
			  List<String> l = Arrays.asList(names);
			  
			  ScrollableList a = cp5.addScrollableList("nodelist")
			     .setPosition(20,150)
			     .setSize(200, 100)
			     .setBarHeight(20)
			     .setItemHeight(20)
			     .addItems(l)
			     .hide()
			     ;
			  
			  ScrollableList b = cp5.addScrollableList("coordMapping")
			     .setPosition(20,50)
			     .setSize(200, 100)
			     .setBarHeight(20)
			     .setItemHeight(20)
			     .addItems(coordMethods)
			     .hide()
			     ;
			  
			  Textfield c = cp5.addTextfield("input")
			     .setPosition((float)(width-220),(float)(50))
			     .setSize(200,40)			     
			     .setFocus(true)
			     .setColor(color(255,0,0))
			     .hide()
			     ;
			  
			  myTextarea = cp5.addTextarea("txt")
	                  .setPosition((float)(width-220),(float)(150))
	                  .setSize(200,100)
	                  .setFont(createFont("arial",12))
	                  .setLineHeight(14)
	                  .setColor(color(128))
	                  .setColorBackground(color(255,100))
	                  .setColorForeground(color(255,100))
			  		  .hide()		  		
	                  ;
			  
			  
	                  
	                  // create new Timer task
	                  TimerTask task = new FileWatcher( new File(path), path);
	                  // create timer
	                  Timer timer = new Timer();	                  
	                  timer.schedule( task, new Date(), 10 ); // check every 10ms
	                  
	                  drawNetwork();
	                  a.show();
	    			  b.show();
	    			  c.show();
	    			  myTextarea.show();
		  			  
		  }
		  
		  public void readExcel(String filepath) {
			  try {
				    inp = new FileInputStream(path);
				  }
				  catch(Exception e) {
				  }
				  try {
				    wb = WorkbookFactory.create(inp);
				  }
				  catch(Exception e) {
				  }
				  Sheet sheet = wb.getSheetAt(0);
				  sizeY = sheet.getLastRowNum()+1; // +1 for index convention
				  sizeX = sheet.getRow(0).getLastCellNum();
				  
				  dataType = new int[sizeX][sizeY];
				  xlsData = new String[sizeX-1][sizeY];
				  for (int i=0; i<sizeX; ++i) {
				    for (int j=0; j<sizeY-1; ++j) {
				      Row row = sheet.getRow(j+1);
				      try {
				        Cell cell = row.getCell(i);
				        CellStyle style = cell.getCellStyle();
				        if(renderColor(style.getFillForegroundColorColor()) !=  "(none)") {
				        	highlightedRow = j;
				        	println("Found highlighted row: "+j);
				        }
				        dataType[i][j] = cell.getCellType();
				        if (cell.getCellType()==1)			       
				        	xlsData[i][j] = cell.getStringCellValue();
				      }
				      catch(Exception e) {
				      }
				    }
				  }			  
				  println("Excel file imported successfully!");				  
		  }
		  
		  
		public void getAdjacency(String[][] xlsData) {
			  
			  // create 1d array of values
			  String[] xls1 = new String[xlsData[0].length];
			  for (int i = 0; i<xlsData[0].length; i++) {
				  xls1[i] = xlsData[0][i];
			  }
			  
			  String[] xls2 = new String[xlsData[1].length];
			  for (int i = 0; i<xlsData[1].length; i++) {
				  xls2[i] = xlsData[1][i];
			  }
			  
			  int aLen = xls1.length;
			  int bLen = xls2.length;
			  
			  String[] xlsLong = new String[aLen+bLen];
			  System.arraycopy(xls1, 0, xlsLong, 0, aLen);
			  System.arraycopy(xls2, 0, xlsLong, aLen, bLen);
			  
			  // find unique values in 1d array
			  Set<String> temp = new LinkedHashSet<String>( Arrays.asList( xlsLong ) );
			  String[] fullnames = temp.toArray( new String[temp.size()] );
			
			  // remove any null values
			  names = new String[0];
			  for (int i=0; i<fullnames.length; i++) {
				  if (fullnames[i] != null) {
					  names = Arrays.copyOf(names, names.length+1);
					  int l = names.length; 
			  		  names[l-1] = fullnames[i];
				  }
			  }
				  
			  println("Found " + names.length + " unique nodes");
			  
			  // Assumes directed network:			  
			  Adjacency = new boolean[names.length][names.length];
			  //Arrays.fill(Adjacency, boolean.false);
			  
			  
			 // loop cells of adjacency matrix - we can half the work here if network is undirected!
			  for (int i = 0; i<names.length; i++) {
				  for (int j = 0; j<names.length; j++) {
					boolean ii = false;
					// loop rows of xlsData and look for names corresponding to current adjacency cell
					for (int k = 0; k<xlsData[0].length; k++) {
						if (xlsData[0][k] == names[i]) {
							if (xlsData[1][k] == names[j]) {
								ii = true;							
								Adjacency[i][j] = true;
								edgecount = edgecount+1;
								
							}
						} 
					}								
						if (!ii) {
							Adjacency[i][j] = false;
						}											
				  }
			  }
			  println("Adjacency Matrix Created");	
			  println("Found "+ edgecount + " edges");
		  } // end getAdjacency
		  
		public void getNetworkParams(String[] vertices)
		{			
			numVertices = vertices.length;
			nodeDegree = new int[numVertices][2];
			clustCoeff = new float[numVertices];
			
			for(int i = 0; i < numVertices; i++) {
				for(int j = 0; j<names.length; j++) {
				if(vertices[i].equals(names[j])) {
					nodeDegree[i]=degree(j);
					clustCoeff[i]=clusteringCoefficient(j);
					println(names[j] + " degree In: " + nodeDegree[i][1] + " degree Out: " + nodeDegree[i][0]);
				}
				}
			}
			
							
		} // end getNetworkParams
		
		public int[] degree(int index)
		{
	        	int numNeighborsIn = 0;
	        	int numNeighborsOut = 0;

			// Scan row/column of vertex in the adjacency matrix, counting the number of neighbors. 
	        // Do this for the two triangles of the matrix. 
	        	for (int j = 0; j <= index; j++)
	        		if(Adjacency[index][j])
	            			numNeighborsOut++;
	        
	        	for (int j = index+1; j < numVertices; j++)
	        		if(Adjacency[j][index])
	        			numNeighborsIn++;
	        	
	        	for (int j = 0; j <= index; j++)
	        		if(Adjacency[j][index])
	            			numNeighborsIn++;
	        
	        	for (int j = index+1; j < numVertices; j++)
	        		if(Adjacency[index][j])
	        			numNeighborsOut++;
	        	
	        	int[] numNeighbors = new int[2];
	        	numNeighbors[0]=numNeighborsOut;
	        	numNeighbors[1]=numNeighborsIn;
	        	return numNeighbors;	
		} // end degree
		
		public float clusteringCoefficient(int i)
		{

			// Get the indices of the neighbors of vertex i
			int[] neighbors = getNeighbors(i);

			// initialize number of edges-in-neighborhood to 0
			int edgesInNbd = 0;

			// Scan pairs of neighbors and increment counter whenever
			// there is an edge
			for(int j = 0; j < neighbors.length; j++)
				for(int k = 0; k < j; k++)
				{
					// Give names to the indices of the jth neighbor and the kth neighbor
					int vj = neighbors[j];
					int vk = neighbors[k];

					// Check the appropriate slot of the Edges matrix to check
					// if there is an edge between vertices vj and vk
					if((vj >= vk)&&(Adjacency[vj][vk]))
						edgesInNbd++;
					if((vj < vk)&&(Adjacency[vk][vj]))
						edgesInNbd++;
						
				}

			// if there are no neighbors or one neighbor then, clustering 
			// coefficient is trivially defined to  be 1. Otherwise, 
			// compute the ratio of number of edges in neighborhood to 
			// the total number of pairs of neighbors
			if(neighbors.length <= 1)
				return 1;
			else 
				return (float) (edgesInNbd*2.0/(neighbors.length*(neighbors.length-1)));

		}
		
    	public int[] getNeighbors(int index)
    	{
        	int[] _numNeighbors = degree(index);
        	int numNeighbors = _numNeighbors[0] + _numNeighbors[1];
        	int[] neighbors = new int[numNeighbors];

		// Scan the row corresponding to vertex in the adjacency matrix 
        	numNeighbors = 0;
        
        	for(int j = 0; j < numVertices; j++)
        	{
        		boolean edge = false;
        		if (j <= index) edge = Adjacency[index][j];
				else edge = Adjacency[j][index];

        		if(edge)
            			neighbors[numNeighbors++] = j;
        	}
        	return neighbors;
    	}
		
		public void drawNetwork() {
    		
	    // Loop nodeDegree find max degree to normalize axes
	 	// Under same loop get indices for source, manager sink
	    		int maxDegree = 0;
	    		float maxclustCoeff = 0;
	    		axisNumber = new int[numVertices];
	    		for (int i=0; i<numVertices; i++) {
	    			int deg = nodeDegree[i][0] + nodeDegree[i][1];
	    			if (deg > maxDegree) {
	    				maxDegree = deg; }
	    			if (clustCoeff[i] > maxclustCoeff) {
	    				maxclustCoeff = clustCoeff[i]; }
	    			if (nodeDegree[i][0] == 0 && nodeDegree[i][1] != 0) {// no edges out then sink
	    				axisNumber[i] = 3;}
	    			else if (nodeDegree[i][1] == 0 && nodeDegree[i][0] != 0){ // no edges in then source
	    				axisNumber[i] = 1;}
	    			else if (nodeDegree[i][1] != 0 && nodeDegree[i][0] != 0) {
	    				axisNumber[i] = 2; } // if both in and out edges then manager
	    			else {}
	    		}
	    		System.out.println("Max Degree: " + maxDegree);
	    		System.out.println("Max Clustering Coefficient: " + maxclustCoeff);
	    		// Loop edges - Use adjacency matrix instead of xlsData.
	    		for (int i = 0; i<xlsData[0].length; i++) {
	    			//ignore null rows
	    			int inDeg = 0; int outDeg = 0; int inAx = 0; int outAx = 0;
	    			float outcc = 0; float incc = 0; 
	    			float xIn = 800; float yIn = 800; float xOut=800; float yOut=800;
	    			
	    			if (xlsData[0][i] != null && xlsData[1][i] != null) {
	    				// find axis and node degree.
	    				for (int n = 0; n<numVertices; n++) {
	    					if (xlsData[0][i] == names[n]) { // node with edge out
	    						outAx = axisNumber[n];	    						
	    						outDeg = nodeDegree[n][0] + nodeDegree[n][1];
	    						outcc = clustCoeff[n];
	    						if (outAx ==3) {
	    							println("error: Edge going out of sink found");
	    						}
	    					} else if (xlsData[1][i] == names[n]) { // node with edge in
	    						inAx = axisNumber[n];
	    						inDeg = nodeDegree[n][0] + nodeDegree[n][1];
	    						incc = clustCoeff[n];
	    						if (inAx ==1) {
	    							println("error: Edge coming into source found");
	    						}
	    					}
	    				}

	    				// distance along respective axis.
	    				float inPctg = 0; float outPctg = 0;
	    				if (selectedMethod != null) {
	    					if (selectedMethod.equals("degree")) {
	    						inPctg = ((float) inDeg)/maxDegree;
	    						outPctg = ((float) outDeg)/maxDegree;
	    					} else if (selectedMethod.equals("clusteringcoefficient")) {
	    						inPctg = ((float) incc)/maxclustCoeff;
	    						outPctg = ((float) outcc)/maxclustCoeff;
	    					}
	    				} else { // default method of degree
	    					inPctg = ((float) inDeg)/maxDegree;
    						outPctg = ((float) outDeg)/maxDegree;	
	    				}
	    				
	    				float dIn = (float) ((0.05*width) + (f2-f1)*inPctg);
	    				float dOut = (float) ((0.05*width) + (f2-f1)*outPctg);
	    				
	    				// axis 1 - both nodes can't be sources.
	    				if (inAx == 1) {	    					
	    					System.out.println("Error: detected edge into Source");
	    				} else 
	    				if (outAx == 1) {
	    					xOut = 0; yOut = dOut;
	    				}
	    				
	    				// Axis 3 - both nodes can't be sinks
	    				if (inAx == 3) {
	    					xIn = -dIn*cos(PI/6); yIn = -dIn*sin(PI/6); 
	    				} else if (outAx == 3) {
	    					xOut = -dOut*cos(PI/6); yOut = -dOut*sin(PI/6);
	    					System.out.println("Error: detected edge out of sink node");
	    				}
	    				
	    				// Axis 2.
	    				if (inAx == 2) {
	    					// if coming from axis 1 then use the axis at pi/2.
	    					if (outAx == 1) {
	    						xIn = dIn; yIn = 0;
	    					// if coming from axis 3 then error
	    					} else if (outAx == 3) {
	    						//System.out.println("Error: edge out from sink");
	    						xIn = dIn*cos(PI/3); yIn = - dIn*sin(PI/3);
	    					// if both managers then use both axes.
	    					}
	    					else if (outAx == 2) {
	    						xIn = dIn; yIn = 0;
	    						xOut = dOut*cos(PI/3); yOut = - dOut*sin(PI/3); 
	    					}
	    				} 
	    				if (outAx ==2) {
	    					// if going to axis 1 then use the axis at pi/2.
	    					if (inAx == 1) {
	    						//xOut = dOut; yOut = 0;
	    						System.out.println("Error: edge into source");
	    					// if going to axis 3 then use the axis at 5pi/6.
	    					} 
	    					else if (inAx == 3) {
	    						xOut = dOut*cos(PI/3); yOut = - dOut*sin(PI/3);	    					
	    					} 		    						    					
	    				}
	    				// initialize this edge	    				
	    					if (selectedNode != null) {
	    						int thisNode = Integer.parseInt(selectedNode);
	    						if (xlsData[1][i] == names[thisNode] || xlsData[0][i] == names[thisNode]) {
	    							allEdges[i] = new Edge(this,xIn, -yIn, xIn, -yOut, xOut, -yOut, xOut, -yOut,selectEdgeClr);
	    						}
	    					} else if (textInput != null) {
	    						if (xlsData[1][i] == names[selNodeinput] || xlsData[0][i] == names[selNodeinput]) {
	    							allEdges[i] = new Edge(this,xIn, -yIn, xIn, -yOut, xOut, -yOut, xOut, -yOut,selectEdgeClr);
	    						}	
	    					}
	    					else {
	    					allEdges[i] = new Edge(this,xIn, -yIn, xIn, -yOut, xOut, -yOut, xOut, -yOut,baseEdgeClr);
	    					}	    						    		
	    			} // if xlsData row isn't null 
	    		} // for each row in xlsData
	    		//redraw();
	    		drawFlg = true;
		} // end drawNetwork
		
		public int getIntFromColor(float Red, float Green, float Blue){
		    int R = Math.round(255 * Red);
		    int G = Math.round(255 * Green);
		    int B = Math.round(255 * Blue);

		    R = (R << 16) & 0x00FF0000;
		    G = (G << 8) & 0x0000FF00;
		    B = B & 0x000000FF;

		    return 0xFF000000 | R | G | B;
		}
	
		
// GUI callback functions-----------------------------------------------------
		
		public void coordMapping(int method ) {
	  		  
			  selectedMethod = cp5.get(ScrollableList.class,"coordMapping").getItem(method).get("name").toString();
			  println(selectedMethod);
			  textInput = null;
			  selectedNode = null;
			  
			  System.out.println("Using " + selectedMethod + " for coordinate location");
			  
			  drawNetwork();
			  							  
			}
		
		public void nodelist(int n) {
			  		  
			  selectedNode = cp5.get(ScrollableList.class,"nodelist").getItem(n).get("value").toString();
			  textInput = null;
			  String function = null;
			  int selNode = Integer.parseInt(selectedNode);			  
			  if (axisNumber[selNode] == 1) {
					function = "Source";
				}else if(axisNumber[selNode] == 2) {
					function = "Manager";
				}else if(axisNumber[selNode] == 3) {
					function = "Sink";
				}
			  String degree = String.valueOf(nodeDegree[selNode][0] + nodeDegree[selNode][1]);
			  String cc = String.valueOf(clustCoeff[selNode]);
			  myTextarea.setText(names[selNode]+" "+function+ " Degree: "+degree+" Clustering Coeff: "+cc);
			  			  								
			  System.out.println("showing connections for " + names[n]);
			  
			  drawNetwork();
			  							  
			}
		
		public void input(String text) {
			selectedNode = null;
			  // receives results from controller input
			  println("user entered: "+ text);
			  textInput = text;
			  // get index of node
			  //int selNode = 0;
			  for (int n=0; n<numVertices; n++){
				  if(textInput.equals(names[n])) {
					  selNodeinput = n;
				  }
			  }	
			  
			  String function = null;			  
			  if (axisNumber[selNodeinput] == 1) {
					function = "Source";
				}else if(axisNumber[selNodeinput] == 2) {
					function = "Manager";
				}else if(axisNumber[selNodeinput] == 3) {
					function = "Sink";
				}
			  String degree = String.valueOf(nodeDegree[selNodeinput][0] + nodeDegree[selNodeinput][1]);
			  String cc = String.valueOf(clustCoeff[selNodeinput]);
			  myTextarea.setText(names[selNodeinput]+" "+function+ " Degree: "+degree+" Clustering Coeff: "+cc);
			  drawNetwork();			  
			}
		
	      private static String renderColor(Color thisColor) {
	          if(thisColor instanceof HSSFColor) {
	             return ((HSSFColor)thisColor).getHexString();
	          } else if(thisColor instanceof XSSFColor) {
	             return ((XSSFColor)thisColor).getARGBHex();
	          } else {
	             return "(none)";
	          }
	       }
		
		public static NetworkVis getInstance() {
		    if (_instance == null) {
		        _instance = new NetworkVis ();
		    }

		    return(_instance);
		}
		
} // end of class NetworkVis

