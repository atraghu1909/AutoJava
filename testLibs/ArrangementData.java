package testLibs;

import java.util.ArrayList;
import java.util.List;

public class ArrangementData {
	protected List<ArrangementData> hokArrangements;

	private final StringBuilder arrStep1FieldsBuilder;
	private final StringBuilder arrStep1ValuesBuilder;
	private final StringBuilder arrStep2FieldsBuilder;
	private final StringBuilder arrStep2ValuesBuilder;
	private final StringBuilder limitFieldsBuilder;
	private final StringBuilder limitValuesBuilder;
	private final StringBuilder collateralLinkFieldsBuilder;
	private final StringBuilder collateralLinkValuesBuilder;
	private final StringBuilder collateralFieldsBuilder;
	private final StringBuilder collateralValuesBuilder;
	private final StringBuilder hokParentLimitFieldsBuilder;
	private final StringBuilder hokParentLimitValuesBuilder;

	private String variableName;
	private String startDate;
	protected String arrangementID;

	protected String advisor;
	protected String collateralCode;
	protected String collateralType;
	protected String collateralNumberOfApts;
	protected String collateralOccupancy;
	protected String collateralValue;
	protected String commissionPlanDealer;
	protected String commissionPlanAdvisor;
	protected String commitmentAmount;
	protected String completeLimitId;
	protected String currency;
	protected String customers;
	protected String customersRoles;
	protected String dealer;
	protected String disburseAmount;
	protected String effectiveDate;
	protected String escrowPayee;
	protected String fundAmount;
	protected String insuranceDisab;
	protected String insuranceLife;
	protected String limitReference;
	protected String limitSerial;
	protected String limitType;
	protected String marginOperator;
	protected String marginRate;
	protected String paymentFrequency;
	protected String productGroup;
	protected String product;
	protected String roleBasedPage;
	protected String settlementAccount;
	protected String reserveAccount;
	protected String statementID;
	protected String statementType;
	protected String term;

	protected String arrangementStep1Fields;
	protected String arrangementStep1Values;
	protected String arrangementStep2Fields;
	protected String arrangementStep2Values;
	protected String limitFields;
	protected String limitValues;
	protected String collateralLinkFields;
	protected String collateralLinkValues;
	protected String collateralFields;
	protected String collateralValues;
	protected String agentCommissionFields;
	protected String agentCommissionValues;
	protected String hokParentLimitFields;
	protected String hokParentLimitValues;
	protected String collateralLink;
	protected String collateralDetails;

	protected int owners;

	protected boolean createAgent;
	protected boolean createBanking;
	protected boolean createBeneficiary;
	protected boolean createCollateral;
	protected boolean createCustomer;
	protected boolean createEscrowAccount;
	protected boolean createEscrowPayee;
	protected boolean createLimit;
	protected boolean createReserveAccount;
	protected boolean createStatement;
	protected boolean disburseArrangement;
	protected boolean fundArrangement;
	protected boolean hokIndicator;
	protected boolean findMunicipalityPayee;

	// Constructors and Getters

	public ArrangementData(final String variableName, final String productGroup, final String product) {
		this.variableName = variableName;
		customers = "NEW";
		customersRoles = "";
		owners = 1;
		createCustomer = true;
		createLimit = false;
		limitSerial = "01";
		createCollateral = true;
		effectiveDate = "+0d";
		insuranceLife = "None";
		insuranceDisab = "None";
		settlementAccount = "";
		currency = "CAD";
		collateralValue = "500000";
		commitmentAmount = "100000";
		paymentFrequency = "e0Y e1M e0W e0D e0F";

		this.productGroup = productGroup;
		this.product = product;

		if (productGroup.equals(RepositoryPaths.RETAIL_MORTGAGES) || productGroup.equals(RepositoryPaths.PERSONAL_LOC)
				|| productGroup.equals(RepositoryPaths.PERSONAL_LOANS)
				|| productGroup.equals(RepositoryPaths.COMMERCIAL_LOANS)) {
			createLimit = true;
		}

		roleBasedPage = DefaultVariables.productLineRoleBasedPage
				.get(DefaultVariables.productGroupArrangementType.get(productGroup));

		limitReference = DefaultVariables.revolvingLimitRefs.get(product);

		if (DefaultVariables.productLimitType.containsKey(product)) {
			limitType = DefaultVariables.productLimitType.get(product);
			limitFields = DefaultVariables.productLimitFields.get(product);
			limitValues = DefaultVariables.productLimitValues.get(product);
		} else if (product.contains(RepositoryPaths.UNSECURED)) {
			limitType = RepositoryPaths.UNSECURED;
			limitFields = DefaultVariables.unsecuredLimitFields;
			limitValues = DefaultVariables.unsecuredLimitValues;
		} else {
			limitType = RepositoryPaths.SECURED;
			limitFields = DefaultVariables.securedLoanLimitFields;
			limitValues = DefaultVariables.securedLoanLimitValues;
		}

		if (limitType.contains(RepositoryPaths.UNSECURED)) {
			createCollateral = false;
		}

		if (RepositoryPaths.COMMERCIAL_LOANS.equals(productGroup)) {
			collateralCode = "151";
			collateralType = "150";
		} else {
			collateralCode = "1";
			collateralType = "1";
		}

		collateralLinkFields = "COLLATERAL.CODE," + "COMPANY:1,";
		collateralLinkValues = collateralCode + "," + "B2B,";

		collateralFields = DefaultVariables.collateralFields + "COLLATERAL.TYPE," + "COLLATERAL.CODE,";
		collateralValues = DefaultVariables.collateralValues + collateralType + "," + collateralCode + ",";

		arrangementStep1Fields = "CURRENCY,";
		arrangementStep1Values = currency + ",";

		if (DefaultVariables.productFields.containsKey(product)) {
			arrangementStep2Fields = DefaultVariables.productFields.get(product);
			arrangementStep2Values = DefaultVariables.productValues.get(product);
		} else {
			arrangementStep2Fields = DefaultVariables.productGroupFields.get(productGroup);
			arrangementStep2Values = DefaultVariables.productGroupValues.get(productGroup);
		}

		arrStep1FieldsBuilder = new StringBuilder(arrangementStep1Fields);
		arrStep1ValuesBuilder = new StringBuilder(arrangementStep1Values);
		arrStep2FieldsBuilder = new StringBuilder(arrangementStep2Fields);
		arrStep2ValuesBuilder = new StringBuilder(arrangementStep2Values);
		limitFieldsBuilder = new StringBuilder(limitFields);
		limitValuesBuilder = new StringBuilder(limitValues);
		collateralLinkFieldsBuilder = new StringBuilder(collateralLinkFields);
		collateralLinkValuesBuilder = new StringBuilder(collateralLinkValues);
		collateralFieldsBuilder = new StringBuilder(collateralFields);
		collateralValuesBuilder = new StringBuilder(collateralValues);
		hokParentLimitFieldsBuilder = new StringBuilder();
		hokParentLimitValuesBuilder = new StringBuilder();
	}

