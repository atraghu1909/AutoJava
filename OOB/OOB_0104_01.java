package testcases.OOB;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class OOB_0104_01 extends testLibs.BaseTest_OOB {

	private String arrangement = "";
	private String altID1;

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product", "customer" })
	public void step1(final String productGroup, final String product, final String customer) {
		String[] fieldsArray;
		String[] valuesArray;
		boolean result = true;
		String actualCIF;

		stepDescription = "Verify that system accept external account number for record";
		stepExpected = "External account number accepted for record";

		if (loginResult) {

			switchToBranch("B2B Branch 817");
			fieldsArray = DefaultVariables.bankingFields.split(",");
			valuesArray = DefaultVariables.bankingValues.split(",");

			actualCIF = findCIF(customer, "", "");
			if (!actualCIF.equals(customer)) {
				Reporter.log("CIF received as parameter (" + customer + ") was not found. Using new CIF instead: "
						+ actualCIF, debugMode);

			}

			final String step1Fields = "CUSTOMER:1,CURRENCY";
			final String step1Values = actualCIF + ",CAD";
			arrangements("Validate", productGroup, product, "", actualCIF, step1Fields, step1Values, "", "");

			arrangement = readData("ACCOUNT.REFERENCE");
			Reporter.log("Arrangement created. Number: " + arrangement, debugMode);

			// Verify if user can input information on Alternate ID field

			try {
				final Random rand = new Random();
				final int num = rand.nextInt(9000000) + 1000000;

				altID1 = "AA" + num;

				Reporter.log(altID1, debugMode);
				inputData("ALT.ID:1", altID1, false);

			} catch (NoSuchElementException e) {
				softVerify.fail("Failed");
				Reporter.log(e.getMessage(), false);
			}

			for (int i = 0; i < fieldsArray.length && result; i++) {
				if (!valuesArray[i].isEmpty()) {
					result = inputData(fieldsArray[i], valuesArray[i], false);
				}
			}

			if (result) {
				toolElements.toolsButton(COMMIT_DEAL).click();
				inputTable.verifyAcceptOverride();

				try {
					final List<WebElement> myList = driver
							.findElements(By.xpath("//table[@id='errors']/tbody/tr/td[3]"));
					final List<String> all_elements_text = new ArrayList<>();

					for (int i = 0; i < myList.size(); i++) {
						all_elements_text.add(myList.get(i).getText());
						Reporter.log("Error Message : " + myList.get(i).getText(), debugMode);
					}
				} catch (NoSuchElementException e) {
					Reporter.log("Unable to retrieve error message", debugMode);
					stepResult = StatusAs.FAILED;
					stepActual = "Unable to retrieve error message";
					softVerify.fail(stepActual);
				}

			} else {
				stepResult = StatusAs.FAILED;
				stepActual = "Error: Problem entering data into Arrangement form";
				softVerify.fail(stepActual);
			}

			authorizeEntity(arrangement, "Arrangement," + productGroup);
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	// validate that the information was saved successfully
	@Test(priority = 2, enabled = true)
	@Parameters({ "productGroup" })
	public void step2(final String productGroup) {
		String legacyID;

		stepDescription = "Validate that the information was saved";
		stepExpected = "Information was saved successfully";

		if (loginResult) {
			switchToBranch("B2B Branch 817");

			findArrangement(AUTHORISED, arrangement, ARRANGEMENT, "", productGroup, "", CAD);

			switchToPage(LASTPAGE, false);

			legacyID = versionScreen.legacySystemID().getText();

			if (legacyID.equals(altID1)) {
				Reporter.log(" Information was saved succesfully", debugMode);
			} else {
				stepResult = StatusAs.FAILED;
				stepActual = "Information was not saved";
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