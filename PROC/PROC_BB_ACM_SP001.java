package testcases.PROC;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;

public class PROC_BB_ACM_SP001 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 1.0";
	private String customer;
	private String mainArrangement;
	private String totalPrincipal;
	private String totalDelinquent;
	private String totalPrincipalAfter;
	private String totalDelinquentAfter;

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
					.withRepayments()
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
	public void verifyDelinquentBalance(@Optional(PERSONAL_LOANS) final String productGroup, final String product) {

		stepDescription = "Verify the Delinquent Balance of arrangement " + mainArrangement;
		stepExpected = "Delinquent Balance verified successfully";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			try {
				findArrangement(AUTHORISED, mainArrangement, ARRANGEMENT, ROLEBASED_LENDING, productGroup, product,
						CAD);
				totalPrincipal = versionScreen.labelElement("Total Principal").getText();
				totalDelinquent = versionScreen.labelElement("Total Delinquent Balance").getText();

				if ("".equals(totalPrincipal) || "".equals(totalDelinquent)) {
					stepActual = "The arrangement was not correctly setup as delinquent";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				} else {
					stepActual = "Total Principal: " + totalPrincipal + System.lineSeparator()
							+ "Total Delinquent Balance: " + totalDelinquent;
				}
			} catch (NoSuchElementException e) {
				Reporter.log(e.getMessage(), false);
				stepActual = "There was an unexpected error while executing this step";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
	}
	
	@Test(priority = 3, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void adjustDelinquentBalance(@Optional(PERSONAL_LOANS) final String productGroup, final String product) {

		stepDescription = "Adjust the amount of the delinquent bill of " + mainArrangement;
		stepExpected = "Delinquent Bill amount has been adjusted";

		String fields;
		String values;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			fields = "NEW.PROP.AMT:1:1,";
			values = "-600,";

			try {
				if (!arrangementActivity(CREATE, "ADJUST.BILL ACTIVITY FOR BALANCE.MAINTENANCE", mainArrangement,
						productGroup, fields, values)) {
					stepActual = "ADJUST.BILL ACTIVITY FOR BALANCE.MAINTENANCE was not created successfully";
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

	@Test(priority = 4, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void adjustTotalPrincipal(@Optional(PERSONAL_LOANS) final String productGroup, final String product) {
		
		stepDescription = "Add the amount adjusted to the total principal of " + mainArrangement;
		stepExpected = "Total Principal adjusted successfully";

		String fields;
		String values;
		Double adjustedAmount;

		if (mainArrangement == null || mainArrangement.contains(ERROR) || totalPrincipal == null
				|| "".equals(totalPrincipal) || totalDelinquent == null || "".equals(totalDelinquent)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			adjustedAmount = Double.parseDouble(totalPrincipal.replaceAll(",", "")) - 600.00;
			fields = "ADJUST.DESC," + "NEW.BAL.AMT:1:1,";
			values = "adjustment," + "-" + adjustedAmount.toString() + ",";

			try {
				if (!arrangementActivity(CREATE, "ADJUST.BALANCE ACTIVITY FOR BALANCE.MAINTENANCE", mainArrangement,
						productGroup, fields, values)) {
					stepActual = "ADJUST.BALANCE ACTIVITY FOR BALANCE.MAINTENANCE was not created successfully";
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
	
	@Test(priority = 4, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void verifyAdjustedBalance(@Optional(PERSONAL_LOANS) final String productGroup, final String product) {

		stepDescription = "Verify the Adjusted Balance of arrangement " + mainArrangement;
		stepExpected = "Adjusted Balance verified successfully";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			try {
				findArrangement(AUTHORISED, mainArrangement, ARRANGEMENT, ROLEBASED_LENDING, productGroup, product,
						CAD);
				totalPrincipalAfter = versionScreen.labelElement("Total Principal").getText();
				totalDelinquentAfter = versionScreen.labelElement("Total Delinquent Balance").getText();

				if (!totalPrincipalAfter.equals(totalPrincipal)) {
					stepActual = "The balances were not correctly adjusted." + System.lineSeparator()
							+ "Old Principal: " + totalPrincipal + System.lineSeparator() + "New Principal: "
							+ totalPrincipalAfter;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				} else if ("600".equals(totalDelinquent)) {
					stepActual = "The delinquent balances was not correctly adjusted." + System.lineSeparator()
							+ "Old Delinquent Balance: " + totalDelinquent + System.lineSeparator()
							+ "New Delinquent Balance: " + totalDelinquentAfter;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				} else {
					stepActual = "Total Principal: " + totalPrincipal + System.lineSeparator()
							+ "Total Delinquent Balance: " + totalDelinquent;
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
