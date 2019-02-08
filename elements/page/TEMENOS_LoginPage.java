package elements.page;

import java.io.IOException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

import testLibs.BaseTest;
import testLibs.BaseTest_OOB;
import testLibs.DefaultVariables;
import testLibs.RepositoryElements;

public class TEMENOS_LoginPage extends BaseTest {
	public static WebElement element;
	final private String loginPageFile = loginPageProperties;
	final private RepositoryElements repoEleLoginPg;

	public TEMENOS_LoginPage(final WebDriver driver) throws IOException {
		repoEleLoginPg = new RepositoryElements(loginPageFile);
	}

	public WebElement username() {
		element = driver.findElement(repoEleLoginPg.byLocator("username"));
		return element;
	}

	public WebElement password() {
		element = driver.findElement(repoEleLoginPg.byLocator("password"));
		return element;
	}

	public WebElement submitBtn() {
		element = driver.findElement(repoEleLoginPg.byLocator("submitBtn"));
		return element;
	}

	public boolean login(final String userName, final String password) {
		boolean result = false;

		Reporter.log("Logging in as " + userName, debugMode);

		driver.get(environmentURL);
		try {
			username().sendKeys(userName);
			password().sendKeys(password);
			submitBtn().click();
			if (driver.findElements(By.xpath("//frame")).size() > 0) {
				result = true;
			}
			environmentTitle = driver.getTitle();
		} catch (NoSuchElementException e) {
			Reporter.log(e.getMessage(), false);
		}
		return result;
	}

