package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PROC_OP_OR_PR039 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version: 2017-11-10";
	private String depositBranch1 = "Branch 247";
	private String depositBranch2 = "B2B Branch 607";
	private String bankingBranch1 = "Branch 994";
	private String bankingBranch2 = "B2B Branch 817";
	private String mortgageBranch1 = "Branch 603";
	private String mortgageBranch2 = "B2B Branch 623";
	private String depositAgent1;
	private String depositAgent2;
	private String bankingAgent1;
	private String bankingAgent2;
	private String mortgageAgent1;
	private String mortgageAgent2;
	private String depositDealer1;
	private String depositDealer2;
	private String bankingDealer1;
	private String bankingDealer2;
	private String mortgageDealer1;
	private String mortgageDealer2;
	private String depositAgentArrangement1;
	private String depositAgentArrangement2;
	private String bankingAgentArrangement1;
	private String bankingAgentArrangement2;
	private String mortgageAgentArrangement1;
	private String mortgageAgentArrangement2;
	private String depositDealerArrangement1;
	private String depositDealerArrangement2;
	private String bankingDealerArrangement1;
	private String bankingDealerArrangement2;
	private String mortgageDealerArrangement1;
	private String mortgageDealerArrangement2;

	@Test(priority = 1, enabled = true)
	@Parameters({ "effectiveDate", "depositAgentCommissionPlan", "mortgageAgentCommissionPlan",
			"bankingAgentCommissionPlan" })
	public void setupDealer(@Optional("+0d") final String effectiveDate, final String depositAgentCommissionPlan,
			final String mortgageAgentCommissionPlan, final String bankingAgentCommissionPlan) {

		stepDescription = "Setup deposit, banking and mortgage Dealer";
		stepExpected = "Deposit, banking and mortgage Dealer steup successfully";

		final String customerFields = DefaultVariables.dealerAdvisorCIFFields + "," + "BUSINESS.NAME:1,";
		final String customerValues = DefaultVariables.dealerAdvisorCIFValues + "," + "DEDE,";
		final String arrangementFields = "CUSTOMER:1," + "CURRENCY," + "EFFECTIVE.DATE,";
		String arrangementValues;
		
		if (loginResult) {

			switchToBranch(depositBranch1);
			depositDealer1 = customer(CREATE, DEALER_ADVISOR, ROLEBASED_BANKING, customerFields, customerValues);
			arrangementValues = depositDealer1 + "," + CAD + "," + effectiveDate + ",";
			depositDealerArrangement1 = arrangements(CREATE, AGENTS, depositAgentCommissionPlan, "", depositDealer1,
					arrangementFields, arrangementValues, "", "");

			switchToBranch(depositBranch2);
			depositDealer2 = customer(CREATE, DEALER_ADVISOR, ROLEBASED_BANKING, customerFields, customerValues);
			arrangementValues = depositDealer2 + "," + CAD + "," + effectiveDate + ",";
			depositDealerArrangement2 = arrangements(CREATE, AGENTS, depositAgentCommissionPlan, "", depositDealer2,
					arrangementFields, arrangementValues, "", "");

			switchToBranch(bankingBranch1);
			bankingDealer1 = customer(CREATE, DEALER_ADVISOR, ROLEBASED_BANKING, customerFields, customerValues);
			arrangementValues = bankingDealer1 + "," + CAD + "," + effectiveDate + ",";
			bankingDealerArrangement1 = arrangements(CREATE, AGENTS, bankingAgentCommissionPlan, "", bankingDealer1,
					arrangementFields, arrangementValues, "", "");

			switchToBranch(bankingBranch2);
			bankingDealer2 = customer(CREATE, DEALER_ADVISOR, ROLEBASED_BANKING, customerFields, customerValues);
			arrangementValues = bankingDealer2 + "," + CAD + "," + effectiveDate + ",";
			bankingDealerArrangement2 = arrangements(CREATE, AGENTS, bankingAgentCommissionPlan, "", bankingDealer2,
					arrangementFields, arrangementValues, "", "");

			switchToBranch(mortgageBranch1);
			mortgageDealer1 = customer(CREATE, DEALER_ADVISOR, ROLEBASED_BANKING, customerFields, customerValues);
			arrangementValues = mortgageDealer1 + "," + CAD + "," + effectiveDate + ",";
			mortgageDealerArrangement1 = arrangements(CREATE, AGENTS, mortgageAgentCommissionPlan, "", mortgageDealer1,
					arrangementFields, arrangementValues, "", "");

			switchToBranch(mortgageBranch2);
			mortgageDealer2 = customer(CREATE, DEALER_ADVISOR, ROLEBASED_BANKING, customerFields, customerValues);
			arrangementValues = mortgageDealer2 + "," + CAD + "," + effectiveDate + ",";
			mortgageDealerArrangement2 = arrangements(CREATE, AGENTS, mortgageAgentCommissionPlan, "", mortgageDealer2,
					arrangementFields, arrangementValues, "", "");

			if (depositDealerArrangement1 == null || depositDealerArrangement2 == null
					|| bankingDealerArrangement1 == null || bankingDealerArrangement2 == null
					|| mortgageDealerArrangement1 == null || mortgageDealerArrangement2 == null
					|| depositDealerArrangement1.contains(ERROR) || depositDealerArrangement2.contains(ERROR)
					|| bankingDealerArrangement1.contains(ERROR) || bankingDealerArrangement2.contains(ERROR)
					|| mortgageDealerArrangement1.contains(ERROR) || mortgageDealerArrangement2.contains(ERROR)) {

				stepActual = "Deposit, banking and mortgage Dealer did not steup successfully";
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
	@Parameters({ "effectiveDate", "depositAgentCommissionPlan" })
	public void depositAgentSetup(@Optional("+0d") final String effectiveDate,
			final String depositAgentCommissionPlan) {

		stepDescription = "Setup Deposite Agent for product: " + depositAgentCommissionPlan;
		stepExpected = "Deposite Agent for product: " + depositAgentCommissionPlan + " steup successfully";

		String fields;
		String values;
		
		if (depositDealerArrangement1 == null || depositDealerArrangement2 == null
				|| depositDealerArrangement1.contains(ERROR) || depositDealerArrangement2.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			switchToBranch(depositBranch1);
			fields = DefaultVariables.dealerAdvisorCIFFields + "," + "GIVEN.NAMES,";
			values = DefaultVariables.dealerAdvisorCIFValues + "," + "ABABA,";
			depositAgent1 = customer(CREATE, DEALER_ADVISOR, ROLEBASED_BANKING, fields, values);
			fields = "CUSTOMER:1," + "CURRENCY," + "EFFECTIVE.DATE,";
			values = depositAgent1 + "," + CAD + "," + effectiveDate + ",";
			depositAgentArrangement1 = arrangements(CREATE, AGENTS, depositAgentCommissionPlan, "", depositAgent1,
					fields, values, "", "");

			switchToBranch(depositBranch2);
			fields = DefaultVariables.dealerAdvisorCIFFields + "," + "GIVEN.NAMES,";
			values = DefaultVariables.dealerAdvisorCIFValues + "," + "ABABA,";
			depositAgent2 = customer(CREATE, DEALER_ADVISOR, ROLEBASED_BANKING, fields, values);
			fields = "CUSTOMER:1," + "CURRENCY," + "EFFECTIVE.DATE,";
			values = depositAgent2 + "," + CAD + "," + effectiveDate + ",";
			depositAgentArrangement2 = arrangements(CREATE, AGENTS, depositAgentCommissionPlan, "", depositAgent2,
					fields, values, "", "");

			if (depositAgentArrangement1 == null || depositAgentArrangement2 == null
					|| depositAgentArrangement1.contains(ERROR) || depositAgentArrangement2.contains(ERROR)) {
				stepActual = "Deposite Agent for product: " + depositAgentCommissionPlan
						+ " did not steup successfully";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "effectiveDate", "bankingAgentCommissionPlan" })
	public void bankingAgentsetup(@Optional("+0d") final String effectiveDate,
			final String bankingAgentCommissionPlan) {

		stepDescription = "Setup Banking Agent for product: " + bankingAgentCommissionPlan;
		stepExpected = "Banking Agent for product: " + bankingAgentCommissionPlan + " steup successfully";

		String fields;
		String values;
		
		if (bankingDealerArrangement1 == null || bankingDealerArrangement2 == null
				|| bankingDealerArrangement1.contains(ERROR) || bankingDealerArrangement2.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			switchToBranch(mortgageBranch1);
			fields = DefaultVariables.dealerAdvisorCIFFields + "," + "GIVEN.NAMES,";
			values = DefaultVariables.dealerAdvisorCIFValues + "," + "ABABA,";
			bankingAgent1 = customer(CREATE, DEALER_ADVISOR, ROLEBASED_BANKING, fields, values);
			fields = "CUSTOMER:1," + "CURRENCY," + "EFFECTIVE.DATE,";
			values = bankingAgent1 + "," + CAD + "," + effectiveDate + ",";
			bankingAgentArrangement1 = arrangements(CREATE, AGENTS, bankingAgentCommissionPlan, "", bankingAgent1,
					fields, values, "", "");

			switchToBranch(mortgageBranch2);
			fields = DefaultVariables.dealerAdvisorCIFFields + "," + "GIVEN.NAMES,";
			values = DefaultVariables.dealerAdvisorCIFValues + "," + "ABABA,";
			bankingAgent2 = customer(CREATE, DEALER_ADVISOR, ROLEBASED_BANKING, fields, values);
			fields = "CUSTOMER:1," + "CURRENCY," + "EFFECTIVE.DATE,";
			values = bankingAgent2 + "," + CAD + "," + effectiveDate + ",";
			bankingAgentArrangement2 = arrangements(CREATE, AGENTS, bankingAgentCommissionPlan, "", bankingAgent2,
					fields, values, "", "");

			if (bankingAgentArrangement1 == null || bankingAgentArrangement2 == null
					|| bankingAgentArrangement1.contains(ERROR) || bankingAgentArrangement2.contains(ERROR)) {
				stepActual = "Banking Agent for product: " + bankingAgentCommissionPlan + " did not steup successfully";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters({ "effectiveDate", "mortgageAgentCommissionPlan" })
	public void mortgageAgentSetup(@Optional("+0d") final String effectiveDate,
			final String mortgageAgentCommissionPlan) {

		stepDescription = "Setup Mortgage Agent for product: " + mortgageAgentCommissionPlan;
		stepExpected = "Mortgage Agent for product: " + mortgageAgentCommissionPlan + " steup successfully";

		String fields;
		String values;
		
		if (mortgageDealerArrangement1 == null || mortgageDealerArrangement2 == null
				|| mortgageDealerArrangement1.contains(ERROR) || mortgageDealerArrangement2.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			switchToBranch(mortgageBranch1);
			fields = DefaultVariables.dealerAdvisorCIFFields + "," + "GIVEN.NAMES,";
			values = DefaultVariables.dealerAdvisorCIFValues + "," + "ABABA,";
			mortgageAgent1 = customer(CREATE, DEALER_ADVISOR, ROLEBASED_BANKING, fields, values);
			fields = "CUSTOMER:1," + "CURRENCY," + "EFFECTIVE.DATE,";
			values = mortgageAgent1 + "," + CAD + "," + effectiveDate + ",";
			mortgageAgentArrangement1 = arrangements(CREATE, AGENTS, mortgageAgentCommissionPlan, "", mortgageAgent1,
					fields, values, "", "");

			switchToBranch(mortgageBranch2);
			fields = DefaultVariables.dealerAdvisorCIFFields + "," + "GIVEN.NAMES,";
			values = DefaultVariables.dealerAdvisorCIFValues + "," + "ABABA,";
			mortgageAgent2 = customer(CREATE, DEALER_ADVISOR, ROLEBASED_BANKING, fields, values);
			fields = "CUSTOMER:1," + "CURRENCY," + "EFFECTIVE.DATE,";
			values = mortgageAgent2 + "," + CAD + "," + effectiveDate + ",";
			mortgageAgentArrangement2 = arrangements(CREATE, AGENTS, mortgageAgentCommissionPlan, "", mortgageAgent2,
					fields, values, "", "");

			if (mortgageAgentArrangement1 == null || mortgageAgentArrangement2 == null
					|| mortgageAgentArrangement1.contains(ERROR) || mortgageAgentArrangement2.contains(ERROR)) {
				stepActual = "Mortgage Agent for product: " + mortgageAgentCommissionPlan
						+ " did not steup successfully";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();

	}

	@Test(priority = 5, enabled = true)
	public void deAactivateAgent() {

		stepDescription = "De-activate Agents " + depositAgent2 + ", " + mortgageAgent2 + " and " + bankingAgent2;
		stepExpected = "Agents " + depositAgent2 + ", " + mortgageAgent2 + " and " + bankingAgent2
				+ " De-activated successfully";

		if (depositAgent1 == null || bankingAgent1 == null || mortgageAgent1 == null || depositAgent1.contains(ERROR)
				|| bankingAgent1.contains(ERROR) || mortgageAgent1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			String message1;
			String message2;
			String message3;
			String fields = "CUSTOMER.STATUS,";
			String values = "404,";

			switchToBranch(depositBranch1);
			message1 = amendCIF(AMEND, depositAgent1, DEALER_ADVISOR, ROLEBASED_BANKING, fields, values);

			switchToBranch(bankingBranch1);
			message2 = amendCIF(AMEND, bankingAgent1, DEALER_ADVISOR, ROLEBASED_BANKING, fields, values);

			switchToBranch(mortgageBranch1);
			message3 = amendCIF(AMEND, mortgageAgent1, DEALER_ADVISOR, ROLEBASED_BANKING, fields, values);

			if (message1.contains(ERROR) || message2.contains(ERROR) || message3.contains(ERROR)) {
				stepActual = "Error while De-activating Agents " + depositAgent2 + ", " + mortgageAgent2 + " and "
						+ bankingAgent2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	public void updateAgent() {

		stepDescription = "Update Agents " + depositAgent2 + ", " + mortgageAgent2 + " and " + bankingAgent2;
		stepExpected = "Agents " + depositAgent2 + ", " + mortgageAgent2 + " and " + bankingAgent2
				+ " updated successfully";

		if (depositAgent2 == null || bankingAgent2 == null || mortgageAgent2 == null || depositAgent2.contains(ERROR)
				|| bankingAgent2.contains(ERROR) || mortgageAgent2.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			String message1;
			String message2;
			String message3;
			String fields = "NAME.1:1," + "SHORT.NAME:1," + "Address#ADDRESS:1:1," + "Address#STREET:1,"
					+ "Address#TOWN.COUNTRY:1," + "Address#ADDR.CNTRY.ID," + "Address#US.STATE,"
					+ "Address#POST.CODE:1," + "Address#CITY," + "Address#RECORD.REFRESH,";
			String values = "nana," + "dada," + "5," + "Fifth Street," + "Unit 5," + "CA," + "ON," + "M4C 2N7,"
					+ "Toronto," + "Y,";

			switchToBranch(depositBranch2);
			message1 = amendCIF(AMEND, depositAgent2, DEALER_ADVISOR, ROLEBASED_BANKING, fields, values);

			switchToBranch(bankingBranch2);
			message2 = amendCIF(AMEND, bankingAgent2, DEALER_ADVISOR, ROLEBASED_BANKING, fields, values);

			switchToBranch(mortgageBranch2);
			message3 = amendCIF(AMEND, mortgageAgent2, DEALER_ADVISOR, ROLEBASED_BANKING, fields, values);

			if (message1.contains(ERROR) || message2.contains(ERROR) || message3.contains(ERROR)) {
				stepActual = "Error while updating Agents " + depositAgent2 + ", " + mortgageAgent2 + " and "
						+ bankingAgent2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
