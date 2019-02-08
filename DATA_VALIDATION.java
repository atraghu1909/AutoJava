package testcases;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import testLibs.SheetReader;
import testLibs.SheetWriter;

public class DATA_VALIDATION extends testLibs.BaseTest {

	final private List<List<Object>> ddReportContents = new ArrayList<List<Object>>();
	final private List<List<Object>> dsReportContents = new ArrayList<List<Object>>();
	private List<List<String>> sheetContents;
	private SoftAssert softVerify;
	private List<String> appList;
	private String parentHandle = "";
	private List<Object> reportLine;

	@BeforeMethod(alwaysRun = true)
	public void beforeTest() {
		softVerify = new SoftAssert();
	}

	@Test(priority = 0, enabled = true)
	@Parameters({ "expectedTabName", "expectedWorksheet" })
	public void setUp(final String expectedTabName, final String expectedWorksheet) throws IOException {
		final SheetReader reader = new SheetReader();
		final String tabName = getHeaderName(expectedWorksheet, expectedTabName);

		try {
			final FileInputStream ins = new FileInputStream(
					"./testData/" + expectedWorksheet + "/" + tabName.replace(".", "_") + ".txt");
			final InputStreamReader inputReader = new InputStreamReader(ins);
			final BufferedReader bufferedReader = new BufferedReader(inputReader);

			appList = new ArrayList<String>();
			String listItem = "";
			while (listItem != null) {
				listItem = bufferedReader.readLine();
				appList.add(listItem);
			}
			inputReader.close();
			appList.removeAll(Arrays.asList(" ", null));
		} catch (FileNotFoundException e) {
			softVerify.fail("Expected file not found. Expected file at following location: " + "./testData/"
					+ expectedWorksheet + "/" + tabName.replace(".", "_") + ".txt");
		} catch (IOException e) {
			softVerify.fail("IOException was thrown: " + e);
		}

		if (appList.isEmpty()) {
			throw new SkipException("No values to verify on this tab");
		} else {
			sheetContents = SheetReader
					.transpose(reader.getExcelContentsRows(getWorksheetName(expectedWorksheet), tabName));
		}

		softVerify.assertAll();
	}

