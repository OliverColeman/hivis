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

package hivis.visual;

import org.gicentre.utils.stat.XYChart;

import hivis.data.DataEvent;
import hivis.data.DataSeries;
import hivis.data.DataSeriesReal;
import hivis.data.DataTable;
import processing.core.PApplet;

/**
 * LineChart using the XYChart object provided by giCentre (see http://www.gicentre.net/utils/chart).
 * If the DataTable consists of one series then the chart is produced using this series as the y-value. 
 * If the DataTable consists of multiple series then the chart is produced using the first series for
 * the x-values and the second series for the y-values. 
 * 
 * TODO Support multiple plots when the data consists of more than two series. The giCentre chart
 * library does not currently support this out of the box.
 * 
 * @author O. J. Coleman
 */
public class LineChartGI extends LineChart<XYChart> {
	private float[] xData, yData;
	
	public LineChartGI(PApplet parent, DataTable data) {
		super(data, new XYChart(parent));
		chart.showXAxis(true); 
		chart.showYAxis(true);
		updateData();
	}
	
	@Override
	public void dataChanged(DataEvent event) {
		updateData();
	}
	
	private void updateData() {
		boolean validData = false;
		if (data.seriesCount() > 0) {
			if (checkSeries(0)) {
				if (data.seriesCount() == 1 || checkSeries(1)) {
					validData = true;
				}
			}
		}
		
		if (!validData) {
			chart.setData(new float[0], new float[0]);
			return;
		}
		
		int size = data.length();
		if (xData == null || xData.length != size) {
			xData = new float[size];
			yData = new float[size];
		}
		
		if (data.seriesCount() == 1) {
			DataSeries<?> ySeries = data.getSeries(0);
			
			for (int i = 0; i < size; i++) {
				xData[i] = i;
				yData[i] = (float) ySeries.getDouble(i);
			}
			
			chart.setYAxisLabel(data.getSeriesLabel(0));
		}
		else {
			DataSeries<?> xSeries = data.getSeries(0);
			DataSeries<?> ySeries = data.getSeries(1);
			
			for (int i = 0; i < size; i++) {
				xData[i] = (float) xSeries.getDouble(i);
				yData[i] = (float) ySeries.getDouble(i);
			}
			
			chart.setXAxisLabel(data.getSeriesLabel(0));
			chart.setYAxisLabel(data.getSeriesLabel(1));
		}
		
		chart.setData(xData, yData);
	}
	
	private boolean checkSeries(int s) {
		if (data.getSeries(s).length() > 0 && !data.getSeries(s).isNumeric()) {
			System.err.println("Cannot chart series '" + data.getSeriesLabel(s) + "' as it is not numeric.");
			return false;
		}
		return true;
	}
	
	@Override
	public void draw(float xOrigin, float yOrigin, float width, float height) {
		synchronized(data) {
			chart.draw(xOrigin, yOrigin, width, height);
		}
	}
}
