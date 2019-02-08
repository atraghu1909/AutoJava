package testcases.OOB;

import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import atu.alm.wrapper.enums.StatusAs;

public class OOB_151 extends testLibs.BaseTest_OOB {

	private boolean localResult;

	@Test(priority = 1, enabled = true)
	@Parameters({ "testType", "arrangement", "product", "actionToPerform", "startDate", "endDate", "expectation",
			"amount", "updatedStartDate", "updatedEndDate", "updatedAmount" })
	public void step1(final String testType, @Optional("") final String arrangement, final String product,
			@Optional("") final String actionToPerform, @Optional("") final String startDate,
			@Optional("") final String endDate, @Optional("true") final boolean expectation,
			@Optional("") final String amount, @Optional("") String updatedStartDate,
			@Optional("") String updatedEndDate, @Optional("") String updatedAmount) {

		String newArrangement;
		String creditAccount;
		final String fundTransferFields;
		final String fundTransferValues;

		stepDescription = "Verify System allows " + testType + " for specific time period";
		stepExpected = "System allows " + testType + " for specific time period Successfully";
		if (!"".equals(actionToPerform)) {
			stepDescription = "Verify System allows " + actionToPerform + " for " + testType;
			stepExpected = "System should not allow " + actionToPerform + " for " + testType;
		}

		if ("".equals(updatedStartDate)) {
			updatedStartDate = startDate;
		}
		if ("".equals(updatedEndDate)) {
			updatedEndDate = endDate;
		}
		if ("".equals(updatedAmount)) {
			updatedAmount = amount;
		}

		if (loginResult) {
			switchToBranch("B2B Branch 817");

			newArrangement = findArrangement(AUTHORISED, arrangement, ARRANGEMENT, "", PERSONAL_ACCOUNTS, product, CAD);

			switchToPage(LASTPAGE, false);
			switch (testType) {
			case "createFundsHold":
				localResult = fundsHold(product, testType, newArrangement, startDate, endDate, amount, "", "", "");

				break;
			case "updateAmountFundsHold":
				localResult = fundsHold(product, "createFundsHold", newArrangement, startDate, endDate, amount, "", "",
						"");
				localResult &= fundsHold(product, testType, newArrangement, "", "", "", updatedStartDate,
						updatedEndDate, updatedAmount);
				break;
			case "removeFundsHold":
				localResult = fundsHold(product, "createFundsHold", newArrangement, startDate, endDate, amount, "", "",
						"");
				localResult &= fundsHold(product, testType, newArrangement, "", "", "", "", "", "");
				break;
			default:
				break;
			}

			// Attempt to do something
			switch (actionToPerform) {
			case "Funds Transfer":

				creditAccount = findArrangement(AUTHORISED, "", CIF, "", PERSONAL_ACCOUNTS, PERSONAL_CHEQUING, CAD);

				fundTransferFields = "DEBIT.ACCT.NO," + "CREDIT.CURRENCY," + "CREDIT.AMOUNT," + "CREDIT.ACCT.NO";
				fundTransferValues = newArrangement + "," + CAD + "," + "15.00" + "," + creditAccount;

				localResult &= !financialTransaction(CREATE, "Account Transfer - Transfer between Accounts",
						fundTransferFields, fundTransferValues).contains("Error");
				break;

			case "Wait For Expiry":

				// to be done

				break;

			default:
				break;
			}

			if (localResult) {
				if (expectation) {
					Reporter.log("PASS: User should be able to perform action and action can be performed", debugMode);
				} else {
					stepResult = StatusAs.FAILED;
					stepActual = "FAIL: User should not be able to perform action, but action can be performed";
					softVerify.fail(stepActual);
				}
			} else {
				if (expectation) {
					stepResult = StatusAs.FAILED;
					stepActual = "FAIL: User should be able to perform action, but action cannot be performed";
					softVerify.fail(stepActual);
				} else {
					Reporter.log("PASS: User should not be able to perform action and action cannot be performed",
							debugMode);
				}
			}

		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
	}
}
