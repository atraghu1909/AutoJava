package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class OOBH_44 extends testLibs.BaseTest_OOB {

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product", "customer", "activity" })
	public void step1(final String productGroup, final String product, final String customer,
			@Optional("") final String activity) {
		if (loginResult) {
			switchToBranch("B2B Branch 817");

			try {

				final String arrangement = findArrangement(AUTHORISED, customer, CIF, "", productGroup, product, CAD);

				arrangementActivity(CREATE_AUTHORISE, activity, arrangement, productGroup, "CHANGE.AMOUNT", "100");
			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "Unable to perform required action";
				softVerify.fail(stepActual);
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
	}
}
