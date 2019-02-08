package testLibs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Reporter;

public class SheetWriter {

	private final String directoryPath;
	private final String fileName;
	private XSSFWorkbook workbook;
	private XSSFSheet sheet;

	public SheetWriter(final String testName) {
		final File directory = new File(String.valueOf("./testData/resultReport/"));
		final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		final Date date = new Date();

		this.directoryPath = "./testData/resultReport/";
		this.fileName = dateFormat.format(date) + "_" + testName;

		try (FileInputStream inputStream = new FileInputStream(directoryPath + fileName + ".xlsx")) {
			this.workbook = new XSSFWorkbook(inputStream);
			this.sheet = workbook.getSheetAt(0);
		} catch (FileNotFoundException e) {
			this.workbook = new XSSFWorkbook();
			this.sheet = workbook.createSheet(fileName);
		} catch (IOException e) {
			Reporter.log(e.getMessage(), false);
			this.workbook = new XSSFWorkbook();
			this.sheet = workbook.createSheet(fileName);
		}

		if (!directory.exists()) {
			directory.mkdir();
		}
	}

	public boolean writeLine(final List<List<Object>> lineItems) {
		boolean result = true;

		try (FileOutputStream outputStream = new FileOutputStream(directoryPath + fileName + ".xlsx")) {
			int rowCount = sheet.getPhysicalNumberOfRows();

			for (final List<Object> aBook : lineItems) {
				final Row row = sheet.createRow(++rowCount);
				int columnCount = 0;

				sheet.autoSizeColumn(0);
				for (final Object field : aBook) {
					final Cell cell = row.createCell(columnCount++);
					if (field instanceof String) {
						cell.setCellValue((String) field);
					} else if (field instanceof Integer) {
						cell.setCellValue((Integer) field);
					}

					sheet.autoSizeColumn(columnCount);
				}
			}
			workbook.write(outputStream);
			outputStream.close();
		} catch (IOException e) {
			Reporter.log(e.getMessage(), false);
			result = false;
		}
		return result;
	}

}
