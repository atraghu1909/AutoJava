package testLibs;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestRunner;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.asserts.SoftAssert;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import atu.alm.wrapper.ALMServiceWrapper;
import atu.alm.wrapper.ITestCase;
import atu.alm.wrapper.ITestCaseRun;
import atu.alm.wrapper.enums.StatusAs;
import atu.alm.wrapper.exceptions.ALMServiceException;
import elements.TEMENOS_Enquiry;
import elements.browserTool.TEMENOS_Toolbars;
import elements.browserTool.TEMENOS_Tools;
import elements.page.TEMENOS_HomePage;
import elements.page.TEMENOS_LoginPage;
import elements.screen.TEMENOS_CompositeScreen;
import elements.screen.TEMENOS_TabbedScreen;
import elements.screen.TEMENOS_VersionScreen;
import elements.table.TEMENOS_InputTable;
import elements.table.TEMENOS_ListTable;
import elements.table.TEMENOS_ReadTable;
import elements.menu.TEMENOS_MainMenu;
import elements.menu.TEMENOS_SubMenu;

public class BaseTest implements RepositoryPaths {

	public static WebDriver driver;
	public static TEMENOS_LoginPage loginPg;
	public static TEMENOS_HomePage homePg;
	public static TEMENOS_Tools toolElements;
	public static TEMENOS_Toolbars toolbarElements;
	public static TEMENOS_ListTable listTable;
	public static TEMENOS_InputTable inputTable;
	public static TEMENOS_ReadTable readTable;
	public static TEMENOS_Enquiry enquiryElements;
	public static TEMENOS_MainMenu mainMenu;
	public static TEMENOS_SubMenu subMenu;
	public static TEMENOS_CompositeScreen compositeScreen;
	public static TEMENOS_VersionScreen versionScreen;
	public static TEMENOS_TabbedScreen tabbedScreen;

	public static String baseBrowser, baseUrl, environmentURL, environmentTitle, environmentName, baseEnv,
			baseEnableNativeEvents, basePersistentHovering, baseRequireWindowFocus, testSetPath, testSetName,
			globalTestSetPath, globalTestSetName, currentBranch;
	public static int testSetNumber, globalTestSetNumber;
	public static InputStream inputStreamForDF;
	public static StatusAs testResult;
	public static String projectPath = "";
	public Map<String, Object[]> testResults;

	protected ALMServiceWrapper wrapper;
	protected ITestCaseRun testRun;
	protected ITestCase testCase;
	protected List<ArrayList<Object>> stepDetails = new ArrayList<ArrayList<Object>>();
	protected SoftAssert softVerify;
	protected String testName;
	protected String today;
	protected String stepDescription;
	protected String stepExpected;
	protected static String stepActual;
	protected static String latestError;
	protected static String latestScreenshot;
	protected static StatusAs stepResult;
	protected int stepNumber;
	protected boolean loginResult;
	protected boolean debugMode = true;

	protected boolean almConnect;
	protected boolean almTestFound;
	protected static boolean commandLineAvailable = true;
	protected static boolean positiveDocumentation = false;
	protected static boolean parallelThreads = false;

	protected List<String> updatedValues = new ArrayList<String>();
	protected static final int MAX_RETRIES = 5;

