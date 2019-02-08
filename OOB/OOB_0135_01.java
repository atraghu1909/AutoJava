package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class OOB_0135_01 extends testLibs.BaseTest_OOB {

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product", "customer" })
	public void step1(final String productGroup, final String product, final String customer) {

		stepDescription = "Verify system allow to create Arrangement" + productGroup + " " + product;
		stepExpected = productGroup + " " + product + " Arrangement is Successfully created";

		if (loginResult) {
			switchToBranch("B2B Branch 817");

			try {
				final String arrangement = findArrangement(AUTHORISED, customer, CIF, "", productGroup, product, CAD);
				stepActual = product + ":" + arrangement + "is successfully created";
			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "System Couldnot create Arrangement";
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