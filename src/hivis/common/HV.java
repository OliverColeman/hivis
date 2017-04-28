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

package hivis.common;

import java.io.File;
import java.util.Date;
import java.util.Random;

import hivis.data.AbstractModifiableDataSeries;
import hivis.data.DataEvent;
import hivis.data.DataMap;
import hivis.data.DataMapDefault;
import hivis.data.DataSeries;
import hivis.data.DataSeriesGeneric;
import hivis.data.DataSeriesInteger;
import hivis.data.DataSeriesLong;
import hivis.data.DataSeriesDouble;
import hivis.data.DataSeriesFloat;
import hivis.data.DataTable;
import hivis.data.DataTableDefault;
import hivis.data.DataValue;
import hivis.data.DataValueDouble;
import hivis.data.DataValueFloat;
import hivis.data.DataValueInteger;
import hivis.data.DataValueLong;
import hivis.data.reader.SpreadSheetReader;
import hivis.data.reader.SpreadSheetReader.Config;
import hivis.data.view.AbstractSeriesView;

/**
 * Collection of factory and utility methods.
 * 
 * @author O. J. Coleman
 */
public class HV {
	public static final int FLOAT = 0;
	public static final int DOUBLE = 1;
	
	public static int defaultFloatingPointPrecision = FLOAT;
	
	
	/**
	 * Create a new DataValue with the specified value.
	 */
	public static DataValue<Integer> newIntegerValue(int v) {
		return new DataValueInteger(v);
	}
	/**
	 * Create a new DataValue with the specified value.
	 */
	public static DataValue<Long> newValue(long v) {
		return new DataValueLong(v);
	}
	/**
	 * Create a new DataValue with the specified value.
	 */
	public static DataValue<Float> newValue(float v) {
		return new DataValueFloat(v);
	}
	/**
	 * Create a new DataValue with the specified value.
	 */
	public static DataValue<Double> newRealValue(double v) {
		return new DataValueDouble(v);
	}
	
	
	/**
	 * Create a new DataTable.
	 */
	public static DataTable newTable() {
		return new DataTableDefault();
	}
	
	
	/**
	 * Create a new DataMap.
	 */
	public static <K, V> DataMap newMap() {
		return new DataMapDefault<K, V>();
	}
	
	
	/**
	 * <p>Load data from the specified file.</p>
	 * <p>If the first row contains all strings, except one column at most, then it is used as the header row.</p>
	 * <p>The returned DataTable will be updated in real time as changes are saved to the file.</p>
	 * <p>DataSeries are created to match the data type for a column (using the first non-empty data element of a column):
	 * 	<dl>
	 * 		<dt>Numeric: </td><dd>DataSeries&lt;Double&gt;.</dd>
	 * 		<dt>Date: </td><dd>DataSeries&lt;TemporalAccessor&gt; (for columns formatted as dates/times (Excel), or text that 
	 * 			looks like an ISO-like date or time (CSV).</dd>
	 * 		<dt>String: </td><dd>DataSeries&lt;String&gt; (for everything else).</dd>
	 * 	</dl>
	 * </p>
	 * 
	 * @param file The spreadsheet file. Excel (xlsx) and CSV-like files are supported. The file type is detected from the file extension.
	 * @return The loaded data.
	 */
	public static DataTable loadSpreadSheet(File file) {
		SpreadSheetReader reader = new SpreadSheetReader(file);
		return reader.getData();
	}
	
	
	/**
	 * <p>Load data from the specified file, sheet number (Excel only), with the specified (optional) 
	 * 	header row and starting at the specified row and column.</p>
	 * <p>If the first row contains all strings, except one column at most, then it is used as the header row.</p>
	 * <p>The returned DataTable will be updated in real time as changes are saved to the file.</p>
	 * <p>DataSeries are created to match the data type for a column (using the first non-empty data element of a column):
	 * 	<dl>
	 * 		<dt>Numeric: </td><dd>DataSeries&lt;Double&gt;.</dd>
	 * 		<dt>Date: </td><dd>DataSeries&lt;TemporalAccessor&gt; (for columns formatted as dates/times (Excel), or text that 
	 * 			looks like an ISO-like date or time (CSV).</dd>
	 * 		<dt>String: </td><dd>DataSeries&lt;String&gt; (for everything else).</dd>
	 * 	</dl>
	 * </p>
	 * 
	 * @param file The spreadsheet file. Excel (xlsx) and CSV-like files are supported. The file type is detected from the file extension.
	 * @param sheet The index of the sheet in the spreadsheet to read from (if applicable).
	 * @param headerRow The index of the row to use as column headers.
	 * @param firstDataRow The row to start reading data from (to the end of the sheet).
	 * @param firstDataColumn The column to start creating series from (up to the last column).
	 * @deprecated As of 2.0. Superseded by {@link #loadSpreadSheet(hivis.data.reader.SpreadSheetReader.Config)}. This method will be removed in future releases.
	 */
	public static DataTable loadSpreadSheet(File file, int sheet, int headerRow, int firstDataRow, int firstDataColumn) {
		SpreadSheetReader reader = new SpreadSheetReader(file, sheet, headerRow, firstDataRow, firstDataColumn, false);
		return reader.getData();
	}
	