	@Test(priority = 1, enabled = true)
	@Parameters({ "expectedTabName", "expectedWorksheet" })
	public void step1(final String expectedTabName, final String expectedWorksheet) throws InterruptedException {
		if (appList.isEmpty()) {
			reportLine = new ArrayList<Object>();
			reportLine.add(expectedWorksheet);
			reportLine.add(expectedTabName);
			reportLine.add("");
			reportLine.add(0);
			reportLine.add(0);
			dsReportContents.add(reportLine);
			throw new SkipException("No values to verify on this tab");
		} else if (SheetReader.transpose(sheetContents).size() == 1) {
			reportLine = new ArrayList<Object>();
			reportLine.add(expectedWorksheet);
			reportLine.add(expectedTabName);
			reportLine.add("");
			reportLine.add(0);
			reportLine.add(0);
			dsReportContents.add(reportLine);
			throw new SkipException("The app has entries on this tab, but the excel file does not");
		} else {
			Reporter.log("Getting values for test", debugMode);

			int numOfFieldsFail = 0;
			int numOfFieldsTotal = 0;
			final int totalCols = sheetContents.size();
			final int totalRows = sheetContents.get(0).size();

			String[] elementSuffixes = new String[totalCols];
			String[] prevRowEntries = new String[totalCols];
			String suffixCandidate = "";
			String entryId = "";
			String entryIdToPrint = "";
			String lastEntryId = "";
			String fieldName = "";
			String expectedValue = "";
			String actualValue = "";
			String nextEntryId = "";
			WebElement pageData = null;

			if (loginPg.login("CHANGE.ME", "CHANGE.ME")) {

				parentHandle = driver.getWindowHandle();
				for (int i = 0; i < elementSuffixes.length; i++) {
					elementSuffixes[i] = "";
				}

				for (int rowIndex = 1; rowIndex < totalRows; rowIndex++) {
					entryId = sheetContents.get(0).get(rowIndex).trim();
					if (" ".equals(entryId)) {
						entryId = "";
					}

					if (!"".equals(entryId) && appList.contains(entryId)) {
						pageData = getPageData(parentHandle, expectedTabName, entryId);
						if (pageData == null) {
							Reporter.log("App Elements could not be fetched for entry " + entryId, debugMode);
						} else {
							try {
								for (int colIndex = 1; colIndex < totalCols; colIndex++) {
									fieldName = sheetContents.get(colIndex).get(0).trim();
									expectedValue = sheetContents.get(colIndex).get(rowIndex);
									if (" ".equals(expectedValue)) {
										expectedValue = "";
									}
									switchToPage(expectedTabName);

									elementSuffixes[colIndex] = findElementSuffix(fieldName, elementSuffixes[colIndex]);
									actualValue = validateData(pageData, entryId, fieldName, elementSuffixes[colIndex],
											expectedValue);
									if (actualValue.equalsIgnoreCase(expectedValue)) {
										reportLine = new ArrayList<Object>();
										reportLine.add(expectedWorksheet);
										reportLine.add(expectedTabName);
										reportLine.add(entryId);
										reportLine.add(fieldName + elementSuffixes[colIndex]);
										reportLine.add(expectedValue);
										reportLine.add(actualValue);
										reportLine.add("PASS");
										ddReportContents.add(reportLine);
									} else {
										numOfFieldsFail++;
										reportLine = new ArrayList<Object>();
										reportLine.add(expectedWorksheet);
										reportLine.add(expectedTabName);
										reportLine.add(entryId);
										reportLine.add(fieldName + elementSuffixes[colIndex]);
										reportLine.add(expectedValue);
										reportLine.add(actualValue);
										reportLine.add("FAIL");
										ddReportContents.add(reportLine);
									}
									numOfFieldsTotal++;
								}
							} catch (NoSuchElementException e) {
								Reporter.log(e.getMessage(), false);
							}
						}
					}

					else if ("".equals(entryId) && appList.contains(lastEntryId)) {
						if (pageData == null) {
							Reporter.log("App Elements could not be fetched for multi value row " + rowIndex
									+ " of entry " + lastEntryId, debugMode);
						} else {
							try {
								for (int colIndex = 1; colIndex < totalCols; colIndex++) {
									fieldName = sheetContents.get(colIndex).get(0).trim();
									expectedValue = sheetContents.get(colIndex).get(rowIndex);
									if (" ".equals(expectedValue)) {
										expectedValue = "";
									}

									for (int i = 0; i < colIndex; i++) {
										prevRowEntries[i] = sheetContents.get(i).get(rowIndex);
									}
									suffixCandidate = findSuffixCandidate(prevRowEntries, colIndex, expectedValue,
											elementSuffixes);

									if (!suffixCandidate.equals(elementSuffixes[colIndex])) {
										suffixCandidate = findElementSuffix(fieldName, suffixCandidate);
										elementSuffixes[colIndex] = suffixCandidate;
									}

									switchToPage(expectedTabName);

									if (!"".equals(expectedValue)) {
										actualValue = validateData(pageData, entryId, fieldName,
												elementSuffixes[colIndex], expectedValue);
										if (actualValue.equalsIgnoreCase(expectedValue)) {
											reportLine = new ArrayList<Object>();
											reportLine.add(expectedWorksheet);
											reportLine.add(expectedTabName);
											reportLine.add(lastEntryId);
											reportLine.add(fieldName + elementSuffixes[colIndex]);
											reportLine.add(expectedValue);
											reportLine.add(actualValue);
											reportLine.add("PASS");
											ddReportContents.add(reportLine);
										} else {
											numOfFieldsFail++;
											reportLine = new ArrayList<Object>();
											reportLine.add(expectedWorksheet);
											reportLine.add(expectedTabName);
											reportLine.add(lastEntryId);
											reportLine.add(fieldName + elementSuffixes[colIndex]);
											reportLine.add(expectedValue);
											reportLine.add(actualValue);
											reportLine.add("FAIL");
											ddReportContents.add(reportLine);
										}
										numOfFieldsTotal++;
									}
								}
							} catch (NoSuchElementException e) {
								Reporter.log(e.getMessage(), false);
							}
						}
					} else if (!appList.contains(entryId) || "".equals(entryId) && !appList.contains(lastEntryId)) {
						entryIdToPrint = "".equals(entryId) ? lastEntryId : entryId;
						entryId = "";
						numOfFieldsFail = 0;
						numOfFieldsTotal = 0;
					}

					if (rowIndex + 1 < totalRows) {
						nextEntryId = sheetContents.get(0).get(rowIndex + 1);
						if (" ".equals(nextEntryId)) {
							nextEntryId = "";
						}
					}
					if (rowIndex + 1 == totalRows || !"".equals(nextEntryId) && appList.contains(nextEntryId)) {
						entryIdToPrint = "".equals(entryId) ? lastEntryId : entryId;
						if (numOfFieldsFail > 0) {
							softVerify.fail("Entry(" + entryIdToPrint + "): " + numOfFieldsFail + " out of "
									+ numOfFieldsTotal + " failed validation");
						}

						reportLine = new ArrayList<Object>();
						reportLine.add(expectedWorksheet);
						reportLine.add(expectedTabName);
						reportLine.add(entryIdToPrint);
						reportLine.add(numOfFieldsTotal);
						reportLine.add(numOfFieldsFail);
						dsReportContents.add(reportLine);

						entryId = "";
						numOfFieldsFail = 0;
						numOfFieldsTotal = 0;
						for (int i = 0; i < elementSuffixes.length; i++) {
							elementSuffixes[i] = "";
						}
						switchToPage(expectedTabName);
						inputTable.returnButton().click();
						driver.switchTo().window(parentHandle);
					}
					if (!"".equals(entryId)) {
						lastEntryId = entryId;
					}
				}

			} else {
				softVerify.fail("Unable to log into System");
			}
			softVerify.assertAll();
		}
	}

	@Test(priority = 2, enabled = true)
	@Parameters({ "expectedTabName", "expectedWorksheet" })
	public void tearDown(final String expectedTabName, final String expectedWorksheet) {
		SheetWriter dataDetailsReport;
		SheetWriter dataSummaryReport;

		dataDetailsReport = new SheetWriter("DataDetails");
		dataSummaryReport = new SheetWriter("DataSummary");

		dataDetailsReport.writeLine(ddReportContents);
		dataSummaryReport.writeLine(dsReportContents);
	}
}
