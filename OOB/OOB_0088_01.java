package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class OOB_0088_01 extends testLibs.BaseTest_OOB {

	private boolean localResult;

	@Test(priority = 1, enabled = true)
	@Parameters({ "testType", "productGroup", "product", "customer", "amount", "transactionDate", "type", "reason" })
	public void step1(final String testType, @Optional("") final String productGroup,
			@Optional("") final String product, @Optional("") final String customer,
			@Optional("0.00") final String stringAmount, final String transactionDate, @Optional("") final String type,
			@Optional("") final String reason) {

		String actualCIF;
		String debitAccount = "";
		String creditAccount = "";
		String fundTransferFields = "";
		String fundTransferValues = "";

		final String requestClosureField = "CLOSURE.REASON," + "EFFECTIVE.DATE";
		final String requestClosureValue = reason + "," + transactionDate;

		if (loginResult) {

			switchToBranch("B2B Branch 817");

			actualCIF = findCIF(customer, "", "");
			if (!actualCIF.equals(customer)) {
				Reporter.log("CIF received as parameter (" + customer + ") was not found. Using new CIF instead: "
						+ actualCIF, debugMode);

			}

			final String step1Fields = "CUSTOMER:1,CURRENCY,EFFECTIVE.DATE";
			final String step1Values = actualCIF + "," + CAD + "," + transactionDate;

			if ("requestClosure".equals(testType)) {
				debitAccount = findArrangement(AUTHORISED, actualCIF, CIF, "", productGroup, product, CAD);
			}

			if ("fundsTransfer".equals(testType)) {
				debitAccount = findArrangement(AUTHORISED, actualCIF, CIF, "", productGroup, product, CAD);
				creditAccount = findArrangement(AUTHORISED, actualCIF, CIF, "", PERSONAL_ACCOUNTS, PERSONAL_CHEQUING,
						CAD);
				fundTransferFields = "DEBIT.ACCT.NO,DEBIT.CURRENCY,CREDIT.AMOUNT,CREDIT.ACCT.NO"
						+ ",DEBIT.VALUE.DATE,CREDIT.VALUE.DATE";
				fundTransferValues = debitAccount + "," + CAD + "," + stringAmount + "," + creditAccount + ","
						+ transactionDate + "," + transactionDate;
			}

			stepDescription = "Perform a " + testType + " action";
			stepExpected = testType + " action is performed successfully";

			switch (testType) {
			case "createAccount":

				localResult = !arrangements("Validate", productGroup, product, "", actualCIF, step1Fields, step1Values,
						"", "").contains(ERROR);

				break;
			case "requestClosure":

				requestClosure(OPEN, debitAccount, "Request Closure", productGroup, requestClosureField,
						requestClosureValue, "", "", "");
				toolElements.toolsButton(VALIDATE_DEAL).click();
				break;
			case "fundsTransfer":
				String paymentID = financialTransaction(OPEN, "Account Transfer - Transfer between Accounts",
						fundTransferFields, fundTransferValues);

				localResult = !paymentID.contains(ERROR);
				toolElements.toolsButton(VALIDATE_DEAL).click();
				break;
			default:
				break;
			}

			if (localResult) {
				stepActual = "Action " + testType + " was performed successfully";
			} else {
				stepResult = StatusAs.FAILED;
				stepActual = "Error in performing " + testType + " action";
				softVerify.fail(stepActual);
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}

	}

	@Test(priority = 2, enabled = true)
	@Parameters({ "expectation" })
	public void step2(@Optional("true") final boolean expectation) {
		stepDescription = "Check if an Error Message is displayed";

		if (loginResult) {
			switchToBranch("B2B Branch 817");
			if (expectation) {
				stepExpected = "Error Message should not appear";
			} else {
				stepExpected = "Error Message should appear";
			}

			try {
				inputTable.effectiveDateError();
				localResult = false;
				stepActual = "Error Message appeared";
			} catch (NoSuchElementException e) {
				localResult = true;
				stepActual = "Error Message did not appear";
			}

			if (expectation ^ localResult) {
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);

		}
		softVerify.assertAll();
	}
}