package testLibs;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebElement;
import org.testng.Reporter;

public class BaseTest_DataGen extends BaseTest_OOB {
	public String payoffInterest = "";
	public String payoffAmount = "";

	public String dataGenCreateBanking(final String customer, final String productGroup, final String product,
			final String currency, final String step1Fields, final String step1Values, final String step2Fields,
			final String step2Values, final String status) {
		final String actualStep1Fields = step1Fields + "CUSTOMER:1," + "CURRENCY,";
		final String actualStep1Values = step1Values + customer + "," + currency + ",";
		String result;
		String mainArrangement;

		mainArrangement = arrangements(CREATE, productGroup, product, ROLEBASED_BANKING, customer, actualStep1Fields,
				actualStep1Values, step2Fields, step2Values);

		result = mainArrangement;
		return result;
	}

	public String dataGenCreateDeposit(final String customer, final String productGroup, final String product,
			final String currency, final String step1Fields, final String step1Values, final String step2Fields,
			final String step2Values, final String status, final String effectiveDate,
			final String maturityInstruction) {
		final String actualStep1Fields = step1Fields + "CUSTOMER:1," + "CURRENCY," + "EFFECTIVE.DATE,";
		final String actualStep1Values = step1Values + customer + "," + currency + "," + effectiveDate + ",";
		String result;
		String fundsTransferFields;
		String fundsTransferValues;
		String period;
		Pattern pattern;
		Matcher matcher;
		String mainArrangement;
		String chequingArrangement;
		String actualStep2Fields;
		String actualStep2Values;
		String redemptionEntity;
		String fundEntity;

		final StringBuilder step2FieldsBuilder = new StringBuilder(step2Fields);
		final StringBuilder step2ValuesBuilder = new StringBuilder(step2Values);

		if ("Rollover".equals(maturityInstruction)) {
			pattern = Pattern.compile("\\d+(\\s*)(Y)");
			matcher = pattern.matcher(product);

			if (matcher.find()) {
				period = product.substring(matcher.start(), matcher.end() - 1).trim() + "Y";
			} else {
				period = "6M";
			}

			actualStep2Fields = step2Fields + "TERM," + "CHANGE.DATE.TYPE," + "CHANGE.PERIOD," + "INITIATION.TYPE,";
			actualStep2Values = step2Values + "," + "Period," + period + "," + "Auto,";
		}

		// values need to be changed
		if (status.contains("via B2B Chequing")) {
			chequingArrangement = findArrangement(AUTHORISED, customer, CIF, "", PERSONAL_ACCOUNTS, PERSONAL_CHEQUING,
					currency);
			fundEntity = chequingArrangement;
			redemptionEntity = chequingArrangement;
		} else if (status.contains("via Cheque")) {
			fundEntity = "CAD1110200017817";
			redemptionEntity = "CAD1100100017817";
		} else {
			fundEntity = "CAD1160200017607";
			redemptionEntity = "CAD1100100017817";
		}

		actualStep2Fields = step2FieldsBuilder.toString();
		actualStep2Values = step2ValuesBuilder.toString();

		mainArrangement = arrangements(CREATE, productGroup, product, ROLEBASED_BANKING, customer, actualStep1Fields,
				actualStep1Values, actualStep2Fields, actualStep2Values);

		if (!mainArrangement.contains(ERROR) && "Funded".equals(status)) {

			fundsTransferFields = "CREDIT.ACCT.NO," + "CREDIT.CURRENCY," + "DEBIT.ACCT.NO," + "CREDIT.AMOUNT,"
					+ "CREDIT.VALUE.DATE,";
			fundsTransferValues = mainArrangement + "," + currency + "," + fundEntity + "," + "100000," + effectiveDate
					+ ",";
			financialTransaction(CREATE_AUTHORISE, "AA Deposit - Fund", fundsTransferFields, fundsTransferValues);
		}
		result = mainArrangement;

		return result;
	}

