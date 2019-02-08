package elements;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Reporter;

import testLibs.BaseTest;
import testLibs.RepositoryElements;

public class TEMENOS_Enquiry extends BaseTest {
	public static WebElement element;
	public static List<WebElement> myList;
	private final String EnquiryFile = enquiryProperties;
	private final RepositoryElements repoEleEnquiry;

	public TEMENOS_Enquiry(final WebDriver driver) throws IOException {
		repoEleEnquiry = new RepositoryElements(EnquiryFile);
	}

	public WebElement findButton() {
		element = driver.findElement(repoEleEnquiry.byLocator("findButton"));
		return element;
	}

	public WebElement statusCIF() {
		element = driver.findElement(repoEleEnquiry.byLocator("statusCIF"));
		return element;
	}

	public WebElement enquiryButtons(final String buttonType) {
		final String prefix = "//img[@title='";
		final String suffix = "']";

		element = driver.findElement(By.xpath(prefix + buttonType + suffix));
		return element;
	}

	public WebElement linkToSelect(final String linkName) {
		final String prefix = "//a[contains(@href,'";
		final String suffix = "')]/b";

		element = driver.findElement(By.xpath(prefix + linkName + suffix));
		return element;
	}

	public WebElement inputr1() {
		element = driver.findElement(repoEleEnquiry.byLocator("inputr1"));
		return element;
	}

	public WebElement linkPR(final String CIF, final String restrictType) {
		final String prefix = "//td[text()='";
		final String middle = "']/following-sibling::*[2]/a[text()='";
		final String suffix = "']";

		switch (restrictType) {
		case "Customer":
			element = driver.findElement(By.xpath(prefix + CIF + middle + restrictType + suffix));
			break;
		case "Account":
			element = driver.findElement(By.xpath(prefix + CIF + middle + restrictType + suffix));
			break;

		default:
			break;
		}
		return element;
	}

	public WebElement enquiryElement(final String type, final String label) {
		final String prefix = "//tr[td/span/label[text()='";
		final String dropDownSuffix = "']]//td/select[contains(@name, 'operand')]";
		final String textFieldSuffix = "']]//td/input[@type = 'text']";

		switch (type) {
		case DROPDOWN:
			element = driver.findElement(By.xpath(prefix + label + dropDownSuffix));
			break;
		case TEXTFIELD:
			element = driver.findElement(By.xpath(prefix + label + textFieldSuffix));
			break;
		default:
			break;
		}
		return element;
	}

	public WebElement transactionIdField(final String textField) {
		final String prefix = "//td[a[contains(text(),'";
		final String suffix = "')]]/following-sibling::*[1]//table/tbody//tr//td/input[@id='transactionId']";

		element = driver.findElement(By.xpath(prefix + textField + suffix));
		return element;
	}

	public WebElement startDateOfChargePaymentType() {
		element = driver.findElement(repoEleEnquiry.byLocator("startDateOfChargePaymentType"));
		return element;
	}

	public WebElement enqHeaderMsg() {
		element = driver.findElement(repoEleEnquiry.byLocator("enqHeaderMsg"));
		return element;
	}

	public void clearEnquiryTextFields() {
		final List<WebElement> myList = driver.findElements(repoEleEnquiry.byLocator("enquiryTextFields"));
		for (int i = 0; i < myList.size(); i++) {
			myList.get(i).clear();
		}
	}

	public List<String> getOverviewAccountNumbers(final String idText) {
		final List<String> result = new ArrayList<String>();
		switch (idText) {
		case "AccountEnquiry":
			myList = driver.findElements(By.xpath("//tr[contains(@id, 'AccountEnquiry')]/td[2]"));
			break;
		case "DepositsDetails":
			myList = driver.findElements(By.xpath("//tr[contains(@id, 'DepositsDetails')]/td[1]"));
			break;
		case "LoanDetailsEnquiry":
			myList = driver.findElements(By.xpath("//tr[contains(@id, 'LoanDetailsEnquiry')]/td[1]"));
			break;
		default:
			break;
		}

		for (final WebElement item : myList) {
			result.add(item.getText());
		}
		return result;
	}

	public WebElement activityMessaging(final String label) {
		element = driver.findElement(By.xpath("//td[span[text()='" + label + "']]/following-sibling::*//select"));
		return element;
	}

