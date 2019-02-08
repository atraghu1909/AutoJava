
package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import atu.alm.wrapper.enums.StatusAs;

public class OOB_0147_01 extends testLibs.BaseTest_OOB {

	@Test(priority = 1, enabled = true)
	@Parameters({ "" })
	public void step1() {
		String returnValue = "";

		if (loginResult) {
			switchToBranch("B2B Branch 817");

			try {

				stepDescription = "Verify that system allows certain transaction codes";
				stepExpected = "System allows transaction control through transaction code and posting restriction";

				commandLine("COS ADMIN.POSTING.RESTRICT", commandLineAvailable);

				compositeScreen.switchToFrame("src", ".jsp");
				compositeScreen.textAction("Post No Debits", AMEND).click();
				switchToPage(LASTPAGE, false);

				compositeScreen.switchToFrame("src", ".html");

				returnValue = readData("ALLOW.TXN") + readData("TXN.CODE:1");
				toolElements.toolsButton(RETURN_TO_SCREEN).click();

				if (returnValue.contains("Element Not Found")) {
					stepActual = "Required field for Transaction Control is not displayed";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

			} catch (NoSuchElementException e) {
				stepActual = "Unable to find required field";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
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
