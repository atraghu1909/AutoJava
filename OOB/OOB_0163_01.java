package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class OOB_0163_01 extends testLibs.BaseTest_OOB {

	private WebElement pageData;

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product", "action", "tab", "fields" })
	public void step1(final String productGroup, final String product, final String action, final String tab,
			@Optional("") final String fields) {
		final String tabUnderTest = tab + "Tab of " + product;
		String productLine = null;

		stepDescription = "Check if " + tabUnderTest + " is displayed on Product Designer";
		stepExpected = tab + " is displayed for " + product;

		if (!fields.isEmpty()) {
			stepDescription += ", and it has " + fields;
			stepExpected += ", and it has " + fields;
		}
		if (loginResult) {
			switchToBranch("B2B Branch 817");
			commandLine("COS AA.PRODUCT.DESIGNER-PRODUCTS", commandLineAvailable);

			compositeScreen.switchToFrame("id", "ProductLines");

			switch (productGroup) {

			case PERSONAL_ACCOUNTS:
			case BUSINESS_ACCOUNTS:
			case SERVICING_ACCOUNTS:
				productLine = "Accounts";
				break;
			case RETAIL_DEPOSITS:
			case BROKER_NONREG_DEPOSITS:
			case BROKER_REG_DEPOSITS:
				productLine = "Deposits";
				break;
			case COMMERCIAL_LOANS:
			case PERSONAL_LOANS:
			case PERSONAL_LOC:
			case RETAIL_MORTGAGES:
				productLine = "Lending";
				break;
			default:
				break;
			}

			compositeScreen.switchToFrame("id", "ProductLines");
			compositeScreen.textAction(productLine, "Product Groups").click();
			switchToPage(LASTPAGE, false);

			compositeScreen.switchToFrame("id", "ProductGroups");
			compositeScreen.textAction(productGroup, "Products").click();
			versionScreen.windowAction(MAXIMIZE);
			versionScreen.windowAction(SWITCH_TO_DEFAULT);

			compositeScreen.switchToFrame("id", "Products");
			compositeScreen.textAction(product, action).click();
			versionScreen.windowAction(SWITCH_TO_DEFAULT);

			try {
				compositeScreen.switchToFrame("id", "Transactions");
				readTable.tabs(tab).click();
				inputTable.listrows("");
				pageData = inputTable.listrows("");
				if (pageData == null) {
					stepResult = StatusAs.FAILED;
					stepActual = tabUnderTest + " is blank";
					softVerify.fail(stepActual);
					Reporter.log(stepActual, debugMode);
				}
			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = tabUnderTest + " does not exist ";
				softVerify.fail(stepActual);
				Reporter.log(stepActual, debugMode);
				Reporter.log(e.getMessage(), false);
			}

			if (!"".equals(fields)) {
				try {
					readData(fields);
				} catch (NoSuchElementException e) {
					stepResult = StatusAs.FAILED;
					stepActual = tabUnderTest + " does not have " + fields;
					softVerify.fail(stepActual);
					Reporter.log(stepActual, debugMode);
					Reporter.log(e.getMessage(), false);
				}
			}

			readTable.returnButton().click();

		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}
}
