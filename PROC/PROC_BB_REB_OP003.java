package testcases.PROC;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class PROC_BB_REB_OP003 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.0";
	private String customer;
	private String branch = "B2B Branch 817";

	@Test(priority = 1, enabled = true)
	@Parameters({ "customerType" })
	public void preCondition(final String customerType) {

		stepDescription = "Create " + customerType + " customer";
		stepExpected = customerType + " customer created successfully";

		switchToBranch(branch);

		customer = createDefaultCustomer(customerType, "", ROLEBASED_LENDING);

		if (customer == null || customer.contains(ERROR)) {
			stepActual = "Error while creating customer: " + customer;
			softVerify.fail(stepActual);
			stepResult = StatusAs.FAILED;
		}
		softVerify.assertAll();
	}

	@Test(priority = 2, enabled = true)
	@Parameters({ "customerType" })
	public void createClientContactNote(final String customerType) {

		stepDescription = "Create Client Contact Note for customer: " + customer;
		stepExpected = "Client Contact Note was created successfully for customer: " + customer;

		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			boolean result;
			String fields = "CONTACT.TYPE," + "CONTACT.CHANNEL," + "CONTACT.DESC," + "CONTACT.DATE,"
					+ "CONTACT.NOTES:1," + "CONTACT.TIME,";
			String values = "Call Centre," + "CALLCENTRE," + "Test Note," + "+0d,"
					+ "This is a test of the Client Contact Note functionality," + "12:00,";

			result = customerIntervention(customer, CREATE, fields, values, customerType);

			if (!result) {
				stepActual = "Error while creating Client Contact Note for customer: " + customer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "customerType" })
	public void openClientContactNote(final String customerType) {

		stepDescription = "Search Client Contact Note for customer: " + customer;
		stepExpected = "Client Contact Note was found for customer: " + customer;

		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			boolean result;

			result = customerIntervention(customer, OPEN, "", "", customerType);
			inputTable.returnButton().click();
			Alert accept = driver.switchTo().alert();
			accept.accept();
			switchToPage(LASTPAGE, false);
			Select moreActions = new Select(driver.findElement(By.id("moreactions")));
			moreActions.selectByVisibleText("Search History File");
			toolElements.toolsButton("Go").click();
			switchToPage(LASTPAGE, false);
			enquiryElements.enquirySearch("CONTACT.CLIENT", "", customer);
			switchToPage(LASTPAGE, false);
			compositeScreen.buttonLink(HREF, "1_1").click();

			if (!result) {
				stepActual = "Error while searching Client Contact Note for customer: " + customer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
