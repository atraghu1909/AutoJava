package testcases.PROC;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.DefaultVariables;

public class PROC_SAE_XX008 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "2018-06-15";
	private String customer;
	private String mainArrangement;
	private String beneficiary;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "customerType", "productGroup", "product" })
	public void createPersonalClient(@Optional("LAURENTIAN BANK - 523") final String branch, final String customerType,
			@Optional("Commercial Loans") final String productGroup, final String product) {

		stepDescription = "Create " + customerType + " Client and " + product
				+ " Arrangement. Perform repayment, change and capitalize ALNSFFEE. Hold Balance activity";
		stepExpected = customerType + " Client and " + product
				+ " Arrangement created successfully. Repayment, change and capitalize ALNSFFEE performed successfully. Balance activity was hold successfully";

		String fields;
		String values;
		String repaymentAmount;
		String adHocID;
		boolean result;

		if (loginResult) {

			switchToBranch(branch);

			final ArrangementData loanData = new ArrangementData("mainArrangement", productGroup, product)
					.withCustomers("NEW,", null, "", "100,", "100,")
					.withEffectiveDate("-88d")
					.withDisbursement()
					.withReserveAccount("NEW", true)
					.withSettlement("Beneficiary", "NEW")
					.build();

			mainArrangement = createDefaultArrangement(loanData);
			customer = loanData.getCustomers();
			beneficiary = loanData.getSettlementAccount();

			if (mainArrangement == null || mainArrangement.contains(ERROR)) {
				stepActual = "Error while creating Personal Client";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {

				repaymentAmount = arrangementBillAmount(mainArrangement, productGroup, product);
				
				fields = "CREDIT.VALUE.DATE," + "CREDIT.ACCT.NO," + "CREDIT.CURRENCY," + "DEBIT.ACCT.NO,"
						+ "CREDIT.AMOUNT," + "ORDERING.BANK:1,";
				values =  "-2m" + "," + mainArrangement + "," + CAD + "," +"CAD1100100017817,"
						+ repaymentAmount + "," + "RBC,";
				
				String transactionID = financialTransaction(CREATE, "AA Repayment", fields, values);
				if (transactionID.contains(ERROR)) {
					stepActual = "Error while repaying";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

				fields = "CREDIT.ACCOUNT," + "PAYMENT.ORDER.PRODUCT," + "PAYMENT.CURRENCY," + "PAYMENT.AMOUNT,"
						+ "DEBIT.VALUE.DATE," + "ORDERING.CUSTOMER.SSI,";
				values = mainArrangement + "," + "ACHPRDEC," + "CAD," + repaymentAmount + "," + "-1m," + beneficiary
						+ ",";

				adHocID = createAdHocPayment(CREATE, EFT, mainArrangement, fields, values);
				if (adHocID.contains(ERROR)) {
					stepActual = "Error while repaying adHoc repayment";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

				result = arrangementActivity(CREATE_AUTHORISE, "Change and Capitalise ALNSFFEE fee", mainArrangement,
						productGroup, "", "");
				if (!result) {
					stepActual = "Error while performing Change and Capitalise ALNSFFEE fee activity";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

				result = arrangementActivity(OPEN, "CAPTURE.BILL ACTIVITY FOR BALANCE.MAINTENANCE", mainArrangement,
						productGroup, "", "");
				toolElements.toolsButton(HOLD).click();
				if (!result) {
					stepActual = "Error while holding CAPTURE.BILL ACTIVITY FOR BALANCE.MAINTENANCE activity";
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
	@Parameters("productGroup")
	public void arrangementOverview(@Optional("Commercial Loans") final String productGroup) {

		stepDescription = "Open arrangement overview for Client: " + customer;
		stepExpected = "Arrangement overview for Client: " + customer + " opened successfully";

		String arrangement;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-condition failed";
			throw new SkipException(stepActual);
		} else {

			arrangement = findArrangement(AUTHORISED, customer, CIF, ROLEBASED_SAE, productGroup, "", "");

			if (!arrangement.equals(mainArrangement)) {
				stepActual = "Error while Openning arrangement overview";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	public void basicInformation() {

		stepDescription = "Validate Client's Basic Informations";
		stepExpected = "Client's Basic Informations are validated successfully";

		String account;
		String arrangement;
		String product;
		String owner;
		String arrangementDate;
		String status;
		String transit;
		String CIF;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-condition failed";
			throw new SkipException(stepActual);
		} else {
			try {
				account = versionScreen.labelElement("Account").getText();
				arrangement = versionScreen.labelElement(ARRANGEMENT).getText();
				product = versionScreen.labelElement("Product").getText();
				owner = versionScreen.labelElement("Owner").getText();
				arrangementDate = versionScreen.labelElement("Arrangement Date").getText();
				status = versionScreen.labelElement("Status").getText();
				transit = versionScreen.labelElement("Transit").getText();
				CIF = versionScreen.labelElement("CIF").getText();
				toolElements.toolsButton("View Bundle Details").click();

				if (account == null || arrangement == null || product == null || owner == null || status == null
						|| arrangementDate == null || CIF == null || transit == null || "".equals(account)
						|| "".equals(arrangement) || "".equals(product) || "".equals(owner) || "".equals(status)
						|| "".equals(arrangementDate) || "".equals(transit) || "".equals(CIF)) {
					stepActual = "Error while validating Client's Basic Informations";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

			} catch (NoSuchElementException e) {
				stepActual = "Error while validating Client's Basic Informations";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters("productGroup")
	public void arrangementConditions(@Optional("Commercial Loans") final String productGroup) {

		stepDescription = "Validate Client's Arrangement Conditions";
		stepExpected = "Client's Arrangement Conditions validated successfully";

		String title;
		String amount;
		String term;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			try {

				switchToPage("Arrangement Overview (Lending)", false);

				title = versionScreen.versionElement("Account Static", "Title", "1").getText();
				amount = versionScreen.versionElement("Commitment", "Amount", "1").getText();
				term = versionScreen.versionElement("Commitment", "Term", "1").getText();
				versionScreen.enquiry("Interest").getText().contains("Principal Interest");
				versionScreen.enquiry("PaymentSchedule").getText().contains("Schedule");

				versionScreen.linkText("Arrangement Conditions", "Additional").click();
				versionScreen.enquiryList("Charge");

				toolElements.toolsButton("View Arrangement").click();
				switchToPage(LASTPAGE, false);

				try {
					if (versionScreen.inputElements().getAttribute("type").equals("text")) {
						stepActual = "Version screen is not read only";
						softVerify.fail(stepActual);
						stepResult = StatusAs.FAILED;
					}
				} catch (NoSuchElementException e) {
					stepActual = "Version screen is read only";
					stepResult = StatusAs.PASSED;
				}

				if (title == null || amount == null || term == null || "".equals(title) || "".equals(amount)
						|| "".equals(term)) {
					stepActual = "Error while Client's validating Basic Informations";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

			} catch (NoSuchElementException e) {
				stepActual = "Error while validating Client's Basic Informations";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	@Parameters("productGroup")
	public void limit(@Optional("Commercial Loans") final String productGroup) {

		stepDescription = "Validate Client's Limit Details";
		stepExpected = "Client's Limit Details are validated successfully";

		String type;
		String CCY;
		String amount;
		String secured;
		String outstandingPrincipal;
		String availableExcess;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			switchToPage("Arrangement Overview (Lending)", false);

			try {
				type = versionScreen.getEnqListCell("r2_Limit", 0, "1").getText();
				CCY = versionScreen.getEnqListCell("r2_Limit", 0, "2").getText();
				amount = versionScreen.getEnqListCell("r2_Limit", 0, "3").getText();
				secured = versionScreen.getEnqListCell("r2_Limit", 0, "4").getText();
				outstandingPrincipal = versionScreen.getEnqListCell("Limit", 2, "5").getText();
				availableExcess = versionScreen.getEnqListCell("Limit", 2, "6").getText();

				versionScreen.limitView().click();
				switchToPage(LASTPAGE, false);
				try {
					if (versionScreen.inputElements().getAttribute("type").equals("text")) {
						stepActual = "Version screen is not read only";
						softVerify.fail(stepActual);
						stepResult = StatusAs.FAILED;
					}
				} catch (NoSuchElementException e) {
					stepActual = "Version screen is read only";
					stepResult = StatusAs.PASSED;
				}

				if (type == null || CCY == null || amount == null || secured == null || outstandingPrincipal == null
						|| availableExcess == null || "".equals(type) || "".equals(CCY) || "".equals(amount)
						|| "".equals(secured) || "".equals(outstandingPrincipal) || "".equals(availableExcess)) {
					stepActual = "Error while validating Client's Limit Details";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

			} catch (NoSuchElementException e) {
				stepActual = "Error while validating Client's Limit Details";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	@Parameters("productGroup")
	public void collateralDetails(@Optional("Commercial Loans") final String productGroup) {

		stepDescription = "Validate Client's Collateral Details";
		stepExpected = "Client's Collateral Details are validated successfully";

		String executionValue;
		String nominalValue;
		String netAmount;
		String thirdPartyAmount;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			try {
				switchToPage("Arrangement Overview (Lending)", false);
				toolElements.toolsButton("Nominal").click();
				toolElements.toolsButton("Customer").click();
				executionValue = versionScreen.getEnqListCell("r1_Collateral", 0, "3").getText();
				nominalValue = versionScreen.getEnqListCell("r1_Collateral", 0, "6").getText();
				netAmount = versionScreen.getEnqListCell("r1_Collateral", 0, "5").getText();
				thirdPartyAmount = versionScreen.getEnqListCell("r1_Collateral", 0, "4").getText();

				if (executionValue == null || nominalValue == null || netAmount == null || thirdPartyAmount == null
						|| "".equals(executionValue) || "".equals(nominalValue) || "".equals(netAmount)
						|| "".equals(thirdPartyAmount)) {
					stepActual = "Error while validating Client's Collateral Details";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			} catch (NoSuchElementException e) {
				stepActual = "Error while validating Client's Collateral Details";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 7, enabled = true)
	public void accountDetails() {

		stepDescription = "Validate Client's Account Details";
		stepExpected = "Client's Account Details validated successfully";

		String overdueStatus;
		String Commitment;
		String totalPrincipal;
		String totalDelinquentBalance;
		String principalCurrent;
		String reverseAccountDeliquent;
		String principalDelinquent;
		String startDate;
		String effectiveDate;
		String nextPayment;
		String maturityDate;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			try {
				overdueStatus = versionScreen.financialSummaryStatusElement().getText();
				Commitment = versionScreen.labelElement("Commitment (Total)").getText();
				totalPrincipal = versionScreen.labelElement("Total Principal").getText();
				totalDelinquentBalance = versionScreen.labelElement("Total Delinquent Balance").getText();

				if (overdueStatus == null || Commitment == null || totalPrincipal == null
						|| totalDelinquentBalance == null || "".equals(overdueStatus) || "".equals(Commitment)
						|| "".equals(totalPrincipal) || "".equals(totalDelinquentBalance)) {
					stepActual = "Error while validating Client's Financial Details";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				} else {
					compositeScreen.textAction("Total Principal", "Balance Details").click();
					principalCurrent = versionScreen.labelElement("Principal (Current)").getText();
					principalDelinquent = versionScreen.labelElement("Principal (Delinquent)").getText();

					if (principalCurrent == null || principalDelinquent == null || "".equals(principalCurrent)
							|| "".equals(principalDelinquent)) {
						stepActual = "Error while validating Client's Financial Details";
						softVerify.fail(stepActual);
						stepResult = StatusAs.FAILED;
					}

					compositeScreen.textAction("Total Delinquent Balance", "Balance Details").click();
					reverseAccountDeliquent = versionScreen.labelElement("Reserve Account (Delinquent)").getText();
					principalDelinquent = versionScreen.labelElement("Principal (Delinquent)").getText();

					if (reverseAccountDeliquent == null || principalDelinquent == null
							|| "".equals(reverseAccountDeliquent) || "".equals(principalDelinquent)) {
						stepActual = "Error while validating Client's Financial Details";
						softVerify.fail(stepActual);
						stepResult = StatusAs.FAILED;
					}

					startDate = versionScreen.labelElement("Start Date").getText();
					effectiveDate = versionScreen.labelElement("Effective Date").getText();
					nextPayment = versionScreen.labelElement("Next Payment").getText();
					maturityDate = versionScreen.labelElement("Maturity Date").getText();

					if (startDate == null || effectiveDate == null || nextPayment == null || maturityDate == null
							|| "".equals(startDate) || "".equals(effectiveDate) || "".equals(nextPayment)
							|| "".equals(maturityDate)) {
						stepActual = "Error while validating Client's Account Dates";
						softVerify.fail(stepActual);
						stepResult = StatusAs.FAILED;
					}

					try {
						compositeScreen.textAction("Request Payoff", "Run");
						versionScreen.linkText("Facilities", "Block Funds");
					} catch (NoSuchElementException e) {
						stepActual = "Error while validating Client's Account Details";
						softVerify.fail(stepActual);
						stepResult = StatusAs.FAILED;
					}
				}

			} catch (NoSuchElementException e) {
				stepActual = "Error while validating Client's Account Details";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 8, enabled = true)
	public void additionalDetails() {

		stepDescription = "Validate Client's Additional Details";
		stepExpected = "Client's Additional Details validated successfully";

		String dueDate;
		String type;
		String amount;
		String settledOn;
		String arrangement;
		String paymentDate;
		String currency;
		String creditOrDebit;
		String totalBilled;
		String outStanding;
		String delinquency;
		String heading = "";
		String additionalDetailsData = "//table[contains(@id,'datadisplay_AdditionalDetails')]//.//tr";
		String activityLogData = "//table[contains(@id,'datadisplay_Log')]//.//tr";
		String[] columnHeader;
		int size;
		boolean result;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			try {
				
				versionScreen.linkText(ADDITIONAL_DETAILS, ACTIVITY).click();
				versionScreen.linkText(ACTIVITY_LOG, "Full").click();
				driver.findElements(By.xpath(activityLogData));
				switchToPage("Arrangement Overview (Lending)", false);
				versionScreen.waitForLoading();
				result = versionScreen.activityCheck("DISBURSE ACTIVITY FOR COMMITMENT", AUTHORISED);
				if (result) {
					try {
						versionScreen.activityAction("DISBURSE ACTIVITY FOR COMMITMENT", AUTHORISED, VIEW);
						versionScreen.activityAction("DISBURSE ACTIVITY FOR COMMITMENT", AUTHORISED, REVERSE);
					} catch (NoSuchElementException e) {
						stepActual = "Error while validating action buttons for Disbursement activity";
						softVerify.fail(stepActual);
						stepResult = StatusAs.FAILED;
					}
				} else {
					stepActual = "Error while validating Disbursement activity";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

				versionScreen.linkText(ACTIVITY_LOG, FINANCIAL).click();
				driver.findElements(By.xpath(activityLogData));

				versionScreen.linkText(ACTIVITY_LOG, "User").click();
				driver.findElements(By.xpath(activityLogData));

				versionScreen.linkText(ACTIVITY_LOG, "System").click();
				driver.findElements(By.xpath(activityLogData));

				versionScreen.linkText(ACTIVITY_LOG, "Saved Activities").click();
				switchToPage(LASTPAGE, false);
				result = versionScreen.rowCell("r", "3").getText()
						.equals("CAPTURE.BILL ACTIVITY FOR BALANCE.MAINTENANCE");
				if (result) {
					try {
						versionScreen.activityAction("CAPTURE.BILL ACTIVITY FOR BALANCE.MAINTENANCE", "IHLD", VIEW);
						versionScreen.activityAction("CAPTURE.BILL ACTIVITY FOR BALANCE.MAINTENANCE", "IHLD", EDIT);
					} catch (NoSuchElementException e) {
						stepActual = "Error while validating action buttons for HOLD activity";
						softVerify.fail(stepActual);
						stepResult = StatusAs.FAILED;
					}
				} else {
					stepActual = "Error while validating HOLD Activity";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
				switchToPage("Arrangement Overview (Lending)", false);
				versionScreen.linkText(ACTIVITY_LOG, "History").click();
				driver.findElements(By.xpath(activityLogData));

				versionScreen.linkText(ACTIVITY_LOG, "Txn History").click();

				switchToPage(LASTPAGE, false);
				driver.findElements(By.xpath("//table[contains(@id,'datadisplay')]//.//tr"));
				versionScreen.rowElements("select");
				versionScreen.rowElements("a");

				switchToPage("Arrangement Overview (Lending)", false);

				versionScreen.linkText(ADDITIONAL_DETAILS, "Bills").click();
				versionScreen.waitForLoading();
				versionScreen.linkText("Bills", "Type");
				versionScreen.linkText("Bills", "Consolidated");
				dueDate = versionScreen.paymentElement("Repayments", "Due Date", "2").getText();
				type = versionScreen.paymentElement("Repayments", "Type", "2").getText();
				amount = versionScreen.paymentElement("Repayments", "Amount", "2").getText();
				settledOn = versionScreen.paymentElement("Repayments", "Settled On", "2").getText();

				if (dueDate == null || type == null || amount == null || settledOn == null || "".equals(dueDate)
						|| "".equals(type) || "".equals(amount) || "".equals(settledOn)) {
					stepActual = "Error while validating Client's Repayments";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

				driver.findElement(By.xpath("//td[text()='" + dueDate + "']//following-sibling::*/a/img")).click();
				versionScreen.waitForLoading();
				arrangement = readTable.readElement("Arrangement", "2").getText();
				paymentDate = readTable.readElement("Payment Date", "2").getText();
				currency = readTable.readElement("Currency", "2").getText();
				creditOrDebit = readTable.readElement("Credit or Debit", "2").getText();
				totalBilled = readTable.readElement("Total Billed", "2").getText();
				outStanding = readTable.readElement("Outstanding", "2").getText();
				delinquency = readTable.readElement("Delinquency", "2").getText();
				if (arrangement == null || paymentDate == null || currency == null || creditOrDebit == null
						|| totalBilled == null || outStanding == null || delinquency == null || "".equals(arrangement)
						|| "".equals(paymentDate) || "".equals(currency) || "".equals(creditOrDebit)
						|| "".equals(totalBilled) || "".equals(outStanding) || "".equals(delinquency)) {
					stepActual = "Error while validating Client's Repayments";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

				switchToTab("Itemised by Property", "");
				driver.findElements(By.xpath("//table[@id='tab1']"));
				switchToTab("Itemised by Payment Type", "");
				driver.findElements(By.xpath("//table[@id='tab2']"));
				switchToTab("Status Timeline", "");
				driver.findElements(By.xpath("//table[@id='tab3']"));

				versionScreen.linkText(ADDITIONAL_DETAILS, "Charges").click();
				versionScreen.waitForLoading();
				versionScreen.linkText("Charges", "Date");
				versionScreen.linkText("Charges", "Type").click();

				compositeScreen.expandButton("NSF Fee").click();
				driver.findElement(By.xpath("//td[text()='NSF Fee']//following-sibling::*/a/img")).click();

				versionScreen.linkText(ADDITIONAL_DETAILS, "Messages").click();
				driver.findElements(By.xpath(additionalDetailsData));

				versionScreen.linkText(ADDITIONAL_DETAILS, "Schedule").click();
				versionScreen.headerElement("AdditionalDetails", "2", "1").getText().equals("Payment Schedule");
				size = versionScreen.columnHeaderElements("AdditionalDetails").size();
				for (int i = 0; i < size; i++) {
					columnHeader = new String[size];
					columnHeader[i] = versionScreen.columnHeaderElement("AdditionalDetails", i + 1).getText();
					heading += " " + columnHeader[i];
				}
				if (!heading.contains("Date") || !heading.contains("Total Due") || !heading.contains("Total Cap")
						|| !heading.contains("Principal") || !heading.contains("Interest")
						|| !heading.contains("Charge") || !heading.contains("Outstanding")) {
					stepActual = "Error while validating Payment Schedule";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
				driver.findElement(By.xpath("//td[text()='" + amount + "']//following-sibling::*/a/img")).click();

				versionScreen.linkText(ADDITIONAL_DETAILS, "Overdue").click();
				driver.findElements(By.xpath(additionalDetailsData));

				versionScreen.linkText(ADDITIONAL_DETAILS, "Sims").click();
				toolElements.launchButton("launch").click();

				versionScreen.linkText(ADDITIONAL_DETAILS, "Payment Orders").click();
				versionScreen.linkText("Payment Orders", "Receipts");
				versionScreen.linkText("Payment Orders", "Payments");
				size = versionScreen.columnHeaderElements("EnquiryFrame").size();
				heading = "";
				for (int i = 0; i < size; i++) {
					columnHeader = new String[size];
					columnHeader[i] = versionScreen.columnHeaderElement("EnquiryFrame", i + 1).getText();
					heading += " " + columnHeader[i];
				}
				if (!heading.contains("Reference") || !heading.contains("Amount") || !heading.contains("Value Date")) {
					stepActual = "Error while validating Payment Orders";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

			} catch (NoSuchElementException e) {
				stepActual = "Error while validating Additional Details section";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 9, enabled = true)
	@Parameters("productGroup")
	public void memosOverview(@Optional("Commercial Loans") final String productGroup) {

		stepDescription = "Create, view, amend and reverse arrangement memo";
		stepExpected = "Create, view, amend and reverse arrangement memo performed successfully";

		String fields = "MEMO.TEXT,";
		String values = "Memo has been created for this arrangement,";
		boolean result;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementMemo(CREATE, mainArrangement, productGroup, fields, values);
			if (!result) {
				stepActual = "Error while creating arrangement memo";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			
			result = arrangementMemo(VIEW, mainArrangement, productGroup, "", "");
			toolElements.toolsButton(RETURN_TO_SCREEN).click();
			if (!result) {
				stepActual = "Error while viewing arrangement memo";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			values = "Memo has been updated for this arrangement,";
			result = arrangementMemo(AMEND, mainArrangement, productGroup, fields, values);
			if (!result) {
				stepActual = "Error while amending arrangement memo";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			
			result = arrangementMemo(REVERSE, mainArrangement, productGroup, "", "");
			if (!result) {
				stepActual = "Error while reversing arrangement memo";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
