package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class OOBH_187 extends testLibs.BaseTest_OOB {

	@Test(priority = 1, enabled = true)
	@Parameters({ "customer", "fields", "values", "productGroup", "product", "activity" })
	public void step1(final String customer, @Optional("") final String fields, @Optional("") final String values,
			@Optional("") final String productGroup, @Optional("") final String product,
			@Optional("") final String activity) {

		String actualCIF;
		String customerType = PERSONAL;
		stepDescription = "Verify System displays overdue statistics on Arrangement Details";
		stepExpected = "Overdue statistics are displayed";

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

			switchToPage(environmentTitle, true);
			final String arrangementId = findArrangement(AUTHORISED, actualCIF, CIF, "", productGroup, product, CAD);

			if (!"None".equals(activity)) {
				if ("Not Disbursed".equals(versionScreen.statusElement("Status").getText())) {
					final String disbursementFields = "CREDIT.ACCT.NO,DEBIT.CURRENCY,DEBIT.ACCT.NO,DEBIT.AMOUNT";
					final String disbursementValues = "AA1624680FL9," + CAD + "," + arrangementId + ",5000";
					financialTransaction(CREATE_AUTHORISE, "AA Disbursement", disbursementFields, disbursementValues);
				}
				try {
					findArrangement(AUTHORISED, arrangementId, ARRANGEMENT, "", productGroup, product, CAD);
					versionScreen.activityAction(activity, AUTHORISED, "Reverse");
				} catch (NoSuchElementException e) {
					arrangementActivity("Open", activity, arrangementId, productGroup, "", "");
					final String word = inputTable.totalAmountElement().getAttribute("id");
					final String numberDigits = word.substring(word.length() - 3);
					final String fieldsTotal = fields + numberDigits;

					multiInputData(fieldsTotal, values, false);

					toolElements.toolsButton(COMMIT_DEAL).click();
					inputTable.verifyAcceptOverride();

					authorizeEntity(arrangementId, ACTIVITY + "," + productGroup);
				}
				arrangementActivity(REVERSE_AUTHORISE, activity, arrangementId, productGroup, "", "");

				findArrangement(AUTHORISED, arrangementId, ARRANGEMENT, "", productGroup, product, CAD);

			}
			try {
				versionScreen.linkText(ADDITIONAL_DETAILS, "Overdue").click();
				versionScreen.overdueStatisticTable();
				stepActual = "Overdue statistics were displayed";
			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "Overdue statistics were not displayed";
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