	public String getCustomers() {
		return this.customers;
	}

	public String getDealer() {
		return this.dealer;
	}

	public String getCommissionPlanDealer() {
		return this.commissionPlanDealer;
	}

	public String getAdvisor() {
		return this.advisor;
	}

	public String getCommissionPlanAdvisor() {
		return this.commissionPlanAdvisor;
	}

	public String getCompleteLimitId() {
		return this.completeLimitId;
	}

	public String getcollateralLink() {
		return this.collateralLink;
	}

	public String getcollateralDetails() {
		return this.collateralDetails;
	}

	public String getSettlementAccount() {
		return this.settlementAccount;
	}

	public String getReserveAccount() {
		return this.reserveAccount;
	}

	public String getLimitType() {
		return this.limitType;
	}

	public String getArrangementID() {
		return this.arrangementID;
	}

	public String getStatementID() {
		return this.statementID;
	}

	public void setCustomers(final String customers, final CustomerData mainCustomerData) {
		this.customers = customers;

		collateralLinkFieldsBuilder.append("LIMIT.REF.CUST:1,");
		collateralLinkValuesBuilder.append(customers.split(",")[0]).append(',');

		collateralFieldsBuilder.append("Collateral details#CA.ADR.LINE1,").append("Collateral details#TOWN.CITY,")
				.append("Collateral details#US.STATE,").append("Collateral details#CA.POST.CODE,");
		collateralValuesBuilder.append(mainCustomerData.getAddressStreet()).append(',')
				.append(mainCustomerData.getAddressCity()).append(',').append(mainCustomerData.getAddressProvince())
				.append(',').append(mainCustomerData.getaddressPostalCode()).append(',');

		for (int i = 0; i < owners; i++) {

			if (i == 0) {
				arrStep1FieldsBuilder.append("CUSTOMER:").append(i + 1).append(',');
				arrStep1ValuesBuilder.append(customers.split(",")[i]).append(',');
			} else {
				arrStep1FieldsBuilder.append("CUSTOMER+:").append(i + 1).append(',');
				arrStep1ValuesBuilder.append(customers.split(",")[i]).append(',');
			}

			if ("".equals(customersRoles)) {
				arrStep1FieldsBuilder.append("CUSTOMER.ROLE:").append(i + 1).append(',');
				arrStep1ValuesBuilder.append("BORROWER.OWNER,");
			} else {
				arrStep1FieldsBuilder.append("CUSTOMER.ROLE:").append(i + 1).append(',');
				arrStep1ValuesBuilder.append(customersRoles.split(",")[i]).append(',');
			}

		}

		if (hokIndicator) {
			for (final ArrangementData currentHOKArrangement : hokArrangements) {
				currentHOKArrangement.setCustomers(customers, mainCustomerData);
			}
		}

		this.collateralLinkFields = collateralLinkFieldsBuilder.toString();
		this.collateralLinkValues = collateralLinkValuesBuilder.toString();
		this.collateralFields = collateralFieldsBuilder.toString();
		this.collateralValues = collateralValuesBuilder.toString();
		this.arrangementStep1Fields = arrStep1FieldsBuilder.toString();
		this.arrangementStep1Values = arrStep1ValuesBuilder.toString();
	}

	public void setLimit(final String limitReference, final String limitSerial, final String singleLimit) {
		String actualLimit = customers.split(",")[0] + ".000" + limitReference + "." + limitSerial;
		this.completeLimitId = actualLimit;
		this.limitReference = limitReference;
		this.limitSerial = limitSerial;

		collateralLinkFieldsBuilder.append("LIMIT.REFERENCE:1,");
		collateralLinkValuesBuilder.append(actualLimit).append(",");

		arrStep2FieldsBuilder.append("LIMIT.REFERENCE,").append("LIMIT.SERIAL,").append("SINGLE.LIMIT,");
		arrStep2ValuesBuilder.append(limitReference).append(',').append(limitSerial).append(',').append(singleLimit)
				.append(',');

		this.collateralLinkFields = collateralLinkFieldsBuilder.toString();
		this.collateralLinkValues = collateralLinkValuesBuilder.toString();
		this.arrangementStep2Fields = arrStep2FieldsBuilder.toString();
		this.arrangementStep2Values = arrStep2ValuesBuilder.toString();
	}

