
package testcases.OOB;

import java.util.Locale;
import org.openqa.selenium.interactions.Actions;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import atu.alm.wrapper.enums.StatusAs;

public class OOB_0058_01 extends testLibs.BaseTest_OOB {

	@Test(priority = 1, enabled = true)
	@Parameters({ "customer", "productGroup", "product", "actionToPerform" })
	public void step1(final String customer, final String productGroup, final String product,
			final String actionToPerform) {
		Actions action;
		String hrefCustomer = "";
		String hrefArrangement = "";
		String actualCIF;

		if (loginResult) {

			switchToBranch("B2B Branch 817");

			stepDescription = "Perform save as " + actionToPerform + " action for customer as well as arrangement";
			stepExpected = "Save as " + actionToPerform + " action is performed for both customer and arrangement";

			actualCIF = findCIF(customer, "", "");
			if (!actualCIF.equals(customer)) {
				Reporter.log("CIF received as parameter (" + customer + ") was not found. Using new CIF instead: "
						+ actualCIF, debugMode);

			}

			parentHandle = driver.getWindowHandle();
			action = new Actions(driver);

			commandLine("ENQ CUSTOMER.SCV", commandLineAvailable);
			enquiryElements.enquiryElement(TEXTFIELD, "Customer ID").clear();
			enquiryElements.enquiryElement(TEXTFIELD, "Customer ID").sendKeys(actualCIF);
			enquiryElements.findButton().click();
			switchToPage(LASTPAGE, false);

			action.moveToElement(toolElements.toolsButton("Enquiry Actions")).build().perform();
			switch (actionToPerform.toUpperCase(Locale.ENGLISH)) {

			case "CSV":
				hrefCustomer = versionScreen.hyperLink("Save as CSV").getAttribute("href");
				break;

			case "HTML":
				hrefCustomer = versionScreen.hyperLink("Save as HTML").getAttribute("href");
				break;

			case "PDF":
				hrefCustomer = versionScreen.hyperLink("Save as PDF").getAttribute("href");
				break;

			default:
				break;

			}

			if (!hrefCustomer.contains(actionToPerform)) {
				stepResult = StatusAs.FAILED;
				stepActual = "Issue in performing save as " + actionToPerform + " for customer";
				softVerify.fail(stepActual);
			}

			findArrangement(AUTHORISED, actualCIF, CIF, "", productGroup, product, CAD);

			switchToPage("AA Arrangement", false);

			action.moveToElement(toolElements.toolsButton("Enquiry Actions")).build().perform();
			switch (actionToPerform.toUpperCase(Locale.ENGLISH)) {

			case "CSV":
				hrefArrangement = versionScreen.hyperLink("Save as CSV").getAttribute("href");
				break;

			case "HTML":
				hrefArrangement = versionScreen.hyperLink("Save as HTML").getAttribute("href");
				break;

			case "PDF":
				hrefArrangement = versionScreen.hyperLink("Save as PDF").getAttribute("href");
				break;

			default:
				break;

			}

			if (!hrefArrangement.contains(actionToPerform)) {
				stepResult = StatusAs.FAILED;
				stepActual = "Issue in performing save as " + actionToPerform + " for arrangement";
				softVerify.fail(stepActual);
			}

		}

		else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
	}

}
