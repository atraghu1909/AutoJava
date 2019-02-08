package elements.screen;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import testLibs.BaseTest;
import testLibs.RepositoryElements;
import testLibs.RepositoryPaths;

public class TEMENOS_VersionScreen extends BaseTest {

	public static WebElement element;
	public static List<WebElement> elementList;
	private final String VersionScreenFile = versionScreenProperties;
	private final RepositoryElements repoEleVersionScreen;

	public TEMENOS_VersionScreen(final WebDriver driver) throws IOException {
		repoEleVersionScreen = new RepositoryElements(VersionScreenFile);
	}

	public WebElement legacySystemID() {
		element = driver.findElement(repoEleVersionScreen.byLocator("legacySystemID"));
		return element;
	}

	public WebElement labelElement(final String name) throws NoSuchElementException {
		final String prefix = "//td[text()='";
		final String suffix = "']/following-sibling::*[1]";

		element = driver.findElement(By.xpath(prefix + name + suffix));
		return element;
	}

	public WebElement enquiry(final String datadisplayID) {
		return driver.findElement(By.xpath("//table[contains(@id,'datadisplay_" + datadisplayID + "')]"));
	}

	public WebElement enquiryWithClass(final String datadisplayID, final String className) {
		return driver.findElement(By.xpath(
				"//table[contains(@id,'datadisplay_" + datadisplayID + "')][contains(@class,'" + className + "')]"));
	}

	public List<WebElement> enquiryList(final String type) {
		List<WebElement> element;
		final String prefix = "//tr[contains(@id,'";
		final String suffix = "')]";

		element = driver.findElements(By.xpath(prefix + type + suffix));
		return element;
	}

	public WebElement getEnqListCell(final String table, final int row, final String column) {

		int rowSize;
		final String prefix = "./td[";
		final String suffix = "]";

		rowSize = enquiryList(table).size();
		if (row < rowSize) {
			element = enquiryList(table).get(row).findElement(By.xpath(prefix + column + suffix));
		} else {
			element = null;
		}
		return element;
	}

	public WebElement valuesDisplayed(final String type) {
		final String prefix = "//tr/td[1][text()='";
		final String suffix = "']";

		element = driver.findElement(By.xpath(prefix + type + suffix));
		return element;
	}

	public WebElement innerElement(final String row, final String column) {
		final String prefix = "//tr[contains(@id,'r";
		final String middle = "_Interest')]//td[";
		final String suffix = "]";

		element = driver.findElement(By.xpath(prefix + row + middle + column + suffix));
		return element;
	}

	public WebElement reverseActivityForCOB(final String activity) {

		element = driver.findElement(By.xpath("//tr[td[2][contains(text()='" + activity
				+ "')]]/following-sibling::*[2]/td[7]/a/img[@title='Reverse']"));
		return element;
	}

	public WebElement viewArrangement() {
		element = driver.findElement(repoEleVersionScreen.byLocator("viewArrangement"));
		return element;
	}

	public WebElement hyperLink(final String text) {
		element = driver.findElement(By.xpath("//a[text()='" + text + "']"));
		return element;
	}

	public WebElement linkText(final String type, final String subType) throws NoSuchElementException {

		element = driver
				.findElement(By.xpath("//td[text()='" + type + "']/following-sibling::*/a[text()='" + subType + "']"));
		return element;
	}

	public WebElement menuSpan(final String childText) {
		element = driver.findElement(By.xpath("//li//span[text()='" + childText.trim() + "']"));
		return element;
	}

	public WebElement menuSpan(final String parentText, final String childText) {
		element = driver.findElement(
				By.xpath("//li[*[text()='" + parentText.trim() + "']]//span[text()='" + childText.trim() + "']"));
		return element;
	}

	public WebElement menuSpanImageParent(final String value) {
		element = driver.findElement(By.xpath("//span//img[contains(@alt,'" + value.trim() + "')]"));
		return element;
	}

