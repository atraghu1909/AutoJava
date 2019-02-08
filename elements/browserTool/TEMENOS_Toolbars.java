package elements.browserTool;

import java.io.IOException;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import testLibs.BaseTest;
import testLibs.RepositoryElements;

public class TEMENOS_Toolbars extends BaseTest {
	public static WebElement element;
	private final String ToolbarsFile = toolbarProperties;
	private final RepositoryElements repoEleToolbars;
	Select dropdown;

	public TEMENOS_Toolbars(final WebDriver driver) throws IOException {
		repoEleToolbars = new RepositoryElements(ToolbarsFile);
	}

	public WebElement moreActionsDropDown() {
		element = driver.findElement(repoEleToolbars.byLocator("moreActionsDropDown"));
		return element;
	}

	public WebElement selectionScreen() {
		element = driver.findElement(repoEleToolbars.byLocator("selectionScreen"));
		return element;
	}

	public boolean moreActionsDropdown(final String option) {
		boolean result = true;
		try {
			dropdown = new Select(moreActionsDropDown());
			dropdown.selectByVisibleText(option);
			toolElements.toolsButton("Go").click();
		} catch (NoSuchElementException e) {
			result = false;
		}
		return result;
	}
}
