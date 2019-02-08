package testcases.PROC;

import java.util.Arrays;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.DefaultVariables;
import testLibs.RepositoryPaths;

public class PROC_SAE_XX015 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "2018-06-12";
	private String customer1;
	private String customer2;
	private String customer3;
	private String lenderLead;
	private String lenderA;
	private String lenderB;
	private String vostroAccountLead;
	private String vostroAccountA;
	private String vostroAccountB;
	private String agreement2LenderA;
	private String agreement2Master;
	private String transit;
	private String syndicatedLoan1;
	private String syndicatedLoan2A;
	private String syndicatedLoan2Master;
	private String syndicatedLoan3A;
	private String syndicatedLoan3B;
	private String syndicatedLoan3Master;
	private String agreement3LenderA;
	private String agreement3LenderB;
	private String agreement3Master;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "productGroup", "product", "customerType" })
	public void preCondition(@Optional("135 SYNDIC.ANDPARTICIP") final String branch, final String productGroup,
			final String product, final String customerType) {

		stepDescription = "Create customers and Vostro Accounts for syndicated loans";
		stepExpected = "Customers and Vostro Accounts created successfully";

		if (loginResult) {

			switchToBranch(branch);
			transit = branch.substring(0, 3);

			customer1 = createDefaultCustomer(customerType, productGroup, ROLEBASED_SAE);
			customer2 = createDefaultCustomer(customerType, productGroup, ROLEBASED_SAE);
			customer3 = createDefaultCustomer(customerType, productGroup, ROLEBASED_SAE);
			lenderLead = createDefaultCustomer(NON_CLIENT_BUSINESS, productGroup, ROLEBASED_SAE);
			lenderA = createDefaultCustomer(NON_CLIENT_BUSINESS, productGroup, ROLEBASED_SAE);
			lenderB = createDefaultCustomer(NON_CLIENT_BUSINESS, productGroup, ROLEBASED_SAE);

			if (customer1 == null || customer2 == null || customer3 == null || lenderLead == null || lenderA == null
					|| lenderB == null || customer1.contains(ERROR) || customer2.contains(ERROR)
					|| customer3.contains(ERROR) || lenderLead.contains(ERROR) || lenderA.contains(ERROR)
					|| lenderB.contains(ERROR)) {
				stepActual = "Error while creating client";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {

				vostroAccountLead = createDefaultArrangement(SERVICING_ACCOUNTS, "Vostro Account", ROLEBASED_SAE,
						lenderLead, "+0d");
				vostroAccountA = createDefaultArrangement(SERVICING_ACCOUNTS, "Vostro Account", ROLEBASED_SAE, lenderA,
						"+0d");
				vostroAccountB = createDefaultArrangement(SERVICING_ACCOUNTS, "Vostro Account", ROLEBASED_SAE, lenderB,
						"+0d");

			}

			if (vostroAccountLead == null || vostroAccountA == null || vostroAccountB == null
					|| vostroAccountLead.contains(ERROR) || vostroAccountA.contains(ERROR)
					|| vostroAccountB.contains(ERROR)) {
				stepActual = "Error while creating vostro account";
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
	@Parameters({ "branch", "productGroup", "product", "customerType" })
	public void syndicatedLoan(@Optional("135 SYNDIC.ANDPARTICIP") final String branch, final String productGroup,
			final String product, final String customerType) {

		stepDescription = "Create Syndicated loan with LBC as Participant";
		stepExpected = "Syndicated loan with LBC as Participant created successfully";

		String parentLimit1;
		String subLimit1;
		String subLimit1Reference;
		ArrangementData loanData1;

		if (customer1 == null || lenderLead == null || customer1.contains(ERROR) || lenderLead.contains(ERROR)) {

			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);

		} else {

			parentLimit1 = customerLimit(CREATE, RepositoryPaths.SECURED, ROLEBASED_SAE, product, customer1, "2400",
					"02", DefaultVariables.securedSAELimitFields, DefaultVariables.securedSAELimitValues);

			subLimit1 = customerLimit(CREATE_AUTHORISE, RepositoryPaths.SECURED, ROLEBASED_SAE, product, customer1, "",
					"02", DefaultVariables.securedSAELimitFields, DefaultVariables.securedSAELimitValues);

			subLimit1Reference = subLimit1.substring(13, 17);

			loanData1 = new ArrangementData("syndicatedLoan1", productGroup, product)
					.withCustomers(customer1 + "," + lenderLead, createdCustomers.get(customer1),
							"BORROWER.OWNER,SERVICER", "100,0", "100,0")
					.withLimit(subLimit1Reference, "02", "N").withReserveAccount("NEW", true)
					.withSyndication("Ext.participat.", "").build();

			syndicatedLoan1 = createDefaultArrangement(loanData1);

			if (syndicatedLoan1 == null || syndicatedLoan1.contains(ERROR)) {
				stepActual = "Error while creating Syndicated loan for LBC as Participant";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

		}
		softVerify.assertAll();

	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "branch", "productGroup", "product", "customerType" })
	public void syndicatedLoansSameData(@Optional("135 SYNDIC.ANDPARTICIP") final String branch,
			final String productGroup, final String product, final String customerType) {

		stepDescription = "Create Syndicated Loan with LBC as the Lead Bank and a single participant";
		stepExpected = "Syndicated loan with LBC as the Lead Bank and a single participant created successfully";

		String parentLimit2;
		String subLimit2;
		String limitFields;
		String limitValues;
		String slAgreementFields;
		String slAgreementValues;
		String subLimit2Reference;
		ArrangementData loanData2A;
		ArrangementData loanData2Master;

		if (customer2 == null || lenderA == null || vostroAccountA == null || customer2.contains(ERROR)
				|| lenderA.contains(ERROR) || vostroAccountA.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);

		} else {

			limitFields = DefaultVariables.securedSAELimitFields + "L.SYNDICAT.LIM,L.INVESTOR.ID:1,L.COMMITTED.AMT:1,";
			limitValues = DefaultVariables.securedSAELimitValues + "Yes," + lenderA + "," + "250T,";

			parentLimit2 = customerLimit(CREATE, RepositoryPaths.SECURED, ROLEBASED_SAE, product, customer2, "2400",
					"02", limitFields, limitValues);

			subLimit2 = customerLimit(CREATE_AUTHORISE, RepositoryPaths.SECURED, ROLEBASED_SAE, product, customer2, "",
					"02", limitFields, limitValues);

			slAgreementFields = "LOAN.CCY,CUSTOMER.ID,LOAN.AMT,SYN.ST.DATE,SYN.MAT.DATE,PART.ID:1,PART.AMT:1,PART.PERC:1,PART.VOSTRO.AC:1,L.MASTER.DETS,L.SERVICE.FEE,";
			slAgreementValues = "CAD," + customer2 + ",250T,+0d,+5y," + lenderA + ",250T,100," + vostroAccountA
					+ ",Yes,LBCSLFEE1,";

			agreement2LenderA = syndicationAgreement(CREATE, "", slAgreementFields, slAgreementValues);

			slAgreementFields = "LOAN.CCY,CUSTOMER.ID,LOAN.AMT,SYN.ST.DATE,SYN.MAT.DATE,OWN.AMT,OWN.PERC,PART.ID:1,PART.AMT:1,PART.PERC:1,PART.VOSTRO.AC:1,L.MASTER.DETS,L.SERVICE.FEE,";
			slAgreementValues = "CAD," + customer2 + ",500T,+0d,+5y,250T,50," + lenderA + ",250T,50," + vostroAccountA
					+ ",Yes,LBCSLFEE1,";

			agreement2Master = syndicationAgreement(CREATE, "", slAgreementFields, slAgreementValues);

			subLimit2Reference = subLimit2.substring(13, 17);

			loanData2A = new ArrangementData("syndicatedLoan2A", productGroup, product)
					.withCustomers(customer2, createdCustomers.get(customer2), "BORROWER.OWNER", "100,", "100,")
					.withLimit(subLimit2Reference, "02", "N").withCommitmentAmount("250T")
					.withReserveAccount("NEW", true).withSyndication("Lbc Participat.", "").build();

			syndicatedLoan2A = createDefaultArrangement(loanData2A);

			loanData2Master = new ArrangementData("syndicatedLoan2Master", productGroup, product)
					.withCustomers(customer2, createdCustomers.get(customer2), "BORROWER.OWNER", "100,", "100,")
					.withLimit(subLimit2Reference, "02", "N").withCommitmentAmount("250T")
					.withSyndication("Lbc Participat.", "").build();

			syndicatedLoan2Master = createDefaultArrangement(loanData2Master);

			if (syndicatedLoan2A == null || syndicatedLoan2Master == null || syndicatedLoan2A.contains(ERROR)
					|| syndicatedLoan2Master.contains(ERROR)) {
				stepActual = "Error while creating Syndicated loan with same Interest and Payoff Priority";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

		}

		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters({ "branch", "productGroup", "product", "customerType" })
	public void SyndicatedloansDifferentData(@Optional("135 SYNDIC.ANDPARTICIP") final String branch,
			final String productGroup, final String product, final String customerType) {

		stepDescription = "Create Syndicated loan with LBC as the Lead Bank and two participants with different Interest Rates and Payoff Priorities";
		stepExpected = "Syndicated loan with LBC as the Lead Bank and two participants with different Interest Rates and Payoff Priorities created successfully";

		String parentLimit3;
		String subLimit3;
		String limitFields;
		String limitValues;
		String slAgreementFields;
		String slAgreementValues;
		String subLimit3Reference;
		ArrangementData loanData3A;
		ArrangementData loanData3B;
		ArrangementData loanData3Master;

		if (customer2 == null || customer3 == null || lenderA == null || lenderB == null || vostroAccountA == null
				|| vostroAccountB == null || customer2.contains(ERROR) || customer3.contains(ERROR)
				|| lenderA.contains(ERROR) || lenderB.contains(ERROR) || vostroAccountA.contains(ERROR)
				|| vostroAccountB.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);

		} else {

			limitFields = DefaultVariables.securedSAELimitFields
					+ "L.SYNDICAT.LIM,L.INVESTOR.ID:1,L.COMMITTED.AMT:1,L.INVESTOR.ID+:2,L.COMMITTED.AMT:2,";
			limitValues = DefaultVariables.securedSAELimitValues + "Yes," + lenderA + "," + "125T," + lenderB + ","
					+ "125T,";

			parentLimit3 = customerLimit(CREATE, RepositoryPaths.SECURED, ROLEBASED_SAE, product, customer3, "2400",
					"02", limitFields, limitValues);

			subLimit3 = customerLimit(CREATE_AUTHORISE, RepositoryPaths.SECURED, ROLEBASED_SAE, product, customer3, "",
					"02", limitFields, limitValues);

			slAgreementFields = "LOAN.CCY,CUSTOMER.ID,LOAN.AMT,SYN.ST.DATE,SYN.MAT.DATE,PART.ID:1,PART.AMT:1,PART.PERC:1,PART.VOSTRO.AC:1,";
			slAgreementValues = "CAD," + customer3 + ",125T,+0d,+5y," + lenderA + ",125T,100," + vostroAccountA + ",";

			agreement3LenderA = syndicationAgreement(CREATE, "", slAgreementFields, slAgreementValues);

			slAgreementFields = "LOAN.CCY,CUSTOMER.ID,LOAN.AMT,SYN.ST.DATE,SYN.MAT.DATE,PART.ID:1,PART.AMT:1,PART.PERC:1,PART.VOSTRO.AC:1,";
			slAgreementValues = "CAD," + customer3 + ",125T,+0d,+5y," + lenderB + ",125T,100," + vostroAccountB + ",";

			agreement3LenderB = syndicationAgreement(CREATE, "", slAgreementFields, slAgreementValues);

			slAgreementFields = "LOAN.CCY,CUSTOMER.ID,LOAN.AMT,SYN.ST.DATE,SYN.MAT.DATE,OWN.AMT,OWN.PERC,PART.ID:1,PART.AMT:1,PART.PERC:1,PART.VOSTRO.AC:1,PART.ID+:2,PART.AMT:2,PART.PERC:2,PART.VOSTRO.AC:2,L.MASTER.DETS,L.SERVICE.FEE,";
			slAgreementValues = "CAD," + customer3 + ",500T,+0d,+5y,250T,50," + lenderA + ",125T,25," + vostroAccountA
					+ "," + lenderB + ",125T,25," + vostroAccountB + ",Yes,LBCSLFEE1,";

			agreement3Master = syndicationAgreement(CREATE, "", slAgreementFields, slAgreementValues);

			subLimit3Reference = subLimit3.substring(13, 17);

			loanData3A = new ArrangementData("syndicatedLoan3A", productGroup, product)
					.withCustomers(customer3, createdCustomers.get(customer3), "BORROWER.OWNER", "100,", "100,")
					.withLimit(subLimit3Reference, "02", "N").withCommitmentAmount("125T")
					.withMarginSpread("+0.75", "3.75").withRenewalPeriod("5Y").withReserveAccount("NEW", true)
					.withSyndication("Lbc Participat.", agreement3LenderA).build();

			syndicatedLoan3A = createDefaultArrangement(loanData3A);

			loanData3B = new ArrangementData("syndicatedLoan3B", productGroup, product)
					.withCustomers(customer3, createdCustomers.get(customer3), "BORROWER.OWNER", "100,", "100,")
					.withLimit(subLimit3Reference, "02", "N").withCommitmentAmount("125T")
					.withMarginSpread("+0.75", "3.75").withRenewalPeriod("5Y")
					.withSyndication("Lbc Participat.", agreement3LenderB).build();

			syndicatedLoan3B = createDefaultArrangement(loanData3B);

			loanData3Master = new ArrangementData("syndicatedLoan3Master", productGroup, product)
					.withCustomers(customer3, createdCustomers.get(customer3), "BORROWER.OWNER", "100,", "100,")
					.withLimit(subLimit3Reference, "02", "N").withCommitmentAmount("250T")
					.withMarginSpread("-0.25", "2.75").withSyndication("Lbc Participat.", "").build();

			syndicatedLoan3Master = createDefaultArrangement(loanData3Master);

			if (syndicatedLoan3A == null || syndicatedLoan3B == null || syndicatedLoan3Master == null
					|| syndicatedLoan3A.contains(ERROR) || syndicatedLoan3B.contains(ERROR)
					|| syndicatedLoan3Master.contains(ERROR)) {
				stepActual = "Error while creating Syndicated loan with different Interest and Payoff Priority";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	@Parameters({ "branch", "productGroup", "product", "customerType" })
	public void disburseSyndicatedLoan(@Optional("135 SYNDIC.ANDPARTICIP") final String branch,
			final String productGroup, final String product, final String customerType) {

		stepDescription = "Disburse Syndicated loans";
		stepExpected = "Syndicated loans disbursed successfully";

		String fields;
		String values;
		boolean result = true;

		if (customer1 == null || customer2 == null || customer3 == null || syndicatedLoan1 == null
				|| syndicatedLoan2A == null || syndicatedLoan2Master == null || syndicatedLoan3A == null
				|| syndicatedLoan3B == null || syndicatedLoan3Master == null || customer1.contains(ERROR)
				|| customer2.contains(ERROR) || customer3.contains(ERROR) || syndicatedLoan1.contains(ERROR)
				|| syndicatedLoan2A.contains(ERROR) || syndicatedLoan2Master.contains(ERROR)
				|| syndicatedLoan3A.contains(ERROR) || syndicatedLoan3B.contains(ERROR)
				|| syndicatedLoan3Master.contains(ERROR)) {

			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);

		} else {

			fields = "DEBIT.CURRENCY,DEBIT.AMOUNT,CREDIT.ACCT.NO";
			values = "CAD,500T,CHEKCAD" + transit;

			result = result && arrangementAction(syndicatedLoan1, customer1, ROLEBASED_SAE, "AA Disbursement", fields,
					values, customerType);

			fields = "DEBIT.CURRENCY,DEBIT.AMOUNT,CREDIT.ACCT.NO";
			values = "CAD,250T,CHEKCAD" + transit;

			result = result && arrangementAction(syndicatedLoan2A, customer2, ROLEBASED_SAE, "AA Disbursement", fields,
					values, customerType);

			fields = "DEBIT.CURRENCY,DEBIT.AMOUNT,CREDIT.ACCT.NO";
			values = "CAD,250T,CHEKCAD" + transit;

			result = result && arrangementAction(syndicatedLoan2Master, customer2, ROLEBASED_SAE, "AA Disbursement",
					fields, values, customerType);

			fields = "DEBIT.CURRENCY,DEBIT.AMOUNT,CREDIT.ACCT.NO";
			values = "CAD,125T,CHEKCAD" + transit;

			result = result && arrangementAction(syndicatedLoan3A, customer3, ROLEBASED_SAE, "AA Disbursement", fields,
					values, customerType);

			fields = "DEBIT.CURRENCY,DEBIT.AMOUNT,CREDIT.ACCT.NO";
			values = "CAD,125T,CHEKCAD" + transit;

			result = result && arrangementAction(syndicatedLoan3B, customer3, ROLEBASED_SAE, "AA Disbursement", fields,
					values, customerType);

			fields = "DEBIT.CURRENCY,DEBIT.AMOUNT,CREDIT.ACCT.NO";
			values = "CAD,250T,CHEKCAD" + transit;

			result = result && arrangementAction(syndicatedLoan3Master, customer3, ROLEBASED_SAE, "AA Disbursement",
					fields, values, customerType);

			if (!result) {
				stepActual = "Error while disbursing syndicated loan";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	@Parameters({ "branch", "productGroup", "product", "customerType" })
	public void closeSyndicatedLoan(@Optional("135 SYNDIC.ANDPARTICIP") final String branch, final String productGroup,
			final String product, final String customerType) {

		stepDescription = "Close Syndicated Loan Agreement " + agreement2LenderA;
		stepExpected = "Syndicated Loan Agreement closed successfully";

		String agreementFields;
		String agreementValues;
		String closedAgreement;

		if (agreement2LenderA == null || agreement2LenderA.contains(ERROR)) {

			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);

		} else {

			agreementFields = "DISB.PART.AMT:1,SYN.STATUS,SYN.STATUS.DATE";
			agreementValues = "250T,Request.closure,+1m";

			closedAgreement = syndicationAgreement("Close", agreement2LenderA, agreementFields, agreementValues);

			if (closedAgreement == null || closedAgreement.contains(ERROR)) {
				stepActual = "Error while closing syndication agreement";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

		}
		softVerify.assertAll();
	}

	@Test(priority = 7, enabled = true)
	@Parameters({ "branch", "productGroup", "product", "customerType" })
	public void renewSyndicatedLoan(@Optional("135 SYNDIC.ANDPARTICIP") final String branch, final String productGroup,
			final String product, final String customerType) {

		stepDescription = "Renew Syndicated Loan Agreement " + agreement3LenderA + ", " + agreement3LenderB + " and "
				+ agreement3Master;
		stepExpected = "Syndicated Loan Agreement renewed successfully";

		String fields;
		String values;
		String renewLenderA;
		String renewLenderB;
		String renewMaster;

		if (agreement3LenderA == null || agreement3LenderB == null || agreement3Master == null
				|| agreement3LenderA.contains(ERROR) || agreement3LenderB.contains(ERROR)
				|| agreement3Master.contains(ERROR)) {

			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);

		} else {

			fields = "SYN.MAT.DATE";
			values = "+10y";

			renewLenderA = syndicationAgreement("Renew", agreement3LenderA, fields, values);
			renewLenderB = syndicationAgreement("Renew", agreement3LenderB, fields, values);
			renewMaster = syndicationAgreement("Renew", agreement3Master, fields, values);

			if (renewLenderA == null || renewLenderB == null || renewMaster == null || renewLenderA.contains(ERROR)
					|| renewLenderB.contains(ERROR) || renewMaster.contains(ERROR)) {
				stepActual = "Error while renewing syndication agreement";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

		}
		softVerify.assertAll();
	}

}
