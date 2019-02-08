package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.DefaultVariables;

public class PROC_OP_BS_XX006 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.1";
	private String mainArrangement;
	private String customer;
	private String productLine;
	private String roleBasedPage;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "productGroup", "product" })
	public void preCondition(@Optional("B2B Branch 623") final String branch,
			@Optional(RETAIL_MORTGAGES) final String productGroup, @Optional(HELOC) final String product) {

		stepDescription = "Create customer and " + product + " arrangement";
		stepExpected = "Customer and " + product + " arrangement created successfully";

		switchToBranch(branch);

		final ArrangementData arrangementData = new ArrangementData("mainArrangement", productGroup, product)
				.withEffectiveDate("-1m").withTerm("1Y").withDisbursement().withRepayments().build();

		mainArrangement = createDefaultArrangement(arrangementData);
		customer = arrangementData.getCustomers();
		productLine = DefaultVariables.productGroupArrangementType.get(productGroup);
		roleBasedPage = DefaultVariables.productLineRoleBasedPage.get(productLine);

		if (customer == null || mainArrangement == null || customer.contains(ERROR)
				|| mainArrangement.contains(ERROR)) {
			stepActual = "Error while creating customer and " + product + " arrangement";
			softVerify.fail(stepActual);
			stepResult = StatusAs.FAILED;
		}

		softVerify.assertAll();
	}

	@Test(priority = 2, enabled = true)
	@Parameters("customerType")
	public void issueDebitCard(@Optional(PERSONAL) final String customerType) {

		stepDescription = "Issue debit card for customer: " + customer;
		stepExpected = "Debit card for customer: " + customer + " issued successfully";

		boolean result;
		String step1Fields = "CUSTOMER," + "DEF.MEMBER," + "CARD.STATUS," + "EXPIRY.DATE,";
		String step1Values = customer + "," + customer + "," + "1," + "+5Y,";
		String step2Fields = "Limits#LIMIT.SCORE," + "Access#INTERAC.FLASH,";
		String step2Values = "025," + "Disabled,";

		if (customer == null || mainArrangement == null || customer.contains(ERROR)
				|| mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = debitCard("Issue", customer, customerType, step1Fields, step1Values, step2Fields, step2Values,
					roleBasedPage);

			if (!result) {
				stepActual = "Error while issuing debit card for customer: " + customer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters("customerType")
	public void verifyLinkBetweenCardAndArrangement(@Optional(PERSONAL) final String customerType) {

		stepDescription = "Verify link between debit card and arrangement";
		stepExpected = "Link between debit card and arrangement verified successfully";

		String debitCardNumber;
		String newArrangement;

		if (customer == null || mainArrangement == null || customer.contains(ERROR)
				|| mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			newArrangement = createDefaultArrangement(PERSONAL_ACCOUNTS, PERSONAL_CHEQUING, ROLEBASED_BANKING, customer,
					"+0d");

			findCIF(customer, customerType, ROLEBASED_BANKING);
			switchToPage(LASTPAGE, false);
			switchToTab("Products", "");
			versionScreen.waitForLoading();
			debitCardNumber = versionScreen.versionElement("", newArrangement, "2").getText();

			if (debitCardNumber == null || "".equals(debitCardNumber)) {
				stepActual = "There is no icon on the Customer 360 View indicating "
						+ "the arrangement is linked to the recently issued debit card";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
