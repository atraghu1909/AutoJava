
package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class OOB_0115_01 extends testLibs.BaseTest_OOB {

	@Test(priority = 1, enabled = true)
	@Parameters({ "tab" })
	public void step1(final String tab) {
		String returnValue = "";

		if (loginResult) {

			switchToBranch("B2B Branch 817");

			try {

				switch (tab) {

				case "Access":

					stepDescription = "Verify that System allows to update/create different cards";
					stepExpected = "System should allow to update/create different parameters for charges/type/status ";

					commandLine("CARD.ACCESS,CAMB.UPD.ACCESS.OL", commandLineAvailable);
					compositeScreen.inputField().sendKeys("TEST.CARD");
					toolElements.toolsButton("Edit a contract").click();
					switchToPage(LASTPAGE, false);
					returnValue = readData("DEF.MEMBER");

					break;

				case "Access flags":

					stepDescription = "Verify that system allows different types of card setup for deposits, withdrawals,"
							+ " bill payments, mini-statement";
					stepExpected = "System allows overdraft protection on specific product";

					commandLine("CARD.ACCESS,CAMB.UPD.ACCESS.OL", commandLineAvailable);
					compositeScreen.inputField().sendKeys("TEST.CARD");
					toolElements.toolsButton("Edit a contract").click();
					switchToPage(LASTPAGE, false);

					tabbedScreen.findTab("Access flags","").click();
					returnValue = readData("BI.FLAG:1:1") + readData("MS.FLAG:1:1") + readData("WD.FLAG:1:1")
							+ readData("DP.FLAG:1:1") + readData("TI.FLAG:1:1") + readData("TO.FLAG:1:1")
							+ readData("BP.FLAG:1:1") + readData("PU.FLAG:1:1");

					break;

				case "Limits":

					stepDescription = "Verify that system allows system allow offline and online different limits";
					stepExpected = "System allows offline/online limits";

					commandLine("CARD.ACCESS,CAMB.UPD.ACCESS.OL", commandLineAvailable);
					compositeScreen.inputField().sendKeys("TEST.CARD");
					toolElements.toolsButton("Edit a contract").click();
					switchToPage(LASTPAGE, false);
					tabbedScreen.findTab("Limits","").click();
					returnValue = readData("WD.ONL.DAY.LIMIT:1") + readData("WD.OFL.DAY.LIMIT:1");

					break;

				default:
					break;

				}

				if (returnValue.contains("Element Not Found")) {
					stepActual = "Required Field is not displayed in tab:" + tab;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
				toolElements.toolsButton("Return to application screen").click();

			} catch (NoSuchElementException e) {
				stepActual = "Unable to perform required action";
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
