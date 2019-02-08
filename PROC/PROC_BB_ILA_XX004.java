package testcases.PROC;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PROC_BB_ILA_XX004 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 1.0";
	private String mainArrangement;
	private String customer;
	private String branch = "B2B Branch 817";
	String creditAmount;
	String beneficiary;
	String customerType;
	private String step2Fields = "";
	private String step2Values = "";
	private String defaultStep2Fields = "";
	private String defaultStep2Values = "";
	private StringBuilder step2FieldsBuilder;
	private StringBuilder step2ValuesBuilder;

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void preCondition(final String productGroup, final String product) {

		stepDescription = "Perform non-scheduled repayment via EFT and reverse activity for arrangement: "
				+ mainArrangement;
		stepExpected = "Non-scheduled repayment via EFT for arrangement: " + mainArrangement
				+ " performed and reversed successfully";

		String benePrefProd = "CHQ";
		boolean result;
		String fields;
		String values;
		int number;
		String amount;
		String creditPaymentId;
		String principalPaymentId;
		String beneficiaryFields;
		String beneficiaryValues;
		Random generator = new Random();

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

			beneficiaryFields = DefaultVariables.beneficiaryFields + "OWNING.CUSTOMER," + "BEN.CUSTOMER,";
			beneficiaryValues = DefaultVariables.beneficiaryValues + customer + ","
					+ createdCustomers.get(customer).getCustomerName() + ",";

			beneficiary = beneficiaryCode(CREATE, "EFT Client", "", beneficiaryFields, beneficiaryValues);
			Reporter.log("Created Beneficiary::" + beneficiary, true);

			step2FieldsBuilder.append(
					"Advanced - Pay In#PAYIN.PO.PRODUCT:1:1,Advanced - Pay In#PAYIN.BENEFICIARY:1:1,Advanced - Pay Out#PAYOUT.PO.PRODUCT:1:1,Advanced - Pay Out#PAYOUT.BENEFICIARY:1:1,Advanced - Pay In#PAYIN.ACTIVITY:1:1,Advanced - Pay Out#PAYOUT.ACTIVITY:1:1,");
			step2Fields = step2FieldsBuilder.toString();
			step2ValuesBuilder.append(benePrefProd).append(',').append(beneficiary).append(',').append(benePrefProd)
					.append(',').append(beneficiary).append(", , ,");
			step2Values = step2ValuesBuilder.toString();

			mainArrangement = dataGenCreateLoan(customer, productGroup, product, CAD, "", "", step2Fields, step2Values,
					"Delinquent", "-1m");

			number = generator.nextInt(99);
			amount = Integer.toString(number);

			fields = "CREDIT.ACCOUNT," + "PAYMENT.ORDER.PRODUCT," + "PAYMENT.CURRENCY," + "PAYMENT.AMOUNT,"
					+ "DEBIT.VALUE.DATE," + "ORDERING.CUSTOMER.SSI,";
			values = mainArrangement + "," + "ACHCREDIT," + "CAD," + "1000." + amount + "," + "+0d," + beneficiary
					+ ",";

			creditPaymentId = createAdHocPayment(CREATE, EFT, mainArrangement, fields, values);

			if (creditPaymentId.contains(ERROR)) {
				stepActual = "Error while performing non-scheduled repayment via EFT of amount 1000." + amount
						+ " for arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			fields = "CREDIT.ACCOUNT," + "PAYMENT.ORDER.PRODUCT," + "PAYMENT.CURRENCY," + "PAYMENT.AMOUNT,"
					+ "DEBIT.VALUE.DATE," + "ORDERING.CUSTOMER.SSI,";
			values = mainArrangement + "," + "ACHPRDEC," + "CAD," + "2000." + amount + "," + "+0d," + beneficiary + ",";

			principalPaymentId = createAdHocPayment(CREATE, EFT, mainArrangement, fields, values);

			if (!principalPaymentId.contains(ERROR)) {
				result = arrangementActivity(REVERSE_AUTHORISE, "APPLYPAYMENT ACTIVITY FOR PR.REPAYMENT",
						mainArrangement, productGroup, "", "");

				if (!result) {
					stepActual = "Error while reversing non-scheduled repayment via EFT for arrangement: "
							+ mainArrangement;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

				result = arrangementActivity(REVERSE_AUTHORISE, "APPLYPAYMENT ACTIVITY FOR PR.PRINCIPAL.DECREASE",
						mainArrangement, productGroup, "", "");

				if (!result) {
					stepActual = "Error while reversing non-scheduled repayment via EFT for arrangement: "
							+ mainArrangement;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			} else {
				stepActual = "Error while performing non-scheduled repayment via EFT of amount 2000." + amount
						+ " for arrangement: " + mainArrangement;
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
	public void clearingOutgoingEFTTransactions() {
		String todayDate;
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		todayDate = dateFormat.format(date);

		stepDescription = "Check if we can save Enquiry results as a CSV file";
		stepExpected = "Enquiry results can be saved as a CSV file";

		if (loginResult) {
			commandLine("ENQ LBC.EFT.DAILY.OUTFILE", commandLineAvailable);

			enquiryElements.favouritesButton("Delete", "Clearing Outgoing");

			enquiryElements.clearEnquiryTextFields();

			enquiryElements.enquiryElement(TEXTFIELD, "EFT File Date").sendKeys(todayDate);
			enquiryElements.enquiryElement(TEXTFIELD, "Company Code").sendKeys("CA0017000");
			enquiryElements.favouritesButton("Add", "Clearing Outgoing");
			commandLine("ENQ LBC.EFT.DAILY.OUTFILE", commandLineAvailable);
			enquiryElements.favouritesButton("Run", "Clearing Outgoing");

			if (!enquiryElements.verifyOptionSaveCSV()) {
				stepActual = "Enquiry results could not be saved as a CSV file";
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

	@Test(priority = 3, enabled = true)
	public void rejectedIncomingEFTTransactions() {
		String references;

		stepDescription = "Check if rejected incoming EFT transactions are correctly displayed";
		stepExpected = "Rejected incoming EFT transactions are displayed successfully";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			commandLine("ENQ STMT.ENT.LAST", commandLineAvailable);

			enquiryElements.favouritesButton("Delete", "Rejected Incoming");
			switchToPage(LASTPAGE, false);
			enquiryElements.enquirySearch("Account", "", "CAD1100100017817");
			switchToPage(LASTPAGE, false);
			toolElements.toolsButton(SELECTION_SCREEN).click();
			switchToPage(LASTPAGE, false);
			enquiryElements.favouritesButton("Add", "Rejected Incoming");
			commandLine("ENQ STMT.ENT.LAST", commandLineAvailable);
			enquiryElements.favouritesButton("Run", "Rejected Incoming");
			switchToPage(LASTPAGE, false);
			references = versionScreen.getEnqListCell("r", 1, "3").getText();

			if (!"!R!1!!".equals(references.substring(references.length() - 6))) {
				stepActual = "Rejected incoming EFT transactions are not displayed successfully";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	public void reclassToEFTReject() {
		final String fields = "DEBIT.ACCT.NO," + "DEBIT.CURRENCY," + "DEBIT.AMOUNT," + "ORDERING.CUST:1,"
				+ "CREDIT.ACCT.NO," + "ORDERING.BANK:1,";
		final String values = "CAD1011000017817," + "CAD,10.00," + "Clearing Payments for today," + "CAD1097000017817,"
				+ "customer,";
		String transactionID;
		stepDescription = "Perform Re-class to EFT Reject Internal Account";
		stepExpected = "Re-class to EFT Reject Internal Account performed successfully";

		if (loginResult) {
			transactionID = financialTransaction(CREATE, "Account Transfer - Transfer between Accounts", fields,
					values);

			if (transactionID.contains(ERROR)) {
				stepActual = "Error while performing Re-class to EFT Reject Internal Account";
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

	@Test(priority = 5, enabled = true)
	public void reclassToEFTClearing() {
		final String fields = "DEBIT.ACCT.NO," + "DEBIT.CURRENCY," + "DEBIT.AMOUNT," + "ORDERING.CUST:1,"
				+ "CREDIT.ACCT.NO," + "ORDERING.BANK:1,";
		final String values = "CAD1097000017817," + "CAD," + "10.00," + "Clearing Payments for today,"
				+ "CAD1011000017817," + "customer,";
		String transactionID;

		stepDescription = "Perform Re-class to EFT Clearing Internal Account";
		stepExpected = "Re-class to EFT Clearing Internal Account performed successfully";

		if (loginResult) {
			transactionID = financialTransaction(CREATE, "Account Transfer - Transfer between Accounts", fields,
					values);

			if (transactionID.contains(ERROR)) {
				stepActual = "Error while performing Re-class to EFT Clearing Internal Account";
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
}
