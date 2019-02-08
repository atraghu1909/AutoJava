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

public class PROC_OP_MG_XX007 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 4.0";
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
	private Calendar cal = Calendar.getInstance();
	private Date date = new Date();
	private String[] customerList;
	private String mainArrangement = "";
	private String dealer;
	private String advisor;
	private String dealerCode;
	private String advisorCode;
	private String dealerArrangement;
	private String advisorArrangement;
	private String limit = "";
	private String limitRef;
	private String chequingArrangement;
	private String beneficiary;
	private String parentLimit;
	private String childLimit;
	private String startDate;
	private String creditAccount;
	private String beneficiaryFields;
	private String beneficiaryValues;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "agentCommissionPlan", "advisorCommissionPlan", "effectiveDate", "withSettleViaBanking" })
	public void preCondition(@Optional("B2B Branch 623") final String branch,
			@Optional("Mortgage Dummy Commission Plan") final String agentCommissionPlan,
			@Optional("Mortgage Dummy Commission Plan") final String advisorCommissionPlan,
			@Optional("+0d") final String effectiveDate, @Optional("false") final String withSettleViaBanking) {

		stepDescription = "Create dealer, advisor, " + agentCommissionPlan + " dealer arrangement and "
				+ advisorCommissionPlan + " advisor arrangement";
		stepExpected = "Dealer, advisor, " + agentCommissionPlan + " dealer arrangement and " + advisorCommissionPlan
				+ " advisor arrangement created successfully";

		String fields;
		String values;
		boolean result;

		if (loginResult) {
			switchToBranch(branch);
			if ("Branch 603".equals(branch)) {
				creditAccount = "CAD1100300011603";
			} else {
				creditAccount = "CAD1100300017623";
			}
			dealer = customer("Open", DEALER_ADVISOR, ROLEBASED_OR, DefaultVariables.dealerAdvisorCIFFields,
					DefaultVariables.dealerAdvisorCIFValues);
			dealerCode = readData("L.DEALER.REP.NO");
			toolElements.toolsButton(COMMIT_DEAL).click();
			result = inputTable.verifyAcceptOverride();

			if (dealer == null || dealer.contains(ERROR) || !result) {
				stepActual = "Error while creating Dealer";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			fields = "CUSTOMER:1," + "CURRENCY," + "EFFECTIVE.DATE,";
			values = dealer + "," + CAD + "," + effectiveDate + ",";
			dealerArrangement = arrangements(CREATE, AGENTS, agentCommissionPlan, "", dealer, fields, values, "", "");
			if (dealerArrangement == null || dealerArrangement.contains(ERROR)) {
				stepActual = "Error while creating Dealer Arrangement";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			advisor = customer("Open", DEALER_ADVISOR, ROLEBASED_OR, DefaultVariables.dealerAdvisorCIFFields,
					DefaultVariables.dealerAdvisorCIFValues);
			advisorCode = readData("L.DEALER.REP.NO");
			fields = "SHORT.NAME:1," + "AGENCY," + "L.DEALER.REP.NO,";
			values = dealerCode + "-" + advisorCode + "," + dealer + "," + dealerCode + "-" + advisorCode + ",";
			multiInputData(fields, values, false);
			toolElements.toolsButton(COMMIT_DEAL).click();
			result = inputTable.verifyAcceptOverride();
			if (advisor == null || advisor.contains(ERROR) || !result) {
				stepActual = "Error while creating Advisor";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			fields = "CUSTOMER:1," + "CURRENCY," + "EFFECTIVE.DATE,";
			values = advisor + "," + CAD + "," + effectiveDate + ",";
			advisorArrangement = arrangements(CREATE, AGENTS, advisorCommissionPlan, "", advisor, fields, values, "",
					"");

			if (advisorArrangement == null || advisorArrangement.contains(ERROR)) {
				stepActual = "Error while creating Advisor Arrangement";
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
	@Parameters({ "owners", "customerType", "withSettleViaBanking" })
	public void customerCreation(@Optional("1") final String owners, @Optional(PERSONAL) final String customerType,
			@Optional("true") final String withSettleViaBanking) {

		stepDescription = "Create " + owners + " customer";
		stepExpected = owners + " customer " + " created successfully";

		String customer;
		String step1Fields;
		String step1Values;
		int ownersCount;

		if (loginResult) {
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
				chequingArrangement = arrangements(CREATE, PERSONAL_ACCOUNTS, PERSONAL_CHEQUING,
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
			stepActual = "Step not run, as LogIn failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "collateralValue", "term", "product", "collateralCode", "collateralOccupancy", "hokProducts",
			"collateralNumberOfApts", "effectiveDate" })
	public void limitAndCollateralCreation(@Optional("200000") final String collateralValue,
			@Optional("25Y") final String term, @Optional("") final String product,
			@Optional("1") final String collateralCode, @Optional("Owner") final String collateralOccupancy,
			@Optional("") final String hokProducts, @Optional("1") final String collateralNumberOfApts,
			@Optional("+0d") final String effectiveDate) {

		stepDescription = "Create limit and collateral for customer: " + customerList[0];
		stepExpected = "Limit and collateral for customer: " + customerList[0] + " created successfully";

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
			if (hokProducts.isEmpty()) {
				limitFields = DefaultVariables.unsecuredLimitFields + "INTERNAL.AMOUNT," + "EXPIRY.DATE,"
						+ "MAXIMUM.TOTAL," + "PROPOSAL.DATE," + "APPROVAL.DATE," + "COLLATERAL.CODE:1,"
						+ "MAXIMUM.SECURED:1,";
				limitValues = DefaultVariables.unsecuredLimitValues + collateralValue + "," + "+" + term + ","
						+ collateralValue + "," + effectiveDate + "," + effectiveDate + "," + "301," + collateralValue
						+ ",";

				if (DefaultVariables.revolvingLimitRefs.containsKey(product)) {
					limitRef = DefaultVariables.revolvingLimitRefs.get(product);
				} else {
					limitRef = "2000";
				}

				limit = customerLimit("Open", SECURED, ROLEBASED_LENDING, product, customerList[0], limitRef, "",
						limitFields, limitValues);

				result = switchToPage(LASTPAGE, false) && compositeScreen.switchToFrame(ID, "workarea")
						&& inputTable.commitAndOverride() && DefaultVariables.authorizeB2BLimit
								? authorizeEntity(limit, LIMIT) : true;

				if (limit == null || limit.contains(ERROR) || !result) {
					stepActual = "Error while creating Limit for customer: " + customerList[0];
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

				collateralLinkFields = "COLLATERAL.CODE," + "COMPANY:1," + "LIMIT.REFERENCE:1," + "EXPIRY.DATE,"
						+ "VALIDITY.DATE,";
				collateralLinkValues = collateralCode + "," + "B02," + limit + "," + "+" + term + "," + effectiveDate
						+ ",";
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

				collateralValues = DefaultVariables.collateralValues
						+ createdCustomers.get(customerList[0]).getAddressStreet() + ","
						+ createdCustomers.get(customerList[0]).getAddressCity() + ","
						+ createdCustomers.get(customerList[0]).getAddressProvince() + ","
						+ createdCustomers.get(customerList[0]).getaddressPostalCode() + "," + collateralCode + ","
						+ effectiveDate + ",1," + collateralValue + "," + collateralValue + "," + collateralValue + ","
						+ collateralOccupancy + "," + collateralNumberOfApts + ",";

				collateral = collateral("Create", "Collateral Details", collateralLink, collateralFields,
						collateralValues);

				if (collateral == null || collateral.contains(ERROR)) {
					stepActual = "Error while creating Collateral for customer: " + customerList[0];
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			} else {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as it is not applicable for this particular Test Case";
				throw new SkipException(stepActual);
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters({ "productGroup", "product", "customerType", "owners", "effectiveDate", "insuranceCompany",
			"insuranceNumber", "commitment", "term", "renewalPeriod", "marginSpread", "postedRate", "paymentFrequency",
			"withSettleViaBanking", "withFixedPayment", "withIAD", "hokProducts", "withEscrow", "ownerRoles",
			"taxLiabilities", "limitAllocations", "lifeInsurance", "disabilityInsurance", "gds", "tds" })
	public void arrangementCreation(@Optional(RETAIL_MORTGAGES) final String productGroup,
			@Optional("") final String product, @Optional(PERSONAL) final String customerType,
			@Optional("1") final String owners, @Optional("+0d") final String effectiveDate,
			@Optional("Cmhc") final String insuranceCompany, @Optional("12345678") final String insuranceNumber,
			@Optional("100000") final String commitment, @Optional("25Y") final String term,
			@Optional("1Y") final String renewalPeriod, @Optional("") final String marginSpread,
			@Optional("4.79") final String postedRate, @Optional("e0Y e1M e0W e0D e0F") final String paymentFrequency,
			@Optional("false") final String withSettleViaBanking, @Optional("false") final String withFixedPayment,
			@Optional("false") final String withIAD, @Optional("") final String hokProducts,
			@Optional("false") final String withEscrow, @Optional("BORROWER.OWNER,") final String ownerRoles,
			@Optional("100.00,") final String taxLiabilities, @Optional("100.00,") final String limitAllocations,
			@Optional("None") final String lifeInsurance, @Optional("None") final String disabilityInsurance,
			@Optional("") final String gds, @Optional("") final String tds) {

		stepDescription = "Create " + product + " Arrangement";
		stepExpected = product + " Arrangement created successfully";

		String step1Fields;
		String step1Values;
		String step2Fields = "";
		String step2Values = "";
		String finalMessage = "";
		String marginRate = "";
		String marginOperator = "";
		String limitSerial;
		String lifePremium = "";
		String disabilityPremium = "";
		int ownersCount;
		boolean result;

		if (customerList[0] == null || customerList[0].contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			if (hokProducts.isEmpty()) {
				if (marginSpread.contains("+")) {
					marginRate = marginSpread.substring(1);
					marginOperator = "Add";
				} else if (marginSpread.contains("-")) {
					marginRate = marginSpread.substring(1);
					marginOperator = "Sub";
				} else if (marginSpread.contains("x")) {
					marginRate = marginSpread.substring(1);
					marginOperator = "Multiply";
				} else {
					marginOperator = "Add";
				}

				if (!lifeInsurance.equals("None")) {
					lifePremium = "100";
				}
				if (!disabilityInsurance.equals("None")) {
					disabilityPremium = "100";
				}

				limitSerial = limit.substring(limit.length() - 2);
				ownersCount = Integer.parseInt(owners);

				beneficiaryFields = DefaultVariables.beneficiaryFields + "OWNING.CUSTOMER," + "BEN.CUSTOMER,";
				beneficiaryValues = DefaultVariables.beneficiaryValues + customerList[0] + ","
						+ createdCustomers.get(customerList[0]).getCustomerName() + ",";

				beneficiary = beneficiaryCode(CREATE, "EFT Client", "", beneficiaryFields, beneficiaryValues);

				if (beneficiary == null || beneficiary.contains(ERROR)) {
					stepActual = "Error while creating beneficiary for customer: " + customerList[0];
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

				step1Fields = "CUSTOMER:1," + "CUSTOMER.ROLE:1," + "AGENT.ID:1," + "AGENT.ARR.ID:1," + "AGENT.ROLE:1,"
						+ "AGENT.ID+:2," + "AGENT.ARR.ID:2," + "AGENT.ROLE:2," + "CURRENCY," + "EFFECTIVE.DATE,";
				step1Values = customerList[0] + "," + ownerRoles.split(",")[0] + "," + advisor + ","
						+ advisorArrangement + ",Agent," + dealer + "," + dealerArrangement + ",Agency," + "CAD,"
						+ effectiveDate + ",";

				step2Fields = "LIMIT.SERIAL," + "SINGLE.LIMIT," + "L.INS.COMP.NAME," + "CA.CMHC.NO," + "L.FIN.SEGMENT,"
						+ "AMOUNT," + "TERM," + "Principal Interest$Periodic#MARGIN.OPER:1:1,"
						+ "Principal Interest$Periodic#MARGIN.RATE:1:1,"
						+ "Principal Interest$MOS Interface#L.POSTED.RATE," + "Customer$Customer#TAX.LIABILITY.PERC:1,"
						+ "Customer$Customer#LIMIT.ALLOC.PERC:1," + "Customer$Insurance#LIFE.INS.FLAG:1,"
						+ "Customer$Insurance#DISABILITY.FLAG:1,";
				step2Values = limitSerial + "," + "Y," + insuranceCompany + "," + insuranceNumber + "," + "1,"
						+ commitment + "," + term + "," + marginOperator + "," + marginRate + "," + postedRate + ","
						+ taxLiabilities.split(",")[0] + "," + limitAllocations.split(",")[0] + "," + "None," + "None,";

				arrangements(OPEN, productGroup, product, ROLEBASED_LENDING, customerList[0], step1Fields, step1Values,
						"", "");

				startDate = readData("EFFECTIVE.DATE");

				if (product.contains(HELOC)) {
					step2Fields = step2Fields + "LIMIT.REFERENCE," + "Settlement$MOS Interface#L.REIMB.TYPE,";
					step2Values = step2Values + limitRef + "," + "1,";

					step2Fields = step2Fields + "Settlement$Advanced - Pay In#PAYIN.SETTLEMENT:1,"
							+ "Settlement$MOS Interface#L.REIMB.TYPE,";

					if ("true".equals(withFixedPayment)) {
						step2Values = step2Values + "No," + "2,";
					} else if ("true".equals(withSettleViaBanking)) {

						step2Fields = step2Fields + "Settlement$Advanced - Pay In#PAYIN.ACCOUNT:1:1,"
								+ "Settlement$Advanced - Pay In#PAYIN.ACTIVITY:1:1,"
								+ "Settlement$Advanced - Pay In#PAYIN.BENEFICIARY:1:1,"
								+ "Settlement$Advanced - Pay In#PAYIN.PO.PRODUCT:1:1,"
								+ "Settlement$Advanced - Pay In#PAYIN.SETTLE.ACTIVITY:1,";

						step2Values = step2Values + "Yes," + "1," + chequingArrangement + ","
								+ "ACCOUNTS-DEBIT-ARRANGEMENT," + " ," + " ," + " ,";
					} else {
						step2Fields = step2Fields + "Settlement$Advanced - Pay In#PAYIN.ACCOUNT:1:1,"
								+ "Settlement$Advanced - Pay In#PAYIN.ACTIVITY:1:1,"
								+ "Settlement$Advanced - Pay In#PAYIN.BENEFICIARY:1:1,"
								+ "Settlement$Advanced - Pay In#PAYIN.PO.PRODUCT:1:1,"
								+ "Settlement$Advanced - Pay In#PAYIN.SETTLE.ACTIVITY:1,";

						step2Values = step2Values + "Yes," + "1," + " ," + " ," + beneficiary + "," + "ACHCREDIT,"
								+ "LENDING-APPLYPAYMENT-PR.REPAYMENT,";
					}
				} else {
					step2Fields = step2Fields + "LIMIT.REFERENCE," + "CHANGE.DATE.TYPE," + "CHANGE.PERIOD,"
							+ "CHANGE.DATE," + "Schedule$PAYMENT.FREQ:1," + "Schedule$START.DATE:1:1,"
							+ "Customer$Insurance#LIFE.INS.FLAG:1," + "Customer$Insurance#DISABILITY.FLAG:1,"
							+ "Customer$Insurance#LIFE.INS.AMT:1," + "Customer$Insurance#DISABILITY.AMT:1," + "GDS,"
							+ "TDS," + "Settlement$Advanced - Pay In#PAYIN.SETTLEMENT:1,"
							+ "Settlement$Advanced - Pay In#PAYIN.ACCOUNT:1:1,"
							+ "Settlement$Advanced - Pay In#PAYIN.ACTIVITY:1:1,"
							+ "Settlement$Advanced - Pay In#PAYIN.BENEFICIARY:1:1,"
							+ "Settlement$Advanced - Pay In#PAYIN.PO.PRODUCT:1:1,"
							+ "Settlement$Advanced - Pay In#PAYIN.SETTLE.ACTIVITY:1,";
					step2Values = step2Values + limitRef + "," + "Period," + renewalPeriod + "," + "+" + term + ","
							+ paymentFrequency + "," + startDate + "," + lifeInsurance + "," + disabilityInsurance + ","
							+ lifePremium + "," + disabilityPremium + "," + gds + "," + tds + ",";

					if ("true".equals(withSettleViaBanking)) {
						step2Values = step2Values + "Yes," + chequingArrangement + "," + "ACCOUNTS-DEBIT-ARRANGEMENT,"
								+ " ," + " ," + " ,";
					} else {
						step2Values = step2Values + "Yes," + " ," + " ," + beneficiary + "," + "ACHCREDIT,"
								+ "LENDING-APPLYPAYMENT-PR.REPAYMENT,";
					}

					if ("true".equals(withEscrow)) {

						step2Fields = step2Fields + "Schedule$PAYMENT.TYPE+:2," + "Schedule$PAYMENT.METHOD:2,"
								+ "Schedule$PAYMENT.FREQ:2," + "Schedule$PROPERTY:2:1," + "Schedule$START.DATE:2:1,"
								+ "Schedule$BILL.TYPE:2," + "Schedule$PAYMENT.TYPE+:3," + "Schedule$PAYMENT.METHOD:3,"
								+ "Schedule$PAYMENT.FREQ:3," + "Schedule$PROPERTY:3:1," + "Schedule$BILL.TYPE:3,"
								+ "Schedule$PAYMENT.TYPE+:4," + "Schedule$PAYMENT.METHOD:4,"
								+ "Schedule$PAYMENT.FREQ:4," + "Schedule$PROPERTY:4:1," + "Schedule$BILL.TYPE:4,";
						step2Values = step2Values + "ESCROW," + "Due," + paymentFrequency + "," + "ESCROW," + startDate
								+ "," + "INSTALLMENT," + "ESCROWINT," + "Capitalise," + "e0Y e0M e0W e0D eLMNTHF,"
								+ "ESCROWDRINT," + "INSTALLMENT," + "ESCROWINT," + "Capitalise,"
								+ "e0Y e0M e0W e0D eLMNTHF," + "ESCROWCRINT," + "INSTALLMENT,";
					}

					if ("true".equals(withIAD)) {
						try {
							cal.setTime(sdf.parse(startDate));
						} catch (ParseException e) {
							Reporter.log(e.getMessage(), debugMode);
						}
						cal.add(Calendar.DATE, 5);
						date = cal.getTime();
						startDate = sdf.format(date);

						step2Fields = step2Fields + "Schedule$PAYMENT.TYPE+:2," + "Schedule$PAYMENT.METHOD:2,"
								+ "Schedule$PROPERTY:2:1," + "Schedule$START.DATE:2:1," + "Schedule$BILL.TYPE:2,";
						step2Values = step2Values + "INTEREST.ONLY," + "Due," + "PRINCIPALINT," + startDate + ","
								+ "INSTALLMENT,";
					}

				}

				if (ownersCount > 1) {
					for (int i = 2; i <= ownersCount; i++) {
						inputData("+CUSTOMER:" + i, customerList[i - 1], false);
						inputData("CUSTOMER.ROLE:" + i, "BENEFICIAL.OWNER", false);
					}
				}

				toolElements.toolsButton(VALIDATE_DEAL).click();

				if (ownersCount > 1 && !product.contains(HELOC)) {
					for (int i = 2; i <= ownersCount; i++) {
						switchToTab("Customer", "Customer");
						inputTable.multiValueButton("CUSTOMER:" + Integer.toString(i - 1), "Expand").click();
						inputData("Customer$Customer#TAX.LIABILITY.PERC:" + i, taxLiabilities.split(",")[i - 1], false);
						inputData("Customer$Customer#LIMIT.ALLOC.PERC:" + i, limitAllocations.split(",")[i - 1], false);
						inputData("Customer$Insurance#LIFE.INS.FLAG:" + i, "None", false);
						inputData("Customer$Insurance#DISABILITY.FLAG:" + i, "None", false);
					}
				}

				mainArrangement = readData("ACCOUNT.REFERENCE");
				multiInputData(step2Fields, step2Values, true);
				result = inputTable.commitAndOverride() && readTable.message().getText().contains(TXN_COMPLETE);

				if (result) {
					switchToPage(environmentTitle, true);
				} else {
					mainArrangement = "Error while creating " + product + " Arrangement";
					stepActual = mainArrangement;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

			} else {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as it is not applicable for this particular Test Case";
				throw new SkipException(stepActual);
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	@Parameters({ "withFixedPayment", "paymentFrequency", "product" })
	public void createHELOCStandingOrder(@Optional("false") final String withFixedPayment,
			@Optional("e0Y e1M e0W e0D e0F") final String paymentFrequency, @Optional("") final String product) {

		stepDescription = "Create HELOC Standing Order";
		stepExpected = "HELOC Standing Order was created successfully";

		String fields;
		String values;
		boolean result = false;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			if (product.contains("HELOC") && "true".equals(withFixedPayment)) {
				fields = "CURRENT.AMOUNT.BAL," + "CURRENT.FREQUENCY," + "BENEFICIARY.ID,";
				values = "100," + paymentFrequency + "," + beneficiary + ",";

				commandLine("STANDING.ORDER,LBC.ACHCREDIT.FIXAMT", commandLineAvailable);
				enquiryElements.transactionIdField("Fixed Amount Standing Orders").sendKeys(mainArrangement);
				toolElements.toolsButton(EDIT_CONTRACT).click();
				switchToPage(LASTPAGE, false);
				inputTable.selectionCriteriaButton("CURRENCY").click();
				switchToPage(LASTPAGE, false);
				enquiryElements.enquirySearch("@ID", "", CAD);
				enquiryElements.linkToSelect("CURRENCY").click();
				switchToPage(LASTPAGE, false);
				result = multiInputData(fields, values, true);
				toolElements.toolsButton(COMMIT_DEAL).click();
				result = inputTable.verifyAcceptOverride();

				if (!result) {
					stepActual = "Error while creating HELOC Standing Order";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			} else {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as it is not applicable for this particular Test Case";
				throw new SkipException(stepActual);
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	@Parameters({ "productGroup", "commitment", "product", "customerType", "hokProducts", "effectiveDate" })
	public void disbursement(@Optional(RETAIL_MORTGAGES) final String productGroup,
			@Optional("100000") final String commitment, @Optional("") final String product,
			@Optional(PERSONAL) final String customerType, @Optional("") final String hokProducts,
			@Optional("+0d") final String effectiveDate) {

		stepDescription = "Disburse " + product + " arrangement: " + mainArrangement;
		stepExpected = product + " Arrangement: " + mainArrangement + " disbursed successfully";

		String fields;
		String values;
		boolean result = false;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			if (hokProducts.isEmpty()) {
				if (product.contains("HELOC")) {
					fields = "DEBIT.AMOUNT," + "CREDIT.ACCT.NO," + "DEBIT.CURRENCY," + "DEBIT.VALUE.DATE,";
					values = commitment + "," + creditAccount + "," + "CAD," + effectiveDate + ",";

					result = arrangementAction(mainArrangement, customerList[0], ROLEBASED_LENDING,
							"Lending Disbursement", fields, values, customerType);

					if (!result) {
						stepActual = "Error while disbursing " + product + " arrangement: " + mainArrangement;
						softVerify.fail(stepActual);
						stepResult = StatusAs.FAILED;
					}

				} else {
					result = disburseBridgeLoan(mainArrangement, effectiveDate, commitment, productGroup);

					if (!result) {
						stepActual = "Error while disbursing " + product + " arrangement: " + mainArrangement;
						softVerify.fail(stepActual);
						stepResult = StatusAs.FAILED;
					}
				}
			} else {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as it is not applicable for this particular Test Case";
				throw new SkipException(stepActual);
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 7, enabled = true)
	@Parameters({ "productGroup", "commitment", "effectiveDate", "product", "hokProducts", "hokCollateralValues",
			"collateralCode", "hokInsuranceCompanies", "owners", "hokMarginSpreads", "hokInsuranceNumbers", "term",
			"withIAD", "withEscrow", "withSettleViaBanking", "withFixedPayment", "renewalPeriod", "hokPostedRates",
			"paymentFrequency", "ownerRoles", "collateralOccupancy", "taxLiabilities", "limitAllocations",
			"lifeInsurance", "disabilityInsurance", "gds", "tds", "customerType", "collateralNumberOfApts" })
	public void arrangementCreationHOK(@Optional(RETAIL_MORTGAGES) final String productGroup,
			@Optional("100000") final String commitment, @Optional("+0d") final String effectiveDate,
			@Optional("Fixed Rate Closed Term Mortgage") final String product, @Optional("") final String hokProducts,
			@Optional("") final String hokCollateralValues, @Optional("1") final String collateralCode,
			@Optional("") final String hokInsuranceCompanies, @Optional("1") final String owners,
			@Optional(" , , , ") final String hokMarginSpreads, @Optional("") final String hokInsuranceNumbers,
			@Optional("25Y") final String term, @Optional("false") final String withIAD,
			@Optional("false") final String withEscrow, @Optional("false") final String withSettleViaBanking,
			@Optional("false") final String withFixedPayment, @Optional("1Y") final String renewalPeriod,
			@Optional("") final String hokPostedRates, @Optional("e0Y e1M e0W e0D e0F") final String paymentFrequency,
			@Optional("BORROWER.OWNER,") final String ownerRoles, @Optional("Owner") final String collateralOccupancy,
			@Optional("100.00,") final String taxLiabilities, @Optional("100.00,") final String limitAllocations,
			@Optional("None") final String lifeInsurance, @Optional("None") final String disabilityInsurance,
			@Optional("") final String gds, @Optional("") final String tds,
			@Optional(PERSONAL) final String customerType, @Optional("1") final String collateralNumberOfApts) {

		stepDescription = "Create HOK arrangement";
		stepExpected = "HOK arrangement created successfully";

		String step1Fields;
		String step1Values;
		String step2Fields;
		String step2Values;
		String limitFields;
		String limitValues;
		String finalMessage = "";
		String marginRate = "";
		String marginOperator = "";
		String lifePremium = "";
		String disabilityPremium = "";
		String limitSerial;
		String collateralLink;
		String collateral;
		String collateralLinkFields;
		String collateralLinkValues;
		String collateralFields;
		String collateralValues;
		String hokCollateralsSum;
		String mortagageLimit = "";
		String helocLimit = "";
		String hokProduct;
		String[] arrangements = new String[hokProducts.split(",").length];
		int hokCollaterals = 0;
		int hokCollateralvalues;
		int limitAmountMortgage = 0;
		int limitAmountHELOC = 0;
		int ownersCount = Integer.parseInt(owners);
		boolean result = false;

		if (!hokProducts.isEmpty()) {
			if (customerList[0] == null || customerList[0].contains(ERROR)) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step not run, as Pre-Condition failed";
				throw new SkipException(stepActual);
			} else {

				hokCollateralvalues = hokCollateralValues.split(",").length;
				for (int i = 0; i < hokCollateralvalues; i++) {
					hokCollaterals = hokCollaterals + Integer.parseInt(hokCollateralValues.split(",")[i]);
				}
				hokCollateralsSum = Integer.toString(hokCollaterals);
				limitFields = DefaultVariables.unsecuredLimitFields + "EXPIRY.DATE," + "INTERNAL.AMOUNT,"
						+ "MAXIMUM.TOTAL," + "PROPOSAL.DATE," + "APPROVAL.DATE," + "COLLATERAL.CODE:1,"
						+ "MAXIMUM.SECURED:1,";
				limitValues = DefaultVariables.unsecuredLimitValues + "+99Y," + hokCollateralsSum + ","
						+ hokCollateralsSum + "," + effectiveDate + "," + effectiveDate + "," + "301,"
						+ hokCollateralsSum + ",";

				parentLimit = customerLimit("Open", SECURED, ROLEBASED_LENDING, product, customerList[0], "2100", "",
						limitFields, limitValues);
				
				result = switchToPage(LASTPAGE, false) && compositeScreen.switchToFrame(ID, "workarea")
						&& inputTable.commitAndOverride() && DefaultVariables.authorizeB2BLimit
								? authorizeEntity(parentLimit, LIMIT) : true;

				if (parentLimit == null || parentLimit.contains(ERROR) || !result) {
					stepActual = "Error while creating parent Limit";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

				for (int i = 0; i < hokCollateralvalues; i++) {

					if (hokProducts.split(",")[i].equals("M")) {
						limitAmountMortgage += Integer.parseInt(hokCollateralValues.split(",")[i]);
						mortagageLimit = Integer.toString(limitAmountMortgage);

					} else if (hokProducts.split(",")[i].equals("H")) {
						limitAmountHELOC += Integer.parseInt(hokCollateralValues.split(",")[i]);
						helocLimit = Integer.toString(limitAmountHELOC);

					}
				}

				beneficiaryFields = DefaultVariables.beneficiaryFields + "OWNING.CUSTOMER," + "BEN.CUSTOMER,";
				beneficiaryValues = DefaultVariables.beneficiaryValues + customerList[0] + ","
						+ createdCustomers.get(customerList[0]).getCustomerName() + ",";

				beneficiary = beneficiaryCode(CREATE, "EFT Client", "", beneficiaryFields, beneficiaryValues);

				if (beneficiary == null || beneficiary.contains(ERROR)) {
					stepActual = "Error while creating HELOC beneficiary for customer: " + customerList[0];
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

				if (hokProducts.contains("M")) {
					limitFields = DefaultVariables.unsecuredLimitFields + "EXPIRY.DATE," + "INTERNAL.AMOUNT,"
							+ "MAXIMUM.TOTAL," + "PROPOSAL.DATE," + "APPROVAL.DATE," + "COLLATERAL.CODE:1,"
							+ "MAXIMUM.SECURED:1,";
					limitValues = DefaultVariables.unsecuredLimitValues + "+99Y," + mortagageLimit + ","
							+ mortagageLimit + "," + effectiveDate + "," + effectiveDate + "," + "301," + mortagageLimit
							+ ",";

					childLimit = customerLimit("Open", SECURED, ROLEBASED_LENDING, product, customerList[0], "2101", "",
							limitFields, limitValues);

					result = switchToPage(LASTPAGE, false) && compositeScreen.switchToFrame(ID, "workarea")
							&& inputTable.commitAndOverride() && DefaultVariables.authorizeB2BLimit
									? authorizeEntity(childLimit, LIMIT) : true;

					if (childLimit == null || childLimit.contains(ERROR) || !result) {
						stepActual = "Error while creating child Limit of: 2101";
						softVerify.fail(stepActual);
						stepResult = StatusAs.FAILED;
					}
				}

				if (hokProducts.contains("H")) {
					limitFields = DefaultVariables.unsecuredLimitFields + "EXPIRY.DATE," + "INTERNAL.AMOUNT,"
							+ "MAXIMUM.TOTAL," + "PROPOSAL.DATE," + "APPROVAL.DATE," + "COLLATERAL.CODE:1,"
							+ "MAXIMUM.SECURED:1,";
					limitValues = DefaultVariables.unsecuredLimitValues + "+99Y," + helocLimit + "," + helocLimit + ","
							+ effectiveDate + "," + effectiveDate + "," + "301," + helocLimit + ",";

					childLimit = customerLimit("Open", UNSECURED, ROLEBASED_LENDING, product, customerList[0], "2103",
							"", limitFields, limitValues);

					result = switchToPage(LASTPAGE, false) && compositeScreen.switchToFrame(ID, "workarea")
							&& inputTable.commitAndOverride() && DefaultVariables.authorizeB2BLimit
									? authorizeEntity(childLimit, LIMIT) : true;


					if (childLimit == null || childLimit.contains(ERROR) || !result) {
						stepActual = "Error while creating childLimit of: 2103";
						softVerify.fail(stepActual);
						stepResult = StatusAs.FAILED;
					}
				}

				collateralLinkFields = "COLLATERAL.CODE," + "COMPANY:1," + "LIMIT.REFERENCE:1," + "EXPIRY.DATE,"
						+ "VALIDITY.DATE,";
				collateralLinkValues = collateralCode + "," + "B02," + parentLimit + "," + "+99Y," + effectiveDate
						+ ",";
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
				collateralValues = DefaultVariables.collateralValues
						+ createdCustomers.get(customerList[0]).getAddressStreet() + ","
						+ createdCustomers.get(customerList[0]).getAddressCity() + ","
						+ createdCustomers.get(customerList[0]).getAddressProvince() + ","
						+ createdCustomers.get(customerList[0]).getaddressPostalCode() + "," + collateralCode + ","
						+ effectiveDate + "," + "1," + hokCollateralsSum + "," + hokCollateralsSum + ","
						+ hokCollateralsSum + "," + collateralOccupancy + "," + collateralNumberOfApts + ",";

				collateral = collateral("Create", "Collateral Details", collateralLink, collateralFields,
						collateralValues);

				if (collateral == null || collateral.contains(ERROR)) {
					stepActual = "Error while creating Collateral for customer: " + customerList[0];
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

				limitSerial = parentLimit.substring(parentLimit.length() - 2);

				for (int j = 0; j < hokProducts.split(",").length; j++) {

					if (hokProducts.split(",")[j].equals("M")) {
						hokProduct = product;
					} else {
						hokProduct = HELOC;
					}

					step2Fields = "L.INS.COMP.NAME,CA.CMHC.NO,Principal Interest$MOS Interface#L.POSTED.RATE,";
					step2Values = hokInsuranceCompanies.split(",")[j] + "," + hokInsuranceNumbers.split(",")[j] + ","
							+ hokPostedRates.split(",")[j] + ",";

					if (hokMarginSpreads.split(",")[j].contains("+")) {
						marginRate = hokMarginSpreads.split(",")[j].substring(1);
						marginOperator = "Add";
					} else if (hokMarginSpreads.split(",")[j].contains("-")) {
						marginRate = hokMarginSpreads.split(",")[j].substring(1);
						marginOperator = "Sub";
					} else if (hokMarginSpreads.split(",")[j].contains("x")) {
						marginRate = hokMarginSpreads.split(",")[j].substring(1);
						marginOperator = "Multiply";
					} else {
						marginOperator = "Add";
					}

					if (!lifeInsurance.equals("None")) {
						lifePremium = "100";
					}
					if (!disabilityInsurance.equals("None")) {
						disabilityPremium = "100";
					}

					step1Fields = "CUSTOMER:1," + "CUSTOMER.ROLE:1," + "AGENT.ID:1," + "AGENT.ARR.ID:1,"
							+ "AGENT.ROLE:1," + "AGENT.ID+:2," + "AGENT.ARR.ID:2," + "AGENT.ROLE:2," + "CURRENCY,"
							+ "EFFECTIVE.DATE,";
					step1Values = customerList[0] + "," + ownerRoles.split(",")[0] + "," + advisor + ","
							+ advisorArrangement + "," + "Agent," + dealer + "," + dealerArrangement + "," + "Agency,"
							+ "CAD," + effectiveDate + ",";

					step2Fields = step2Fields + "LIMIT.REFERENCE," + "LIMIT.SERIAL," + "SINGLE.LIMIT,"
							+ "L.FIN.SEGMENT," + "AMOUNT," + "TERM," + "Principal Interest$Periodic#MARGIN.OPER:1:1,"
							+ "Principal Interest$Periodic#MARGIN.RATE:1:1,";
					step2Values = step2Values + "2103," + limitSerial + "," + "N," + "1," + commitment + "," + term
							+ "," + marginOperator + "," + marginRate + ",";

					arrangements("Open", productGroup, hokProduct, ROLEBASED_LENDING, customerList[0], step1Fields,
							step1Values, "", "");

					startDate = readData("EFFECTIVE.DATE");

					if (ownersCount > 1) {
						for (int i = 2; i <= ownersCount; i++) {
							inputData("CUSTOMER+:" + i, customerList[i - 1], false);
							inputData("CUSTOMER.ROLE:" + i, ownerRoles.split(",")[i - 1], false);
						}
					}

					if (hokProducts.split(",")[j].equals("M")) {
						step2Fields = step2Fields + "LIMIT.REFERENCE," + "LIMIT.SERIAL," + "SINGLE.LIMIT,"
								+ "L.FIN.SEGMENT," + "AMOUNT," + "TERM," + "CHANGE.DATE.TYPE," + "CHANGE.PERIOD,"
								+ "CHANGE.DATE," + "Principal Interest$Periodic#MARGIN.OPER:1:1,"
								+ "Principal Interest$Periodic#MARGIN.RATE:1:1," + "Schedule$PAYMENT.FREQ:1,"
								+ "Schedule$START.DATE:1:1," + "Customer$Insurance#LIFE.INS.FLAG:1,"
								+ "Customer$Insurance#DISABILITY.FLAG:1," + "Customer$Insurance#LIFE.INS.AMT:1,"
								+ "Customer$Insurance#DISABILITY.AMT:1," + "GDS," + "TDS,"
								+ "Settlement$Advanced - Pay In#PAYIN.SETTLEMENT:1,"
								+ "Settlement$Advanced - Pay In#PAYIN.ACCOUNT:1:1,"
								+ "Settlement$Advanced - Pay In#PAYIN.ACTIVITY:1:1,"
								+ "Settlement$Advanced - Pay In#PAYIN.BENEFICIARY:1:1,"
								+ "Settlement$Advanced - Pay In#PAYIN.PO.PRODUCT:1:1,"
								+ "Settlement$Advanced - Pay In#PAYIN.SETTLE.ACTIVITY:1,";
						step2Values = step2Values + "2101," + limitSerial + "," + "N," + "1," + commitment + "," + term
								+ "," + "Period," + renewalPeriod + "," + "+" + term + "," + marginOperator + ","
								+ marginRate + "," + paymentFrequency + "," + startDate + "," + lifeInsurance + ","
								+ disabilityInsurance + "," + lifePremium + "," + disabilityPremium + "," + gds + ","
								+ tds + ",";

						if ("true".equals(withSettleViaBanking)) {
							step2Values = step2Values + "Yes," + chequingArrangement + ","
									+ "ACCOUNTS-DEBIT-ARRANGEMENT," + " ," + " ," + " ,";
						} else {
							step2Values = step2Values + "Yes," + " ," + " ," + beneficiary + "," + "ACHCREDIT,"
									+ "LENDING-APPLYPAYMENT-PR.REPAYMENT,";
						}

						if ("true".equals(withEscrow)) {
							step2Fields = step2Fields + "Schedule$PAYMENT.TYPE+:2," + "Schedule$PAYMENT.METHOD:2,"
									+ "Schedule$PAYMENT.FREQ:2," + "Schedule$PROPERTY:2:1," + "Schedule$START.DATE:2:1,"
									+ "Schedule$BILL.TYPE:2," + "Schedule$PAYMENT.TYPE+:3,"
									+ "Schedule$PAYMENT.METHOD:3," + "Schedule$PAYMENT.FREQ:3,"
									+ "Schedule$PROPERTY:3:1," + "Schedule$BILL.TYPE:3," + "Schedule$PAYMENT.TYPE+:4,"
									+ "Schedule$PAYMENT.METHOD:4," + "Schedule$PAYMENT.FREQ:4,"
									+ "Schedule$PROPERTY:4:1," + "Schedule$BILL.TYPE:4,";
							step2Values = step2Values + "ESCROW," + "Due," + paymentFrequency + "," + "ESCROW,"
									+ startDate + "," + "INSTALLMENT," + "ESCROWINT," + "Capitalise,"
									+ "e0Y e0M e0W e0D eLMNTHF," + "ESCROWDRINT," + "INSTALLMENT," + "ESCROWINT,"
									+ "Capitalise," + "e0Y e0M e0W e0D eLMNTHF," + "ESCROWCRINT," + "INSTALLMENT,";
						}
					} else if (hokProducts.split(",")[j].equals("H")) {
						step2Fields = step2Fields + "LIMIT.REFERENCE," + "Settlement$MOS Interface#L.REIMB.TYPE,";
						step2Values = step2Values + "2103," + "1,";

						step2Fields = step2Fields + "Settlement$Advanced - Pay In#PAYIN.SETTLEMENT:1,"
								+ "Settlement$MOS Interface#L.REIMB.TYPE,";

						if ("true".equals(withFixedPayment)) {
							step2Values = step2Values + "No," + "2,";
						} else if ("true".equals(withSettleViaBanking)) {

							step2Fields = step2Fields + "Settlement$Advanced - Pay In#PAYIN.ACCOUNT:1:1,"
									+ "Settlement$Advanced - Pay In#PAYIN.ACTIVITY:1:1,"
									+ "Settlement$Advanced - Pay In#PAYIN.BENEFICIARY:1:1,"
									+ "Settlement$Advanced - Pay In#PAYIN.PO.PRODUCT:1:1,"
									+ "Settlement$Advanced - Pay In#PAYIN.SETTLE.ACTIVITY:1,";

							step2Values = step2Values + "Yes," + "1," + chequingArrangement + ","
									+ "ACCOUNTS-DEBIT-ARRANGEMENT," + " ," + " ," + " ,";
						} else {
							step2Fields = step2Fields + "Settlement$Advanced - Pay In#PAYIN.ACCOUNT:1:1,"
									+ "Settlement$Advanced - Pay In#PAYIN.ACTIVITY:1:1,"
									+ "Settlement$Advanced - Pay In#PAYIN.BENEFICIARY:1:1,"
									+ "Settlement$Advanced - Pay In#PAYIN.PO.PRODUCT:1:1,"
									+ "Settlement$Advanced - Pay In#PAYIN.SETTLE.ACTIVITY:1,";

							step2Values = step2Values + "Yes," + "1," + " ," + " ," + beneficiary + "," + "ACHCREDIT,"
									+ "LENDING-APPLYPAYMENT-PR.REPAYMENT,";
						}
					}

					if ("true".equals(withIAD)) {
						try {
							cal.setTime(sdf.parse(startDate));
						} catch (ParseException e) {
							e.printStackTrace();
						}
						cal.add(Calendar.DATE, 5);
						date = cal.getTime();
						startDate = sdf.format(date);

						step2Fields = step2Fields + "Schedule$PAYMENT.TYPE+:2," + "Schedule$PAYMENT.METHOD:2,"
								+ "Schedule$PROPERTY:2:1," + "Schedule$START.DATE:2:1," + "Schedule$BILL.TYPE:2,";
						step2Values = step2Values + "INTEREST.ONLY," + "Due," + "PRINCIPALINT," + startDate + ","
								+ "INSTALLMENT,";
					}

					toolElements.toolsButton(VALIDATE_DEAL).click();

					if (ownersCount > 1 && hokProducts.split(",")[j].equals("M")) {
						for (int i = 2; i <= ownersCount; i++) {
							switchToTab("Customer", "Customer");
							inputTable.multiValueButton("CUSTOMER:" + Integer.toString(i - 1), "Expand").click();
							inputData("Customer$Customer#TAX.LIABILITY.PERC:" + i, taxLiabilities.split(",")[i - 1],
									false);
							inputData("Customer$Customer#LIMIT.ALLOC.PERC:" + i, limitAllocations.split(",")[i - 1],
									false);
						}
					}

					arrangements[j] = readData("ACCOUNT.REFERENCE");
					multiInputData(step2Fields, step2Values, true);
					result = inputTable.commitAndOverride() && readTable.message().getText().contains(TXN_COMPLETE);

					if (result) {
						switchToPage(environmentTitle, true);
					} else {
						if (hokProducts.split(",")[j].equals("M")) {
							stepActual = "Error while creating hok Mortgage Arrangement " + j + 1;
							softVerify.fail(stepActual);
							stepResult = StatusAs.FAILED;
						} else {
							stepActual = "Error while creating hok HELOC Arrangement " + j + 1;
							softVerify.fail(stepActual);
							stepResult = StatusAs.FAILED;
						}
					}
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
