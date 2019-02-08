package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class OOB_0073_01 extends testLibs.BaseTest_OOB {

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product", "customer", "interestCategory", "interestType" })
	public void step1(final String productGroup, final String product, final String customer,
			@Optional("") final String interestCategory, @Optional("") final String interestType) {
		stepDescription = "Validate that " + interestCategory + " " + interestType + " is displayed for " + product;
		stepExpected = interestCategory + " " + interestType + " is displayed successfully for " + product;
		String actualCIF;

		if (loginResult) {

			switchToBranch("B2B Branch 817");

			actualCIF = findCIF(customer, "", "");
			if (!actualCIF.equals(customer)) {
				Reporter.log("CIF received as parameter (" + customer + ") was not found. Using new CIF instead: "
						+ actualCIF, debugMode);

			}
			final String step1Fields = "CUSTOMER:1,CURRENCY";
			final String step1Values = actualCIF + ",CAD";
			arrangements("Validate", productGroup, product, "", actualCIF, step1Fields, step1Values, "", "");

			try {
				inputTable.legendElement(interestCategory);
				Reporter.log("Interest category(" + interestCategory + ") is present", debugMode);
				if (!("".equals(interestType))) {
					try {
						inputTable.interestTypeElement(interestCategory, interestType);
						Reporter.log("Element(" + interestType + ") of interest category(" + interestCategory
								+ ") is present", debugMode);

					} catch (NoSuchElementException e) {
						stepActual = "Element(" + interestType + ") of interest category(" + interestCategory
								+ ") is not present";
						softVerify.fail(stepActual);
						stepResult = StatusAs.FAILED;
						Reporter.log(stepActual, debugMode);
					}
				}

			} catch (NoSuchElementException e) {
				stepActual = "Interest category(" + interestCategory + ") is not present";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
				Reporter.log(stepActual, debugMode);
			}
		}
		softVerify.assertAll();
	}
}
