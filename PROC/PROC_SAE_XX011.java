package testcases.PROC;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.openqa.selenium.NoSuchElementException;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.DefaultVariables;

public class PROC_SAE_XX011 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "2018-05-22";
	private String mainArrangement;
	private String mainCustomer;
	private boolean result;
	private String municipality;
	private String fields;
	private String values;
	private String customerBeneficiary;
	private String municipalityBeneficiary;
	final SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
	Date date = new Date();
	Calendar cal = Calendar.getInstance();

	@Test(priority = 1, enabled = true)
	@Parameters({ "customerType", "productGroup", "product", "branch" })
	public void preCondition(final String customerType, final String productGroup, final String product,
			@Optional("LAURENTIAN BANK - 523") final String branch) {

		stepDescription = "create " + customerType + " Customer and create current " + product + " Arrangement";
		stepExpected = customerType + " customer and " + product + " Arrangement created Successfully";

		if (loginResult) {

			switchToBranch(branch);
			ArrangementData arrangementData = new ArrangementData("mainArrangement", productGroup, product)
					.withEffectiveDate("-45d")
					.withDisbursement()
					.withRepayments()
					.build();
			mainArrangement = createDefaultArrangement(arrangementData);
			mainCustomer = arrangementData.getCustomers();

			if (mainCustomer == null || mainCustomer.contains(ERROR)) {
				stepActual = "Error while creating customer";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else if (mainArrangement == null || mainArrangement.contains(ERROR)) {
				stepActual = "Error while creating Arrangement";
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
	@Parameters({ "productGroup", "branch" })
	public void createMunicipality(final String productGroup, @Optional("LAURENTIAN BANK - 523") final String branch) {

		stepDescription = "Create Non Client Business(municipality) customer ";
		stepExpected = "Non Client Business(municipality) customer created successfully ";
		if (loginResult) {

			switchToBranch(branch);
			municipality = customer(CREATE, NON_CLIENT_BUSINESS, ROLEBASED_SAE,
					DefaultVariables.nonClientBusinessCIFFields, DefaultVariables.nonClientBusinessCIFValues);
			if (municipality == null || municipality.contains(ERROR)) {
				stepActual = "Error while creating Non Client Business customer";
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

	@Test(priority = 3, enabled = true)
	@Parameters({ "customerType" })
	public void createbeneficiary(final String customerType) {

		stepDescription = "Create Beneficiaries for " + customerType + " and Non-Client (Business)  Customers";
		stepExpected = "Beneficiearies for " + customerType
				+ " and Non-Client (Business)  Customers created successfully ";

		String customerBenFields = DefaultVariables.beneficiaryFields + "OWNING.CUSTOMER," + "BEN.CUSTOMER,";
		String customerBenValues = DefaultVariables.beneficiaryValues + mainCustomer + ","
				+ createdCustomers.get(mainCustomer).getCustomerName() + ",";
		String municipalityBenFields = DefaultVariables.beneficiaryFields + "BEN.CUSTOMER,";
		String municipalityBenValues = DefaultVariables.beneficiaryValues
				+ createdCustomers.get(municipality).getCustomerName() + ",";

		if (mainCustomer == null || mainCustomer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			customerBeneficiary = beneficiaryCode(CREATE, "EFT Client", "", customerBenFields, customerBenValues);
			municipalityBeneficiary = beneficiaryCode(CREATE, "EFT Non-Client", "", municipalityBenFields,
					municipalityBenValues);
			if (customerBeneficiary == null || customerBeneficiary.contains(ERROR) || municipalityBeneficiary == null
					|| municipalityBeneficiary.contains(ERROR)) {
				stepActual = "Error while creating Beneficiaries";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();

	}

	@Test(priority = 4, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void createEscrowPayee(final String productGroup, final String product) {

		stepDescription = "Create Escrow payee for " + municipality;
		stepExpected = "Escrow Payee for created successfully ";

		String additionalFields;
		String additionalValues;

		if (municipality == null || municipality.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			Random randomNumber = new Random();
			int caseNumber = randomNumber.nextInt(4);

			switch (caseNumber) {

			default:
			case 0:
				additionalFields = "Additional#PENALTY.TYPE," + "PENALTY.RATE," + "DAY.BASIS,";
				additionalValues = "Daily," + "2.5," + "E,";
				break;

			case 1:
				additionalFields = "Additional#PENALTY.TYPE," + "PENALTY.RATE," + "DAY.BASIS,";
				additionalValues = "Monthly.exclude," + "3.5," + "E,";
				break;

			case 2:
				additionalFields = "Additional#PENALTY.TYPE," + "PENALTY.RATE," + "DAY.BASIS,";
				additionalValues = "Monthly.include," + "2.87," + "E,";
				break;

			case 3:
				additionalFields = "Additional#PENALTY.TYPE," + "DAY.BASIS," + "PERIOD.EFF:1," + "PERIOD.RATE:1,"
						+ "PERIOD.EFF+:2," + "PERIOD.RATE:2," + "PERIOD.EFF+:3," + "PERIOD.RATE:3," + "PERIOD.EFF+:4,"
						+ "PERIOD.RATE:4,";
				additionalValues = "Period," + "E," + "0524," + "1.6," + "0724," + "2.2," + "0924," + "2.85," + "1124,"
						+ "3.57,";
				break;

			}

			fields = "PAYEE.TYPE," + "STATUS," + "Setup#SETTLEMENT.TYPE," + "BENEFICIARY," + "COMPANY.CODE:1,"
					+ "DISBURSE.ACC:1," + "IRREGULAR.PRD:1," + "IRREGULAR.PRD+:2," + "IRREGULAR.PRD+:3,"
					+ "IRREGULAR.PRD+:4," + additionalFields;
			values = "MT," + "Active," + "ACH," + municipalityBeneficiary + "," + "CA0011000," + "CAD1160000017623,"
					+ "0519," + "0619," + "0719," + "0819," + additionalValues;
			result = escrowPayee(EDIT, fields, values, municipality);
			if (!result) {
				stepActual = "Error while creating Escrow payee";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();

	}

	@Test(priority = 5, enabled = true)
	@Parameters({ "productGroup" })
	public void createEscrowPayeeMerge(final String productGroup) {

		stepDescription = "Mearge new municipality Escrow payee to  " + municipality + " escrow payee";
		stepExpected = "escrow payee mearged successfully ";
		String newMunicipality;
		String additionalFields;
		String additionalValues;
		String mergeFields;
		String mergeValues;

		if (mainCustomer == null || mainCustomer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			Random randomNumber = new Random();
			int caseNumber = randomNumber.nextInt(4);

			switch (caseNumber) {
			default:
			case 0:
				additionalFields = "Additional#PENALTY.TYPE," + "PENALTY.RATE," + "DAY.BASIS,";
				additionalValues = "Daily," + "2.5," + "E,";
				break;
			case 1:
				additionalFields = "Additional#PENALTY.TYPE," + "PENALTY.RATE," + "DAY.BASIS,";
				additionalValues = "Monthly.exclude," + "3.5," + "E,";
				break;

			case 2:
				additionalFields = "Additional#PENALTY.TYPE," + "PENALTY.RATE," + "DAY.BASIS,";
				additionalValues = "Monthly.include," + "2.87," + "E,";
				break;

			case 3:
				additionalFields = "Additional#PENALTY.TYPE," + "DAY.BASIS," + "PERIOD.EFF:1," + "PERIOD.RATE:1,"
						+ "PERIOD.EFF+:2," + "PERIOD.RATE:2," + "PERIOD.EFF+:3," + "PERIOD.RATE:3," + "PERIOD.EFF+:4,"
						+ "PERIOD.RATE:4,";
				additionalValues = "Period," + "E," + "0524," + "1.6," + "0724," + "2.2," + "0924," + "2.85," + "1124,"
						+ "3.57,";
				break;

			}

			fields = "PAYEE.TYPE," + "STATUS," + "Setup#SETTLEMENT.TYPE," + "BENEFICIARY," + "COMPANY.CODE:1,"
					+ "DISBURSE.ACC:1," + "IRREGULAR.PRD:1," + "IRREGULAR.PRD+:2," + "IRREGULAR.PRD+:3,"
					+ "IRREGULAR.PRD+:4," + additionalFields;
			values = "MT," + "Active," + "ACH," + municipalityBeneficiary + "," + "CA0011000," + "CAD1160000017623,"
					+ "0519," + "0619," + "0719," + "0819," + additionalValues;

			mergeFields = "Merge#MERGE.PAYEE:1," + "MERGE.DATE:1,";
			mergeValues = municipality + ",+6m,";

			newMunicipality = customer(CREATE, NON_CLIENT_BUSINESS, ROLEBASED_SAE,
					DefaultVariables.nonClientBusinessCIFFields, DefaultVariables.nonClientBusinessCIFValues);
			result = escrowPayee(EDIT, fields + mergeFields, values + mergeValues, newMunicipality);

			if (!result) {
				stepActual = "Error while Merging Escrow payee";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();

	}

	@Test(priority = 6, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void linkEscrowAccountToLoan(final String productGroup, final String product) {

		stepDescription = "Link Escrow account to Loan " + mainArrangement;
		stepExpected = "Escrow account to Loan " + mainArrangement + " Linked Successfully ";

		cal.setTime(date);
		cal.add(Calendar.DATE, -1);
		date = cal.getTime();
		String startDate = inputDateFormat.format(date);

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			fields = "PAYMENT.TYPE+:2," + "PAYMENT.METHOD:2," + "PAYMENT.FREQ:2," + "PROPERTY:2:1," + "DUE.FREQ:2:1,"
					+ "BILL.TYPE:2," + "PAYMENT.TYPE+:3," + "PAYMENT.METHOD:3," + "PAYMENT.FREQ:3," + "PROPERTY:3:1,"
					+ "DUE.FREQ:3:1," + "START.DATE:3:1," + "BILL.TYPE:3," + "PAYMENT.TYPE+:4," + "PAYMENT.METHOD:4,"
					+ "PAYMENT.FREQ:4," + "PROPERTY:4:1," + "DUE.FREQ:4:1," + "START.DATE:4:1," + "BILL.TYPE:4,";
			values = "ESCROW," + "Due," + "e0Y e1M e0W e0D e0F," + "ESCROW," + "e0Y e1M e0W e0D e0F," + "INSTALLMENT,"
					+ "INTEREST," + "Capitalise," + "e0Y e0M e0W e0D eLMNTHF," + "ESCROWDRINT,"
					+ "e0Y e0M e0W e0D eLMNTHF," + startDate + "," + "INSTALLMENT," + "INTEREST," + "Capitalise,"
					+ "e0Y e0M e0W e0D eLMNTHF," + "ESCROWCRINT," + "e0Y e0M e0W e0D eLMNTHF," + startDate + ","
					+ "INSTALLMENT,";
			result = arrangementActivity("Create Scheduled -2d", "CHANGE ACTIVITY FOR SCHEDULE", mainArrangement,
					productGroup, fields, values);
			if (!result) {
				stepActual = "Error while Linking escrow account to loan";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();

	}

	@Test(priority = 7, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void createEscrowAccount(final String productGroup, final String product) {

		stepDescription = "Create Escrow account for product:" + product;
		stepExpected = " Escrow account Created successfully";
		String customerAddress = createdCustomers.get(mainCustomer).getAddressStreet();

		if (municipality == null || municipality.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			fields = "OVERDRAW," + "REFERENCE.NO:1," + "ANNUAL.DISBURSE.AMT:1," + "ADJUST.PERCENT:1,"
					+ "Analysis Details#ANALYSIS.TYPE," + "INSTAL.EFF.DATE," + "Additional Info#PAYEE:1,"
					+ "Additional Info#ADDITIONAL.INFO:1,";
			values = "Yes," + "TBD," + "4500," + "5.5," + "TPA," + "-2d," + municipality + "," + customerAddress + ",";
			result = createEscrowAccount(mainArrangement, productGroup, product, municipality, fields, values);
			if (!result) {
				stepActual = "Error while creating Escrow account";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();

	}

	@Test(priority = 8, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void processAdHocDepositToEscrowAccount(final String productGroup, final String product) {

		stepDescription = "Process AdHoc deposit to escrow account";
		stepExpected = "AdHoc deposit to escrow account processed successfully";
		String adHocDeposit;

		if (mainArrangement == null || mainArrangement.contains(ERROR) || mainCustomer == null
				|| mainCustomer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			fields = "CREDIT.ACCT.NO," + "CREDIT.CURRENCY," + "CREDIT.AMOUNT," + "DEBIT.ACCT.NO," + "ORDERING.CUST:1,";
			values = mainArrangement + "," + "CAD," + "10T," + "CAD1006500011000," + mainCustomer + ",";
			adHocDeposit = financialTransaction(CREATE, "Escrow Deposit", fields, values);
			if (adHocDeposit == null || adHocDeposit.contains(ERROR)) {
				stepActual = "Error while Processing AdHoc deposit to escrow account";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();

	}

	@Test(priority = 9, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void processAdHocWithdrawalToEscrowAccount(final String productGroup, final String product) {

		stepDescription = "Process AdHoc Withdrawal to escrow account";
		stepExpected = "AdHoc Withdrawal to escrow account processed successfully";
		String adHocWithdrawal;

		if (mainArrangement == null || mainArrangement.contains(ERROR) || mainCustomer == null
				|| mainCustomer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			fields = "CREDIT.ACCT.NO," + "CREDIT.CURRENCY," + "CREDIT.AMOUNT," + "DEBIT.ACCT.NO,";
			values = "CAD1011000011000," + "CAD," + "5000," + mainArrangement + ",";
			adHocWithdrawal = financialTransaction(CREATE, "Escrow Withdrawal", fields, values);
			if (adHocWithdrawal == null || adHocWithdrawal.contains(ERROR)) {
				stepActual = "Error while Processing AdHoc Withdrawal to escrow account";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();

	}

	@Test(priority = 10, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void paymentOrderByCheque(final String productGroup, final String product) {

		stepDescription = "Perform AdHoc payment by cheque for " + mainArrangement;
		stepExpected = "AdHoc payment by cheque Performed successfully";
		String poProduct;

		if (mainArrangement == null || mainArrangement.contains(ERROR) || mainCustomer == null
				|| mainCustomer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			Random randomNumber = new Random();
			int caseNumber = randomNumber.nextInt(3);

			switch (caseNumber) {

			case 0:
				poProduct = "CHQESCROW";
				break;
			case 1:
				poProduct = "CHQMUNI";
				break;

			case 2:
				poProduct = "CHQINT";
				break;

			default:
				poProduct = "CHQESCROW";
				break;
			}

			fields = "PAYMENT.ORDER.PRODUCT," + "DEBIT.ACCOUNT," + "PAYMENT.AMOUNT," + "DEBIT.VALUE.DATE,"
					+ "BENEFICIARY.ID," + "NARRATIVE:1," + "ORDERING.CUSTOMER,";
			values = poProduct + "," + mainArrangement + "," + "10T," + "+3d," + customerBeneficiary + ","
					+ "Test Narrative," + mainCustomer + ",";

			String adhocPayment = createAdHocPayment(CREATE, CHEQUE, mainArrangement, fields, values);
			if (adhocPayment == null || adhocPayment.contains(ERROR)) {
				stepActual = "Error while creating AdHoc payment by cheque";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();

	}

	@Test(priority = 11, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void escrowAccountOverview(final String productGroup, final String product) {

		stepDescription = "View Escrow Account";
		stepExpected = "Escrow account is overviewed Successfully ";

		String currentPaymentAmount;
		String currentPaymentDate;
		String newPaymentAmount;
		String newPaymentDate;
		String payeeDetail;
		String overdueStatus;
		String escrowBalance;
		String activityListHeading;

		if (mainArrangement == null || mainArrangement.contains(ERROR) || mainCustomer == null
				|| mainCustomer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			commandLine("TAB ESCROW.FIND.ACCOUNT", commandLineAvailable);
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "workarea");
			enquiryElements.enquirySearch("Account/Arrangement", "", mainArrangement);
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "workarea");
			enquiryElements.enquiryButtons(VIEW).click();
			switchToPage(LASTPAGE, false);
			for (int i = 1; i <= 3; i++) {
				try {
					String column = Integer.toString(i);
					payeeDetail = versionScreen.rowCell("PayeeDetails", column).getText();

					if (payeeDetail == null) {
						stepActual = "Payee Details' value is null";
						softVerify.fail(stepActual);
						stepResult = StatusAs.FAILED;
					}
				} catch (NoSuchElementException e) {
					stepActual = "Unable to Locate Payee Details elements ";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
			try {
				currentPaymentAmount = versionScreen.versionElement("", "Current Payment Amount", "1").getText();
				currentPaymentDate = versionScreen.versionElement("", "Current Payment Amount", "3").getText();
				newPaymentAmount = versionScreen.versionElement("", "New Payment Amount", "1").getText();
				newPaymentDate = versionScreen.versionElement("", "New Payment Amount", "3").getText();
				if (currentPaymentAmount == null || currentPaymentDate == null || newPaymentAmount == null
						|| newPaymentDate == null) {
					stepActual = "Intallment Details' value is null";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;

				}
			} catch (NoSuchElementException e) {
				stepActual = "Unable to Locate Intallment Details elements ";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			overdueStatus = versionScreen.headerElement("TaxBalances", "3", "2").getAttribute("class");
			if (!overdueStatus.contains("RED")) {

				stepActual = "Escrow Balance Overdue status is shown in Red Text  ";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			escrowBalance = versionScreen.statusElement("Escrow Balance").getText();
			if (escrowBalance == null) {

				stepActual = "Escrow Balance is null ";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			versionScreen.linkText(ADDITIONAL_DETAILS, "Activities").click();

			activityListHeading = versionScreen.headerElement("Transactions", "2", "1").getText();

			if (!activityListHeading.contains("Activities")) {

				stepActual = "List of Activities is not Displayed ";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}

		softVerify.assertAll();

	}

	@Test(priority = 12, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void editEscrowAccount(final String productGroup, final String product) {

		stepDescription = "Edit escrow account";
		stepExpected = "Escrow account edited successfully";

		if (mainArrangement == null || mainArrangement.contains(ERROR) || mainCustomer == null
				|| mainCustomer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			commandLine("TAB ESCROW.FIND.ACCOUNT", commandLineAvailable);
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "workarea");
			enquiryElements.enquirySearch("Account/Arrangement", "", mainArrangement);
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "workarea");
			toolElements.toolsButton(EDIT).click();
			switchToPage(LASTPAGE, false);

			fields = "OPERATION," + "ANNUAL.DISBURSE.AMT:1,";
			values = "MAINTENANCE," + "3000,";

			multiInputData(fields, values, false);
			toolElements.toolsButton(COMMIT_DEAL).click();
			result = inputTable.verifyAcceptOverride();

			if (!result) {
				stepActual = "Error while editing escrow account";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();

	}

	@Test(priority = 13, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void billActions(final String productGroup, final String product) {

		stepDescription = "Perform Bill actions on escrow Account";
		stepExpected = "Bill actions on escrow Account Performed successfully";
		String failedBills;

		if (mainArrangement == null || mainArrangement.contains(ERROR) || mainCustomer == null
				|| mainCustomer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			commandLine("TAB ESCROW.FIND.ACCOUNT", commandLineAvailable);
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "workarea");
			enquiryElements.enquirySearch("Account/Arrangement", "", mainArrangement);
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "workarea");
			enquiryElements.enquiryButtons(VIEW).click();
			switchToPage(LASTPAGE, false);
			versionScreen.linkText(ADDITIONAL_DETAILS, "Bill Actions").click();
			versionScreen.linkText("Bill Actions", "Bill Capture").click();
			switchToPage(LASTPAGE, false);

			fields = "PAYEE.ID," + "PAYEE.REF.NO," + "ORIG.AMOUNT," + "REASON,";
			values = municipality + "," + "TBD," + "100," + "Test Manual Bill,";
			multiInputData(fields, values, false);
			toolElements.toolsButton(COMMIT_DEAL).click();
			result = inputTable.verifyAcceptOverride();

			switchToPage("Escrow Overview", false);
			versionScreen.linkText(ADDITIONAL_DETAILS, "Failed").click();
			failedBills = versionScreen.headerElement("Transactions", "2", "1").getText();
			if (failedBills == null) {
				stepActual = "fail bills is not displayed";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;

			}

			versionScreen.linkText(ADDITIONAL_DETAILS, "Bill Actions").click();
			versionScreen.linkText("Bill Actions", "Bill Maintenance").click();
			switchToPage(LASTPAGE, false);

			inputData("ACTION:1", "Manual", false);

			toolElements.toolsButton(COMMIT_DEAL).click();
			result = inputTable.verifyAcceptOverride();

			if (!result) {
				stepActual = "Error while Performing Bill actions on escrow Account";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();

	}

	@Test(priority = 14, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void closeEscrowAccountCapitalizeInterests(final String productGroup, final String product) {

		stepDescription = "Close Capitalised Interest in escrow account";
		stepExpected = "Capitalised Interest in escrow account closed successfully";

		if (mainArrangement == null || mainArrangement.contains(ERROR) || mainCustomer == null
				|| mainCustomer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -1);
			date = cal.getTime();
			String capitalizeInterestOn = "D_" + inputDateFormat.format(date);
			fields = "START.DATE:3:1," + "START.DATE:4:1,";
			values = capitalizeInterestOn + "," + capitalizeInterestOn + ",";

			result = arrangementActivity("Create Scheduled -2d", "CHANGE ACTIVITY FOR SCHEDULE", mainArrangement,
					productGroup, fields, values);
			if (!result) {
				stepActual = "Error while performing CHANGE ACTIVITY FOR SCHEDULE for escrow account";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();

	}

	@Test(priority = 15, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void closeEscrowAccountStopPayments(final String productGroup, final String product) {

		stepDescription = "Stop Payments for escrow account";
		stepExpected = "Payments for escrow account Stopped successfully";
		if (mainArrangement == null || mainArrangement.contains(ERROR) || mainCustomer == null
				|| mainCustomer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			fields = "PAYMENT.TYPE<:4," + "PAYMENT.TYPE<:3," + "PAYMENT.TYPE<:2,";
			values = " , , ,";

			arrangementActivity("Create Scheduled +1d", "CHANGE ACTIVITY FOR SCHEDULE", mainArrangement, productGroup,
					fields, values);
			if (!result) {
				stepActual = "Error while performing CHANGE ACTIVITY FOR SCHEDULE for escrow account";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();

	}

	@Test(priority = 16, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void closeEscrowAccountSetBalanceToZero(final String productGroup, final String product) {

		stepDescription = "Set Escrow Balance to Zero for escrow account";
		stepExpected = "Escrow Balance is set to Zero for escrow account";
		String escrowBalance;
		if (mainArrangement == null || mainArrangement.contains(ERROR) || mainCustomer == null
				|| mainCustomer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			commandLine("TAB ESCROW.FIND.ACCOUNT", commandLineAvailable);
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "workarea");
			enquiryElements.enquirySearch("Account/Arrangement", "", mainArrangement);
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "workarea");
			enquiryElements.enquiryButtons(VIEW).click();
			switchToPage(LASTPAGE, false);
			escrowBalance = versionScreen.statusElement("Escrow Balance").getText().replaceAll(",", "");

			if (Double.parseDouble(escrowBalance) > 0) {
				fields = "CREDIT.ACCT.NO," + "CREDIT.CURRENCY," + "CREDIT.AMOUNT," + "DEBIT.ACCT.NO,";
				values = "CAD1011000011000," + "CAD," + "5000," + mainArrangement + ",";

				financialTransaction(CREATE, "Escrow Withdrawal", fields, values);
			} else if (Double.parseDouble(escrowBalance) < 0) {
				fields = "CREDIT.ACCT.NO," + "CREDIT.CURRENCY," + "CREDIT.AMOUNT," + "DEBIT.ACCT.NO,"
						+ "ORDERING.CUST:1,";
				values = mainArrangement + ",CAD," + escrowBalance + "," + "CAD1006500011000," + mainCustomer + ",";
			}

			String financialTransaction = financialTransaction(CREATE, "Escrow Deposit", fields, values);

			if (financialTransaction == null || financialTransaction.contains(ERROR)) {
				stepActual = "Error while adjusting escrow balance to zero for escrow account";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();

	}

	@Test(priority = 17, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void closeEscrowAccountFinalStep(final String productGroup, final String product) {

		stepDescription = "Close Escrow account";
		stepExpected = "Escrow account closed successfully";

		if (mainArrangement == null || mainArrangement.contains(ERROR) || mainCustomer == null
				|| mainCustomer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			commandLine("TAB ESCROW.FIND.ACCOUNT", commandLineAvailable);
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "workarea");
			enquiryElements.enquirySearch("Account/Arrangement", "", mainArrangement);
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "workarea");
			toolElements.toolsButton(EDIT).click();
			switchToPage(LASTPAGE, false);
			fields = "OPERATION,";
			values = "CLOSURE,";

			multiInputData(fields, values, false);
			toolElements.toolsButton(COMMIT_DEAL).click();
			result = inputTable.verifyAcceptOverride();

			if (!result) {
				stepActual = "Error while closing escrow account";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();

	}

	@Test(priority = 18, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void processTaxFile(final String productGroup, final String product) {

		stepDescription = "Process Tax file";
		stepExpected = "Tax file processed successfully";

		if (mainArrangement == null || mainArrangement.contains(ERROR) || mainCustomer == null
				|| mainCustomer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			fields = "IRREGULAR.PRD:1," + "IRREGULAR.PRD+:2," + "IRREGULAR.PRD+:3," + "IRREGULAR.PRD+:4,";
			values = "0320," + "0620," + "1020," + "1120,";

			result = escrowPayee(EDIT, fields, values, municipality);
			commandLine("COS ESCROW.IF.EXCEPTION", true);
			switchToPage(LASTPAGE, false);
			if (!driver.getTitle().contains("Escrow File Recon Exceptions")) {
				stepActual = "Enquiry Screen is not displayed for Escrow File Exception";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			commandLine("COS ESCROW.IF.MATCHED", true);
			switchToPage(LASTPAGE, false);
			if (!driver.getTitle().contains("Escrow File Reconciliation")) {
				stepActual = "Enquiry Screen is not displayed for Escrow warehouse";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			commandLine("ENQ ESCROW.MATCHED.RETURNED.LBC", true);
			switchToPage(LASTPAGE, false);
			if (!driver.getTitle().contains("ESCROW.MATCHED.RETURNED")) {
				stepActual = "Enquiry Screen is not displayed for Escrow Matched and Returns";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			if (!result) {
				stepActual = "Error while Processing Tax file";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();

	}
}