package testcases;

import java.io.File;
import java.io.FileWriter;
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

public class LIST_VALIDATION extends testLibs.BaseTest {

	final private List<List<Object>> ldReportContents = new ArrayList<List<Object>>();
	final private List<List<Object>> lsReportContents = new ArrayList<List<Object>>();
	private List<List<String>> idColumn;
	private List<String> list;
	private List<String> excelList;
	private List<String> appList;
	private List<Object> reportLine;
	private SheetWriter listDetailsReport;
	private SheetWriter listSummaryReport;
	private SoftAssert softVerify;
	private String tabName = "";
	private int expectedTotal;
	private int actualTotal;
	private int missingEntries;
	private int extraEntries;
	private boolean testResult = true;

	@BeforeMethod(alwaysRun = true)
	public void beforeTest() {
		softVerify = new SoftAssert();
	}

	@Test(priority = 0, enabled = true)
	@Parameters({ "expectedTabName", "expectedWorksheet" })
	public void setUp(final String expectedTabName, final String expectedWorksheet) throws IOException {
		final SheetReader reader = new SheetReader();

		tabName = getHeaderName(expectedWorksheet, expectedTabName);

		idColumn = SheetReader.transpose(reader.getExcelContentsRows(getWorksheetName(expectedWorksheet), tabName));
		idColumn.get(0).remove(0);
		idColumn.get(0).removeIf(value -> value.isEmpty() || " ".equals(value));
	}

	@Test(priority = 1, enabled = true)
	@Parameters("expectedTabName")
	public void step1(final String expectedTabName) throws InterruptedException {
		Reporter.log("Getting values for test", debugMode);
		final boolean loginAttempt = loginPg.login("CHANGE.ME", "CHANGE.ME");
		if (idColumn == null) {
			throw new SkipException("Could not open excel file");
		} else if (loginAttempt) {
			driver.switchTo().frame(driver.findElement(By.xpath("//frame[contains(@id, 'banner')]")));
			homePg.commandLineField().sendKeys(expectedTabName + " L");
			homePg.commandLineField().sendKeys(Keys.ENTER);

			switchToPage("Default List");
			list = listTable.getList();

			excelList = new ArrayList<String>(idColumn.get(0));
			appList = new ArrayList<String>(list);

		} else {
			softVerify.fail("Unable to log into System");
		}

		softVerify.assertAll();
	}

	@Test(priority = 2, enabled = true)
	public void step2() {
		if (idColumn == null) {
			throw new SkipException("Could not open excel file");
		} else {
			expectedTotal = excelList.size();
			actualTotal = appList.size();
			Reporter.log("Size of Excel List: " + excelList.size(), debugMode);
			Reporter.log("Size of Application List: " + appList.size(), debugMode);
			if (appList.size() == excelList.size()) {
				Reporter.log("Size of excel list matches application list", debugMode);
			} else {
				testResult = false;
				softVerify.fail("Size of excel list does not match application list");
			}
		}

		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "expectedTabName", "expectedWorksheet" })
	public void step3(final String expectedTabName, final String expectedWorksheet) {
		if (idColumn == null) {
			throw new SkipException("Could not open excel file");
		} else {
			if (excelList.isEmpty()) {
				if (list.isEmpty()) {
					Reporter.log("Both the excel list and the app list are empty", debugMode);
				} else {
					Reporter.log("The excel list is empty, but the app list has some results", debugMode);
					softVerify.fail("Some items in app are not present in excel");
					testResult = false;
					for (final String s : appList) {
						Reporter.log("Item not in excel: " + s, debugMode);
						reportLine = new ArrayList<Object>();
						reportLine.add(expectedWorksheet);
						reportLine.add(expectedTabName);
						reportLine.add(s);
						reportLine.add("Extra Entry");
						ldReportContents.add(reportLine);
						extraEntries++;
					}
				}
			} else {
				if (list.isEmpty()) {
					Reporter.log("The app list is empty, but the excel list has some results", debugMode);
					softVerify.fail("Some items in excel not present in app");
					testResult = false;
					for (final String s : excelList) {
						Reporter.log("Item not in app: " + s, debugMode);
						reportLine = new ArrayList<Object>();
						reportLine.add(expectedWorksheet);
						reportLine.add(expectedTabName);
						reportLine.add(s);
						reportLine.add("Missing Entry");
						ldReportContents.add(reportLine);
						missingEntries++;
					}
				} else {
					// Verify what is in excel but not in the app
					if (list.containsAll(idColumn.get(0))) {
						Reporter.log("All items in excel are present in app", debugMode);
					} else {
						Reporter.log("Some items in excel not present in app", debugMode);
						softVerify.fail("Some items in excel not present in app");
						testResult = false;
						for (final String s : list) {
							excelList.remove(s);
						}
						for (final String s : excelList) {
							Reporter.log("Item not in app: " + s, debugMode);
							reportLine = new ArrayList<Object>();
							reportLine.add(expectedWorksheet);
							reportLine.add(expectedTabName);
							reportLine.add(s);
							reportLine.add("Missing Entry");
							ldReportContents.add(reportLine);
							missingEntries++;
						}
					}

					// Verify what is in the app but not in excel
					if (idColumn.get(0).containsAll(list)) {
						Reporter.log("All items in app are present in excel", debugMode);
					} else {
						Reporter.log("Some items in app are not present in excel", debugMode);
						softVerify.fail("Some items in app are not present in excel");
						testResult = false;
						for (final String s : idColumn.get(0)) {
							appList.remove(s);
						}
						for (final String s : appList) {
							Reporter.log("Item not in excel: " + s, debugMode);
							reportLine = new ArrayList<Object>();
							reportLine.add(expectedWorksheet);
							reportLine.add(expectedTabName);
							reportLine.add(s);
							reportLine.add("Extra Entry");
							ldReportContents.add(reportLine);
							extraEntries++;
						}
					}
				}
			}
			softVerify.assertAll();
		}
	}

	@Test(priority = 4, enabled = true)
	@Parameters({ "expectedTabName", "expectedWorksheet" })
	public void tearDown(final String expectedTabName, final String expectedWorksheet) throws IOException {
		if (idColumn == null) {
			throw new SkipException("Could not open excel file");
		} else {
			final File directory = new File(String.valueOf("./testData/" + expectedWorksheet));
			if (!directory.exists()) {
				directory.mkdir();
			}

			final File file = new File("./testData/" + expectedWorksheet + "/" + tabName.replace(".", "_") + ".txt");
			if (!file.exists()) {
				file.createNewFile();
			}

			final FileWriter fileWriter = new FileWriter(file, false);
			for (final String s : list) {
				fileWriter.write(s + "\n");
			}
			fileWriter.close();

			listDetailsReport = new SheetWriter("ListDetails");
			listSummaryReport = new SheetWriter("ListSummary");

			listDetailsReport.writeLine(ldReportContents);

			reportLine = new ArrayList<Object>();
			reportLine.add(expectedWorksheet);
			reportLine.add(expectedTabName);
			reportLine.add(expectedTotal);
			reportLine.add(actualTotal);
			reportLine.add(missingEntries);
			reportLine.add(extraEntries);
			reportLine.add(testResult ? "PASS" : "FAIL");
			lsReportContents.add(reportLine);
			listSummaryReport.writeLine(lsReportContents);
		}
	}
}
