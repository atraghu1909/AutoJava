package testLibs;

import java.util.concurrent.TimeUnit;

public interface RepositoryPaths {

	// Setting paths in BaseTest class
	String log4jPath = "./config/Log4j.properties";
	String outputDirectory = "./testReport/results";
	String chromeDriverPropPath = "./testLibrary/chromedriver_win32/chromedriver.exe";
	String fireFoxDriverPropPath = "./TestLibrary/firefox/firefox.exe";
	String ieDriverPropPath = "./testLibrary/IEDriverServer_Win32_2.53.0/IEDriverServer.exe";
	String edgeDriverPropPath = "./testLibrary/edge/MicrosoftWebDriver.exe";

	// Set timeout in RepositoryElements class
	int elementWaitTimeout = 15;
	int elementPolling = 30;
	TimeUnit elementTimeUnit = TimeUnit.MILLISECONDS;

	// Set ALM properties
	String almAddress = "https://alm.deloitte.com/qcbin";
	String almDomain = "LAURENTIAN_BANK";
	String almProject = "AILOUETTE";
	String defaultTestSetPath = "B2B Automation\\Release 3\\OOB";
	String defaultTestSetName = "OOB Tests";
	String almUsername = "automation_cloudpipe.ca";
	String almPassword = "P@ssw0rd#6";
	int defaultTestSetNumber = 704;

	// Page Class paths to repository files
	String loginPageProperties = "./config/TEMENOS_LoginPage.properties";
	String homePageProperties = "./config/TEMENOS_HomePage.properties";
	String listTableProperties = "./config/TEMENOS_ListTable.properties";
	String inputTableProperties = "./config/TEMENOS_InputTable.properties";
	String readTableProperties = "./config/TEMENOS_ReadTable.properties";
	String compositeScreenProperties = "./config/TEMENOS_CompositeScreen.properties";
	String tabbedScreenProperties = "./config/TEMENOS_TabbedScreen.properties";
	String toolbarProperties = "./config/TEMENOS_Toolbars.properties";
	String toolProperties = "./config/TEMENOS_Tools.properties";
	String enquiryProperties = "./config/TEMENOS_Enquiry.properties";
	String mainMenuProperties = "./config/TEMENOS_MainMenu.properties";
	String subMenuProperties = "./config/TEMENOS_SubMenu.properties";
	String versionScreenProperties = "./config/TEMENOS_VersionScreen.properties";

	// Paths to TAABS Sheets Excel files
	String dynamicResourcesPath = "./testData/resources/varFileName.xls";
	String dynamicResultReportPath = "./testData/resultReport/varFileName.xlsx";

	// Paths to OOB Simple Check Excel Files
	String coreMenuFile = "./testData/resources/Core Menu v5_06Apr2017.xlsx.";
	String cambMenuFile = "./testData/resources/CAMB Menu v4_06Apr2017.xlsx.";
	String oobTestDataFile = "./testData/resources/OOB Test Data v1_20Apr2017.xlsx.";

	// Environment Variables
	String sit1EnvURL = "http://10.155.16.21:9081/t24Sit1/servlet/BrowserServlet";
	String trainingEnvURL = "http://10.155.16.31:9084/t24Train/servlet/BrowserServlet";
	String devEnvURL = "http://10.155.16.31:9088/t24Dev/servlet/BrowserServlet";
	String easeEnvURL = "https://uat.ease.b2bbank.com/ease/login.do";
	String preProdEnvURL = "http://10.155.20.200:9080/t24Preprod/servlet/BrowserServlet";
	String sit2EnvURL = "http://10.155.16.22:9081/t24Sit2/servlet/BrowserServlet";
	String mg2EnvURL = "http://10.155.16.29:9084/t24Mg2/servlet/BrowserServlet";

