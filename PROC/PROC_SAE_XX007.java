package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PROC_SAE_XX007 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "2018-03-27";
	private String customer;
	private String mainArrangement;

	@Test(priority = 1, enabled = true)
	@Parameters({ "customerType", "productGroup", "product", "branch", "effectiveDate" })
	public void preCondition(final String customerType, @Optional("Commercial Loans") final String productGroup,
			final String product, @Optional("LAURENTIAN BANK - 523") final String branch,
			@Optional("+0d") final String effectiveDate) {

		stepDescription = "Create " + customerType + " Client and " + product + " Arrangement";
		stepExpected = customerType + " Client and " + product + " Arrangement created successfully";

		if (loginResult) {

			switchToBranch(branch);

			customer = createDefaultCustomer(customerType, productGroup, ROLEBASED_SAE);
			if (customer == null || customer.contains(ERROR)) {
				stepActual = "Error while creating " + customerType + " Client";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			mainArrangement = createDefaultArrangement(productGroup, product, ROLEBASED_SAE, customer, effectiveDate);
			if (mainArrangement == null || mainArrangement.contains(ERROR)) {
				stepActual = "Error while creating " + product + " Arrangement";
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
	@Parameters("customerType")
	public void disbursement(final String customerType) {

		stepDescription = "Disburse arrangement: " + mainArrangement;
		stepExpected = "Arrangement: " + mainArrangement + " disbursed successfully";

		String fields = "DEBIT.ACCT.NO," + "DEBIT.CURRENCY," + "CREDIT.ACCT.NO," + "DEBIT.AMOUNT,";
		String values = mainArrangement + "," + "CAD,"
				+ /*
					 * Find CREDIT Account Number from Annex A from the
					 * procedure +
					 */"CAD1100000017817," + "100000,";
		boolean result;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementAction(mainArrangement, customer, ROLEBASED_SAE, "AA Disbursement", fields,
					values, customerType);

			if (!result) {
				stepActual = "Error while disbursing arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void viewDisbursedArrangement(@Optional("Commercial Loans") final String productGroup,
			final String product) {

		stepDescription = "Verify the disbursement of arrangement: " + mainArrangement;
		stepExpected = "Arrangement: " + mainArrangement + " disbursement varified successfully";

		String status;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			findArrangement(AUTHORISED, mainArrangement, ARRANGEMENT, ROLEBASED_SAE, productGroup, product, CAD);
			status = versionScreen.labelElement("Status").getText();

			if (!"Current".equals(status)) {
				stepActual = "Arrangement: " + mainArrangement + " is not Current. Instead, it is: " + status;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
