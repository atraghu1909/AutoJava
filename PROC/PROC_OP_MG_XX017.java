package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.DefaultVariables;

public class PROC_OP_MG_XX017 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 4.0";
	private ArrangementData mortgageCheque;
	private ArrangementData mortgageEFT;
	private String arrangementCheque;
	private String arrangementEFT;
	private String customer;
	private String beneficiary;
	private boolean result = false;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "customerType", "product" })
	public void preCondition(@Optional("B2B Branch 623") final String branch,
			@Optional(PERSONAL) final String customerType, final String product) {

		stepDescription = "Create " + customerType + " customer and two " + product + " arrangements";
		stepExpected = customerType + " customer and two " + product + " arrangements created successfully";

		boolean localResult = true;

		if (loginResult) {
			switchToBranch(branch);

			mortgageCheque = new ArrangementData("chequeArrangement", RETAIL_MORTGAGES, product)
					.withEffectiveDate("-1m").withTerm("1Y").withCollateralValue("200000")
					.withCommitmentAmount("100000").withEscrow(MUNICIPALITY).withSettlement("Beneficiary", "NEW")
					.withDisbursement().build();

			arrangementCheque = createDefaultArrangement(mortgageCheque);
			customer = mortgageCheque.getCustomers();
			beneficiary = mortgageCheque.getSettlementAccount();

			mortgageEFT = new ArrangementData("EFTArrangement", RETAIL_MORTGAGES, product)
					.withCustomers(customer, createdCustomers.get(customer), "", "100,", "100,")
					.withEffectiveDate("-1m").withTerm("1Y").withCommitmentAmount("100000").withEscrow(MUNICIPALITY)
					.withDisbursement().build();

			arrangementEFT = createDefaultArrangement(mortgageEFT);

			localResult = customer != null && arrangementCheque != null && arrangementEFT != null
					&& !customer.contains(ERROR) && !arrangementEFT.contains(ERROR)
					&& !arrangementCheque.contains(ERROR);

			if (!localResult) {
				stepActual = "Error while creating a " + customerType + " customer and two " + product
						+ " arrangements";
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
	@Parameters("customerType")
	public void chequePayment(@Optional(PERSONAL) final String customerType) {

		stepDescription = "Perform payment by cheque";
		stepExpected = "Payment by cheque performed successfully";

		final String fields = "CREDIT.ACCT.NO," + "CREDIT.CURRENCY," + "DEBIT.ACCT.NO," + "CREDIT.AMOUNT,"
				+ "ORDERING.BANK:1," + "CHEQUE.NUMBER,";
		final String values = arrangementCheque + "," + CAD + ","
				+ DefaultVariables.internalAccountForBranch.get(currentBranch) + "," + "700.00," + arrangementCheque
				+ "," + "123456789,";

		if (arrangementCheque == null || arrangementCheque.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementAction(arrangementCheque, customer, ROLEBASED_LENDING, "Lending Repayment", fields,
					values, customerType);

			if (!result) {
				stepActual = "Error while performing payment by cheque";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	public void EFTPayment() {

		stepDescription = "Perform payment by EFT";
		stepExpected = "Payment by EFT performed successfully";

		final String fields = "PAYMENT.ORDER.PRODUCT," + "CREDIT.ACCOUNT," + "PAYMENT.CURRENCY," + "PAYMENT.AMOUNT,"
				+ "ORDERING.CUSTOMER.SSI,";
		final String values = "ACHCREDIT," + arrangementEFT + "," + CAD + "," + "700.00," + beneficiary + ",";
		String transactionID;

		if (arrangementEFT == null || arrangementEFT.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			transactionID = createAdHocPayment(CREATE, "EFT", arrangementEFT, fields, values);

			if (transactionID.contains(ERROR)) {
				stepActual = "Error while performing payment by EFT";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	public void escrowPaymentByCheque() {

		stepDescription = "Perform escrow payment by cheque";
		stepExpected = "Escrow payment by cheque performed successfully";

		final String fields = "CREDIT.ACCT.NO," + "CREDIT.CURRENCY," + "DEBIT.ACCT.NO," + "CREDIT.AMOUNT,"
				+ "ORDERING.BANK:1," + "CHEQUE.NUMBER,";
		final String values = arrangementCheque + "," + CAD + ","
				+ DefaultVariables.internalAccountForBranch.get(currentBranch) + "," + "50.00," + arrangementCheque
				+ "," + "987654321,";
		String transactionID;

		if (arrangementCheque == null || arrangementCheque.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			transactionID = financialTransaction(CREATE, "Escrow Deposit", fields, values);

			if (transactionID.contains(ERROR)) {
				stepActual = "Error while performing escrow payment by cheque";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	public void escrowPaymentByEFT() {

		stepDescription = "Perform escrow payment by EFT";
		stepExpected = "Escrow payment by EFT performed successfully";

		final String fields = "PAYMENT.ORDER.PRODUCT," + "CREDIT.ACCOUNT," + "PAYMENT.CURRENCY," + "PAYMENT.AMOUNT,"
				+ "ORDERING.CUSTOMER.SSI,";
		final String values = "ESCROWCR," + arrangementEFT + "," + CAD + "," + "100.00," + beneficiary + ",";
		String transactionID;

		if (arrangementEFT == null || arrangementEFT.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			transactionID = createAdHocPayment(CREATE, "EFT", arrangementEFT, fields, values);

			if (transactionID.contains(ERROR)) {
				stepActual = "Error while performing escrow payment by EFT";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	public void applyManualPaymentFee() {

		stepDescription = "Apply manual payment fee";
		stepExpected = "Manual payment fee applied successfully";

		String fields = "FIXED.AMOUNT";
		String values = "75.00";

		if (arrangementCheque == null || arrangementEFT == null || arrangementCheque.contains(ERROR)
				|| arrangementEFT.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementActivity(CREATE, "Change and Capitalise ALMANPAYFEE", arrangementCheque,
					RETAIL_MORTGAGES, fields, values)
					&& arrangementActivity(CREATE, "Change and Capitalise ALMANPAYFEE", arrangementEFT,
							RETAIL_MORTGAGES, fields, values)
					&& arrangementActivity(CREATE, "Change and Due ALMANPAYFEE", arrangementCheque, RETAIL_MORTGAGES,
							fields, values)
					&& arrangementActivity(CREATE, "Change and Due ALMANPAYFEE", arrangementEFT, RETAIL_MORTGAGES,
							fields, values);

			if (!result) {
				stepActual = "Error while applying manual payment fee";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 7, enabled = true)
	@Parameters("customerType")
	public void payDueFeeByCheque(@Optional(PERSONAL) final String customerType) {

		stepDescription = "Pay due fee by cheque";
		stepExpected = "Due fee paid successfully by cheque";

		final String fields = "CREDIT.ACCT.NO," + "CREDIT.CURRENCY," + "DEBIT.ACCT.NO," + "CREDIT.AMOUNT,"
				+ "ORDERING.BANK:1," + "CHEQUE.NUMBER,";
		final String values = arrangementCheque + "," + CAD + ","
				+ DefaultVariables.internalAccountForBranch.get(currentBranch) + "," + "75.00," + arrangementCheque
				+ "," + "123456789,";

		if (arrangementCheque == null || arrangementCheque.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementAction(arrangementCheque, customer, ROLEBASED_LENDING, "Lending Repayment", fields,
					values, customerType);

			if (!result) {
				stepActual = "Error while paying due fee by cheque";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 8, enabled = true)
	public void payDueFeeByEFT() {

		stepDescription = "Pay due fee by EFT";
		stepExpected = "Due fee paid successfully by EFT";

		final String fields = "PAYMENT.ORDER.PRODUCT," + "CREDIT.ACCOUNT," + "PAYMENT.CURRENCY," + "PAYMENT.AMOUNT,"
				+ "ORDERING.CUSTOMER.SSI,";
		final String values = "ACHCREDIT," + arrangementEFT + "," + CAD + "," + "75.00," + beneficiary + ",";
		String transactionID;

		if (arrangementEFT == null || arrangementEFT.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			transactionID = createAdHocPayment(CREATE, "EFT", arrangementEFT, fields, values);

			if (transactionID.contains(ERROR)) {
				stepActual = "Error while paying due fee by EFT";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 9, enabled = true)
	public void payCapitalisedFeeByCheque() {

		stepDescription = "Pay capitalised fee by cheque";
		stepExpected = "Capitalised fee paid successfully by cheque";

		final String fields = "CREDIT.ACCT.NO," + "CREDIT.CURRENCY," + "DEBIT.ACCT.NO," + "CREDIT.AMOUNT,"
				+ "ORDERING.BANK:1,";
		final String values = arrangementCheque + "," + CAD + ","
				+ DefaultVariables.internalAccountForBranch.get(currentBranch) + "," + "75.00," + arrangementCheque
				+ ",";
		String transactionID;

		if (arrangementCheque == null || arrangementCheque.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			transactionID = financialTransaction(CREATE, "Principal Decrease without Prepayment", fields, values);

			if (transactionID.contains(ERROR)) {
				stepActual = "Error while paying capitalised fee by cheque";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 10, enabled = true)
	public void payCapitalisedFeeByEFT() {

		stepDescription = "Pay capitalised fee by EFT";
		stepExpected = "Capitalised fee paid successfully by EFT";

		final String fields = "PAYMENT.ORDER.PRODUCT," + "CREDIT.ACCOUNT," + "PAYMENT.CURRENCY," + "PAYMENT.AMOUNT,"
				+ "ORDERING.CUSTOMER.SSI,";
		final String values = "ACHRDPR," + arrangementEFT + "," + CAD + "," + "75.00," + beneficiary + ",";
		String transactionID;

		if (arrangementEFT == null || arrangementEFT.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			transactionID = createAdHocPayment(CREATE, "EFT", arrangementEFT, fields, values);

			if (transactionID.contains(ERROR)) {
				stepActual = "Error while paying capitalised fee by EFT";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}