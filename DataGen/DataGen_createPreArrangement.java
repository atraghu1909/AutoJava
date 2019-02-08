package testcases.DataGen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class DataGen_createPreArrangement extends testLibs.BaseTest_DataGen {

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product", "quantity", "branch" })
	public void step1(final String productGroup, final String product, final String quantity,
			@Optional("Default") final String branch) {
		final int iterations = Integer.valueOf(quantity);
		String customer;
		String mainArrangement;
		String defaultStep2Fields;
		String defaultStep2Values;
		List<String> arrangementList;
		String problem;
		String limitReference;
		String limitFields = "";
		String limitValues = "";
		String limitType;
		int termIndex;
		String completeLimitId;
		String linkStr;
		String interestFields;
		String interestValues;
		List<WebElement> listOfPaymentTypes;
		String finalMessage = "";

		arrangementList = new ArrayList<String>();

		try {
			final File file = new File("./testData/" + environmentName + "_preArrangements.txt");
			if (!file.exists()) {
				file.createNewFile();
			}

			final FileInputStream ins = new FileInputStream("./testData/" + environmentName + "_preArrangements.txt");
			final InputStreamReader inputReader = new InputStreamReader(ins);
			final BufferedReader bufferedReader = new BufferedReader(inputReader);

			String listItem = "";
			while (listItem != null) {
				listItem = bufferedReader.readLine();
				arrangementList.add(listItem);
			}
			inputReader.close();
			arrangementList.removeAll(Arrays.asList(" ", null));
		} catch (FileNotFoundException e) {
			Reporter.log("Expected file not found. Expected file at following location: " + "./testData/"
					+ environmentName + "_preArrangements.txt", debugMode);
		} catch (IOException e) {
			Reporter.log("IOException was thrown: " + e, debugMode);
		}

		stepDescription = "Create " + quantity + " Not Disbursed " + product + " Arrangements";
		stepExpected = "All Arrangements are created successfully";

		if (!"Default".equals(branch)) {
			switchToBranch(branch);
		}

		if (DefaultVariables.productFields.containsKey(product)) {
			defaultStep2Fields = DefaultVariables.productFields.get(product);
			defaultStep2Values = DefaultVariables.productValues.get(product);
		} else {
			defaultStep2Fields = DefaultVariables.productGroupFields.get(productGroup);
			defaultStep2Values = DefaultVariables.productGroupValues.get(productGroup);
		}

		for (int i = 1; i <= iterations; i++) {

			try {
				boolean result;

				// Ownership
				customer = createDefaultCustomer("", productGroup, ROLEBASED_BANKING);

				// Create Loan
				final String step1Fields = "CUSTOMER:1,CURRENCY";
				final String step1Values = customer + ",CAD";
				mainArrangement = arrangements("Validate", productGroup, product, ROLEBASED_LENDING, customer,
						step1Fields, step1Values, "", "");

				limitReference = DefaultVariables.revolvingLimitRefs.get(product);
				if (limitReference == null) {
					limitReference = readData("LIMIT.REFERENCE");
				} else {
					inputData("LIMIT.REFERENCE", limitReference, false);
				}

				termIndex = Arrays.asList(defaultStep2Fields.split(",")).lastIndexOf("TERM");

				if (DefaultVariables.productLimitType.containsKey(product)) {
					limitType = DefaultVariables.productLimitType.get(product);
					limitFields = DefaultVariables.productLimitFields.get(product) + "EXPIRY.DATE,";
					limitValues = DefaultVariables.productLimitValues.get(product)
							+ defaultStep2Values.split(",")[termIndex] + ",";
				} else if (product.contains(UNSECURED)) {
					limitType = UNSECURED;
					limitFields = DefaultVariables.unsecuredLimitFields + "EXPIRY.DATE,";
					limitValues = DefaultVariables.unsecuredLimitValues + defaultStep2Values.split(",")[termIndex]
							+ ",";
				} else {
					limitType = SECURED;
					limitFields = DefaultVariables.securedLoanLimitFields + "EXPIRY.DATE,";
					limitValues = DefaultVariables.securedLoanLimitValues + defaultStep2Values.split(",")[termIndex]
							+ ",";
				}

				completeLimitId = customerLimit("Create and Authorise", limitType, "", product, customer,
						limitReference, "01", limitFields, limitValues);
				limitReference = completeLimitId.substring(13, 17);
				mainArrangement = arrangements("Validate", productGroup, product, ROLEBASED_LENDING, customer,
						step1Fields, step1Values, "", "");
				result = inputData("LIMIT.SERIAL", "01", false) && inputData("LIMIT.REFERENCE", limitReference, false)
						&& inputData("SINGLE.LIMIT", "N", true);
				if (!mainArrangement.contains(ERROR) && result) {
					result = multiInputData(defaultStep2Fields, defaultStep2Values, true);

					linkStr = inputTable.createArrangementHeaderLinks().getText();
					if (defaultStep2Fields.contains("CHANGE.DATE.TYPE") && !linkStr.contains("Deposit Renewal")) {
						Reporter.log("Renewal Deposit can not be set up ", debugMode);
						result = false;
					}

					if (!HELOC.equals(product)) {
						interestFields = DefaultVariables.productInterestFields.get(product);
						interestValues = DefaultVariables.productInterestValues.get(product);
						if (interestFields != null) {
							result = multiInputData(interestFields, interestValues, true);
						}

						listOfPaymentTypes = inputTable.listOfFields("PAYMENT.TYPE", "Schedule");

						for (int j = 1; j <= listOfPaymentTypes.size(); j++) {
							if ("SPECIAL".equals(readData("PAYMENT.TYPE:" + j))) {
								inputData("ACTUAL.AMT:" + j + ":1", "10000", false);
								break;
							}
						}
					}

					mainArrangement = inputTable.identifier("New Arrangement").getText();
					toolElements.toolsButton(HOLD).click();
					result = inputTable.verifyAcceptOverride();
					if (result) {
						finalMessage = readTable.message().getText();
					}

					if (!finalMessage.contains(TXN_COMPLETE)) {
						mainArrangement = "Error: Problem creating CIF";
					}

					Reporter.log("Arrangement" + i + ": " + mainArrangement, debugMode);
					stepActual += System.lineSeparator();
					stepActual += "Arrangement" + i + ": " + mainArrangement;

					if (mainArrangement.contains("Error")) {
						softVerify.fail(mainArrangement);
						stepResult = StatusAs.FAILED;
					} else {
						arrangementList.add(mainArrangement);
					}
				}
			} catch (ElementNotVisibleException | NoSuchElementException e) {
				try {
					problem = "Iteration " + i + ": " + versionScreen.errorMessage().getText()
							+ " Continue with next iteration";
					stepActual += System.lineSeparator();
					stepActual += problem;
					versionScreen.errorMessage().click();
					Reporter.log(e.getMessage(), false);
				} catch (NoSuchElementException e2) {
					problem = "Exception caught in iteration " + i + ". Continue with next iteration";
					stepActual += System.lineSeparator();
					stepActual += problem;
					Reporter.log(e.getMessage(), false);
				}
				stepResult = StatusAs.FAILED;
				softVerify.fail(problem);
				Reporter.log(problem, debugMode);
				final String oldStepDescription = stepDescription;
				final String oldStepExpected = stepExpected;
				final String oldStepActual = stepActual;
				login();
				switchToBranch(branch);
				stepDescription = oldStepDescription;
				stepExpected = oldStepExpected;
				stepActual = oldStepActual;
			}
			mainArrangement = "";
		}
		try {
			final File file = new File("./testData/" + environmentName + "_preArrangements.txt");
			if (!file.exists()) {
				file.createNewFile();
			}

			final FileWriter fileWriter = new FileWriter(file, false);
			for (final String s : arrangementList) {
				fileWriter.write(s + "\n");
			}
			fileWriter.close();
		} catch (IOException e) {
			Reporter.log("IOException was thrown: " + e, debugMode);
		}
		softVerify.assertAll();
	}
}