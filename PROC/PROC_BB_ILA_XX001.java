package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PROC_BB_ILA_XX001 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 1.1";
	private String mainArrangement;
	private String customer;
	String customerType;
	private String branch = "B2B Branch 817";
	private String defaultStep2Fields = "";
	private String defaultStep2Values = "";

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void preCondition(final String productGroup, final String product) {

		stepDescription = "Create " + product + " Arrangement";
		stepExpected = product + " Arrangement created successfully";

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

			mainArrangement = dataGenCreateLoan(customer, productGroup, product, CAD, "", "", defaultStep2Fields,
					defaultStep2Values, "Not Disbursed", "+0d");

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
	public void disbursementCheque() {

		stepDescription = "Perform Disbursement via Cheque for customer: " + customer;
		stepExpected = "Disbursement via Cheque for customer: " + customer + " performed successfully";

		boolean result;
		final String fields;
		final String values;
		String defaultFields[];
		String defaultValues[];
		String commitmentAmount = "";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			defaultFields = defaultStep2Fields.split(",");
			defaultValues = defaultStep2Values.split(",");
			for (int i = 0; i < defaultFields.length; i++) {
				if ("AMOUNT".equals(defaultFields[i])) {
					commitmentAmount = defaultValues[i];
				}
			}

			fields = "DEBIT.AMOUNT," + "CREDIT.ACCT.NO,";
			values = commitmentAmount + "," + "CAD1110200017817,";
			result = arrangementAction(mainArrangement, customer, ROLEBASED_LENDING, LENDING_DISBURSEMENT, fields,
					values, customerType);

			if (!result) {
				stepActual = "Error while performing Disbursement via Cheque for customer: " + customer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	public void compensationCheque() {

		stepDescription = "Perform Compensation via Cheque for customer: " + customer;
		stepExpected = "Compensation via Cheque for customer: " + customer + " performed successfully";

		String transactionID;
		final String fields;
		final String values;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			fields = "DEBIT.AMOUNT," + "CREDIT.ACCT.NO," + "DEBIT.ACCT.NO," + "PROFIT.CENTRE.DEPT," + "ORDERING.BANK:1,"
					+ "DEBIT.CURRENCY,";
			values = "10.00," + "CAD1110200017817," + "PL52815,1," + "Test," + "CAD,";
			transactionID = financialTransaction(CREATE, "Account Transfer - Transfer between Accounts", fields,
					values);

			if (transactionID.contains(ERROR)) {
				stepActual = "Error while performing Compensation via Cheque for customer: " + customer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
