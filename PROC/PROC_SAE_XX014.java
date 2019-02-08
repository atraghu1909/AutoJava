package testcases.PROC;

import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.DefaultVariables;

public class PROC_SAE_XX014 extends testLibs.BaseTest_DataGen {

	private static final String ADHOC_CHARGES = "AdHoc Charges";
	private static final String BALANCE_ADJUSTMENT = "Balance Adjustment";
	private static final String EFT_PAYMENTS = "EFT Payments";
	private static final String FULL_PAYOFF = "Full Payoff";
	private static final String MANUAL_PAYMENT = "Manual Payment";
	private static final String PARTIAL_PAYOFF = "Partial Payoff";
	private static final String PRINCIPAL_DECREASE = "Principal Decrease";
	private static final String STANDING_ORDER = "Standing Order";
	private static final String UNC_ACCOUNT = "UNC Account";
	private static final String WRITE_OFF = "Write Off";

	private static String documentVersion = "2018-09-04";
	private ArrangementData arrangementsData;
	private String[] arrangements;
	private String customer;
	private String transit;
	private String fields;
	private String values;
	private String transactionID;
	private String reserveAccount;
	private String payoffAmount1;
	private String payoffAmount2;
	private String beneficiary;
	private String creditPO;
	private String debitPO;
	private boolean result;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "customerType", "productGroup", "product", "testType" })
	public void preCondition(@Optional("LAURENTIAN BANK - 523") final String branch,
			@Optional(BUSINESS) final String customerType, @Optional(COMMERCIAL_LOANS) final String productGroup,
			final String product, final String testType) {

		stepDescription = "Create a " + customerType + " customer and some " + product + " arrangements";
		stepExpected = "Customer and Arrangements created successfully";

		int totalArrangements;
		boolean localResult = true;

		if (loginResult) {

			switchToBranch(branch);
			transit = branch.substring(branch.length() - 3);

			switch (testType) {
			case STANDING_ORDER:
			case PARTIAL_PAYOFF:
				totalArrangements = 1;
				break;
			case MANUAL_PAYMENT:
			case UNC_ACCOUNT:
			case PRINCIPAL_DECREASE:
			case FULL_PAYOFF:
			case ADHOC_CHARGES:
			case EFT_PAYMENTS:
				totalArrangements = 2;
				break;
			case WRITE_OFF:
				totalArrangements = 3;
				break;
			case BALANCE_ADJUSTMENT:
				totalArrangements = 4;
				break;
			default:
				totalArrangements = 0;
				stepActual = "Framework cannot handle this testType: " + testType;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
				Reporter.log(stepActual, debugMode);
				break;
			}

			customer = createDefaultCustomer(customerType, productGroup, ROLEBASED_SAE);
			arrangements = new String[totalArrangements];

			for (int i = 0; i < totalArrangements && localResult; i++) {
				arrangementsData = new ArrangementData("allArrangements", productGroup, product)
						.withCustomers(customer, createdCustomers.get(customer), "", "100", "100")
						.withLimit("", "NEW", "N").withEffectiveDate("-45d").withDisbursement().build();

				arrangements[i] = createDefaultArrangement(arrangementsData);
				localResult = !arrangements[i].contains(ERROR);
			}

			if (customer == null || customer.contains(ERROR) || !localResult) {
				stepActual = "Error while creating test customer and arrangement(s)";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {
				stepActual = "Customer " + customer + " and " + totalArrangements
						+ " Arrangement(s) created successfully:";
				for (int i = 0; i < totalArrangements; i++) {
					stepActual += System.lineSeparator();
					stepActual += arrangements[i];
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
	@Parameters("testType")
	public void repaymentLink(final String testType) {
		if (MANUAL_PAYMENT.equals(testType)) {
			stepDescription = "Perform a manual payment on arrangement " + arrangements[0]
					+ " using the AA Repayment Link on the Role Based Page";
			stepExpected = "Manual Payment performed successfully";

			if (arrangements[0] == null || arrangements[0].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = "CREDIT.ACCT.NO," + "CREDIT.CURRENCY," + "DEBIT.ACCT.NO," + "CREDIT.AMOUNT,"
						+ "ORDERING.BANK:1,";
				values = arrangements[0] + "," + CAD + "," + "CHEKCAD" + transit + "," + "2000," + "LBC,";

				transactionID = financialTransaction(CREATE, "AA Repayment", fields, values);

				if (transactionID.contains(ERROR)) {
					stepActual = transactionID;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "customerType", "testType" })
	public void repaymentArrangementAction(@Optional(BUSINESS) final String customerType, final String testType) {
		if (MANUAL_PAYMENT.equals(testType)) {
			stepDescription = "Perform a manual payment on arrangement " + arrangements[1]
					+ " using the dropdown on the Customer 360 View";
			stepExpected = "Manual Payment performed successfully";

			if (arrangements[1] == null || arrangements[1].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = "CREDIT.ACCT.NO," + "CREDIT.CURRENCY," + "DEBIT.ACCT.NO," + "CREDIT.AMOUNT,"
						+ "ORDERING.BANK:1,";
				values = arrangements[1] + "," + CAD + "," + "CHEKCAD" + transit + "," + "2000," + "LBC,";

				result = arrangementAction(arrangements[1], customer, ROLEBASED_SAE, "AA Repayment", fields, values,
						customerType);
				if (!result) {
					stepActual = "Error while performing Manual Payment on Customer 360 View";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters("testType")
	public void creditLink(final String testType) {
		if (UNC_ACCOUNT.equals(testType)) {
			stepDescription = "Credit the UNC balance on arrangement " + arrangements[0]
					+ " using the Credit UNC Balance Link on the Role Based Page";
			stepExpected = "UNC Balance credited successfully";

			if (arrangements[0] == null || arrangements[0].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = "CREDIT.ACCT.NO," + "CREDIT.CURRENCY," + "DEBIT.ACCT.NO," + "CREDIT.AMOUNT,"
						+ "ORDERING.BANK:1,";
				values = arrangements[0] + "," + CAD + "," + "CHEKCAD" + transit + "," + "2000," + "LBC,";

				transactionID = financialTransaction(CREATE, "Credit UNC Balance", fields, values);

				if (transactionID.contains(ERROR)) {
					stepActual = transactionID;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	@Parameters({ "customerType", "testType" })
	public void creditArrangementAction(@Optional(BUSINESS) final String customerType, final String testType) {
		if (UNC_ACCOUNT.equals(testType)) {
			stepDescription = "Credit the UNC balance on arrangement " + arrangements[1]
					+ " using the dropdown on the Customer 360 View";
			stepExpected = "UNC Balance credited successfully";

			if (arrangements[1] == null || arrangements[1].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = "CREDIT.ACCT.NO," + "CREDIT.CURRENCY," + "DEBIT.ACCT.NO," + "CREDIT.AMOUNT,"
						+ "ORDERING.BANK:1,";
				values = arrangements[1] + "," + CAD + "," + "CHEKCAD" + transit + "," + "2000," + "LBC,";

				result = arrangementAction(arrangements[1], customer, ROLEBASED_SAE, "Credit Arrangement", fields,
						values, customerType);

				if (!result) {
					stepActual = "Error while performing Manual Payment on Customer 360 View";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	@Parameters("testType")
	public void debitLink(final String testType) {
		if (UNC_ACCOUNT.equals(testType)) {
			stepDescription = "Debit the UNC balance on arrangement " + arrangements[0]
					+ " using the Debit UNC Balance Link on the Role Based Page";
			stepExpected = "UNC Balance debited successfully";

			if (arrangements[0] == null || arrangements[0].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = "DEBIT.ACCT.NO," + "DEBIT.CURRENCY," + "CREDIT.ACCT.NO," + "DEBIT.AMOUNT,"
						+ "ORDERING.BANK:1,";
				values = arrangements[0] + "," + CAD + "," + "CHEKCAD" + transit + "," + "1750," + "LBC,";

				transactionID = financialTransaction(CREATE, "Debit UNC Balance", fields, values);

				if (transactionID.contains(ERROR)) {
					stepActual = transactionID;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 7, enabled = true)
	@Parameters({ "customerType", "testType" })
	public void debitArrangementAction(@Optional(BUSINESS) final String customerType, final String testType) {
		if (UNC_ACCOUNT.equals(testType)) {
			stepDescription = "Debit the UNC balance on arrangement " + arrangements[1]
					+ " using the dropdown on the Customer 360 View";
			stepExpected = "UNC Balance debited successfully";

			if (arrangements[1] == null || arrangements[1].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = "DEBIT.ACCT.NO," + "DEBIT.CURRENCY," + "CREDIT.ACCT.NO," + "DEBIT.AMOUNT,"
						+ "ORDERING.BANK:1,";
				values = arrangements[1] + "," + CAD + "," + "CHEKCAD" + transit + "," + "1750," + "LBC,";

				result = arrangementAction(arrangements[1], customer, ROLEBASED_SAE, "Debit UNC Balance", fields,
						values, customerType);

				if (!result) {
					stepActual = "Error while performing Manual Payment on Customer 360 View";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 8, enabled = true)
	@Parameters("testType")
	public void createStandingOrder(final String testType) {
		if (STANDING_ORDER.equals(testType)) {
			stepDescription = "Create a Standing Order for arrangement " + arrangements[0];
			stepExpected = "Standing Order created successfully";

			if (arrangements[0] == null || arrangements[0].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				reserveAccount = createDefaultArrangement(SERVICING_ACCOUNTS, "Reserve Account -LBC Bus Serv",
						ROLEBASED_SAE, customer, "+0d");

				fields = "CURRENCY," + "CURRENT.AMOUNT.BAL," + "CURRENT.FREQUENCY," + "CURRENT.END.DATE,"
						+ "CPTY.ACCT.NO," + "PAYMENT.DETAILS:1,";
				values = "CAD," + "350.00," + "e0Y e1M e0W o18D e0F," + "+1Y," + arrangements[0] + "," + "Test,";

				result = reoccurringFixedTransfer("Setup", "Internal", customer, reserveAccount, fields, values);

				if (reserveAccount == null || reserveAccount.contains(ERROR) || !result) {
					stepActual = "Error while creating Standing Order";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 9, enabled = true)
	@Parameters("testType")
	public void reverseStandingOrder(final String testType) {
		if (STANDING_ORDER.equals(testType)) {
			stepDescription = "Reverse the Standing Order created for arrangement " + arrangements[0];
			stepExpected = "Standing Order reversed successfully";

			if (reserveAccount == null || reserveAccount.contains(ERROR) || arrangements[0] == null
					|| arrangements[0].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				result = reoccurringFixedTransfer("Reverse", "Internal", customer, reserveAccount + ".1", "", "");

				if (!result) {
					stepActual = "Error while closing Standing Order";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 10, enabled = true)
	@Parameters("testType")
	public void restoreStandingOrder(final String testType) {
		if (STANDING_ORDER.equals(testType)) {
			stepDescription = "Restore the Standing Order reversed for arrangement " + arrangements[0];
			stepExpected = "Standing Order restored successfully";

			if (reserveAccount == null || reserveAccount.contains(ERROR) || arrangements[0] == null
					|| arrangements[0].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				result = reoccurringFixedTransfer("Restore", "Internal", customer, reserveAccount + ".1", "", "");

				if (!result) {
					stepActual = "Error while restoring Standing Order";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 11, enabled = true)
	@Parameters({ "productGroup", "testType" })
	public void applyPenalty3Months(@Optional(COMMERCIAL_LOANS) final String productGroup, final String testType) {
		if (PRINCIPAL_DECREASE.equals(testType)) {
			stepDescription = "Apply the prepayment penalty fee on arrangement " + arrangements[0] + " and"
					+ arrangements[1] + " using CHANGE ACTIVITY FOR ALPREPAYPENALTY activity";
			stepExpected = "Prepayment penalty fee applied successfully";

			if (arrangements[0] == null || arrangements[0].contains(ERROR) || arrangements[1] == null
					|| arrangements[1].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = "L.PEN.TYPE,";
				values = "3mths.payoff,";
				result = arrangementActivity(CREATE, "CHANGE ACTIVITY FOR ALPREPAYPENALTY", arrangements[0],
						productGroup, fields, values)
						&& arrangementActivity(CREATE, "CHANGE ACTIVITY FOR ALPREPAYPENALTY", arrangements[1],
								productGroup, fields, values);

				if (!result) {
					stepActual = "Error while applying the prepayment penalty fee";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 12, enabled = true)
	@Parameters("testType")
	public void repayLink(final String testType) {
		if (PRINCIPAL_DECREASE.equals(testType)) {
			stepDescription = "Perform a principal decrease on arrangement " + arrangements[0]
					+ " using the Principal Decrease / Repay Link on the Role Based Page";
			stepExpected = "Principal decrease performed successfully";

			if (arrangements[0] == null || arrangements[0].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = "CREDIT.ACCT.NO," + "DEBIT.ACCT.NO," + "CREDIT.AMOUNT," + "CREDIT.CURRENCY,"
						+ "ORDERING.BANK:1,";
				values = arrangements[0] + "," + "CHEKCAD" + transit + "," + "2000," + "CAD," + "LBC,";
				transactionID = financialTransaction(CREATE, "Principal Decrease / Repay", fields, values);

				if (transactionID.contains(ERROR)) {
					stepActual = transactionID;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 13, enabled = true)
	@Parameters({ "customerType", "testType" })
	public void repayArrangementAction(@Optional(BUSINESS) final String customerType, final String testType) {
		if (PRINCIPAL_DECREASE.equals(testType)) {
			stepDescription = "Perform a principal decrease on arrangement " + arrangements[1]
					+ " using the dropdown on the Customer 360 View";
			stepExpected = "Principal decrease performed successfully";

			if (arrangements[1] == null || arrangements[1].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = "CREDIT.ACCT.NO," + "DEBIT.ACCT.NO," + "CREDIT.AMOUNT," + "CREDIT.CURRENCY,"
						+ "ORDERING.BANK:1,";
				values = arrangements[1] + "," + "CHEKCAD" + transit + "," + "2000," + "CAD," + "LBC,";
				result = arrangementAction(arrangements[1], customer, ROLEBASED_SAE, "Principal Decrease/ Repay",
						fields, values, customerType);

				if (!result) {
					stepActual = "Error while performing principal decrease on Customer 360 View";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 14, enabled = true)
	@Parameters({ "productGroup", "testType" })
	public void applyPenaltyIRD(@Optional(COMMERCIAL_LOANS) final String productGroup, final String testType) {
		if (FULL_PAYOFF.equals(testType)) {
			stepDescription = "Apply the prepayment penalty fee on arrangement " + arrangements[0]
					+ " and adjust its effective interest rate";
			stepExpected = "Prepayment penalty fee applied and new interest adjusted successfully";

			if (arrangements[0] == null || arrangements[0].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = "L.PEN.TYPE,";
				values = "Greater.3m.or.ird,";
				result = arrangementActivity(CREATE, "CHANGE ACTIVITY FOR ALPREPAYPENALTY", arrangements[0],
						productGroup, fields, values);

				if (!result) {
					stepActual = "Error while applying the prepayment penalty fee";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

				fields = "Principal Interest$MOS Interface#L.POSTED.RATE,";
				values = "3.69,";
				result = arrangementActivity(CREATE, "CHANGE ACTIVITY FOR PRINCIPALINT", arrangements[0], productGroup,
						fields, values);

				if (!result) {
					stepActual = "Error while adjusting the new effective rate of the arrangement";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 15, enabled = true)
	@Parameters({ "productGroup", "testType" })
	public void applyPenaltyNPV(@Optional(COMMERCIAL_LOANS) final String productGroup, final String testType) {
		if (FULL_PAYOFF.equals(testType)) {
			stepDescription = "Apply the prepayment penalty fee on arrangement " + arrangements[1]
					+ " and adjust its effective interest rate";
			stepExpected = "Prepayment penalty fee applied and new interest adjusted successfully";

			if (arrangements[1] == null || arrangements[1].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = "L.PEN.TYPE,";
				values = "Greater.3m.or.npv,";
				result = arrangementActivity(CREATE, "CHANGE ACTIVITY FOR ALPREPAYPENALTY", arrangements[1],
						productGroup, fields, values);

				if (!result) {
					stepActual = "Error while applying the prepayment penalty fee";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

				fields = "Principal Interest$L.RE.INVEST.RT," + "Principal Interest$MOS Interface#L.POSTED.RATE,";
				values = "3.69," + "3.69,";
				result = arrangementActivity(CREATE, "CHANGE ACTIVITY FOR PRINCIPALINT", arrangements[1], productGroup,
						fields, values);

				if (!result) {
					stepActual = "Error while adjusting the new effective rate of the arrangement";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 16, enabled = true)
	@Parameters({ "productGroup", "product", "testType" })
	public void requestPayoff(@Optional(COMMERCIAL_LOANS) final String productGroup, final String product,
			final String testType) {
		if (FULL_PAYOFF.equals(testType)) {
			stepDescription = "Request Payoff on arrangements " + arrangements[0] + " and" + arrangements[1]
					+ " and retrieve their Payoff Amounts";
			stepExpected = "Payoff requested and amounts retrieved successfully";

			if (arrangements[0] == null || arrangements[0].contains(ERROR) || arrangements[1] == null
					|| arrangements[1].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = "EFFECTIVE.DATE," + "CLOSURE.REASON,";
				values = "+1d," + "Tsf To Competitor,";

				result = requestClosure(SIMULATE, arrangements[0], "Request Payoff", productGroup, fields, values, "",
						"", "")
						&& requestClosure(SIMULATE, arrangements[1], "Request Payoff", productGroup, fields, values, "",
								"", "");

				if (result) {
					payoffAmount1 = retrievePayoffAmount(arrangements[0], productGroup, product);
					payoffAmount2 = retrievePayoffAmount(arrangements[1], productGroup, product);
				} else {
					stepActual = "Error while requesting payoff for the arrangements";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

				if (payoffAmount1 == null || payoffAmount1.contains(ERROR) || payoffAmount2 == null
						|| payoffAmount2.contains(ERROR)) {
					stepActual = "Error while retrieving payoff amount for the arrangements";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				} else {
					stepActual = "Payoff Amount for " + arrangements[0] + ": " + payoffAmount1 + System.lineSeparator()
							+ "Payoff Amount for " + arrangements[1] + ": " + payoffAmount2;
				}

			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 17, enabled = true)
	@Parameters("testType")
	public void payoffLink(final String testType) {
		if (FULL_PAYOFF.equals(testType)) {
			stepDescription = "Payoff arrangement " + arrangements[0]
					+ " using the AA Pay-Off Link on the Role Based Page";
			stepExpected = "Arrangement paid off successfully";

			if (payoffAmount1 == null || payoffAmount1.contains(ERROR) || arrangements[0] == null
					|| arrangements[0].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = "CREDIT.ACCT.NO," + "DEBIT.ACCT.NO," + "CREDIT.AMOUNT," + "CREDIT.CURRENCY,"
						+ "ORDERING.BANK:1," + "DEBIT.VALUE.DATE," + "CREDIT.VALUE.DATE,";
				values = arrangements[0] + "," + "CHEKCAD" + transit + "," + payoffAmount1 + "," + "CAD," + "LBC,"
						+ "+1d," + "+1d,";
				transactionID = financialTransaction(CREATE, "AA Pay-Off", fields, values);

				if (transactionID.contains(ERROR)) {
					stepActual = transactionID;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 18, enabled = true)
	@Parameters({ "customerType", "testType" })
	public void payoffArrangementAction(@Optional(BUSINESS) final String customerType, final String testType) {
		if (FULL_PAYOFF.equals(testType)) {
			stepDescription = "Payoff arrangement " + arrangements[1] + " using the dropdown on the Customer 360 View";
			stepExpected = "Arrangement paid off successfully";

			if (payoffAmount2 == null || payoffAmount2.contains(ERROR) || arrangements[1] == null
					|| arrangements[1].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = "CREDIT.ACCT.NO," + "DEBIT.ACCT.NO," + "CREDIT.AMOUNT," + "CREDIT.CURRENCY,"
						+ "ORDERING.BANK:1," + "DEBIT.VALUE.DATE," + "CREDIT.VALUE.DATE,";
				values = arrangements[1] + "," + "CHEKCAD" + transit + "," + payoffAmount2 + "," + "CAD," + "LBC,"
						+ "+1d," + "+1d,";
				result = arrangementAction(arrangements[1], customer, ROLEBASED_SAE, "AA Pay-Off", fields, values,
						customerType);

				if (!result) {
					stepActual = "Error while performing AA Pay-Off on Customer 360 View";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 19, enabled = true)
	@Parameters({ "productGroup", "testType" })
	public void applyPenaltyBreakcost(@Optional(COMMERCIAL_LOANS) final String productGroup, final String testType) {
		if (PARTIAL_PAYOFF.equals(testType)) {
			stepDescription = "Apply the prepayment penalty fee on arrangement " + arrangements[0]
					+ " and adjust its effective interest rate";
			stepExpected = "Prepayment penalty fee applied and new interest adjusted successfully";

			if (arrangements[0] == null || arrangements[0].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = "L.PEN.TYPE,";
				values = "Breakcost,";
				result = arrangementActivity(CREATE, "CHANGE ACTIVITY FOR ALPREPAYPENALTY", arrangements[0],
						productGroup, fields, values);

				if (!result) {
					stepActual = "Error while applying the prepayment penalty fee";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

				fields = "Principal Interest$L.RE.INVS.BRC,";
				values = "3.86,";
				result = arrangementActivity(CREATE, "CHANGE ACTIVITY FOR PRINCIPALINT", arrangements[0], productGroup,
						fields, values);

				if (!result) {
					stepActual = "Error while adjusting the new effective rate of the arrangement";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 20, enabled = true)
	@Parameters({ "testType", "customerType" })
	public void principalDecreaseArrangementAction(final String testType,
			@Optional(PERSONAL) final String customerType) {
		if (PARTIAL_PAYOFF.equals(testType)) {
			stepDescription = "Perform a Partial Payoff on arrangement " + arrangements[0]
					+ " using the Principal Decrease dropdown on the Customer 360 View";
			stepExpected = "Partial Payoff performed successfully";

			if (arrangements[0] == null || arrangements[0].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = "CREDIT.ACCT.NO," + "DEBIT.ACCT.NO," + "CREDIT.AMOUNT," + "CREDIT.CURRENCY,"
						+ "ORDERING.BANK:1,";
				values = arrangements[0] + "," + "CHEKCAD" + transit + "," + "2000," + "CAD," + "LBC,";
				result = arrangementAction(arrangements[0], customer, ROLEBASED_SAE, "Principal Decrease/ Repay",
						fields, values, customerType);

				if (!result) {
					stepActual = "Error while performing principal decrease on Customer 360 View";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 21, enabled = true)
	@Parameters({ "productGroup", "testType" })
	public void bypassPriority(@Optional(COMMERCIAL_LOANS) final String productGroup, final String testType) {
		if (ADHOC_CHARGES.equals(testType)) {
			stepDescription = "Update the Byspass Priority rule on arrangements " + arrangements[0] + " and"
					+ arrangements[1] + " using UPDATE ACTIVITY FOR PR.BYPASSREPAY activity";
			stepExpected = "Bypass Priority rules updated successfully";

			if (arrangements[0] == null || arrangements[0].contains(ERROR) || arrangements[1] == null
					|| arrangements[1].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = "APPLICATION.TYPE:1," + "APPLICATION.ORDER:1," + "SEQUENCE:1:1," + "PROPERTY:1:1,"
						+ "BALANCE.TYPE:1:1," + "PROP.APPL.TYPE:1:1," + "SEQUENCE:1:2," + "PROPERTY:1:2,"
						+ "BALANCE.TYPE:1:2," + "PROP.APPL.TYPE:1:2," + "SEQUENCE:1+:3," + "PROPERTY:1:3,"
						+ "BALANCE.TYPE:1:3," + "PROP.APPL.TYPE:1:3," + "SEQUENCE:1+:4," + "PROPERTY:1:4,"
						+ "BALANCE.TYPE:1:4," + "PROP.APPL.TYPE:1:4,";
				values = "BILL.PROPERTY," + "Oldest.first," + "1," + "ALPREPAYPENALTY," + "," + "," + "2,"
						+ "ALDISCHARGE," + "," + "," + "3," + "," + "CURACCOUNT," + "Balances," + "4," + ","
						+ "ACCPRINCIPALINT," + "Balances,";

				result = arrangementActivity(CREATE, "UPDATE ACTIVITY FOR PR.BYPASSREPAY", arrangements[0],
						productGroup, fields, values)
						&& arrangementActivity(CREATE, "UPDATE ACTIVITY FOR PR.BYPASSREPAY", arrangements[1],
								productGroup, fields, values);

				if (!result) {
					stepActual = "Error while updating the Bypass Priority rule for the arrangements";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 22, enabled = true)
	@Parameters("testType")
	public void applyPaymentLink(final String testType) {
		if (ADHOC_CHARGES.equals(testType)) {
			stepDescription = "Apply a payment to an AdHoc Charge on arrangement " + arrangements[0]
					+ " using the link on the Role Based Page";
			stepExpected = "AdHoc Charge payment applied successfully";

			if (arrangements[0] == null || arrangements[0].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = "CREDIT.ACCT.NO," + "DEBIT.ACCT.NO," + "CREDIT.AMOUNT," + "CREDIT.CURRENCY,"
						+ "ORDERING.BANK:1,";
				values = arrangements[0] + "," + "CHEKCAD" + transit + "," + "2000," + "CAD," + "LBC,";
				transactionID = financialTransaction(CREATE, "Apply AdHoc Charges", fields, values);

				if (transactionID.contains(ERROR)) {
					stepActual = transactionID;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 23, enabled = true)
	@Parameters({ "customerType", "testType" })
	public void applyPaymentArrangementAction(@Optional(BUSINESS) final String customerType, final String testType) {
		if (ADHOC_CHARGES.equals(testType)) {
			stepDescription = "Apply a payment to an AdHoc Charge on arrangement " + arrangements[1]
					+ " using the dropdown on the Customer 360 View";
			stepExpected = "AdHoc Charge payment applied successfully";

			if (arrangements[1] == null || arrangements[1].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = "CREDIT.ACCT.NO," + "DEBIT.ACCT.NO," + "CREDIT.AMOUNT," + "CREDIT.CURRENCY,"
						+ "ORDERING.BANK:1,";
				values = arrangements[1] + "," + "CHEKCAD" + transit + "," + "2000," + "CAD," + "LBC,";
				result = arrangementAction(arrangements[1], customer, ROLEBASED_SAE, "Apply Adhoc Charges", fields,
						values, customerType);

				if (!result) {
					stepActual = "Error while performing Apply Adhoc Charges on Customer 360 View";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 24, enabled = true)
	@Parameters("testType")
	public void createBeneficiary(final String testType) {
		if (EFT_PAYMENTS.equals(testType)) {
			stepDescription = "Create a new Beneficiary for customer " + customer;
			stepExpected = "Beneficiary created successfully";

			if (customer == null || customer.contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = DefaultVariables.beneficiaryFields + "OWNING.CUSTOMER," + "BEN.CUSTOMER,";
				values = DefaultVariables.beneficiaryValues + customer + ","
						+ createdCustomers.get(customer).getCustomerName() + ",";

				beneficiary = beneficiaryCode(CREATE, "EFT Client", "", fields, values);

				if (beneficiary == null || beneficiary.contains(ERROR)) {
					stepActual = beneficiary;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				} else {
					stepActual = "Beneficiary created successfully: " + beneficiary;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 25, enabled = true)
	@Parameters("testType")
	public void adHocPaymentCredit(final String testType) {
		if (EFT_PAYMENTS.equals(testType)) {
			stepDescription = "Apply an EFT AdHoc Credit Payment on arrangement " + arrangements[0];
			stepExpected = "EFT AdHoc payment applied successfully";

			if (arrangements[0] == null || arrangements[0].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = "CREDIT.ACCOUNT," + "PAYMENT.ORDER.PRODUCT," + "PAYMENT.CURRENCY," + "PAYMENT.AMOUNT,"
						+ "DEBIT.VALUE.DATE," + "ORDERING.CUSTOMER.SSI,";
				values = arrangements[0] + "," + "ACHCREDIT," + "CAD," + "2000," + "+0d," + beneficiary + ",";

				creditPO = createAdHocPayment(CREATE, EFT, arrangements[0], fields, values);

				if (creditPO == null || creditPO.contains(ERROR)) {
					stepActual = creditPO;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				} else {
					stepActual = "EFT AdHoc Credit created successfully: " + creditPO;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 26, enabled = true)
	@Parameters("testType")
	public void adHocPaymentDebit(final String testType) {
		if (EFT_PAYMENTS.equals(testType)) {
			stepDescription = "Apply an EFT AdHoc Debit Payment on arrangement " + arrangements[1];
			stepExpected = "EFT AdHoc payment applied successfully";

			if (arrangements[1] == null || arrangements[1].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = "DEBIT.ACCOUNT," + "PAYMENT.ORDER.PRODUCT," + "PAYMENT.CURRENCY," + "PAYMENT.AMOUNT,"
						+ "DEBIT.VALUE.DATE," + "BENEFICIARY.ID,";
				values = arrangements[1] + "," + "ACHDEBIT," + "CAD," + "2000," + "+0d," + beneficiary + ",";

				debitPO = createAdHocPayment(CREATE, EFT, arrangements[1], fields, values);

				if (debitPO == null || debitPO.contains(ERROR)) {
					stepActual = debitPO;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				} else {
					stepActual = "EFT AdHoc Debit created successfully: " + debitPO;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 27, enabled = true)
	@Parameters("testType")
	public void reversePaymentOrder(final String testType) {
		if (EFT_PAYMENTS.equals(testType)) {
			stepDescription = "Reverse the Payment Orders for the EFT AdHoc Transactions " + creditPO + " and "
					+ debitPO;
			stepExpected = "Payment Orders reversed successfully";

			if (creditPO == null || creditPO.contains(ERROR) || debitPO == null || debitPO.contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				result = reverseEntity(creditPO, ADHOC_EFT) && reverseEntity(debitPO, ADHOC_EFT);

				if (!result) {
					stepActual = "Error while reversing Payment Orders";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 28, enabled = true)
	@Parameters("testType")
	public void createPAPCredit(final String testType) {
		if (EFT_PAYMENTS.equals(testType)) {
			stepDescription = "Create a Pre-Authorized Credit Payment (PAP Credit) for arrangement " + arrangements[0];
			stepExpected = "PAP created successfully";

			if (arrangements[0] == null || arrangements[0].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = "CPA.TYPE," + "PAP.FOREGN," + "CAMB.PAP.ACCT2," + "CAMB.PAP.ACCT," + "STAND.ALONE.AMT,"
						+ "CYCLE.FREQUENCY," + "DEST.ACCT.NAME:1," + "CA.PAP.PURPOSE," + "CREATE.DATE,"
						+ "CA.PAP.MATDATE,";
				values = "372," + "000101791," + "12345678," + "12345678," + "5T," + "e0Y e1M e0W o18D e0F," + "Test,"
						+ "New Authorization," + "+1d," + "+1y,";

				result = reoccurringFixedTransfer("Setup", "PAP Credit", customer, arrangements[0], fields, values);

				if (!result) {
					stepActual = "Error while creating PAP";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 29, enabled = true)
	@Parameters("testType")
	public void createPAPDebit(final String testType) {
		if (EFT_PAYMENTS.equals(testType)) {
			stepDescription = "Create a Pre-Authorized Debit Payment (PAP Debit) for arrangement " + arrangements[1];
			stepExpected = "PAP created successfully";

			if (arrangements[1] == null || arrangements[1].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = "CPA.TYPE," + "PAP.FOREGN," + "CAMB.PAP.ACCT2," + "CAMB.PAP.ACCT," + "STAND.ALONE.AMT,"
						+ "CYCLE.FREQUENCY," + "DEST.ACCT.NAME:1," + "CA.PAP.PURPOSE," + "CREATE.DATE,"
						+ "CA.PAP.MATDATE," + "CAMB.PAP.AMT,";
				values = "372," + "000101791," + "12345678," + "12345678," + "5T," + "e0Y e1M e0W o18D e0F," + "Test,"
						+ "New Authorization," + "+1d," + "+1y," + "No,";

				result = reoccurringFixedTransfer("Setup", "PAP Debit", customer, arrangements[1], fields, values);

				if (!result) {
					stepActual = "Error while creating PAP";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 30, enabled = true)
	@Parameters("testType")
	public void reversePAP(final String testType) {
		if (EFT_PAYMENTS.equals(testType)) {
			stepDescription = "Reverse the Pre-Authorized Payments on " + arrangements[0] + " and " + arrangements[1];
			stepExpected = "PAPs reversed successfully";

			if (arrangements[0] == null || arrangements[0].contains(ERROR) || arrangements[1] == null
					|| arrangements[1].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				result = reoccurringFixedTransfer("Close", "PAP Credit", customer, arrangements[0], fields, values)
						&& reoccurringFixedTransfer("Close", "PAP Debit", customer, arrangements[1], fields, values);

				if (!result) {
					stepActual = "Error while reversing PAPs";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 31, enabled = true)
	@Parameters({ "productGroup", "testType" })
	public void adjustAllFixed(@Optional(COMMERCIAL_LOANS) final String productGroup, final String testType) {
		if (BALANCE_ADJUSTMENT.equals(testType)) {
			stepDescription = "Adjust both Balance and Bill information on " + arrangements[0] + " to a Fixed amount";
			stepExpected = "Information adjusted successfully";
			if (arrangements[0] == null || arrangements[0].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = "ADJUST.DESC," + "Balance Adjustment#NEW.BAL.AMT:1:1," + "Balance Adjustment#NEW.BAL.AMT:2:1,"
						+ "Bill Adjustment#BILL.ADJ.NARR:1," + "Bill Adjustment#NEW.PROP.AMT:1:1,"
						+ "Bill Adjustment#NEW.PROP.AMT:1:2,";
				values = "All Fixed," + "-2000," + "-20," + "All Fixed," + "20000," + "200,";

				result = arrangementActivity(CREATE, "ADJUST.ALL ACTIVITY FOR BALANCE.MAINTENANCE", arrangements[0],
						productGroup, fields, values);

				if (!result) {
					stepActual = "Error while adjusting Balance and Bill information";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 32, enabled = true)
	@Parameters({ "productGroup", "testType" })
	public void adjustAllRelative(@Optional(COMMERCIAL_LOANS) final String productGroup, final String testType) {
		if (BALANCE_ADJUSTMENT.equals(testType)) {
			stepDescription = "Adjust both Balance and Bill information on " + arrangements[1]
					+ " by a Relative amount";
			stepExpected = "Information adjusted successfully";
			if (arrangements[1] == null || arrangements[1].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = "ADJUST.DESC," + "Balance Adjustment#ADJ.BAL.AMT:1:1," + "Balance Adjustment#ADJ.BAL.AMT:2:1,"
						+ "Bill Adjustment#BILL.ADJ.NARR:1," + "Bill Adjustment#ADJ.PROP.AMT:1:1,"
						+ "Bill Adjustment#ADJ.PROP.AMT:1:2,";
				values = "All Relative," + "-2000," + "-20," + "All Relative," + "-2000," + "-20,";

				result = arrangementActivity(CREATE, "ADJUST.ALL ACTIVITY FOR BALANCE.MAINTENANCE", arrangements[1],
						productGroup, fields, values);

				if (!result) {
					stepActual = "Error while adjusting Balance and Bill information";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 33, enabled = true)
	@Parameters({ "productGroup", "testType" })
	public void adjustCapitalFixed(@Optional(COMMERCIAL_LOANS) final String productGroup, final String testType) {
		if (BALANCE_ADJUSTMENT.equals(testType)) {
			stepDescription = "Adjust Balance information on " + arrangements[2] + " to a Fixed amount";
			stepExpected = "Information adjusted successfully";
			if (arrangements[2] == null || arrangements[2].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = "NEW.BAL.AMT:1:1," + "NEW.BAL.AMT:2:1,";
				values = "-2000," + "-20,";

				result = arrangementActivity(CREATE_AUTHORISE, "ADJUST.BALANCE ACTIVITY FOR BALANCE.MAINTENANCE",
						arrangements[2], productGroup, fields, values);

				if (!result) {
					stepActual = "Error while adjusting Balance information";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 34, enabled = true)
	@Parameters({ "productGroup", "testType" })
	public void adjustCapitalRelative(@Optional(COMMERCIAL_LOANS) final String productGroup, final String testType) {
		if (BALANCE_ADJUSTMENT.equals(testType)) {
			stepDescription = "Adjust Balance information on " + arrangements[3] + " by a Relative amount";
			stepExpected = "Information adjusted successfully";
			if (arrangements[3] == null || arrangements[3].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = "ADJ.BAL.AMT:1:1," + "ADJ.BAL.AMT:2:1,";
				values = "-2000," + "-20,";

				result = arrangementActivity(CREATE_AUTHORISE, "ADJUST.BALANCE ACTIVITY FOR BALANCE.MAINTENANCE",
						arrangements[3], productGroup, fields, values);

				if (!result) {
					stepActual = "Error while adjusting Balance information";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 35, enabled = true)
	@Parameters({ "productGroup", "testType" })
	public void adjustBillFixed(@Optional(COMMERCIAL_LOANS) final String productGroup, final String testType) {
		if (BALANCE_ADJUSTMENT.equals(testType)) {
			stepDescription = "Adjust Bill information on " + arrangements[3] + " to a Fixed amount";
			stepExpected = "Information adjusted successfully";
			if (arrangements[3] == null || arrangements[3].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = "BILL.ADJ.NARR:1," + "NEW.PROP.AMT:1:1,";
				values = "Bill Fixed," + "2000,";

				result = arrangementActivity(CREATE, "ADJUST.BILL ACTIVITY FOR BALANCE.MAINTENANCE", arrangements[3],
						productGroup, fields, values);

				if (!result) {
					stepActual = "Error while adjusting Bill information";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 36, enabled = true)
	@Parameters({ "productGroup", "testType" })
	public void adjustBillRelative(@Optional(COMMERCIAL_LOANS) final String productGroup, final String testType) {
		if (BALANCE_ADJUSTMENT.equals(testType)) {
			stepDescription = "Adjust Bill information on " + arrangements[2] + " by a Relative amount";
			stepExpected = "Information adjusted successfully";
			if (arrangements[2] == null || arrangements[2].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = "BILL.ADJ.NARR:1," + "ADJ.PROP.AMT:1:1,";
				values = "Bill Relative," + "-2000,";

				result = arrangementActivity(CREATE, "ADJUST.BILL ACTIVITY FOR BALANCE.MAINTENANCE", arrangements[2],
						productGroup, fields, values);

				if (!result) {
					stepActual = "Error while adjusting Bill information";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 37, enabled = true)
	@Parameters({ "testType", "productGroup" })
	public void writeOffAll(final String testType, @Optional(COMMERCIAL_LOANS) final String productGroup) {
		if (WRITE_OFF.equals(testType)) {
			stepDescription = "Write off all balance on arrangement " + arrangements[0]
					+ " using WRITE.OFF ACTIVITY FOR BALANCE.MAINTENANCE activity";
			stepExpected = "All balance write off successfully";

			if (arrangements[0] == null || arrangements[0].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = "ADJUST.DESC," + "WRITE.OFF:1:1," + "Bill Adjustment#WRITE.OFF.BILL:1,";
				values = "Write Off All," + "YES," + "YES,";
				result = arrangementActivity(CREATE, "WRITE.OFF ACTIVITY FOR BALANCE.MAINTENANCE", arrangements[0],
						productGroup, fields, values);
				if (!result) {
					stepActual = "Error while write off all balance";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 38, enabled = true)
	@Parameters({ "testType", "productGroup" })
	public void writeOffCapital(final String testType, @Optional(COMMERCIAL_LOANS) final String productGroup) {
		if (WRITE_OFF.equals(testType)) {
			stepDescription = "Write off capital amount on arrangement " + arrangements[1]
					+ " using WRITE.OFF.BALANCE ACTIVITY FOR BALANCE.MAINTENANCE activity";
			stepExpected = "Capital amount write off successfully";

			if (arrangements[1] == null || arrangements[1].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = "WRITE.OFF:1:1," + "WOF.AMOUNT:1:1,";
				values = "YES," + "2000,";
				result = arrangementActivity(CREATE, "WRITE.OFF.BALANCE ACTIVITY FOR BALANCE.MAINTENANCE",
						arrangements[1], productGroup, fields, values);
				if (!result) {
					stepActual = "Error while write off capital amount";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}

	@Test(priority = 39, enabled = true)
	@Parameters({ "testType", "productGroup" })
	public void writeOffBill(final String testType, @Optional(COMMERCIAL_LOANS) final String productGroup) {
		if (WRITE_OFF.equals(testType)) {
			stepDescription = "Write off bill amount on arrangement " + arrangements[2]
					+ " using WRITE.OFF.BILL ACTIVITY FOR BALANCE.MAINTENANCE activity";
			stepExpected = "Bill amount write off successfully";

			if (arrangements[2] == null || arrangements[2].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {
				fields = "WRITE.OFF.BILL:1," + "BILL.ADJ.NARR:1,";
				values = "YES," + "Write Off Bill,";
				result = arrangementActivity(CREATE, "WRITE.OFF.BILL ACTIVITY FOR BALANCE.MAINTENANCE", arrangements[2],
						productGroup, fields, values);
				if (!result) {
					stepActual = "Error while write off bill amount";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as it is not applicable for this particular Test Case";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}
}