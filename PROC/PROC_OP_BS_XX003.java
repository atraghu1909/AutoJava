package testcases.PROC;

import org.openqa.selenium.support.ui.Select;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PROC_OP_BS_XX003 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.5";
	private String mainArrangement1;
	private String mainArrangement2;
	private String mainArrangement3;
	private String mainArrangement4;
	private String mainArrangement5;
	private String mainArrangement6;
	private String customer1;
	private String customer2;
	private String customer3;
	private String customer4;
	private String customer5;
	private String customer6;

	private String branch = "B2B Branch 623";

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void preCondition(@Optional(PERSONAL_ACCOUNTS) final String productGroup,
			@Optional(PERSONAL_CHEQUING) final String product) {
		String fields;
		String values;

		stepDescription = "Create six " + product + " Arrangements and transfer fund from internal account";
		stepExpected = "Six " + product
				+ " Arrangements created successfully and fund transferred successfully from internal account";

		String transactionID;

		if (loginResult) {
			switchToBranch(branch);

			customer1 = customer(CREATE, PERSONAL, ROLEBASED_BANKING, DefaultVariables.personalCIFFields,
					DefaultVariables.personalCIFValues);
			customer2 = customer(CREATE, PERSONAL, ROLEBASED_BANKING, DefaultVariables.personalCIFFields,
					DefaultVariables.personalCIFValues);
			customer3 = customer(CREATE, PERSONAL, ROLEBASED_BANKING, DefaultVariables.personalCIFFields,
					DefaultVariables.personalCIFValues);
			customer4 = customer(CREATE, PERSONAL, ROLEBASED_BANKING, DefaultVariables.personalCIFFields,
					DefaultVariables.personalCIFValues);
			customer5 = customer(CREATE, PERSONAL, ROLEBASED_BANKING, DefaultVariables.personalCIFFields,
					DefaultVariables.personalCIFValues);
			customer6 = customer(CREATE, PERSONAL, ROLEBASED_BANKING, DefaultVariables.personalCIFFields,
					DefaultVariables.personalCIFValues);

			mainArrangement1 = dataGenCreateBanking(customer1, productGroup, product, CAD, "", "",
					DefaultVariables.bankingFields, DefaultVariables.bankingValues, "");
			if (mainArrangement1 == null || mainArrangement1.contains(ERROR)) {
				stepActual = "Error while creating " + product + " Arrangement: " + mainArrangement1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			mainArrangement2 = dataGenCreateBanking(customer2, productGroup, product, CAD, "", "",
					DefaultVariables.bankingFields, DefaultVariables.bankingValues, "");
			if (mainArrangement2 == null || mainArrangement2.contains(ERROR)) {
				stepActual = "Error while creating " + product + " Arrangement: " + mainArrangement2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			mainArrangement3 = dataGenCreateBanking(customer3, productGroup, product, CAD, "", "",
					DefaultVariables.bankingFields, DefaultVariables.bankingValues, "");
			if (mainArrangement3 == null || mainArrangement3.contains(ERROR)) {
				stepActual = "Error while creating " + product + " Arrangement: " + mainArrangement3;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			mainArrangement4 = dataGenCreateBanking(customer4, productGroup, product, CAD, "", "",
					DefaultVariables.bankingFields, DefaultVariables.bankingValues, "");
			if (mainArrangement4 == null || mainArrangement4.contains(ERROR)) {
				stepActual = "Error while creating " + product + " Arrangement: " + mainArrangement4;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			mainArrangement5 = dataGenCreateBanking(customer5, productGroup, product, CAD, "", "",
					DefaultVariables.bankingFields, DefaultVariables.bankingValues, "");
			if (mainArrangement5 == null || mainArrangement5.contains(ERROR)) {
				stepActual = "Error while creating " + product + " Arrangement: " + mainArrangement5;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			mainArrangement6 = dataGenCreateBanking(customer6, productGroup, product, CAD, "", "",
					DefaultVariables.bankingFields, DefaultVariables.bankingValues, "");
			if (mainArrangement6 == null || mainArrangement6.contains(ERROR)) {
				stepActual = "Error while creating " + product + " Arrangement: " + mainArrangement6;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			if (mainArrangement1 != null || !mainArrangement1.contains(ERROR)) {
				fields = "CREDIT.AMOUNT," + "CREDIT.ACCT.NO," + "DEBIT.ACCT.NO," + "PROFIT.CENTRE.DEPT,"
						+ "ORDERING.BANK:1," + "DEBIT.CURRENCY," + "CREDIT.CURRENCY,";
				values = "50.00," + mainArrangement1 + "," + "CAD1100100017623" + "," + "1," + "Test," + "CAD,"
						+ "CAD,";
				transactionID = financialTransaction(CREATE, "Account Transfer - Transfer between Accounts", fields,
						values);
				if (transactionID.contains(ERROR)) {
					stepActual = "Error while transferring funds from internal account to banking Arrangement: "
							+ mainArrangement1;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
			if (mainArrangement2 != null || !mainArrangement2.contains(ERROR)) {
				fields = "CREDIT.AMOUNT," + "CREDIT.ACCT.NO," + "DEBIT.ACCT.NO," + "PROFIT.CENTRE.DEPT,"
						+ "ORDERING.BANK:1," + "DEBIT.CURRENCY," + "CREDIT.CURRENCY,";
				values = "50.00," + mainArrangement2 + "," + "CAD1100100017623" + "," + "1," + "Test," + "CAD,"
						+ "CAD,";
				transactionID = financialTransaction(CREATE, "Account Transfer - Transfer between Accounts", fields,
						values);
				if (transactionID.contains(ERROR)) {
					stepActual = "Error while transferring funds from internal account to banking Arrangement: "
							+ mainArrangement2;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
			if (mainArrangement3 != null || !mainArrangement3.contains(ERROR)) {
				fields = "CREDIT.AMOUNT," + "CREDIT.ACCT.NO," + "DEBIT.ACCT.NO," + "PROFIT.CENTRE.DEPT,"
						+ "ORDERING.BANK:1," + "DEBIT.CURRENCY," + "CREDIT.CURRENCY,";
				values = "50.00," + mainArrangement3 + "," + "CAD1100100017623" + "," + "1," + "Test," + "CAD,"
						+ "CAD,";
				transactionID = financialTransaction(CREATE, "Account Transfer - Transfer between Accounts", fields,
						values);
				if (transactionID.contains(ERROR)) {
					stepActual = "Error while transferring funds from internal account to banking Arrangement: "
							+ mainArrangement3;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
			if (mainArrangement4 != null || !mainArrangement4.contains(ERROR)) {
				fields = "CREDIT.AMOUNT," + "CREDIT.ACCT.NO," + "DEBIT.ACCT.NO," + "PROFIT.CENTRE.DEPT,"
						+ "ORDERING.BANK:1," + "DEBIT.CURRENCY," + "CREDIT.CURRENCY,";
				values = "50.00," + mainArrangement4 + "," + "CAD1100100017623" + "," + "1," + "Test," + "CAD,"
						+ "CAD,";
				transactionID = financialTransaction(CREATE, "Account Transfer - Transfer between Accounts", fields,
						values);
				if (transactionID.contains(ERROR)) {
					stepActual = "Error while transferring funds from internal account to banking Arrangement: "
							+ mainArrangement4;
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
	@Parameters({ "productGroup" })
	public void requestClosure(@Optional(PERSONAL_ACCOUNTS) final String productGroup) {

		stepDescription = "Perform Request Closure on all 6 arrangements";
		stepExpected = "Request Closure on all 6 arrangements performed successfully";

		boolean result;

		if (mainArrangement1 == null || mainArrangement2 == null || mainArrangement3 == null || mainArrangement4 == null
				|| mainArrangement5 == null || mainArrangement6 == null || mainArrangement1.contains(ERROR)
				|| mainArrangement2.contains(ERROR) || mainArrangement3.contains(ERROR)
				|| mainArrangement4.contains(ERROR) || mainArrangement5.contains(ERROR)
				|| mainArrangement6.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = requestClosure(SIMULATE, mainArrangement1, "Request Closure", productGroup, "CLOSURE.REASON",
					"Other", "", "", "")
					&& requestClosure(SIMULATE, mainArrangement2, "Request Closure", productGroup, "CLOSURE.REASON",
							"Other", "", "", "")
					&& requestClosure(SIMULATE, mainArrangement3, "Request Closure", productGroup, "CLOSURE.REASON",
							"Other", "", "", "")
					&& requestClosure(SIMULATE, mainArrangement4, "Request Closure", productGroup, "CLOSURE.REASON",
							"Other", "", "", "")
					&& requestClosure(SIMULATE, mainArrangement5, "Request Closure", productGroup, "CLOSURE.REASON",
							"Other", "", "", "")
					&& requestClosure(SIMULATE, mainArrangement6, "Request Closure", productGroup, "CLOSURE.REASON",
							"Other", "", "", "");

			if (!result) {
				stepActual = "Error while performing Request closure";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void waiveFees(@Optional(PERSONAL_ACCOUNTS) final String productGroup,
			@Optional(PERSONAL_CHEQUING) final String product) {

		stepDescription = "Waive fees for arrangement: " + mainArrangement6;
		stepExpected = "Fees for arrangement: " + mainArrangement6 + " Waived successfully";

		String earlyClosure;

		if (mainArrangement6 == null || mainArrangement6.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			findArrangement(AUTHORISED, mainArrangement6, ARRANGEMENT, ROLEBASED_BANKING, productGroup, product, CAD);
			toolElements.toolsButton("Payoff Statement").click();
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "PayoffbyProperty");
			earlyClosure = versionScreen.statusElement("Account Early Closure Fee").getText();
			if (earlyClosure.contains("-")) {
				versionScreen.activityAction("Account Early Closure Fee", "", "Adjust Bill").click();
				switchToPage(LASTPAGE, false);
				compositeScreen.switchToFrame(ID, "ALL");
				toolElements.toolsButton(VALIDATE_DEAL).click();
				inputData("ALL%Bill Adjustment%ALL#NEW.PROP.AMT:1:1", "0", false);
				switchToPage(LASTPAGE, false);
				compositeScreen.switchToFrame(ID, "ALL");
				toolElements.toolsButton(COMMIT_DEAL).click();
				switchToPage("Arrangement Overview (Accounts)", false);
				toolElements.toolsButton("Payoff Statement").click();
				switchToPage(LASTPAGE, false);
				compositeScreen.switchToFrame(ID, "PayoffbyProperty");
				earlyClosure = versionScreen.statusElement("Account Early Closure Fee").getText();
				if (!"0".equals(earlyClosure)) {
					stepActual = "Error while waving fees for arrangement: " + mainArrangement6;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void settleViaEFT(@Optional(PERSONAL_ACCOUNTS) final String productGroup,
			@Optional(PERSONAL_CHEQUING) final String product) {

		stepDescription = "Settle via EFT using payment order for account: " + mainArrangement1;
		stepExpected = "Payment order via EFT for account: " + mainArrangement1 + " added successfully";

		boolean result;
		String beneficiary;
		String beneficiaryFields;
		String beneficiaryValues;
		final String fields;
		final String values;
		Select dropdown;

		if (mainArrangement1 == null || mainArrangement1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			beneficiaryFields = DefaultVariables.beneficiaryFields + "OWNING.CUSTOMER," + "BEN.CUSTOMER,";
			beneficiaryValues = DefaultVariables.beneficiaryValues + customer1 + ","
					+ createdCustomers.get(customer1).getCustomerName() + ",";

			beneficiary = beneficiaryCode(CREATE, "EFT Client", "", beneficiaryFields, beneficiaryValues);
			fields = "PAYMENT.ORDER.PRODUCT," + "BENEFICIARY.ID,";
			values = "ACHACCL," + beneficiary + ",";
			findArrangement(AUTHORISED, mainArrangement1, ARRANGEMENT, ROLEBASED_BANKING, productGroup, product, CAD);
			toolElements.toolsButton("Payoff Statement").click();
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "TotalClosure");
			toolElements.toolsButton("Continue with Closure").click();
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "ALL");
			dropdown = new Select(compositeScreen.actionDropDown("", "drillbox"));
			dropdown.selectByVisibleText("Settle by Payment Order");
			versionScreen.activityAction("Choose Closure Method", "", "Select Drilldown").click();
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "ALL");
			result = multiInputData(fields, values, false);
			toolElements.toolsButton(COMMIT_DEAL).click();
			result = inputTable.verifyAcceptOverride();

			if (!result) {
				stepActual = "Error while settling Payment order via EFT for account: " + mainArrangement1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void settleViaChequeWave2(@Optional(PERSONAL_ACCOUNTS) final String productGroup,
			@Optional(PERSONAL_CHEQUING) final String product) {

		stepDescription = "Settle via Cheque using payment order (wave 2) for account: " + mainArrangement2;
		stepExpected = "Payment order via Cheque (wave 2) for account: " + mainArrangement2 + " settled successfully";

		boolean result;
		Select dropdown;

		if (mainArrangement2 == null || mainArrangement2.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			findArrangement(AUTHORISED, mainArrangement2, ARRANGEMENT, ROLEBASED_BANKING, productGroup, product, CAD);
			toolElements.toolsButton("Payoff Statement").click();
			switchToPage(LASTPAGE, false);
			versionScreen.waitForLoading();
			compositeScreen.switchToFrame(ID, "TotalClosure");
			toolElements.toolsButton("Continue with Closure").click();
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "ALL");
			dropdown = new Select(compositeScreen.actionDropDown("", "drillbox"));
			dropdown.selectByVisibleText("Settle by Funds Transfer");
			versionScreen.activityAction("Choose Closure Method", "", "Select Drilldown").click();
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "ALL");
			result = inputData("CREDIT.ACCT.NO", "CAD1110200017623", false);
			toolElements.toolsButton(COMMIT_DEAL).click();
			result = inputTable.verifyAcceptOverride();

			if (!result) {
				stepActual = "Error while settling Payment order via Cheque (wave 2) for account: " + mainArrangement2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void settleViaChequeWave3(@Optional(PERSONAL_ACCOUNTS) final String productGroup,
			@Optional(PERSONAL_CHEQUING) final String product) {

		stepDescription = "Settle via Cheque using payment order (wave 3) for account: " + mainArrangement3;
		stepExpected = "Payment order via Cheque (wave 3) for account: " + mainArrangement3 + " settled successfully";

		boolean result;
		final String fields;
		final String values;
		Select dropdown;

		if (mainArrangement3 == null || mainArrangement3.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			fields = "PAYMENT.ORDER.PRODUCT," + "BENEFICIARY.ID,";
			values = "CHQACCL," + DefaultVariables.dummyBeneficiary + ",";

			findArrangement(AUTHORISED, mainArrangement3, ARRANGEMENT, ROLEBASED_BANKING, productGroup, product, CAD);
			toolElements.toolsButton("Payoff Statement").click();
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "TotalClosure");
			toolElements.toolsButton("Continue with Closure").click();
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "ALL");
			dropdown = new Select(compositeScreen.actionDropDown("", "drillbox"));
			dropdown.selectByVisibleText("Settle by Payment Order");
			versionScreen.activityAction("Choose Closure Method", "", "Select Drilldown").click();
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "ALL");
			result = multiInputData(fields, values, false);
			toolElements.toolsButton(COMMIT_DEAL).click();
			result = inputTable.verifyAcceptOverride();

			if (!result) {
				stepActual = "Error while settling Payment order via Cheque (wave 3) for account: " + mainArrangement3;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 7, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void writeOffPositiveBalance(@Optional(PERSONAL_ACCOUNTS) final String productGroup,
			@Optional(PERSONAL_CHEQUING) final String product) {

		stepDescription = "Write-off positive balance for account: " + mainArrangement4;
		stepExpected = "Positive balance for account: " + mainArrangement4 + " written-off successfully";

		boolean result;
		Select dropdown;

		if (mainArrangement4 == null || mainArrangement4.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			findArrangement(AUTHORISED, mainArrangement4, ARRANGEMENT, ROLEBASED_BANKING, productGroup, product, CAD);
			toolElements.toolsButton("Payoff Statement").click();
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "TotalClosure");
			toolElements.toolsButton("Continue with Closure").click();
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "ALL");
			dropdown = new Select(compositeScreen.actionDropDown("", "drillbox"));
			dropdown.selectByVisibleText("Write-Off to P&L");
			versionScreen.activityAction("Choose Closure Method", "", "Select Drilldown").click();
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "ALL");
			toolElements.toolsButton(COMMIT_DEAL).click();
			result = inputTable.verifyAcceptOverride();

			if (!result) {
				stepActual = "Error while Writing-off positive balance for account: " + mainArrangement4;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 8, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void writeOffNegativeBalance(@Optional(PERSONAL_ACCOUNTS) final String productGroup,
			@Optional(PERSONAL_CHEQUING) final String product) {

		stepDescription = "Write-off negative balance for account: " + mainArrangement5;
		stepExpected = "Negative balance for account: " + mainArrangement5 + " written-off successfully";

		boolean result;
		Select dropdown;

		if (mainArrangement5 == null || mainArrangement5.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			findArrangement(AUTHORISED, mainArrangement5, ARRANGEMENT, ROLEBASED_BANKING, productGroup, product, CAD);
			toolElements.toolsButton("Payoff Statement").click();
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "TotalClosure");
			toolElements.toolsButton("Continue with Closure").click();
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "ALL");
			dropdown = new Select(compositeScreen.actionDropDown("", "drillbox"));
			dropdown.selectByVisibleText("Write-Off to P&L");
			versionScreen.activityAction("Choose Closure Method", "", "Select Drilldown").click();
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "ALL");
			toolElements.toolsButton(COMMIT_DEAL).click();
			result = inputTable.verifyAcceptOverride();

			if (!result) {
				stepActual = "Error while Writing-off negative balance for account: " + mainArrangement5;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 9, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void closeZeroBalanceAccount(@Optional(PERSONAL_ACCOUNTS) final String productGroup,
			@Optional(PERSONAL_CHEQUING) final String product) {

		stepDescription = "Close account with Zero account balance: " + mainArrangement6;
		stepExpected = "Account with Zero account balance: " + mainArrangement6 + " closed successfully";

		boolean result = false;
		String closeMessage;

		if (mainArrangement6 == null || mainArrangement6.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			findArrangement(AUTHORISED, mainArrangement6, ARRANGEMENT, ROLEBASED_BANKING, productGroup, product, CAD);
			toolElements.toolsButton("Payoff Statement").click();
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "TotalClosure");
			toolElements.toolsButton("Continue with Closure").click();
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "ALL");
			closeMessage = versionScreen.closeMessage().getText();
			if (closeMessage.contains("The Arrangement will be Closed after this step")) {
				toolElements.toolsButton(VALIDATE_DEAL).click();
				switchToPage(LASTPAGE, false);
				compositeScreen.switchToFrame(ID, "ALL");
				toolElements.toolsButton(COMMIT_DEAL).click();
				result = inputTable.verifyAcceptOverride();
			}

			if (!result) {
				stepActual = "Error while closing account with Zero account balance: " + mainArrangement6;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

		}
		softVerify.assertAll();
	}
}
