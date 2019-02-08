
package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class OOB_0142_01 extends testLibs.BaseTest_OOB {

	@Test(priority = 1, enabled = true)
	@Parameters({ "testType", "setting", "customer", "productGroup", "product" })
	public void step1(final String testType, final String setting, @Optional("") final String customer,
			@Optional("") final String productGroup, @Optional("") final String product) {
		String typeValue;
		boolean localResult = true;
		String fields;
		String values;
		String defaultFields;
		String defaultValues;
		String actualCIF;
		;

		if (loginResult) {
			switchToBranch("B2B Branch 817");

			try {

				if ("Product Level".equalsIgnoreCase(testType)) {

					switch (setting) {

					case "Monthly Fee":

						stepDescription = "Verify that system waives monthly fee on specific product";
						stepExpected = "System waives fee on specific product";

						commandLine("COS AA.PRODUCT.DESIGNER-PRODUCT.CONDITIONS", commandLineAvailable);
						compositeScreen.switchToFrame("id", "Classes");
						compositeScreen.textAction("PAYMENT.SCHEDULE", "Conditions").click();
						switchToPage(LASTPAGE, false);
						compositeScreen.switchToFrame("id", "Conditions");
						compositeScreen.textAction("AR.CAPITALISE.INTEREST.CHARGES", "Update").click();
						switchToPage(LASTPAGE, false);
						compositeScreen.switchToFrame("id", "Transactions");
						typeValue = enquiryElements.startDateOfChargePaymentType().getAttribute("type");
						softVerify.assertEquals("text", typeValue);
						toolElements.toolsButton("Return to application screen").click();

						break;

					case "Overdraft Limit":

						stepDescription = "Verify that system allows overdraft protection on specific product";
						stepExpected = "System allows overdraft protection on specific product";

						commandLine("COS AA.PRODUCT.DESIGNER-PRODUCT.CONDITIONS", commandLineAvailable);
						compositeScreen.switchToFrame("id", "Classes");
						compositeScreen.textAction("LIMIT", "Conditions").click();
						switchToPage(LASTPAGE, false);
						compositeScreen.switchToFrame("id", "Conditions");
						compositeScreen.textAction("AR.OVERDRAFT", "Update").click();
						switchToPage(LASTPAGE, false);
						compositeScreen.switchToFrame("id", "Transactions");
						softVerify.assertEquals(inputData("LIMIT.AMOUNT", "0.00", true), true);
						toolElements.toolsButton("Return to application screen").click();

						break;

					default:
						break;
					}

				} else {

					actualCIF = findCIF(customer, "", "");
					if (!actualCIF.equals(customer)) {
						Reporter.log("CIF received as parameter (" + customer
								+ ") was not found. Using new CIF instead: " + actualCIF, debugMode);

					}
					findArrangement(AUTHORISED, actualCIF, CIF, "", productGroup, product, CAD);
					switchToPage(LASTPAGE, false);

					final String step1Fields = "CUSTOMER:1,CURRENCY";
					final String step1Values = actualCIF + ",CAD";
					localResult = !arrangements("Validate", productGroup, product, "", actualCIF, step1Fields,
							step1Values, "", "").contains(ERROR);

					if (localResult) {

						if (DefaultVariables.productFields.containsKey(product)) {
							defaultFields = DefaultVariables.productFields.get(product);
							defaultValues = DefaultVariables.productValues.get(product);
						} else {
							defaultFields = DefaultVariables.productGroupFields.get(productGroup);
							defaultValues = DefaultVariables.productGroupValues.get(productGroup);
						}

						multiInputData(defaultFields, defaultValues, true);
					}

					switch (setting) {

					case "Monthly Fee":

						stepDescription = "Verify that system waives fees for cretain period";
						stepExpected = "System waives the fees for certain period";
						fields = "+PAYMENT.TYPE:2,PAYMENT.FREQ:2,PROPERTY:2:1,START.DATE:2:1";
						values = "CHARGE,e0Y e1M e0W o1D e0F,ARMONTHLYFEE,D_20210921";
						if (localResult) {
							multiInputData(fields, values, true);

							toolElements.toolsButton(VALIDATE_DEAL).click();
						}
						break;

					case "Overdraft Limit":

						stepDescription = "Verify that system allows overdraft protection on specific amount";
						stepExpected = "System allows overdraft protection on specific amount";

						if (localResult) {
							inputData("LIMIT.AMOUNT", "1000", true);
							toolElements.toolsButton(VALIDATE_DEAL).click();
						}

						break;

					default:
						break;
					}

				}

				if (localResult) {

					try {

						inputTable.errorMessageImg();
						stepActual = "Error message is present";
						softVerify.fail(stepActual);
						stepResult = StatusAs.FAILED;

					} catch (NoSuchElementException e) {
						Reporter.log(e.getMessage(), false);
					}
				} else {
					stepActual = "Unable to create arrangement";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "Unable to perform required action";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
				Reporter.log(e.getMessage(), false);
			}

		} else

		{
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}
}
