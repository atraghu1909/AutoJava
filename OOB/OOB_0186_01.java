package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class OOB_0186_01 extends testLibs.BaseTest_OOB {

	private String newArrangement = "";

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product", "customer" })
	public void step1(final String productGroup, final String product, final String customer) {

		String actualCIF;

		if (loginResult) {
			switchToBranch("B2B Branch 817");

			actualCIF = findCIF(customer, "", "");
			if (!actualCIF.equals(customer)) {
				Reporter.log("CIF received as parameter (" + customer + ") was not found. Using new CIF instead: "
						+ actualCIF, debugMode);

			}
			newArrangement = arrangements(CREATE, productGroup, product, "", actualCIF, "CUSTOMER:1," + "CURRENCY,",
					actualCIF + "," + CAD + ",", DefaultVariables.productGroupFields.get(productGroup),
					DefaultVariables.productGroupValues.get(productGroup));

			if (newArrangement.contains("Error")) {

				stepResult = StatusAs.FAILED;
				stepActual = "Could not create new arrangement";
				softVerify.fail(stepActual);
			} else {
				Reporter.log("Arrangement created successfully: " + newArrangement, debugMode);
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 2, enabled = true)
	public void step2() {
		if (loginResult) {
			switchToBranch("B2B Branch 817");
			if (!"".equals(newArrangement) && !newArrangement.contains("Error")) {
				findArrangement(AUTHORISED, newArrangement, ARRANGEMENT, "", PERSONAL_ACCOUNTS, "", CAD);

				for (final String winHandle : driver.getWindowHandles()) {
					driver.switchTo().window(winHandle);
				}

				versionScreen.linkText("Facilities", "Cheque").click();
				versionScreen.linkText("Cheque Enquiries", "Issued").click();

				try {
					versionScreen.dataDisplay("summary", "Cheques Issued");
				} catch (NoSuchElementException e) {

					stepResult = StatusAs.FAILED;
					stepActual = "Could not create new arrangement";
					softVerify.fail(stepActual);
					Reporter.log(e.getMessage(), false);
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}
}