	/**
	 * <p>Load data from a file using the given configuration.</p>
	 * <p>The returned DataTable will be updated in real time as changes are saved to the file.</p>
	 * <p>DataSeries are created to match the data type for a column (using the first non-empty data element of a column):
	 * 	<dl>
	 * 		<dt>Numeric: </td><dd>DataSeries&lt;Double/Float&gt; (see {@link Config#doublePrecision(boolean)}.</dd>
	 * 		<dt>Date: </td><dd>DataSeries&lt;TemporalAccessor/Date&gt; (for columns formatted as dates/times (Excel), 
	 * 			or text that looks like an ISO-like date or time (CSV). See {@link Config#useDeprecatedDates(boolean)}).</dd>
	 * 		<dt>String: </td><dd>DataSeries&lt;String&gt; (for everything else).</dd>
	 * 	</dl>
	 * 
	 * @param config The configuration for the {@link SpreadSheetReader}.
	 */
	public static DataTable loadSpreadSheet(SpreadSheetReader.Config config) {
		SpreadSheetReader reader = new SpreadSheetReader(config);
		return reader.getData();
	}
	
	/**
	 * Convenience method to create a new configuration object for 
	 * {@link #loadSpreadSheet(hivis.data.reader.SpreadSheetReader.Config)}.
	 */
	public static SpreadSheetReader.Config loadSSConfig() {
		return new SpreadSheetReader.Config();
	}
	
	/**
	 * Create a new DataSeries containing the given real (double) numbers.
	 */
	public static DataSeries<Double> newRealSeries(double... data) {
		return new DataSeriesDouble(data);
	}
	
	/**
	 * Create a new DataSeries containing the given integer (int) numbers.
	 */
	public static DataSeries<Integer> newIntegerSeries(int... data) {
		return new DataSeriesInteger(data);
	}
	

	/**
	 * Create a new generic DataSeries.
	 */
	public static <V> DataSeries<V> newSeries() {
		return new DataSeriesGeneric<V>();
	}
	
	/**
	 * Create a new DataSeries storing the specified items.
	 */
	public static <V> DataSeries<V> newSeries(V... items) {
		// First check if all the values are numeric, and which type.
		boolean isNumeric = true;
		boolean isDouble = true;
		boolean isInt = true;
		boolean isLong = true;
		boolean isFloat = true;
		for (Object o : items) {
			if (!(o instanceof Number)) {
				isNumeric = false;
				break;
			}
			else if (!(o instanceof Integer)) {
				isInt = false;
			}
			else if (!(o instanceof Long)) {
				isLong = false;
			}
			else if (!(o instanceof Float)) {
				isFloat = false;
			}
			else if (!(o instanceof Double)) {
				isDouble = false;
			}
		}
		
		if (isNumeric) {
			if (isInt) {
				int[] vals = new int[items.length];
				for (int i = 0; i < items.length; i++) {
					vals[i] = ((Number) items[i]).intValue();
				}
				return (DataSeries<V>) new DataSeriesInteger(vals);
			}
			if (isLong) {
				long[] vals = new long[items.length];
				for (int i = 0; i < items.length; i++) {
					vals[i] = ((Number) items[i]).longValue();
				}
				return (DataSeries<V>) new DataSeriesLong(vals);
			}
			if (isFloat) {
				float[] vals = new float[items.length];
				for (int i = 0; i < items.length; i++) {
					vals[i] = ((Number) items[i]).floatValue();
				}
				return (DataSeries<V>) new DataSeriesFloat(vals);
			}
			if (isDouble) {
				double[] vals = new double[items.length];
				for (int i = 0; i < items.length; i++) {
					vals[i] = ((Number) items[i]).doubleValue();
				}
				return (DataSeries<V>) new DataSeriesDouble(vals);
			}
		}
		return new DataSeriesGeneric<V>(items);
	}
	
