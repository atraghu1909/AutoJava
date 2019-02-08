package testcases.PROC;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.DefaultVariables;

public class PROC_SAE_XX012 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "2018-07-05";
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
	private Calendar cal = Calendar.getInstance();
	private Date date = new Date();
	private String startDate1;
	private String startDate2;
	private ArrangementData arrangementsData;
	private String[] arrangements;
	private String mainCustomer;
	private String newCustomer;
	private String activityFields;
	private String activityValues;
	boolean result = false;

	@Test(priority = 1, enabled = true)
	@Parameters({ "customerType", "branch", "productGroup", "product" })
	public void preCondition(final String customerType, @Optional("LAURENTIAN BANK - 523") final String branch,
			final String productGroup, final String product) {

		stepDescription = "Create a " + customerType + " customer and four " + product + " arrangements";
		stepExpected = customerType + " customer and four " + product + " arrangements created successfully";

		int totalArrangements = 4;
		boolean localResult = true;

		if (loginResult) {
			switchToBranch(branch);

			mainCustomer = createDefaultCustomer(customerType, productGroup, ROLEBASED_SAE);

			arrangements = new String[totalArrangements];

			for (int i = 0; i < totalArrangements && localResult; i++) {
				arrangementsData = new ArrangementData("allArrangements", productGroup, product)
						.withCustomers(mainCustomer, createdCustomers.get(mainCustomer), "", "100,", "100,")
						.withLimit("", "NEW", "No").withDisbursement().build();

				arrangements[i] = createDefaultArrangement(arrangementsData);
				localResult = !arrangements[i].contains(ERROR);
			}

			if (mainCustomer == null || mainCustomer.contains(ERROR) || !localResult) {
				stepActual = "Error while creating a " + customerType + " customer and four " + product
						+ " arrangements";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {
				stepActual = "Customer " + mainCustomer + " and " + totalArrangements
						+ " arrangements created successfully:";
				for (int i = 0; i < totalArrangements; i++) {
					stepActual += System.lineSeparator();
					stepActual += arrangements[i];
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
	@Parameters({ "productGroup", "product" })
	public void modifyLoanAccountStatics(final String productGroup, final String product) {

		stepDescription = "Modify account statics for " + product + " arrangement: " + arrangements[0];
		stepExpected = "Account statics for " + product + " arrangement: " + arrangements[0] + " modified successfully";

		if (arrangements[0] == null || arrangements[0].contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			activityFields = "Account$Account Static#L.RESTRUCT.FLAG," + "Account$Account Static#L.REASON.NOTE,"
					+ "Account$Reserve Account & Other Details#CA.NPL,"
					+ "Account$Reserve Account & Other Details#CA.NPL.REASON,"
					+ "Account$Reserve Account & Other Details#CA.NAB,";
			activityValues = "Restructure," + "Test Notes," + "Y," + "Insufficient Debt Service," + "Y,";

			result = arrangementActivity(CREATE, "UPDATE ACTIVITY FOR ACCOUNT", arrangements[0], productGroup,
					activityFields, activityValues);

			if (!result) {
				stepActual = "Error while modifying account statics for " + product + " arrangement: "
						+ arrangements[0];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "customerType", "productGroup", "product" })
	public void modifyLoanCustomer(final String customerType, final String productGroup, final String product) {

		stepDescription = "Modify customer for " + product + " arrangement: " + arrangements[1];
		stepExpected = "Customer for " + product + " arrangement: " + arrangements[1] + " modified successfully";

		final String fields;
		final String values;

		if (arrangements[1] == null || arrangements[1].contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			newCustomer = createDefaultCustomer(customerType, productGroup, ROLEBASED_SAE);

			activityFields = "CUSTOMER+:2," + "CUSTOMER.ROLE:2,";
			activityValues = newCustomer + "," + "COBORROWER,";

			result = arrangementActivity(OPEN, "CHANGE ACTIVITY FOR CUSTOMER", arrangements[1], productGroup,
					activityFields, activityValues);
			multiInputData(activityFields, activityValues, false);
			toolElements.toolsButton(VALIDATE_DEAL).click();
			switchToPage(LASTPAGE, false);

			fields = "TAX.LIABILITY.PERC:2";
			values = "";

			result = multiInputData(fields, values, true);
			toolElements.toolsButton(COMMIT_DEAL).click();
			result = inputTable.verifyAcceptOverride();

			if (result) {
				activityFields = "TAX.LIABILITY.PERC:1," + "LIMIT.ALLOC.PERC:1," + "TAX.LIABILITY.PERC:2,"
						+ "LIMIT.ALLOC.PERC:2,";
				activityValues = "50.00," + "50.00," + "50.00," + "50.00,";

				result = arrangementActivity(CREATE, "UPDATE ACTIVITY FOR CUSTOMER", arrangements[1], productGroup,
						fields, values);
			}

			if (!result) {
				stepActual = "Error while modifying customer for " + product + " arrangement: " + arrangements[1];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void modifyLoanInterestCalculation(final String productGroup, final String product) {

		stepDescription = "Modify Interest Calculation for " + product + " arrangement: " + arrangements[2];
		stepExpected = "Interest Calculation for " + product + " arrangement: " + arrangements[2]
				+ " modified successfully";

		String penaltyInterestField;
		String penaltyInterestValue;

		if (arrangements[2] == null || arrangements[2].contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			switch (product) {
			case "Multi-Residential MG 5 Pls Fix Rate":
			case "Multi-Residential MG 1-4 Fix. Rate":
				penaltyInterestField = "Principal Interest$Fixed/Floating/Linked#L.RE.INVS.BRC,";
				penaltyInterestValue = "4.79,";

				break;
			case "Real Estate Mortgage Fix Rate":
			case "Commercial Term Loans Fixed Rate":
				penaltyInterestField = "Principal Interest$Fixed/Floating/Linked#L.RE.INVEST.RT,";
				penaltyInterestValue = "4.79,";

				break;
			case "Multi-Residential MG 5 Pls Var Rate":
			case "Multi-Residential MG 1-4 Var. Rate":
			case "Real Estate Mortgage Var Rate":
			case "Commercial Term Loans Var Rate":
				penaltyInterestField = "Principal Interest$MOS Interface#L.POSTED.RATE,"
						+ "Principal Interest$Fixed/Floating/Linked#MARGIN.RATE:1:1,";
				penaltyInterestValue = "4.79," + "1.75,";

				break;
			default:
				penaltyInterestField = "";
				penaltyInterestValue = "";

				break;
			}

			result = arrangementActivity(CREATE, "CHANGE ACTIVITY FOR PRINCIPALINT", arrangements[2], productGroup,
					penaltyInterestField, penaltyInterestValue);

			if (!result) {
				stepActual = "Error while modifying Interest Calculation for " + product + " arrangement: "
						+ arrangements[2];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void modifyLoanAccountLimit(final String productGroup, final String product) {

		stepDescription = "Modify Account Limit for " + product + " arrangement: " + arrangements[3];
		stepExpected = "Account Limit for " + product + " arrangement: " + arrangements[3] + " modified successfully";

		final String newParentLimit;
		final String newChildLimit;

		if (arrangements[3] == null || arrangements[3].contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			newParentLimit = customerLimit(CREATE, "", ROLEBASED_SAE, product, mainCustomer, "2400", "05",
					DefaultVariables.securedSAELimitFields, DefaultVariables.securedSAELimitValues);

			newChildLimit = customerLimit(CREATE, "", ROLEBASED_SAE, product, mainCustomer, "", "05",
					DefaultVariables.securedSAELimitFields, DefaultVariables.securedSAELimitValues);

			activityFields = "Limit$LIMIT.SERIAL,";
			activityValues = "05,";

			result = arrangementActivity(CREATE, "UPDATE ACTIVITY FOR LIMIT", arrangements[3], productGroup,
					activityFields, activityValues);

			if (!result) {
				stepActual = "Error while modifying Account Limit for " + product + " arrangement: " + arrangements[3];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void modifyLoanOfficers(final String productGroup, final String product) {

		stepDescription = "Modify Officers for " + product + " arrangement: " + arrangements[0];
		stepExpected = "Officers for " + product + " arrangement: " + arrangements[0] + " modified successfully";

		if (arrangements[0] == null || arrangements[0].contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			activityFields = "Account Officers$OTHER.OFFICER:1," + "Account Officers$OFFICER.ROLE:1,";
			activityValues = "10," + "Active Loan Manager,";

			result = arrangementActivity(CREATE, "UPDATE ACTIVITY FOR OFFICERS", arrangements[0], productGroup,
					activityFields, activityValues);

			if (!result) {
				stepActual = "Error while modifying Officers for " + product + " arrangement: " + arrangements[0];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 7, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void modifyLoanPaymentSchedule(final String productGroup, final String product) {

		stepDescription = "Modify Payment Schedule for " + product + " arrangement: " + arrangements[1];
		stepExpected = "Payment Schedule for " + product + " arrangement: " + arrangements[1]
				+ " modified successfully";

		if (arrangements[1] == null || arrangements[1].contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			activityFields = "Schedule$PAYMENT.FREQ:1," + "Schedule$DUE.FREQ:1:1,";
			activityValues = "e0Y e0M e1W o2D e0F," + "e0Y e0M e1W o2D e0F,";

			result = arrangementActivity(CREATE, "CHANGE ACTIVITY FOR SCHEDULE", arrangements[1], productGroup,
					activityFields, activityValues);

			if (!result) {
				stepActual = "Error while modifying Payment Schedule for " + product + " arrangement: "
						+ arrangements[1];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 8, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void modifyLoanDefineHoliday(final String productGroup, final String product) {

		stepDescription = "Perform Define Holiday activity for " + product + " arrangement: " + arrangements[2];
		stepExpected = "Define Holiday activity for " + product + " arrangement: " + arrangements[2]
				+ " performed successfully";

		if (arrangements[2] == null || arrangements[2].contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			activityFields = "Schedule$HOL.PAYMENT.TYPE:1," + "Schedule$HOL.START.DATE:1,"
					+ "Schedule$HOL.NUM.PAYMENTS:1,";
			activityValues = "INTEREST.ONLY," + "+1m," + "1,";

			result = arrangementActivity(CREATE, "DEFINE.HOLIDAY ACTIVITY FOR SCHEDULE", arrangements[2], productGroup,
					activityFields, activityValues);

			if (!result) {
				stepActual = "Error while performing Define Holiday activity for " + product + " arrangement: "
						+ arrangements[2];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 9, enabled = true)
	@Parameters({ "productGroup", "product", "customerType" })
	public void modifyLoanHoldPayment(final String productGroup, final String product, final String customerType) {

		stepDescription = "Perform Hold Payment activity for " + product + " arrangement: " + arrangements[3];
		stepExpected = "Hold Payment activity for " + product + " arrangement: " + arrangements[3]
				+ " performed successfully";

		if (arrangements[3] == null || arrangements[3].contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			cal.add(Calendar.MONTH, 1);
			date = cal.getTime();
			startDate1 = sdf.format(date);

			cal.add(Calendar.MONTH, 3);
			date = cal.getTime();
			startDate2 = sdf.format(date);

			activityFields = "Schedule$START.DATE:1:1," + "Schedule$NUM.PAYMENTS:1:1," + "Schedule$PAYMENT.TYPE+:2,"
					+ "Schedule$PAYMENT.METHOD:2," + "Schedule$PAYMENT.FREQ:2," + "Schedule$PROPERTY:2:1,"
					+ "Schedule$DUE.FREQ:2:1," + "Schedule$START.DATE:2:1," + "Schedule$BILL.TYPE:2,";
			activityValues = startDate1 + ",1," + "INTEREST.ONLY," + "Due," + "e0Y e1M e0W o1D e0F," + "PRINCIPALINT,"
					+ "e0Y e1M e0W o1D e0F," + startDate2 + ",INSTALLMENT,";

			result = arrangementActivity(CREATE, "Hold Payment", arrangements[3], productGroup, activityFields,
					activityValues);

			if (!result) {
				stepActual = "Error while performing Hold Payment activity for " + product + " arrangement: "
						+ arrangements[3];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 10, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void modifyLoanSettlementInstructions(final String productGroup, final String product) {

		stepDescription = "Modify Settlement Instructions for " + product + " arrangement: " + arrangements[0];
		stepExpected = "Settlement Instructions for " + product + " arrangement: " + arrangements[0]
				+ " modified successfully";

		final String beneficiary;
		final String beneficiaryFields;
		final String beneficiaryValues;

		if (arrangements[0] == null || arrangements[0].contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			beneficiaryFields = DefaultVariables.beneficiaryFields + "OWNING.CUSTOMER," + "BEN.CUSTOMER,";
			beneficiaryValues = DefaultVariables.beneficiaryValues + mainCustomer + ","
					+ createdCustomers.get(mainCustomer).getCustomerName() + ",";

			beneficiary = beneficiaryCode(CREATE, "EFT Client", "", beneficiaryFields, beneficiaryValues);

			activityFields = "Settlement$Advanced - Pay In#PAYIN.SETTLEMENT:1,"
					+ "Settlement$Advanced - Pay In#PAYIN.ACCOUNT:1:1,"
					+ "Settlement$Advanced - Pay In#PAYIN.PO.PRODUCT:1:1,"
					+ "Settlement$Advanced - Pay In#PAYIN.ACTIVITY:1:1,"
					+ "Settlement$Advanced - Pay In#PAYIN.BENEFICIARY:1:1,"
					+ "Settlement$Advanced - Pay In#PAYIN.SETTLE.ACTIVITY:1,";
			activityValues = "Yes," + "" + ",ACHCREDIT," + "" + "," + beneficiary
					+ ",LENDING-APPLYPAYMENT-PR.REPAYMENT,";

			result = arrangementActivity(CREATE, "UPDATE ACTIVITY FOR SETTLEMENT", arrangements[0], productGroup,
					activityFields, activityValues);

			if (!result) {
				stepActual = "Error while modifying Settlement Instructions for " + product + " arrangement: "
						+ arrangements[0];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 11, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void modifyLoanTermAmount(final String productGroup, final String product) {

		stepDescription = "Modify Term and Amount for " + product + " arrangement";
		stepExpected = "Term and Amount for " + product + " arrangement modified successfully";

		if (arrangements[1] == null || arrangements[1].contains(ERROR) || arrangements[2] == null
				|| arrangements[2].contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			activityFields = "Commitment$TERM,";
			activityValues = "3Y,";

			result = arrangementActivity(CREATE_AUTHORISE, "CHANGE.TERM ACTIVITY FOR COMMITMENT", arrangements[1],
					productGroup, activityFields, activityValues);

			if (!result) {
				stepActual = "Error while modifying Commitment Term for " + product + " arrangement";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			activityFields = "Commitment$CHANGE.AMOUNT,";
			activityValues = "-2T,";

			result = arrangementActivity(CREATE_AUTHORISE, "DECREASE ACTIVITY FOR COMMITMENT", arrangements[2], productGroup,
					activityFields, activityValues);

			if (!result) {
				stepActual = "Error while modifying Commitment Amount for " + product + " arrangement";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 12, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void modifyLoanPayoffRules(final String productGroup, final String product) {

		stepDescription = "Modify Payoff Rules for " + product + " arrangement: " + arrangements[3];
		stepExpected = "Payoff Rules for " + product + " arrangement: " + arrangements[3] + " modified successfully";

		if (arrangements[3] == null || arrangements[3].contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			activityFields = "EXPIRY.DAYS," + "SETTLE.DUES,";
			activityValues = "3," + "Yes,";

			result = arrangementActivity(CREATE, "UPDATE ACTIVITY FOR PAYOFF", arrangements[3], productGroup,
					activityFields, activityValues);

			if (!result) {
				stepActual = "Error while modifying Payoff Rules for " + product + " arrangement: " + arrangements[3];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 13, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void assumption(final String productGroup, final String product) {

		stepDescription = "Perform Assumption activity for " + product + " arrangement: " + arrangements[0];
		stepExpected = "Assumption activity for " + product + " arrangement: " + arrangements[0] + " performed successfully";

		final String newParentLimit;
		final String newChildLimit;
		final String editedLimit;
		final String editedCollateral;
		final String fields;
		final String values;

		if (arrangements[0] == null || arrangements[0].contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			newParentLimit = customerLimit(CREATE, "Revolving Secured", ROLEBASED_SAE, product, newCustomer, "2400",
					"01", DefaultVariables.securedSAELimitFields, DefaultVariables.securedSAELimitValues);

			newChildLimit = customerLimit(CREATE, "Revolving Secured", ROLEBASED_SAE, product, newCustomer, "", "01",
					DefaultVariables.securedSAELimitFields, DefaultVariables.securedSAELimitValues);

			activityFields = "Settlement$Advanced - Pay In#PAYIN.SETTLEMENT:1,"
					+ "Assumption / release of co-borrower$FIXED.AMOUNT,";
			activityValues = "No," + "20,";

			result = arrangementActivity(CREATE, "RENEGOTIATE ACTIVITY WITH PAYMENT", arrangements[0], productGroup,
					activityFields, activityValues);

			if (!result) {
				stepActual = "Error while performing RENEGOTIATE ACTIVITY WITH PAYMENT for " + product
						+ " arrangement: " + arrangements[0];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			activityFields = "CUSTOMER:1," + "EFFECTIVE.DATE,";
			activityValues = newCustomer + ",+1m,";

			result = arrangementActivity(OPEN, "Assumption", arrangements[0], productGroup, activityFields,
					activityValues);
			multiInputData(activityFields, activityValues, false);
			toolElements.toolsButton(VALIDATE_DEAL).click();
			switchToPage(LASTPAGE, false);

			fields = "Customer$L.CU.REASON," + "Customer$L.ASSUME.DATE," + "Customer$L.LOAN.BALANCE,"
					+ "Customer$LIMIT.ALLOC.PERC:1," + "Customer$GL.ALLOC.PERC:1," + "Limit$LIMIT.SERIAL,";
			values = "Assumption," + "+1m," + "90000," + "100.00," + "100.00," + "01,";

			result = multiInputData(fields, values, false);
			toolElements.toolsButton(COMMIT_DEAL).click();
			result = inputTable.verifyAcceptOverride();

			if (!result) {
				Reporter.log("Error while performing Assumption activity for arrangement: " + arrangements[0],
						debugMode);
			}

			editedLimit = customerLimit(AMEND, "Revolving Secured", ROLEBASED_SAE, product, mainCustomer, "", "01",
					"EXPIRY.DATE,", "+1m,");

			editedCollateral = collateral(AMEND, COLLATERAL_DETAILS, mainCustomer + ".1.1", "EXPIRY.DATE", "+1m");

			if (editedCollateral.contains(ERROR)) {
				stepActual = "Error while performing Assumptions for " + product + " arrangement: " + arrangements[0];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 14, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void loanRenewal(final String productGroup, final String product) {

		stepDescription = "Modify Renewal Instructions for " + product + " arrangement: " + arrangements[1];
		stepExpected = "Renewal Instructions for " + product + " arrangement: " + arrangements[1]
				+ " modified successfully";

		String fields;
		String values;

		if (arrangements[1] == null || arrangements[1].contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			switch (product) {
			case "Real Estate Mortgage Fix Rate":
			case "Real Estate Mortgage Var Rate":
			case "Commercial Term Loans Fixed Rate":
			case "Commercial Term Loans Var Rate":
				activityFields = "Settlement$Basic#PAYIN.SETTLEMENT:1," + "Customer$DELIVERY.REQD:1,"
						+ "Customer$Statement#STATEMENT.REQD:1,";
				activityValues = "No," + "" + ",No,";

				result = arrangementActivity(CREATE, "RENEGOTIATE ACTIVITY WITHOUTPAYMENT", arrangements[1],
						productGroup, activityFields, activityValues);

				if (product.contains("Mortgage")) {
					result = arrangementActivity(CREATE, "CHANGE.TERM ACTIVITY FOR COMMITMENT", arrangements[1],
							productGroup, "MATURITY.DATE,", "+3m,");
				}

				break;
			case "Multi-Residential MG 1-4 Fix. Rate":
			case "Multi-Residential MG 1-4 Var. Rate":
			case "Multi-Residential MG 5 Pls Fix Rate":
			case "Multi-Residential MG 5 Pls Var Rate":
				activityFields = "PRODUCT,";
				activityValues = "Default Comm.Multi-Residential Prod,";

				result = arrangementActivity(OPEN, "Renewal Processing", arrangements[1], productGroup, activityFields,
						activityValues);
				multiInputData(activityFields, activityValues, false);
				toolElements.toolsButton(VALIDATE_DEAL).click();
				switchToPage(LASTPAGE, false);

				fields = "Commitment$TERM,";
				values = "6m,";

				result = multiInputData(fields, values, false);
				toolElements.toolsButton(COMMIT_DEAL).click();
				result = inputTable.verifyAcceptOverride();

				break;
			case "Construction loan":
			case "Demand Loans":
			default:
				Reporter.log("Step is not run, as it is not applicable for " + product, debugMode);
				result = true;
				break;
			}

			if (!result) {
				stepActual = "Error while modifying Renewal Instructions for " + product + " arrangement: "
						+ arrangements[1];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
