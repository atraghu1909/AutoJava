package elements.table;

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import testLibs.BaseTest;
import testLibs.RepositoryElements;

public class TEMENOS_ReadTable extends BaseTest {
	public static WebElement element;
	public static List<WebElement> elementList;
	private final String readTableFile = readTableProperties;
	private final RepositoryElements repoEleReadTable;

	@SuppressWarnings({ "PMD.UnusedFormalParameter", "PMD.CallSuperInConstructor" })
	public TEMENOS_ReadTable(final WebDriver driver) throws IOException {
		repoEleReadTable = new RepositoryElements(readTableFile);
	}

	public WebElement listrows() {
		element = driver.findElement(repoEleReadTable.byLocator("listrows"));
		return element;
	}

	public WebElement returnButton() {
		element = driver.findElement(repoEleReadTable.byLocator("returnButton"));
		return element;
	}

	public WebElement message() {
		element = driver.findElement(repoEleReadTable.byLocator("message"));
		return element;
	}

	public WebElement tabs(final String tabName) {
		final String prefix = "//span[contains(.,'";
		final String suffix = "')]";

		element = driver.findElement(By.xpath(prefix + tabName + suffix));
		return element;
	}

	public WebElement verifyActivity(final String values) {
		element = driver.findElement(By.xpath("//tr[td/label/a[text()='" + values + "']]//td[3]/span"));
		return element;
	}

	public WebElement settelmentAccount() {
		element = driver.findElement(repoEleReadTable.byLocator("settelmentAccount"));
		return element;
	}

	public WebElement readElement(final String name, final String number) throws NoSuchElementException {
		final String prefix = "//td[label[a[text()='";
		final String middle = "']]]/following-sibling::*[";
		final String suffix = "]";

		element = driver.findElement(By.xpath(prefix + name + middle + number + suffix));
		return element;
	}

	public WebElement elementValue(final String classValue) {
		final String prefix = "//td[contains(@class,'";
		final String suffix = "')]";

		element = driver.findElement(By.xpath(prefix + classValue + suffix));
		return element;
	}

	public WebElement retrieveValue(final String value, final String number) {
		element = driver.findElement(By.xpath("//tr[td/label/a[text()='" + value + "']]//td[" + number + "]/span"));
		return element;
	}

	public WebElement paymentScheduleFrequency(final String value) {
		element = driver.findElement(By
				.xpath("//tr/td[label/a[text()='" + value + "']]//following-sibling::*//span[contains(text(),'e0')]"));
		return element;
	}

	public WebElement scheduledPaymentAmount(final String value) {
		element = driver.findElement(
				By.xpath("//tr/td[label/a[text()='" + value + "']]//following-sibling::*//span[contains(text(),',')]"));
		return element;
	}

}
