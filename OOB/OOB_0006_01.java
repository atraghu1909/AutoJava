package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class OOB_0006_01 extends testLibs.BaseTest_OOB {

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product", "customer" })
	public void step1(final String productGroup, final String product, final String customer) {

		stepDescription = "Verify " + product + " mandatory fields";
		stepExpected = product + " mandatory fields verified successfully";
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

			inputData("CUSTOMER:1", actualCIF, false);
			inputData("CURRENCY", CAD, true);

			toolElements.toolsButton(VALIDATE_DEAL).click();

			try {
				inputTable.isMandatoryElementPresent("primaryOfficerMandatory");
				Reporter.log("Primary Officer is Mandatory", debugMode);
			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "ERROR : Primary Officer Field Madatory is Missing";
				softVerify.fail(stepActual);
			}

			try {
				inputTable.isMandatoryElementPresent("intendedUseMandatory");
				Reporter.log("Intended Use is Mandatory", debugMode);
			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "ERROR : Intended Use Madatory Missing";
				softVerify.fail(stepActual);
				Reporter.log(e.getMessage(), false);
			}

			try {
				inputTable.isMandatoryElementPresent("thirdPartyMandatory");
				Reporter.log("Third Party is Mandatory ", debugMode);
			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "ERROR: Third Party Mandatory is Missing";
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
