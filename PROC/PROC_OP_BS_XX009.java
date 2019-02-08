package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.DefaultVariables;

public class PROC_OP_BS_XX009 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 2.0";
	private String mainArrangement;
	private String customer;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "customerType", "productGroup", "product" })
	public void preCondition(@Optional("B2B Branch 623") final String branch,
			@Optional(PERSONAL) final String customerType, @Optional(PERSONAL_ACCOUNTS) final String productGroup,
			@Optional(HISA_PERSONAL) final String product) {

		stepDescription = "Create " + customerType + " customer and " + product + " arrangement";
		stepExpected = customerType + " customer and " + product + " arrangement created successfully";

		if (loginResult) {
			switchToBranch(branch);

			customer = createDefaultCustomer(customerType, productGroup, ROLEBASED_BANKING);
			mainArrangement = createDefaultArrangement(productGroup, product, ROLEBASED_BANKING, customer, "+0d");

			if (customer == null || mainArrangement == null || customer.contains(ERROR)
					|| mainArrangement.contains(ERROR)) {
				stepActual = "Error while creating customer and " + product + " arrangement";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Login failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
	}

	@Test(priority = 2, enabled = true)
	@Parameters("productGroup")
	public void processTransactionDateReset(@Optional(PERSONAL_ACCOUNTS) final String productGroup) {

		stepDescription = "Process Transaction Date Reset for arrangement: " + mainArrangement;
		stepExpected = "Transaction Date Reset for arrangement: " + mainArrangement + " procceed successfully";

		boolean result;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementActivity(CREATE, "Update the last transaction date", mainArrangement, productGroup, "",
					"");

			if (!result) {
				stepActual = "Error while processing Transaction Date Reset for arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
