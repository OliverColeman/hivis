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

package hivis.chart;

import hivis.data.DataListener;
import hivis.data.DataTable;
import hivis.data.DataTableDefault;

/**
 * @author O. J. Coleman
 *
 */
public abstract class LineChart<C> implements DataListener {
	/**
	 * The data for this chart.
	 */
	public final DataTable data;
	
	/**
	 * The chart object. This may be used to customise the appearance of the chart.
	 */
	public final C chart;
	
	public LineChart(DataTable data, C chart) {
		this.data = data;
		this.chart = chart;
		
		data.addChangeListener(this);
	}
	
	/**
	 * Draw the chart within the given bounds. All implementing classes must include this method do do the drawing.
	 * @param xOrigin Left-hand pixel coordinate of the area in which to draw the chart.
	 * @param yOrigin Top pixel coordinate of the area in which to draw the chart.
	 * @param width Width in pixels of the area in which to draw the chart.
	 * @param height Height in pixels of the area in which to draw the chart.
	 */
	public abstract void draw(float xOrigin, float yOrigin, float width, float height);
}
