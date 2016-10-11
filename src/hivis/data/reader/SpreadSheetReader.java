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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import hivis.data.DataEvent;
import hivis.data.DataListener;
import hivis.data.DataSeries;
import hivis.data.DataSeriesGeneric;
import hivis.data.DataSeriesDouble;
import hivis.data.DataTable;
import hivis.data.DataTableDefault;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;


public class SpreadSheetReader implements DataSetSource<DataTable> {
	private DataTableDefault dataset;
	
	private File sourceFile;
	
	private int sheetIndex;
	private int rowCount;
	private int colCount;
	
	private int headerRowIndex = -1;
	
	private int firstDataRowIndex;
	private int firstDataColumnIndex;
	
	private List<Integer> currentDataCellTypes = new ArrayList<>();
	
	WatchService watcher; // Service to monitor the file for changes.
	
	
	/**
	 * Create a new SpreadSheetReader that reads data from the specified file and sheet, 
	 * with data beginning at the specified row and column.
	 */
	public SpreadSheetReader(String filepath, int sheetIndex, int headerRowIndex, int firstDataRow, int firstDataColumn) {
		this.sheetIndex = sheetIndex;
		sourceFile = new File(filepath);
		System.out.println("SpreadSheetReader reading from " + sourceFile.getAbsolutePath());
		firstDataRowIndex = firstDataRow;
		firstDataColumnIndex = firstDataColumn;
		this.headerRowIndex = headerRowIndex;
		
		try {
			watcher = FileSystems.getDefault().newWatchService();
			Paths.get(sourceFile.getParentFile().getAbsolutePath()).register(watcher, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
			(new FileChangeMonitor(sourceFile.getParentFile().toPath())).start();
		} catch (IOException e) {
			System.err.println("Unable to monitor file for changes. Error occurred: " + e.getMessage());
			throw new RuntimeException(e);
		}
		
		dataset = new DataTableDefault();
		readFromExcel();	
	}
	
	
	private synchronized void readFromExcel() {
		try {
			Workbook wb = WorkbookFactory.create(sourceFile);
			
			Sheet sheet = wb.getSheetAt(sheetIndex);
			
			rowCount = sheet.getLastRowNum() + 1;
			colCount = sheet.getRow(0).getLastCellNum();
			
			Row headerRow = headerRowIndex == -1 ? null : sheet.getRow(headerRowIndex);
			
			// Notify table we're going to make changes to it (to suppress events being fired
			// every time we add or remove a series.
			dataset.beginChanges(this);
			
			// Ensure series in data table are consistent with what's in the sheet (or set-up for first read).
			// We use the first row of data to determine the type of a series.
			List<String> columnLabels = new ArrayList<>(colCount);
			List<Integer> columnIndices = new ArrayList<>(colCount);
			List<DataSeries<?>> series = new ArrayList<>(colCount);
			List<Integer> newDataCellTypes = new ArrayList<>();
			for (int c = firstDataColumnIndex, ci = 0; c < colCount; c++, ci++) {
				// For some reason getStringCellValue() is throwing an exception for blank 
				// cells instead of returning an empty string like it's supposed to.
				String label = null;
				try {
					label = headerRow == null ? ""+ci : headerRow.getCell(c).getStringCellValue();
				}
				catch (Exception ex) {}
				
				// Ignore columns with no header (when a header row is defined).
				if (label == null || label.length() == 0) {
					continue;
				}
				
				Cell sampleCell = findSampleDataCellInColumn(sheet, c, firstDataRowIndex, rowCount);
				// Ignore columns for which we cannot determine a valid data type from any cell.
				if (sampleCell == null) {
					continue;
				}
				
				columnLabels.add(label);
				columnIndices.add(c);
				
				newDataCellTypes.add(sampleCell.getCellType());
				
				// If this is a new column (or the header was renamed).
				if (!dataset.hasSeries(label)) {
					dataset.addSeries(label, dataSeriesFromSampleCell(sampleCell));
				}
				// If the type/formatting of the data was changed.
				else if (currentDataCellTypes.size() <= ci || currentDataCellTypes.get(ci) != newDataCellTypes.get(ci)) {
					dataset.removeSeries(label);
					dataset.addSeries(label, dataSeriesFromSampleCell(sampleCell));
				}
				
				series.add(dataset.getSeries(label));
			}
			
			currentDataCellTypes = newDataCellTypes;
			

			// Notify series that we're going to make changes to them 
			// (to suppress events being fired every time we change the values).
			for (DataSeries<?> s : series) {
				s.beginChanges(this);
			}
			
			// Remove series for which the columns have been removed from the sheet (or which were renamed),
			// otherwise notify the series we're going to make changes to it (to suppress events being fired
			// every time we change a value, and then remove values from series if the number of rows in the 
			// sheet has been reduced.
			List<String> currentLabels = new ArrayList<>(dataset.getSeriesLabels());
			for (String currentLabel : currentLabels) {
				if (!columnLabels.contains(currentLabel)) {
					dataset.removeSeries(currentLabel);
				}
				else {
					DataSeries<?> s = dataset.getSeries(currentLabel);
					
					// If values have been removed from the sheet, remove them from the series.
					if (s.length() > rowCount - firstDataRowIndex) {
						while (s.length() > rowCount - firstDataRowIndex) {
							s.remove(s.length() - 1);
						}
					}
				}
			}
			
			// Read the data and set in Dataset.
			for (int r = firstDataRowIndex, ri = 0; r < rowCount; r++, ri++) {
				Row row = sheet.getRow(r);
				
				for (int c = 0; c < columnLabels.size(); c++) {
					int ci = columnIndices.get(c);
					String label = columnLabels.get(c);
					// Ignore columns with no header (when a header row is defined).
					if (label == null || label.length() == 0) {
						continue;
					}
					
					DataSeries<?> s = dataset.getSeries(label);
					
					
					try {
						Cell cell = row.getCell(ci);
						Object val = cell == null ? null : getCellValue(cell);
						
						if (val == null) {
							val = s.getEmptyValue();
						}
						
						if (ri >= s.length()) {
							s.append(val);
						}
						else {
							s.set(ri, val);
						}
					}
					catch (IllegalArgumentException ex) {
						System.err.println("Warning: data type/format mismatch" + ex.getMessage());
					}
				}
			}
			
			// Notify series and table that changes are complete.
			for (DataSeries<?> s : series) {
				s.finishChanges(this);
			}
			dataset.finishChanges(this);
		} catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
			throw new DataReadException("Unable to read data from " + sourceFile.getPath(), e);
		}
	}
	
	
	/** 
	 * Attempt to get the value of a cell as a numeric value, handling numeric, formula and string cell types.
	 * @return The cell value or NaN if the cell does not appear to contain numeric data.
	 */
	private Object getCellValue(Cell cell) {
		int type = cell.getCellType();
		
		if (type == Cell.CELL_TYPE_NUMERIC || type == Cell.CELL_TYPE_FORMULA) {
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue();
			}
			return cell.getNumericCellValue();
		}
		
