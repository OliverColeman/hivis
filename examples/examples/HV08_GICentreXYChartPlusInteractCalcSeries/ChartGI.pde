import org.gicentre.utils.stat.XYChart;
import hivis.data.DataEvent;
import hivis.data.DataSeries;
import hivis.data.DataTable;
import processing.core.PApplet;

/**
 * Produce a line or scatter plot of a DataTable using the XYChart object provided by giCentre (see http://www.gicentre.net/utils/chart).
 * The chart is automatically updated when the DataTable is changed.
 * If the DataTable consists of one series then the chart is produced using this series as the y-value. 
 * If the DataTable consists of multiple series then the chart is produced using the first series for
 * the x-values and the second series for the y-values. 
 */
public class ChartGI implements DataListener {
	/**
	 * The data for this chart.
	 */
	public final DataTable data;
	
	/**
	 * The chart object. This may be used to customise the appearance of the chart.
	 */
	public final XYChart chart;
	
	// Data provided to the XYChart. Updated when the DataTable changes.
	private float[] xData, yData;
	
	
	public ChartGI(PApplet parent, DataTable data) {
		this.data = data;
		data.addChangeListener(this);
		
		chart = new XYChart(parent);
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
				yData[i] = ySeries.getFloat(i);
			}
			
			chart.setYAxisLabel(data.getSeriesLabel(0));
		}
		else {
			DataSeries<?> xSeries = data.getSeries(0);
			DataSeries<?> ySeries = data.getSeries(1);
			
			for (int i = 0; i < size; i++) {
				xData[i] = xSeries.getFloat(i);
				yData[i] = ySeries.getFloat(i);
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
	
	public void draw(float xOrigin, float yOrigin, float width, float height) {
		synchronized(data) {
			chart.draw(xOrigin, yOrigin, width, height);
		}
	}
}