	public WebElement menuSpanImageChild(final String parentText, final String value) {
		element = driver.findElement(By.xpath(
				"//li[*[text()='" + parentText.trim() + "']]//span//img[contains(@alt,'" + value.trim() + "')]"));
		return element;
	}

	public WebElement menuLink(final String type, final String value) {

		switch (type) {
		default:
		case "text":

			element = driver.findElement(By.xpath("//li//a[contains(text(),'" + value.trim() + "')]"));

			break;

		case "command":

			element = driver.findElement(By.xpath("//li//a[contains(@href,'" + value.trim() + "')]"));

			break;
		}
		return element;
	}

	public WebElement issueCheque() {
		element = driver.findElement(repoEleVersionScreen.byLocator("issueCheque"));
		return element;
	}

	public WebElement connectionError() {
		element = driver.findElement(repoEleVersionScreen.byLocator("connectionErrorMessage"));
		return element;
	}

	public WebElement dataDisplayTable(final String action) {
		switch (action.toUpperCase(Locale.ENGLISH)) {
		case "REQUESTED":
		case "ISSUED":
			element = driver.findElement(By.xpath("//table[contains(@id,'datadisplay')]//tr[1]/td[1]"));
			break;
		case "STOPPED_BY_NUMBER":
			element = driver.findElement(By.xpath("//table[contains(@id,'datadisplay_FRAMEA')]//tr[1]/td[1]"));
			break;
		case "DATA_DISPLAY":
			element = driver.findElement(By.xpath("//*[contains(@id,'datadisplay')]//tr[1]/td[1]"));
			break;
		default:
			break;
		}
		return element;
	}

	public List<WebElement> recordList(final String action) {
		switch (action.toUpperCase(Locale.ENGLISH)) {
		case "STOPPED_BY_NUMBER":
			elementList = driver.findElements(By.xpath("//table[contains(@id,'datadisplay_FRAMEA')]//tr"));
			break;

		case "ENQUIRY":
			elementList = driver.findElements(By.xpath("//table[contains(@id,'datadisplay_Enquiries')]//tr"));
			break;

		case "CONTACT_ENQUIRY":
			elementList = driver.findElements(By.xpath("//table[contains(@id,'datadisplay_ContactsEnquiry')]//tr"));
			break;

		case "DATA_DISPLAY":
			elementList = driver.findElements(By.xpath("//*[@id='datadisplay']//tr"));
			break;

		default:
			break;

		}
		return elementList;
	}

	public WebElement checkDataDisplayColumn(final WebElement elementRecord, final String columnNum) {
		element = elementRecord.findElement(By.xpath(".//td[" + columnNum + "]"));
		return element;
	}

	public WebElement overdueLink() {
		element = driver.findElement(repoEleVersionScreen.byLocator("overdueLink"));
		return element;
	}

	public WebElement overdueStatisticTable() {
		element = driver.findElement(repoEleVersionScreen.byLocator("overdueStatisticTable"));
		return element;
	}

	public WebElement statusElement(final String text) throws NoSuchElementException {
		element = driver.findElement(By.xpath("//td[text()='" + text + "']/following-sibling::*[1]"));
		return element;
	}

	public WebElement errorMessage() {
		element = driver.findElement(repoEleVersionScreen.byLocator("errorMessage"));
		return element;
	}

	public WebElement viewArrangementDetails(final String text) {
		element = driver.findElement(By.xpath("//div[contains(@id,'pane_AA.ARR.OFFICERS')]//table//td[label//a[text()='"
				+ text + "']]" + "/following-sibling::*[2]"));
		return element;
	}

	public WebElement viewCIFDetails(final String text) {
		element = driver.findElement(
				By.xpath("//td[label[a[contains(text(),'" + text + "')]]]/following-sibling::*[2]//input"));
		return element;
	}

	public WebElement dataDisplay(final String identifier, final String value) {

		element = driver.findElement(By.xpath("//table[contains(@" + identifier + ",'" + value + "')]"));
		return element;
	}

