package testcases.PROC;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;

public class PROC_OP_IL_PR102 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.0";
	private String mainArrangement;
	private String nextBillDate;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch ", "customerType", "product", "productGroup" })
	public void preCondition(@Optional("B2B Branch 817") final String branch,
			@Optional(PERSONAL) final String customerType, final String product,
			@Optional(PERSONAL_LOANS) final String productGroup) {

		stepDescription = "Create " + product
				+ " arrangement and verify that Total Due is not 0.00 for next payment date";
		stepExpected = product
				+ " arrangement created and Total Due is not 0.00 for next payment date verified successfully";

		final SimpleDateFormat enquiryDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
		final SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
		String nextBillDue;
		int nextBillDateIndex;

		if (loginResult) {
			switchToBranch(branch);

			ArrangementData loanData = new ArrangementData("loanArrangement", productGroup, product)
					.withEffectiveDate("-1m").withDisbursement().withRepayments().build();

			mainArrangement = createDefaultArrangement(loanData);

			if (mainArrangement == null || mainArrangement.contains(ERROR)) {
				stepActual = "Error while creating " + product + " arrangement";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {
				findArrangement(AUTHORISED, mainArrangement, ARRANGEMENT, ROLEBASED_LENDING, productGroup, product,
						CAD);
				switchToPage(LASTPAGE, false);
				versionScreen.linkText(ADDITIONAL_DETAILS, "Schedule").click();
				versionScreen.waitForLoading();

				nextBillDateIndex = retrieveNextDuePaymentAfter(inputDateFormat.format(new Date())) + 1;
				nextBillDue = enquiryElements
						.getElementAtCell("Payment Schedule", "AdditionalDetails",
								enquiryElements.getColumnHeaderNumber("Payment Schedule", "Total Due"), nextBillDateIndex + 1)
						.getText().replaceAll(",", "");
				
				try {
					nextBillDate = inputDateFormat.format(enquiryDateFormat
							.parse(enquiryElements.getElementAtCell("Payment Schedule", "AdditionalDetails",
									enquiryElements.getColumnHeaderNumber("Payment Schedule", "Date"),
									nextBillDateIndex).getText()));
				} catch (ParseException e) {
					Reporter.log(e.getMessage(), debugMode);
					stepActual = "Pre-Condition failed because Payment Date to be skipped could not be parsed";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

				if (nextBillDateIndex <= 0) {
					stepActual = "Pre-Condition failed because Payment to be skipped could not be found";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				} else {
					stepActual = "Payment to be skipped is $" + nextBillDue + " on " + nextBillDate;
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
	@Parameters({ "product", "productGroup" })
	public void skipPayment(final String product, @Optional(PERSONAL_LOANS) final String productGroup)
			throws ParseException {

		stepDescription = "Perform Skip Payment activity and verify that Next Payment Date is omitted successfully";
		stepExpected = "Skip Payment activty performed successfully and verified that Next Payment Date is omitted";

		final String skippedValue;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			if (skipPayment(mainArrangement, ROLEBASED_LENDING, productGroup, product, "")) {
				findArrangement(AUTHORISED, mainArrangement, ARRANGEMENT, ROLEBASED_LENDING, productGroup, product,
						CAD);
				versionScreen.linkText(ADDITIONAL_DETAILS, "Schedule").click();
				switchToPage(LASTPAGE, false);
				try {
					skippedValue = versionScreen.valuesDisplayed(nextBillDate).getText();
					if (skippedValue.equals(nextBillDate)) {
						stepActual = "Next Payment Date is not omitted after performing Skip Payment Activity";
						softVerify.fail(stepActual);
						stepResult = StatusAs.FAILED;
					}
				} catch (NoSuchElementException e) {
					Reporter.log("Next Payment Date is omitted after performing Skip Payment Activity", debugMode);
				}
			} else {
				stepActual = "Error while performing Skip Payment Activity";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