	@BeforeSuite(alwaysRun = true)
	public void setUpSuite(final ITestContext context) {
		PropertyConfigurator.configure(log4jPath);
		String testSetNum;
		String globalTestSetNum;
		baseBrowser = context.getCurrentXmlTest().getParameter("runBrowser");
		baseUrl = context.getCurrentXmlTest().getParameter("runUrl");

		if (context.getCurrentXmlTest().getParameter("globalEnvironment") == null) {
			environmentName = context.getCurrentXmlTest().getParameter("environment");
		} else {
			environmentName = context.getCurrentXmlTest().getParameter("globalEnvironment");
		}

		testSetPath = context.getCurrentXmlTest().getParameter("testSetPath");
		testSetName = context.getCurrentXmlTest().getParameter("testSetName");
		testSetNum = context.getCurrentXmlTest().getParameter("testSetNumber");
		globalTestSetPath = context.getCurrentXmlTest().getParameter("globalTestSetPath");
		globalTestSetName = context.getCurrentXmlTest().getParameter("globalTestSetName");
		globalTestSetNum = context.getCurrentXmlTest().getParameter("globalTestSetNumber");

		if ("".equals(globalTestSetNum) || globalTestSetNum == null) {
			if ("".equals(testSetNum) || testSetNum == null) {
				testSetPath = defaultTestSetPath;
				testSetName = defaultTestSetName;
				testSetNumber = defaultTestSetNumber;
			} else {
				testSetNumber = Integer.parseInt(testSetNum);
			}
		} else {
			testSetPath = globalTestSetPath;
			testSetName = globalTestSetName;
			testSetNumber = Integer.parseInt(globalTestSetNum);
		}

		switch (environmentName) {
		default:
		case "SIT1":
			environmentURL = sit1EnvURL;

			break;
		case "SIT2":
			environmentURL = sit2EnvURL;

			break;
		case "TRAIN":
			environmentURL = trainingEnvURL;

			break;
		case "DEV":
			environmentURL = devEnvURL;

			break;

		case "PREPROD":
			environmentURL = preProdEnvURL;

			break;
		case "EASE":
			environmentURL = easeEnvURL;

			break;
		case "MG2":
			environmentURL = mg2EnvURL;

			break;
		}

		loginPg = PageFactory.initElements(driver, TEMENOS_LoginPage.class);
		homePg = PageFactory.initElements(driver, TEMENOS_HomePage.class);
		listTable = PageFactory.initElements(driver, TEMENOS_ListTable.class);
		inputTable = PageFactory.initElements(driver, TEMENOS_InputTable.class);
		readTable = PageFactory.initElements(driver, TEMENOS_ReadTable.class);
		compositeScreen = PageFactory.initElements(driver, TEMENOS_CompositeScreen.class);
		versionScreen = PageFactory.initElements(driver, TEMENOS_VersionScreen.class);
		toolElements = PageFactory.initElements(driver, TEMENOS_Tools.class);
		toolbarElements = PageFactory.initElements(driver, TEMENOS_Toolbars.class);
		enquiryElements = PageFactory.initElements(driver, TEMENOS_Enquiry.class);
		mainMenu = PageFactory.initElements(driver, TEMENOS_MainMenu.class);
		subMenu = PageFactory.initElements(driver, TEMENOS_SubMenu.class);
		tabbedScreen = PageFactory.initElements(driver, TEMENOS_TabbedScreen.class);

	}

	@BeforeSuite(enabled = false, alwaysRun = false)
	public void removeExistingProcesses() throws InterruptedException, IOException {
		Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
		Runtime.getRuntime().exec("taskkill /F /IM iexplore.exe");
	}

