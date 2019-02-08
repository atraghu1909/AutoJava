package testcases.PROC;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PROC_OP_MG_XX001 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.1";
	private static String branch = "B2B Branch 623";
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
	private static Calendar cal = Calendar.getInstance();
	private Date date = new Date();
	private String[] customerList;
	private String mainArrangement;
	private String beneficiary;
	private String chequingArrangement;
	private String customer;
	private String limit;
	private String startDate;
	private int ownersCount;

	@Test(priority = 1, enabled = true)
	@Parameters({ "owners", "customerType", "withSettleViaBanking" })
	public void preCondition(@Optional("1") final String owners, @Optional(PERSONAL) final String customerType,
			@Optional("true") final String withSettleViaBanking) {

		stepDescription = "Create customer";
		stepExpected = "Customer created successfully";

		String step1Fields;
		String step1Values;

		if (loginResult) {

			switchToBranch(branch);

			ownersCount = Integer.parseInt(owners);
			customerList = new String[ownersCount];
			for (int i = 0; i < ownersCount; i++) {
				customer = createDefaultCustomer(customerType, "", ROLEBASED_LENDING);
				customerList[i] = customer;
				if (customerList[i] == null || customerList[i].contains(ERROR)) {
					stepActual = "Error while creating customer" + i + ": " + customerList[i];
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}

			if ("true".equals(withSettleViaBanking)) {
				step1Fields = "CUSTOMER:1,CURRENCY,";
				step1Values = customerList[0] + ",CAD,";
				chequingArrangement = arrangements("Create and Authorise", PERSONAL_ACCOUNTS, PERSONAL_CHEQUING,
						ROLEBASED_BANKING, customerList[0], step1Fields, step1Values, DefaultVariables.bankingFields,
						DefaultVariables.bankingValues);
				if (chequingArrangement == null || chequingArrangement.contains(ERROR)) {
					stepActual = "Error while creating Banking Arrangement";
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
	@Parameters({ "collateralValue", "term", "product", "collateralCode", "collateralOccupancy",
			"collateralNumberOfApts", "effectiveDate" })
	public void createLimit(@Optional("200000") final String collateralValue, @Optional("25Y") final String term,
			@Optional("") final String product, @Optional("1") final String collateralCode,
			@Optional("Owner") final String collateralOccupancy, @Optional("1") final String collateralNumberOfApts,
			@Optional("+0d") final String effectiveDate) {

		stepDescription = "Create Limit for customer: " + customer;
		stepExpected = "Limit was created successfully for customer: " + customer;

		String collateralLink;
		String collateral;
		String limitFields;
		String limitValues;
		String collateralLinkFields;
		String collateralLinkValues;
		String collateralFields;
		String collateralValues;
		boolean result;

		if (customerList[0] == null || customerList[0].contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			limitFields = "LIMIT.CURRENCY," + "COUNTRY.OF.RISK:1," + "COLLATERAL.CODE:1," + "INTERNAL.AMOUNT,"
					+ "EXPIRY.DATE," + "MAXIMUM.TOTAL," + "PROPOSAL.DATE," + "APPROVAL.DATE," + "MAXIMUM.SECURED:1,";
			limitValues = "CAD," + "CA," + "301," + collateralValue + "," + "+" + term + "," + collateralValue + ","
					+ effectiveDate + "," + effectiveDate + "," + collateralValue + ",";

			limit = customerLimit("Open", SECURED, ROLEBASED_LENDING, product, customerList[0], "2300", "", limitFields,
					limitValues);

			result = switchToPage(LASTPAGE, false) && compositeScreen.switchToFrame(ID, "workarea")
					&& inputTable.commitAndOverride() && DefaultVariables.authorizeB2BLimit ? authorizeEntity(limit, LIMIT)
							: true;

			if (limit == null || limit.contains(ERROR) || !result) {
				stepActual = "Error while creating Limit for customer: " + customer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			collateralLinkFields = "COLLATERAL.CODE," + "COMPANY:1," + "LIMIT.REFERENCE:1," + "EXPIRY.DATE,"
					+ "VALIDITY.DATE,";
			collateralLinkValues = collateralCode + "," + "B02," + limit + "," + "+" + term + "," + effectiveDate + ",";
			collateralLink = collateral("Create", "Collateral Link", customerList[0], collateralLinkFields,
					collateralLinkValues);

			if (collateralLink == null || collateralLink.contains(ERROR)) {
				stepActual = "Error while creating CollateralLink for customer: " + customerList[0];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			collateralFields = DefaultVariables.collateralFields + "Collateral details#CA.ADR.LINE1,"
					+ "Collateral details#TOWN.CITY," + "Collateral details#US.STATE,"
					+ "Collateral details#CA.POST.CODE," + "Collateral#COLLATERAL.CODE," + "Collateral#VALUE.DATE,"
					+ "COLLATERAL.TYPE," + "NOMINAL.VALUE," + "EXECUTION.VALUE," + "Collateral details#COLL.VALUE,"
					+ "Collateral details#L.COLL.OCCUP," + "Collateral details#L.COLL.BLD.UNTS,";

			collateralValues = DefaultVariables.collateralValues + createdCustomers.get(customer).getAddressStreet()
					+ "," + createdCustomers.get(customer).getAddressCity() + ","
					+ createdCustomers.get(customer).getAddressProvince() + ","
					+ createdCustomers.get(customer).getaddressPostalCode() + "," + collateralCode + "," + effectiveDate
					+ ",1," + collateralValue + "," + collateralValue + "," + collateralValue + ","
					+ collateralOccupancy + "," + collateralNumberOfApts + ",";

			collateral = collateral("Create", "Collateral Details", collateralLink, collateralFields, collateralValues);

			if (collateral == null || collateral.contains(ERROR)) {
				stepActual = "Error while creating Collateral for customer: " + customerList[0];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "productGroup", "product", "customerType", "effectiveDate", "insuranceCompany", "insuranceNumber",
			"commitment", "term", "paymentFrequency", "withSettleViaBanking", "withFixedPayment", "withIAD",
			"withEscrow", "ownerRoles", "gds", "tds" })
	public void openBridgeLoan(@Optional(RETAIL_MORTGAGES) final String productGroup,
			@Optional("") final String product, @Optional(PERSONAL) final String customerType,
			@Optional("+0d") final String effectiveDate, @Optional("Cmhc") final String insuranceCompany,
			@Optional("12345678") final String insuranceNumber, @Optional("100000") final String commitment,
			@Optional("25Y") final String term, @Optional("e0Y e1M e0W e0D e0F") final String paymentFrequency,
			@Optional("false") final String withSettleViaBanking, @Optional("false") final String withFixedPayment,
			@Optional("true") final String withIAD, @Optional("false") final String withEscrow,
			@Optional("BORROWER.OWNER,") final String ownerRoles, @Optional("") final String gds,
			@Optional("") final String tds) {
		stepDescription = "Create Bridge Loan";
		stepExpected = "Bridge Loan Created successfully";

		String step1Fields;
		String step1Values;
		String beneficiaryFields;
		String beneficiaryValues;
		String step2Fields = "";
		String step2Values = "";
		String finalMessage = "";
		boolean result;

		if (limit == null || limit.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			beneficiaryFields = DefaultVariables.beneficiaryFields + "OWNING.CUSTOMER," + "BEN.CUSTOMER,";
			beneficiaryValues = DefaultVariables.beneficiaryValues + customerList[0] + ","
					+ createdCustomers.get(customerList[0]).getCustomerName() + ",";

			beneficiary = beneficiaryCode(CREATE, "EFT Client", "", beneficiaryFields, beneficiaryValues);

			if (beneficiary == null || beneficiary.contains(ERROR)) {
				stepActual = "Error while creating beneficiary for customer: " + customerList[0];
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			step1Fields = "CUSTOMER:1," + "CUSTOMER.ROLE:1," + "CURRENCY," + "EFFECTIVE.DATE,";
			step1Values = customerList[0] + "," + ownerRoles.split(",")[0] + "," + "CAD," + effectiveDate + ",";

			arrangements(OPEN, productGroup, product, ROLEBASED_LENDING, customerList[0], step1Fields, step1Values, "",
					"");

			startDate = readData("EFFECTIVE.DATE");

			step2Fields = DefaultVariables.bridgeLoanFields + "LIMIT.REFERENCE," + "SINGLE.LIMIT," + "L.INS.COMP.NAME,"
					+ "AMOUNT," + "TERM," + "Principal Interest$Control#DAY.BASIS," + "Schedule$PAYMENT.FREQ:1,"
					+ "Schedule$START.DATE:1:1,";
			step2Values = DefaultVariables.bridgeLoanValues + "2300," + "Y," + insuranceCompany + "," + commitment + ","
					+ term + "," + "C," + paymentFrequency + "," + "D_" + startDate + ",";

			step2Fields = step2Fields + "Settlement$Advanced - Pay In#PAYIN.SETTLEMENT:1,"
					+ "Settlement$Advanced - Pay In#PAYIN.ACCOUNT:1:1,"
					+ "Settlement$Advanced - Pay In#PAYIN.ACTIVITY:1:1,"
					+ "Settlement$Advanced - Pay In#PAYIN.BENEFICIARY:1:1,"
					+ "Settlement$Advanced - Pay In#PAYIN.PO.PRODUCT:1:1,"
					+ "Settlement$Advanced - Pay In#PAYIN.SETTLE.ACTIVITY:1,";

			if ("true".equals(withSettleViaBanking)) {
				step2Values = step2Values + "Yes," + chequingArrangement + "," + "ACCOUNTS-DEBIT-ARRANGEMENT," + " ,"
						+ " ," + " ,";
			} else {
				step2Values = step2Values + "Yes," + " ," + " ," + beneficiary + "," + "ACHCREDIT,"
						+ "LENDING-APPLYPAYMENT-PR.REPAYMENT,";
			}

			if ("true".equals(withEscrow)) {
				step2Fields = step2Fields + "Schedule$PAYMENT.TYPE+:2," + "Schedule$PAYMENT.METHOD:2,"
						+ "Schedule$PAYMENT.FREQ:2," + "Schedule$PROPERTY:2:1," + "Schedule$START.DATE:2:1,"
						+ "Schedule$BILL.TYPE:2," + "Schedule$PAYMENT.TYPE+:3," + "Schedule$PAYMENT.METHOD:3,"
						+ "Schedule$PAYMENT.FREQ:3," + "Schedule$PROPERTY:3:1," + "Schedule$BILL.TYPE:3,"
						+ "Schedule$PAYMENT.TYPE+:4," + "Schedule$PAYMENT.METHOD:4," + "Schedule$PAYMENT.FREQ:4,"
						+ "Schedule$PROPERTY:4:1," + "Schedule$BILL.TYPE:4,";
				step2Values = step2Values + "ESCROW," + "Due," + paymentFrequency + "," + "ESCROW," + startDate + ","
						+ "INSTALLMENT," + "ESCROWINT," + "Capitalise," + "e0Y e0M e0W e0D eLMNTHF," + "ESCROWDRINT,"
						+ "INSTALLMENT," + "ESCROWINT," + "Capitalise," + "e0Y e0M e0W e0D eLMNTHF," + "ESCROWCRINT,"
						+ "INSTALLMENT,";
			}

			if ("true".equals(withIAD)) {
				try {
					cal.setTime(sdf.parse(startDate));
				} catch (ParseException e) {
					Reporter.log(e.getMessage(), debugMode);
				}
				cal.add(Calendar.DATE, 15);
				date = cal.getTime();
				startDate = sdf.format(date);

				step2Fields = step2Fields + "Schedule$PAYMENT.TYPE+:2," + "Schedule$PAYMENT.METHOD:2,"
						+ "Schedule$PROPERTY:2:1," + "Schedule$START.DATE:2:1," + "Schedule$BILL.TYPE:2,";
				step2Values = step2Values + "INTEREST.ONLY," + "Due," + "PRINCIPALINT," + startDate + ","
						+ "INSTALLMENT,";
			}

			if (ownersCount > 1) {
				for (int i = 2; i <= ownersCount; i++) {
					inputData("CUSTOMER+:" + i, customerList[i - 1], false);
					inputData("CUSTOMER.ROLE:" + i, "BENEFICIAL.OWNER", false);
				}
			}

			toolElements.toolsButton(VALIDATE_DEAL).click();

			result = multiInputData(step2Fields, step2Values, true);
			mainArrangement = readData("ACCOUNT.REFERENCE");
			toolElements.toolsButton(COMMIT_DEAL).click();
			result = inputTable.verifyAcceptOverride();

			if (result) {
				finalMessage = readTable.message().getText();
			}

			if (result && finalMessage.contains(TXN_COMPLETE)) {
				switchToPage(environmentTitle, true);
			} else {
				mainArrangement = "Error while creating Bridge Loan";
				stepActual = mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters({ "productGroup", "commitment", "effectiveDate" })
	public void disburseBridgeLoan(@Optional(RETAIL_MORTGAGES) final String productGroup,
			@Optional("100000") final String commitment, @Optional("+0d") final String effectiveDate) {

		stepDescription = "Disburse Bridge Loan: " + mainArrangement;
		stepExpected = "Bridge Loan: " + mainArrangement + " disbursed successfully";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			boolean result;

			result = disburseBridgeLoan(mainArrangement, effectiveDate, commitment, productGroup);

			if (!result) {
				stepActual = "Error while Disbursing Bridge Loan: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			softVerify.assertAll();
		}
	}
}