	public void setSettlementAccount(final String settlementType, final String settlementAccount) {
		this.settlementAccount = settlementAccount;

		if ("Banking".equals(settlementType)) {
			arrStep2FieldsBuilder.append("Settlement$Advanced - Pay In#PAYIN.SETTLEMENT:1,")
					.append("Settlement$Advanced - Pay In#PAYIN.ACCOUNT:1:1,")
					.append("Settlement$Advanced - Pay In#PAYIN.ACTIVITY:1:1,")
					.append("Settlement$Advanced - Pay In#PAYIN.BENEFICIARY:1:1,")
					.append("Settlement$Advanced - Pay In#PAYIN.PO.PRODUCT:1:1,")
					.append("Settlement$Advanced - Pay In#PAYIN.SETTLE.ACTIVITY:1,");

			arrStep2ValuesBuilder.append("Yes,").append(settlementAccount).append(",")
					.append("ACCOUNTS-DEBIT-ARRANGEMENT,").append(" ,").append(" ,").append(" ,");
		} else if ("Beneficiary".equals(settlementType)) {
			arrStep2FieldsBuilder.append("Settlement$Advanced - Pay In#PAYIN.SETTLEMENT:1,")
					.append("Settlement$Advanced - Pay In#PAYIN.ACCOUNT:1:1,")
					.append("Settlement$Advanced - Pay In#PAYIN.ACTIVITY:1:1,")
					.append("Settlement$Advanced - Pay In#PAYIN.BENEFICIARY:1:1,")
					.append("Settlement$Advanced - Pay In#PAYIN.PO.PRODUCT:1:1,")
					.append("Settlement$Advanced - Pay In#PAYIN.SETTLE.ACTIVITY:1,");

			arrStep2ValuesBuilder.append("Yes,").append(" ,").append(" ,").append(settlementAccount).append(',')
					.append("ACHCREDIT,").append("LENDING-APPLYPAYMENT-PR.REPAYMENT,");
		}

		this.arrangementStep2Fields = arrStep2FieldsBuilder.toString();
		this.arrangementStep2Values = arrStep2ValuesBuilder.toString();
	}

	public void setReserveAccount(final String reserveAccount, final boolean primaryReserve) {
		this.reserveAccount = reserveAccount;

		final String primaryFlag = primaryReserve ? "Yes" : "No";

		arrStep2FieldsBuilder.append("Account$Reserve Account & Other Details#CA.RSRV.ACCOUNT:1,")
				.append("Account$Reserve Account & Other Details#CA.RSRV.PRIMARY:1,");
		arrStep2ValuesBuilder.append(reserveAccount).append(',').append(primaryFlag).append(',');

		this.arrangementStep2Fields = arrStep2FieldsBuilder.toString();
		this.arrangementStep2Values = arrStep2ValuesBuilder.toString();
	}

	public void setEscrowPayee(final String escrowPayee) {
		this.escrowPayee = escrowPayee;
	}

	public void setAgent(final String dealer, final String commissionPlanDealer, final String advisor,
			final String commissionPlanAdvisor) {
		this.dealer = dealer;
		this.advisor = advisor;
		this.commissionPlanDealer = commissionPlanDealer;
		this.commissionPlanAdvisor = commissionPlanAdvisor;

		arrStep1FieldsBuilder.append("AGENT.ID:1,").append("AGENT.ARR.ID:1,").append("AGENT.ROLE:1,")
				.append("AGENT.ID+:2,").append("AGENT.ARR.ID:2,").append("AGENT.ROLE:2,");
		arrStep1ValuesBuilder.append(dealer).append(',').append(commissionPlanDealer).append(',').append("Agency")
				.append(',').append(advisor).append(',').append(commissionPlanAdvisor).append(',').append("Agent")
				.append(',');

		if (hokIndicator) {
			for (final ArrangementData currentHOKArrangement : hokArrangements) {
				currentHOKArrangement.setAgent(dealer, commissionPlanDealer, advisor, commissionPlanAdvisor);
			}
		}

		this.arrangementStep1Fields = arrStep1FieldsBuilder.toString();
		this.arrangementStep1Values = arrStep1ValuesBuilder.toString();
	}

	public void setStartDate(final String startDate) {
		this.startDate = startDate;

		arrStep2FieldsBuilder.append("Schedule$START.DATE:1:1,");
		arrStep2ValuesBuilder.append(startDate).append(',');

		if (hokIndicator) {
			for (final ArrangementData currentHOKArrangement : hokArrangements) {
				currentHOKArrangement.setStartDate(startDate);
			}
		}

		if (productGroup.equals(RepositoryPaths.PERSONAL_LOANS)) {
			arrStep2FieldsBuilder.append("Schedule$START.DATE:2:1,");
			arrStep2ValuesBuilder.append(startDate).append(',');
		}

		this.arrangementStep2Fields = arrStep2FieldsBuilder.toString();
		this.arrangementStep2Values = arrStep2ValuesBuilder.toString();
	}

