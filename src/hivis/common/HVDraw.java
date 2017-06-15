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

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Streams;
import com.google.common.collect.Table;

import hivis.data.DataSequence;
import hivis.data.DataSeries;
import hivis.data.DataTable;
import hivis.data.DataValue;
import hivis.data.DataValueDouble;
import hivis.data.view.CalcSeries;
import processing.core.PApplet;
import processing.core.PConstants;

/**
 * Defines convenience methods for visualising data in Processing.
 *
 * @author oliver
 */
public class HVDraw {
	
	/**
	 * Palette comprising 8 pastel colours. Violet, orange, blue, pink, green, tan, slate, brown.
	 */
	public static final int[] PASTEL8 = new int[] {-10139262, -1416405, -12877884, -959371, -7426015, -941481, -11901083, -8299718};
	
	private static Map<String, Object> paletteCache = new HashMap<>();
	/**
	 * Make a palette constructed by creating colours specified in HSB colour space such that the hues are evenly distributed.
	 * @param number The number of colours to create.
	 * @param saturation The saturation of the colours.
	 * @param brightness The brightness of the colours.
	 */
	public static int[] makeRainbowPalette(int number, float saturation, float brightness) {
		String cacheKey = "pr" + number + ":" + saturation + ":" + brightness;
		if (paletteCache.containsKey(cacheKey)) {
			return (int[]) paletteCache.get(cacheKey);
		}
		
		int[] colours = new int[number];
		for (int i = 0; i < number; i++) {
			colours[i] = Color.HSBtoRGB((float) i / number, saturation, brightness);
		}
		
		paletteCache.put(cacheKey, colours);
		
		return colours;
	}
	
	
	
	/**
	 * Draw an x (horizontal) axis based on the values in a given numeric {@link DataSeries}.
	 * @param applet The applet window on which to draw the axis.
	 * @param series The {@link DataSeries} for which to draw the axis.
	 * @param xPos The x coordinate for the left end of the axis line.   
	 * @param yPos The y coordinate for the axis line.
	 * @param width The length of the axis (in pixels).
	 */
	public static void xAxis(PApplet applet, DataSeries<?> series, float xPos, float yPos, float width) {
		Config config = new Config();
		config.set("type", "x");
		config.set("series", series);
		config.set("x", xPos);
		config.set("y", yPos);
		config.set("size", width);
		config.set("applet", applet);
		axis(applet, config);
	}
	
	/**
	 * Draw a y (vertical) axis based on the values in a given numeric {@link DataSeries}.
	 * @param applet The applet window on which to draw the axis.
	 * @param series The {@link DataSeries} for which to draw the axis.
	 * @param xPos The x coordinate for the axis line.
	 * @param yPos The y coordinate for the bottom of the axis line.
	 * @param width The length of the axis (in pixels).
	 */
	public static void yAxis(PApplet applet, DataSeries<?> series, float xPos, float yPos, float height) {
		Config config = new Config();
		config.set("type", "y");
		config.set("series", series);
		config.set("x", xPos);
		config.set("y", yPos);
		config.set("size", height);
		config.set("applet", applet);
		axis(applet, config);
	}
	
	/**
	 * Draw an axis. The configuration accepts the following parameters:
	 * <dl>
	 * <dt>type (String)</dt>
	 * <dd>The type/direction of the axis, either "x" for horizontal or "y" for
	 * vertical.</dd>
	 * <dt>x (Numeric)</dt>
	 * <dd>The x coordinate for the the axis line (left end of for type "x").
	 * </dd>
	 * <dt>y (Numeric)</dt>
	 * <dd>The y coordinate for the the axis line (bottom of for type "y").</dd>
	 * <dt>size (Numeric)</dt>
	 * <dd>The width/height of the axis.</dd>
	 * <dt>series (DataSeries)</dt>
	 * <dd>The {@link DataSeries} for which to draw the axis, if the
	 * <em>min</em> and <em>max</em> parameters are not specified.</dd>
	 * <dt>min (Numeric)</dt>
	 * <dd>The minimum value to be represented by the axis, if <em>series</em>
	 * is not specified.</dd>
	 * <dt>max (Numeric)</dt>
	 * <dd>The maximum value to be represented by the axis, if <em>series</em>
	 * is not specified.</dd>
	 * </dl>
	 * 
	 * @param applet
	 *            The applet window on which to draw the axis.
	 * @param config
	 *            The axis configuration.
	 */
	public static void axis(PApplet applet, Config config) {
		float xPos = config.getFloat("x");
		float yPos = config.getFloat("y");
		float size = config.getFloat("size");
		
		DataSeries<Double> tics = getTicSeries(config);
		
		applet.pushStyle();
		
		DataSeries<?> series =  config.get("series", null);
		float min = (series != null ? series.min() : config.getNumericDataValue("min")).getFloat();
		float max = (series != null ? series.max() : config.getNumericDataValue("max")).getFloat();
		float range = max - min;
		
		String type = config.get("type");
		if (type.equals("x")) {
			applet.line(xPos, yPos, xPos + size, yPos);
		
			applet.textAlign(PConstants.CENTER, PConstants.TOP);
		
			for (double tic : tics) {
				float tx = xPos + size * (((float) tic - min) / range);
				// cast to float to avoid numbers like "7.200000000000001"
				applet.text(""+(float)tic, tx, yPos + 5);
				applet.line(tx, yPos, tx, yPos-5);
			}
		}
		else if (type.equals("y")) {
			applet.line(xPos, yPos, xPos, yPos + size);
			
			applet.textAlign(PConstants.LEFT, PConstants.CENTER);
			
			// Work out max width of labels. Cast to float to avoid numbers like "7.200000000000001"
			float maxWidth = Streams.stream(tics).map(tic -> applet.textWidth("" + tic.floatValue())).reduce(0f, Float::max);
			float labelXPos = xPos - maxWidth - 5;
			for (double tic : tics) {
				float ty = applet.height - (yPos + size * (((float) tic - min) / range));
				// cast to float to avoid numbers like "7.200000000000001"
				applet.text(""+(float)tic, labelXPos, ty);
				applet.line(xPos, ty, xPos + 5, ty);
			}
		}
		
		applet.popStyle();
	}
	
	
	private static Map<Config, DataSeries<Double>> ticSeriesCache = new HashMap<>();