	public void favouritesButton(final String action, final String title) {
		List<WebElement> favouritesList;

		switch (action) {
		case "Add":
			enquiryElements.enquiryButtons("Add Favourite").click();
			Alert accept = driver.switchTo().alert();
			accept.sendKeys(title);
			accept.accept();
			break;
		case "Delete":
			List<WebElement> trCount = driver.findElements(By.xpath("//table[@id='enqfav']//tr"));
			if (trCount.size() > 1) {
				favouritesList = driver.findElements(By.xpath("//table[@id='enqfav']//tbody//tr"));
				for (int i = 1; i <= favouritesList.size(); i++) {
					String favouriteTitle = driver
							.findElement(By.xpath("//table[@id='enqfav']//tbody//tr[" + i + "]/td[1]/a")).getText();
					if (title.equals(favouriteTitle)) {
						driver.findElement(By.xpath("//table[@id='enqfav']//tbody//tr[" + i + "]/td[2]/img")).click();
						Alert delete = driver.switchTo().alert();
						delete.accept();
						break;
					}
				}
			} else {
				Reporter.log("There were no favourites to be deleted");
			}
			break;
		case "Run":
			List<WebElement> rowCount = driver.findElements(By.xpath("//table[@id='enqfav']//tr"));
			if (rowCount.size() > 1) {
				favouritesList = driver.findElements(By.xpath("//table[@id='enqfav']//tbody//tr"));
				for (int i = 1; i <= favouritesList.size(); i++) {
					String favouriteTitle = driver
							.findElement(By.xpath("//table[@id='enqfav']//tbody//tr[" + i + "]/td[1]/a")).getText();
					if (title.equals(favouriteTitle)) {
						driver.findElement(By.xpath("//table[@id='enqfav']//tbody//tr[" + i + "]/td[1]/a")).click();
						break;
					}
				}
			} else {
				Reporter.log("There were no favourites to be run");
			}
			break;
		}
	}

	public WebElement unauthorisedFT() {
		element = driver.findElement(repoEleEnquiry.byLocator("unauthorisedFT"));
		return element;
	}

	public WebElement enquiryByMagnifierGlass(String row) {
		final String prefix = "//tr[@id='r";
		final String suffix = "']//td[1]//img";
		element = driver.findElement(By.xpath(prefix + row + suffix));
		return element;
	}

	public WebElement enquiryByLink(String row) {
		final String prefix = "//tr[@id='r";
		final String suffix = "']//td[2]//b";
		element = driver.findElement(By.xpath(prefix + row + suffix));
		return element;
	}

	public String optionsDropDown(final String activity, final String idValue) {
		final String prefix;
		final String suffix;
		prefix = "//td[text()='" + activity + "']/following-sibling::*//select[contains(@class,'";
		suffix = "')]";
		element = driver.findElement(By.xpath(prefix + idValue + suffix));
		return element.getText();
	}

	public List<WebElement> optionsRadioButtons(final String activity) {
		final String prefix = "//td[text()='";
		final String suffix = "']//following-sibling::*//input[@type = 'radio']";
		myList = driver.findElements(By.xpath(prefix + activity + suffix));
		return myList;
	}

	public boolean enquirySearch(final String fields, final String criteria, final String values) {
		final String[] listOfFields = fields.split(",");
		final String[] listOfCriteria = criteria.split(",", -1);
		final String[] listOfValues = values.split(",");
		Select dropdown;
		boolean isEnquiryResult;
		boolean result = true;

		try {
			isEnquiryResult = !"".equals(driver
					.findElement(By.xpath("//form[contains(@id,'enqsel')]//input[@id='enqid']")).getAttribute("value"));
			if (isEnquiryResult) {
				enquiryButtons(SELECTION_SCREEN).click();
			}
			clearEnquiryTextFields();

			for (int i = 0; i < listOfFields.length; i++) {
				if (!"".equals(listOfCriteria[i])) {
					dropdown = new Select(enquiryElement(DROPDOWN, listOfFields[i]));
					dropdown.selectByVisibleText(listOfCriteria[i]);
				}
				enquiryElement(TEXTFIELD, listOfFields[i]).sendKeys(listOfValues[i]);
			}

			findButton().click();
		} catch (NoSuchElementException e) {
			Reporter.log(e.getMessage(), false);
			result = false;
		}

		return result;
	}

	public boolean verifyOptionSaveCSV() {
		final Actions action = new Actions(driver);
		boolean result;

		try {
			action.moveToElement(toolElements.toolsButton("Enquiry Actions")).build().perform();
			result = "javascript:doEnquiry(\"CSV\")"
					.equalsIgnoreCase(versionScreen.hyperLink("Save as CSV").getAttribute("href"));

		} catch (NoSuchElementException e) {
			result = false;
			Reporter.log(e.getMessage(), false);
		}

		return result;
	}

	public int getColumnHeaderNumber(final String tableSummary, final String headerText) {
		return getColumnHeaderNumber(tableSummary, headerText, false);
	}

