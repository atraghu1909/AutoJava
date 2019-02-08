package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PROC_BB_ILA_OP005 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.0";
	private String mainArrangement;
	private String customer;
	private String branch = "B2B Branch 817";
	private String step1Fields = "";
	private String step1Values = "";
	private String defaultStep2Fields = "";
	private String defaultStep2Values = "";
	private final StringBuilder step1FieldsBuilder = new StringBuilder(step1Fields);
	private final StringBuilder step1ValuesBuilder = new StringBuilder(step1Values);

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void preCondition(final String productGroup, final String product) {

		stepDescription = "Create Current " + product + " Arrangement";
		stepExpected = "Current " + product + " Arrangement created successfully";

		String customerType;

		if (loginResult) {

			switchToBranch(branch);

			customerType = DefaultVariables.productGroupCustomerType.get(productGroup);

			if (DefaultVariables.productFields.containsKey(product)) {
				defaultStep2Fields = DefaultVariables.productFields.get(product);
				defaultStep2Values = DefaultVariables.productValues.get(product);
			} else {
				defaultStep2Fields = DefaultVariables.productGroupFields.get(productGroup);
				defaultStep2Values = DefaultVariables.productGroupValues.get(productGroup);
			}

			customer = createDefaultCustomer(customerType, productGroup, ROLEBASED_LENDING);

			step1FieldsBuilder.append("EFFECTIVE.DATE,");
			step1Fields = step1FieldsBuilder.toString();
			step1ValuesBuilder.append("-1m,");
			step1Values = step1ValuesBuilder.toString();

			mainArrangement = dataGenCreateLoan(customer, productGroup, product, CAD, step1Fields, step1Values,
					defaultStep2Fields, defaultStep2Values, "Current via Cheque", "-1m");

			if (mainArrangement == null || mainArrangement.contains(ERROR)) {
				stepActual = "Error while creating " + product + " Arrangement: " + mainArrangement;
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
	@Parameters({ "productGroup" })
	public void test(final String productGroup) {

		stepDescription = "Reverse Repayment Activity and apply NSF fee for arrangement: " + mainArrangement;
		stepExpected = "Repayment Activity reversed successfully and NSF fee applied successfully for arrangement: "
				+ mainArrangement;

		boolean result;
		String fields = "FIXED.AMOUNT,";
		String values = "30,";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementActivity(REVERSE_AUTHORISE, "APPLYPAYMENT ACTIVITY FOR PR.REPAYMENT", mainArrangement,
					productGroup, "", "");

			if (!result) {
				stepActual = "Error while reversing Repayment Activity for arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {
				result = arrangementActivity(CREATE_AUTHORISE, "Change and Capitalise ALNSFFEE fee", mainArrangement,
						productGroup, fields, values);

				fields = "MEMO.TEXT,";
				values = "1 payment returned NSF for the amount of XXX with cheque number YYY. Letter sent to client and copy emailed to Account Manager for follow-up,";

				result = arrangementMemo(CREATE, mainArrangement, productGroup, fields, values);

				if (!result) {
					stepActual = "Error while applying NSF fee for arrangement: " + mainArrangement;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		}
		softVerify.assertAll();
	}
}
