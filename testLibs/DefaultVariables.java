package testLibs;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface DefaultVariables {

	// T24 Application Access Strings
	String felipeUsername = "FELIPE.DE.OLIVE";
	String felipePassword = "P@ssw0rd#1";
	String filipeUsername = "FILIPE.MENDES";
	String filipePassword = "Motdepasse1";
	String amruthaUsername = "AMRUTHA.KOYAM";
	String amruthaPassword = "Amrutha@1234";
	String gorasUsername = "GORAS.DESAI";
	String gorasPassword = "1605Goras1";
	String maulikUsername = "MAULIK.SHAH";
	String maulikPassword = "Tester456";
	String ashishUsername = "ASHISH.MISTRY1";
	String ashishPassword = "Test1234";
	String anuragUsername = "ANURAG.RADADIYA1";
	String anuragPassword = "Test1234";
	String aartiUsername = "AARTI.WADHWANI";
	String aartiPassword = "123Online";

	String felipeAuthorizer = "FELIPE.AUTH1";
	String felipeAuthorizerPassword = "P@ssw0rd#1";
	String filipeAuthorizer = "FILIPE.MENDES1";
	String filipeAuthorizerPassword = "Motdepasse1";
	String gorasAuthorizer = "GORAS.DESAI1";
	String gorasAuthorizerPassword = "1605Goras1";
	String amruthaAuthorizer = "AMRUTHA.KOYAM1";
	String amruthaAuthorizerPassword = "Amrutha@1234";
	String maulikAuthorizer = "MAULIK.SHAH1";
	String maulikAuthorizerPassword = "welcome1";
	String ashishAuthorizer = "ASHISH.MISTRY2";
	String ashishAuthorizerPassword = "Test1234";
	String anuragAuthorizer = "ANURAG.RADADIYA2";
	String anuragAuthorizerPassword = "Test1234";
	String aartiAuthorizer = "IGOR.KUNAREV";
	String aartiAuthorizerPassword = "Acvator9";

	// Additional Access for Data Generation
	String dataGen1Username = "AUTOMATION1";
	String dataGen1Password = "Test1234";
	String dataGen2Username = "AUTOMATION2";
	String dataGen2Password = "Test1234";
	String dataGen3Username = "AUTOMATION3";
	String dataGen3Password = "Test1234";
	String dataGen4Username = "AUTOMATION4";
	String dataGen4Password = "Test1234";
	String dataGen5Username = "AUTOMATION5";
	String dataGen5Password = "Test1234";
	String dataGen6Username = "AUTOMATION6";
	String dataGen6Password = "Test1234";
	String dataGen7Username = "AUTOMATION7";
	String dataGen7Password = "Test1234";
	String dataGen8Username = "AUTOMATION8";
	String dataGen8Password = "Test1234";
	String dataGen9Username = "AUTOMATION9";
	String dataGen9Password = "Test1234";
	String dataGen10Username = "AUTOMATION10";
	String dataGen10Password = "Test1234";
	String dataGen11Username = "AUTOMATION11";
	String dataGen11Password = "Test1234";
	String dataGen12Username = "AUTOMATION12";
	String dataGen12Password = "Test1234";
	String dataGen13Username = "AUTOMATION13";
	String dataGen13Password = "Test1234";
	String dataGen14Username = "AUTOMATION14";
	String dataGen14Password = "Test1234";
	String dataGen15Username = "AUTOMATION15";
	String dataGen15Password = "Test1234";
	String dataGen16Username = "AUTOMATION16";
	String dataGen16Password = "Test1234";

	String dataGen1Authorizer = "AUTOMATION.AUTH1";
	String dataGen1AuthorizerPassword = "Test1234";
	String dataGen2Authorizer = "AUTOMATION.AUTH2";
	String dataGen2AuthorizerPassword = "Test1234";
	String dataGen3Authorizer = "AUTOMATION.AUTH3";
	String dataGen3AuthorizerPassword = "Test1234";
	String dataGen4Authorizer = "AUTOMATION.AUTH4";
	String dataGen4AuthorizerPassword = "Test1234";
	String dataGen5Authorizer = "AUTOMATION.AUTH5";
	String dataGen5AuthorizerPassword = "Test1234";
	String dataGen6Authorizer = "AUTOMATION.AUTH6";
	String dataGen6AuthorizerPassword = "Test1234";
	String dataGen7Authorizer = "AUTOMATION.AUTH7";
	String dataGen7AuthorizerPassword = "Test1234";
	String dataGen8Authorizer = "AUTOMATION.AUTH8";
	String dataGen8AuthorizerPassword = "Test1234";
	String dataGen9Authorizer = "AUTOMATION.AUTH9";
	String dataGen9AuthorizerPassword = "Test1234";
	String dataGen10Authorizer = "AUTOMATION.AUT10";
	String dataGen10AuthorizerPassword = "Test1234";
	String dataGen11Authorizer = "AUTOMATION.AUT11";
	String dataGen11AuthorizerPassword = "Test1234";
	String dataGen12Authorizer = "AUTOMATION.AUT12";
	String dataGen12AuthorizerPassword = "Test1234";
	String dataGen13Authorizer = "AUTOMATION.AUT13";
	String dataGen13AuthorizerPassword = "Test1234";
	String dataGen14Authorizer = "AUTOMATION.AUT14";
	String dataGen14AuthorizerPassword = "Test1234";
	String dataGen15Authorizer = "AUTOMATION.AUT15";
	String dataGen15AuthorizerPassword = "Test1234";
	String dataGen16Authorizer = "AUTOMATION.AUT16";
	String dataGen16AuthorizerPassword = "Test1234";

	Map<String, String> dataGenPasswords = new ConcurrentHashMap<String, String>() {
		{
			put(dataGen1Username, dataGen1Password);
			put(dataGen2Username, dataGen2Password);
			put(dataGen3Username, dataGen3Password);
			put(dataGen4Username, dataGen4Password);
			put(dataGen5Username, dataGen5Password);
			put(dataGen6Username, dataGen6Password);
			put(dataGen7Username, dataGen7Password);
			put(dataGen8Username, dataGen8Password);
			put(dataGen9Username, dataGen9Password);
			put(dataGen10Username, dataGen10Password);
			put(dataGen11Username, dataGen11Password);
			put(dataGen12Username, dataGen12Password);
			put(dataGen13Username, dataGen13Password);
			put(dataGen14Username, dataGen14Password);
			put(dataGen15Username, dataGen15Password);
			put(dataGen16Username, dataGen16Password);
		}
	};

	// Parent Handle Strings for T24 Application
	String felipeDisplayName = "B2B.FILIPE.D.OLI01";
	String filipeDisplayName = "B2B.FILIPE.MEND01";
	String amruthaDisplayName = "B2B.AMRUTHA.KOYA01";
	String gorasDisplayName = "B2B.GORAS.DESAI01";
	String maulikDisplayName = "B2B.MAULIK.SHAH01";
	String ashishDisplayName = "B2B.ASHISH.MISTRY01";
	String anuragDisplayName = "B2B.ANURAG.RADADIYA01";
	String aartiDisplayName = "B2B.AARTI";

	String dataGen1DisplayName = "B2B.DATA.GEN01";
	String dataGen2DisplayName = "B2B.DATA.GEN02";
	String dataGen3DisplayName = "B2B.DATA.GEN03";
	String dataGen4DisplayName = "B2B.DATA.GEN04";
	String dataGen5DisplayName = "B2B.DATA.GEN05";
	String dataGen6DisplayName = "B2B.DATA.GEN06";
	String dataGen7DisplayName = "B2B.DATA.GEN07";
	String dataGen8DisplayName = "B2B.DATA.GEN08";
	String dataGen9DisplayName = "B2B.DATA.GEN09";
	String dataGen10DisplayName = "B2B.DATA.GEN10";
	String dataGen11DisplayName = "B2B.DATA.GEN11";
	String dataGen12DisplayName = "B2B.DATA.GEN12";
	String dataGen13DisplayName = "B2B.DATA.GEN13";
	String dataGen14DisplayName = "B2B.DATA.GEN14";
	String dataGen15DisplayName = "B2B.DATA.GEN15";
	String dataGen16DisplayName = "B2B.DATA.GEN16";

	public static String dummyBeneficiary = "BEN1111111111";
	public static boolean authorizeEscrowPayee = false;
	public static boolean authorizeEscrowAccount = false;
	public static boolean authorizeB2BLimit = false;
	public static boolean authorizeSAELimit = false;

	// Default Customer Fields and Values
	String personalCIFFields = "ID#CAMB.ID.TYPE:1," + "ID#CAMB.ID.UNQ.ID:1," + "ID#ID.CTRY.ISS:1,"
			+ "ID#DOC.ISS.DATE:1," + "ID#DOC.EXP.DATE:1," + "ID#CAMB.ID.TYPE:2," + "ID#CAMB.ID.UNQ.ID:2,"
			+ "ID#ID.CTRY.ISS:2," + "ID#DOC.ISS.DATE:2," + "ID#DOC.EXP.DATE:2," + "TITLE," + "SECTOR," + "INDUSTRY,"
			+ "NATIONALITY," + "EXL.SIN," + "CUSTOMER.STATUS," + "Residential Details#RESIDENCE.STATUS:1,"
			+ "Residential Details#RESIDENCE.TYPE:1," + "Residential Details#RESIDENCE.SINCE:1,"
			+ "Residential Details#RESIDENCE.VALUE:1," + "ACCOUNT.OFFICER," + "RESIDENCE,";
	String personalCIFValues = "LICENSE," + "123456," + "CA," + "20141129," + "20201130," + "PASSPORT," + "654321,"
			+ "CA," + "20151129," + "20211130," + "Other," + "1001," + "1010," + "CA," + "N," + "1," + "Householder,"
			+ "Apartment," + "-5y," + "500000," + "1," + "CA,";

	String businessCIFFields = "BLOCK.CARD.ISSU," + "CUSTOMER.RATING:1," + "BIRTH.INCORP.DATE," + "RESIDENCE.REGION,"
			+ "ACCOUNT.OFFICER," + "RESIDENCE,";
	String businessCIFValues = "NO," + "1," + "-3y," + "CA02," + "1," + "CA,";

	String clientBusinessCIFFields = businessCIFFields + "SECTOR," + "NAICS," + "INDUSTRY,";
	String clientBusinessCIFValues = businessCIFValues + "2004," + "111110," + "1600,";

	String nonClientBusinessCIFFields = businessCIFFields + "SECTOR," + "INDUSTRY,";
	String nonClientBusinessCIFValues = businessCIFValues + "1010," + "1010,";

	String dealerAdvisorCIFFields = "INDUSTRY," + "TARGET," + "SECTOR," + "BUSINESS.NAME:1," + "BUSINESS.NAME+:2,"
			+ "ACCOUNT.OFFICER," + "TARGET," + "CUSTOMER.STATUS," + "RESIDENCE.REGION," + "RESIDENCE," + "L.MAIL.PREF,"
			+ "ACCOUNT.OFFICER," + "RESIDENCE,";
	String dealerAdvisorCIFValues = "3201," + "1," + "7010," + "Tester Dealer Name," + "Continuation1," + "1," + "999,"
			+ "401," + "CA02," + "CA," + "Purolator," + "1," + "CA,";

	// Default Arrangement Fields and Values
	String bankingFields = "PRIMARY.OFFICER," + "CA.INTEND.USE," + "CA.THIRD.PARTY,";
	String bankingValues = "1," + "17," + "N,";

	String loanFields = "SINGLE.LIMIT," + "AMOUNT," + "TERM," + "Advanced - Pay In#PAYIN.SETTLEMENT:1,"
			+ "STATEMENT.REQD:1," + "Account$L.LOAN.OFFER.ID," + "Account$L.INV.LOAN.TYPE,";
	String loanValues = "N," + "100000," + "1Y," + "No," + "Yes," + "B2b Bank (sd & Deposits)," + "2.for.1,";

	String mortgageFields = "SINGLE.LIMIT," + "CA.INTEND.USE," + "CA.THIRD.PARTY," + "CA.PURPOSE.CODE:1,"
			+ "L.FIN.SEGMENT," + "L.INS.COMP.NAME," + "CA.CMHC.NO," + "L.1ST.HOME.OWN," + "AMOUNT," + "TERM,"
			+ "L.NET.DISB.AMT," + "Advanced - Pay In#PAYIN.SETTLEMENT:1," + "Insurance#LIFE.INS.FLAG:1,"
			+ "Insurance#DISABILITY.FLAG:1," + "Account$MOS Interface#L.INS.AMOUNT," + "L.HOLDBACK.AMT,"
			+ "L.IMPROV.HB.AMT," + "L.TAX.PAY.IND," + "Principal Interest$Control#DAY.BASIS,"
			+ "Principal Interest$MOS Interface#L.POSTED.RATE,";
	String mortgageValues = "N," + "11," + "N," + "3," + "1," + "Cmhc," + "12345678," + "NO," + "100000," + "25Y,"
			+ "100000," + "No," + "Rejected.By.Insurance," + "Rejected.By.Insurance," + "100000," + "5000," + "5000,"
			+ "YES," + "A," + "4.79,";

	String helocFields = "SINGLE.LIMIT," + "CA.INTEND.USE," + "CA.THIRD.PARTY," + "CA.PURPOSE.CODE:1,"
			+ "L.FIN.SEGMENT," + "L.1ST.HOME.OWN," + "ORIGINATION.REF," + "Account$MOS Interface#L.HOK.IND," + "AMOUNT,"
			+ "TERM," + "Advanced - Pay In#PAYIN.SETTLEMENT:1," + "Principal Interest$MOS Interface#L.POSTED.RATE,"
			+ "Settlement$MOS Interface#L.REIMB.TYPE," + "L.DEBIT.CARD,";
	String helocValues = "N," + "11," + "N," + "3," + "Equity," + "NO," + "123456789," + "NO," + "100000," + "25Y,"
			+ "No," + "1.5," + "2," + "NO,";

	String bridgeLoanFields = "SINGLE.LIMIT," + "CA.INTEND.USE," + "CA.THIRD.PARTY," + "L.1ST.HOME.OWN,"
			+ "ORIGINATION.REF," + "Account$MOS Interface#L.HOK.IND," + "AMOUNT," + "TERM,"
			+ "Advanced - Pay In#PAYIN.SETTLEMENT:1," + "Principal Interest$Fixed/Floating/Linked#FIXED.RATE:1,";
	String bridgeLoanValues = "N," + "11," + "N," + "NO," + "123456789," + "NO," + "100000," + "25Y," + "No," + "1.5,";

	String gicFields = "PRIMARY.OFFICER," + "Default Values#CA.INTEND.USE," + "CA.THIRD.PARTY," + "AMOUNT," + "TERM,"
			+ "Advanced - Pay In#PAYIN.SETTLEMENT:1," + "Advanced - Pay Out#PAYOUT.SETTLEMENT:1,"
			+ "Advanced - Pay Out#PAYOUT.SETTLEMENT:2," + "Deposit Renewal$CHANGE.DATE.TYPE,"
			+ "Deposit Renewal$CHANGE.PERIOD," + "Deposit Renewal$INITIATION.TYPE,"
			+ "Deposit Interest$PERIODIC.RATE:1,";
	String gicValues = "1," + "01," + "N," + "100000," + "25Y," + "No," + "No," + "No," + "Period," + "1Y," + "Auto,"
			+ "1.5,";

	String agentRequiredFields = "AGENT.ID:1," + "AGENT.ARR.ID:1," + "AGENT.ROLE:1,";

	String reserveAccountFields = "PRIMARY.OFFICER," + "CA.INTEND.USE," + "CA.THIRD.PARTY,"
			+ "Credit Interest$Fixed/Floating/Linked#FIXED.RATE:1,"
			+ "Debit Interest$Fixed/Floating/Linked#FIXED.RATE:1,";
	String reserveAccountValues = "1," + "18," + "N," + "1.5," + "1.5,";

	String vostroAccountFields = "PRIMARY.OFFICER," + "CA.INTEND.USE," + "CA.THIRD.PARTY,";
	String vostroAccountValues = "1," + "12," + "N,";

	String fixRateFields = "Fixed/Floating/Linked#FIXED.RATE:1,";
	String fixRateValues = "1.5,";

	String varRateLoanFields = "Periodic#MARGIN.RATE:1:1,";
	String varRateLoanValues = "1.00,";

	String varRateMrtgFields = "Periodic#PERIODIC.INDEX:1,";
	String varRateMrtgValues = "02,";

	String varRateSAEFields = "Principal Interest$Fixed/Floating/Linked#FLOATING.INDEX:1,";
	String varRateSAEValues = "11,";

	String unsecuredLimitFields = "LIMIT.CURRENCY," + "COUNTRY.OF.RISK:1," + "EXPIRY.DATE," + "INTERNAL.AMOUNT,"
			+ "AVAILABLE.MARKER," + "ALLOW.NETTING,";
	String unsecuredLimitValues = "CAD," + "CA," + "+99Y," + "200000," + "Y," + "NO,";

	String securedLoanLimitFields = unsecuredLimitFields + "FIXED.VARIABLE," + "COLLATERAL.CODE:1," + "ONLINE.UPDATE,"
			+ "MAXIMUM.SECURED:1,";
	String securedLoanLimitValues = unsecuredLimitValues + "VARIABLE," + "401," + "Y," + "200000,";

	String securedMortgageLimitFields = "LIMIT.CURRENCY," + "COUNTRY.OF.RISK:1," + "EXPIRY.DATE," + "FIXED.VARIABLE,"
			+ "COLLATERAL.CODE:1," + "MAXIMUM.SECURED:1," + "INTERNAL.AMOUNT,";
	String securedMortgageLimitValues = "CAD," + "CA," + "+99Y," + "FIXED," + "301," + "200000," + "200000,";

	String securedSAELimitFields = "LIMIT.CURRENCY," + "COUNTRY.OF.RISK:1," + "EXPIRY.DATE," + "FIXED.VARIABLE,"
			+ "COLLATERAL.CODE:1," + "MAXIMUM.SECURED:1," + "INTERNAL.AMOUNT,";
	String securedSAELimitValues = "CAD," + "CA," + "+3Y," + "FIXED," + "326," + "200000," + "200000,";

	String collateralFields = "Collateral details#COLL.VALUE," + "Collateral details#ADDR.CNTRY.ID,"
			+ "Collateral details#CA.PROP.CODE," + "Collateral details#L.COLL.BLD.UNTS,"
			+ "Collateral details#L.COLL.RESID," + "Collateral details#L.COLL.RENTAL,"
			+ "Collateral details#MANUFACTURE.YR," + "Collateral#CURRENCY," + "Collateral#NOMINAL.VALUE,";

	String collateralValues = "500000," + "CA," + "0," + "1," + "PRIMARY," + "NO," + "2005," + "CAD," + "500000,";

	String beneficiaryFields = "BANK.SORT.CODE," + "BEN.ACCT.NO,";
	String beneficiaryValues = "000100011," + "04001057603,";

	String saeLoanFields = "PRIMARY.OFFICER," + " ALLOW.NETTING," + "AMOUNT," + "TERM,"
			+ "Advanced - Pay In#PAYIN.SETTLEMENT:1," + "REVOLVING,";
	String saeLoanValues = "1059," + " No," + "500T," + "5Y," + "No," + "PAYMENT,";

	String escrowAccountFields = "Default#REFERENCE.NO:1," + "Default#ANNUAL.DISBURSE.AMT:1,"
			+ "Analysis Details#ANALYSIS.TYPE," + "Analysis Details#INSTAL.AMOUNT,";
	String escrowAccountValues = "600089," + "3000," + "TPA," + "150,";

	String escrowPayeeFields = "PAYEE.TYPE," + "STATUS," + "Setup#SETTLEMENT.TYPE," + "COMPANY.CODE:1,"
			+ "DISBURSE.ACC:1," + "IRREGULAR.PRD:1," + "IRREGULAR.PRD+:2," + "IRREGULAR.PRD+:3," + "IRREGULAR.PRD+:4,"
			+ "Additional#PENALTY.TYPE," + "PENALTY.RATE," + "DAY.BASIS,";
	String escrowPayeeValues = "MT," + "Active," + "ACH," + "CA0017000," + "CAD1160000017623," + "0519," + "0619,"
			+ "0719," + "0819," + "Daily," + "2.5," + "E,";

	Map<String, String> customerTypeFields = new ConcurrentHashMap<String, String>() {
		{
			put(RepositoryPaths.PERSONAL, personalCIFFields);
			put(RepositoryPaths.BUSINESS, clientBusinessCIFFields);
			put(RepositoryPaths.DEALER_ADVISOR, dealerAdvisorCIFFields);
			put(RepositoryPaths.NON_CLIENT_PERSONAL, personalCIFFields);
			put(RepositoryPaths.NON_CLIENT_BUSINESS, nonClientBusinessCIFFields);
		}
	};

	Map<String, String> customerTypeValues = new ConcurrentHashMap<String, String>() {
		{
			put(RepositoryPaths.PERSONAL, personalCIFValues);
			put(RepositoryPaths.BUSINESS, clientBusinessCIFValues);
			put(RepositoryPaths.DEALER_ADVISOR, dealerAdvisorCIFValues);
			put(RepositoryPaths.NON_CLIENT_PERSONAL, personalCIFValues);
			put(RepositoryPaths.NON_CLIENT_BUSINESS, nonClientBusinessCIFValues);
		}
	};

	Map<String, String> productGroupFields = new ConcurrentHashMap<String, String>() {
		{
			put(RepositoryPaths.PERSONAL_ACCOUNTS, bankingFields);
			put(RepositoryPaths.BUSINESS_ACCOUNTS, bankingFields);
			put(RepositoryPaths.SERVICING_ACCOUNTS, reserveAccountFields);
			put(RepositoryPaths.RETAIL_DEPOSITS, gicFields);
			put(RepositoryPaths.BROKER_NONREG_DEPOSITS, gicFields);
			put(RepositoryPaths.BROKER_REG_DEPOSITS, gicFields);
			put(RepositoryPaths.COMMERCIAL_LOANS, saeLoanFields);
			put(RepositoryPaths.PERSONAL_LOANS, loanFields);
			put(RepositoryPaths.PERSONAL_LOC, loanFields);
			put(RepositoryPaths.RETAIL_MORTGAGES, mortgageFields);
			put(RepositoryPaths.AGENTS, "");
		}
	};

	Map<String, String> productGroupValues = new ConcurrentHashMap<String, String>() {
		{
			put(RepositoryPaths.PERSONAL_ACCOUNTS, bankingValues);
			put(RepositoryPaths.BUSINESS_ACCOUNTS, bankingValues);
			put(RepositoryPaths.SERVICING_ACCOUNTS, reserveAccountValues);
			put(RepositoryPaths.RETAIL_DEPOSITS, gicValues);
			put(RepositoryPaths.BROKER_NONREG_DEPOSITS, gicValues);
			put(RepositoryPaths.BROKER_REG_DEPOSITS, gicValues);
			put(RepositoryPaths.COMMERCIAL_LOANS, saeLoanValues);
			put(RepositoryPaths.PERSONAL_LOANS, loanValues);
			put(RepositoryPaths.PERSONAL_LOC, loanValues);
			put(RepositoryPaths.RETAIL_MORTGAGES, mortgageValues);
			put(RepositoryPaths.AGENTS, "");
		}
	};

	Map<String, String> productFields = new ConcurrentHashMap<String, String>() {
		{
			put(RepositoryPaths.HELOC, helocFields);
			put("Bridge Loan", bridgeLoanFields);
			put("Vostro Account", vostroAccountFields);
		}
	};

	Map<String, String> productValues = new ConcurrentHashMap<String, String>() {
		{
			put(RepositoryPaths.HELOC, helocValues);
			put("Bridge Loan", bridgeLoanValues);
			put("Vostro Account", vostroAccountValues);
		}
	};

	Map<String, String> productInterestFields = new ConcurrentHashMap<String, String>() {
		{
			put("Demand Loans", fixRateFields);
			put("Construction loan", fixRateFields);
			put("Default Comm.Multi-Residential Prod", varRateLoanFields);
			put("Immigrant Investor Program-FED", fixRateFields);
			put("Immigrant Investor Program-Prov", fixRateFields);
			put("B2B Bank LOC - RRSP", varRateLoanFields);
			put("Secured Line of Credit", varRateLoanFields);
			put("B2B Bank LOC - Personal Secured", varRateLoanFields);
			put("Unsecured Line of Credit", varRateLoanFields);
			put("B2B Bank LOC - Personal Unsecured", varRateLoanFields);
			put("Mortgages AGF Trust (grandfathered)", fixRateFields);
			put("Bridge Loan", fixRateFields);
			put("Convertible Mortgage", varRateMrtgFields);
			put("HELOC", varRateMrtgFields);
			put("HELOC CMHC AGF Trust(Grandfathered)", varRateMrtgFields);
			put("Real Estate Mortgage(grandfathered)", varRateMrtgFields);
			put("Multi-Residential MG 5 Pls Var Rate", varRateSAEFields);
			put("Commercial Term Loans Var Rate", varRateSAEFields);
			put("CSA Term Loan Variable Rate", varRateSAEFields);
			put("Real Estate Mortgage Var Rate", varRateSAEFields);
			put("Default Comm.Multi-Residential Prod", fixRateFields);
			put("Multi-Residential MG 5 Pls Fix Rate", fixRateFields);
			put("Commercial Term Loans Fixed Rate", fixRateFields);
			put("CSA Term Loan Fixed Rate", fixRateFields);
			put("Real Estate Mortgage Fix Rate", fixRateFields);
			put("Multi-Residential MG 1-4 Fix. Rate", fixRateFields);
			put("Investment Loan Fixed Rate", fixRateFields);
			put("Personal Loan Secured Fixed Rate", fixRateFields);
			put("Personal Loan Unsecured Fixed Rate", fixRateFields);
			put("RRSP Personal Loan Fixed Rate", fixRateFields);
			put("TFSA Loan Fixed Rate", fixRateFields);
			put("Investment Loan Variable Rate", varRateLoanFields);
			put("Personal Loan Secured Variable Rate", varRateLoanFields);
			put("Personal Loan Unsecured Variable Rate", varRateLoanFields);
			put("RRSP Personal Loan Variable Rate", varRateLoanFields);
			put("TFSA Loan Variable Rate", varRateLoanFields);

		}
	};

	Map<String, String> productInterestValues = new ConcurrentHashMap<String, String>() {
		{
			put("Demand Loans", fixRateValues);
			put("Construction loan", fixRateValues);
			put("Default Comm.Multi-Residential Prod", varRateLoanValues);
			put("Immigrant Investor Program-FED", fixRateValues);
			put("Immigrant Investor Program-Prov", fixRateValues);
			put("B2B Bank LOC - RRSP", varRateLoanValues);
			put("Secured Line of Credit", varRateLoanValues);
			put("B2B Bank LOC - Personal Secured", varRateLoanValues);
			put("Unsecured Line of Credit", varRateLoanValues);
			put("B2B Bank LOC - Personal Unsecured", varRateLoanValues);
			put("Mortgages AGF Trust (grandfathered)", fixRateValues);
			put("Bridge Loan", fixRateValues);
			put("Convertible Mortgage", varRateMrtgValues);
			put("HELOC", varRateMrtgValues);
			put("HELOC CMHC AGF Trust(Grandfathered)", varRateMrtgValues);
			put("Real Estate Mortgage(grandfathered)", varRateMrtgValues);
			put("Multi-Residential MG 5 Pls Var Rate", varRateSAEValues);
			put("Commercial Term Loans Var Rate", varRateSAEValues);
			put("CSA Term Loan Variable Rate", varRateSAEValues);
			put("Real Estate Mortgage Var Rate", varRateSAEValues);
			put("Default Comm.Multi-Residential Prod", fixRateValues);
			put("Multi-Residential MG 5 Pls Fix Rate", fixRateValues);
			put("Commercial Term Loans Fixed Rate", fixRateValues);
			put("CSA Term Loan Fixed Rate", fixRateValues);
			put("Real Estate Mortgage Fix Rate", fixRateValues);
			put("Multi-Residential MG 1-4 Fix. Rate", fixRateValues);
			put("Investment Loan Fixed Rate", fixRateValues);
			put("Personal Loan Secured Fixed Rate", fixRateValues);
			put("Personal Loan Unsecured Fixed Rate", fixRateValues);
			put("RRSP Personal Loan Fixed Rate", fixRateValues);
			put("TFSA Loan Fixed Rate", fixRateValues);
			put("Investment Loan Variable Rate", varRateLoanValues);
			put("Personal Loan Secured Variable Rate", varRateLoanValues);
			put("Personal Loan Unsecured Variable Rate", varRateLoanValues);
			put("RRSP Personal Loan Variable Rate", varRateLoanValues);
			put("TFSA Loan Variable Rate", varRateLoanValues);

		}
	};

	Map<String, String> productGroupCustomerType = new ConcurrentHashMap<String, String>() {
		{
			put(RepositoryPaths.PERSONAL_ACCOUNTS, RepositoryPaths.PERSONAL);
			put(RepositoryPaths.BUSINESS_ACCOUNTS, RepositoryPaths.BUSINESS);
			put(RepositoryPaths.SERVICING_ACCOUNTS, RepositoryPaths.BUSINESS);
			put(RepositoryPaths.RETAIL_DEPOSITS, RepositoryPaths.BUSINESS);
			put(RepositoryPaths.BROKER_REG_DEPOSITS, RepositoryPaths.PERSONAL);
			put(RepositoryPaths.BROKER_NONREG_DEPOSITS, RepositoryPaths.BUSINESS);
			put(RepositoryPaths.COMMERCIAL_LOANS, RepositoryPaths.BUSINESS);
			put(RepositoryPaths.PERSONAL_LOANS, RepositoryPaths.PERSONAL);
			put(RepositoryPaths.PERSONAL_LOC, RepositoryPaths.PERSONAL);
			put(RepositoryPaths.RETAIL_MORTGAGES, RepositoryPaths.PERSONAL);
			put(RepositoryPaths.AGENTS, RepositoryPaths.DEALER_ADVISOR);
			put(RepositoryPaths.SERVICING_ACCOUNTS, RepositoryPaths.BUSINESS);
		}
	};

	Map<String, String> productGroupArrangementType = new ConcurrentHashMap<String, String>() {
		{
			put(RepositoryPaths.PERSONAL_ACCOUNTS, "Banking");
			put(RepositoryPaths.BUSINESS_ACCOUNTS, "Banking");
			put(RepositoryPaths.SERVICING_ACCOUNTS, "Banking");
			put(RepositoryPaths.RETAIL_DEPOSITS, "Deposit");
			put(RepositoryPaths.BROKER_NONREG_DEPOSITS, "Deposit");
			put(RepositoryPaths.BROKER_REG_DEPOSITS, "Deposit");
			put(RepositoryPaths.COMMERCIAL_LOANS, "SAE Lending");
			put(RepositoryPaths.PERSONAL_LOANS, "Lending");
			put(RepositoryPaths.PERSONAL_LOC, "Lending");
			put(RepositoryPaths.RETAIL_MORTGAGES, "Mortgages");
			put(RepositoryPaths.AGENTS, "Agent");
			put(RepositoryPaths.LBC_BROKER_AGENT, "Agent");
		}
	};

	Map<String, String> productLineRoleBasedPage = new ConcurrentHashMap<String, String>() {
		{
			put("Banking", RepositoryPaths.ROLEBASED_BANKING);
			put("Deposit", RepositoryPaths.ROLEBASED_BANKING);
			put("Lending", RepositoryPaths.ROLEBASED_LENDING);
			put("Mortgages", RepositoryPaths.ROLEBASED_LENDING);
			put("Agent", RepositoryPaths.ROLEBASED_OR);
			put("SAE Lending", RepositoryPaths.ROLEBASED_SAE);
		}
	};

	Map<String, String> productCodes = new ConcurrentHashMap<String, String>() {
		{
			put("Personal Chequing", "AR.PER.CAD.CHQ");
			put("HISA Personal", "AR.PER.CAD.HISA");
			put("B2B Bank Chequing Account", "AR.UNLIM.CAD.CHQ");
			put("Cashable Simple Annually", "AD.CASH.ANNUAL.GIC");
			put("Cashable Simple Monthly", "AD.CASH.MONTHLY.GIC");
			put("Multi Rate 3 Year GIC", "AD.MULTI.RATE.3YGIC");
			put("Multi Rate 5 Year GIC", "AD.MULTI.RATE.5YGIC");
			put("Personal Loan Secured Fixed Rate", "AL.PR.LN.SEC.FIX.RT");
			put("Personal Loan Secured Var Rate", "AL.PR.LN.SEC.VAR.RT");
			put("Personal Loan Unsecured Fixed Rate", "AL.PR.LN.USEC.FIX.RT");
			put("Personal Loan UnSecured Var Rate", "AL.PR.LN.USEC.VAR.RT");
			put("Investment Loan Fixed Rate", "AL.INVS.PL.FIX.RATE");
			put("Investment Loan Variable Rate", "AL.INVS.PL.VAR.RATE");
			put("B2B Bank LOC - RRSP", "AL.LOC.RRSP");
			put("Secured Line of Credit", "AL.SLOC");
			put("B2B Bank LOC - Personal Secured", "AL.SLOC.PERSONAL");
			put("Unsecured Line of Credit", "AL.ULOC");
			put("B2B Bank LOC - Personal Unsecured", "AL.USLOC.PERSONAL");
			put("Commercial Term Loans Fixed Rate", "AL.COMM.TERM.LOAN.FIX");
			put("Commercial Term Loans Var Rate", "AL.COMM.TERM.LOAN.VAR");
			put("Multi-Residential MG 5 Pls Fix Rate", "AL.COM.RE.MRM.5P.FIX");
			put("Multi-Residential MG 5 Pls Var Rate", "AL.COM.RE.MRM.5P.VAR");
			put("Construction loan", "AL.CONSTRUCT.LOAN");
			put("Mortgages AGF Trust (grandfathered)", "AL.AGF.MORTGAGE.GF");
			put("Bridge Loan", "AL.BRIDGE.LOAN");
			put("Convertible Mortgage", "AL.CONVERTABLE.MG");
			put("Fixed Rate Closed Term Mortgage", "AL.FX.RT.MG.CL.TM");
			put("Fixed Rate Open Term Mortgage", "AL.FX.RT.MG.OP.TM");
			put("HELOC", "AL.HELOC");
			put("HELOC CMHC AGF Trust(Grandfathered)", "AL.HELOC.CMHC.AFG.GF");
			put("Real Estate Mortgage(grandfathered) ", "AL.RE.MORTGAGE.GF");
			put("Var Rate Closed Term MG Fix Pay", "AL.V.R.MG.CL.TM.FX.PY");
			put("Var Rate Closed Term Mrtg Var Pay", "AL.V.R.MG.CL.TM.VR.PY");
			put("Reserve Account", "AR.RESERVE");
			put("Non-Redeemable Simple Semi-Annually", "AD.BKR.NR.SEMIANN");
			put("Non-Redeemable Simple Quarterly", "AD.BKR.NR.QRTLY.GIC");
			put("Non-Redeemable Simple Monthly", "AD.BKR.NR.MONTHLY.GIC");
			put("Non-Redeemable Simple At Maturity", "AD.BKR.NR.MATURITY");
			put("Non-Redeemable Compound Annual", "AD.BKR.NR.CMPD.GIC");
			put("Non-Redeemable Simple Annual", "AD.BKR.NR.ANNUAL.GIC");
			// For Agents

			put("Account LOC Commission Plan", "AG.ACCOUNT.PLAN");
			put("Deposit Commission Plan", "AG.DEPOSIT.PLAN");
			put("Dummy Commission Plan", "AG.AGENT.PLAN");
			put("General Broker Commission Plan", "AG.GENERAL.BKR.PLAN");
			put("PFS Commission Plan", "AG.PFS.PLAN");
			put("PMPP Commission Plan", "AG.PMPP.PLAN");
			put("TMACC Commission Plan", "AG.TMCC.PLAN");
			put("Verico-Upfront Commission Plan", "AG.UF.VERICO.PLAN");
			put("DLC VB GT 52% Commission Plan", "AG.DLCVB.GT52.PLAN");
			put("DLC VB LT 52% Commission Plan", "AG.DLCVB.LT52.PLAN");
			put("RBC Commission Plan", "AG.RBC.PLAN");
			put("Royalty-Trailer Commission Plan", "AG.TR.ROYALTY.PLAN");
			put("Verico-Trailer Commission Plan", "AG.TR.VERICO.PLAN");
			put("Royalty-Upfront Commission Plan", "AG.UF.ROYALTY.PLAN");
			put("PFS Volume Bonus Commission Plan", "AG.PFS.VB.PLAN");
			put("PMPP TMACC Volume Bonus Plan", "AG.PMTM.VB.PLAN");
			put("General Broker VB Commission Plan", "AG.BROKER.VB.PLAN");

		}
	};

	Map<String, String> revolvingLimitRefs = new ConcurrentHashMap<String, String>() {
		{
			put("B2B Bank LOC - Personal Secured", "2300");
			put("B2B Bank LOC - Personal Unsecured", "2300");
			put("B2B Bank LOC - RRSP", "2300");
			put("Bankers Acceptance", "2407");
			put("Bridge Loan", "2300");
			put("Commercial Term Loans Fixed Rate", "2404");
			put("Commercial Term Loans Var Rate", "2404");
			put("Construction loan", "2403");
			put("Convertible Mortgage", "2000");
			put("CSA Term Loan Fixed Rate", "2404");
			put("CSA Term Loan Variable Rate", "2404");
			put("Default Comm.Multi-Residential Prod", "2403");
			put("Demand Loans", "2404");
			put("Fixed Rate Closed Term Mortgage", "2000");
			put("Fixed Rate Open Term Mortgage", "2000");
			put("FX Line", "2401");
			put("HELOC", "2200");
			put("HELOC CMHC AGF Trust(Grandfathered)", "2200");
			put("Immigrant Investor Program-FED", "2300");
			put("Immigrant Investor Program-Prov", "2300");
			put("Investment Loan Fixed Rate", "2300");
			put("Investment Loan Variable Rate", "2300");
			put("Letter Guarantee", "2405");
			put("Letter of Credit", "2405");
			put("Line of Credit", "2402");
			put("Mortgages AGF Trust (grandfathered)", "2000");
			put("Multi-Residential MG 1-4 Fix. Rate", "2403");
			put("Multi-Residential MG 1-4 Var. Rate", "2403");
			put("Multi-Residential MG 5 Pls Fix Rate", "2403");
			put("Multi-Residential MG 5 Pls Var Rate", "2403");
			put("Personal Loan Secured Fixed Rate", "2300");
			put("Personal Loan Secured Var Rate", "2300");
			put("Personal Loan Unsecured Fixed Rate", "2300");
			put("Personal Loan UnSecured Var Rate", "2300");
			put("Real Estate Mortgage(grandfathered)", "2000");
			put("Real Estate Mortgage Fix Rate", "2403");
			put("Real Estate Mortgage Var Rate", "2403");
			put("RRSP Personal Loan Fixed Rate", "2300");
			put("RRSP Personal Loan Variable Rate", "2300");
			put("Secured Line of Credit", "2200");
			put("TFSA Loan Fixed Rate", "2300");
			put("TFSA Loan Variable Rate", "2300");
			put("Unsecured Line of Credit", "2200");
			put("Var Rate Closed Term MG Fix Pay", "2000");
			put("Var Rate Closed Term Mrtg Var Pay", "2000");
			put("Visa", "2406");
		}
	};

	Map<String, String> nonrevolvingLimitRefs = new ConcurrentHashMap<String, String>() {
		{
			put("Bankers Acceptance", "2507");
			put("Bridge Loan", "2300");
			put("Commercial Term Loans Fixed Rate", "2504");
			put("Commercial Term Loans Var Rate", "2504");
			put("Construction loan", "2503");
			put("CSA Term Loan Fixed Rate", "2504");
			put("CSA Term Loan Variable Rate", "2504");
			put("Default Comm.Multi-Residential Prod", "2503");
			put("Demand Loans", "2504");
			put("FX Line", "2501");
			put("HELOC", "2200");
			put("HELOC CMHC AGF Trust(Grandfathered)", "2200");
			put("Letter of Credit / Letter Guarantee", "2505");
			put("Line of Credit", "2502");
			put("Multi-Residential MG 1-4 Fix. Rate", "2503");
			put("Multi-Residential MG 1-4 Var. Rate", "2503");
			put("Multi-Residential MG 5 Pls Fix Rate", "2503");
			put("Multi-Residential MG 5 Pls Var Rate", "2503");
			put("Real Estate Mortgage Fix Rate", "2503");
			put("Real Estate Mortgage Var Rate", "2503");
			put("Visa", "2506");
		}
	};

	Map<String, String> productLimitType = new ConcurrentHashMap<String, String>() {
		{
			put("Multi-Residential MG 5 Pls Fix Rate", RepositoryPaths.SECURED);
			put("Multi-Residential MG 5 Pls Var Rate", RepositoryPaths.SECURED);
			put("Demand Loans", RepositoryPaths.SECURED);
			put("Commercial Term Loans Fixed Rate", RepositoryPaths.SECURED);
			put("Commercial Term Loans Var Rate", RepositoryPaths.SECURED);
			put("Construction loan", RepositoryPaths.SECURED);
			put("CSA Term Loan Fixed Rate", RepositoryPaths.SECURED);
			put("CSA Term Loan Variable Rate", RepositoryPaths.SECURED);
			put("Default Comm.Multi-Residential Prod", RepositoryPaths.SECURED);
			put("Real Estate Mortgage Fix Rate", RepositoryPaths.SECURED);
			put("Multi-Residential MG 1-4 Fix. Rate", RepositoryPaths.SECURED);
			put("Multi-Residential MG 1-4 Var. Rate", RepositoryPaths.SECURED);
			put("Real Estate Mortgage Var Rate", RepositoryPaths.SECURED);
			put("Investment Loan Fixed Rate", RepositoryPaths.SECURED);
			put("Investment Loan Variable Rate", RepositoryPaths.SECURED);
			put("RRSP Personal Loan Fixed Rate", RepositoryPaths.UNSECURED);
			put("RRSP Personal Loan Variable Rate", RepositoryPaths.UNSECURED);
			put("TFSA Loan Fixed Rate", RepositoryPaths.UNSECURED);
			put("TFSA Loan Variable Rate", RepositoryPaths.UNSECURED);
			put("Immigrant Investor Program-FED", RepositoryPaths.UNSECURED);
			put("Immigrant Investor Program-Prov", RepositoryPaths.UNSECURED);
			put("B2B Bank LOC - RRSP", RepositoryPaths.UNSECURED);
			put("Secured Line of Credit", RepositoryPaths.SECURED);
			put("B2B Bank LOC - Personal Secured", RepositoryPaths.SECURED);
			put("Unsecured Line of Credit", RepositoryPaths.UNSECURED);
			put("Mortgages AGF Trust (grandfathered)", RepositoryPaths.SECURED);
			put("Bridge Loan", RepositoryPaths.SECURED);
			put("Convertible Mortgage", RepositoryPaths.SECURED);
			put("Fixed Rate Closed Term Mortgage", RepositoryPaths.SECURED);
			put("Fixed Rate Open Term Mortgage", RepositoryPaths.SECURED);
			put("HELOC", RepositoryPaths.SECURED);
			put("HELOC CMHC AGF Trust(Grandfathered)", RepositoryPaths.SECURED);
			put("Real Estate Mortgage(grandfathered)", RepositoryPaths.SECURED);
			put("Var Rate Closed Term MG Fix Pay", RepositoryPaths.SECURED);
			put("Var Rate Closed Term Mrtg Var Pay", RepositoryPaths.SECURED);

		}
	};

	Map<String, String> productLimitFields = new ConcurrentHashMap<String, String>() {
		{
			put("Multi-Residential MG 5 Pls Fix Rate", securedSAELimitFields);
			put("Multi-Residential MG 5 Pls Var Rate", securedSAELimitFields);
			put("Demand Loans", securedSAELimitFields);
			put("Commercial Term Loans Fixed Rate", securedSAELimitFields);
			put("Commercial Term Loans Var Rate", securedSAELimitFields);
			put("Construction loan", securedSAELimitFields);
			put("CSA Term Loan Fixed Rate", securedSAELimitFields);
			put("CSA Term Loan Variable Rate", securedSAELimitFields);
			put("Default Comm.Multi-Residential Prod", securedSAELimitFields);
			put("Real Estate Mortgage Fix Rate", securedSAELimitFields);
			put("Multi-Residential MG 1-4 Fix. Rate", securedSAELimitFields);
			put("Multi-Residential MG 1-4 Var. Rate", securedSAELimitFields);
			put("Real Estate Mortgage Var Rate", securedSAELimitFields);
			put("Investment Loan Fixed Rate", securedLoanLimitFields);
			put("Investment Loan Variable Rate", securedLoanLimitFields);
			put("RRSP Personal Loan Fixed Rate", unsecuredLimitFields);
			put("RRSP Personal Loan Variable Rate", unsecuredLimitFields);
			put("TFSA Loan Fixed Rate", unsecuredLimitFields);
			put("TFSA Loan Variable Rate", unsecuredLimitFields);
			put("Immigrant Investor Program-FED", unsecuredLimitFields);
			put("Immigrant Investor Program-Prov", unsecuredLimitFields);
			put("B2B Bank LOC - RRSP", unsecuredLimitFields);
			put("Secured Line of Credit", securedLoanLimitFields);
			put("B2B Bank LOC - Personal Secured", securedLoanLimitFields);
			put("Unsecured Line of Credit", unsecuredLimitFields);
			put("Mortgages AGF Trust (grandfathered)", securedMortgageLimitFields);
			put("Bridge Loan", securedMortgageLimitFields);
			put("Convertible Mortgage", securedMortgageLimitFields);
			put("Fixed Rate Closed Term Mortgage", securedMortgageLimitFields);
			put("Fixed Rate Open Term Mortgage", securedMortgageLimitFields);
			put("HELOC", securedMortgageLimitFields);
			put("HELOC CMHC AGF Trust(Grandfathered)", securedMortgageLimitFields);
			put("Real Estate Mortgage(grandfathered)", securedMortgageLimitFields);
			put("Var Rate Closed Term MG Fix Pay", securedMortgageLimitFields);
			put("Var Rate Closed Term Mrtg Var Pay", securedMortgageLimitFields);

		}
	};

	Map<String, String> productLimitValues = new ConcurrentHashMap<String, String>() {
		{
			put("Multi-Residential MG 5 Pls Fix Rate", securedSAELimitValues);
			put("Multi-Residential MG 5 Pls Var Rate", securedSAELimitValues);
			put("Demand Loans", securedSAELimitValues);
			put("Commercial Term Loans Fixed Rate", securedSAELimitValues);
			put("Commercial Term Loans Var Rate", securedSAELimitValues);
			put("Construction loan", securedSAELimitValues);
			put("CSA Term Loan Fixed Rate", securedSAELimitValues);
			put("CSA Term Loan Variable Rate", securedSAELimitValues);
			put("Default Comm.Multi-Residential Prod", securedSAELimitValues);
			put("Real Estate Mortgage Fix Rate", securedSAELimitValues);
			put("Multi-Residential MG 1-4 Fix. Rate", securedSAELimitValues);
			put("Multi-Residential MG 1-4 Var. Rate", securedSAELimitValues);
			put("Real Estate Mortgage Var Rate", securedSAELimitValues);
			put("Investment Loan Fixed Rate", securedLoanLimitValues);
			put("Investment Loan Variable Rate", securedLoanLimitValues);
			put("RRSP Personal Loan Fixed Rate", unsecuredLimitValues);
			put("RRSP Personal Loan Variable Rate", unsecuredLimitValues);
			put("TFSA Loan Fixed Rate", unsecuredLimitValues);
			put("TFSA Loan Variable Rate", unsecuredLimitValues);
			put("Immigrant Investor Program-FED", unsecuredLimitValues);
			put("Immigrant Investor Program-Prov", unsecuredLimitValues);
			put("B2B Bank LOC - RRSP", unsecuredLimitValues);
			put("Secured Line of Credit", securedLoanLimitValues);
			put("B2B Bank LOC - Personal Secured", securedLoanLimitValues);
			put("Unsecured Line of Credit", unsecuredLimitValues);
			put("Mortgages AGF Trust (grandfathered)", securedMortgageLimitValues);
			put("Bridge Loan", securedMortgageLimitValues);
			put("Convertible Mortgage", securedMortgageLimitValues);
			put("Fixed Rate Closed Term Mortgage", securedMortgageLimitValues);
			put("Fixed Rate Open Term Mortgage", securedMortgageLimitValues);
			put("HELOC", securedMortgageLimitValues);
			put("HELOC CMHC AGF Trust(Grandfathered)", securedMortgageLimitValues);
			put("Real Estate Mortgage(grandfathered)", securedMortgageLimitValues);
			put("Var Rate Closed Term MG Fix Pay", securedMortgageLimitValues);
			put("Var Rate Closed Term Mrtg Var Pay", securedMortgageLimitValues);

		}
	};

	Map<String, String> findArrangementTableSummary = new ConcurrentHashMap<String, String>() {
		{
			put(RepositoryPaths.AUTHORISED + RepositoryPaths.COMMERCIAL_LOANS, "Find Lending Arrangements");
			put(RepositoryPaths.AUTHORISED + RepositoryPaths.PERSONAL_LOANS, "Find Lending Arrangements");
			put(RepositoryPaths.AUTHORISED + RepositoryPaths.PERSONAL_LOC, "Find Lending Arrangements");
			put(RepositoryPaths.AUTHORISED + RepositoryPaths.RETAIL_MORTGAGES, "Find Lending Arrangements");
			put(RepositoryPaths.AUTHORISED + RepositoryPaths.RETAIL_DEPOSITS, "Find Deposit Arrangements");
			put(RepositoryPaths.AUTHORISED + RepositoryPaths.BROKER_NONREG_DEPOSITS, "Find Deposit Arrangements");
			put(RepositoryPaths.AUTHORISED + RepositoryPaths.BROKER_REG_DEPOSITS, "Find Deposit Arrangements");
			put(RepositoryPaths.AUTHORISED + RepositoryPaths.AGENTS, "Agent Arrangement");
			put(RepositoryPaths.AUTHORISED + RepositoryPaths.PERSONAL_ACCOUNTS, "Find Accounts");
			put(RepositoryPaths.AUTHORISED + RepositoryPaths.BUSINESS_ACCOUNTS, "Find Accounts");
			put(RepositoryPaths.AUTHORISED + RepositoryPaths.SERVICING_ACCOUNTS, "Find Accounts");

			put(RepositoryPaths.UNAUTHORISED + RepositoryPaths.COMMERCIAL_LOANS, "Find Unauthorized Lending");
			put(RepositoryPaths.UNAUTHORISED + RepositoryPaths.PERSONAL_LOANS, "Find Unauthorized Lending");
			put(RepositoryPaths.UNAUTHORISED + RepositoryPaths.PERSONAL_LOC, "Find Unauthorized Lending");
			put(RepositoryPaths.UNAUTHORISED + RepositoryPaths.RETAIL_MORTGAGES, "Find Unauthorized Lending");
			put(RepositoryPaths.UNAUTHORISED + RepositoryPaths.RETAIL_DEPOSITS, "Find Unauthorized Deposits");
			put(RepositoryPaths.UNAUTHORISED + RepositoryPaths.BROKER_NONREG_DEPOSITS, "Find Unauthorized Deposits");
			put(RepositoryPaths.UNAUTHORISED + RepositoryPaths.BROKER_REG_DEPOSITS, "Find Unauthorized Deposits");
			put(RepositoryPaths.UNAUTHORISED + RepositoryPaths.AGENTS, "Unauthorized Agent Arrangements");
			put(RepositoryPaths.UNAUTHORISED + RepositoryPaths.PERSONAL_ACCOUNTS, "Find Unauthorized Accounts");
			put(RepositoryPaths.UNAUTHORISED + RepositoryPaths.BUSINESS_ACCOUNTS, "Find Unauthorized Accounts");
			put(RepositoryPaths.UNAUTHORISED + RepositoryPaths.SERVICING_ACCOUNTS, "Find Unauthorized Accounts");

			put(RepositoryPaths.PENDING + RepositoryPaths.COMMERCIAL_LOANS, "Find Pending Loans");
			put(RepositoryPaths.PENDING + RepositoryPaths.PERSONAL_LOANS, "Find Pending Loans");
			put(RepositoryPaths.PENDING + RepositoryPaths.PERSONAL_LOC, "Find Pending Loans");
			put(RepositoryPaths.PENDING + RepositoryPaths.RETAIL_MORTGAGES, "Find Pending Loans");
			put(RepositoryPaths.PENDING + RepositoryPaths.RETAIL_DEPOSITS, "Find Pending Deposits");
			put(RepositoryPaths.PENDING + RepositoryPaths.BROKER_NONREG_DEPOSITS, "Find Pending Deposits");
			put(RepositoryPaths.PENDING + RepositoryPaths.BROKER_REG_DEPOSITS, "Find Pending Deposits");
			put(RepositoryPaths.PENDING + RepositoryPaths.AGENTS, "Pending Agent Arrangements");
			put(RepositoryPaths.PENDING + RepositoryPaths.PERSONAL_ACCOUNTS, "Find Pending Accounts");
			put(RepositoryPaths.PENDING + RepositoryPaths.BUSINESS_ACCOUNTS, "Find Pending Accounts");
			put(RepositoryPaths.PENDING + RepositoryPaths.SERVICING_ACCOUNTS, "Find Pending Accounts");

			put(RepositoryPaths.NEW_OFFERS + RepositoryPaths.COMMERCIAL_LOANS, "Find Lending Offers");
			put(RepositoryPaths.NEW_OFFERS + RepositoryPaths.PERSONAL_LOANS, "Find Lending Offers");
			put(RepositoryPaths.NEW_OFFERS + RepositoryPaths.PERSONAL_LOC, "Find Lending Offers");
			put(RepositoryPaths.NEW_OFFERS + RepositoryPaths.RETAIL_MORTGAGES, "Find Lending Offers");
			put(RepositoryPaths.NEW_OFFERS + RepositoryPaths.RETAIL_DEPOSITS, "Find Deposit Offers");
			put(RepositoryPaths.NEW_OFFERS + RepositoryPaths.BROKER_NONREG_DEPOSITS, "Find Deposit Offers");
			put(RepositoryPaths.NEW_OFFERS + RepositoryPaths.BROKER_REG_DEPOSITS, "Find Deposit Offers");
			put(RepositoryPaths.NEW_OFFERS + RepositoryPaths.AGENTS, "");
			put(RepositoryPaths.NEW_OFFERS + RepositoryPaths.PERSONAL_ACCOUNTS, "Find Account Offers");
			put(RepositoryPaths.NEW_OFFERS + RepositoryPaths.BUSINESS_ACCOUNTS, "Find Account Offers");
			put(RepositoryPaths.NEW_OFFERS + RepositoryPaths.SERVICING_ACCOUNTS, "Find Account Offers");

			put(RepositoryPaths.MATURED_CLOSED + RepositoryPaths.COMMERCIAL_LOANS, "Find Matured/Closed Loans");
			put(RepositoryPaths.MATURED_CLOSED + RepositoryPaths.PERSONAL_LOANS, "Find Matured/Closed Loans");
			put(RepositoryPaths.MATURED_CLOSED + RepositoryPaths.PERSONAL_LOC, "Find Matured/Closed Loans");
			put(RepositoryPaths.MATURED_CLOSED + RepositoryPaths.RETAIL_MORTGAGES, "Find Matured/Closed Loans");
			put(RepositoryPaths.MATURED_CLOSED + RepositoryPaths.RETAIL_DEPOSITS, "Find Matured/Closed Deposits");
			put(RepositoryPaths.MATURED_CLOSED + RepositoryPaths.BROKER_NONREG_DEPOSITS,
					"Find Matured/Closed Deposits");
			put(RepositoryPaths.MATURED_CLOSED + RepositoryPaths.BROKER_REG_DEPOSITS, "Find Matured/Closed Deposits");
			put(RepositoryPaths.MATURED_CLOSED + RepositoryPaths.AGENTS, "Find Matured/Closed Arrangements");
			put(RepositoryPaths.MATURED_CLOSED + RepositoryPaths.PERSONAL_ACCOUNTS, "Find Matured/Closed Accounts");
			put(RepositoryPaths.MATURED_CLOSED + RepositoryPaths.BUSINESS_ACCOUNTS, "Find Matured/Closed Accounts");
			put(RepositoryPaths.MATURED_CLOSED + RepositoryPaths.SERVICING_ACCOUNTS, "Find Matured/Closed Accounts");
		}
	};

	Map<String, String> commandLineEntries = new ConcurrentHashMap<String, String>() {
		{

			put("COLLATERAL,LBC.B2BPPSA",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Loans and Mortgages &&& RETAILHOMEPAGE &&& Collateral > Collateral - Invest. loans");
			put("CUSTOMER,LBC.CAMB.INPUT2.CIF I F3",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Accounts and Deposits &&& RETAILHOMEPAGE &&& Customer > Create Client (Personal)");
			put("CUSTOMER,LBC.CAMB.CORP1 I F3",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Accounts and Deposits &&& RETAILHOMEPAGE &&& Customer > Create Client (Business)");
			put("CUSTOMER,LBC.AGENT I F3",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Combined RBHP &&& RETAILHOMEPAGE &&& Dealer and Advisor > Create Dealer or Advisor");
			put("BENEFICIARY,CAMB",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Accounts and Deposits &&& RETAILHOMEPAGE &&& Accounts &&& Account Transactions > Create Beneficiary");
			put("FUNDS.TRANSFER,AA.ACDI I F3",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Loans and Mortgages &&& RETAILHOMEPAGE &&& Payments > AA Disbursement ");
			put("FUNDS.TRANSFER,AA.ACRP I F3",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Loans and Mortgages &&& RETAILHOMEPAGE &&& Payments > AA Repayment ");
			put("FUNDS.TRANSFER,AA.ACAR I F3",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Loans and Mortgages &&& RETAILHOMEPAGE &&& Payments > AA Credit Arrangement  ");
			put("FUNDS.TRANSFER,LBC.AA.ACPO I F3",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Loans and Mortgages &&& RETAILHOMEPAGE &&& Payments > AA Pay-Off ");
			put("FUNDS.TRANSFER,AA.ACPD I F3",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Loans and Mortgages &&& RETAILHOMEPAGE &&& Payments > AA Principal Decrease ");
			put("FUNDS.TRANSFER,AA.ACDF I F3",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Accounts and Deposits &&& RETAILHOMEPAGE &&& Deposits &&& Deposit Transactions > Deposit - Fund");
			put("FUNDS.TRANSFER,AA.ACDP I F3",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Accounts and Deposits &&& RETAILHOMEPAGE &&& Deposits &&& Deposit Transactions > Deposit Pay-out");
			put("FUNDS.TRANSFER,AA.ACPP I F3",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Accounts and Deposits &&& RETAILHOMEPAGE &&& Deposits &&& Deposit Transactions > Deposit Partial Withdrawal");
			put("FUNDS.TRANSFER,AA.ACCR I F3",
					"Standard Model Bank Menu > MB User Menu > Retail Operations> Loan Transactions>Arrangement Activities (FT) > AA Credit Arrangement ");
			put("ENQ CUSTOMER.SCV", "Standard Model Bank Menu > MB User Menu > Retail Operations> Find Customer");
			put("ENQ CO.001",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Loans and Mortgages &&& RETAILHOMEPAGE &&& Collateral > Main Collateral Enquiry");
			put("ENQ COLLAT.EXPIRY",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Loans and Mortgages &&& RETAILHOMEPAGE &&& Collateral > Collateral Expiry");
			put("COLLATERAL.RIGHT,INP",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Loans and Mortgages &&& RETAILHOMEPAGE &&& Collateral > Collateral Link");
			put("COLLATERAL,INP",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Loans and Mortgages &&& RETAILHOMEPAGE &&& Collateral > Collateral");
			put("CARD.ACCESS,CAMB.UPD.ACCESS.OL",
					"CAMB Bank Menu> CAMB User Menu> Customer > Card Ordering and Maintenance > Manage Active Cards > View or Modify Access & Limits");
			put("COS AA.FIND.ARRANGEMENT.AR",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Accounts and Deposits &&& RETAILHOMEPAGE &&& Accounts > Find Accounts");
			put("COS AA.FIND.ARRANGEMENT.AD",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Accounts and Deposits &&& RETAILHOMEPAGE &&& Deposits > Find Deposits");
			put("COS AA.FIND.ARRANGEMENT.AL",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Loans and Mortgages &&& RETAILHOMEPAGE &&& Loans and Mortgages > Find Loans");
			put("COS AA.FIND.ARRANGEMENT.AG",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Combined RBHP &&& RETAILHOMEPAGE &&& Dealer and Advisor > Find Agent Arrangement ");
			put("COS CUST.POST.RESTRICT",
					"Standard Model Bank Menu > MB User Menu > Customer > Set/Remove Posting Restrict");
			put("COS CUST.POST.RESTRICT.AUTH",
					"Standard Model Bank Menu > MB User Menu > Customer > Authorise/Delete Posting Restrict ");
			put("COS LIMIT.AUTH",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Loans and Mortgages &&& RETAILHOMEPAGE &&& Limit > Authorisation ");
			put("TAB CREATE.LIMIT.CO",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Loans and Mortgages &&& RETAILHOMEPAGE &&& Limit > Create Limit ");
			put("COS LBC.LIMIT.AMEND",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Loans and Mortgages &&& RETAILHOMEPAGE &&& Limit > Amend Reverse Limit ");
			put("COS CUST.AMEND",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Accounts and Deposits &&& RETAILHOMEPAGE &&& Customer > Amend Client Profile");
			put("COS AA.PRODUCT.DESIGNER-PRODUCTS",
					"Standard Model Bank Menu > MB Admin Menu > Product Builder > Products");
			put("COS AA.PRODUCT.DESIGNER-PRODUCT.CONDITIONS",
					"Standard Model Bank Menu > MB Admin Menu > Product Builder > Product Conditions");
			put("COS ADMIN.POSTING.RESTRICT",
					" Standard Model Bank Menu > MB Admin Menu > Framework Parameter > Customer > Posting Restrict");
			put("COS TXN.LIST", "Standard Model Bank Menu > MB Admin Menu > Framework Parameter > Core > Transaction");
			put("COS OUTWARD.REMITTANCES",
					"Standard Model Bank Menu > MB User Menu > Payment Services >Funds Transfer> Outward Remittances");
			put("COS INWARD.REMITTANCES",
					"Standard Model Bank Menu > MB User Menu > Payment Services >Funds Transfer> Inward Remittances");
			put("COS ACCOUNT.TRANSFER",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Loans and Mortgages &&& RETAILHOMEPAGE &&& Payments > Account Transfer (using FT) ");
			put("COS AA.PRODUCT.CATALOG.RETAIL",
					"CAMB Bank Menu > LBCB2B Role Based Pages > B2B Home Page - Accounts and Deposits &&& FRAMETABCSM &&& #Product Catalog");
			put("ENQ CAMB.CUST.SRC.CIF",
					"CAMB Bank Menu > LBCB2B Role Based Pages > B2B Home Page - Accounts and Deposits &&& FRAMETABCSM &&& #Search Customer");
			put("PAYMENT.ORDER,CAMB.EFT.ACH",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Loans and Mortgages &&& RETAILHOMEPAGE &&& Payments > Payment Order EFT/CHQ ");
			put("CARD.ISSUE,CAMB.ORDER.MAIL.OL I MEMC.NEW",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Accounts and Deposits &&& RETAILHOMEPAGE &&& Accounts &&& Order / Issue Card > Order Personal Card (Mail)");
			put("CAPL.H.PAP.DD.DDI,CAMB.CREDIT",
					"CAMB Bank Menu > CAMB User Menu > Payment Services > EFT > PAP User Menu > PAP Credit");
			put("COS LBC.CUST.ADDRESS",
					"CAMB Bank Menu > LBCB2B Role Based Pages > B2B Home Page - Accounts and Deposits &&& RETAILHOMEPAGE &&& Customer > Add/Amend View Print Address");
			put("COS LBC.CUST.ADDRESS",
					"CAMB Bank Menu > LBCB2B Role Based Pages > B2B Home Page - Loans and Mortgages &&& RETAILHOMEPAGE &&& Customer > Additional Address of Customer");
			put("DE.ADDRESS,LBC.CAMB.ADD21",
					"CAMB Bank Menu > LBCB2B Role Based Pages > B2B Home Page - Loans and Mortgages &&& RETAILHOMEPAGE &&& Customer > Amend Address");
			put("FUNDS.TRANSFER,LBC.AA.ACCR I F3",
					"CAMB Bank Menu > LBCB2B Role Based Pages > B2B Home Page - Loans and Mortgages &&& RETAILHOMEPAGE &&& Payments > Credit UNC Balance");
			put("FUNDS.TRANSFER,LBC.ACUN",
					"CAMB Bank Menu > LBCB2B Role Based Pages > B2B Home Page - Loans and Mortgages &&& RETAILHOMEPAGE &&& Payments > Debit UNC Balance");
			put("FUNDS.TRANSFER,LBC.AA.ACBY",
					"CAMB Bank Menu > LBCB2B Role Based Pages > SAE Loans and Mortgages &&& RETAILHOMEPAGECSM &&& SAE Loans and Mortgages > Loan Financial Transactions > Apply Adhoc Charges");
			put("ENQ LBC.LOAN.LIST.ENQ",
					"CAMB Bank Menu > LBCB2B Role Based Pages > B2B Home Page - Loans and Mortgages &&& RETAILHOMEPAGE &&& Discharge Request > List current lending products");
			put("ENQ LBC.EFT.DAILY.OUTFILE",
					"CAMB Bank Menu > LBCB2B Role Based Pages > B2B Home Page - Finance &&& FINANCEMANAGERMENU &&& Enquiries > Daily EFT clearing Outfile");
			put("ENQ LBC.EFT.DAILY.OUTFILE",
					"CAMB Bank Menu > LBCB2B Role Based Pages > B2B Home Page - Finance &&& FINANCEMANAGERMENU &&& Enquiries > Account/Internal Account > Entries from Last Statement");
			put("TELLER.FINANCIAL.SERVICES,CAMB.INPUT2",
					"CAMB Bank Menu > LBCB2B Role Based Pages > B2B Home Page - Finance &&& FINANCEMANAGERMENU &&& Cash Management > TFS > Teller Financial Services");
			put("COS ESCROW.PAYEE",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Combined RBHP &&& RETAILHOMEPAGE &&& Loans and Mortgages > Escrow Processing > Escrow User Menu > Escrow Payee > Find Escrow Payee");
			put("ESCROW.PAYEE,INPUT I",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Combined RBHP &&& RETAILHOMEPAGE &&& Loans and Mortgages > Escrow Processing > Escrow User Menu > Escrow Payee > New Escrow Payee");
			put("EB.LBC.CRS.DATA,LBC.INDIVIDUAL",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Combined RBHP &&& RETAILHOMEPAGE &&& Customer > CRS data - Individual Client");
			put("TAB RETAIL.HP.TAB.BA",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Combined RBHP &&& RETAILHOMEPAGE &&& Dealer and Advisor > Agent Product Setup");
			put("AC.CASH.POOL,CAMB.SWEEPS",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Accounts and Deposits &&& RETAILHOMEPAGE &&& Accounts &&& Account Transactions > Create/Amend Coverdraft");
			put("STANDING.ORDER,FIXAMTSAE",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Accounts and Deposits &&& RETAILHOMEPAGE &&& Accounts &&& Account Transactions > Create Fixed Transfer STO");
			put("CAPL.H.PAP.DD.DDI,CAMB.CREDIT",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Accounts and Deposits &&& RETAILHOMEPAGE &&& Accounts &&& Account Transactions > PAP Credit");
			put("ENQ LBC.AGENT.SCV",
					"CAMB Bank Menu > LBCB2B Role Based Pages > B2B Home Page - Combined RBHP &&& FRAMETABCSM &&& #Search Agent");
			put("CUSTOMER,LBC.AGENT",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Combined RBHP &&& RETAILHOMEPAGE &&& Dealer and Advisor > Amend Dealer or Advisor");
			put("FUNDS.TRANSFER,LBC.ACTR.FTHP.3 I F3",
					"CAMB Bank Menu > LBCB2B Role Based Pages > B2B Home Page - Accounts and Deposits &&& RETAILHOMEPAGE &&& Accounts &&& Account Transactions > Remote/Cheque Deposit");
			put("FUNDS.TRANSFER,ACTR.FTHP I F3",
					"CAMB Bank Menu > LBCB2B Role Based Pages > B2B Home Page - Accounts and Deposits &&& RETAILHOMEPAGE &&& Accounts &&& Account Transactions > Account Transfer");
			put("COLLATERAL,LBC.INP.MACHINE",
					"CAMB Bank Menu > LBCB2B Role Based Pages > SAE Loans and Mortgages &&& RETAILHOMEPAGECSM &&& Collateral > Collateral - Machinery");
			put("COLLATERAL,LBC.INP.INVEST",
					"CAMB Bank Menu > LBCB2B Role Based Pages > SAE Loans and Mortgages &&& RETAILHOMEPAGECSM &&& Collateral >  Collateral - Investment");
			put("COLLATERAL,LBC.INP.REALEST",
					"CAMB Bank Menu > LBCB2B Role Based Pages > SAE Loans and Mortgages &&& RETAILHOMEPAGECSM &&& Collateral >  Collateral - Real Estate");
			put("ENQ TFS.NAU",
					"CAMB Bank Menu > LBCB2B Role Based Pages > B2B Home Page - Finance &&& FINANCEMANAGERMENU &&& Enquiries > Cash Management > Authorized / Delete TFS Transactions");
			put("STANDING.ORDER,LBC.ACHCREDIT.FIXAMT",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Loans and Mortgages &&& RETAILHOMEPAGE &&& Lines of Credit > LOC Transaction > Create/Maintain Fixed Loan Repayment STO");
			put("DE.PRODUCT,INPUT",
					"CAMB Bank Menu> LBCB2B Role Based Pages > B2B Home Page - Accounts and Deposits &&& RETAILHOMEPAGE &&& Customer > Delivery Product");
			put("EB.COMPANY.CHANGE",
					"CAMB Bank Menu > LBCB2B Role Based Pages > B2B Home Page - SAE Loans and Mortgages &&& RETAILHOMEPAGECSM &&& Branch Modification");
			put("FUNDS.TRANSFER,ESCROW.ADHOC.DEPOSIT I F3",
					"CAMB Bank Menu > LBCB2B Role Based Pages > SAE Loans and Mortgages &&& RETAILHOMEPAGECSM &&& Escrow Processing > Escrow User Menu > Escrow Operations > Escrow Transactions(FT) > Escrow Deposit(FT)");
			put("FUNDS.TRANSFER,ESCROW.ADHOC.WITHDRAW I F3",
					"CAMB Bank Menu > LBCB2B Role Based Pages > SAE Loans and Mortgages &&& RETAILHOMEPAGECSM &&& Escrow Processing > Escrow User Menu > Escrow Operations > Escrow Transactions(FT) > Escrow Withdrawal(FT)");
			put("DEP.H.EXT.TFR.PARAM,CAMB",
					"CAMB Bank Menu > CAMB Admin Menu > Payment Services > EFT/PAP Admin Menu > External Transfer Parameter");
			put("BENEFICIARY,CAMB.EFT.CIF",
					"CAMB Bank Menu > LBCB2B Role Based Pages > SAE Loans and Mortgages &&& RETAILHOMEPAGECSM &&& SAE Loans and Mortgage > Loan Financial Transactions > Create Beneficiary > EFT Beneficiary Client");
			put("BENEFICIARY,CAMB.EFT.NOCIF",
					"CAMB Bank Menu > LBCB2B Role Based Pages > SAE Loans and Mortgages &&& RETAILHOMEPAGECSM &&& SAE Loans and Mortgage > Loan Financial Transactions > Create Beneficiary > EFT Beneficiary Non-Client");
			put("BENEFICIARY,CAMB.CHQ.NOCIF",
					"CAMB Bank Menu > LBCB2B Role Based Pages > SAE Loans and Mortgages &&& RETAILHOMEPAGECSM &&& SAE Loans and Mortgage > Loan Financial Transactions > Create Beneficiary > CHQ Beneficiary Non-Client");
			put("FUNDS.TRANSFER,AA.ACPP I F3",
					"CAMB Bank Menu > LBCB2B Role Based Pages > B2B Home Page - Accounts and Deposits &&& RETAILHOMEPAGE &&& Deposits &&& Deposit Transactions > Deposit Partial Redemption - No Clawback");
			put("CAMB.H.SL.DETAILS,LBC.CAMB",
					"CAMB Bank Menu > LBCB2B Role Based Pages > SAE Loans and Mortgages &&& RETAILHOMEPAGECSM &&& SAE Loans and Mortgage > Syndication Loans > Syndication Details");
			put("CAMB.H.SL.DETAILS,CAMB.CHANGE",
					"CAMB Bank Menu > LBCB2B Role Based Pages > SAE Loans and Mortgages &&& RETAILHOMEPAGECSM &&& SAE Loans and Mortgage > Syndication Loans > SL Details for Part.Change and Closure");
			put("CAMB.H.SL.DETAILS,CAMB.MAT",
					"CAMB Bank Menu > LBCB2B Role Based Pages > SAE Loans and Mortgages &&& RETAILHOMEPAGECSM &&& SAE Loans and Mortgage > Syndication Loans > SL Loan Renewal");
			put("ENQ CUSTOMER.DETS.SCV",
					"CAMB Bank Menu > LBCB2B Role Based Pages > B2B Home Page - SAE Loans and Mortgages &&& RETAILHOMEPAGECSM &&& Customer > Enquiries > Client Details");
			put("ENQ CUSTOMER.LIST",
					"CAMB Bank Menu > LBCB2B Role Based Pages > B2B Home Page - SAE Loans and Mortgages &&& RETAILHOMEPAGECSM &&& Customer > Enquiries > Client List");
			put("COS ESCROW.IF.EXCEPTION",
					"CAMB Bank Menu > CAMB Bank Menu > LBCB2B Role Based Pages > SAE Loans and Mortgages &&& RETAILHOMEPAGECSM &&& Escrow Processing > Escrow Process Tax Files > Escrow File Exceptions");
			put("COS ESCROW.IF.MATCHED",
					"CAMB Bank Menu > LBCB2B Role Based Pages > SAE Loans and Mortgages &&& RETAILHOMEPAGECSM &&& Escrow Processing > Escrow Process Tax Files > Escrow Warehouse");
			put("ENQ ESCROW.MATCHED.RETURNED.LBC",
					"CAMB Bank Menu > LBCB2B Role Based Pages > SAE Loans and Mortgages &&& RETAILHOMEPAGECSM &&& Escrow Processing > Escrow Process Tax Files > Escrow Matched and Returned");
			put("LBC.H.DISCHARGE.REQUEST,LBC.MODIFY",
					"CAMB Bank Menu > LBCB2B Role Based Pages > B2B Home Page - Loans and Mortgages &&& RETAILHOMEPAGE &&& Discharge Request > Modify Freeze Flag");
			put("ENQ LBC.NPL.NAB.HISTORY.DETAIL",
					"CAMB Bank Menu > LBCB2B Role Based Pages > B2B Home Page - Loans and Mortgages &&& RETAILHOMEPAGE &&& Loans and Mortgages > NPL Propagation > NPL and NAB History Detail");
			put("ENQ LBC.DISCHARGE.LIST.ENQ",
					"CAMB Bank Menu > LBCB2B Role Based Pages > B2B Home Page - Loans and Mortgages &&& RETAILHOMEPAGE &&& Discharge Request > Discharge request list");
			put("ENQ EB.CONTRACT.BALANCES.BALANCE",
					"CAMB Bank Menu > LBCB2B Role Based Pages > B2B Home Page - Loans and Mortgages &&& RETAILHOMEPAGE &&& Enquiries > Contract-wise Balances");
			put("STANDING.ORDER,LBC.ACHCREDIT.FIXAMT",
					"CAMB Bank Menu > LBCB2B Role Based Pages > B2B Home Page - Loans and Mortgages &&& RETAILHOMEPAGE &&& Lines of Credit &&& LOC Transaction > Create/Maintain Fixed Loan Repayment STO ");
			put("STANDING.ORDER,LBC.ACHCREDIT.INTONLY",
					"CAMB Bank Menu > LBCB2B Role Based Pages > B2B Home Page - Loans and Mortgages &&& RETAILHOMEPAGE &&& Lines of Credit &&& LOC Transaction > Create/Maintain Interest Loan Repayment STO ");
			put("ENQ LBC.ASSUMED.LOANS",
					"CAMB Bank Menu > LBCB2B Role Based Pages > SAE Loans and Mortgages &&& RETAILHOMEPAGECSM &&& Enquiries > Assumed Loans");
			put("TELLER.FINANCIAL.SERVICES,CAMB.INPUT2",
					"ROLEBASED_LENDING &&& ProperFrame &&& Loans and Mortgages > TFS > Teller Financial Services");
			put("LBC.H.PAYOUT.REQUEST,LBC.DISH.COLL.INPUT",
					"ROLEBASED_LENDING &&& ProperFrame &&& Payout Request > Mark discharge collateral flag");
		}
	};

	Map<String, String> restrictionCodes = new ConcurrentHashMap<String, String>() {
		{
			put("1", "Post No Debits - CREDIT");
			put("2", "Post No Credits - DEBIT");
			put("3", "Post No Entries - ALL");
			put("4", "Refer Credits to Supervisor - CREDIT");
			put("5", "Refer debits to Supervisor - DEBIT");
			put("6", "Refer all entries to Supervisor - ALL");
			put("7", "Account on Referal List - All");
			put("8", "Account on Referal List - CR - CREDIT");
			put("9", "Account on Referal List - DR - DEBIT");
			put("10", "Post Credits to Savings a/c - CREDIT");
			put("11", "Post Debit to current a/c - DEBIT");
			put("12", "Customer Deceased - ALL");
			put("13", "Moratorium - ALL");
			put("14", "Bankrupcy - ALL");
			put("15", "CRA Requirement - DEBIT");
			put("16", "Consumer Proposal - ALL");
			put("17", "Deceased Client - DEBIT");
			put("18", "Legal action against client - DEBIT");
			put("19", "Do not contact advisor/client - DEBIT");
			put("80", "Pending Closure - ALL");
			put("90", "Automatic Closing - ALL");
		}
	};

	Map<String, String> internalAccountForBranch = new ConcurrentHashMap<String, String>() {
		{
			put("B2B Branch 623", "CAD1100300017623");
			put("B2B Branch 817", "CAD1011000017817");
			put("B2B Branch 607", "CAD1011000017607");
			put("Branch 603", "CAD1100300011603");
			put("Branch 247", "CAD1160300011247");
			put("LAURENTIAN BANK - 523", "CAD1011000011523");
			put("LAURENTIAN BANK - 526", "CAD1011000011526");

		}
	};

}