		if (type == Cell.CELL_TYPE_STRING) {
			return cell.getStringCellValue();
		}
		
		return null;
	}
	
	
	private Cell findSampleDataCellInColumn(Sheet sheet, int columnIndex, int firstRowIndex, int rowCount) {
		for (int row = firstRowIndex; row < rowCount; row++) {
			Cell cell = sheet.getRow(row).getCell(columnIndex);
			
			if (cell != null) {
				int type = cell.getCellType();
				if (type == Cell.CELL_TYPE_NUMERIC || type == Cell.CELL_TYPE_FORMULA || type == Cell.CELL_TYPE_STRING) {
					return cell;
				}
			}
		}
		return null;
	}
	
	
	private DataSeries<?> dataSeriesFromSampleCell(Cell cell) {
		int type = cell.getCellType();
		
		if (type == Cell.CELL_TYPE_NUMERIC || type == Cell.CELL_TYPE_FORMULA) {
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				return new DataSeriesGeneric<Date>();
			}
			return new DataSeriesDouble();
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
						if (modifiedFile.equals(sourceFile)) {
							if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
								System.err.println("The data source file " + sourceFile.getAbsolutePath() + " was deleted.");
								return;
							} else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
								try {
									readFromExcel();
								}
								catch (Exception ex) {
									System.err.println("Error reading modified source file \"" + sourceFile.getAbsolutePath() + "\". Will try again on next modification. Error was: " + ex.getMessage());
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
					System.err.println("An error occurred monitoring the file " + sourceFile.getAbsolutePath() + " for changes:");
					ex.printStackTrace();
					return;
				}
			}
		}
	}
	
	public static void main(String[] args) {
		  int sheetIndex = 1;
		  int headerRowIndex = 0;
		  int firstDataRow = 3;
		  int firstDataColumn = 0;
		  SpreadSheetReader reader = new SpreadSheetReader("/home/data/processing/sketchbook/libraries/HiVis/examples/HV6_InteractiveSonification/KIB - Oil Well.xlsx", sheetIndex, headerRowIndex, firstDataRow, firstDataColumn);
		  DataTable data = reader.getData();
		  System.out.println("\ndata:\n" + data);
		  
		  // Get the series/columns we're interested in.
		  // Transform some to unit range [0, 1] to make them easier to work with.
		  DataSeries<Float> retailMarkup = data.getSeries(7).toUnitRange().asFloat();
		  DataSeries<Float> healthRating = data.getSeries(19).toUnitRange().asFloat();
		  DataSeries<Float> retailCost = data.getSeries(6).asFloat();
		  DataSeries<Integer> tasteStrength = data.getSeries(2).asInt();
		  
		  System.out.println("\nretailMarkup:\n" + retailMarkup);
		  System.out.println("\nhealthRating:\n" + healthRating);
		  System.out.println("\nretailCost:\n" + retailCost);
		  System.out.println("\ntasteStrength:\n" + tasteStrength);
		  
		  
		  /*
		SpreadSheetReader reader = new SpreadSheetReader(args[0], 0, 0, 1, 0);
		DataTable data = reader.getData();
		System.out.println(data.toString());
		
		data.addChangeListener(new DataListener() {
			@Override
			public void dataChanged(DataEvent event) {
				System.out.println(event.toString());
			}
		});
		*/
	}
}
