package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.DefaultVariables;

public class PROC_OP_MG_XX009 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version: 3.0";
	private String reserveAccountShortfall;
	private String reserveAccountSurPlus;
	private String beneficiary;
	private String customer;
	private String heloc;
	private String orderingClient;
	private String financialID;
	private String orderingCustomer;
	private int numberOfMortgages = 6;
	private String[] mortgage = new String[numberOfMortgages];
	private String[] totalPayoff;

	String fields;
	String values;
	private boolean result;

	@Test(priority = 1, enabled = true)
	@Parameters({ "product", "branch" })
	public void preCondition(final String product, @Optional("B2B Branch 623") final String branch) {

		stepDescription = "Create Six " + product + " Arrangements";
		stepExpected = product + " Arrangement created successfully";

		if (loginResult) {
			switchToBranch(branch);

			ArrangementData mortgage1Data = new ArrangementData("mortgage1", RETAIL_MORTGAGES, product)
					.withEffectiveDate("-1m")
					.withTerm("1Y")
					.withSettlement("Beneficiary", "NEW")
					.withDisbursement()
					.withRepayments()
					.build();
			mortgage[0] = createDefaultArrangement(mortgage1Data);
			beneficiary = mortgage1Data.getSettlementAccount();
			customer = mortgage1Data.getCustomers();

			ArrangementData mortgage2Data = new ArrangementData("mortgage2", RETAIL_MORTGAGES, product)
					.withEffectiveDate("-1m")
					.withDisbursement()
					.withRepayments()
					.build();
			mortgage[1] = createDefaultArrangement(mortgage2Data);

			ArrangementData mortgage3Data = new ArrangementData("mortgage3", RETAIL_MORTGAGES, product)
					.withEffectiveDate("-1m")
					.withTerm("1Y")
					.withDisbursement()
					.withRepayments()
					.withReserveAccount("NEW", true)
					.build();
			mortgage[2] = createDefaultArrangement(mortgage3Data);
			orderingCustomer = mortgage3Data.getCustomers();
			reserveAccountShortfall = mortgage3Data.getReserveAccount();

			ArrangementData mortgage4Data = new ArrangementData("mortgage4", RETAIL_MORTGAGES, product)
					.withEffectiveDate("-1m")
					.withDisbursement()
					.withRepayments()
					.build();
			mortgage[3] = createDefaultArrangement(mortgage4Data);

			ArrangementData mortgage5Data = new ArrangementData("mortgage5", RETAIL_MORTGAGES, product)
					.withEffectiveDate("-1m")
					.withTerm("1Y")
					.withDisbursement()
					.withRepayments()
					.withReserveAccount("NEW", true)
					.build();
			mortgage[4] = createDefaultArrangement(mortgage5Data);
			reserveAccountSurPlus = mortgage5Data.getReserveAccount();

			ArrangementData mortgage6Data = new ArrangementData("mortgage6", RETAIL_MORTGAGES, product)
					.withEffectiveDate("-1m")
					.withTerm("1Y")
					.withDisbursement()
					.withRepayments()
					.withEscrow(MUNICIPALITY)
					.build();
			mortgage[5] = createDefaultArrangement(mortgage6Data);
			orderingClient = mortgage6Data.getCustomers();

			ArrangementData helocData = new ArrangementData("heloc", RETAIL_MORTGAGES, HELOC)
					.withEffectiveDate("-1m")
					.withTerm("1Y")
					.withSettlement("Beneficiary", "NEW")
					.withDisbursement()
					.withRepayments()
					.build();
			heloc = createDefaultArrangement(helocData);

			if (mortgage[0] == null || mortgage[1] == null || mortgage[2] == null || mortgage[3] == null
					|| mortgage[4] == null || mortgage[5] == null || heloc == null || mortgage[0].contains(ERROR)
					|| mortgage[1].contains(ERROR) || mortgage[2].contains(ERROR) || mortgage[3].contains(ERROR)
					|| mortgage[4].contains(ERROR) || mortgage[5].contains(ERROR) || heloc.contains(ERROR)) {
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
	public void requestPayoffSimulation() {

		stepDescription = "Perform Request Payoff Simulation";
		stepExpected = "Request Payoff Simulation created successfully";

		fields = "EFFECTIVE.DATE," + "CLOSURE.REASON,";
		values = "+0d," + "Undesirable Client,";

		totalPayoff = new String[numberOfMortgages];

		if (mortgage[1] == null || mortgage[1].contains(ERROR) || mortgage[2] == null || mortgage[2].contains(ERROR)
				|| mortgage[3] == null || mortgage[3].contains(ERROR) || mortgage[4] == null
				|| mortgage[4].contains(ERROR) || mortgage[5] == null || mortgage[5].contains(ERROR)
				|| mortgage[0] == null || mortgage[0].contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as precondition failed";
			throw new SkipException(stepActual);
		} else {
			for (int i = 0; i < numberOfMortgages; i++) {
				result = requestClosure(SIMULATE, mortgage[i], "Request Payoff", RETAIL_MORTGAGES, fields, values, "",
						"", "");
				if (result) {
					totalPayoff[i] = Double.toString(Math.round(Double.parseDouble(retrievePayoffAmount(mortgage[i], RETAIL_MORTGAGES, "")) * 100) / 100.00);
					if (totalPayoff[i].equals("0.00") || totalPayoff[i] == null) {
						stepActual = "Error while fetching Total payoff amount for " + mortgage[i];
						softVerify.fail(stepActual);
						stepResult = StatusAs.FAILED;
					}
				} else {
					stepActual = "Error while performing request closure for " + mortgage[i];
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	public void deLinkReserveAccountFromMortgage() {

		stepDescription = "De-link reserve account from arrangement";
		stepExpected = "Reserve account de-linked successfully";

		fields = "Account$Reserve Account & Other Details#CA.RSRV.ACCOUNT:1,"
				+ "Account$Reserve Account & Other Details#CA.RSRV.PRIMARY:1,";
		values = " ," + " ,";

		if (mortgage[2] == null || mortgage[2].contains(ERROR) || mortgage[4] == null || mortgage[4].contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementActivity(CREATE_AUTHORISE, "UPDATE ACTIVITY FOR ACCOUNT", mortgage[2], RETAIL_MORTGAGES,
					fields, values)
					&& arrangementActivity(CREATE_AUTHORISE, "UPDATE ACTIVITY FOR ACCOUNT", mortgage[4],
							RETAIL_MORTGAGES, fields, values);

			if (!result) {
				stepActual = "Error while de-linking Reserve Account";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)

	public void payoffFullAmountCheque() {

		stepDescription = "Payoff Full Amount by cheque for " + mortgage[0];
		stepExpected = "Payoff Full Amount by cheque for " + mortgage[0] + " performed successfully ";

		fields = "PRIMARY.ACCOUNT," + "CONSOL.LEVEL.ADDON," + "TRANSACTION:1," + "SURROGATE.AC:1," + "CURRENCY:1,"
				+ "AMOUNT:1," + "CHEQUE.NUMBER:1,";
		values = "CAD1100100017623," + "No," + "Loanpayoff," + mortgage[0] + "," + "CAD," + totalPayoff[0] + ","
				+ "123456789,";

		if (mortgage[0] == null || mortgage[0].contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Login failed";
			throw new SkipException(stepActual);
		} else {
			financialID = financialTransaction(CREATE_AUTHORISE, "Teller Financial Services", fields, values);

			if (financialID == null || financialID.contains(ERROR)) {
				stepActual = "Error while performing Payoff Full Amount by cheque for " + mortgage[0];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	public void payoffShortfallMorethan50() {

		stepDescription = "Perform Payoff Shortfall more than $50 via Teller Financial Services Financial Transaction for "
				+ mortgage[1];
		stepExpected = "Payoff Shortfall more than $50 via Teller Financial Services Financial Transaction for "
				+ mortgage[1] + " Performed successfully ";

		fields = "PRIMARY.ACCOUNT," + "CONSOL.LEVEL.ADDON," + "TRANSACTION:1," + "SURROGATE.AC:1," + "CURRENCY:1,"
				+ "AMOUNT:1," + "CHEQUE.NUMBER:1," + "TRANSACTION:2," + "SURROGATE.AC:2," + "CURRENCY:2," + "AMOUNT:2,";
		values = "CAD1100100017623," + "No," + "Loanpayoff," + mortgage[1] + "," + "CAD," + totalPayoff[1] + ","
				+ "123456789," + "Debit," + "CAD1100000017623," + "CAD," + "75.00,";

		if (mortgage[1] == null || mortgage[1].contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Login failed";
			throw new SkipException(stepActual);
		} else {
			financialID = financialTransaction(CREATE_AUTHORISE, "Teller Financial Services", fields, values);
			if (financialID == null || financialID.contains(ERROR)) {
				stepActual = "Error while performing Payoff Shortfall more than $50 via Teller Financial Services Financial Transaction for "
						+ mortgage[1];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	public void payoffShortfallWithReserveAccount() {

		stepDescription = "Perform Payoff Shortfall with Reserve account: " + reserveAccountShortfall
				+ " via Teller Financial Services Financial Transaction for " + mortgage[2];
		stepExpected = "Payoff_Shortfall with Reserve account: " + reserveAccountShortfall
				+ " via Teller Financial Services Financial Transaction for " + mortgage[2]
				+ " Performed successfully ";

		String financialID;
		String reserveAccountBalance;
		int columnNumber;

		if (mortgage[2] == null || mortgage[2].contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Login failed";
			throw new SkipException(stepActual);
		} else {

			fields = "PAYMENT.ORDER.PRODUCT," + "DEBIT.ACCOUNT," + "DEBIT.CCY," + "PAYMENT.CURRENCY,"
					+ "PAYMENT.AMOUNT," + "DEBIT.VALUE.DATE," + "BENEFICIARY.ID," + "ORDERING.CUSTOMER,";
			values = "CHQADHOC," + reserveAccountShortfall + "," + "CAD," + "CAD," + "1600," + "+0d,"
					+ DefaultVariables.dummyBeneficiary + "," + orderingCustomer + ",";

			createAdHocPayment(CREATE, CHEQUE, reserveAccountShortfall, fields, values);
			findArrangement(AUTHORISED, reserveAccountShortfall, ARRANGEMENT, "", SERVICING_ACCOUNTS, "", "");
			columnNumber = enquiryElements.getColumnHeaderNumber("Recent Transactions", "Balance");
			reserveAccountBalance = enquiryElements.getElementAtCell("Recent Transactions", columnNumber, 1).getText()
					.replaceAll(",", "").replaceAll("\\-", "");
			fields = "PRIMARY.ACCOUNT," + "CONSOL.LEVEL.ADDON," + "TRANSACTION:1," + "SURROGATE.AC:1," + "CURRENCY:1,"
					+ "AMOUNT:1," + "CHEQUE.NUMBER:1," + "TRANSACTION:2," + "SURROGATE.AC:2," + "CURRENCY:2,"
					+ "AMOUNT:2," + "TRANSACTION+:3," + "SURROGATE.AC:3," + "CURRENCY:3," + "AMOUNT:3,";
			values = "CAD1100100017623," + "No," + "Loanpayoff," + mortgage[2] + "," + "CAD," + totalPayoff[2] + ","
					+ "123456789," + "Debit," + "CAD1100000017623," + "CAD," + "75.00," + "Credit,"
					+ reserveAccountShortfall + "," + "CAD," + reserveAccountBalance + ",";
			financialID = financialTransaction(CREATE_AUTHORISE, "Teller Financial Services", fields, values);
			if (financialID == null || financialID.contains(ERROR)) {
				stepActual = "Error while performing Payoff Shortfall with Reserve account: " + reserveAccountShortfall
						+ " via Teller Financial Services Financial Transaction for " + mortgage[2];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 7, enabled = true)
	public void payoffSurplusWithin50() {

		stepDescription = "Perform Payoff Surplus within $50 via Teller Financial Services Financial Transaction for "
				+ mortgage[3];
		stepExpected = "Payoff Surplus within $50 via Teller Financial Services Financial Transaction for "
				+ mortgage[3] + " Performed successfully ";

		fields = "PRIMARY.ACCOUNT," + "CONSOL.LEVEL.ADDON," + "TRANSACTION:1," + "SURROGATE.AC:1," + "CURRENCY:1,"
				+ "AMOUNT:1," + "CHEQUE.NUMBER:1," + "TRANSACTION:2," + "SURROGATE.AC:2," + "CURRENCY:2," + "AMOUNT:2,";
		values = "CAD1100100017623," + "No," + "Loanpayoff," + mortgage[3] + "," + "CAD," + totalPayoff[3] + ","
				+ "123456789," + "Credit," + "PL54201," + "CAD," + "25.00,";

		if (mortgage[3] == null || mortgage[3].contains(ERROR)) {

			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Login failed";
			throw new SkipException(stepActual);
		} else {
			financialID = financialTransaction(CREATE_AUTHORISE, "Teller Financial Services", fields, values);

			if (financialID == null || financialID.contains(ERROR)) {
				stepActual = "Error while performing Payoff Surplus within $50 via Teller Financial Services Financial Transaction for "
						+ mortgage[3];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 8, enabled = true)
	public void payoffSurplusMorethan50WithReserveAccount() {

		stepDescription = "Perform Payoff Surplus more than $50 with Reserve account: " + reserveAccountSurPlus
				+ " via Teller Financial Services Financial Transaction for " + mortgage[4];
		stepExpected = "Payoff Surplus more than $50 with Reserve account: " + reserveAccountSurPlus
				+ " via Teller Financial Services Financial Transaction for " + mortgage[4]
				+ " Performed successfully ";

		String reserveAccountBalance;
		int columnNumber;
		double amount = Double.parseDouble(totalPayoff[4]) + 75.00;
		fields = "PAYMENT.ORDER.PRODUCT," + "DEBIT.ACCOUNT," + "DEBIT.CCY," + "PAYMENT.CURRENCY," + "PAYMENT.AMOUNT,"
				+ "DEBIT.VALUE.DATE," + "BENEFICIARY.ID," + "ORDERING.CUSTOMER,";
		values = "CHQADHOC," + reserveAccountSurPlus + "," + "CAD," + "CAD," + "1600," + "+0d,"
				+ DefaultVariables.dummyBeneficiary + "," + orderingCustomer + ",";

		if (mortgage[4] == null || mortgage[4].contains(ERROR)) {

			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Login failed";
			throw new SkipException(stepActual);
		} else {

			createAdHocPayment(CREATE, CHEQUE, reserveAccountShortfall, fields, values);

			findArrangement(AUTHORISED, reserveAccountSurPlus, ARRANGEMENT, "", SERVICING_ACCOUNTS, "", "");
			columnNumber = enquiryElements.getColumnHeaderNumber("Recent Transactions", "Balance");
			reserveAccountBalance = enquiryElements.getElementAtCell("Recent Transactions", columnNumber, 1).getText()
					.replaceAll(",", "").replaceAll("\\-", "");

			fields = "PRIMARY.ACCOUNT," + "CONSOL.LEVEL.ADDON," + "TRANSACTION:1," + "SURROGATE.AC:1," + "CURRENCY:1,"
					+ "AMOUNT:1," + "CHEQUE.NUMBER:1," + "TRANSACTION:2," + "SURROGATE.AC:2," + "CURRENCY:2,"
					+ "AMOUNT:2,";
			values = "CAD1100100017623," + "No," + "Loanpayoff," + mortgage[4] + "," + "CAD," + Double.toString(amount)
					+ "," + "123456789," + "Debit," + reserveAccountSurPlus + "," + "CAD," + reserveAccountBalance
					+ ",";
			financialID = financialTransaction(CREATE_AUTHORISE, "Teller Financial Services", fields, values);
			if (financialID == null || financialID.contains(ERROR)) {
				stepActual = "Error while performing Payoff Surplus more than $50 with Reserve account: "
						+ reserveAccountSurPlus + " via Teller Financial Services Financial Transaction for "
						+ mortgage[4];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 9, enabled = true)
	public void payoffFromEscrowAccount() {

		stepDescription = "Perform Payoff from Escrow Account for Arrangement:  " + mortgage[5];
		stepExpected = "Payoff from Escrow Account for Arrangement:  " + mortgage[5] + " Performed Successfully";

		double totalPayoffEscrow;
		fields = "CREDIT.ACCT.NO," + "CREDIT.CURRENCY," + "CREDIT.AMOUNT," + "DEBIT.ACCT.NO," + "ORDERING.CUST:1,";
		values = mortgage[5] + "," + "CAD," + totalPayoff[5] + "," + "CAD1006500011000," + orderingClient + ",";
		String escrowBalance;

		if (mortgage[5] == null || mortgage[5].contains(ERROR)) {

			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Login failed";
			throw new SkipException(stepActual);

		} else {
			financialID = financialTransaction(CREATE_AUTHORISE, "Escrow Deposit", fields, values);
			commandLine("TAB ESCROW.FIND.ACCOUNT", true);
			compositeScreen.switchToFrame(ID, "workarea");
			enquiryElements.enquirySearch("Account/Arrangement", "", mortgage[5]);
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "workarea");
			enquiryElements.enquiryButtons(VIEW).click();
			switchToPage(LASTPAGE, false);
			escrowBalance = versionScreen.versionElement("", "Escrow Balance", "1").getText().replaceAll(",", "");
			fields = "CREDIT.ACCT.NO," + "CREDIT.CURRENCY," + "CREDIT.AMOUNT," + "DEBIT.ACCT.NO,";
			values = "CAD1110200017623," + "CAD," + escrowBalance + "," + mortgage[5] + ",";
			financialID = financialTransaction(CREATE_AUTHORISE, "Escrow Withdrawal", fields, values);

			fields = "EFFECTIVE.DATE," + "CLOSURE.REASON,";
			values = "+1m," + "Undesirable Client,";
			requestClosure(SIMULATE, mortgage[5], "Request Payoff", RETAIL_MORTGAGES, fields, values, "", "", "");

			totalPayoffEscrow = Double.parseDouble(retrievePayoffAmount(mortgage[5], RETAIL_MORTGAGES, ""));

			fields = "PRIMARY.ACCOUNT," + "CONSOL.LEVEL.ADDON," + "TRANSACTION:1," + "SURROGATE.AC:1," + "CURRENCY:1,"
					+ "AMOUNT:1," + "CHEQUE.NUMBER:1," + "TRANSACTION:2," + "SURROGATE.AC:2," + "CURRENCY:2,"
					+ "AMOUNT:2,";
			values = "CAD1100100017623," + "No," + "Loanpayoff," + mortgage[5] + "," + "CAD," + totalPayoffEscrow + ","
					+ "123456789," + "Debit," + "CAD1100000017623," + "CAD," + escrowBalance + ",";

			financialID = financialTransaction(CREATE_AUTHORISE, "Teller Financial Services", fields, values);
			if (financialID == null || financialID.contains(ERROR)) {
				stepActual = "Error while performing Payoff from Escrow Account for " + mortgage[5];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

		}
		softVerify.assertAll();
	}

	@Test(priority = 10, enabled = true)
	@Parameters("product")
	public void pendingClosureForHELOC(final String product) {

		stepDescription = "Perform pending closure activity for HELOC if Product is HELOC";
		stepExpected = "pending closure activity for HELOC Performed Successfully";

		fields = "MATURITY.DATE,";
		values = "+0d,";

		if (heloc == null || heloc.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Login failed";
			throw new SkipException(stepActual);
		} else {

			if (!arrangementActivity(CREATE, "Mature LOC/HELOC for PendingClosure", heloc, RETAIL_MORTGAGES,
					fields, values)) {
				stepActual = "Error while performing pending closure activity for Account  " + heloc;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

		}
		softVerify.assertAll();
	}

	@Test(priority = 11, enabled = true)
	public void payoffFullAmountEFT() {

		stepDescription = "Payoff full amount via EFT for Arrangement: " + mortgage[0];
		stepExpected = "Full amount via EFT for Arrangement: " + mortgage[0] + " paidoff successfully ";

		fields = "PAYMENT.ORDER.PRODUCT," + "CREDIT.ACCOUNT," + "PAYMENT.AMOUNT," + "ORDERING.CUSTOMER.SSI,";
		values = "ACHPYOFF," + mortgage[0] + "," + totalPayoff[0] + "," + beneficiary + ",";

		if (mortgage[0] == null || mortgage[0].contains(ERROR)) {

			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Login failed";
			throw new SkipException(stepActual);
		} else {

			financialID = createAdHocPayment(CREATE, EFT, mortgage[0], fields, values);

			if (financialID == null || financialID.contains(ERROR)) {
				stepActual = "Error while performing full Payoff via EFT for " + mortgage[0];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 12, enabled = true)
	public void reversePayoutActivity() {

		stepDescription = "Reverse Payout activity for Arrangement: " + mortgage[0];
		stepExpected = "Payout activity for Arrangement: " + mortgage[0] + " Reversed successfully ";

		String payOffTFS;

		if (mortgage[0] == null || mortgage[0].contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Login failed";
			throw new SkipException(stepActual);
		} else {
			findArrangement(AUTHORISED, mortgage[0], ARRANGEMENT, "", RETAIL_MORTGAGES, "", "");
			versionScreen.linkText(ACTIVITY_LOG, "Txn History").click();
			switchToPage(LASTPAGE, false);
			versionScreen.activityAction("Settle Activity For Payoff", "", "Select Drilldown").click();
			switchToPage(LASTPAGE, false);
			payOffTFS = readTable.retrieveValue("Debit Their Ref", "3").getText();

			if (!reverseEntity(payOffTFS, "TFS")) {
				stepActual = "Error while Reversing  Payout activity for Arrangement " + mortgage[0];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 13, enabled = true)
	public void dischargeFlag() {

		stepDescription = "Set Discharge Collateral flag to YES for Arrangement: " + mortgage[0];
		stepExpected = "Discharge Collateral flag setted to YES successfully for Arrangement: " + mortgage[0];

		String arrangementId;

		if (mortgage[0] == null || mortgage[0].contains(ERROR)) {

			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Login failed";
			throw new SkipException(stepActual);
		} else {
			findArrangement(AUTHORISED, mortgage[0], ARRANGEMENT, "", RETAIL_MORTGAGES, "", "");
			arrangementId = versionScreen.labelElement("Arrangement").getText();
			fields = "REFINANCE.FLAG," + "COLLATERAL.FLAG," + "ACCOUNT," + "CUSTOMER," + "ARRANGEMENT.ID:1,";
			values = "NO," + "YES," + "CAD1100100017623," + customer + "," + arrangementId + ",";
			commandLine("LBC.H.PAYOUT.REQUEST,LBC.DISH.COLL.INPUT", commandLineAvailable);
			switchToPage(LASTPAGE, false);
			toolElements.toolsButton("New Deal").click();
			switchToPage(LASTPAGE, false);
			result = multiInputData(fields, values, false) && inputTable.commitAndOverride();
			if (!result) {
				stepActual = "Error while Setting Discharge Collateral flag to YES for Arrangement " + mortgage[0];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}