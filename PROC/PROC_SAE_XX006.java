package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PROC_SAE_XX006 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "2018-03-15";
	private String customer;
	private String arrangement1;
	private String arrangement2;
	private String mainArrangement;
	private String beneficiary;
	private String reserveAccount1;
	private String reserveAccount2;
	private String reserveAccount3;
	private String transactionId;
	private String fundsTransferFields;
	private String fundsTransferValues;
	private String step1Fields;
	private String step1Values;
	private String step2Fields;
	private String step2Values;
	private String defaultStep2Fields;
	private String defaultStep2Values;
	private String arrangementActivityFields;
	private String arrangementActivityValues;
	private String closureFields;
	private String closureValues;
	boolean result = false;
	private String settlementFields;
	private String settlementValues;
	private String reserveAccountProduct = "Reserve Account -LBC Bus Serv";
	String beneficiaryFields;
	String beneficiaryValues;

	@Test(priority = 1, enabled = true)
	@Parameters({ "customerType", "productGroup", "product", "branch" })
	public void preCondition(final String customerType, final String productGroup, final String product,
			@Optional("LAURENTIAN BANK - 523") final String branch) {

		stepDescription = "Create Non Personal Client, beneficiary and " + product;
		stepExpected = "Personal Client, Beneficiary and " + product + " created successfully";

		if (loginResult) {
			switchToBranch(branch);
			customer = createDefaultCustomer(customerType, productGroup, ROLEBASED_SAE);
			if (customer == null || customer.contains(ERROR)) {
				stepActual = "Error while creating customer";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {
				arrangement1 = createDefaultArrangement(productGroup, product, ROLEBASED_SAE, customer, "+0d");

				beneficiaryFields = DefaultVariables.beneficiaryFields + "OWNING.CUSTOMER," + "BEN.CUSTOMER,";
				beneficiaryValues = DefaultVariables.beneficiaryValues + customer + ","
						+ createdCustomers.get(customer).getCustomerName() + ",";

				beneficiary = beneficiaryCode(CREATE, "EFT Client", "", beneficiaryFields, beneficiaryValues);

				if (arrangement1 == null || arrangement1.contains(ERROR)) {
					stepActual = "Error while creating " + product + " Arrangement";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				} else if (beneficiary == null || beneficiary.contains(ERROR)) {
					stepActual = "Error while creating Beneficiary for customer: " + customer;
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

		stepDescription = "Create Three Reserve Account for Customer :" + customer;
		stepExpected = "Three Reserve Account created successfully";

		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			step1Fields = "CUSTOMER:1," + "CURRENCY,";
			step1Values = customer + "," + "CAD,";
			step2Fields = "Statement#STATEMENT.REQD:1," + "PRIMARY.OFFICER," + "CA.INTEND.USE," + "CA.THIRD.PARTY,"
					+ "Credit Interest$Fixed/Floating/Linked#FLOATING.INDEX:1,"
					+ "Debit Interest$Fixed/Floating/Linked#FLOATING.INDEX:1,";
			step2Values = "No," + "10," + "18," + "N," + "11," + "11,";

			reserveAccount1 = arrangements(CREATE, SERVICING_ACCOUNTS, reserveAccountProduct, ROLEBASED_SAE, customer,
					step1Fields, step1Values, step2Fields, step2Values);

			step2Fields = "Statement#STATEMENT.REQD:1," + "PRIMARY.OFFICER," + "CA.INTEND.USE," + "CA.THIRD.PARTY,"
					+ "Credit Interest$Fixed/Floating/Linked#FIXED.RATE:1,"
					+ "Debit Interest$Fixed/Floating/Linked#FIXED.RATE:1,";
			step2Values = "No," + "10," + "18," + "N," + "2.50," + "2.50,";

			reserveAccount2 = arrangements(CREATE, SERVICING_ACCOUNTS, reserveAccountProduct, ROLEBASED_SAE, customer,
					step1Fields, step1Values, step2Fields, step2Values);
			reserveAccount3 = arrangements(CREATE, SERVICING_ACCOUNTS, reserveAccountProduct, ROLEBASED_SAE, customer,
					step1Fields, step1Values, step2Fields, step2Values);

			if (reserveAccount1 == null || reserveAccount2 == null || reserveAccount3 == null
					|| reserveAccount1.contains(ERROR) || reserveAccount2.contains(ERROR)
					|| reserveAccount3.contains(ERROR)) {
				stepActual = "Error while creating Reserve Account Arrangement for customer: " + customer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void linkReserveAccountToLoanAfterCreation(final String productGroup, final String product) {

		stepDescription = "Link Reserve Account " + reserveAccount1 + " and " + product + "Arrangement: " + arrangement1
				+ " after Loan Arrangement Creation";
		stepExpected = "  Reserve Account " + reserveAccount1 + " and " + product + "Arrangement: " + arrangement1
				+ " Linked Successfully";

		if (arrangement1 == null || arrangement1.contains(ERROR) || reserveAccount1 == null
				|| reserveAccount1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			arrangementActivityFields = "Account$Reserve Account & Other Details#CA.RSRV.ACCOUNT:1,"
					+ "Account$Reserve Account & Other Details#CA.RSRV.PRIMARY:1,";
			arrangementActivityValues = reserveAccount1 + "," + "Yes,";

			result = arrangementActivity(CREATE, "UPDATE ACTIVITY FOR ACCOUNT", arrangement1, productGroup,
					arrangementActivityFields, arrangementActivityValues);
			if (!result) {
				stepActual = "Error while linking Reserve Account " + reserveAccount1 + " to Loan: " + arrangement1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			arrangementActivityFields = "PAYMENT.TYPE+:2," + "PAYMENT.METHOD:2," + "PAYMENT.FREQ:2," + "PROPERTY:2:1,"
					+ "DUE.FREQ:1:2," + "ACTUAL.AMT:2:1," + "BILL.TYPE:2,";
			arrangementActivityValues = "RESERVEACCT," + "Due," + "e0Y e1M e0W o15D e0F," + "RESERVEACCT,"
					+ "e0Y e1M e0W o15D e0F," + "200.00," + "INSTALLMENT,";

			result = arrangementActivity(CREATE, "RENEGOTIATE ACTIVITY WITHOUTPAYMENT", arrangement1, productGroup,
					arrangementActivityFields, arrangementActivityValues);
			if (!result) {
				stepActual = "Error while defining payment schedule for Reserve Account recently linked to Loan Arrangement: "
						+ arrangement1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void linkReserveAccountToLoanDuringCreation(final String productGroup, final String product) {

		stepDescription = "Link Reserve Account " + reserveAccount2 + " and " + product
				+ " Arrangement during Loan Creation";
		stepExpected = "  Reserve Account " + reserveAccount2 + " and " + product + " Arrangement Linked Successfully";

		if (reserveAccount2 == null || reserveAccount2.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			step1Fields = "CUSTOMER:1," + "CURRENCY,";
			step1Values = customer + "," + "CAD,";

			if (DefaultVariables.productFields.containsKey(product)) {
				defaultStep2Fields = DefaultVariables.productFields.get(product);
				defaultStep2Values = DefaultVariables.productValues.get(product);
			} else {
				defaultStep2Fields = DefaultVariables.productGroupFields.get(productGroup);
				defaultStep2Values = DefaultVariables.productGroupValues.get(productGroup);
			}

			step2Fields = defaultStep2Fields + "Account$Reserve Account & Other Details#CA.RSRV.ACCOUNT:1,"
					+ "Account$Reserve Account & Other Details#CA.RSRV.PRIMARY:1," + "PAYMENT.TYPE+:2,"
					+ "PAYMENT.METHOD:2," + "PAYMENT.FREQ:2," + "PROPERTY:2:1," + "DUE.FREQ:1:2," + "ACTUAL.AMT:2:1,"
					+ "BILL.TYPE:2,";
			step2Values = defaultStep2Values + reserveAccount2 + "," + "Yes," + "RESERVEACCT," + "Due,"
					+ "e0Y e1M e0W o15D e0F," + "RESERVEACCT," + "e0Y e1M e0W o15D e0F," + "200.00," + "INSTALLMENT,";

			arrangement2 = arrangements(CREATE, productGroup, product, ROLEBASED_SAE, customer, step1Fields,
					step1Values, step2Fields, step2Values);
			if (arrangement2 == null || arrangement2.contains(ERROR)) {
				stepActual = "Error Linking Reserve Account: " + reserveAccount2 + " while Creating" + product
						+ " Arrangement for customer: " + customer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	public void modifyReserveAccount_AccountStatic() {

		stepDescription = "Modify Reserve Account " + reserveAccount2 + " by Performing Update account activity ";
		stepExpected = "  Reserve Account " + reserveAccount2 + " Modified Successfully";

		if (reserveAccount2 == null || reserveAccount2.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			arrangementActivityFields = "CA.INTEND.USE," + "CA.THIRD.PARTY,";
			arrangementActivityValues = "18," + "Y,";

			result = arrangementActivity(CREATE, "Update account", reserveAccount2, SERVICING_ACCOUNTS,
					arrangementActivityFields, arrangementActivityValues);
			if (!result) {
				stepActual = "Error while modifying Static details of Reserve Account: " + reserveAccount2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	public void modifyReserveAccount_ChargeDefinition() {

		stepDescription = "Modify Reserve Account " + reserveAccount2 + " by modifying Charge Definition";
		stepExpected = " Charge Definition Modified Successfully for  Reserve Account " + reserveAccount2;

		if (reserveAccount2 == null || reserveAccount2.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			arrangementActivityFields = "FIXED.AMOUNT,";
			arrangementActivityValues = "20,";

			result = arrangementActivity(CREATE, "Change and capitalize misc fees", reserveAccount2, SERVICING_ACCOUNTS,
					arrangementActivityFields, arrangementActivityValues);
			if (!result) {
				stepActual = "Error while performing Change and Capitalise NSF Fee for Arrangement: " + reserveAccount2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 7, enabled = true)

	public void modifyReserveAccount_Customer() {
		stepDescription = "Modify Reserve Account " + reserveAccount2 + " by performing update Activity for customer";
		stepExpected = " Reserve Account " + reserveAccount2
				+ " Modified Successfully by performing update Activity for customer ";

		if (reserveAccount2 == null || reserveAccount2.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			arrangementActivityFields = "Customer$Customer#DELIVERY.REQD:1," + "Customer$Statement#STATEMENT.REQD:1,";
			arrangementActivityValues = "YES," + "Yes,";

			result = arrangementActivity(CREATE, "UPDATE ACTIVITY FOR CUSTOMER", reserveAccount2, SERVICING_ACCOUNTS,
					arrangementActivityFields, arrangementActivityValues);
			if (!result) {
				stepActual = "Error while performing Update activity for cutomer for Arrangement: " + reserveAccount2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 8, enabled = true)
	public void modifyReserveAccount_InterestCalculation() {
		stepDescription = "Modify Reserve Account " + reserveAccount2 + " by Changing Interest Calculation";
		stepExpected = " Interest Calculation for Reserve Account " + reserveAccount2 + " Modified Successfully ";

		if (reserveAccount2 == null || reserveAccount2.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			arrangementActivityFields = "FLOATING.INDEX:1," + "FIXED.RATE:1,";
			arrangementActivityValues = "11," + " ,";

			result = arrangementActivity(CREATE, "UPDATE ACTIVITY FOR CRINTEREST", reserveAccount2, SERVICING_ACCOUNTS,
					arrangementActivityFields, arrangementActivityValues);
			if (!result) {
				stepActual = "Error while performing Update activity for CRINTEREST for Arrangement: "
						+ reserveAccount2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			result = arrangementActivity(CREATE, "UPDATE ACTIVITY FOR DRINTEREST", reserveAccount2, SERVICING_ACCOUNTS,
					arrangementActivityFields, arrangementActivityValues);

			if (!result) {
				stepActual = "Error while performing Update activity for DRINTEREST for Arrangement: "
						+ reserveAccount2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 9, enabled = true)
	public void modifyReserveAccount_Officers() {
		stepDescription = "Modify Reserve Account " + reserveAccount2 + " by performing update Activity for Officers";
		stepExpected = " Reserve Account " + reserveAccount2
				+ " Modified Successfully by performing update Activity for Officers ";

		if (reserveAccount2 == null || reserveAccount2.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			arrangementActivityFields = "PRIMARY.OFFICER,";
			arrangementActivityValues = "1,";

			result = arrangementActivity(CREATE, "UPDATE ACTIVITY FOR OFFICERS", reserveAccount2, SERVICING_ACCOUNTS,
					arrangementActivityFields, arrangementActivityValues);
			if (!result) {
				stepActual = "Error while performing Update activity for Officers for Arrangement: " + reserveAccount2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 10, enabled = true)
	public void modifyReserveAccount_Arrangement() {
		stepDescription = "Renegotiate Reserve Account Arrangement: " + reserveAccount1;
		stepExpected = " RENEGOTIATE ACTIVITY FOR ARRANGEMENT for Reserve Account:  " + reserveAccount1
				+ " performed Successfully ";

		if (reserveAccount1 == null || reserveAccount1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			arrangementActivityFields = "PRIMARY.OFFICER," + "CA.INTEND.USE," + "CA.THIRD.PARTY,"
					+ "Credit Interest$Fixed/Floating/Linked#FIXED.RATE:1,"
					+ "Debit Interest$Fixed/Floating/Linked#FIXED.RATE:1,";
			arrangementActivityValues = "1," + "18," + "Y," + "2.50," + "2.50,";

			result = arrangementActivity(OPEN, "RENEGOTIATE ACTIVITY FOR ARRANGEMENT", reserveAccount1,
					SERVICING_ACCOUNTS, arrangementActivityFields, arrangementActivityValues);
			inputTable.clearFieldValue("CRINTEREST", "cancel").click();
			inputTable.clearFieldValue("DRINTEREST", "cancel").click();
			result = multiInputData(arrangementActivityFields, arrangementActivityValues, false);
			if (toolElements.toolsButton(COMMIT_DEAL).getAttribute("src").contains("txncommit_dis")) {
				toolElements.toolsButton(VALIDATE_DEAL).click();
			}
			toolElements.toolsButton(COMMIT_DEAL).click();
			result = inputTable.verifyAcceptOverride();

			if (!result) {
				stepActual = "Error while performing RENEGOTIATE ACTIVITY FOR ARRANGEMENT for Arrangement: "
						+ reserveAccount1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 11, enabled = true)

	public void viewReserveAccount() {
		stepDescription = "View Reserve Account: " + reserveAccount1;
		stepExpected = reserveAccount1 + " Opened Successfully ";

		if (reserveAccount1 == null || reserveAccount1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			mainArrangement = findArrangement(AUTHORISED, reserveAccount1, ARRANGEMENT, ROLEBASED_SAE,
					SERVICING_ACCOUNTS, reserveAccountProduct, CAD);

			if (!reserveAccount1.equals(mainArrangement)) {
				stepActual = "Error while searching Reserve Account: " + reserveAccount1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 12, enabled = true)
	public void addNarrativeToAccountTransaction() {
		stepDescription = "Add Narrative to Account Transaction berween  Reserve Accounts: " + reserveAccount1 + " and "
				+ reserveAccount2;
		stepExpected = "Narratives to Account Transaction added successfully ";

		if (reserveAccount1 == null || reserveAccount1.contains(ERROR) || reserveAccount2 == null
				|| reserveAccount2.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			fundsTransferFields = "DEBIT.ACCT.NO," + "DEBIT.CURRENCY ," + "DEBIT.AMOUNT," + "DEBIT.THEIR.REF,"
					+ "CREDIT.ACCT.NO," + "CREDIT.THEIR.REF,";
			fundsTransferValues = reserveAccount1 + "," + "CAD," + "100," + "Debit Narrative," + reserveAccount2 + ","
					+ "Credit Narrative,";

			transactionId = financialTransaction(CREATE, "Account Transfer - Transfer between Accounts",
					fundsTransferFields, fundsTransferValues);

			if (transactionId == null || transactionId.contains(ERROR)) {
				stepActual = "Error while Performing Funds Transfer between : " + reserveAccount1 + " and "
						+ reserveAccount2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 13, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void closeReserveAccounts(final String productGroup, final String product) {
		boolean result1 = false;
		boolean result2 = false;
		boolean result3 = false;

		stepDescription = "Close All Reserve Account and " + product + "Arrangements";
		stepExpected = "All Reserve Accounts and " + product + " Arrangements closed Successfully";

		if (arrangement1 == null || arrangement1.contains(ERROR) || arrangement2 == null || arrangement2.contains(ERROR)
				|| reserveAccount1 == null || reserveAccount1.contains(ERROR) || reserveAccount2 == null
				|| reserveAccount2.contains(ERROR) || reserveAccount3 == null || reserveAccount3.contains(ERROR)
				|| transactionId == null || transactionId.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementActivity(CREATE, "WRITE.OFF ACTIVITY FOR BALANCE.MAINTENANCE", arrangement1,
					productGroup, "", "")
					&& arrangementActivity(CREATE, "CLOSE ACTIVITY FOR ARRANGEMENT", arrangement1, productGroup, "", "")
					&& arrangementActivity(CREATE, "WRITE.OFF ACTIVITY FOR BALANCE.MAINTENANCE", arrangement2,
							productGroup, "", "")
					&& arrangementActivity(CREATE, "CLOSE ACTIVITY FOR ARRANGEMENT", arrangement2, productGroup, "",
							"");
			if (!result) {
				stepActual = "Error while closing Two " + product + " Arrangements";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			fundsTransferFields = "CREDIT.AMOUNT," + "CREDIT.ACCT.NO," + "DEBIT.ACCT.NO," + "DEBIT.CURRENCY,"
					+ "CREDIT.CURRENCY,";
			fundsTransferValues = "200.00," + reserveAccount1 + "," + "810000011958," + "CAD," + "CAD,";
			transactionId = financialTransaction(CREATE, "Account Transfer - Transfer between Accounts",
					fundsTransferFields, fundsTransferValues);

			if (transactionId == null || transactionId.contains(ERROR)) {
				stepActual = "Error while Performing Funds Transfer between Accounts: " + reserveAccount1
						+ " and 810000011958";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {

				closureFields = "EFFECTIVE.DATE," + "CLOSURE.REASON,";
				closureValues = "+1d," + "Other,";
				settlementFields = "CREDIT.VALUE.DATE," + "DEBIT.VALUE.DATE," + "CREDIT.ACCT.NO,";
				settlementValues = "+1d," + "+1d," + "CAD1051000011000,";

				result1 = requestClosure(COMPLETE, reserveAccount1, "Request Closure", SERVICING_ACCOUNTS,
						closureFields, closureValues, "Settle by Funds Transfer", settlementFields, settlementValues);
			}

			closureFields = "EFFECTIVE.DATE," + "CLOSURE.REASON,";
			closureValues = "+1d," + "Other,";
			settlementFields = "CREDIT.VALUE.DATE,";
			settlementValues = "+1d,";
			result2 = requestClosure(COMPLETE, reserveAccount2, "Request Closure", SERVICING_ACCOUNTS, closureFields,
					closureValues, "Write-Off to P&L", settlementFields, settlementValues);

			fundsTransferFields = "CREDIT.AMOUNT," + "CREDIT.ACCT.NO," + "DEBIT.ACCT.NO," + "DEBIT.CURRENCY,"
					+ "CREDIT.CURRENCY,";
			fundsTransferValues = "100.00," + reserveAccount3 + "," + "810000011958," + "CAD," + "CAD,";
			transactionId = financialTransaction(CREATE, "Account Transfer - Transfer between Accounts",
					fundsTransferFields, fundsTransferValues);

			if (transactionId == null || transactionId.contains(ERROR)) {
				stepActual = "Error while Performing Funds Transfer between Accounts: " + reserveAccount3
						+ " and 810000011958";

				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {
				closureFields = "EFFECTIVE.DATE," + "CLOSURE.REASON,";
				closureValues = "+0d," + "Other,";

				settlementFields = "PAYMENT.ORDER.PRODUCT," + "BENEFICIARY.ID,";
				settlementValues = "ACHACCL," + beneficiary + ",";
				result3 = requestClosure(COMPLETE, reserveAccount3, "Request Closure", SERVICING_ACCOUNTS,
						closureFields, closureValues, "Settle by Payment Order", settlementFields, settlementValues);
			}
			if (!result1) {
				stepActual = "Error closing Reserve Account: " + reserveAccount1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else if (!result2) {
				stepActual = "Error closing Reserve Account: " + reserveAccount2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else if (!result3) {
				stepActual = "Error closing Reserve Account: " + reserveAccount3;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

		}
		softVerify.assertAll();
	}

	@Test(priority = 14, enabled = true)
	public void viewClosedReserveAccount() {
		stepDescription = "View closed  Reserve Account: " + reserveAccount1;
		stepExpected = "Reserve Account " + reserveAccount1 + " is opened Successfully in Matured/Closed Tab";

		if (reserveAccount1 == null || reserveAccount1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			mainArrangement = findArrangement(MATURED_CLOSED, reserveAccount1, ARRANGEMENT, ROLEBASED_SAE,
					SERVICING_ACCOUNTS, reserveAccountProduct, CAD);
			if (!reserveAccount1.equals(mainArrangement)) {
				stepActual = "Error while searching Reserve Account: " + reserveAccount1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 15, enabled = true)
	public void restoreClosedReserveAccount() {
		stepDescription = "Restore closed  Reserve Account: " + reserveAccount1;
		stepExpected = "Reserve Account " + reserveAccount1 + " Restored Successfully";

		if (reserveAccount1 == null || reserveAccount1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			mainArrangement = findArrangement(MATURED_CLOSED, reserveAccount1, ARRANGEMENT, ROLEBASED_SAE,
					SERVICING_ACCOUNTS, reserveAccountProduct, CAD);
			if (mainArrangement == null || mainArrangement.contains(ERROR)) {
				stepActual = "Error finding " + reserveAccount1 + " Arrangement";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {
				versionScreen.hyperLink("New Activity").click();
				switchToPage(LASTPAGE, false);
				compositeScreen.textAction("RESTORE ACTIVITY FOR ARRANGEMENT", "Do Activity").click();
				switchToPage(LASTPAGE, false);
				inputData("EFFECTIVE.DATE", "+1d", false);
				toolElements.toolsButton(VALIDATE_DEAL).click();
				switchToPage(LASTPAGE, false);
				result = true;
				if (toolElements.toolsButton(COMMIT_DEAL).getAttribute("src").contains("txncommit_dis")) {
					toolElements.toolsButton(VALIDATE_DEAL).click();
				}
				toolElements.toolsButton(COMMIT_DEAL).click();
				result = inputTable.verifyAcceptOverride();
				if (!result) {
					stepActual = "Error Restoring account: " + reserveAccount1;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		}
		softVerify.assertAll();
	}
}
