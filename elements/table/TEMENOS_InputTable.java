package elements.table;

import java.io.IOException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Reporter;

import testLibs.BaseTest;
import testLibs.ListenerScreen;
import testLibs.RepositoryElements;

public class TEMENOS_InputTable extends BaseTest {
	public static WebElement element;
	public static List<WebElement> myList;
	private final String inputPageFile = inputTableProperties;
	private final RepositoryElements repoEleInputPg;
	private WebElement pageData;

	@SuppressWarnings({ "PMD.UnusedFormalParameter", "PMD.CallSuperInConstructor" })
	public TEMENOS_InputTable(final WebDriver driver) throws IOException {
		repoEleInputPg = new RepositoryElements(inputPageFile);
	}

	public WebElement listrows(final String section) {
		if ("".equals(section)) {
			element = driver.findElement(repoEleInputPg.byLocator("listrows"));
		} else {
			element = driver.findElement(
					By.xpath("//legend[text()='" + section + "']/following-sibling::div//table[@id='alltab']//tr"));
		}
		return element;
	}

	public WebElement tabrows(final String tabName, final String section) {

		if ("".equals(tabName)) {
			element = listrows(section);
		} else {
			if ("".equals(section)) {

				final String[] tabValue = driver
						.findElement(By.xpath("//*[span[text()='" + tabName + "']]/parent::*/a")).getAttribute("href")
						.split("'", 4);
				final String prefix = "//tr[td[table[tbody[tr[td[a[span[text()='";
				final String middle = "']]]]]]]]/following-sibling::*[1]//table[@id='";
				final String suffix = "']";

				element = driver.findElement(By.xpath(prefix + tabName + middle + tabValue[1] + suffix));
			} else {

				final String[] tabValue = driver
						.findElement(By.xpath("//legend[text()='" + section + "']/following-sibling::div//span[text()='"
								+ tabName + "']/ancestor::*/a[@class='active-tab']"))
						.getAttribute("href").split("'", 4);
				final String prefix = "//legend[text()='" + section
						+ "']/following-sibling::div//tr[td[table[tbody[tr[td[a[span[text()='";
				final String middle = "']]]]]]]]/following-sibling::*[1]//table[@id='";
				final String suffix = "']";

				element = driver.findElement(By.xpath(prefix + tabName + middle + tabValue[1] + suffix));
			}
		}

		return element;
	}

	public WebElement returnButton() {
		element = driver.findElement(repoEleInputPg.byLocator("returnButton"));
		return element;
	}

	public WebElement errorMessage() {
		element = driver.findElement(repoEleInputPg.byLocator("errorMessage"));
		return element;
	}

	public WebElement errorMessageImg() {
		element = driver.findElement(repoEleInputPg.byLocator("errorMessageImg"));
		return element;
	}

	public WebElement idDisplay(final String type) {
		switch (type) {
		case "Product Group":
			element = driver.findElement(repoEleInputPg.byLocator("iddisplayProductGroup"));
			break;
		case "Product Designer":
			element = driver.findElement(repoEleInputPg.byLocator("iddisplayProductDesigner"));
			break;
		case "Basic Interest":
			element = driver.findElement(repoEleInputPg.byLocator("iddisplayBasicInterest"));
			break;
		case "Periodic Interest":
			element = driver.findElement(repoEleInputPg.byLocator("iddisplayPeriodicInterest"));
			break;
		default:
			break;
		}
		return element;
	}

	public WebElement role(final String number) {
		final String prefix = "//select[contains(@id,'fieldName:OFFICER.ROLE:";
		final String suffix = "')]";

		element = driver.findElement(By.xpath(prefix + number + suffix));
		return element;
	}

	public WebElement selectionCriteriaButton(final String fieldName) {
		final String prefix = "//img[contains(@dropfield,'fieldName:";
		final String suffix = "')][@title='Selection Criteria']";

		element = driver.findElement(By.xpath(prefix + fieldName + suffix));
		return element;
	}

	public WebElement multiValueButton(final String identifierName, final String action) {

		element = driver.findElement(By.xpath("//td[*[@id='fieldName:" + identifierName
				+ "']]/preceding-sibling::*[1]//a/img[contains(@title,'" + action + "')]"));

		return element;
	}