	public void setStatement(final String statementID, final String statementType) {
		this.statementID = statementID;
		this.statementType = statementType;
	}

	public ArrangementData withCustomers(final String customers, final CustomerData mainCustomerData,
			final String customersRoles, final String taxLiabilities, final String limitAllocations) {
		final String percentageFieldsTab = RepositoryPaths.RETAIL_MORTGAGES.equals(productGroup)
				&& !"Bridge Loan".equals(product) ? "Customer#" : "";
		if (customers.contains("NEW")) {
			this.owners = customers.split(",").length;
			this.createCustomer = true;
		} else {
			this.owners = customers.split(",").length;
			this.customers = customers;
			this.createCustomer = false;

			setCustomers(customers, mainCustomerData);

		}

		for (int i = 0; i < owners; i++) {
			if (!"".equals(taxLiabilities) && taxLiabilities.split(",").length > i) {
				arrStep2FieldsBuilder.append("Customer$").append(percentageFieldsTab).append("TAX.LIABILITY.PERC:")
						.append(i + 1).append(',');
				arrStep2ValuesBuilder.append(taxLiabilities.split(",")[i]).append(',');
			}

			if (!"".equals(limitAllocations) && limitAllocations.split(",").length > i) {
				arrStep2FieldsBuilder.append("Customer$").append(percentageFieldsTab).append("LIMIT.ALLOC.PERC:")
						.append(i + 1).append(',');
				arrStep2ValuesBuilder.append(limitAllocations.split(",")[i]).append(',');
			}

			if (i > 0 && !product.contains(RepositoryPaths.HELOC)) {

				arrStep2FieldsBuilder.append("Customer$Insurance#LIFE.INS.FLAG:").append(i + 1).append(',')
						.append("Customer$Insurance#DISABILITY.FLAG:").append(i + 1).append(',');
				arrStep2ValuesBuilder.append("None,").append("None,");

			}
		}
		return this;
	}

	public ArrangementData withAgent(final String dealer, final String commissionPlanDealer, final String advisor,
			final String commissionPlanAdvisor) {
		if ("NEW".equals(dealer)) {
			this.createAgent = true;
			this.commissionPlanDealer = commissionPlanDealer;
			this.commissionPlanAdvisor = commissionPlanAdvisor;
		} else {
			this.createAgent = false;
			this.dealer = dealer;
			this.commissionPlanDealer = commissionPlanDealer;
			this.advisor = advisor;
			this.commissionPlanAdvisor = commissionPlanAdvisor;

			setAgent(dealer, commissionPlanDealer, advisor, commissionPlanAdvisor);
		}

		return this;
	}

	public ArrangementData withRoleBasedPage(final String roleBasedPage) {
		this.roleBasedPage = roleBasedPage;
		return this;
	}

	public ArrangementData withEffectiveDate(final String effectiveDate) {
		this.effectiveDate = effectiveDate;

		limitFieldsBuilder.append("PROPOSAL.DATE,").append("APPROVAL.DATE,");
		limitValuesBuilder.append(effectiveDate).append(',').append(effectiveDate).append(',');

		collateralFieldsBuilder.append("Collateral#VALUE.DATE,");
		collateralValuesBuilder.append(effectiveDate).append(',');

		arrStep1FieldsBuilder.append("EFFECTIVE.DATE,");
		arrStep1ValuesBuilder.append(effectiveDate).append(',');

		return this;
	}

	public ArrangementData withLimit(final String limitReference, final String limitSerial, final String singleLimit) {
		if ("NEW".equals(limitSerial)) {
			this.limitSerial = limitSerial;
			this.createLimit = true;

			if ("".equals(limitReference)) {
				if (limitType.contains("Non-Revolving")) {
					this.limitReference = DefaultVariables.nonrevolvingLimitRefs.get(product);
				} else {
					this.limitReference = DefaultVariables.revolvingLimitRefs.get(product);
				}
				if ("".equals(this.limitReference)) {
					this.limitReference = "2300";
				}
			} else {
				this.limitReference = limitReference;
			}

		} else {
			this.createLimit = false;
			this.createCollateral = false;

			setLimit(limitReference, limitSerial, singleLimit);
		}

		return this;
	}

	public ArrangementData withSettlement(final String settlementType, final String settlementAccount) {
		this.settlementAccount = settlementAccount;

		if ("NEW".equals(settlementAccount)) {
			if ("Banking".equals(settlementType)) {
				createBanking = true;
			} else if ("Beneficiary".equals(settlementType)) {
				createBeneficiary = true;
			}
		} else {
			setSettlementAccount(settlementType, settlementAccount);
		}

		return this;
	}

	public ArrangementData withTerm(final String term) {
		this.term = term;

		limitFieldsBuilder.append("EXPIRY.DATE,");
		limitValuesBuilder.append(term).append(',');

		arrStep2FieldsBuilder.append("TERM,");
		arrStep2ValuesBuilder.append(term).append(',');

		return this;
	}

	public ArrangementData withCurrency(final String currency) {
		this.currency = currency;

		arrStep1FieldsBuilder.append("CURRENCY,");
		arrStep1FieldsBuilder.append(currency).append(',');

		return this;
	}

