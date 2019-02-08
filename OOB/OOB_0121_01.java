package testcases.OOB;

import org.testng.Reporter;
import org.testng.annotations.Optional;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class OOB_0121_01 extends testLibs.BaseTest_OOB {

	private String newArrangement;

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product", "customer", "currency" })
	public void step1(final String productGroup, final String product, final String customer,
			@Optional("CAD") final String currency) {
		stepDescription = "Open account in one of the " + product + " for " + currency + " Currency ";
		stepExpected = "account  created successfully for " + currency + " Currency for " + product;
		String actualCIF;

		if (loginResult) {
			switchToBranch("B2B Branch 817");
			actualCIF = findCIF(customer, "", "");
			if (!actualCIF.equals(customer)) {
				Reporter.log("CIF received as parameter (" + customer + ") was not found. Using new CIF instead: "
						+ actualCIF, debugMode);

			}

			newArrangement = arrangements(CREATE, productGroup, product, "", actualCIF, "CUSTOMER:1," + "CURRENCY,",
					actualCIF + "," + currency + ",", DefaultVariables.productGroupFields.get(productGroup),
					DefaultVariables.productGroupValues.get(productGroup));
			
			if (newArrangement.contains("Error")) {
				stepResult = StatusAs.FAILED;
				stepActual = "Could not create new arrangement";
				softVerify.fail(stepActual);
			} else {
				Reporter.log("Arrangement created successfully: " + newArrangement, debugMode);
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
	}
}