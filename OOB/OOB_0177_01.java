package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class OOB_0177_01 extends testLibs.BaseTest_OOB {

	@Test(priority = 1, enabled = true)
	@Parameters({ "customer", "productGroup", "product", "tab" })
	public void step1(final String customer, final String productGroup, final String product, final String tab) {

		if (loginResult) {
			switchToBranch("B2B Branch 817");
			stepDescription = "Open the version screen to click on " + tab + " tab ";
			stepExpected = "Tab values are populated";

			findArrangement(AUTHORISED, customer, CIF, "", productGroup, product, CAD);

			switchToPage(LASTPAGE, false);

			try {

				switch (tab) {

				case "Bills":
					versionScreen.linkText(ADDITIONAL_DETAILS, "Bills").click();
					versionScreen.dataDisplay("id", "datadisplay_Capitalised");
					versionScreen.dataRow("id", "datadisplay_Capitalised");
					break;

				case "Schedule":
					versionScreen.linkText(ADDITIONAL_DETAILS, "Schedule").click();
					versionScreen.dataDisplay("summary", "Payment Schedule");
					versionScreen.dataRow("summary", "Payment Schedule");
					break;

				case "Charges":
					versionScreen.linkText("Facilities and Conditions", "Conditions").click();
					versionScreen.dataDisplay("id", "datadisplay_Charges");
					versionScreen.dataRow("id", "datadisplay_Charges");
					break;

				default:
					break;

				}
			} catch (NoSuchElementException e) {

				stepResult = StatusAs.FAILED;
				stepActual = "Tab data doesn't exist";
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
