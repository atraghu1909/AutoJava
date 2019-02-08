package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class OOB_0034_01 extends testLibs.BaseTest_OOB {

	private WebElement eleToFind;
	boolean step1Result = false;
	boolean step2Result = false;

	@Test(priority = 1, enabled = true)
	public void step1() {
		stepDescription = "Open Product Designer";
		stepExpected = "Product Designer opens successfully";
		if (loginResult) {
			switchToBranch("B2B Branch 817");
			commandLine("COS AA.PRODUCT.DESIGNER-PRODUCTS", commandLineAvailable);
			step1Result = true;

		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}
	}

	@Test(priority = 2, enabled = true)
	public void step2() {
		stepDescription = "Open version screen to create new Product Group";
		stepExpected = "Version opens successfully";

		if (step1Result) {
			compositeScreen.switchToFrame("id", "ProductLines");

			try {
				compositeScreen.newButton("Accounts").click();

				versionScreen.windowAction(SWITCH_TO_DEFAULT);
				compositeScreen.switchToFrame("id", "Transactions");

				compositeScreen.inputField().click();
				compositeScreen.inputField().sendKeys("test");
				toolElements.toolsButton("Edit a contract").click();

				try {
					eleToFind = inputTable.idDisplay("Product Group");
					stepActual = "User can successfully access the version to create new Product Group: "
							+ eleToFind.getAttribute("text");
					inputTable.returnButton().click();
					step2Result = true;
				}

				catch (NoSuchElementException e) {
					stepResult = StatusAs.FAILED;
					stepActual = "User could not get to the version to create new Product Group";
					softVerify.fail(stepActual);
					Reporter.log(e.getMessage(), false);
				}
			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "User could not click on the link to create new Product Group";
				softVerify.fail(stepActual);
				Reporter.log(e.getMessage(), false);
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	public void step3() {
		stepDescription = "Open version screen to create new Product";
		stepExpected = "Version opens successfully";
		if (step2Result) {
			versionScreen.windowAction(SWITCH_TO_DEFAULT);
			compositeScreen.switchToFrame("id", "ProductLines");

			try {
				compositeScreen.textAction("Accounts", "Product Groups").click();

				versionScreen.windowAction(SWITCH_TO_DEFAULT);
				compositeScreen.switchToFrame("id", "ProductGroups");

				compositeScreen.newButton(BUSINESS_ACCOUNTS).click();

				versionScreen.windowAction(SWITCH_TO_DEFAULT);
				compositeScreen.switchToFrame("id", "Transactions");

				compositeScreen.inputField().click();
				compositeScreen.inputField().sendKeys("test");
				toolElements.toolsButton("Edit a contract").click();

				try {
					eleToFind = inputTable.idDisplay("Product Designer");
					stepActual = "User can successfully access the version to create new Product: "
							+ eleToFind.getAttribute("text");
					inputTable.returnButton().click();
				} catch (NoSuchElementException e) {
					stepResult = StatusAs.FAILED;
					stepActual = "User could not get to the version to create new Product";
					softVerify.fail(stepActual);
					Reporter.log(e.getMessage(), false);
				}
			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "User could not click on the link to create new Product";
				softVerify.fail(stepActual);
				Reporter.log(e.getMessage(), false);
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
	}
}
