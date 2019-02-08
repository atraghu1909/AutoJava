package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PROC_OP_BS_XX005 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.5";
	private String mainArrangement1;
	private String customer1;
	private String mainArrangement2;
	private String customer2;
	private String branch = "B2B Branch 817";

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void preCondition(@Optional(PERSONAL_ACCOUNTS) final String productGroup,
			@Optional(PERSONAL_CHEQUING) final String product) {

		stepDescription = "Create " + product + " Arrangement";
		stepExpected = product + " Arrangement created successfully";

		switchToBranch(branch);

		customer1 = customer(CREATE, PERSONAL, ROLEBASED_BANKING, DefaultVariables.personalCIFFields,
				DefaultVariables.personalCIFValues);

		mainArrangement1 = dataGenCreateBanking(customer1, productGroup, product, CAD, "", "",
				DefaultVariables.bankingFields, DefaultVariables.bankingValues, "");
		if (mainArrangement1 == null || mainArrangement1.contains(ERROR)) {
			stepActual = "Error while creating " + product + " Arrangement: " + mainArrangement1;
			softVerify.fail(stepActual);
			stepResult = StatusAs.FAILED;
		}

		customer2 = customer(CREATE, PERSONAL, ROLEBASED_BANKING, DefaultVariables.personalCIFFields,
				DefaultVariables.personalCIFValues);

		mainArrangement2 = dataGenCreateBanking(customer2, productGroup, product, CAD, "", "",
				DefaultVariables.bankingFields, DefaultVariables.bankingValues, "");
		if (mainArrangement2 == null || mainArrangement2.contains(ERROR)) {
			stepActual = "Error while creating " + product + " Arrangement: " + mainArrangement2;
			softVerify.fail(stepActual);
			stepResult = StatusAs.FAILED;
		}

		softVerify.assertAll();
	}

	@Test(priority = 2, enabled = true)
	public void internalFixedTransferSetup() {

		stepDescription = "Setup Internal Fixed Transfer for arrangement: " + mainArrangement1;
		stepExpected = "Internal Fixed Transfer for arrangement: " + mainArrangement1 + " setup successfully";

		if (customer1 == null || mainArrangement1 == null || customer1.contains(ERROR)
				|| mainArrangement1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			boolean result;
			String fields;
			String values;

			fields = "CURRENCY," + "CURRENT.AMOUNT.BAL," + "CURRENT.FREQUENCY," + "CPTY.ACCT.NO,";
			values = "CAD," + "20.00," + "e0Y e1M e0W o1D e0F," + mainArrangement2 + ",";

			result = reoccurringFixedTransfer("Setup", "Internal", customer1, mainArrangement1, fields, values);

			if (!result) {
				stepActual = "Error while setting up Internal Fixed Transfer for arrangement: " + mainArrangement1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	public void internalFixedTransferModification() {

		stepDescription = "Modify Internal Fixed Transfer for arrangement: " + mainArrangement1;
		stepExpected = "Internal Fixed Transfer for arrangement: " + mainArrangement1 + " modified successfully";

		if (customer1 == null || mainArrangement1 == null || customer1.contains(ERROR)
				|| mainArrangement1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			boolean result;
			final String fields;
			final String values;

			fields = "CURRENT.AMOUNT.BAL," + "CURRENT.FREQUENCY,";
			values = "20.00," + "e0Y e1M e0W o5D e0F,";

			result = reoccurringFixedTransfer("Modify", "Internal", customer1, mainArrangement1, fields, values);

			if (!result) {
				stepActual = "Error while modifying Internal Fixed Transfer for arrangement: " + mainArrangement1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	public void internalFixedTransferClosure() {

		stepDescription = "Close Internal Fixed Transfer for arrangement: " + mainArrangement1;
		stepExpected = "Internal Fixed Transfer for arrangement: " + mainArrangement1 + " closed successfully";

		if (customer1 == null || mainArrangement1 == null || customer1.contains(ERROR)
				|| mainArrangement1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			boolean result;

			result = reoccurringFixedTransfer("Close", "Internal", customer1, mainArrangement1, "", "");

			if (!result) {
				stepActual = "Error while closing Internal Fixed Transfer for arrangement: " + mainArrangement1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	public void externalFixedTransferSetup() {

		stepDescription = "Setup External Fixed Transfer for arrangement: " + mainArrangement2;
		stepExpected = "External Fixed Transfer for arrangement: " + mainArrangement2 + " setup successfully";

		if (customer2 == null || mainArrangement2 == null || customer2.contains(ERROR)
				|| mainArrangement2.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			boolean result;
			String fields;
			String values;
			fields = "CPA.TYPE," + "PAP.FOREGN," + "CAMB.PAP.ACCT2," + "CAMB.PAP.ACCT," + "STAND.ALONE.AMT,"
					+ "CYCLE.FREQUENCY,";
			values = "323," + "000100782," + "123654789369," + "123654789369," + "15.00," + "e0Y e1M e0W o1D e0F,";

			result = reoccurringFixedTransfer("Setup", "External", customer2, mainArrangement2, fields, values);

			if (!result) {
				stepActual = "Error while setting up External Fixed Transfer for arrangement: " + mainArrangement2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	public void externalFixedTransferModification() {

		stepDescription = "Modify External Fixed Transfer for arrangement: " + mainArrangement2;
		stepExpected = "External Fixed Transfer for arrangement: " + mainArrangement2 + " modified successfully";

		if (customer2 == null || mainArrangement2 == null || customer2.contains(ERROR)
				|| mainArrangement2.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			boolean result;
			final String fields;
			final String values;
			fields = "PAP.FOREGN," + "CAMB.PAP.ACCT2," + "CAMB.PAP.ACCT," + "STAND.ALONE.AMT," + "CYCLE.FREQUENCY,";
			values = "000292932," + "987654321369," + "987654321369," + "25.00," + "e0Y e1M e0W o5D e0F,";

			result = reoccurringFixedTransfer("Modify", "External", customer2, mainArrangement2, fields, values);

			if (!result) {
				stepActual = "Error while modifying External Fixed Transfer for arrangement: " + mainArrangement2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 7, enabled = true)
	public void externalFixedTransferClosure() {

		stepDescription = "Close External Fixed Transfer for arrangement: " + mainArrangement2;
		stepExpected = "External Fixed Transfer for arrangement: " + mainArrangement2 + " closed successfully";

		if (customer2 == null || mainArrangement2 == null || customer2.contains(ERROR)
				|| mainArrangement2.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			boolean result;

			result = reoccurringFixedTransfer("Close", "External", customer2, mainArrangement2, "", "");

			if (!result) {
				stepActual = "Error while closing External Fixed Transfer for arrangement: " + mainArrangement2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