	/**
	 * Generates a DataSeries that contains a sequence of integer values that increase or decrease by the given step size.
	 * @param length The length of the series.
	 * @param start The first value in the series.
	 * @param step The step between values, may be positive or negative.
	 */
	public static DataSeries<Integer> integerSequence(final int length, final int start, final int step) {
		return new AbstractSeriesView<Object, Integer>() {
			public int length() {
				return length;
			}
			public Integer get(int index) {
				return start + index * step;
			}
			public Integer getEmptyValue() {
				return Integer.MIN_VALUE;
			}
			public void update(DataEvent cause) {
			}
		};
	}

	/**
	 * Generates a DataSeries that contains a sequence of real (double) values that increase or decrease by the given step size.
	 * @param length The length of the series.
	 * @param start The first value in the series.
	 * @param step The step between values, may be positive or negative.
	 */
	public static DataSeries<Double> realSequence(final int length, final double start, final double step) {
		return new AbstractSeriesView<Object, Double>() {
			public int length() {
				return length;
			}
			public Double get(int index) {
				return start + index * step;
			}
			public Double getEmptyValue() {
				return Double.NaN;
			}
			public void update(DataEvent cause) {
			}
		};
	}

	
	/**
	 * Generates a DataSeries containing uniformly distributed numbers.
	 * @param length The length of the series.
	 * @param min The minimum allowable value.
	 * @param max The maximum allowable value.
	 */
	public static DataSeriesDouble randomUniformSeries(int length, double min, double max) {
		double[] data = new double[length];
		for (int i = 0; i < length; i++) {
			data[i] = Math.random() * (max - min) + min;
		}
		return new DataSeriesDouble(data);
	}
	
	/**
	 * Generates a DataSeries containing normally (Gaussian) distributed numbers.
	 * @param length The length of the series.
	 * @param mean The mean of the distribution.
	 * @param stdDev The standard deviation of the distribution.
	 */
	public static DataSeriesDouble randomNormalSeries(int length, double mean, double stdDev) {
		double[] data = new double[length];
		Random r = new Random();
		for (int i = 0; i < length; i++) {
			data[i] = r.nextGaussian() * stdDev + mean;
		}
		return new DataSeriesDouble(data);
	}
	
	/**
	 * Generates a DataSeries containing uniformly distributed integer numbers.
	 * @param length The length of the series.
	 * @param min The minimum allowable value.
	 * @param max The maximum allowable value.
	 */
	public static DataSeriesInteger randomIntegerSeries(int length, int min, int max) {
		int[] data = new int[length];
		for (int i = 0; i < length; i++) {
			data[i] = (int)Math.round(Math.random() * (max - min)) + min;
		}
		return new DataSeriesInteger(data);
	}
	
	/**
	 * Generates a DataSeries containing uniformly distributed dates.
	 * @param length The length of the series.
	 * @param min The minimum allowable date.
	 * @param max The maximum allowable date.
	 */
	public static AbstractModifiableDataSeries<Date> randomDateSeries(int length, Date min, Date max) {
		AbstractModifiableDataSeries<Date> dates = new DataSeriesGeneric<Date>();
		long minTS = min.getTime();
		long maxTS = max.getTime();
		for (int i = 0; i < length; i++) {
			dates.appendValue(new Date((long) Math.round(Math.random() * (maxTS - minTS)) + minTS));
		}
		return dates;
	}
	
	/**
	 * Generates a table containing DataSeries as such: "real [0, 1]", "real normal", "integer [0, 100]", "date time [1970, now]".
	 * @param length The length of each DataSeries.
	 */
	public static DataTable makeRandomTable(int length) {
		DataTable table = new DataTableDefault();
		table.addSeries("real [0, 1]", randomUniformSeries(length, 0, 1));
		table.addSeries("real normal", randomNormalSeries(length, 0, 1));
		table.addSeries("integer [0, 100]", randomIntegerSeries(length, 0, 100));
		table.addSeries("date time [1970, now]", randomDateSeries(length, new Date(0), new Date()));
		return table;
	}
	
