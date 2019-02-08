package testcases.PROC;

import java.util.Random;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PROC_OP_OR_PR029 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version: 2017-11-10";
	private String branch = "B2B Branch 817";
	private String agent1;
	private String agent2;
	private String customer1;
	private String customer2;
	private String agentArrangement1;
	private String agentArrangement2;
	private String productArrangement1;
	private String productArrangement2;

	@Test(priority = 1, enabled = true)
	@Parameters({ "effectiveDate", "agentCommissionPlan", "productGroup", "product" })
	public void preCondition(@Optional("+0d") final String effectiveDate, final String agentCommissionPlan,
			final String productGroup, final String product) {

		stepDescription = "Setup Dealer and Advisor with " + agentCommissionPlan + " product";
		stepExpected = "Dealer and Advisor with " + agentCommissionPlan + " product steup successfully";

		final Random generator = new Random();
		final int dealerRep = generator.nextInt(8999) + 1000;
		final int advisorRep = generator.nextInt(89999) + 10000;
		String step1Fields;
		String step1Values;
		String defaultStep2Fields;
		String defaultStep2Values;
		String fields;
		String values;

		if (loginResult) {

			if (DefaultVariables.productFields.containsKey(product)) {
				defaultStep2Fields = DefaultVariables.productFields.get(product);
				defaultStep2Values = DefaultVariables.productValues.get(product);
			} else {
				defaultStep2Fields = DefaultVariables.productGroupFields.get(productGroup);
				defaultStep2Values = DefaultVariables.productGroupValues.get(productGroup);
			}

			switchToBranch(branch);

			fields = DefaultVariables.dealerAdvisorCIFFields + "BUSINESS.NAME:1,L.DEALER.REP.NO,";
			values = DefaultVariables.dealerAdvisorCIFValues + "Tester Dealer Name," + dealerRep + ",";
			agent1 = customer(CREATE, DEALER_ADVISOR, ROLEBASED_LENDING, fields, values);

			fields = DefaultVariables.dealerAdvisorCIFFields + "AGENCY,L.DEALER.REP.NO,";
			values = DefaultVariables.dealerAdvisorCIFValues + agent1 + "," + dealerRep + "-" + advisorRep + ",";
			agent2 = customer(CREATE, DEALER_ADVISOR, ROLEBASED_LENDING, fields, values);

			step1Fields = "CUSTOMER:1," + "CURRENCY," + "EFFECTIVE.DATE,";
			step1Values = agent1 + "," + "CAD," + effectiveDate + ",";

			agentArrangement1 = arrangements(CREATE, AGENTS, agentCommissionPlan, "", agent1, step1Fields,
					step1Values, "", "");

			step1Fields = "CUSTOMER:1," + "CURRENCY," + "EFFECTIVE.DATE,";
			step1Values = agent2 + "," + "CAD," + effectiveDate + ",";

			agentArrangement2 = arrangements(CREATE, AGENTS, agentCommissionPlan, "", agent2, step1Fields,
					step1Values, "", "");

			customer1 = createDefaultCustomer(PERSONAL, productGroup, ROLEBASED_LENDING);
			step1Fields = "AGENT.ID:1," + "AGENT.ARR.ID:1," + "AGENT.ROLE:1," + "CUSTOMER:1," + "CURRENCY,";
			step1Values = agent1 + "," + agentArrangement1 + "," + "Agent," + customer1 + "," + "CAD,";
			productArrangement1 = arrangements(CREATE_AUTHORISE, productGroup, product, "", customer1, step1Fields,
					step1Values, defaultStep2Fields, defaultStep2Values);

			customer2 = createDefaultCustomer(PERSONAL, productGroup, ROLEBASED_LENDING);
			step1Fields = "AGENT.ID:1," + "AGENT.ARR.ID:1," + "AGENT.ROLE:1," + "CUSTOMER:1," + "CURRENCY,";
			step1Values = agent2 + "," + agentArrangement2 + "," + "Agency," + customer2 + "," + "CAD,";
			productArrangement2 = arrangements(CREATE_AUTHORISE, productGroup, product, "", customer2, step1Fields,
					step1Values, defaultStep2Fields, defaultStep2Values);

			if (agentArrangement1 == null || agentArrangement2 == null || productArrangement1 == null
					|| productArrangement2 == null || agent1 == null || agent2 == null || agent1.contains(ERROR)
					|| agent2.contains(ERROR) || agentArrangement1.contains(ERROR) || agentArrangement2.contains(ERROR)
					|| productArrangement1.contains(ERROR) || productArrangement2.contains(ERROR)) {
				stepActual = "Dealer and Advisor with " + agentCommissionPlan + " product did not steup successfully";
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
	@Parameters({ "productGroup" })
	public void dealerUpdate(final String productGroup) {

		stepDescription = "Update dealer for " + productGroup + " arrangement: " + productArrangement1;
		stepExpected = "Dealer for " + productGroup + " arrangement: " + productArrangement1 + " updated successfully";
		String fields;
		String values;
		boolean result;

		if (productArrangement1 == null || agent2 == null || agentArrangement2 == null
				|| productArrangement1.contains(ERROR) || agent2.contains(ERROR) || agentArrangement2.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			fields = "AGENT.ID:1," + "AGENT.ARR.ID:1,";
			values = agent2 + "," + agentArrangement2 + ",";

			result = arrangementActivity(CREATE_AUTHORISE, "UPDATE ACTIVITY FOR AR.COMMISSION", productArrangement1,
					productGroup, fields, values);

			if (!result) {
				stepActual = "Dealer for " + productGroup + " arrangement: " + productArrangement1
						+ " did not updated successfully";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "productGroup" })
	public void advisorUpdate(final String productGroup) {

		stepDescription = "Update advisor for " + productGroup + " arrangement: " + productArrangement2;
		stepExpected = "Advisor for " + productGroup + " arrangement: " + productArrangement2 + " updated successfully";
		String fields;
		String values;
		boolean result;

		if (productArrangement2 == null || agent2 == null || agentArrangement2 == null
				|| productArrangement2.contains(ERROR) || agent2.contains(ERROR) || agentArrangement2.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			fields = "AGENT.ID:1," + "AGENT.ARR.ID:1,";
			values = agent2 + "," + agentArrangement2 + ",";

			result = arrangementActivity(CREATE_AUTHORISE, "UPDATE ACTIVITY FOR AR.COMMISSION", productArrangement2,
					productGroup, fields, values);

			if (!result) {
				stepActual = "Advisor for " + productGroup + " arrangement: " + productArrangement2
						+ " did not updated successfully";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
