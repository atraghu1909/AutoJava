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
import org.testng.Reporter;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class DataGen_createPreCustomer extends testLibs.BaseTest_DataGen {

	@Test(priority = 1, enabled = true)
	@Parameters({ "customerType", "quantity", "branch" })
	public void step1(final String customerType, final String quantity, @Optional("Default") final String branch) {
		List<String> customerList;
		final int iterations = Integer.valueOf(quantity);
		String customer = "";
		String problem;
		String finalMessage;

		customerList = new ArrayList<String>();

		try {
			final File file = new File("./testData/" + environmentName + "_preCustomers.txt");
			if (!file.exists()) {
				file.createNewFile();
			}

			final FileInputStream ins = new FileInputStream("./testData/" + environmentName + "_preCustomers.txt");
			final InputStreamReader inputReader = new InputStreamReader(ins);
			final BufferedReader bufferedReader = new BufferedReader(inputReader);

			String listItem = "";
			while (listItem != null) {
				listItem = bufferedReader.readLine();
				customerList.add(listItem);
			}
			inputReader.close();
			customerList.removeAll(Arrays.asList(" ", null));
		} catch (FileNotFoundException e) {
			Reporter.log("Expected file not found. Expected file at following location: " + "./testData/"
					+ environmentName + "_preCustomers.txt", debugMode);
		} catch (IOException e) {
			Reporter.log("IOException was thrown: " + e, debugMode);
		}

		stepDescription = "Create " + quantity + " customers";
		stepExpected = "All customers are created successfully";

		if (!"Default".equals(branch)) {
			switchToBranch(branch);
		}

		for (int i = 1; i <= iterations; i++) {

			try {
				createDefaultCustomer(customerType, "", ROLEBASED_BANKING);
				customer = inputTable.identifier("Basic Details").getText();
				inputData("MNEMONIC", "C" + customer, true);

				toolElements.toolsButton(HOLD).click();
				finalMessage = readTable.message().getText();

				if (!finalMessage.contains(TXN_COMPLETE)) {
					customer = "Error: Problem creating CIF";
				}

				Reporter.log("Customer" + i + ": " + customer, debugMode);
				stepActual += System.lineSeparator();
				stepActual += "Customer" + i + ": " + customer;

				if (customer.contains("Error")) {
					softVerify.fail(customer);
					stepResult = StatusAs.FAILED;
				} else {
					customerList.add(customer);
				}

			} catch (ElementNotVisibleException | NoSuchElementException e) {
				try {
					problem = "Iteration " + i + ": " + versionScreen.errorMessage().getText()
							+ " Continue with next iteration";
					stepActual += problem;
					versionScreen.errorMessage().click();
					Reporter.log(e.getMessage(), false);
				} catch (NoSuchElementException e2) {
					problem = "Exception caught in iteration " + i + ". Continue with next iteration";
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
			customer = "";
		}

		try {
			final File file = new File("./testData/" + environmentName + "_preCustomers.txt");
			if (!file.exists()) {
				file.createNewFile();
			}

			final FileWriter fileWriter = new FileWriter(file, false);
			for (final String s : customerList) {
				fileWriter.write(s + "\n");
			}
			fileWriter.close();
		} catch (IOException e) {
			Reporter.log("IOException was thrown: " + e, debugMode);
		}

		softVerify.assertAll();
	}
}