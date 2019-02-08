package testcases;

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import testLibs.SheetReader;

public class OOB_SIMPLE_CHECK extends testLibs.BaseTest {

	private List<List<String>> sheetContents;
	private SoftAssert softVerify;
	private String parentHandle = "";

	@BeforeMethod(alwaysRun = true)
	public void beforeTest() {
		softVerify = new SoftAssert();
	}

	@Test(priority = 0, enabled = true)
	@Parameters({ "expectedTabName", "expectedWorksheet" })
	public void setUp(final String expectedTabName, final String expectedWorksheet) throws IOException {
		final SheetReader reader = new SheetReader();
		sheetContents = SheetReader
				.transpose(reader.getExcelContentsRows(getWorksheetName(expectedWorksheet), expectedTabName));
	}

	@Test(priority = 1, enabled = true)
	@Parameters({ "expectedTabName", "expectedWorksheet" })
	public void step1(final String expectedTabName, final String expectedWorksheet) {

		Reporter.log("Getting values for test", debugMode);

		int numOfFieldsFail = 0;
		int numOfFieldsTotal = 0;
		final int totalRows = sheetContents.get(0).size();

		String entryId;
		WebElement errorMessage;

		if (loginPg.login("CHANGE.ME", "CHANGE.ME")) {

			parentHandle = driver.getWindowHandle();

			for (int rowIndex = 1; rowIndex < totalRows; rowIndex++) {
				entryId = sheetContents.get(9).get(rowIndex).trim();
				if (" ".equals(entryId)) {
					entryId = "";
				}

				if (!"".equals(entryId)) {

					driver.switchTo().frame(driver.findElement(By.xpath("//frame[contains(@id, 'banner')]")));
					homePg.commandLineField().clear();
					homePg.commandLineField().sendKeys(entryId);
					homePg.commandLineField().sendKeys(Keys.ENTER);

					try {
						numOfFieldsTotal++;
						for (final String winHandle : driver.getWindowHandles()) {
							driver.switchTo().window(winHandle);
						}
						errorMessage = versionScreen.errorMessage();
						Reporter.log(entryId + " FAIL: " + errorMessage.getText(), debugMode);
					} catch (NoSuchElementException e) {
						numOfFieldsFail++;
						Reporter.log(entryId + " PASS", debugMode);
					}
					for (final String winHandle : driver.getWindowHandles()) {
						driver.switchTo().window(winHandle);
						if (!driver.switchTo().window(winHandle).getTitle().contains(environmentTitle)) {
							driver.close();
						}
					}
					driver.switchTo().window(parentHandle);
				}

			}

			if (numOfFieldsFail > 0) {
				softVerify.fail(numOfFieldsFail + " out of " + numOfFieldsTotal + " failed validation");
			}

		} else {
			softVerify.fail("Unable to log into System");
		}
		softVerify.assertAll();
	}
}
