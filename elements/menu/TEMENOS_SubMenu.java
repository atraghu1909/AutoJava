package elements.menu;

import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import testLibs.BaseTest;
import testLibs.RepositoryElements;

public class TEMENOS_SubMenu extends BaseTest {

	public static WebElement element;
	private final String SubMenuFile = subMenuProperties;
	private final RepositoryElements repoEleSubMenu;

	public TEMENOS_SubMenu(final WebDriver driver) throws IOException {
		repoEleSubMenu = new RepositoryElements(SubMenuFile);
	}

	public WebElement findAccount() {
		element = driver.findElement(repoEleSubMenu.byLocator("findAccount"));
		return element;
	}

	public WebElement productCatalog() {
		element = driver.findElement(repoEleSubMenu.byLocator("productCatalog"));
		return element;
	}

	public WebElement toolsSubMenu(final String subMenuName) {
		WebElement pageData = driver.findElement(By.xpath("//div[@id='pane_']"));
		element = pageData.findElement(By.xpath("//ul[@style='display: block;']//a[text()='" + subMenuName + " ']"));
		return element;
	}
}