	public String dataGenCreateLoan(final String customer, final String productGroup, final String product,
			final String currency, final String step1Fields, final String step1Values, final String step2Fields,
			final String step2Values, final String status, final String effectiveDate) {
		final String actualStep1Fields = step1Fields + "CUSTOMER:1," + "CURRENCY," + "EFFECTIVE.DATE,";
		final String actualStep1Values = step1Values + customer + "," + currency + "," + effectiveDate + ",";
		WebElement commitmentElement;
		String mainArrangement;
		String chequingArrangement;
		String disbursementEntity;
		String repaymentEntity;
		String type = "";
		String amount = "";
		String fundsTransferFields;
		String fundsTransferValues;
		String acitivityUpdateFields;
		String acitivityUpdateValues;
		boolean result;

		mainArrangement = arrangements(CREATE, productGroup, product, ROLEBASED_LENDING, customer, actualStep1Fields,
				actualStep1Values, step2Fields, step2Values);

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			Reporter.log("Error in creating Loan");
		} else {
			if (status.contains("via B2B Chequing")) {
				chequingArrangement = findArrangement(AUTHORISED, customer, CIF, "", PERSONAL_ACCOUNTS,
						PERSONAL_CHEQUING, currency);
				disbursementEntity = chequingArrangement;
				repaymentEntity = chequingArrangement;
			} else if (status.contains("via Cheque")) {
				type = "via Cheque";
				disbursementEntity = "CAD1100000017817";
				repaymentEntity = "CAD1100100017817";
			} else {
				type = "via Internal Account";
				disbursementEntity = "CAD1100000017817";
				repaymentEntity = "CAD1100100017817";
			}

			if (status.contains("Delinquent") || status.contains("Current") || status.contains("Paid Out")
					|| status.contains("Rewrite") || status.contains("RTP")) {
				findArrangement(AUTHORISED, mainArrangement, ARRANGEMENT, "", productGroup, product, currency);
				switchToPage(LASTPAGE, false);

				commitmentElement = versionScreen.statusElement("Available Commitment");

				if (commitmentElement == null) {
					result = false;
				} else {
					amount = Integer.toString(
							Integer.parseInt(commitmentElement.getText().replaceAll(",", "").replaceAll("\\.0*$", "")));
					result = true;
				}

				if (result) {
					result = disburseArrangement(type, mainArrangement, disbursementEntity, currency, amount,
							effectiveDate, customer, productGroup);
				}

				if (result) {

					switch (status) {

					case "Current":
					case "Current via Cheque":
					case "Current via EFT":
					case "Current With Payment":

						repayArrangement(repaymentEntity, mainArrangement, currency, "", effectiveDate, customer,
								productGroup, product);

						break;

					case "Paid Out":
						fundsTransferFields = "CREDIT.VALUE.DATE," + "CREDIT.ACCT.NO," + "CREDIT.CURRENCY,"
								+ "DEBIT.ACCT.NO," + "CREDIT.AMOUNT,";
						fundsTransferValues = effectiveDate + "," + mainArrangement + "," + currency + ","
								+ repaymentEntity + "," + amount + ",";
						financialTransaction(CREATE, "AA Pay-Off", fundsTransferFields, fundsTransferValues);

						break;

					case "Rewrite":
						requestPayOff(mainArrangement, productGroup);
						arrangements(CREATE_AUTHORISE, productGroup, product, ROLEBASED_LENDING, customer,
								actualStep1Fields, actualStep1Values, step2Fields, step2Values);
						break;

					case "RTP":
						acitivityUpdateFields = "Posting Restriction#POSTING.RESTRICT:1," + "BLOCKING.CODE:1";
						acitivityUpdateValues = "15," + "CUSTOMER.REQUEST";

						arrangementActivity(CREATE_AUTHORISE, "UPDATE ACTIVITY FOR ACCOUNT", mainArrangement,
								productGroup, acitivityUpdateFields, acitivityUpdateValues);
						break;

					}
				} else {
					mainArrangement = "Error in Loan Disbursment. " + latestError;
					Reporter.log(mainArrangement, debugMode);
				}
			}
		}