	public List<WebElement> dataRow(final String identifier, final String value) {
		List<WebElement> elements;

		elements = driver.findElements(By.xpath("//table[contains(@" + identifier + ",'" + value + "')]//tbody/tr"));
		return elements;
	}

	public boolean windowAction(final String action) {
		String parentHandle = "";
		boolean result = true;
		switch (action) {

		case MAXIMIZE:
			driver.manage().window().maximize();
			break;

		case SWITCH_TO_PARENT:
			parentHandle = driver.getWindowHandle();
			driver.switchTo().window(parentHandle);
			break;

		case SWITCH_TO_DEFAULT:
			driver.switchTo().defaultContent();
			break;

		default:
			result = false;
			break;
		}
		return result;
	}

	public boolean alertAction(final String action) {
		Alert alert;
		boolean result = true;

		alert = driver.switchTo().alert();

		switch (action.toUpperCase(Locale.ENGLISH)) {

		case "ACCEPT":
			alert.accept();
			break;

		case "CANCEL":
			alert.dismiss();
			break;

		default:
			result = false;
			break;
		}

		return result;

	}

	public WebElement accrualCategory(final String identifier, final String tableName, final String interestCategory) {

		element = driver.findElement(By.xpath("//table[contains(@" + identifier + ",'" + tableName
				+ "')]//tr[td[contains(text(), '" + interestCategory + "')]]"));
		return element;
	}

	public List<WebElement> custInterventionSummary() {
		List<WebElement> elements;

		elements = driver.findElements(repoEleVersionScreen.byLocator("custInterventionSummary"));
		return elements;
	}

	public WebElement activityAction(final String activityName, final String status, final String action)
			throws NoSuchElementException {

		if ("".equals(status)) {
			element = driver.findElement(By.xpath("//tr//td[contains(., '" + activityName
					+ "')]/following-sibling::*//a/img[@alt='" + action + "']"));
		} else {
			element = driver
					.findElement(By.xpath("//tr/td[contains(., '" + activityName + "')]/following-sibling::*[text()= '"
							+ status + "']/following-sibling::*//a/img[@alt='" + action + "']"));
		}
		return element;
	}

	public WebElement memoAction(final String action) {
		element = driver.findElement(By.xpath(
				"//table[contains(@id,'datadisplay_MemoLog')]//tr[contains(@id, 'r1_MemoLog')]//td[1]/following-sibling::*/a/img[@alt='"
						+ action + "']"));
		return element;
	}

	public boolean activityCheck(final String activity, final String status) {
		boolean activityFound = false;
		boolean nextButtonEnabled = true;

		final String results = driver.findElement(By.xpath("//td[contains(@class,'enqheader-msg')]/span")).getText();
		final String resultsValue = results.split("\\s+")[5];
		final int numOfResults = Integer.parseInt(resultsValue);

		while (!activityFound && nextButtonEnabled) {
			if (numOfResults > 5) {
				nextButtonEnabled = !toolElements.toolsButton("Next Page").getAttribute("src").contains("_dis");
			} else {
				nextButtonEnabled = false;
			}

			final List<WebElement> allActivities = driver
					.findElements(By.xpath("//table[contains(@id,'datadisplay_Log')]//.//tr"));

			for (final WebElement tr : allActivities) {
				final String currentActivity = tr
						.findElement(By.xpath(
								".//td[" + enquiryElements.getColumnHeaderNumber("Activity Log", "Activity") + "]"))
						.getText();
				final String currentStatus = tr
						.findElement(By.xpath(
								".//td[" + enquiryElements.getColumnHeaderNumber("Activity Log", "Status") + "]"))
						.getText();
				if (currentActivity.contains(activity) && status.equals(currentStatus)) {
					activityFound = true;
					break;
				}
			}

			if (!activityFound) {
				if (nextButtonEnabled) {
					toolElements.toolsButton("Next Page").click();
					waitForLoading();

				} else {
					nextButtonEnabled = false;
				}
				activityFound = false;
			}

		}
		return activityFound;

	}

