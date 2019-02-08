package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class OOB_0139_01 extends testLibs.BaseTest_OOB {

	@Test(priority = 1, enabled = true)
	@Parameters({ "type", "fieldToModify", "amount", "customer", "authorizeFlag" })
	public void step1(final String type, final String fieldToModify, final String amount,
			@Optional("") final String customer, @Optional("true") final boolean authorizeFlag) {
		String actualCIF;

		if (loginResult) {
			switchToBranch("B2B Branch 817");
			String arrangement = "";
			String activityAction;

			if (authorizeFlag) {
				activityAction = CREATE_AUTHORISE;
			} else {
				activityAction = CREATE;
			}

			actualCIF = findCIF(customer, "", "");
			if (!actualCIF.equals(customer)) {
				Reporter.log("CIF received as parameter (" + customer + ") was not found. Using new CIF instead: "
						+ actualCIF, debugMode);

			}

			switch (type) {
			case "Create Arrangement":
				final String step1Fields = "CUSTOMER:1,CURRENCY";
				final String step1Values = actualCIF + ",CAD";
				arrangements("Validate", PERSONAL_ACCOUNTS, PERSONAL_CHEQUING, "", actualCIF, step1Fields, step1Values,
						"", "");

				try {
					switch (fieldToModify) {
					case "Charge":
						inputData("FIXED.AMOUNT", amount, false);
						Reporter.log("FIXED.AMOUNT found and updated by value: " + amount, debugMode);
						break;
					case "Monthly Fee":
						// TBD
						break;
					case "Interest":
						inputData("FIXED.RATE:1", amount, false);
						Reporter.log("FIXED.RATE:1 found and updated by value: " + amount, debugMode);
						break;
					default:
						break;
					}
					toolElements.toolsButton(COMMIT_DEAL).click();
				} catch (NoSuchElementException e) {
					stepResult = StatusAs.FAILED;
					stepActual = "Field to be modified was not found!";
					softVerify.fail(stepActual);
				}
				break;
			case "Update Arrangement":

				try {
					switch (fieldToModify) {
					case "Charge":
						arrangement = findArrangement(AUTHORISED, actualCIF, CIF, "", PERSONAL_ACCOUNTS,
								PERSONAL_CHEQUING, CAD);

						arrangementActivity(activityAction, "RENEGOTIATE ACTIVITY FOR ARRANGEMENT", arrangement,
								PERSONAL_ACCOUNTS, "fieldName:FIXED.AMOUNT", amount);
						Reporter.log("FIXED.AMOUNT found and updated by value: " + amount, debugMode);
						break;
					case "Monthly Fee":
						// TBD
						break;
					case "Interest":
						arrangement = findArrangement(AUTHORISED, actualCIF, CIF, "", PERSONAL_ACCOUNTS,
								PERSONAL_CHEQUING, CAD);
						arrangementActivity(activityAction, "RENEGOTIATE ACTIVITY FOR ARRANGEMENT", arrangement,
								PERSONAL_ACCOUNTS, "FIXED.RATE:1", amount);
						Reporter.log("FIXED.RATE:1 found and updated by value: " + amount, debugMode);
						break;
					default:
						break;
					}
				} catch (NoSuchElementException e) {
					stepResult = StatusAs.FAILED;
					stepActual = "Field to be modified was not found!";
					softVerify.fail(stepActual);
				}
				break;
			default:
				break;
			}

		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}
}