	public WebElement effectiveDateError() {
		element = driver.findElement(repoEleInputPg.byLocator("effectiveDateError"));
		return element;
	}

	public WebElement acceptOverride() {
		element = driver.findElement(repoEleInputPg.byLocator("acceptOverride"));
		return element;
	}

	public WebElement errorOverrideImg() {
		element = driver.findElement(repoEleInputPg.byLocator("errorOverrideImg"));
		return element;
	}

	public WebElement isMandatoryElementPresent(final String MandatoryElem) {

		switch (MandatoryElem) {
		case "primaryOfficerMandatory":
			element = driver.findElement(repoEleInputPg.byLocator("primaryOfficerMandatory"));
			break;
		case "intendedUseMandatory":
			element = driver.findElement(repoEleInputPg.byLocator("intendedUseMandatory"));
			break;
		case "thirdPartyMandatory":
			element = driver.findElement(repoEleInputPg.byLocator("thirdPartyMandatory"));
			break;
		default:
			break;
		}

		return element;
	}

	public WebElement legendElement(final String type) {
		final String prefix = "//legend[text()='";
		final String suffix = "']";

		element = driver.findElement(By.xpath(prefix + type + suffix));
		return element;
	}

	public WebElement interestTypeElement(final String type, final String field) {
		final String prefix = "//legend[text()='";
		final String middle = "']/following-sibling::*[1]//*[@id='fieldCaption:";
		final String suffix = "']";

		element = driver.findElement(By.xpath(prefix + type + middle + field + suffix));
		return element;
	}

	public WebElement lendingRenewalLable() {
		element = driver.findElement(repoEleInputPg.byLocator("lendingRenewalLable"));
		return element;
	}

	public WebElement paymentType2() {
		element = driver.findElement(repoEleInputPg.byLocator("paymentType2"));
		return element;
	}

	public List<WebElement> propertyField() {

		myList = driver.findElements(repoEleInputPg.byLocator("propertyField"));
		return myList;
	}

	public WebElement property2() {
		element = driver.findElement(repoEleInputPg.byLocator("property2"));
		return element;
	}

	public WebElement dateCalendar(final String calendarType) {

		element = driver.findElement(By.xpath("//*[@id= 'fieldName:" + calendarType
				+ "'][not(contains(@class, 'enrichment'))][not(contains(@class, 'radioCheckStyle'))]"
				+ "/following-sibling::a[1]/img[@title='Calendar']"));

		return element;
	}

	public WebElement date(final String date) {
		pageData = driver.findElement(By.xpath("//div[@id = 'calendar_popup']"));
		final String prefix = "//td/a[text()='";
		final String suffix = "']";
		String index = date;
		if (Integer.parseInt(date) <= 9) {
			index = date.substring(1);
		}
		element = pageData.findElement(By.xpath(prefix + index + suffix));
		return element;
	}

	public WebElement totalAmountElement() {
		element = driver.findElement(repoEleInputPg.byLocator("totalAmountElement"));
		return element;
	}

	public List<WebElement> limitReferenceField() {

		myList = driver.findElements(repoEleInputPg.byLocator("limitReferenceField"));
		return myList;
	}

	public WebElement inputElement(final String idValue) throws NoSuchElementException {
		final String prefix = "//input[contains(@id,'";
		final String suffix = "')]";
		element = driver.findElement(By.xpath(prefix + idValue + suffix));
		return element;
	}

	public boolean verifyAcceptOverride() {
		boolean result = true;
		String altAttribute = "";
		boolean activeTabNotEmpty = true;
		boolean defaultFunctionInput = true;

		try {

			activeTabNotEmpty = inputTable.inputElement("activeTab").getAttribute("value").contains("tab");
			if (!activeTabNotEmpty) {
				defaultFunctionInput = "I"
						.equalsIgnoreCase(inputTable.inputElement("defaultButton").getAttribute("defaultFunction"));
			}

			while (activeTabNotEmpty || defaultFunctionInput) {

				altAttribute = inputTable.errorOverrideImg().getAttribute("alt");

				if (altAttribute.contains("override")) {
					inputTable.acceptOverride().click();
				} else if (altAttribute.contains("error")) {
					latestError = inputTable.errorMessage().getText();
					Reporter.log("Unable to perform action due to error: " + latestError, debugMode);
					takeScreenshot();
					result = false;
					break;
				}

				activeTabNotEmpty = inputTable.inputElement("activeTab").getAttribute("value").contains("tab");
				if (!activeTabNotEmpty) {
					defaultFunctionInput = "I"
							.equalsIgnoreCase(inputTable.inputElement("defaultButton").getAttribute("defaultFunction"));
				}
			}

		} catch (NoSuchElementException e) {
			Reporter.log(e.getMessage(), false);
			result = false;
		}

		return result;
	}

