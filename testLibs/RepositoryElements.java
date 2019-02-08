package testLibs;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

public class RepositoryElements extends BaseTest {

	final private Properties propertyFile = new Properties();
	private By locator;

	public RepositoryElements(final String fileName) throws IOException {
		final String repositoryFile = fileName;
		final FileInputStream stream = new FileInputStream(repositoryFile);
		propertyFile.load(stream);
	}

	public By byLocator(final String locatorName) {
		final String locatorProperty = propertyFile.getProperty(locatorName);
		final String locatorType = locatorProperty.split(":")[0];
		final String locatorValue = locatorProperty.split(":", 2)[1];
		By result;

		switch (locatorType.toLowerCase(Locale.ENGLISH)) {
		case "id":
			locator = By.id(locatorValue);
			break;
		case "name":
			locator = By.name(locatorValue);
			break;
		case "cssselector":
			locator = By.cssSelector(locatorValue);
			break;
		case "linktext":
			locator = By.linkText(locatorValue);
			break;
		case "partiallinktext":
			locator = By.partialLinkText(locatorValue);
			break;
		case "tagName":
			locator = By.tagName(locatorValue);
			break;
		case "xpath":
			locator = By.xpath(locatorValue);
			break;
		default:
			break;
		}

		try {
			new WebDriverWait(driver, elementWaitTimeout).pollingEvery(elementPolling, elementTimeUnit)
					.ignoring(NoSuchElementException.class).ignoring(ElementNotVisibleException.class)
					.ignoring(WebDriverException.class).ignoring(InvalidElementStateException.class)
					.ignoring(StaleElementReferenceException.class).ignoring(NullPointerException.class)
					.until(new ExpectedCondition<WebElement>() {
						@Override
						public WebElement apply(final WebDriver input) {
							return driver.findElement(locator).isDisplayed() ? driver.findElement(locator) : null;
						}
					});
			result = locator;
		} catch (TimeoutException e) {
			Reporter.log("Element " + locator + " not found", false);
			result = locator;
		}
		return result;
	}
}
