package testcases.PROC;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.DefaultVariables;

public class PROC_BB_REB_OP002 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.1";
	private String namedDepositArrangement;
	private String nomineeDepositArrangement;
	private String loanArrangement;
	private String mortgageArrangement;
	private String agentCustomer;
	private String namedCustomer;
	private String nomineeCustomer;
	private String loanCustomer;
	private String mortgageCustomer;
	private String depositBranch = "B2B Branch 607";
	private String loanBranch = "B2B Branch 817";
	private String mortgageBranch = "Branch 603";
	String namedDepositBeneficiary;
	String nomineeDepositBeneficiary;
	String loanBeneficiary;
	String mortgageBeneficiary;
	String agentBeneficiary;
	String beneficiaryFields;
	String beneficiaryValues;
	private String agentNumber;
	String creditAmount;
	private StringBuilder step2FieldsBuilder;
	private StringBuilder step2ValuesBuilder;

	@Test(priority = 1, enabled = true)
	@Parameters({ "agentProduct", "namedDepositProductGroup", "namedDepositProduct", "nomineeDepositProductGroup",
			"nomineeDepositProduct", "loanProductGroup", "loanProduct", "mortgageProductGroup", "mortgageProduct" })
	public void preCondition(final String agentProduct, final String namedDepositProductGroup,
			final String namedDepositProduct, final String nomineeDepositProductGroup,
			final String nomineeDepositProduct, final String loanProductGroup, final String loanProduct,
			final String mortgageProductGroup, final String mortgageProduct) {

		stepDescription = "Create Agent Commission Plan, Client Name GIC, nominee GIC, Loan Arrangement, Mortgage Arrangement";
		stepExpected = "Agent Commission Plan, Client Name GIC, nominee GIC, Loan Arrangement, Mortgage Arrangement created successfully";

		String agentFields;
		String agentValues;
		String benePrefProd = "CHQ";
		step2FieldsBuilder = new StringBuilder();
		step2ValuesBuilder = new StringBuilder();
		agentFields = step2FieldsBuilder.toString();
		agentValues = step2ValuesBuilder.toString();

		if (loginResult) {
			switchToBranch(depositBranch);

			agentCustomer = customer(CREATE, DEALER_ADVISOR, ROLEBASED_OR, DefaultVariables.dealerAdvisorCIFFields,
					DefaultVariables.dealerAdvisorCIFValues);

			beneficiaryFields = DefaultVariables.beneficiaryFields + "OWNING.CUSTOMER," + "BEN.CUSTOMER,";
			beneficiaryValues = DefaultVariables.beneficiaryValues + agentCustomer + ","
					+ createdCustomers.get(agentCustomer).getCustomerName() + ",";

			agentBeneficiary = beneficiaryCode(CREATE, "EFT Client", "", beneficiaryFields, beneficiaryValues);

			step2FieldsBuilder.append(
					"Advanced - Pay Out#PAYOUT.SETTLEMENT:1,Advanced - Pay Out#PAYOUT.PO.PRODUCT:1:1,Advanced - Pay Out#PAYOUT.BENEFICIARY:1:1,Advanced - Pay Out#PAYOUT.ACTIVITY:1:1,");
			agentFields = step2FieldsBuilder.toString();
			step2ValuesBuilder.append("Yes,").append(benePrefProd).append(',').append(agentBeneficiary).append(", ,");
			agentValues = step2ValuesBuilder.toString();
			agentNumber = arrangements(CREATE, AGENTS, agentProduct, "", agentCustomer,
					"CUSTOMER:1," + "CURRENCY," + "EFFECTIVE.DATE,", agentCustomer + "," + CAD + "," + "-1m,",
					agentFields, agentValues);

			if (agentNumber == null || agentNumber.contains(ERROR)) {
				stepActual = "Error while creating " + agentProduct + " Agent: " + agentNumber;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			if (!"".equals(namedDepositProductGroup) && !"".equals(namedDepositProduct)) {
				namedCustomer = customer(CREATE, PERSONAL, ROLEBASED_BANKING, DefaultVariables.personalCIFFields,
						DefaultVariables.personalCIFValues);
				namedDepositArrangement = dataGenCreateDeposit(namedCustomer, namedDepositProductGroup,
						namedDepositProduct, CAD, "", "", DefaultVariables.gicFields + "PAYMENT.FREQ:2,",
						DefaultVariables.gicValues + "e1Y e0M e0W e0D,", "Funded", "+0d", "");

				if (namedDepositArrangement == null || namedDepositArrangement.contains(ERROR)) {
					stepActual += "Error while creating " + namedDepositProduct + " Arrangement: "
							+ namedDepositArrangement;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}

			if (!"".equals(nomineeDepositProductGroup) && !"".equals(nomineeDepositProduct)) {
				nomineeCustomer = customer(CREATE, BUSINESS, ROLEBASED_BANKING,
						DefaultVariables.clientBusinessCIFFields, DefaultVariables.clientBusinessCIFValues);
				nomineeDepositArrangement = dataGenCreateDeposit(nomineeCustomer, nomineeDepositProductGroup,
						nomineeDepositProduct, CAD, "", "", DefaultVariables.gicFields, DefaultVariables.gicValues,
						"Funded", "+0d", "");
				if (nomineeDepositArrangement == null || nomineeDepositArrangement.contains(ERROR)) {
					stepActual += "Error while creating " + nomineeDepositProduct + " Arrangement: "
							+ nomineeDepositArrangement;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}

			if (!"".equals(loanProductGroup) && !"".equals(loanProduct)) {
				switchToBranch(loanBranch);

				final ArrangementData loanData = new ArrangementData("loanArrangement", mortgageProductGroup,
						mortgageProduct)
						.withDisbursement()
						.withRepayments()
						.build();

				loanArrangement = createDefaultArrangement(loanData);
				loanCustomer = loanData.getCustomers();

				if (loanArrangement == null || loanArrangement.contains(ERROR)) {
					stepActual += "Error while creating " + loanProduct + " Arrangement: " + loanArrangement;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}

			if (!"".equals(mortgageProductGroup) && !"".equals(mortgageProduct)) {
				switchToBranch(mortgageBranch);

				final ArrangementData mortgageData = new ArrangementData("mortgageArrangement", mortgageProductGroup,
						mortgageProduct)
						.withDisbursement()
						.build();

				mortgageArrangement = createDefaultArrangement(mortgageData);
				mortgageCustomer = mortgageData.getCustomers();

				if (mortgageArrangement == null || mortgageArrangement.contains(ERROR)) {
					stepActual += "Error while creating " + mortgageProduct + " Arrangement: " + mortgageArrangement;
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
	@Parameters({ "namedDepositProductGroup" })
	public void linkBeneficiaryToNameGIC(final String namedDepositProductGroup) {
		stepDescription = "Link Beneficiary To Client name GIC: " + namedDepositArrangement;
		stepExpected = "Beneficiary is linked To Client name GIC: " + namedDepositArrangement + " successfully";

		if (namedDepositArrangement == null || namedDepositArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			boolean result;
			List<WebElement> listOfPaymentTypes;
			String fields;
			String values;

			switchToBranch(depositBranch);

			beneficiaryFields = DefaultVariables.beneficiaryFields + "OWNING.CUSTOMER," + "BEN.CUSTOMER,";
			beneficiaryValues = DefaultVariables.beneficiaryValues + namedCustomer + ","
					+ createdCustomers.get(namedCustomer).getCustomerName() + ",";

			namedDepositBeneficiary = beneficiaryCode(CREATE, "EFT Client", "", beneficiaryFields, beneficiaryValues);

			arrangementActivity(OPEN, "UPDATE ACTIVITY FOR SETTLEMENT", namedDepositArrangement,
					namedDepositProductGroup, "", "");
			switchToPage(LASTPAGE, false);
			tabbedScreen.findTab("Advanced - Pay Out", "Settlement").click();
			listOfPaymentTypes = inputTable.listOfFields("PAYOUT.PPTY.CLASS", "Settlement");
			for (int i = 1; i <= listOfPaymentTypes.size(); i++) {
				if ("ACCOUNT".equals(readData("PAYOUT.PPTY.CLASS:" + i + ":1"))) {
					fields = "Advanced - Pay Out#PAYOUT.SETTLEMENT:" + i + "," + "Advanced - Pay Out#PAYOUT.PO.PRODUCT:"
							+ i + ":1," + "Advanced - Pay Out#PAYOUT.BENEFICIARY:" + i + ":1,";
					values = "Yes," + "CHQ," + "BEN1111111111,";
					result = multiInputData(fields, values, false);
				}
				if ("INTEREST".equals(readData("PAYOUT.PPTY.CLASS:" + i + ":1"))) {
					fields = "Advanced - Pay Out#PAYOUT.SETTLEMENT:" + i + "," + "Advanced - Pay Out#PAYOUT.PO.PRODUCT:"
							+ i + ":1," + "Advanced - Pay Out#PAYOUT.BENEFICIARY:" + i + ":1,";
					values = "Yes," + "ACHDEBIT," + namedDepositBeneficiary + ",";
					result = multiInputData(fields, values, false);
				}
			}

			if (toolElements.toolsButton(COMMIT_DEAL).getAttribute("src").contains("txncommit_dis")) {
				toolElements.toolsButton(VALIDATE_DEAL).click();
			}
			toolElements.toolsButton(COMMIT_DEAL).click();
			result = inputTable.verifyAcceptOverride();

			if (result) {
				result = authorizeEntity(namedDepositArrangement, ACTIVITY + "," + namedDepositProductGroup);
			} else {
				stepActual = "Error while linking Beneficiary To Client name GIC: " + namedDepositArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "nomineeDepositProductGroup" })
	public void linkBeneficiaryToNomineeGIC(final String nomineeDepositProductGroup) {
		stepDescription = "Link Beneficiary To Nominee GIC: " + nomineeDepositArrangement;
		stepExpected = "Beneficiary is linked To Nominee GIC: " + nomineeDepositArrangement + " successfully";

		if (nomineeDepositArrangement == null || nomineeDepositArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			boolean result;
			List<WebElement> listOfPaymentTypes;
			String fields;
			String values;

			switchToBranch(depositBranch);

			beneficiaryFields = DefaultVariables.beneficiaryFields + "OWNING.CUSTOMER," + "BEN.CUSTOMER,";
			beneficiaryValues = DefaultVariables.beneficiaryValues + nomineeCustomer + ","
					+ createdCustomers.get(nomineeCustomer).getCustomerName() + ",";

			nomineeDepositBeneficiary = beneficiaryCode(CREATE, "EFT Client", "", beneficiaryFields, beneficiaryValues);

			arrangementActivity(OPEN, "UPDATE ACTIVITY FOR SETTLEMENT", nomineeDepositArrangement,
					nomineeDepositProductGroup, "", "");
			switchToPage(LASTPAGE, false);
			tabbedScreen.findTab("Advanced - Pay Out", "Settlement").click();
			listOfPaymentTypes = inputTable.listOfFields("PAYOUT.PPTY.CLASS", "Settlement");
			for (int i = 1; i <= listOfPaymentTypes.size(); i++) {
				if ("ACCOUNT".equals(readData("PAYOUT.PPTY.CLASS:" + i + ":1"))) {
					fields = "Advanced - Pay Out#PAYOUT.SETTLEMENT:" + i + "," + "Advanced - Pay Out#PAYOUT.PO.PRODUCT:"
							+ i + ":1," + "Advanced - Pay Out#PAYOUT.BENEFICIARY:" + i + ":1,";
					values = "Yes," + "ACHDEBIT," + nomineeDepositBeneficiary + ",";
					result = multiInputData(fields, values, false);
				}
				if ("INTEREST".equals(readData("PAYOUT.PPTY.CLASS:" + i + ":1"))) {
					fields = "Advanced - Pay Out#PAYOUT.SETTLEMENT:" + i + "," + "Advanced - Pay Out#PAYOUT.PO.PRODUCT:"
							+ i + ":1," + "Advanced - Pay Out#PAYOUT.BENEFICIARY:" + i + ":1,";
					values = "Yes," + "ACHDEBIT," + nomineeDepositBeneficiary + ",";
					result = multiInputData(fields, values, false);
				}
			}

			if (toolElements.toolsButton(COMMIT_DEAL).getAttribute("src").contains("txncommit_dis")) {
				toolElements.toolsButton(VALIDATE_DEAL).click();
			}
			toolElements.toolsButton(COMMIT_DEAL).click();
			result = inputTable.verifyAcceptOverride();

			if (result) {
				result = authorizeEntity(nomineeDepositArrangement, ACTIVITY + "," + nomineeDepositProductGroup);
			} else {
				stepActual = "Error while linking Beneficiary To Nominee GIC: " + nomineeDepositArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters({ "loanProductGroup", "loanProduct" })
	public void linkBeneficiaryToLoan(final String loanProductGroup, final String loanProduct) {
		stepDescription = "Link Beneficiary To Loan Arrangement: " + loanArrangement;
		stepExpected = "Beneficiary is linked To Loan Arrangement: " + loanArrangement + " successfully";

		if (loanArrangement == null || loanArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			boolean result;

			switchToBranch(loanBranch);

			beneficiaryFields = DefaultVariables.beneficiaryFields + "OWNING.CUSTOMER," + "BEN.CUSTOMER,";
			beneficiaryValues = DefaultVariables.beneficiaryValues + loanCustomer + ","
					+ createdCustomers.get(loanCustomer).getCustomerName() + ",";

			loanBeneficiary = beneficiaryCode(CREATE, "EFT Client", "", beneficiaryFields, beneficiaryValues);

			String fields = "Advanced - Pay In#PAYIN.SETTLEMENT:1," + "Advanced - Pay In#PAYIN.PO.PRODUCT:1:1,"
					+ "Advanced - Pay In#PAYIN.BENEFICIARY:1:1,";
			String values = "Yes," + "ACHCREDIT," + loanBeneficiary + ",";
			result = arrangementActivity(CREATE_AUTHORISE, "UPDATE ACTIVITY FOR SETTLEMENT", loanArrangement,
					loanProductGroup, fields, values);
			if (!result) {
				stepActual = "Error while linking Beneficiary To Loan Arrangement: " + loanArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	@Parameters({ "mortgageProductGroup", "mortgageProduct" })
	public void linkBeneficiaryToMortgage(final String mortgageProductGroup, final String mortgageProduct) {
		stepDescription = "Link Beneficiary To Mortgage Arrangement: " + mortgageArrangement;
		stepExpected = "Beneficiary is linked To Mortgage Arrangement: " + mortgageArrangement + " successfully";

		if (mortgageArrangement == null || mortgageArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			boolean result;
			switchToBranch(mortgageBranch);

			beneficiaryFields = DefaultVariables.beneficiaryFields + "OWNING.CUSTOMER," + "BEN.CUSTOMER,";
			beneficiaryValues = DefaultVariables.beneficiaryValues + mortgageCustomer + ","
					+ createdCustomers.get(mortgageCustomer).getCustomerName() + ",";

			mortgageBeneficiary = beneficiaryCode(CREATE, "EFT Client", "", beneficiaryFields, beneficiaryValues);

			String fields = "Advanced - Pay In#PAYIN.SETTLEMENT:1," + "Advanced - Pay In#PAYIN.PO.PRODUCT:1:1,"
					+ "Advanced - Pay In#PAYIN.BENEFICIARY:1:1,";
			String values = "Yes," + "ACHCREDIT," + mortgageBeneficiary + ",";
			result = arrangementActivity(CREATE_AUTHORISE, "UPDATE ACTIVITY FOR SETTLEMENT", mortgageArrangement,
					mortgageProductGroup, fields, values);

			if (!result) {
				stepActual = "Error while linking Beneficiary To Mortgage Arrangement: " + mortgageArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	public void linkBeneficiaryToEscrowPayee() {
		stepDescription = "Setup Tax Escrow for mortgage: " + mortgageArrangement;
		stepExpected = "Tax Escrow for mortgage: " + mortgageArrangement + " is steup successfully";

		if (mortgageArrangement == null || mortgageArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			boolean result;
			String fields;
			String values;

			fields = "Setup#SETTLEMENT.TYPE," + "Setup#BENEFICIARY," + "SETTLEMENT.TYPE,";
			values = "ESCROWDR," + mortgageBeneficiary + "," + "CHQ,";
			result = escrowPayee(EDIT, fields, values, "200000009");

			if (!result) {
				stepActual = "Error while settingup Tax Escrow for mortgage: " + mortgageArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 7, enabled = true)
	public void linkBeneficiaryToAgent() {
		stepDescription = "Link Beneficiary To Agent: " + agentNumber;
		stepExpected = "Beneficiary To Agent: " + agentNumber + " is linked successfully";

		if (agentNumber == null || agentNumber.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			boolean result;
			String fields;
			String values;

			switchToBranch(depositBranch);

			fields = "Commission Payout Schedule#COMM.SCHEDULE," + "Commission Payout Schedule#PAYMENT.PRODUCT,"
					+ "Commission Payout Schedule#PO.BENEFICIARY,";
			values = "M0101," + "CHQCOM," + agentBeneficiary + ",";
			result = arrangementActivity(CREATE_AUTHORISE, "UPDATE ACTIVITY FOR PRODUCT.COMMISSION", agentNumber,
					AGENTS, fields, values);

			if (!result) {
				stepActual = "Error while linking Beneficiary To Agent: " + agentNumber;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 8, enabled = true)
	public void modifyBeneficiary() {
		stepDescription = "Change Existing Beneficiary ID Record for customer: " + loanCustomer;
		stepExpected = "Existing Beneficiary ID Record for customer: " + loanCustomer + " is changed successfully";

		if (loanBeneficiary == null || loanCustomer == null || loanBeneficiary.contains(ERROR)
				|| loanCustomer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			String result;
			String fields;
			String values;

			switchToBranch(loanBranch);

			fields = "BEN.CUSTOMER,";
			values = "Test Beneficiary,";
			result = beneficiaryCode(AMEND, "EFT Client", loanBeneficiary, fields, values);

			if (!loanBeneficiary.equals(result)) {
				stepActual = "Error while changing Existing Beneficiary ID Record for customer: " + loanCustomer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 9, enabled = true)
	public void removeBeneficiary() {
		stepDescription = "Remove Beneficiary ID Record for customer: " + loanCustomer;
		stepExpected = "Beneficiary ID Record for customer: " + loanCustomer + " is removed successfully";

		if (loanBeneficiary == null || loanCustomer == null || loanBeneficiary.contains(ERROR)
				|| loanCustomer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			boolean result;
			String message;

			switchToBranch(loanBranch);

			loanBeneficiary = findBeneficiaryCode(loanCustomer, PERSONAL, "EFT Client", ROLEBASED_LENDING);
			versionScreen.activityAction(loanBeneficiary, "", "Delete Beneficiary").click();
			switchToPage(LASTPAGE, false);
			toolElements.toolsButton(REVERSE_DEAL).click();
			switchToPage(LASTPAGE, false);
			message = readTable.message().getText();
			if (message.contains(TXN_COMPLETE)) {
				result = authorizeEntity(loanBeneficiary, BENEFICIARY);
			} else {
				Reporter.log("Error while amending beneficiary: " + loanBeneficiary, true);
				result = false;
			}

			if (!result) {
				stepActual = "Error while removing Beneficiary ID Record for customer: " + loanCustomer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
