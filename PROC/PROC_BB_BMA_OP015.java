package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.DefaultVariables;

public class PROC_BB_BMA_OP015 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 4.0";
	private String mortgageArrangement;
	private String customer;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "customerType", "product" })
	public void preCondition(@Optional("B2B Branch 623") final String branch,
			@Optional(PERSONAL) final String customerType, final String product) {

		stepDescription = "Create customer and " + product + " arrangement";
		stepExpected = "Customer and " + product + " arrangement created successfully";

		if (loginResult) {

			switchToBranch(branch);

			final ArrangementData mortgageData = new ArrangementData("mortgageArrangement", RETAIL_MORTGAGES, product)
					.withCustomers("NEW", null, "", "100", "100").withEffectiveDate("-1m").withDisbursement()
					.withRepayments().build();

			mortgageArrangement = createDefaultArrangement(mortgageData);
			customer = mortgageData.getCustomers();

			if (customer == null || mortgageArrangement == null || customer.contains(ERROR)
					|| mortgageArrangement.contains(ERROR)) {
				stepActual = "Error while creating " + product + " arrangement";
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
	public void discharge() {

		stepDescription = "Enter the discharge of charge registration number";
		stepExpected = "Discharge of charge registration number entered successfully";

		String fields = "Registration details#L.COLL.REG.NO+:2," + "Registration details#L.COLL.REG.DATE:2,"
				+ "Registration details#L.COLL.REG.RSN:2,";
		String values = "REG#2 TEST," + "+0d," + "Discharge,";
		String collateral;

		if (customer == null || mortgageArrangement == null || customer.contains(ERROR)
				|| mortgageArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			collateral = collateral(AMEND, COLLATERAL_DETAILS, customer + ".1.1", fields, values);
			if (collateral.contains(ERROR)) {
				stepActual = "Error while entering the discharge of charge registration number";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