	/**
	 * Returns a DataSeries containing nice, rounded tic values for a plot axis.
	 * If the type, size and applet parameters are specified then the number of
	 * tics will be adjusted to ensure the labels will fit the available space.
	 * The configuration accepts the following parameters:
	 * <dl>
	 * <dt>series (DataSeries)</dt>
	 * <dd>The {@link DataSeries} for which to draw the axis, if the
	 * <em>min</em> and <em>max</em> parameters are not specified.</dd>
	 * <dt>min (Numeric)</dt>
	 * <dd>The minimum value to be represented by the axis, if <em>series</em>
	 * is not specified.</dd>
	 * <dt>max (Numeric)</dt>
	 * <dd>The maximum value to be represented by the axis, if <em>series</em>
	 * is not specified.</dd>
	 * <dt>includeOuter (Boolean)</dt>
	 * <dd>Optional. Whether to include 'containing' tic marks beyond the range
	 * of the series, if necessary. Default is false.</dd>
	 * <dt>targetTicCount (Numeric)</dt>
	 * <dd>Optional. A target or suggested number of tics to generate. Default
	 * is 20.</dd>
	 * <dt>type (String)</dt>
	 * <dd>Optional. The type/direction of the axis, either "x" for horizontal
	 * or "y" for vertical. Used to determine the number of tics.</dd>
	 * <dt>size (Numeric)</dt>
	 * <dd>The width/height of the axis.</dd>
	 * <dt>applet (PApplet)</dt>
	 * <dd>Optional. The type/direction of the axis, either "x" for horizontal
	 * or "y" for vertical. Used to determine the number of tics.</dd>
	 * </dl>
	 * 
	 * @param applet
	 *            The applet window on which to draw the axis.
	 */
	public static <I> DataSeries<Double> getTicSeries(final Config config) {
		if (!ticSeriesCache.containsKey(config)) {
			DataSeries<?> series =  config.get("series", null);
			final DataValue<?> minValue = series != null ? series.min() : config.getNumericDataValue("min");
			final DataValue<?> maxValue = series != null ? series.max() : config.getNumericDataValue("max");
			final DataValue<?> size = config.getNumericDataValue("size", HV.newValue(0));
			final DataValue<?> includeOuterTics = config.getDataValue("includeOuter", HV.newValue(false));
			final DataValue<?> targetTics = config.getDataValue("targetTicCount", HV.newValue(20));
			
			DataSeries<Double> tics = new CalcSeries<Object, Double>(minValue, maxValue, size, targetTics, includeOuterTics) {
				@Override
				public int length() {
					if (recalc) {
						update(null);
					}
					if (cache == null) return 0;
					
					return cache.length();
				}
				
				@Override
				public void update() {
					double minVal = minValue.getDouble();
					double maxVal = maxValue.getDouble();
					if (minVal == maxVal) {
						minVal -= 0.5;
						maxVal += 0.5;
					}
					
					int target = targetTics.getInt();
					
					double spacing = findSpacing(minVal, maxVal, target);
					int numTics = calcNumTics(minVal, maxVal, spacing);
					
					int minTics = includeOuterTics.getBoolean() ? 2 : 1;
					
					int maxSpacingChanges = 10;
					if (numTics > minTics && !ticsFit(minVal, spacing, numTics)) {
						// If we have too many tics, reduce number of tics until they fit.
						int prevNumTics;
						double prevSpacing;
						do {
							prevNumTics = numTics;
							prevSpacing = spacing;
							
							target = (int) Math.max(Math.round(target / 1.44), target - 1);
							spacing = findSpacing(minVal, maxVal, target);
							numTics = calcNumTics(minVal, maxVal, spacing);
						} while (--maxSpacingChanges > 0 && numTics > minTics && !ticsFit(minVal, spacing, numTics));
						
						// If we went past the minimum number of tics, go back to previous number.
						if (numTics < minTics) {
							numTics = prevNumTics;
							spacing = prevSpacing;
						}
					}
					
					double minTic = calcMinTic(minVal, spacing);
					
					cache.resize(numTics);
					for (int i = 0; i < numTics; i++) {
						cache.setValue(i, minTic + i * spacing);
						//System.out.println(cache.get(i).floatValue());
					}
				}
				
				private double findSpacing(double minVal, double maxVal, int targetSteps) {
					double range = maxVal - minVal;
					double initialStep = range / targetSteps;
					double magnitude = Math.pow(10, Math.floor(Math.log10(initialStep)));
					double msd = initialStep / magnitude + 0.5;
			        if (msd > 5.0)
			            msd = 10;
			        else if (msd > 2.5)
			            msd = 5;
			        else if (msd > 2.0)
			            msd = 2.5;
			        else if (msd > 1.0)
			            msd = 2;
			        return msd * magnitude;
				}
				
				private double calcMinTic(double minVal, double spacing) {
					return includeOuterTics.getBoolean() ? Math.floor(minVal / spacing) * spacing : Math.ceil(minVal / spacing) * spacing;
				}
				private double calcMaxTic(double maxVal, double spacing) {
					return includeOuterTics.getBoolean() ? Math.ceil(maxVal / spacing) * spacing : Math.floor(maxVal / spacing) * spacing;
				}
				
				private int calcNumTics(double minVal, double maxVal, double spacing) {
					double minTic = calcMinTic(minVal, spacing);
					double maxTic = calcMaxTic(maxVal, spacing);
					int numTics = 0;
					double tic = minTic;
					while (tic <= maxTic) {
						tic = minTic + numTics * spacing;
						numTics++;
					}
					return numTics - 1;
				}
				
				private double[] calcTics(double minTic, double spacing, int numTics) {
					double[] tics = new double[numTics];
					for (int i = 0; i < numTics; i++) {
						tics[i] = minTic + i * spacing;
					}
					return tics;
				}
				
				private boolean ticsFit(double minVal, double spacing, int numTics) {
					if (config.hasParameter("applet") && size.getInt() > 0 && config.hasParameter("type")) {
						PApplet applet = config.get("applet");
						boolean vertical = config.get("type").equals("y");
						double minTic = calcMinTic(minVal, spacing);
						double maxLabelSize = vertical 
								? (applet.textAscent()+applet.textDescent()) 
								: Arrays.stream(calcTics(minTic, spacing, numTics))
									.map(tic -> applet.textWidth(""+(float)tic)) // cast to float to avoid numbers like "7.200000000000001"
									.reduce(0, Double::max);
						return size.getInt() >= 2 * numTics * maxLabelSize;
					}
					return true;
				}
			};
			ticSeriesCache.put(config, tics);
		}
		return ticSeriesCache.get(config);
	}
	
	
	
