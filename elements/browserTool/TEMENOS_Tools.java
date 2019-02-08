package elements.browserTool;

import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.NoSuchElementException;
import testLibs.RepositoryElements;
import org.openqa.selenium.support.ui.Select;
import org.testng.Reporter;
import testLibs.BaseTest_OOB;

public class TEMENOS_Tools extends BaseTest_OOB {
	public static WebElement element;

	private final String ToolsFile = toolProperties;
	private final RepositoryElements repoEleTools;

	public TEMENOS_Tools(final WebDriver driver) throws IOException {
		repoEleTools = new RepositoryElements(ToolsFile);
	}

	public WebElement authorise(final String CIF) {
		final String prefix = "//tr[td[1][contains(.,'";
		final String suffix = "')]]/td[7]//img[(@title='Authorise')]";

		element = driver.findElement(By.xpath(prefix + CIF + suffix));

		return element;
	}

	public WebElement toolsButton(final String buttonTitle) {

		final String prefix = "//img[@title='";
		final String suffix = "']";
		element = driver.findElement(By.xpath(prefix + buttonTitle + suffix));

		if (positiveDocumentation) {
			takeScreenshot();
		}

		return element;
	}

	public WebElement toolsButtonTitle(final String buttonTitle) {

		final String prefix = "//*[@title='";
		final String suffix = "']";
		element = driver.findElement(By.xpath(prefix + buttonTitle + suffix));

		return element;
	}

	public void listAndSearchFile(final String entity, final String chosenOption) {
		String searchEntity;
		switch (entity) {
		case CUSTOMER:
			searchEntity = customer(OPEN, PERSONAL, ROLEBASED_SAE, "", "");
			break;
		case LIMIT:
			searchEntity = customerLimit(OPEN, "Revolving Secured", ROLEBASED_SAE, "", "", "", "", "", "");
			break;
		case COLLATERAL_LINK:
			searchEntity = collateral(OPEN, COLLATERAL_LINK, "", "", "");
			break;
		case COLLATERAL:
			searchEntity = collateral(OPEN, COLLATERAL_DETAILS, "", "", "");
			break;
		}
		try {
			Select dropdown = new Select(compositeScreen.actionDropDown("", "moreactions"));
			dropdown.selectByVisibleText(chosenOption);
			toolElements.toolsButton("Go").click();
			Reporter.log(chosenOption + " for " + entity, debugMode);
		} catch (NoSuchElementException e) {
			Reporter.log("Unable to " + chosenOption + " for " + entity, debugMode);
		}
	}

	public WebElement launchButton(final String value) {
		final String prefix = "//img[contains(@src,'";
		final String suffix = "')]";
		element = driver.findElement(By.xpath(prefix + value + suffix));
		return element;
	}
}
