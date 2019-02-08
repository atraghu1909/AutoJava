package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class OOB_0515_02 extends testLibs.BaseTest_OOB {

	@Test(priority = 1, enabled = true)
	public void step1() {
		boolean result = false;

		if (loginResult) {
			switchToBranch("B2B Branch 817");
			stepDescription = "Validate system allow to create new type";
			stepExpected = "System allow to create new type successfully";

			commandLine("COS TXN.LIST", commandLineAvailable);

			switchToPage(LASTPAGE, false);

			compositeScreen.switchToFrame("id", "Enquiry");

			enquiryElements.inputr1().click();
			versionScreen.windowAction(SWITCH_TO_DEFAULT);
			compositeScreen.switchToFrame("id", "Blank");
			compositeScreen.inputField().sendKeys("9999");
			toolElements.toolsButton("Edit a contract").click();

			try {
				inputTable.listrows("");
				result = true;

				if (result) {
					inputTable.returnButton().click();
				}

			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "System does not allow to create new type";
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
