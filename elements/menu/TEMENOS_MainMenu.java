package elements.menu;

import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import testLibs.BaseTest;
import testLibs.RepositoryElements;

public class TEMENOS_MainMenu extends BaseTest {
	public static WebElement element;
	private final String MainMenuFile = mainMenuProperties;
	private final RepositoryElements repoEleMainMenu;

	public TEMENOS_MainMenu(final WebDriver driver) throws IOException {
		repoEleMainMenu = new RepositoryElements(MainMenuFile);
	}

	public WebElement standerUserBankenu() {
		element = driver.findElement(repoEleMainMenu.byLocator("standerUserBankenu"));
		return element;
	}

	public WebElement mbUserMenu() {
		element = driver.findElement(repoEleMainMenu.byLocator("mbUserMenu"));
		return element;
	}

	public WebElement retailOperations() {
		element = driver.findElement(repoEleMainMenu.byLocator("retailOperations"));
		return element;
	}

	public WebElement toolsMainMenu(final String mainMenuName) {
		WebElement pageData = driver.findElement(By.xpath("//div[@id='pane_']"));
		element = pageData.findElement(By.xpath("//span[text()='" + mainMenuName + "']"));
		return element;
	}
}
