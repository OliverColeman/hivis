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

import processing.core.PApplet;

public class Edge {
	float x1; float y1;
	float cpx1; float cpy1;
	float cpx2; float cpy2;
	float x2; float y2;
	int eColor;
	PApplet parent;
	
	public Edge(PApplet p, float tx1, float ty1, float tcpx1, float tcpy1, float tcpx2, float tcpy2, float tx2, float ty2, int teColor) {
		// TODO Auto-generated constructor stub
		x1 = tx1; y1 = ty1; 
		cpx1 = tcpx1; cpy1 = tcpy1;
		cpx2 = tcpx2; cpy2 = tcpy2;
		x2 = tx2; y2 = ty2;
		parent = p;
		eColor = parent.color(teColor);		
	}
	
	public void display() {
		parent.noFill();
		parent.stroke(eColor); 
		parent.bezier(x1,y1,cpx1,cpy1,cpx2,cpy2,x2,y2);
		//println("drawing Edge");
	}

}