	public WebElement rowCell(final String rowId, final String columnId) throws NoSuchElementException {
		element = driver.findElement(By.xpath("//tr[contains(@id,'" + rowId + "')]//td[" + columnId + "]"));
		return element;
	}

	public List<WebElement> rowCellList(final String rowId, final String columnId) throws NoSuchElementException {
		elementList = driver.findElements(By.xpath("//tr[contains(@id,'" + rowId + "')]//td[" + columnId + "]"));
		return elementList;
	}

	public WebElement postRestrictionButton() {
		element = driver.findElement(repoEleVersionScreen.byLocator("postRestrictionButton"));
		return element;
	}

	public List<WebElement> delRestrictionList() {
		elementList = driver.findElements(repoEleVersionScreen.byLocator("delRestrictionList"));
		return elementList;
	}

	public List<WebElement> restrictionValueElements() {
		elementList = driver.findElements(repoEleVersionScreen.byLocator("restrictionValueElements"));
		return elementList;
	}

	public WebElement viewStatement(final String type) {

		element = driver.findElement(By.xpath("//img[contains(@title,'" + type + "')]"));
		return element;
	}

	public List<WebElement> loadingElements() {
		elementList = driver.findElements(repoEleVersionScreen.byLocator("loadingElement"));
		return elementList;
	}

	public boolean waitForLoading() {
		final WebDriverWait wait = new WebDriverWait(driver, elementWaitTimeout * 2);
		boolean result = true;

		// Temporarily change the implicit wait, since we're looking for a
		// negative response
		driver.manage().timeouts().implicitlyWait(elementWaitTimeout / 20, TimeUnit.SECONDS);

		try {
			wait.until(
					ExpectedConditions.invisibilityOfElementLocated(repoEleVersionScreen.byLocator("loadingElement")));
		} catch (TimeoutException e) {
			result = false;
		} catch (NoSuchElementException e) {
			result = true;
		}

		// Change the implicit wait back to normal
		driver.manage().timeouts().implicitlyWait(elementWaitTimeout / 5, TimeUnit.SECONDS);

		return result;
	}

	public boolean waitForPageLoading(final WebElement element, final String type) {
		boolean result = false;
		final WebDriverWait wait = new WebDriverWait(driver, elementWaitTimeout * 2);
		driver.manage().timeouts().implicitlyWait(elementWaitTimeout / 20, TimeUnit.SECONDS);
		if ("Visible".equals(type)) {
			wait.until(ExpectedConditions.visibilityOf(element));
		} else {
			wait.until(ExpectedConditions.elementToBeClickable(element));
		}

		if (element.isDisplayed()) {
			result = true;
		}

		return result;
	}

	public WebElement contactEnquiry() throws NoSuchElementException {
		element = driver.findElement(repoEleVersionScreen.byLocator("contactEnquiry"));
		return element;
	}

	public WebElement paymentOrderTable() throws NoSuchElementException {
		element = driver.findElement(repoEleVersionScreen.byLocator("paymentOrderTable"));
		return element;
	}

	public WebElement closeMessage() {

		element = driver.findElement(By.xpath("//td/span[contains(@class,'comment_AAARRANGEMENTACTIVITY')]"));
		return element;
	}

	public WebElement limitView() {
		element = driver.findElement(repoEleVersionScreen.byLocator("limitView"));
		return element;
	}

	public WebElement columnHeader(final String className, final String textValue) {
		element = driver
				.findElement(By.xpath("//th[@class='" + className + "'][contains(text(),'" + textValue + "')]"));
		return element;
	}

	public WebElement versionElement(final String label, final String name, final String column)
			throws NoSuchElementException {
		final String prefix;
		if (!"".equals(label)) {
			prefix = "//tr[td[text()='" + label + "']]//following-sibling::*//td[text()='";
		} else {
			prefix = "//td[text()='";
		}

		final String middle = "']/following-sibling::*[";
		final String suffix = "]";

		element = driver.findElement(By.xpath(prefix + name + middle + column + suffix));
		return element;
	}

