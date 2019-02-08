package testcases.PROC;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.openqa.selenium.support.ui.Select;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.DefaultVariables;

public class PROC_OP_MG_XX005 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.3";

	private String customer;
	private String mainArrangement1;
	private String mainArrangement2;
	private String mainArrangement3;
	private String mainArrangement4;
	private String mainArrangement5;
	private String mainArrangement6;
	private boolean result;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "customerType", "product" })
	public void preCondition(@Optional("B2B Branch 623") final String branch,
			@Optional(PERSONAL) final String customerType, final String product) {

		stepDescription = "Create customer and six current " + product + " arrangements";
		stepExpected = "Customer and six current " + product + " arrangements created successfully";

		ArrangementData arrangementData;

		if (loginResult) {
			switchToBranch(branch);

			arrangementData = new ArrangementData("mainArrangement1", RETAIL_MORTGAGES, product)
					.withEffectiveDate("-45d")
					.withDisbursement()
					.withRepayments()
					.build();
			mainArrangement1 = createDefaultArrangement(arrangementData);
			customer = arrangementData.getCustomers();

			arrangementData = new ArrangementData("mainArrangement", RETAIL_MORTGAGES, product)
					.withEffectiveDate("-45d")
					.withCustomers(customer, createdCustomers.get(customer), "", "100,", "100,")
					.withDisbursement()
					.withRepayments()
					.build();
			mainArrangement2 = createDefaultArrangement(arrangementData);
			mainArrangement3 = createDefaultArrangement(arrangementData);
			mainArrangement5 = createDefaultArrangement(arrangementData);

			arrangementData = new ArrangementData("mainArrangement4", RETAIL_MORTGAGES, product)
					.withEffectiveDate("-45d")
					.withCustomers(customer, createdCustomers.get(customer), "", "100,", "100,")
					.withEscrow(MUNICIPALITY)
					.withDisbursement()
					.withRepayments()
					.build();
			mainArrangement4 = createDefaultArrangement(arrangementData);

			arrangementData = new ArrangementData("mainArrangement6", RETAIL_MORTGAGES, product)
					.withEffectiveDate("-45d")
					.withCustomers(customer, createdCustomers.get(customer), "", "100,", "100,")
					.withPaymentFrequency("e0Y e0M e1W o1D")
					.withSettlement("Banking", "NEW")
					.withDisbursement()
					.withRepayments()
					.build();
			mainArrangement6 = createDefaultArrangement(arrangementData);

			if (mainArrangement1 == null || mainArrangement2 == null || mainArrangement3 == null
					|| mainArrangement4 == null || mainArrangement5 == null || mainArrangement6 == null
					|| mainArrangement1.contains(ERROR) || mainArrangement2.contains(ERROR)
					|| mainArrangement3.contains(ERROR) || mainArrangement4.contains(ERROR)
					|| mainArrangement5.contains(ERROR) || mainArrangement6.contains(ERROR)) {
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
	@Parameters("product")
	public void skipPayment(final String product) throws ParseException {

		stepDescription = "Perform skip payment activity for arrangement: " + mainArrangement1;
		stepExpected = "Skip payment activity for arrangement: " + mainArrangement1 + " performed successfully";

		int nextBillIndex;
		int nextBillRow;
		String nextBillDate;
		String skipPaymentCharge;
		String totalDue;
		String totalCapital;
		int paymentsPerPage;

		SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);

		if (mainArrangement1 == null || mainArrangement1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			findArrangement(AUTHORISED, mainArrangement1, ARRANGEMENT, ROLEBASED_LENDING, RETAIL_MORTGAGES, product,
					CAD);
			switchToPage(LASTPAGE, false);
			versionScreen.linkText(ADDITIONAL_DETAILS, "Schedule").click();
			switchToPage(LASTPAGE, false);
			versionScreen.waitForLoading();
			nextBillIndex = retrieveNextDuePaymentAfter(inputDateFormat.format(new Date())) + 1;

			nextBillDate = enquiryElements.getElementAtCell("Payment Schedule", "AdditionalDetails",
					enquiryElements.getColumnHeaderNumber("Payment Schedule", "Date"), nextBillIndex).getText();

			if (!skipPayment(mainArrangement1, ROLEBASED_LENDING, RETAIL_MORTGAGES, product, "")) {
				stepActual = "Error while performing skip payment activity";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			findArrangement(AUTHORISED, mainArrangement1, ARRANGEMENT, ROLEBASED_LENDING, RETAIL_MORTGAGES, product,
					CAD);
			switchToPage(LASTPAGE, false);
			versionScreen.linkText(ADDITIONAL_DETAILS, "Schedule").click();
			switchToPage(LASTPAGE, false);
			versionScreen.waitForLoading();
			paymentsPerPage = enquiryElements.enquiryResults("Current Page Last");
			nextBillRow = enquiryElements.getRowNumberMatching("Payment Schedule", "Date", nextBillDate);
			totalDue = enquiryElements
					.getElementAtCell("Payment Schedule", "AdditionalDetails",
							enquiryElements.getColumnHeaderNumber("Payment Schedule", "Total Due"), nextBillRow)
					.getText().replaceAll(",", "").trim();
			if (!totalDue.equals("0.00")) {
				stepActual = "Error: Total due of next bill date is not zero: " + totalDue;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			totalCapital = enquiryElements
					.getElementAtCell("Payment Schedule", "AdditionalDetails",
							enquiryElements.getColumnHeaderNumber("Payment Schedule", "Total Cap"), nextBillRow)
					.getText().replaceAll(",", "").trim();
			if (totalCapital.equals("0.00")) {
				stepActual = "Error: Total capital of next bill date is not a positive value: " + totalCapital;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			skipPaymentCharge = enquiryElements
					.getElementAtCell("Payment Schedule", "AdditionalDetails",
							enquiryElements.getColumnHeaderNumber("Payment Schedule", "Charge"), nextBillRow)
					.getText().replaceAll(",", "").trim();
			if (skipPaymentCharge.equals("0.00") || "".equals(skipPaymentCharge)) {
				stepActual = "Error: there was no charge for skip payment";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			for (int i = 1; i < paymentsPerPage; i++) {

				totalDue = enquiryElements
						.getElementAtCell("Payment Schedule", "AdditionalDetails",
								enquiryElements.getColumnHeaderNumber("Payment Schedule", "Total Due"), nextBillRow + i)
						.getText().replaceAll(",", "").trim();
				if (totalDue.equals("0.00")) {
					stepActual = "Error: Total due after the next bill date is not a positive value: " + totalDue;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

				totalCapital = enquiryElements
						.getElementAtCell("Payment Schedule", "AdditionalDetails",
								enquiryElements.getColumnHeaderNumber("Payment Schedule", "Total Cap"), nextBillRow + i)
						.getText().replaceAll(",", "").trim();
				if (!totalCapital.equals("0.00")) {
					stepActual = "Error: Total capital after the next bill date is not zero: " + totalCapital;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 13, enabled = true)
	@Parameters("product")
	public void reverseSkipActivity(final String product) throws ParseException {

		stepDescription = "Reverse skip payment activity";
		stepExpected = "Skip payment activity reversed successfully";

		if (mainArrangement1 == null || mainArrangement4 == null || mainArrangement1.contains(ERROR)
				|| mainArrangement4.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementActivity(REVERSE, "Skip Payment Activity", mainArrangement1, RETAIL_MORTGAGES, "", "")
					&& arrangementActivity(REVERSE, "Skip Payment Activity", mainArrangement4, RETAIL_MORTGAGES, "",
							"");
			if (!result) {
				stepActual = "Error while reversing skip payment activity";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters("product")
	public void holdPayment(final String product) throws ParseException {

		stepDescription = "Perform hold payment activity";
		stepExpected = "Hold payment activity performed released successfully";

		int nextBillIndex;
		String holdPaymentCharge;
		String nextBillDue;
		String mergedBillDate;
		String mergedBillDue;
		int paymentsPerPage;
		int mergedBillIndex;
		double total;

		SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);

		if (mainArrangement2 == null || mainArrangement2.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			findArrangement(AUTHORISED, mainArrangement2, ARRANGEMENT, ROLEBASED_LENDING, RETAIL_MORTGAGES, product,
					CAD);
			switchToPage(LASTPAGE, false);
			versionScreen.linkText(ADDITIONAL_DETAILS, "Schedule").click();
			switchToPage(LASTPAGE, false);
			versionScreen.waitForLoading();
			paymentsPerPage = enquiryElements.enquiryResults("Current Page Last");
			nextBillIndex = retrieveNextDuePaymentAfter(inputDateFormat.format(new Date())) + 1;

			nextBillDue = enquiryElements
					.getElementAtCell("Payment Schedule", "AdditionalDetails",
							enquiryElements.getColumnHeaderNumber("Payment Schedule", "Total Due"), nextBillIndex)
					.getText().replaceAll(",", "");

			if (nextBillIndex == paymentsPerPage) {
				enquiryElements.clickNextPage();
				mergedBillIndex = 1;

			} else {
				mergedBillIndex = nextBillIndex + 1;
			}

			mergedBillDate = enquiryElements
					.getElementAtCell("Payment Schedule", "AdditionalDetails",
							enquiryElements.getColumnHeaderNumber("Payment Schedule", "Date"), mergedBillIndex)
					.getText();
			mergedBillDue = enquiryElements
					.getElementAtCell("Payment Schedule", "AdditionalDetails",
							enquiryElements.getColumnHeaderNumber("Payment Schedule", "Total Due"), mergedBillIndex)
					.getText().replaceAll(",", "");
			total = Double.parseDouble(nextBillDue) + Double.parseDouble(mergedBillDue);

			if (!holdPayment(mainArrangement2, ROLEBASED_LENDING, RETAIL_MORTGAGES, product,
					inputDateFormat.format(new Date()), "5")) {
				stepActual = "Error while performing hold payment activity";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			findArrangement(AUTHORISED, mainArrangement2, ARRANGEMENT, ROLEBASED_LENDING, RETAIL_MORTGAGES, product,
					CAD);
			switchToPage(LASTPAGE, false);
			versionScreen.linkText(ADDITIONAL_DETAILS, "Schedule").click();
			switchToPage(LASTPAGE, false);
			versionScreen.waitForLoading();
			nextBillIndex = retrieveNextDuePaymentAfter(inputDateFormat.format(new Date())) + 1;
			if (!mergedBillDate.equals(enquiryElements
					.getElementAtCell("Payment Schedule", "AdditionalDetails",
							enquiryElements.getColumnHeaderNumber("Payment Schedule", "Date"), nextBillIndex)
					.getText())) {

				stepActual = "Error: new bill date is not replaced with megred bill date";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			if (!enquiryElements
					.getElementAtCell("Payment Schedule", "AdditionalDetails",
							enquiryElements.getColumnHeaderNumber("Payment Schedule", "Total Due"), nextBillIndex)
					.getText().replaceAll(",", "").trim().equals(Double.toString(total))) {

				stepActual = "Total Due is not equal to sum of nextBillDue and mergedBillDue";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			holdPaymentCharge = enquiryElements
					.getElementAtCell("Payment Schedule", "AdditionalDetails",
							enquiryElements.getColumnHeaderNumber("Payment Schedule", "Charge"), nextBillIndex - 1)
					.getText().replaceAll(",", "").trim();
			if (holdPaymentCharge.equals("0.00") || "".equals(holdPaymentCharge)) {
				stepActual = "There was no charge for hold payment";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 14, enabled = true)
	@Parameters("product")
	public void reverseHoldPayment(final String product) throws ParseException {

		stepDescription = "Reverse hold payment activity";
		stepExpected = "Hold payment activity reversed successfully";

		if (mainArrangement2 == null || mainArrangement2.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementActivity(REVERSE, "Hold Payment", mainArrangement2, RETAIL_MORTGAGES, "", "");
			if (!result) {
				stepActual = "Error while reversing hold payment activity";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters("product")
	public void changeFrequencyToWeekly(final String product) throws ParseException {

		stepDescription = "Change frequency to weekly";
		stepExpected = "Frequency changed to weekly successfully";

		int nextBillIndex;
		String nextBillDate;
		String newBillDate;
		String frequencyChangeCharge;
		SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);

		if (mainArrangement3 == null || mainArrangement3.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			findArrangement(AUTHORISED, mainArrangement3, ARRANGEMENT, ROLEBASED_LENDING, RETAIL_MORTGAGES, product,
					CAD);
			switchToPage(LASTPAGE, false);
			versionScreen.linkText(ADDITIONAL_DETAILS, "Schedule").click();
			switchToPage(LASTPAGE, false);
			versionScreen.waitForLoading();
			nextBillIndex = retrieveNextDuePaymentAfter(inputDateFormat.format(new Date())) + 1;

			nextBillDate = enquiryElements.getElementAtCell("Payment Schedule", "AdditionalDetails",
					enquiryElements.getColumnHeaderNumber("Payment Schedule", "Date"), nextBillIndex).getText();

			if (!changePaymentFrequency(mainArrangement3, RETAIL_MORTGAGES, "Weekly", "5")) {
				stepActual = "Error while changing payment frequency to weekly";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			findArrangement(AUTHORISED, mainArrangement3, ARRANGEMENT, ROLEBASED_LENDING, RETAIL_MORTGAGES, product,
					CAD);
			switchToPage(LASTPAGE, false);
			versionScreen.linkText(ADDITIONAL_DETAILS, "Schedule").click();
			versionScreen.waitForLoading();
			nextBillIndex = retrieveNextDuePaymentAfter(inputDateFormat.format(new Date())) + 1;
			newBillDate = enquiryElements.getElementAtCell("Payment Schedule", "AdditionalDetails",
					enquiryElements.getColumnHeaderNumber("Payment Schedule", "Date"), nextBillIndex).getText();
			if (newBillDate.equals(nextBillDate)) {
				stepActual = "Error: next bill date is not changed as per changed frequency";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			frequencyChangeCharge = enquiryElements
					.getElementAtCell("Payment Schedule", "AdditionalDetails",
							enquiryElements.getColumnHeaderNumber("Payment Schedule", "Charge"), nextBillIndex)
					.getText().replaceAll(",", "").trim();
			if (frequencyChangeCharge.equals("0.00") || "".equals(frequencyChangeCharge)) {
				stepActual = "There was no charge for frequency change";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 15, enabled = true)
	@Parameters("product")
	public void reverseChangeFrequency(final String product) throws ParseException {

		stepDescription = "Reverse change frequency activity";
		stepExpected = "Change frequency activity reversed successfully";

		if (mainArrangement3 == null || mainArrangement3.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementActivity(REVERSE, "CHANGE ACTIVITY FOR PRINCIPALINT", mainArrangement3,
					RETAIL_MORTGAGES, "", "")
					&& arrangementActivity(REVERSE, "Change Payment Frequency", mainArrangement3, RETAIL_MORTGAGES, "",
							"");
			if (!result) {
				stepActual = "Error while reversing change frequency activity";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 7, enabled = true)
	@Parameters("product")
	public void skipPaymentWithTaxEscrow(final String product) throws ParseException {

		stepDescription = "Perform skip payment with tax escrow";
		stepExpected = "Skip payment with tax escrow performed successfully";

		int nextBillIndex;
		String nextBillDate;
		String totalDue;
		String totalCapital;
		int paymentsPerPage;
		int nextBillRow;
		SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);

		if (mainArrangement4 == null || mainArrangement4.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			findArrangement(AUTHORISED, mainArrangement4, ARRANGEMENT, ROLEBASED_LENDING, RETAIL_MORTGAGES, product,
					CAD);
			switchToPage(LASTPAGE, false);
			versionScreen.linkText(ADDITIONAL_DETAILS, "Schedule").click();
			switchToPage(LASTPAGE, false);
			versionScreen.waitForLoading();
			nextBillIndex = retrieveNextDuePaymentAfter(inputDateFormat.format(new Date())) + 1;

			nextBillDate = enquiryElements.getElementAtCell("Payment Schedule", "AdditionalDetails",
					enquiryElements.getColumnHeaderNumber("Payment Schedule", "Date"), nextBillIndex).getText();

			if (!skipPayment(mainArrangement4, ROLEBASED_LENDING, RETAIL_MORTGAGES, product, "Escrow")) {
				stepActual = "Error while performing skip payment activity";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			findArrangement(AUTHORISED, mainArrangement4, ARRANGEMENT, ROLEBASED_LENDING, RETAIL_MORTGAGES, product,
					CAD);
			switchToPage(LASTPAGE, false);
			versionScreen.linkText(ADDITIONAL_DETAILS, "Schedule").click();
			switchToPage(LASTPAGE, false);
			versionScreen.waitForLoading();
			paymentsPerPage = enquiryElements.enquiryResults("Current Page Last");
			nextBillRow = enquiryElements.getRowNumberMatching("Payment Schedule", "Date", nextBillDate);
			totalDue = enquiryElements
					.getElementAtCell("Payment Schedule", "AdditionalDetails",
							enquiryElements.getColumnHeaderNumber("Payment Schedule", "Total Due"), nextBillRow)
					.getText().replaceAll(",", "").trim();
			if (!totalDue.equals("0.00")) {
				stepActual = "Error: total due is not equal to zero";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			totalCapital = enquiryElements
					.getElementAtCell("Payment Schedule", "AdditionalDetails",
							enquiryElements.getColumnHeaderNumber("Payment Schedule", "Total Cap"), nextBillRow)
					.getText().replaceAll(",", "").trim();
			if (Integer.parseInt(totalCapital) <= 0) {
				stepActual = "Error: total capital is not a positive value";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			for (int i = 1; i < paymentsPerPage; i++) {

				totalDue = enquiryElements
						.getElementAtCell("Payment Schedule", "AdditionalDetails",
								enquiryElements.getColumnHeaderNumber("Payment Schedule", "Total Due"), nextBillRow + i)
						.getText().replaceAll(",", "").trim();
				if (Integer.parseInt(totalDue) <= 0) {
					stepActual = "Error: Total due after the next bill date is not a positive value: " + totalDue;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

				totalCapital = enquiryElements
						.getElementAtCell("Payment Schedule", "AdditionalDetails",
								enquiryElements.getColumnHeaderNumber("Payment Schedule", "Total Cap"), nextBillRow + i)
						.getText().replaceAll(",", "").trim();
				if (!totalCapital.equals("0.00")) {
					stepActual = "Error: Total capital after the next bill date is not zero: " + totalCapital;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	@Parameters("product")
	public void prepayFeeReceivedViaCheque(final String product) throws ParseException {

		stepDescription = "Reverse hold payment activity";
		stepExpected = "Hold payment activity reversed successfully";

		int nextBillIndex;
		int diffDays;
		String nextBillDate;
		String endOfSkipPeriod;
		Date date;
		Calendar cal;
		SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
		SimpleDateFormat enquiryDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
		Select dropdown;

		if (mainArrangement4 == null || mainArrangement4.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			findArrangement(AUTHORISED, mainArrangement4, ARRANGEMENT, ROLEBASED_LENDING, RETAIL_MORTGAGES, product,
					CAD);
			switchToPage(LASTPAGE, false);
			versionScreen.linkText(ADDITIONAL_DETAILS, "Schedule").click();
			switchToPage(LASTPAGE, false);
			versionScreen.waitForLoading();
			nextBillIndex = retrieveNextDuePaymentAfter(inputDateFormat.format(new Date())) + 1;

			nextBillDate = enquiryElements.getElementAtCell("Payment Schedule", "AdditionalDetails",
					enquiryElements.getColumnHeaderNumber("Payment Schedule", "Date"), nextBillIndex).getText();

			arrangementActivity(OPEN, "UPDATE ACTIVITY FOR ACTIVITY.CHARGES", mainArrangement4, RETAIL_MORTGAGES, "",
					"");
			dropdown = new Select(versionScreen.activityChargesElement("Skip Payment Activity"));
			dropdown.selectByVisibleText("Due");
			result = inputTable.commitAndOverride();

			if (!result) {
				stepActual = "Error while performing UPDATE ACTIVITY FOR ACTIVITY.CHARGES activity for arrangement "
						+ mainArrangement4;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			date = enquiryDateFormat.parse(nextBillDate);
			cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DATE, 1);
			date = cal.getTime();
			endOfSkipPeriod = inputDateFormat.format(date);

			diffDays = (int) ((inputDateFormat.parse(endOfSkipPeriod).getTime()
					- inputDateFormat.parse(inputDateFormat.format(new Date())).getTime()) / (24 * 60 * 60 * 1000));

			arrangementActivity("Open Scheduled +" + diffDays + "d", "UPDATE ACTIVITY FOR ACTIVITY.CHARGES",
					mainArrangement4, RETAIL_MORTGAGES, "", "");
			dropdown = new Select(versionScreen.activityChargesElement("Skip Payment Activity"));
			dropdown.selectByVisibleText("Capitalise");
			result = inputTable.commitAndOverride();

			if (!result) {
				stepActual = "Error while performing UPDATE ACTIVITY FOR ACTIVITY.CHARGES activity for arrangement "
						+ mainArrangement4;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 11, enabled = true)
	@Parameters("product")
	public void validatePrepayFee(final String product) throws ParseException {

		stepDescription = "Validate prepay fee";
		stepExpected = "Prepay fee validated successfully";

		int nextBillIndex;
		String prepayCharge = "";
		SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);

		if (mainArrangement4 == null || mainArrangement4.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			findArrangement(AUTHORISED, mainArrangement4, ARRANGEMENT, ROLEBASED_LENDING, RETAIL_MORTGAGES, product,
					CAD);
			switchToPage(LASTPAGE, false);
			versionScreen.linkText(ADDITIONAL_DETAILS, "Schedule").click();
			switchToPage(LASTPAGE, false);
			versionScreen.waitForLoading();
			nextBillIndex = retrieveNextDuePaymentAfter(inputDateFormat.format(new Date())) + 1;

			prepayCharge = enquiryElements
					.getElementAtCell("Payment Schedule", "AdditionalDetails",
							enquiryElements.getColumnHeaderNumber("Payment Schedule", "Charge"), nextBillIndex)
					.getText().replaceAll(",", "").trim();

			if (!prepayCharge.equals("0.00") || !"".equals(prepayCharge)) {
				stepActual = "Error: charge for skip payment activity is not prepaid";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 8, enabled = true)
	@Parameters("product")
	public void holdPaymentUntilNextBill(final String product) throws ParseException {

		stepDescription = "Hold payment until next bill";
		stepExpected = "Payment was hold successfully until next bill";

		int nextBillIndex;
		String nextBillDue;
		String mergedBillDate;
		String mergedBillDue;
		int paymentsPerPage;
		int mergedBillIndex;
		double total;

		SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);

		if (mainArrangement5 == null || mainArrangement5.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			findArrangement(AUTHORISED, mainArrangement5, ARRANGEMENT, ROLEBASED_LENDING, RETAIL_MORTGAGES, product,
					CAD);
			switchToPage(LASTPAGE, false);
			versionScreen.linkText(ADDITIONAL_DETAILS, "Schedule").click();
			switchToPage(LASTPAGE, false);
			versionScreen.waitForLoading();
			paymentsPerPage = enquiryElements.enquiryResults("Current Page Last");
			nextBillIndex = retrieveNextDuePaymentAfter(inputDateFormat.format(new Date())) + 1;

			nextBillDue = enquiryElements
					.getElementAtCell("Payment Schedule", "AdditionalDetails",
							enquiryElements.getColumnHeaderNumber("Payment Schedule", "Total Due"), nextBillIndex)
					.getText().replaceAll(",", "");

			if (nextBillIndex == paymentsPerPage) {
				enquiryElements.clickNextPage();
				mergedBillIndex = 1;

			} else {
				mergedBillIndex = nextBillIndex + 1;
			}

			mergedBillDate = enquiryElements
					.getElementAtCell("Payment Schedule", "AdditionalDetails",
							enquiryElements.getColumnHeaderNumber("Payment Schedule", "Date"), mergedBillIndex)
					.getText();
			mergedBillDue = enquiryElements
					.getElementAtCell("Payment Schedule", "AdditionalDetails",
							enquiryElements.getColumnHeaderNumber("Payment Schedule", "Total Due"), mergedBillIndex)
					.getText().replaceAll(",", "");
			total = Double.parseDouble(nextBillDue) + Double.parseDouble(mergedBillDue);

			if (!holdPayment(mainArrangement5, ROLEBASED_LENDING, RETAIL_MORTGAGES, product,
					inputDateFormat.format(new Date()), "Until Next Bill")) {
				stepActual = "Error while performing hold payment activity";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			findArrangement(AUTHORISED, mainArrangement5, ARRANGEMENT, ROLEBASED_LENDING, RETAIL_MORTGAGES, product,
					CAD);
			switchToPage(LASTPAGE, false);
			versionScreen.linkText(ADDITIONAL_DETAILS, "Schedule").click();
			switchToPage(LASTPAGE, false);
			versionScreen.waitForLoading();
			nextBillIndex = retrieveNextDuePaymentAfter(inputDateFormat.format(new Date())) + 1;
			if (!mergedBillDate.equals(enquiryElements
					.getElementAtCell("Payment Schedule", "AdditionalDetails",
							enquiryElements.getColumnHeaderNumber("Payment Schedule", "Date"), nextBillIndex)
					.getText())) {
				stepActual = "Megred bill date is not replaced to new bill date after hold payment activity";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			if (!enquiryElements
					.getElementAtCell("Payment Schedule", "AdditionalDetails",
							enquiryElements.getColumnHeaderNumber("Payment Schedule", "Total Due"), nextBillIndex)
					.getText().replaceAll(",", "").trim().equals(Double.toString(total))) {
				stepActual = "Total Due is not equal to sum of nextBillDue and mergedBillDue after performing hold payment";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	@Parameters("product")
	public void waiveFee(final String product) throws ParseException {

		stepDescription = "Waive hold fee";
		stepExpected = "Hold fee waived successfully";

		int nextBillIndex;
		int diffDays;
		String nextBillDate;
		String endOfSkipPeriod;
		String fields = "FIXED.AMOUNT";
		String values = "0.00";
		Date date;
		Calendar cal;
		SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
		SimpleDateFormat enquiryDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);

		if (mainArrangement5 == null || mainArrangement5.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			findArrangement(AUTHORISED, mainArrangement5, ARRANGEMENT, ROLEBASED_LENDING, RETAIL_MORTGAGES, product,
					CAD);
			switchToPage(LASTPAGE, false);
			versionScreen.linkText(ADDITIONAL_DETAILS, "Schedule").click();
			switchToPage(LASTPAGE, false);
			versionScreen.waitForLoading();
			nextBillIndex = retrieveNextDuePaymentAfter(inputDateFormat.format(new Date())) + 1;

			nextBillDate = enquiryElements.getElementAtCell("Payment Schedule", "AdditionalDetails",
					enquiryElements.getColumnHeaderNumber("Payment Schedule", "Date"), nextBillIndex).getText();

			result = arrangementActivity(CREATE_AUTHORISE, "CHANGE ACTIVITY FOR ALHOLDFEE", mainArrangement5,
					RETAIL_MORTGAGES, fields, values);

			if (!result) {
				stepActual = "Error while performing CHANGE ACTIVITY FOR ALHOLDFEE activity for arrangement "
						+ mainArrangement5;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			date = enquiryDateFormat.parse(nextBillDate);
			cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DATE, 1);
			date = cal.getTime();
			endOfSkipPeriod = inputDateFormat.format(date);

			diffDays = (int) ((inputDateFormat.parse(endOfSkipPeriod).getTime()
					- inputDateFormat.parse(inputDateFormat.format(new Date())).getTime()) / (24 * 60 * 60 * 1000));

			result = arrangementActivity("Create Scheduled +" + diffDays + "d", "CHANGE ACTIVITY FOR ALHOLDFEE",
					mainArrangement5, RETAIL_MORTGAGES, "FIXED.AMOUNT", "75.00");

			if (!result) {
				stepActual = "Error while performing CHANGE ACTIVITY FOR ALHOLDFEE activity for arrangement "
						+ mainArrangement5;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

		}
		softVerify.assertAll();
	}

	@Test(priority = 12, enabled = true)
	@Parameters("product")
	public void validateWaivedFee(final String product) throws ParseException {

		stepDescription = "Validate waived fee";
		stepExpected = "Waived fee validated successfully";

		int nextBillIndex;
		String holdPaymentCharge = "";
		SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);

		if (mainArrangement5 == null || mainArrangement5.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			findArrangement(AUTHORISED, mainArrangement5, ARRANGEMENT, ROLEBASED_LENDING, RETAIL_MORTGAGES, product,
					CAD);
			switchToPage(LASTPAGE, false);
			versionScreen.linkText(ADDITIONAL_DETAILS, "Schedule").click();
			switchToPage(LASTPAGE, false);
			versionScreen.waitForLoading();
			nextBillIndex = retrieveNextDuePaymentAfter(inputDateFormat.format(new Date()));

			holdPaymentCharge = enquiryElements
					.getElementAtCell("Payment Schedule", "AdditionalDetails",
							enquiryElements.getColumnHeaderNumber("Payment Schedule", "Charge"), nextBillIndex)
					.getText().replaceAll(",", "").trim();

			if (!"0.00".equals(holdPaymentCharge) || !"".equals(holdPaymentCharge)) {
				stepActual = "Error: charge for the Hold Payment activity is not waived";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 9, enabled = true)
	@Parameters("product")
	public void changeFrequencyToMonthly(final String product) throws ParseException {

		stepDescription = "Change payment frequency to monthly";
		stepExpected = "Payment frequency changed to monthly successfully";

		int nextBillIndex;
		String nextBillDate;
		String newBillDate;
		SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);

		if (mainArrangement6 == null || mainArrangement6.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			findArrangement(AUTHORISED, mainArrangement6, ARRANGEMENT, ROLEBASED_LENDING, RETAIL_MORTGAGES, product,
					CAD);
			switchToPage(LASTPAGE, false);
			versionScreen.linkText(ADDITIONAL_DETAILS, "Schedule").click();
			switchToPage(LASTPAGE, false);
			versionScreen.waitForLoading();
			nextBillIndex = retrieveNextDuePaymentAfter(inputDateFormat.format(new Date())) + 1;

			nextBillDate = enquiryElements.getElementAtCell("Payment Schedule", "AdditionalDetails",
					enquiryElements.getColumnHeaderNumber("Payment Schedule", "Date"), nextBillIndex).getText();

			if (!changePaymentFrequency(mainArrangement6, RETAIL_MORTGAGES, "Monthly", "12")) {
				stepActual = "Error while changing payment frequency to weekly";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			findArrangement(AUTHORISED, mainArrangement6, ARRANGEMENT, ROLEBASED_LENDING, RETAIL_MORTGAGES, product,
					CAD);
			switchToPage(LASTPAGE, false);
			versionScreen.linkText(ADDITIONAL_DETAILS, "Schedule").click();
			switchToPage(LASTPAGE, false);
			versionScreen.waitForLoading();
			nextBillIndex = retrieveNextDuePaymentAfter(inputDateFormat.format(new Date())) + 1;
			newBillDate = enquiryElements.getElementAtCell("Payment Schedule", "AdditionalDetails",
					enquiryElements.getColumnHeaderNumber("Payment Schedule", "Date"), nextBillIndex).getText();
			if (newBillDate.equals(nextBillDate) || !newBillDate.split("\\s+")[0].equals("12")) {
				stepActual = "Error while validating changed payment frequency to monthly";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 10, enabled = true)
	@Parameters("product")
	public void refundFeeReceivedViaEFT(final String product) throws ParseException {

		stepDescription = "Refund fee received via EFT";
		stepExpected = "Fee received via EFT refunded successfully";

		String fields = "PAYMENT.ORDER.PRODUCT," + "CREDIT.ACCOUNT," + "PAYMENT.AMOUNT," + "PAYMENT.CURRENCY,"
				+ "ORDERING.CUSTOMER.SSI,";
		String values = "ACHRDPR," + mainArrangement6 + "," + "75.00," + "CAD,"
				+ beneficiaryCode(CREATE, "", "",
						DefaultVariables.beneficiaryFields + "OWNING.CUSTOMER," + "BEN.CUSTOMER,",
						DefaultVariables.beneficiaryValues + customer + ","
								+ createdCustomers.get(customer).getCustomerName() + ",")
				+ ",";

		if (mainArrangement6 == null || mainArrangement6.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			if (createAdHocPayment(CREATE, EFT, mainArrangement6, fields, values).contains(ERROR)) {
				stepActual = "Error while refunding fee received via EFT";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