	public ArrangementData withCommitmentAmount(final String commitmentAmount) {
		this.commitmentAmount = commitmentAmount;

		arrStep2FieldsBuilder.append("AMOUNT,");
		arrStep2ValuesBuilder.append(commitmentAmount).append(',');

		if (hokIndicator) {
			limitFieldsBuilder.append("INTERNAL.AMOUNT,").append("MAXIMUM.TOTAL,").append("MAXIMUM.SECURED:1,");
			limitValuesBuilder.append(collateralValue).append(',').append(collateralValue).append(',')
					.append(collateralValue).append(',');
		}

		return this;
	}

	public ArrangementData withCollateralValue(final String collateralValue) {
		this.collateralValue = collateralValue;

		if (hokIndicator) {
			hokParentLimitFieldsBuilder.append("INTERNAL.AMOUNT,").append("MAXIMUM.TOTAL,")
					.append("MAXIMUM.SECURED:1,");
			hokParentLimitValuesBuilder.append(collateralValue).append(',').append(collateralValue).append(',')
					.append(collateralValue).append(',');
		} else {
			limitFieldsBuilder.append("INTERNAL.AMOUNT,").append("MAXIMUM.TOTAL,").append("MAXIMUM.SECURED:1,");
			limitValuesBuilder.append(collateralValue).append(',').append(collateralValue).append(',')
					.append(collateralValue).append(',');
		}

		collateralFieldsBuilder.append("NOMINAL.VALUE,").append("EXECUTION.VALUE,")
				.append("Collateral details#COLL.VALUE,");
		collateralValuesBuilder.append(collateralValue).append(',').append(collateralValue).append(',')
				.append(collateralValue).append(',');

		return this;
	}

	public ArrangementData withCollateralSpecs(final String collateralCode, final String collateralType,
			final String collateralOccupancy, final String collateralNumberOfApts) {

		if (!"".equals(collateralCode)) {
			this.collateralCode = collateralCode;
			collateralFieldsBuilder.append("Collateral#COLLATERAL.CODE,");
			collateralValuesBuilder.append(collateralCode).append(',');
		}

		if (!"".equals(collateralType)) {
			this.collateralType = collateralType;
			collateralFieldsBuilder.append("Collateral#COLLATERAL.TYPE,");
			collateralValuesBuilder.append(collateralType).append(',');
		}

		if (!"".equals(collateralOccupancy)) {
			this.collateralOccupancy = collateralOccupancy;
			collateralFieldsBuilder.append("Collateral details#L.COLL.OCCUP,");
			collateralValuesBuilder.append(collateralOccupancy).append(',');
		}

		if (!"".equals(collateralNumberOfApts)) {
			this.collateralNumberOfApts = collateralNumberOfApts;
			collateralFieldsBuilder.append("Collateral details#L.COLL.BLD.UNTS,");
			collateralValuesBuilder.append(collateralNumberOfApts).append(',');
		}

		return this;
	}

	public ArrangementData withMarginSpread(final String marginSpread, final String postedRate) {

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
			marginRate = "";
			marginOperator = "Add";
		}

		arrStep2FieldsBuilder.append("Principal Interest$Periodic#MARGIN.OPER:1:1,")
				.append("Principal Interest$Periodic#MARGIN.RATE:1:1,")
				.append("Principal Interest$MOS Interface#L.POSTED.RATE,");
		arrStep2ValuesBuilder.append(marginOperator).append(',').append(marginRate).append(',').append(postedRate)
				.append(',');

