package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class OOBH_146 extends testLibs.BaseTest_OOB {

	@Test(priority = 1, enabled = true)
	@Parameters({ "customer", "productGroup", "product", "activity" })
	public void step1(final String customer, final String productGroup, final String product, final String activity) {
		String arrangement = "";
		String actualCIF;
		String customerType = PERSONAL;
		stepDescription = "Verify Reverse Transaction";
		stepExpected = "Reverse Transaction is successfull";
		if (loginResult) {

			switchToBranch("B2B Branch 817");
			if (BUSINESS_ACCOUNTS.equals(productGroup) || COMMERCIAL_LOANS.equals(productGroup)) {
				customerType = BUSINESS;
			}
			actualCIF = findCIF(customer, customerType, "");
			if (!actualCIF.equals(customer)) {
				Reporter.log("CIF received as parameter (" + customer + ") was not found. Using new CIF instead: "
						+ actualCIF, debugMode);

			}

			try {
				arrangement = findArrangement(AUTHORISED, actualCIF, CIF, "", productGroup, product, CAD);
				switchToPage(LASTPAGE, false);

				versionScreen.linkText(ADDITIONAL_DETAILS, ACTIVITY).click();

				try {
					versionScreen.activityAction(activity, AUTHORISED, "Reverse");

				} catch (NoSuchElementException e) {

					arrangementActivity(CREATE_AUTHORISE, activity, arrangement, productGroup, "", "");
				}

				arrangementActivity(REVERSE_AUTHORISE, activity, arrangement, productGroup, "", "");
				stepActual = "Activity " + activity + " was successfully reversed.";
			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "Activity " + activity + " could not be Reversed";
				softVerify.fail(stepActual);
				Reporter.log(e.getMessage(), false);
			}
		} else {

			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);

		}
	}
}