	@BeforeClass(alwaysRun = true)
	@Parameters({ "runBrowser", "runUrl", "env", "enableNativeEvents", "persistentHovering", "requireWindowFocus" })
	public void setUpBrowser(final String runBrowser, final String runUrl, final ITestContext ctx,
			@Optional("") final String env, @Optional("FALSE") final String enableNativeEvents,
			@Optional("FALSE") final String persistentHovering, @Optional("FALSE") final String requireWindowFocus) {

		baseEnv = env;
		baseEnableNativeEvents = enableNativeEvents;
		basePersistentHovering = persistentHovering;
		baseRequireWindowFocus = requireWindowFocus;

		// Setting up worksheet
		if ("".equals(baseEnv)) {
			Reporter.log("environment parameter not defined in TestNG suite file. Using ENV1 environment as default",
					false);
			baseEnv = "ENV1";
		} else {
			baseEnv = ctx.getCurrentXmlTest().getParameter("env").toUpperCase().trim();
			Reporter.log("Environment set to: " + baseEnv, false);
		}
		if ("FALSE".equals(baseEnableNativeEvents)) {
			Reporter.log("Native Events not defined in TestNG suite file. Using FALSE as default", false);
		} else {
			baseEnableNativeEvents = ctx.getCurrentXmlTest().getParameter("enableNativeEvents").toUpperCase().trim();
			Reporter.log("Native Events set to: " + baseEnableNativeEvents, false);
		}
		if ("FALSE".equals(basePersistentHovering)) {
			Reporter.log("Persistent Hovering parameter not defined in TestNG suite file. Using FALSE as default",
					false);
		} else {
			basePersistentHovering = ctx.getCurrentXmlTest().getParameter("persistentHovering").toUpperCase().trim();
			Reporter.log("Persistent Hovering set to: " + baseEnableNativeEvents, false);
		}

		if ("FALSE".equals(baseRequireWindowFocus)) {
			Reporter.log("Requires Window Focus parameter not defined in TestNG suite file. Using FALSE as default",
					false);
		} else {
			baseRequireWindowFocus = ctx.getCurrentXmlTest().getParameter("requireWindowFocus").toUpperCase().trim();
			Reporter.log("Require Window Focus set to: " + baseRequireWindowFocus, false);
		}

		launchBrowser(baseBrowser, baseUrl);
		final TestRunner runner = (TestRunner) ctx;
		runner.setOutputDirectory(outputDirectory);

		Reporter.log("Application Launched successfully", false);
		driver.manage().timeouts().implicitlyWait(elementWaitTimeout / 5, TimeUnit.SECONDS);
	}

	public static WebDriver launchBrowser(final String runBrowser, final String runUrl) {

		final Path currentRelPath = Paths.get("");
		projectPath = currentRelPath.toAbsolutePath().toString();
		final String baseBrowser1 = runBrowser.toLowerCase(Locale.ENGLISH).trim();
		final String baseUrl1 = runUrl;
		DesiredCapabilities capabilities;

		System.setProperty("webdriver.chrome.driver", chromeDriverPropPath);
		System.setProperty("webdriver.firefox.bin", fireFoxDriverPropPath);
		System.setProperty("webdriver.ie.driver", ieDriverPropPath);
		System.setProperty("webdriver.edge.driver", edgeDriverPropPath);

		if ("firefox".equals(baseBrowser1)) {
			System.setProperty("webdriver.gecko.driver", "testLibrary/firefox/geckodriver.exe");
			driver = new FirefoxDriver();
		} else if ("ie".equals(baseBrowser1)) {
			commandLineAvailable = false;
			capabilities = DesiredCapabilities.internetExplorer();
			capabilities.setCapability("initialBrowserUrl", environmentURL);
			// capabilities.setCapability("nativeevents", false);
			driver = new InternetExplorerDriver(capabilities);
		} else if ("chrome".equals(baseBrowser1)) {
			driver = new ChromeDriver();
		} else if ("edge".equals(baseBrowser1)) {
			driver = new EdgeDriver();
		} else if ("Safari".equals(baseBrowser1)) {
			driver = new ChromeDriver();
		} else {
			driver = new ChromeDriver();
		}
		driver.get(baseUrl1);

		return driver;
	}

