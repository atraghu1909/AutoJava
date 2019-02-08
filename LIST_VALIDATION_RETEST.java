package testcases;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import testLibs.SheetReader;
import testLibs.SheetWriter;

public class LIST_VALIDATION_RETEST extends testLibs.BaseTest {

	final private List<List<Object>> ldReportContents = new ArrayList<List<Object>>();
	final private List<List<Object>> lsReportContents = new ArrayList<List<Object>>();
	private List<List<String>> idColumnOE;
	private List<String> appList;
	private List<String> list;
	private List<String> excelList;
	private List<Object> reportLine;
	private SheetWriter listDetailsReport;
	private SheetWriter listSummaryReport;
	private SoftAssert softVerify;
	private String tabNameRow = "";
	private String worksheetRow = "";
	private int expActualCount;
	private int expMissingEntriesCount;
	private int expExtraEntriesCount;
	private int expectedTotal;
	private int actualTotal;
	private int missingEntries;
	private int extraEntries;
	private int rowCount;
	private boolean testResult = true;

	@BeforeMethod(alwaysRun = true)
	public void beforeTest() {
		softVerify = new SoftAssert();
	}

	@Test(priority = 0, enabled = true)
	@Parameters({ "expectedTabName", "expectedWorksheet" })
	public void setUp(final String expectedTabName, final String expectedWorksheet)
			throws InterruptedException, IOException {
		List<List<String>> idColumn;
		List<String> excelColumnFile;
		List<String> excelColumnTab;
		List<String> excelColumnActual;
		List<String> excelColumnMissing;
		List<String> excelColumnExtra;
		List<String> excelColumnResult;
		SheetReader reader;
		String tabName = "";

		reader = new SheetReader();

		tabName = getHeaderName(expectedWorksheet, expectedTabName);
		idColumn = SheetReader.transpose(reader.getExcelContentsRows(getWorksheetName(expectedWorksheet), tabName));
		idColumn.get(0).removeIf(value -> value.isEmpty() || " ".equals(value));

		excelColumnFile = new ArrayList<String>(idColumn.get(0));
		excelColumnTab = new ArrayList<String>(idColumn.get(1));
		excelColumnActual = new ArrayList<String>(idColumn.get(3));
		excelColumnMissing = new ArrayList<String>(idColumn.get(4));
		excelColumnExtra = new ArrayList<String>(idColumn.get(5));
		excelColumnResult = new ArrayList<String>(idColumn.get(6));

		final boolean loginAttempt = loginPg.login("CHANGE.ME", "CHANGE.ME");
		if (idColumn.isEmpty()) {
			throw new SkipException("Could not open excel file");
		}

		else if (loginAttempt) {

			for (final String sFile : excelColumnFile) {

				if (excelColumnResult.get(rowCount).equalsIgnoreCase("FAIL")) {

					expectedTotal = Integer.parseInt(idColumn.get(2).get(rowCount));
					worksheetRow = excelColumnFile.get(rowCount);
					tabNameRow = excelColumnTab.get(rowCount);
					expActualCount = Integer.valueOf(excelColumnActual.get(rowCount));
					expMissingEntriesCount = Integer.valueOf(excelColumnMissing.get(rowCount));
					expExtraEntriesCount = Integer.valueOf(excelColumnExtra.get(rowCount));

					getOriginalExcelData(tabNameRow, worksheetRow);

					step1(tabNameRow);
					step2();
					step3(tabNameRow, worksheetRow);
					tearDown(tabNameRow, worksheetRow, expActualCount, expMissingEntriesCount, expExtraEntriesCount);
					switchToPage(environmentURL);

				}

				rowCount++;
			}

		} else {
			softVerify.fail("Unable to log into System");
		}
		softVerify.assertAll();

	}

	public void getOriginalExcelData(final String tabNameRow, final String worksheetRow) throws IOException {
		SheetReader readerOE;
		String expectedTabNameOE = "";

		readerOE = new SheetReader();

		expectedTabNameOE = getHeaderName(worksheetRow, tabNameRow);
		idColumnOE = SheetReader
				.transpose(readerOE.getExcelContentsRows(getWorksheetName(worksheetRow), expectedTabNameOE));
		idColumnOE.get(0).remove(0); // Should include for Actual Excel Reading
		idColumnOE.get(0).removeIf(value -> value.isEmpty() || " ".equals(value));

	}

	public void step1(final String tabNameRow) throws InterruptedException {
		String pageTitle;

		driver.switchTo().frame(driver.findElement(By.xpath("//frame[contains(@id, 'banner')]")));
		homePg.commandLineField().clear();
		homePg.commandLineField().sendKeys(tabNameRow + " L");
		homePg.commandLineField().sendKeys(Keys.ENTER);

		pageTitle = tabNameRow + " - " + "Default List";
		switchToPage(pageTitle);
		list = listTable.getList();

		excelList = new ArrayList<String>(idColumnOE.get(0));
		appList = new ArrayList<String>(list);

	}

