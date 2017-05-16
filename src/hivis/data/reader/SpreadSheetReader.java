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

package hivis.data.reader;

import java.io.*;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

import hivis.common.HV;
import hivis.common.Util;
import hivis.data.DataSeries;
import hivis.data.DataSeriesGeneric;
import hivis.data.DataSeriesDouble;
import hivis.data.DataSeriesFloat;
import hivis.data.DataTable;
import hivis.data.DataTableDefault;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;

/**
 * Class to read data from a spreadsheet file. The data is presented as a {@link DataTable}. 
 * The DataTable is automatically updated when the file changes.
 * Currently Excel (xlsx) and CSV-like files are supported.
 * 
 * TODO Currently columns in the sheet are read as DataSeries. Add support for row-based format.
 * 
 * @author O. J. Coleman
 */
public class SpreadSheetReader implements DataSetSource<DataTable> {
	private DataTableDefault dataset;
	
	private Config conf;
	
	Sheet excelSheet; // Excel sheet, if applicable.
	List<List<String>> textSheet;
	
	private Map<Integer, DateTimeFormatter> columnDateFormatMap;
	
	private int lastRowIndex;
	private int lastColumnIndex;
	private boolean hasHeaderRow;
	private int lastRowIndexDesired;
	private int lastColumnIndexDesired;
	
	private List<CellType> columnCellTypes = new ArrayList<>();
	
	WatchService watcher; // Service to monitor the file for changes.
	
	
	/**
	 * <p>Load data from the specified file.</p>
	 * <p>If the first row contains all strings, except one column at most, then it is used as the header row.</p>
	 * <p>The generated DataTable will be updated in real time as changes are saved to the file.</p>
	 * <p>DataSeries are created to match the data type for a column (using the first non-empty data element of a column):
	 * 	<dl>
	 * 		<dt>Numeric: </td><dd>DataSeries&lt;Double&gt;.</dd>
	 * 		<dt>Date: </td><dd>DataSeries&lt;TemporalAccessor&gt; (for columns formatted as dates/times (Excel), or text that 
	 * 			looks like an ISO-like date or time (CSV).</dd>
	 * 		<dt>String: </td><dd>DataSeries&lt;String&gt; (for everything else).</dd>
	 * 	</dl>
	 * </p>
	 * 
	 * @param file The spreadsheet file.
	 */
	public SpreadSheetReader(File file) {
		this(file, 0, -2, 0, 0);
	}
	
	/**
	 * Create a new SpreadSheetReader that reads data from the specified file and sheet, 
	 * with data beginning at the specified row and column.
	 * Numeric series will be created as DataSeries&lt;Double&gt;.
	 * 
	 * @param file The spreadsheet file.
	 * @param sheet The zero-based index of the sheet in the spreadsheet to read from (if applicable).
	 * @param headerRow The zero-based index of the row to use as column headers, or -1 for no header row.
	 * @param firstDataRow The zero-based index of the row to start reading data from (to the end of the sheet).
	 * @param firstDataColumn The zero-based index of the column to start creating series from (up to the last column).
	 * @deprecated As of 2.0. Superseded by {@link #SpreadSheetReader(hivis.data.reader.SpreadSheetReader.Config)}. This constructor will be removed in future releases.
	 */
	public SpreadSheetReader(File file, int sheet, int headerRow, int firstDataRow, int firstDataColumn) {
		this(file, sheet, headerRow, firstDataRow, firstDataColumn, true);
	}
	
