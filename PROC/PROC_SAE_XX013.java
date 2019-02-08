package testcases.PROC;

import java.util.Arrays;

import org.openqa.selenium.support.ui.Select;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;
import testLibs.RepositoryPaths;

public class PROC_SAE_XX013 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "2018-06-12";
	private String customer;
	private String mainArrangement;
	private String simulation1;
	private String fields;
	private String values;
	private String simulation2;
	private String simulatedLoan = "";
	private String simulationResult;
	private boolean result;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "productGroup", "product", "customerType" })
	public void preCondition(@Optional("LAURENTIAN BANK - 523") final String branch, final String productGroup,
			final String product, final String customerType) {

		stepDescription = "Create " + customerType + " customer and " + product + " arrangement";
		stepExpected = customerType + " customer and " + product + " arrangement created successfully";

		if (loginResult) {

			switchToBranch(branch);

			customer = createDefaultCustomer(customerType, productGroup, ROLEBASED_SAE);

			if (customer == null || customer.contains(ERROR)) {
				stepActual = "Error while creating " + customerType + " client";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {

				mainArrangement = createDefaultArrangement(productGroup, product, ROLEBASED_SAE, customer, "+0d");

			}

			if (mainArrangement == null || mainArrangement.contains(ERROR)) {
				stepActual = "Error while creating " + product + " arrangement";
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
	public void simulateActivity(final String productGroup) {

		stepDescription = "Simulate activity for principal interest";
		stepExpected = "Principal interest Activity Simulated successfully";

		if (customer == null || mainArrangement == null || customer.contains(ERROR)
				|| mainArrangement.contains(ERROR)) {

			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as preconditon failed";
			throw new SkipException(stepActual);

		} else {

			fields = "Fixed/Floating/Linked#FIXED.RATE:1,";
			values = "4.25,";

			simulation1 = activitySimulations(CREATE + " Simulate +5d", "CHANGE ACTIVITY FOR PRINCIPALINT", mainArrangement,
					productGroup, fields, values);

			if (simulation1 == null || simulation1.contains(ERROR)) {
				stepActual = "Error while simulating principal interest activity ";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "productGroup" })
	public void viewSimulatedActivity(final String productGroup) {

		stepDescription = "View simulated activity";
		stepExpected = "Simulated activity viewed successfully";

		if (simulation1 == null || simulation1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as preconditon failed";
			throw new SkipException(stepActual);
		} else {

			simulationResult = activitySimulations("View", simulation1, mainArrangement, productGroup, "", "");

			if (simulationResult == null || simulationResult.contains(ERROR)) {
				stepActual = "Error while viewing simulated activity ";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}

		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters({ "productGroup" })
	public void modifySimulatedActivity(final String productGroup) {

		stepDescription = "Modify simulated activity";
		stepExpected = "Simulated activity modified successfully";

		if (simulation1 == null || simulation1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as preconditon failed";
			throw new SkipException(stepActual);
		} else {

			fields = "Fixed/Floating/Linked#FIXED.RATE:1,";
			values = "5.25,";

			simulationResult = activitySimulations("Modify", simulation1, mainArrangement, productGroup, "", "");

			if (simulationResult == null || simulationResult.contains(ERROR)) {
				stepActual = "Error while modifying simulated activity ";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}

		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void SimulateNewLoan(final String productGroup, final String product) {

		String defaultStep2Fields;
		String defaultStep2Values;
		String parentLimitIdSAE;
		String completeLimitId;
		String limitReference;
		String limitFields;
		String limitSerial;
		String limitValues;

		stepDescription = "Simulate new " + product + " arrangement";
		stepExpected = "New " + product + " arrangement simulated successfully";

		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as preconditon failed";
			throw new SkipException(stepActual);
		} else {
			limitSerial = "02";
			limitFields = DefaultVariables.productLimitFields.get(product) + "EXPIRY.DATE,";
			limitValues = DefaultVariables.productLimitValues.get(product) + "5Y,";

			parentLimitIdSAE = customerLimit(CREATE_AUTHORISE, RepositoryPaths.SECURED, ROLEBASED_SAE, product,
					customer, "2400", limitSerial, limitFields, limitValues, false);

			completeLimitId = customerLimit(CREATE_AUTHORISE, RepositoryPaths.SECURED, "", product, customer, "2404",
					limitSerial, limitFields, limitValues);
			limitReference = completeLimitId.substring(13, 17);

			defaultStep2Fields = DefaultVariables.productGroupFields.get(productGroup) + "LIMIT.REFERENCE,"
					+ "LIMIT.SERIAL," + "Escrow Debit Interest$FIXED.RATE:1,";
			defaultStep2Values = DefaultVariables.productGroupValues.get(productGroup) + limitReference + ","
					+ limitSerial + "," + "6.25,";

			simulatedLoan = arrangementSimulations("Simulate", customer, productGroup, product, ROLEBASED_SAE, "+5d",
					defaultStep2Fields, defaultStep2Values);

			if (simulatedLoan == null || simulatedLoan.contains(ERROR)) {
				stepActual = "Error while simulating " + product + " arrangement ";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void ViewSimulatedLoan(final String productGroup, final String product) {

		stepDescription = "View simulated " + product + " arrangement";
		stepExpected = "Simulated " + product + " arrangement viewed successfully";

		if (simulatedLoan == null || simulatedLoan.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as preconditon failed";
			throw new SkipException(stepActual);
		} else {

			simulationResult = arrangementSimulations("View", customer, productGroup, product, ROLEBASED_SAE, "", "",
					"");

			if (simulationResult == null || simulationResult.contains(ERROR)) {
				stepActual = "Error while viewing simulated " + product + " arrangement";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 7, enabled = true)
	@Parameters({ "productGroup" })
	public void compareSimulations(final String productGroup) {

		stepDescription = "Compare simulations";
		stepExpected = "Simulations compared successfully";

		if (simulation1 == null || simulation1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as preconditon failed";
			throw new SkipException(stepActual);
		} else {

			fields = "Fixed/Floating/Linked#FIXED.RATE:1,";
			values = "3.25,";

			simulation2 = activitySimulations(CREATE + " Simulate +5d", "CHANGE ACTIVITY FOR PRINCIPALINT", mainArrangement,
					productGroup, fields, values);

			simulationResult = activitySimulations("Compare", simulation1 + "," + simulation2, mainArrangement,
					productGroup, "", "");

			if (simulationResult == null || simulationResult.contains(ERROR)) {
				stepActual = "Error while comparing simulations";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 8, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void applySimulations(final String productGroup, final String product) {

		stepDescription = "Apply simulation to " + product + " arrangement";
		stepExpected = "Simulation applied successfully";

		if (simulation2 == null || simulation2.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as preconditon failed";
			throw new SkipException(stepActual);
		} else {

			simulationResult = activitySimulations("Apply", simulation2, mainArrangement, productGroup, "", "");

			if (simulationResult == null || simulationResult.contains(ERROR)) {
				stepActual = "Error while applying simulation";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

		}
		softVerify.assertAll();
	}

	@Test(priority = 8, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void viewSimulations(final String productGroup, final String product) {

		stepDescription = "Validate that the simulated activity was applied successfully to the arrangement " + mainArrangement;
		stepExpected = "Simulated activity was applied successfully";
		String activity;
		result = false;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as preconditon failed";
			throw new SkipException(stepActual);
		} else {

			findArrangement(AUTHORISED, mainArrangement, ARRANGEMENT, "", productGroup, product, CAD);
			result = versionScreen.activityCheck("CHANGE ACTIVITY FOR PRINCIPALINT", "Authorised");

			if (!result) {
				stepActual = "Activity for PRINCIPALINT not found";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}

		softVerify.assertAll();
	}

}
