package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class OOB_0195 extends testLibs.BaseTest_OOB {

	@Test(priority = 1, enabled = true)
	@Parameters({ "type", "subType", "debitAccount", "creditAccount", "currency", "amount" })
	public void step1(final String type, final String subType, final String debitAccount, final String creditAccount,
			final String currency, final String amount) {

		if (loginResult) {
			switchToBranch("B2B Branch 817");

			stepDescription = "Perform a " + type + ": " + subType + " for a total of " + currency + amount;
			stepExpected = "Action is performed successfully";

			try {
				final String actualDebit = findArrangement(AUTHORISED, debitAccount, ARRANGEMENT, "", PERSONAL_ACCOUNTS,
						PERSONAL_CHEQUING, CAD);
				if (!actualDebit.equals(debitAccount) && !actualDebit.contains("Error")) {
					updatedValues.add("debitAccount," + actualDebit);
				}

				final String actualCredit = findArrangement(AUTHORISED, creditAccount, ARRANGEMENT, "",
						PERSONAL_ACCOUNTS, PERSONAL_CHEQUING, CAD);
				if (!actualCredit.equals(creditAccount) && !actualCredit.contains("Error")) {
					updatedValues.add("creditAccount," + actualCredit);
				}

				if (actualCredit.contains("Error") || actualDebit.contains("Error")) {
					stepActual = "Action could not be performed from arrangement " + debitAccount + " to arrangement "
							+ creditAccount;
					stepResult = StatusAs.FAILED;
					softVerify.fail(stepActual);
					Reporter.log(stepActual, debugMode);
				} else {
					final String fundTransferFields = "DEBIT.ACCT.NO," + "CREDIT.CURRENCY," + "CREDIT.AMOUNT,"
							+ "CREDIT.ACCT.NO";
					final String fundTransferValues = actualDebit + "," + currency + "," + amount + "," + actualCredit;

					if (!financialTransaction(CREATE, type + " - " + subType, fundTransferFields, fundTransferValues)
							.contains("Error")) {
						Reporter.log("Funds Transfer performed successfully", debugMode);
					} else {
						stepActual = type + ": " + subType + " action could not be performed";
						stepResult = StatusAs.FAILED;
						softVerify.fail(stepActual);
						Reporter.log(stepActual, debugMode);
					}
				}

			} catch (NoSuchElementException e) {
				stepActual = "Action could not be performed from arrangement " + debitAccount + " to arrangement "
						+ creditAccount;
				stepResult = StatusAs.FAILED;
				softVerify.fail(stepActual);
				Reporter.log(stepActual, debugMode);
			}

		}

		softVerify.assertAll();
	}
}