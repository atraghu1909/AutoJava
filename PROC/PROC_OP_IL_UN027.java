package testcases.PROC;

import java.util.Random;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PROC_OP_IL_UN027 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.5";
	private String mainArrangement;
	private String customer;
	private String limit;
	private String dealer;
	private String advisor;
	private String advisorRep;
	private String dealerRep;
	private String dealerArrangement;
	private String advisorArrangement;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch ", "customerType", "product", "productGroup" })
	public void preCondition(@Optional("B2B Branch 817") final String branch,
			@Optional(PERSONAL) final String customerType, final String product,
			@Optional(PERSONAL_LOANS) final String productGroup) {

		stepDescription = "Create " + customerType + " customer";
		stepExpected = customerType + " customer created successfully";

		if (loginResult) {

			switchToBranch(branch);

			customer = createDefaultCustomer(customerType, productGroup, ROLEBASED_LENDING);

			if (customer == null || customer.contains(ERROR)) {
				stepActual = "Error while creating " + customerType + " customer";
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
	public void createLimitAndCollateral(final String product) {

		stepDescription = "Create Limit for customer: " + customer;
		stepExpected = "Limit for customer: " + customer + " created successfully";

		String limitType;
		String limitFields;
		String limitValues;

		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			limitType = DefaultVariables.productLimitType.get(product);
			limitFields = DefaultVariables.productLimitFields.get(product);
			limitValues = DefaultVariables.productLimitValues.get(product);

			limit = customerLimit(CREATE, limitType, ROLEBASED_LENDING, product, customer, "", "", limitFields,
					limitValues);

			if (limit == null || limit.contains(ERROR)) {
				stepActual = "Error while creating Limit for customer: " + customer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void createLoan(@Optional(PERSONAL_LOANS) final String productGroup, final String product) {

		stepDescription = "Create " + product + " Loan for customer: " + customer;
		stepExpected = product + " Loan  for customer: " + customer + " created successfully";

		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			mainArrangement = createDefaultArrangement(productGroup, product, ROLEBASED_LENDING, customer, "+0d");

			if (mainArrangement == null || mainArrangement.contains(ERROR)) {
				stepActual = "Error while creating " + product + " Loan for customer: " + customer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters("productGroup")
	public void createDealerAndAdvisor(@Optional(PERSONAL_LOANS) final String productGroup) {

		stepDescription = "Create Dealer and Advisor";
		stepExpected = "Dealer and Advisor created successfully";

		String fields;
		String values;
		boolean result;

		if (loginResult) {
			dealer = customer(OPEN, DEALER_ADVISOR, ROLEBASED_OR, DefaultVariables.dealerAdvisorCIFFields,
					DefaultVariables.dealerAdvisorCIFValues);
			dealerRep = readData("L.DEALER.REP.NO");

			toolElements.toolsButton(COMMIT_DEAL).click();

			if (inputTable.verifyAcceptOverride() && !readTable.message().getText().contains(TXN_COMPLETE)) {
				dealer = "Error: Problem creating CIF";
			}

			if (dealer == null || dealer.contains(ERROR)) {
				stepActual = "Error while creating dealer";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			dealerArrangement = arrangements(CREATE, AGENTS, "Dummy Commission Plan", "", dealer,
					"CUSTOMER:1," + "CURRENCY,", dealer + "," + CAD + ",", "", "");

			if (dealerArrangement == null || dealerArrangement.contains(ERROR)) {
				stepActual = "Error while Creating Dummy Commission Plan for Dealer: " + dealer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			advisor = customer(OPEN, DEALER_ADVISOR, ROLEBASED_OR, DefaultVariables.dealerAdvisorCIFFields,
					DefaultVariables.dealerAdvisorCIFValues);
			advisorRep = readData("L.DEALER.REP.NO");
			fields = "SHORT.NAME:1," + "AGENCY," + "L.DEALER.REP.NO,";
			values = dealerRep + "-" + advisorRep + "," + dealer + "," + dealerRep + "-" + advisorRep + ",";

			multiInputData(fields, values, false);
			toolElements.toolsButton(COMMIT_DEAL).click();

			if (inputTable.verifyAcceptOverride() && !readTable.message().getText().contains(TXN_COMPLETE)) {
				advisor = "Error: Problem creating CIF";
			}

			if (advisor == null || advisor.contains(ERROR)) {
				stepActual = "Error while creating Advisor";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			advisorArrangement = arrangements(CREATE, AGENTS, "Dummy Commission Plan", "", advisor,
					"CUSTOMER:1," + "CURRENCY,", advisor + "," + CAD + ",", "", "");

			if (advisorArrangement == null || advisorArrangement.contains(ERROR)) {
				stepActual = "Error while Creating Dummy Commission Plan for Advisor: " + advisor;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			fields = "AGENT.ID:1," + "AGENT.ARR.ID:1," + "AGENT.ROLE:1," + "AGENT.ID+:2," + "AGENT.ARR.ID:2,"
					+ "AGENT.ROLE:2,";
			values = dealer + "," + dealerArrangement + "," + "Agency," + advisor + "," + advisorArrangement + ","
					+ "Agent,";
			result = arrangementActivity(CREATE, "UPDATE ACTIVITY FOR AL.COMMISSION", mainArrangement, productGroup,
					fields, values);
			if (!result) {
				stepActual = "Error while performing UPDATE ACTIVITY FOR AL.COMISSION activity for arrangement: "
						+ mainArrangement;
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

	@Test(priority = 5, enabled = true)
	@Parameters("customerType")
	public void disburseLoan(@Optional(PERSONAL) final String customerType) {

		stepDescription = "Disburse Loan: " + mainArrangement;
		stepExpected = "Loan: " + mainArrangement + " disbursed successfully";

		String fields;
		String values;
		boolean result;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			fields = "DEBIT.CURRENCY," + "DEBIT.AMOUNT," + "CREDIT.ACCT.NO," + "CREDIT.THEIR.REF,";
			values = CAD + "," + "95T," + "CAD1100000017817," + "987654321,";

			result = arrangementAction(mainArrangement, customer, ROLEBASED_LENDING, "Lending Disbursement", fields,
					values, customerType);

			if (!result) {
				stepActual = "Error while disbursing Loan: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	@Parameters("customerType")
	public void otherFinancialTransactionAtFunding(@Optional(PERSONAL) final String customerType) {

		stepDescription = "Perform Other Financial Transaction at Funding for Loan: " + mainArrangement;
		stepExpected = "Other Financial Transaction at Funding for Loan: " + mainArrangement
				+ " performed successfully";

		String fields;
		String values;
		boolean result;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			fields = "DEBIT.CURRENCY," + "DEBIT.AMOUNT," + "CREDIT.ACCT.NO," + "CREDIT.THEIR.REF," + "DEBIT.ACCT.NO,";
			values = CAD + "," + "5T," + "CAD1110200017817," + "987654321," + mainArrangement + ",";

			result = arrangementAction(mainArrangement, customer, ROLEBASED_LENDING, "Transfer Between Accounts",
					fields, values, customerType);

			if (!result) {
				stepActual = "Error while performing Other Financial Transaction at Funding for Loan: "
						+ mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
