package testcases.PROC;

import java.util.Random;

import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PROC_SF_ORA_OP001 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version: 2017-11-10";
	private static String branch = "B2B Branch 817";
	private String dealer;
	private String advisor1;
	private String advisor2;
	private String customer;
	private String dealerBeneficiary;
	private String dealerArrangement;
	private String advisorArrangement;
	private String newAdvisorArrangement;
	private String productArrangement;
	private String dealerRep;
	private String fields;
	private String values;
	private final Random generator = new Random();
	private final int advisorRep1 = generator.nextInt(89999) + 10000;
	private final int advisorRep2 = generator.nextInt(89999) + 10000;

	@Test(priority = 1, enabled = true)
	public void createDealer() {

		stepDescription = "Create Dealer";
		stepExpected = "Dealer was created successfully";

		if (loginResult) {
			switchToBranch(branch);
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
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Login failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
	}

	@Test(priority = 2, enabled = true)
	public void searchDealer() {

		stepDescription = "Search Dealer: " + dealer;
		stepExpected = "Dealer: " + dealer + " was found successfully";

		String actualDealer;

		if (dealer == null || dealer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			actualDealer = findCIF(dealer, DEALER_ADVISOR, ROLEBASED_OR);

			if (!dealer.equals(actualDealer)) {
				stepActual = "Error while searching Dealer: " + dealer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	public void modifyDealer() {

		stepDescription = "Modify Dealer: " + dealer;
		stepExpected = "Dealer: " + dealer + " modified successfully";

		String message;
		fields = "BUSINESS.NAME:1," + "BUSINESS.NAME+:2," + "NAME.1:1," + "L.MAIL.PREF,"
				+ "Communication Details#EMAIL.1:1,Communication Details#PHONE.1:1," + "Communication Details#SMS.1:1,"
				+ "Communication Details#FAX.1:1," + "Communication Details#OFF.PHONE:1,";
		values = "Edited Dealer Name," + "Modified Continuation," + "Edited Dealer Name," + "Canada Post Standard,"
				+ "another@email.com," + "416-987-0000,647-333-1234," + "905-555-1357," + "1-800-456-7777,";

		if (dealer == null || dealer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			message = amendCIF(AMEND, dealer, DEALER_ADVISOR, ROLEBASED_OR, fields, values);

			if (message == null || message.contains(ERROR)) {
				stepActual = "Error while modifying dealer: " + dealer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters({ "productLine" })
	public void dealerBeneficiarySetup(final String productLine) {

		String beneficiaryFields;
		String beneficiaryValues;

		stepDescription = "Setup Beneficiary for Dealer: " + dealer;
		stepExpected = "Beneficiary for Dealer: " + dealer + " steup successfully";
		if (dealer == null || dealer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			if ("Lending".equals(productLine)) {
				stepActual = "Dealers for Lending Product do not need to setup Beneficiary information";
				throw new SkipException(stepActual);
			} else {
				beneficiaryFields = DefaultVariables.beneficiaryFields + "OWNING.CUSTOMER," + "BEN.CUSTOMER,";
				beneficiaryValues = DefaultVariables.beneficiaryValues + dealer + ","
						+ createdCustomers.get(dealer).getCustomerName() + ",";

				dealerBeneficiary = beneficiaryCode(CREATE, "EFT Client", "", beneficiaryFields, beneficiaryValues);

				if (dealerBeneficiary == null || dealerBeneficiary.contains(ERROR)) {
					stepActual = "Beneficiary for Dealer: " + dealer + " did not steup successfully";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	@Parameters({ "agentCommissionPlan", "productLine" })
	public void setupDealerArrangement(final String agentCommissionPlan, final String productLine) {

		stepDescription = "Setup " + agentCommissionPlan + " Arrangement for Dealer: " + dealer;
		stepExpected = agentCommissionPlan + " Arrangement for Dealer: " + dealer + " Setup successfully";

		if (dealer == null || dealer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			if ("Lending".equals(productLine)) {
				stepActual = "Dealers for Lending Product do not need to setup Beneficiary information";
			} else {
				fields = "Commission Payout Schedule#COMM.SCHEDULE," + "Commission Payout Schedule#PAYMENT.PRODUCT,"
						+ "Commission Payout Schedule#PO.BENEFICIARY,";
				values = "M0101," + "CHQCOM," + dealerBeneficiary + ",";
			}

			fields = "CUSTOMER:1," + "CURRENCY," + "EFFECTIVE.DATE,";
			values = dealer + "," + CAD + "," + "+0d,";
			dealerArrangement = arrangements(CREATE, AGENTS, agentCommissionPlan, "", dealer, fields, values, "", "");

			if (dealerArrangement == null || dealerArrangement.contains(ERROR)) {
				stepActual = "Error while Creating " + agentCommissionPlan + " Arrangement for Dealer: " + dealer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	@Parameters({ "agentCommissionPlan" })
	public void searchDealerArrangement(final String agentCommissionPlan) {

		stepDescription = "Search Dealer Arrangement: " + dealerArrangement;
		stepExpected = "Dealer Arrangement: " + dealerArrangement + " was found successfully";

		String actualDealerArrangement;

		if (dealerArrangement == null || dealerArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			actualDealerArrangement = findArrangement(AUTHORISED, dealerArrangement, ARRANGEMENT, "", AGENTS,
					agentCommissionPlan, CAD);

			if (!dealerArrangement.equals(actualDealerArrangement)) {
				stepActual = "Error while searching Dealer Arrangement: " + dealerArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 7, enabled = true)
	public void inactivateDealerArrangement() {

		stepDescription = "Inactivate Dealer Arrangement: " + dealerArrangement;
		stepExpected = "Dealer Arrangement: " + dealerArrangement + " was inactivated successfully";

		boolean result;
		fields = "MEMO.TEXT,";
		values = "Inactivated Arrangement as a Test,";

		if (dealerArrangement == null || dealerArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementActivity(CREATE_AUTHORISE, "INACTIVE ACTIVITY FOR ARRANGEMENT", dealerArrangement,
					AGENTS, "", "");
			if (result) {
				result = arrangementMemo(CREATE, dealerArrangement, AGENTS, fields, values);
			}

			if (!result) {
				stepActual = "Error while inactivating Dealer Arrangement: " + dealerArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 8, enabled = true)
	public void inactivateDealer() {

		stepDescription = "Inactivate Dealer Arrangement: " + dealerArrangement;
		stepExpected = "Dealer Arrangement: " + dealerArrangement + " was inactivated successfully";

		String message;
		boolean result = false;
		fields = "CUSTOMER.STATUS,";
		values = "402,";

		if (dealer == null || dealer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			message = amendCIF(AMEND, dealer, DEALER_ADVISOR, ROLEBASED_OR, fields, values);

			if (message == null || message.contains(ERROR)) {
				stepActual = "Error while inactivating Dealer Arrangement: " + dealerArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {
				fields = "CONTACT.NOTES:1," + "CONTACT.CHANNEL," + "CONTACT.DESC," + "CONTACT.DATE," + "CONTACT.TIME,";
				values = "Inactivated Dealer as a test," + "ATM," + "Inactivated Dealer as a test," + "+0d," + "00:00,";
				result = customerIntervention(dealer, CREATE, fields, values, DEALER_ADVISOR);
			}
			if (!result) {
				stepActual = "Error while inactivating Dealer Arrangement: " + dealerArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 9, enabled = true)
	public void createAdvisor() {

		stepDescription = "Create Advisor";
		stepExpected = "Advisor was created successfully";

		fields = DefaultVariables.dealerAdvisorCIFFields + "SHORT.NAME:1," + "AGENCY," + "L.DEALER.REP.NO,";
		values = DefaultVariables.dealerAdvisorCIFValues + dealerRep + "-" + advisorRep1 + "," + dealer + ","
				+ dealerRep + "-" + advisorRep1 + ",";

		if (loginResult) {

			advisor1 = customer(CREATE, DEALER_ADVISOR, ROLEBASED_OR, fields, values);

			if (advisor1 == null || advisor1.contains(ERROR)) {
				stepActual = "Error while creating Advisor";
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

	@Test(priority = 10, enabled = true)
	public void searchAdvisor() {

		stepDescription = "Search advisor: " + advisor1;
		stepExpected = "Advisor: " + advisor1 + " was found successfully";

		String actualAdvisor;

		if (advisor1 == null || advisor1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			actualAdvisor = findCIF(advisor1, DEALER_ADVISOR, ROLEBASED_OR);

			if (!advisor1.equals(actualAdvisor)) {
				stepActual = "Error while searching advisor: " + advisor1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 11, enabled = true)
	public void modifyAdvisor() {

		stepDescription = "Modify advisor: " + advisor1;
		stepExpected = "Advisor: " + advisor1 + " modified successfully";

		String message;
		fields = "FAMILY.NAME," + "NAME.1:1," + "L.MAIL.PREF," + "Communication Details#EMAIL.1:1,"
				+ "Communication Details#PHONE.1:1," + "Communication Details#SMS.1:1,"
				+ "Communication Details#FAX.1:1," + "Communication Details#OFF.PHONE:1,";
		values = "Smyth," + "John Smyth," + "Canada Post Standard," + "another@email.com," + "416-987-0000,"
				+ "647-333-1234," + "905-555-1357," + "1-800-456-7777,";

		if (advisor1 == null || advisor1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			message = amendCIF(AMEND, advisor1, DEALER_ADVISOR, ROLEBASED_OR, fields, values);

			if (message == null || message.contains(ERROR)) {
				stepActual = "Error while modifying advisor: " + advisor1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 12, enabled = true)
	@Parameters({ "advisorCommissionPlan" })
	public void setupAdvisorArrangement(final String advisorCommissionPlan) {

		stepDescription = "Setup " + advisorCommissionPlan + " Arrangement for Advisor: " + advisor1;
		stepExpected = advisorCommissionPlan + " Arrangement for Advisor: " + advisor1 + " Setup successfully";

		if (advisor1 == null || advisor1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			fields = "CUSTOMER:1," + "CURRENCY," + "EFFECTIVE.DATE,";
			values = advisor1 + "," + CAD + "," + "+0d,";
			advisorArrangement = arrangements(CREATE, AGENTS, advisorCommissionPlan, "", advisor1, fields, values, "",
					"");

			if (advisorArrangement == null || advisorArrangement.contains(ERROR)) {
				stepActual = "Error while Creating " + advisorCommissionPlan + " Arrangement for advisor: " + advisor1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 13, enabled = true)
	@Parameters({ "advisorCommissionPlan" })
	public void searchAdvisorArrangement(final String advisorCommissionPlan) {

		stepDescription = "Search Advisor Arrangement: " + advisorArrangement;
		stepExpected = "Advisor Arrangement: " + advisorArrangement + " was found successfully";

		String actualAdvisorArrangement;

		if (advisorArrangement == null || advisorArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			actualAdvisorArrangement = findArrangement(AUTHORISED, advisorArrangement, ARRANGEMENT, "", AGENTS,
					advisorCommissionPlan, CAD);

			if (!advisorArrangement.equals(actualAdvisorArrangement)) {
				stepActual = "Error while searching Advisor Arrangement: " + advisorArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 14, enabled = true)
	public void inactivateAdvisorArrangement() {

		stepDescription = "Inactivate Advisor Arrangement: " + advisorArrangement;
		stepExpected = "Advisor Arrangement: " + advisorArrangement + " was inactivated successfully";

		boolean result;
		fields = "MEMO.TEXT,";
		values = "Inactivated Arrangement as a Test,";

		if (advisorArrangement == null || advisorArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementActivity(CREATE_AUTHORISE, "INACTIVE ACTIVITY FOR ARRANGEMENT", advisorArrangement,
					AGENTS, "", "");
			if (result) {
				result = arrangementMemo(CREATE, advisorArrangement, AGENTS, fields, values);
			}
			if (!result) {
				stepActual = "Error while inactivating Advisor Arrangement: " + advisorArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 15, enabled = true)
	public void inactivateAdvisor() {

		stepDescription = "Inactivate Advisor Arrangement: " + advisorArrangement;
		stepExpected = "Advisor Arrangement: " + advisorArrangement + " was inactivated successfully";

		String message;
		boolean result = false;
		fields = "CUSTOMER.STATUS,";
		values = "402,";

		if (advisor1 == null || advisor1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			message = amendCIF(AMEND, advisor1, DEALER_ADVISOR, ROLEBASED_OR, fields, values);
			if (message == null || message.contains(ERROR)) {
				stepActual = "Error while inactivating Dealer Arrangement: " + dealerArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {
				fields = "CONTACT.NOTES:1," + "CONTACT.CHANNEL," + "CONTACT.DESC," + "CONTACT.DATE," + "CONTACT.TIME,";
				values = "Inactivated Advisor as a test," + "ATM," + "Inactivated Advisor as a test," + "+0d,"
						+ "00:00,";
				result = customerIntervention(advisor1, CREATE, fields, values, DEALER_ADVISOR);
			}
			if (!result) {
				stepActual = "Error while inactivating Advisor Arrangement: " + advisorArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 16, enabled = true)
	@Parameters({ "advisorCommissionPlan" })
	public void transferAdvisorArrangement(final String advisorCommissionPlan) {

		stepDescription = "Change Advisor of arrangement: " + advisorArrangement + " from advisor " + advisor1
				+ " to advisor " + advisor2;
		stepExpected = "Advisor of arrangement: " + advisorArrangement + " from advisor " + advisor1 + " to advisor "
				+ advisor2 + " transfered successfully";

		boolean result;
		fields = DefaultVariables.dealerAdvisorCIFFields + "SHORT.NAME:1," + "AGENCY," + "L.DEALER.REP.NO,";
		values = DefaultVariables.dealerAdvisorCIFValues + "2468," + dealer + "," + "1234 " + advisorRep2 + ",";

		if (advisorArrangement == null || advisorArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			advisor2 = customer(CREATE, DEALER_ADVISOR, ROLEBASED_OR, fields, values);

			result = arrangementActivity(OPEN, "CHANGE ACTIVITY FOR CUSTOMER", advisorArrangement, AGENTS, "", "");

			if (result) {
				result = inputData("CUSTOMER:1", advisor2, false);

				fields = "LIMIT.ALLOC.PERC:1," + "GL.ALLOC.PERC:1,";
				values = "100," + "100,";
				toolElements.toolsButton(VALIDATE_DEAL).click();
				result = multiInputData(fields, values, false);

				toolElements.toolsButton(COMMIT_DEAL).click();
				result = inputTable.verifyAcceptOverride();
			}

			if (result) {
				fields = "MEMO.TEXT,";
				values = "Transferred commission plan from " + advisor1 + " to " + advisor2 + ",";
				result = arrangementMemo(CREATE, advisorArrangement, AGENTS, fields, values);
			}

			if (advisor2 == null || advisor2.contains(ERROR) || !result) {
				stepActual = "Error while transferring Advisor Arrangement from advisor " + advisor1 + " to advisor "
						+ advisor2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 17, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void setupProductArrangementAndLinkAdvisor(final String productGroup, final String product) {

		stepDescription = "Setup " + product + " Arrangement and link Advisor: " + advisor2;
		stepExpected = product + " Arrangement was setup and Advisor: " + advisor2 + " linked successfully";

		String defaultStep2Fields;
		String defaultStep2Values;

		if (advisor2 == null || dealer == null || advisor2.contains(ERROR) || dealer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			if (DefaultVariables.productFields.containsKey(product)) {
				defaultStep2Fields = DefaultVariables.productFields.get(product);
				defaultStep2Values = DefaultVariables.productValues.get(product);
			} else {
				defaultStep2Fields = DefaultVariables.productGroupFields.get(productGroup);
				defaultStep2Values = DefaultVariables.productGroupValues.get(productGroup);
			}

			customer = createDefaultCustomer(PERSONAL, productGroup, ROLEBASED_BANKING);
			
			final String step1Fields = "EFFECTIVE.DATE," + "AGENT.ID:1," + "AGENT.ID+:2," + "AGENT.ARR.ID:1,"
					+ "AGENT.ARR.ID:2," + "AGENT.ROLE:1," + "AGENT.ROLE:2," + "CUSTOMER:1," + "CURRENCY,";
			final String step1Values = "+0d," + advisor2 + "," + dealer + "," + advisorArrangement + ","
					+ dealerArrangement + "," + "Agent," + "Agency," + customer + "," + CAD + ",";

			productArrangement = arrangements(CREATE, productGroup, product, "", customer, step1Fields, step1Values,
					defaultStep2Fields, defaultStep2Values);


			if (productArrangement == null || productArrangement.contains(ERROR)) {
				stepActual = "Error while linking  Advisor: " + advisor2 + " to " + product + " Arrangement";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 18, enabled = true)
	@Parameters({ "productGroup", "productLine", "advisorCommissionPlan" })
	public void transferProductArrangement(final String productGroup, final String productLine,
			final String advisorCommissionPlan) {

		stepDescription = "Transfer " + productGroup + " Arrangement: " + productArrangement + " to advisor "
				+ advisor1;
		stepExpected = productGroup + "  Arrangement: " + productArrangement + " was transfered to advisor " + advisor1
				+ " successfully";

		boolean result;
		String fields;
		String values;
		String activity = "";

		if (advisor1 == null || productArrangement == null || advisor1.contains(ERROR)
				|| productArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			fields = "CUSTOMER:1," + "CURRENCY," + "EFFECTIVE.DATE,";
			values = advisor1 + "," + CAD + "," + "+0d,";
			newAdvisorArrangement = arrangements(CREATE, AGENTS, advisorCommissionPlan, "", advisor1, fields, values,
					"", "");

			switch (productLine) {
			case "Lending":
				activity = "UPDATE ACTIVITY FOR AL.COMMISSION";
				break;
			case "Deposits":
				activity = "UPDATE ACTIVITY FOR AD.COMMISSION";
				break;
			default:
			case "Accounts":
				activity = "";
				break;
			}

			fields = "AGENT.ID:1," + "AGENT.ARR.ID:1,";
			values = advisor1 + "," + newAdvisorArrangement + ",";
			result = arrangementActivity(CREATE_AUTHORISE, activity, productArrangement, productGroup, fields, values);
			if (result) {
				fields = "MEMO.TEXT,";
				values = "Transferred" + productArrangement + " Arrangement from " + advisor2 + " to " + advisor1 + ",";
				result = arrangementMemo(CREATE, productArrangement, productGroup, fields, values);
			}
			if (newAdvisorArrangement == null || newAdvisorArrangement.contains(ERROR) || !result) {
				stepActual = "Error while transferring " + productGroup + " Arrangement: " + productArrangement
						+ " to advisor " + advisor1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
