
package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class OOB_0097_01 extends testLibs.BaseTest_OOB {

	@Test(priority = 1, enabled = true)
	@Parameters({ "testType", "productGroup", "product", "customer" })
	public void step1(final String testType, @Optional("") final String productGroup,
			@Optional("") final String product, @Optional("") final String customer) {
		String returnValue = "";
		String productLine = "";
		final String fields = "+PROPERTY:2,PRD.PROPERTY:2:1,ARR.LINK:2:1";
		final String values = "PERIODICCHARGES,NONNEGOTIABLE,Non.tracking";
		String actualCIF = "";

		if (loginResult) {

			switchToBranch("B2B Branch 817");

			try {

				switch (testType) {

				case "Link Plan to Product":

					stepDescription = "Verify that system allows linking banking plan to " + product + " product ";
					stepExpected = "System allows to customize the " + product + " product ";

					commandLine("COS AA.PRODUCT.DESIGNER-PRODUCTS", commandLineAvailable);

					switch (productGroup) {

					case PERSONAL_ACCOUNTS:
					case BUSINESS_ACCOUNTS:
						productLine = "Accounts";
						break;
					case RETAIL_DEPOSITS:
					case BROKER_NONREG_DEPOSITS:
					case BROKER_REG_DEPOSITS:
						productLine = "Deposits";
						break;
					case COMMERCIAL_LOANS:
					case PERSONAL_LOANS:
					case PERSONAL_LOC:
					case RETAIL_MORTGAGES:
						productLine = "Lending";
						break;

					default:
						break;
					}

					compositeScreen.switchToFrame("id", "ProductLines");
					compositeScreen.textAction(productLine, "Product Groups").click();

					switchToPage(LASTPAGE, false);
					compositeScreen.switchToFrame("id", "ProductGroups");
					compositeScreen.textAction(productGroup, "Products").click();

					switchToPage(LASTPAGE, false);
					versionScreen.windowAction(MAXIMIZE);

					compositeScreen.switchToFrame("id", "Products");
					compositeScreen.textAction(product, AMEND).click();

					switchToPage(LASTPAGE, false);
					compositeScreen.switchToFrame("id", "Transactions");
					tabbedScreen.findTab("Property Conditions", "").click();
					multiInputData(fields, values, true);
					toolElements.toolsButton(VALIDATE_DEAL).click();

					try {
						inputTable.errorMessageImg();
						stepActual = "Error message is present";
						softVerify.fail(stepActual);
						stepResult = StatusAs.FAILED;
					} catch (NoSuchElementException e) {
						Reporter.log(e.getMessage(), false);
					}

					toolElements.toolsButton(RETURN_TO_SCREEN).click();

					versionScreen.alertAction("ACCEPT");

					break;

				case "Link Arrangement to Plan":

					stepDescription = "Verify that system allows single account for specific plan";
					stepExpected = "System allows to attached plan to single account";

					actualCIF = findCIF(customer, "", "");
					if (!actualCIF.equals(customer)) {
						Reporter.log("CIF received as parameter (" + customer
								+ ") was not found. Using new CIF instead: " + actualCIF, debugMode);
					}

					final String step1Fields = "CUSTOMER:1,CURRENCY";
					final String step1Values = actualCIF + ",CAD";
					arrangements("Validate", productGroup, product, "", actualCIF, step1Fields, step1Values, "", "");
					versionScreen.hyperLink("Periodic Charges").click();
					tabbedScreen.findTab("Included Charges", "").click();

					break;

				case "Add Plan":

					stepDescription = "Verify that system allows to add banking plan";
					stepExpected = "System allows to add plan";

					commandLine("COS AA.PRODUCT.DESIGNER-PRODUCT.CONDITIONS", commandLineAvailable);
					compositeScreen.switchToFrame("id", "Classes");
					compositeScreen.textAction("PERIODIC.CHARGES", "New Conditions").click();

					switchToPage(LASTPAGE, false);
					compositeScreen.switchToFrame("id", "Transactions");
					compositeScreen.inputField().sendKeys("TEST.CARD");
					toolElements.toolsButton("Edit a contract").click();

					returnValue = readData("FREE.CHARGE.GROUP:1") + readData("FREE.TXN.CNT:1")
							+ readData("MIN.CHG.AMOUNT:1") + readData("MAX.CHG.AMOUNT:1")
							+ readData("MIN.CHG.AMOUNT.CR:1") + readData("MAX.CHG.AMOUNT.CR:1");

					tabbedScreen.findTab("Periodic Rules", "").click();
					returnValue += readData("PR.ATTRIBUTE:1");

					toolElements.toolsButton(RETURN_TO_SCREEN).click();

					break;

				case "Change Plan":

					stepDescription = "Verify that system allows to change banking plan";
					stepExpected = "System allows to change plan";

					commandLine("COS AA.PRODUCT.DESIGNER-PRODUCT.CONDITIONS", commandLineAvailable);
					compositeScreen.switchToFrame("id", "Classes");
					compositeScreen.textAction("PERIODIC.CHARGES", "Conditions").click();

					switchToPage(LASTPAGE, false);
					versionScreen.windowAction("MAXIMIZE");
					compositeScreen.switchToFrame("id", "Conditions");
					compositeScreen.textAction("NONNEGOTIABLE", "Update").click();

					switchToPage(LASTPAGE, false);
					compositeScreen.switchToFrame("id", "Transactions");
					returnValue = readData("FREE.CHARGE.GROUP:1") + readData("FREE.TXN.CNT:1")
							+ readData("MIN.CHG.AMOUNT:1") + readData("MAX.CHG.AMOUNT:1")
							+ readData("MIN.CHG.AMOUNT.CR:1") + readData("MAX.CHG.AMOUNT.CR:1");

					tabbedScreen.findTab("Periodic Rules", "").click();
					returnValue += readData("PR.ATTRIBUTE:1");

					toolElements.toolsButton(RETURN_TO_SCREEN).click();

					break;

				case "Remove Plan":

					stepDescription = "Verify that system allows to remove banking plan";
					stepExpected = "System allows to remove plan";

					commandLine("COS AA.PRODUCT.DESIGNER-PRODUCT.CONDITIONS", commandLineAvailable);
					compositeScreen.switchToFrame("id", "Classes");
					compositeScreen.textAction("PERIODIC.CHARGES", "Conditions").click();

					switchToPage(LASTPAGE, false);
					versionScreen.windowAction("MAXIMIZE");
					compositeScreen.switchToFrame("id", "Conditions");
					compositeScreen.textAction("NONNEGOTIABLE", "Update").click();

					switchToPage(LASTPAGE, false);
					compositeScreen.switchToFrame("id", "Transactions");

					inputData("FREE.CHARGE.GROUP:1", "", true);
					inputData("FREE.TXN.CNT:1", "", true);
					inputData("MIN.CHG.AMOUNT:1", "", true);
					inputData("MAX.CHG.AMOUNT:1", "", true);
					inputData("MIN.CHG.AMOUNT.CR:1", "", true);
					inputData("MAX.CHG.AMOUNT.CR:1", "", true);

					tabbedScreen.findTab("Periodic Rules", "").click();
					inputData("PR.ATTRIBUTE:1", "", true);

					toolElements.toolsButton(VALIDATE_DEAL).click();

					try {
						inputTable.errorMessageImg();
						stepActual = "Error message is present";
						softVerify.fail(stepActual);
						stepResult = StatusAs.FAILED;
					} catch (NoSuchElementException e) {
						Reporter.log(e.getMessage(), false);
					}

					toolElements.toolsButton(RETURN_TO_SCREEN).click();
					versionScreen.alertAction("ACCEPT");

				default:
					break;

				}

				if (returnValue.contains("Element Not Found")) {
					stepActual = "Required Field is not displayed";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

			} catch (NoSuchElementException e) {
				Reporter.log(e.getMessage(), debugMode);
				stepActual = "Unable to perform " + testType + " action";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
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