	public void step2() {

		if (idColumnOE == null) {
			throw new SkipException("Could not open excel file");
		} else {

			if (appList.size() == excelList.size()) {
				Reporter.log("Size of excel list matches application list", debugMode);

			} else {
				Reporter.log("Size of excel list does not match application list", debugMode);
				testResult = false;
			}
			expectedTotal = excelList.size();
			actualTotal = appList.size();
		}
	}

	public void step3(final String tabNameRow, final String worksheetRow) {
		missingEntries = 0;
		extraEntries = 0;
		if (idColumnOE == null) {
			throw new SkipException("Could not open excel file");
		} else {
			if (excelList.isEmpty()) {
				if (list.isEmpty()) {
					Reporter.log("Both the excel list and the app list are empty", debugMode);
				} else {
					Reporter.log("The excel list is empty, but the app list has some results", debugMode);
					testResult = false;
					for (final String s : appList) {
						Reporter.log("Item not in excel: " + s, debugMode);
						reportLine = new ArrayList<Object>();
						reportLine.add(worksheetRow);
						reportLine.add(tabNameRow);
						reportLine.add(s);
						reportLine.add("Extra Entry");
						ldReportContents.add(reportLine);
						extraEntries++;
					}
				}
			} else {
				if (list.isEmpty()) {
					Reporter.log("The app list is empty, but the excel list has some results", debugMode);
					testResult = false;
					for (final String s : excelList) {
						Reporter.log("Item not in app: " + s, debugMode);
						reportLine = new ArrayList<Object>();
						reportLine.add(worksheetRow);
						reportLine.add(tabNameRow);
						reportLine.add(s);
						reportLine.add("Missing Entry");
						ldReportContents.add(reportLine);
						missingEntries++;
					}
				}

				else {
					// Verify what is in excel but not in the app
					if (list.containsAll(idColumnOE.get(0))) {
						Reporter.log("All items in excel are present in app", debugMode);
					} else {
						Reporter.log("Some items in excel not present in app", debugMode);
						testResult = false;
						for (final String s : list) {
							excelList.remove(s);
						}
						for (final String s : excelList) {
							Reporter.log("Item not in app: " + s, debugMode);
							reportLine = new ArrayList<Object>();
							reportLine.add(worksheetRow);
							reportLine.add(tabNameRow);
							reportLine.add(s);
							reportLine.add("Missing Entry");
							ldReportContents.add(reportLine);
							missingEntries++;
						}
					}

					// Verify what is in the app but not in excel
					if (idColumnOE.get(0).containsAll(list)) {
						Reporter.log("All items in app are present in excel", debugMode);
					} else {
						Reporter.log("Some items in app are not present in excel", debugMode);
						testResult = false;
						for (final String s : idColumnOE.get(0)) {
							appList.remove(s);
						}
						for (final String s : appList) {
							Reporter.log("Item not in excel: " + s, debugMode);
							reportLine = new ArrayList<Object>();
							reportLine.add(worksheetRow);
							reportLine.add(tabNameRow);
							reportLine.add(s);
							reportLine.add("Extra Entry");
							ldReportContents.add(reportLine);
							extraEntries++;
						}
					}
				}
			}
		}

	}

	public void tearDown(final String tabNameRow, final String worksheetRow, final int expActualCount,
			final int expMissingCount, final int expExtraCount) {

		if (idColumnOE == null) {
			throw new SkipException("Could not open excel file");
		} else {

			String comment = "Same";
			listDetailsReport = new SheetWriter("ListRetestDetails");
			listSummaryReport = new SheetWriter("ListRetestSummary");

			listDetailsReport.writeLine(ldReportContents);

			reportLine = new ArrayList<Object>();

			if (expActualCount != actualTotal || expExtraCount != extraEntries) {
				comment = "Different";
			}

			reportLine.add(worksheetRow);
			reportLine.add(tabNameRow);
			reportLine.add(expectedTotal);
			reportLine.add(actualTotal);
			reportLine.add(missingEntries);
			reportLine.add(extraEntries);
			reportLine.add(testResult ? "PASS" : "FAIL");
			reportLine.add(comment); // Addition column for Retest
			lsReportContents.add(reportLine);
			listSummaryReport.writeLine(lsReportContents);

			expectedTotal = 0;
			actualTotal = 0;
			ldReportContents.clear();
			lsReportContents.clear();

		}
	}
}
