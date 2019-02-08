package elements.screen;

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import testLibs.BaseTest;
import testLibs.RepositoryElements;

public class TEMENOS_CompositeScreen extends BaseTest {
	public static WebElement element;
	public static List<WebElement> elementList;
	private final String CompositeScreenFile = compositeScreenProperties;
	private final RepositoryElements repoEleCompScreen;

	public TEMENOS_CompositeScreen(final WebDriver driver) throws IOException {
		repoEleCompScreen = new RepositoryElements(CompositeScreenFile);
	}

	public WebElement newButton(final String type) {
		final String prefix = "//tr[td[contains(.,'";
		final String suffix = "')][contains(@class,'ENQ')]]//td/a/img[contains(@title,'New')] ";

		element = driver.findElement(By.xpath(prefix + type + suffix));
		return element;
	}

	public WebElement inputField() throws NoSuchElementException {
		element = driver.findElement(repoEleCompScreen.byLocator("inputField"));
		return element;
	}

	public WebElement payingAccountNumber(final String newArrangement) {

		final String prefix = "//tr[contains(@id, 'Arrangement')]/td[contains(., '";
		final String suffix = "')]";

		element = driver.findElement(By.xpath(prefix + newArrangement + suffix));

		return element;
	}

	public WebElement expandButton(final String type) {
		final String prefix = "//td[contains(.,'";
		final String suffix = "')]/a/img[1][contains(@id,'treestop')]";

		element = driver.findElement(By.xpath(prefix + type + suffix));
		return element;
	}

	public WebElement newArrangement(final String type) {
		final String prefix = "//tr//td[text()='";
		final String suffix = "']//following-sibling::*[1]/a/img[@title = 'New Arrangement']";

		element = driver.findElement(By.xpath(prefix + type + suffix));
		return element;
	}

	public WebElement fundsTransferElement(final String type) {
		element = driver.findElement(By.xpath("//a[contains(., '" + type + "')]"));
		return element;
	}

	public WebElement actionButton(final String label) {
		final String prefix;
		final String suffix;
		if (!"".equals(label)) {
			prefix = "//td[text()='";
			suffix = "']/following-sibling::*//img[@alt='Select Drilldown']";
			element = driver.findElement(By.xpath(prefix + label + suffix));
		} else {
			element = driver.findElement(By.xpath("//img[@alt='Select Drilldown']"));
		}

		return element;
	}

	public List<WebElement> actionButtonlist() {
		elementList = driver.findElements(repoEleCompScreen.byLocator("actionButton"));
		return elementList;
	}

	public WebElement actionDropDown(final String activity, final String idValue) {
		final String prefix;
		final String suffix;
		if (!"".equals(activity)) {
			prefix = "//td[text()='" + activity + "']/following-sibling::*//select[contains(@id,'";
			suffix = "')]";
		} else {
			prefix = "//select[contains(@id,'";
			suffix = "')]";
		}
		element = driver.findElement(By.xpath(prefix + idValue + suffix));
		return element;
	}

	public WebElement additionalDetailsStatement() {
		element = driver.findElement(repoEleCompScreen.byLocator("additionalDetailsStatement"));
		return element;
	}

	public WebElement buttonLink(final String identifier, final String value) {
		final String prefix = "//a[contains(@" + identifier + ",'";
		final String suffix = "')]/img";
		element = driver.findElement(By.xpath(prefix + value + suffix));
		return element;
	}

	public WebElement limitLink(final String limitType) {
		String actualLimitType = limitType;

		switch (limitType) {
		default:
		case "Secured Child":
		case "Secured":
			actualLimitType = "Secured Limit";
			break;
		case "Unsecured":
			actualLimitType = "Unsecured Limit";
			break;
		}

		final String prefix = "//a[@class='enqDrillLink'][text()='";
		final String suffix = "']";

		element = driver.findElement(By.xpath(prefix + actualLimitType + suffix));
		return element;
	}

	public WebElement completeLimitId() {
		element = driver.findElement(repoEleCompScreen.byLocator("completeLimitId"));
		return element;
	}

	public List<WebElement> unblockButtonList(final String userName) {
		final String prefix = "//tr[td[contains(., '";
		final String suffix = "')]]/td/a/img[contains(@title, 'Reset')]";

		return driver.findElements(By.xpath(prefix + userName + suffix));
	}

	public List<WebElement> blockedRecordMessage() {
		elementList = driver.findElements(repoEleCompScreen.byLocator("blockedRecordMessage"));
		return elementList;
	}

	public WebElement pendingActivity(final String activity) {

		element = driver.findElement(By.xpath("//td[contains(@id,'Pending')]//td[text()='" + activity + "']"));
		return element;
	}

	public WebElement pendingActivity() {
		element = driver.findElement(repoEleCompScreen.byLocator("pendingActivity"));
		return element;
	}

	public WebElement feeCharged(final String feeType) {

		element = driver.findElement(By.xpath("//table[contains(@id,'datadisplay_Charges')]//td[contains(.,'" + feeType
				+ "')]/following-sibling::*[1]"));
		return element;
	}

	public WebElement textAction(final String description, final String title) throws NoSuchElementException {

		final String prefix;
		final String suffix;

		if ("Simulate".equals(title)) {
			prefix = "//td[text()='";
			suffix = "']/following-sibling::*/a[text()='" + title + "']";
		} else {
			prefix = "//td[text()='";
			suffix = "']/following-sibling::*/a[@title='" + title + "']";
		}

		element = driver.findElement(By.xpath(prefix + description + suffix));
		return element;
	}

	public boolean switchToFrame(final String identifier, final String value) {
		WebElement element;
		final String prefix = "//frame[contains(@" + identifier + ", '";
		final String suffix = "')]";
		boolean result = true;

		try {
			element = driver.findElement(By.xpath(prefix + value + suffix));
			driver.switchTo().frame(element);
		} catch (NoSuchElementException e) {
			result = false;
		}

		return result;
	}

	public List<WebElement> arrangementActivityLocked(String arrangementId) {

		final String prefix = "//td/span[contains(., '";
		final String suffix = " is already locked by')]";
		elementList = driver.findElements(By.xpath(prefix + arrangementId + suffix));
		return elementList;
	}

	public WebElement resetLockedArrangement() {
		element = driver.findElement(repoEleCompScreen.byLocator("resetLockedArrangement"));
		return element;
	}

	public WebElement simulate(final String type) {
		final String prefix = "//tr//td[text()='";
		final String suffix = "']//following-sibling::*[2]/a[text() = 'Simulate']";
		element = driver.findElement(By.xpath(prefix + type + suffix));
		return element;
	}

}