	/**
	 * Create a new SpreadSheetReader that reads data from the specified file and sheet, 
	 * with data beginning at the specified row and column.
	 *
	 * @param file The spreadsheet file.
	 * @param sheet The zero-based index of the sheet in the spreadsheet to read from (if applicable).
	 * @param headerRow The zero-based index of the row to use as column headers, or -1 for no header row.
	 * @param firstDataRow The zero-based index of the row to start reading data from (to the end of the sheet).
	 * @param firstDataColumn The zero-based index of the column to start creating series from (up to the last column).
	 * @param doublePrecision If true then numeric series will be created as DataSeries&lt;Double&gt;, otherwise they will be created as DataSeries&lt;Float&gt;.
	 * @deprecated As of 2.0. Superseded by {@link #SpreadSheetReader(hivis.data.reader.SpreadSheetReader.Config)}. This constructor will be removed in future releases.
	 */
	public SpreadSheetReader(File file, int sheet, int headerRow, int firstDataRow, int firstDataColumn, boolean doublePrecision) {
		this(new Config().sourceFile(file).sheetIndex(sheet).headerRowIndex(headerRow).rowIndex(firstDataRow).columnIndex(firstDataColumn).doublePrecision(doublePrecision));
	}
	
	
	/**
	 * <p>Load data from a file using the given configuration.</p>
	 * <p>The generated DataTable will be updated in real time as changes are saved to the file.</p>
	 * <p>DataSeries are created to match the data type for a column (using the first non-empty data element of a column):
	 * 	<dl>
	 * 		<dt>Numeric: </td><dd>DataSeries&lt;Double/Float&gt; (see {@link SpreadSheetReader.Config#doublePrecision(boolean)}.</dd>
	 * 		<dt>Date: </td><dd>DataSeries&lt;TemporalAccessor/Date&gt; (for columns formatted as dates/times (Excel), 
	 * 			or text that looks like an ISO-like date or time (CSV). See {@link SpreadSheetReader.Config#useDeprecatedDates(boolean)}).</dd>
	 * 		<dt>String: </td><dd>DataSeries&lt;String&gt; (for everything else).</dd>
	 * 	</dl>
	 *
	 * @param config The configuration describing how to read and process the spreadsheet.
	 */
	public SpreadSheetReader(Config config) {
		conf = config.copy();
		
		if (conf.fileFormat == Config.AUTO) {
			String sourceFileName = conf.sourceFile.getName();
			if (sourceFileName.endsWith(".xlsx")) {
				conf.fileFormat = Config.EXCEL;
			} else if (sourceFileName.endsWith(".csv")) {
				conf.fileFormat = Config.CSV;
			} else if (sourceFileName.endsWith(".tsv")) {
				conf.fileFormat = Config.CSV;
				conf.csvSeparator = "\t";
			}
			else {
				throw new IllegalArgumentException("Could not determine file format of " + conf.sourceFile.getAbsolutePath());
			}
		}
		
		try {
			watcher = FileSystems.getDefault().newWatchService();
			Paths.get(conf.sourceFile.getParentFile().getAbsolutePath()).register(watcher, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
			(new FileChangeMonitor(conf.sourceFile.getParentFile().toPath())).start();
		} catch (IOException e) {
			System.err.println("Unable to monitor file for changes. Error occurred: " + e.getMessage());
			throw new RuntimeException(e);
		}
		
		dataset = new DataTableDefault();
		readData();	
	}
	
	
	
	private void readData() {
		try {
			if (conf.fileFormat == Config.EXCEL) {
				Workbook wb = WorkbookFactory.create(conf.sourceFile);
				excelSheet = wb.getSheetAt(conf.sheetIndex);
				lastRowIndex = excelSheet.getLastRowNum();
				lastColumnIndex = excelSheet.getRow(0).getLastCellNum()-1;
			} 
			else {
				// Create file format preference.
				CsvPreference.Builder csvb = new CsvPreference.Builder(conf.csvQuote.charAt(0), conf.csvSeparator.charAt(0), "\r\n");
				// Don't ignore empty lines to make indexing behaviour consistent between excel and csv/tsv files. 
				csvb.ignoreEmptyLines(false);
				
				lastColumnIndex = 0;
				textSheet = new ArrayList<>();
				columnDateFormatMap = new HashMap<>();
				try (CsvListReader listReader = new CsvListReader(new FileReader(conf.sourceFile), csvb.build())) {
					List<String> row;
					while ((row = listReader.read()) != null) {
						textSheet.add(row);
						lastColumnIndex = Math.max(lastColumnIndex, row.size()-1);
					}
				}
				lastRowIndex = textSheet.size()-1;
			}
			
			// Determine index of last row and column.
			if (conf.dataRowCountDesired <= 0) {
				lastRowIndexDesired = lastRowIndex;
			} else {
				lastRowIndexDesired = Math.min(conf.firstDataRowIndex + conf.dataRowCountDesired - 1, lastRowIndex);
			}
			if (conf.dataColumnCountDesired <= 0) {
				lastColumnIndexDesired = lastColumnIndex;
			} else {
				lastColumnIndexDesired = Math.min(conf.firstDataColumnIndex + conf.dataColumnCountDesired - 1, lastColumnIndex);
			}
			int dataColumnCountActual = (lastColumnIndexDesired-conf.firstDataColumnIndex)+1;
			
			
			if (conf.headerRowIndex == Config.AUTO) {
				// Determine if the first row is a header row.
				int stringCount = 0;
				for (int c = 0; c <= lastColumnIndex; c++) {
					if (getCellType(0, c) == CellType.STRING) {
						stringCount++;
					}
				}
				
				// If the first row contains all strings, except one column at most.
				if (stringCount >= lastColumnIndexDesired) {
					// Use it as the header row.
					conf.headerRowIndex = 0;
					conf.firstDataRowIndex = Math.max(conf.firstDataRowIndex, conf.headerRowIndex + 1);
				}
			}
			hasHeaderRow = conf.headerRowIndex >= 0;
			
			System.err.println("ssr 1");
			
			// Notify table we're going to make changes to it (to suppress events being fired
			// every time we add or remove a series.
			dataset.beginChanges(this);
			
			// Ensure series in data table are consistent with what's in the sheet (or set-up for first read).
			// We use the first row of data to determine the type of a series.
			List<String> columnLabels = new ArrayList<>(dataColumnCountActual);
			List<Integer> columnIndices = new ArrayList<>(dataColumnCountActual);
			List<DataSeries<?>> series = new ArrayList<>(dataColumnCountActual);
			List<CellType> newDataCellTypes = new ArrayList<>();
			for (int c = conf.firstDataColumnIndex, ci = 0; c <= lastColumnIndexDesired; c++, ci++) {
				String label = hasHeaderRow ? getStringCellValue(conf.headerRowIndex, c) : ""+ci;
				
				if (label.trim().length() == 0) {
					// Ignore columns with no header (when a header row is defined and the ignoreColumnsWithNoHeader option is set).
					if (conf.ignoreColumnsWithNoHeader)
						continue;
					label = ""+ci;
				}
				
				CellType sampleCellType = findSampleDataCellTypeInColumn(c, conf.firstDataRowIndex, lastRowIndexDesired);
				// Ignore columns for which we cannot determine a valid data type from any cell.
				if (sampleCellType == null) {
					continue;
				}
				
				columnLabels.add(label);
				columnIndices.add(c);
				
				newDataCellTypes.add(sampleCellType);
				
				// If this is a new column (or the header was renamed).
				if (!dataset.hasSeries(label)) {
					dataset.addSeries(label, dataSeriesFromCellType(sampleCellType));
				}
				// If the type/formatting of the data was changed.
				else if (columnCellTypes.size() <= ci || columnCellTypes.get(ci) != newDataCellTypes.get(ci)) {
					dataset.removeSeries(label);
					dataset.addSeries(label, dataSeriesFromCellType(sampleCellType));
				}
				
				series.add(dataset.getSeries(label));
			}
			
			columnCellTypes = newDataCellTypes;
							
			// Remove series for which the columns have been removed from the sheet (or which were renamed).
			List<String> currentLabels = new ArrayList<>(dataset.getSeriesLabels());
			for (String currentLabel : currentLabels) {
				if (!columnLabels.contains(currentLabel)) {
					dataset.removeSeries(currentLabel);
				}
			}
			
			// Read the data and set in Dataset.
			for (int colLabelIdx = 0; colLabelIdx < columnLabels.size(); colLabelIdx++) {
				int column = columnIndices.get(colLabelIdx);
				String label = columnLabels.get(colLabelIdx);
				
				DataSeries<?> s = dataset.getSeries(label);
				
				s.beginChanges(this);
				
				s.resize((lastRowIndexDesired - conf.firstDataRowIndex) + 1);
				
				for (int row = conf.firstDataRowIndex, ri = 0; row <= lastRowIndexDesired; row++, ri++) {
					try {
						Object val = getCellValue(row, column, columnCellTypes.get(colLabelIdx));
						
						if (val == null) {
							val = s.getEmptyValue();
						}
						
						s.set(ri, val);
					}
					catch (IllegalArgumentException ex) {
						// Only emit warning if cell is not empty.
						if (getStringCellValue(row, column).trim().length() > 0) {
							System.err.println("Warning: value in column '" + label + "', row " + row + ", does not match expected column data type/format: " + ex.getMessage());
						}
					}
				}
				
				s.finishChanges(this);
			}
			
			dataset.finishChanges(this);
			System.err.println("ssr 3");
		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			throw new DataReadException("Unable to read data from " + conf.sourceFile.getPath(), e);
		}
	}
	
	
	/**
	 * Determines the CellType of the value at the given row and column.
	 */
	private CellType getCellType(int row, int column) {
		if (excelSheet != null) {
			return CellType.fromExcel(excelSheet.getRow(row).getCell(column), conf);
		}
		
		if (column >= textSheet.get(row).size()) return CellType.BLANK;
		return CellType.fromRaw(textSheet.get(row).get(column), conf);
	}
	
	
	/**
	 * Get the value at the given row and column. Returns the appropriate object 
	 * type (String, Numeric, Date), or null if there is no value present.
	 */
	private Object getCellValue(int row, int column, CellType type) {
		if (type == null) type = getCellType(row, column);
		
		if (excelSheet != null) {
			Cell cell = excelSheet.getRow(row).getCell(column);
			if (cell == null) return null;
			try {
				switch (type) {
					case STRING: return cell.getStringCellValue();
					case BOOLEAN: return cell.getBooleanCellValue();
					case NUMERIC: return cell.getNumericCellValue();
					case DATE: return cell.getDateCellValue();
					default: return null;
				}
			}
			catch (Exception e) {}
			return null;
		}
		
		String v = getStringCellValue(row, column);
		switch (type) {
			case STRING: 
				return v;
			case NUMERIC: 
				try {
					return Double.parseDouble(v);
				}
				catch (Exception e) { return null; }
			case DATE:
				// Try format that worked previously for this column if we found one.
				if (columnDateFormatMap.containsKey(column)) {
					try {
						TemporalAccessor dt = Util.parseDateTime(v, columnDateFormatMap.get(column));
						return conf.useDeprecatedDates ? Util.temporalAccessorToDate(dt) : dt;
					} catch (DateTimeParseException e) {}
				}
				// Try to determine the date/time format.
				DateTimeFormatter format = Util.determineDateTimeFormat(v, conf.dateFormats);
				if (format != null) {
					columnDateFormatMap.put(column, format);
					// Shouldn't get an exception here since Util.determineDateFormat just provided this format because it works.
					try {
						TemporalAccessor dt = Util.parseDateTime(v, format);
						return conf.useDeprecatedDates ? Util.temporalAccessorToDate(dt) : dt;
					} catch (DateTimeParseException e) {}
				}
				return null;
			default: return null;
		}
	}
	
	
	/**
	 * Get the value at the given row and column as a String. 
	 * Returns the empty String if no value is present.
	 */
	private String getStringCellValue(int row, int column) {
		String val = null;
		if (excelSheet != null) {
			Cell cell = excelSheet.getRow(row).getCell(column);
			if (cell != null) val = cell.getStringCellValue();
		}
		else {
			if (column < textSheet.get(row).size()) val = textSheet.get(row).get(column);
		}
		if (val == null) return "";
		return val;
	}
	
	
	/**
	 * Finds the first non-empty value in the given column and returns its type.
	 */
	private CellType findSampleDataCellTypeInColumn(int columnIndex, int firstRowIndex, int lastRow) {
		for (int row = firstRowIndex; row <= lastRow; row++) {
			CellType type = getCellType(row, columnIndex);
			if (type != CellType.BLANK) {
				return type;
			}
		}
		return null;
	}
	
	
	/**
	 * Return a DataSeries suitable for the given CellType.
	 */
	private DataSeries<?> dataSeriesFromCellType(CellType type) {
		if (type == CellType.NUMERIC) {
			return conf.doublePrecision ? new DataSeriesDouble() : new DataSeriesFloat();
		}
		if (type == CellType.DATE) {
			return conf.useDeprecatedDates ? new DataSeriesGeneric<Date>() : new DataSeriesGeneric<TemporalAccessor>();
		}
		if (type == CellType.BOOLEAN) {
			return new DataSeriesGeneric<Boolean>();
		}
		// Default to String.
		return new DataSeriesGeneric<String>();
	}
	
	
	@Override
	public DataTable getData() {
		return dataset;
	}
	
	

	/**
	 * Thread to respond to changes to file.
	 */
	private class FileChangeMonitor extends Thread {
		Path dir;
		
		public FileChangeMonitor(Path dir) {
			this.dir = dir;
		}
		
		public void run() {
			while (true) {
				try {
					WatchKey key = watcher.take();
					
					for (WatchEvent<?> event : key.pollEvents()) {
						File modifiedFile = dir.resolve((Path) event.context()).toFile();
						
						// If the source file has changed.
						if (modifiedFile.equals(conf.sourceFile)) {
							if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
								System.err.println("The data source file " + conf.sourceFile.getAbsolutePath() + " was deleted.");
								return;
							} else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
								try {
									readData();
								}
								catch (Exception ex) {
									System.err.println("Error reading modified source file \"" + conf.sourceFile.getAbsolutePath() + "\". Will try again on next modification. Error was: " + ex.getMessage());
								}
							}
						}
					}
	
					boolean valid = key.reset();
					if (!valid) {
						throw new IllegalStateException("The watch service key has become invalid.");
					}
				}
				catch (ClosedWatchServiceException cwse) {
					// This is expected if the watch service is closed when terminate() is called.
				}
				catch (Exception ex) {
					System.err.println("An error occurred monitoring the file " + conf.sourceFile.getAbsolutePath() + " for changes:");
					ex.printStackTrace();
					return;
				}
			}
		}
	}
	
	private enum CellType {
		BLANK,
		BOOLEAN,
		NUMERIC,
		DATE,
		STRING;
		
		public static CellType fromExcel(Cell c, Config conf) {
			if (c == null) return BLANK;
			
			int type = c.getCellType();
			
			if (type == Cell.CELL_TYPE_BOOLEAN) return BOOLEAN;
			if (type == Cell.CELL_TYPE_STRING) return STRING;
		
			if (type == Cell.CELL_TYPE_FORMULA || type == Cell.CELL_TYPE_NUMERIC) {
				if (HSSFDateUtil.isCellDateFormatted(c)) {
					return DATE;
				}
				return NUMERIC;
			}
			
			return BLANK;
		}
		
		public static CellType fromRaw(String v, Config conf) {
			if (v == null || v.trim().length() == 0)
				return BLANK;
			
			try {
				Double.parseDouble(v);
				return NUMERIC;
			}
			catch (Exception e) {}
			
			DateTimeFormatter format = Util.determineDateTimeFormat(v, conf.dateFormats);
			if (format != null) return DATE;
			
			return STRING;
		}
	}
	
	/**
	 * Defines the configuration for a SpreadSheetReader.
	 */
	public static class Config implements Cloneable {
		public static final int AUTO = -2;
		public static final int NONE = -1;
		public static final int EXCEL = 0;
		public static final int CSV = 1;
		
		protected File sourceFile;
		/**
		 * Set the file to read data from.
		 */
		public Config sourceFile(File sourceFile) { this.sourceFile = sourceFile; return this; }
		/**
		 * Set the file to read data from.
		 */
		public Config sourceFile(String sourceFile) { 
			this.sourceFile = new File(sourceFile); return this; }
		
		protected int fileFormat = AUTO;
		/**
		 * Set the format of the data, options are {@link #AUTO}, {@link #EXCEL}, {@link #CSV}.
		 * @see #csvSeparator
		 * @see #csvQuote
		 */
		public Config fileFormat(int fileFormat) { this.fileFormat = fileFormat; return this; }
		
		protected int sheetIndex = 0;
		/**
		 * Set the (zero-based) index of the sheet in the spreadsheet to read from (if applicable). 
		 */
		public Config sheetIndex(int sheetIndex) { this.sheetIndex = sheetIndex; return this; }
		
		protected int headerRowIndex = AUTO;
		/**
		 * Set the (zero-based) index of the row to use as column headers, or {@link #AUTO} or {@link #NONE}. Default is AUTO.
		 */
		public Config headerRowIndex(int headerRowIndex) { this.headerRowIndex = headerRowIndex; return this; }
		
		protected int firstDataRowIndex = 0;
		/**
		 * Set the zero-based index of the row to start reading data from. Default is 0.
		 */
		public Config rowIndex(int rowIndex) { this.firstDataRowIndex = rowIndex; return this; }
		
		protected int dataRowCountDesired = AUTO;
		/**
		 * Set the number of rows to extract (not including header), or {@link #AUTO} to read to the end of the sheet.
		 */
		public Config rowCount(int rowCount) { this.dataRowCountDesired = rowCount; return this; }

		protected int firstDataColumnIndex = 0;
		/**
		 * Set the (zero-based) index of the column to start creating series from. Default is 0.
		 */
		public Config columnIndex(int columnIndex) { this.firstDataColumnIndex = columnIndex; return this; }
		
		protected int dataColumnCountDesired = AUTO;
		/**
		 * Set the number of columns to extract, or {@link #AUTO} to read up to the last column.
		 */
		public Config columnCount(int columnCount) { this.dataColumnCountDesired = columnCount; return this; }
		
		protected boolean doublePrecision = false;
		/**
		 * Set whether numeric series should be created as DataSeries&lt;Double&gt; (true), or as DataSeries&lt;Float&gt; (false).
		 */
		public Config doublePrecision(boolean doublePrecision) { this.doublePrecision = doublePrecision; return this; }
		
		protected boolean ignoreColumnsWithNoHeader = false;
		/**
		 * Set whether to ignore columns that have an empty header value. Only applicable when a header row has been set (manually or automatically detected).
		 */
		public Config ignoreColumnsWithNoHeader(boolean ignoreColumnsWithNoHeader) { this.ignoreColumnsWithNoHeader = ignoreColumnsWithNoHeader; return this; }
		
		protected String csvSeparator = ",";
		/**
		 * Set the value separator character, only applicable for {@link #CSV} format. Use "\t" for tab-separated (TSV). Default is "," (CSV).
		 */
		public Config csvSeparator(String csvSeparator) { this.csvSeparator = csvSeparator; return this; }
		
		protected String csvQuote = "\"";
		/**
		 * Set the value quote character, only applicable for {@link #CSV} format. Default is ".
		 */
		public Config csvQuote(String csvQuote) { this.csvQuote = csvQuote; return this; }
		
		protected String[] dateFormats = new String[0];
		/**
		 * Set custom date/time formats for parsing dates, only applicable for {@link #CSV} format.  See https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
		 */
		public Config dateFormats(String... formats) { this.dateFormats = formats; return this; }
		
		protected boolean useDeprecatedDates = false;
		/**
		 * Set whether to use the (now deprecated) java.util.Date objects to represent dates/times (true), instead of java.time.temporal.TemporalAccessor (false). Default is false.
		 */
		public Config useDeprecatedDates(boolean useDeprecatedDates) { this.useDeprecatedDates = useDeprecatedDates; return this; }
		
		/**
		 * Creates a copy of this Config.
		 */
		public Config copy() { 
			try {
				// Most fields can be (shallow) copied by value.
				Config copy = (Config) clone();
				// Just not the date format array.
				copy.dateFormats = Arrays.copyOf(dateFormats, dateFormats.length);
				return copy;
			} catch (CloneNotSupportedException e) {}
			return null;
		}
	}
}
