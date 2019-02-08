package testcases.PROC;

import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PROC_BB_REB_OP001 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 4.0";

	private String branch = "B2B Branch 817";

	@Test(priority = 1, enabled = true)
	public void createPersonalClient() {

		stepDescription = "Create Personal Client";
		stepExpected = "Personal Client was Created successfully";
		switchToBranch(branch);
		String personalClient;
		String fields = "APPLIED.FOR.US.TIN," + "TAX.RESIDENCE:1," + "TAX.ID:1,";
		String values = "N," + "GB," + "123456789,";

		commandLine("ENQ CAMB.CUST.SRC.CIF", commandLineAvailable);
		enquiryElements.enquirySearch("Last Name", "", "CrazyTest");
		switchToPage(LASTPAGE, false);
		personalClient = enquiryElements.enqHeaderMsg().getText();
		if ("No data to display".equalsIgnoreCase(personalClient)) {
			personalClient = customer(CREATE, PERSONAL, ROLEBASED_BANKING, DefaultVariables.personalCIFFields,
					DefaultVariables.personalCIFValues);
			commandLine("EB.LBC.CRS.DATA,LBC.INDIVIDUAL", commandLineAvailable);
			enquiryElements.transactionIdField("Additional data for CRS - Individual").click();
			enquiryElements.transactionIdField("Additional data for CRS - Individual").sendKeys(personalClient);
			toolElements.toolsButton(EDIT_CONTRACT).click();

			multiInputData(fields, values, false);
			toolElements.toolsButton(COMMIT_DEAL).click();
			inputTable.verifyAcceptOverride();

		} else {
			stepActual = "Personal Client record already exists and therefore should not be created again";
			softVerify.fail(stepActual);
			stepResult = StatusAs.FAILED;
		}

		if (personalClient.contains(ERROR)) {
			stepActual = "Error while creating Personal Client";
			softVerify.fail(stepActual);
			stepResult = StatusAs.FAILED;
		}
		softVerify.assertAll();
	}

	@Test(priority = 2, enabled = true)
	public void createPersonalNonClient() {

		stepDescription = "Create Personal Non-Client";
		stepExpected = "Personal Non-Client was Created successfully";
		switchToBranch(branch);
		String personalNonClient;
		personalNonClient = customer(CREATE, NON_CLIENT_PERSONAL, ROLEBASED_BANKING, DefaultVariables.personalCIFFields,
				DefaultVariables.personalCIFValues);

		if (personalNonClient.contains(ERROR)) {
			stepActual = "Error while creating Personal Non-Client";
			softVerify.fail(stepActual);
			stepResult = StatusAs.FAILED;
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	public void createBusinessClient() {
		stepDescription = "Create Business Client";
		stepExpected = "Business Client was created successfully";
		switchToBranch(branch);
		String businessClient;
		businessClient = customer(CREATE, BUSINESS, ROLEBASED_BANKING, DefaultVariables.clientBusinessCIFFields,
				DefaultVariables.clientBusinessCIFValues);

		if (businessClient.contains(ERROR)) {
			stepActual = "Error while creating Business Client";
			softVerify.fail(stepActual);
			stepResult = StatusAs.FAILED;
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	public void createBusinessNonClient() {

		stepDescription = "Create Business Non-Client";
		stepExpected = "Business Non-Client was created successfully";
		switchToBranch(branch);
		String businessNonClient;
		businessNonClient = customer(CREATE, NON_CLIENT_BUSINESS, ROLEBASED_BANKING,
				DefaultVariables.nonClientBusinessCIFFields, DefaultVariables.nonClientBusinessCIFValues);

		if (businessNonClient.contains(ERROR)) {
			stepActual = "Error while creating Business Non-Client";
			softVerify.fail(stepActual);
			stepResult = StatusAs.FAILED;
		}
		softVerify.assertAll();
	}
}
