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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import hivis.data.AbstractDataSeries;
import hivis.data.DataSeries;
import hivis.data.DataSeriesGeneric;
import hivis.data.DataSeriesInteger;
import hivis.data.DataSeriesReal;
import hivis.data.DataTable;
import hivis.data.DataTableDefault;

/**
 * @author O. J. Coleman
 */
public class Util {
	/**
	 * Return true iff both objects are null, or if their equals method returns true. If one is null and the other is not returns false.
	 * @param o1
	 * @param o2
	 */
	public static boolean equalsIncNull(Object o1, Object o2) {
		if (o1 == null && o2 == null) return true;
		if (o1 == null && o2 != null || o1 != null && o2 == null) return false;
		return o1.equals(o2);
	}
	

	public static String dataTableToString(DataTable table) {
		if (table.length() == 0) {
			return "Empty DataTable with series " + table.getSeriesLabels().toString();
		}
		
		// Get minimum and maximum values for numeric series.
		boolean[] numeric = new boolean[table.seriesCount()];
		double[] magnitude = new double[table.seriesCount()];
		for (int s = 0; s < table.seriesCount(); s++) {
			DataSeries<?> series = table.get(s);
			if (Number.class.isAssignableFrom(series.getType())) {
				numeric[s] = true;
				for (int r = 0; r < table.length(); r++) {
					magnitude[s] = Math.max(magnitude[s], Math.abs(((Number) series.get(r)).doubleValue()));
				}
			}
		}
		
		StringBuilder sb = new StringBuilder("| ");		
		
		String format = "| ";
		String headerFormat = "";
		int idx = 1;
		for (DataSeries<?> s : table.getAll()) {
			int headerLength = table.getSeriesLabel(idx-1).length();
			
			if (numeric[idx-1]) {
				boolean big = magnitude[idx-1] >= 1000000000;
				int width;
				if (Float.class.isAssignableFrom(s.getType()) || Double.class.isAssignableFrom(s.getType()) || BigDecimal.class.isAssignableFrom(s.getType())) {
					String grouping = big ? "" : ", ";
					int precision = (int) Math.max(0, Math.min(6, 6-("" + ((int) Math.ceil(magnitude[idx-1]))).length()));
					String subFrm = "." + precision + (big ? "e" : "f");
					String str = String.format("%1$" + grouping + "1" + subFrm, -magnitude[idx-1]);
					width = str.length();
					width = Math.max(width, headerLength);
					format += "%" + idx + "$" + grouping + "" + width + subFrm + " | ";
				}
				else {
					String str = String.format("%1$,1d", -((int) Math.ceil(magnitude[idx-1])));
					width = str.length();
					width = Math.max(width, headerLength);
					format += "%" + idx + "$," + width + "d | ";
				}
				headerFormat += "%" + idx + "$" + width + "s | ";
			}
			else if (Date.class.isAssignableFrom(s.getType())) {
				int width = Math.max(19, headerLength);
				format += "%" + idx +"$tF %" + idx + "$tT | ";
				headerFormat += "%" + idx + "$" + width + "s | ";
			}
			else {
				int width = 0;
				for (int i = 0; i < s.length(); i++) {
					int l = s.get(i).toString().length();
					if (l > width) {
						width = l;
					}
				}
				width = Math.max(width, headerLength);
				format += "%" + idx + "$" + width + "s | ";
				headerFormat += "%" + idx + "$" + width + "s | ";
			}
			
			idx++;
		}
		
		sb.append(String.format(headerFormat, table.getSeriesLabels().toArray()));
		int width = sb.length()-1;
		sb.append("\n");		
		
		for (int i = 0; i < width; i++) {
			sb.append("-");
		}
		sb.append("\n");		
		Object[] row = new Object[table.seriesCount()];
		for (int r = 0; r < table.length(); r++) {
			for (int s = 0; s < table.seriesCount(); s++) {
				row[s] = table.get(s).get(r);
			}
			sb.append(String.format(format, row));
			sb.append("\n");		
		}
		
		for (int i = 0; i < width; i++) {
			sb.append("-");
		}
		sb.append("\n");		
		
		return sb.toString();
	}
}
