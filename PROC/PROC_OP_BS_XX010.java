package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.DefaultVariables;

public class PROC_OP_BS_XX010 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.5";
	private String bankingArrangement;
	private String locArrangement;
	private String customer;
	private String beneficiary;
	private String fields;
	private String values;
	private boolean result;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "customerType", "bankingProductGroup", "bankingProduct", "locProductGroup", "locProduct" })
	public void preCondition(@Optional("B2B Branch 623") final String branch,
			@Optional(PERSONAL) final String customerType,
			@Optional(PERSONAL_ACCOUNTS) final String bankingProductGroup,
			@Optional(HISA_PERSONAL) final String bankingProduct,
			@Optional(RETAIL_MORTGAGES) final String locProductGroup, @Optional(HELOC) final String locProduct) {

		stepDescription = "Create " + customerType + " customer, beneficiary for that customer, " + bankingProduct
				+ " arrangement and " + locProduct + " arrangement. Also, Request and Issue cheque book";
		stepExpected = customerType + " customer, beneficiary for that customer, " + bankingProduct
				+ " arrangement and " + locProduct + " arrangement created successfully";

		String beneficiaryFields;
		String beneficiaryValues;

		if (loginResult) {
			switchToBranch(branch);

			customer = createDefaultCustomer(customerType, "", ROLEBASED_BANKING);
			if (customer == null || customer.contains(ERROR)) {
				stepActual = "Error while creating customer";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			beneficiaryFields = DefaultVariables.beneficiaryFields + "OWNING.CUSTOMER," + "BEN.CUSTOMER,";
			beneficiaryValues = DefaultVariables.beneficiaryValues + customer + ","
					+ createdCustomers.get(customer).getCustomerName() + ",";
			beneficiary = beneficiaryCode(CREATE, "EFT Client", "", beneficiaryFields, beneficiaryValues);
			if (beneficiary == null || beneficiary.contains(ERROR)) {
				stepActual = "Error while creating " + bankingProduct + " arrangement";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			bankingArrangement = createDefaultArrangement(bankingProductGroup, bankingProduct, ROLEBASED_BANKING,
					customer, "+0d");
			if (bankingArrangement == null || bankingArrangement.contains(ERROR)) {
				stepActual = "Error while creating " + bankingProduct + " arrangement";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			fields = "CHQ.NO.START,";
			values = "1,";
			result = bankingCheque("Request Book", CHEQUE, bankingArrangement, bankingProduct, "", "")
					&& bankingCheque("Issue Book", CHEQUE, bankingArrangement, bankingProduct, fields, values);
			if (!result) {
				stepActual = "Error while requesting and issuing cheque book";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			locArrangement = createDefaultArrangement(locProductGroup, locProduct, ROLEBASED_LENDING, customer, "+0d");
			if (locArrangement == null || locArrangement.contains(ERROR)) {
				stepActual = "Error while creating " + locProduct + " arrangement";
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
	@Parameters("bankingProduct")
	public void stopChequeBanking(@Optional(HISA_PERSONAL) final String bankingProduct) {

		stepDescription = "Stop cheque for banking arrangement: " + bankingArrangement;
		stepExpected = "Cheque for banking arrangement: " + bankingArrangement + " stopped successfully";

		if (bankingArrangement == null || bankingArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			fields = "PAYM.STOP.TYPE:1," + "CHEQUE.TYPE:1," + "FIRST.CHEQUE.NO:1," + "LAST.CHEQUE.NO:1,"
					+ "BENEFICIARY:1," + "Charges and Taxes#WAIVE.CHARGE:1," + "Charges and Taxes#CHARGE.CODE:1:1,"
					+ "Charges and Taxes#CHG.AMOUNT:1:1";
			values = "1," + "CURR," + "1," + "3," + beneficiary + "," + "No," + "STOPCHQ," + "5.00";
			result = bankingCheque("Stop", CHEQUE, bankingArrangement, bankingProduct, fields, values);

			if (!result) {
				stepActual = "Error while stopping cheque for banking arrangement: " + bankingArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters("bankingProduct")
	public void revokeStopChequeBanking(@Optional(HISA_PERSONAL) final String bankingProduct) {

		stepDescription = "Revoke stop cheque for banking arrangement: " + bankingArrangement;
		stepExpected = "Stopped cheque for banking arrangement: " + bankingArrangement + " revoked successfully";

		if (bankingArrangement == null || bankingArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			fields = "MOD.PS.CHQ.NO:1," + "MOD.CHQ.TYPE:1";
			values = "2," + "CURR";
			result = bankingCheque("Revoke Stop", CHEQUE, bankingArrangement, bankingProduct, fields, values);

			if (!result) {
				stepActual = "Error while revoking stopped cheque for banking arrangement: " + bankingArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters("bankingProduct")
	public void stopAmountBanking(@Optional(HISA_PERSONAL) final String bankingProduct) {

		stepDescription = "Stop amount for banking arrangement: " + bankingArrangement;
		stepExpected = "Amount for banking arrangement: " + bankingArrangement + " stopped successfully";

		if (bankingArrangement == null || bankingArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			fields = "PAYM.STOP.TYPE:1," + "AMOUNT.FROM:1," + "AMOUNT.TO:1," + "BENEFICIARY:1,"
					+ "Charges and Taxes#WAIVE.CHARGE:1,";
			values = "1," + "100.00," + "100.00," + beneficiary + "," + "Yes,";
			result = bankingCheque("Stop", "Amount", bankingArrangement, bankingProduct, fields, values);

			if (!result) {
				stepActual = "Error while stopping amount for banking arrangement: " + bankingArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	@Parameters("bankingProduct")
	public void revokeStopAmountBanking(@Optional(HISA_PERSONAL) final String bankingProduct) {

		stepDescription = "Revoke stop amount for banking arrangement: " + bankingArrangement;
		stepExpected = "Stop amount for banking arrangement: " + bankingArrangement + " revoked successfully";

		if (bankingArrangement == null || bankingArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			fields = "STOP.END.FLAG:1," + "MOD.PS.CHQ.NO:1," + "MOD.CHQ.TYPE:1,";
			values = "Yes," + "," + ",";
			result = bankingCheque("Revoke Stop", "Amount", bankingArrangement, bankingProduct, fields, values);

			if (!result) {
				stepActual = "Error while revoking stop amount for banking arrangement: " + bankingArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	@Parameters("locProduct")
	public void stopChequeLOC(@Optional(HELOC) final String locProduct) {

		stepDescription = "Stop cheque for LOC arrangement: " + locArrangement;
		stepExpected = "Cheque for LOC arrangement: " + locArrangement + " stopped successfully";

		if (locArrangement == null || locArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			fields = "PAYM.STOP.TYPE:1," + "FIRST.CHEQUE.NO:1," + "LAST.CHEQUE.NO:1," + "BENEFICIARY:1,"
					+ "CHEQUE.TYPE:1,";
			values = "1," + "1," + "3," + beneficiary + "," + "CURR,";
			result = bankingCheque("Stop", CHEQUE, locArrangement, locProduct, fields, values);

			if (!result) {
				stepActual = "Error while stopping cheque for LOC arrangement: " + locArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 7, enabled = true)
	@Parameters("locProduct")
	public void revokeStopChequeLOC(@Optional(HELOC) final String locProduct) {

		stepDescription = "Revoke stop cheque for LOC arrangement: " + locArrangement;
		stepExpected = "Stopped cheque for LOC arrangement: " + locArrangement + " revoked successfully";

		if (locArrangement == null || locArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			fields = "MOD.PS.CHQ.NO:1," + "MOD.CHQ.TYPE:1";
			values = "2," + "CURR";
			result = bankingCheque("Revoke Stop", CHEQUE, locArrangement, locProduct, fields, values);

			if (!result) {
				stepActual = "Error while revoking stopped cheque for LOC arrangement: " + locArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 8, enabled = true)
	@Parameters("locProduct")
	public void stopAmountLOC(@Optional(HELOC) final String locProduct) {

		stepDescription = "Stop amount for LOC arrangement: " + locArrangement;
		stepExpected = "Amount for LOC arrangement: " + locArrangement + " stopped successfully";

		if (locArrangement == null || locArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			fields = "PAYM.STOP.TYPE:1," + "AMOUNT.FROM:1," + "AMOUNT.TO:1," + "BENEFICIARY:1,";
			values = "1," + "100.00," + "100.00," + beneficiary + ",";
			result = bankingCheque("Stop", "Amount", locArrangement, locProduct, fields, values);

			if (!result) {
				stepActual = "Error while stopping amount for LOC arrangement: " + locArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 9, enabled = true)
	@Parameters("locProduct")
	public void revokeStopAmountLOC(@Optional(HELOC) final String locProduct) {

		stepDescription = "Revoke stop amount for LOC arrangement: " + locArrangement;
		stepExpected = "Stopped cheque for LOC arrangement: " + locArrangement + " revoked successfully";

		if (locArrangement == null || locArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			fields = "STOP.END.FLAG:1," + "MOD.PS.CHQ.NO:1," + "MOD.CHQ.TYPE:1,";
			values = "Yes," + "," + ",";
			result = bankingCheque("Revoke Stop", "Amount", locArrangement, locProduct, fields, values);

			if (!result) {
				stepActual = "Error while revoking stopped cheque for LOC arrangement: " + locArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
