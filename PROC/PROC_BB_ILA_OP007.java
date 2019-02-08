package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PROC_BB_ILA_OP007 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.6";
	private String mainArrangement;
	private String customer;
	private String poa;
	private String branch = "B2B Branch 817";

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void preCondition(final String productGroup, final String product) {

		stepDescription = "Create a Client and a Power of Attorney CIF";
		stepExpected = "Client and Power of Attorney CIFs are created successfullyr";

		if (loginResult) {
			switchToBranch(branch);

			customer = customer(CREATE, PERSONAL, ROLEBASED_LENDING, DefaultVariables.personalCIFFields,
					DefaultVariables.personalCIFValues);
			poa = customer(CREATE, NON_CLIENT_PERSONAL, ROLEBASED_LENDING, DefaultVariables.personalCIFFields,
					DefaultVariables.personalCIFValues);
			if (customer == null || poa == null || customer.contains(ERROR) || poa.contains(ERROR)) {
				stepActual = "Error while creating CIFs";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {
				mainArrangement = findArrangement(AUTHORISED, customer, CIF, ROLEBASED_LENDING, productGroup, product,
						CAD);
				if (mainArrangement == null || mainArrangement.contains(ERROR)) {
					stepActual = "Error while creating " + product + " arrangement";
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

		stepDescription = "Update Basic Details for customer: " + customer;
		stepExpected = "Basic Details for customer: " + customer + " updated successfully";

		String message;
		final String fields = "GIVEN.NAMES," + "MARITAL.STATUS," + "SIN.NO," + "RESIDENCE,"
				+ "Communication Details#PHONE.1:1,";
		final String values = "bruce," + "Married," + "932123458," + "CA," + "4162032222,";

		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			message = amendCIF(AMEND, customer, "", ROLEBASED_LENDING, fields, values);

			if (message.contains(ERROR)) {
				stepActual = "Error while updating Basic Details for customer: " + customer;
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
		final String fields = "RESIDENCE," + "RESIDENCE.REGION," + "Address#ADDRESS:1:1," + "Address#STREET:1,"
				+ "Address#TOWN.COUNTRY:1," + "Address#CITY," + "Address#ADDR.CNTRY.ID," + "Address#US.STATE,"
				+ "Address#POST.CODE:1,";
		final String values = "CA," + "CA02," + "55," + "King Street," + "unit 10," + "Toronto," + "CA," + "ON,"
				+ "M9B 8S7,";

		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			message = amendCIF(AMEND, customer, "", ROLEBASED_LENDING, fields, values);

			if (message.contains(ERROR)) {
				stepActual = "Error while updating Address for customer: " + customer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void addNewAddress(final String productGroup, final String product) {

		stepDescription = "Add New Address for customer: " + customer;
		stepExpected = "New Address for customer: " + customer + " added successfully";

		boolean result;
		final String fields = "SHORT.NAME:1," + "NAME.1:1," + "ADDRESS," + "CITY," + "ADDR.CNTRY.ID,"
				+ "POST.CODE:1,";
		final String values = "WB," + "Wayn Bruce," + "King Street," + "Toronto," + "CA," + "M4V8T6,";

		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = !addNewCustomerAddress(PRINT, customer, fields, values).contains(ERROR);

			if (!result) {
				stepActual = "Error while adding New Address for customer: " + customer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void linkPOA(final String productGroup, final String product) {

		stepDescription = "Link POA " + poa + " with customer " + customer + " on arrangement " + mainArrangement;
		stepExpected = "POA " + poa + " added to arrangement " + mainArrangement + " of customer " + customer;

		boolean result;
		final String fields = "CUSTOMER+:2," + "CUSTOMER.ROLE:2,";
		final String values = poa + "," + "POA,";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementActivity(CREATE_AUTHORISE, "CHANGE ACTIVITY FOR CUSTOMER", mainArrangement,
					productGroup, fields, values);
			if (!result) {
				stepActual = "Error while linking POA: " + poa + " with customer: " + customer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	public void modifyPOA() {

		stepDescription = "Modify POA: " + poa;
		stepExpected = "POA: " + poa + " modified successfully";

		String message;
		final String fields = "GIVEN.NAMES," + "FAMILY.NAME," + "DATE.OF.BIRTH," + "Address#ADDRESS:1:1,"
				+ "Address#STREET:1," + "Address#TOWN.COUNTRY:1," + "Address#CITY," + "Address#ADDR.CNTRY.ID,"
				+ "Address#US.STATE," + "Address#POST.CODE:1," + "Financial Details#EMPLOYMENT.STATUS:1,"
				+ "Financial Details#L.INDUSTRY.SECT," + "Financial Details#JOB.TITLE:1,"
				+ "Financial Details#OCCUPATION:1," + "Financial Details#EMPLOYERS.NAME:1,";
		final String values = "Batman," + "Wayn Bruce," + "29 NOV 1975," + "1," + "Gotham street," + "unit 1,"
				+ "Gotham," + "CA," + "ON," + "M9B 3P5," + "Aut - Self-employed," + "Urg - Emergency And Protection,"
				+ "447," + "Super Hero," + "Wayn Enterprice,";

		if (poa == null || poa.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			message = amendCIF(AMEND, poa, NON_CLIENT_PERSONAL, ROLEBASED_LENDING, fields, values);

			if (message.contains(ERROR)) {
				stepActual = "Error while modifying POA: " + poa;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 7, enabled = true)
	@Parameters({ "productGroup" })
	public void removePOA(final String productGroup) {

		stepDescription = "Remove POA " + poa + " from arrangement " + mainArrangement + " of customer " + customer;
		stepExpected = "POA " + poa + " removed from arrangement " + mainArrangement + " of customer " + customer;

		boolean result;
		final String fields = "CUSTOMER<:2,";
		final String values = "No,";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementActivity(CREATE_AUTHORISE, "UPDATE ACTIVITY FOR CUSTOMER", mainArrangement,
					productGroup, fields, values);
			if (!result) {
				stepActual = "Error while removing POA: " + poa + " from customer: " + customer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 8, enabled = true)
	@Parameters({ "productGroup" })
	public void addNotes(final String productGroup) {

		stepDescription = "Add Notes for arrangement: " + mainArrangement;
		stepExpected = "Notes for arrangement: " + mainArrangement + " added successfully";

		String fields = "MEMO.TEXT,";
		String values = "Test DTA or LTA information,";
		boolean result;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementMemo(CREATE, mainArrangement, productGroup, fields, values);

			if (!result) {
				stepActual = "Error while adding Notes for arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 9, enabled = true)
	@Parameters({ "productGroup" })
	public void postRestrictions(final String productGroup) {

		stepDescription = "Change Post Restrictions for arrangement: " + mainArrangement;
		stepExpected = "Post Restrictions for arrangement: " + mainArrangement + " changed successfully";

		boolean result;
		final String fields = "Posting Restriction#POSTING.RESTRICT:1,";
		final String values = "1,";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementActivity(CREATE_AUTHORISE, "UPDATE ACTIVITY FOR ACCOUNT", mainArrangement, productGroup,
					fields, values);
			if (!result) {
				stepActual = "Error while changing Post Restrictions for arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