	public void openCalendarTool(final String fieldName) {
		String className = "";

		className = driver.findElement(By.xpath("//div[@id='fieldName:" + fieldName + ":div']")).getAttribute("class");
		while (className.contains("hidden")) {
			dateCalendar(fieldName).click();
			className = driver.findElement(By.xpath("//div[@id='fieldName:" + fieldName + ":div']"))
					.getAttribute("class");
		}
		pageData = driver.findElement(By.xpath("//div[@id = 'calendar_popup']"));
	}

	public void setCalendar(final String date, final String fieldName) {
		WebElement mySelectYear;
		WebElement mySelectMonth;
		String day;
		String month;
		String year;
		String relativeValue;
		String relativeUnit;
		String inputMethod = "";
		String[] testDate = null;
		Select dropdown;

		if (date.length() == 8) {
			inputMethod = "DIRECT";
		} else {
			inputMethod = "RELATIVE";
		}

		inputTable.openCalendarTool(fieldName);
		pageData = driver.findElement(By.xpath("//div[@id = 'calendar_popup']"));
		switch (inputMethod) {
		case "DIRECT":
			year = date.substring(0, 4);
			month = date.substring(4, 6);
			day = date.substring(6, 8);

			if (("20".equals(year.substring(0, 2)) || "19".equals(year.substring(0, 2)))
					&& ("0".equals(month.substring(0, 1)) || "1".equals(month.substring(0, 1)))) {
				mySelectMonth = driver.findElement(By.id("monthListfieldName:" + fieldName));
				dropdown = new Select(mySelectMonth);
				dropdown.selectByValue(month);

				inputTable.openCalendarTool(fieldName);
				mySelectYear = driver.findElement(By.id("yearListfieldName:" + fieldName));
				dropdown = new Select(mySelectYear);
				dropdown.selectByVisibleText(year);

				inputTable.openCalendarTool(fieldName);
				inputTable.date(day).click();
			} else {
				Reporter.log("Absolute Date Format could not be parsed on setCalendar: " + date, debugMode);
				Reporter.log("Correct format is YYYYMMDD, as in 20170130", debugMode);
			}

			break;

		case "RELATIVE":

			final Calendar calendar = Calendar.getInstance();

			final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

			final String effectiveDate[] = driver.findElement(By.id("today")).getAttribute("value").split("-");

			String monthValue;
			boolean relativeDateCorrect = true;
			relativeValue = date.substring(0, date.length() - 1);
			relativeUnit = date.substring(date.length() - 1, date.length());
			day = effectiveDate[0];
			month = effectiveDate[1];
			year = effectiveDate[2];

			switch (month) {
			default:
			case "JAN":
				monthValue = "01";
				break;
			case "FEB":
				monthValue = "02";
				break;
			case "MAR":
				monthValue = "03";
				break;
			case "APR":
				monthValue = "04";
				break;
			case "MAY":
				monthValue = "05";
				break;
			case "JUN":
				monthValue = "06";
				break;
			case "JUL":
				monthValue = "07";
				break;
			case "AUG":
				monthValue = "08";
				break;
			case "SEP":
				monthValue = "09";
				break;
			case "OCT":
				monthValue = "10";
				break;
			case "NOV":
				monthValue = "11";
				break;
			case "DEC":
				monthValue = "12";
				break;

			}
			final String finaldate = day + "/" + monthValue + "/" + year;
			try {
				calendar.setTime(sdf.parse(finaldate));
			} catch (ParseException e) {
				Reporter.log(e.getMessage(), false);
			}

			switch (relativeUnit.toUpperCase(Locale.ENGLISH)) {
			case "D":
				calendar.add(Calendar.DATE, Integer.valueOf(relativeValue));
				testDate = sdf.format(calendar.getTime()).split("/");
				break;
			case "M":
				calendar.add(Calendar.MONTH, Integer.valueOf(relativeValue));
				testDate = sdf.format(calendar.getTime()).split("/");
				break;
			case "Y":
				calendar.add(Calendar.YEAR, Integer.valueOf(relativeValue));
				testDate = sdf.format(calendar.getTime()).split("/");
				break;
			default:
				Reporter.log("Relative Date Format could not be parsed on setCalendar: " + date, debugMode);
				Reporter.log("Correct format is [+-][0-9]*[YMD], as in -10M", debugMode);
				relativeDateCorrect = false;
				break;
			}

			if (relativeDateCorrect) {
				mySelectYear = pageData.findElement(By.id("yearListfieldName:" + fieldName));
				dropdown = new Select(mySelectYear);
				dropdown.selectByVisibleText(testDate[2]);
				inputTable.openCalendarTool(fieldName);
				mySelectMonth = pageData.findElement(By.id("monthListfieldName:" + fieldName));
				dropdown = new Select(mySelectMonth);
				dropdown.selectByValue(testDate[1]);
				inputTable.openCalendarTool(fieldName);
				pageData = driver.findElement(By.xpath("//div[@id = 'calendar_popup']//div[@id='calendar']"));
				inputTable.date(testDate[0]).click();

			}

		default:
			break;
		}
	}