	public int getColumnHeaderNumber(final String tableSummary, final String headerText, final boolean exactMatch) {
		String xpath;
		int columnNumber;

		if (exactMatch) {
			xpath = "(//table[@summary = '" + tableSummary + "']//th[contains(@class, 'columnHeader')][text() = '"
					+ headerText + "']/../../../.. |//table[@summary = '" + tableSummary
					+ "']//td[contains(@class, 'Bold')][text() = '" + headerText + "'])";
		} else {
			xpath = "(//table[contains(@summary,'" + tableSummary
					+ "')]//th[contains(@class, 'columnHeader')][text() = '" + headerText
					+ "']/../../../.. |//table[contains(@summary,'" + tableSummary
					+ "')]//td[contains(@class, 'Bold')][text() = '" + headerText + "'])";
		}

		try {
			element = driver.findElement(By.xpath(xpath));
			myList = driver.findElements(By.xpath(xpath + "/preceding-sibling::*"));
			columnNumber = myList.isEmpty() ? 1 : myList.size() + 1;
		} catch (NoSuchElementException e) {
			columnNumber = 0;
			Reporter.log(e.getMessage(), false);
		}

		return columnNumber;

	}

	public List<WebElement> getRowsMatching(final String tableSummary, final String identifierColumnText,
			final String cellText) {
		return getRowsMatching(tableSummary, identifierColumnText, cellText, false);
	}

	public List<WebElement> getRowsMatching(final String tableSummary, final String identifierColumnText,
			final String cellText, final boolean exactMatch) {
		final int columnNumber = getColumnHeaderNumber(tableSummary, identifierColumnText, exactMatch);
		String xpath;

		if (exactMatch) {
			xpath = "//table[@summary = '" + tableSummary + "']//tr//td[" + columnNumber + "][text() = '" + cellText
					+ "']/parent::tr";
		} else {
			xpath = "//table[contains(@summary,'" + tableSummary + "')]//tr//td[" + columnNumber + "][contains(text(),'"
					+ cellText + "')]/parent::tr";
		}

		if (columnNumber > 0) {
			try {
				myList = driver.findElements(By.xpath(xpath));
			} catch (NoSuchElementException e) {
				Reporter.log(e.getMessage(), false);
			}
		}
		return myList;
	}

	public int getRowNumber(final WebElement rowElement) {
		final String elementText = rowElement.toString();
		final String xpath = elementText.substring(elementText.indexOf("xpath: ") + 7, elementText.length() - 1);

		myList = driver.findElements(By.xpath(xpath + "/preceding-sibling::*"));
		return myList.isEmpty() ? 1 : myList.size() + 1;
	}

	public int getRowNumberMatching(final String tableSummary, final String identifierColumnText, final String cellText,
			final int position) {
		int rowNumber;
		myList = getRowsMatching(tableSummary, identifierColumnText, cellText);

		if (myList.isEmpty()) {
			rowNumber = 0;
		} else if (position < 0) {
			rowNumber = getRowNumber(myList.get(0));
		} else if (position < myList.size()) {
			rowNumber = getRowNumber(myList.get(position));
		} else {
			rowNumber = 0;
		}
		return rowNumber;
	}

	public int getRowNumberMatching(final String tableSummary, final String identifierColumnText,
			final String cellText) {
		return getRowNumberMatching(tableSummary, identifierColumnText, cellText, -1);
	}

	public WebElement getElementAtCell(final String tableSummary, final int columnNumber, final int rowNumber) {
		return getElementAtCell(tableSummary, "", columnNumber, rowNumber);
	}

	public WebElement getElementAtCell(final String tableSummary, final String rowSummary, final int columnNumber,
			final int rowNumber) {
		try {
			element = driver.findElement(
					By.xpath("//table[contains(@summary,'" + tableSummary + "')]//tr[" + rowNumber + "][contains(@id, '"
							+ rowSummary + "')]//td[" + columnNumber + "][not(contains(@class,'caption'))]"));
		} catch (NoSuchElementException e) {
			Reporter.log(e.getMessage(), false);
		}

		return element;
	}

	public boolean clickNextPage() {
		final int totalElementNumber = enquiryResults("Total");
		final int pageElementNumber = enquiryResults("Current Page Last");

		final boolean nextButtonEnabled = totalElementNumber > pageElementNumber
				&& !toolElements.toolsButton("Next Page").getAttribute("src").contains("_dis");

		if (nextButtonEnabled) {
			toolElements.toolsButton("Next Page").click();
		}

		return nextButtonEnabled;
	}

	public int enquiryResults(final String type) {
		int index;
		int value;

		switch (type) {
		case "Current Page First":
			index = 1;
			break;
		case "Current Page Last":
			index = 3;
			break;
		case "Total":
		default:
			index = 5;
			break;
		}

		value = Integer.parseInt(driver.findElement(By.xpath("//td[contains(@class,'enqheader-msg')]/span")).getText()
				.split("\\s+")[index]);

		return value;
	}
}