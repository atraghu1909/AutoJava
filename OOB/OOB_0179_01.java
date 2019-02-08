package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class OOB_0179_01 extends testLibs.BaseTest_OOB {

	private String newArrangement;

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product", "customer", "activity" })
	public void step1(@Optional("") final String productGroup, @Optional("") final String product,
			@Optional("") final String customer, @Optional("") final String activity) throws InterruptedException {

		stepDescription = "Validate that the user can successfully start " + activity + " Arrangement Activity";
		stepExpected = "User can successfully start " + activity + " Arrangement Activity";

		if (loginResult) {
			switchToBranch("B2B Branch 817");

			newArrangement = findArrangement(AUTHORISED, customer, CIF, "", productGroup, product, CAD);

			Reporter.log("Arrangement created: " + newArrangement, debugMode);

			arrangementActivity("Open", activity, newArrangement, productGroup, "", "");
			try {
				inputTable.listrows("");
			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "User could not start " + activity + " Arrangement Activity";
				softVerify.fail(stepActual);
			}

		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}

	}
}