package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class OOB_0707_01 extends testLibs.BaseTest_OOB {
	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product", "customer", "actionToPerform", })
	public void step1(final String productGroup, final String product, final String customer,
			final String actionToPerform) {
		String arrangementId = null;
		String transaction = null;
		String transactionFee = null;
		String activityName = null;
		int chargesList;
		String fields = "FIXED.AMOUNT";
		String values = "0.00";
		String actualCIF;
		String customerType = PERSONAL;

		String chargeDescription = null;

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

				arrangementId = findArrangement(AUTHORISED, actualCIF, CIF, "", productGroup, product, CAD);
				stepDescription = "Waive Account Fees for " + actionToPerform;
				stepExpected = actionToPerform + " Fees should be waived for " + productGroup + " for arrangement: "
						+ arrangementId;

				switch (actionToPerform) {

				case "Interac charges":
					activityName = "Change and Capitalise ABMINTCA fee";
					chargeDescription = "ABM Interac network (within Canada)";

					break;

				case "Stop Payment charges":
					activityName = "Change and Capitalise ARSTOPPAY";
					chargeDescription = "Stop payment";

					break;

				case "EFT charges":
					activityName = "Change and Capitalise AREFTWIRESNT";
					chargeDescription = "EFT (Sent in Canada)";

					break;

				case "Early Close charges":
					activityName = "UPDATE ACTIVITY FOR ARCLOSUREFEE";
					chargeDescription = "Account Early Closure Fee";

					break;

				default:
					break;
				}

				versionScreen.linkText("Facilities and Conditions", "Conditions").click();
				chargesList = versionScreen.enquiryList("Charges").size();

				for (int i = 0; i < chargesList; i++) {
					transaction = versionScreen.getEnqListCell("Charges", i, "1").getText();
					if (transaction.equals(chargeDescription)) {

						transactionFee = versionScreen.getEnqListCell("Charges", i + 1, "3").getText();
						Reporter.log(
								transaction + "Fee  " + transactionFee + " is posted before activity " + activityName,
								debugMode);
					}
				}

				arrangementActivity(CREATE_AUTHORISE, activityName, arrangementId, PERSONAL_ACCOUNTS, fields, values);
				arrangementId = findArrangement(AUTHORISED, actualCIF, CIF, "", productGroup, product, CAD);
				versionScreen.linkText("Facilities and Conditions", "Conditions").click();
				chargesList = versionScreen.enquiryList("Charges").size();

				for (int i = 0; i < chargesList; i++) {
					transaction = versionScreen.getEnqListCell("Charges", i, "1").getText();
					if (transaction.equals(chargeDescription)) {

						transactionFee = versionScreen.getEnqListCell("Charges", i + 1, "3").getText();
						Reporter.log(
								transaction + "Fee  " + transactionFee + " is posted after activity " + activityName,
								debugMode);
					}
				}
				arrangementActivity(REVERSE_AUTHORISE, activityName, arrangementId, PERSONAL_ACCOUNTS, "", "");

				if (transactionFee.equals("0.00 CAD")) {
					stepResult = StatusAs.PASSED;
					stepActual = chargeDescription + " charge is waived";

				} else {

					stepActual = chargeDescription + " charge is not waived";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;

				}

			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "could not find the list of charges in conditions  ";
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