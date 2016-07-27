/**
 * 
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
