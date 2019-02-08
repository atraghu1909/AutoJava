package testcases.DataGen;

import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class DataGen_createAgent extends testLibs.BaseTest_DataGen {

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "productGroup", "product", "quantity" })
	public void step1(@Optional("Default") final String branch, final String productGroup, final String product,
			final String quantity) {
		final int iterations = Integer.valueOf(quantity);
		String customer;
		String agentNumber;

		stepDescription = "Create " + quantity + " " + product + " arrangements";
		stepExpected = "All agent commission plans are created successfully";

		if (loginResult) {

			if (!"Default".equals(branch)) {
				switchToBranch(branch);
			}

			for (int i = 1; i <= iterations; i++) {

				try {
					customer = customer("Create", DEALER_ADVISOR, ROLEBASED_OR, DefaultVariables.dealerAdvisorCIFFields,
							DefaultVariables.dealerAdvisorCIFValues);

					agentNumber = arrangements("Create and Authorise", productGroup, product, ROLEBASED_OR, customer,
							"EFFECTIVE.DATE", "-5y", "", "");

					stepActual += System.lineSeparator();
					stepActual += "Agent " + customer + " created with " + product + " " + agentNumber;

				} catch (ElementNotVisibleException | NoSuchElementException e) {
					Reporter.log("Exception caught in iteration " + i + ". Continue with next iteration", debugMode);

				}

			}

		} else {

			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
	}
}
