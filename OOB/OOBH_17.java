
package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class OOBH_17 extends testLibs.BaseTest_OOB {

	private String step1Result;
	private boolean expectation;
	private String[] sinArray;
	private String[] expectationArray;

	@Test(priority = 1, enabled = true)
	public void step1() {

		if (loginResult) {
			switchToBranch("B2B Branch 817");
			stepDescription = "Open the version screen to create a CIF";
			stepExpected = "Default values are populated";

			step1Result = customer("Open", PERSONAL, ROLEBASED_BANKING, DefaultVariables.personalCIFFields,
					DefaultVariables.personalCIFValues);
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}

		if (step1Result == null || step1Result.contains("Error")) {
			stepResult = StatusAs.FAILED;
			stepActual = "Issue in creating CIF";
			softVerify.fail(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 2, enabled = true)
	@Parameters({ "sinNumbers", "expectations" })
	public void step2(@Optional("") final String sinNumberString, @Optional("") final String expectationString) {
		String currentSin;
		boolean isValidSin;

		if (step1Result != null || !step1Result.contains("Error")) {

			sinArray = sinNumberString.split(",");
			expectationArray = expectationString.split(",");

			for (int i = 0; i < sinArray.length; i++) {

				try {
					inputData("SIN.NO", sinArray[i], false);
					toolElements.toolsButton(VALIDATE_DEAL).click();
					inputTable.errorMessageImg();
					isValidSin = false;
				} catch (NoSuchElementException e) {
					isValidSin = true;
					Reporter.log("Error Message Not Found", debugMode);
				}

				if ("".equals(sinArray[i])) {
					currentSin = "blank";
				} else {
					currentSin = sinArray[i];
				}
				stepDescription += "Enter SIN value " + currentSin + " and validate";
				stepDescription += System.lineSeparator();

				if (expectationArray[i].equalsIgnoreCase("pass")) {
					stepExpected += "SIN " + currentSin + " is a valid value";
					stepExpected += System.lineSeparator();
					expectation = true;
				} else {
					stepExpected += "SIN " + currentSin + " is not a valid value";
					stepExpected += System.lineSeparator();
					expectation = false;
				}

				if (isValidSin ^ expectation) {
					stepResult = StatusAs.FAILED;
					stepActual = "Test case result doesn't match its expectation for SIN " + currentSin;
					stepActual += System.lineSeparator();
					softVerify.fail(stepActual);
				}
			}
		}

		softVerify.assertAll();
	}
}
