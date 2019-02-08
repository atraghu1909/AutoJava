package testcases.OOB;

import java.math.BigDecimal;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class OOB_0081 extends testLibs.BaseTest_OOB {

	private BigDecimal amount;

	@Test(priority = 1, enabled = true)
	@Parameters({ "customer", "productGroup", "product", "activity" })
	public void step1(final String customer, final String productGroup, final String product, final String activity) {
		String creditAccount = "";
		String debitAccount = "";
		String actualCIF;
		String customerType = PERSONAL;
		stepDescription = "Verify Reverse Transaction";
		stepExpected = "Reverse Transaction is successfull";
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

			amount = new BigDecimal("200.00");
			try {

				debitAccount = findArrangement(AUTHORISED, actualCIF, CIF, "", PERSONAL_ACCOUNTS, "HISA Personal", CAD);

				switchToPage(environmentTitle, true);

				creditAccount = findArrangement(AUTHORISED, actualCIF, CIF, "", productGroup, product, CAD);

				switchToPage(LASTPAGE, false);
				versionScreen.linkText(ADDITIONAL_DETAILS, ACTIVITY).click();
				try {
					versionScreen.activityAction(activity, AUTHORISED, "Reverse");

				} catch (NoSuchElementException e) {
					final String fundTransferFields = "DEBIT.ACCT.NO," + "CREDIT.CURRENCY," + "CREDIT.AMOUNT,"
							+ "CREDIT.ACCT.NO";
					final String fundTransferValues = debitAccount + "," + CAD + "," + amount + "," + creditAccount;
					financialTransaction(CREATE, "Account Transfer - Transfer between Accounts", fundTransferFields,
							fundTransferValues);
					authorizeEntity(creditAccount, ACTIVITY + "," + productGroup);
				}

				arrangementActivity(REVERSE_AUTHORISE, activity, creditAccount, productGroup, "", "");
				stepActual = "Transaction " + activity + " is successfully reversed";
			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "Transaction " + activity + " could not be Reversed ";
				softVerify.fail(stepActual);
				Reporter.log(e.getMessage(), false);
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}

	}

}