	@BeforeClass(alwaysRun = true)
	public void setUpTestRun(final ITestContext ctx) throws ALMServiceException {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		Reporter.log("Time Stamp Start: " + timestamp.toString(), debugMode);
		testResult = StatusAs.PASSED;
		testName = ctx.getName();

		if (!almConnect) {
			try {
				wrapper = new ALMServiceWrapper(almAddress);
				wrapper.connect(almUsername, almPassword, almDomain, almProject);
				almConnect = true;
			} catch (ALMServiceException e) {
				Reporter.log("There was an issue logging in to ALM", debugMode);
				Reporter.log("Test Results will not be automatically updated", debugMode);
				Reporter.log("Username was: " + almUsername, debugMode);
				Reporter.log(e.getMessage(), false);
				almConnect = false;
			} catch (NoClassDefFoundError e) {
				Reporter.log("There was an issue running jacob.dll", debugMode);
				Reporter.log("Test Results will not be automatically updated", debugMode);
				Reporter.log(e.getMessage(), false);
				almConnect = false;
			}
		}

		if (almConnect) {
			try {
				testCase = wrapper.updateResult(testSetPath, testSetName, testSetNumber, testName, StatusAs.NO_RUN);
				stepNumber = 0;
				almTestFound = true;
			} catch (ALMServiceException e) {
				Reporter.log("There was an issue accessing the Test Case in ALM", debugMode);
				Reporter.log("Test Results will not be automatically updated", debugMode);
				Reporter.log("Test Case: " + testName + ". Test Set: " + testSetPath + "\\" + testSetName
						+ "(Test Set ID " + testSetNumber + ")", debugMode);
				Reporter.log(e.getMessage(), false);
				almTestFound = false;
			}
		}
		Reporter.log("== Starting Test " + testName + " ==", debugMode);
	}

	@BeforeMethod
	public void resetStepVariables() {
		stepResult = StatusAs.PASSED;
		stepDescription = "";
		stepExpected = "";
		stepActual = "";
		latestError = "";
		latestScreenshot = "";
		softVerify = new SoftAssert();
		Reporter.clear();
	}

	@AfterMethod
	public void addStepToALM(final ITestResult result) {
		String displayedMessage;
		boolean activeTabEmpty;
		boolean logStepToALM;

		if (stepResult.equals(StatusAs.FAILED)) {
			testResult = StatusAs.FAILED;

			if (latestError.isEmpty()) {
				try {
					activeTabEmpty = "".equalsIgnoreCase(inputTable.inputElement("activeTab").getAttribute("value"));
					if (activeTabEmpty) {
						latestError = "Error Displayed: " + versionScreen.errorMessage().getText();
						versionScreen.errorMessage().click();
					} else {
						latestError = "Error Displayed: " + inputTable.errorMessage().getText();
						inputTable.errorMessage().click();
					}
				} catch (NoSuchElementException | ElementNotVisibleException e1) {
					try {
						latestError = "Error Displayed: " + versionScreen.connectionError().getText();
					} catch (NoSuchElementException | ElementNotVisibleException e2) {
						latestError = "No additional error message was displayed in T24";
					}
				}
			} else {
				latestError = "Error Displayed: " + latestError;
			}

			stepActual += System.lineSeparator();
			stepActual += latestError;

		} else if (!stepResult.equals(StatusAs.NOT_COMPLETED) && "".equals(stepActual)) {
			stepActual = stepExpected;
		}

		if (latestError.contains("JMS queue") || !loginResult && !stepResult.equals(StatusAs.NOT_COMPLETED)) {
			stepResult = StatusAs.BLOCKED;
			testResult = StatusAs.BLOCKED;
		}

		if (!Reporter.getOutput().isEmpty()) {
			stepActual += System.lineSeparator();
			stepActual += System.lineSeparator();
			stepActual += "Debug Log:";
			for (final String reporterLine : Reporter.getOutput()) {
				stepActual += System.lineSeparator();
				stepActual += reporterLine;
			}
			Reporter.clear();
		}

		Reporter.log("= " + result.getMethod().getMethodName() + " =", debugMode);
		Reporter.log("Step Description: " + stepDescription, debugMode);
		Reporter.log("Step Expected: " + stepExpected, debugMode);
		Reporter.log("Step Actual: " + stepActual, debugMode);
		Reporter.log("===", debugMode);

		logStepToALM = !stepResult.equals(StatusAs.NOT_COMPLETED)
				|| stepResult.equals(StatusAs.NOT_COMPLETED) && testResult.equals(StatusAs.BLOCKED);

		if (almConnect && almTestFound && logStepToALM) {
			stepDetails.add(new ArrayList<Object>());
			stepDetails.get(stepNumber).add(result.getMethod().getMethodName());
			stepDetails.get(stepNumber).add(stepResult);
			stepDetails.get(stepNumber).add(stepDescription);
			stepDetails.get(stepNumber).add(stepExpected);
			stepDetails.get(stepNumber).add(stepActual);
			stepDetails.get(stepNumber).add(latestScreenshot);
			stepNumber++;
		}
	}

