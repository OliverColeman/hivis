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
import java.util.HashMap;
import java.util.Map;

import hivis.data.DataSequence;
import hivis.data.DataSeries;
import hivis.data.DataTable;
import processing.core.PApplet;

/**
 * Defines convenience methods for visualising data in Processing.
 *
 * @author oliver
 */
public class HVDraw {
	private static Map<String, Object> cache = new HashMap<>();
	
	
	/**
	 * Palette comprising 8 pastel colours. Violet, orange, blue, pink, green, tan, slate, brown.
	 */
	public static final int[] PASTEL8 = new int[] {-10139262, -1416405, -12877884, -959371, -7426015, -941481, -11901083, -8299718};
	

	/**
	 * Make a palette constructed by creating colours specified in HSB colour space such that the hues are evenly distributed.
	 * @param number The number of colours to create.
	 * @param saturation The saturation of the colours.
	 * @param brightness The brightness of the colours.
	 */
	public static int[] makeRainbowPalette(int number, float saturation, float brightness) {
		String cacheKey = "pr" + number + ":" + saturation + ":" + brightness;
		if (cache.containsKey(cacheKey)) {
			return (int[]) cache.get(cacheKey);
		}
		
		int[] colours = new int[number];
		for (int i = 0; i < number; i++) {
			colours[i] = Color.HSBtoRGB((float) i / number, saturation, brightness);
		}
		
		cache.put(cacheKey, colours);
		
		return colours;
	}


	/**
	 * Draws a pie chart based on a sequence of values (for example a {@link DataSeries} or {@link DataRow}). 
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
}
