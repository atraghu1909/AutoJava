package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.DefaultVariables;

public class PROC_OP_IL_PR091 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.5";
	private String fixMainArrangement;
	private String varMainArrangement;
	private String varCustomer;
	private String fixCustomer;
	private String fixDischargeRequest;
	private String varDischargeRequest;
	private String customerType;
	private String branch = "B2B Branch 817";
	private String beneficiary;

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "fixProduct", "varProduct" })
	public void preCondition(final String productGroup, final String fixProduct, final String varProduct) {

		final ArrangementData varArrangementData = new ArrangementData("varArrangement", productGroup, varProduct)
													.withEffectiveDate("-1m")
													.withDisbursement()
													.withRepayments()
													.build();
		
		final ArrangementData fixArrangementData = new ArrangementData("fixArrangement", productGroup, fixProduct)
													.withEffectiveDate("-1m")
													.withSettlement("Beneficiary", "NEW")
													.withDisbursement()
													.withRepayments()
													.build();

		stepDescription = "Create " + varProduct + " and " + fixProduct + " Arrangements";
		stepExpected = varProduct + " and " + fixProduct + " Arrangements created successfully";

		if (loginResult) {
			switchToBranch(branch);

			customerType = DefaultVariables.productGroupCustomerType.get(productGroup);
			
			varMainArrangement = createDefaultArrangement(varArrangementData);
			varCustomer = varArrangementData.getCustomers();

			fixMainArrangement = createDefaultArrangement(fixArrangementData);
			fixCustomer = fixArrangementData.getCustomers();
			beneficiary = fixArrangementData.getSettlementAccount();
			
			if (varMainArrangement == null || varMainArrangement.contains(ERROR)) {
				stepActual = "Error while creating " + varProduct + " Arrangement: " + varMainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else if (fixMainArrangement == null || fixMainArrangement.contains(ERROR)) {
				stepActual = "Error while creating " + fixProduct + " Arrangement: " + fixMainArrangement;
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
	@Parameters({ "productGroup", "fixProduct", "varProduct" })
	public void requestPayout(final String productGroup, final String fixProduct, final String varProduct) {

		stepDescription = "Request Payout and Save Payout values for arrangements: " + fixMainArrangement + " And "
				+ varMainArrangement;
		stepExpected = "Request Payout performed successfully and Payout values for arrangements: " + fixMainArrangement
				+ " And " + varMainArrangement + " saved successfully";

		if (varMainArrangement == null || fixMainArrangement == null || varMainArrangement.contains(ERROR)
				|| fixMainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			fixDischargeRequest = generateStatement("", fixCustomer, productGroup, fixProduct, "", "");
			stepActual = fixDischargeRequest + " for Arrangement: " + fixProduct;
			varDischargeRequest = generateStatement("", varCustomer, productGroup, varProduct, "", "");
			stepActual += " " + varDischargeRequest + " for Arrangement: " + varProduct;
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "productGroup" })
	public void collateralReleaseDeficientVariableRateAccount(final String productGroup) {

		stepDescription = "Verify that the collateral has been redeemed and add an exception for Deficient Variable Rate account: "
				+ varMainArrangement;
		stepExpected = "Collateral has been redeemed successfully and exception for Deficient Variable Rate account: "
				+ varMainArrangement + " has been added";

		boolean result;
		String payoffAmount;
		String fields;
		String values;

		if (varMainArrangement == null || varMainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else if (varDischargeRequest == null) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Request Payout step failed";
			throw new SkipException(stepActual);
		} else {

			payoffAmount = Double.toString(Double.parseDouble(varDischargeRequest.split("&")[0]) - 1000.00);

			fields = "CREDIT.CURRENCY," + "CREDIT.AMOUNT," + "DEBIT.ACCT.NO," + "ORDERING.BANK:1,";
			values = "CAD," + payoffAmount + "," + "CAD1100100017817," + "RBC,";

			result = arrangementAction(varMainArrangement, varCustomer, ROLEBASED_LENDING, "Lending Repayment", fields,
					values, customerType);

			if (result) {
				fields = "L.LOAN.TYPE,";
				values = "Def 00 Nmc Pi Var,";

				result = arrangementActivity(CREATE_AUTHORISE, "UPDATE ACTIVITY FOR ACCOUNT", varMainArrangement,
						productGroup, fields, values);
			}
			if (!result) {
				stepActual = "Error while adding exception for Deficient Variable Rate account: " + varMainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters({ "productGroup" })
	public void collateralReleaseDeficientFixedRateInvestment(final String productGroup) {

		stepDescription = "Verify that the collateral has been redeemed and add an exception for Deficient Fixed Rate account: "
				+ fixMainArrangement;
		stepExpected = "Collateral has been redeemed successfully and exception for Deficient Fixed Rate account: "
				+ fixMainArrangement + " has been added";

		boolean result;
		String payoffAmount;
		String fields;
		String values;

		if (fixMainArrangement == null || fixMainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else if (fixDischargeRequest == null) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Request Payout step failed";
			throw new SkipException(stepActual);
		} else {

			payoffAmount = Double.toString(Double.parseDouble(fixDischargeRequest.split("&")[0]) - 1000.00);

			fields = "CREDIT.CURRENCY," + "CREDIT.AMOUNT," + "DEBIT.ACCT.NO," + "ORDERING.BANK:1,";
			values = "CAD," + payoffAmount + "," + "CAD1100100017817," + "RBC,";

			result = arrangementAction(fixMainArrangement, fixCustomer, ROLEBASED_LENDING, "Lending Repayment", fields,
					values, customerType);

			if (result) {
				fields = "L.LOAN.TYPE,";
				values = "Def 00 Nmc Pi Var,";

				result = arrangementActivity(CREATE_AUTHORISE, "UPDATE ACTIVITY FOR ACCOUNT", fixMainArrangement,
						productGroup, fields, values);
			}
			if (!result) {
				stepActual = "Error while adding exception for Deficient Fixed Rate account: " + fixMainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	@Parameters({ "productGroup", "fixProduct", "varProduct" })
	public void payoffActivityViaCheque(final String productGroup, final String fixProduct, final String varProduct) {

		stepDescription = "Perform Loan PayOff via Cheque for arrangement: " + varMainArrangement;
		stepExpected = "Loan PayOff via Cheque for arrangement: " + varMainArrangement + " performed successfully";

		boolean result;
		String payoffAmount;
		String fields;
		String values;

		if (varMainArrangement == null || varMainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else if (varDischargeRequest == null) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Request Payout step failed";
			throw new SkipException(stepActual);
		} else {

			arrangementActivity(VIEW_AUTHORISED, "ISSUE ACTIVITY FOR PAYOFF", varMainArrangement, productGroup, "", "");
			inputTable.returnButton().click();
			payoffAmount = Double.toString(Double.parseDouble(varDischargeRequest.split("&")[0]) + 2000.00);

			fields = "CREDIT.CURRENCY," + "CREDIT.AMOUNT," + "DEBIT.ACCT.NO," + "CREDIT.THEIR.REF,"
					+ "ORDERING.BANK:1,";
			values = "CAD," + payoffAmount + "," + "CAD1100100017817," + "CHQ#123forPayoff," + "RBC,";

			result = arrangementAction(varMainArrangement, varCustomer, ROLEBASED_LENDING, "Lending Full Payout",
					fields, values, customerType);
			if (!result) {
				stepActual = "Error while performing Loan PayOff via Cheque for arrangement: " + varMainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	@Parameters({ "productGroup" })
	public void payoffActivityViaEFT(final String productGroup) {

		stepDescription = "Perform Loan PayOff via EFT for arrangement: " + fixMainArrangement;
		stepExpected = "Loan PayOff via EFT for arrangement: " + fixMainArrangement + " performed successfully";

		String payoffAmount;
		String paymentID;
		String fields;
		String values;

		if (fixMainArrangement == null || fixMainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else if (fixDischargeRequest == null) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Request Payout step failed";
			throw new SkipException(stepActual);
		} else {

			beneficiary = findBeneficiaryCode(fixCustomer, customerType, "EFT Client", ROLEBASED_LENDING);
			arrangementActivity(VIEW_AUTHORISED, "ISSUE ACTIVITY FOR PAYOFF", fixMainArrangement, productGroup, "", "");
			inputTable.returnButton().click();
			payoffAmount = Double.toString(Double.parseDouble(fixDischargeRequest.split("&")[0]) + 2000.00);

			fields = "CREDIT.ACCOUNT," + "PAYMENT.ORDER.PRODUCT," + "DEBIT.CCY," + "PAYMENT.CURRENCY,"
					+ "PAYMENT.AMOUNT," + "DEBIT.VALUE.DATE," + "ORDERING.CUSTOMER.SSI,";
			values = fixMainArrangement + "," + "ACHPYOFF," + "CAD," + "CAD," + payoffAmount + "," + "+0d,"
					+ beneficiary + ",";

			paymentID = createAdHocPayment(CREATE, EFT, fixMainArrangement, fields, values);

			if (paymentID.contains(ERROR)) {
				stepActual = "Error while performing Loan PayOff via EFT for arrangement: " + fixMainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 7, enabled = true)
	@Parameters({ "productGroup" })
	public void refundExcessFundsViaCheque(final String productGroup) {

		stepDescription = "Refund Excess Funds via Cheque for arrangement: " + varMainArrangement;
		stepExpected = "Excess Funds via Cheque for arrangement: " + varMainArrangement + " refunded successfully";

		if (varMainArrangement == null || varMainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			boolean result;

			result = refundExcessFunds(varCustomer, varMainArrangement, customerType, productGroup, "Cheque",
					ROLEBASED_LENDING);

			if (!result) {
				stepActual = "Error while refunding Excess Funds via Cheque for arrangement: " + varMainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 8, enabled = true)
	@Parameters({ "productGroup" })
	public void refundExcessFundsViaEFT(final String productGroup) {

		stepDescription = "Refund Excess Funds via EFT for arrangement: " + fixMainArrangement;
		stepExpected = "Excess Funds via EFT for arrangement: " + fixMainArrangement + " refunded successfully";

		if (fixMainArrangement == null || fixMainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			boolean result;

			result = refundExcessFunds(fixCustomer, fixMainArrangement, customerType, productGroup, "EFT",
					ROLEBASED_LENDING);

			if (!result) {
				stepActual = "Error while refunding Excess Funds via EFT for arrangement: " + fixMainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
