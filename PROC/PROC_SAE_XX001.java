package testcases.PROC;

import org.openqa.selenium.NoSuchElementException;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PROC_SAE_XX001 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "2018-05-29";
	private String mainArrangement;
	private String customer;
	private String defaultStep2Fields = "";
	private String defaultStep2Values = "";
	private String transitChange;
	private String branchID;

	@Test(priority = 1, enabled = true)
	@Parameters({ "customerType", "productGroup", "product" })
	public void preCondition(final String customerType, final String productGroup, final String product) {

		stepDescription = "Switch branch, create a customer, and create a secured loan";
		stepExpected = "Switch branch, create a customer, and create a secured loan performed successfully";

		String step1Fields;
		String step1Values;

		if (loginResult) {

			switchToBranch("LAURENTIAN BANK - 523");

			customer = createDefaultCustomer(customerType, productGroup, ROLEBASED_SAE);

			if (customer == null || customer.contains(ERROR)) {
				stepActual = "Error while creating customer";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {
				step1Fields = "CUSTOMER:1," + "CURRENCY,";
				step1Values = customer + "," + "CAD,";
				if (DefaultVariables.productFields.containsKey(product)) {
					defaultStep2Fields = DefaultVariables.productFields.get(product);
					defaultStep2Values = DefaultVariables.productValues.get(product);
				} else {
					defaultStep2Fields = DefaultVariables.productGroupFields.get(productGroup);
					defaultStep2Values = DefaultVariables.productGroupValues.get(productGroup);
				}

				mainArrangement = arrangements(CREATE, productGroup, product, ROLEBASED_SAE, customer,
						step1Fields, step1Values, defaultStep2Fields, defaultStep2Values);

				if (mainArrangement == null || mainArrangement.contains(ERROR)) {
					stepActual = "Error while creating " + product + " Arrangement";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Login failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
	}

	@Test(priority = 2, enabled = true)
	@Parameters("branch")
	public void changeTransit(final String branch) {

		stepDescription = "Change Transit to: " + branch;
		stepExpected = "Transit was changed successfully to: " + branch;

		String transit;

		if (!loginResult) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Login failed";
			throw new SkipException(stepActual);
		} else {

			try {
				transit = switchToBranch(branch);
			} catch (NoSuchElementException e) {

				stepActual = "Error while changing Transit to: " + branch;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;

			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters("branch")
	public void changeProductTransit(final String branch) {
		stepDescription = "Change Product Transit from Laurentian Bank - 523 to " + branch;
		stepExpected = "Product Transit from Laurentian Bank - 523 to " + branch + " Changed successfully";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			String fields = "APPLICATION," + "CONTRACT.KEY," + "EFFECTIVE.DATE,";
			String values = "ACCOUNT," + mainArrangement + "," + "+0d,";

			transitChange = companyChange("Create", branch, fields, values);

			if (transitChange == null || transitChange.contains(ERROR)) {
				stepActual = "Error while changing Product Transit from Laurentian Bank - 523 to " + branch;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	public void authoriseTransitChange() {

		stepDescription = "Authorise TransitChange: " + transitChange;
		stepExpected = "TransitChange: " + transitChange + " authorised successfully";

		boolean result;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = authorizeEntity(transitChange, "Product Transit Change");

			if (!result) {
				stepActual = "Error while authorising TransitChange: " + transitChange;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			softVerify.assertAll();
		}
	}

	@Test(priority = 5, enabled = true)
	@Parameters({ "customerType", "branch" })
	public void changeClientTransit(final String customerType, final String branch) {

		stepDescription = "Change Client Transit from Laurentian Bank - 523 to " + branch;
		stepExpected = "Client Transit from Laurentian Bank - 523 to " + branch + " changed successfully";

		String actualCustomer;

		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			customer(OPEN, customerType, ROLEBASED_SAE, "", "");
			switchToPage(LASTPAGE, false);
			switchToTab("Other Details", "");
			inputTable.selectionCriteriaButton("COMPANY.BOOK").click();
			switchToPage(LASTPAGE, false);
			enquiryElements.enquirySearch("COMPANY.CODE", "contains", branch);
			switchToPage(LASTPAGE, false);
			branchID = enquiryElements.linkToSelect("COMPANY.BOOK").getText();

			String fields = "Other Details#COMPANY.BOOK,";
			String values = branchID + ",";
			actualCustomer = amendCIF(AMEND, customer, customerType, ROLEBASED_SAE, fields, values);

			if (actualCustomer == null || actualCustomer.contains(ERROR)) {
				stepActual = "Error while changing Client Transit from Laurentian Bank - 523 to " + branch;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			softVerify.assertAll();
		}
	}

	@Test(priority = 6, enabled = true)
	@Parameters("customerType")
	public void viewCustomerTransit(final String customerType) {

		stepDescription = "Validate that the Customer Transit was successfully changed to " + branchID;
		stepExpected = "Customer Transit was changed to " + branchID + " successfully";

		String transit;

		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			findCIF(customer, customerType, ROLEBASED_SAE);
			compositeScreen.switchToFrame(ID, "CompositeScreen");
			compositeScreen.switchToFrame(ID, "main");
			switchToTab("Basic Details", "");
			driver.manage().window().maximize();
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "CompositeScreen");
			compositeScreen.switchToFrame(ID, "workarea");
			transit = versionScreen.labelElement("Transit").getText();

			if (!transit.equals(branchID)) {
				stepActual = "Error: the value of Customer Transit is " + transit + " instead of " + branchID;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			softVerify.assertAll();
		}
	}
}