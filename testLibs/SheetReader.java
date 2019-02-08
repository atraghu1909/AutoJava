package testLibs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.testng.Reporter;

public class SheetReader {

	private Workbook workbook;

	public static List<List<String>> transpose(final List<List<String>> input) {
		final List<List<String>> transposedInput = new ArrayList<List<String>>();
		final int limit = input.get(0).size();
		for (int i = 0; i < limit; i++) {
			final List<String> col = new ArrayList<String>();
			for (final List<String> row : input) {
				col.add(row.get(i));
			}
			transposedInput.add(col);
		}
		return transposedInput;
	}

	public List<List<String>> cleanupSheet(final List<List<String>> input) {
		List<List<String>> output = input;
		boolean allNull = true;

		for (int i = 0; i < output.size(); i++) {
			allNull = true;
			for (int j = 0; j < output.get(i).size() && allNull; j++) {
				allNull = output.get(i).get(j).isEmpty() || " ".equals(output.get(i).get(j));
			}
			if (allNull) {
				output.get(i).clear();
			}
		}

		output.removeIf(item -> item.isEmpty());
		output = transpose(output);

		for (int i = 0; i < output.size(); i++) {
			allNull = true;
			for (int j = 0; j < output.get(i).size() && allNull; j++) {
				allNull = output.get(i).get(j).isEmpty() || " ".equals(output.get(i).get(j));
			}
			if (allNull) {
				output.get(i).clear();
			}
		}

		output.removeIf(item -> item.isEmpty());
		output = transpose(output);

		return output;
	}

	public List<List<String>> getExcelContentsRows(final String path, final String sheetName) throws IOException {
		List<List<String>> result = null;
		if (openFile(path)) {
			final DataFormatter dataF = new DataFormatter();
			final Sheet sheet = workbook.getSheet(sheetName);
			final List<List<String>> sheetContents = new ArrayList<List<String>>();
			final Iterator<Row> rows = sheet.rowIterator();
			Short lastValidCol = -1;
			Row row = rows.next();

			// Find the last column on the first row that is not empty
			lastValidCol = row.getLastCellNum();
			while (dataF.formatCellValue(row.getCell(lastValidCol)).isEmpty() && lastValidCol != 0) {
				lastValidCol--;
			}

			while (rows.hasNext()) {

				sheetContents.add(new ArrayList<String>());
				for (int i = 0; i <= lastValidCol; i++) {
					final Cell cell = row.getCell(i);
					if (dataF.formatCellValue(cell).isEmpty()) {
						sheetContents.get(row.getRowNum()).add(" ");
					} else {
						sheetContents.get(row.getRowNum()).add(dataF.formatCellValue(cell));
					}
				}
				row = rows.next();
			}

			workbook.close();
			result = cleanupSheet(sheetContents);
		} else {
			Reporter.log("Failed to open Excel file", true);
		}
		return result;
	}

	public List<String> getTabNamesFromExcelSpreadsheet(final String path) throws IOException {
		ArrayList<String> result = null;
		if (openFile(path)) {
			final ArrayList<String> tabNames = new ArrayList<String>();
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				tabNames.add(workbook.getSheetAt(i).getSheetName());
			}
			workbook.close();
			result = tabNames;

		} else {
			Reporter.log("Failed to open Excel file", true);
		}
		return result;
	}

	public List<List<String>> getExcelContentsColumns(final String path, final String sheetName) throws IOException {
		List<List<String>> result = null;
		if (openFile(path)) {
			final DataFormatter dataF = new DataFormatter();
			final Sheet sheet = workbook.getSheet(sheetName);
			final List<List<String>> sheetContents = new ArrayList<List<String>>();
			Iterator<Row> rows = sheet.rowIterator();
			Short lastValidCol = -1;
			Row row = rows.next();

			// Find the last column on the first row that is not empty
			lastValidCol = row.getLastCellNum();
			while (dataF.formatCellValue(row.getCell(lastValidCol)).isEmpty() && lastValidCol != 0) {
				lastValidCol--;
			}

			for (int i = 0; i <= lastValidCol; i++) {
				sheetContents.add(new ArrayList<String>());

				while (rows.hasNext()) {
					final Cell cell = row.getCell(i);
					if (dataF.formatCellValue(cell).isEmpty()) {
						sheetContents.get(i).add(" ");
					} else {
						sheetContents.get(i).add(dataF.formatCellValue(cell));
					}
					row = rows.next();
				}
				rows = sheet.rowIterator();
				row = rows.next();
			}

			workbook.close();
			sheetContents.removeIf(item -> item.isEmpty());
			for (int k = 0; k < sheetContents.size(); k++) {
				sheetContents.get(k).removeIf(item -> item.isEmpty());
			}
			result = cleanupSheet(sheetContents);
		} else {
			Reporter.log("Failed to open Excel file", true);
		}
		return result;
	}

	private boolean openFile(final String fileName) {
		boolean result;
		try {
			workbook = WorkbookFactory.create(new File(fileName));
			result = true;
		} catch (IOException e) {
			Reporter.log(e.getMessage(), false);
			result = false;
		} catch (InvalidFormatException e) {
			Reporter.log(e.getMessage(), false);
			result = false;
		}
		return result;
	}
}
