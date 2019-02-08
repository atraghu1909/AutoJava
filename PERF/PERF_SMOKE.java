package testcases.PERF;

import java.sql.Timestamp;

import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class PERF_SMOKE extends testLibs.BaseTest_Perf {

	// Expected Loading Times
	private static int loadCustomerCreation = 10;

	private static boolean enforceLimits = false;

	private Timestamp timestampStart = new Timestamp(System.currentTimeMillis());
	private Timestamp timestampEnd = new Timestamp(System.currentTimeMillis());
	private StringBuilder actualResult;
	private String customer;
	private String action;
	private String element;
	private int loadingTime;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "productGroup" })
	public void createCustomer(final String branch, final String productGroup) {

		element = "Create Customer screen";
		stepDescription = "Measure " + element + " loading time";
		stepExpected = element + " loading time is under " + loadCustomerCreation + " seconds";

		boolean result = true;
		actualResult = new StringBuilder();

		if (loginResult) {
			switchToBranch(branch);

			createDefaultCustomer("", productGroup, ROLEBASED_LENDING);
			customer = inputTable.identifier("Basic Details").getText();

			if (customer == null || customer.contains("Error")) {
				timestampStart = new Timestamp(System.currentTimeMillis());
				action = "Validate ";
				toolElements.toolsButton(VALIDATE_DEAL).click();

				if (versionScreen.waitForLoading()) {
					timestampEnd = new Timestamp(System.currentTimeMillis());

					loadingTime = logPerformanceLine(action + element, timestampStart, timestampEnd);

					actualResult.append(action + element + " loading time was " + loadingTime + " seconds"
							+ System.lineSeparator() + "Start: " + timestampStart.toString() + System.lineSeparator()
							+ "End: " + timestampEnd.toString() + System.lineSeparator());

					if (enforceLimits && loadingTime > loadCustomerCreation) {
						latestError = action + element + " loading time exceeded expectations";
						softVerify.fail(latestError);
						stepResult = StatusAs.FAILED;
					}

					timestampStart = new Timestamp(System.currentTimeMillis());
					action = "Commit ";
					toolElements.toolsButton(COMMIT_DEAL).click();
					result = inputTable.verifyAcceptOverride();
				} else {
					result = false;
					latestError = action + element + " did not finish loading and timed out";
					stepActual = latestError;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

				if (result && versionScreen.waitForLoading()) {
					timestampEnd = new Timestamp(System.currentTimeMillis());

					loadingTime = logPerformanceLine(action + element, timestampStart, timestampEnd);
					result = readTable.message().getText().contains(TXN_COMPLETE);

					actualResult.append(action + element + " loading time was " + loadingTime + " seconds"
							+ System.lineSeparator() + "Start: " + timestampStart.toString() + System.lineSeparator()
							+ "End: " + timestampEnd.toString() + System.lineSeparator());

					if (enforceLimits && loadingTime > loadCustomerCreation) {
						latestError = action + element + " loading time exceeded expectations";
						softVerify.fail(latestError);
						stepResult = StatusAs.FAILED;
					}
				} else {
					latestError = action + element + " did not finish loading and timed out";
					stepActual = latestError;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			} else {
				latestError = "Error while opening version to create customer";
				stepActual = latestError;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			if (result && customer != null && !customer.contains("Error")) {
				stepActual = actualResult.toString();
				stepResult = StatusAs.PASSED;
			}

		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Login failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
	}
}
