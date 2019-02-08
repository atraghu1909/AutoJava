package testcases.PROC;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;

public class PROC_OP_IL_PR015 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.3";
	private String mainArrangement;
	private String customer;
	private String beneficiary;
	private String branch = "B2B Branch 817";

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void preCondition(final String productGroup, final String product) {

		stepDescription = "Create Current " + product + " Arrangement";
		stepExpected = "Current " + product + " Arrangement created successfully";

		ArrangementData arrangementData;

		if (loginResult) {
			switchToBranch(branch);

			arrangementData = new ArrangementData("mainArrangement", productGroup, product)
								.withDisbursement()
								.withRepayments()
								.build();
			
			mainArrangement = createDefaultArrangement(arrangementData);
			customer = arrangementData.getCustomers();

			if (mainArrangement == null || mainArrangement.contains(ERROR)) {
				stepActual = "Error while creating " + product + " Arrangement: " + mainArrangement;
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
	public void updateBankingInfo(final String productGroup) {

		stepDescription = "Update Banking Info for arrangement: " + mainArrangement;
		stepExpected = "Banking Info for arrangement: " + mainArrangement + " updated successfully";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			final String fields = "BEN.CUSTOMER," + "BANK.SORT.CODE," + "BEN.ACCT.NO,";
			final String values = "abcd efge," + "089972019," + "12345678987,";
			String customerType = PERSONAL;
			String result;
			if (BUSINESS_ACCOUNTS.equals(productGroup) || COMMERCIAL_LOANS.equals(productGroup)) {
				customerType = BUSINESS;
			}

			beneficiary = findBeneficiaryCode(customer, customerType, "EFT Client", ROLEBASED_LENDING);

			result = beneficiaryCode(AMEND, "EFT Client", beneficiary, fields, values);

			if (!beneficiary.equals(result)) {
				stepActual = "Error while updating Banking Info for arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "productGroup" })
	public void changePaymentFrequencyandAmount(final String productGroup) {

		stepDescription = "Change Payment Frequency and Amount for arrangement: " + mainArrangement;
		stepExpected = "Payment Frequency and Amount for arrangement: " + mainArrangement + " changed successfully";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			boolean result;
			List<WebElement> listOfPaymentTypes;

			String fields = "";
			String values = "";

			arrangementActivity(OPEN, "CHANGE ACTIVITY FOR SCHEDULE", mainArrangement, productGroup, "", "");
			listOfPaymentTypes = inputTable.listOfFields("PAYMENT.TYPE", "Schedule");
			for (int i = 1; i <= listOfPaymentTypes.size(); i++) {
				if ("SPECIAL".equals(readData("PAYMENT.TYPE:" + i))) {
					fields += "PAYMENT.FREQ:" + i + ",";
					values += "e0Y e1M e0W e0D e0F,";
					break;
				}
			}

			fields += "GROUP.BILL.TYPE:1," + "GROUP.MIN.AMOUNT:1,";
			values += "INSTALLMENT," + "100,";

			result = multiInputData(fields, values, false) && inputTable.commitAndOverride();

			if (!result) {
				stepActual = "Error while changing Payment Frequency and Amount for arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void cancelPreAuthorizedPayment(final String productGroup, final String product) {

		stepDescription = "Cancel PreAuthorized Payment for arrangement: " + mainArrangement;
		stepExpected = "PreAuthorized Payment for arrangement: " + mainArrangement + " was cancelled successfully";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			final String fields = "PAYIN.SETTLEMENT:1,";
			final String values = "No,";
			boolean result;
			
			switchToBranch(branch);

			result = arrangementActivity(CREATE, "UPDATE ACTIVITY FOR SETTLEMENT", mainArrangement, productGroup,
					fields, values);

			if (!result) {
				stepActual = "Error while cancelling PreAuthorized Payment for arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