	public WebElement createArrangementHeaderLinks() {
		element = driver.findElement(repoEleInputPg.byLocator("createArrangementHeaderLinks"));
		return element;
	}

	public WebElement customerNumber() {
		element = driver.findElement(repoEleInputPg.byLocator("customerNumber"));
		return element;
	}

	public WebElement identifier(final String fieldName) {

		element = driver
				.findElement(By.xpath("//td[a[text()='" + fieldName + "']]/following-sibling::*[1]//tr//td[2]/span"));
		return element;
	}

	public List<WebElement> listOfFields(String fieldName, String label) {

		final String prefix = "//legend[text()='" + label
				+ "']/following-sibling::div//table[@id='alltab']//tr//input[contains(@id,'fieldName:";
		final String suffix = "')]";
		myList = driver.findElements(By.xpath(prefix + fieldName + suffix));
		return myList;

	}

	public List<WebElement> elementCandidates(final String fieldName, final String section) {
		final String prefix;
		final String middle;
		final String suffix;

		if ("".equals(section)) {
			prefix = "//*[@id = 'fieldName:";
			middle = "'][not(contains(@class, 'enrichment'))] | //*[contains(@id,'";
			suffix = "')][contains(@class, 'radioCheckStyle')]";
		} else {
			prefix = "//legend[text()='" + section + "']/following-sibling::div//*[@id = 'fieldName:";
			middle = "'][not(contains(@class, 'enrichment'))] | //*[contains(@id,'";
			suffix = "')][contains(@class, 'radioCheckStyle')]";
		}
		myList = driver.findElements(By.xpath(prefix + fieldName + middle + fieldName + suffix));
		return myList;
	}

	public WebElement pendingActivity() throws NoSuchElementException {
		element = driver.findElement(repoEleInputPg.byLocator("pendingActivity"));
		return element;
	}

	public List<WebElement> pendingElements() throws NoSuchElementException {

		myList = driver.findElements(repoEleInputPg.byLocator("pendingElements"));
		return myList;
	}

	public boolean commitAndOverride() {
		boolean result;

		if (toolElements.toolsButton(COMMIT_DEAL).getAttribute("src").contains("txncommit_dis")) {
			toolElements.toolsButton(VALIDATE_DEAL).click();
		}
		toolElements.toolsButton(COMMIT_DEAL).click();
		result = inputTable.verifyAcceptOverride();
		return result;
	}

	public WebElement clearFieldValue(final String identifierName, final String action) {
		element = driver.findElement(By.xpath("//div[contains(@id,'" + identifierName
				+ "')]//table[@id='tab1']//*[contains(@src,'" + action + "')]"));
		return element;
	}

	public WebElement refreshEveryField() {
		element = driver.findElement(repoEleInputPg.byLocator("refreshEveryField"));
		return element;
	}
}