	/**
	 * Returns a table containing the "mt cars" dataset.
	 * The data was extracted from the 1974 Motor Trend US magazine, and comprises fuel consumption and 10 aspects of automobile design and performance for 32 automobiles (1973–74 models).
	 */
	public static DataTable mtCars() {
		DataTable mt = newTable();
		
		mt.addSeries("model", newSeries( "Mazda RX4", "Mazda RX4 Wag", "Datsun 710", "Hornet 4 Drive", "Hornet Sportabout", "Valiant", "Duster 360", "Merc 240D", "Merc 230", "Merc 280", "Merc 280C", "Merc 450SE", "Merc 450SL", "Merc 450SLC", "Cadillac Fleetwood", "Lincoln Continental", "Chrysler Imperial", "Fiat 128", "Honda Civic", "Toyota Corolla", "Toyota Corona", "Dodge Challenger", "AMC Javelin", "Camaro Z28", "Pontiac Firebird", "Fiat X1-9", "Porsche 914-2", "Lotus Europa", "Ford Pantera L", "Ferrari Dino", "Maserati Bora", "Volvo 142E"));
		mt.addSeries("mpg", newSeries( 21, 21, 22.8, 21.4, 18.7, 18.1, 14.3, 24.4, 22.8, 19.2, 17.8, 16.4, 17.3, 15.2, 10.4, 10.4, 14.7, 32.4, 30.4, 33.9, 21.5, 15.5, 15.2, 13.3, 19.2, 27.3, 26, 30.4, 15.8, 19.7, 15, 21.4));
		mt.addSeries("cyl", newSeries( 6, 6, 4, 6, 8, 6, 8, 4, 4, 6, 6, 8, 8, 8, 8, 8, 8, 4, 4, 4, 4, 8, 8, 8, 8, 4, 4, 4, 8, 6, 8, 4));
		mt.addSeries("disp", newSeries( 160, 160, 108, 258, 360, 225, 360, 146.7, 140.8, 167.6, 167.6, 275.8, 275.8, 275.8, 472, 460, 440, 78.7, 75.7, 71.1, 120.1, 318, 304, 350, 400, 79, 120.3, 95.1, 351, 145, 301, 121));
		mt.addSeries("hp", newSeries( 110, 110, 93, 110, 175, 105, 245, 62, 95, 123, 123, 180, 180, 180, 205, 215, 230, 66, 52, 65, 97, 150, 150, 245, 175, 66, 91, 113, 264, 175, 335, 109));
		mt.addSeries("drat", newSeries( 3.9, 3.9, 3.85, 3.08, 3.15, 2.76, 3.21, 3.69, 3.92, 3.92, 3.92, 3.07, 3.07, 3.07, 2.93, 3, 3.23, 4.08, 4.93, 4.22, 3.7, 2.76, 3.15, 3.73, 3.08, 4.08, 4.43, 3.77, 4.22, 3.62, 3.54, 4.11));
		mt.addSeries("wt", newSeries( 2.62, 2.875, 2.32, 3.215, 3.44, 3.46, 3.57, 3.19, 3.15, 3.44, 3.44, 4.07, 3.73, 3.78, 5.25, 5.424, 5.345, 2.2, 1.615, 1.835, 2.465, 3.52, 3.435, 3.84, 3.845, 1.935, 2.14, 1.513, 3.17, 2.77, 3.57, 2.78));
		mt.addSeries("qsec", newSeries( 16.46, 17.02, 18.61, 19.44, 17.02, 20.22, 15.84, 20, 22.9, 18.3, 18.9, 17.4, 17.6, 18, 17.98, 17.82, 17.42, 19.47, 18.52, 19.9, 20.01, 16.87, 17.3, 15.41, 17.05, 18.9, 16.7, 16.9, 14.5, 15.5, 14.6, 18.6));
		mt.addSeries("vs", newSeries( 0, 0, 1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1));
		mt.addSeries("am", newSeries( 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1));
		mt.addSeries("gear", newSeries( 4, 4, 4, 3, 3, 3, 3, 4, 4, 4, 4, 3, 3, 3, 3, 3, 3, 4, 4, 4, 3, 3, 3, 3, 3, 4, 5, 5, 5, 5, 5, 4));
		mt.addSeries("carb", newSeries( 4, 4, 1, 1, 2, 1, 4, 2, 2, 4, 4, 3, 3, 3, 4, 4, 4, 1, 2, 1, 1, 2, 2, 4, 2, 1, 2, 2, 4, 6, 8, 2));
		
		return mt;
	}
}
