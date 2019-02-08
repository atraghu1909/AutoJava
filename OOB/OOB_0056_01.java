package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class OOB_0056_01 extends testLibs.BaseTest_OOB {

	@Test(priority = 1, enabled = true)
	@Parameters({ "customer" })
	public void step1(final String customer) {
		boolean statementFound;
		String actualCIF;

		if (loginResult) {

			switchToBranch("B2B Branch 817");

			stepDescription = "Verify that system finds an arrangement and generates a statement ";
			stepExpected = "System generates a statement after finding an arrangement ";

			actualCIF = findCIF(customer, "", "");
			if (!actualCIF.equals(customer)) {
				Reporter.log("CIF received as parameter (" + customer + ") was not found. Using new CIF instead: "
						+ actualCIF, debugMode);

			}

			try {
				findArrangement(AUTHORISED, actualCIF, CIF, "", PERSONAL_ACCOUNTS, "Personal Chequing", CAD);
				switchToPage(LASTPAGE, false);

				compositeScreen.additionalDetailsStatement().click();

				statementFound = true;
			} catch (NoSuchElementException e) {
				Reporter.log(e.getMessage(), false);
				statementFound = false;

			}

			if (statementFound) {
				Reporter.log("Statement generated for the arrangement of customer:" + actualCIF, debugMode);
			} else {
				stepActual = "Statement not generated for the arrangement of customer:" + actualCIF;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
	}
}
