
package testcases.OOB;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class OOTB_0013_01 extends testLibs.BaseTest_OOB {

	@Test(priority = 1, enabled = true)
	@Parameters({ "customer", "testType", "checkDisplay", "fields", "values" })
	public void step1(final String customer, final String testType, final String checkDisplay, final String fields,
			String values) {
		String actualCustomer = "";
		int rowCount;
		String finalMessage = "";
		boolean result = true;

		if (loginResult) {
			switchToBranch("B2B Branch 817");

			stepDescription = "Verify that system allows to " + testType + " customer intervention log.";
			stepExpected = "A Customer intervention log is " + testType + "ed";

			if ("Yes".equalsIgnoreCase(checkDisplay)) {

				stepDescription = stepDescription
						+ " Also, verify that customer intervention log displays successfully "
						+ "in the customer details";
				stepExpected = stepExpected + " and intervention log is displayed successfully";
			}

			try {

				actualCustomer = findContactTab(customer);

				switch (testType) {

				case "Create":

					compositeScreen.textAction("Capture New Contact", "Capture New Contact").click();
					switchToPage(LASTPAGE, false);

					break;

				case AMEND:

					compositeScreen.textAction("NO", "Update Contact").click();
					switchToPage(LASTPAGE, false);

					final Date dNow = new Date();
					final SimpleDateFormat sdf = new SimpleDateFormat("mm/dd/yyyy hh:mm:ss", Locale.ENGLISH);

					values = values.replace("Test Intervention", "Test Intervention" + "-" + sdf.format(dNow));

					break;

				default:
					break;

				}

				multiInputData(fields, values, false);

				toolElements.toolsButton(COMMIT_DEAL).click();
				result = inputTable.verifyAcceptOverride();

				if (result) {
					finalMessage = readTable.message().getText();
				}

				if (!finalMessage.contains(TXN_COMPLETE)) {
					stepActual = "User was unable to " + testType + " a Customer Intervention Log";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;

				}

				if (checkDisplay.contains("Yes")) {
					actualCustomer = findContactTab(actualCustomer);
					rowCount = versionScreen.recordList("CONTACT_ENQUIRY").size();

					if (rowCount == 0) {
						stepActual = "Customer intervention log is not displayed";
						softVerify.fail(stepActual);
						stepResult = StatusAs.FAILED;
					}
				}

			} catch (NoSuchElementException e) {
				stepActual = "Unable to perform " + testType + " cusromer intervention log";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
				Reporter.log(e.getMessage(), false);
			}

		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	public String findContactTab(final String customer) {
		String actualCustomer;
		String summary = "";
		String customerType = PERSONAL;

		actualCustomer = findCIF(customer, customerType, "");
		versionScreen.windowAction(MAXIMIZE);
		tabbedScreen.findTab("Contacts", "").click();

		summary = versionScreen.custInterventionSummary().get(0).getAttribute("summary");

		while (summary.contains("Customer Pending Process")) {
			tabbedScreen.findTab("Contacts", "").click();
			summary = versionScreen.custInterventionSummary().get(0).getAttribute("summary");

		}

		return actualCustomer;
	}

}
