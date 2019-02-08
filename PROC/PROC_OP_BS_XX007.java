package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.DefaultVariables;

public class PROC_OP_BS_XX007 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.0";
	private String mainArrangement;
	private String customer;
	private String productLine;
	private String roleBasedPage;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "customerType", "productGroup", "product" })
	public void preCondition(@Optional("B2B Branch 623") final String branch,
			@Optional(PERSONAL) final String customerType, @Optional(PERSONAL_ACCOUNTS) final String productGroup,
			@Optional(HISA_PERSONAL) final String product) {

		stepDescription = "Create " + customerType + " customer and " + product + " arrangement";
		stepExpected = customerType + " customer and " + product + " arrangement created successfully";

		if (loginResult) {
			switchToBranch(branch);

			productLine = DefaultVariables.productGroupArrangementType.get(productGroup);
			roleBasedPage = DefaultVariables.productLineRoleBasedPage.get(productLine);

			customer = createDefaultCustomer(customerType, productGroup, roleBasedPage);
			mainArrangement = createDefaultArrangement(productGroup, product, roleBasedPage, customer, "+0d");

			if (customer == null || mainArrangement == null || customer.contains(ERROR)
					|| mainArrangement.contains(ERROR)) {
				stepActual = "Error while creating customer and " + product + " arrangement";
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
	public void setPostingRestrictToAccount(@Optional(PERSONAL) final String customerType,
			@Optional(PERSONAL_ACCOUNTS) final String productGroup) {

		stepDescription = "Set Posting Restrict to Account: " + mainArrangement;
		stepExpected = "Posting Restrict to Account: " + mainArrangement + " set successfully";

		final String fields = "Account$POSTING.RESTRICT:1";
		final String values = "1";
		boolean result;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = postingRestrict(CREATE, ARRANGEMENT, mainArrangement, customerType, productGroup, fields, values);

			if (!result) {
				stepActual = "Error while setting up Posting Restrict to Account: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "customerType", "productGroup" })
	public void setPostingRestrictToCustomer(@Optional(PERSONAL) final String customerType,
			@Optional(PERSONAL_ACCOUNTS) final String productGroup) {

		stepDescription = "Set Posting Restrict to Customer: " + customer;
		stepExpected = "Posting Restrict to Customer: " + customer + " set successfully";

		final String fields = "POSTING.RESTRICT:1";
		final String values = "1";
		boolean result;

		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = postingRestrict(CREATE, CUSTOMER, customer, customerType, productGroup, fields, values);

			if (!result) {
				stepActual = "Error while setting up Posting Restrict to Customer: " + customer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters({ "customerType", "productGroup" })
	public void removePostingRestrictFromAccount(@Optional(PERSONAL) final String customerType,
			@Optional(PERSONAL_ACCOUNTS) final String productGroup) {

		stepDescription = "Remove Posting Restrict from Account: " + mainArrangement;
		stepExpected = "Posting Restrict from Account: " + mainArrangement + " removed successfully";

		boolean result;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = postingRestrict("Remove", ARRANGEMENT, mainArrangement, customerType, productGroup, "", "");

			if (!result) {
				stepActual = "Error while removing Posting Restrict from account: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	@Parameters({ "customerType", "productGroup" })
	public void removePostingRestrictFromCustomer(@Optional(PERSONAL) final String customerType,
			@Optional(PERSONAL_ACCOUNTS) final String productGroup) {

		stepDescription = "Remove Posting Restrict from Customer: " + customer;
		stepExpected = "Posting Restrict from Customer: " + customer + " removed successfully";

		boolean result;

		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = postingRestrict("Remove", CUSTOMER, customer, customerType, productGroup, "", "");

			if (!result) {
				stepActual = "Error while removing Posting Restrict from Customer: " + customer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
