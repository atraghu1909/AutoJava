package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;

public class PROC_OP_MG_XX014 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 4.0";
	private ArrangementData mortgageData;
	private String mainArrangement;
	private String originalCustomer;
	private String newCustomer;
	private String extraCustomer;
	private String beneficiary;
	private String newLimit;
	private String reserveAccount;
	private String fields;
	private String values;
	private boolean result = false;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "customerType", "product" })
	public void preCondition(@Optional("B2B Branch 623") final String branch,
			@Optional(PERSONAL) final String customerType, final String product) {

		stepDescription = "Create " + customerType + " customer and " + product + " arrangement. Also, create another "
				+ customerType + " customer and beneficiary";
		stepExpected = customerType + " customers, beneficiary and " + product + " arrangement created successfully";

		if (loginResult) {
			switchToBranch(branch);

			mortgageData = new ArrangementData("mainArrangement", RETAIL_MORTGAGES, product)
					.withCustomers("NEW", null, "", "100", "100")
					.withEffectiveDate("-1m")
					.withTerm("1Y")
					.withReserveAccount("NEW", true)
					.withDisbursement()
					.withRepayments()
					.build();

			mainArrangement = createDefaultArrangement(mortgageData);
			originalCustomer = mortgageData.getCustomers();
			reserveAccount = mortgageData.getReserveAccount();
			newCustomer = createDefaultCustomer(customerType, "", ROLEBASED_LENDING);
			beneficiary = findBeneficiaryCode(newCustomer, customerType, "", ROLEBASED_LENDING);

			if (newCustomer == null || originalCustomer == null || mainArrangement == null || reserveAccount == null
					|| newCustomer.contains(ERROR) || originalCustomer.contains(ERROR)
					|| mainArrangement.contains(ERROR) || reserveAccount.contains(ERROR)) {
				stepActual = "Error while creating a " + customerType + " customer and " + product + " arrangement";
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
	@Parameters("product")
	public void transferLimit(final String product) {

		stepDescription = "Create limit with new borrower information by copying limit information of previous primary owner";
		stepExpected = "Limit with new borrower information by copying limit information of previous primary owner created successfully";

		if (originalCustomer == null || newCustomer == null || originalCustomer.contains(ERROR)
				|| newCustomer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = !customerLimit(OPEN, SECURED, ROLEBASED_LENDING, product, originalCustomer, "", "01", "", "",
					false).contains(ERROR)
					&& toolbarElements.moreActionsDropdown("Copy")
					&& !customerLimit(OPEN, SECURED, ROLEBASED_LENDING, product, newCustomer, "", "01", "", "", false)
							.contains(ERROR)
					&& toolbarElements.moreActionsDropdown("Paste") && inputTable.commitAndOverride();

			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "workarea");
			newLimit = readTable.message().getText().split("\\s+")[2];

			result = !newLimit.contains(ERROR) && authorizeEntity(newLimit, LIMIT);

			if (!result) {
				stepActual = "Error while creating limit with new borrower information by copying limit information of previous primary owner";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters("product")
	public void transferCollateralLink(final String product) {

		stepDescription = "Create Collatarel Link with new borrower information by copying limit information of previous primary owner";
		stepExpected = "Collatarel Link with new borrower information by copying limit information of previous primary owner created successfully";

		fields = "LIMIT.REFERENCE:1," + "LIMIT.REF.CUST:1,";
		values = newLimit + "," + newCustomer + ",";

		if (originalCustomer == null || newCustomer == null || originalCustomer.contains(ERROR)
				|| newCustomer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = !collateral(OPEN, COLLATERAL_LINK, originalCustomer + ".1", "", "").contains(ERROR)
					&& toolbarElements.moreActionsDropdown("Copy")
					&& !collateral(OPEN, COLLATERAL_LINK, newCustomer + ".1", "", "").contains(ERROR)
					&& toolbarElements.moreActionsDropdown("Paste") && multiInputData(fields, values, false)
					&& inputTable.commitAndOverride();

			if (!result) {
				stepActual = "Error while creating Collatarel Link with new borrower information by copying limit information of previous primary owner";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters("product")
	public void transferCollateralGeneral(final String product) {

		stepDescription = "Create Collatarel General with new borrower information by copying limit information of previous primary owner";
		stepExpected = "Collatarel General with new borrower information by copying limit information of previous primary owner created successfully";

		if (originalCustomer == null || newCustomer == null || originalCustomer.contains(ERROR)
				|| newCustomer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = !collateral(OPEN, COLLATERAL_DETAILS, originalCustomer + ".1.1", "", "").contains(ERROR)
					&& toolbarElements.moreActionsDropdown("Copy")
					&& !collateral(OPEN, COLLATERAL_DETAILS, newCustomer + ".1.1", "", "").contains(ERROR)
					&& toolbarElements.moreActionsDropdown("Paste") && inputTable.commitAndOverride();

			if (!result) {
				stepActual = "Error while creating Collatarel General with new borrower information by copying limit information of previous primary owner";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	public void delinkReserveAccount() {

		stepDescription = "Delink reserve account";
		stepExpected = "Reserve account delinked successfully";

		fields = "Reserve Account & Other Details#CA.RSRV.ACCOUNT:1,"
				+ "Reserve Account & Other Details#CA.RSRV.PRIMARY:1,";
		values = " ," + " ,";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementActivity(CREATE, "UPDATE ACTIVITY FOR ACCOUNT", mainArrangement, RETAIL_MORTGAGES,
					fields, values);
			if (!result) {
				stepActual = "Error while delink reserve account";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	public void changeOwnerAssumption() {

		stepDescription = "Change owner by performing assumption Activity";
		stepExpected = "Owner changed successfully by performing assumption activity";

		fields = "CUSTOMER:1";
		values = newCustomer;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			arrangementActivity(OPEN, "Assumption", mainArrangement, RETAIL_MORTGAGES, fields, values);
			multiInputData(fields, values, false);
			toolElements.toolsButton(VALIDATE_DEAL).click();
			switchToPage(LASTPAGE, false);
			fields = "LIMIT.ALLOC.PERC:1," + "GL.ALLOC.PERC:1," + "DELIVERY.REQD:1," + "L.CU.REASON,"
					+ "Insurance#LIFE.INS.FLAG:1," + "Insurance#DISABILITY.FLAG:1,";
			values = "100.00," + "100.00," + "YES," + "Assumption," + "None," + "None,";
			multiInputData(fields, values, false);
			result = inputTable.commitAndOverride();

			if (!result) {
				stepActual = "Error while performing assumption activity for owner change";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			fields = "Basic#PAYIN.BENEFICIARY:1:1";
			values = beneficiary;
			result = arrangementActivity(CREATE, "UPDATE ACTIVITY FOR SETTLEMENT", mainArrangement, RETAIL_MORTGAGES,
					fields, values);
			if (!result) {
				stepActual = "Error while updating new beneficiary to arrangement";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 7, enabled = true)
	public void reserveAccountUpdate() {

		stepDescription = "Change owner for reserveAccount";
		stepExpected = "Owner changed successfully for reserveAccount";

		fields = "CUSTOMER:1";
		values = newCustomer;

		if (reserveAccount == null || reserveAccount.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementActivity(OPEN, "CHANGE ACTIVITY FOR CUSTOMER", reserveAccount, SERVICING_ACCOUNTS,
					fields, values);
			multiInputData(fields, values, false);
			toolElements.toolsButton(VALIDATE_DEAL).click();
			switchToPage(LASTPAGE, false);
			fields = "LIMIT.ALLOC.PERC:1," + "GL.ALLOC.PERC:1,";
			values = "100.00," + "100.00,";
			multiInputData(fields, values, false);
			result = inputTable.commitAndOverride();

			if (!result) {
				stepActual = "Error while changing owner for reserveAccount";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 8, enabled = true)
	@Parameters("product")
	public void disableOldLimit(final String product) {

		stepDescription = "Disable Old Limit for customer " + originalCustomer;
		stepExpected = "Old Limit for customer " + originalCustomer + " disabled successfully";

		fields = "REVIEW.FREQUENCY," + "EXPIRY.DATE,";
		values = "DAILY," + "+0d,";
		String oldLimit;

		if (originalCustomer == null || originalCustomer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			oldLimit = customerLimit(AMEND_AUTHORISE, SECURED, ROLEBASED_LENDING, product, originalCustomer, "", "01", fields,
					values, false);

			if (oldLimit.contains(ERROR)) {
				stepActual = "Error while disabling Old Limit for customer " + originalCustomer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 9, enabled = true)
	public void relinkReserveAccount() {

		stepDescription = "Relink reserve account with updated owner";
		stepExpected = "Reserve account with updated owner relinked successfully";

		fields = "Reserve Account & Other Details#CA.RSRV.ACCOUNT:1,"
				+ "Reserve Account & Other Details#CA.RSRV.PRIMARY:1,";
		values = reserveAccount + "," + "Yes,";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementActivity(CREATE, "UPDATE ACTIVITY FOR ACCOUNT", mainArrangement, RETAIL_MORTGAGES,
					fields, values);
			if (!result) {
				stepActual = "Error while relink reserve account with updated owner";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 10, enabled = true)
	public void delinkReserveAccountToAddNewOwner() {

		stepDescription = "Delink reserve account to add new owner";
		stepExpected = "Reserve account successfully delinked to add new owner";

		fields = "Reserve Account & Other Details#CA.RSRV.ACCOUNT:1,"
				+ "Reserve Account & Other Details#CA.RSRV.PRIMARY:1,";
		values = " ," + " ,";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementActivity(CREATE, "UPDATE ACTIVITY FOR ACCOUNT", mainArrangement, RETAIL_MORTGAGES,
					fields, values);
			if (!result) {
				stepActual = "Error while delink reserve account to add new owner";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 11, enabled = true)
	@Parameters({ "product", "customerType" })
	public void addBorrower(final String product, @Optional(PERSONAL) final String customerType) {

		stepDescription = "Add Borrower to arrangement " + mainArrangement + " and reserveAccount " + reserveAccount;
		stepExpected = "Borrower to arrangement " + mainArrangement + " and reserve account " + reserveAccount
				+ " added successfully";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			extraCustomer = createDefaultCustomer(customerType, "", ROLEBASED_LENDING);

			fields = "CUSTOMER+:2," + "CUSTOMER.ROLE:2,";
			values = extraCustomer + "," + "COBORROWER,";

			arrangementActivity(OPEN, "Assumption", mainArrangement, RETAIL_MORTGAGES, fields, values);
			multiInputData(fields, values, false);
			toolElements.toolsButton(VALIDATE_DEAL).click();
			switchToPage(LASTPAGE, false);
			fields = "LIMIT.ALLOC.PERC:1," + "GL.ALLOC.PERC:1," + "TAX.LIABILITY.PERC:2," + "LIMIT.ALLOC.PERC:2,"
					+ "GL.ALLOC.PERC:2," + "DELIVERY.REQD:1," + "L.CU.REASON," + "Insurance#LIFE.INS.FLAG:1,"
					+ "Insurance#DISABILITY.FLAG:1," + "Insurance#LIFE.INS.FLAG:2," + "Insurance#DISABILITY.FLAG:2,";
			values = "100.00," + "100.00," + "0," + "0," + "0," + "YES," + "Assumption," + "None," + "None," + "None,"
					+ "None,";
			multiInputData(fields, values, false);
			result = inputTable.commitAndOverride();

			if (!result) {
				stepActual = "Error while adding borrower to arrangement " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			fields = "CUSTOMER+:2," + "CUSTOMER.ROLE:2,";
			values = extraCustomer + "," + "COBORROWER,";

			result = arrangementActivity(OPEN, "CHANGE ACTIVITY FOR CUSTOMER", reserveAccount, SERVICING_ACCOUNTS,
					fields, values);
			multiInputData(fields, values, false);
			toolElements.toolsButton(VALIDATE_DEAL).click();
			switchToPage(LASTPAGE, false);
			fields = "LIMIT.ALLOC.PERC:1," + "GL.ALLOC.PERC:1," + "TAX.LIABILITY.PERC:2," + "LIMIT.ALLOC.PERC:2,"
					+ "GL.ALLOC.PERC:2,";
			values = "100.00," + "100.00," + "0," + "0," + "0,";
			multiInputData(fields, values, false);
			result = inputTable.commitAndOverride();

			if (!result) {
				stepActual = "Error while adding borrower to reserve account " + reserveAccount;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 12, enabled = true)
	public void relinkReserveAccountForAddedOwner() {

		stepDescription = "Relink reserve account after adding new owner";
		stepExpected = "Reserve account relinked successfully after adding new owner";

		fields = "Reserve Account & Other Details#CA.RSRV.ACCOUNT:1,"
				+ "Reserve Account & Other Details#CA.RSRV.PRIMARY:1,";
		values = reserveAccount + "," + "Yes,";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementActivity(CREATE, "UPDATE ACTIVITY FOR ACCOUNT", mainArrangement, RETAIL_MORTGAGES,
					fields, values);
			if (!result) {
				stepActual = "Error while relink reserve account after adding new owner";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 13, enabled = true)
	public void updateCollateralDetails() {

		stepDescription = "Update collateral details for arrangement " + mainArrangement;
		stepExpected = "Collateral details for arrangement " + mainArrangement + " updated successfully";

		fields = "Registration details#L.COLL.REG.NO+:2," + "Registration details#L.COLL.REG.DATE:2,"
				+ "Registration details#L.COLL.REG.RSN:2,";
		values = "CV124482645," + "+0d," + "Amendment,";
		String amendedCollateral;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			amendedCollateral = collateral(AMEND, COLLATERAL_DETAILS, newCustomer + ".1.1", fields, values);

			if (amendedCollateral.contains(ERROR)) {
				stepActual = "Error while update collateral details for arrangement " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 14, enabled = true)
	@Parameters("product")
	public void statementEnquiry(final String product) {

		stepDescription = "Validate statement enquiry";
		stepExpected = "Statement enquiry validated successfully";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			commandLine("ENQ LBC.ASSUMED.LOANS", commandLineAvailable);
			result = enquiryElements.enquirySearch("Arrangement Id,Customer Id", "equals,equals",
					mainArrangement + "," + originalCustomer);

			if (!result) {
				stepActual = "Error while validating statement enquiry";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

}