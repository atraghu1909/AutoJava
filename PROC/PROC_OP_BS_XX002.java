package testcases.PROC;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class PROC_OP_BS_XX002 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.2";
	private String mainArrangement;
	private String customer;
	private String branch = "B2B Branch 817";

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product", "effectiveDate" })
	public void preCondition(@Optional(PERSONAL_ACCOUNTS) final String productGroup,
			@Optional(PERSONAL_CHEQUING) final String product, @Optional("+0d") final String effectiveDate) {

		stepDescription = "Create " + product + " Arrangement";
		stepExpected = product + " Arrangement created successfully";
		if (loginResult) {
			switchToBranch(branch);
			customer = createDefaultCustomer(PERSONAL, productGroup, ROLEBASED_BANKING);
			if (customer == null || customer.contains(ERROR)) {
				stepActual = "Error while creating customer";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {

				mainArrangement = createDefaultArrangement(productGroup, product, ROLEBASED_BANKING, customer,
						effectiveDate);
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
	public void updateBasicDetails() {

		stepDescription = "Processing changes to existing record of customer: " + customer;
		stepExpected = "Existing record of customer: " + customer + " changed successfully";

		String message;
		String fields = "FAMILY.NAME," + "NAME.1:1," + "SHORT.NAME:1,";
		String values = "Wayn," + "Wayn Bruce," + "Batman,";

		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			message = amendCIF(AMEND, customer, PERSONAL, ROLEBASED_BANKING, fields, values);

			if (message.contains(ERROR)) {
				stepActual = "Error while changing Existing CIF record for customer: " + customer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	public void updateAddress() {

		stepDescription = "Update Address for customer: " + customer;
		stepExpected = "Address for customer: " + customer + " updated successfully";

		String message;
		String fields = "RESIDENCE,RESIDENCE.REGION," + "Address#ADDRESS:1:1," + "Address#STREET:1,"
				+ "Address#TOWN.COUNTRY:1," + "Address#ADDR.CNTRY.ID," + "Address#US.STATE," + "Address#POST.CODE:1,"
				+ "Address#CITY," + "Address#RECORD.REFRESH,";
		String values = "CA," + "CA02," + "5," + "Third Street," + "Unit 5," + "CA," + "ON," + "M4C 2M7," + "Toronto,"
				+ "Y,";

		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			message = amendCIF(AMEND, customer, PERSONAL, ROLEBASED_BANKING, fields, values);

			if (message.contains(ERROR)) {
				stepActual = "Error while updating Address for customer: " + customer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	public void addNewAddress() {

		stepDescription = "Adding additional address to customer: " + customer;
		stepExpected = "Additional address to customer: " + customer + " added successfully";

		boolean result;
		final String fields;
		final String values;

		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			fields = "SHORT.NAME:1," + "NAME.1:1," + "STREET.ADDR:1," + "CITY," + "ADDR.CNTRY.ID,"
					+ "POST.CODE:1,";
			values = "Abcd," + "Efgh," + "2 Gotham Avenue," + "Gotham," + "CA," + "B1A2T3,";

			result = !addNewCustomerAddress("XML", customer, fields, values).contains(ERROR);

			if (!result) {
				stepActual = "Error while adding Additional address to customer: " + customer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	public void linkNewAddressToProduct() {

		stepDescription = "Link additional address to customer: " + customer;
		stepExpected = "Additional address linked successfully to customer: " + customer;

		boolean result;
		final String fields = "CARRIER.ADDR.NO:1,";
		final String values = "XML.2,";

		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			commandLine("DE.PRODUCT,INPUT", commandLineAvailable);
			enquiryElements.transactionIdField("Delivery product").sendKeys("CA0017000.C-" + customer + ".ALL.ALL");
			toolElements.toolsButton(EDIT_CONTRACT).click();
			switchToPage(LASTPAGE, false);
			result = multiInputData(fields, values, false);
			toolElements.toolsButton(COMMIT_DEAL).click();
			result = inputTable.verifyAcceptOverride();

			if (!result) {
				stepActual = "Error while linking Additional address to customer: " + customer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
