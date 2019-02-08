package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class OOBH_176 extends testLibs.BaseTest_OOB {

	private String actualCIF;
	String customerType = PERSONAL;

	@Test(priority = 1, enabled = true)
	@Parameters({ "customer", "fields", "values", "productGroup", "product" })
	public void step1(final String customer, @Optional("") final String fields, @Optional("") final String values,
			@Optional("") final String productGroup, @Optional("") final String product) {

		stepDescription = "Verify System allows creating Limit";
		stepExpected = "Limit was successfully created";
		if (loginResult) {
			switchToBranch("B2B Branch 817");
			try {

				if (BUSINESS_ACCOUNTS.equals(productGroup) || COMMERCIAL_LOANS.equals(productGroup)) {
					customerType = BUSINESS;
				}
				actualCIF = findCIF(customer, customerType, "");
				if (!actualCIF.equals(customer)) {
					Reporter.log("CIF received as parameter (" + customer + ") was not found. Using new CIF instead: "
							+ actualCIF, debugMode);
				}
				customerLimit("Create and Authorise", UNSECURED, "", product, actualCIF, "", "", fields, values);
				stepActual = "Limit was successfully created";
			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "Limit was not created";
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