	public String[] getCurrentUser(final String bannerText) {
		String[] result = new String[4];

		switch (bannerText) {
		case DefaultVariables.felipeDisplayName:
			result[0] = DefaultVariables.felipeUsername;
			result[1] = DefaultVariables.felipePassword;
			result[2] = DefaultVariables.felipeAuthorizer;
			result[3] = DefaultVariables.felipeAuthorizerPassword;
			break;
		case DefaultVariables.filipeDisplayName:
			result[0] = DefaultVariables.filipeUsername;
			result[1] = DefaultVariables.filipePassword;
			result[2] = DefaultVariables.filipeAuthorizer;
			result[3] = DefaultVariables.filipeAuthorizerPassword;
			break;
		case DefaultVariables.amruthaDisplayName:
			result[0] = DefaultVariables.amruthaUsername;
			result[1] = DefaultVariables.amruthaPassword;
			result[2] = DefaultVariables.amruthaAuthorizer;
			result[3] = DefaultVariables.amruthaAuthorizerPassword;
			break;
		case DefaultVariables.gorasDisplayName:
			result[0] = DefaultVariables.gorasUsername;
			result[1] = DefaultVariables.gorasPassword;
			result[2] = DefaultVariables.gorasAuthorizer;
			result[3] = DefaultVariables.gorasAuthorizerPassword;
			break;
		case DefaultVariables.maulikDisplayName:
			result[0] = DefaultVariables.maulikUsername;
			result[1] = DefaultVariables.maulikPassword;
			result[2] = DefaultVariables.maulikAuthorizer;
			result[3] = DefaultVariables.maulikAuthorizerPassword;
			break;
		case DefaultVariables.ashishDisplayName:
			result[0] = DefaultVariables.ashishUsername;
			result[1] = DefaultVariables.ashishPassword;
			result[2] = DefaultVariables.ashishAuthorizer;
			result[3] = DefaultVariables.ashishAuthorizerPassword;
			break;
		case DefaultVariables.anuragDisplayName:
			result[0] = DefaultVariables.anuragUsername;
			result[1] = DefaultVariables.anuragPassword;
			result[2] = DefaultVariables.anuragAuthorizer;
			result[3] = DefaultVariables.anuragAuthorizerPassword;
			break;

		case DefaultVariables.dataGen1DisplayName:
			result[0] = DefaultVariables.dataGen1Username;
			result[1] = DefaultVariables.dataGen1Password;
			result[2] = DefaultVariables.dataGen1Authorizer;
			result[3] = DefaultVariables.dataGen1AuthorizerPassword;
			break;

		case DefaultVariables.dataGen2DisplayName:
			result[0] = DefaultVariables.dataGen2Username;
			result[1] = DefaultVariables.dataGen2Password;
			result[2] = DefaultVariables.dataGen2Authorizer;
			result[3] = DefaultVariables.dataGen2AuthorizerPassword;
			break;

		case DefaultVariables.dataGen3DisplayName:
			result[0] = DefaultVariables.dataGen3Username;
			result[1] = DefaultVariables.dataGen3Password;
			result[2] = DefaultVariables.dataGen3Authorizer;
			result[3] = DefaultVariables.dataGen3AuthorizerPassword;
			break;

		case DefaultVariables.dataGen4DisplayName:
			result[0] = DefaultVariables.dataGen4Username;
			result[1] = DefaultVariables.dataGen4Password;
			result[2] = DefaultVariables.dataGen4Authorizer;
			result[3] = DefaultVariables.dataGen4AuthorizerPassword;
			break;

		case DefaultVariables.dataGen5DisplayName:
			result[0] = DefaultVariables.dataGen5Username;
			result[1] = DefaultVariables.dataGen5Password;
			result[2] = DefaultVariables.dataGen5Authorizer;
			result[3] = DefaultVariables.dataGen5AuthorizerPassword;
			break;

		case DefaultVariables.dataGen6DisplayName:
			result[0] = DefaultVariables.dataGen6Username;
			result[1] = DefaultVariables.dataGen6Password;
			result[2] = DefaultVariables.dataGen6Authorizer;
			result[3] = DefaultVariables.dataGen6AuthorizerPassword;
			break;

		case DefaultVariables.dataGen7DisplayName:
			result[0] = DefaultVariables.dataGen7Username;
			result[1] = DefaultVariables.dataGen7Password;
			result[2] = DefaultVariables.dataGen7Authorizer;
			result[3] = DefaultVariables.dataGen7AuthorizerPassword;
			break;

		case DefaultVariables.dataGen8DisplayName:
			result[0] = DefaultVariables.dataGen8Username;
			result[1] = DefaultVariables.dataGen8Password;
			result[2] = DefaultVariables.dataGen8Authorizer;
			result[3] = DefaultVariables.dataGen8AuthorizerPassword;
			break;

		case DefaultVariables.dataGen9DisplayName:
			result[0] = DefaultVariables.dataGen9Username;
			result[1] = DefaultVariables.dataGen9Password;
			result[2] = DefaultVariables.dataGen9Authorizer;
			result[3] = DefaultVariables.dataGen9AuthorizerPassword;
			break;

		case DefaultVariables.dataGen10DisplayName:
			result[0] = DefaultVariables.dataGen10Username;
			result[1] = DefaultVariables.dataGen10Password;
			result[2] = DefaultVariables.dataGen10Authorizer;
			result[3] = DefaultVariables.dataGen10AuthorizerPassword;
			break;

		case DefaultVariables.dataGen11DisplayName:
			result[0] = DefaultVariables.dataGen11Username;
			result[1] = DefaultVariables.dataGen11Password;
			result[2] = DefaultVariables.dataGen11Authorizer;
			result[3] = DefaultVariables.dataGen11AuthorizerPassword;
			break;

		case DefaultVariables.dataGen12DisplayName:
			result[0] = DefaultVariables.dataGen12Username;
			result[1] = DefaultVariables.dataGen12Password;
			result[2] = DefaultVariables.dataGen12Authorizer;
			result[3] = DefaultVariables.dataGen12AuthorizerPassword;
			break;

		case DefaultVariables.dataGen13DisplayName:
			result[0] = DefaultVariables.dataGen13Username;
			result[1] = DefaultVariables.dataGen13Password;
			result[2] = DefaultVariables.dataGen13Authorizer;
			result[3] = DefaultVariables.dataGen13AuthorizerPassword;
			break;

		case DefaultVariables.dataGen14DisplayName:
			result[0] = DefaultVariables.dataGen14Username;
			result[1] = DefaultVariables.dataGen14Password;
			result[2] = DefaultVariables.dataGen14Authorizer;
			result[3] = DefaultVariables.dataGen14AuthorizerPassword;
			break;

		case DefaultVariables.dataGen15DisplayName:
			result[0] = DefaultVariables.dataGen15Username;
			result[1] = DefaultVariables.dataGen15Password;
			result[2] = DefaultVariables.dataGen15Authorizer;
			result[3] = DefaultVariables.dataGen15AuthorizerPassword;
			break;

		case DefaultVariables.dataGen16DisplayName:
			result[0] = DefaultVariables.dataGen16Username;
			result[1] = DefaultVariables.dataGen16Password;
			result[2] = DefaultVariables.dataGen16Authorizer;
			result[3] = DefaultVariables.dataGen16AuthorizerPassword;
			break;

		case DefaultVariables.aartiDisplayName:
			result[0] = DefaultVariables.aartiUsername;
			result[1] = DefaultVariables.aartiPassword;
			result[2] = DefaultVariables.aartiAuthorizer;
			result[3] = DefaultVariables.aartiAuthorizerPassword;
			break;

		default:
			result[0] = BaseTest_OOB.username;
			result[1] = BaseTest_OOB.password;
			result[2] = BaseTest_OOB.authorizerUsername;
			result[3] = BaseTest_OOB.authorizerPassword;
			break;
		}
		return result;

	}

}
