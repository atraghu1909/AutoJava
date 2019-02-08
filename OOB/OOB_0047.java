
package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.Select;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import atu.alm.wrapper.enums.StatusAs;

public class OOB_0047 extends testLibs.BaseTest_OOB {

	@Test(priority = 1, enabled = true)
	@Parameters({ "chargeType" })
	public void step1(final String chargeType) {
		Select idDropdown;
		int totalRows = 0;

		if (loginResult) {

			switchToBranch("B2B Branch 817");

			try {

				stepDescription = "Verify " + chargeType + " charges on product ";
				stepExpected = "System displays charge list";

				commandLine("COS AA.PRODUCT.DESIGNER-PRODUCT.CONDITIONS", commandLineAvailable);
				compositeScreen.switchToFrame("id", "Classes");
				compositeScreen.textAction("CHARGE", "New Conditions").click();
				switchToPage(LASTPAGE, false);

				compositeScreen.switchToFrame("id", "Transactions");
				toolElements.toolsButton("Selection Criteria").click();
				switchToPage(LASTPAGE, false);

				idDropdown = new Select(enquiryElements.enquiryElement(DROPDOWN, "@ID"));
				idDropdown.selectByVisibleText("begins with");
				enquiryElements.enquiryElement(TEXTFIELD, "@ID").clear();
				enquiryElements.enquiryElement(TEXTFIELD, "@ID").sendKeys(chargeType);
				enquiryElements.findButton().click();
				switchToPage(LASTPAGE, false);
				totalRows = versionScreen.recordList("DATA_DISPLAY").size();

				if (totalRows == 0) {
					stepActual = chargeType + " charge list not found";
					Reporter.log(stepActual);
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

			} catch (NoSuchElementException e) {
				Reporter.log(e.getMessage(), true);
				stepActual = "Unable to display " + chargeType + " charge list";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
				Reporter.log(e.getMessage());
			}

		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}
}
