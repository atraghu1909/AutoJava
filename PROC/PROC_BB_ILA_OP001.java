package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.DefaultVariables;

public class PROC_BB_ILA_OP001 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.8";
	private String mainArrangementCheque;
	private String mainArrangementEFT;
	private String customer;
	private String branch = "B2B Branch 817";
	private String creditAmountCheque;
	private String creditAmountEFT;
	private String beneficiary;
	private String paymentOrderScheduled;
	private String paymentOrderAdditional;
	private boolean result;

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void preCondition(final String productGroup, final String product) {

		stepDescription = "Create Delinquent " + product + " Arrangement";
		stepExpected = "Delinquent " + product + " Arrangement created successfully";

		ArrangementData loanEFTData;
		ArrangementData loanChequeData = new ArrangementData("chequeArrangement", productGroup, product)
				.withEffectiveDate("-1m")
				.withSettlement("Beneficiary", "NEW")
				.withDisbursement()
				.build();

		if (loginResult) {

			switchToBranch(branch);

			mainArrangementCheque = createDefaultArrangement(loanChequeData);

			customer = loanChequeData.getCustomers();
			beneficiary = loanChequeData.getSettlementAccount();
			loanEFTData = new ArrangementData("eftArrangement", productGroup, product)
					.withCustomers(customer, createdCustomers.get(customer), "", "100", "100")
					.withEffectiveDate("-1m")
					.withSettlement("Beneficiary", beneficiary)
					.withDisbursement()
					.build();

			mainArrangementEFT = createDefaultArrangement(loanEFTData);

			creditAmountCheque = arrangementBillAmount(mainArrangementCheque, productGroup, product);
			creditAmountEFT = arrangementBillAmount(mainArrangementEFT, productGroup, product);

			if (mainArrangementCheque == null || mainArrangementEFT == null || mainArrangementCheque.contains(ERROR)
					|| mainArrangementEFT.contains(ERROR)) {
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
	@Parameters({ "productGroup" })
	public void paymentViaChequeScheduled(final String productGroup) {

		stepDescription = "Perform Scheduled repayment via cheque for arrangement: " + mainArrangementCheque;
		stepExpected = "Scheduled Repayment via cheque for arrangement: " + mainArrangementCheque
				+ " performed successfully";

		String fields;
		String values;
		String customerType = PERSONAL;

		if (mainArrangementCheque == null || mainArrangementCheque.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			if (BUSINESS_ACCOUNTS.equals(productGroup) || COMMERCIAL_LOANS.equals(productGroup)) {
				customerType = BUSINESS;
			}
			fields = "CREDIT.CURRENCY," + "CREDIT.AMOUNT," + "DEBIT.ACCT.NO," + "CREDIT.THEIR.REF,"
					+ "ORDERING.BANK:1,";
			values = "CAD," + creditAmountCheque + "," + "CAD1100100017817," + "123456789," + mainArrangementCheque
					+ ",";

			result = arrangementAction(mainArrangementCheque, customer, ROLEBASED_LENDING, "Lending Repayment", fields,
					values, customerType);
			if (!result) {
				stepActual = "Error while performing Scheduled Repayment via cheque for arrangement: "
						+ mainArrangementCheque;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "productGroup" })
	public void paymentViaChequeAdditional(final String productGroup) {

		stepDescription = "Perform Additional repayment via cheque for arrangement: " + mainArrangementCheque;
		stepExpected = "Additional Repayment via cheque for arrangement: " + mainArrangementCheque
				+ " performed successfully";

		String fields;
		String values;
		String customerType = PERSONAL;

		if (mainArrangementCheque == null || mainArrangementCheque.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			fields = "CREDIT.CURRENCY," + "CREDIT.AMOUNT," + "DEBIT.ACCT.NO," + "CREDIT.THEIR.REF,"
					+ "ORDERING.BANK:1,";
			values = "CAD," + creditAmountCheque + "," + "CAD1100100017817," + "123456789," + mainArrangementCheque
					+ ",";
			result = arrangementAction(mainArrangementCheque, customer, ROLEBASED_LENDING,
					"Lending Principal Decrease / Repay", fields, values, customerType);
			if (!result) {
				stepActual = "Error while performing Additional Repayment via cheque for arrangement: "
						+ mainArrangementCheque;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	public void paymentViaEFTScheduled() {

		stepDescription = "Perform Ad-Hoc EFT Payment for Arrears for arrangement: " + mainArrangementEFT;
		stepExpected = "Ad-Hoc EFT Payment for Arrears for arrangement: " + mainArrangementEFT
				+ " performed successfully";
		String collateralFields;
		String collateralValues;

		if (mainArrangementEFT == null || mainArrangementEFT.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			collateralFields = "CREDIT.ACCOUNT," + "PAYMENT.ORDER.PRODUCT," + "PAYMENT.CURRENCY," + "PAYMENT.AMOUNT,"
					+ "DEBIT.VALUE.DATE," + "ORDERING.CUSTOMER.SSI,";
			collateralValues = mainArrangementEFT + "," + "ACHCREDIT," + "CAD," + creditAmountEFT + "," + "+0d,"
					+ beneficiary + ",";

			paymentOrderScheduled = createAdHocPayment(CREATE, EFT, mainArrangementEFT, collateralFields,
					collateralValues);
			if (paymentOrderScheduled.contains(ERROR)) {
				stepActual = "Error while performing Ad-Hoc EFT Payment for Arrears for arrangement: "
						+ mainArrangementEFT;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	public void paymentViaEFTAdditional() {

		stepDescription = "Perform Ad-Hoc EFT Payment for Additional Payment for arrangement: " + mainArrangementEFT;
		stepExpected = "Ad-Hoc EFT Payment for Additional Payment for arrangement: " + mainArrangementEFT
				+ " performed successfully";

		final String fields;
		final String values;

		if (mainArrangementEFT == null || mainArrangementEFT.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			fields = "CREDIT.ACCOUNT," + "PAYMENT.ORDER.PRODUCT," + "PAYMENT.CURRENCY," + "PAYMENT.AMOUNT,"
					+ "DEBIT.VALUE.DATE," + "ORDERING.CUSTOMER.SSI,";
			values = mainArrangementEFT + "," + "ACHPRDEC," + "CAD," + creditAmountEFT + "," + "+0d," + beneficiary
					+ ",";

			paymentOrderAdditional = createAdHocPayment(CREATE, EFT, mainArrangementEFT, fields, values);
			if (paymentOrderAdditional.contains(ERROR)) {
				stepActual = "Error while performing Ad-Hoc EFT Payment for Additional Payment for arrangement: "
						+ mainArrangementEFT;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void reversalViaEFTScheduled(final String productGroup, final String product) {

		stepDescription = "Reverse a scheduled repayment via EFT for arrangement: " + mainArrangementEFT;
		stepExpected = "Scheduled repayment via EFT for arrangement: " + mainArrangementEFT + " reversed successfully";

		String inwardPayType;

		if (mainArrangementEFT == null || mainArrangementEFT.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			reverseEntity(paymentOrderScheduled, ADHOC_EFT);
			reverseEntity(paymentOrderAdditional, ADHOC_EFT);

			if (arrangementActivity(VIEW_AUTHORISED_FINANCIAL, "APPLYPAYMENT ACTIVITY FOR PR.REPAYMENT",
					mainArrangementEFT, productGroup, "", "")) {

				switchToPage(LASTPAGE, false);
				inwardPayType = readTable.verifyActivity("Inward Pay Type").getText();
				readTable.returnButton().click();
				if (inwardPayType.equals("PO-" + paymentOrderScheduled)) {
					if (!arrangementActivity(REVERSE_AUTHORISE, "APPLYPAYMENT ACTIVITY FOR PR.REPAYMENT",
							mainArrangementEFT, productGroup, "", "")) {
						stepActual = "Error while reversing a scheduled repayment via EFT for arrangement: "
								+ mainArrangementEFT;
						softVerify.fail(stepActual);
						stepResult = StatusAs.FAILED;
					}
				} else {
					stepActual = "Error while reversing a scheduled repayment via EFT for arrangement: "
							+ mainArrangementEFT;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			} else {
				stepActual = "Error: APPLYPAYMENT ACTIVITY FOR PR.REPAYMENT activity is not present for arrangement: "
						+ mainArrangementEFT;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
