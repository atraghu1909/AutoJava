package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PROC_BB_REB_OP006 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 4.0";
	private String mainArrangement = "";
	private String mainCustomer;
	private String personalCustomer;
	private String businessCustomer;
	private String branch = "B2B Branch 817";
	private String defaultStep2Fields = "";
	private String defaultStep2Values = "";

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void preCondition(final String productGroup, final String product) {

		stepDescription = "Create " + product + " Arrangement, Non-Client personal and business customers";
		stepExpected = product + " Arrangement, Non-Client personal and business customers created successfully";

		String customerType;

		switchToBranch(branch);

		customerType = DefaultVariables.productGroupCustomerType.get(productGroup);

		if (DefaultVariables.productFields.containsKey(product)) {
			defaultStep2Fields = DefaultVariables.productFields.get(product);
			defaultStep2Values = DefaultVariables.productValues.get(product);
		} else {
			defaultStep2Fields = DefaultVariables.productGroupFields.get(productGroup);
			defaultStep2Values = DefaultVariables.productGroupValues.get(productGroup);
		}

		mainCustomer = createDefaultCustomer(customerType, productGroup, ROLEBASED_BANKING);
		mainArrangement = createDefaultArrangement(productGroup, product, "", mainCustomer, "+0d");
		
		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepActual = "Error while creating " + product + " Arrangement: " + mainArrangement;
			softVerify.fail(stepActual);
			stepResult = StatusAs.FAILED;
		}

		personalCustomer = createDefaultCustomer(NON_CLIENT_PERSONAL, "", ROLEBASED_BANKING);
		if (personalCustomer.contains(ERROR)) {
			stepActual = "Error while creating Non-Client personal customer: " + personalCustomer;
			softVerify.fail(stepActual);
			stepResult = StatusAs.FAILED;
		}

		businessCustomer = createDefaultCustomer(NON_CLIENT_BUSINESS, "", ROLEBASED_BANKING);
		if (businessCustomer.contains(ERROR)) {
			stepActual = "Error while creating Non-Client business customer: " + businessCustomer;
			softVerify.fail(stepActual);
			stepResult = StatusAs.FAILED;
		}
		softVerify.assertAll();
	}

	@Test(priority = 2, enabled = true)
	@Parameters({ "productGroup" })
	public void linkNonClientToArrangement(final String productGroup) {

		stepDescription = "Link Non-Client to Arrangement: " + mainArrangement;
		stepExpected = "Non-Client was linked successfully to arrangement: " + mainArrangement;

		if (mainArrangement == null || personalCustomer == null || mainArrangement.contains(ERROR)
				|| personalCustomer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			boolean result;
			String fields = "CUSTOMER+:2," + "CUSTOMER.ROLE:1," + "CUSTOMER.ROLE:2,";
			String values = personalCustomer + "," + "OWNER," + "POA,";

			result = arrangementActivity(CREATE_AUTHORISE, "CHANGE ACTIVITY FOR CUSTOMER", mainArrangement,
					productGroup, fields, values);

			if (!result) {
				stepActual = "Error while linking Non-Client to Arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "productGroup" })
	public void deLinkNonClientToArrangement(final String productGroup) {
		stepDescription = "De-Link Non-Client from Arrangement: " + mainArrangement;
		stepExpected = "Non-Client was De-Linked successfully from arrangement: " + mainArrangement;

		if (mainArrangement == null || personalCustomer == null || mainArrangement.contains(ERROR)
				|| personalCustomer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			boolean result;
			String fields = "CUSTOMER<:2,";
			String values = personalCustomer + ",";

			result = arrangementActivity(CREATE_AUTHORISE, "CHANGE ACTIVITY FOR CUSTOMER", mainArrangement,
					productGroup, fields, values);

			if (!result) {
				stepActual = "Error while De-Linking Non-Client from Arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	public void convertPersonalNonClient() {

		stepDescription = "Convert existing Personal Non-Client: " + personalCustomer + " to a client";
		stepExpected = "Personal Non-Client: " + personalCustomer + " to a client conversion performed successfully";

		if (personalCustomer == null || personalCustomer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			String message;
			String fields = "CAPL.CUS.TYPE,";
			String values = "10,";

			message = amendCIF(AMEND, personalCustomer, NON_CLIENT_PERSONAL, ROLEBASED_BANKING, fields, values);

			if (message.contains(ERROR)) {
				stepActual = "Error while converting Personal Non-Client: " + personalCustomer + " to a client";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	public void convertBusinessNonClient() {

		stepDescription = "Convert existing Business Non-Client: " + businessCustomer + " to a client";
		stepExpected = "Business Non-Client: " + businessCustomer + " to a client conversion performed successfully";

		if (businessCustomer == null || businessCustomer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			String message;
			String fields = "CAPL.CUS.TYPE," + "SECTOR," + "INDUSTRY,";
			String values = "11," + "2004," + "2010,";

			message = amendCIF(AMEND, businessCustomer, NON_CLIENT_BUSINESS, ROLEBASED_BANKING, fields, values);

			if (message.contains(ERROR)) {
				stepActual = "Error while converting Business Non-Client: " + businessCustomer + " to a client";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
