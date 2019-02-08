package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.DefaultVariables;

public class PROC_OP_BS_XX008 extends testLibs.BaseTest_DataGen {

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

		boolean result;

		if (loginResult) {
			switchToBranch(branch);

			customer = createDefaultCustomer(customerType, productGroup, ROLEBASED_BANKING);
			mainArrangement = createDefaultArrangement(productGroup, product, ROLEBASED_BANKING, customer, "+0d");

			if (customer == null || mainArrangement == null || customer.contains(ERROR)
					|| mainArrangement.contains(ERROR)) {
				stepActual = "Error while creating customer and " + product + " arrangement";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {
				result = arrangementActivity(CREATE, "MANUAL.SET ACTIVITY FOR DORMANCY", mainArrangement, productGroup,
						"ACTIVITY", "ACCOUNTS-MANUAL.SET-DORMANCY*DORMANT.2Y");
				if (!result) {
					stepActual = "Error while performing MANUAL.SET ACTIVITY FOR DORMANCY activity for arrangement: "
							+ mainArrangement;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Login failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
	}

	@Test(priority = 2, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void reActivatingDormantAccountManualProcedure(@Optional(PERSONAL_ACCOUNTS) final String productGroup,
			@Optional("HISA Personal") final String product) {

		stepDescription = "Re-activating dormant account for arrangement: " + mainArrangement;
		stepExpected = "Dormant account for arrangement: " + mainArrangement + " re-acticated successfully";

		boolean result;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			findArrangement(AUTHORISED, mainArrangement, ARRANGEMENT, ROLEBASED_BANKING, productGroup, product, CAD);
			toolElements.toolsButton("Reset").click();
			switchToPage(LASTPAGE, false);
			result = inputTable.commitAndOverride();
			if (!result) {
				stepActual = "Error while re-activating dormant account for arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
