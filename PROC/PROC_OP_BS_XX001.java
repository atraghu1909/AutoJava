package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PROC_OP_BS_XX001 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.0";
	private String mainArrangement;
	private String customer;
	private String branch = "B2B Branch 817";
	String beneficiaryFields;
	String beneficiaryValues;

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void preCondition(@Optional(PERSONAL_ACCOUNTS) final String productGroup,
			@Optional(PERSONAL_CHEQUING) final String product) {

		stepDescription = "Create " + product + " Arrangement";
		stepExpected = product + " Arrangement created successfully";

		switchToBranch(branch);

		customer = customer(CREATE, PERSONAL, ROLEBASED_BANKING, DefaultVariables.personalCIFFields,
				DefaultVariables.personalCIFValues);

		mainArrangement = dataGenCreateBanking(customer, productGroup, product, CAD, "", "",
				DefaultVariables.bankingFields, DefaultVariables.bankingValues, "");
		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepActual = "Error while creating " + product + " Arrangement: " + mainArrangement;
			softVerify.fail(stepActual);
			stepResult = StatusAs.FAILED;
		}
		softVerify.assertAll();
	}

	@Test(priority = 2, enabled = true)
	public void adHocChequeWave2() {

		stepDescription = "Issue ad-hoc cheque for arrangement: " + mainArrangement;
		stepExpected = "Ad-hoc cheque for arrangement: " + customer + " issued successfully";

		String transactionID;
		String fields;
		String values;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			fields = "DEBIT.AMOUNT," + "CREDIT.ACCT.NO," + "DEBIT.ACCT.NO," + "ORDERING.BANK:1," + "DEBIT.CURRENCY,"
					+ "CREDIT.CURRENCY,";
			values = "10.00," + "CAD11102," + mainArrangement + "," + "B2B," + "CAD," + "CAD,";
			transactionID = financialTransaction(CREATE, "Account Transfer - Transfer between Accounts", fields,
					values);
			if (transactionID.contains(ERROR)) {
				stepActual = "Error while issuing Ad-hoc cheque for arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	public void adHocChequeWave3() {

		stepDescription = "Issue ad-hoc cheque in T24 using payment order for arrangement: " + mainArrangement;
		stepExpected = "Ad-hoc cheque using payment order for arrangement: " + mainArrangement + " issued successfully";

		final String fields;
		final String values;
		String paymentId;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			fields = "DEBIT.ACCOUNT," + "PAYMENT.ORDER.PRODUCT," + "PAYMENT.CURRENCY," + "PAYMENT.AMOUNT,"
					+ "BENEFICIARY.ID," + "ORDERING.CUSTOMER,";
			values = mainArrangement + "," + "CHQADHOC," + "CAD," + "10.00," + "BEN1111111111," + customer + ",";

			paymentId = createAdHocPayment(CREATE, "Cheque", mainArrangement, fields, values);

			if (paymentId.contains(ERROR)) {
				stepActual = "Error while issuing Ad-hoc cheque using payment order for arrangement: " + customer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	public void adHocEFTWave3() {

		stepDescription = "Issue ad-hoc electronic payment for arrangement: " + mainArrangement;
		stepExpected = "Ad-hoc electronic payment for arrangement: " + mainArrangement + " issued successfully";

		final String fields;
		final String values;
		String beneficiary;
		String paymentId;

		if (mainArrangement == null || mainArrangement.contains(ERROR) || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			beneficiaryFields = DefaultVariables.beneficiaryFields + "OWNING.CUSTOMER," + "BEN.CUSTOMER,";
			beneficiaryValues = DefaultVariables.beneficiaryValues + customer + ","
					+ createdCustomers.get(customer).getCustomerName() + ",";

			beneficiary = beneficiaryCode(CREATE, "EFT Client", "", beneficiaryFields, beneficiaryValues);

			fields = "DEBIT.ACCOUNT," + "PAYMENT.ORDER.PRODUCT," + "PAYMENT.CURRENCY," + "PAYMENT.AMOUNT,"
					+ "BENEFICIARY.ID,";
			values = mainArrangement + "," + "ACHDEBIT," + "CAD," + "15.00," + beneficiary + ",";

			paymentId = createAdHocPayment(CREATE, EFT, mainArrangement, fields, values);

			if (paymentId.contains(ERROR)) {
				stepActual = "Error while issuing Ad-hoc electronic payment for arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