		return mainArrangement;
	}

	public String dataGenCreateMortgage(final String customer, final String productGroup, final String product,
			final String currency, final String step1Fields, final String step1Values, final String step2Fields,
			final String step2Values, final String status, final String effectiveDate, final String principal,
			final String escrowPayee) {
		final String actualStep1Fields = step1Fields + "CUSTOMER:1," + "CURRENCY," + "EFFECTIVE.DATE,";
		final String actualStep1Values = step1Values + customer + "," + currency + "," + effectiveDate + ",";
		String mainArrangement;
		String limitFields;
		String limitValues;
		String chequingArrangement;
		String actualPrincipal = principal;
		String limitType;
		String completeLimitId;
		String limitReference;
		String limitSerial;
		String actualStep2Fields = step2Fields;
		String actualStep2Values = step2Values;
		final StringBuilder step2FieldsBuilder = new StringBuilder(actualStep2Fields);
		final StringBuilder step2ValuesBuilder = new StringBuilder(actualStep2Values);

		if ("".equals(principal)) {
			actualPrincipal = "100000";
		}

		ArrangementData arrangementData = new ArrangementData("mainArrangement", productGroup, product)
				.withCustomers(customer, null, "", "100,", "100,").withEffectiveDate(effectiveDate)
				.withCommitmentAmount(actualPrincipal).withCollateralValue(actualPrincipal).withCurrency(currency)
				.build();

		if (status.contains("HOK")) {
			final ArrangementData helocData = new ArrangementData("heloc", RETAIL_MORTGAGES, HELOC)
					.withEffectiveDate(effectiveDate).build();

			arrangementData = arrangementData.withHOK().withHOKProduct(helocData).build();
		}

		if (status.contains("Escrow")) {
			arrangementData = arrangementData.withEscrow(escrowPayee).build();
		}

		if (status.contains("via B2B Chequing")) {
			chequingArrangement = findArrangement(AUTHORISED, customer, CIF, "", PERSONAL_ACCOUNTS, PERSONAL_CHEQUING,
					currency);

			arrangementData = arrangementData.withSettlement("Banking", chequingArrangement).build();
		} else {
			arrangementData = arrangementData.withSettlement("Beneficiary", "NEW").build();
		}

		if (status.contains("Current") || status.contains("Refinanced")) {
			arrangementData = arrangementData.withDisbursement().withRepayments().build();
		} else if (status.contains("Delinquent")) {
			arrangementData = arrangementData.withDisbursement().build();
		}

		mainArrangement = createDefaultArrangement(arrangementData);

		if (mainArrangement.contains(ERROR)) {
			Reporter.log("Error in creating Mortgage");
		} else if (status.contains("Refinanced")) {
			limitFields = "LIMIT.CURRENCY," + "INTERNAL.AMOUNT,";
			limitValues = currency + "," + "200000,";
			limitType = UNSECURED;

			completeLimitId = customerLimit(CREATE_AUTHORISE, limitType, "", product, customer, "", limitFields, "02",
					limitValues);

			limitReference = completeLimitId.substring(13, 17);
			limitSerial = completeLimitId.length() > 2 ? completeLimitId.substring(completeLimitId.length() - 2) : "";

			step2FieldsBuilder.append("TERM,LIMIT.REFERENCE,LIMIT.SERIAL,");
			actualStep2Fields = step2FieldsBuilder.toString();
			step2ValuesBuilder.append("20Y,").append(limitReference).append(',').append(limitSerial).append(',');
			actualStep2Values = step2ValuesBuilder.toString();

			mainArrangement = arrangements(CREATE_AUTHORISE, productGroup, product, ROLEBASED_LENDING, customer,
					actualStep1Fields, actualStep1Values, actualStep2Fields, actualStep2Values);

		}

		return mainArrangement;
	}

	public int depositsRedemption(final String customer, final String arrangementID, final String type) {

		String commitmentRow;
		String totalPrincipalRowBefore;
		String totalPrincipalRowAfter;
		String chequingArrangement;
		int TotalPrincipalAmountLeft = 0;
		int totalCommitment;
		int totalPrincipal;
		double withdrawAmount = 0;
		boolean simulationStatusChanged = false;
		String simulationStatusValue = "";

		Reporter.log("Redeeming Deposit: " + arrangementID + " (" + type + ")", debugMode);

		chequingArrangement = findArrangement(AUTHORISED, customer, CIF, "", PERSONAL_ACCOUNTS, PERSONAL_CHEQUING, "");
		findArrangement(AUTHORISED, arrangementID, ARRANGEMENT, "", RETAIL_DEPOSITS, "", "");
		commitmentRow = versionScreen.accrualCategory(ID, "FinancialSummary", "Commitment (Total)").getText();
		totalPrincipalRowBefore = versionScreen.accrualCategory(ID, "FinancialSummary", "Total Principal").getText();
		totalCommitment = Integer.parseInt(commitmentRow.split(" ")[2].replaceAll(",", "").replaceAll("\\.0*$", ""));
		totalPrincipal = Integer
				.parseInt(totalPrincipalRowBefore.split(" ")[2].replaceAll(",", "").replaceAll("\\.0*$", ""));

		withdrawAmount = (totalCommitment * .25);
		switch (type) {

		case "Early":
			compositeScreen.textAction("Redeem Deposit", "Run").click();
			switchToPage(LASTPAGE, false);
			toolElements.toolsButton(VALIDATE_DEAL).click();
			inputData("PAYIN.ACCOUNT:1:1", chequingArrangement, true);
			toolElements.toolsButton(COMMIT_DEAL).click();
			inputTable.verifyAcceptOverride();
			switchToPage(LASTPAGE, false);

			while (!simulationStatusChanged) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					Reporter.log(e.getMessage(), false);
				}

				switchToPage("Sim Runner Status", false);
				simulationStatusValue = versionScreen.statusElement("Status").getText();

				if (!simulationStatusValue.contains("Processing")) {
					simulationStatusChanged = true;
					break;
				}
			}
			if (simulationStatusChanged) {
				switchToPage(environmentTitle, true);
				findArrangement(AUTHORISED, arrangementID, ARRANGEMENT, "", RETAIL_DEPOSITS, "", "");
				versionScreen.viewStatement("View Statement").click();
				switchToPage(LASTPAGE, false);
				compositeScreen.switchToFrame(ID, "RedeemList");
				toolElements.toolsButton("Bill Details").click();
				switchToPage(LASTPAGE, false);
				compositeScreen.switchToFrame(ID, "RedeemStatement");
				toolElements.toolsButton("Redeem Deposit").click();
				switchToPage(LASTPAGE, false);
				compositeScreen.switchToFrame(ID, "Version");
				inputData("CLOSURE.REASON", "Customer Not Satisfied With Service", true);
				toolElements.toolsButton(VALIDATE_DEAL).click();
				inputData("PAYOUT.SETTLEMENT:1", "Yes", true);
				inputData("PAYOUT.ACCOUNT:1:1", chequingArrangement, true);
				toolElements.toolsButton(COMMIT_DEAL).click();
				inputTable.verifyAcceptOverride();
				switchToPage(environmentTitle, true);
				findArrangement(AUTHORISED, arrangementID, ARRANGEMENT, "", RETAIL_DEPOSITS, "", "");
				TotalPrincipalAmountLeft = 0;
				Reporter.log("Principal Amount left after Early(ALL) Redemption is   " + TotalPrincipalAmountLeft,
						debugMode);
			}

			break;

		case "Partial":

			compositeScreen.textAction("Withdraw Deposit", "Run").click();
			switchToPage(LASTPAGE, false);
			inputData("TXN.AMOUNT", Double.toString(withdrawAmount), true);
			toolElements.toolsButton(VALIDATE_DEAL).click();
			inputData("PAYIN.ACCOUNT:1:1", chequingArrangement, true);
			toolElements.toolsButton(COMMIT_DEAL).click();
			inputTable.verifyAcceptOverride();
			switchToPage(LASTPAGE, false);

			while (!simulationStatusChanged) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					Reporter.log(e.getMessage(), false);
				}

				switchToPage("Sim Runner Status", false);
				simulationStatusValue = versionScreen.statusElement("Status").getText();

				if (!simulationStatusValue.contains("Processing")) {
					simulationStatusChanged = true;
					break;
				}
			}
			if (simulationStatusChanged) {
				switchToPage(environmentTitle, true);
				findArrangement(AUTHORISED, arrangementID, ARRANGEMENT, "", RETAIL_DEPOSITS, "", "");
				versionScreen.viewStatement("Withdrawal Statement").click();
				switchToPage(LASTPAGE, false);
				compositeScreen.switchToFrame(ID, "WithdrawalList");
				toolElements.toolsButton("Withdrawal Details").click();
				switchToPage(LASTPAGE, false);
				compositeScreen.switchToFrame(ID, "WithdrawalStatement");
				toolElements.toolsButton("Proceed Withdrawal").click();
				switchToPage(LASTPAGE, false);
				compositeScreen.switchToFrame(ID, "Version");
				inputData("CURRENCY", CAD, true);
				toolElements.toolsButton(VALIDATE_DEAL).click();
				inputData("PAYOUT.SETTLEMENT:1", "Yes", true);
				inputData("PAYOUT.ACCOUNT:1:1", chequingArrangement, true);
				toolElements.toolsButton(COMMIT_DEAL).click();
				inputTable.verifyAcceptOverride();
				switchToPage(environmentTitle, true);
				findArrangement(AUTHORISED, arrangementID, ARRANGEMENT, "", RETAIL_DEPOSITS, "", "");

				totalPrincipalRowAfter = versionScreen.accrualCategory(ID, "FinancialSummary", "Total Principal")
						.getText();
				totalPrincipal = Integer
						.parseInt(totalPrincipalRowAfter.split(" ")[2].replaceAll(",", "").replaceAll("\\.0*$", ""));
				TotalPrincipalAmountLeft = totalPrincipal;
				Reporter.log("Principal Amount left after partial Redemption is  " + TotalPrincipalAmountLeft,
						debugMode);
			}
			break;
		}

		return TotalPrincipalAmountLeft;
	}

	public int diffBetweenDatesInMonths(final String effectiveDate) {
		LocalDate todayDate = null;
		LocalDate effectDate = null;
		Period difference;
		String today;
		String effective;
		financialTransaction(OPEN, "Account Transfer - Transfer between Accounts", "DEBIT.VALUE.DATE,CREDIT.VALUE.DATE",
				"+0D," + effectiveDate);
		today = readData("DEBIT.VALUE.DATE");
		effective = readData("CREDIT.VALUE.DATE");

		try {
			todayDate = LocalDate.parse(today, DateTimeFormatter.BASIC_ISO_DATE);
			effectDate = LocalDate.parse(effective, DateTimeFormatter.BASIC_ISO_DATE);
		} catch (DateTimeParseException e1) {
			Reporter.log(e1.getMessage(), false);
		}
		difference = Period.between(effectDate, todayDate);
		return difference.getYears() * 12 + difference.getMonths();
	}

	public boolean disburseArrangement(final String type, final String fromEntity, final String toEntity,
			final String currency, final String amount, final String effectiveDate, final String customer,
			final String productGroup) {
		String fundsTransferFields;
		String fundsTransferValues;
		String customerType = PERSONAL;
		boolean result;
		if (BUSINESS_ACCOUNTS.equals(productGroup) || COMMERCIAL_LOANS.equals(productGroup)) {
			customerType = BUSINESS;
		}
		if ("via Cheque".equals(type)) {
			fundsTransferFields = "CREDIT.ACCT.NO," + "DEBIT.CURRENCY," + "DEBIT.ACCT.NO," + "DEBIT.AMOUNT,"
					+ "CREDIT.VALUE.DATE," + "CREDIT.THEIR.REF,";
			fundsTransferValues = "CAD1110200017817," + currency + "," + fromEntity + "," + amount + "," + effectiveDate
					+ "," + "123456789,";
			result = arrangementAction(fromEntity, customer, ROLEBASED_LENDING, TRANSFER_ACCOUNTS, fundsTransferFields,
					fundsTransferValues, customerType);
			if (result) {
				result = authorizeEntity(fromEntity, ACTIVITY + "," + productGroup);
			}
		} else {

			fundsTransferFields = "CREDIT.ACCT.NO," + "DEBIT.CURRENCY," + "DEBIT.AMOUNT," + "CREDIT.VALUE.DATE,"
					+ "CREDIT.THEIR.REF,";
			fundsTransferValues = toEntity + "," + currency + "," + amount + "," + effectiveDate + "," + "123456789,";
			result = arrangementAction(fromEntity, customer, ROLEBASED_LENDING, LENDING_DISBURSEMENT,
					fundsTransferFields, fundsTransferValues, customerType);

			if (result) {
				result = authorizeEntity(fromEntity, ACTIVITY + "," + productGroup);
			}
		}

		return result;
	}

	public boolean repayArrangement(final String fromEntity, final String toEntity, final String currency,
			final String amount, final String effectiveDate, final String customer, final String productGroup,
			final String product) {

		String fundsTransferFields;
		String fundsTransferValues;
		String actualAmount;
		String transactionID;

		boolean result = false;
		final int months = diffBetweenDatesInMonths(effectiveDate);

		if (months > 0 && "".equals(amount)) {
			actualAmount = arrangementBillAmount(toEntity, productGroup, product);
		} else {
			actualAmount = amount;
		}

		for (int i = months; i > 0; i--) {

			fundsTransferFields = "CREDIT.VALUE.DATE," + "CREDIT.ACCT.NO," + "CREDIT.CURRENCY," + "DEBIT.ACCT.NO,"
					+ "CREDIT.AMOUNT," + "ORDERING.BANK:1,";
			fundsTransferValues = "-" + (i - 1) + "m" + "," + toEntity + "," + currency + "," + fromEntity + ","
					+ actualAmount + "," + "RBC,";
			transactionID = financialTransaction(CREATE, "AA Repayment", fundsTransferFields, fundsTransferValues);
			result = !transactionID.contains(ERROR);

		}
		return result;
	}

	public boolean requestPayOff(final String mainArrangement, final String productGroup) {
		boolean simulationStatusChanged = false;
		String simulationStatusValue = "";

		requestClosure(OPEN, mainArrangement, "Request Payoff", productGroup, "", "", "", "", "");
		toolElements.toolsButton(VALIDATE_DEAL).click();
		toolElements.toolsButton(COMMIT_DEAL).click();
		inputTable.verifyAcceptOverride();

		while (!simulationStatusChanged) {
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				Reporter.log(e.getMessage(), false);
			}

			switchToPage("Sim Runner Status", false);
			simulationStatusValue = versionScreen.statusElement("Status").getText();

			if (!simulationStatusValue.contains("Processing")) {
				simulationStatusChanged = true;
				break;
			}
		}

		if (simulationStatusChanged) {
			toolElements.toolsButton("Details").click();
			switchToPage(LASTPAGE, false);
			payoffInterest = versionScreen.rowCell("r2_PayoffTotal", "2").getText();
			payoffAmount = versionScreen.rowCell("r2_PayoffTotal", "3").getText();

		}

		return true;
	}

	public boolean createPAPCredit(final String arrangement, final String productGroup, final String product,
			final String type) {
		String maturityDate;
		String fields;
		String values;
		String maturityDateFormat = null;
		boolean result = false;

		Reporter.log("Creating PAP Credit for " + product + ": " + arrangement, debugMode);

		findArrangement(AUTHORISED, arrangement, ARRANGEMENT, "", productGroup, product, CAD);
		maturityDate = versionScreen.statusElement("Maturity Date").getText();

		DateFormat calenderFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
		DateFormat originalFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
		try {
			maturityDateFormat = calenderFormat.format(originalFormat.parse(maturityDate));
		} catch (ParseException e) {
			Reporter.log(e.getMessage());
		}

		if ("Fixed".equals(type)) {

			fields = "CPA.TYPE," + "PAP.FOREGN," + "CAMB.PAP.ACCT2," + "CAMB.PAP.ACCT," + "DEST.ACCT.NAME:1,"
					+ "CA.PAP.PURPOSE," + "STAND.ALONE.AMT," + "CREATE.DATE," + "CYCLE.FREQUENCY," + "CA.PAP.MATDATE,"
					+ "CAMB.PAD.AGREE,";
			values = "350," + "000100011," + "123654789369," + "123654789369," + "Test Person,"
					+ "Amend Existing Authorization," + "500," + "+0d," + "e0Y e5M e0W o5D e0F," + maturityDateFormat
					+ ",YES,";

			commandLine("CAPL.H.PAP.DD.DDI,CAMB.CREDIT", commandLineAvailable);
			enquiryElements.transactionIdField("PAP Credit").click();
			enquiryElements.transactionIdField("PAP Credit").sendKeys(arrangement);
			toolElements.toolsButton(EDIT_CONTRACT).click();
			multiInputData(fields, values, false);

			toolElements.toolsButton(COMMIT_DEAL).click();
			result = inputTable.verifyAcceptOverride();

		}
		return result;
	}

	public boolean disburseBridgeLoan(final String arrangement, String effectiveDate, final String amount,
			final String productGroup) {
		String fields;
		String values;
		String transactionID;
		boolean result;

		fields = "CONSOL.INSTRUCTION," + "PRIMARY.ACCOUNT," + "TRANSACTION:1," + "SURROGATE.AC:1," + "CURRENCY:1,"
				+ "AMOUNT:1," + "DR.VALUE.DATE:1," + "TRANSACTION:2," + "SURROGATE.AC:2," + "CURRENCY:2," + "AMOUNT:2,"
				+ "DR.VALUE.DATE:2,";
		values = "No," + "CAD1100000017623," + "Disburse-ft," + arrangement + "," + "CAD," + amount + ","
				+ effectiveDate + "," + "Credit," + "PL52725," + "CAD," + "500," + effectiveDate + ",";
		transactionID = financialTransaction(CREATE, "Teller Financial Services", fields, values);
		result = !transactionID.contains(ERROR);

		return result;
	}

	public String createNominee(final String effectiveDate) {
		boolean result;
		String customer;
		String beneficiary;
		String agentCustomer;
		String agentBeneficiary;
		String agentFields;
		String agentValues;
		String agentCommissionPlan;
		StringBuilder step2FieldsBuilder;
		StringBuilder step2ValuesBuilder;
		String fields;
		String values;
		String beneficiaryFields;
		String beneficiaryValues;

		customer = createDefaultCustomer(BUSINESS, RETAIL_DEPOSITS, ROLEBASED_BANKING);

		beneficiaryFields = DefaultVariables.beneficiaryFields + "OWNING.CUSTOMER," + "BEN.CUSTOMER,";
		beneficiaryValues = DefaultVariables.beneficiaryValues + customer + ","
				+ createdCustomers.get(customer).getCustomerName() + ",";

		beneficiary = beneficiaryCode(CREATE, "EFT Client", "", beneficiaryFields, beneficiaryValues);

		step2FieldsBuilder = new StringBuilder();
		step2ValuesBuilder = new StringBuilder();

		agentCustomer = createDefaultCustomer(DEALER_ADVISOR, AGENTS, ROLEBASED_OR);

		beneficiaryFields = DefaultVariables.beneficiaryFields + "OWNING.CUSTOMER," + "BEN.CUSTOMER,";
		beneficiaryValues = DefaultVariables.beneficiaryValues + agentCustomer + ","
				+ createdCustomers.get(agentCustomer).getCustomerName() + ",";

		agentBeneficiary = beneficiaryCode(CREATE, "EFT Client", "", beneficiaryFields, beneficiaryValues);

		step2FieldsBuilder.append(
				"Advanced - Pay Out#PAYOUT.SETTLEMENT:1,Advanced - Pay Out#PAYOUT.PO.PRODUCT:1:1,Advanced - Pay Out#PAYOUT.BENEFICIARY:1:1,Advanced - Pay Out#PAYOUT.ACTIVITY:1:1,");
		agentFields = step2FieldsBuilder.toString();
		step2ValuesBuilder.append("Yes,").append("CHQ").append(',').append(agentBeneficiary).append(", ,");
		agentValues = step2ValuesBuilder.toString();
		agentCommissionPlan = arrangements(CREATE, AGENTS, "Deposit Commission Plan", "", agentCustomer,
				"CUSTOMER:1," + "CURRENCY," + "EFFECTIVE.DATE,", agentCustomer + "," + CAD + "," + effectiveDate + ",",
				agentFields, agentValues);

		fields = "PROPERTY:1," + "PAYMENT.ORDER.PRODUCT:1," + "BENEFICIARY:1," + "PROPERTY+:2,"
				+ "PAYMENT.ORDER.PRODUCT:2," + "BENEFICIARY:2," + "AGENT.ID," + "AGENT.ARR.ID,";
		values = "ACCOUNT," + "ACHDEBIT," + beneficiary + "," + "DEPOSITINT," + "ACHDEBIT," + beneficiary + ","
				+ agentCustomer + "," + agentCommissionPlan + ",";

		commandLine("DEP.H.EXT.TFR.PARAM,CAMB", commandLineAvailable);
		enquiryElements.transactionIdField("External Transfer Parameter").sendKeys(customer);
		toolElements.toolsButton(EDIT_CONTRACT).click();
		switchToPage(LASTPAGE, false);
		result = multiInputData(fields, values, false);
		toolElements.toolsButton(COMMIT_DEAL).click();
		result = inputTable.verifyAcceptOverride();
		if (!result) {
			customer = "Error while creating nominee";
		}
		return customer;
	}

	public String createDefaultCustomer(final String customerType, final String productGroup,
			final String roleBasedPage) {
		String actualCustomerType;

		if (customerType.isEmpty()) {
			actualCustomerType = DefaultVariables.productGroupCustomerType.get(productGroup);
		} else {
			actualCustomerType = customerType;
		}
		final String defaultCIFFields = DefaultVariables.customerTypeFields.get(actualCustomerType);
		final String defaultCIFValues = DefaultVariables.customerTypeValues.get(actualCustomerType);

		return customer(CREATE, actualCustomerType, roleBasedPage, defaultCIFFields, defaultCIFValues);
	}

	public String createDefaultArrangement(final String productGroup, final String product, final String roleBasedPage,
			final String customer, final String effectiveDate) {
		String actualCustomer = customer;
		String step1Fields;
		String step1Values;
		String step2Values;
		String step2Fields;

		if ("NEW".equals(customer)) {
			actualCustomer = createDefaultCustomer("", productGroup, roleBasedPage);
		}

		step1Fields = "CUSTOMER:1," + "CURRENCY," + "EFFECTIVE.DATE,";
		step1Values = actualCustomer + "," + "CAD," + effectiveDate + ",";

		if (DefaultVariables.productFields.containsKey(product)) {
			step2Fields = DefaultVariables.productFields.get(product);
			step2Values = DefaultVariables.productValues.get(product);
		} else {
			step2Fields = DefaultVariables.productGroupFields.get(productGroup);
			step2Values = DefaultVariables.productGroupValues.get(productGroup);
		}

		return arrangements(CREATE, productGroup, product, roleBasedPage, customer, step1Fields, step1Values,
				step2Fields, step2Values);
	}

	public String createDefaultArrangement(final ArrangementData arrangement) {
		CustomerData mainCustomerData;
		String actualLimitSerial;
		String advisor;
		String advisorArrangement;
		String advisorCode;
		String beneficiaryFields;
		String beneficiaryValues;
		String closureFields;
		String closureValues;
		String completeLimitId;
		String collateral;
		String collateralLink;
		String customers = arrangement.customers;
		String dealer;
		String dealerArrangement;
		String dealerCode;
		String escrowPayee = arrangement.escrowPayee;
		String fundsTransferFields;
		String fundsTransferValues;
		String mainArrangement;
		String reserveAccount;
		String settlementAccount;
		String startDate;
		String statementID;
		int limitSerialInteger;
		boolean localResult = true;
		mainCustomerData = createdCustomers.get(customers.split(",")[0]);

		if (arrangement.createCustomer) {
			customers = createDefaultCustomer("", arrangement.productGroup, arrangement.roleBasedPage);
			for (int i = 1; i < arrangement.owners; i++) {
				customers += "," + createDefaultCustomer("", arrangement.productGroup, arrangement.roleBasedPage);
			}

			mainCustomerData = createdCustomers.get(customers.split(",")[0]);
			arrangement.setCustomers(customers, mainCustomerData);
		}

		if (arrangement.createAgent) {

			dealer = customer(OPEN, DEALER_ADVISOR, ROLEBASED_OR, DefaultVariables.dealerAdvisorCIFFields,
					DefaultVariables.dealerAdvisorCIFValues);
			dealerCode = readData("L.DEALER.REP.NO");
			toolElements.toolsButton(COMMIT_DEAL).click();
			inputTable.verifyAcceptOverride();

			dealerArrangement = createDefaultArrangement(AGENTS, arrangement.commissionPlanDealer,
					arrangement.roleBasedPage, dealer, arrangement.effectiveDate);

			advisor = customer(OPEN, DEALER_ADVISOR, ROLEBASED_OR, DefaultVariables.dealerAdvisorCIFFields,
					DefaultVariables.dealerAdvisorCIFValues);
			advisorCode = readData("L.DEALER.REP.NO");
			multiInputData("SHORT.NAME:1," + "AGENCY," + "L.DEALER.REP.NO,",
					dealerCode + "-" + advisorCode + "," + dealer + "," + dealerCode + "-" + advisorCode + ",", false);
			toolElements.toolsButton(COMMIT_DEAL).click();
			inputTable.verifyAcceptOverride();

			advisorArrangement = createDefaultArrangement(AGENTS, arrangement.commissionPlanAdvisor,
					arrangement.roleBasedPage, advisor, arrangement.effectiveDate);

			arrangement.setAgent(dealer, dealerArrangement, advisor, advisorArrangement);

		}

		if (arrangement.createBeneficiary) {
			beneficiaryFields = DefaultVariables.beneficiaryFields + "OWNING.CUSTOMER," + "BEN.CUSTOMER,";
			beneficiaryValues = DefaultVariables.beneficiaryValues + customers.split(",")[0] + ","
					+ mainCustomerData.getCustomerName() + ",";
			settlementAccount = beneficiaryCode(CREATE, "EFT Client", "", beneficiaryFields, beneficiaryValues);
			arrangement.setSettlementAccount("Beneficiary", settlementAccount);
		} else if (arrangement.createBanking) {
			settlementAccount = createDefaultArrangement(PERSONAL_ACCOUNTS, PERSONAL_CHEQUING, ROLEBASED_BANKING,
					arrangement.customers.split(",")[0], arrangement.effectiveDate);
			arrangement.setSettlementAccount("Banking", settlementAccount);
		}

		if (arrangement.createReserveAccount) {
			reserveAccount = createDefaultArrangement(SERVICING_ACCOUNTS, "Reserve Account", ROLEBASED_BANKING,
					arrangement.customers.split(",")[0], arrangement.effectiveDate);
			arrangement.setReserveAccount(reserveAccount, true);
		}

		if (arrangement.createLimit) {
			if ("NEW".equals(arrangement.limitSerial)) {
				limitSerialInteger = getCurrentLimitSerial(arrangement.customers.split(",")[0],
						arrangement.limitReference);
				if (limitSerialInteger < 9) {
					actualLimitSerial = "0" + Integer.toString(limitSerialInteger + 1);
				} else {
					actualLimitSerial = Integer.toString(limitSerialInteger + 1);
				}
			} else {
				actualLimitSerial = arrangement.limitSerial;
			}

			if (ROLEBASED_SAE.equals(arrangement.roleBasedPage)) {
				customerLimit(CREATE, arrangement.limitType, ROLEBASED_SAE, arrangement.product,
						arrangement.customers.split(",")[0], "2400", actualLimitSerial, arrangement.limitFields,
						arrangement.limitValues, false);
			} else if (arrangement.hokIndicator && "01".equals(actualLimitSerial)) {
				customerLimit(CREATE, arrangement.limitType, ROLEBASED_BANKING, arrangement.product,
						arrangement.customers.split(",")[0], "2100", "01", arrangement.hokParentLimitFields,
						arrangement.hokParentLimitValues, false);
			}

			completeLimitId = customerLimit(CREATE, arrangement.limitType, arrangement.roleBasedPage,
					arrangement.product, arrangement.customers.split(",")[0], arrangement.limitReference,
					actualLimitSerial, arrangement.limitFields, arrangement.limitValues, !arrangement.createCollateral);
			arrangement.setLimit(completeLimitId.split("\\.")[1].substring(3), completeLimitId.split("\\.")[2], "N");

			if (arrangement.createCollateral) {
				collateralLink = collateral(CREATE, COLLATERAL_LINK, arrangement.customers.split(",")[0],
						arrangement.collateralLinkFields, arrangement.collateralLinkValues);
				collateral = collateral(CREATE, COLLATERAL_DETAILS, collateralLink, arrangement.collateralFields,
						arrangement.collateralValues);
				arrangement.collateralLink = collateralLink;
				arrangement.collateralDetails = collateral;

			}
		}

		arrangements(OPEN, arrangement.productGroup, arrangement.product, arrangement.roleBasedPage,
				arrangement.customers.split(",")[0], arrangement.arrangementStep1Fields,
				arrangement.arrangementStep1Values, "", "");

		startDate = readData("EFFECTIVE.DATE");
		// arrangement.setStartDate(startDate);

		toolElements.toolsButton(VALIDATE_DEAL).click();
		mainArrangement = readData("ACCOUNT.REFERENCE");

		if (setupRenewalAndInterest(arrangement.productGroup, arrangement.product, arrangement.arrangementStep2Fields)
				&& multiInputData(arrangement.arrangementStep2Fields, arrangement.arrangementStep2Values, true)) {
			Reporter.log("Creating " + arrangement.product + " Arrangement " + mainArrangement + " for Customer "
					+ arrangement.customers.split(",")[0], debugMode);
		}

		if (inputTable.commitAndOverride() && readTable.message().getText().contains(TXN_COMPLETE)) {
			switchToPage(environmentTitle, true);
			arrangement.arrangementID = mainArrangement;

			// Temporary
			// authorizeEntity(mainArrangement, ARRANGEMENT + "," +
			// arrangement.productGroup);

			if (arrangement.disburseArrangement) {
				if (!disburseBridgeLoan(mainArrangement, arrangement.effectiveDate, arrangement.disburseAmount,
						arrangement.productGroup)) {
					Reporter.log("Arrangement " + mainArrangement + " was not disbursed successfully", debugMode);
				}
			}

			if (arrangement.fundArrangement) {
				fundsTransferFields = "CREDIT.ACCT.NO," + "CREDIT.CURRENCY," + "DEBIT.ACCT.NO," + "CREDIT.AMOUNT,"
						+ "CREDIT.VALUE.DATE,";
				fundsTransferValues = mainArrangement + "," + arrangement.currency + "," + "CAD1160200017607,"
						+ arrangement.fundAmount + "," + arrangement.effectiveDate + ",";
				if (financialTransaction(CREATE_AUTHORISE, "AA Deposit - Fund", fundsTransferFields,
						fundsTransferValues).contains(ERROR)) {
					Reporter.log("Arrangement " + mainArrangement + " was not funded successfully", debugMode);
				}
			}

			if (arrangement.createEscrowAccount) {
				if (arrangement.createEscrowPayee) {
					escrowPayee = createDefaultCustomer(NON_CLIENT_BUSINESS, arrangement.productGroup,
							arrangement.roleBasedPage);
					localResult = escrowPayee(EDIT, DefaultVariables.escrowPayeeFields,
							DefaultVariables.escrowPayeeValues, escrowPayee);
					arrangement.setEscrowPayee(escrowPayee);
				} else if (arrangement.findMunicipalityPayee) {
					escrowPayee("Find", "PAYEE.NAME", mainCustomerData.addressCity, "");
					if (enquiryElements.enqHeaderMsg().getText().contains("Result")) {
						escrowPayee = enquiryElements.getElementAtCell("Manage Escrow Payee", 1, 1).getText();
						localResult = !"".equals(escrowPayee);
					} else {
						escrowPayee = createDefaultCustomer(NON_CLIENT_BUSINESS, arrangement.productGroup,
								arrangement.roleBasedPage);
						localResult = escrowPayee(EDIT, DefaultVariables.escrowPayeeFields + "PAYEE.NAME,",
								DefaultVariables.escrowPayeeValues + mainCustomerData.addressCity + ",", escrowPayee);
					}
					if (localResult) {
						arrangement.setEscrowPayee(escrowPayee);
					}
				}

				if (localResult) {
					createEscrowAccount(mainArrangement, arrangement.productGroup, arrangement.product,
							arrangement.escrowPayee,
							DefaultVariables.escrowAccountFields + "Additional Info#ADDITIONAL.INFO:1,",
							DefaultVariables.escrowAccountValues + mainCustomerData.addressStreet + ",");
				}
			}
		} else {
			mainArrangement = "Error while creating " + arrangement.product + " Arrangement";
		}

		if (arrangement.hokIndicator) {
			String hokArrangement;

			for (final ArrangementData currentHOKArrangement : arrangement.hokArrangements) {
				completeLimitId = customerLimit(CREATE, currentHOKArrangement.limitType,
						currentHOKArrangement.roleBasedPage, currentHOKArrangement.product,
						currentHOKArrangement.customers.split(",")[0], currentHOKArrangement.limitReference, "",
						currentHOKArrangement.limitFields, currentHOKArrangement.limitValues, false);
				currentHOKArrangement.setLimit(completeLimitId.split("\\.")[1].substring(3),
						completeLimitId.split("\\.")[2], "N");

				arrangements(OPEN, currentHOKArrangement.productGroup, currentHOKArrangement.product,
						currentHOKArrangement.roleBasedPage, currentHOKArrangement.customers.split(",")[0],
						currentHOKArrangement.arrangementStep1Fields, currentHOKArrangement.arrangementStep1Values, "",
						"");

				startDate = readData("EFFECTIVE.DATE");
				currentHOKArrangement.setStartDate(startDate);

				toolElements.toolsButton(VALIDATE_DEAL).click();
				hokArrangement = readData("ACCOUNT.REFERENCE");

				if (setupRenewalAndInterest(currentHOKArrangement.productGroup, currentHOKArrangement.product,
						currentHOKArrangement.arrangementStep2Fields)
						&& multiInputData(currentHOKArrangement.arrangementStep2Fields,
								currentHOKArrangement.arrangementStep2Values, true)) {
					toolElements.toolsButton(COMMIT_DEAL).click();
				}

				if (inputTable.verifyAcceptOverride() && readTable.message().getText().contains(TXN_COMPLETE)) {
					switchToPage(environmentTitle, true);
					currentHOKArrangement.arrangementID = hokArrangement;
					if (currentHOKArrangement.disburseArrangement) {
						disburseBridgeLoan(hokArrangement, currentHOKArrangement.effectiveDate,
								currentHOKArrangement.disburseAmount, currentHOKArrangement.productGroup);
					}
				} else {
					hokArrangement = "Error while creating HOK Member: " + currentHOKArrangement.product
							+ " Arrangement";
				}
			}
		}

		if (arrangement.createStatement) {
			closureFields = "";
			closureValues = "";

			statementID = generateStatement(arrangement.statementType, arrangement.customers.split(",")[0],
					arrangement.productGroup, arrangement.product, closureFields, closureValues);

			arrangement.setStatement(statementID, arrangement.statementType);
		}

		return mainArrangement;
	}
}