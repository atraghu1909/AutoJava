package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class OOB_0515_01 extends testLibs.BaseTest_OOB {

	@Test(priority = 1, enabled = true)
	public void step1() {

		if (loginResult) {

			switchToBranch("B2B Branch 817");
			stepDescription = "Validate system allow to create new product";
			stepExpected = "System allow to create new product successfully";

			try {

				commandLine("COS AA.PRODUCT.DESIGNER-PRODUCT.CONDITIONS", commandLineAvailable);

				switchToPage(LASTPAGE, false);

				compositeScreen.switchToFrame("id", "Classes");
				versionScreen.activityAction("Accounting", "", "Conditions").click();
				versionScreen.windowAction(SWITCH_TO_DEFAULT);
				compositeScreen.switchToFrame("id", "Conditions");
				versionScreen.activityAction("Accounting Rule for Customer Accts", "", "Update").click();

			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "System does not allow to create new product";
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
