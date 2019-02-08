package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PROC_OP_DS_RF011 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "2017-07-27";
	private String nominee;
	private String arrangementLegacySimple;
	private String arrangementLegacyCompound;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "productGroup", "productSimple", "productCompound" })
	public void preCondition(@Optional("B2B Branch 607") final String branch,
			@Optional("Broker - Non Registered Deposits") final String productGroup, final String productSimple,
			final String productCompound) {

		stepDescription = "Create nominee, simple and compound Deposit Arrangements";
		stepExpected = "Nominee, simple and compound Deposit Arrangements created successfully";

		if (loginResult) {

			switchToBranch(branch);

			nominee = createNominee("-13m");

			if (nominee == null || nominee.contains(ERROR)) {
				stepActual = "Error while creating Nominee";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			arrangementLegacySimple = dataGenCreateDeposit(nominee, productGroup, productSimple, CAD, "", "",
					DefaultVariables.gicFields, DefaultVariables.gicValues, "Funded", "-2m", "");
			arrangementLegacyCompound = dataGenCreateDeposit(nominee, productGroup, productCompound, CAD, "", "",
					DefaultVariables.gicFields + "START.DATE:1:1", DefaultVariables.gicValues + "R_RENEWAL + ",
					"Funded", "-13m", "");

			if (arrangementLegacySimple == null || arrangementLegacyCompound == null
					|| arrangementLegacySimple.contains(ERROR) || arrangementLegacyCompound.contains(ERROR)) {
				stepActual = "Error while creating " + productGroup + " Arrangement";
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
	public void interimLegacySimpleBalanceAdjustment(
			@Optional("Broker - Non Registered Deposits") final String productGroup) {

		stepDescription = "Adjusting balance for simple pay GIC: " + arrangementLegacySimple;
		stepExpected = "Balance for simple pay GIC: " + arrangementLegacySimple + " adjusted successfully";

		String fields = "NEW.BAL.AMT:2:1";
		String values = "00.00";
		boolean result;

		if (arrangementLegacySimple == null || arrangementLegacySimple.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementActivity(CREATE, "ADJUST.BALANCE ACTIVITY FOR BALANCE.MAINTENANCE",
					arrangementLegacySimple, productGroup, fields, values);

			if (!result) {
				stepActual = "Error while Adjusting balance for simple pay GIC: " + arrangementLegacySimple;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters("productGroup")
	public void interimLegacyCompoundBalanceAdjustment(
			@Optional("Broker - Non Registered Deposits") final String productGroup) {

		stepDescription = "Adjusting balance for compounding GIC: " + arrangementLegacyCompound;
		stepExpected = "Balance for compounding GIC: " + arrangementLegacyCompound + " adjusted successfully";

		String fields = "NEW.BAL.AMT:2:1";
		String values = "105.73";
		boolean result;

		if (arrangementLegacyCompound == null || arrangementLegacyCompound.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementActivity(CREATE, "ADJUST.BALANCE ACTIVITY FOR BALANCE.MAINTENANCE",
					arrangementLegacyCompound, productGroup, fields, values);

			if (!result) {
				stepActual = "Error while Adjusting balance for compounding GIC: " + arrangementLegacyCompound;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
