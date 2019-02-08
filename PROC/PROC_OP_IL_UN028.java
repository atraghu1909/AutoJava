package testcases.PROC;

import java.util.List;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.DefaultVariables;

public class PROC_OP_IL_UN028 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.3";
	private ArrangementData arrangementData;
	private String mainArrangement;
	private String customer;
	private String collateralAmendedID;
	private String collateralReverseID;
	private boolean result;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "customerType", "product" })
	public void preCondition(@Optional("B2B Branch 817") final String branch,
			@Optional("Personal") final String customerType,
			@Optional("Investment Loan Fixed Rate") final String product) {

		stepDescription = "Create customer, collateral and " + product + " arrangement";
		stepExpected = "Customer, collateral and " + product + " arrangement created successfully";
		String collateralLink;
		String collateralDetails;
		String collateralFields;
		String collateralValues;
		String collateralLinkFields;
		String collateralLinkValues;
		String completeLimitId;

		if (loginResult) {

			switchToBranch(branch);
			arrangementData = new ArrangementData("mainArrangement", PERSONAL_LOANS, product).withEffectiveDate("-1m")
					.withAgent("NEW", "Dummy Commission Plan", "NEW", "Dummy Commission Plan").build();

			mainArrangement = createDefaultArrangement(arrangementData);
			customer = arrangementData.getCustomers();
			completeLimitId = arrangementData.getCompleteLimitId();

			if (mainArrangement == null || mainArrangement.contains(ERROR)) {
				stepActual = "Error while creating " + product + " arrangement";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {
				collateralLinkFields = "COLLATERAL.CODE," + "COMPANY:1," + "LIMIT.REF.CUST:1," + "LIMIT.REFERENCE:1,";
				collateralLinkValues = "1," + "B2B," + customer + "," + completeLimitId + ",";
				collateralFields = DefaultVariables.collateralFields + "Collateral details#CA.ADR.LINE1,"
						+ "Collateral details#TOWN.CITY," + "Collateral details#US.STATE,"
						+ "Collateral details#CA.POST.CODE," + "Collateral#COLLATERAL.CODE," + "COLLATERAL.TYPE,";
				collateralValues = DefaultVariables.collateralValues + createdCustomers.get(customer).getAddressStreet()
						+ "," + createdCustomers.get(customer).getAddressCity() + ","
						+ createdCustomers.get(customer).getAddressProvince() + ","
						+ createdCustomers.get(customer).getaddressPostalCode() + "," + "1," + "1,";

				collateralLink = collateral(CREATE, COLLATERAL_LINK, customer, collateralLinkFields,
						collateralLinkValues);
				collateralDetails = collateral(CREATE, COLLATERAL_DETAILS, collateralLink, collateralFields,
						collateralValues);

				if (collateralLink == null || collateralLink.contains(ERROR) || collateralDetails == null
						|| collateralDetails.contains(ERROR)) {
					stepActual = "Error while creating collateral link and collateral details";
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
	@Parameters({ "product" })
	public void validateLoanEligibility(@Optional("Investment Loan Fixed Rate") final String product) {

		stepDescription = "Validate loan eligibility";
		stepExpected = "Loan eligibility validated successfully";
		result = true;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			findArrangement(AUTHORISED, customer, CIF, "", PERSONAL_LOANS, product, CAD);
			List<WebElement> summaryList = versionScreen.rowCellList("FinancialSummary", "1");
			for (WebElement summary : summaryList) {

				if (summary.getText().contains("Total Delinquent Balance")) {
					result = false;
				}
			}
			if (!result) {
				stepActual = "Failed to validate loan eligibility";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;

			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	public void validateDealerStatus() {

		stepDescription = "Validate Dealer Status";
		stepExpected = "Dealer Status is current";
		String status;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			enquiryElements.enquiryButtons("View Pricing Details").click();
			switchToPage("Arrangement Overview (Agent)", false);
			status = versionScreen.labelElement("Status").getText();

			if (!"Current".equals(status)) {
				stepActual = "Dealer status is:" + status;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;

			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	public void editCollateralRight() {

		stepDescription = "Edit collateral right";
		stepExpected = "Collateral right edited  successfully";
		String collateralFields;
		String collateralValues;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			collateralFields = "COLLATERAL.CODE,";
			collateralValues = "426,";

			collateralAmendedID = collateral(AMEND, COLLATERAL_LINK, customer + ".1", collateralFields,
					collateralValues);

			if (collateralAmendedID == null || collateralAmendedID.contains(ERROR)) {
				stepActual = "Error while amending collateral right: " + collateralAmendedID;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	public void deleteCollateralRight() {

		stepDescription = "Delete collateral right";
		stepExpected = "Collateral right deleted  successfully";

		if (collateralAmendedID == null || collateralAmendedID.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			collateral(REVERSE, COLLATERAL_DETAILS, customer + ".2.1", "", "");
			collateralReverseID = collateral(REVERSE, COLLATERAL_LINK, customer + ".2", "", "");

			if (collateralReverseID == null || collateralReverseID.contains(ERROR)) {
				stepActual = "Error while Deleting Collateral Right";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	public void editCollateralDetails() {

		stepDescription = "Edit collateral details";
		stepExpected = "Collateral details edited successfully";
		String collateralFields;
		String collateralValues;
		String collateralID;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			collateralFields = "Registration details#L.COLL.ADM.CODE,Registration details#L.COLL.EXT.NO,";
			collateralValues = "B2b Investment Loan,123123123,";

			collateralID = collateral(AMEND, COLLATERAL_DETAILS_LOANS, customer + ".1.1", collateralFields,
					collateralValues);

			if (collateralID == null || collateralID.contains(ERROR)) {
				stepActual = "Error while amending collateral details: " + collateralID;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 7, enabled = true)
	public void updateOfferID() {

		stepDescription = "Update Offer id";
		stepExpected = "Offer id updated successfully";
		String activityFields;
		String activityValues;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			activityFields = "L.LOAN.OFFER.ID,";
			activityValues = "Lbs,";

			result = arrangementActivity(CREATE, "UPDATE ACTIVITY FOR ACCOUNT", mainArrangement, PERSONAL_LOANS,
					activityFields, activityValues);

			if (!result) {
				stepActual = "Unable to update offer id for account";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 8, enabled = true)
	@Parameters({ "product" })
	public void addArrangementNotes(@Optional("Investment Loan Fixed Rate") final String product) {

		stepDescription = "Add arrangement notes to " + product + " arrangement";
		stepExpected = "Arrangement notes added successfully";
		String noteFields;
		String noteValues;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			noteFields = "MEMO.TEXT,";
			noteValues = "Test Note,";

			arrangementMemo(CREATE, mainArrangement, PERSONAL_LOANS, noteFields, noteValues);

			if (!result) {
				stepActual = "Unable to create arrangement memo";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

}
