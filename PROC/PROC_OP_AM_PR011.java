package testcases.PROC;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;

public class PROC_OP_AM_PR011 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 2.0";
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
					.withEffectiveDate("-45d")
					.withTerm("1Y")
					.withDisbursement()
					.build();

			mainArrangement = createDefaultArrangement(arrangementData);
			customer = arrangementData.getCustomers();

			if (mainArrangement == null ||  mainArrangement.contains(ERROR)) {
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
	@Parameters({ "customerType" })
	public void retrieveWriteOffBalance(@Optional(PERSONAL) final String customerType) {

		stepDescription = "Retrieve balance to be written off for Arrangement " + mainArrangement;
		stepExpected = "Balance to be written off retrieved successfully";

		String balance;
		int closingBalanceColumn;
		int principalIntRow;
		
		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			try {
				commandLine("ENQ EB.CONTRACT.BALANCES.BALANCE", commandLineAvailable);
				switchToPage(LASTPAGE, false);
				enquiryElements.enquirySearch("@ID", "", mainArrangement);
				switchToPage(LASTPAGE, false);

				closingBalanceColumn = enquiryElements.getColumnHeaderNumber("Contract wise balances",
						"Credit Mvmt");
				principalIntRow = enquiryElements.getRowNumberMatching("Contract wise balances", "Balance Type",
						"ACCPRINCIPALINT");
				
				if (closingBalanceColumn > 0 && principalIntRow > 0) {
					balance = enquiryElements
							.getElementAtCell("Contract wise balances", closingBalanceColumn, principalIntRow).getText();
					stepActual = "Balance to be written off is: $" + balance;
				} else {
					stepActual = "Principal Interest Balance is not displayed on enquiry results";
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
