package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class OOB_0055_01 extends testLibs.BaseTest_OOB {
	private String actualArrangement;
	private boolean arrangementDisplayed;

	@Test(priority = 1, enabled = true)
	@Parameters({ "arrangement", "productGroup" })
	public void step1(final String arrangement, final String productGroup) {
		stepDescription = "Open the Arrangement Details view for " + productGroup;
		stepExpected = "Arrangement Details view is displayed";

		if (loginResult) {

			switchToBranch("B2B Branch 817");
			try {
				actualArrangement = findArrangement(AUTHORISED, arrangement, ARRANGEMENT, "", productGroup,
						B2B_BANK_CHEQUING_ACCOUNT, CAD);

				switchToPage(LASTPAGE, false);
				arrangementDisplayed = true;
			} catch (NoSuchElementException e) {
				stepActual = "Arrangement Details cannot be opened for " + productGroup;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
				Reporter.log(stepActual, debugMode);
				arrangementDisplayed = false;
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
	}

	public void step2() {
		String statementFrequency;
		stepDescription = "Validate that the Statement Frequency is displayed for " + actualArrangement;
		stepExpected = "Statement Frequency is correctly displayed";

		if (arrangementDisplayed) {
			try {
				versionScreen.linkText("Facilities", "Statement Freq").click();
				statementFrequency = readData("STMT.FQU.1:1");

				if (statementFrequency.isEmpty()) {
					stepActual = "Statement Frequency is blank on arrangement " + actualArrangement;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
					Reporter.log(stepActual, debugMode);
				} else {
					Reporter.log("Statement Frequency is correctly displayed: " + statementFrequency, true);
				}
				inputTable.returnButton().click();
			} catch (NoSuchElementException e) {
				stepActual = "Statement Frequency field is not displayed on arrangement " + actualArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
				Reporter.log(stepActual, debugMode);

			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as arrangement could not be opened";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
	}
}
