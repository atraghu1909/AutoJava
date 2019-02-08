package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PROC_BB_REB_OP004 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 1.1";
	private String mainArrangement = "";
	private String customer;
	private String branch = "B2B Branch 817";
	private String defaultStep2Fields = "";
	private String defaultStep2Values = "";

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void preCondition(final String productGroup, final String product) {

		stepDescription = "Create " + product + " Arrangement";
		stepExpected = product + " Arrangement created successfully";

		String customerType;

		switchToBranch(branch);

		customerType = DefaultVariables.productGroupCustomerType.get(productGroup);

		if (DefaultVariables.productFields.containsKey(product)) {
			defaultStep2Fields = DefaultVariables.productFields.get(product);
			defaultStep2Values = DefaultVariables.productValues.get(product);
		} else {
			defaultStep2Fields = DefaultVariables.productGroupFields.get(productGroup);
			defaultStep2Values = DefaultVariables.productGroupValues.get(productGroup);
		}

		customer = createDefaultCustomer(customerType, productGroup, ROLEBASED_BANKING);

		mainArrangement = dataGenCreateLoan(customer, productGroup, product, CAD, "", "", defaultStep2Fields,
				defaultStep2Values, "Not Disburse", "+0d");

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepActual = "Error while creating " + product + " Arrangement: " + mainArrangement;
			softVerify.fail(stepActual);
			stepResult = StatusAs.FAILED;
		}
		softVerify.assertAll();
	}

	@Test(priority = 2, enabled = true)
	@Parameters({ "productGroup" })
	public void createMemo(final String productGroup) {

		stepDescription = "Create Memo for arrangement: " + mainArrangement;
		stepExpected = "Memo was created successfully for arrangement: " + mainArrangement;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			boolean result;
			String fields = "MEMO.TEXT," + "MEMO.TYPE,";
			String values = "This is a test of the Create Memo functionality," + "Descriptive Text,";

			result = arrangementMemo(CREATE, mainArrangement, productGroup, fields, values);

			if (!result) {
				stepActual = "Error while creating Memo for arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "productGroup" })
	public void amendMemo(final String productGroup) {

		stepDescription = "Amend Memo for arrangement: " + mainArrangement;
		stepExpected = "Memo was amended successfully for arrangement: " + mainArrangement;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			boolean result;
			String fields = "MEMO.TEXT,";
			String values = "This is an edit to a test of the Create Memo functionality,";

			result = arrangementMemo(AMEND, mainArrangement, productGroup, fields, values);

			if (!result) {
				stepActual = "Error while amending Memo for arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
