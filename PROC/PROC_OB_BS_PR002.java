package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PROC_OB_BS_PR002 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.4";
	private static String depositBranch = "B2B Branch 607";
	private static String bankingBranch = "B2B Branch 623";
	private String bankingArrangement1;
	private String bankingArrangement2;
	private String bankingArrangement3;
	private String depositArrangement;
	private String bankingCustomer1;
	private String bankingCustomer2;
	private String bankingCustomer3;
	private String bankingBeneficiary;
	private String depositCustomer;
	private String customerType;
	private String internalAccount;
	private String remoteAccountRBC;
	private String remoteAccountTD;
	private String beneficiaryFields;
	private String beneficiaryValues;

	@Test(priority = 1, enabled = true)
	@Parameters({ "bankingProductGroup", "bankingProduct", "depositProductGroup", "depositProduct" })
	public void preCondition(@Optional(PERSONAL_ACCOUNTS) final String bankingProductGroup,
			@Optional(PERSONAL_CHEQUING) final String bankingProduct,
			final String depositProductGroup, final String depositProduct) {

		stepDescription = "Create " + bankingProduct + " Banking Arrangement and " + depositProduct
				+ " Deposit Arrangement";
		stepExpected = bankingProduct + " Banking Arrangement and " + depositProduct
				+ " Deposit Arrangement created successfully";

		if (loginResult) {

			switchToBranch(bankingBranch);
			customerType = DefaultVariables.productGroupCustomerType.get(bankingProductGroup);

			bankingCustomer1 = createDefaultCustomer(customerType, bankingProductGroup, ROLEBASED_BANKING);
			bankingCustomer2 = createDefaultCustomer(customerType, bankingProductGroup, ROLEBASED_BANKING);
			bankingCustomer3 = createDefaultCustomer(customerType, bankingProductGroup, ROLEBASED_BANKING);

			beneficiaryFields = DefaultVariables.beneficiaryFields + "OWNING.CUSTOMER," + "BEN.CUSTOMER,";
			beneficiaryValues = DefaultVariables.beneficiaryValues + bankingCustomer1 + ","
					+ createdCustomers.get(bankingCustomer1).getCustomerName() + ",";

			bankingBeneficiary = beneficiaryCode(CREATE, "EFT Client", "", beneficiaryFields, beneficiaryValues);

			bankingArrangement1 = dataGenCreateBanking(bankingCustomer1, bankingProductGroup, bankingProduct, CAD, "",
					"", DefaultVariables.bankingFields, DefaultVariables.bankingValues, "");
			bankingArrangement2 = dataGenCreateBanking(bankingCustomer2, bankingProductGroup, bankingProduct, CAD, "",
					"", DefaultVariables.bankingFields, DefaultVariables.bankingValues, "");
			bankingArrangement3 = dataGenCreateBanking(bankingCustomer2, bankingProductGroup, bankingProduct, CAD, "",
					"", DefaultVariables.bankingFields, DefaultVariables.bankingValues, "");

			switchToBranch(depositBranch);
			customerType = DefaultVariables.productGroupCustomerType.get(depositProductGroup);

			depositCustomer = createDefaultCustomer(customerType, depositProductGroup, ROLEBASED_BANKING);
			depositArrangement = dataGenCreateDeposit(depositCustomer, depositProductGroup, depositProduct, CAD, "", "",
					DefaultVariables.gicFields, DefaultVariables.gicValues, "", "+0d", "");

			if ("B2B Branch 607".equals(depositBranch)) {
				internalAccount = "CAD1100100017607";
				remoteAccountRBC = "CAD1160000017607";
				remoteAccountTD = "CAD1160100017607";
			} else {
				internalAccount = "CAD1100100011247";
				remoteAccountRBC = "CAD1160000011247";
				remoteAccountTD = "CAD1160100017247";
			}

			if (bankingArrangement1 == null || bankingArrangement2 == null || bankingArrangement3 == null
					|| bankingCustomer1 == null || bankingCustomer2 == null || bankingCustomer3 == null
					|| depositArrangement == null || depositCustomer == null || depositArrangement.contains(ERROR)
					|| depositCustomer.contains(ERROR) || bankingArrangement1.contains(ERROR)
					|| bankingArrangement2.contains(ERROR) || bankingArrangement3.contains(ERROR)
					|| bankingCustomer1.contains(ERROR) || bankingCustomer2.contains(ERROR)
					|| bankingCustomer3.contains(ERROR)) {
				stepActual = "Error while creating Arrangement";
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
	@Parameters({ "bankingProductGroup" })
	public void depositChequeIntoBanking(@Optional(PERSONAL_ACCOUNTS) final String bankingProductGroup) {

		stepDescription = "Deposit Cheque into Banking arrangement: " + bankingArrangement1;
		stepExpected = "Cheque into Banking arrangement: " + bankingArrangement1 + " Deposited successfully";

		if (bankingArrangement1 == null || bankingArrangement1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			String paymentID;
			String fields;
			String values;
			switchToBranch(bankingBranch);
			fields = "DEBIT.ACCT.NO," + "DEBIT.CURRENCY," + "CREDIT.CURRENCY," + "ORDERING.CUST:1," + "CREDIT.ACCT.NO,"
					+ "DEBIT.AMOUNT," + "TRANSACTION.TYPE,";
			values = "CAD1100100017623," + "CAD," + "CAD," + "Test Client," + bankingArrangement1 + "," + "10000,"
					+ "ACAD,";
			paymentID = financialTransaction(CREATE, "Remote Cheque Deposit", fields, values);

			if (paymentID.contains(ERROR)) {
				stepActual = "Error while Depositing Cheque into Banking arrangement: " + bankingArrangement1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "bankingProductGroup" })
	public void transferFundsIntoBanking(@Optional(PERSONAL_ACCOUNTS) final String bankingProductGroup) {

		stepDescription = "Transfer Funds from Banking arrangement: " + bankingArrangement1
				+ "  into Banking arrangement: " + bankingArrangement2;
		stepExpected = "Funds from Banking arrangement: " + bankingArrangement2 + " into Banking arrangement: "
				+ bankingArrangement3 + " Transferred successfully";

		if (bankingArrangement2 == null || bankingArrangement3 == null || bankingArrangement2.contains(ERROR)
				|| bankingArrangement3.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			String paymentID;
			String fields;
			String values;

			fields = "DEBIT.ACCT.NO," + "DEBIT.CURRENCY," + "CREDIT.CURRENCY," + "ORDERING.CUST:1," + "CREDIT.ACCT.NO,"
					+ "DEBIT.AMOUNT,";
			values = "CAD1100100017623," + "CAD," + "CAD," + "Test Client," + bankingArrangement2 + "," + "10000,";
			paymentID = financialTransaction(CREATE, "Account Transfer", fields, values);

			if (paymentID.contains(ERROR)) {
				stepActual = "Error while Transferring Funds from arrangement: CAD1100100017623 into Banking arrangement: "
						+ bankingArrangement2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			fields = "DEBIT.AMOUNT," + "CREDIT.ACCT.NO," + "DEBIT.ACCT.NO," + "DEBIT.CURRENCY," + "CREDIT.CURRENCY,";
			values = "5000," + bankingArrangement3 + "," + bankingArrangement2 + "," + "CAD," + "CAD,";
			paymentID = financialTransaction(CREATE, "Account Transfer", fields, values);

			if (paymentID.contains(ERROR)) {
				stepActual = "Error while Transferring Funds from Banking arrangement: " + bankingArrangement2
						+ "  into Banking arrangement: " + bankingArrangement3;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters({ "depositProductGroup" })
	public void depositChequeIntoDeposit(final String depositProductGroup) {

		stepDescription = "Deposit Cheque into Deposit arrangement: " + depositArrangement;
		stepExpected = "Cheque into Deposit arrangement: " + depositArrangement + " Deposited successfully";

		if (depositArrangement == null || depositCustomer == null || depositArrangement.contains(ERROR)
				|| depositCustomer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			boolean result = false;
			String fields;
			String values;
			switchToBranch(depositBranch);
			fields = "DEBIT.ACCT.NO," + "CREDIT.AMOUNT," + "CREDIT.CURRENCY,";
			values = remoteAccountRBC + "," + "10000," + "CAD,";

			result = arrangementAction(depositArrangement, depositCustomer, ROLEBASED_BANKING, "Fund Deposit(Transfer)",
					fields, values, customerType);
			if (result) {
				result = authorizeEntity(depositArrangement, ACTIVITY + "," + depositProductGroup);
			}
			if (!result) {
				stepActual = "Error while Depositing Cheque from " + remoteAccountRBC + " into Deposit arrangement: "
						+ depositArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {
				result = arrangementActivity(REVERSE_AUTHORISE, "APPLYPAYMENT ACTIVITY FOR PR.DEPOSIT",
						depositArrangement, depositProductGroup, "", "");
			}
			if (!result) {
				stepActual = "Error while reversing APPLYPAYMENT ACTIVITY FOR PR.DEPOSIT arrangement activity for arrangement: "
						+ depositArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			fields = "DEBIT.ACCT.NO," + "CREDIT.AMOUNT," + "CREDIT.CURRENCY,";
			values = remoteAccountTD + "," + "10000," + "CAD,";

			result = arrangementAction(depositArrangement, depositCustomer, ROLEBASED_BANKING, "Fund Deposit(Transfer)",
					fields, values, customerType);
			if (result) {
				result = authorizeEntity(depositArrangement, ACTIVITY + "," + depositProductGroup);
			}
			if (!result) {
				stepActual = "Error while Depositing Cheque from " + remoteAccountTD + " into Deposit arrangement: "
						+ depositArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {
				result = arrangementActivity(REVERSE_AUTHORISE, "APPLYPAYMENT ACTIVITY FOR PR.DEPOSIT",
						depositArrangement, depositProductGroup, "", "");
			}
			if (!result) {
				stepActual = "Error while reversing APPLYPAYMENT ACTIVITY FOR PR.DEPOSIT arrangement activity for arrangement: "
						+ depositArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	public void collectFeeForDepositFunding() {

		stepDescription = "Collect Fee for Deposit Funding";
		stepExpected = "Fee for Deposit Funding collected successfully";

		String paymentID;
		final String fields;
		final String values;

		if (loginResult) {
			fields = "DEBIT.ACCT.NO," + "DEBIT.CURRENCY," + "CREDIT.CURRENCY," + "ORDERING.CUST:1," + "CREDIT.ACCT.NO,"
					+ "DEBIT.AMOUNT," + "TRANSACTION.TYPE,";
			values = internalAccount + "," + "CAD," + "CAD," + "Test Nominee," + "PL52783," + "25.00," + "ACAD,";
			paymentID = financialTransaction(CREATE, "Remote Cheque Deposit", fields, values);

			if (paymentID.contains(ERROR)) {
				stepActual = "Error while collecting Fee for Deposit Funding";
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

	@Test(priority = 6, enabled = true)
	public void depositEFTIntoBanking() {

		stepDescription = "Deposit EFT into Banking arrangement: " + bankingArrangement1;
		stepExpected = "EFT into Banking arrangement: " + bankingArrangement1 + " Deposited successfully";

		final String fields;
		final String values;
		String paymentId;

		if (bankingArrangement1 == null || bankingArrangement1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			switchToBranch(bankingBranch);
			fields = "CREDIT.ACCOUNT," + "PAYMENT.ORDER.PRODUCT," + "DEBIT.CCY," + "PAYMENT.CURRENCY,"
					+ "PAYMENT.AMOUNT," + "ORDERING.CUSTOMER.SSI,";
			values = bankingArrangement1 + "," + "ACHCREDIT," + "CAD," + "CAD," + "2000," + bankingBeneficiary + ",";
			paymentId = createAdHocPayment(CREATE, EFT, bankingArrangement1, fields, values);

			if (paymentId.contains(ERROR)) {
				stepActual = "Error while Depositing EFT into Banking arrangement: " + bankingArrangement1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