	public WebElement additionalDetailsTable(final String row, final String column) {

		element = driver.findElement(By.xpath("//tr[contains(@id,'r" + row + "_AdditionalDetails')]/td[" + column
				+ "][contains(@class,'AADETAILSSCHEDULE')]"));
		return element;
	}

	public WebElement inputElements() {
		element = driver.findElement(By.xpath("//input"));
		return element;
	}

	public WebElement rowElements(final String type) {
		element = driver.findElement(By.xpath("//tr[@id='r1']//td/" + type));
		return element;
	}

	public WebElement arrangementOverviewCollateral(final String row) {
		element = driver.findElement(
				By.xpath("//table[contains(@id,'enquiryResponseData_Collateral')]/tbody/tr[2]//div[" + row + "]"));
		return element;
	}

	public WebElement headerElement(final String type, final String row, final String column) {
		element = driver.findElement(By.xpath("//table[contains(@id,'enquiryResponseData_" + type
				+ "')]//table[@id='enqheader']//tr[" + row + "]/td[" + column + "]"));
		return element;
	}

	public WebElement paymentElement(final String summary, final String referenceElement, final String row) {
		int column = 1;
		WebElement columnElement;
		boolean columnFound = false;
		elementList = driver.findElements(
				By.xpath("//table[@summary = '" + summary + "']//table[contains(@id,'datadisplay_Due')]//tr[1]/td"));
		for (int i = 1; i <= elementList.size() && !columnFound; i++) {
			columnElement = driver.findElement(By.xpath("//table[@summary = '" + summary
					+ "']//table[contains(@id,'datadisplay_Due')]//tr[1]/td[" + i + "]"));
			if (columnElement.getText().equals(referenceElement)) {
				column = i;
				columnFound = true;
			}
		}
		element = driver.findElement(By.xpath("//table[@summary = '" + summary
				+ "']//table[contains(@id,'datadisplay_Due')]//tr[td[" + column + "][text()='" + referenceElement
				+ "']]//following-sibling::*[" + row + "]/td[" + column + "]"));
		return element;
	}

	public WebElement columnHeaderElement(final String type, final int row) {
		element = driver.findElement(By.xpath("//table[contains(@id,'headingdisplay_" + type
				+ "')]//tr//th[contains(@id,'columnHeader')][table[@class='columnHeader']]//th[contains(@id,'columnHeaderText"
				+ row + "')]"));
		return element;
	}

	public List<WebElement> columnHeaderElements(final String type) {
		final String prefix = "//table[contains(@id,'headingdisplay_";
		final String suffix = "')]//tr//th[contains(@id,'columnHeader')][table[@class='columnHeader']]";
		elementList = driver.findElements(By.xpath(prefix + type + suffix));
		return elementList;
	}

	public WebElement viewSimActivity(final String IDValue) {
		final String prefix = "//tr[contains(@id,'";
		final String suffix = "')]/td[3]/a/img";

		element = driver.findElement(By.xpath(prefix + IDValue + suffix));
		return element;
	}

	public WebElement delinquentIcon(final String arrangement) {
		return driver.findElement(By.xpath("//td[text()='" + arrangement + "']//following-sibling::*[10]"));
	}

	public WebElement tdElement(final String attribute, final String value, final String position) {
		return driver.findElement(By.xpath("//td[contains(@" + attribute + ",'" + value + "')][" + position + "]"));
	}

	public WebElement activityChargesElement(final String acticityName) {
		return driver
				.findElement(By.xpath("//td[span[contains(.,'" + acticityName + "')]]/following-sibling::*/select"));
	}

	public WebElement financialSummaryStatusElement() {
		element = driver.findElement(By.xpath(
				"//table[contains(@id,'enquiryResponseData_FinancialSummary')]//table[@id='enqheader']//tr/td[contains(@class,'BOLD')]"));
		return element;
	}

}
