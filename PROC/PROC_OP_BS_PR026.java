package testcases.PROC;

import org.openqa.selenium.NoSuchElementException;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.DefaultVariables;

public class PROC_OP_BS_PR026 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.0";
	private String mainArrangement;
	private String customer;
	private String fields;
	private String values;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "customerType", "productGroup", "product" })
	public void preCondition(@Optional("B2B Branch 623") final String branch,
			@Optional(PERSONAL) final String customerType, @Optional(PERSONAL_ACCOUNTS) final String productGroup,
			@Optional(HISA_PERSONAL) final String product) {

		stepDescription = "Create " + customerType + " customer and " + product
				+ " arrangement. Also, deposit a cheque on the arrangement";
		stepExpected = "Customer and arrangement created and cheque deposited successfully";

		String financialTransaction;

		if (loginResult) {
			switchToBranch(branch);

			customer = createDefaultCustomer(customerType, productGroup, ROLEBASED_BANKING);
			mainArrangement = createDefaultArrangement(productGroup, product, ROLEBASED_BANKING, customer, "+0d");

			if (customer == null || mainArrangement == null || customer.contains(ERROR)
					|| mainArrangement.contains(ERROR)) {
				stepActual = "Error while creating customer and " + product + " arrangement";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			fields = "TRANSACTION.TYPE," + "DEBIT.ACCT.NO," + "DEBIT.CURRENCY," + "CREDIT.CURRENCY,"
					+ "ORDERING.CUST:1," + "CREDIT.ACCT.NO," + "DEBIT.AMOUNT,";
			values = "ACAD," + "CAD1100100017623," + "CAD," + "CAD," + createdCustomers.get(customer).getFirstName()
					+ "," + mainArrangement + "," + "10T,";
			financialTransaction = financialTransaction(CREATE_AUTHORISE, "Remote Cheque Deposit", fields, values);
			if (financialTransaction == null || financialTransaction.contains(ERROR)) {
				stepActual = "Error while depositing cheque on arrangement: " + mainArrangement;
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
	@Parameters("productGroup")
	public void viewLockedFunds(@Optional(PERSONAL_ACCOUNTS) final String productGroup) {

		stepDescription = "View Locked Funds for arrangement: " + mainArrangement;
		stepExpected = "Locked Funds for arrangement: " + mainArrangement + " viewed successfully";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			try {
				findArrangement(AUTHORISED, mainArrangement, ARRANGEMENT, ROLEBASED_BANKING, productGroup, "", CAD);
				versionScreen.linkText("Account Details", "Locked Funds").click();
				toolElements.toolsButton(VIEW).click();
			} catch (NoSuchElementException e) {
				stepActual = "Error while viewing Locked Funds for arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters("productGroup")
	public void editLockedFunds(@Optional(PERSONAL_ACCOUNTS) final String productGroup) {

		stepDescription = "Edit Locked Funds for arrangement: " + mainArrangement;
		stepExpected = "Locked Funds for arrangement: " + mainArrangement + " edited successfully";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			try {
				switchToPage("Arrangement Overview (Accounts)", false);
				toolElements.toolsButton(AMEND).click();
			} catch (NoSuchElementException e) {
				stepActual = "Error while editing Locked Funds for arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters("productGroup")
	public void createNewLockedFunds(@Optional(PERSONAL_ACCOUNTS) final String productGroup) {

		stepDescription = "Create New Locked Funds for arrangement: " + mainArrangement;
		stepExpected = "New Locked Funds for arrangement: " + mainArrangement + " created successfully";

		boolean result;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			switchToPage("Arrangement Overview (Accounts)", false);
			versionScreen.linkText("Facilities and Conditions", "Facilities").click();
			versionScreen.linkText("Facilities", "Block Funds").click();
			fields = "DESCRIPTION," + "FROM.DATE," + "TO.DATE," + "LOCKED.AMOUNT,";
			values = "Deposit hold," + "+0d," + "+14d," + "1T,";
			multiInputData(fields, values, false);
			result = inputTable.commitAndOverride();

			if (!result) {
				stepActual = "Error while creating New Locked Funds for arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
