package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PROC_OP_IL_PR103 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.2";
	private String mainArrangement;
	private String customer;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch ", "customerType", "product" })
	public void preCondition(@Optional("B2B Branch 817") final String branch,
			@Optional(PERSONAL) final String customerType,
			@Optional("Investment Loan Fixed Rate") final String product) {

		stepDescription = "Create customer and " + product + " arrangement";
		stepExpected = "Customer and " + product + " arrangement created successfully";

		if (loginResult) {
			switchToBranch(branch);

			customer = createDefaultCustomer(customerType, "", ROLEBASED_LENDING);

			if (customer == null || customer.contains(ERROR)) {
				stepActual = "Error while creating customer";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {

				mainArrangement = dataGenCreateLoan(customer, PERSONAL_LOANS, product, CAD, "", "",
						DefaultVariables.loanFields, DefaultVariables.loanValues, "Current", "-1m");
				if (mainArrangement == null || mainArrangement.contains(ERROR)) {
					stepActual = "Error while creating " + product + " arrangement";
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
	@Parameters({ "customerType", "product" })
	public void collateralRedemption(@Optional(PERSONAL) final String customerType,
			@Optional("Investment Loan Fixed Rate") final String product) {

		stepDescription = "Perform collateral redemption for arrangement: " + mainArrangement;
		stepExpected = "Collateral redemption for arrangement: " + mainArrangement + " performed successfully";

		String collateralAmount;
		String fields;
		String values;
		boolean result = false;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			findArrangement(AUTHORISED, mainArrangement, ARRANGEMENT, ROLEBASED_LENDING, PERSONAL_LOANS, product, CAD);
			switchToPage(LASTPAGE, false);
			collateralAmount = versionScreen.getEnqListCell("r2_Limit", 0, "3").getText().replaceAll(",", "");
			fields = "DEBIT.ACCT.NO," + "CREDIT.AMOUNT," + "CREDIT.THEIR.REF," + "ORDERING.BANK:1,";
			values = "CAD1100100017817," + collateralAmount + "," + "123456789," + mainArrangement + ",";
			result = arrangementAction(mainArrangement, customer, ROLEBASED_LENDING,
					"Lending Principal Decrease / Repay", fields, values, customerType);

			if (!result) {
				stepActual = "Error while performing Lending Principal Decrease / Repay for arrangement: "
						+ mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;

			}
		}
		softVerify.assertAll();
	}
}
