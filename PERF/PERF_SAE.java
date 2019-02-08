package testcases.PERF;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PERF_SAE extends testLibs.BaseTest_Perf {

	// Expected Loading Times
	private static int loadCustomerCreation = 10;
	private static int loadArrangementDetails = 50;
	private static int loadPayoff = 20;
	private static int loadPayment = 20;
	private static int loadActivity = 50;
	private static int loadLoanCreation = 50;
	private static int loadReversal = 20;
	private static int loadSearch = 20;
	private static int loadReport = 150;

	private static boolean enforceLimits = false;

	private Timestamp timestampStart = new Timestamp(System.currentTimeMillis());
	private Timestamp timestampEnd = new Timestamp(System.currentTimeMillis());
	private StringBuilder actualResult;
	private String mainArrangement;
	private String customer;
	private String customerType = BUSINESS;
	private String defaultStep2Fields = "";
	private String defaultStep2Values = "";
	private String action;
	private String element;
	private int loadingTime;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch" })
	public void createCustomer(final String branch) {

		element = "Create Customer screen";
		stepDescription = "Measure " + element + " loading time";
		stepExpected = element + " loading time is under " + loadCustomerCreation + " seconds";

		boolean result = true;
		actualResult = new StringBuilder();

		if (loginResult) {
			switchToBranch(branch);

			customer = createDefaultCustomer(customerType, "", ROLEBASED_SAE);

			if (customer != null && !customer.contains(ERROR)) {
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

	@Test(priority = 2, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void loanCreation(final String productGroup, final String product) {

		element = "Create Arrangement screen";
		stepDescription = "Create " + product + " Arrangement";
		stepExpected = product + " Arrangement created successfully" + System.lineSeparator() + element
				+ " loading time is under " + loadLoanCreation + " seconds";

		boolean result = true;
		actualResult = new StringBuilder();

		if (customer == null || customer.contains("Error")) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			switchToPage(environmentTitle, true);

			if (DefaultVariables.productFields.containsKey(product)) {
				defaultStep2Fields = DefaultVariables.productFields.get(product);
				defaultStep2Values = DefaultVariables.productValues.get(product);
			} else {
				defaultStep2Fields = DefaultVariables.productGroupFields.get(productGroup);
				defaultStep2Values = DefaultVariables.productGroupValues.get(productGroup);
			}

			result = validateLimit(productGroup, product, customer, ROLEBASED_SAE, "CUSTOMER:1,CURRENCY,",
					customer + ",CAD,", defaultStep2Fields, defaultStep2Values);

			if (result) {
				result = multiInputData(defaultStep2Fields, defaultStep2Values, true);
			} else {
				latestError = "Error while creating Limit and Collateral for " + product + " Arrangement";
				stepActual = latestError;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			if (result) {
				result = setupRenewalAndInterest(productGroup, product, defaultStep2Fields);
			} else {
				latestError = "Error while entering valid data for " + product + " Arrangement";
				stepActual = latestError;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			if (result) {
				mainArrangement = readData("ACCOUNT.REFERENCE");
				timestampStart = new Timestamp(System.currentTimeMillis());
				action = "Validate ";
				toolElements.toolsButton(VALIDATE_DEAL).click();
			} else {
				latestError = "Error while entering renewal and interest data for " + product + " Arrangement";
				stepActual = latestError;
				mainArrangement = stepActual;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			if (versionScreen.waitForLoading()) {
				timestampEnd = new Timestamp(System.currentTimeMillis());

				loadingTime = logPerformanceLine(action + element, timestampStart, timestampEnd);

				actualResult.append(action + element + " loading time was " + loadingTime + " seconds"
						+ System.lineSeparator() + "Start: " + timestampStart.toString() + System.lineSeparator()
						+ "End: " + timestampEnd.toString() + System.lineSeparator());

				if (enforceLimits && loadingTime > loadLoanCreation) {
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

				if (enforceLimits && loadingTime > loadLoanCreation) {
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

			if (mainArrangement == null || mainArrangement.contains("Error") || !result) {
				latestError = "Error while creating " + product + " Arrangement";
				stepActual = latestError;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {
				result = disburseArrangement("Regular", mainArrangement, "CAD1110200017817", CAD, "100000", "+0d",
						customer, productGroup);
			}

			if (result) {
				stepActual = actualResult.toString();
				stepResult = StatusAs.PASSED;
			} else {
				latestError = "Error while disbursing " + product + " Arrangement";
				stepActual = latestError;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "productGroup" })
	public void arrangementDetails(final String productGroup) {

		element = "Arrangement Details";
		stepDescription = "Measure " + element + " loading time (all tabs)";
		stepExpected = element + " loading time is under " + loadArrangementDetails + " seconds per tab";

		final List<String> tabs = new ArrayList<String>();
		String[] sectionAndSubsection;
		boolean result = true;
		actualResult = new StringBuilder();

		tabs.add("Arrangement Conditions, Basic");
		tabs.add("Arrangement Conditions, Additional");
		tabs.add("Additional Details, Activity");
		tabs.add("Activity Log, Full");
		tabs.add("Activity Log, Financial");
		tabs.add("Activity Log, User");
		tabs.add("Activity Log, System");
		tabs.add("Activity Log, Saved Activities");
		tabs.add("Activity Log, History");
		tabs.add("Activity Log, Txn History");
		tabs.add("Additional Details, Bills");
		tabs.add("Bills, Type");
		tabs.add("Bills, Consolidated");
		tabs.add("Additional Details, Charges");
		tabs.add("Charges, Date");
		tabs.add("Charges, Type");
		tabs.add("Additional Details, Messages");
		tabs.add("Additional Details, Correspondence");
		tabs.add("Messages, Recent");
		tabs.add("Messages, History");
		tabs.add("Additional Details, Schedule");
		tabs.add("Additional Details, Overdue");
		tabs.add("Additional Details, Sims");
		tabs.add("Additional Details, Payment Orders");
		tabs.add("Payment Orders, Receipts");
		tabs.add("Payment Orders, Payments");

		if (mainArrangement == null || mainArrangement.contains("Error")) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			switchToPage(environmentTitle, true);

			Reporter.log("Loading Arrangement Details for " + productGroup + " " + mainArrangement, debugMode);
			timestampStart = new Timestamp(System.currentTimeMillis());
			action = "Initial ";
			findArrangement(AUTHORISED, mainArrangement, ARRANGEMENT, ROLEBASED_SAE, productGroup, "", "");

			if (versionScreen.waitForLoading()) {
				timestampEnd = new Timestamp(System.currentTimeMillis());

				loadingTime = logPerformanceLine(action + element, timestampStart, timestampEnd);

				actualResult.append(action + element + " loading time was " + loadingTime + " seconds"
						+ System.lineSeparator() + "Start: " + timestampStart.toString() + System.lineSeparator()
						+ "End: " + timestampEnd.toString() + System.lineSeparator());

				if (enforceLimits && loadingTime > loadArrangementDetails) {
					latestError = action + element + " loading time exceeded expectations";
					softVerify.fail(latestError);
					stepResult = StatusAs.FAILED;
				}
			} else {
				result = false;
				latestError = action + element + " did not finish loading and timed out";
				stepActual = latestError;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			if (result) {
				for (final String currentTab : tabs) {
					sectionAndSubsection = currentTab.split(",");
					timestampStart = new Timestamp(System.currentTimeMillis());
					action = "";
					element = currentTab + " Tab";
					versionScreen.linkText(sectionAndSubsection[0], sectionAndSubsection[1].trim()).click();

					if (versionScreen.waitForLoading()) {
						timestampEnd = new Timestamp(System.currentTimeMillis());

						loadingTime = logPerformanceLine(action + element, timestampStart, timestampEnd);

						actualResult.append(action + element + " loading time was " + loadingTime + " seconds"
								+ System.lineSeparator() + "Start: " + timestampStart.toString()
								+ System.lineSeparator() + "End: " + timestampEnd.toString() + System.lineSeparator());

						if (enforceLimits && loadingTime > loadArrangementDetails) {
							latestError = action + element + " loading time exceeded expectations";
							softVerify.fail(latestError);
							stepResult = StatusAs.FAILED;
						}
					} else {
						latestError = action + element + " did not finish loading and timed out";
						stepActual = latestError + System.lineSeparator();
						actualResult.append(stepActual);
						softVerify.fail(stepActual);
						stepResult = StatusAs.FAILED;
					}
				}
			}

			if (result) {
				stepActual = actualResult.toString();
				stepResult = StatusAs.PASSED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void paymentOrder(final String productGroup, final String product) {

		element = "Create Payment Order";
		stepDescription = "Create an Adhoc EFT Payment Order for " + product + " " + mainArrangement;
		stepExpected = "Payment Order created successfully" + System.lineSeparator() + element
				+ " loading time is under " + loadPayment + " seconds";

		String adHocID;
		String paymentOrder;
		String beneficiary;
		String beneficiaryFields;
		String beneficiaryValues;
		String paymentOrderFields;
		String paymentOrderValues;
		boolean result = true;
		actualResult = new StringBuilder();

		if (mainArrangement == null || mainArrangement.contains("Error")) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			switchToPage(environmentTitle, true);

			beneficiaryFields = DefaultVariables.beneficiaryFields + "OWNING.CUSTOMER," + "BEN.CUSTOMER,";
			beneficiaryValues = DefaultVariables.beneficiaryValues + customer + ","
					+ createdCustomers.get(customer).getCustomerName() + ",";

			beneficiary = beneficiaryCode(CREATE, "EFT Client", "", beneficiaryFields, beneficiaryValues);

			paymentOrderFields = "CREDIT.ACCOUNT,PAYMENT.ORDER.PRODUCT,DEBIT.CCY,PAYMENT.CURRENCY,"
					+ "PAYMENT.AMOUNT,ORDERING.CUSTOMER.SSI,";
			paymentOrderValues = mainArrangement + ",ACHCREDIT,CAD,CAD,500," + beneficiary + ",";

			paymentOrder = createAdHocPayment("Open", "EFT", mainArrangement, paymentOrderFields, paymentOrderValues);

			timestampStart = new Timestamp(System.currentTimeMillis());
			action = "Validate ";
			adHocID = inputTable.identifier("Payment Order for Ad-hoc EFT").getText();
			toolElements.toolsButton(VALIDATE_DEAL).click();

			if (!paymentOrder.contains(ERROR) && versionScreen.waitForLoading()) {
				timestampEnd = new Timestamp(System.currentTimeMillis());

				loadingTime = logPerformanceLine(action + element, timestampStart, timestampEnd);

				actualResult.append(action + element + " loading time was " + loadingTime + " seconds"
						+ System.lineSeparator() + "Start: " + timestampStart.toString() + System.lineSeparator()
						+ "End: " + timestampEnd.toString() + System.lineSeparator());

				if (enforceLimits && loadingTime > loadPayment) {
					latestError = action + element + " loading time exceeded expectations";
					softVerify.fail(latestError);
					stepResult = StatusAs.FAILED;
				}

				timestampStart = new Timestamp(System.currentTimeMillis());
				action = "Commit ";
				toolElements.toolsButton(COMMIT_DEAL).click();
				result = inputTable.verifyAcceptOverride();
			} else {
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

				if (enforceLimits && loadingTime > loadPayment) {
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

			if (result) {
				result = authorizeEntity(adHocID, "AdHocPayment Via EFT");
			}

			if (result) {
				stepActual = actualResult.toString();
				stepResult = StatusAs.PASSED;
			} else {
				latestError = "Error while creating Payment Order for " + product;
				stepActual = latestError;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void createActivity(final String productGroup, final String product) {
		element = "Arrangement Activity";
		stepDescription = "Add a NSF Fee for " + product + " " + mainArrangement;
		stepExpected = "NSF Fee is added successfully" + System.lineSeparator() + element + " loading time is under "
				+ loadActivity + " seconds";

		final String fields = "FIXED.AMOUNT,";
		final String values = "30,";
		boolean result = true;
		actualResult = new StringBuilder();

		if (mainArrangement == null || mainArrangement.contains("Error")) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			switchToPage(environmentTitle, true);

			timestampStart = new Timestamp(System.currentTimeMillis());
			action = "Load list of ";
			result = arrangementActivity("Open", "Change and Capitalise ALNSFFEE fee", mainArrangement, productGroup,
					"", "");

			if (result && versionScreen.waitForLoading()) {
				timestampEnd = new Timestamp(System.currentTimeMillis());

				loadingTime = logPerformanceLine(action + element, timestampStart, timestampEnd);

				actualResult.append(action + element + " loading time was " + loadingTime + " seconds"
						+ System.lineSeparator() + "Start: " + timestampStart.toString() + System.lineSeparator()
						+ "End: " + timestampEnd.toString() + System.lineSeparator());

				if (enforceLimits && loadingTime > loadActivity) {
					latestError = action + element + " loading time exceeded expectations";
					softVerify.fail(latestError);
					stepResult = StatusAs.FAILED;
				}

				result = multiInputData(fields, values, false);
			} else {
				latestError = action + element + " did not finish loading and timed out";
				stepActual = latestError;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			if (result) {
				timestampStart = new Timestamp(System.currentTimeMillis());
				action = "Validate ";
				toolElements.toolsButton(VALIDATE_DEAL).click();

				if (versionScreen.waitForLoading()) {
					timestampEnd = new Timestamp(System.currentTimeMillis());

					loadingTime = logPerformanceLine(action + element, timestampStart, timestampEnd);

					actualResult.append(action + element + " loading time was " + loadingTime + " seconds"
							+ System.lineSeparator() + "Start: " + timestampStart.toString() + System.lineSeparator()
							+ "End: " + timestampEnd.toString() + System.lineSeparator());

					if (enforceLimits && loadingTime > loadActivity) {
						latestError = action + element + " loading time exceeded expectations";
						softVerify.fail(latestError);
						stepResult = StatusAs.FAILED;
					}

					timestampStart = new Timestamp(System.currentTimeMillis());
					action = "Commit ";
					toolElements.toolsButton(COMMIT_DEAL).click();
					result = inputTable.verifyAcceptOverride();
				} else {
					latestError = action + element + " did not finish loading and timed out";
					stepActual = latestError;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}

			if (result && versionScreen.waitForLoading()) {
				timestampEnd = new Timestamp(System.currentTimeMillis());

				loadingTime = logPerformanceLine(action + element, timestampStart, timestampEnd);
				result = readTable.message().getText().contains(TXN_COMPLETE);

				actualResult.append(action + element + " loading time was " + loadingTime + " seconds"
						+ System.lineSeparator() + "Start: " + timestampStart.toString() + System.lineSeparator()
						+ "End: " + timestampEnd.toString() + System.lineSeparator());

				if (enforceLimits && loadingTime > loadActivity) {
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

			if (result) {
				stepActual = actualResult.toString();
				stepResult = StatusAs.PASSED;
			} else {
				latestError = "Error while Creating NSF Fee for " + product;
				stepActual = latestError;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void reverseActivity(final String productGroup, final String product) {
		element = "Reverse Arrangement Activity";
		stepDescription = "Reverse the NSF Fee for " + product + " " + mainArrangement;
		stepExpected = "NSF Fee is reversed successfully" + System.lineSeparator() + element + " loading time is under "
				+ loadReversal + " seconds";

		boolean result = true;
		actualResult = new StringBuilder();

		if (mainArrangement == null || mainArrangement.contains("Error")) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			switchToPage(environmentTitle, true);

			timestampStart = new Timestamp(System.currentTimeMillis());
			action = "";
			result = arrangementActivity(REVERSE, "Change and Capitalise ALNSFFEE fee", mainArrangement, productGroup,
					"", "");

			if (result && versionScreen.waitForLoading()) {
				timestampEnd = new Timestamp(System.currentTimeMillis());

				loadingTime = logPerformanceLine(action + element, timestampStart, timestampEnd);
				result = readTable.message().getText().contains(TXN_COMPLETE)
						&& authorizeEntity(mainArrangement, ACTIVITY + "," + productGroup);

				actualResult.append(action + element + " loading time was " + loadingTime + " seconds"
						+ System.lineSeparator() + "Start: " + timestampStart.toString() + System.lineSeparator()
						+ "End: " + timestampEnd.toString() + System.lineSeparator());

				if (enforceLimits && loadingTime > loadReversal) {
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

			if (result) {
				stepActual = actualResult.toString();
				stepResult = StatusAs.PASSED;
			} else {
				latestError = "Error while reversing Payment Order for " + product;
				stepActual = latestError;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 7, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void requestClosure(final String productGroup, final String product) {
		element = "Payoff Simulation";
		stepDescription = "Run a Payoff Simulation for " + product + " " + mainArrangement;
		stepExpected = "Payoff Simulation is run successfully" + System.lineSeparator() + element
				+ " loading time is under " + loadPayoff + " seconds";

		boolean result = true;
		actualResult = new StringBuilder();

		if (mainArrangement == null || mainArrangement.contains("Error")) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			switchToPage(environmentTitle, true);

			timestampStart = new Timestamp(System.currentTimeMillis());
			action = "";
			result = requestClosure(SIMULATE, mainArrangement, "Request Payoff", productGroup, "", "", "", "", "");

			if (result && versionScreen.waitForLoading()) {
				timestampEnd = new Timestamp(System.currentTimeMillis());

				loadingTime = logPerformanceLine(action + element, timestampStart, timestampEnd);
				result = readTable.message().getText().contains(TXN_COMPLETE);

				actualResult.append(action + element + " loading time was " + loadingTime + " seconds"
						+ System.lineSeparator() + "Start: " + timestampStart.toString() + System.lineSeparator()
						+ "End: " + timestampEnd.toString() + System.lineSeparator());

				if (enforceLimits && loadingTime > loadPayoff) {
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

			if (result) {
				stepActual = actualResult.toString();
				stepResult = StatusAs.PASSED;
			} else {
				latestError = "Error while running Payoff Simulation for " + product;
				stepActual = latestError;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
