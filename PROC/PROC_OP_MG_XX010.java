package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.CustomerData;

public class PROC_OP_MG_XX010 extends testLibs.BaseTest_DataGen {
	private static String documentVersion = "Version 3.9";
	private CustomerData customerData;
	private String mainArrangement;
	private String customer;
	private String customerProvince;
	private String fields;
	private String values;
	private boolean result;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "customerType", "product", "statement" })
	public void preCondition(@Optional("B2B Branch 623") final String branch,
			@Optional(PERSONAL) final String customerType, final String product, final String statement) {

		stepDescription = "Create " + customerType + " customer, " + product
				+ " Arrangement and retrieve province for customer created";
		stepExpected = customerType + " customer, " + product
				+ " Arrangement created, and province for customer retrieved Successfully";
		if (loginResult) {

			switchToBranch(branch);
			final ArrangementData mortgageData = new ArrangementData("mainArrangement", RETAIL_MORTGAGES, product)
					.withEffectiveDate("-1m").withDisbursement().withRepayments().build();

			mainArrangement = createDefaultArrangement(mortgageData);
			customer = mortgageData.getCustomers();
			customerData = createdCustomers.get(customer);
			customerProvince = customerData.getAddressProvince();

			if (customer == null || customer.contains(ERROR)) {
				stepActual = "Error while creating " + customerType + " customer";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			if (mainArrangement == null || mainArrangement.contains(ERROR)) {
				stepActual = "Error while creating " + product + " Arrangement";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			if (customerProvince == null || customerProvince.contains(ERROR)) {
				stepActual = "Error while fetching province detail";
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
	@Parameters()
	public void cashBackCalculations() {

		stepDescription = "Update cashback calculations for Arrangement: " + mainArrangement;
		stepExpected = "cashback calculations for Arrangement: " + mainArrangement + " updated successfully";

		String paymentsToMaturity;
		double chargeAmount;
		int columnNumber;
		int rowNumber;
		
		if (customer == null || customer.contains(ERROR) || mainArrangement == null
				|| mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			commandLine("ENQ LBC.NUM.OF.PAYMENTS", true);
			switchToPage(LASTPAGE, false);
			enquiryElements.enquirySearch("ARRANGEMENT.ID", "equals", mainArrangement);
			switchToPage(LASTPAGE, false);
			columnNumber = enquiryElements.getColumnHeaderNumber("LBC.NUM.OF.PAYMENTS",
					"Number of Payments to maturity");
			rowNumber = enquiryElements.getRowNumberMatching("LBC.NUM.OF.PAYMENTS", "Account number", mainArrangement);
			paymentsToMaturity = enquiryElements.getElementAtCell("LBC.NUM.OF.PAYMENTS", columnNumber, rowNumber)
					.getText();
			chargeAmount = 1000 * Double.parseDouble(paymentsToMaturity);
			fields = "FIXED.AMOUNT,";
			values = chargeAmount + ",";
			result = arrangementActivity(CREATE, "CHANGE ACTIVITY FOR ALCASHBACK", mainArrangement, RETAIL_MORTGAGES,
					fields, values);

			if (!result) {
				stepActual = "Error while updating Cashback calculation for Arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "statement" })
	public void feesByProvincePortAndRefinance(final String statement) {

		stepDescription = "Change Registration  and Discharge fee for port and Refinance for Arrangement "
				+ mainArrangement;
		stepExpected = "Registration  and Discharge fee for port and Refinance for Arrangement: " + mainArrangement
				+ " Changed successfully";

		if (customerProvince == null || customerProvince.contains(ERROR) || mainArrangement == null
				|| mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			if ("For Port Only".equals(statement) || "For Refinance Only".equals(statement)) {
				String registrationFee;
				String dischargeFee;
				switch (customerProvince) {

				case "AB":
					registrationFee = "15.00";
					dischargeFee = "0.00";
					break;
				case "BC":
					registrationFee = "33.23";
					dischargeFee = "75.00";
					break;

				case "MB":
					registrationFee = "41.00";
					dischargeFee = "100.00";
					break;

				case "NB":
					registrationFee = "97.60";
					dischargeFee = "125.00";
					break;

				case "NL":
				case "NS":
					registrationFee = "100.00";
					dischargeFee = "125.00";
					break;

				case "ON":
					registrationFee = "74.72";
					dischargeFee = "125.00";
					break;

				case "PE":
					registrationFee = "25.00";
					dischargeFee = "125.00";
					break;

				case "SK":
					registrationFee = "0.00";
					dischargeFee = "125.00";
					break;

				case "QC":
				default:
					registrationFee = "0.00";
					dischargeFee = "0.00";
					break;
				}

				if (!"0.00".equals(registrationFee)) {
					fields = "FIXED.AMOUNT,";
					values = registrationFee + ",";
					result = arrangementActivity(CREATE, "CHANGE ACTIVITY FOR ALREGISTFEE", mainArrangement,
							RETAIL_MORTGAGES, fields, values);
					if (!result) {
						stepActual = "Error while Changing Registration for port and Refinance for Arrangement: "
								+ mainArrangement;
						softVerify.fail(stepActual);
						stepResult = StatusAs.FAILED;
					}
				}
				if (!"0.00".equals(dischargeFee)) {
					fields = "FIXED.AMOUNT,";
					values = registrationFee + ",";
					result = arrangementActivity(CREATE, "CHANGE ACTIVITY FOR ALDISCHARGE", mainArrangement,
							RETAIL_MORTGAGES, fields, values);
					if (!result) {
						stepActual = "Error while Changing Discharge fee for port and Refinance for Arrangement: "
								+ mainArrangement;
						softVerify.fail(stepActual);
						stepResult = StatusAs.FAILED;
					}
				}

			} else {
				stepActual = "step doesn't apply for this test";
				stepResult = StatusAs.NOT_COMPLETED;
				throw new SkipException(stepActual);
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters({ "statement" })
	public void feesByProvinceDischarge(final String statement) {

		stepDescription = "Change Registration  and Discharge fee for Discharge for Arrangement " + mainArrangement;
		stepExpected = "Registration  and Discharge fee for Discharge for Arrangement: " + mainArrangement
				+ " Changed successfully";

		if (customerProvince == null || customerProvince.contains(ERROR) || mainArrangement == null
				|| mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			if ("For Bridge Loan".equals(statement) || "Discharge stmt".equals(statement)
					|| "For Partial Discharge Only".equals(statement) || "LOC Discharge Stmt".equals(statement)) {
				String registrationFee;
				String dischargeFee;
				switch (customerProvince) {

				case "AB":
					registrationFee = "15.00";
					dischargeFee = "0.00";
					break;
				case "BC":
					registrationFee = "33.23";
					dischargeFee = "75.00";
					break;

				case "MB":
					registrationFee = "41.00";
					dischargeFee = "100.00";
					break;

				case "NB":
					registrationFee = "97.60";
					dischargeFee = "350.00";
					break;

				case "NL":
				case "NS":
					registrationFee = "100.00";
					dischargeFee = "350.00";
					break;

				case "ON":
					registrationFee = "74.72";
					dischargeFee = "350.00";
					break;

				case "PE":
					registrationFee = "25.00";
					dischargeFee = "350.00";
					break;

				case "SK":
					registrationFee = "0.00";
					dischargeFee = "350.00";
					break;

				case "QC":
				default:
					registrationFee = "0.00";
					dischargeFee = "0.00";
					break;
				}

				if (!"0.00".equals(registrationFee)) {
					fields = "FIXED.AMOUNT,";
					values = registrationFee + ",";
					result = arrangementActivity(CREATE, "CHANGE ACTIVITY FOR ALREGISTFEE", mainArrangement,
							RETAIL_MORTGAGES, fields, values);
					if (!result) {
						stepActual = "Error while Changing Registration for Discharge for Arrangement: "
								+ mainArrangement;
						softVerify.fail(stepActual);
						stepResult = StatusAs.FAILED;
					}
				}
				if (!"0.00".equals(dischargeFee)) {
					fields = "FIXED.AMOUNT,";
					values = registrationFee + ",";
					result = arrangementActivity(CREATE, "CHANGE ACTIVITY FOR ALDISCHARGE", mainArrangement,
							RETAIL_MORTGAGES, fields, values);
					if (!result) {
						stepActual = "Error while Changing Discharge fee for Discharge for Arrangement: "
								+ mainArrangement;
						softVerify.fail(stepActual);
						stepResult = StatusAs.FAILED;
					}
				}

			} else {
				stepActual = "step doesn't apply for this test";
				stepResult = StatusAs.NOT_COMPLETED;
				throw new SkipException(stepActual);
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	@Parameters({ "statement" })
	public void feesByProvinceTransfer(final String statement) {

		stepDescription = "Change Transfer fee for Arrangement " + mainArrangement;
		stepExpected = "Transfer fee for Arrangement: " + mainArrangement + " Changed successfully";
		String transferFee;
		if (customerProvince == null || customerProvince.contains(ERROR) || mainArrangement == null
				|| mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			if ("LBC Assumption Statement".equals(statement) || "B2B Assumption Statement".equals(statement)
					|| "For Transfer Only".equals(statement)) {

				switch (customerProvince) {

				case "ON":
				case "NB":
				case "SK":
					transferFee = "350.00";
					break;
				case "MB":
					transferFee = "180.00";
					break;

				case "BC":
					transferFee = "75.00";
					break;

				case "NL":
					transferFee = "50.00";
					break;

				case "PE":
				case "NS":
					transferFee = "25.00";
					break;

				case "QC":
				case "AB":
				default:
					transferFee = "0.00";
					break;
				}

				if (!"0.00".equals(transferFee)) {
					fields = "FIXED.AMOUNT,";
					values = transferFee + ",";
					result = arrangementActivity(CREATE, "CHANGE ACTIVITY FOR ALTRANSFERFEE", mainArrangement,
							RETAIL_MORTGAGES, fields, values);
				}

				if (!result) {
					stepActual = "Error while Changing Transfer fee for Arrangement: " + mainArrangement;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			} else {
				stepActual = "step doesn't apply for this test";
				stepResult = StatusAs.NOT_COMPLETED;
				throw new SkipException(stepActual);
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	@Parameters({ "statement" })
	public void feesForInformationStatement(final String statement) {

		stepDescription = "Change Information statement fee for Arrangement " + mainArrangement;
		stepExpected = "Information statement fee for Arrangement: " + mainArrangement + " Changed successfully";

		if (customerProvince == null || customerProvince.contains(ERROR) || mainArrangement == null
				|| mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			if ("Mortgage Information Statement".equals(statement) || "For Information Only".equals(statement)) {
				fields = "FIXED.AMOUNT,";
				values = "35.00,";
				result = arrangementActivity(CREATE, "CHANGE ACTIVITY FOR ALINFOSTMTFEE", mainArrangement,
						RETAIL_MORTGAGES, fields, values);

				if (!result) {
					stepActual = "Error while Changing Information statement fee for Arrangement: " + mainArrangement;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			} else {
				stepActual = "step doesn't apply for this test";
				stepResult = StatusAs.NOT_COMPLETED;
				throw new SkipException(stepActual);
			}

		}
		softVerify.assertAll();
	}

	@Test(priority = 7, enabled = true)
	@Parameters({ "statement" })
	public void prePaymentPrivilege(final String statement) {

		stepDescription = "Change Activity for pre-payment privilege for Arrangement " + mainArrangement;
		stepExpected = "Activity for pre-payment privilege for Arrangement: " + mainArrangement
				+ " Changed successfully";
		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			fields = "L.PREP.PRVLG," + "L.PRVLG.DESC,";
			values = "YES," + "15,";
			result = arrangementActivity(CREATE, "CHANGE ACTIVITY FOR ALPREPAYPENALTY", mainArrangement,
					RETAIL_MORTGAGES, fields, values);

			if (!result) {
				stepActual = "Error while Changing Activity for pre-payment privilege for Arrangement: "
						+ mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 8, enabled = true)
	@Parameters({ "statement" })
	public void noTaxList(final String statement) {

		stepDescription = "Update Tax paid by customer Indicator for Arrangement " + mainArrangement;
		stepExpected = "Tax paid by customer Indicator for Arrangement: " + mainArrangement + " Updated successfully";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			fields = "MOS Interface#L.TAX.PAY.IND,";
			values = "YES,";
			result = arrangementActivity(CREATE, "UPDATE ACTIVITY FOR ACCOUNT", mainArrangement, RETAIL_MORTGAGES,
					fields, values);

			if (!result) {
				stepActual = "Error while updating Tax paid by customer Indicator for Arrangement " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

		}
		softVerify.assertAll();
	}

	@Test(priority = 9, enabled = true)
	@Parameters({ "statement", "product" })
	public void statementCreation(final String statement, final String product) {
		String dischargeRequest;
		stepDescription = "Generate Statement for Arrangement " + mainArrangement;
		stepExpected = "Statement for Arrangement: " + mainArrangement + " Generated successfully";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			fields = "EFFECTIVE.DATE," + "CLOSURE.REASON,";
			values = "+1d," + "Customer Has Passed Away,";
			dischargeRequest = generateStatement(statement, customer, RETAIL_MORTGAGES, product, fields, values);
			if (dischargeRequest == null || dischargeRequest.contains(ERROR)) {
				stepActual = "Error While Generating Statement for Arrangement " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 10, enabled = true)
	@Parameters({ "statement", "product" })
	public void enquiryOrRetrievingStatement(final String statement, final String product) {
		String headerMessage;

		stepDescription = "Retrieve Statement for Arrangement " + mainArrangement;
		stepExpected = "Statement for Arrangement: " + mainArrangement + " Retrieved successfully";
		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			commandLine("ENQ LBC.DISCHARGE.LIST.ENQ", true);
			switchToPage(LASTPAGE, false);
			enquiryElements.enquirySearch("Arrangement ID", "", mainArrangement);
			switchToPage(LASTPAGE, false);
			headerMessage = enquiryElements.enqHeaderMsg().getText();
			if ("No data to display".equalsIgnoreCase(headerMessage)) {
				stepActual = "Error Retrieving Statement for Arrangement " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}

		softVerify.assertAll();
	}
}
