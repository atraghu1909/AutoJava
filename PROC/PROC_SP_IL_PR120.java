package testcases.PROC;

import org.openqa.selenium.NoSuchElementException;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.DefaultVariables;

public class PROC_SP_IL_PR120 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.7";
	private final String paymentFields = "CREDIT.CURRENCY," + "DEBIT.ACCT.NO," + "ORDERING.BANK:1,";
	private final String paymentValues = "CAD," + "CAD1100100017817," + "customer,";
	private String mainArrangement1;
	private String mainArrangement2;
	private String mainArrangement3;
	private String mainArrangement4;
	private String customer1;
	private String customer2;
	private String customer3;
	private String customer4;
	private String branch = "B2B Branch 817";
	private String customerType;
	private String overpayAbove5;
	private String overpayBelow5;
	private String underpayAbove5;
	private String underpayBelow5;
	private boolean result;

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void preCondition(final String productGroup, final String product) {

		stepDescription = "Create four Delinquent " + product + " Arrangements";
		stepExpected = "Four Delinquent " + product + " Arrangements created successfully";

		final ArrangementData loanData = new ArrangementData("mainArrangement1", productGroup, product)
				.withEffectiveDate("-45d")
				.withTerm("1Y")
				.withDisbursement()
				.withRepayments()
				.withStatement("NEW", "Input Genral Stmt Request")
				.build();

		String payoffDate;
		Double payoffAmount;
		int amountColumnIndex;
		int currentPayoffRowIndex;

		if (loginResult) {
			switchToBranch(branch);

			customerType = DefaultVariables.productGroupCustomerType.get(productGroup);

			mainArrangement1 = createDefaultArrangement(loanData);
			customer1 = loanData.getCustomers();
			mainArrangement2 = createDefaultArrangement(loanData);
			customer2 = loanData.getCustomers();
			mainArrangement3 = createDefaultArrangement(loanData);
			customer3 = loanData.getCustomers();
			mainArrangement4 = createDefaultArrangement(loanData);
			customer4 = loanData.getCustomers();

			findArrangement(AUTHORISED, customer1, CIF, ROLEBASED_LENDING, productGroup, product, CAD);
			switchToPage(LASTPAGE, false);
			payoffDate = versionScreen.labelElement("Payoff Statement for").getText();
			versionScreen.activityAction("Payoff Statement for", "", "Payoff Statement").click();
			switchToPage(LASTPAGE, false);
			versionScreen.waitForLoading();
			amountColumnIndex = enquiryElements.getColumnHeaderNumber("Payoff Total", "Amount");
			currentPayoffRowIndex = enquiryElements.getRowNumberMatching("Payoff Total", "Payoff Date", payoffDate);
			payoffAmount = new Double(enquiryElements
					.getElementAtCell("Payoff Total", "PayoffTotal", amountColumnIndex, currentPayoffRowIndex).getText()
					.replaceAll(",", ""));
			overpayAbove5 = Double.toString(Double.sum(payoffAmount, new Double(7.50)));
			overpayBelow5 = Double.toString(Double.sum(payoffAmount, new Double(2.50)));
			underpayAbove5 = Double.toString(Double.sum(payoffAmount, new Double(-7.50)));
			underpayBelow5 = Double.toString(Double.sum(payoffAmount, new Double(-2.50)));

			if (mainArrangement1 == null || mainArrangement1.contains(ERROR) || mainArrangement2 == null
					|| mainArrangement2.contains(ERROR) || mainArrangement3 == null || mainArrangement3.contains(ERROR)
					|| mainArrangement4 == null || mainArrangement4.contains(ERROR)) {
				stepActual = "Error while creating one of " + product + " Arrangements";
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
	@Parameters({ "productGroup", "product" })
	public void handleOverPaymentOver5Dollar(final String productGroup, final String product) {

		stepDescription = "Overpay arrangement:" + mainArrangement1 + " by more than $5 ";
		stepExpected = "A UNC Balance of more than $5 is displayed on arrangement: " + mainArrangement1;

		String balanceAmount;

		if (mainArrangement1 == null || mainArrangement1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Error while creating Arrangement1: " + mainArrangement1;
			throw new SkipException(stepActual);
		} else if (arrangementAction(mainArrangement1, customer1, ROLEBASED_LENDING, "Lending Repayment",
				paymentFields + "CREDIT.AMOUNT,", paymentValues + overpayAbove5 + ",", customerType)) {

			try {
				findArrangement(AUTHORISED, customer1, CIF, ROLEBASED_LENDING, productGroup, product, CAD);
				switchToPage(LASTPAGE, false);
				balanceAmount = versionScreen.statusElement("Unspecified Credit").getText().replaceAll(",", "");
				stepActual = "Unspecified Credit: " + balanceAmount;
			} catch (NoSuchElementException e) {
				balanceAmount = "";
				stepActual = "Error retrieving Unspecified Credit from arrangement " + mainArrangement1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			if (new Double(balanceAmount).intValue() < 5) {
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		} else {
			stepActual = "Error while overpaying arrangement: " + mainArrangement1 + " by more than $5 ";
			softVerify.fail(stepActual);
			stepResult = StatusAs.FAILED;
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void handleOverPaymentUnder5Dollar(final String productGroup, final String product) {

		stepDescription = "Overpay arrangement:" + mainArrangement2 + " by less than $5 ";
		stepExpected = "A UNC Balance of less than $5 is displayed on arrangement: " + mainArrangement2;

		String balanceAmount;

		if (mainArrangement2 == null || mainArrangement2.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Error while creating Arrangement2: " + mainArrangement2;
			throw new SkipException(stepActual);
		} else if (arrangementAction(mainArrangement2, customer2, ROLEBASED_LENDING, "Lending Repayment",
				paymentFields + "CREDIT.AMOUNT,", paymentValues + overpayBelow5 + ",", customerType)) {

			try {
				findArrangement(AUTHORISED, customer2, CIF, ROLEBASED_LENDING, productGroup, product, CAD);
				switchToPage(LASTPAGE, false);
				balanceAmount = versionScreen.statusElement("Unspecified Credit").getText().replaceAll(",", "");
				stepActual = "Unspecified Credit: " + balanceAmount;

				if (new Double(balanceAmount).intValue() > 5) {
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			} catch (NoSuchElementException e) {
				balanceAmount = "";
				stepActual = "Error retrieving Unspecified Credit from arrangement " + mainArrangement2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		} else {
			stepActual = "Error while overpaying arrangement: " + mainArrangement2 + " by less than $5 ";
			softVerify.fail(stepActual);
			stepResult = StatusAs.FAILED;
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void handleResidualBalanceOver5Dollar(final String productGroup, final String product) {

		stepDescription = "Underpay arrangement: " + mainArrangement3 + " by more than $5";
		stepExpected = "A Residual Balance of more than $5 remains on arrangement: " + mainArrangement3;

		String balanceAmount;

		if (mainArrangement3 == null || mainArrangement3.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Error while creating Arrangement3: " + mainArrangement3;
			throw new SkipException(stepActual);
		} else if (arrangementAction(mainArrangement3, customer3, ROLEBASED_LENDING, "Lending Repayment",
				paymentFields + "CREDIT.AMOUNT,", paymentValues + underpayAbove5 + ",", customerType)) {

			try {
				findArrangement(AUTHORISED, customer3, CIF, ROLEBASED_LENDING, productGroup, product, CAD);
				switchToPage(LASTPAGE, false);
				balanceAmount = versionScreen.labelElement("Principal Interest (Accrued)").getText().replaceAll(",",
						"");
				stepActual = "Residual Balance: " + balanceAmount;

				if (new Double(balanceAmount).intValue() < 5) {
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			} catch (NoSuchElementException e) {
				balanceAmount = "";
				stepActual = "Error retrieving Residual Balance from arrangement " + mainArrangement3;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		} else {
			stepActual = "Error while underpaying arrangement: " + mainArrangement3 + " by more than $5";
			softVerify.fail(stepActual);
			stepResult = StatusAs.FAILED;
		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void handleResidualBalanceUnder5Dollar(final String productGroup, final String product) {

		stepDescription = "Underpay arrangement: " + mainArrangement4 + " by less than $5";
		stepExpected = "A Residual Balance of less than $5 remains on arrangement: " + mainArrangement4;

		String balanceAmount;

		if (mainArrangement4 == null || mainArrangement4.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Error while creating Arrangement4: " + mainArrangement4;
			throw new SkipException(stepActual);
		} else if (arrangementAction(mainArrangement4, customer4, ROLEBASED_LENDING, "Lending Repayment",
				paymentFields + "CREDIT.AMOUNT,", paymentValues + underpayBelow5 + ",", customerType)) {

			try {
				findArrangement(AUTHORISED, customer4, CIF, ROLEBASED_LENDING, productGroup, product, CAD);
				switchToPage(LASTPAGE, false);
				balanceAmount = versionScreen.labelElement("Principal Interest (Accrued)").getText().replaceAll(",",
						"");
				stepActual = "Residual Balance: " + balanceAmount;

				if (new Double(balanceAmount).intValue() > 5) {
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			} catch (NoSuchElementException e) {
				balanceAmount = "";
				stepActual = "Error retrieving Residual Balance from arrangement " + mainArrangement4;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		} else {
			stepActual = "Error while underpaying arrangement: " + mainArrangement4 + " by less than $5";
			softVerify.fail(stepActual);
			stepResult = StatusAs.FAILED;
		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	@Parameters({ "productGroup" })
	public void tearDown(final String productGroup) {

		stepDescription = "Handle overpayments and underpayments on all test arrangements";
		stepExpected = "Overpayments and underpayments are handled successfully";
		
		if (mainArrangement1 == null || mainArrangement1.contains(ERROR) || mainArrangement2 == null
				|| mainArrangement2.contains(ERROR) || mainArrangement3 == null || mainArrangement3.contains(ERROR)
				|| mainArrangement4 == null || mainArrangement4.contains(ERROR)) {
			stepActual = "Step not executed, as pre-condition failed";
			stepResult = StatusAs.NOT_COMPLETED;
			throw new SkipException(stepActual);
		} else {
			final String extensionFields = "EFFECTIVE.DATE," + "MATURITY.DATE,";
			final String extensionValues = "+1d," + "+3m,";
			final String writeoffFields = "WRITE.OFF:1:1,";
			final String writeoffValues = "2.50,";

			result = refundExcessFunds(customer1, mainArrangement1, customerType, productGroup, "EFT",
					ROLEBASED_LENDING)
					&& refundExcessFunds(customer2, mainArrangement2, customerType, productGroup, "Cheque",
							ROLEBASED_LENDING)
					&& arrangementActivity(CREATE_AUTHORISE, "Future Dated P Loan P&I Term Ext.", mainArrangement3,
							productGroup, extensionFields, extensionValues)
					&& arrangementActivity(CREATE_AUTHORISE, "WRITE.OFF ACTIVITY FOR BALANCE.MAINTENANCE",
							mainArrangement4, productGroup, writeoffFields, writeoffValues);

			if (!result) {
				stepActual = "Error while settling pending transactions on one of the test arrangements";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
	}
}
