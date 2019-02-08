package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class OOB_0001_01 extends testLibs.BaseTest_OOB {

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product", "customer", "fields", "values", "currency" })
	public void step1(final String productGroup, final String product, final String customer,
			@Optional("") final String fields, @Optional("") final String values,
			@Optional(CAD) final String currency) {

		stepDescription = "Create " + product + " arrangement";
		stepExpected = product + " arrangement created successfully";

		String defaultFields;
		String defaultValues;
		String arrangement;
		String allFields;
		String allValues;
		String actualCIF;
		String customerType = PERSONAL;
		boolean currResult = true;
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
			final String step1Values = customer + "," + currency;
			arrangements("Validate", productGroup, product, "", customer, step1Fields, step1Values, "", "");

			if (DefaultVariables.productFields.containsKey(product)) {
				defaultFields = DefaultVariables.productFields.get(product);
				defaultValues = DefaultVariables.productValues.get(product);
			} else {
				defaultFields = DefaultVariables.productGroupFields.get(productGroup);
				defaultValues = DefaultVariables.productGroupValues.get(productGroup);
			}

			allFields = defaultFields + "," + fields;
			allValues = defaultValues + "," + values;

			currResult = multiInputData(allFields, allValues, false);

			if (currResult) {
				// Validate the arrangement has an Account Number with a check
				// digit
				// that obeys the business logic
				toolElements.toolsButton(VALIDATE_DEAL).click();

				try {
					arrangement = readData("ACCOUNT.REFERENCE");
					Reporter.log("Arrangement created. Number: " + arrangement, debugMode);

				} catch (NoSuchElementException e) {
					stepResult = StatusAs.FAILED;
					stepActual = "Error creating the Arrangement";
					softVerify.fail(stepActual);
				}
			} else {
				stepResult = StatusAs.FAILED;
				stepActual = "Error entering data in the Arrangement form";
				softVerify.fail(stepActual);
			}

		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			softVerify.fail(stepActual);
			throw new SkipException(stepActual);
		}

	}
}
