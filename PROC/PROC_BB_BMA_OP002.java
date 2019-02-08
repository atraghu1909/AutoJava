package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;

public class PROC_BB_BMA_OP002 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 4.0";
	private String customer;
	private String mainArrangement;
	private String dischargeRequest;
	private String activityFields;
	private String activityValues;
	private String closureFields;
	private String closureValues;
	boolean result = false;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "customerType", "product" })
	public void preCondition(@Optional("B2B Branch 623") final String branch,
			@Optional(PERSONAL) final String customerType, final String product) {

		stepDescription = "Create a " + customerType + " customer, " + product
				+ " arrangement and perform activities for that arrangement";
		stepExpected = customerType + " customer, " + product
				+ " arrangement created and activities for that arrangement performed successfully";

		if (loginResult) {

			switchToBranch(branch);

			final ArrangementData mortgageData = new ArrangementData("mortgageArrangement", RETAIL_MORTGAGES, product)
					.withEffectiveDate("-1m")
					.withDisbursement()
					.withRepayments()
					.build();

			mainArrangement = createDefaultArrangement(mortgageData);
			customer = mortgageData.getCustomers();

			if (customer == null || mainArrangement == null || customer.contains(ERROR)
					|| mainArrangement.contains(ERROR)) {
				stepActual = "Error while creating " + product + " arrangement";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {

				activityFields = "Cashback fee$FIXED.AMOUNT,";
				activityValues = "1000,";

				result = arrangementActivity(CREATE, "CHANGE ACTIVITY FOR ALCASHBACK", mainArrangement,
						RETAIL_MORTGAGES, activityFields, activityValues);

				if (!result) {
					stepActual = "Error while performing CHANGE ACTIVITY FOR ALCASHBACK activity for arrangement "
							+ mainArrangement;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

				activityFields = "Prepayment Penalty$L.PREP.PRVLG," + "Prepayment Penalty$L.PRVLG.DESC,";
				activityValues = "YES," + "15,";

				result = arrangementActivity(CREATE, "CHANGE ACTIVITY FOR ALPREPAYPENALTY", mainArrangement,
						RETAIL_MORTGAGES, activityFields, activityValues);

				if (!result) {
					stepActual = "Error while performing CHANGE ACTIVITY FOR ALPREPAYPENALTY activity for arrangement "
							+ mainArrangement;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

				closureFields = "EFFECTIVE.DATE," + "CLOSURE.REASON,";
				closureValues = "+1d," + "Other,";

				dischargeRequest = generateStatement("Discharge stmt", customer, RETAIL_MORTGAGES, product,
						closureFields, closureValues);

				if (dischargeRequest == null || dischargeRequest.contains(ERROR)) {
					stepActual = "Error while generating Discharge Statement";
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
	public void unfreezeLoan() {

		stepDescription = "Unfreeze Loan for customer";
		stepExpected = "Loan for customer Unfreezed successfully";

		if (customer == null || mainArrangement == null || customer.contains(ERROR)
				|| mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			commandLine("LBC.H.DISCHARGE.REQUEST,LBC.MODIFY", commandLineAvailable);
			compositeScreen.inputField().clear();
			compositeScreen.inputField().sendKeys(dischargeRequest);
			toolElements.toolsButton(EDIT_CONTRACT).click();
			switchToPage(LASTPAGE, false);
			result = inputData("LOAN.FREEZE.FLAG", "NO", false);
			result = inputTable.commitAndOverride();

			if (!result) {
				stepActual = "Error while Unfreezing Loan for customer";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	public void reverseActivities() {

		stepDescription = "Reverse activities for arrangement " + mainArrangement;
		stepExpected = "Activities for arrangement " + mainArrangement + " reversed successfully";

		if (customer == null || mainArrangement == null || customer.contains(ERROR)
				|| mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementActivity(REVERSE, "CHANGE ACTIVITY FOR ALCASHBACK", mainArrangement, RETAIL_MORTGAGES,
					"", "");

			if (!result) {
				stepActual = "Error while reversing CHANGE ACTIVITY FOR ALCASHBACK activity for arrangement "
						+ mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			result = arrangementActivity(REVERSE, "CHANGE ACTIVITY FOR ALPREPAYPENALTY", mainArrangement,
					RETAIL_MORTGAGES, "", "");

			if (!result) {
				stepActual = "Error while reversing CHANGE ACTIVITY FOR ALPREPAYPENALTY activity for arrangement "
						+ mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
