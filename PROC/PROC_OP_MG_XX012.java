package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.DefaultVariables;

public class PROC_OP_MG_XX012 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.1";
	private ArrangementData arrangementsData;
	private String[] arrangements;
	private String customer;
	private String adHocSimulation;
	private String activityFields;
	private String activityValues;
	private String renewalProductCode;
	boolean result = false;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "customerType", "product", "renewalProduct" })
	public void preCondition(@Optional("B2B Branch 623") final String branch,
			@Optional(PERSONAL) final String customerType, final String product, final String renewalProduct) {

		stepDescription = "Create " + customerType + " customer, three " + product
				+ " arrangements and issue an adHoc Renewal Agreement for one of the arrangements";
		stepExpected = customerType + " customer, three " + product
				+ " arrangements created and an adHoc Renewal Agreement for one of the arrangements issued successfully";

		final String fields;
		final String values;
		int totalArrangements = 3;
		boolean localResult = true;

		if (loginResult) {
			switchToBranch(branch);

			customer = createDefaultCustomer(customerType, RETAIL_MORTGAGES, ROLEBASED_LENDING);

			renewalProductCode = DefaultVariables.productCodes.get(renewalProduct);

			arrangements = new String[totalArrangements];

			for (int i = 0; i < totalArrangements && localResult; i++) {

				arrangementsData = new ArrangementData("allArrangements", RETAIL_MORTGAGES, product)
						.withCustomers(customer, createdCustomers.get(customer), "", "100,", "100,")
						.withLimit("", "NEW", "No").withEffectiveDate("-1m").withDisbursement().withRepayments()
						.build();

				arrangements[i] = createDefaultArrangement(arrangementsData);
				localResult = !arrangements[i].contains(ERROR);
			}

			if (customer == null || customer.contains(ERROR) || !localResult) {
				stepActual = "Error while creating a " + customerType + " customer and three " + product
						+ " arrangements";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {

				activityFields = "PRODUCT," + "EFFECTIVE.DATE,";
				activityValues = renewalProductCode + ",+11m,";

				adHocSimulation = activitySimulations(OPEN + " Simulate", "Renewal Processing", arrangements[1],
						RETAIL_MORTGAGES, activityFields, activityValues);
				multiInputData(activityFields, activityValues, false);
				toolElements.toolsButton(VALIDATE_DEAL).click();

				fields = "Lending Renewal$CHANGE.DATE.TYPE," + "Lending Renewal$CHANGE.PERIOD,"
						+ "Principal Interest$MOS Interface#L.POSTED.RATE," + "Schedule$ACTUAL.AMT:1:1,"
						+ "Settlement$Basic#PAYIN.SETTLEMENT:1,";
				values = "Period," + "5Y," + "4.99," + " " + ",No,";

				switchToPage(LASTPAGE, false);
				multiInputData(fields, values, false);
				result = inputTable.commitAndOverride();

				if (result) {
					switchToPage("Sim Runner Status", false);
					adHocSimulation = readTable.elementValue("ENQ-H-ID_AASIMULATIONMONITOR").getText();
				}

				if (adHocSimulation == null || adHocSimulation.contains(ERROR)) {
					stepActual = "Error while issuing an adHoc Renewal Agreement for arrangement " + arrangements[1];
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
	@Parameters({ "renewalProduct" })
	public void renewalAgreementReceived(final String renewalProduct) {

		stepDescription = "Perform Renewal Processing activity for arrangement " + arrangements[0]
				+ " to receive renewal agreement";
		stepExpected = "Renewal Processing activity for arrangement " + arrangements[0]
				+ " performed and renewal agreement received successfully";

		final String fields;
		final String values;

		if (arrangements[0] == null || arrangements[0].contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			activityFields = "PRODUCT," + "EFFECTIVE.DATE,";
			activityValues = renewalProductCode + ",+11m,";

			result = arrangementActivity(OPEN, "Renewal Processing", arrangements[0], RETAIL_MORTGAGES, activityFields,
					activityValues);
			multiInputData(activityFields, activityValues, false);
			toolElements.toolsButton(VALIDATE_DEAL).click();

			fields = "Lending Renewal$CHANGE.DATE.TYPE," + "Lending Renewal$CHANGE.PERIOD,"
					+ "Principal Interest$MOS Interface#L.POSTED.RATE," + "Schedule$ACTUAL.AMT:1:1,"
					+ "Settlement$Basic#PAYIN.SETTLEMENT:1,";
			values = "Period," + "5Y," + "4.99," + " " + ",No,";

			switchToPage(LASTPAGE, false);
			multiInputData(fields, values, false);
			result = inputTable.commitAndOverride();

			if (!result) {
				stepActual = "Error while Performing Renewal Processing activity for arrangement " + arrangements[0]
						+ " to receive renewal agreement";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	public void renegotiationsAndCorrections() {

		stepDescription = "Perform Renegotiate Activity With Payment for arrangement " + arrangements[0];
		stepExpected = "Renegotiate Activity With Payment for arrangement " + arrangements[0]
				+ " performed successfully";

		if (arrangements[0] == null || arrangements[0].contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			activityFields = "Principal Interest$Periodic#MARGIN.TYPE:1:1,"
					+ "Principal Interest$Periodic#MARGIN.OPER:1:1," + "Principal Interest$Periodic#MARGIN.RATE:1:1,";
			activityValues = "Single Margin," + "Add," + "3.00,";

			result = arrangementActivity(CREATE, "RENEGOTIATE ACTIVITY WITH PAYMENT", arrangements[0], RETAIL_MORTGAGES,
					activityFields, activityValues);

			if (!result) {
				stepActual = "Error while Performing Renegotiate Activity With Payment for arrangement "
						+ arrangements[0];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	public void addFeeForCommercialRenewal() {

		stepDescription = "Perform an activity to add fee for commercial renewal for arrangement " + arrangements[0];
		stepExpected = "An activity to add fee for commercial renewal for arrangement " + arrangements[0]
				+ " performed successfully";

		if (arrangements[0] == null || arrangements[0].contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			activityFields = "Renewal Fee$FIXED.AMOUNT,";
			activityValues = "100.00,";

			result = arrangementActivity(CREATE, "CHANGE ACTIVITY FOR ALRENEWAL", arrangements[0], RETAIL_MORTGAGES,
					activityFields, activityValues);

			if (!result) {
				stepActual = "Error while Performing an activity to add fee for commercial renewal for arrangement "
						+ arrangements[0];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	public void manuallyGeneratedAgreementReceived() {

		stepDescription = "Apply an issued adHoc Renewal Agreement " + adHocSimulation + " to arrangement "
				+ arrangements[1];
		stepExpected = "An issued adHoc Renewal Agreement applied successfully";

		final String appliedSimulation;

		if (arrangements[1] == null || arrangements[1].contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			appliedSimulation = activitySimulations("Apply", adHocSimulation, arrangements[1], RETAIL_MORTGAGES, "",
					"");

			if (!appliedSimulation.equals(adHocSimulation)) {
				stepActual = "Error while Applying an issued adHoc Renewal Agreement " + adHocSimulation
						+ " to arrangement " + arrangements[1];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	public void stopAutoRenewal() {

		stepDescription = "Perform activities to stop auto renewal for arrangement " + arrangements[2];
		stepExpected = "Activities to stop auto renewal for arrangement " + arrangements[2] + " performed successfully";

		if (arrangements[2] == null || arrangements[2].contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			activityFields = "Lending Renewal$INITIATION.TYPE,";
			activityValues = "Manual,";

			result = arrangementActivity(CREATE, "UPDATE ACTIVITY FOR AL.RENEWAL", arrangements[2], RETAIL_MORTGAGES,
					activityFields, activityValues);

			if (!result) {
				stepActual = "Error while Performing an activity UPDATE ACTIVITY FOR AL.RENEWAL for arrangement "
						+ arrangements[2];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			activityFields = "Settlement$Advanced - Pay In#PAYIN.SETTLEMENT:1,";
			activityValues = "No,";

			result = arrangementActivity("Create Scheduled +11m", "UPDATE ACTIVITY FOR SETTLEMENT", arrangements[2],
					RETAIL_MORTGAGES, activityFields, activityValues);

			if (!result) {
				stepActual = "Error while Performing an activity UPDATE ACTIVITY FOR SETTLEMENT for arrangement "
						+ arrangements[2];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			activityFields = "Account$CA.NPL," + "Account$CA.NAB," + "Account$CA.NPL.REASON,";
			activityValues = "Y," + "Y," + "Market Deterioration,";

			result = arrangementActivity("Create Scheduled +11m", "Update Risk Details", arrangements[2],
					RETAIL_MORTGAGES, activityFields, activityValues);

			if (!result) {
				stepActual = "Error while Performing an activity Update Risk Details for arrangement "
						+ arrangements[2];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			activityFields = "Customer$Customer#DELIVERY.REQD:1,";
			activityValues = " ,";

			result = arrangementActivity(CREATE, "UPDATE ACTIVITY FOR CUSTOMER", arrangements[2], RETAIL_MORTGAGES,
					activityFields, activityValues);

			if (!result) {
				stepActual = "Error while Performing an activity UPDATE ACTIVITY FOR CUSTOMER for arrangement "
						+ arrangements[2];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}