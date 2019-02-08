package testcases.PROC;

import org.openqa.selenium.NoSuchElementException;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PROC_OP_ES_PR003 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 1.3";
	private String mainArrangement = "";
	private String customer;
	private String customerType;
	private String branch = "B2B Branch 817";
	private String step1Fields = "";
	private String step1Values = "";
	private String defaultStep2Fields = "";
	private String defaultStep2Values = "";
	private final StringBuilder step1FieldsBuilder = new StringBuilder(step1Fields);
	private final StringBuilder step1ValuesBuilder = new StringBuilder(step1Values);

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void preCondition(final String productGroup, final String product) {

		stepDescription = "Create " + product + " Arrangement";
		stepExpected = product + " Arrangement created successfully";

		String fields;
		String values;
		switchToBranch(branch);

		customerType = DefaultVariables.productGroupCustomerType.get(productGroup);

		if (DefaultVariables.productFields.containsKey(product)) {
			defaultStep2Fields = DefaultVariables.productFields.get(product);
			defaultStep2Values = DefaultVariables.productValues.get(product);
		} else {
			defaultStep2Fields = DefaultVariables.productGroupFields.get(productGroup);
			defaultStep2Values = DefaultVariables.productGroupValues.get(productGroup);
		}

		customer = createDefaultCustomer(customerType, productGroup, ROLEBASED_LENDING);

		step1FieldsBuilder.append("EFFECTIVE.DATE,");
		step1Fields = step1FieldsBuilder.toString();
		step1ValuesBuilder.append("-1m,");
		step1Values = step1ValuesBuilder.toString();

		mainArrangement = dataGenCreateLoan(customer, productGroup, product, CAD, step1Fields, step1Values,
				defaultStep2Fields, defaultStep2Values, "Current", "-1m");

		fields = "Registration details#L.COLL.ADM.CODE," + "Registration details#L.COLL.EXT.NO,";
		values = "B2b Investment Loan," + "123456,";

		collateral(AMEND_AUTHORISE, COLLATERAL_DETAILS_LOANS, customer + ".1.1", fields, values);

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepActual = "Error while creating " + product + " Arrangement: " + mainArrangement;
			softVerify.fail(stepActual);
			stepResult = StatusAs.FAILED;
		}
		softVerify.assertAll();
	}

	@Test(priority = 2, enabled = true)
	@Parameters({ "productGroup" })
	public void amendDeceasedCustomer(final String productGroup) {

		stepDescription = "Amend Deceased Client's details " + customer;
		stepExpected = "Deceased Client's details " + customer + " added successfully";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			String fields;
			String values;
			String amendClient;

			fields = "DATE.OF.DEATH," + "CUSTOMER.STATUS,";
			values = "+0d," + "5,";
			amendClient = amendCIF(AMEND, customer, customerType, ROLEBASED_LENDING, fields, values);

			fields = "POSTING.RESTRICT:1";
			values = "12";
			postingRestrict(CREATE, "Menu", customer, PERSONAL, productGroup, fields, values);
			if (amendClient.contains(ERROR)) {
				stepActual = "Error while adding deceased client's details: " + customer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void identifyPledgeAccountInfo(final String productGroup, final String product) {

		stepDescription = "Identify Pledge Account information from arrangement: " + mainArrangement;
		stepExpected = "Pledge Account information is identified from arrangement: " + mainArrangement;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			String status;
			String administrationCode;

			try {
				findArrangement(AUTHORISED, mainArrangement, ARRANGEMENT, ROLEBASED_LENDING, productGroup, "", "");
				status = versionScreen.statusElement("Status").getText();
				if (status.equals("Current")) {
					commandLine("COLLATERAL,LBC.B2BPPSA", commandLineAvailable);
					enquiryElements.transactionIdField(COLLATERAL_DETAILS).click();
					enquiryElements.transactionIdField(COLLATERAL_DETAILS).sendKeys(customer + ".1.1");
					toolElements.toolsButton(VIEW_CONTRACT).click();
					tabbedScreen.findTab("Registration details", "").click();
					administrationCode = readTable.verifyActivity("Administration Code").getText();
					String externalAccount = readTable.verifyActivity("External Acc Number").getText();
					if (!administrationCode.equalsIgnoreCase("B2b Investment Loan")
							|| !externalAccount.equalsIgnoreCase("123456")) {
						stepActual = "Error while identifying Pledge Account information from arrangement: "
								+ mainArrangement;
						softVerify.fail(stepActual);
						stepResult = StatusAs.FAILED;
					}
				} else {
					stepActual = "loan arrangement: " + mainArrangement + " is not in good standing";
				}
			} catch (NoSuchElementException e) {
				stepActual = "Error investigating arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

		}
		softVerify.assertAll();
	}
}
