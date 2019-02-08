
package testcases.OOB;

import java.util.Random;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class OOBH_19 extends testLibs.BaseTest_OOB {

	private String step1Result;

	@Test(priority = 1, enabled = true)
	@Parameters({ "CIFType", "fields", "values" })
	public void step1(final String CIFType, @Optional("") final String fields, @Optional("") final String values) {

		if (loginResult) {
			switchToBranch("B2B Branch 817");

			stepDescription = "Open the version to Createa new CIF";
			stepExpected = "Version is displayed and default CIF values are populated.";
			step1Result = customer("Open", CIFType, ROLEBASED_BANKING, DefaultVariables.personalCIFFields,
					DefaultVariables.personalCIFValues);

			String actualFields = fields;
			String actualValues = values;
			final Random randomizer = new Random();
			String[] consonantArray = null;
			String[] vowelArray = null;
			final String startConsonants = "BCDFGHJKLMNPRSTVW";
			final String midConsonants = "bdfglmnprst";
			final String midVowels = "aeiou";
			final String endVowels = "aeoy";
			final StringBuilder consonantBuilder = new StringBuilder();
			final StringBuilder vowelBuilder = new StringBuilder();

			final char startConsonant = startConsonants.charAt(randomizer.nextInt(startConsonants.length()));
			final char endVowel = endVowels.charAt(randomizer.nextInt(endVowels.length()));

			for (int i = 0; i < 2; i++) {
				consonantArray = consonantBuilder
						.append(midConsonants.charAt(randomizer.nextInt(midConsonants.length()))).toString().split("");
				vowelArray = vowelBuilder.append(midVowels.charAt(randomizer.nextInt(midVowels.length()))).toString()
						.split("");
			}

			final String mnemonic = startConsonant + vowelArray[0] + consonantArray[1] + consonantArray[0] + endVowel;

			final StringBuilder fieldRequired = new StringBuilder();
			final StringBuilder valueRequired = new StringBuilder();

			if (fields.contains("MNEMONIC") && (values.contains("mnemonic"))) {
				actualFields = fieldRequired.append("MNEMONIC").toString();
				actualValues = valueRequired.append(mnemonic).toString();
			}

			if (!"".equals(fields)) {
				multiInputData(actualFields, actualValues, true);
				stepExpected += " The fields: " + fields + " are populated with: " + values;
			}

			if (step1Result == null || step1Result.contains("Error")) {
				stepResult = StatusAs.FAILED;
				stepActual = "There was an issue while entering values for CIF";
				softVerify.fail(stepActual);
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
		WebElement displayedMessage;
		String createdCIF = "";

		stepDescription = "Click on Commit to complete the creation of the CIF";
		stepExpected = "CIF is successfully created";

		if (step1Result != null || !step1Result.contains("Error")) {
			toolElements.toolsButton(COMMIT_DEAL).click();

			inputTable.verifyAcceptOverride();

			try {
				displayedMessage = readTable.message();
				createdCIF = displayedMessage.getText().substring(createdCIF.indexOf(':') + 1, 18);
				stepActual = "CIF is created successfully: " + createdCIF;
				Reporter.log(createdCIF, debugMode);

			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "There was a problem creating the CIF";
				softVerify.fail(stepActual);
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as Step 1 failed";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}
}
