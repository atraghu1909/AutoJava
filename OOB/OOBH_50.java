package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class OOBH_50 extends testLibs.BaseTest_OOB {

	@Test(priority = 1, enabled = true)
	@Parameters({ "customer", "specialFundsTransfer", "productGroup", "product", "transactionDate", "authorizeFlag" })
	public void step1(final String customer, final @Optional("") String specialFundsTransfer, final String productGroup,
			final String product, @Optional("") final String transactionDate, final boolean authorizeFlag) {
		String creditAccount = "";
		String debitAccount = "";
		String fields;
		String values = null;
		String status;
		String fieldsForAADisbursement;
		String commitmentRow;
		String commitmentAmountString;
		String actualCIF;
		int totalCommitment;
		int amount = 0;
		String customerType = PERSONAL;

		if ("AA Disbursement".equals(specialFundsTransfer)) {
			fields = "CREDIT.ACCT.NO,DEBIT.CURRENCY,DEBIT.ACCT.NO,DEBIT.AMOUNT,DEBIT.VALUE.DATE";
		} else {
			fields = "CREDIT.ACCT.NO,CREDIT.CURRENCY,DEBIT.ACCT.NO,CREDIT.AMOUNT,CREDIT.VALUE.DATE";
		}

		stepDescription = "Create Arrangement Activity " + specialFundsTransfer + " for a " + product;
		stepExpected = specialFundsTransfer + " is successfully created";
		if (loginResult) {
			switchToBranch("B2B Branch 817");
			if (BUSINESS_ACCOUNTS.equals(productGroup) || COMMERCIAL_LOANS.equals(productGroup)) {
				customerType = BUSINESS;
			}
			actualCIF = findCIF(customer, customerType, "");
			if (!actualCIF.equals(customer)) {
				Reporter.log("CIF received as parameter (" + customer + ") was not found. Using new CIF instead: "
						+ actualCIF, debugMode);

			}
			try {
				debitAccount = findArrangement(AUTHORISED, actualCIF, CIF, "", PERSONAL_ACCOUNTS, PERSONAL_CHEQUING,
						CAD);
				try {
					compositeScreen.pendingActivity();
					Reporter.log("There were other pending activities to authorise on arrangement " + debitAccount,
							debugMode);
					authorizeEntity(debitAccount, ACTIVITY + "," + PERSONAL_ACCOUNTS);
				} catch (NoSuchElementException e) {
					Reporter.log("There was no pending activity to authorise on arrangement " + debitAccount,
							debugMode);
				}

				creditAccount = findArrangement(AUTHORISED, actualCIF, CIF, "", productGroup, product, CAD);

				String activityString = null;
				switch (specialFundsTransfer) {
				case "AA Repayment":
					activityString = "APPLYPAYMENT ACTIVITY FOR PR.REPAYMENT";
					break;

				case "AA Principal Decrease":
					activityString = "APPLYPAYMENT ACTIVITY FOR PR.PRINCIPAL.DECREASE";
					break;

				case "AA Disbursement":
					activityString = "DISBURSE ACTIVITY FOR COMMITMENT";
					break;

				case "AA Pay-Off":
					activityString = "APPLYPAYMENT ACTIVITY FOR PR.PAYOFF";
					break;

				default:
					break;
				}

				if (versionScreen.activityCheck(activityString, AUTHORISED)) {
					arrangementActivity(REVERSE_AUTHORISE, activityString, creditAccount, productGroup, "", "");
				}

				commitmentRow = versionScreen.accrualCategory("id", "FinancialSummary", "Commitment (Total)").getText();
				commitmentAmountString = commitmentRow.split(" ")[2].replaceAll(",", "").replaceAll("\\.0*$", "");
				totalCommitment = Integer.parseInt(commitmentAmountString);

				switch (specialFundsTransfer) {
				case "AA Disbursement":
					fields = "CREDIT.ACCT.NO,DEBIT.CURRENCY,DEBIT.ACCT.NO,DEBIT.AMOUNT,DEBIT.VALUE.DATE";
					amount = totalCommitment;
					values = creditAccount + "," + CAD + "," + debitAccount + "," + amount + "," + transactionDate;
					break;

				case "AA Pay-Off":
					fields = "CREDIT.ACCT.NO,CREDIT.CURRENCY,DEBIT.ACCT.NO,CREDIT.AMOUNT";
					amount = totalCommitment;
					values = creditAccount + "," + CAD + "," + debitAccount + "," + amount;
					break;

				case "AA Advance Repayment":
					fields = "CREDIT.ACCT.NO,CREDIT.CURRENCY,DEBIT.ACCT.NO,CREDIT.AMOUNT,CREDIT.VALUE.DATE";
					amount = (int) (totalCommitment * 0.15);
					values = creditAccount + "," + CAD + "," + debitAccount + "," + amount + "," + transactionDate;
					break;

				case "AA Repayment":
					fields = "CREDIT.ACCT.NO,CREDIT.CURRENCY,DEBIT.ACCT.NO,CREDIT.AMOUNT,CREDIT.VALUE.DATE";
					amount = (int) (totalCommitment * 0.15);
					values = creditAccount + "," + CAD + "," + debitAccount + "," + amount + "," + transactionDate;
					break;

				case "AA Principal Decrease":
					fields = "CREDIT.ACCT.NO,CREDIT.CURRENCY,DEBIT.ACCT.NO,CREDIT.AMOUNT,CREDIT.VALUE.DATE";
					amount = (int) (totalCommitment * 0.15);
					values = creditAccount + "," + CAD + "," + debitAccount + "," + amount + "," + transactionDate;
					break;

				default:
					break;
				}
				try {
					compositeScreen.pendingActivity();
					Reporter.log("There were other pending activities to authorise on arrangement " + creditAccount,
							debugMode);
					authorizeEntity(creditAccount, ACTIVITY + "," + productGroup);
					findArrangement(AUTHORISED, actualCIF, CIF, "", productGroup, product, CAD);
				} catch (NoSuchElementException e) {
					Reporter.log("There was no pending activity to authorise on arrangement " + creditAccount,
							debugMode);
				} finally {

					status = versionScreen.statusElement("Status").getText();
					if ("Pending Closure".equals(status)
							&& versionScreen.activityCheck("APPLYPAYMENT ACTIVITY FOR PR.PAYOFF", AUTHORISED)) {
						arrangementActivity(REVERSE_AUTHORISE, "APPLYPAYMENT ACTIVITY FOR PR.PAYOFF", creditAccount,
								productGroup, "", "");
						Reporter.log("PayOff activity was reversed", debugMode);

					} else if ("Not Disbursed".equals(status) && !"AA Disbursement".equals(specialFundsTransfer)) {
						fieldsForAADisbursement = "CREDIT.ACCT.NO,DEBIT.CURRENCY,DEBIT.ACCT.NO,DEBIT.AMOUNT,DEBIT.VALUE.DATE";
						financialTransaction(CREATE_AUTHORISE, "AA Disbursement", fieldsForAADisbursement, values);
						Reporter.log("Disbursement was performed successfully", debugMode);
					}
				}

				switchToPage(environmentTitle, true);
				financialTransaction(CREATE_AUTHORISE, specialFundsTransfer, fields, values);
			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "User could not create " + specialFundsTransfer;
				softVerify.fail(stepActual);
				Reporter.log(stepActual, debugMode);
			}
		} else {

			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
	}

}
