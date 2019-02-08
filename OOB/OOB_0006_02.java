package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class OOB_0006_02 extends testLibs.BaseTest_OOB {

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product", "customer" })
	public void step1(final String productGroup, final String product, final String customer) {

		stepDescription = "Verify overrides for change of fields";
		stepExpected = "Override message is displayed when modifying the value of a mandatory field";

		String actualCIF;
		String customerType = PERSONAL;

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

			final String step1Fields = "CUSTOMER:1,CURRENCY";
			final String step1Values = actualCIF + ",CAD";
			arrangements("Validate", productGroup, product, "", actualCIF, step1Fields, step1Values, "", "");

			switchToPage(LASTPAGE, false);

			if (inputData("CA.THIRD.PARTY", "Y", true)) {
				Reporter.log("Third Party set to: YES", debugMode);
			} else {
				stepResult = StatusAs.FAILED;
				stepActual = "ERROR : Third Party field is Missing";
				softVerify.fail(stepActual);
			}

			if (inputData("PRIMARY.OFFICER", "1", true)) {
				Reporter.log("Primary officer field set to: 1", debugMode);
			} else {
				stepResult = StatusAs.FAILED;
				stepActual = "ERROR : Primary officer field is Missing";
				softVerify.fail(stepActual);
			}

			if (inputData("CA.INTEND.USE", "06", true)) {
				Reporter.log("Intended Use field set to: 06", debugMode);
			} else {
				stepResult = StatusAs.FAILED;
				stepActual = "ERROR : Intended Use field is Missing";
				softVerify.fail(stepActual);
			}

			try {
				toolElements.toolsButton(COMMIT_DEAL).click();
				Reporter.log("Commit the deal sent", debugMode);
			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "ERROR : Commit The Deal button is Missing";
				softVerify.fail(stepActual);
				Reporter.log(e.getMessage(), false);
			}

			if (inputTable.verifyAcceptOverride()) {
				Reporter.log("Override Message is: Present", debugMode);
			} else {
				stepResult = StatusAs.FAILED;
				stepActual = "ERROR : Override Message is Missing";
				softVerify.fail(stepActual);
				Reporter.log(stepActual, false);
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
	}
}
