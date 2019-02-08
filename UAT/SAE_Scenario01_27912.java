package testcases.UAT;

import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.testng.ISuite;
import org.testng.ITestContext;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.CustomerData;
import testLibs.DefaultVariables;

public class SAE_Scenario01_27912 extends testLibs.BaseTest_DataGen {

	private static String testVersion = "Version 2";
	private String customer;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch" })
	public void step1(@Optional("LAURENTIAN BANK - 523") final String branch) {
		stepDescription = "Go to screen: Create Customer (business)";
		stepExpected = "The page 'Business' (Profile 11) is open.";

		if (loginResult) {
			switchToBranch(branch);
			commandLine("CUSTOMER,LBC.CAMB.CORP1.SAE I F3", commandLineAvailable);
			if ("11".equals(readData("CAPL.CUS.TYPE"))) {
				switchToPage(environmentTitle, true);
			} else {
				stepActual = "Error while opening version to Create Customer (Business)";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Login failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
		
	}
	
	@Test(priority = 2, enabled = true)
	@Parameters({ "language" })
	public void step2(@Optional("1") final String language) {
		stepDescription = "Fill in the mandatory fields "
				+ "(the one with the red star. Refer to SAE - Test Data (Design Steps) "
				+ "at end click 'Validate a deal'";
		stepExpected = "No other fields are mandatory";

		if (loginResult) {
			customer = customer(OPEN, BUSINESS, ROLEBASED_SAE,
					DefaultVariables.customerTypeFields.get(BUSINESS) + "LANGUAGE,",
					DefaultVariables.customerTypeValues.get(BUSINESS) + language + ",");
			toolElements.toolsButton(VALIDATE_DEAL).click();
			if (toolElements.toolsButton(COMMIT_DEAL).getAttribute("src").contains("txncommit_dis")) {
				stepActual = "Error messages are displayed: " + inputTable.errorMessage().getText();
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Login failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
		
	}

	@Test(priority = 3, enabled = true)
	public void step3() {
		stepDescription = "Once all field are completed, click 'Commit the deal'";
		stepExpected = "You should get a message saying: txn Complete 999999 (this is the customer #) "
				+ "Time Date and more info. KEEP THIS INFORMATION IN NOTE.";

		String finalMessage;
		
		if (loginResult) {
			inputTable.commitAndOverride();
			finalMessage = readTable.message().getText();
			if (finalMessage.contains(TXN_COMPLETE)) {
				stepActual = finalMessage;
			} else {
				stepActual = "Error while creating customer: " + finalMessage;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Login failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
		
	}

	@Test(priority = 4, enabled = true)
	public void step4() {
		stepDescription = "Go to screen: CAMB Bank Menu + CAMB User Menu + Customer + Amend Address";
		stepExpected = "The 'Input Customer Address' page is display and empty.";

		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			commandLine("DE.ADDRESS,CAMB.ADD21", commandLineAvailable);
			if (!enquiryElements.transactionIdField("Input Customer Address").isDisplayed()) {
				stepActual = "Error while opening version to Amend Customer Address";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
		
	}

	@Test(priority = 5, enabled = true)
	public void step5() {
		stepDescription = "Click the Dropdown List arrow of field 'Input Customer Address'. "
				+ "Select your customer # and hit Enter.";
		stepExpected = "The 'Input Customer Address' page is display with information of your customer.";

		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			enquiryElements.transactionIdField("Input Customer Address").click();
			enquiryElements.transactionIdField("Input Customer Address").sendKeys(customer);
			enquiryElements.transactionIdField("Input Customer Address").sendKeys(Keys.ENTER);
			try {
				readData("DELIVERY.ADDRESS");
			} catch (NoSuchElementException e) {
				stepActual = "Error while opening version to Amend Customer Address";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
		
	}

	@Test(priority = 6, enabled = true)
	public void step6() {
		stepDescription = "Set up needed field.Validate the deal. Correct any error if needed.Commit the deal.";
		stepExpected = "You should get the message: "
				+ "Txn Complete: with the information of your client as well as the date and time and a bit more info.";

		String finalMessage;
		
		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			final String fields = "SHORT.NAME:1," + "NAME.1:1," + "STREET.ADDR:1," + "CITY," + "POST.CODE:1,";
			final String values = "WB," + "Wayn Bruce," + "King Street," + "Toronto," + "M4V8T6,";
			multiInputData(fields, values, false);
			inputTable.commitAndOverride();
			finalMessage = readTable.message().getText();
			if (finalMessage.contains(TXN_COMPLETE)) {
				stepActual = finalMessage;
			} else {
				stepActual = "Error while amending Customer Address: " + finalMessage;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
		
	}

	@Test(priority = 7, enabled = true)
	public void step7() {
		stepDescription = "Close this window.";
		stepExpected = "This window is closed";

		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			if (!switchToPage(environmentTitle, true)) {
				stepActual = "Error while closing the window";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
		
	}

	@Test(priority = 8, enabled = true)
	public void step8() {
		stepDescription = "Have the report printed. (Ad Hoc or ??)Make sure the report is printed in the client's language. "
				+ "Validate with timur.gayfullin@laurentianbank.ca";
		stepExpected = "The report is in the client's language WITH the return main address to send to.";

		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			// TBD
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "TBD";
			throw new SkipException(stepActual);
		}
		//softVerify.assertAll();
		
	}

	@Test(priority = 9, enabled = true)
	@Parameters({ "language" })
	public void step9(@Optional("1") final String language) {
		stepDescription = "Keep in note the main items of the individual customer.";
		stepExpected = "The main items of the individual customer.";

		String languageDescription;
		
		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			final CustomerData customerData = createdCustomers.get(customer);
			switch (language) {
			case "2": 
				languageDescription = "French";
				break;
			case "1": 
			default:
				languageDescription = "English";
				break;
			}
			
			stepActual = "Customer ID: " + customer 
					+ System.lineSeparator() 
					+ "Customer Name: " + customerData.getFirstName() + " " + customerData.getLastName() 
					+ System.lineSeparator()
					+ "Customer Address: " + customerData.getAddressStreet() + ", " + customerData.getAddressCity() 
					+ System.lineSeparator() 
					+ "Customer Language: " + languageDescription;
		}
		softVerify.assertAll();
		
	}
	
	@AfterClass (alwaysRun = true)
	public void setSuiteAttribute(final ITestContext ctx) {
		final ISuite suite = ctx.getSuite();
		suite.setAttribute("customer", customer);
	}
}
