package testcases.OOB;

import java.util.List;
import java.util.ArrayList;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;
import org.testng.SkipException;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class OOB_0508_01 extends testLibs.BaseTest_OOB {

	private String actualCIF;

	private List<String> expectedBankingAccNumbers = new ArrayList<String>();
	private List<String> expectedDepositAccNumbers = new ArrayList<String>();
	private List<String> expectedLoansAccNumbers = new ArrayList<String>();

	private List<String> actualPersonnalAccNumbers = new ArrayList<String>();
	private List<String> actualDepositAccNumbers = new ArrayList<String>();
	private List<String> actualLoanAccNumbers = new ArrayList<String>();

	public List<String> getListOfArrangements(final String comLineTosend, final String customer, final String product) {
		final List<String> accountnumbers = new ArrayList<String>();
		List<WebElement> accountList = null;
		String productCode;
		String headerMessage;

		switchToPage(environmentTitle, true);

		commandLine(comLineTosend, commandLineAvailable);
		tabbedScreen.findTab(AUTHORISED, "").click();
		enquiryElements.clearEnquiryTextFields();
		enquiryElements.enquiryElement(TEXTFIELD, OWNER).sendKeys(customer);
		productCode = DefaultVariables.productCodes.get(product);
		enquiryElements.enquiryElement(TEXTFIELD, PRODUCT).sendKeys(productCode);
		enquiryElements.findButton().click();
		switchToPage(LASTPAGE, false);
		compositeScreen.switchToFrame(ID, "top");

		headerMessage = versionScreen.enquiry("workarea").getText();
		if (!"No data to display".equalsIgnoreCase(headerMessage)) {
			accountList = versionScreen.enquiryList("workarea");
			String accountNumber = "";
			for (final WebElement item : accountList) {
				accountNumber = item.getText().split(" ")[1];
				if (accountNumber.matches("[0-9]+")) {
					accountnumbers.add(accountNumber);
				}
			}
		}

		return accountnumbers;
	}

	public boolean verifyAccountNumbers(final List<String> arrangementValues, final List<String> overviewValues) {
		boolean allAccountsFound = true;

		if (!overviewValues.containsAll(arrangementValues)) {
			allAccountsFound = false;
			for (final String s : overviewValues) {
				arrangementValues.remove(s);
			}
			for (final String s : arrangementValues) {
				Reporter.log("The account number " + s + " has not been found in the Overview ", debugMode);

			}
		}
		return allAccountsFound;
	}

	@Test(priority = 1, enabled = true)
	@Parameters({ "customer" })
	public void step1(final String customer) {

		stepDescription = "Verify that Accounts in Arrangement arrays match Customer 360 view";
		stepExpected = "Accounts in Arrangement arrays match Customer 360 view ";

		boolean testError = false;
		String errorMessage = "";
		String customerType = PERSONAL;

		if (loginResult) {
			switchToBranch("B2B Branch 817");
			actualCIF = findCIF(customer, customerType, "");
			if (!actualCIF.equals(customer)) {
				Reporter.log("CIF received as parameter (" + customer + ") was not found. Using new CIF instead: "
						+ actualCIF, debugMode);

			}

			switchToPage(LASTPAGE, false);

			try {

				expectedBankingAccNumbers = getListOfArrangements("COS AA.FIND.ARRANGEMENT.AR", actualCIF,
						B2B_BANK_CHEQUING_ACCOUNT);
			} catch (NoSuchElementException e) {
				errorMessage += "Error while getting personal account numbers for " + actualCIF + ": " + e
						+ System.lineSeparator();
				testError = true;
			}

			try {

				expectedDepositAccNumbers = getListOfArrangements("COS AA.FIND.ARRANGEMENT.AD", actualCIF,
						"Multi Rate GIC");

			} catch (NoSuchElementException e) {
				errorMessage += "Error while getting deposit account numbers for " + actualCIF + ": " + e
						+ System.lineSeparator();
				testError = true;
			}

			try {

				expectedLoansAccNumbers = getListOfArrangements("COS AA.FIND.ARRANGEMENT.AL", actualCIF,
						"Personal Loan Secured Fixed Rate");

			} catch (NoSuchElementException e) {
				errorMessage += "Error while getting loan account numbers for " + actualCIF + ": " + e
						+ System.lineSeparator();
				testError = true;
			}

			findCIF(actualCIF, customerType, "");
			tabbedScreen.findTab("Products", "").click();
			actualPersonnalAccNumbers = enquiryElements.getOverviewAccountNumbers("AccountEnquiry");
			actualDepositAccNumbers = enquiryElements.getOverviewAccountNumbers("DepositsDetails");

			tabbedScreen.findTab("Loans", "").click();
			actualLoanAccNumbers = enquiryElements.getOverviewAccountNumbers("LoanDetailsEnquiry");

			if (expectedBankingAccNumbers.isEmpty()) {
				Reporter.log("There was no Banking Arrangement to look for in Overview", debugMode);
			} else {
				if (verifyAccountNumbers(expectedBankingAccNumbers, actualPersonnalAccNumbers)) {
					Reporter.log("All Banking Arrangement have been found in Overview", debugMode);
				} else {
					testError = true;
				}
			}

			if (expectedDepositAccNumbers.isEmpty()) {
				Reporter.log("There was no Deposit Arrangement to look for in Overview", debugMode);
			} else {
				if (verifyAccountNumbers(expectedDepositAccNumbers, actualDepositAccNumbers)) {
					Reporter.log("All Deposit Arrangements have been found in Overview", debugMode);
				} else {
					testError = true;
				}
			}

			if (expectedLoansAccNumbers.isEmpty()) {
				Reporter.log("There was no Loan Arrangement to look for in Overview", debugMode);
			} else {
				if (verifyAccountNumbers(expectedLoansAccNumbers, actualLoanAccNumbers)) {
					Reporter.log("All Loan Arrangements have been found in Overview", debugMode);
				} else {
					testError = true;
				}
			}

			if (testError) {
				stepActual = errorMessage;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
				Reporter.log(stepActual, debugMode);
			}

		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			softVerify.fail(stepActual);
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();

	}
}
