package testcases.PROC;

import java.text.DateFormat;
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

public class PROC_BB_ACM_OP003 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 1.0";
	private final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
	private final Date date = new Date();
	private final String todayDate = dateFormat.format(date);
	private String customer;
	private String mainArrangement;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "productGroup", "product" })
	public void preCondition(@Optional("B2B Branch 817") final String branch,
			@Optional(PERSONAL_LOANS) final String productGroup, final String product) {

		stepDescription = "Create a Delinquent " + product + " arrangement";
		stepExpected = "Delinquent Arrangement created successfully";

		String fields;
		String values;
		
		if (loginResult) {

			switchToBranch(branch);

			final ArrangementData arrangementData = new ArrangementData("mainArrangement", productGroup, product)
					.withEffectiveDate("-45d")
					.withTerm("1Y")
					.withSettlement("Beneficiary", "NEW")
					.withDisbursement()
					.build();

			mainArrangement = createDefaultArrangement(arrangementData);
			customer = arrangementData.getCustomers();

			if (customer == null || mainArrangement == null || customer.contains(ERROR)
					|| mainArrangement.contains(ERROR)) {
				stepActual = "Error while creating " + product + " arrangement";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {
				fields = "CA.NPL," + "CA.NPL.REASON," + "CA.NAB,";
				values = "Y," + "Insufficient Debt Service," + "Y,";

				try {
					arrangementActivity(CREATE, "Update Risk Details", mainArrangement, productGroup, fields, values);
				} catch (NoSuchElementException e) {
					Reporter.log(e.getMessage(), false);
					stepActual = "There was an unexpected error while executing this step";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
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
	@Parameters({ "productGroup", "product" })
	public void historyReport(@Optional(PERSONAL_LOANS) final String productGroup, final String product) {

		stepDescription = "Verify if the code of arrangement " + mainArrangement
				+ " is displayed on the NPL/NAB Report and that the Report can be saved as CSV";
		stepExpected = "Arrangement is displayed on the History Report, and Report can be saved as CSV";

		String arrangementCode;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			try {
				findArrangement(AUTHORISED, mainArrangement, ARRANGEMENT, ROLEBASED_LENDING, productGroup, product,
						CAD);
				arrangementCode = versionScreen.labelElement(ARRANGEMENT).getText();

				commandLine("ENQ LBC.NPL.NAB.HISTORY.DETAIL", commandLineAvailable);
				switchToPage(LASTPAGE, false);
				enquiryElements.enquirySearch("Date,Customer ID", ",", todayDate + "," + customer);
				switchToPage(LASTPAGE, false);

				if (enquiryElements.getRowNumberMatching("NPL and NAB History Detail", "Arrangement", arrangementCode) == 0) {
					stepActual = "The arrangement " + arrangementCode + " was not displayed on the History Report";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				} else {
					stepActual = "Code " + arrangementCode + " is displayed on the History Report";
				}
				
				if (!enquiryElements.verifyOptionSaveCSV()) {
					stepActual += System.lineSeparator() + "The enquiry results cannot be saved as a CSV file";
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
