package testcases.OOB;

import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.openqa.selenium.NoSuchElementException;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class OOB_0120 extends testLibs.BaseTest_OOB {

	private String newArrangement = "";

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product", "customer", "payoutAccount" })
	public void step1(final String productGroup, final String product, final String customer,
			@Optional("") final String payoutAccount) {
		String actualPayout;
		String actualCIF;
		String customerType = PERSONAL;
		stepDescription = "Create a " + product;
		stepExpected = "GIC Arrangement is created successfully";
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

			if ("".equals(payoutAccount)) {
				actualPayout = findArrangement(AUTHORISED, actualCIF, CIF, "", "Personal Accounts", PERSONAL_CHEQUING,
						CAD);
			} else {
				actualPayout = findArrangement(AUTHORISED, payoutAccount, ARRANGEMENT, "", PERSONAL_ACCOUNTS,
						PERSONAL_CHEQUING, CAD);
			}

			if (!actualPayout.equals(payoutAccount)) {
				updatedValues.add("payoutAccount," + actualPayout);
			}

			final String actualGICFields = DefaultVariables.gicFields + "," + "PAYIN.ACCOUNT:1:1";
			final String actualGICValues = DefaultVariables.gicValues + "," + actualPayout;
			newArrangement = arrangements(CREATE, productGroup, product, "", actualCIF, "CUSTOMER:1," + "CURRENCY,",
					actualCIF + "," + CAD + ",", actualGICFields, actualGICValues);
			if (newArrangement.contains("Error")) {
				stepResult = StatusAs.FAILED;
				stepActual = "Could not create new arrangement";
				softVerify.fail(stepActual);
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
	}

	@Test(priority = 2, enabled = true)
	@Parameters({ "customer", "productGroup", "product" })
	public void step2(final String customer, final String productGroup, final String product) {
		stepDescription = "Search for the payingAccountNumber element";
		stepExpected = "payingAccountNumber element is present";
		if (loginResult) {
			switchToBranch("B2B Branch 817");

			findArrangement(AUTHORISED, newArrangement, ARRANGEMENT, "", productGroup, product, CAD);

			switchToPage(LASTPAGE, false);

			try {
				compositeScreen.payingAccountNumber(newArrangement);
				stepActual = "PayingAccountNumber element was found";
			}

			catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "PayingAccountNumber element was not found";
				softVerify.fail(stepActual);
				Reporter.log(e.getMessage(), false);
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
	}
}
