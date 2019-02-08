package testcases.PROC;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;

public class PROC_BB_ACM_OP004 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 4.0";
	private String customer;
	private String mainArrangement;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "productGroup", "product" })
	public void preCondition(@Optional("B2B Branch 817") final String branch,
			@Optional(PERSONAL_LOANS) final String productGroup, final String product) {

		stepDescription = "Create a Delinquent " + product + " arrangement";
		stepExpected = "Delinquent Arrangement created successfully";

		if (loginResult) {

			switchToBranch(branch);

			final ArrangementData arrangementData = new ArrangementData("mainArrangement", productGroup, product)
					.withEffectiveDate("-45d")
					.withTerm("1Y")
					.withDisbursement()
					.withRepayments()
					.build();

			mainArrangement = createDefaultArrangement(arrangementData);
			customer = arrangementData.getCustomers();

			if (customer == null || mainArrangement == null || customer.contains(ERROR)
					|| mainArrangement.contains(ERROR)) {
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
	@Parameters({ "productGroup", "product" })
	public void activateIndicators(@Optional(PERSONAL_LOANS) final String productGroup, final String product) {

		stepDescription = "Set NPL and NAB indicators on arrangement " + mainArrangement;
		stepExpected = "Indicators were set successfully";

		String fields;
		String values;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			fields = "CA.NPL," + "CA.NPL.REASON," + "CA.NAB,";
			values = "Y," + "Insufficient Debt Service," + "Y,";

			try {
				arrangementActivity(CREATE_AUTHORISE, "Update Risk Details", mainArrangement, productGroup, fields,
						values);
			} catch (NoSuchElementException e) {
				Reporter.log(e.getMessage(), false);
				stepActual = "There was an unexpected error while executing this step";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}

		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void closePAC(@Optional(PERSONAL_LOANS) final String productGroup, final String product) {
		
		stepDescription = "Close the Pre-Authorised Credit from arrangement " + mainArrangement;
		stepExpected = "PAC closed successfully";

		String fields;
		String values;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			fields = "PAYMENT.METHOD:1," + "PAYIN.SETTLEMENT:1,";
			values = "Due," + "No,";

			try {
				arrangementActivity(CREATE_AUTHORISE, "CHANGE ACTIVITY FOR SCHEDULE", mainArrangement, productGroup,
						fields, values);
			} catch (NoSuchElementException e) {
				Reporter.log(e.getMessage(), false);
				stepActual = "There was an unexpected error while executing this step";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		
		softVerify.assertAll();
	}
}
