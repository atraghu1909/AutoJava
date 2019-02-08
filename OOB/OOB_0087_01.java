package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class OOB_0087_01 extends testLibs.BaseTest_OOB {
	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product", "customer", "actionToPerform" })
	public void step1(final String productGroup, final String product, final String customer,
			final String actionToPerform) {
		String arrangementId = null;
		String transaction;
		String paymentScheduleList = null;
		String activityName = null;
		int chargesList;
		int finalChargesList;
		int transactionCount;
		int finalTransactionCount;
		String fields = "FIXED.AMOUNT";
		String values = null;
		String chargeDescription = null;

		if (loginResult) {

			switchToBranch("B2B Branch 817");

			try {
				arrangementId = findArrangement(AUTHORISED, customer, CIF, "", productGroup, product, CAD);
				if ("Monthly Fees".equalsIgnoreCase(actionToPerform)) {
					stepDescription = "Charge monthly fees for Account :" + arrangementId;
					stepExpected = actionToPerform + " should be posted  for " + productGroup + "for arrangement: "
							+ arrangementId;

					versionScreen.linkText("Facilities and Conditions", "Conditions").click();

					paymentScheduleList = versionScreen.enquiry("PaymentSchedule").getText();
					Reporter.log(paymentScheduleList, debugMode);

					if ("Monthly Account Fee Capitalise Monthly on day 31".contains(paymentScheduleList)) {
						stepResult = StatusAs.PASSED;
						stepActual = actionToPerform + " is posted in payment schedule   ";
					} else {
						stepActual = "There was no fee posted for the action: " + actionToPerform;
						softVerify.fail(stepActual);
						stepResult = StatusAs.FAILED;
					}
				} else {

					stepDescription = "Charge " + actionToPerform + " for Account :" + arrangementId;
					stepExpected = actionToPerform + " should be posted after Activity 'Change and Capitalise : "
							+ actionToPerform + "'  for " + productGroup + " for arrangement: " + arrangementId;

					switch (actionToPerform) {

					case "OTC Withdrawal Fee":
						activityName = "Change and Capitalise ARCWDOTC";
						values = "1.25";
						chargeDescription = "Account OTC Withdrawal Fee";

						break;

					case "Certified Cheque charges":
						activityName = "Change and Capitalise ARCERTIFIEDCH";
						values = "10.00";
						chargeDescription = "Certified Cheque";

						break;

					case "EFT Received charges":
						activityName = "Change and Capitalise AREFTWIREREC";
						values = "15.00";
						chargeDescription = "EFT (wire received in CAD)";

						break;

					case "EFT Send Charges":
						activityName = "Change and Capitalise AREFTWIRESNT";
						values = "20.00";
						chargeDescription = "EFT (Sent in Canada)";

						break;

					case "Interact fee (within Canada) - ABM":
						activityName = "Change and Capitalise ABMINTCA fee";
						values = "1.50";
						chargeDescription = "ABM Interac network (within Canada)";

						break;

					case "Interact fee (Outside Canada) - ABM":
						activityName = "Change and Capitalise ABMINTOUT fee";
						values = "3.00";
						chargeDescription = "ABM PLUS network (outside Canada";

						break;

					case "Interact fee (LBCD- WITHDRAWAL) - ABM":
						activityName = "Change and Capitalise ARABMWDTRF";
						values = "1.00";
						chargeDescription = "ABM - LBCD Withd, transfer Pre-DR";

						break;

					case "Interac fund transfers charges":
						activityName = "CChange and Capitalise ARINTERACFT";
						values = "1.00";
						chargeDescription = "Interac fund transfers";

						break;

					case "Interac direct payments":
						activityName = "Change and Capitalise ARINTRACDP";
						values = "1.00";
						chargeDescription = "Interac direct payments";

						break;

					case "NSF Fee":
						activityName = "Change and Capitalise NSF fee";
						values = "50.00";
						chargeDescription = "NSF FEE";

						break;

					case "Returned Chq fee":
						activityName = "Change and Capitalise ARRETUFEE";
						values = "5.00";
						chargeDescription = "Account Returned item Fee";

						break;

					case "Stop Payment charges":
						activityName = "Change and Capitalise ARSTOPPAY";
						values = "13.00";
						chargeDescription = "Stop payment";

						break;

					case "Stop Payment Incomplete information charges":
						activityName = "Change and Capitalise ARSTOPPAYINC";
						values = "20.00";
						chargeDescription = "Stop payment Incomplete Info";

						break;

					default:
						break;
					}
					versionScreen.linkText(ADDITIONAL_DETAILS, "Charges").click();
					chargesList = versionScreen.enquiryList("Charges").size();

					transactionCount = 0;
					for (int i = 0; i < chargesList; i++) {
						transaction = versionScreen.getEnqListCell("Charges", i, "2").getText();
						if (transaction.equals(chargeDescription)) {
							transactionCount++;
						}
					}

					Reporter.log("There are " + transactionCount + " transacions posted for: " + actionToPerform
							+ " before Activity: Change and Capitalise " + actionToPerform, debugMode);

					arrangementActivity(CREATE_AUTHORISE, activityName, arrangementId, PERSONAL_ACCOUNTS, fields,
							values);

					findArrangement(AUTHORISED, arrangementId, ARRANGEMENT, "", productGroup, product, CAD);
					versionScreen.linkText(ADDITIONAL_DETAILS, "Charges").click();
					finalChargesList = versionScreen.enquiryList("Charges").size();

					finalTransactionCount = 0;
					for (int i = 0; i < finalChargesList; i++) {
						transaction = versionScreen.getEnqListCell("Charges", i, "2").getText();
						if (transaction.equals(chargeDescription)) {
							finalTransactionCount++;
						}
					}
					stepActual = "There are " + finalTransactionCount + " transacions posted for " + actionToPerform
							+ " after Activity: Change and Capitalise " + actionToPerform;

					if (finalTransactionCount == transactionCount) {

						stepActual = "There was no fee posted for the action: " + actionToPerform;
						softVerify.fail(stepActual);
						stepResult = StatusAs.FAILED;
					} else {
						stepResult = StatusAs.PASSED;
						stepActual = actionToPerform + " is posted   ";
					}

				}
			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "could not find the list of transaction charges ";
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