		return this;
	}

	public ArrangementData withInsurance(final String insuranceCompany, final String insuranceNumber,
			final String insuranceLife, final String insuranceLifePremium, final String insuranceDisab,
			final String insuranceDisabPremium) {
		this.insuranceLife = insuranceLife;
		this.insuranceDisab = insuranceDisab;

		if (!"None".equals(insuranceLife)) {
			arrStep2FieldsBuilder.append("Customer$Insurance#LIFE.INS.AMT:1,");
			arrStep2ValuesBuilder.append(insuranceLifePremium).append(',');
		}
		if (!"None".equals(insuranceDisab)) {
			arrStep2FieldsBuilder.append("Customer$Insurance#DISABILITY.AMT:1,");
			arrStep2ValuesBuilder.append(insuranceDisabPremium).append(',');
		}

		arrStep2FieldsBuilder.append("Account$Main#L.INS.COMP.NAME,").append("Account$Main#CA.CMHC.NO,")
				.append("Customer$Insurance#LIFE.INS.FLAG:1,").append("Customer$Insurance#DISABILITY.FLAG:1,");
		arrStep2ValuesBuilder.append(insuranceCompany).append(',').append(insuranceNumber).append(',')
				.append(insuranceLife).append(',').append(insuranceDisab).append(',');

		if (owners > 1 && !product.contains(RepositoryPaths.HELOC)) {
			for (int i = 2; i <= owners; i++) {
				arrStep2FieldsBuilder.append("Customer$Insurance#LIFE.INS.FLAG:").append(i).append(',')
						.append("Customer$Insurance#DISABILITY.FLAG:").append(i).append(',');
				arrStep2ValuesBuilder.append("None,").append("None,");
			}
		}

		return this;
	}

	public ArrangementData withRenewalPeriod(final String renewalPeriod) {

		arrStep2FieldsBuilder.append("CHANGE.DATE.TYPE,").append("CHANGE.PERIOD,");
		arrStep2ValuesBuilder.append("Period").append(',').append(renewalPeriod).append(',');

		return this;
	}

	public ArrangementData withPaymentFrequency(final String paymentFrequency) {
		this.paymentFrequency = paymentFrequency;

		arrStep2FieldsBuilder.append("Schedule$PAYMENT.FREQ:1,");
		arrStep2ValuesBuilder.append(paymentFrequency).append(',');

		if (RepositoryPaths.HELOC.equals(product) || RepositoryPaths.PERSONAL_LOANS.equals(productGroup)) {
			arrStep2FieldsBuilder.append("Schedule$PAYMENT.FREQ:2,");
			arrStep2ValuesBuilder.append(paymentFrequency).append(',');
		}

		return this;
	}

	public ArrangementData withPaymentType(final String paymentType) {
		String extraFields;
		String extraValues;

		if (RepositoryPaths.COMMERCIAL_LOANS.equals(productGroup)) {
			switch (paymentType) {
			case "BLENDED":
				extraFields = "PAYMENT.TYPE:1," + "PAYMENT.METHOD:1," + "PROPERTY:1:1," + "BILL.TYPE:1,"
						+ "ACTUAL.AMT:1:1," + "PROPERTY:1:2," + "PAYMENT.TYPE<:2,";
				extraValues = "BLENDED," + "Due," + "ACCOUNT," + "INSTALLMENT," + "," + "PRINCIPALINT," + " ,";
				break;
			case "LINEAR":
				extraFields = "PAYMENT.TYPE:1," + "PAYMENT.METHOD:1," + "PROPERTY:1:1," + "BILL.TYPE:1,"
						+ "ACTUAL.AMT:1:1," + "PROPERTY:1<:2," + "PAYMENT.TYPE<:2," + "PAYMENT.TYPE+:2,"
						+ "PAYMENT.METHOD:2," + "PROPERTY:2:1," + "BILL.TYPE:2," + "ACTUAL.AMT:2:1,"
						+ "PAYMENT.FREQ:2,";
				extraValues = "LINEAR," + "Due," + "ACCOUNT," + "INSTALLMENT," + " ," + " ," + " ," + "INTEREST.ONLY,"
						+ "Due," + "PRINCIPALINT," + "INSTALLMENT," + " ," + "e0Y e1M e0W e0D e0F,";
				break;
			case "INTEREST.ONLY":
				extraFields = "PAYMENT.TYPE:1," + "PAYMENT.METHOD:1," + "PROPERTY:1:1," + "BILL.TYPE:1,"
						+ "ACTUAL.AMT:1:1," + "PROPERTY:1<:2," + "PAYMENT.TYPE<:2,";
				extraValues = "INTEREST.ONLY," + "Due," + "PRINCIPALINT," + "INSTALLMENT," + " ," + " ," + " ,";
				break;
			case "PRINCIPAL":
				extraFields = "PAYMENT.TYPE:1," + "PAYMENT.METHOD:1," + "PROPERTY:1:1," + "BILL.TYPE:1,"
						+ "ACTUAL.AMT:1:1," + "PROPERTY:1<:2," + "PAYMENT.TYPE<:2,";
				extraValues = "INTEREST," + "Capitalise," + "PRINCIPALINT," + "INSTALLMENT," + " ," + " ," + " ,";
				break;
			default:
				extraFields = "";
				extraValues = "";
				break;
			}

			arrStep2FieldsBuilder.append(extraFields);
			arrStep2ValuesBuilder.append(extraValues);
		}

		return this;
	}

	public ArrangementData withGdsTds(final String gds, final String tds) {

		arrStep2FieldsBuilder.append("Account$Main#GDS,").append("Account$Main#TDS,");
		arrStep2ValuesBuilder.append(gds).append(',').append(tds).append(',');

		return this;
	}

	public ArrangementData withFundingSource(final String fundingSource, final String ownerType, final String branch,
			final String beneficiary) {
		String payInAccount;

		if (productGroup.contains("Deposits")) {
			if ("Cheque".equals(fundingSource) && "B2B Branch 607".equals(branch)) {
				payInAccount = "CAD1100100017607";
			} else if ("Cheque".equals(fundingSource)) {
				payInAccount = "CAD1100100011247";
			} else if ("RBC Account".equals(fundingSource) && "B2B Branch 607".equals(branch)) {
				payInAccount = "CAD1160000017607";
			} else if ("RBC Account".equals(fundingSource)) {
				payInAccount = "CAD1160000011247";
			} else if ("TD Account".equals(fundingSource) && "B2B Branch 607".equals(branch)) {
				payInAccount = "CAD1160100017607";
			} else if ("TD Account".equals(fundingSource)) {
				payInAccount = "CAD1160100011247";
			} else if ("Nominee".equals(ownerType) && "VMBL".equals(fundingSource) && "B2B Branch 607".equals(branch)) {
				payInAccount = "CAD1120000017607";
			} else if ("Nominee".equals(ownerType) && "VMBL".equals(fundingSource)) {
				payInAccount = "CAD1120000011247";
			} else if ("Nominee".equals(ownerType) && "Cannex".equals(fundingSource)) {
				payInAccount = "CAD11602";
			} else {
				payInAccount = "CAD11102";
			}

			arrStep2FieldsBuilder.append("Settlement$Advanced - Pay In#PAYIN.SETTLEMENT:1,")
					.append("Settlement$Advanced - Pay In#PAYIN.ACCOUNT:1:1,");
			arrStep2ValuesBuilder.append("Yes,").append(payInAccount).append(',');

			if (productGroup.contains("Retail")) {
				arrStep2FieldsBuilder.append("Settlement$Advanced - Pay Out#PAYOUT.SETTLEMENT:1,")
						.append("Settlement$Advanced - Pay Out#PAYOUT.ACCOUNT:1:1,");
				arrStep2ValuesBuilder.append("Yes,").append("CAD1100000011539,");

			} else if ("Nominee".equals(ownerType) && "VMBL".equals(fundingSource)) {
				arrStep2FieldsBuilder.append("Settlement$Advanced - Pay Out#PAYOUT.SETTLEMENT:1,")
						.append("Settlement$Advanced - Pay Out#PAYOUT.ACCOUNT:1:1,");
				arrStep2ValuesBuilder.append("Yes,").append(payInAccount).append(',');

			} else {
				arrStep2FieldsBuilder.append("Settlement$Advanced - Pay Out#PAYOUT.SETTLEMENT:1,")
						.append("Settlement$Advanced - Pay Out#PAYOUT.PO.PRODUCT:1:1,")
						.append("Settlement$Advanced - Pay Out#PAYOUT.BENEFICIARY:1:1,")
						.append("Settlement$Advanced - Pay Out#PAYOUT.SETTLEMENT:2,")
						.append("Settlement$Advanced - Pay Out#PAYOUT.PO.PRODUCT:2:1,")
						.append("Settlement$Advanced - Pay Out#PAYOUT.BENEFICIARY:2:1,");
				arrStep2ValuesBuilder.append("Yes,").append("ACHDEBIT,").append(beneficiary).append(',').append("Yes,")
						.append("ACHDEBIT,").append(beneficiary).append(',');

			}
		}

		return this;
	}

	public ArrangementData withMaturityInstructions(final String instructionType) {

		if (productGroup.contains("Deposits")) {
			if (instructionType.equals("Renew Both")) {
				arrStep2FieldsBuilder.append("Schedule$PAYMENT.METHOD:1,").append("Schedule$START.DATE:1:1,");
				arrStep2ValuesBuilder.append("Capitalise,").append("R_RENEWAL +,");
			} else if (instructionType.equals("Payout Both")) {
				arrStep2FieldsBuilder.append("Schedule$PAYMENT.METHOD:1,").append("Schedule$START.DATE:1:1,");
				arrStep2ValuesBuilder.append("Pay,").append("R_MATURITY +,");
			} else if (instructionType.equals("Interest Only")) {
				arrStep2FieldsBuilder.append("Schedule$PAYMENT.METHOD:1,").append("Schedule$START.DATE:1:1,");
				arrStep2ValuesBuilder.append("Pay,").append("R_RENEWAL +,");
			}
		}
		return this;
	}

	public ArrangementData withReason(final String reason) {

		arrStep1FieldsBuilder.append("REASON,");
		arrStep1ValuesBuilder.append(reason).append(",");

		return this;
	}

	public ArrangementData withEscrow(final String escrowPayee) {
		this.createEscrowAccount = true;

		if ("NEW".equals(escrowPayee)) {
			this.createEscrowPayee = true;
		} else if (RepositoryPaths.MUNICIPALITY.equals(escrowPayee)) {
			this.findMunicipalityPayee = true;
		} else {
			setEscrowPayee(escrowPayee);
		}

		arrStep2FieldsBuilder.append("Schedule$PAYMENT.TYPE+:2,").append("Schedule$PAYMENT.METHOD:2,")
				.append("Schedule$PAYMENT.FREQ:2,").append("Schedule$PROPERTY:2:1,").append("Schedule$BILL.TYPE:2,")
				.append("Schedule$PAYMENT.TYPE+:3,").append("Schedule$PAYMENT.METHOD:3,")
				.append("Schedule$PAYMENT.FREQ:3,").append("Schedule$PROPERTY:3:1,").append("Schedule$BILL.TYPE:3,")
				.append("Schedule$PAYMENT.TYPE+:4,").append("Schedule$PAYMENT.METHOD:4,")
				.append("Schedule$PAYMENT.FREQ:4,").append("Schedule$PROPERTY:4:1,").append("Schedule$BILL.TYPE:4,");

		arrStep2ValuesBuilder.append("ESCROW,").append("Due,").append(paymentFrequency).append(',').append("ESCROW,")
				.append("INSTALLMENT,").append("ESCROWINT,").append("Capitalise,").append("e0Y e0M e0W e0D eLMNTHF,")
				.append("ESCROWDRINT,").append("INSTALLMENT,").append("ESCROWINT,").append("Capitalise,")
				.append("e0Y e0M e0W e0D eLMNTHF,").append("ESCROWCRINT,").append("INSTALLMENT,");

		return this;
	}

	public ArrangementData withReserveAccount(final String reserveAccount, final boolean primaryReserve) {
		if ("NEW".equals(reserveAccount)) {
			this.createReserveAccount = true;
		} else {
			this.createReserveAccount = false;
			setReserveAccount(reserveAccount, primaryReserve);
		}

		arrStep2FieldsBuilder.append("Schedule$PAYMENT.TYPE+:2,").append("Schedule$PAYMENT.METHOD:2,")
				.append("Schedule$PAYMENT.FREQ:2,").append("Schedule$PROPERTY:2:1,").append("Schedule$ACTUAL.AMT:2:1,")
				.append("Schedule$BILL.TYPE:2,");
		arrStep2ValuesBuilder.append("RESERVEACCT,").append("Due,").append(paymentFrequency).append(',')
				.append("RESERVEACCT,").append("500,").append("INSTALLMENT,");

		return this;
	}

	public ArrangementData withSyndication(final String slParticipationType, final String syndicationAgreement) {

		arrStep2FieldsBuilder.append("Account$Syndication Details#L.PARTICIP.TYPE,");
		arrStep2ValuesBuilder.append(slParticipationType).append(',');

		if (slParticipationType.contains("Ext")) {
			arrStep2FieldsBuilder.append("Customer$DELIVERY.REQD:1,").append("Customer$Statement#STATEMENT.REQD:1,")
					.append("Account$Main#L.NON.NOTIFY.FL,");
			arrStep2ValuesBuilder.append("NO,").append("NO,").append("on,");
		}

		if (!"".equals(syndicationAgreement)) {
			arrStep2FieldsBuilder.append("Account$Syndication Details#SYN.LOAN,")
					.append("Account$Syndication Details#SYNDICATION.ID,");
			arrStep2ValuesBuilder.append("YES,").append(syndicationAgreement).append(',');
		}

		return this;
	}

	public ArrangementData withHOK() {
		this.hokIndicator = true;
		this.hokArrangements = new ArrayList<ArrangementData>();
		this.limitReference = "2101";

		hokParentLimitFieldsBuilder.append(limitFieldsBuilder.toString());
		hokParentLimitValuesBuilder.append(limitValuesBuilder.toString());

		arrStep2FieldsBuilder.append("Account$MOS Interface#L.HOK.IND,");
		arrStep2ValuesBuilder.append("YES,");

		return this;
	}

	public ArrangementData withHOKProduct(final ArrangementData hokArrangement) {
		String totalCollateralValue = Integer
				.toString((Integer.parseInt(collateralValue) + Integer.parseInt(hokArrangement.collateralValue)));

		hokParentLimitFieldsBuilder.append("INTERNAL.AMOUNT,").append("MAXIMUM.TOTAL,").append("MAXIMUM.SECURED:1,");
		hokParentLimitValuesBuilder.append(totalCollateralValue).append(',').append(totalCollateralValue).append(',')
				.append(totalCollateralValue).append(',');

		collateralFieldsBuilder.append("Collateral#NOMINAL.VALUE,").append("EXECUTION.VALUE,")
				.append("Collateral details#COLL.VALUE,");
		collateralValuesBuilder.append(totalCollateralValue).append(',').append(totalCollateralValue).append(',')
				.append(totalCollateralValue).append(',');

		hokArrangement.arrStep2FieldsBuilder.append("Account$MOS Interface#L.HOK.IND,");
		hokArrangement.arrStep2ValuesBuilder.append("YES,");

		if (hokArrangement.product.contains("HELOC")) {
			hokArrangement.limitReference = "2103";
		} else {
			hokArrangement.limitReference = "2101";
		}

		this.hokArrangements.add(hokArrangement);
		return this;
	}

	public ArrangementData withDisbursement() {
		this.disburseArrangement = true;
		this.disburseAmount = commitmentAmount;

		return this;
	}

	public ArrangementData withDisbursement(final String disburseAmount) {
		this.disburseArrangement = true;
		this.disburseAmount = disburseAmount;

		return this;
	}

	public ArrangementData withFunding() {
		this.fundArrangement = true;
		this.fundAmount = commitmentAmount;

		return this;
	}

	public ArrangementData withFunding(final String fundAmount) {
		this.fundArrangement = true;
		this.fundAmount = fundAmount;

		return this;
	}

	public ArrangementData withRepayments() {
		this.disburseArrangement = true;

		if (settlementAccount.isEmpty()) {
			this.withSettlement("Beneficiary", "NEW");
		}

		arrStep2FieldsBuilder.append("Settlement$Advanced - Pay In#PAYIN.AC.DB.RULE:1,");
		arrStep2ValuesBuilder.append("Full,");

		return this;
	}

	public ArrangementData withStatement(final String statementID, final String statementType) {
		if ("NEW".equals(statementID)) {
			this.createStatement = true;
			this.statementType = statementType;
		} else {
			setStatement(statementID, statementType);
		}
		return this;
	}

	public ArrangementData withMarginCallDetails(final String marginCall, final String arrangementType) {
		arrStep2FieldsBuilder.append("L.MRG.CALL.IND,").append("L.INV.LOAN.TYPE,");
		arrStep2ValuesBuilder.append(marginCall).append(',').append(arrangementType).append(',');
		return this;
	}

	public ArrangementData build() {
		arrangementStep1Fields = arrStep1FieldsBuilder.toString();
		arrangementStep1Values = arrStep1ValuesBuilder.toString();
		arrangementStep2Fields = arrStep2FieldsBuilder.toString();
		arrangementStep2Values = arrStep2ValuesBuilder.toString();
		limitFields = limitFieldsBuilder.toString();
		limitValues = limitValuesBuilder.toString();
		collateralLinkFields = collateralLinkFieldsBuilder.toString();
		collateralLinkValues = collateralLinkValuesBuilder.toString();
		collateralFields = collateralFieldsBuilder.toString();
		collateralValues = collateralValuesBuilder.toString();
		hokParentLimitFields = hokParentLimitFieldsBuilder.toString();
		hokParentLimitValues = hokParentLimitValuesBuilder.toString();

		return this;
	}
}
