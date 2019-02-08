package testcases.PROC;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;

public class PROC_OP_AM_PR012 extends testLibs.BaseTest_DataGen {

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
					.withEffectiveDate("-45d").withTerm("1Y").withDisbursement().build();

			mainArrangement = createDefaultArrangement(arrangementData);
			customer = arrangementData.getCustomers();

			if (mainArrangement == null || mainArrangement.contains(ERROR)) {
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
	@Parameters({ "customerType", "productGroup" })
	public void setClientToBankrupt(@Optional(PERSONAL) final String customerType,
			@Optional(PERSONAL_LOANS) final String productGroup) {

		stepDescription = "Set Customer " + customer + " to Bankrupt";
		stepExpected = "Customer is successfully set to Bankrupt";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			try {
				if (!postingRestrict(CREATE, CUSTOMER, customer, PERSONAL, productGroup, "POSTING.RESTRICT:1", "3")
						|| amendCIF(AMEND, customer, customerType, ROLEBASED_LENDING, "CUSTOMER.STATUS", "26")
								.contains(ERROR)) {
					stepActual = "Customer could not be set to Bankrupt";
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
	@Parameters({ "productGroup" })
	public void addArrangementMemo(@Optional(PERSONAL_LOANS) final String productGroup) {

		stepDescription = "Add memo to Arrangement " + mainArrangement;
		stepExpected = "Memo was added successfully";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			try {
				if (!arrangementMemo(CREATE, mainArrangement, productGroup, "MEMO.TEXT",
						"Test Note - Customer Bankrupt")) {
					stepActual = "Error adding Memo to arrangement";
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
	}
}
