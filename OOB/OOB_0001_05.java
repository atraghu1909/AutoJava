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

public class OOB_0001_05 extends testLibs.BaseTest_OOB {

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product", "customer" })
	public void step1(final String productGroup, final String product, final String customer) {

		stepDescription = "Try to use already assigned " + product + " account number";
		stepExpected = "Not allowed to use already assigned " + product + " account number";

		final String step1Fields = "CUSTOMER:1,CURRENCY";
		String step1Values;
		String defaultFields;
		String defaultValues;
		String actualCIF;
		String accountNumber = "";
		String payingAccount = "";

		String customerType = PERSONAL;
		boolean notAllowedMsg = false;

		if (loginResult) {

			switchToBranch("B2B Branch 817");
			if (BUSINESS_ACCOUNTS.equals(productGroup) || COMMERCIAL_LOANS.equals(productGroup)) {
				customerType = BUSINESS;
			}
			actualCIF = findCIF(customer, customerType, "");
			step1Values = actualCIF + ",CAD";
			if (!actualCIF.equals(customer)) {
				Reporter.log("CIF received as parameter (" + customer + ") was not found. Using new CIF instead: "
						+ actualCIF, debugMode);

			}

			if (product.contains("GIC")) {
				payingAccount = findArrangement(AUTHORISED, actualCIF, CIF, "", PERSONAL_ACCOUNTS, "Personal Chequing",
						CAD);
			}

			arrangements("Validate", productGroup, product, "", actualCIF, step1Fields, step1Values, "", "");

			if (DefaultVariables.productFields.containsKey(product)) {
				defaultFields = DefaultVariables.productFields.get(product);
				defaultValues = DefaultVariables.productValues.get(product);
			} else {
				defaultFields = DefaultVariables.productGroupFields.get(productGroup);
				defaultValues = DefaultVariables.productGroupValues.get(productGroup);
			}

			multiInputData(defaultFields, defaultValues, false);

			if (product.contains("GIC")) {
				inputData("PAYIN.ACCOUNT:1:1", payingAccount, false);
			}

			accountNumber = readData("ACCOUNT.REFERENCE");

			toolElements.toolsButton(COMMIT_DEAL).click();
			if (inputTable.verifyAcceptOverride()) {
				Reporter.log("Override Message is: Present", debugMode);
			}

			arrangements("Validate", productGroup, product, "", actualCIF, step1Fields, step1Values, "", "");

			multiInputData(defaultFields, defaultValues, false);

			if (product.contains("GIC")) {
				inputData("PAYIN.ACCOUNT:1:1", payingAccount, false);
			}

			inputData("ACCOUNT.REFERENCE", accountNumber, false);
			toolElements.toolsButton(COMMIT_DEAL).click();

			try {
				final List<WebElement> myList = driver.findElements(By.xpath("//table[@id='errors']/tbody/tr/td[3]"));
				final List<String> allElementsText = new ArrayList<>();

				for (int i = 0; i < myList.size(); i++) {
					allElementsText.add(myList.get(i).getText());
					Reporter.log("Error Message : " + myList.get(i).getText(), debugMode);
					if (myList.get(i).getText().equals("Account Ref is already linked to another Arrangement")) {
						notAllowedMsg = true;
					}

				}
				if (!notAllowedMsg) {
					stepResult = StatusAs.FAILED;
					stepActual = "The message 'Account Ref is already linked to another Arrangement' was not present";
					softVerify.fail(stepActual);
					throw new SkipException(stepActual);
				}
			}

			catch (NoSuchElementException e) {
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
