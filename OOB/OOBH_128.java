package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class OOBH_128 extends testLibs.BaseTest_OOB {

	@Test(priority = 1, enabled = true)
	@Parameters({ "customer", "productGroup", "product", "activity", "fields", "values", "authorizeFlag",
			"actionToPerform" })
	public void step1(final String customer, final String productGroup, final String product, final String activity,
			@Optional("") final String fields, @Optional("") final String values,
			@Optional("true") final boolean authorizeFlag, @Optional("") final String actionToPerform) {
		final String disbursementFields = "CREDIT.ACCT.NO,DEBIT.CURRENCY,DEBIT.ACCT.NO,DEBIT.AMOUNT";
		String debitAccount;
		String arrangement;
		String disbursementValues;
		String fee;
		String actualCIF;
		String customerType = PERSONAL;
		String createActivityAction;
		String reverseActivityAction;

		if (authorizeFlag) {
			createActivityAction = CREATE_AUTHORISE;
			reverseActivityAction = REVERSE_AUTHORISE;

		} else {

			createActivityAction = CREATE;
			reverseActivityAction = REVERSE;

		}

		stepDescription = "Create a Skip Payment Activity for a " + product;
		stepExpected = "Skip Payment Activity is successfully created";

		if (authorizeFlag) {
			stepDescription += System.lineSeparator();
			stepDescription += "Authorize the Skip Payment Activity";
			stepExpected = "Skip Payment Activity is successfully created and authorized";
		}

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
				arrangement = findArrangement(AUTHORISED, actualCIF, CIF, "", productGroup, product, CAD);
				if ("Not Disbursed".equals(versionScreen.statusElement("Status").getText())) {
					debitAccount = findArrangement(AUTHORISED, actualCIF, CIF, "", PERSONAL_ACCOUNTS, PERSONAL_CHEQUING,
							CAD);
					disbursementValues = arrangement + "," + CAD + "," + debitAccount + ",200";
					financialTransaction(CREATE_AUTHORISE, "AA Disbursement", disbursementFields, disbursementValues);
				}
				arrangementActivity(createActivityAction, activity, arrangement, productGroup, fields, values);
				switch (actionToPerform) {
				case "Reverse":
					stepDescription += System.lineSeparator();
					stepDescription += "Reverse the Skip Payment Activity";
					stepExpected = "Skip Payment Activity is created, and then reversed";
					arrangementActivity(reverseActivityAction, activity, arrangement, productGroup, "", "");
					break;
				case "Capitalise Fee":
					stepDescription += System.lineSeparator();
					stepDescription += "Verify there was a charge for the Skip Payment Activity";
					stepExpected = "Skip Payment Activity is created, and a charge for" + activity
							+ " Should be present on the " + product;
					findArrangement(AUTHORISED, arrangement, ARRANGEMENT, "", productGroup, product, CAD);
					versionScreen.linkText(ADDITIONAL_DETAILS, "Charges").click();

					try {
						final WebElement skippaymentFees = compositeScreen.feeCharged("Skip Payment Fee");
						fee = skippaymentFees.getText();
						stepActual = "Skip Payment Activity is created, and a charge of " + fee + " is present on the "
								+ product;
					} catch (NoSuchElementException e) {
						stepResult = StatusAs.FAILED;
						stepActual = "There is no Fee posted on the arrangement ";
						softVerify.fail(stepActual);
					}
					break;
				case "Do Not Capitalise Fee":
					stepDescription += System.lineSeparator();
					stepDescription += "Varify that there was no Capitalise charge for the skip payment Activity for the "
							+ product;
					stepExpected = "Skip Payment Activity is created, and there was no charge for the " + product;
					findArrangement(AUTHORISED, arrangement, ARRANGEMENT, "", productGroup, product, CAD);
					versionScreen.linkText(ADDITIONAL_DETAILS, "Charges").click();
					try {
						final WebElement skippaymentFees = compositeScreen.feeCharged("Skip Payment Fee");
						fee = skippaymentFees.getText();

						stepResult = StatusAs.FAILED;
						stepActual = "There is charge" + fee + " posted in the arrangement for the " + product;
						softVerify.fail(stepActual);

					} catch (NoSuchElementException e) {
						stepResult = StatusAs.PASSED;
						stepActual = "There is no Fee posted on the arrangement ";

					}
					break;
				default:
					break;
				}

			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "Skip Payment Activity is not created successfully ";
				softVerify.fail(stepActual);
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
	}
}
