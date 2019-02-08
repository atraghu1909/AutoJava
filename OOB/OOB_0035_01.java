package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class OOB_0035_01 extends testLibs.BaseTest_OOB {

	private WebElement element;
	boolean step1result = false;

	@Test(priority = 1, enabled = true)
	public void step1() {
		if (loginResult) {

			switchToBranch("B2B Branch 817");
			stepDescription = "Navigate to T24 Product Conditions Browser";
			stepExpected = "Product Conditions Browser is opened successfully";

			commandLine("COS AA.PRODUCT.DESIGNER-PRODUCT.CONDITIONS", commandLineAvailable);
			step1result = true;
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}
	}

	@Test(priority = 2, enabled = true)
	public void step2() {
		if (step1result) {

			stepDescription = "Verify that user can create new Fixed Rate Interest Product Condition";
			stepExpected = "User can successfully access the version to create new Fixed Rate Interest";

			try {

				compositeScreen.switchToFrame("id", "Classes");
				versionScreen.activityAction("Interest Calculation", "", "Conditions").click();
				versionScreen.windowAction(SWITCH_TO_DEFAULT);
				compositeScreen.switchToFrame("id", "Conditions");
				versionScreen.activityAction("AD.3YR.MULTI.RATE.1YR", "", "Update").click();
				versionScreen.windowAction(SWITCH_TO_DEFAULT);
				compositeScreen.switchToFrame("id", "Transactions");
				tabbedScreen.findTab("Rate (Fixed/Floating)", "").click();

				try {
					inputData("RATE.TIER.TYPE", "Single", false);
					if (readData("DESCRIPTION:1").equalsIgnoreCase("AD.3YR.MULTI.RATE.1YR")) {
						Reporter.log("User can successfully access the version to create new Fixed Rate Interest: "
								+ element.getAttribute("text"), debugMode);
						toolElements.toolsButton("Return to application screen").click();
					}

				} catch (NoSuchElementException e) {
					stepResult = StatusAs.FAILED;
					stepActual = "User could not get to the version to create new Fixed Rate Interest";
					softVerify.fail(stepActual);
					Reporter.log(stepActual, debugMode);
				}
			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "User could not click on the link to create new Fixed Rate Interest";
				softVerify.fail(stepActual);
				Reporter.log(stepActual, debugMode);
			}
			softVerify.assertAll();
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}
	}

}
