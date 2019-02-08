package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.DefaultVariables;

public class PROC_OP_MG_XX011 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.9";
	private String customer;
	private String[] arrangements;
	private ArrangementData arrangementsData;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "customerType", "product" })
	public void preCondition(@Optional("B2B Branch 623") final String branch,
			@Optional(PERSONAL) final String customerType, final String product) {

		stepDescription = "Create customer and two current " + product + " arrangements";
		stepExpected = "Customer and two current " + product + " arrangements created successfully";

		int totalArrangements = 2;
		boolean localResult = true;

		if (loginResult) {
			switchToBranch(branch);

			customer = createDefaultCustomer(customerType, RETAIL_MORTGAGES, ROLEBASED_LENDING);
			if (customer == null || customer.contains(ERROR)) {
				stepActual = "Error while creating " + customerType + " customer";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			arrangements = new String[totalArrangements];

			for (int i = 0; i < totalArrangements && localResult; i++) {
				arrangementsData = new ArrangementData("arrangement" + i, RETAIL_MORTGAGES, product)
						.withCustomers(customer, createdCustomers.get(customer), "", "100,", "100,")
						.withLimit("", "NEW", "No").withEffectiveDate("-1m").withDisbursement().withRepayments()
						.build();
				arrangements[i] = createDefaultArrangement(arrangementsData);
				localResult = !arrangements[i].contains(ERROR);
				if (arrangements[i] == null || arrangements[i].contains(ERROR)) {
					stepActual = "Error while creating " + product + " arrangement";
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
	public void issueDeclineLetter() {

		stepDescription = "Issue Decline Letter for arrangement: " + arrangements[0];
		stepExpected = "Decline Letter for arrangement: " + arrangements[0] + " issued successfully";

		String fields = "INITIATION.TYPE,";
		String values = "Manual,";
		boolean result;

		if (arrangements[0] == null || arrangements[0].contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			result = arrangementActivity("Create Scheduled +11m", "UPDATE ACTIVITY FOR AL.RENEWAL", arrangements[0],
					RETAIL_MORTGAGES, fields, values);
			if (!result) {
				stepActual = "Error while performing UPDATE ACTIVITY FOR AL.RENEWAL activity for arrangement: "
						+ arrangements[0];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			fields = "Advanced - Pay In#PAYIN.SETTLEMENT:1";
			values = "No";

			result = arrangementActivity("Create Scheduled +11m", "UPDATE ACTIVITY FOR SETTLEMENT", arrangements[0],
					RETAIL_MORTGAGES, fields, values);
			if (!result) {
				stepActual = "Error while performing UPDATE ACTIVITY FOR SETTLEMENT activity for arrangement: "
						+ arrangements[0];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			fields = "CA.NPL," + "CA.NAB," + "CA.NPL.REASON,";
			values = "Y," + "Y," + "Market Deterioration,";

			result = arrangementActivity(CREATE, "Update Risk Details", arrangements[0], RETAIL_MORTGAGES, fields,
					values);
			if (!result) {
				stepActual = "Error while updating Risk Details for arrangement: " + arrangements[0];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			fields = "DELIVERY.REQD:1,";
			values = " ,";

			result = arrangementActivity(CREATE, "UPDATE ACTIVITY FOR CUSTOMER", arrangements[0], RETAIL_MORTGAGES,
					fields, values);
			if (!result) {
				stepActual = "Error while declining delivery option for arrangement: " + arrangements[0];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters("renewalProduct")
	public void adHocRenewalAgreement(final String renewalProduct) {

		stepDescription = "Perform renewal processing activity for arrangement: " + arrangements[1];
		stepExpected = "Renewal processing activity for arrangement: " + arrangements[1] + " performed successfully";

		boolean result;
		String renewalProductType = DefaultVariables.productCodes.get(renewalProduct);
		String fields = "PRODUCT," + "EFFECTIVE.DATE,";
		String values = renewalProductType + "," + "+11m,";

		if (arrangements[1] == null || arrangements[1].contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			activitySimulations("Open Simulate", "Renewal Processing", arrangements[1], RETAIL_MORTGAGES, "", "");
			multiInputData(fields, values, false);
			toolElements.toolsButton(VALIDATE_DEAL).click();

			fields = "CHANGE.DATE.TYPE," + "CHANGE.PERIOD," + "Principal Interest$MOS Interface#L.POSTED.RATE,"
					+ "Schedule$ACTUAL.AMT:1:1,";
			values = "Period," + "5Y," + "4.99," + " ,";
			result = multiInputData(fields, values, true);
			result = inputTable.commitAndOverride();

			if (!result) {
				stepActual = "Error while performing renewal processing activity for arrangement: " + arrangements[1];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

}
