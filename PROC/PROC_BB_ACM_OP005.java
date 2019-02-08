package testcases.PROC;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

public class PROC_BB_ACM_OP005 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 1.1";
	private String customer;
	private String mainArrangement;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "productGroup", "product" })
	public void preCondition(@Optional("B2B Branch 817") final String branch,
			@Optional(PERSONAL_LOANS) final String productGroup, final String product) {

		stepDescription = "Create a Delinquent " + product + " arrangement";
		stepExpected = "Delinquent Arrangement created successfully";

		if (loginResult) {

			switchToBranch(branch);

			final ArrangementData arrangementData = new ArrangementData("mainArrangement", productGroup, product)
					.withEffectiveDate("-58d")
					.withTerm("1Y")
					.withDisbursement()
					.build();

			mainArrangement = createDefaultArrangement(arrangementData);
			customer = arrangementData.getCustomers();

			if (customer == null || mainArrangement == null || customer.contains(ERROR)
					|| mainArrangement.contains(ERROR)) {
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
	@Parameters({ "productGroup", "product" })
	public void clearArrears(@Optional(PERSONAL_LOANS) final String productGroup, final String product) {

		stepDescription = "Clear the Arrears on arrangement " + mainArrangement;
		stepExpected = "Arrears cleared successfully";

		final SimpleDateFormat formatArrangementOverview = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
		final SimpleDateFormat formatInputElement = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
		final Calendar cal = Calendar.getInstance();
		Date date;
		String activityStart;
		String fields;
		String lastSchedPayDate;
		String overdueStatusBefore;
		String scheduleStart;
		String totalDelinquentBalance;
		String values;
		boolean delinquentBefore;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			arrangementAction(mainArrangement, customer, ROLEBASED_LENDING, LOAN_OVERVIEW, "", "", PERSONAL);
			switchToPage("Customer Details", false);
			versionScreen.waitForLoading();

			try {
				delinquentBefore = versionScreen.delinquentIcon(mainArrangement).getAttribute("innerHTML")
						.contains("delinquent");
				switchToPage(LASTPAGE, false);
				overdueStatusBefore = versionScreen.headerElement("FinancialSummary", "3", "2").getText();
				totalDelinquentBalance = versionScreen.labelElement("Total Delinquent Balance").getText();

				if (delinquentBefore
						&& (overdueStatusBefore.contains("Past Due") || overdueStatusBefore.contains("Delinquent"))
						&& !"".equals(totalDelinquentBalance)) {
					versionScreen.linkText(ADDITIONAL_DETAILS, "Bills").click();
					lastSchedPayDate = versionScreen.paymentElement("Outstanding Repayments", "Due Date", "1")
							.getText();

					try {
						date = formatArrangementOverview.parse(lastSchedPayDate);
						scheduleStart = formatInputElement.format(date);
						cal.setTime(date);
						cal.add(Calendar.DATE, -1);
						activityStart = formatInputElement.format(cal.getTime());
					} catch (ParseException e) {
						Reporter.log(e.getMessage(), false);
						stepActual = "There was an unexpected error while parsing dates on this step";
						softVerify.fail(stepActual);
						stepResult = StatusAs.FAILED;
						activityStart = "";
						scheduleStart = "";
					}

					fields = "HOL.PAYMENT.TYPE:1," + "HOL.START.DATE:1," + "HOL.NUM.PAYMENTS:1,";
					values = "SPECIAL.PI," + scheduleStart + ",1,";
					arrangementActivity("Create Scheduled " + activityStart, "DEFINE.HOLIDAY ACTIVITY FOR SCHEDULE",
							mainArrangement, productGroup, fields, values);

				} else {
					stepActual = "Arrangement " + mainArrangement + " was not properly setup as delinquent"
							+ System.lineSeparator() + "Delinquent Icon displayed? " + String.valueOf(delinquentBefore)
							+ System.lineSeparator() + "Overdue Status: " + overdueStatusBefore + System.lineSeparator()
							+ "Total Delinquent Balance: " + totalDelinquentBalance;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

			} catch (NoSuchElementException e) {
				Reporter.log(e.getMessage(), false);
				stepActual = "There was an unexpected error while executing this step";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}

		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void verifyClearedArrears(@Optional(PERSONAL_LOANS) final String productGroup, final String product) {
		
		stepDescription = "Verify if the Delinquency was cleared";
		stepExpected = "Delinquency was cleared from the arrangement";

		String overdueStatusAfter;
		String outstandingRepayments;
		boolean delinquentAfter;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			arrangementAction(mainArrangement, customer, ROLEBASED_LENDING, LOAN_OVERVIEW, "", "", PERSONAL);
			switchToPage("Customer Details", false);

			try {
				delinquentAfter = versionScreen.delinquentIcon(mainArrangement).getAttribute("innerHTML")
						.contains("delinquent");
				if (delinquentAfter) {
					stepActual = "Arrangement " + mainArrangement + " still has the delinquent icon";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

				switchToPage(LASTPAGE, false);
				versionScreen.waitForLoading();
				overdueStatusAfter = versionScreen.headerElement("FinancialSummary", "3", "2").getText();
				if (overdueStatusAfter.contains("Past Due") || overdueStatusAfter.contains("Delinquent")) {
					stepActual += System.lineSeparator();
					stepActual += "Arrangement " + mainArrangement + " is still marked as " + overdueStatusAfter;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

				versionScreen.linkText(ADDITIONAL_DETAILS, "Bills").click();
				versionScreen.waitForLoading();
				outstandingRepayments = versionScreen.tdElement("id", "DueOS", "1").getAttribute("textContent")
						.replaceAll("\n+", "\n");
				if (!"".equals(outstandingRepayments)) {
					stepActual += System.lineSeparator();
					stepActual += "Arrangement " + mainArrangement + " still has Outstanding Repayments:"
							+ System.lineSeparator() + outstandingRepayments;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

				versionScreen.linkText(ADDITIONAL_DETAILS, "Overdue").click();
				if (!versionScreen.enquiryWithClass("AdditionalDetails", "AADETAILSOVERDUESTATS").getText()
						.contains("No Overdues")) {
					stepActual += System.lineSeparator();
					stepActual += "Arrangement " + mainArrangement + " still has overdue amount";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			} catch (NoSuchElementException e) {
				Reporter.log(e.getMessage(), false);
				stepActual = "There was an unexpected error while executing this step";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		
		softVerify.assertAll();
	}
}