	@AfterClass(alwaysRun = true)
	public void testTearDown(final ITestContext ctx) throws ALMServiceException {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		Reporter.log("Time Stamp End: " + timestamp.toString(), debugMode);
		final String className = this.getClass().getName().substring(14);
		final String currentTest = ctx.getName();
		String[] paramPair;

		while (!updatedValues.isEmpty()) {
			paramPair = updatedValues.get(0).split(",", 2);
			updateTestSuite(className, currentTest, paramPair[0], paramPair[1]);
			updatedValues.remove(0);
		}

		driver.quit();

		if (almConnect && almTestFound) {
			try {
				today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date());
				testRun = wrapper.createNewRun(testCase, "Automated Run " + today, testResult);
				testCase = wrapper.updateResult(testSetPath, testSetName, testSetNumber, testName, testResult);

				for (final ArrayList<Object> stepInfo : stepDetails) {
					wrapper.addStep(testRun, (String) stepInfo.get(0), (StatusAs) stepInfo.get(1),
							(String) stepInfo.get(2), (String) stepInfo.get(3), (String) stepInfo.get(4));
					if (!((String) stepInfo.get(5)).isEmpty()) {
						wrapper.newAttachment((String) stepInfo.get(5), "Screenshot for " + (String) stepInfo.get(0),
								testRun);
					}
				}
			} catch (ALMServiceException e) {
				Reporter.log("There was an issue creating a Test Run to add steps in ALM", debugMode);
				Reporter.log("Test Results will not be automatically updated", debugMode);
				Reporter.log("Test Case: " + testName + ".", debugMode);
				Reporter.log(e.getMessage(), false);
			}
		}
		Reporter.log("== Finished Test " + testName + ". ALM Result: " + testResult.getStatus() + " ==", debugMode);
	}

	@AfterSuite(alwaysRun = true)
	public void suiteTearDown() throws ALMServiceException {
		if (almConnect) {
			wrapper.close();
		}
	}

	public void updateTestSuite(final String fileName, final String test, final String paramName,
			final String paramValue) {
		try {
			final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder;
			Document doc;

			final TransformerFactory transformerFactory = TransformerFactory.newInstance();
			final Transformer transformer = transformerFactory.newTransformer();
			DOMSource source;
			NodeList tests;
			NodeList testParameters;
			Node thisTest;
			Node thisParameter;

			final String filepath = "./testRun/OOB_Munir/" + fileName + ".xml";
			String thisTestName;
			String thisParamName;

			final StreamResult streamResult = new StreamResult(new File(filepath));

			docFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.parse(filepath);

			tests = doc.getElementsByTagName("test");

			boolean testFound = false;
			boolean parameterFound = false;

			for (int i = 0; i < tests.getLength() && !testFound; i++) {
				thisTest = tests.item(i);
				thisTestName = thisTest.getAttributes().getNamedItem("name").getTextContent();
				if (thisTestName.equals(test)) {
					testFound = true;
					testParameters = thisTest.getChildNodes();
					for (int j = 0; j < testParameters.getLength() && !parameterFound; j++) {
						thisParameter = testParameters.item(j);
						if (thisParameter.getNodeName().equals("parameter")) {
							thisParamName = thisParameter.getAttributes().getNamedItem("name").getTextContent();
							if (thisParamName.equals(paramName)) {
								parameterFound = true;
								thisParameter.getAttributes().getNamedItem("value").setNodeValue(paramValue);
							}
						}
					}
				}
			}

			source = new DOMSource(doc);
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "http://testng.org/testng-1.0.dtd");
			transformer.transform(source, streamResult);

		} catch (IOException e) {
			Reporter.log(e.getMessage(), false);
		} catch (SAXException e) {
			Reporter.log(e.getMessage(), false);
		} catch (ParserConfigurationException e) {
			Reporter.log(e.getMessage(), false);
		} catch (TransformerConfigurationException e) {
			Reporter.log(e.getMessage(), false);
		} catch (TransformerException e) {
			Reporter.log(e.getMessage(), false);
		}
	}

	public String getWorksheetName(final String expectedWorkSheet) {
		String result = "";
		String excelFilePath = "";

		final Map<String, String> fileMap = new ConcurrentHashMap<String, String>();

		if (expectedWorkSheet.contains("ListSummary") || expectedWorkSheet.contains("DataDetails")) {
			excelFilePath = dynamicResultReportPath.replace("varFileName", expectedWorkSheet);
		} else {
			excelFilePath = dynamicResourcesPath.replace("varFileName", expectedWorkSheet);
		}

		fileMap.put("coreMenu", coreMenuFile);
		fileMap.put("cambMenu", cambMenuFile);

		// fileMap.put("oobTestData", oobTestDataFile);
		if (fileMap.containsKey(expectedWorkSheet)) {
			result = fileMap.get(expectedWorkSheet);
		} else {
			result = excelFilePath;
		}
		return result.toUpperCase(Locale.ENGLISH);
	}

	public String getHeaderName(final String expectedWorksheet, final String expectedTabName) {
		String actualTabName = "";
		List<String> tabs = new ArrayList<String>();
		final SheetReader reader = new SheetReader();
		try {
			tabs = reader.getTabNamesFromExcelSpreadsheet(getWorksheetName(expectedWorksheet));
			for (final String s : tabs) {
				if (s.contains("_")) {
					Reporter.log(s.substring(s.indexOf('_') + 1, s.length()), debugMode);
					if (s.substring(s.indexOf('_') + 1, s.length()).equals(expectedTabName)) {
						actualTabName = s;
						break;
					}
				}
			}
		} catch (IOException e) {
			Reporter.log(e.getMessage(), false);
		}

		if (actualTabName.isEmpty()) {
			Reporter.log("Unable to get tab name", debugMode);
		} else {
			Reporter.log("Tab name set to : " + actualTabName, debugMode);
		}
		return actualTabName;
	}

	public boolean isFirstNonEmpty(final String[] strArray, final int index) {
		boolean result = true;

		for (int i = 0; i < strArray.length && i < index; i++) {
			result = !"".equals(strArray[i]);
		}

		return result;
	}

	public String findElementSuffix(final String fieldName, final String multiValueSuffix) {
		String result = multiValueSuffix;
		String fieldToFind = fieldName + multiValueSuffix;
		final StringBuilder resultBuilder = new StringBuilder(result);
		final StringBuilder fieldToFindBuilder = new StringBuilder(fieldToFind);
		while (inputTable.listrows("")
				.findElements(By.xpath("//*[contains(@id, 'fieldName:" + fieldToFind + "') "
						+ "and not(contains(@class, 'enrichment')) and not(contains(@class, 'radioCheckStyle'))]"))
				.size() > 1) {
			resultBuilder.append(":1");
			result = resultBuilder.toString();
			fieldToFindBuilder.append(":1");
			fieldToFind = fieldToFindBuilder.toString();
		}
		if (inputTable.listrows("")
				.findElements(By.xpath("//*[contains(@id, 'fieldName:" + fieldToFind + "') "
						+ "and not(contains(@class, 'enrichment')) and not(contains(@class, 'radioCheckStyle'))]"))
				.size() == 0) {
			result = "";
		}
		result = resultBuilder.toString();
		return result;
	}

	public String findSuffixCandidate(final String[] prevRowEntries, final int colIndex, final String expectedValue,
			final String[] elementSuffixes) {
		final boolean isFirstNonEmpty = isFirstNonEmpty(prevRowEntries, colIndex);
		final boolean isNewMultiValue = elementSuffixes[colIndex].split(":").length > elementSuffixes[colIndex - 1]
				.split(":").length;
		final boolean isBlank = "".equals(expectedValue);
		String result;
		String[] valuesArray;
		int myInteger;

		if (isBlank && isFirstNonEmpty) {
			result = elementSuffixes[colIndex];
		} else {
			if (isNewMultiValue) {
				if (isFirstNonEmpty || isBlank) {
					result = elementSuffixes[colIndex];
				} else {
					result = elementSuffixes[colIndex - 1] + ":0";
				}

				valuesArray = result.split(":");
				for (int i = 0; i < valuesArray.length; i++) {
					if ("".equals(valuesArray[i])) {
						valuesArray[i] = "0";
					}
				}
				myInteger = Integer.parseInt(valuesArray[valuesArray.length - 1]);
				valuesArray[valuesArray.length - 1] = Integer.toString(myInteger + 1);
				for (int i = 0; i < valuesArray.length; i++) {
					if ("0".equals(valuesArray[i])) {
						valuesArray[i] = "";
					}
				}
				result = Arrays.toString(valuesArray).replace(", ", ":").replaceAll("[\\[\\]]", "");
			} else {
				result = elementSuffixes[colIndex - 1];
			}
		}

		return result;
	}

	public boolean switchToPage(final String expectedTabName) throws InterruptedException {
		if (driver.getWindowHandles().size() == 1) {
			Thread.sleep(5000);
		}
		final Set<String> windowIterator = driver.getWindowHandles();
		boolean result = false;
		for (final String s : windowIterator) {
			if (driver.switchTo().window(s).getTitle().contains(expectedTabName.replace(".", " "))) {
				driver.switchTo().window(s);
				result = true;
				break;
			}
		}
		return result;
	}

	public WebElement getPageData(final String parentHandle, final String expectedTabName, final String entryId)
			throws InterruptedException {
		WebElement pageData = null;

		driver.switchTo().frame(driver.findElement(By.xpath("//frame[contains(@id, 'banner')]")));
		homePg.commandLineField().clear();
		homePg.commandLineField().sendKeys(expectedTabName + " I " + entryId);
		homePg.commandLineField().sendKeys(Keys.ENTER);

		try {
			switchToPage(expectedTabName);
			pageData = inputTable.listrows("");
		} catch (NoSuchElementException e) {
			Reporter.log(e.getMessage(), false);
			driver.close();
			driver.switchTo().window(parentHandle);
			driver.switchTo().frame(driver.findElement(By.xpath("//frame[contains(@id, 'banner')]")));
			homePg.commandLineField().clear();
			homePg.commandLineField().sendKeys(expectedTabName + " S " + entryId);
			homePg.commandLineField().sendKeys(Keys.ENTER);

			switchToPage(expectedTabName);
			// pageData = readPg.listrows();
		}

		return pageData;
	}

	public String validateData(final WebElement pageData, final String entryId, final String fieldName,
			final String multiValueSuffix, final String expectedValue) {
		WebElement eleToFind;
		boolean result = false;
		String valueInApp = "";

		try {
			eleToFind = pageData.findElement(By.xpath("//*[contains(@id, 'fieldName:" + fieldName + multiValueSuffix
					+ "') " + "and not(contains(@class, 'enrichment')) and not(contains(@class, 'radioCheckStyle'))]"));

			switch (eleToFind.getTagName().toUpperCase(Locale.ENGLISH)) {
			case "INPUT":
				valueInApp = eleToFind.getAttribute("value");
				result = valueInApp.equalsIgnoreCase(expectedValue);
				break;
			case "SELECT":
				valueInApp = new Select(eleToFind).getFirstSelectedOption().getText();
				result = valueInApp.equalsIgnoreCase(expectedValue);
				break;
			case "HIDDEN":
				valueInApp = eleToFind.getAttribute("value");
				result = valueInApp.equalsIgnoreCase(expectedValue);
				break;
			default:
				valueInApp = "UNKNOWN ELEMENT";
				result = false;
				break;
			}

			if (result) {
				Reporter.log("PASS: Entry ID: " + entryId + ": Field " + fieldName + multiValueSuffix + " "
						+ " . App matches excel", debugMode);
			} else {
				Reporter.log("FAIL: Entry ID: " + entryId + ": Field " + fieldName + multiValueSuffix + ". Expected("
						+ expectedValue + ") Actual(" + valueInApp + ")", debugMode);
			}

		} catch (NoSuchElementException e) {

			if ("".equals(getLockedRecordError())) {
				Reporter.log(e.getMessage(), false);
				Reporter.log("FAIL: Entry ID: " + entryId + ": Field " + fieldName + multiValueSuffix
						+ " was not found in App.", debugMode);
				valueInApp = "ELEMENT NOT FOUND";
			} else {
				unblockEntity("User", getLockedRecordError());
				Reporter.log("FAIL: Entry ID: " + entryId + ": Field " + fieldName + multiValueSuffix + " was locked",
						debugMode);
				valueInApp = "ELEMENT LOCKED";
			}

		}

		return valueInApp;
	}

	public List<List<String>> appendReportContents(final String[] values, final int index,
			final List<List<String>> listBefore) {
		final List<List<String>> listAfter = listBefore;
		listAfter.add(new ArrayList<String>());
		for (int i = 0; i < values.length; i++) {
			listAfter.get(index).add(values[i]);
		}
		return listAfter;
	}

	public boolean unblockEntity(final String fieldId, final String idValue) {

		List<WebElement> resetButtonList;
		boolean resetSuccess = true;
		boolean lastPage = false;

		try {
			switchToPage(environmentTitle);
			driver.switchTo().frame(homePg.bannerFrame());
			homePg.commandLineField().clear();
			homePg.commandLineField().click();
			homePg.commandLineField().sendKeys("COS RESET.RECORDLOCK");
			homePg.commandLineField().sendKeys(Keys.ENTER);

			for (final String winHandle : driver.getWindowHandles()) {
				driver.switchTo().window(winHandle);
			}

			compositeScreen.switchToFrame("id", "ENQUIRY");
			toolElements.toolsButton(SELECTION_SCREEN).click();
			enquiryElements.enquirySearch(fieldId, "contains", idValue);

			while (!lastPage) {
				resetButtonList = compositeScreen.unblockButtonList(idValue);
				for (final WebElement resetButton : resetButtonList) {
					resetButton.click();

					versionScreen.windowAction(SWITCH_TO_DEFAULT);
					compositeScreen.switchToFrame("id", "BLANK");
					toolElements.toolsButton("Verifies a deal").click();

					versionScreen.windowAction(SWITCH_TO_DEFAULT);
					compositeScreen.switchToFrame("id", "ENQUIRY");
				}

				toolElements.toolsButton("Refresh").click();
				if (compositeScreen.unblockButtonList(idValue).size() <= 0) {
					lastPage = true;
				}

			}

		} catch (NoSuchElementException e) {
			Reporter.log("Fail to unblock entities with:" + idValue, debugMode);
			resetSuccess = false;
		} catch (InterruptedException e) {
			Reporter.log("There was an interruption while unblocking: " + idValue, debugMode);
			resetSuccess = false;
		}
		return resetSuccess;
	}

	public String getLockedRecordError() {
		return compositeScreen.blockedRecordMessage().get(0).getText().split(" ")[5];
	}

	public void takeScreenshot() {
		SimpleDateFormat sdf;
		String ntime;
		String ndate;
		String nhour;
		ListenerScreen listener;

		// To takeScreenShot
		sdf = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.ENGLISH);
		ntime = sdf.format(new Date());
		ndate = ntime.split("-")[0];
		nhour = ntime.split("-")[1];
		listener = new ListenerScreen();

		final String screenTitle = driver.getTitle().replaceAll("/", "_");
		final String fileSeperator = System.getProperty("file.separator");
		final String screenShotPath = "testReport/screenshots/errorScreens" + fileSeperator + ndate;
		final String screenShotName = "E" + nhour + "_" + screenTitle + ".png";

		driver.manage().window().maximize();
		listener.takeScreenShot(driver, screenShotPath, screenShotName);
	}

}
