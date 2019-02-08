package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.DefaultVariables;

public class PROC_OP_MG_XX008 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.4";
	private String branch = "B2B Branch 623";
	private String customer1;
	private String customer2;
	private String mainArrangement;
	private String reserveAccount1;
	private String reserveAccount2;
	private String beneficiary;
	private String transactionID;
	private String paymentID;
	private String step1Fields;
	private String step1Values;
	private String step2Fields;
	private String step2Values;
	private String activityFields;
	private String activityValues;
	boolean result = false;

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void preCondition(final String productGroup, final String product) {

		stepDescription = "Create two customers, beneficiary and " + product + " arrangement";
		stepExpected = "Two customers, beneficiary and " + product
				+ " arrangement created successfully";

		if (loginResult) {
			switchToBranch(branch);

			customer1 = createDefaultCustomer("", productGroup, ROLEBASED_LENDING);
			customer2 = createDefaultCustomer("", productGroup, ROLEBASED_LENDING);

			if (customer1 == null || customer1.contains(ERROR) || customer2 == null || customer2.contains(ERROR)) {
				stepActual = "Error while creating customers";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {

				final ArrangementData mortgageData = new ArrangementData("mortgageArrangement", productGroup, product)
						.withCustomers(customer1, createdCustomers.get(customer1), "", "100,", "100,")
						.withSettlement("Beneficiary", "NEW").build();

				mainArrangement = createDefaultArrangement(mortgageData);
				beneficiary = mortgageData.getSettlementAccount();

				if (mainArrangement == null || mainArrangement.contains(ERROR) || beneficiary == null
						|| beneficiary.contains(ERROR)) {
					stepActual = "Error while creating beneficiary and " + product + " arrangement";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Login failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
	}

	@Test(priority = 2, enabled = true)
	public void createReserveAccount() {

		stepDescription = "Create reserve account for customer: " + customer1;
		stepExpected = "Reserve account for customer: " + customer1 + " created successfully";

		if (customer1 == null || customer1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			step1Fields = "CUSTOMER:1," + "CURRENCY,";
			step1Values = customer1 + ",CAD,";

			step2Fields = DefaultVariables.reserveAccountFields + "NSF Fee$FIXED.AMOUNT,";
			step2Values = DefaultVariables.reserveAccountValues + "100.00,";

			reserveAccount1 = arrangements(CREATE, SERVICING_ACCOUNTS, "Reserve Account", ROLEBASED_LENDING, customer1,
					step1Fields, step1Values, step2Fields, step2Values);

			if (reserveAccount1 == null || reserveAccount1.contains(ERROR)) {
				stepActual = "Error while creating Reserve Account for customer: " + customer1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void linkReserveAccountToMortgage(final String productGroup, final String product) {

		stepDescription = "Link reserve account: " + reserveAccount1 + " to " + product + " arrangement: "
				+ mainArrangement;
		stepExpected = "reserve account: " + reserveAccount1 + " and " + product + " arrangement: " + mainArrangement
				+ " linked successfully";

		if (mainArrangement == null || mainArrangement.contains(ERROR) || reserveAccount1 == null
				|| reserveAccount1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			activityFields = "Account$Reserve Account & Other Details#CA.RSRV.ACCOUNT:1,"
					+ "Account$Reserve Account & Other Details#CA.RSRV.PRIMARY:1,";
			activityValues = reserveAccount1 + ",Yes,";

			result = arrangementActivity(CREATE, "UPDATE ACTIVITY FOR ACCOUNT", mainArrangement, productGroup,
					activityFields, activityValues);

			if (!result) {
				stepActual = "Error while linking Reserve Account: " + reserveAccount1 + " to " + product
						+ " arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void schedulePaymentsToReserveAccount(final String productGroup, final String product) {

		stepDescription = "Schedule payments to reserve account: " + reserveAccount1;
		stepExpected = "Payments to reserve account: " + reserveAccount1 + " scheduled successfully";

		final String fields;
		final String values;

		if (mainArrangement == null || mainArrangement.contains(ERROR) || reserveAccount1 == null
				|| reserveAccount1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			if (product.contains(HELOC)) {
				activityFields = "Schedule$PAYMENT.TYPE+:3," + "Schedule$PAYMENT.METHOD:3," + "Schedule$PAYMENT.FREQ:3,"
						+ "Schedule$PROPERTY:3:1," + "Schedule$ACTUAL.AMT:3:1," + "Schedule$BILL.TYPE:3,"
						+ "Settlement$Advanced - Pay In#PAYMENT.TYPE+:4:1,";
				activityValues = "RESERVEACCT," + "Due," + "e0Y e1M e0W e0D e0F," + "RESERVEACCT," + "500,"
						+ "INSTALLMENT," + "RESERVEACCT,";
			} else {
				activityFields = "Schedule$PAYMENT.TYPE+:2," + "Schedule$PAYMENT.METHOD:2," + "Schedule$PAYMENT.FREQ:2,"
						+ "Schedule$PROPERTY:2:1," + "Schedule$ACTUAL.AMT:2:1," + "Schedule$BILL.TYPE:2,";
				activityValues = "RESERVEACCT," + "Due," + "e0Y e1M e0W e0D e0F," + "RESERVEACCT," + "500,"
						+ "INSTALLMENT,";
			}
			result = arrangementActivity(CREATE, "CHANGE ACTIVITY FOR SCHEDULE", mainArrangement, productGroup,
					activityFields, activityValues);

			if (product.contains(HELOC)) {
				fields = "CURRENCY," + "CURRENT.AMOUNT.BAL," + "CPTY.ACCT.NO,";
				values = CAD + ",100.00," + beneficiary + ",";

				result = reoccurringFixedTransfer("Setup", "Internal", customer1, mainArrangement, fields, values);
			}

			if (!result) {
				stepActual = "Error while scheduling payments to reserve account: " + reserveAccount1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	public void adHocPaymentToReserveAccountViaCheque() {

		stepDescription = "Issue ad-hoc cheque payment to reserve account: " + reserveAccount1;
		stepExpected = "Ad-hoc cheque payment to reserve account: " + reserveAccount1 + " issued successfully";

		final String fields;
		final String values;

		if (reserveAccount1 == null || reserveAccount1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			fields = "PRIMARY.ACCOUNT," + "TRANSACTION:1," + "SURROGATE.AC:1," + "CURRENCY:1," + "AMOUNT:1,"
					+ "CHEQUE.NUMBER:1,";
			values = "CAD1100100017623," + "Credit," + reserveAccount1 + "," + CAD + ",10000," + "1234534,";

			transactionID = financialTransaction(CREATE, "Teller Financial Services", fields, values);

			if (transactionID == null || transactionID.contains(ERROR)) {
				stepActual = "Error while issuing Ad-hoc cheque payment to reserveAccount: " + reserveAccount1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	public void adHocPaymentToReserveAccountViaEFT() {

		stepDescription = "Issue ad-hoc electronic payment to reserve account: " + reserveAccount1;
		stepExpected = "Ad-hoc electronic payment to reserve account: " + reserveAccount1 + " issued successfully";

		final String fields;
		final String values;

		if (reserveAccount1 == null || reserveAccount1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			fields = "PAYMENT.ORDER.PRODUCT," + "CREDIT.ACCOUNT," + "PAYMENT.CURRENCY," + "PAYMENT.AMOUNT,"
					+ "ORDERING.CUSTOMER.SSI,";
			values = "ACHCREDIT," + reserveAccount1 + "," + CAD + ",600," + beneficiary + ",";

			paymentID = createAdHocPayment(CREATE, EFT, reserveAccount1, fields, values);

			if (paymentID == null || paymentID.contains(ERROR)) {
				stepActual = "Error while issuing Ad-hoc electronic payment to reserveAccount: " + reserveAccount1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 7, enabled = true)
	public void chequePaymentFromReserveAccount() {

		stepDescription = "Issue cheque payment from reserve account: " + reserveAccount1;
		stepExpected = "Cheque payment from reserve account: " + reserveAccount1 + " issued successfully";

		final String fields;
		final String values;

		if (reserveAccount1 == null || reserveAccount1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			fields = "PAYMENT.ORDER.PRODUCT," + "DEBIT.ACCOUNT," + "DEBIT.CCY," + "PAYMENT.CURRENCY,"
					+ "PAYMENT.AMOUNT," + "DEBIT.VALUE.DATE," + "BENEFICIARY.ID," + "ORDERING.CUSTOMER,";
			values = "CHQADHOC," + reserveAccount1 + "," + CAD + "," + CAD + ",1600," + "+0d," + DefaultVariables.dummyBeneficiary + ","
					+ customer1 + ",";

			paymentID = createAdHocPayment(CREATE, CHEQUE, reserveAccount1, fields, values);

			if (paymentID == null || paymentID.contains(ERROR)) {
				stepActual = "Error while issuing cheque payment from reserveAccount: " + reserveAccount1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 8, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void removeScheduledPaymentFromReserveAccount(final String productGroup, final String product) {

		stepDescription = "Remove scheduled payment from reserve account: " + reserveAccount1;
		stepExpected = "Scheduled payment from reserve account: " + reserveAccount1 + " removed successfully";

		if (mainArrangement == null || mainArrangement.contains(ERROR) || reserveAccount1 == null
				|| reserveAccount1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			if (!product.contains(HELOC)) {
				activityFields = "Schedule$PAYMENT.TYPE<:2,";
				activityValues = " ,";
			}
			result = arrangementActivity(CREATE, "CHANGE ACTIVITY FOR SCHEDULE", mainArrangement, productGroup,
					activityFields, activityValues);

			if (!result) {
				stepActual = "Error while removing scheduled payments from reserve account: " + reserveAccount1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 9, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void deLinkReserveAccountFromMortgage(final String productGroup, final String product) {

		stepDescription = "De-link reserve account: " + reserveAccount1 + " from " + product + " arrangement: "
				+ mainArrangement;
		stepExpected = "Reserve account: " + reserveAccount1 + " and " + product + " arrangement: " + mainArrangement
				+ " de-linked successfully";

		if (mainArrangement == null || mainArrangement.contains(ERROR) || reserveAccount1 == null
				|| reserveAccount1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			activityFields = "Account$Reserve Account & Other Details#CA.RSRV.ACCOUNT:1,"
					+ "Account$Reserve Account & Other Details#CA.RSRV.PRIMARY:1,";
			activityValues = " ," + " ,";

			result = arrangementActivity(CREATE, "UPDATE ACTIVITY FOR ACCOUNT", mainArrangement, productGroup,
					activityFields, activityValues);

			if (!result) {
				stepActual = "Error while de-linking Reserve Account: " + reserveAccount1 + " and " + product
						+ " arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 10, enabled = true)
	public void closeReserveAccount() {

		stepDescription = "close reserve account: " + reserveAccount1;
		stepExpected = "Reserve account: " + reserveAccount1 + " closed successfully";

		final String closureFields;
		final String closureValues;

		if (reserveAccount1 == null || reserveAccount1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			closureFields = "EFFECTIVE.DATE," + "CLOSURE.REASON,";
			closureValues = "+0d," + "Cancelation,";

			result = requestClosure("COMPLETE", reserveAccount1, "Request Closure", SERVICING_ACCOUNTS, closureFields,
					closureValues, "Write-Off to P&L", "", "");

			if (!result) {
				stepActual = "Error while closing reserve account: " + reserveAccount1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 11, enabled = true)
	public void overdrawZeroBalanceReserveAccount() {

		stepDescription = "Overdraw zero balance reserve account";
		stepExpected = "Zero balance reserve account overdrawn successfully";

		final String fields;
		final String values;

		if (customer1 == null || customer1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			reserveAccount2 = createDefaultArrangement(SERVICING_ACCOUNTS, "Reserve Account", ROLEBASED_LENDING,
					customer1, "+0d");

			if (reserveAccount2 == null || reserveAccount2.contains(ERROR)) {
				stepActual = "Error while creating Reserve Account for customer: " + customer1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {

				fields = "PAYMENT.ORDER.PRODUCT," + "DEBIT.ACCOUNT," + "DEBIT.CCY," + "PAYMENT.CURRENCY,"
						+ "PAYMENT.AMOUNT," + "DEBIT.VALUE.DATE," + "BENEFICIARY.ID," + "ORDERING.CUSTOMER,";
				values = "CHQADHOC," + reserveAccount2 + "," + CAD + "," + CAD + ",100," + "+0d," + DefaultVariables.dummyBeneficiary
						+ "," + customer1 + ",";

				paymentID = createAdHocPayment(CREATE_AUTHORISE, CHEQUE, reserveAccount2, fields, values);

				if (paymentID == null || paymentID.contains(ERROR)) {
					stepActual = "Error while overdrawing zero balance reserve account: " + reserveAccount2;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 12, enabled = true)
	public void changeReserveAccountCustomerName() {

		stepDescription = "Change customer name for reserve account: " + reserveAccount2;
		stepExpected = "Customer name for reserve account: " + reserveAccount2 + " changed successfully";

		final String fields;
		final String values;

		if (reserveAccount2 == null || reserveAccount2.contains(ERROR)) {
			stepActual = "Error while creating Reserve Account for customer: " + customer1;
			softVerify.fail(stepActual);
			stepResult = StatusAs.FAILED;
		} else {

			activityFields = "CUSTOMER:1,";
			activityValues = customer2 + ",";

			arrangementActivity(OPEN, "CHANGE ACTIVITY FOR CUSTOMER", reserveAccount2, SERVICING_ACCOUNTS,
					activityFields, activityValues);
			multiInputData(activityFields, activityValues, false);
			toolElements.toolsButton(VALIDATE_DEAL).click();
			switchToPage(LASTPAGE, false);

			fields = "Customer$Customer#LIMIT.ALLOC.PERC:1," + "Customer$Customer#GL.ALLOC.PERC:1,";
			values = "100.00," + "100.00,";

			multiInputData(fields, values, false);
			result = inputTable.commitAndOverride();

			if (!result) {
				stepActual = "Error while changing customer name for reserve account: " + reserveAccount2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
