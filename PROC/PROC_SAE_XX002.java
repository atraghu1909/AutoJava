package testcases.PROC;

import org.openqa.selenium.support.ui.Select;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class PROC_SAE_XX002 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "2018-05-29";
	private String personalCustomer;
	private String nonPersonalCustomer;
	private String group;
	private String customer1FIC;
	private String customer1name;
	private String personalCustomerName;
	private String nonPersonalCustomerName;
	private String groupCustomerName;

	@Test(priority = 1, enabled = true)
	@Parameters("branch")
	public void createPersonalClient(@Optional("LAURENTIAN BANK - 523") final String branch) {

		stepDescription = "Create Personal Client";
		stepExpected = "Personal Client created successfully";

		if (loginResult) {

			switchToBranch(branch);

			personalCustomer = createDefaultCustomer(PERSONAL, "", ROLEBASED_SAE);

			personalCustomerName = createdCustomers.get(personalCustomer).getFirstName() + " "
					+ createdCustomers.get(personalCustomer).getLastName();

			if (personalCustomer == null || personalCustomer.contains(ERROR)) {
				stepActual = "Error while creating Personal Client";
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
	public void createNonPersonalClient() {

		stepDescription = "Create Non-Personal Client";
		stepExpected = "Non-Personal Client created successfully";

		if (loginResult) {

			nonPersonalCustomer = createDefaultCustomer(BUSINESS, "", ROLEBASED_SAE);
			nonPersonalCustomerName = createdCustomers.get(nonPersonalCustomer).getEmployerName();

			if (nonPersonalCustomer == null || nonPersonalCustomer.contains(ERROR)) {
				stepActual = "Error while creating Non-Personal Client";
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
	public void createNonPersonalNonClient() {

		stepDescription = "Create Non-Personal Non-Client";
		stepExpected = "Non-Personal Non-Client created successfully";

		if (loginResult) {

			group = createDefaultCustomer(NON_CLIENT_BUSINESS, "", ROLEBASED_SAE);
			groupCustomerName = createdCustomers.get(group).getEmployerName();

			if (group == null || group.contains(ERROR)) {
				stepActual = "Error while creating Non-Personal Non-Client";
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
	public void modifyClient() {

		stepDescription = "Modify Client: " + personalCustomer;
		stepExpected = "Client: " + personalCustomer + " modified successfully";

		String fields = "L.MAIL.PREF,";
		String values = "Purolator,";
		boolean result;

		if (personalCustomer == null || personalCustomer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			amendCIF(OPEN, personalCustomer, PERSONAL, ROLEBASED_SAE, fields, values);

			compositeScreen.switchToFrame(ID, "FRAME02");

			switchToTab("External System ID", "");
			if (!commandLineAvailable) {
				compositeScreen.switchToFrame(ID, "FRAMETABCSM");
			}
			customer1FIC = readData("EXTERN.CUS.ID:1");
			toolElements.toolsButton(COMMIT_DEAL).click();
			switchToPage(LASTPAGE, false);
			if (!commandLineAvailable) {
				compositeScreen.switchToFrame(ID, "FRAMETABCSM");
			}
			result = compositeScreen.switchToFrame(ID, "FRAME02");
			result = inputTable.verifyAcceptOverride();

			if (!result) {
				inputTable.returnButton();
				versionScreen.alertAction("ACCEPT");
				stepActual = "Error while modifying Client: " + personalCustomer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	public void searchViaFIC() {

		stepDescription = "Search Client: " + personalCustomer + " Via FIC";
		stepExpected = "Client: " + personalCustomer + " found while searching Via FIC";

		Select dropdown;
		String searchedCustomer;

		if (personalCustomer == null || personalCustomer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			customer(OPEN, PERSONAL, ROLEBASED_SAE, "", "");
			switchToPage(LASTPAGE, false);

			toolbarElements.moreActionsDropdown("Search Live File");
			switchToPage(LASTPAGE, false);
			enquiryElements.enquirySearch("EXTERN.SYS.ID,EXTERN.CUS.ID", ",", "FIC," + customer1FIC);
			switchToPage(LASTPAGE, false);
			searchedCustomer = versionScreen.getEnqListCell("r1", 0, "2").getText();
			searchedCustomer = findCIF(searchedCustomer, PERSONAL, ROLEBASED_SAE);

			if (searchedCustomer == null || searchedCustomer.contains(ERROR)) {
				stepActual = "Client: " + personalCustomer + " was not found while searching Via FIC";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	public void linkClientToGroup() {

		stepDescription = "Link Client: " + personalCustomer + " and " + nonPersonalCustomer + " to group: " + group;
		stepExpected = "Client: " + personalCustomer + " and " + nonPersonalCustomer + " Linked to group: " + group
				+ " successfully";

		String amendCustomer1;
		String amendCustomer2;
		String fields = "Relation#RELATION.CODE:1," + "Relation#REL.CUSTOMER:1,";
		String values = "18," + group + ",";

		if (personalCustomer == null || nonPersonalCustomer == null || group == null || personalCustomer.contains(ERROR)
				|| nonPersonalCustomer.contains(ERROR) || group.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			amendCustomer1 = amendCIF(AMEND, personalCustomer, PERSONAL, ROLEBASED_SAE, fields, values);

			amendCustomer2 = amendCIF(AMEND, nonPersonalCustomer, BUSINESS, ROLEBASED_SAE, fields, values);

			if (amendCustomer1.contains(ERROR) || amendCustomer2.contains(ERROR)) {
				stepActual = "Error while Linking clients to group";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 7, enabled = true)
	public void viewClientGroup() {

		stepDescription = "Validate that Client and Group are linked";
		stepExpected = "Link between Client and Group validated successfully";

		String relationGroup = "";
		String relationPersonal = "";
		String relationNonPersonal = "";

		if (personalCustomer == null || group == null || personalCustomer.contains(ERROR) || group.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			findCIF(personalCustomer, PERSONAL, ROLEBASED_SAE);
			switchToTab("Relationship", "");
			for (int i = 0; i < versionScreen.enquiryList("DataEnquiry").size(); i++) {
				if (versionScreen.getEnqListCell("DataEnquiry", i, "2").getText().equals(groupCustomerName)) {
					relationGroup = versionScreen.getEnqListCell("DataEnquiry", i, "1").getText();
				}
			}

			if ("".equals(relationGroup)) {
				stepActual = "Group: " + group + " (" + groupCustomerName + ") is not linked to customer: "
						+ personalCustomer + " (" + personalCustomerName + ")";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			if (!"Group".equals(relationGroup)) {
				stepActual = "Linked profile's relationship to customer1 is listed as: " + relationGroup
						+ " instead of Group";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			findCIF(group, NON_CLIENT_BUSINESS, ROLEBASED_SAE);
			compositeScreen.switchToFrame(ID, "CompositeScreen");
			compositeScreen.switchToFrame(ID, "main");
			switchToTab("Relationship", "");
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "CompositeScreen");
			compositeScreen.switchToFrame(ID, "workarea");

			for (int i = 0; i < versionScreen.enquiryList("DataEnquiry").size(); i++) {
				if (versionScreen.getEnqListCell("DataEnquiry", i, "2").getText().contains(personalCustomerName)) {
					relationPersonal = versionScreen.getEnqListCell("DataEnquiry", i, "1").getText();
				}
				if (versionScreen.getEnqListCell("DataEnquiry", i, "2").getText().contains(nonPersonalCustomerName)) {
					relationNonPersonal = versionScreen.getEnqListCell("DataEnquiry", i, "1").getText();
				}
			}

			if ("".equals(relationPersonal)) {
				stepActual = "Profile: " + personalCustomer + " (" + personalCustomerName + ") is not linked to group: "
						+ group + " (" + groupCustomerName + ")";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else if (!"Subsidiary".equals(relationPersonal)) {
				stepActual = "Linked Personal profile's relationship to group is listed as: " + relationNonPersonal
						+ " instead of Subsidiary";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			if ("".equals(relationNonPersonal)) {
				stepActual = "Profile: " + nonPersonalCustomer + " (" + nonPersonalCustomerName
						+ ") is not linked to group: " + group + " (" + groupCustomerName + ")";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else if (!"Subsidiary".equals(relationNonPersonal)) {
				stepActual = "Linked Non-Personal profile's relationship to group is listed as: " + relationNonPersonal
						+ " instead of Subsidiary";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 8, enabled = true)
	public void customer360View() {

		stepDescription = "Open Customer 360 View";
		stepExpected = "Customer 360 View opened successfully";

		if (personalCustomer == null || personalCustomer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			findCIF(personalCustomer, PERSONAL, ROLEBASED_SAE);
			switchToPage(LASTPAGE, false);
			customer1name = versionScreen.labelElement("Short Name").getText();

			if (customer1name == null) {
				stepActual = "Error while opening Customer 360 View";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 9, enabled = true)
	public void clientDetailsEnquiry() {

		stepDescription = "Client Details Enquiry";
		stepExpected = "Client Details Enquiry performed successfully";

		String customerID;

		if (customer1name == null) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			commandLine("ENQ CUSTOMER.DETS.SCV", commandLineAvailable);
			enquiryElements.enquirySearch("Short name", "contains", customer1name);
			switchToPage(LASTPAGE, false);
			toolElements.toolsButton("Single Customer View").click();
			switchToPage(LASTPAGE, false);
			switchToTab("Basic Details", "");
			customerID = versionScreen.labelElement("Customer").getText();

			if (!customerID.equals(personalCustomer)) {
				stepActual = "Error while performing Client Details Enquiry";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 10, enabled = true)
	public void clientListEnquiry() {

		stepDescription = "Client List Enquiry";
		stepExpected = "Client List Enquiry performed successfully";

		String customerID;

		if (customer1name == null) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			commandLine("ENQ CUSTOMER.LIST", commandLineAvailable);
			enquiryElements.enquirySearch("Short name", "contains", customer1name);
			switchToPage(LASTPAGE, false);
			toolElements.toolsButton("View Record").click();
			switchToPage(LASTPAGE, false);
			customerID = inputTable.identifier("Basic Details").getText();

			if (!customerID.equals(personalCustomer)) {
				stepActual = "Error while performing Client List Enquiry";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
