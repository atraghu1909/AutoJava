package testcases.PROC;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;

public class PROC_OP_AM_PR003 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 2.0";
	private String beneficiary;
	private String mainArrangement;
	private String nextPaymentDate;
	private String totalDelinquentBalance;
	private String beneficiaryName;
	private String beneficiarySortCode;

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
					.withSettlement("Beneficiary", "NEW")
					.withDisbursement()
					.build();

			mainArrangement = createDefaultArrangement(arrangementData);
			beneficiary = arrangementData.getSettlementAccount();

			if (beneficiary == null || mainArrangement == null || beneficiary.contains(ERROR)
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
	public void retrieveMemoDetails(@Optional(PERSONAL_LOANS) final String productGroup, final String product) {

		stepDescription = "Retrieve information from arrangement " + mainArrangement + " and Beneficiary "
				+ beneficiary;
		stepExpected = "Information retrieved successfully";

		if (beneficiary == null || mainArrangement == null || beneficiary.contains(ERROR)
				|| mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			try {
				findArrangement(AUTHORISED, mainArrangement, ARRANGEMENT, ROLEBASED_LENDING, productGroup, product,
						CAD);
				totalDelinquentBalance = versionScreen.labelElement("Total Delinquent Balance").getText();
				nextPaymentDate = versionScreen.labelElement("Next Payment").getText();
				
				beneficiaryCode(OPEN, "EFT Client", beneficiary, "", "");
				beneficiaryName = readData("BEN.CUSTOMER");
				beneficiarySortCode = readData("BEN.ACCT.NO");
				toolElements.toolsButton(RETURN_TO_SCREEN).click();

				stepActual = "Total Delinquen Balance: " + totalDelinquentBalance + System.lineSeparator()
						+ "Next Payment: " + nextPaymentDate + System.lineSeparator() + "Beneficiary's Name: "
						+ beneficiaryName + System.lineSeparator() + "Bank Sort Code: " + beneficiarySortCode
						+ System.lineSeparator();

				if (totalDelinquentBalance == null || nextPaymentDate == null || beneficiaryName == null
						|| beneficiarySortCode == null || "".equals(totalDelinquentBalance)
						|| "".equals(nextPaymentDate) || "".equals(beneficiaryName) || "".equals(beneficiarySortCode)) {
					stepActual += System.lineSeparator() + "One of the retrieved values is invalid";
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
