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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import hivis.data.AbstractDataSeries;
import hivis.data.DataSeries;
import hivis.data.DataSeriesGeneric;
import hivis.data.DataSeriesInteger;
import hivis.data.DataSeriesDouble;
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
			DataSeries<?> series = table.getSeries(s);
			Class<?> type = series.getType();
			if (type != null) {
				if (Number.class.isAssignableFrom(type)) {
					numeric[s] = true;
					for (int r = 0; r < table.length(); r++) {
						if (series.get(r) != null) {
							magnitude[s] = Math.max(magnitude[s], Math.abs(((Number) series.get(r)).doubleValue()));
						}
					}
				}
			}
		}
		
		StringBuilder sb = new StringBuilder("| ");		
		
		String format = "| ";
		String headerFormat = "";
		int idx = 1;
		for (DataSeries<?> s : table.getAll()) {
			int headerLength = table.getSeriesLabel(idx-1).length();
			
			Class<?> type = s.getType();
			
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
			else if (type != null && Date.class.isAssignableFrom(type)) {
				int width = Math.max(19, headerLength);
				format += "%" + idx +"$tF %" + idx + "$tT | ";
				headerFormat += "%" + idx + "$" + width + "s | ";
			}
			else {
				int width = 0;
				for (int i = 0; i < s.length(); i++) {
					Object val = s.get(i);
					if (val != null) {
						int l = val.toString().length();
						if (l > width) {
							width = l;
						}
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
				row[s] = table.getSeries(s).get(r);
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

	
	/**
	 * Try to determine the date format of the given String.
	 * @param input The String containing a date and optional time.
	 * @param customDateFormats One or more date formats to use instead of the standard date formats. Time elements may also be included. See https://docs.oracle.com/javase/8/docs/api/index.html?java/time/format/DateTimeFormatter.html
	 * @return A DateTimeFormatter object able to parse the given date/time, or null if the format could not be determined.
	 */
	public static DateTimeFormatter determineDateTimeFormat(String input, String... customDateFormats) {
		input = input.trim();
		
		List<DateTimeFormatter> formats;
		if (customDateFormats != null && customDateFormats.length > 0) {
			formats = new ArrayList<>();
			for (String cdf : customDateFormats) {
				try {
					formats.add(DateTimeFormatter.ofPattern(cdf));
				}
				catch (Exception ex) {
					throw new IllegalArgumentException("Invalid date/time format: \"" + cdf + "\". For formatting info see https://docs.oracle.com/javase/8/docs/api/index.html?java/time/format/DateTimeFormatter.html", ex);
				}
			}
		}
		else {
			formats = dateTimeFormatsStandard;
		}
		
		// Try each date format in turn.
		for (DateTimeFormatter f : formats) {
			try {
				parseDateTime(input, f);
				return f;
			}
			catch (Exception ex) {
			}
		}
		return null;
	}
	
	/**
	 * Utility method to parse a date/time string with a formatter, attempting 
	 * to get the most applicable type of TemporalAccessor object via 
	 * format.parseBest. The TemporalAccessor subclasses tried in order are 
	 * ZonedDateTime, OffsetDateTime, LocalDateTime, LocalDate, OffsetTime, LocalTime.
	 */
	public static TemporalAccessor parseDateTime(String input, DateTimeFormatter format) throws DateTimeParseException {
		return format.parseBest(input, ZonedDateTime::from, OffsetDateTime::from, LocalDateTime::from, LocalDate::from, OffsetTime::from, LocalTime::from);
	}
	
	
	/**
	 * Attempt to convert the given TemporalAccessor to the equivalent Date.
	 */
	public static Date temporalAccessorToDate(TemporalAccessor ta) {
	    if (ta instanceof LocalDateTime) {
	    	return Date.from(((LocalDateTime) ta).atZone(ZoneId.systemDefault()).toInstant());
	    }
	    if (ta instanceof LocalDate) {
	    	return Date.from(((LocalDate) ta).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	    }
	    if (ta instanceof LocalTime) {
	    	return Date.from(((LocalTime) ta).atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant());
	    }
	    if (ta instanceof ZonedDateTime) {
	    	return Date.from(((ZonedDateTime) ta).toInstant());
	    }
	    if (ta instanceof OffsetDateTime) {
	    	return Date.from(((OffsetDateTime) ta).toInstant());
	    }
	    if (ta instanceof OffsetTime) {
	    	return Date.from(((OffsetTime) ta).atDate(LocalDate.now()).toInstant());
	    }
	    
	    return null;
	}
// 
//    /**
//     * Convert from LocalDate to Date
//     *
//     * @param ld the {@link LocalDate} to convert
//     *
//     * @return Date at {@link ZoneId#systemDefault()} whereas the time of the date is at start of the day
//     */
//    public static Date LocalDateToDate(LocalDate ld) {
//        Instant instant = ld.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
//        return Date.from(instant);
//    }
// 
//    /**
//     * Convert from LocalTime to Date
//     *
//     * @param lt the {@link LocalTime} to convert
//     *
//     * @return Date at {@link ZoneId#systemDefault()} on todays date
//     */
//    public static Date LocalTimeToDate(LocalTime lt) {
//        LocalDate now = LocalDate.now();
//        Instant instant = lt.atDate(now).atZone(ZoneId.systemDefault()).toInstant();
//        return Date.from(instant);
//    }
	
	// Build date/time formatters for use with determineDateTimeFormat()
	private static final DateTimeFormatter dateTimeZoneBracketed = new DateTimeFormatterBuilder().appendLiteral('[').appendZoneId().appendLiteral(']').toFormatter();	
	private static final List<DateTimeFormatter> dateTimeFormatsStandard = new ArrayList<>(Arrays.asList(new DateTimeFormatter[] { 
			DateTimeFormatter.ISO_LOCAL_DATE,
			DateTimeFormatter.BASIC_ISO_DATE,
			
			new DateTimeFormatterBuilder().append(DateTimeFormatter.ISO_LOCAL_DATE).appendLiteral('T').append(DateTimeFormatter.ISO_LOCAL_TIME).appendZoneOrOffsetId().toFormatter(),
			new DateTimeFormatterBuilder().append(DateTimeFormatter.ISO_LOCAL_DATE).appendLiteral('T').append(DateTimeFormatter.ISO_LOCAL_TIME).appendLiteral(' ').appendZoneOrOffsetId().toFormatter(),
			new DateTimeFormatterBuilder().append(DateTimeFormatter.ISO_LOCAL_DATE).appendLiteral(' ').append(DateTimeFormatter.ISO_LOCAL_TIME).appendZoneOrOffsetId().toFormatter(),
			new DateTimeFormatterBuilder().append(DateTimeFormatter.ISO_LOCAL_DATE).appendLiteral(' ').append(DateTimeFormatter.ISO_LOCAL_TIME).appendLiteral(' ').appendZoneOrOffsetId().toFormatter(),
			
			new DateTimeFormatterBuilder().append(DateTimeFormatter.ISO_LOCAL_DATE).appendLiteral('T').append(DateTimeFormatter.ISO_TIME).appendOptional(dateTimeZoneBracketed).toFormatter(),
			new DateTimeFormatterBuilder().append(DateTimeFormatter.ISO_LOCAL_DATE).appendLiteral('T').append(DateTimeFormatter.ISO_TIME).appendLiteral(' ').appendOptional(dateTimeZoneBracketed).toFormatter(),
			new DateTimeFormatterBuilder().append(DateTimeFormatter.ISO_LOCAL_DATE).appendLiteral(' ').append(DateTimeFormatter.ISO_TIME).appendOptional(dateTimeZoneBracketed).toFormatter(),
			new DateTimeFormatterBuilder().append(DateTimeFormatter.ISO_LOCAL_DATE).appendLiteral(' ').append(DateTimeFormatter.ISO_TIME).appendLiteral(' ').appendOptional(dateTimeZoneBracketed).toFormatter(),
			
			new DateTimeFormatterBuilder().append(DateTimeFormatter.ISO_LOCAL_TIME).appendZoneOrOffsetId().toFormatter(),
			new DateTimeFormatterBuilder().append(DateTimeFormatter.ISO_TIME).appendOptional(dateTimeZoneBracketed).toFormatter()
			
		}));
}
