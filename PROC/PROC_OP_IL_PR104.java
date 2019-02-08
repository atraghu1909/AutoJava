package testcases.PROC;

import org.openqa.selenium.support.ui.Select;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.DefaultVariables;

public class PROC_OP_IL_PR104 extends testLibs.BaseTest_DataGen {

	private String mainArrangement1;
	private String mainArrangement2;
	private String customer;
	private String customerType;
	private String branch = "B2B Branch 817";

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "unsecuredProduct", "securedProduct" })
	public void preCondition(final String productGroup, final String unsecuredProduct, final String securedProduct) {

		stepDescription = "Create Current " + unsecuredProduct + ", " + securedProduct + " Arrangements";
		stepExpected = "Current " + unsecuredProduct + ", " + securedProduct + " Arrangements created successfully";

		String fields;
		String values;
		ArrangementData securedLoanData;
		ArrangementData unsecuredLoanData;

		if (loginResult) {

			switchToBranch(branch);

			customerType = DefaultVariables.productGroupCustomerType.get(productGroup);

			securedLoanData = new ArrangementData("securedLoanArrangement", productGroup, securedProduct)
					.withEffectiveDate("-1m")
					.withDisbursement()
					.withRepayments()
					.build();
			mainArrangement1 = createDefaultArrangement(securedLoanData);
			customer = securedLoanData.getCustomers();

			unsecuredLoanData = new ArrangementData("unsecuredLoanArrangement", productGroup, unsecuredProduct)
					.withCustomers(customer, createdCustomers.get(customer), "", "100", "100")
					.withEffectiveDate("-1m")
					.withDisbursement()
					.withRepayments()
					.build();
			mainArrangement2 = createDefaultArrangement(unsecuredLoanData);

			fields = "Registration details#L.COLL.ADM.CODE,";
			values = "B2b Investment Loan,";
			if (collateral(AMEND_AUTHORISE, COLLATERAL_DETAILS_LOANS, customer + ".1.1", fields, values)
					.contains(ERROR)) {
				stepActual = "Error while amending collateral";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			if (mainArrangement1 == null || mainArrangement1.contains(ERROR)) {
				stepActual = "Error while creating " + securedProduct + " Arrangements" + mainArrangement1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			if (mainArrangement2 == null || mainArrangement2.contains(ERROR)) {
				stepActual = "Error while creating " + unsecuredProduct + " Arrangements" + mainArrangement2;
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
	public void adjudication(final String productGroup) {

		stepDescription = "Verify if loan is in good standing, the admin code on the existing loan account and client exposure for arrangement: "
				+ mainArrangement2;
		stepExpected = "Loan is in good standing, the admin code on the existing loan account and client exposure for arrangement: "
				+ mainArrangement2 + " is verified";

		String status;
		String administrationCode;

		if (mainArrangement2 == null || mainArrangement2.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			findArrangement(AUTHORISED, mainArrangement2, ARRANGEMENT, ROLEBASED_LENDING, productGroup, "", "");
			status = versionScreen.statusElement("Status").getText();
			if (status.equals("Current")) {
				commandLine("COLLATERAL,LBC.B2BPPSA", commandLineAvailable);
				enquiryElements.transactionIdField(COLLATERAL_DETAILS).click();
				enquiryElements.transactionIdField(COLLATERAL_DETAILS).sendKeys(customer + ".1.1");
				toolElements.toolsButton(VIEW_CONTRACT).click();
				tabbedScreen.findTab("Registration details", "").click();
				administrationCode = readTable.verifyActivity("Administration Code").getText();

				if (!administrationCode.equalsIgnoreCase("B2b Investment Loan")) {
					stepActual = "Error while verifying the admin code on the existing loan account for arrangement: "
							+ mainArrangement2;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			} else {
				stepActual = "loan arrangement: " + mainArrangement2 + " is not in good standing";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "productGroup", "unsecuredProduct" })
	public void rewrite(final String productGroup, final String unsecuredProduct) {

		stepDescription = "Rewrite a Loan via EFT for arrangement: " + mainArrangement2;
		stepExpected = "Loan via EFT for arrangement: " + mainArrangement2 + " is rewritten successfully";

		boolean result;
		String fields;
		String values;
		String dischargeRequest;
		String principalAmountOld;
		String principalAmountNew;
		String rewrittenArrangement;
		Select dropdown;
		String totalPayoffAmount;
		ArrangementData rewrittenLoanData;

		if (mainArrangement2 == null || mainArrangement2.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			dischargeRequest = generateStatement("", customer, productGroup, unsecuredProduct, "", "");
			principalAmountOld = dischargeRequest.split("&")[0].replaceAll(",", "");
			principalAmountNew = Double.toString(Double.parseDouble(principalAmountOld) + 1000.00);

			rewrittenLoanData = new ArrangementData("rewrittenLoanArrangement", productGroup, unsecuredProduct)
					.withCustomers(customer, createdCustomers.get(customer), "", "100", "100")
					.withCommitmentAmount(principalAmountNew)
					.build();
			rewrittenArrangement = createDefaultArrangement(rewrittenLoanData);

			fields = "DEBIT.ACCT.NO," + "DEBIT.CURRENCY," + "CREDIT.ACCT.NO," + "DEBIT.AMOUNT," + "CREDIT.THEIR.REF,";
			values = rewrittenArrangement + "," + "CAD," + "CAD1100000017817," + "1000," + "RegistrdAcctTest,";

			if (!arrangementAction(rewrittenArrangement, customer, ROLEBASED_LENDING, LENDING_DISBURSEMENT, fields,
					values, customerType)) {
				stepActual = "Error while disbursing rewritten Arrangement: " + rewrittenArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;

			}

			fields = "DEBIT.ACCT.NO," + "DEBIT.CURRENCY," + "CREDIT.ACCT.NO," + "DEBIT.AMOUNT," + "CREDIT.THEIR.REF,";
			values = rewrittenArrangement + "," + "CAD," + "CAD1100000017817," + principalAmountOld + "," + "OldL"
					+ mainArrangement2 + ",";

			if (!arrangementAction(rewrittenArrangement, customer, ROLEBASED_LENDING, LENDING_DISBURSEMENT, fields,
					values, customerType)) {
				stepActual = "Error while disbursing rewritten Arrangement: " + rewrittenArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			if (unsecuredProduct.contains("Investment Loan")) {
				fields = "FIXED.AMOUNT," + "PAYMENT.TYPE+:3," + "PAYMENT.METHOD:3," + "PAYMENT.FREQ:3,"
						+ "PROPERTY:3:1," + "BILL.TYPE:3,";
				values = "50," + "CHARGE," + "Due," + "e0Y e1M e0W o1D e0F," + "ALPPSAFEE," + "PAYMENT,";

				if (!arrangementActivity(CREATE_AUTHORISE, "CHANGE ACTIVITY FOR ALPPSAFEE", mainArrangement2,
						productGroup, fields, values)) {
					stepActual = "Error while creating CHANGE ACTIVITY FOR ALPPSAFEE arrangement activity for Arrangement: "
							+ mainArrangement2;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}

			fields = "PAYMENT.TYPE:1," + "ACTUAL.AMT:1:1," + "PAYMENT.METHOD:1," + "PROPERTY:1:1,";
			values = "SPECIAL.IO," + "500," + "Due," + "ACCOUNT,";

			if (!arrangementActivity(CREATE_AUTHORISE, "CHANGE ACTIVITY FOR SCHEDULE", mainArrangement2, productGroup,
					fields, values)) {
				stepActual = "Error while creating CHANGE ACTIVITY FOR SCHEDULE arrangement activity for Arrangement: "
						+ mainArrangement2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			fields = "L.REASON.NOTE," + "L.RESTRUCT.FLAG," + "L.LINKED.REF.ID,";
			values = "Changed from PI to IO," + "Refinance," + mainArrangement2 + ",";

			if (!arrangementActivity(CREATE_AUTHORISE, "UPDATE ACTIVITY FOR ACCOUNT", mainArrangement2, productGroup,
					fields, values)) {
				stepActual = "Error while creating UPDATE ACTIVITY FOR ACCOUNT arrangement activity for Arrangement: "
						+ mainArrangement2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			if (!arrangementActivity(OPEN, "UPDATE ACTIVITY FOR MESSAGING", mainArrangement2, productGroup, "", "")) {
				stepActual = "Error while openning UPDATE ACTIVITY FOR MESSAGING arrangement activity for Arrangement: "
						+ mainArrangement2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {
				dropdown = new Select(enquiryElements.activityMessaging("LENDING-CLOSE-ARRANGEMENT"));
				dropdown.selectByVisibleText("No");

				result = inputTable.commitAndOverride()
						&& authorizeEntity(mainArrangement2, ACTIVITY + "," + productGroup);
				if (!result) {
					stepActual = "Error while creating UPDATE ACTIVITY FOR MESSAGING arrangement activity for Arrangement: "
							+ mainArrangement2;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}

			String closureFields = "EFFECTIVE.DATE," + "CLOSURE.REASON,";
			String closureValues = "+0d," + "Other,";

			requestClosure(SIMULATE, mainArrangement2, "Request Payoff", productGroup, closureFields, closureValues, "",
					"", "");
			totalPayoffAmount = retrievePayoffAmount("", "", "");

			fields = "CREDIT.ACCT.NO," + "DEBIT.ACCT.NO," + "CREDIT.AMOUNT," + "ORDERING.BANK:1," + "CREDIT.THEIR.REF,";
			values = mainArrangement2 + "," + "CAD1100000017817," + totalPayoffAmount + "," + "RBC," + "OldL"
					+ mainArrangement2 + ",";

			if (!arrangementAction(mainArrangement2, customer, ROLEBASED_LENDING, "Lending Full Payout", fields, values,
					customerType)) {
				stepActual = "Error while rewriting a Loan via EFT for arrangement: " + mainArrangement2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			softVerify.assertAll();
		}
	}

	@Test(priority = 4, enabled = true)
	@Parameters({ "productGroup", "securedProduct" })
	public void rewriteViaCheque(final String productGroup, final String securedProduct) {

		stepDescription = "Rewrite a Loan via Cheque for arrangement: " + mainArrangement1;
		stepExpected = "Loan via Cheque for arrangement: " + mainArrangement1 + " is rewritten successfully";

		boolean result;
		String fields;
		String values;
		String dischargeRequest;
		String principalAmountOld;
		String principalAmountNew;
		String rewrittenArrangement;
		ArrangementData rewrittenLoanData;

		if (mainArrangement1 == null || mainArrangement1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			dischargeRequest = generateStatement("", customer, productGroup, securedProduct, "", "");
			principalAmountOld = dischargeRequest.split("&")[0].replaceAll(",", "");
			principalAmountNew = Double.toString(Double.parseDouble(principalAmountOld) + 15000.00);

			rewrittenLoanData = new ArrangementData("rewrittenLoanArrangement", productGroup, securedProduct)
					.withCustomers(customer, createdCustomers.get(customer), "", "100", "100")
					.withCommitmentAmount(principalAmountNew)
					.build();
			rewrittenArrangement = createDefaultArrangement(rewrittenLoanData);

			fields = "DEBIT.ACCT.NO," + "CREDIT.ACCT.NO," + "CREDIT.AMOUNT," + "CREDIT.THEIR.REF," + "CREDIT.CURRENCY,";
			values = rewrittenArrangement + "," + "CAD1100000017817," + principalAmountNew + "," + "ChqfromotherIns,"
					+ "CAD,";

			result = arrangementAction(mainArrangement1, customer, ROLEBASED_LENDING, TRANSFER_ACCOUNTS, fields, values,
					customerType);
			if (!result) {
				stepActual = "Error while Transferring funds Between Accounts: " + rewrittenArrangement
						+ " and CAD1100000017817";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			fields = "MARGIN.TYPE:1:1," + "MARGIN.OPER:1:1," + "MARGIN.RATE:1:1,";
			values = "Single Margin," + "Add," + "1.00,";

			result = arrangementActivity(CREATE_AUTHORISE, "CHANGE ACTIVITY FOR PRINCIPALINT", mainArrangement1,
					productGroup, fields, values);
			if (!result) {
				stepActual = "Error while creating CHANGE ACTIVITY FOR PRINCIPALINT arrangement activity for arrangement: "
						+ mainArrangement2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			fields = "L.REASON.NOTE," + "L.RESTRUCT.FLAG," + "L.LINKED.REF.ID," + "L.ACQ.PORTFOLIO,";
			values = "AGF Blended Rate Rewrite," + "Refinance," + mainArrangement1 + "," + "AGF,";

			result = arrangementActivity(CREATE_AUTHORISE, "UPDATE ACTIVITY FOR ACCOUNT", mainArrangement1,
					productGroup, fields, values);

			if (!result) {
				stepActual = "Error while creating UPDATE ACTIVITY FOR ACCOUNT arrangement activity for arrangement: "
						+ mainArrangement1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
