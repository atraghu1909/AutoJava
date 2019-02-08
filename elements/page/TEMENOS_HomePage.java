package elements.page;

import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import testLibs.BaseTest;
import testLibs.RepositoryElements;

public class TEMENOS_HomePage extends BaseTest {
	public static WebElement element;
	private final String homePageFile = homePageProperties;
	private final RepositoryElements repoEleHomePg;

	public TEMENOS_HomePage(final WebDriver driver) throws IOException {
		repoEleHomePg = new RepositoryElements(homePageFile);
	}

	public WebElement commandLineField() {
		element = driver.findElement(repoEleHomePg.byLocator("commandLineField"));
		return element;
	}

	public WebElement bannerFrame() {
		element = driver.findElement(repoEleHomePg.byLocator("bannerFrame"));
		return element;
	}

	public WebElement homePageLinks(final String linkName) {
		element = driver.findElement(By.xpath("//a[@title='" + linkName + "']"));
		return element;
	}

	public WebElement bannerText() {
		element = driver.findElement(repoEleHomePg.byLocator("bannerText"));
		return element;
	}
}
