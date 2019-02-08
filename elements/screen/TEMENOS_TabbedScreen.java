package elements.screen;

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import testLibs.BaseTest;
import testLibs.RepositoryElements;

public class TEMENOS_TabbedScreen extends BaseTest {

	public static WebElement element;
	public static List<WebElement> elementList;
	private final String TabbedScreenFile = tabbedScreenProperties;
	private final RepositoryElements repoEleTabScreen;

	public TEMENOS_TabbedScreen(final WebDriver driver) throws IOException {
		repoEleTabScreen = new RepositoryElements(TabbedScreenFile);
	}

	public WebElement findTab(final String tabTitle, final String section) {
		if ("".equals(section)) {
			element = driver.findElement(By.xpath("//span[text()='" + tabTitle + "']/ancestor::a"));
		} else {
			element = driver.findElement(By.xpath("//legend[text()='" + section
					+ "']/following-sibling::div//span[text()='" + tabTitle + "']/ancestor::a"));
		}
		return element;
	}

}
