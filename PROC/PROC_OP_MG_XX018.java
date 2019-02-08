package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.CustomerData;
import testLibs.DefaultVariables;

public class PROC_OP_MG_XX018 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 4.0";
	private ArrangementData mortgageData;
	private ArrangementData helocData;
	private String mainArrangement;
	private String heloc;
	private String customer;
	private String beneficiary;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "customerType", "product" })
	public void preCondition(@Optional("B2B Branch 623") final String branch,
			@Optional(PERSONAL) final String customerType, final String product) {

		stepDescription = "Create two " + customerType + " customers, one mortgage and one HELOC arrangement";
		stepExpected = customerType + " customers, one mortgage and one HELOC arrangement created successfully";

		if (loginResult) {
			switchToBranch(branch);

			mortgageData = new ArrangementData("mainArrangement", RETAIL_MORTGAGES, product)
					.withCustomers("NEW,NEW", null, "", "100,0", "100,0").withEffectiveDate("-1m").withTerm("1Y")
					.withCollateralValue("700000").withCommitmentAmount("350000").withEscrow(MUNICIPALITY)
					.withSettlement("Banking", "NEW").build();

			mainArrangement = createDefaultArrangement(mortgageData);
			customer = mortgageData.getCustomers();

			if (customer == null || mainArrangement == null || customer.contains(ERROR)
					|| mainArrangement.contains(ERROR)) {
				stepActual = "Error while creating two " + customerType + " customers and " + product + " arrangement";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			helocData = new ArrangementData("EFTArrangement", RETAIL_MORTGAGES, HELOC)
					.withCustomers(customer.split(",")[0], createdCustomers.get(customer.split(",")[0]), "", "100,",
							"100,")
					.withEffectiveDate("-1m").withTerm("1Y").withCommitmentAmount("350000").build();

			heloc = createDefaultArrangement(helocData);

			if (heloc == null && heloc.contains(ERROR)) {
				stepActual = "Error while creating HELOC arrangement";
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
	@Parameters("customerType")
	public void createBeneficiary(@Optional(PERSONAL) final String customerType) {

		stepDescription = "Create beneficiary for customer: " + customer;
		stepExpected = "Beneficiary for customer: " + customer + " created successfully";

		CustomerData mainCustomerData = createdCustomers.get(customer.split(",")[0]);
		final String fields = DefaultVariables.beneficiaryFields + "OWNING.CUSTOMER," + "BEN.CUSTOMER,";
		final String values = DefaultVariables.beneficiaryValues + customer.split(",")[0] + ","
				+ mainCustomerData.getCustomerName() + ",";

		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			beneficiary = beneficiaryCode(CREATE, "EFT Client", "", fields, values);

			if (beneficiary.contains(ERROR)) {
				stepActual = "Error while performing payment by cheque";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	public void updateClientInformation() {

		stepDescription = "Update client information";
		stepExpected = "Client information updated successfully";

		final String fields = "Address#ADDRESS:1:1," + "Address#STREET:1," + "Address#TOWN.COUNTRY:1," + "Address#CITY,"
				+ "Address#ADDR.CNTRY.ID," + "Address#US.STATE," + "Address#POST.CODE:1," + "ID#CAMB.ID.TYPE:1,"
				+ "ID#CAMB.ID.UNQ.ID:1," + "ID#ID.CTRY.ISS:1," + "ID#CAMB.ID.PL.ISS:1,"
				+ "Financial Details#EMPLOYMENT.STATUS:1,";
		final String values = "123," + "Bridle Path," + "Avenue," + "Toronto," + "CA," + "ON," + "M3C 1G5," + "CAPR,"
				+ "1233465456," + "CA," + "TORONTO," + "Njt - Never Employed,";
		String customerID;

		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			customerID = amendCIF(AMEND, customer.split(",")[0], "", ROLEBASED_LENDING, fields, values);

			if (customerID.contains(ERROR)) {
				stepActual = "Error while updating client information";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	public void mortgageDisbursementAndWireRelease() {

		stepDescription = "Perform mortgage disbursement and wire release";
		stepExpected = "Mortgage disbursement and wire release performed successfully";

		final String fields = "PRIMARY.ACCOUNT," + "TRANSACTION:1," + "SURROGATE.AC:1," + "CURRENCY:1," + "AMOUNT:1,"
				+ "CR.VALUE.DATE:1," + "TRANSACTION:2," + "SURROGATE.AC:2," + "CURRENCY:2," + "AMOUNT:2,"
				+ "CR.VALUE.DATE:2," + "TRANSACTION:3," + "SURROGATE.AC:3," + "CURRENCY:3," + "AMOUNT:3,"
				+ "CR.VALUE.DATE:3,";
		final String values = "CAD1100300017623," + "Disburse-ft," + mainArrangement + "," + "CAD," + "350T," + "+0d,"
				+ "Credit," + "CAD1530400017623," + "CAD," + "8500.00," + "+0d," + "Escrowdeposit," + mainArrangement
				+ "," + "CAD," + "1250.00," + "+0d,";
		String transactionID;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			transactionID = financialTransaction(CREATE_AUTHORISE, "Teller Financial Services", fields, values);

			if (transactionID.contains(ERROR)) {
				stepActual = "Error while performing mortgage disbursement and wire release";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	public void mortgageAccountUpdate() {

		stepDescription = "Update mortgage account";
		stepExpected = "Mortgage account updated successfully";

		final String fields = "DELIVERY.REQD:1," + "DELIVERY.REQD:2,";
		final String values = "YES," + "YES,";
		boolean result;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementActivity(CREATE_AUTHORISE, "UPDATE ACTIVITY FOR CUSTOMER", mainArrangement,
					RETAIL_MORTGAGES, fields, values);

			if (!result) {
				stepActual = "Error while updating mortgage account";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	public void HELOCDisbursement() {

		stepDescription = "Disburse HELOC arrangement";
		stepExpected = "HELOC arrangement disbursed successfully";

		final String fields = "PRIMARY.ACCOUNT," + "TRANSACTION:1," + "SURROGATE.AC:1," + "CURRENCY:1," + "AMOUNT:1,"
				+ "CR.VALUE.DATE:1,";
		final String values = "CAD1100300017623," + "Disburse-ft," + heloc + "," + "CAD," + "10T," + "+0d,";
		String transactionID;

		if (heloc == null || heloc.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			transactionID = financialTransaction(CREATE_AUTHORISE, "Teller Financial Services", fields, values);

			if (transactionID.contains(ERROR)) {
				stepActual = "Error while disbursing HELOC arrangement";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 7, enabled = true)
	@Parameters("customerType")
	public void createHELOCStandingOrder(@Optional(PERSONAL) final String customerType) {

		stepDescription = "Create HELOC standing order";
		stepExpected = "HELOC standing order created successfully";

		final String fields = "CURRENT.AMOUNT.BAL," + "CURRENT.FREQUENCY," + "BENEFICIARY.ID,";
		final String values = "200.00," + "e0Y e1M e0W o27D e0F," + beneficiary + ",";
		boolean result;

		if (heloc == null || heloc.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = reoccurringFixedTransfer(OPEN, "Fixed", customer.split(",")[0], heloc, fields, values);
			inputTable.selectionCriteriaButton("CURRENCY").click();
			switchToPage(LASTPAGE, false);
			enquiryElements.enquirySearch("@ID", "", CAD);
			enquiryElements.linkToSelect("CURRENCY").click();
			switchToPage(LASTPAGE, false);
			result = multiInputData(fields, values, false) && inputTable.commitAndOverride();

			if (!result) {
				stepActual = "Error while creating HELOC standing order";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}