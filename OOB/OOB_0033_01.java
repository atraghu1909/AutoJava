package testcases.OOB;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class OOB_0033_01 extends testLibs.BaseTest_OOB {

	@Test(priority = 1, enabled = true)
	@Parameters({ "testType", "productGroup", "product", "customer", "primaryOfficer", "fields", "values",
			"expectation" })
	public void step1(final String testType, @Optional("") final String productGroup,
			@Optional("") final String product, @Optional("") final String customer, final String primaryOfficer,
			final String fields, final String values, @Optional("pass") final String expectationString) {
		String[] officerValuesArray;
		String[] officerFieldsArray;
		boolean localResult = true;
		String arrangement;
		String officerValue;
		String actualCIF = "";
		boolean expectation;

		stepDescription = testType + " " + product + " with Primary Officer set to " + primaryOfficer
				+ " and Other Officers ";
		stepExpected = testType + " performed successfully";

		if (loginResult) {

			switchToBranch("B2B Branch 817");

			if (!"".equals(customer)) {
				actualCIF = findCIF(customer, "", "");
				if (!actualCIF.equals(customer)) {
					Reporter.log("CIF received as parameter (" + customer + ") was not found. Using new CIF instead: "
							+ actualCIF, debugMode);
				}
			}

			if ("pass".equalsIgnoreCase(expectationString)) {
				expectation = true;
			} else {
				expectation = false;
			}

			try {

				officerFieldsArray = fields.split(",");
				officerValuesArray = values.split(",");

				switch (testType) {

				case "Create CIF":
					customer("Open", PERSONAL, ROLEBASED_LENDING, DefaultVariables.personalCIFFields,
							DefaultVariables.personalCIFValues);
					localResult = inputData("ACCOUNT.OFFICER", primaryOfficer, true);

					multiInputData(fields, values, true);

					toolElements.toolsButton(COMMIT_DEAL).click();
					inputTable.verifyAcceptOverride();

					break;

				case "Amend CIF":
					commandLine("COS CUST.AMEND", commandLineAvailable);
					compositeScreen.switchToFrame("src", ".jsp");
					enquiryElements.enquiryElement(TEXTFIELD, "Customer No").clear();
					enquiryElements.enquiryElement(TEXTFIELD, "Customer No").sendKeys(actualCIF);
					enquiryElements.findButton().click();
					enquiryElements.enquiryButtons(AMEND).click();
					switchToPage(LASTPAGE, false);
					compositeScreen.switchToFrame("src", ".html");

					if ("Element Not Found".equals(readData("OTHER.OFFICER:2"))) {

						multiInputData(fields, values, true);

					} else {

						final Date dNow = new Date();
						final SimpleDateFormat sdf = new SimpleDateFormat("mm/dd/yyyy hh:mm:ss", Locale.ENGLISH);
						tabbedScreen.findTab("Further Details", "").click();
						inputData("FURTHER.DETAILS", "Updated on " + sdf.format(dNow), true);
					}

					toolElements.toolsButton(COMMIT_DEAL).click();
					inputTable.verifyAcceptOverride();
					break;

				case "View CIF":
					commandLine("COS CUST.AMEND", commandLineAvailable);
					compositeScreen.switchToFrame("src", ".jsp");
					enquiryElements.enquiryElement(TEXTFIELD, "Customer No").clear();
					enquiryElements.enquiryElement(TEXTFIELD, "Customer No").sendKeys(actualCIF);
					enquiryElements.findButton().click();
					enquiryElements.enquiryButtons(AMEND).click();
					switchToPage(LASTPAGE, false);
					compositeScreen.switchToFrame("src", ".html");

					officerValue = versionScreen.viewCIFDetails("Account Officer").getAttribute("value");

					if (!officerValue.equalsIgnoreCase(primaryOfficer)) {
						localResult &= false;
					}

					for (int i = 0; i < officerFieldsArray.length; i++) {
						if (officerFieldsArray[i].contains("OTHER.OFFICER")) {
							officerValue = versionScreen.viewCIFDetails("Second Officer." + Integer.toString(i + 1))
									.getAttribute("value");

							if (!officerValue.equalsIgnoreCase(officerValuesArray[i])) {
								localResult &= false;
							}
						}
					}

					toolElements.toolsButton("Return to application screen").click();

					break;

				case "Create Arrangement":
					final String step1Fields = "CUSTOMER:1,CURRENCY";
					final String step1Values = actualCIF + ",CAD";
					arrangements("Validate", productGroup, product, "", actualCIF, step1Fields, step1Values, "", "");
					localResult = multiInputData(DefaultVariables.bankingFields, DefaultVariables.bankingValues, false);

					multiInputData(fields, values, true);

					toolElements.toolsButton(COMMIT_DEAL).click();

					break;

				case "Amend Arrangement":

					arrangement = findArrangement(AUTHORISED, actualCIF, CIF, "", productGroup, product, CAD);

					arrangementActivity(CREATE_AUTHORISE, "UPDATE ACTIVITY FOR OFFICERS", arrangement, productGroup,
							fields, values);
					break;

				case "View Arrangement":
					arrangement = findArrangement(AUTHORISED, actualCIF, CIF, "", productGroup, product, CAD);

					arrangementActivity(CREATE_AUTHORISE, "UPDATE ACTIVITY FOR OFFICERS", arrangement, productGroup,
							fields, values);
					findArrangement(AUTHORISED, actualCIF, CIF, "", productGroup, product, CAD);

					enquiryElements.enquiryButtons("View Arrangement").click();
					switchToPage(LASTPAGE, false);

					officerValue = versionScreen.viewArrangementDetails("Primary Officer").getText();

					if (!officerValue.equalsIgnoreCase(primaryOfficer)) {
						localResult &= false;
					}

					for (int i = 0; i < officerValuesArray.length && localResult; i++) {
						if (officerFieldsArray[i].contains("OTHER")) {
							officerValue = versionScreen
									.viewArrangementDetails("Other Officer." + Integer.toString(i + 1)).getText();

							if (!officerValue.equalsIgnoreCase(officerValuesArray[i])) {
								localResult &= false;
							}
						}
					}

					toolElements.toolsButton("Return to application screen").click();

					break;

				default:
					break;

				}

				if (!testType.contains("View")) {
					try {
						inputTable.errorMessageImg();
						inputTable.returnButton().click();
						versionScreen.alertAction("ACCEPT");
						Reporter.log("Error message is present", debugMode);
						localResult = false;
					} catch (NoSuchElementException e) {
						Reporter.log(e.getMessage(), false);
						localResult = true;
					}
				}

				if (expectation ^ localResult) {
					stepActual = "Expectation does not match application behaviour";
					softVerify.fail(stepActual);
					Reporter.log(stepActual, debugMode);
					stepResult = StatusAs.FAILED;
				} else {
					Reporter.log("Pass", debugMode);
				}

			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "Unable to perform required action";
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