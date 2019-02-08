package testcases.OOB;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class OOB_0001_04 extends testLibs.BaseTest_OOB {

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product", "customer" })
	public void step1(final String productGroup, final String product, final String customer) {

		stepDescription = "Modify arrangement reference";
		stepExpected = "Arrangement reference modified successfully";

		String defaultFields;
		String defaultValues;
		String actualCIF;
		String customerType = PERSONAL;
		stepResult = StatusAs.PASSED;

		if (loginResult) {

			switchToBranch("B2B Branch 817");
			if (BUSINESS_ACCOUNTS.equals(productGroup) || COMMERCIAL_LOANS.equals(productGroup)) {
				customerType = BUSINESS;
			}
			actualCIF = findCIF(customer, customerType, "");
			if (!actualCIF.equals(customer)) {
				Reporter.log("CIF received as parameter (" + customer + ") was not found. Using new CIF instead: "
						+ actualCIF, debugMode);

			}
			final String step1Fields = "CUSTOMER:1,CURRENCY";
			final String step1Values = actualCIF + ",CAD";
			arrangements("Validate", productGroup, product, "", actualCIF, step1Fields, step1Values, "", "");

			if (DefaultVariables.productFields.containsKey(product)) {
				defaultFields = DefaultVariables.productFields.get(product);
				defaultValues = DefaultVariables.productValues.get(product);
			} else {
				defaultFields = DefaultVariables.productGroupFields.get(productGroup);
				defaultValues = DefaultVariables.productGroupValues.get(productGroup);
			}

			multiInputData(defaultFields, defaultValues, false);

			try {

				String accountNumber = readData("ACCOUNT.REFERENCE");
				Reporter.log("The Account Number is " + accountNumber, debugMode);

				if (accountNumber.substring(accountNumber.length() - 1).equals("6")) {
					accountNumber = accountNumber.substring(0, accountNumber.length() - 1) + "8";
				} else {
					accountNumber = accountNumber.substring(0, accountNumber.length() - 1) + "6";
				}

				inputData("ACCOUNT.REFERENCE", accountNumber, false);
				Reporter.log("The modifed Account Number is " + accountNumber, debugMode);
				Reporter.log("Account Reference is Editable", debugMode);
			} catch (NoSuchElementException e) {
				Reporter.log(e.getMessage(), false);
				stepResult = StatusAs.FAILED;
				stepActual = "User could not read 'ACCOUNT.REFERENCE'";
				softVerify.fail(stepActual);
			}

			try {

				toolElements.toolsButton(COMMIT_DEAL).click();

				final List<WebElement> myList = driver.findElements(By.xpath("//table[@id='errors']/tbody/tr/td[3]"));
				final List<String> allElementsText = new ArrayList<>();

				for (int i = 0; i < myList.size(); i++) {
					allElementsText.add(myList.get(i).getText());
					Reporter.log("Error Message : " + myList.get(i).getText(), debugMode);
					stepResult = StatusAs.FAILED;
					stepActual = "Error Message : " + myList.get(i).getText();
					softVerify.fail(stepActual);
				}
			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "User could not click on the link to commit the deal";
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
