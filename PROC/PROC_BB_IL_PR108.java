package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PROC_BB_IL_PR108 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.0";
	private static String branch = "B2B Branch 817";
	private String mainArrangement;
	private String customer;
	private String paymentAmount;
	private String beneficiary;
	private String step1Fields = "";
	private String step1Values = "";
	private String defaultStep2Fields = "";
	private String defaultStep2Values = "";
	private final StringBuilder step1FieldsBuilder = new StringBuilder(step1Fields);
	private final StringBuilder step1ValuesBuilder = new StringBuilder(step1Values);

	@Test(priority = 1, enabled = true)
	@Parameters({ "customerType", "productGroup", "product" })
	public void preCondition(@Optional(PERSONAL) final String customerType, final String productGroup, final String product) {

		stepDescription = "Create " + product + " Arrangement";
		stepExpected = product + " Arrangement created successfully" + mainArrangement;

		if (loginResult) {
			switchToBranch(branch);

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
					defaultStep2Fields, defaultStep2Values, "Current via EFT", "-1m");

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
	public void reverseEFTTransaction(final String productGroup) {

		stepDescription = "Reverse a repayment transaction via EFT for arrangement: " + mainArrangement;
		stepExpected = "Repayment transaction via EFT for arrangement: " + mainArrangement + " reversed successfully";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			boolean result;

			result = arrangementActivity(REVERSE_AUTHORISE, "APPLYPAYMENT ACTIVITY FOR PR.REPAYMENT", mainArrangement,
					productGroup, "", "");
			if (!result) {
				stepActual = "Error while reversing repayment transaction via EFT for arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "productGroup" })
	public void applyNSFFee(final String productGroup) {

		stepDescription = "Apply NSF Fee";
		stepExpected = "NSF Fee applied successfully";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			boolean result;
			String fields = "FIXED.AMOUNT,";
			String values = "60,";

			result = arrangementActivity(CREATE_AUTHORISE, "Change and Capitalise ALNSFFEE fee", mainArrangement,
					productGroup, fields, values);
			if (!result) {
				stepActual = "Error while applying NSF Fee";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void reverseNSFFeeTransation(final String productGroup, final String product) {

		stepDescription = "Reverse NSF Fee Transation for arrangement: " + mainArrangement;
		stepExpected = "NSF Fee Transation for arrangement: " + mainArrangement + " reversed successfully";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			boolean result;

			result = arrangementActivity(REVERSE_AUTHORISE, "Change and Capitalise ALNSFFEE fee", mainArrangement,
					productGroup, "", "");
			if (!result) {
				stepActual = "Error while reversing NSF Fee Transation for arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	@Parameters({"customerType", "productGroup", "product" })
	public void retakeDebitTransactions(@Optional(PERSONAL) final String customerType, final String productGroup, final String product) {

		stepDescription = "Repeat a failed Debit Transaction by performing a Loan Payments for arrangement: "
				+ mainArrangement;
		stepExpected = "Loan Payments for arrangement: " + mainArrangement + " performed successfully";

		String fields;
		String values;
		String paymentID;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			paymentAmount = arrangementBillAmount(mainArrangement, productGroup, product);
			beneficiary = findBeneficiaryCode(customer, customerType, "EFT Client", ROLEBASED_LENDING);

			fields = "CREDIT.ACCOUNT," + "PAYMENT.ORDER.PRODUCT," + "PAYMENT.CURRENCY," + "PAYMENT.AMOUNT,"
					+ "ORDERING.CUSTOMER.SSI,";
			values = mainArrangement + "," + "ACHCREDIT," + "CAD," + paymentAmount + "," + beneficiary + ",";

			paymentID = createAdHocPayment(CREATE, EFT, mainArrangement, fields, values);
			if (!paymentID.contains(ERROR)) {
				fields = "CREDIT.ACCOUNT," + "PAYMENT.ORDER.PRODUCT," + "PAYMENT.CURRENCY," + "PAYMENT.AMOUNT,"
						+ "ORDERING.CUSTOMER.SSI,";
				values = mainArrangement + "," + "ACHCREDIT," + "CAD," + "50.00," + beneficiary + ",";

				paymentID = createAdHocPayment(CREATE, EFT, mainArrangement, fields, values);
			}
			if (paymentID.contains(ERROR)) {
				stepActual = "Error while performing Loan Payments for arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	public void retakeCreditTransaction() {

		stepDescription = "Correct charges on an account by performing Refunds via EFT for arrangement: "
				+ mainArrangement;
		stepExpected = "Charges on an account by performing Refunds via EFT for arrangement: " + mainArrangement
				+ " corrected successfully";

		String fields;
		String values;
		String paymentID;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			fields = "DEBIT.ACCOUNT," + "PAYMENT.ORDER.PRODUCT," + "PAYMENT.CURRENCY," + "PAYMENT.AMOUNT,"
					+ "BENEFICIARY.ID,";
			values = mainArrangement + "," + "ACHUNCDR," + "CAD," + paymentAmount + "," + beneficiary + ",";

			paymentID = createAdHocPayment(CREATE, EFT, mainArrangement, fields, values);

			if (paymentID.contains(ERROR)) {
				stepActual = "Error while correcting charges on an account by performing Refunds via EFT for arrangement: "
						+ mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
