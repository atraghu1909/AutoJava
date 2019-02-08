package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;

public class PROC_SF_FIN_OP001 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 1.1";
	private String customer;
	private String mainArrangement;
	private String transactionID;
	private String fields;
	private String values;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch ", "customerType", "product", "productGroup" })
	public void preCondition(@Optional("B2B Branch 817") final String branch,
			@Optional(PERSONAL) final String customerType, final String product,
			@Optional(PERSONAL_LOANS) final String productGroup) {

		stepDescription = "Create " + customerType + " customer and " + product + " arrangement";
		stepExpected = customerType + " customer and " + product + " arrangement created successfully";

		if (loginResult) {
			switchToBranch(branch);

			ArrangementData loanData = new ArrangementData("loanArrangement", productGroup, product).build();

			mainArrangement = createDefaultArrangement(loanData);
			customer = loanData.getCustomers();

			if (mainArrangement == null || mainArrangement.contains(ERROR)) {
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
	public void chequeForDisbursement() {

		stepDescription = "Perform disbursement via cheque for customer: " + customer;
		stepExpected = "Disbursement via cheque for customer: " + customer + " performed successfully";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			fields = "DEBIT.ACCT.NO," + "DEBIT.AMOUNT," + "CREDIT.ACCT.NO," + "DEBIT.CURRENCY,";
			values = mainArrangement + "," + "50T," + "CAD1110200017817," + CAD + ",";
			transactionID = financialTransaction(CREATE, "Account Transfer - Transfer between Accounts", fields,
					values);

			if (transactionID == null || transactionID.contains(ERROR)) {
				stepActual = "Error while performing disbursement via cheque for arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	public void chequeForCompensation() {

		stepDescription = "Perform compensation via cheque for customer: " + customer;
		stepExpected = "Compensation via cheque for customer: " + customer + " performed successfully";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			fields = "DEBIT.ACCT.NO," + "DEBIT.AMOUNT," + "CREDIT.ACCT.NO," + "DEBIT.CURRENCY," + "ORDERING.BANK:1,"
					+ "PROFIT.CENTRE.CUST,";
			values = "PL52815," + "2T," + "CAD1110200017817," + CAD + "," + "Bank," + customer + ",";
			transactionID = financialTransaction(CREATE, "Account Transfer - Transfer between Accounts", fields,
					values);

			if (transactionID == null || transactionID.contains(ERROR)) {
				stepActual = "Error while performing compensation via cheque for arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}