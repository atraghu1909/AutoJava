package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PROC_BB_ILA_OP006 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 1.0";
	private String mainArrangement;
	private String customer;
	String customerType;
	private String branch = "B2B Branch 817";
	private String step1Fields = "";
	private String step1Values = "";
	private String step2Fields = "";
	private String step2Values = "";
	private String defaultStep2Fields = "";
	private String defaultStep2Values = "";
	private StringBuilder step2FieldsBuilder;
	private StringBuilder step2ValuesBuilder;
	private final StringBuilder step1FieldsBuilder = new StringBuilder(step1Fields);
	private final StringBuilder step1ValuesBuilder = new StringBuilder(step1Values);

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void preCondition(final String productGroup, final String product) {

		stepDescription = "Create Delinquent " + product + " Arrangement";
		stepExpected = "Delinquent " + product + " Arrangement created successfully";

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

			step2FieldsBuilder = new StringBuilder(defaultStep2Fields);
			step2ValuesBuilder = new StringBuilder(defaultStep2Values);
			step2Fields = step2FieldsBuilder.toString();
			step2Values = step2ValuesBuilder.toString();

			customer = createDefaultCustomer(customerType, productGroup, ROLEBASED_LENDING);

			step1FieldsBuilder.append("EFFECTIVE.DATE,");
			step1Fields = step1FieldsBuilder.toString();
			step1ValuesBuilder.append("-1m,");
			step1Values = step1ValuesBuilder.toString();

			mainArrangement = dataGenCreateLoan(customer, productGroup, product, CAD, step1Fields, step1Values,
					step2Fields, step2Values, "Delinquent via Cheque", "-1m");

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
	@Parameters({ "productGroup", "product" })
	public void createBackdatedBill(final String productGroup, final String product) {

		stepDescription = "Create backdated bill for customer: " + customer;
		stepExpected = "Backdated bill for customer: " + customer + " created successfully";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			boolean result;

			result = retakeLegacyPayment(mainArrangement, productGroup, product, "-1d", "-1d", "10000");

			if (!result) {
				stepActual = "Error while creating backdated bill for customer: " + customer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
