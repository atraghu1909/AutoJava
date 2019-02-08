package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PROC_OP_OR_PR038 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 2017-11-10";
	private String depositBranch1 = "Branch 247";
	private String depositBranch2 = "B2B Branch 607";
	private String mortgageBranch1 = "Branch 603";
	private String mortgageBranch2 = "B2B Branch 623";
	private String depositAgent1;
	private String depositAgent2;
	private String mortgageAgent1;
	private String mortgageAgent2;
	private String depositAgentArrangement1;
	private String depositAgentArrangement2;
	private String mortgageAgentArrangement1;
	private String mortgageAgentArrangement2;

	@Test(priority = 1, enabled = true)
	@Parameters({ "effectiveDate", "depositAgentCommissionPlan" })
	public void depositBrokerSetup(@Optional("+0d") final String effectiveDate,
			final String depositAgentCommissionPlan) {

		stepDescription = "Setup Deposit Broker for product: " + depositAgentCommissionPlan;
		stepExpected = "Deposit Broker for product: " + depositAgentCommissionPlan + " steup successfully";

		String fields;
		String values;
		
		if (loginResult) {

			switchToBranch(depositBranch1);
			fields = DefaultVariables.dealerAdvisorCIFFields + "," + "BUSINESS.NAME:1,";
			values = DefaultVariables.dealerAdvisorCIFValues + "," + "DEDE,";
			depositAgent1 = customer(CREATE, DEALER_ADVISOR, ROLEBASED_OR, fields, values);
			fields = "CUSTOMER:1," + "CURRENCY," + "EFFECTIVE.DATE,";
			values = depositAgent1 + "," + CAD + "," + effectiveDate + ",";
			depositAgentArrangement1 = arrangements(CREATE, AGENTS, depositAgentCommissionPlan, "", depositAgent1,
					fields, values, "", "");

			switchToBranch(depositBranch2);
			fields = DefaultVariables.dealerAdvisorCIFFields + "," + "BUSINESS.NAME:1,";
			values = DefaultVariables.dealerAdvisorCIFValues + "," + "DEDE,";
			depositAgent2 = customer(CREATE, DEALER_ADVISOR, ROLEBASED_OR, fields, values);
			fields = "CUSTOMER:1," + "CURRENCY," + "EFFECTIVE.DATE,";
			values = depositAgent2 + "," + CAD + "," + effectiveDate + ",";
			depositAgentArrangement2 = arrangements(CREATE, AGENTS, depositAgentCommissionPlan, "", depositAgent2,
					fields, values, "", "");

			if (depositAgentArrangement1 == null || depositAgentArrangement2 == null
					|| depositAgentArrangement1.contains(ERROR) || depositAgentArrangement2.contains(ERROR)) {
				stepActual = "Deposite Broker for product: " + depositAgentCommissionPlan
						+ " did not steup successfully";
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
	@Parameters({ "effectiveDate", "mortgageAgentCommissionPlan" })
	public void mortgageDealerSetup(@Optional("+0d") final String effectiveDate,
			final String mortgageAgentCommissionPlan) {

		stepDescription = "Setup Mortgage Broker for product: " + mortgageAgentCommissionPlan;
		stepExpected = "Mortgage Broker for product: " + mortgageAgentCommissionPlan + " steup successfully";

		String fields;
		String values;
		
		if (loginResult) {

			switchToBranch(mortgageBranch1);
			fields = DefaultVariables.dealerAdvisorCIFFields + "," + "GIVEN.NAMES,";
			values = DefaultVariables.dealerAdvisorCIFValues + "," + "ABAB,";
			mortgageAgent1 = customer(CREATE, DEALER_ADVISOR, ROLEBASED_LENDING, fields, values);
			fields = "CUSTOMER:1," + "CURRENCY," + "EFFECTIVE.DATE,";
			values = mortgageAgent1 + "," + CAD + "," + effectiveDate + ",";
			mortgageAgentArrangement1 = arrangements(CREATE, AGENTS, mortgageAgentCommissionPlan, "", mortgageAgent1,
					fields, values, "", "");

			switchToBranch(mortgageBranch2);
			fields = DefaultVariables.dealerAdvisorCIFFields + "," + "GIVEN.NAMES,";
			values = DefaultVariables.dealerAdvisorCIFValues + "," + "ABAB,";
			mortgageAgent2 = customer(CREATE, DEALER_ADVISOR, ROLEBASED_LENDING, fields, values);
			fields = "CUSTOMER:1," + "CURRENCY," + "EFFECTIVE.DATE,";
			values = mortgageAgent2 + "," + CAD + "," + effectiveDate + ",";
			mortgageAgentArrangement2 = arrangements(CREATE, AGENTS, mortgageAgentCommissionPlan, "", mortgageAgent2,
					fields, values, "", "");

			if (mortgageAgentArrangement1 == null || mortgageAgentArrangement2 == null
					|| mortgageAgentArrangement1.contains(ERROR) || mortgageAgentArrangement2.contains(ERROR)) {
				stepActual = "Mortgage Broker for product: " + mortgageAgentCommissionPlan
						+ " did not steup successfully";
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
	public void deAactivateBrokerDealer() {

		stepDescription = "De-activate Broker/Dealer " + depositAgent1 + " and " + mortgageAgent1;
		stepExpected = "Broker/Dealer " + depositAgent1 + " and " + mortgageAgent1 + " De-activated successfully";

		if (depositAgent1 == null || mortgageAgent1 == null || depositAgent1.contains(ERROR)
				|| mortgageAgent1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			String message1;
			String message2;
			String fields = "CUSTOMER.STATUS,";
			String values = "402,";

			switchToBranch(depositBranch1);
			message1 = amendCIF(AMEND, depositAgent1, DEALER_ADVISOR, ROLEBASED_OR, fields, values);

			switchToBranch(mortgageBranch1);
			message2 = amendCIF(AMEND, mortgageAgent1, DEALER_ADVISOR, ROLEBASED_LENDING, fields, values);

			if (message1 == null || message2 == null || message1.contains(ERROR) || message2.contains(ERROR)) {
				stepActual = "Error while De-activating Broker/Dealer " + depositAgent1 + " and " + mortgageAgent1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	public void updateBrokerDealer() {

		stepDescription = "Update Broker/Dealer " + depositAgent2 + " and " + mortgageAgent2;
		stepExpected = "Broker/Dealer " + depositAgent2 + " and " + mortgageAgent2 + " updated successfully";

		if (depositAgent2 == null || mortgageAgent2 == null || depositAgent2.contains(ERROR)
				|| mortgageAgent2.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			String message1;
			String message2;
			String fields = "NAME.1:1," + "SHORT.NAME:1," + "Address#ADDRESS:1:1," + "Address#STREET:1,"
					+ "Address#TOWN.COUNTRY:1," + "Address#ADDR.CNTRY.ID," + "Address#US.STATE,"
					+ "Address#POST.CODE:1," + "Address#CITY," + "Address#RECORD.REFRESH,";
			String values = "nana," + "dada," + "5," + "Fifth Street," + "Unit 5," + "CA," + "ON," + "M4C 2N7,"
					+ "Toronto," + "Y,";

			switchToBranch(depositBranch2);
			message1 = amendCIF(AMEND, depositAgent2, DEALER_ADVISOR, ROLEBASED_OR, fields, values);

			switchToBranch(mortgageBranch2);
			message2 = amendCIF(AMEND, mortgageAgent2, DEALER_ADVISOR, ROLEBASED_LENDING, fields, values);

			if (message1 == null || message2 == null || message1.contains(ERROR) || message2.contains(ERROR)) {
				stepActual = "Error while updating Broker/Dealer " + depositAgent2 + " and " + mortgageAgent2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