	/**
	 * Draws a pie chart based on a sequence of values (for example a {@link DataSeries} or {@link hivis.data.DataRow}). 
	 * Non-numeric values are ignored.
	 */
	public static void pie(PApplet applet, DataSequence values, float diameter, float x, float y, int[] palette, int paletteOffset) {
		applet.pushStyle();
		applet.noStroke();
		float total = 0;
		for (int item = 0; item < values.length(); item++) {
			if (values.isNumeric(item)) {
				float value = values.getFloat(item);
				if (!Float.isNaN(value)) {
					total += value;
				}
			}
		}
		float lastAngle = 0;
		for (int item = 0; item < values.length(); item++) {
			if (values.isNumeric(item)) {
				float value = values.getFloat(item);
				if (!Float.isNaN(value)) {
					applet.fill(palette[(item + paletteOffset) % palette.length]);
					float angle = (value / total) * applet.TWO_PI;
					applet.arc(x, y, diameter, diameter, lastAngle, lastAngle + angle);
					lastAngle += angle;
				}
			}
		}
		applet.popStyle();
	}
	
	
	/**
	 * Draws a pie chart based on a row from a DataTable. Non-numeric series are ignored.
	 * @deprecated As of 2.0. Superseded by {@link #pie(PApplet, DataSequence, float, float, float, int[], int)}, 
	 *     passing {@link DataTable#getRow(int)} as the sequence of values.
	 */
	public static void pie(PApplet applet, DataTable data, int row, float diameter, float x, float y, int[] palette, int paletteOffset) {
		pie(applet, data.getRow(row), diameter, x, y, palette, paletteOffset);
	}
	
	
	public static void main (String[] args) {
		DataValue min = HV.newValue(4.3);
		DataValue max = HV.newValue(7.9);
		DataValue targetTics = HV.newValue(5);
		Config config = new Config();
		config.set("size", 1000);
		config.set("targetTicCount", targetTics);
		config.set("min", min);
		config.set("max", max);
		DataSeries tics = getTicSeries(config);
		
		for (int count = 2; count <= 25; count += 1) {
			targetTics.set(count);
			System.out.println(count + ": " + tics);
		}
	}
}
