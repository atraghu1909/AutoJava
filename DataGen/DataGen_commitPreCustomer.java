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

public class DataGen_commitPreCustomer extends testLibs.BaseTest_DataGen {

	@Test(priority = 1, enabled = true)
	@Parameters({ "quantity", "branch" })
	public void step1(final String quantity, @Optional("Default") final String branch) {
		List<String> customerList;
		List<String> failedCustomerList;
		final int iterations = Integer.valueOf(quantity);
		String problem;
		String finalMessage;
		String customer;
		int listSize;

		customerList = new ArrayList<String>();
		failedCustomerList = new ArrayList<String>();
		
		try {
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
			listSize = customerList.size();
		} catch (FileNotFoundException e) {
			Reporter.log("Expected file not found. Expected file at following location: "
					+ "./testData/" + environmentName + "_preCustomers.txt", debugMode);
			listSize = 0;
		} catch (IOException e) {
			Reporter.log("IOException was thrown: " + e, debugMode);
			listSize = 0;
		}

		if (!"Default".equals(branch)) {
			switchToBranch(branch);
		}

		stepDescription = "Commit all " + listSize + " customers";
		stepExpected = "All customers are created successfully";

		for (int i = 0; i < listSize && i < iterations; i++) {
			customer = customerList.get(i);

			try {
				commandLine("CUSTOMER,LBC.CAMB.INPUT2.CIF", commandLineAvailable);
				switchToPage(LASTPAGE, false);
				compositeScreen.inputField().sendKeys(customer);
				toolElements.toolsButton("Edit a contract").click();
				toolElements.toolsButton(COMMIT_DEAL).click();
				inputTable.verifyAcceptOverride();
				finalMessage = readTable.message().getText();

				if (!finalMessage.contains(TXN_COMPLETE)) {
					customer = "Error: Problem creating CIF";
				}

				stepActual += "Customer: " + customer;
				Reporter.log("Customer: " + customer, debugMode);

				if (customer.contains("Error")) {
					softVerify.fail(customer);
					stepResult = StatusAs.FAILED;
					failedCustomerList.add(customer);
				}

			} catch (ElementNotVisibleException | NoSuchElementException e) {
				try {
					problem = "Iteration " + i + ": " + versionScreen.errorMessage().getText()
							+ " Continue with next iteration";
					stepActual += problem;
					versionScreen.errorMessage().click();
					Reporter.log(e.getMessage(), false);
					failedCustomerList.add(customer);
				} catch (NoSuchElementException e2) {
					problem = "Exception caught in iteration " + i + ". Continue with next iteration";
					stepActual += problem;
					Reporter.log(e.getMessage(), false);
					failedCustomerList.add(customer);
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
		
		if (listSize > iterations) {
			for (int i = iterations; i < listSize; i++) {
				failedCustomerList.add(customerList.get(i));
			}
		}
		
		try {
			final File file = new File("./testData/" + environmentName + "_preCustomers.txt");
			if (!file.exists()) {
				file.createNewFile();
			}

			final FileWriter fileWriter = new FileWriter(file, false);
			for (final String s : failedCustomerList) {
				fileWriter.write(s + "\n");
			}
			fileWriter.close();
		} catch (IOException e) {
			Reporter.log("IOException was thrown: " + e, debugMode);
		}
		
		softVerify.assertAll();
	}
}