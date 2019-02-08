package testcases.TREG;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class TREG_LOS extends testLibs.BaseTest_OOB {
	@Test(priority = 1, enabled = true)
	@Parameters({ "testType", "targetCommand", "fieldName ", "originalApplication ", "originalField " })
	public void step1(final String testType, final String targetCommand, @Optional("") final String fieldName,
			@Optional("") final String originalApplication, @Optional("") final String originalField) {
		String valueIdentifier;
		String valueDisplayed = null;
		String expectedValue = null;

		commandLine(targetCommand, commandLineAvailable);

		switch (testType) {
		default:
		case "Parameter":

			stepDescription = "Check the displayed data for " + testType + "  " + targetCommand;
			stepExpected = "Data should be displayed for the " + testType + "  " + targetCommand;

			toolElements.toolsButton("Selection Criteria").click();
			switchToPage(LASTPAGE, false);
			enquiryElements.findButton().click();
			switchToPage(LASTPAGE, false);

			if (versionScreen.dataDisplay("summary", ".LOS") == null) {
				try {
					enquiryElements.enqHeaderMsg();
					if ("No data to display".equals(enquiryElements.enqHeaderMsg().getText()) || readTable.message()
							.getText().contains("We're sorry but the request could not be processed")) {
						stepActual += "No Data Displayed for the " + testType + "  " + targetCommand;
						stepActual += System.lineSeparator();
						softVerify.fail();
						stepResult = StatusAs.FAILED;
					}
				} catch (NoSuchElementException e) {
					stepActual += "Error interecting with " + targetCommand + " page";
					stepActual += System.lineSeparator();
					softVerify.fail();
					stepResult = StatusAs.FAILED;
				}
			}
			break;
		case "Enquiry":

			stepDescription = "Check the displayed data for " + testType + "  " + targetCommand;
			stepExpected = "Data should be displayed for the " + testType + "  " + targetCommand;

			enquiryElements.findButton().click();
			switchToPage(LASTPAGE, false);
			if (versionScreen.dataDisplay("summary", ".LOS") == null) {
				try {
					enquiryElements.enqHeaderMsg();
					if ("No data to display".equals(enquiryElements.enqHeaderMsg().getText()) || readTable.message()
							.getText().contains("We're sorry but the request could not be processed")) {
						stepActual += "No Data Displayed for the " + testType + "  " + targetCommand;
						stepActual += System.lineSeparator();
						softVerify.fail();
						stepResult = StatusAs.FAILED;
					}
				} catch (NoSuchElementException e) {
					stepActual += "Error interecting with " + targetCommand + " page";
					stepActual += System.lineSeparator();
					softVerify.fail();
					stepResult = StatusAs.FAILED;
				}
			}
			break;
		case "Enquiry Field":

			stepDescription = "Compare the Expected field value and Displayed field value";
			stepExpected = "Expected field value " + expectedValue + " should be same as Displayed field value ";

			enquiryElements.findButton().click();
			switchToPage(LASTPAGE, false);

			if (versionScreen.dataDisplay("summary", ".LOS") == null) {
				try {
					enquiryElements.enqHeaderMsg();
					if ("No data to display".equals(enquiryElements.enqHeaderMsg().getText()) || readTable.message()
							.getText().contains("We're sorry but the request could not be processed")) {
						stepActual += "No Data is displayed!";
						stepActual += System.lineSeparator();
						softVerify.fail();
						stepResult = StatusAs.FAILED;
					} else {
						valueIdentifier = versionScreen.getEnqListCell("r1", 1, "1").getText();
						final String column = driver
								.findElement(By.xpath(
										"//th[contains(.,'" + fieldName + "')][contains(@id,'columnHeaderText')]"))
								.getAttribute("id");
						final String columnNumber = column.substring(column.length() - 1);
						valueDisplayed = versionScreen.getEnqListCell("r1", 1, columnNumber).getText();

						switch (originalApplication) {
						default:
						case "Customer":

							commandLine("CUSTOMER", commandLineAvailable);
							enquiryElements.transactionIdField("CUSTOMER").click();
							enquiryElements.transactionIdField("CUSTOMER").sendKeys(valueIdentifier);
							toolElements.toolsButton("Edit a contract").click();

							break;
						case "Product":

							commandLine("DE.PRODUCT", commandLineAvailable);
							enquiryElements.transactionIdField("DE.PRODUCT").click();
							enquiryElements.transactionIdField("DE.PRODUCT").sendKeys(valueIdentifier);
							toolElements.toolsButton("Edit a contract").click();

							break;
						case "Collateral":

							commandLine("COLLATERAL,INP", commandLineAvailable);
							enquiryElements.transactionIdField("Collateral Details").click();
							enquiryElements.transactionIdField("Collateral Details").sendKeys(valueIdentifier);
							toolElements.toolsButton("Edit a contract").click();

							break;
						case "Collateral Right":

							commandLine("COLLATERAL.RIGHT", commandLineAvailable);
							enquiryElements.transactionIdField("COLLATERAL.RIGHT").click();
							enquiryElements.transactionIdField("COLLATERAL.RIGHT").sendKeys(valueIdentifier);
							toolElements.toolsButton("Edit a contract").click();

							break;
						case "Account":

							commandLine("ACCOUNT", commandLineAvailable);
							enquiryElements.transactionIdField("ACCOUNT").click();
							enquiryElements.transactionIdField("ACCOUNT").sendKeys(valueIdentifier);
							toolElements.toolsButton("Edit a contract").click();

							break;
						case "Posting Restrict":

							commandLine("POSTING.RESTRICT", commandLineAvailable);
							enquiryElements.transactionIdField("POSTING.RESTRICT").click();
							enquiryElements.transactionIdField("POSTING.RESTRICT").sendKeys(valueIdentifier);
							toolElements.toolsButton("Edit a contract").click();

							break;
						case "Limit":

							commandLine("LIMIT", commandLineAvailable);
							enquiryElements.transactionIdField("LIMIT").click();
							enquiryElements.transactionIdField("LIMIT").sendKeys(valueIdentifier);
							toolElements.toolsButton("Edit a contract").click();

							break;
						case "Interest Accruals":

							commandLine("AA.INTEREST.ACCRUALS", commandLineAvailable);
							enquiryElements.transactionIdField("AA.INTEREST.ACCRUALS").click();
							enquiryElements.transactionIdField("AA.INTEREST.ACCRUALS").sendKeys(valueIdentifier);
							toolElements.toolsButton("View a contract").click();

							break;
						case "Cash Pool":

							commandLine("AC.CASH.POOL", commandLineAvailable);
							enquiryElements.transactionIdField("AC.CASH.POOL").click();
							enquiryElements.transactionIdField("AC.CASH.POOL").sendKeys(valueIdentifier);
							toolElements.toolsButton("Edit a contract").click();

							break;

						}
						StringBuilder value = null;
						final String[] actualField = originalField.split(",");
						final StringBuilder actualValue = new StringBuilder(expectedValue);
						if (actualField.length > 1) {
							for (int i = 0; i < actualField.length; i++) {
								value = actualValue.append(" ").append(readData(actualField[i]));
							}
							expectedValue = value.toString();
						} else {
							expectedValue = readData(actualField[0]);
						}

						if ("".equals(originalApplication) || "".equals(originalField)) {
							if (!"".equals(valueDisplayed)) {
								stepActual += "Expected field value " + expectedValue + " and Displayed field value "
										+ valueDisplayed + " are different!";
								stepActual += System.lineSeparator();
								softVerify.fail();
								stepResult = StatusAs.FAILED;
							}
						} else if ("HOLD".equals(expectedValue) || "DELETE".equals(expectedValue)) {
							if (!"1".equals(valueDisplayed)) {
								stepActual += "Expected field value " + expectedValue + " and Displayed field value "
										+ valueDisplayed + " are different!";
								stepActual += System.lineSeparator();
								softVerify.fail();
								stepResult = StatusAs.FAILED;
							}
						} else if (!expectedValue.equals(valueDisplayed)) {
							stepActual += "Expected field value " + expectedValue + " and Displayed field value "
									+ valueDisplayed + " are different!";
							stepActual += System.lineSeparator();
							softVerify.fail();
							stepResult = StatusAs.FAILED;
						}
					}
				} catch (NoSuchElementException e) {
					stepActual += "Error interecting with " + targetCommand + " page";
					stepActual += System.lineSeparator();
					softVerify.fail();
					stepResult = StatusAs.FAILED;
				}
			}

			break;
		}
		softVerify.assertAll();
	}
}
