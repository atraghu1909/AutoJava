package testcases;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import testLibs.SheetReader;
import testLibs.SheetWriter;

public class DATA_VALIDATION_RETEST extends testLibs.BaseTest {

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
	public void setUp(final String expectedTabName, final String expectedWorksheet) {
		final SheetReader reader = new SheetReader();
		final String tabName = getHeaderName(expectedWorksheet, expectedTabName);

		try {
			sheetContents = SheetReader
					.transpose(reader.getExcelContentsRows(getWorksheetName(expectedWorksheet), tabName));
		} catch (IOException e) {
			softVerify.fail("No data exist in excel file");
		}
		softVerify.assertAll();
	}

	@Test(priority = 1, enabled = true)
	@Parameters({ "expectedTabName", "expectedWorksheet" })
	public void step1(final String expectedTabName, final String expectedWorksheet) throws InterruptedException {

		Reporter.log("Getting values for test", debugMode);

		int numOfFieldsFail = 0;
		int numOfFieldsTotal = 0;
		final int totalCols = sheetContents.size();
		final int totalRows = sheetContents.get(0).size();

		WebElement pageData = null;
		String[] elementSuffixes = new String[totalCols];
		String entryId = "";
		String fieldName = "";
		String expectedValue = "";
		String actualExcelValue = "";
		String actualValue = "";
		String nextEntryId = "";
		String customizedTabName = "";
		String customizedWorksheet = "";
		String detailComment = "";
		String summaryComment = "";
		final int colD = 3;
		final int colE = 4;
		final int colf = 5;
		int actNumOfFieldsFail = 0;

		if (loginPg.login("CHANGE.ME", "CHANGE.ME")) {
			parentHandle = driver.getWindowHandle();

			for (int rowIndex = 1; rowIndex < totalRows; rowIndex++) {

				if (!sheetContents.get(6).get(rowIndex).trim().contentEquals("PASS")) {
					actNumOfFieldsFail++;
					customizedWorksheet = sheetContents.get(0).get(rowIndex).trim();
					customizedTabName = sheetContents.get(1).get(rowIndex).trim();
					entryId = sheetContents.get(2).get(rowIndex).trim();

					if (" ".equals(entryId)) {
						entryId = "";
					}

					if (!"".equals(entryId)) {
						pageData = getPageData(parentHandle, customizedTabName, entryId);

						if (pageData == null) {
							Reporter.log("App Elements could not be fetched for entry " + entryId, debugMode);
						} else {

							try {

								fieldName = sheetContents.get(colD).get(rowIndex).trim();
								expectedValue = sheetContents.get(colE).get(rowIndex);
								actualExcelValue = sheetContents.get(colf).get(rowIndex);

								if (" ".equals(expectedValue)) {
									expectedValue = "";
								}
								switchToPage(customizedTabName);

								elementSuffixes[colD] = findElementSuffix(fieldName, elementSuffixes[colD]);
								actualValue = validateData(pageData, entryId, fieldName, elementSuffixes[colD],
										expectedValue);

								if (actualValue.equalsIgnoreCase(expectedValue)) {

									detailComment = "Different";
									if (actualValue.equalsIgnoreCase(actualExcelValue)) {
										detailComment = "Same";
									}

									reportLine = new ArrayList<Object>();
									reportLine.add(customizedWorksheet);
									reportLine.add(customizedTabName);
									reportLine.add(entryId);
									reportLine.add(fieldName + elementSuffixes[colD]);
									reportLine.add(expectedValue);
									reportLine.add(actualValue);
									reportLine.add("PASS");
									reportLine.add(detailComment); // Addition
																	// column
																	// for
																	// Retest
									ddReportContents.add(reportLine);
								} else {

									detailComment = "Different";
									if (actualValue.equalsIgnoreCase(actualExcelValue)) {
										detailComment = "Same";
									}
									numOfFieldsFail++;
									reportLine = new ArrayList<Object>();
									reportLine.add(customizedWorksheet);
									reportLine.add(customizedTabName);
									reportLine.add(entryId);
									reportLine.add(fieldName + elementSuffixes[colD]);
									reportLine.add(expectedValue);
									reportLine.add(actualValue);
									reportLine.add("FAIL");
									reportLine.add(detailComment); // Addition
																	// column
																	// for
																	// Retest
									ddReportContents.add(reportLine);
								}
								numOfFieldsTotal++;
							} catch (NoSuchElementException e) {
								Reporter.log(e.getMessage(), false);
							}
						}
					}
					inputTable.returnButton().click();
					// driver.close();
					driver.switchTo().window(parentHandle);

				}
				if (rowIndex + 1 < totalRows) {
					entryId = sheetContents.get(2).get(rowIndex).trim();
					nextEntryId = sheetContents.get(2).get(rowIndex + 1).trim();

					if (" ".equals(nextEntryId)) {
						nextEntryId = "";
					}
				}
				if (!entryId.equalsIgnoreCase(nextEntryId) || rowIndex + 1 == totalRows) {
					summaryComment = "Same";
					if (actNumOfFieldsFail != numOfFieldsFail) {
						summaryComment = "Different";
					}

					reportLine = new ArrayList<Object>();
					reportLine.add(customizedWorksheet);
					reportLine.add(customizedTabName);
					reportLine.add(entryId);
					reportLine.add(numOfFieldsTotal);
					reportLine.add(numOfFieldsFail);
					reportLine.add(summaryComment); // Addition column for
													// Retest
					dsReportContents.add(reportLine);

					numOfFieldsFail = 0;
					numOfFieldsTotal = 0;
					actNumOfFieldsFail = 0;
				}
			}

		} else {
			softVerify.fail("Unable to log into System");
		}
		softVerify.assertAll();
	}

	@Test(priority = 2, enabled = true)
	@Parameters({ "expectedTabName", "expectedWorksheet" })
	public void tearDown(final String expectedTabName, final String expectedWorksheet) {
		SheetWriter dataDetailsReport;
		SheetWriter dataSummaryReport;

		dataDetailsReport = new SheetWriter("DataRetestDetails");
		dataSummaryReport = new SheetWriter("DataRetestSummary");

		dataDetailsReport.writeLine(ddReportContents);
		dataSummaryReport.writeLine(dsReportContents);
	}
}