	// Commonly used strings
	String ACTIVITY = "Activity";
	String ACTIVITY_LOG = "Activity Log";
	String ADDITIONAL_DETAILS = "Additional Details";
	String ADDRESS = "Customer Address";
	String AGENTS = "Broker Agent";
	String AMEND = "Amend";
	String AMEND_AUTHORISE = "Amend and Authorise";
	String ADHOC_CHEQUE = "AdHocPayment via Cheque";
	String ADHOC_EFT = "AdHocPayment via EFT";
	String ARRANGEMENT = "Arrangement";
	String ARRANGEMENT_AUTHORIZATION = "Arrangement Authorization";
	String AUTHORISE_DEAL = "Authorises a deal";
	String AUTHORISE = "Authorise";
	String AUTHORISED = "Authorised";
	String B2B_BANK_CHEQUING_ACCOUNT = "B2B Bank Chequing Account";
	String BENEFICIARY = "Beneficiary";
	String BROKER_NONREG_DEPOSITS = "Broker - Non Registered Deposits";
	String BROKER_REG_DEPOSITS = "Broker - Registered Deposits";
	String BUSINESS = "Non Personal";
	String BUSINESS_ACCOUNTS = "Business Accounts";
	String CAD = "CAD";
	String CHEQUE = "Cheque";
	String CIF = "CIF";
	String CREATE = "Create";
	String CREATE_AUTHORISE = "Create and Authorise";
	String CORRECT = "Correct";
	String CORRECT_AUTHORISE = "Correct and Authorise";
	String COLLATERAL = "Collateral";
	String COLLATERAL_DETAILS = "Collateral Details";
	String COLLATERAL_DETAILS_INVESTMENT = "Collateral Details - Investment";
	String COLLATERAL_DETAILS_MACHINERY = "Collateral Details - Machinery";
	String COLLATERAL_DETAILS_REAL_ESTATE = "Collateral Details - Real Estate";
	String COLLATERAL_DETAILS_LOANS = "Collateral Details - Loans";
	String COLLATERAL_LINK = "Collateral Link";
	String COMMERCIAL_LOANS = "Commercial Loans";
	String COMMIT_DEAL = "Commit the deal";
	String COMPLETE = "Complete";
	String COMPLETE_AUTHORISE = "Complete And Authorise";
	String CURRENCY = "Currency";
	String CUSTOMER = "Customer";
	String DEALER_ADVISOR = "Dealer or Advisor";
	String DELETE_CORRECTION = "Delete Correction";
	String DELETE_CORRECTION_AUTHORISE = "Delete Correction and Authorise";
	String DELETE_DEAL = "Deletes a Deal";
	String DROPDOWN = "dropDown";
	String EDIT = "Edit";
	String EDIT_CONTRACT = "Edit a contract";
	String EFT = "EFT";
	String ENQ_NO_RESULTS = "No Data to display";
	String ERROR = "Error";
	String ESCROW_ACCOUNT = "Escrow Account";
	String ESCROW_PAYEE = "Escrow Payee";
	String FACILITIES = "Facilities";
	String FINANCIAL = "Financial";
	String FIX_RATE_LOAN = "Fix";
	String FUNDSTRANSFER = "FundsTransfer";
	String FUNDSTRANSFER_TFS = "FundsTransfer TFS";
	String HISA_BUSINESS = "HISA Business";
	String HISA_PERSONAL = "High Interest Savings Account";
	String HELOC = "HELOC";
	String HOLD = "Place a contract on Hold";
	String HREF = "href";
	String ID = "id";
	String LBC_BROKER_AGENT = "LBC Broker Agent";
	String LENDING_DISBURSEMENT = "Lending Disbursement";
	String LIMIT = "Limit";
	String LIMIT_SAE = "Limit SAE";
	String LOAN_OVERVIEW = "Loan Overview";
	String MATURED_CLOSED = "Matured / Closed";
	String MUNICIPALITY = "Municipality";
	String MAXIMIZE = "Maximize";
	String NEW_OFFERS = "New Offers";
	String NO_OVERRIDE = "No Override Button present";
	String NO_ACTIVITY = "No Activity present";
	String NON_CLIENT_PERSONAL = "Non-Client (Personal)";
	String NON_CLIENT_BUSINESS = "Non-Client (Business)";
	String OPEN = "Open";
	String OVERVIEW = "Overview";
	String OWNER = "Owner";
	String PENDING = "Pending";
	String PERSONAL = "Personal";
	String PERSONAL_ACCOUNTS = "Personal Accounts";
	String PERSONAL_CHEQUING = "Chequing Account";
	String PERSONAL_LOANS = "Personal Loans";
	String PERSONAL_LOC = "Personal Lines of Credit";
	String POSTING_RESTRICT = "Posting Restrict";
	String PRINT = "Print";
	String PRODUCT = "Product";
	String PRODUCT_TRANSIT_CHANGE = "Product Transit Change";
	String RESERVE_ACCOUNTS = "Reserve Account";
	String RETAIL_DEPOSITS = "Retail - Non Registered Deposits";
	String RETAIL_MORTGAGES = "Retail Mortgages";
	String RETURN_TO_SCREEN = "Return to application screen";
	String REVERSE = "Reverse";
	String REVERSE_AUTHORISE = "Reverse and Authorise";
	String REVERSE_DEAL = "Reverses a deal from the live file";
	String ROLEBASED_BANKING = "Accounts and Deposits";
	String ROLEBASED_LENDING = "Loans and Mortgages";
	String ROLEBASED_OR = "Combined RBHP";
	String ROLEBASED_SAE = "SAE Loans and Mortgages";
	String SECURED = "Secured";
	String SECURED_CHILD = "Secured Child";
	String SERVICING_ACCOUNTS = "Servicing Accounts";
	String SELECTION_SCREEN = "Selection Screen";
	String SIMULATE = "Simulate";
	String STANDING_ORDER = "Standing Order";
	String SWITCH_TO_DEFAULT = "Switch to Default";
	String SWITCH_TO_PARENT = "Switch to Parent";
	String TEXTFIELD = "textField";
	String TRANSFER_ACCOUNTS = "Transfer Between Accounts";
	String TXN_COMPLETE = "Txn Complete";
	String UNAUTHORISED = "Unauthorised";
	String UNSECURED = "Unsecured";
	String VALIDATE = "Validate";
	String VALIDATE_DEAL = "Validate a deal";
	String VAR_RATE_LOAN = "Var Loan";
	String VAR_RATE_MRTG = "Var Mortgage";
	String VIEW = "View";
	String VIEW_CONTRACT = "View a contract";
	String VIEW_AUTHORISED = "View Authorized";
	String VIEW_AUTHORISED_FINANCIAL = "View Authorized Financial";
	String VIEW_PENDING_AUTHORISED = "View Pending Authorization";
	String WRENCH = "Perform an action on the contract";

}