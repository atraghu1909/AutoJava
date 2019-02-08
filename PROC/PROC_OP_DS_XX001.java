package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PROC_OP_DS_XX001 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version: 3.2";
	private String poa;
	private String mainCustomer;
	private String agent;
	private String customerBeneficiary;
	private String agentBeneficiary;
	private String agentArrangement;
	private String gicArrangement;
	private String payInAccount;
	private String beneficiaryFields;
	private String beneficiaryValues;
	private String scheduleFields;
	private String scheduleValues;
	private String settlementPayInFields;
	private String settlementPayInValues;
	private String settlementPayOutFields;
	private String settlementPayOutValues;
	private String step1Fields;
	private String step1Values;
	private String step2Fields;
	private String step2Values;

	@Test(priority = 1, enabled = true)
	@Parameters({ "customerType", "branch", "productGroup", "product", "ownerType", "fundingSource" })
	public void preCondition(final String customerType, @Optional("B2B Branch 607") final String branch,
			final String productGroup, final String product, @Optional("Client Owned") final String ownerType,
			@Optional("Cheque") final String fundingSource) {

		stepDescription = "Create one Non-Client Personal customer, another customer based on ownerType and Beneficiary for that customer";
		stepExpected = "A Non-Client Personal customer, another customer based on ownerType and Beneficiary for that customer created successfully";

		if (loginResult) {
			switchToBranch(branch);

			poa = createDefaultCustomer(NON_CLIENT_PERSONAL, productGroup, ROLEBASED_BANKING);

			if (poa == null || poa.contains(ERROR)) {
				stepActual = "Error while creating Non-Client Personal customer";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			if ("Nominee".equals(ownerType)) {
				mainCustomer = createNominee("+0d");
				customerBeneficiary = findBeneficiaryCode(mainCustomer, BUSINESS, "EFT Client", ROLEBASED_SAE);
			} else {
				mainCustomer = createDefaultCustomer(customerType, productGroup, ROLEBASED_BANKING);

				beneficiaryFields = DefaultVariables.beneficiaryFields + "OWNING.CUSTOMER," + "BEN.CUSTOMER,";
				beneficiaryValues = DefaultVariables.beneficiaryValues + mainCustomer + ","
						+ createdCustomers.get(mainCustomer).getCustomerName() + ",";

				customerBeneficiary = beneficiaryCode(CREATE, "EFT Client", "", beneficiaryFields, beneficiaryValues);
				if (productGroup.contains("Broker")) {
					agent = customer(CREATE, DEALER_ADVISOR, ROLEBASED_OR, DefaultVariables.dealerAdvisorCIFFields,
							DefaultVariables.dealerAdvisorCIFValues);

					beneficiaryFields = DefaultVariables.beneficiaryFields + "OWNING.CUSTOMER," + "BEN.CUSTOMER,";
					beneficiaryValues = DefaultVariables.beneficiaryValues + agent + ","
							+ createdCustomers.get(agent).getCustomerName() + ",";

					agentBeneficiary = beneficiaryCode(CREATE, "EFT Client", "", beneficiaryFields, beneficiaryValues);

					final String fields = "Commission Payout Schedule#PO.BENEFICIARY,";
					final String values = agentBeneficiary + ",";

					agentArrangement = arrangements(CREATE, AGENTS, "Deposit Commission Plan", "", agent,
							"CUSTOMER:1," + "CURRENCY," + "EFFECTIVE.DATE,", agent + "," + CAD + "," + "+0d,", fields,
							values);
				}
			}

			if (mainCustomer == null || customerBeneficiary == null || mainCustomer.contains(ERROR)
					|| customerBeneficiary.contains(ERROR)) {
				stepActual = "Error while creating a customer based on ownerType and Beneficiary for that customer";
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
	@Parameters({ "branch", "productGroup", "product", "ownerType", "fundingSource" })
	public void processNonRegisteredGICPurchase(@Optional("B2B Branch 607") final String branch,
			final String productGroup, final String product, @Optional("Client Owned") final String ownerType,
			@Optional("Cheque") final String fundingSource) {

		stepDescription = "Process Non-Registered GIC Purchase";
		stepExpected = "Non-Registered GIC Purchase processed successfully";

		final String branchNumericValue = branch.replaceAll("[^0-9]", "");

		if (mainCustomer == null || customerBeneficiary == null || mainCustomer.contains(ERROR)
				|| customerBeneficiary.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			if (product.contains("Compound") || product.contains("at Maturity")) {
				scheduleFields = "Schedule$PAYMENT.METHOD:1," + "Schedule$START.DATE:1:1,";
				scheduleValues = "Pay," + "R_RENEWAL +,";
			} else if (product.contains("Simple")) {
				scheduleFields = "";
				scheduleValues = "";
			} else {
				scheduleFields = "Schedule$PAYMENT.METHOD:1," + "Schedule$START.DATE:1:1,";
				scheduleValues = "Capitalise," + "R_RENEWAL +,";
			}

			if ("Cheque".equals(fundingSource) && "B2B Branch 607".equals(branch)) {
				payInAccount = "CAD1100100017607";
			} else if ("Cheque".equals(fundingSource)) {
				payInAccount = "CAD1100100011247";
			} else if ("RBC Account".equals(fundingSource) && "B2B Branch 607".equals(branch)) {
				payInAccount = "CAD1160000017607";
			} else if ("RBC Account".equals(fundingSource)) {
				payInAccount = "CAD1160000011247";
			} else if ("TD Account".equals(fundingSource) && "B2B Branch 607".equals(branch)) {
				payInAccount = "CAD1160100017607";
			} else if ("TD Account".equals(fundingSource)) {
				payInAccount = "CAD1160100011247";
			} else if ("Nominee".equals(ownerType) && "VMBL".equals(fundingSource) && "B2B Branch 607".equals(branch)) {
				payInAccount = "CAD1120000017607";
			} else if ("Nominee".equals(ownerType) && "VMBL".equals(fundingSource)) {
				payInAccount = "CAD1120000011247";
			} else if ("Nominee".equals(ownerType) && "Cannex".equals(fundingSource)) {
				payInAccount = "CAD11602";
			} else {
				payInAccount = "CAD11102";
			}

			settlementPayInFields = "Settlement$Advanced - Pay In#PAYIN.SETTLEMENT:1,"
					+ "Settlement$Advanced - Pay In#PAYIN.ACCOUNT:1:1,";
			settlementPayInValues = "Yes," + payInAccount + ",";

			if (productGroup.contains("Retail")) {
				settlementPayOutFields = "Settlement$Advanced - Pay Out#PAYOUT.SETTLEMENT:1,"
						+ "Settlement$Advanced - Pay Out#PAYOUT.ACCOUNT:1:1,";
				settlementPayOutValues = "Yes," + "CAD1100000011539,";
			} else if ("Nominee".equals(ownerType) && "VMBL".equals(fundingSource)) {
				settlementPayOutFields = "Settlement$Advanced - Pay Out#PAYOUT.SETTLEMENT:1,"
						+ "Settlement$Advanced - Pay Out#PAYOUT.ACCOUNT:1:1,";
				settlementPayOutValues = "Yes," + payInAccount + ",";
			} else {
				settlementPayOutFields = "Settlement$Advanced - Pay Out#PAYOUT.SETTLEMENT:1,"
						+ "Settlement$Advanced - Pay Out#PAYOUT.PO.PRODUCT:1:1,"
						+ "Settlement$Advanced - Pay Out#PAYOUT.BENEFICIARY:1:1,"
						+ "Settlement$Advanced - Pay Out#PAYOUT.SETTLEMENT:2,"
						+ "Settlement$Advanced - Pay Out#PAYOUT.PO.PRODUCT:2:1,"
						+ "Settlement$Advanced - Pay Out#PAYOUT.BENEFICIARY:2:1,";
				settlementPayOutValues = "Yes," + "ACHDEBIT," + customerBeneficiary + ",Yes," + "ACHDEBIT,"
						+ customerBeneficiary + ",";
			}

			step1Fields = "CUSTOMER:1," + "CUSTOMER.ROLE:1," + "CUSTOMER+:2," + "CUSTOMER.ROLE:2," + "CURRENCY,";
			step1Values = mainCustomer + ",BORROWER.OWNER," + poa + ",POA," + CAD + ",";

			if ("Client Owned".equals(ownerType) && productGroup.contains("Broker")) {
				step1Fields = step1Fields + "AGENT.ID:1," + "AGENT.ARR.ID:1," + "AGENT.ROLE:1,";
				step1Values = step1Values + agent + "," + agentArrangement + ",Broker,";
			}

			step2Fields = DefaultVariables.gicFields + "Customer$TAX.LIABILITY.PERC:2," + "Customer$LIMIT.ALLOC.PERC:2,"
					+ "Customer$GL.ALLOC.PERC:2," + settlementPayInFields + settlementPayOutFields;
			step2Values = DefaultVariables.gicValues + "0," + "0," + "0," + settlementPayInValues
					+ settlementPayOutValues;

			if (productGroup.contains("Retail")) {
				step2Fields = step2Fields + "Account$Default Values#L.BOB,";
				step2Values = step2Values + branchNumericValue + ",";
			}

			if ("Nominee".equals(ownerType)) {
				step2Fields = step2Fields + "Commitment$SECURITY.NO:1," + "Commitment$NAME:1,";
				step2Values = step2Values + "123456789," + "Test Name,";
			}

			if (!"".equals(scheduleFields)) {
				step2Fields = step2Fields + scheduleFields;
				step2Values = step2Values + scheduleValues;
			}

			gicArrangement = arrangements(CREATE, productGroup, product, ROLEBASED_BANKING, mainCustomer, step1Fields,
					step1Values, step2Fields, step2Values);

			if (gicArrangement == null || gicArrangement.contains(ERROR)) {
				stepActual = "Error while processing Non-Registered GIC Purchase";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}