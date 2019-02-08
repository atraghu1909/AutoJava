package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class OOB_0076_01 extends testLibs.BaseTest_OOB {

	@Test(priority = 1, enabled = true)
	@Parameters({ "customer", "productGroup", "product", "activity", "fields", "values", "authorizeFlag", "CCY" })
	public void step1(final String customer, final String productGroup, final String product, final String activity,
			@Optional("") final String fields, @Optional("") final String values,
			@Optional("true") final boolean authorizeFlag, @Optional(CAD) final String CCY) {
		WebElement nextButton;
		String inputter;
		String date;
		String authoriser;
		String arrangement;
		boolean localResult;
		stepDescription = "Create arrangement activity " + activity + " for a " + product;
		stepExpected = "System keeps a record of the activity and displays all changes current and modified in Audit Tab";
		if (loginResult) {

			switchToBranch("B2B Branch 817");

			try {
				arrangement = findArrangement(AUTHORISED, customer, CIF, "", productGroup, product, CCY);
				if (authorizeFlag) {
					try {
						compositeScreen.pendingActivity(activity);
						Reporter.log("Utilising pre-existing activity to authorise", debugMode);
						localResult = authorizeEntity(arrangement, ACTIVITY + "," + productGroup);
					} catch (NoSuchElementException e) {

						localResult = arrangementActivity(CREATE_AUTHORISE, activity, arrangement, productGroup, fields,
								values);
					}
				} else {

					localResult = arrangementActivity(CREATE, activity, arrangement, productGroup, fields, values);
				}

				if (localResult) {
					switchToPage(environmentTitle, true);
					findArrangement(AUTHORISED, arrangement, ARRANGEMENT, "", productGroup, product, CCY);
					versionScreen.linkText(ADDITIONAL_DETAILS, ACTIVITY).click();
					versionScreen.windowAction(MAXIMIZE);
					boolean activityFound = false;
					boolean nextButtonEnabled = true;

					while (!activityFound && nextButtonEnabled) {
						try {
							nextButton = toolElements.toolsButton("Next Page");
							if (nextButton.getAttribute("src").contains("_dis.")) {
								nextButtonEnabled = false;
							}
						} catch (NoSuchElementException e) {
							Reporter.log("Next Button was not found", debugMode);
						}
						try {
							if (authorizeFlag) {
								compositeScreen.textAction(activity, "View").click();
							} else {
								compositeScreen.actionDropDown("", "drillbox").sendKeys("View");
								compositeScreen.actionButton("").click();
							}
							switchToPage(LASTPAGE, false);
							activityFound = true;
						} catch (NoSuchElementException e) {
							toolElements.toolsButton("Next Page").click();
						}
					}

					if (activityFound) {
						tabbedScreen.findTab("Audit", "").click();

						inputter = readTable.verifyActivity("Inputter.1").getText();
						date = readTable.verifyActivity("Date / Time.1").getText();
						if (authorizeFlag) {
							authoriser = readTable.verifyActivity("Authoriser").getText();
						} else {
							authoriser = "blank";
						}

						if ("".equals(inputter) || "".equals(date) || "".equals(authoriser)) {
							stepActual = "One or more of the Audit fields are blank";
							softVerify.fail(stepActual);
						} else {
							Reporter.log("Inputter    :" + inputter, debugMode);
							Reporter.log("Date/Time.1 :" + date, debugMode);
							Reporter.log("Authoriser  :" + authoriser, debugMode);
							stepActual = "Inputter :" + inputter + "  Date/Time.1: " + date + "  Authoriser :   "
									+ authoriser + "  were successfully displayed in Audit Tab";
							Reporter.log(stepActual, debugMode);
						}
					} else {
						stepActual = "Activity details for " + activity + " were not recorded on arrangement "
								+ product;
						softVerify.fail(stepActual);
						stepResult = StatusAs.FAILED;
						Reporter.log(stepActual, debugMode);
					}
				} else {
					stepActual = "Activity " + activity + " could not be created for " + product;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
					Reporter.log(stepActual, debugMode);
				}
			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "There was a problem creating activity " + activity + " for a " + product;
				softVerify.fail(stepActual);
				Reporter.log(stepActual, debugMode);
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}
	}

}