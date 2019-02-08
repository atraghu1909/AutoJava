package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class OOB_0137_01 extends testLibs.BaseTest_OOB {

	@Test(priority = 1, enabled = true)
	@Parameters({ "arrangement", "productGroup", "testType" })
	public void step1(final String arrangement, final String productGroup, final String testType) {
		String value1;
		String value2;
		String value3;

		if (loginResult) {
			switchToBranch("B2B Branch 817");

			findArrangement(AUTHORISED, arrangement, ARRANGEMENT, "", productGroup, "", CAD);
			switchToPage(LASTPAGE, false);
			switch (testType) {
			case "CR":
				versionScreen.valuesDisplayed("Credit Interest");
				try {
					value1 = versionScreen.innerElement("2", "1").getText();
					value2 = versionScreen.innerElement("2", "2").getText();
					value3 = versionScreen.innerElement("2", "3").getText();

					if (value1.isEmpty() || value2.isEmpty() || value3.isEmpty()) {
						stepResult = StatusAs.FAILED;
						stepActual = "Credit interest has missing values: = " + value1 + ", " + value2 + ", " + value3;
						Reporter.log("Credit interest has missing values: " + value1 + ", " + value2 + ", " + value3,
								debugMode);
						softVerify.fail(stepActual);
					} else {
						Reporter.log("Credit interest has values: " + value1 + ", " + value2 + ", " + value3,
								debugMode);
					}
				} catch (NoSuchElementException e) {
					stepResult = StatusAs.FAILED;
					stepActual = "No Credit interest values are found!";
					softVerify.fail(stepActual);
				}
				break;
			case "DR":
				versionScreen.valuesDisplayed("Debit Interest");
				try {
					value1 = versionScreen.innerElement("4", "1").getText();
					value2 = versionScreen.innerElement("4", "2").getText();
					value3 = versionScreen.innerElement("4", "3").getText();

					if (value1.isEmpty() || value2.isEmpty() || value3.isEmpty()) {
						stepResult = StatusAs.FAILED;
						stepActual = "Debit interest has missing values: " + value1 + ", " + value2 + ", " + value3;
						Reporter.log("Debit interest has missing values: " + value1 + ", " + value2 + ", " + value3,
								debugMode);
						softVerify.fail(stepActual);
					} else {
						Reporter.log("Debit interest has values: " + value1 + ", " + value2 + ", " + value3, debugMode);
					}
				} catch (NoSuchElementException e) {
					stepResult = StatusAs.FAILED;
					stepActual = "No Debit interest values are found!";
					softVerify.fail(stepActual);
				}
				break;
			default:
				break;

			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
	}
}