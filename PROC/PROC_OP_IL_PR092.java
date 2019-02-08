package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class PROC_OP_IL_PR092 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 2.1";
	private String mainArrangementFix;
	private String mainArrangementVar;
	private String customer;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "customerType" })
	public void preCondition(@Optional("B2B Branch 817") final String branch,
			@Optional(PERSONAL) final String customerType) {

		stepDescription = "Create " + customerType + " customer and two Investment Loans";
		stepExpected = customerType + " customer and two Investment Loans created successfully";

		if (loginResult) {

			switchToBranch(branch);

			customer = createDefaultCustomer(customerType, "", ROLEBASED_LENDING);
			mainArrangementFix = createDefaultArrangement(PERSONAL_LOANS, "Investment Loan Fixed Rate",
					ROLEBASED_LENDING, customer, "+0d");
			mainArrangementVar = createDefaultArrangement(PERSONAL_LOANS, "Investment Loan Variable Rate",
					ROLEBASED_LENDING, customer, "+0d");

			if (mainArrangementFix == null || mainArrangementVar == null || mainArrangementFix.contains(ERROR)
					|| mainArrangementVar.contains(ERROR)) {
				stepActual = "Error while creating Investment Loan";
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
	public void flagDeficientLoan() {

		stepDescription = "Flag the Loans as Deficient";
		stepExpected = "Loan successfully flagged as deficient";

		String fields = "L.LOAN.TYPE";
		String values = "Def 00 Nmc Pi Var";
		boolean result;

		if (mainArrangementFix == null || mainArrangementVar == null || mainArrangementFix.contains(ERROR)
				|| mainArrangementVar.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementActivity(CREATE, "UPDATE ACTIVITY FOR ACCOUNT", mainArrangementVar, PERSONAL_LOANS,
					fields, values);

			if (!result) {
				stepActual = "Error while flagging the Variable Rate Investment Loan: " + mainArrangementVar
						+ " as Deficient";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			fields = "L.LOAN.TYPE";
			values = "Def 00 Nmc Pi Fix";

			result = arrangementActivity(CREATE, "UPDATE ACTIVITY FOR ACCOUNT", mainArrangementFix, PERSONAL_LOANS,
					fields, values);

			if (!result) {
				stepActual = "Error while flagging the Fixed Rate Investment Loan: " + mainArrangementFix
						+ " as Deficient";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
