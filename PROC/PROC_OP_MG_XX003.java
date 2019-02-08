package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.DefaultVariables;

public class PROC_OP_MG_XX003 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.1";
	private String mainArrangement;
	private String customer;
	private String branch = "B2B Branch 623";

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void preCondition(final String productGroup, final String product) {

		stepDescription = "Create " + product + " Arrangement";
		stepExpected = product + " Arrangement created successfully";

		if (loginResult) {
			switchToBranch(branch);
			final ArrangementData mortgageData = new ArrangementData("mortgageArrangement", productGroup, product)
					.withCustomers("NEW", null, "", "100", "100").withDisbursement().build();

			mainArrangement = createDefaultArrangement(mortgageData);
			customer = mortgageData.getCustomers();

			if (customer == null || mainArrangement == null || customer.contains(ERROR)
					|| mainArrangement.contains(ERROR)) {
				stepActual = "Error while creating " + product + " Arrangement";
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
	public void preAuthorizedPaymentSetup(final String productGroup, final String product) {

		stepDescription = "Setup Pre-Authorized Payment for Arrangement " + mainArrangement;
		stepExpected = "Pre-Authorized Payment for Arrangement " + mainArrangement + " was setup successfully";

		if (customer == null || mainArrangement == null || customer.contains(ERROR)
				|| mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			boolean result;
			String fields = "CPA.TYPE," + "PAP.FOREGN," + "CAMB.PAP.ACCT2," + "CAMB.PAP.ACCT," + "DEST.ACCT.NAME:1,"
					+ "CA.PAP.PURPOSE," + "STAND.ALONE.AMT," + "CREATE.DATE," + "CYCLE.FREQUENCY," + "CA.PAP.MATDATE,"
					+ "CAMB.PAD.AGREE,";
			String values = "450," + "000100011," + "123456789369," + "123456789369," + "Test Person,"
					+ "New Authorization," + "200.00," + "+0d," + "e0Y e1M e0W o5D e0F," + "+5y," + "YES,";

			result = reoccurringFixedTransfer("Setup", "External", customer, mainArrangement, fields, values);

			if (!result) {
				stepActual = "Error while setup Pre-Authorized Payment for Arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void preAuthorizedPaymentModification(final String productGroup, final String product) {
		stepDescription = "Modify Pre-Authorized Payment for Arrangement " + mainArrangement;
		stepExpected = "Pre-Authorized Payment for Arrangement " + mainArrangement + " modified successfully";

		if (customer == null || mainArrangement == null || customer.contains(ERROR)
				|| mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			boolean result;
			String fields = "CPA.TYPE," + "PAP.FOREGN," + "CAMB.PAP.ACCT2," + "CAMB.PAP.ACCT," + "DEST.ACCT.NAME:1,"
					+ "CA.PAP.PURPOSE," + "STAND.ALONE.AMT," + "CYCLE.FREQUENCY,";
			String values = "450," + "000100011," + "987654321369," + "987654321369," + "Another Test,"
					+ "Amend Existing Authorization," + "250.00," + "e0Y e1M e0W o5D e0F,";

			result = reoccurringFixedTransfer("Setup", "External", customer, mainArrangement, fields, values);

			if (!result) {
				stepActual = "Error while modifying Pre-Authorized Payment for Arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters({ "productGroup" })
	public void preAuthorizedPaymentCancellation(final String productGroup) {

		stepDescription = "Cancel Pre-Authorized Payment for Arrangement " + mainArrangement;
		stepExpected = "Pre-Authorized Payment for Arrangement " + mainArrangement + " cancelled successfully";

		if (customer == null || mainArrangement == null || customer.contains(ERROR)
				|| mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			boolean result;
			String fields = "CPA.TYPE," + "PAP.FOREGN," + "CAMB.PAP.ACCT2," + "CAMB.PAP.ACCT," + "CA.PAP.PURPOSE,"
					+ "CYCLE.FREQUENCY," + "STAND.ALONE.AMT,";
			String values = "450," + "000100011," + "987654321369," + "987654321369," + "Cancellation,"
					+ "e0Y e1M e0W o5D e0F," + "250.00,";

			result = reoccurringFixedTransfer("Setup", "External", customer, mainArrangement, fields, values);

			if (!result) {
				stepActual = "Error while cancelling Pre-Authorized Payment for Arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			softVerify.assertAll();
		}
	}
}
