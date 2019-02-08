package testLibs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.maven.surefire.shade.org.apache.maven.shared.utils.StringUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Reporter;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class BaseTest_OOB extends BaseTest {
	protected static final String LASTPAGE = "LAST";
	protected String parentHandle = "";
	
	//Change Login Credentials Here!!
	public static String username = "RaghavT";
	public static String password = "654321";
	public static String authorizerUsername = "CHANGE.ME";
	public static String authorizerPassword = "CHANGE.ME";

	@Test(priority = 0, enabled = true)
	public void login() {
		String loggedUser;
		long threadNumber;
		stepDescription = "Login to T24: " + environmentName + " Environment";
		stepExpected = "User successfully logs in";

		if (parallelThreads) {
			threadNumber = Thread.currentThread().getId() % 16 + 1;
			loginResult = loginPg.login("AUTOMATION" + threadNumber,
					DefaultVariables.dataGenPasswords.get("AUTOMATION" + threadNumber));
		} else {
			loginResult = loginPg.login(username, password);
		}

		if (loginResult) {
			switchToFrame(homePg.bannerFrame());
			loggedUser = homePg.bannerText().getText().split(" ")[0];
			stepActual = "User " + loggedUser + " successfully logs in";
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			testResult = StatusAs.NOT_COMPLETED;
			stepActual = "Login failed";
			softVerify.fail(stepActual);
			softVerify.assertAll();
		}
	}

	public boolean switchToPage(final String pageName, final boolean closeOthers) {
		// This method switches the driver to the page specified,
		// or the last opened page if it can't find it.
		// If closeOthers is true, it closes all the other pages as well
		final Set<String> windowIterator = driver.getWindowHandles();
		String pageHandler = "";
		boolean pageFound = false;

		for (final String s : windowIterator) {
			driver.switchTo().window(s);
			if (driver.getTitle().contains(pageName)) {
				if (!pageName.equals(LASTPAGE)) {
					pageFound = true;
					pageHandler = s;
				}
			} else if (closeOthers) {
				driver.close();
			}
		}

		if (pageFound) {
			driver.switchTo().window(pageHandler);
		}

		if (!commandLineAvailable) {
			compositeScreen.switchToFrame(ID, "FRAMETABCSM");
		}

		if (pageFound || LASTPAGE.equals(pageName)) {
			versionScreen.windowAction(MAXIMIZE);
		}
		return pageFound || LASTPAGE.equals(pageName);
	}

	public String switchToBranch(final String branch) {
		boolean expectedPageTitle = false;
		Reporter.log("Switching to branch " + branch, debugMode);
		switchToPage(environmentTitle, true);
		switchToFrame(homePg.bannerFrame());
		homePg.homePageLinks("Tools").click();
		switchToPage("Temenos T24", false);
		mainMenu.toolsMainMenu("My Companies").click();
		subMenu.toolsSubMenu(branch).click();
		while (!expectedPageTitle) {
			switchToPage(branch, false);
			environmentTitle = driver.getTitle();

			if (environmentTitle.contains(branch)) {
				expectedPageTitle = true;
			} else {
				switchToPage("Temenos T24", false);
				subMenu.toolsSubMenu(branch).click();
				expectedPageTitle = false;
			}
		}
		currentBranch = branch;
		return currentBranch;
	}

	public boolean switchToTab(final String tabName, final String sectionName) {
		WebElement tabLinkElement;
		String firstFrame = "";
		String actualTabName;
		String lastFrame = "";
		boolean result = true;

		try {

			if (tabName.contains("%")) {
				switchToPage(LASTPAGE, false);
				firstFrame = tabName.split("%")[0];
				actualTabName = tabName.split("%")[1];
				lastFrame = tabName.split("%")[2];
				if (!"".equals(firstFrame)) {
					if ("main".equals(firstFrame)) {
						switchToPage(LASTPAGE, false);
						compositeScreen.switchToFrame(ID, "TAB");
					}
					compositeScreen.switchToFrame(ID, firstFrame);
				}
			} else {
				actualTabName = tabName;
			}

			tabLinkElement = tabbedScreen.findTab(actualTabName, sectionName);
			if ("nonactive-tab".equals(tabLinkElement.getAttribute("class"))) {
				if (positiveDocumentation) {
					takeScreenshot();
				}
				tabLinkElement.click();
				result = true;
			}
			if (tabName.contains("%") && !"".equals(lastFrame)) {
				switchToPage(LASTPAGE, false);
				if ("main".equals(firstFrame)) {
					switchToPage(LASTPAGE, false);
					compositeScreen.switchToFrame(ID, "TAB");
				}
				compositeScreen.switchToFrame(ID, lastFrame);
			}
		} catch (NoSuchElementException e) {
			result = false;
		}

		return result;
	}

	public boolean switchToFrame(final WebElement frame) {
		boolean result;
		if (parallelThreads) {
			try {
				LocalDriverManager.getDriver().switchTo().frame(frame);
				result = true;
			} catch (NoSuchElementException e) {
				result = false;
			}
		} else {
			try {
				driver.switchTo().frame(frame);
				result = true;
			} catch (NoSuchElementException e) {
				result = false;
			}
		}
		return result;
	}

	public String setParentHandle() {
		String result;
		if (parallelThreads) {
			result = LocalDriverManager.getDriver().getWindowHandle();
		} else {
			result = driver.getWindowHandle();
		}
		return result;
	}

	protected boolean inputData(final String fieldName, final String expectedValue, final boolean overrideWithBlank) {
		List<WebElement> elementCandidates;
		WebElement eleToFind;
		WebElement pageData;
		Select dropdown;
		String tabValue;
		String actualFieldName;
		String fieldNameWithSign;
		String tabName = "";
		String section = "";
		String tabAttribute;
		String dateformatAttribute;
		String enriFieldNameAttribute;
		String typeAttribute;
		String dropDownAttribute;
		String expandField;
		int afterDollarPosition;
		int afterHashPosition;
		boolean hiddenElement;
		boolean calendarElement;
		boolean radioButtonElement;
		boolean correctTabElement;
		boolean checkBoxElement;
		boolean result = false;
		// #=tab, +=expand, $=label, <=delete %=frame

		try {
			pageData = inputTable.listrows("");
			if (fieldName.contains("$")) {
				section = fieldName.substring(0, fieldName.indexOf('$'));
				afterDollarPosition = fieldName.indexOf('$') + 1;
			} else {
				section = "";
				afterDollarPosition = 0;
			}

			if (fieldName.contains("#")) {
				afterHashPosition = fieldName.indexOf('#') + 1;
				tabName = fieldName.substring(afterDollarPosition, afterHashPosition - 1);
				switchToTab(tabName, section);
				if (tabName.contains("%")) {
					tabName = tabName.split("%")[1];
				}
				if (!"".equals(section)) {

					tabValue = driver
							.findElement(
									By.xpath("//legend[text()='" + section + "']/following-sibling::div//span[text()='"
											+ tabName + "']/ancestor::*/a[@class='active-tab']"))
							.getAttribute(HREF).split("'", 4)[1];
				} else {
					tabValue = driver.findElement(By.xpath("//*[span[text()='" + tabName + "']]/parent::*/a"))
							.getAttribute(HREF).split("'", 4)[1];
				}
			} else {
				tabName = "";
				afterHashPosition = afterDollarPosition;
				tabValue = "tab1";
			}

			if (fieldName.contains("+")) {
				fieldNameWithSign = fieldName.substring(afterHashPosition);
				actualFieldName = fieldName.substring(afterHashPosition).replaceAll("\\+", "");
				pageData = inputTable.tabrows(tabName, section);
				final int index = Integer.parseInt(fieldNameWithSign.split("\\+")[1].split(":")[1]);
				expandField = fieldNameWithSign.substring(0, fieldNameWithSign.indexOf('+')) + ":"
						+ Integer.toString(index - 1) + fieldNameWithSign.substring(fieldNameWithSign.indexOf('+') + 3);
				inputTable.multiValueButton(expandField, "Expand").click();
			} else if (fieldName.contains("<")) {
				fieldNameWithSign = fieldName.substring(afterHashPosition);
				actualFieldName = fieldName.substring(afterHashPosition).replaceAll("\\<", "");
				pageData = inputTable.tabrows(tabName, section);
				inputTable.multiValueButton(actualFieldName, "Delete").click();
				result = true;
			} else {
				actualFieldName = fieldName.substring(afterHashPosition);
				pageData = inputTable.tabrows(tabName, section);
			}

			if (!fieldName.contains("<")) {
				elementCandidates = inputTable.elementCandidates(actualFieldName, section);
				if (elementCandidates.isEmpty()) {
					Reporter.log(
							"Field " + fieldName + " was not found. Value " + expectedValue + " will not be entered.",
							debugMode);
					result = true;
				} else {
					for (int i = 0; i < elementCandidates.size() && !result; i++) {
						eleToFind = elementCandidates.get(i);

						switch (eleToFind.getTagName().toUpperCase(Locale.ENGLISH)) {
						case "TEXTAREA":
						case "INPUT":

							dropDownAttribute = eleToFind.getAttribute("dropdown");
							typeAttribute = eleToFind.getAttribute("type");
							if (eleToFind.getAttribute("tabname") != null) {
								tabAttribute = eleToFind.getAttribute("tabname");
							} else if (eleToFind.getAttribute("autopopulated") != null) {
								tabAttribute = eleToFind.getAttribute("autopopulated");
							} else {
								tabAttribute = "";
							}

							dateformatAttribute = eleToFind.getAttribute("dateformat");
							enriFieldNameAttribute = eleToFind.getAttribute("enrifieldname");
							correctTabElement = "".equals(tabName) || tabAttribute.equals(tabValue);
							hiddenElement = typeAttribute.contains("hidden");
							calendarElement = dateformatAttribute != null && enriFieldNameAttribute != null;
							radioButtonElement = hiddenElement && !calendarElement && tabAttribute != null
									&& !"".equals(tabAttribute) && dropDownAttribute == null;
							checkBoxElement = eleToFind.getAttribute("id").contains("CheckBox") && !calendarElement
									&& dropDownAttribute == null;

							if (correctTabElement) {
								if (radioButtonElement) {
									eleToFind = pageData.findElement(
											By.xpath("//*[contains(@id, 'radio')][contains(@id, '" + actualFieldName
													+ "')][@type='radio'][contains(@value, '" + expectedValue + "')]"));

									eleToFind.click();
									result = true;
								} else if (checkBoxElement) {
									eleToFind = pageData
											.findElement(By.xpath("//*[contains(@id, 'CheckBox')][contains(@id, '"
													+ actualFieldName + "')][@type='checkbox']"));

									for (int j = 0; j < MAX_RETRIES
											&& !expectedValue.equals(eleToFind.getAttribute("value")); j++) {
										eleToFind.click();

										if (j + 1 == MAX_RETRIES) {
											Reporter.log("Maximum attempts reached for function inputData", debugMode);
										}
									}

									result = true;
								} else if (!calendarElement && hiddenElement) {

									if (dropDownAttribute != null) {
										inputTable.selectionCriteriaButton(actualFieldName).click();

										switchToPage(LASTPAGE, false);

										enquiryElements.enquirySearch("@ID", "", expectedValue);
										enquiryElements.linkToSelect(actualFieldName).click();

										switchToPage(LASTPAGE, false);
									}
									result = true;
								} else if (calendarElement) {
									if (!hiddenElement) {
										eleToFind.click();
									}
									if ("".equals(expectedValue) && overrideWithBlank) {
										eleToFind.clear();
									} else if (!"".equals(expectedValue)) {
										inputTable.dateCalendar(actualFieldName).click();
										inputTable.setCalendar(expectedValue, actualFieldName);
									}
									if ("".equals(section)) {
										if ("".equals(tabName)) {
											pageData = inputTable.listrows("");
										} else {
											pageData = inputTable.tabrows(tabName, "");
										}
									} else {
										if ("".equals(tabName)) {
											pageData = inputTable.listrows(section);
										} else {
											pageData = inputTable.tabrows(tabName, section);
										}
									}
									result = true;
								} else {
									if ((!"".equals(expectedValue) || overrideWithBlank) && !hiddenElement
											&& typeAttribute.contains("text")) {

										eleToFind.click();

										for (int j = 0; j < MAX_RETRIES
												&& !eleToFind.getAttribute("value").isEmpty(); j++) {
											eleToFind.clear();

											if (j + 1 == MAX_RETRIES) {
												Reporter.log("Maximum attempts reached for function inputData",
														debugMode);
											}
										}

										eleToFind.sendKeys(expectedValue);
										result = true;

									}
								}
							}
							break;
						case "SELECT":

							if (eleToFind.getAttribute("tabname") == null) {
								tabAttribute = eleToFind.getAttribute("autopopulated");
							} else {
								tabAttribute = eleToFind.getAttribute("tabname");
							}

							correctTabElement = "".equals(tabName) || tabAttribute.equals(tabValue);
							if (correctTabElement) {
								dropdown = new Select(eleToFind);
								dropdown.selectByVisibleText(expectedValue);
								result = true;
							}

							break;
						default:
							Reporter.log("BLOCKED: Framework cannot handle field type yet: " + eleToFind.getTagName(),
									debugMode);
							result = false;
							break;
						}
					}
					pageData.click();
				}
			}
		} catch (NoSuchElementException e) {
			Reporter.log("NoSuchElementException while interacting with field " + fieldName, debugMode);
			result = false;
		} catch (ElementNotVisibleException e) {
			Reporter.log("ElementNotVisibleException while interacting with field " + fieldName, debugMode);
			result = false;
		} catch (StaleElementReferenceException e) {
			Reporter.log("StaleElementReferenceException while interacting with field " + fieldName, debugMode);
			result = false;
		}

		return result;
	}

	public boolean multiInputData(final String fields, final String values, final boolean overrideWithBlank) {
		String[] listOfFields;
		String[] listOfValues;
		int fieldsCommas;
		int valuesCommas;
		boolean isInputPage;
		boolean result = false;

		fieldsCommas = StringUtils.countMatches(fields, ",");
		valuesCommas = StringUtils.countMatches(values, ",");

		if (fieldsCommas != valuesCommas) {
			Reporter.log("The fields and values strings have a different number of commas", debugMode);
			Reporter.log(fieldsCommas + " commas on Fields: " + fields, debugMode);
			Reporter.log(valuesCommas + " commas on Values: " + values, debugMode);
		}

		listOfFields = fields.split(",", -1);
		listOfValues = values.split(",", -1);

		isInputPage = inputTable.inputElement("activeTab").getAttribute("value").contains("tab");

		for (int i = 0; i < listOfFields.length; i++) {
			if (!"".equals(listOfFields[i]) && isInputPage) {
				isInputPage = inputTable.inputElement("activeTab").getAttribute("value").contains("tab");
				result = false;
				try {

					for (int j = 0; j < MAX_RETRIES && !result; j++) {
						result = inputData(listOfFields[i].trim(), listOfValues[i].trim(), overrideWithBlank);

						if (j + 1 == MAX_RETRIES) {
							Reporter.log("Maximum attempts reached for function multiInputData", debugMode);
						}
					}

				} catch (ArrayIndexOutOfBoundsException e) {
					Reporter.log(e.getMessage(), false);
					result = false;
				}
			}
		}

		return result;
	}

	public String readData(final String fieldName) {
		List<WebElement> elementCandidates;
		WebElement eleToFind;
		WebElement pageData;
		String readData = "";

		pageData = inputTable.listrows("");

		elementCandidates = pageData.findElements(By.xpath("//*[@id= 'fieldName:" + fieldName
				+ "'][not(contains(@class, 'enrichment'))][not(contains(@class, 'radioCheckStyle'))]"));

		if (elementCandidates.isEmpty()) {
			readData = "Element Not Found";
		}

		for (int i = 0; i < elementCandidates.size(); i++) {
			eleToFind = elementCandidates.get(i);

			readData = eleToFind.getAttribute("value");

		}

		return readData;
	}

	public String customer(final String action, final String customerType, final String roleBasedPage,
			final String fields, final String values) {
		final Random generator = new Random();
		final int number = generator.nextInt(8999) + 1000;
		final String dealerRepNumber = Integer.toString(number);

		final CustomerData randomCustomer = new CustomerData();

		String commandLine;
		String createdCIF = null;
		String finalMessage;
		String allFields = "";
		String allValues = "";
		boolean result = true;

		if (randomCustomer.successfulRetrieve) {
			switchToPage(LASTPAGE, false);

			switch (customerType) {
			default:
			case PERSONAL:
			case NON_CLIENT_PERSONAL:
				allFields = "FAMILY.NAME," + "GIVEN.NAMES," + "DATE.OF.BIRTH," + "GENDER," + "SIN.NO,"
						+ "Financial Details#EMPLOYERS.ADD:1:1," + "Financial Details#EMPLOYERS.ADD:1:2,"
						+ "Financial Details#EMPLOYERS.ADD:1:3," + "Financial Details#EMPLOYERS.ADD:1:4,"
						+ "Financial Details#EMPLOYERS.ADD:1:5," + "Financial Details#L.EMPL.PROVINCE,"
						+ "Financial Details#EMPLOYMENT.STATUS:1," + "Financial Details#L.INDUSTRY.SECT,"
						+ "Financial Details#L.EMPL.COUNTRY," + "Financial Details#JOB.TITLE:1,"
						+ "Financial Details#EMPLOYERS.NAME:1," + "Communication Details#PHONE.1:1,"
						+ "Communication Details#EMAIL.1:1," + "Communication Details#SMS.1:1,"
						+ "Communication Details#FAX.1:1," + "Communication Details#OFF.PHONE:1,"
						+ "Address#ADDRESS:1:1," + "Address#US.STATE," + "Address#POST.CODE:1," + "Address#CITY,"
						+ "Address#ADDR.CNTRY.ID," + "Address#RECORD.REFRESH," + "ID#LEG.ID.HOLDER:1,"
						+ "ID#CAMB.ID.PL.ISS:1," + "LEG.ID.HOLDER+:2," + "ID#CAMB.ID.PL.ISS:2," + fields + ",";
				allValues = randomCustomer.lastName + "," + randomCustomer.firstName + "," + randomCustomer.dateOfBirth
						+ "," + randomCustomer.gender + "," + randomCustomer.sinNumber + ","
						+ randomCustomer.employerAddressStreet + ",x,x," + randomCustomer.employmentAddressCity + ","
						+ randomCustomer.employmentAddressPostalCode + "," + randomCustomer.employmentAddressProvince
						+ "," + "Emp - Employed," + "Tec - Information Technology," + "CA," + "487,"
						+ randomCustomer.employerName + "," + randomCustomer.phoneMain + ","
						+ randomCustomer.emailAddress + "," + randomCustomer.phoneSms + "," + randomCustomer.phoneFax
						+ "," + randomCustomer.phoneOffice + "," + randomCustomer.addressStreet + ","
						+ randomCustomer.addressProvince + "," + randomCustomer.addressPostalCode + ","
						+ randomCustomer.addressCity + "," + "CA," + "Y," + randomCustomer.firstName + ","
						+ randomCustomer.addressCity + "," + randomCustomer.firstName + "," + randomCustomer.addressCity
						+ "," + values + ",";

				if (PERSONAL.equals(customerType)) {
					if (ROLEBASED_SAE.equals(roleBasedPage)) {
						commandLine = "CUSTOMER,LBC.CAMB.INPUT2.CIF.SAE I F3";
					} else {
						commandLine = "CUSTOMER,LBC.CAMB.INPUT2.CIF I F3";
					}
				} else {
					if (ROLEBASED_SAE.equals(customerType)) {
						commandLine = "Error: Framework does not support: " + customerType + " for " + roleBasedPage;
						Reporter.log(commandLine, debugMode);
					} else {
						commandLine = "CUSTOMER,LBC.CAMB.INPUT2.CIF.NC I F3";
					}
				}
				break;

			case BUSINESS:
			case NON_CLIENT_BUSINESS:
				allFields = "BUSINESS.NAME:1," + "SHORT.NAME:1," + "Communication Details#PHONE.1:1,"
						+ "Address#ADDRESS:1:1," + "Address#US.STATE," + "Address#POST.CODE:1," + "Address#CITY,"
						+ "Address#ADDR.CNTRY.ID," + "Address#RECORD.REFRESH," + fields + ",";
				allValues = randomCustomer.employerName + "," + randomCustomer.employerName + ","
						+ randomCustomer.phoneMain + "," + randomCustomer.addressStreet + ","
						+ randomCustomer.addressProvince + "," + randomCustomer.addressPostalCode + ","
						+ randomCustomer.addressCity + "," + "CA," + "Y," + values + ",";

				if (BUSINESS.equals(customerType)) {
					if (ROLEBASED_SAE.equals(roleBasedPage)) {
						commandLine = "CUSTOMER,LBC.CAMB.CORP1.SAE I F3";
					} else {
						commandLine = "CUSTOMER,LBC.CAMB.CORP1 I F3";
					}
				} else {
					if (ROLEBASED_SAE.equals(roleBasedPage)) {
						commandLine = "CUSTOMER,LBC.CAMB.CORP1.NC.SAE I F3";
					} else {
						commandLine = "CUSTOMER,LBC.CAMB.CORP1.NC I F3";
					}
				}
				break;

			case DEALER_ADVISOR:
				allFields = "NAME.1:1," + "SHORT.NAME:1," + "L.DEALER.REP.NO," + "Communication Details#EMAIL.1:1,"
						+ "Communication Details#PHONE.1:1," + "Communication Details#SMS.1:1,"
						+ "Communication Details#FAX.1:1," + "Communication Details#OFF.PHONE:1,"
						+ "Address#ADDRESS:1:1," + "Address#US.STATE," + "Address#POST.CODE:1," + "Address#CITY,"
						+ "Address#ADDR.CNTRY.ID," + "Address#RECORD.REFRESH," + fields + ",";
				allValues = randomCustomer.lastName + " " + randomCustomer.firstName + "," + dealerRepNumber + ","
						+ dealerRepNumber + "," + randomCustomer.emailAddress + "," + randomCustomer.phoneMain + ","
						+ randomCustomer.phoneSms + "," + randomCustomer.phoneFax + "," + randomCustomer.phoneOffice
						+ "," + randomCustomer.addressStreet + "," + randomCustomer.addressProvince + ","
						+ randomCustomer.addressPostalCode + "," + randomCustomer.addressCity + "," + "CA," + "Y,"
						+ values + ",";

				commandLine = "CUSTOMER,LBC.AGENT I F3";
				break;
			}

			if (commandLine != null || !commandLine.contains(ERROR)) {
				commandLine(commandLine, commandLineAvailable);

				if (!"".equals(fields)) {
					result = multiInputData(allFields, allValues, false);
				}

				if (ROLEBASED_SAE.equals(roleBasedPage)) {

					result = inputData("External System ID#EXTERN.SYS.ID:1", "FIC", false);
					result = inputData("External System ID#EXTERN.CUS.ID:1", dealerRepNumber, false);

					switchToTab("Other Details", "");
					inputTable.selectionCriteriaButton("COMPANY.BOOK").click();
					switchToPage(LASTPAGE, false);
					enquiryElements.enquirySearch("COMPANY.CODE", "contains", currentBranch);
					enquiryElements.linkToSelect("COMPANY.BOOK").click();

					switchToPage(LASTPAGE, false);
				}

				createdCIF = inputTable.identifier("Basic Details").getText();
				result = inputData("MNEMONIC", "C" + createdCIF, true);
				if (result && !OPEN.equals(action)) {

					Reporter.log("Creating new " + customerType + " customer: " + createdCIF, debugMode);

					toolElements.toolsButton(COMMIT_DEAL).click();

					result = inputTable.verifyAcceptOverride();
					if (result) {
						finalMessage = readTable.message().getText();
						if (!finalMessage.contains(TXN_COMPLETE)) {
							createdCIF = "Error: Problem creating CIF";
						}
						switchToPage(environmentTitle, true);
					} else {
						createdCIF = "Error: Problem creating CIF";
					}

				} else if (!result) {
					createdCIF = "Error: Problem creating CIF";
					Reporter.log(createdCIF, debugMode);
					inputTable.returnButton();
					versionScreen.alertAction("ACCEPT");
					switchToPage(environmentTitle, true);
				}

				if (!createdCIF.contains(ERROR)) {
					createdCustomers.put(createdCIF, randomCustomer);
				}
			}
		} else {
			createdCIF = "Error: API Could not retrieve random values";
		}

		return createdCIF;
	}

	public Map<String, CustomerData> createdCustomers = new ConcurrentHashMap<String, CustomerData>() {
		{

		}
	};

	public String arrangements(final String action, final String productGroup, final String product,
			final String roleBasedPage, final String customer, final String step1Fields, final String step1Values,
			final String step2Fields, final String step2Values) {
		final String customerType = DefaultVariables.productGroupCustomerType.get(productGroup);
		final String arrangementType = DefaultVariables.productGroupArrangementType.get(productGroup);
		final boolean isNonPersonalLoanType = COMMERCIAL_LOANS.equals(productGroup)
				|| RETAIL_MORTGAGES.equals(productGroup);
		final boolean isPersonalLoanType = PERSONAL_LOC.equals(productGroup) || PERSONAL_LOANS.equals(productGroup);
		String arrangement = "";
		String actualRoleBasedPage;
		String finalMessage = "";
		String newDealButton;
		String tabName;
		boolean result = true;
		boolean dataInputed = false;

		if ("".equals(roleBasedPage)) {
			actualRoleBasedPage = DefaultVariables.productLineRoleBasedPage.get(arrangementType);
		} else {
			actualRoleBasedPage = roleBasedPage;
		}

		switch (productGroup) {
		case BUSINESS_ACCOUNTS:
		case PERSONAL_ACCOUNTS:
		case SERVICING_ACCOUNTS:
			tabName = "Products";
			newDealButton = "Create Current & Savings Accounts";
			break;
		case COMMERCIAL_LOANS:
		case PERSONAL_LOANS:
		case PERSONAL_LOC:
		case RETAIL_MORTGAGES:
			tabName = "Loans";
			newDealButton = "Apply New Loan";
			break;
		case BROKER_NONREG_DEPOSITS:
		case BROKER_REG_DEPOSITS:
		case RETAIL_DEPOSITS:
			tabName = "Products";
			newDealButton = "New Deposits";
			break;
		case AGENTS:
		case LBC_BROKER_AGENT:
			tabName = "Product Catalog";
			newDealButton = "";
			break;
		default:
			tabName = "";
			newDealButton = "";
			Reporter.log("Error openning arrangement: " + productGroup, debugMode);
			break;
		}

		if (BUSINESS.equals(customerType) || AGENTS.equals(productGroup) || LBC_BROKER_AGENT.equals(productGroup)) {
			tabName = "main%" + tabName + "%workarea";
		}

		try {
			if (AGENTS.equals(productGroup) || LBC_BROKER_AGENT.equals(productGroup)) {
				commandLine("TAB RETAIL.HP.TAB.BA", commandLineAvailable);
			} else {
				findCIF(customer, customerType, actualRoleBasedPage);
			}
			switchToPage(LASTPAGE, false);
			switchToTab(tabName, "");
			versionScreen.waitForLoading();
			if (!"".equals(newDealButton)) {
				compositeScreen.newButton(newDealButton).click();
				switchToPage(LASTPAGE, false);
			}
			compositeScreen.expandButton(productGroup).click();
			compositeScreen.newArrangement(product).click();
			switchToPage(LASTPAGE, false);
			result = multiInputData(step1Fields, step1Values, true);
			if (action.contains(CREATE) || action.contains(VALIDATE)) {
				if (result) {
					toolElements.toolsButton(VALIDATE_DEAL).click();

					if (isNonPersonalLoanType
							&& inputTable.createArrangementHeaderLinks().getText().contains("Lending Renewal")) {
						inputData("CHANGE.DATE.TYPE", "Date", true);
						inputData("CHANGE.DATE", "+5Y", true);
					}
				} else {
					result = false;
					arrangement = "Error on first screen to create " + productGroup + " : " + product;
					Reporter.log(arrangement, debugMode);
				}

				if (action.contains(CREATE)) {

					if ((isPersonalLoanType || isNonPersonalLoanType) && !step2Fields.contains("LIMIT.SERIAL")) {
						// If we created a new limit for the arrangement, we
						// haven't inputed the step2 data yet
						dataInputed = !validateLimit(productGroup, product, customer, roleBasedPage, step1Fields,
								step1Values, step2Fields, step2Values);
					}

					if (result) {

						if (!dataInputed) {
							result = multiInputData(step2Fields, step2Values, true);
						}

						result = setupRenewalAndInterest(productGroup, product, step2Fields);

						if (AGENTS.equalsIgnoreCase(productGroup)) {
							arrangement = readData("ARRANGEMENT");
						} else {
							arrangement = readData("ACCOUNT.REFERENCE");
						}
						toolElements.toolsButton(COMMIT_DEAL).click();
						result = inputTable.verifyAcceptOverride();

						if (result) {
							finalMessage = readTable.message().getText();
						}

						if (result && finalMessage.contains(TXN_COMPLETE)) {
							switchToPage(environmentTitle, true);
						} else {
							arrangement = "Error: Problem on final screen while creating the Arrangement: "
									+ finalMessage;
							Reporter.log(arrangement, debugMode);
						}
					} else {
						arrangement = "Error: Problem on first screen while creating the Arrangement";
						Reporter.log(arrangement, debugMode);
					}

					if (!arrangement.contains(ERROR)) {
						Reporter.log(
								"Creating " + product + " Arrangement " + arrangement + " for Customer " + customer,
								debugMode);
					}
				}
			}

			if (result && action.contains(AUTHORISE)) {
				result = authorizeEntity(arrangement, ARRANGEMENT + "," + productGroup);
				if (!result) {
					Reporter.log("Error: Problem authorizing the Arrangement " + arrangement, debugMode);
				}
			}

		} catch (NoSuchElementException e) {
			result = false;
			arrangement = "Error opening version to create " + productGroup + " : " + product;
			Reporter.log(arrangement, debugMode);
		}

		return arrangement;
	}

	public boolean validateLimit(final String productGroup, final String product, final String customer,
			final String roleBasedPage, final String step1Fields, final String step1Values, final String step2Fields,
			final String step2Values) {
		String arrangement;
		String actualCustomer;
		String actualStep1Fields = step1Fields;
		String actualStep1Values = step1Values;
		String limitReference;
		String limitSerial;
		String completeLimitId;
		String limitFields = "";
		String limitValues = "";
		String parentLimitIdSAE;
		String limitType;
		String customerType;
		String defaultCIFFields;
		String defaultCIFValues;
		int termIndex;
		boolean result = false;
		boolean dataInputed = false;

		final String[] fields = step1Fields.split(",");
		final String[] values = step1Values.split(",");

		String currency = "";

		for (int i = 0; i < fields.length; i++) {
			if ("CURRENCY".equals(fields[i])) {
				currency = values[i];
				break;
			}
		}

		arrangement = arrangements(VALIDATE, productGroup, product, roleBasedPage, customer, step1Fields, step1Values,
				"", "");
		result = !arrangement.contains(ERROR);

		limitReference = DefaultVariables.revolvingLimitRefs.get(product);
		if (limitReference != null) {
			inputData("LIMIT.REFERENCE", limitReference, false);
		}

		final int currentLimitSerial = getCurrentLimitSerial(customer, limitReference);

		termIndex = Arrays.asList(step2Fields.split(",")).lastIndexOf("TERM");

		if (DefaultVariables.productLimitType.containsKey(product)) {
			limitType = DefaultVariables.productLimitType.get(product);
			limitFields = DefaultVariables.productLimitFields.get(product) + "EXPIRY.DATE,";
			limitValues = DefaultVariables.productLimitValues.get(product) + step2Values.split(",")[termIndex] + ",";
		} else if (product.contains(UNSECURED)) {
			limitType = UNSECURED;
			limitFields = DefaultVariables.unsecuredLimitFields + "EXPIRY.DATE,";
			limitValues = DefaultVariables.unsecuredLimitValues + step2Values.split(",")[termIndex] + ",";
		} else {
			limitType = SECURED;
			limitFields = DefaultVariables.securedLoanLimitFields + "EXPIRY.DATE,";
			limitValues = DefaultVariables.securedLoanLimitValues + step2Values.split(",")[termIndex] + ",";
		}

		customerType = DefaultVariables.productGroupCustomerType.get(productGroup);

		defaultCIFFields = DefaultVariables.customerTypeFields.get(customerType);
		defaultCIFValues = DefaultVariables.customerTypeValues.get(customerType);

		if ("0".equals(Integer.toString(currentLimitSerial))) {
			limitSerial = "01";
			if (ROLEBASED_SAE.equals(roleBasedPage)) {
				parentLimitIdSAE = customerLimit(CREATE, limitType, ROLEBASED_SAE, product, customer, "2400",
						limitSerial, limitFields, limitValues, false);
			}
			completeLimitId = customerLimit(CREATE, limitType, "", product, customer, limitReference, limitSerial,
					limitFields, limitValues);
			limitReference = completeLimitId.substring(13, 17);
			arrangement = arrangements(VALIDATE, productGroup, product, roleBasedPage, customer, step1Fields,
					step1Values, "", "");
			result = !arrangement.contains(ERROR);
			inputData("LIMIT.SERIAL", limitSerial, false);
			inputData("LIMIT.REFERENCE", limitReference, false);
			inputData("SINGLE.LIMIT", "N", true);
		} else if ("99".equals(Integer.toString(currentLimitSerial))) {
			actualCustomer = customer(CREATE, PERSONAL, roleBasedPage, defaultCIFFields, defaultCIFValues);
			updatedValues.add("customer," + actualCustomer);
			limitSerial = "01";

			if (roleBasedPage.contains("SAE")) {
				parentLimitIdSAE = customerLimit(CREATE, limitType, "", product, actualCustomer, "2400", limitSerial,
						limitFields, limitValues, false);
			}
			completeLimitId = customerLimit(CREATE, limitType, "", product, actualCustomer, limitReference, limitSerial,
					limitFields, limitValues);
			limitReference = completeLimitId.substring(10, 14);
			actualStep1Fields = step1Fields + "CUSTOMER:1," + "CURRENCY,";
			actualStep1Values = step1Values + actualCustomer + "," + currency + ",";
			arrangement = arrangements(VALIDATE, productGroup, product, roleBasedPage, customer, actualStep1Fields,
					actualStep1Values, "", "");
			result = !arrangement.contains(ERROR);
			inputData("LIMIT.SERIAL", limitSerial, false);
			inputData("LIMIT.REFERENCE", limitReference, false);
			inputData("SINGLE.LIMIT", "N", true);
		} else {
			limitSerial = Integer.toString(currentLimitSerial);
			arrangement = arrangements(VALIDATE, productGroup, product, roleBasedPage, customer, step1Fields,
					step1Values, "", "");
			result = !arrangement.contains(ERROR);
			dataInputed = multiInputData(step2Fields, step2Values, false);
			inputData("LIMIT.SERIAL", limitSerial, false);
			toolElements.toolsButton(VALIDATE_DEAL).click();
			try {
				final String errorMessage = inputTable.errorMessage().getText();
				if (errorMessage.toLowerCase(Locale.ENGLISH).contains("limit")) {
					if (roleBasedPage.contains("SAE")) {
						parentLimitIdSAE = customerLimit(CREATE, limitType, "", product, customer, "2400", "",
								limitFields, limitValues, false);
					}
					completeLimitId = customerLimit(CREATE, limitType, "", product, customer, limitReference, "",
							limitFields, limitValues);
					limitSerial = completeLimitId.length() > 2 ? completeLimitId.substring(completeLimitId.length() - 2)
							: "";
					switchToPage(environmentTitle, true);
					arrangement = arrangements(VALIDATE, productGroup, product, roleBasedPage, customer, step1Fields,
							step1Values, "", "");
					result = !arrangement.contains(ERROR);
					dataInputed = multiInputData(step2Fields, step2Values, false);
					inputData("LIMIT.SERIAL", limitSerial, false);
					inputData("LIMIT.REFERENCE", limitReference, false);
					inputData("SINGLE.LIMIT", "N", true);
				}
			} catch (NoSuchElementException e) {
				limitSerial = readData("LIMIT.SERIAL");
				completeLimitId = customer + ".000" + limitReference + "." + limitSerial;
			}
		}

		if (dataInputed) {
			Reporter.log("There was no need to create a new Limit; a valid existing limit was used instead", debugMode);
			result = false;
		}

		return result;
	}

	public boolean setupRenewalAndInterest(final String productGroup, final String product, final String step2Fields) {
		List<WebElement> listOfPaymentTypes;
		String linkStr;
		String interestFields;
		String interestValues;
		boolean result = true;

		final boolean isPersonalLoanType = PERSONAL_LOC.equals(productGroup) || PERSONAL_LOANS.equals(productGroup);
		final boolean isNonPersonalLoanType = COMMERCIAL_LOANS.equals(productGroup)
				|| RETAIL_MORTGAGES.equals(productGroup);

		linkStr = inputTable.createArrangementHeaderLinks().getText();
		if (step2Fields.contains("CHANGE.DATE.TYPE") && !linkStr.contains("Renewal")) {
			Reporter.log("Renewal Settings can not be set up ", debugMode);
			result = true;
		} else if (linkStr.contains("Lending Renewal")) {
			result = inputData("CHANGE.PERIOD", "1Y", false);
		}

		if ((isPersonalLoanType || isNonPersonalLoanType) && !HELOC.equals(product)) {
			interestFields = DefaultVariables.productInterestFields.get(product);
			interestValues = DefaultVariables.productInterestValues.get(product);
			if (interestFields != null && !interestFields.isEmpty()) {
				result = multiInputData(interestFields, interestValues, true);
			}

			listOfPaymentTypes = inputTable.listOfFields("PAYMENT.TYPE", "Schedule");

			for (int i = 1; i <= listOfPaymentTypes.size(); i++) {
				if ("SPECIAL".equals(readData("PAYMENT.TYPE:" + i))) {
					result = inputData("ACTUAL.AMT:" + i + ":1", "10000", false)
							&& inputData("Schedule$PAYMENT.FREQ:" + i, readData("PAYMENT.FREQ:1"), false);
					break;
				}
			}
		}

		return result;
	}

	public String createArrangement(final String productGroup, final String product, final String customer,
			final String currency, final String step1Fields, final String step1Values, final String step2Fields,
			final String step2Values) {
		final String actualStep1Fields = "CUSTOMER:1," + "CURRENCY," + step1Fields;
		final String actualStep1Values = customer + "," + currency + "," + step1Values;

		return arrangements(CREATE_AUTHORISE, productGroup, product, "", customer, actualStep1Fields, actualStep1Values,
				step2Fields, step2Values);
	}

	public String financialTransaction(final String action, final String versionName, final String fields,
			final String values) {
		WebElement displayedMessage;
		String fundsTransfer = "";
		String transactionID = null;
		String transactionLabel;
		String authorisationType = FUNDSTRANSFER;
		boolean slashPresent = true;
		boolean result = false;

		Reporter.log(action + " " + versionName + " Financial Transaction", debugMode);

		switch (versionName) {
		case "Inward Remittance":
			commandLine("COS INWARD.REMITTANCES", commandLineAvailable);
			transactionLabel = "Inward Remittance";
			// To be finished
			break;
		case "Outward Remittance - With MT102":
			commandLine("FUNDS.TRANSFER,OT102 I F3", commandLineAvailable);
			transactionLabel = "Outward Remittance";
			break;
		case "Outward Remittance - With MT103":
			commandLine("FUNDS.TRANSFER,OT103.SERIAL.FTHP I F3", commandLineAvailable);
			transactionLabel = "Outward Remittance";
			break;
		case "Outward Remittance - With MT103+":
			commandLine("FUNDS.TRANSFER,OT103.PLUS I F3", commandLineAvailable);
			transactionLabel = "Outward Remittance";
			break;
		case "Outward Remittance - With MT103 Remit":
			commandLine("FUNDS.TRANSFER,OT103.REMIT I F3", commandLineAvailable);
			transactionLabel = "Outward Remittance";
			break;
		case "Outward Remittance - With MT103+202":
			commandLine("FUNDS.TRANSFER,OT103.COVER.FTHP I F3", commandLineAvailable);
			transactionLabel = "Outward SWIFT Remittance (Cover-Direct)";
			break;
		case "Outward Remittance - With MT400":
			commandLine("FUNDS.TRANSFER,OT400.FTHP I F3", commandLineAvailable);
			transactionLabel = "Outward SWIFT Remittance (MT400)";
			break;
		case "Outward Remittance - With MT400+202":
			commandLine("FUNDS.TRANSFER,OT400.202.FTHP I F3", commandLineAvailable);
			transactionLabel = "Outward SWIFT Remittance (MT400+MT202)";
			break;
		case "Account Transfer - Transfer Between Nostro (MT200)":
			commandLine("FUNDS.TRANSFER,ACTR.NOST.FTHP I F3", commandLineAvailable);
			transactionLabel = "Transfer Between Nostro Account(MT200)";
			break;
		case "Account Transfer - Transfer Between Nostro (MT202)":
			commandLine("FUNDS.TRANSFER,ACTTR.NOST.FTHP I F3", commandLineAvailable);
			transactionLabel = "Transfer Between Nostro Accounts(MT202)";
			break;
		case "Escrow Deposit":
			commandLine("FUNDS.TRANSFER,ESCROW.ADHOC.DEPOSIT I F3", commandLineAvailable);
			transactionLabel = "Escrow - Adhoc Deposit";
			break;
		case "Escrow Withdrawal":
			commandLine("FUNDS.TRANSFER,ESCROW.ADHOC.WITHDRAW I F3", commandLineAvailable);
			transactionLabel = "Escrow - Adhoc Withdrawal";
			break;
		case "AA Disbursement":
			commandLine("FUNDS.TRANSFER,AA.ACDI I F3", commandLineAvailable);
			transactionLabel = "AA - Disbursement";
			break;
		case "AA Repayment":
			commandLine("FUNDS.TRANSFER,AA.ACRP I F3", commandLineAvailable);
			transactionLabel = "AA - Repayment";
			break;
		case "AA Advance Repayment":
			commandLine("FUNDS.TRANSFER,AA.ACAR I F3", commandLineAvailable);
			transactionLabel = "AA - Advance Repayment";
			break;
		case "AA Pay-Off":
			commandLine("FUNDS.TRANSFER,LBC.AA.ACPO I F3", commandLineAvailable);
			transactionLabel = "AA - Payoff";
			break;
		case "AA Principal Decrease":
			commandLine("FUNDS.TRANSFER,AA.ACPD I F3", commandLineAvailable);
			transactionLabel = "AA - Principal Decrease";
			break;
		case "AA Credit Arrangement":
			commandLine("FUNDS.TRANSFER,AA.ACCR I F3", commandLineAvailable);
			transactionLabel = "AA - Credit Arrangement";
			break;
		case "AA Deposit - Fund":
			commandLine("FUNDS.TRANSFER,AA.ACDF I F3", commandLineAvailable);
			transactionLabel = "AA - Deposit Funding";
			break;
		case "AA Pay-out":
			commandLine("FUNDS.TRANSFER,AA.ACDP I F3", commandLineAvailable);
			transactionLabel = "AA - Deposit Payout";
			break;
		case "AA Partial Withdrawal":
			commandLine("FUNDS.TRANSFER,AA.ACPP I F3", commandLineAvailable);
			transactionLabel = "AA - Dep Partial Withdrawal";
			break;
		case "Credit UNC Balance":
			commandLine("FUNDS.TRANSFER,LBC.AA.ACCR I F3", commandLineAvailable);
			transactionLabel = "Credit UNC Balance";
			break;
		case "Debit UNC Balance":
			commandLine("FUNDS.TRANSFER,LBC.ACUN I F3", commandLineAvailable);
			transactionLabel = "AA - Withdraw from UNC";
			break;
		case "Apply AdHoc Charges":
			commandLine("FUNDS.TRANSFER,LBC.AA.ACBY I F3", commandLineAvailable);
			transactionLabel = "Adhoc Charge";
			break;
		case "Teller Financial Services":
			commandLine("TELLER.FINANCIAL.SERVICES,CAMB.INPUT2 I F3", commandLineAvailable);
			authorisationType = FUNDSTRANSFER_TFS;
			transactionLabel = "Transaction";
			slashPresent = false;
			break;
		case "Remote Cheque Deposit":
			commandLine("FUNDS.TRANSFER,LBC.ACTR.FTHP.3 I F3", commandLineAvailable);
			transactionLabel = "Remote/Cheque Deposits";
			break;
		case "Deposit Partial Redemption - No Clawback":
			commandLine("FUNDS.TRANSFER,AA.ACPP I F3", commandLineAvailable);
			transactionLabel = "AA - Dep Partial Withdrawal";
			break;
		case "Account Transfer - Transfer Between Accounts":
		default:
			commandLine("FUNDS.TRANSFER,LBC.ACTR.FTHP I F3", commandLineAvailable);
			transactionLabel = "Transfer Between Accounts";
			break;
		}

		if (!"".equals(fields)) {
			result = multiInputData(fields, values, false);
		}

		transactionID = inputTable.identifier(transactionLabel).getText().trim();
		if (slashPresent) {
			transactionID = transactionID.replaceAll("/", "");
		}
		if (action.contains(CREATE)) {
			result = inputTable.commitAndOverride();

			if (result) {
				displayedMessage = readTable.message();
				fundsTransfer = displayedMessage.getText();
				result = fundsTransfer.contains(TXN_COMPLETE);
			} else {
				transactionID = "Error while Creating " + versionName + " Financial Transaction";
			}
		}

		if (action.contains(AUTHORISE) && !transactionID.contains(ERROR)) {
			result = authorizeEntity(transactionID, authorisationType);
			if (!result) {
				transactionID = "Error while Authorising " + versionName + " Financial Transaction";
			}
		}
		return transactionID;
	}

	public String findArrangement(final String tab, final String idValue, final String searchType,
			final String roleBasedPage, final String productGroup, final String product, final String currency) {
		final String enquirySummary = DefaultVariables.findArrangementTableSummary.get(tab + productGroup);
		String result = "";
		String productCode;
		String actualIdValue = idValue;
		String newSearchType = searchType;
		String defaultStep2Fields;
		String defaultStep2Values;
		String actualRoleBasedPage;
		String defaultCIFFields;
		String defaultCIFValues;
		String customerType = PERSONAL;
		final String arrangementType = DefaultVariables.productGroupArrangementType.get(productGroup);
		if (BUSINESS_ACCOUNTS.equals(productGroup) || COMMERCIAL_LOANS.equals(productGroup)) {
			customerType = BUSINESS;
		}

		if ("".equals(roleBasedPage)) {
			actualRoleBasedPage = DefaultVariables.productLineRoleBasedPage.get(arrangementType);
		} else {
			actualRoleBasedPage = roleBasedPage;
		}
		parentHandle = setParentHandle();

		if (CIF.equals(searchType) && !AGENTS.equals(productGroup)) {
			actualIdValue = findCIF(idValue, customerType, "");
		}
		defaultCIFFields = DefaultVariables.customerTypeFields.get(customerType);
		defaultCIFValues = DefaultVariables.customerTypeValues.get(customerType);

		switchToPage(environmentTitle, true);

		switch (productGroup) {
		case RETAIL_DEPOSITS:
		case BROKER_NONREG_DEPOSITS:
		case BROKER_REG_DEPOSITS:
			commandLine("COS AA.FIND.ARRANGEMENT.AD", commandLineAvailable);
			break;
		case COMMERCIAL_LOANS:
		case PERSONAL_LOANS:
		case PERSONAL_LOC:
		case RETAIL_MORTGAGES:
			commandLine("COS AA.FIND.ARRANGEMENT.AL", commandLineAvailable);
			break;
		case AGENTS:
			commandLine("COS AA.FIND.ARRANGEMENT.AG", commandLineAvailable);
			break;
		case PERSONAL_ACCOUNTS:
		case BUSINESS_ACCOUNTS:
		case SERVICING_ACCOUNTS:
		default:
			commandLine("COS AA.FIND.ARRANGEMENT.AR", commandLineAvailable);
			break;
		}

		switchToTab(tab, "");
		enquiryElements.clearEnquiryTextFields();

		switch (searchType) {
		case CIF:
			if (AGENTS.equals(productGroup)) {
				enquiryElements.enquiryElement(TEXTFIELD, CUSTOMER).sendKeys(actualIdValue);
			} else {
				enquiryElements.enquiryElement(TEXTFIELD, OWNER).sendKeys(actualIdValue);
			}
			break;
		case ARRANGEMENT:
		case ARRANGEMENT_AUTHORIZATION:
			enquiryElements.enquiryElement(TEXTFIELD, ARRANGEMENT).sendKeys(idValue);
			break;
		default:
			Reporter.log("Framework cannot handle arrangement searches of this type yet: " + searchType, debugMode);

			break;
		}

		if (!"".equals(product)) {
			enquiryElements.enquiryElement(TEXTFIELD, PRODUCT).clear();
			productCode = DefaultVariables.productCodes.get(product);
			enquiryElements.enquiryElement(TEXTFIELD, PRODUCT).sendKeys(productCode);
		}
		enquiryElements.enquiryElement(TEXTFIELD, CURRENCY).sendKeys(currency);
		enquiryElements.findButton().click();
		switchToPage(LASTPAGE, false);
		try {
			if ("No Data to display"
					.equalsIgnoreCase(enquiryElements.getElementAtCell(enquirySummary, 1, 1).getText())) {
				if (AUTHORISED.equals(tab) && !productGroup.equals(AGENTS)
						&& !ARRANGEMENT_AUTHORIZATION.equals(searchType)) {
					switchToPage(environmentTitle, true);
					switch (searchType) {
					case ARRANGEMENT:
						actualIdValue = customer(CREATE, customerType, actualRoleBasedPage, defaultCIFFields,
								defaultCIFValues);
						if (actualIdValue.contains(ERROR)) {
							break;
						}
						// Do not break, as we continue on the lines of the next
						// case
					case CIF:
					default:
						if (DefaultVariables.productFields.containsKey(product)) {
							defaultStep2Fields = DefaultVariables.productFields.get(product);
							defaultStep2Values = DefaultVariables.productValues.get(product);
						} else {
							defaultStep2Fields = DefaultVariables.productGroupFields.get(productGroup);
							defaultStep2Values = DefaultVariables.productGroupValues.get(productGroup);
						}

						actualIdValue = arrangements(CREATE, productGroup, product, "", actualIdValue,
								"CUSTOMER:1,CURRENCY,", actualIdValue + "," + currency + ",", defaultStep2Fields,
								defaultStep2Values);

						newSearchType = ARRANGEMENT;

						break;
					}
					if (!actualIdValue.contains(ERROR)) {
						updatedValues.add("arrangement," + actualIdValue);
						findArrangement(tab, actualIdValue, newSearchType, actualRoleBasedPage, productGroup, product,
								"");
					}
				} else {
					result = "Error: Could not find Arrangement";
				}
			} else if (enquiryElements.enqHeaderMsg().getText().contains("Result")) {
				enquiryElements.enquiryButtons(OVERVIEW).click();
			} else {
				result = "Error: Could not find Arrangement";
			}
		} catch (NoSuchElementException e) {
			result = "Error: Could not find Arrangement";
		}

		switchToPage(LASTPAGE, false);
		versionScreen.waitForLoading();

		if (!result.contains(ERROR)) {
			try {
				if (productGroup.contains(AGENTS)) {
					result = versionScreen.labelElement(ARRANGEMENT).getText();
				} else {
					result = versionScreen.labelElement("Account").getText().split("\\s+")[0];
				}
			} catch (NoSuchElementException e) {
				result = "Error: Could not find Arrangement";
			}
		}

		return result;
	}

	public boolean requestClosure(final String action, final String arrangement, final String linkLabel,
			final String productGroup, final String closureFields, final String closureValues,
			final String closureMethod, final String settlementFields, final String settlementValues) {
		String requestClosure = "";
		String actualArrangement = arrangement;
		Select dropdown;
		Alert alert;
		boolean result = true;

		Reporter.log("Performing " + action + " Closure" + " on " + productGroup + " arrangement " + arrangement,
				debugMode);

		if (arrangement != "") {
			actualArrangement = findArrangement(AUTHORISED, arrangement, ARRANGEMENT, "", productGroup, "", "");
		}
		switchToPage(LASTPAGE, false);
		versionScreen.waitForLoading();
		compositeScreen.textAction(linkLabel, "Run").click();
		switchToPage(LASTPAGE, false);
		if (actualArrangement.contains(ERROR) || "".equals(actualArrangement)) {
			result = false;
		}

		result = multiInputData(closureFields, closureValues, false);

		switch (action) {
		case OPEN:
		default:
			break;

		case SIMULATE:
		case COMPLETE:
		case COMPLETE_AUTHORISE:

			result = inputTable.commitAndOverride();
			if (result) {
				for (int j = 0; j < MAX_RETRIES && !"Executed - Successfully".equals(requestClosure); j++) {
					switchToPage("Sim Runner Status", false);
					versionScreen.waitForLoading();
					requestClosure = readTable.elementValue("BOLD-ITALIC_AASIMULATIONMONITOR").getText();
				}

				if (action.toUpperCase(Locale.ENGLISH).equals("SIMULATE")) {

					switchToPage("AA Arrangement Simulation Data capture", false);
					requestClosure = readTable.message().getText();
					result = requestClosure.contains(TXN_COMPLETE);

				} else {

					switchToPage("Arrangement Overview", false);
					driver.navigate().refresh();
					alert = driver.switchTo().alert();
					alert.accept();
					versionScreen.waitForLoading();
					compositeScreen.textAction("Closure Statement for", "Payoff Statement").click();
					switchToPage(LASTPAGE, false);

					compositeScreen.switchToFrame(ID, "Validationerror");
					if ("No Errors Encountered".equals(versionScreen.dataDisplayTable("ISSUED").getText())) {

						switchToPage(LASTPAGE, false);
						compositeScreen.switchToFrame(ID, "TotalClosure");
						enquiryElements.enquiryButtons("Continue with Closure").click();
						switchToPage(LASTPAGE, false);
						compositeScreen.switchToFrame(ID, "ALL");
						dropdown = new Select(compositeScreen.actionDropDown("", "drillbox"));

						if ("".equals(closureMethod)) {
							dropdown.selectByVisibleText("Write-Off to P&L");
						} else {
							dropdown.selectByVisibleText(closureMethod);
						}

						versionScreen.activityAction("Choose Closure Method", "", "Select Drilldown").click();
						switchToPage(LASTPAGE, false);
						compositeScreen.switchToFrame(ID, "ALL");

						result = multiInputData(settlementFields, settlementValues, false);

						if (toolElements.toolsButton(COMMIT_DEAL).getAttribute("src").contains("txncommit_dis")) {
							toolElements.toolsButton(VALIDATE_DEAL).click();
						}
						toolElements.toolsButton(COMMIT_DEAL).click();
						result = inputTable.verifyAcceptOverride();

						if (action.toUpperCase(Locale.ENGLISH).equals("COMPLETE AND AUTHORIZE") && result) {
							requestClosure = readTable.message().getText().substring(14, 30);
							enquiryElements.transactionIdField("Payment order").sendKeys(requestClosure);
							toolElements.toolsButton(WRENCH).click();

							if (!toolElements.toolsButton(AUTHORISE_DEAL).getAttribute("src")
									.contains("txnauthorise_dis")) {
								toolElements.toolsButton(AUTHORISE_DEAL).click();
							}
						}

					} else {
						result = false;
					}

					if (result) {
						requestClosure = readTable.message().getText();
						result = requestClosure.contains(TXN_COMPLETE);
					}

				}
			}

			break;

		}

		return result;
	}

	public boolean authorizeEntity(final String idValue, String type) {

		List<WebElement> myList;
		List<WebElement> pendingElement;
		String[] currentUser = new String[4];
		String message = "";
		String bannerText;
		String productGroup = "";
		String attributeValue = "";
		String commandLine = "";
		String enquiryLabel = "";
		boolean loginResult;
		boolean result = true;
		boolean wrenchAuthorization = false;

		if (type.startsWith(ARRANGEMENT) || type.startsWith(ACTIVITY)) {
			// Break the type into type and productGroup, if it's an Arrangement

			productGroup = type.substring(type.indexOf(',', 0) + 1, type.length());

			type = type.substring(0, type.indexOf(',', 0));
		}

		switchToPage(environmentTitle, true);

		switchToFrame(homePg.bannerFrame());
		bannerText = homePg.bannerText().getText().split(" ")[0];
		currentUser = loginPg.getCurrentUser(bannerText);

		homePg.homePageLinks("Sign off").click();
		loginResult = loginPg.login(currentUser[2], currentUser[3]);
		if (loginResult) {
			Reporter.log("Authorizing " + productGroup + " " + type + ": " + idValue, debugMode);
			if (currentBranch != null) {
				switchToBranch(currentBranch);
			}
			switch (type) {
			case ARRANGEMENT:

				message = findArrangement(UNAUTHORISED, idValue, ARRANGEMENT_AUTHORIZATION, "", productGroup, "", "");

				if (message.contains(ERROR)) {
					message = findArrangement(AUTHORISED, idValue, ARRANGEMENT_AUTHORIZATION, "", productGroup, "", "");
					if (message.contains(ERROR)) {
						message = findArrangement(MATURED_CLOSED, idValue, ARRANGEMENT_AUTHORIZATION, "", productGroup,
								"", "");
						if (message.contains(ERROR)) {
							Reporter.log(message + " to authorize: " + idValue, debugMode);
							result = false;
						} else {
							Reporter.log("Arrangement was closed: " + idValue, debugMode);
							result = true;
						}
					} else {
						Reporter.log("Arrangement was already authorized: " + idValue, debugMode);
						result = true;
					}
				} else {
					compositeScreen.actionButton("").click();
					switchToPage(LASTPAGE, false);

					toolElements.toolsButton(AUTHORISE_DEAL).click();
					switchToPage(LASTPAGE, false);
					message = readTable.message().getText();
					if (!message.contains(TXN_COMPLETE)) {
						result = false;
					}
				}
				break;
			case POSTING_RESTRICT:
				commandLine("COS CUST.POST.RESTRICT.AUTH", commandLineAvailable);
				enquiryElements.enquiryButtons(SELECTION_SCREEN).click();
				switchToPage(LASTPAGE, false);
				enquiryElements.enquirySearch(CUSTOMER, "", idValue);
				switchToPage(LASTPAGE, false);
				toolElements.authorise(idValue).click();
				toolElements.toolsButton(AUTHORISE_DEAL).click();
				switchToPage(LASTPAGE, false);
				message = readTable.message().getText();

				if (message.contains(ERROR)) {
					result = false;
				}
				break;

			case ACTIVITY:
				String pending = "";
				findArrangement(AUTHORISED, idValue, ARRANGEMENT, "", productGroup, "", "");
				pendingElement = inputTable.pendingElements();
				if (pendingElement == null) {
					Reporter.log("There was no Activity to Authorize on Arrangement", true);
				} else {
					for (int i = 0; i < pendingElement.size(); i++) {
						pending = inputTable.pendingElements().get(i).getAttribute("class");
						if (pending.contains("PENDING")) {
							pending = inputTable.pendingActivity().getText();
							break;
						}
					}
					if (pending.contains("Pending Approval") || pending.contains("Pending test Approval")) {
						myList = compositeScreen.actionButtonlist();

						Reporter.log("Authorizing Pending Activities", debugMode);

						for (int i = 0; i < myList.size(); i++) {
							myList.get(i).click();
							switchToPage(LASTPAGE, false);
							toolElements.toolsButton(AUTHORISE_DEAL).click();
							message = readTable.message().getText();
							if (!message.contains(TXN_COMPLETE)) {
								result = false;
							}
							switchToPage("Arrangement Overview", false);
						}

					} else {
						Reporter.log("There was no Activity to Authorize on Arrangement", true);
					}
				}
				break;

			case LIMIT:
				commandLine("COS LIMIT.AUTH", commandLineAvailable);
				toolbarElements.selectionScreen().click();
				enquiryElements.enquirySearch("@ID", "", idValue);

				switchToPage(LASTPAGE, false);

				if (versionScreen.dataDisplayTable("DATA_DISPLAY").getText().contains("No Records to Display")) {
					Reporter.log("There was no limit to authorize");
				} else {
					enquiryElements.enquiryButtons("Authorise").click();
					toolElements.toolsButton(AUTHORISE_DEAL).click();
				}
				break;

			case LIMIT_SAE:
				commandLine("COS LBC.LIMITS", commandLineAvailable);
				toolbarElements.selectionScreen().click();
				enquiryElements.enquirySearch("@ID", "", idValue);

				switchToPage(LASTPAGE, false);

				if (versionScreen.dataDisplayTable("DATA_DISPLAY").getText().contains("No Records to Display")) {
					Reporter.log("There was no SAE limit to authorize");
				} else {
					enquiryElements.enquiryButtons("Authorise").click();
					toolElements.toolsButton(AUTHORISE_DEAL).click();
				}

				break;

			case COLLATERAL_LINK:
				wrenchAuthorization = true;
				commandLine = "COLLATERAL.RIGHT,INP";
				enquiryLabel = COLLATERAL_LINK;
				break;

			case COLLATERAL:
			case COLLATERAL_DETAILS:
			case COLLATERAL_DETAILS_INVESTMENT:
			case COLLATERAL_DETAILS_MACHINERY:
			case COLLATERAL_DETAILS_REAL_ESTATE:
			case COLLATERAL_DETAILS_LOANS:
				wrenchAuthorization = true;
				commandLine = "COLLATERAL,INP";
				enquiryLabel = COLLATERAL_DETAILS;
				break;

			case ESCROW_ACCOUNT:
				wrenchAuthorization = true;
				commandLine = "ESCROW.ACCOUNT,NEW I";
				enquiryLabel = ESCROW_ACCOUNT;
				break;

			case BENEFICIARY:
				wrenchAuthorization = true;
				commandLine = "BENEFICIARY,CAMB";
				enquiryLabel = "Beneficiary Details for ACH/CHQ";
				break;

			case ADHOC_CHEQUE:
				wrenchAuthorization = true;
				commandLine = "PAYMENT.ORDER,LBC.CAMB.EFT.ACH.2";
				enquiryLabel = "Payment Order for Ad-hoc Cheque";
				break;

			case ADHOC_EFT:
				wrenchAuthorization = true;
				commandLine = "PAYMENT.ORDER,LBC.CAMB.EFT.ACH.1";
				enquiryLabel = "Payment Order for Ad-hoc EFT";
				break;

			case ESCROW_PAYEE:
				wrenchAuthorization = true;
				commandLine = "ESCROW.PAYEE,INPUT I";
				enquiryLabel = ESCROW_PAYEE;
				break;

			case FUNDSTRANSFER:
				wrenchAuthorization = true;
				commandLine = "FUNDS.TRANSFER";
				enquiryLabel = "FUNDS.TRANSFER";
				break;

			case FUNDSTRANSFER_TFS:
			case "TFS":
				commandLine("ENQ TFS.NAU", commandLineAvailable);
				enquiryElements.enquirySearch("TXN.REF", "", idValue);
				switchToPage(LASTPAGE, false);

				if (enquiryElements.unauthorisedFT().getText().contains("No Pending Transactions")) {
					Reporter.log("No Pending Transactions to Authorized: " + idValue, true);
					switchToPage(LASTPAGE, false);
				} else {
					toolElements.toolsButton("Authorise").click();
					switchToPage(LASTPAGE, false);
					attributeValue = toolElements.toolsButtonTitle(AUTHORISE_DEAL).getAttribute(HREF);

					if (attributeValue != null) {
						toolElements.toolsButton(AUTHORISE_DEAL).click();
						result = inputTable.verifyAcceptOverride();
						switchToPage(LASTPAGE, false);
						message = readTable.message().getText();
						if (!message.contains(TXN_COMPLETE)) {
							result = false;
						}
					} else {
						Reporter.log("FundsTransfer TFS is already authorized: " + idValue, true);
						toolElements.toolsButton(RETURN_TO_SCREEN).click();
						switchToPage(LASTPAGE, false);
					}
				}

				break;

			case PRODUCT_TRANSIT_CHANGE:
				wrenchAuthorization = true;
				commandLine = "EB.COMPANY.CHANGE";
				enquiryLabel = "EB.COMPANY.CHANGE";
				break;

			case STANDING_ORDER:
				wrenchAuthorization = true;
				commandLine = "STANDING.ORDER,FIXAMTSAE";
				enquiryLabel = "Fixed amount";

				break;

			case ADDRESS:
				wrenchAuthorization = true;
				commandLine = "DE.ADDRESS";
				enquiryLabel = "DE.ADDRESS";

				break;

			default:
				Reporter.log("Framework does not know how to authorize this entity yet: " + type, debugMode);
				result = false;
				break;
			}

			if (wrenchAuthorization) {
				commandLine(commandLine, commandLineAvailable);
				switchToPage(LASTPAGE, false);
				enquiryElements.transactionIdField(enquiryLabel).sendKeys(idValue);
				toolElements.toolsButton(WRENCH).click();
				switchToPage(LASTPAGE, false);
				attributeValue = toolElements.toolsButtonTitle(AUTHORISE_DEAL).getAttribute(HREF);

				if (attributeValue == null) {
					Reporter.log(type + " is already authorised: " + idValue, true);
					toolElements.toolsButton(RETURN_TO_SCREEN).click();
					switchToPage(LASTPAGE, false);
				} else {
					toolElements.toolsButton(AUTHORISE_DEAL).click();
					result = inputTable.verifyAcceptOverride() && switchToPage(LASTPAGE, false)
							&& readTable.message().getText().contains(TXN_COMPLETE);
				}
			}

			switchToPage(environmentTitle, true);
			switchToFrame(homePg.bannerFrame());
			homePg.homePageLinks("Sign off").click();
		} else {
			Reporter.log(
					"Login as Authoriser failed: " + currentUser[2] + " is the authorizer account for " + bannerText,
					debugMode);
			result = false;
		}

		result &= loginPg.login(currentUser[0], currentUser[1]);

		if (currentBranch != null) {
			switchToBranch(currentBranch);
		}

		return result;
	}

	public String findCIF(final String customer, final String customerType, final String roleBasedPage) {
		String result = customer;
		String viewButtonLabel;
		String actualCustomerType = customerType;
		String defaultCIFFields;
		String defaultCIFValues;
		parentHandle = setParentHandle();
		if ("".equals(customerType)) {
			actualCustomerType = PERSONAL;
		}

		defaultCIFFields = DefaultVariables.customerTypeFields.get(customerType);
		defaultCIFValues = DefaultVariables.customerTypeValues.get(customerType);

		switch (roleBasedPage) {
		case ROLEBASED_LENDING:
			commandLine("ENQ LBC.LOAN.CUST.SRC.CIF", commandLineAvailable);
			enquiryElements.clearEnquiryTextFields();
			enquiryElements.enquiryElement(TEXTFIELD, CUSTOMER).clear();
			enquiryElements.enquiryElement(TEXTFIELD, CUSTOMER).sendKeys(customer);
			viewButtonLabel = "Customer Centric View";
			break;
		case ROLEBASED_SAE:
			commandLine("ENQ LBC.SAE.CUST.SRC.CIF", commandLineAvailable);
			enquiryElements.clearEnquiryTextFields();
			enquiryElements.enquiryElement(TEXTFIELD, CUSTOMER).clear();
			enquiryElements.enquiryElement(TEXTFIELD, CUSTOMER).sendKeys(customer);
			viewButtonLabel = "Customer Centric View";
			break;
		case "Finance":
			commandLine("ENQ CUSTOMER.BR.OPS", commandLineAvailable);
			enquiryElements.clearEnquiryTextFields();
			enquiryElements.enquiryElement(TEXTFIELD, "Customer ID").clear();
			enquiryElements.enquiryElement(TEXTFIELD, "Customer ID").sendKeys(customer);
			viewButtonLabel = "Single Customer View";
			break;
		case ROLEBASED_BANKING:
		case ROLEBASED_OR:
		default:
			if (DEALER_ADVISOR.equals(customerType)) {
				commandLine("ENQ LBC.AGENT.SCV", commandLineAvailable);
				enquiryElements.clearEnquiryTextFields();
				enquiryElements.enquiryElement(TEXTFIELD, "Agent ID").clear();
				enquiryElements.enquiryElement(TEXTFIELD, "Agent ID").sendKeys(customer);
				viewButtonLabel = "Single Customer View";
			} else {
				commandLine("ENQ CAMB.CUST.SRC.CIF", commandLineAvailable);
				enquiryElements.clearEnquiryTextFields();
				enquiryElements.enquiryElement(TEXTFIELD, CUSTOMER).clear();
				enquiryElements.enquiryElement(TEXTFIELD, CUSTOMER).sendKeys(customer);
				viewButtonLabel = "Customer Centric View";
			}
			break;
		}

		enquiryElements.findButton().click();
		switchToPage(LASTPAGE, false);
		if (!commandLineAvailable) {
			compositeScreen.switchToFrame(ID, "NAME");
			compositeScreen.switchToFrame(ID, "workarea");
		}
		// compositeScreen.switchToFrame("id", "top");
		final String customerStatus = enquiryElements.enqHeaderMsg().getText();
		if ("No data to display".equalsIgnoreCase(customerStatus) && !customer.contains(ERROR)) {
			result = customer(CREATE, actualCustomerType, roleBasedPage, defaultCIFFields, defaultCIFValues);
			updatedValues.add("customer," + result);
			result = findCIF(result, actualCustomerType, roleBasedPage);
		} else if (!customer.contains(ERROR)) {
			try {
				enquiryElements.enquiryButtons(viewButtonLabel).click();
				switchToPage(LASTPAGE, false);
			} catch (NoSuchElementException e) {
				result = customer(CREATE, actualCustomerType, roleBasedPage, defaultCIFFields, defaultCIFValues);
				updatedValues.add("customer," + result);
				result = findCIF(result, actualCustomerType, roleBasedPage);
			}
		} else {
			result = customer;
		}
		return result;
	}

	public boolean findStatement(final String arrangement) {
		boolean statementFound = false;
		try {
			findArrangement(AUTHORISED, arrangement, ARRANGEMENT, "", PERSONAL_ACCOUNTS, "", "");
			switchToPage(LASTPAGE, false);

			compositeScreen.additionalDetailsStatement().click();

			statementFound = true;
		}

		catch (NoSuchElementException e) {
			Reporter.log(e.getMessage(), false);
		}

		return statementFound;
	}

	public boolean fundsHold(final String product, final String actionToPerform, final String arrangement,
			final String startDate, final String endDate, final String amount, final String updatedStartDate,
			final String updatedEndDate, final String updatedAmount) {
		boolean result = true;
		WebElement displayedMessage;
		String fundsHold = "";

		Reporter.log(actionToPerform + "  on arrangement " + arrangement, debugMode);

		findArrangement(AUTHORISED, arrangement, ARRANGEMENT, "", PERSONAL_ACCOUNTS, product, CAD);
		switchToPage(LASTPAGE, false);

		switch (actionToPerform) {

		case "updateAmountFundsHold":

			versionScreen.linkText("Account Details", "Locked Funds").click();
			toolElements.toolsButton(AMEND).click();
			switchToPage(LASTPAGE, false);
			result = inputData("FROM.DATE", updatedStartDate, false) && inputData("TO.DATE", updatedEndDate, false)
					&& inputData("LOCKED.AMOUNT", updatedAmount, false);
			toolElements.toolsButton(COMMIT_DEAL).click();
			result = inputTable.verifyAcceptOverride();
			break;

		case "removeFundsHold":

			versionScreen.linkText("Account Details", "Locked Funds").click();
			toolElements.toolsButton(REVERSE).click();

			switchToPage(LASTPAGE, false);

			toolElements.toolsButton(REVERSE_DEAL).click();
			result = true;
			break;

		case "createFundsHold":
		default:

			versionScreen.linkText("Facilities and Conditions", FACILITIES).click();
			versionScreen.linkText(FACILITIES, "Block Funds").click();

			result = inputData("FROM.DATE", startDate, false) && inputData("TO.DATE", endDate, false)
					&& inputData("LOCKED.AMOUNT", amount, false);
			toolElements.toolsButton(COMMIT_DEAL).click();

			result = inputTable.verifyAcceptOverride();
			break;

		}
		if (result) {
			displayedMessage = readTable.message();
			fundsHold = displayedMessage.getText();
		}

		if (fundsHold.contains(ERROR)) {
			result = false;
		}

		switchToPage(environmentTitle, true);
		return result;
	}

	public String customerLimit(final String action, final String limitType, final String roleBasedPage,
			final String product, final String customer, final String limitReference, final String limitSerial,
			final String fields, final String values) {
		return customerLimit(action, limitType, roleBasedPage, product, customer, limitReference, limitSerial, fields,
				values, true);
	}

	public String customerLimit(final String action, final String limitType, final String roleBasedPage,
			final String product, final String customer, final String limitReference, final String limitSerial,
			final String fields, final String values, final boolean createCollateral) {
		String completeLimitId = "";
		String actualLimitSerial = "";
		String actualLimitRef;
		String commandLine;
		String authorizationType;
		String limitAction;
		String actionButton;
		int limitSerialInteger;
		boolean localResult = true;
		boolean actualPage = false;
		boolean authorizeLimit;

		if ("".equals(limitReference)) {
			if (limitType.contains("Non-Revolving")) {
				actualLimitRef = DefaultVariables.nonrevolvingLimitRefs.get(product);
			} else {
				actualLimitRef = DefaultVariables.revolvingLimitRefs.get(product);
			}
			if ("".equals(actualLimitRef)) {
				actualLimitRef = "2300";
			}
		} else {
			actualLimitRef = limitReference;
		}
		if ("".equals(limitSerial)) {
			limitSerialInteger = getCurrentLimitSerial(customer, actualLimitRef);
			if (limitSerialInteger < 9) {
				actualLimitSerial = "0" + Integer.toString(limitSerialInteger + 1);
			} else {
				actualLimitSerial = Integer.toString(limitSerialInteger + 1);
			}
		} else {
			actualLimitSerial = limitSerial;
		}

		switch (roleBasedPage) {
		case ROLEBASED_SAE:
			commandLine = "TAB LBC.CREATE.LIMIT.CO.SAE";
			authorizationType = LIMIT_SAE;
			authorizeLimit = DefaultVariables.authorizeSAELimit;
			break;

		default:
			commandLine = "TAB CREATE.LIMIT.CO";
			authorizationType = LIMIT;
			authorizeLimit = DefaultVariables.authorizeB2BLimit;
			break;
		}

		switchToPage(environmentTitle, true);
		completeLimitId = customer + ".000" + actualLimitRef + "." + actualLimitSerial;
		Reporter.log(action + " Limit " + completeLimitId, debugMode);

		switch (action) {
		case OPEN:
		case VALIDATE:
		case CREATE:
		case CREATE_AUTHORISE:

			commandLine(commandLine, commandLineAvailable);
			compositeScreen.switchToFrame(ID, "workarea");
			compositeScreen.limitLink(limitType).click();
			for (int i = 0; i < MAX_RETRIES && !actualPage; i++) {
				try {
					switchToPage(LASTPAGE, false);
					compositeScreen.switchToFrame(ID, "workarea");
					compositeScreen.inputField().sendKeys(completeLimitId);
					compositeScreen.inputField().sendKeys(Keys.ENTER);
					toolElements.toolsButton(VALIDATE_DEAL);
					actualPage = true;
				} catch (NoSuchElementException e) {
					actualPage = false;
				}
			}

			if (!"".equals(fields)) {
				completeLimitId = compositeScreen.completeLimitId().getText();
				localResult = multiInputData(fields, values, false)
						&& inputData("REVIEW.FREQUENCY", readData("EXPIRY.DATE") + " M0101", false);
			}
			if (!localResult) {
				completeLimitId = "Error: Could not " + action + " limit";
				Reporter.log(completeLimitId, debugMode);
			}
			if (localResult && action.contains(VALIDATE)) {
				try {
					toolElements.toolsButton(VALIDATE_DEAL).click();
				} catch (NoSuchElementException e) {
					completeLimitId = "Error: Could not " + action + " limit";
					Reporter.log(e.getMessage(), false);
					Reporter.log(completeLimitId, debugMode);
				}
			}

			if (localResult && action.contains(CREATE)) {
				try {

					localResult = inputTable.commitAndOverride() && authorizeLimit
							? authorizeEntity(completeLimitId, authorizationType)
							: true;

					authorizeLimit = false;

					if (!limitType.contains(UNSECURED) && !limitType.contains("Child") && createCollateral) {
						String collateralLink;
						String collateralLinkFields = "COLLATERAL.CODE," + "COMPANY:1," + "LIMIT.REF.CUST:1,"
								+ "LIMIT.REFERENCE:1,";
						String collateralLinkValues = "1," + "B2B," + customer + "," + completeLimitId + ",";
						collateralLink = collateral(CREATE, COLLATERAL_LINK, customer, collateralLinkFields,
								collateralLinkValues);
						String collateralFields = DefaultVariables.collateralFields + "Collateral details#CA.ADR.LINE1,"
								+ "Collateral details#TOWN.CITY," + "Collateral details#US.STATE,"
								+ "Collateral details#CA.POST.CODE," + "Collateral#COLLATERAL.CODE,"
								+ "COLLATERAL.TYPE,";
						String collateralValues = DefaultVariables.collateralValues
								+ createdCustomers.get(customer).getAddressStreet() + ","
								+ createdCustomers.get(customer).getAddressCity() + ","
								+ createdCustomers.get(customer).getAddressProvince() + ","
								+ createdCustomers.get(customer).getaddressPostalCode() + "," + "1," + "1,";

						collateral(CREATE, COLLATERAL_DETAILS, collateralLink, collateralFields, collateralValues);
					}
				} catch (NoSuchElementException e) {
					completeLimitId = "Error: Could not " + action + " limit";
					Reporter.log(e.getMessage(), false);
					Reporter.log(completeLimitId, debugMode);
				}
			}

			if (!localResult) {
				completeLimitId = "Error: Could not " + action + " limit";
				Reporter.log(completeLimitId, true);
				inputTable.returnButton();
				versionScreen.alertAction("ACCEPT");
			}

			break;
		case CORRECT:
		case CORRECT_AUTHORISE:
		case DELETE_CORRECTION:
		case DELETE_CORRECTION_AUTHORISE:

			commandLine(commandLine, commandLineAvailable);
			compositeScreen.switchToFrame(ID, "main");
			switchToTab("Amend Limits", "");
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "workarea");
			toolElements.toolsButton("Selection Screen").click();
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "workarea");
			enquiryElements.enquirySearch("Limit ID", "contains", completeLimitId);
			switchToPage(LASTPAGE, false);

			if (action.contains("Delete")) {
				limitAction = "Delete Limit";
				actionButton = DELETE_DEAL;
			} else {
				limitAction = "Amend Limit";
				actionButton = COMMIT_DEAL;

			}

			versionScreen.activityAction(customer + ".000" + actualLimitRef, "", limitAction).click();
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "workarea");
			multiInputData(fields, values, false);
			try {
				toolElements.toolsButton(actionButton).click();
				localResult = inputTable.verifyAcceptOverride();

			} catch (NoSuchElementException e) {
				completeLimitId = "Error: Could not " + action + " limit";
				Reporter.log(e.getMessage(), false);
				Reporter.log(completeLimitId, debugMode);
			}

			break;
		case AMEND:
		case AMEND_AUTHORISE:

			commandLine("COS LBC.LIMIT.AMEND", commandLineAvailable);
			toolElements.toolsButton(SELECTION_SCREEN).click();
			switchToPage(LASTPAGE, false);
			enquiryElements.enquirySearch("Limit ID", "", completeLimitId);
			switchToPage(LASTPAGE, false);
			versionScreen.activityAction(completeLimitId, "", AMEND).click();
			switchToPage(LASTPAGE, false);
			multiInputData(fields, values, false);
			inputData("REVIEW.FREQUENCY", readData("EXPIRY.DATE") + " M0101", false);
			toolElements.toolsButton(COMMIT_DEAL).click();
			localResult = inputTable.verifyAcceptOverride();
			break;
		case REVERSE:
		case REVERSE_AUTHORISE:

			localResult = reverseEntity(completeLimitId, authorizationType);
			break;
		default:
			completeLimitId = "Error: Framework cannot handle action " + action;
			Reporter.log(completeLimitId, debugMode);
		}

		if (localResult && authorizeLimit) {
			localResult = authorizeEntity(completeLimitId, authorizationType);
		}

		if (!localResult) {
			completeLimitId = "Error: Could not " + action + " limit";
			Reporter.log(completeLimitId);
		}

		return completeLimitId;
	}

	public int getCurrentLimitSerial(final String customer, final String limitReference) {
		String[] headerMessageSplit;
		String headerMessage;
		int currentLimitSerial;

		commandLine("COS LBC.LIMIT.AMEND", commandLineAvailable);
		toolbarElements.selectionScreen().click();
		enquiryElements.enquirySearch("Limit ID", "begins with", customer + ".000" + limitReference);
		switchToPage(LASTPAGE, false);
		headerMessage = enquiryElements.enqHeaderMsg().getText();
		if ("No data to display".equals(headerMessage)) {
			currentLimitSerial = 0;
		} else {
			headerMessageSplit = headerMessage.split("\\s");
			currentLimitSerial = Integer.parseInt(headerMessageSplit[5]);
		}

		Reporter.log("Customer " + customer + " has " + currentLimitSerial + " Limits", debugMode);

		return currentLimitSerial;

	}

	public void commandLine(final String command, final boolean commandLineAvailable) {
		String menuNavigation = "";
		String navigationArray[];
		String subNavigationArray[] = null;

		switchToPage(environmentTitle, true);

		if (commandLineAvailable) {
			switchToFrame(homePg.bannerFrame());
			homePg.commandLineField().clear();
			homePg.commandLineField().click();
			homePg.commandLineField().sendKeys(command);
			homePg.commandLineField().sendKeys(Keys.ENTER);
			switchToPage(LASTPAGE, false);

		} else {
			menuNavigation = DefaultVariables.commandLineEntries.get(command);
			navigationArray = menuNavigation.split(">");
			compositeScreen.switchToFrame(ID, "menu");

			for (int i = 0; i < navigationArray.length; i++) {
				if (navigationArray[i].contains("&&&")) {
					subNavigationArray = navigationArray[i].split("&&&");
					versionScreen.menuLink("text", subNavigationArray[0]).click();
					switchToPage(LASTPAGE, false);
					driver.switchTo().defaultContent();
					compositeScreen.switchToFrame(ID, subNavigationArray[1].trim());
					if (subNavigationArray[2].contains("#")) {
						compositeScreen.switchToFrame(ID, "NAME");
						compositeScreen.switchToFrame("target", "workarea");
						switchToTab(subNavigationArray[2].trim().substring(1), "");
					} else {
						versionScreen.menuSpan(subNavigationArray[2]).click();
						if (subNavigationArray.length > 3) {
							versionScreen.menuSpan(subNavigationArray[3]).click();
						}
					}
				} else {

					if (i < (navigationArray.length - 1)) {
						if (i == 0) {

							for (int j = 0; j < MAX_RETRIES && versionScreen.menuSpanImageParent(navigationArray[i])
									.getAttribute("src").contains("menu_down.gif"); j++) {
								versionScreen.menuSpan(navigationArray[i]).click();

								if (j + 1 == MAX_RETRIES) {
									Reporter.log("Maximum attempts reached for function commandLine", debugMode);
								}
							}

						} else {

							for (int j = 0; j < MAX_RETRIES
									&& versionScreen.menuSpanImageChild(navigationArray[i - 1], navigationArray[i])
											.getAttribute("src").contains("menu_down.gif"); j++) {
								versionScreen.menuSpan(navigationArray[i - 1], navigationArray[i]).click();

								if (j + 1 == MAX_RETRIES) {
									Reporter.log("Maximum attempts reached for function commandLine", debugMode);
								}
							}

						}

					} else {
						versionScreen.menuLink("command", command).click();
					}
				}
			}
			switchToPage(LASTPAGE, false);
			if (subNavigationArray[2].contains("#")) {
				compositeScreen.switchToFrame(ID, "NAME");
				compositeScreen.switchToFrame(ID, "workarea");
			}
		}
	}

	public String setUpAgent(final String productGroup, final String agentCode, final String product,
			final String effectiveDate) {
		final Random randomizer = new Random();
		String agentProduct = null;
		String agentCustomer = "";
		String agentCommArrangement = "";
		String returnValues = "";
		int randomChanceProduct;
		boolean createNewAgent = false;

		switch (productGroup) {
		case PERSONAL_ACCOUNTS:
		case PERSONAL_LOC:
		case BUSINESS_ACCOUNTS:
			agentProduct = "Account LOC Commission Plan";
			break;
		case BROKER_NONREG_DEPOSITS:
		case BROKER_REG_DEPOSITS:
			agentProduct = "Deposit Commission Plan";
			break;
		case RETAIL_DEPOSITS:
			agentProduct = ""; // Special agent needs to create
			break;
		case COMMERCIAL_LOANS:
		case PERSONAL_LOANS:
		case RETAIL_MORTGAGES:
			agentProduct = "Dummy Commission Plan";
			break;
		case "TBD":
		default:
			switch (product) {
			case "Var Rate Closed Term MG Fix Pay":
			case "Var Rate Closed Term Mrtg Var Pay":
			case "HELOC":

				randomChanceProduct = randomizer.nextInt(5);

				switch (randomChanceProduct) {
				case 0:
					agentProduct = "General Broker Commission Plan";
					break;
				case 1:
					agentProduct = "PFS Commission Plan";
					break;
				case 2:
					agentProduct = "PMPP Commission Plan";
					break;
				case 3:
					agentProduct = "TMACC Commission Plan";
					break;
				case 4:
				default:
					agentProduct = "Verico-Upfront Commission Plan";
					break;
				}

				break;

			case "Mortgages AGF Trust (grandfathered)":
			case "Bridge Loan":
			case "Convertible Mortgage":
			case "Fixed Rate Closed Term Mortgage":
			case "Fixed Rate Open Term Mortgage":
			case "HELOC CMHC AGF Trust(Grandfathered)":
			case "Real Estate Mortgage(grandfathered)":
			default:

				randomChanceProduct = randomizer.nextInt(7);

				switch (randomChanceProduct) {
				case 0:
					agentProduct = "PFS Volume Bonus Commission Plan";
					break;
				case 1:
					agentProduct = "PMPP TMACC Volume Bonus Plan";
					break;
				case 2:
					agentProduct = "General Broker VB Commission Plan";
					break;
				case 3:
					agentProduct = "DLC VB GT 52% Commission Plan";
					break;
				case 4:
					agentProduct = "DLC VB LT 52% Commission Plan";
					break;
				case 5:
					agentProduct = "RBC Commission Plan";
					break;
				case 6:
				default:
					agentProduct = "Royalty-Trailer Commission Plan";
					break;
				}
			}
			break;
		}
		if (!"".equals(effectiveDate) && "".equals(agentCode)) {
			createNewAgent = true;
		}
		if (!"".equals(agentProduct) && !createNewAgent) {
			agentCommArrangement = findArrangement(AUTHORISED, agentCode, CIF, "", AGENTS, agentProduct, CAD);
			if ("Error: Could not find Arrangement".equals(agentCommArrangement)) {
				createNewAgent = true;
			} else {
				switchToPage("Arrangement Overview (Agent)", false);
				versionScreen.viewArrangement().click();
				switchToPage("AA ARRANGEMENT ACTIVITY", false);
				agentCustomer = inputTable.customerNumber().getText().trim();
				toolElements.toolsButton(RETURN_TO_SCREEN).click();

				if ("Could not find Arrangement".equals(agentCommArrangement)) {
					createNewAgent = true;
				}
			}
		}
		if (createNewAgent) {
			agentCustomer = customer(CREATE, DEALER_ADVISOR, ROLEBASED_OR, DefaultVariables.dealerAdvisorCIFFields,
					DefaultVariables.dealerAdvisorCIFValues);
			agentCommArrangement = arrangements(CREATE, AGENTS, agentProduct, "", agentCustomer,
					"CUSTOMER:1," + "CURRENCY," + "EFFECTIVE.DATE,",
					agentCustomer + "," + CAD + "," + effectiveDate + ",", "", "");
		}

		returnValues = agentCustomer + "," + agentCommArrangement + "," + "Broker,";

		return returnValues;
	}

	public boolean arrangementMemo(final String action, final String arrangement, final String productGroup,
			final String fields, final String values) {
		boolean result = true;

		Reporter.log(action + " Arrangement Memo for " + productGroup + " arrangement " + arrangement, debugMode);

		findArrangement(AUTHORISED, arrangement, ARRANGEMENT, "", productGroup, "", "");
		switch (action) {
		case CREATE:
		case AMEND:

			try {
				if (CREATE.equals(action)) {
					versionScreen.linkText("Memos Overview", "New Memo").click();
				} else {
					versionScreen.memoAction("Modify").click();
				}
				switchToPage(LASTPAGE, false);
			} catch (NoSuchElementException e) {
				result = false;
			}

			if (result) {
				result = multiInputData(fields, values, false);
			}
			if (result) {
				toolElements.toolsButton(COMMIT_DEAL).click();
				result = inputTable.verifyAcceptOverride();
			}
			if (result) {
				result = readTable.message().getText().contains(TXN_COMPLETE);
			}
			break;
		case VIEW:
			versionScreen.memoAction(VIEW).click();
			switchToPage(LASTPAGE, false);
			break;
		case REVERSE:
			versionScreen.memoAction(REVERSE).click();
			switchToPage(LASTPAGE, false);
			toolElements.toolsButton(REVERSE_DEAL).click();
			result = inputTable.verifyAcceptOverride();
			if (result) {
				result = readTable.message().getText().contains(TXN_COMPLETE);
			}

			break;
		default:
			Reporter.log("Error while performing " + action + " Arrangement Memo. Action not supported");
		}
		return result;
	}

	public boolean customerIntervention(final String customer, final String action, final String contactLogFields,
			final String contactLogValues, final String customerType) {
		String finalMessage = "";
		boolean result = true;

		Reporter.log("Changing Contact Log (" + action + ") of customer " + customer, debugMode);

		findCIF(customer, customerType, "");
		switchToTab(CUSTOMER, "");
		switchToTab("Contacts", "");
		switch (action) {
		case AMEND:
			versionScreen.waitForPageLoading(versionScreen.contactEnquiry(), "Visible");
			compositeScreen.textAction("Test Note", "Update Contact").click();
			switchToPage(LASTPAGE, false);
			break;
		case CREATE:
		case OPEN:
		default:
			compositeScreen.textAction("Capture New Contact", "Capture New Contact").click();
			switchToPage(LASTPAGE, false);
			break;
		}
		if (!OPEN.equals(action)) {
			result = multiInputData(contactLogFields, contactLogValues, false);

			if (result) {
				toolElements.toolsButton(COMMIT_DEAL).click();
				result = inputTable.verifyAcceptOverride();

				if (result) {
					finalMessage = readTable.message().getText();
				}

				if (!finalMessage.contains(TXN_COMPLETE)) {
					result = false;
				}
			}
		}

		return result;
	}

	public String createAdHocPayment(final String action, final String type, final String arrangement,
			final String fields, final String values) {
		boolean result = true;
		String message;
		String adHocID;
		String label = "Payment Order for Ad-hoc Cheque";
		String commandLine = "PAYMENT.ORDER,CAMB.EFT.ACH";
		Reporter.log("Creating AdHoc Payment via " + type + " for arrangement " + arrangement, debugMode);
		switch (type) {

		case CHEQUE:
			label = "Payment Order for Ad-hoc Cheque";
			commandLine = "PAYMENT.ORDER,LBC.CAMB.EFT.ACH.2";

			break;
		case EFT:
		default:
			label = "Payment Order for Ad-hoc EFT";
			commandLine = "PAYMENT.ORDER,LBC.CAMB.EFT.ACH.1";

			break;
		}

		commandLine(commandLine, commandLineAvailable);
		switchToPage(LASTPAGE, false);
		toolElements.toolsButton("New Deal").click();
		switchToPage(LASTPAGE, false);
		result = multiInputData(fields, values, false);
		adHocID = inputTable.identifier(label).getText();

		if (VALIDATE.equals(action) && result) {
			toolElements.toolsButton(VALIDATE_DEAL).click();
		} else if (!OPEN.equals(action) && result) {
			inputTable.commitAndOverride();
			if (result) {
				message = readTable.message().getText();
				if (message.contains(TXN_COMPLETE)) {
					authorizeEntity(adHocID, "AdHocPayment via " + type);
				} else {
					adHocID = "Error: creating AdHoc Payment via " + type;
				}
			}
		}

		return adHocID;
	}

	public String amendCIF(final String action, final String customer, final String customerType,
			final String roleBasedPage, final String fields, final String values) {

		WebElement displayedMessage;
		String actualCustomerType = customerType;
		String message = "";
		boolean result;

		Reporter.log("Amending customer " + customer, debugMode);

		if ("".equals(customerType)) {
			actualCustomerType = PERSONAL;
		}

		if (DEALER_ADVISOR.equals(actualCustomerType)) {

			commandLine("CUSTOMER,LBC.AGENT", commandLineAvailable);
			enquiryElements.transactionIdField("Basic Details").sendKeys(customer);
			toolElements.toolsButton(EDIT_CONTRACT).click();
			switchToPage(LASTPAGE, false);
			result = true;

		} else {

			commandLine("COS CUST.AMEND", commandLineAvailable);
			compositeScreen.switchToFrame(ID, "FRAME01");
			enquiryElements.enquirySearch("Customer No", "", customer);
			switchToPage(LASTPAGE, false);

			try {
				compositeScreen.switchToFrame(ID, "FRAME01");
				toolElements.toolsButton(AMEND).click();
				switchToPage(LASTPAGE, false);
				result = true;
			} catch (NoSuchElementException e) {
				result = false;
			}
			if (!commandLineAvailable) {
				compositeScreen.switchToFrame(ID, "FRAMETABCSM");
			}

			compositeScreen.switchToFrame(ID, "FRAME02");
		}

		if (result) {
			result = multiInputData(fields, values, false);
		}

		if (result) {
			if (!action.contains(OPEN)) {
				toolElements.toolsButton(COMMIT_DEAL).click();
				switchToPage(LASTPAGE, false);
				result = compositeScreen.switchToFrame(ID, "FRAME02");
				result = inputTable.verifyAcceptOverride();

				if (result) {
					displayedMessage = readTable.message();
					message = displayedMessage.getText().substring(14, 20);
				} else {
					message = "Error: Problem while amending CIF";
					inputTable.returnButton();
					versionScreen.alertAction("ACCEPT");
				}
				switchToPage(environmentTitle, true);
			}
		} else {
			message = "Error: Problem while amending CIF";
			inputTable.returnButton();
			versionScreen.alertAction("ACCEPT");
			switchToPage(environmentTitle, true);
		}
		return message;

	}

	public boolean arrangementAction(final String arrangement, final String customer, final String roleBasedPage,
			final String action, final String fields, final String values, final String customerType) {
		boolean result = true;
		String message;
		String tabName;
		Select dropdown;

		Reporter.log("Performing " + action + " for customer " + customer, debugMode);

		try {
			findCIF(customer, customerType, roleBasedPage);
			switchToPage(LASTPAGE, false);
			switch (roleBasedPage) {
			case ROLEBASED_BANKING:
			case ROLEBASED_OR:
				tabName = "Products";

				break;
			case ROLEBASED_LENDING:
			case ROLEBASED_SAE:
			default:
				tabName = "Loans";

				break;
			}

			if ((customerType.equals(BUSINESS) || customerType.equals(NON_CLIENT_BUSINESS))
					&& !ROLEBASED_SAE.equals(roleBasedPage)) {
				tabName = "main%" + tabName + "%workarea";
			}

			switchToTab(tabName, "");
			versionScreen.waitForLoading();
			dropdown = new Select(compositeScreen.actionDropDown(arrangement, "drillbox"));
			dropdown.selectByVisibleText(action);
			compositeScreen.actionButton(arrangement).click();

			switchToPage(LASTPAGE, false);

			if (!LOAN_OVERVIEW.equals(action)) {
				multiInputData(fields, values, false);
				toolElements.toolsButton(COMMIT_DEAL).click();
				result = inputTable.verifyAcceptOverride();

				if (result) {
					message = readTable.message().getText();
					if (!message.contains(TXN_COMPLETE)) {
						result = false;
					}
				}
			}
		} catch (NoSuchElementException e) {
			Reporter.log("Error while performing " + action + " for customer " + customer, debugMode);
			Reporter.log(e.getMessage(), false);
			result = false;
		}

		return result;
	}

	public String orderNewPersonalCard(final String productGroup, final String product, final String customerType,
			final String customer, final String customerFields, final String customerValues) {
		final String fields = "CUSTOMER," + "DEF.MEMBER," + "CARD.STATUS";
		final String values = customer + "," + customer + "," + "1";
		String errormessage = "";
		String message = "";
		String createdCIF;
		final String arrangementType = DefaultVariables.productGroupArrangementType.get(productGroup);
		String roleBasedPage = DefaultVariables.productLineRoleBasedPage.get(arrangementType);

		commandLine("CARD.ISSUE,CAMB.ORDER.MAIL.OL I MEMC.NEW", commandLineAvailable);
		compositeScreen.inputField().clear();
		compositeScreen.inputField().sendKeys("B2BP.NEW");
		try {
			toolElements.toolsButton(EDIT_CONTRACT).click();
		} catch (NoSuchElementException e) {
			Reporter.log("Problem Interacting with Button Edit a contract");
		}
		multiInputData(fields, values, false);
		toolElements.toolsButton(VALIDATE_DEAL).click();

		toolElements.toolsButton(COMMIT_DEAL).click();
		try {
			inputTable.verifyAcceptOverride();
			switchToTab("Limits", "");
			inputData("LIMIT.SCORE", "001", false);

			switchToTab("Access flags", "");
			inputData("EBIZ.ACSTYP:1", "6", false);

			switchToTab("Access", "");
			inputData("CA.EN.FLAG:1", "C1", false);

			toolElements.toolsButton(COMMIT_DEAL).click();
			inputTable.verifyAcceptOverride();
			message = readTable.message().getText();
		} catch (NoSuchElementException e) {
			errormessage = inputTable.errorMessage().getText();
			if ("ID IN FILE MISSING".contains(errormessage) || "MISSING CUSTOMER - RECORD".contains(errormessage)) {
				createdCIF = customer(CREATE, customerType, roleBasedPage, customerFields, customerValues);

				arrangements(CREATE, productGroup, product, "", createdCIF, "CUSTOMER:1," + "CURRENCY,",
						createdCIF + "," + CAD + ",", customerFields, customerValues);

				orderNewPersonalCard(productGroup, product, customerType, createdCIF, customerFields, customerValues);
			} else {
				message = errormessage;
			}

		}
		if (message.contains(TXN_COMPLETE)) {
			Reporter.log(message);
		} else {
			Reporter.log("Unable to order new personal card");
		}

		switchToPage(environmentTitle, true);
		return message;
	}

	public String arrangementBillAmount(final String arrangementID, final String productGroup, final String product) {

		String billAmount;
		int rowNumber;
		findArrangement(AUTHORISED, arrangementID, ARRANGEMENT_AUTHORIZATION, "", productGroup, product, CAD);
		versionScreen.linkText(ADDITIONAL_DETAILS, "Bills").click();
		versionScreen.waitForLoading();
		try {
			rowNumber = enquiryElements.getRowNumberMatching("Repayments", "Billed", "Billed") + 1;
			billAmount = enquiryElements
					.getElementAtCell("Repayments", "Due",
							enquiryElements.getColumnHeaderNumber("Repayments", "Billed"), rowNumber)
					.getText().replaceAll(",", "");
		} catch (NoSuchElementException e) {
			Reporter.log("Error while catching bill amount", debugMode);
			billAmount = "Error: Could not find amount";
		}

		return billAmount;

	}

	public boolean retakeLegacyPayment(final String arrangement, final String productGroup, final String product,
			final String originalBillDate, final String originalPaymentDate, final String amount) {
		String fields;
		String values;
		boolean result = false;

		Reporter.log("Retaking rejected Legacy EFT Payment on " + product + ": " + arrangement, debugMode);

		fields = "ADJUST.DESC," + "Bill Adjustment#BILL.REF:1," + "Bill Adjustment#BILL.DATE:1,"
				+ "Bill Adjustment#PAYMENT.DATE:1," + "Bill Adjustment#BILL.TYPE:1," + "Bill Adjustment#PAYMENT.TYPE:1,"
				+ "Bill Adjustment#PAYMENT.METHOD:1," + "Bill Adjustment#PROPERTY:1:1,"
				+ "Bill Adjustment#NEW.PROP.AMT:1:1,";
		values = "REJECTED ITEM FROM SIT," + "NEW," + originalBillDate + "," + originalPaymentDate + ","
				+ "INSTALLMENT," + "SPECIAL,Due," + "ACCOUNT," + amount + ",";
		result = arrangementActivity(CREATE_AUTHORISE, "CAPTURE.BILL ACTIVITY FOR BALANCE.MAINTENANCE", arrangement,
				productGroup, fields, values);

		fields = "MEMO.TYPE," + "MEMO.TEXT," + "MEMO.DATE,";
		values = "Arrears Comment," + amount
				+ "payment returned NSF. Letter sent to client and copy emailed to Account Manager for follow up. "
				+ "This is a Rejected Payment comint from SIT/AS400 for the bill date " + originalBillDate
				+ " rejected on " + originalPaymentDate + "," + "+0d,";

		result = arrangementMemo(CREATE, arrangement, productGroup, fields, values);

		return result;
	}

	public boolean holdOutput(final String customer) {

		String xmlAddress = "";
		String finalMessage = "";
		String fields;
		String values;
		int addressIDColumnNumber;
		int xmlRowNumber;
		boolean result = false;

		Reporter.log("Changing settings to hold mail output for customer " + customer, debugMode);

		commandLine("COS LBC.CUST.ADDRESS", commandLineAvailable);
		driver.switchTo().frame(0);
		enquiryElements.enquirySearch(CUSTOMER, "", customer);
		switchToPage(LASTPAGE, false);
		driver.switchTo().frame(0);
		int size = versionScreen.dataRow(ID, "datadisplay").size();

		for (int i = 0; i < MAX_RETRIES && size <= 8; i++) {
			toolElements.toolsButton("Refresh").click();
			switchToPage(LASTPAGE, false);
			driver.switchTo().frame(0);
			size = versionScreen.dataRow(ID, "datadisplay").size();
		}

		addressIDColumnNumber = enquiryElements.getColumnHeaderNumber("Customer Address", "Address Id");
		xmlRowNumber = enquiryElements.getRowNumberMatching("Customer Address", "Address Id",
				"CA0017000.C-" + customer + ".XML");

		xmlAddress = enquiryElements.getElementAtCell("Customer Address", addressIDColumnNumber, xmlRowNumber)
				.getText();

		if (addressIDColumnNumber > 0 && xmlRowNumber > 0 && !"".equals(xmlAddress)) {
			commandLine("DE.ADDRESS,LBC.CAMB.ADD21", commandLineAvailable);

			enquiryElements.transactionIdField("Input Customer Address").click();
			enquiryElements.transactionIdField("Input Customer Address").sendKeys(xmlAddress);
			toolElements.toolsButton(EDIT_CONTRACT).click();

			fields = "L.HOLD.OUTPUT," + "RETURNED.MAIL,";
			values = "YES," + "Y,";
			multiInputData(fields, values, false);
			toolElements.toolsButton(COMMIT_DEAL).click();
			finalMessage = readTable.message().getText();

			if (finalMessage.contains(TXN_COMPLETE)) {
				fields = "CONTACT.TYPE," + "CONTACT.STATUS," + "CONTACT.CHANNEL," + "CONTACT.DESC," + "CONTACT.DATE,"
						+ "CONTACT.NOTES:1," + "CONTACT.TIME,";
				values = "Letter," + "New," + "POST," + "We have received a Returned Mail for client," + "+0d,"
						+ "Please Update: call client and request an address update," + "1,";
				result = customerIntervention(customer, CREATE, fields, values, "");
			}
		} else {
			result = false;
			Reporter.log("XML was not found", debugMode);
		}

		return result;
	}

	public boolean arrangementActivity(final String action, final String activity, final String arrangement,
			final String productGroup, final String fields, final String values) {
		String actionButton;
		String actualArrangement = arrangement;
		String message;
		String activityStatus;
		String[] effectiveDate;
		boolean result = false;

		Reporter.log(
				action + " Arrangement Activity " + activity + " of " + productGroup + " arrangement " + arrangement,
				debugMode);

		if (!"".equals(arrangement)) {
			actualArrangement = findArrangement(AUTHORISED, arrangement, ARRANGEMENT, "", productGroup, "", "");
			switchToPage(LASTPAGE, false);
		}

		if (actualArrangement.contains(ERROR) || !arrangement.equals(actualArrangement)) {
			result = false;
		} else {
			if (action.contains("Scheduled")) {
				actionButton = "Do Activity";
			} else if (action.contains("Simulate")) {
				actionButton = "Simulate";
			} else {
				actionButton = "Do Activity Today";
			}

			if (action.contains(CREATE) || action.contains(OPEN)) {
				try {
					versionScreen.hyperLink("New Activity").click();
					switchToPage(LASTPAGE, false);
					compositeScreen.textAction(activity, actionButton).click();
					if ((action.contains("Scheduled") || action.contains("Simulate"))
							&& !action.equals("Open Simulate")) {
						effectiveDate = action.split("\\s+");
						switchToPage(LASTPAGE, false);
						String value = effectiveDate[effectiveDate.length - 1];
						multiInputData("EFFECTIVE.DATE", value, false);
						toolElements.toolsButton(VALIDATE_DEAL).click();
					}
					switchToPage(LASTPAGE, false);
					result = true;
				} catch (NoSuchElementException e) {
					Reporter.log("Could not start Arrangement Activity " + activity, debugMode);
				}

				if (action.contains(CREATE)) {

					if (result && !"".equals(fields)) {
						result = multiInputData(fields, values, true);
					}
					result = inputTable.commitAndOverride();
				}

			} else if (action.contains(REVERSE)) {
				versionScreen.linkText(ADDITIONAL_DETAILS, ACTIVITY).click();
				versionScreen.waitForLoading();
				if (versionScreen.activityCheck(activity, AUTHORISED)) {
					versionScreen.activityAction(activity, AUTHORISED, REVERSE).click();
					switchToPage(LASTPAGE, false);

					toolElements.toolsButton(REVERSE_DEAL).click();

					try {
						inputTable.acceptOverride().click();
					} catch (NoSuchElementException e) {
						Reporter.log(NO_OVERRIDE, false);
					}

					message = readTable.message().getText();

					if (message.contains(TXN_COMPLETE)) {
						result = true;
					}

				} else {
					Reporter.log(NO_ACTIVITY, false);
					result = false;
				}
			} else if (action.contains(VIEW_AUTHORISED)) {
				if (action.contains(FINANCIAL)) {
					activityStatus = "AUTH";
					versionScreen.linkText(ACTIVITY_LOG, FINANCIAL).click();
				} else {
					activityStatus = AUTHORISED;
					versionScreen.linkText(ADDITIONAL_DETAILS, ACTIVITY).click();
				}
				switchToPage(LASTPAGE, false);
				versionScreen.waitForLoading();

				if (versionScreen.activityCheck(activity, activityStatus)) {
					versionScreen.activityAction(activity, activityStatus, VIEW).click();
					switchToPage(LASTPAGE, false);
					result = true;

				} else {
					Reporter.log(NO_ACTIVITY, false);
					result = false;
				}
			} else if (action.contains(VIEW_PENDING_AUTHORISED) || action.contains(EDIT)) {
				compositeScreen.pendingActivity(activity);
				if ("Edit".equals(action)) {
					compositeScreen.actionDropDown(activity, "drillbox").sendKeys(EDIT);

				} else {
					compositeScreen.actionDropDown(activity, "drillbox").sendKeys(VIEW);
				}
				compositeScreen.actionButton("").click();
				result = true;
			} else {
				result = false;
				Reporter.log("Framework cannot handle action " + action, debugMode);
			}

			if (result && action.contains(AUTHORISE)) {
				result = authorizeEntity(arrangement, ACTIVITY + "," + productGroup);
			}
		}
		return result;
	}

	public boolean reverseEntity(final String idValue, final String type) {
		String attributeValue = "";
		String commandLine = "";
		String enquiryLabel = "";
		boolean result = true;
		boolean wrenchReversal = false;
		Select dropdown;

		switchToPage(environmentTitle, true);

		switch (type) {

		case COLLATERAL_LINK:
			wrenchReversal = true;
			commandLine = "COLLATERAL.RIGHT,INP";
			enquiryLabel = COLLATERAL_LINK;
			break;

		case COLLATERAL:
		case COLLATERAL_DETAILS:
		case COLLATERAL_DETAILS_INVESTMENT:
		case COLLATERAL_DETAILS_MACHINERY:
		case COLLATERAL_DETAILS_REAL_ESTATE:
		case COLLATERAL_DETAILS_LOANS:
			wrenchReversal = true;
			commandLine = "COLLATERAL,INP";
			enquiryLabel = COLLATERAL_DETAILS;
			break;

		case ESCROW_ACCOUNT:
			wrenchReversal = true;
			commandLine = "ESCROW.ACCOUNT,NEW I";
			enquiryLabel = ESCROW_ACCOUNT;
			break;

		case BENEFICIARY:
			wrenchReversal = true;
			commandLine = "BENEFICIARY,CAMB";
			enquiryLabel = "Beneficiary Details for ACH/CHQ";
			break;

		case ADHOC_CHEQUE:
		case ADHOC_EFT:
			wrenchReversal = true;
			if (ADHOC_CHEQUE.equals(type)) {
				commandLine = "PAYMENT.ORDER,LBC.CAMB.EFT.ACH.2";
				enquiryLabel = "Payment Order for Ad-hoc Cheque";
			} else {
				commandLine = "PAYMENT.ORDER,LBC.CAMB.EFT.ACH.1";
				enquiryLabel = "Payment Order for Ad-hoc EFT";
			}

			break;

		case FUNDSTRANSFER:
			commandLine("COS ACCOUNT.TRANSFER", commandLineAvailable);
			compositeScreen.switchToFrame(ID, "FRAMEA");
			compositeScreen.fundsTransferElement("Transfer between Accounts").click();
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "FRAMEA");
			toolElements.toolsButton(RETURN_TO_SCREEN).click();
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "FRAMEA");
			enquiryElements.transactionIdField(TRANSFER_ACCOUNTS).sendKeys(idValue);
			toolElements.toolsButton(WRENCH).click();
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "FRAMEA");
			attributeValue = toolElements.toolsButtonTitle(REVERSE).getAttribute(HREF);

			if (attributeValue == null) {
				Reporter.log("FundsTransfer is already reversed: " + idValue, true);
				toolElements.toolsButton(RETURN_TO_SCREEN).click();
				switchToPage(LASTPAGE, false);
			} else {
				toolElements.toolsButton(REVERSE).click();
				result = switchToPage(LASTPAGE, false) && compositeScreen.switchToFrame(ID, "FRAMEA")
						&& readTable.message().getText().contains(TXN_COMPLETE);
			}

			break;
		case LIMIT:
		case LIMIT_SAE:
			commandLine("COS LBC.LIMIT.AMEND", commandLineAvailable);
			switchToPage(LASTPAGE, false);
			toolbarElements.selectionScreen().click();
			switchToPage(LASTPAGE, false);
			enquiryElements.enquirySearch("Limit ID", "", idValue);
			versionScreen.activityAction(idValue, "", REVERSE).click();
			switchToPage(LASTPAGE, false);
			attributeValue = toolElements.toolsButtonTitle(REVERSE_DEAL).getAttribute(HREF);

			if (attributeValue == null) {
				Reporter.log(type + " is already reversed: " + idValue, true);
				toolElements.toolsButton(RETURN_TO_SCREEN).click();
				switchToPage(LASTPAGE, false);
			} else {
				toolElements.toolsButton(REVERSE_DEAL).click();
				result = switchToPage(LASTPAGE, false) && inputTable.verifyAcceptOverride()
						&& switchToPage(LASTPAGE, false) && readTable.message().getText().contains(TXN_COMPLETE);
			}

			break;
		case "Standing Order":
			commandLine("COS STO.REVERSE", commandLineAvailable);

			toolbarElements.selectionScreen().click();
			enquiryElements.enquirySearch("Account", "", idValue);

			switchToPage(LASTPAGE, false);

			versionScreen.activityAction(idValue, "", "Reverse FT").click();
			switchToPage(LASTPAGE, false);
			toolElements.toolsButton(REVERSE_DEAL).click();
			result = switchToPage(LASTPAGE, false) && inputTable.verifyAcceptOverride() && switchToPage(LASTPAGE, false)
					&& readTable.message().getText().contains(TXN_COMPLETE);

			break;
		case "TFS":
			commandLine("ENQ TFS.FIND.TXN.REV", commandLineAvailable);
			enquiryElements.enquirySearch("TFS Id", "", idValue);
			switchToPage(LASTPAGE, false);
			versionScreen.activityAction(idValue, "", "Reverse").click();
			switchToPage(LASTPAGE, false);
			dropdown = new Select(compositeScreen.actionDropDown("", "REVERSAL.MARK:1"));
			dropdown.selectByVisibleText("R");
			result = inputTable.commitAndOverride();

			break;
		default:
			Reporter.log("Framework does not know how to reverse this entity yet: " + type, debugMode);
			result = false;

			break;
		}

		if (wrenchReversal) {
			commandLine(commandLine, commandLineAvailable);
			switchToPage(LASTPAGE, false);
			enquiryElements.transactionIdField(enquiryLabel).sendKeys(idValue);
			toolElements.toolsButton(WRENCH).click();
			switchToPage(LASTPAGE, false);
			attributeValue = toolElements.toolsButtonTitle(REVERSE_DEAL).getAttribute(HREF);

			if (attributeValue == null) {
				Reporter.log(type + " is already reversed: " + idValue, true);
				toolElements.toolsButton(RETURN_TO_SCREEN).click();
				switchToPage(LASTPAGE, false);
			} else {
				toolElements.toolsButton(REVERSE_DEAL).click();
				result = switchToPage(LASTPAGE, false) && inputTable.verifyAcceptOverride()
						&& switchToPage(LASTPAGE, false) && readTable.message().getText().contains(TXN_COMPLETE);
			}
		}

		if (result) {
			result = authorizeEntity(idValue, type);
		}
		return result;
	}

	public String findBeneficiaryCode(final String customer, final String customerType, final String beneficiaryType,
			final String roleBasedPage) {
		String beneficiaryCode;
		String beneficiaryFields;
		String beneficiaryValues;

		findCIF(customer, customerType, roleBasedPage);
		switchToTab("Beneficiaries", "");
		final String beneficiary = enquiryElements
				.getElementAtCell("Beneficiary Standard Settlement Instructions", 1, 1).getText();
		if (beneficiary.contains("No Beneficiary Available")) {

			beneficiaryFields = DefaultVariables.beneficiaryFields + "OWNING.CUSTOMER," + "BEN.CUSTOMER,";
			beneficiaryValues = DefaultVariables.beneficiaryValues + customer + ","
					+ createdCustomers.get(customer).getCustomerName() + ",";

			beneficiaryCode = beneficiaryCode(CREATE, beneficiaryType, "", beneficiaryFields, beneficiaryValues);
		} else {
			beneficiaryCode = beneficiary;
		}

		return beneficiaryCode;
	}

	public String beneficiaryCode(final String action, final String beneficiaryType, final String beneficiaryCode,
			final String fields, final String values) {
		String actualBeneficiaryCode = beneficiaryCode;
		boolean result = false;
		String commandLine;
		String displayedMessage = "";

		Reporter.log("Performing action: " + action + " Beneficiary Record " + actualBeneficiaryCode, debugMode);

		switch (beneficiaryType) {

		case "EFT Client":
		default:
			commandLine = "BENEFICIARY,CAMB.EFT.CIF";

			break;
		case "EFT Non-Client":
			commandLine = "BENEFICIARY,CAMB.EFT.NOCIF";

			break;
		case "CHQ Non-Client":
			commandLine = "BENEFICIARY,CAMB.CHQ.NOCIF";

			break;
		}

		commandLine(commandLine, commandLineAvailable);
		switchToPage(LASTPAGE, false);

		if (CREATE.equals(action)) {

			toolElements.toolsButton("New Deal").click();
			switchToPage(LASTPAGE, false);
			result = multiInputData(fields, values, false);

			if (result) {
				toolElements.toolsButton(COMMIT_DEAL).click();
				result = inputTable.verifyAcceptOverride();

				if (result) {
					displayedMessage = readTable.message().getText();
					actualBeneficiaryCode = displayedMessage.substring(14, 27);
				} else {
					Reporter.log("Error while creating Beneficiary Record for customer", debugMode);
				}
			}

		} else {
			compositeScreen.inputField().sendKeys(actualBeneficiaryCode);

			switch (action) {

			case AMEND:
			default:
				toolElements.toolsButton(EDIT_CONTRACT).click();

				break;
			case VIEW:
				toolElements.toolsButton(VIEW_CONTRACT).click();

				break;
			case "Perform Action":
				toolElements.toolsButton(WRENCH).click();

				break;
			}
			switchToPage(LASTPAGE, false);

			if (!"".equals(fields)) {
				result = multiInputData(fields, values, false);
			}

			if (result && !OPEN.equals(action)) {
				toolElements.toolsButton(COMMIT_DEAL).click();
				displayedMessage = readTable.message().getText();
				if (displayedMessage.contains(TXN_COMPLETE)) {
					result = true;
				} else {
					Reporter.log("Error performing action: " + action + " Beneficiary Record for Beneficiary: "
							+ actualBeneficiaryCode, true);
				}
			}

		}
		return actualBeneficiaryCode;
	}

	public String generateStatement(final String statementType, final String customer, final String productGroup,
			final String product, final String closureFields, final String closureValues) {
		Select dropdown;
		String accountID = null;
		String fields;
		String values;
		String principalAmount = null;
		String interestAccrued = null;
		String perDiem = null;
		String actualStatementType;
		String dischargeResult = null;
		String statementFields = null;
		String statementValues = null;
		String finalMessage;
		boolean result;

		if ("".equals(statementType)) {
			actualStatementType = "Input Genral Stmt Request";
		} else {
			actualStatementType = statementType;
		}
		findArrangement(AUTHORISED, customer, CIF, "", productGroup, product, CAD);
		accountID = versionScreen.labelElement(ARRANGEMENT).getText();

		result = requestClosure(SIMULATE, "", "Request Payoff", productGroup, closureFields, closureValues, "", "", "");
		commandLine("ENQ LBC.LOAN.LIST.ENQ", commandLineAvailable);
		enquiryElements.enquirySearch("Customer(owner)", "", customer);
		switchToPage(LASTPAGE, false);

		if (enquiryElements.getRowsMatching("LBC.LOAN.LIST.ENQ", "Arrangement ID", accountID).size() > 0) {
			dropdown = new Select(compositeScreen.actionDropDown("", "drillbox"));
			dropdown.selectByVisibleText(actualStatementType);
			versionScreen.activityAction(accountID, "", "Select Drilldown").click();
		}

		if (PERSONAL_LOANS.equals(productGroup)) {
			if (result) {
				fields = "WHO.REQUESTED," + "LOAN.FREEZE.FLAG," + "PAYOFF.DATE,";
				values = "Test Client," + "NO," + "+0d,";

				switchToPage(LASTPAGE, false);
				multiInputData(fields, values, false);
				toolElements.toolsButton(VALIDATE_DEAL).click();
				principalAmount = readData("CAPITAL.BAL:1").replaceAll(",", "");

				for (int j = 0; j < MAX_RETRIES && "".equals(principalAmount); j++) {
					switchToTab("Contacts", "");
					toolElements.toolsButton(VALIDATE_DEAL).click();
					principalAmount = readData("CAPITAL.BAL:1").replaceAll(",", "");

					if (j + 1 == MAX_RETRIES) {
						Reporter.log("Maximum attempts reached for function dischargeRequest", debugMode);
					}
				}
				interestAccrued = readData("ACCRUAL:1").replaceAll(",", "");
				perDiem = readData("DAILY.INT.AMT:1").replaceAll(",", "");

				dischargeResult = principalAmount + "&" + interestAccrued + "&" + perDiem;
			} else {
				Reporter.log("Error while performing request closure during discharge request");
			}
		} else if (RETAIL_MORTGAGES.equals(productGroup)) {
			switch (actualStatementType) {
			case "For Information Only":
			case "For Partial Discharge Only":
				statementFields = "WHO.REQUESTED," + "FAX," + "PAYOFF.DATE," + "LOAN.FREEZE.FLAG,";
				statementValues = createdCustomers.get(customer).firstName + " "
						+ createdCustomers.get(customer).lastName + "," + createdCustomers.get(customer).phoneFax + ","
						+ "+0d," + "NO,";
				break;
			case "For Refinance only":
			case "For Transfer only":
			case "For Port Only":
			case "For Bridge Loan":
			case "Discharge stmt":
			case "LOC Discharge Stmt":
				statementFields = "WHO.REQUESTED," + "FAX," + "PAYOFF.DATE," + "LOAN.FREEZE.FLAG,";
				statementValues = createdCustomers.get(customer).firstName + " "
						+ createdCustomers.get(customer).lastName + "," + createdCustomers.get(customer).phoneFax + ","
						+ "+0d," + "YES,";
				break;
			case "LBC Assumption statement":
			case "B2B Assumption Stmt":
				statementFields = "WHO.REQUESTED," + "FAX," + "PAYOFF.DATE," + "PURCHASER," + "ADRESS.LINE.1,"
						+ "ADRESS.CITY," + "ADRESS.PROVINCE," + "ADRESS.ZIP.CODE,";
				statementValues = createdCustomers.get(customer).firstName + " "
						+ createdCustomers.get(customer).lastName + "," + createdCustomers.get(customer).phoneFax + ","
						+ "+0d," + createdCustomers.get(customer).employerName + ","
						+ createdCustomers.get(customer).addressStreet + ","
						+ createdCustomers.get(customer).addressCity + ","
						+ createdCustomers.get(customer).addressProvince + ","
						+ createdCustomers.get(customer).addressPostalCode + ",";
				break;
			case "Mortgage information statement":
				statementFields = "WHO.REQUESTED," + "FAX," + "PURCHASER," + "ADRESS.LINE.1," + "ADRESS.CITY,"
						+ "ADRESS.PROVINCE," + "ADRESS.ZIP.CODE,";
				statementValues = createdCustomers.get(customer).firstName + " "
						+ createdCustomers.get(customer).lastName + "," + createdCustomers.get(customer).phoneFax + ","
						+ createdCustomers.get(customer).employerName + ","
						+ createdCustomers.get(customer).addressStreet + ","
						+ createdCustomers.get(customer).addressCity + ","
						+ createdCustomers.get(customer).addressProvince + ","
						+ createdCustomers.get(customer).addressPostalCode + ",";
				break;

			default:
				statementFields = "WHO.REQUESTED," + "PAYOFF.DATE," + "LOAN.FREEZE.FLAG,";
				statementValues = createdCustomers.get(customer).firstName + " "
						+ createdCustomers.get(customer).lastName + "," + "+0d," + "NO,";
				break;
			}
			multiInputData(statementFields, statementValues, false);
		}

		if (dischargeResult == null) {

			dischargeResult = inputTable.identifier("Main Details").getText();
			result = inputTable.commitAndOverride();
			finalMessage = readTable.message().getText();
			if (!result && !finalMessage.contains(TXN_COMPLETE)) {
				Reporter.log("Unable to generate statement Type: " + actualStatementType + " for " + accountID,
						debugMode);
				dischargeResult = null;
			}
		}

		return dischargeResult;
	}

	public boolean refundExcessFunds(final String customer, final String arrangement, final String customerType,
			final String productGroup, final String type, String roleBasedPage) {
		String creditAmount;
		String fields;
		String values;
		String beneficiary;
		String paymentId;
		boolean result;

		findArrangement(AUTHORISED, arrangement, ARRANGEMENT, roleBasedPage, productGroup, "", "");
		creditAmount = versionScreen.statusElement("Unspecified Credit").getText().replaceAll(",", "");
		switch (type) {

		case CHEQUE:
			fields = "DEBIT.ACCT.NO," + "DEBIT.CURRENCY," + "CREDIT.ACCT.NO," + "DEBIT.AMOUNT,";
			values = arrangement + "," + "CAD," + "CAD1110200017817," + creditAmount + ",";

			paymentId = financialTransaction(CREATE_AUTHORISE, "Debit UNC Balance", fields, values);
			break;
		case EFT:
		default:
			beneficiary = findBeneficiaryCode(customer, customerType, "EFT Client", roleBasedPage);
			fields = "DEBIT.ACCOUNT," + "PAYMENT.ORDER.PRODUCT," + "DEBIT.CCY," + "PAYMENT.CURRENCY,"
					+ "PAYMENT.AMOUNT," + "DEBIT.VALUE.DATE," + "BENEFICIARY.ID,";
			values = arrangement + "," + "ACHUNCDR," + "CAD," + "CAD," + creditAmount + "," + "-0d," + beneficiary
					+ ",";
			paymentId = createAdHocPayment(CREATE, EFT, arrangement, fields, values);
			break;
		}
		result = !paymentId.contains(ERROR);

		return result;
	}

	public boolean escrowPayee(final String action, final String fields, final String values, final String payee) {
		String actualFields = fields;
		String actualValues = values;
		boolean result = true;
		switch (action) {

		case "Find":

			commandLine("COS ESCROW.PAYEE", commandLineAvailable);
			compositeScreen.switchToFrame(ID, "PAYEETAB");
			compositeScreen.switchToFrame(ID, "workarea");
			if ("".equals(fields)) {
				result = enquiryElements.enquirySearch("PAYEE.ID", "equals", payee);
			} else {
				result = enquiryElements.enquirySearch(fields, "contains", values);
			}
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "PAYEETAB");
			compositeScreen.switchToFrame(ID, "workarea");

			break;
		case EDIT:
		case OPEN:

			actualFields = fields + "Setup#COMPANY.CODE:1," + "DISBURSE.ACC:1,";
			if (currentBranch.contains("B2B")) {
				actualValues = values + "CA0017000," + "CAD1160000017623,";
			} else {
				actualValues = values + "CA0011000," + "CAD1100000011526,";
			}

			commandLine("ESCROW.PAYEE,INPUT I", commandLineAvailable);
			enquiryElements.transactionIdField("Escrow Payee").click();
			enquiryElements.transactionIdField("Escrow Payee").sendKeys(payee);
			toolElements.toolsButton(EDIT_CONTRACT).click();
			if ("Edit".equals(action)) {
				multiInputData(actualFields, actualValues, false);
				toolElements.toolsButton(COMMIT_DEAL).click();
				result = inputTable.verifyAcceptOverride() && DefaultVariables.authorizeEscrowPayee
						? authorizeEntity(payee, "Escrow Payee")
						: true;
			}

			break;
		case VIEW:

			commandLine("ESCROW.PAYEE,INPUT I", commandLineAvailable);

			enquiryElements.transactionIdField("Escrow Payee").click();
			enquiryElements.transactionIdField("Escrow Payee").sendKeys(payee);
			toolElements.toolsButton(VIEW_CONTRACT).click();

			break;
		default:
			Reporter.log("Framework cannot handle action " + action, debugMode);
		}
		return result;
	}

	public boolean editCoverdraft(final String arrangement, final String action, final String fields,
			final String values) {
		boolean result = true;

		Reporter.log("Editing Coverdraft Limit for Arrangement: " + arrangement, debugMode);

		switch (action) {

		case OPEN:
			commandLine("AC.CASH.POOL,CAMB.SWEEPS", commandLineAvailable);
			enquiryElements.transactionIdField("Covering Sweeps").sendKeys(arrangement);
			toolElements.toolsButton(EDIT_CONTRACT).click();
			switchToPage(LASTPAGE, false);
			result = multiInputData(fields, values, false);
			break;
		case "TBD":
			toolElements.toolsButton(COMMIT_DEAL).click();
			break;
		default:
			Reporter.log("Framework cannot handle action " + action, debugMode);
		}
		return result;
	}

	public boolean reoccurringFixedTransfer(final String action, final String type, final String customer,
			final String arrangement, final String fields, final String values) {
		boolean result = true;
		String section = "";
		String commandLine = "";
		String transactionID = "";
		String activityActionModify = "";

		switch (type) {

		case "Fixed":
			section = "Standing Order";
			commandLine = "STANDING.ORDER,LBC.ACHCREDIT.FIXAMT";
			transactionID = "Fixed Amount Standing Orders";
			activityActionModify = "Edit StandingOrder";

			break;
		case "Interest Only":
			section = "Standing Order";
			commandLine = "STANDING.ORDER,LBC.ACHCREDIT.INTONLY";
			transactionID = "Interest Only Standing Orders";
			activityActionModify = "Edit StandingOrder";

			break;
		case "Internal":
			section = "Standing Order";
			commandLine = "STANDING.ORDER,FIXAMTSAE";
			transactionID = "Fixed amount";
			activityActionModify = "Edit StandingOrder";

			break;
		case "PAP Debit":
			section = "PAP";
			commandLine = "CAPL.H.PAP.DD.DDI,CAMB.DEBIT";
			transactionID = "PAP Debit";
			activityActionModify = EDIT;

			break;
		case "PAP Credit":
		default:
			section = "PAP";
			commandLine = "CAPL.H.PAP.DD.DDI,CAMB.CREDIT";
			transactionID = "PAP Credit";
			activityActionModify = EDIT;
			break;
		}

		Reporter.log(action + " " + section + " for Customer " + customer + " on Arrangement " + arrangement,
				debugMode);

		switch (action) {

		case "Setup":
		case OPEN:

			commandLine(commandLine, commandLineAvailable);
			enquiryElements.transactionIdField(transactionID).sendKeys(arrangement);
			toolElements.toolsButton(EDIT_CONTRACT).click();
			switchToPage(LASTPAGE, false);
			if (!OPEN.equals(action)) {
				result = multiInputData(fields, values, false) && inputTable.commitAndOverride();
			}

			break;
		case "Modify":

			findCIF(customer, "", ROLEBASED_BANKING);
			switchToTab("Cash Management", "");
			versionScreen.activityAction(arrangement, "", activityActionModify).click();
			switchToPage(LASTPAGE, false);
			result = multiInputData(fields, values, false);
			toolElements.toolsButton(COMMIT_DEAL).click();
			result = inputTable.verifyAcceptOverride();

			break;
		case "Close":

			findCIF(customer, PERSONAL, ROLEBASED_SAE);
			switchToTab("Cash Management", "");

			if ("Internal".equals(type)) {

				versionScreen.activityAction(arrangement, "", "Cancel StandingOrder").click();
				switchToPage(LASTPAGE, false);
				toolElements.toolsButton(REVERSE_DEAL).click();
			} else {

				versionScreen.activityAction(arrangement, "", EDIT).click();
				switchToPage(LASTPAGE, false);
				inputData("CA.PAP.MATDATE", "+0d", false);
				toolElements.toolsButton(COMMIT_DEAL).click();
				result = inputTable.verifyAcceptOverride();
			}

			break;
		case "Reverse":

			reverseEntity(arrangement, section);

			break;
		case "Restore":

			commandLine(commandLine, commandLineAvailable);
			enquiryElements.transactionIdField(transactionID).sendKeys(arrangement);
			toolElements.toolsButton(WRENCH).click();

			Select dropdown = new Select(compositeScreen.actionDropDown("", "moreactions"));
			dropdown.selectByVisibleText("History Restore");
			toolElements.toolsButton("Go").click();

			break;
		default:
			Reporter.log("Framework cannot handle action " + action, debugMode);
		}
		return result;
	}

	public boolean changePaymentFrequency(final String arrangement, final String productGroup,
			final String newFrenquencyType, final String newFrequencyDate) {
		String fields;
		String values;
		String dayBasis;
		String frequency;
		List<WebElement> listOfPaymentfrequencies;
		boolean result1 = false;
		boolean result2 = false;

		Reporter.log("Changing Payment Frequency for arrangement: " + arrangement, debugMode);

		if ("Weekly".equals(newFrenquencyType)) {
			dayBasis = "G";
			frequency = "e0Y e0M e1W o" + newFrequencyDate + "D e0f";
		} else {
			dayBasis = "A";
			frequency = "e0Y e1M e0W o" + newFrequencyDate + "D e0f";
		}

		fields = "Principal Interest$Control#DAY.BASIS,";
		values = dayBasis + ",";
		result1 = arrangementActivity(CREATE_AUTHORISE, "CHANGE ACTIVITY FOR PRINCIPALINT", arrangement, productGroup,
				fields, values);

		result2 = arrangementActivity(OPEN, "Change Payment Frequency", arrangement, productGroup, "", "");

		listOfPaymentfrequencies = inputTable.listOfFields("PAYMENT.FREQ", "Schedule");

		for (int i = 1; i <= listOfPaymentfrequencies.size(); i++) {
			inputData("PAYMENT.FREQ:" + i, frequency, false);
		}
		toolElements.toolsButton(COMMIT_DEAL).click();
		result2 = inputTable.verifyAcceptOverride();

		return result1 && result2;
	}

	public String addNewCustomerAddress(final String type, final String customer, final String fields,
			final String values) {
		Select dropdown;
		String addressID;
		int xmlCount = 0;

		Reporter.log("Adding New " + type + " Address for customer: " + customer, debugMode);

		commandLine("COS LBC.CUST.ADDRESS", commandLineAvailable);
		driver.switchTo().frame(0);
		enquiryElements.enquirySearch(CUSTOMER, "", customer);
		switchToPage(LASTPAGE, false);
		driver.switchTo().frame(0);

		if ("XML".equals(type)) {
			xmlCount = enquiryElements
					.getRowsMatching("Customer Address", "Address Id", "CA0017000.C-" + customer + ".XML").size();

			addressID = "CA0017000.C-" + customer + ".XML." + Integer.toString(xmlCount + 1);
			switchToPage(environmentTitle, true);
			commandLine("DE.ADDRESS I " + addressID, commandLineAvailable);
			switchToPage(LASTPAGE, false);
		} else {
			addressID = "CA0017000.C-" + customer + ".PRINT.1";
			dropdown = new Select(compositeScreen.actionDropDown(addressID, "drillbox"));
			dropdown.selectByVisibleText("Add Print Address");
			versionScreen.activityAction(addressID, "", "Select Drilldown").click();
			switchToPage(LASTPAGE, false);
			driver.switchTo().frame(1);
		}

		if (!(multiInputData(fields, values, false) && inputTable.commitAndOverride()
				&& authorizeEntity(addressID, ADDRESS))) {
			addressID = "Error creating new " + type + " address for customer " + customer;
		}

		return addressID;
	}

	public String collateral(final String action, final String collateralType, final String collateralReference,
			final String fields, final String values) {

		boolean result = true;
		String commandLine = "";
		String transactionID = collateralType;
		String enquiryField = "";
		String currentSerial = "";
		String finalID = "";
		String rowCount;
		String data = "";
		String toolsButton;
		String[] headerMessage;

		if ("Count".equals(action)) {

			if (COLLATERAL_LINK.equals(collateralType)) {
				commandLine = "ENQ CO.001";
				enquiryField = "Liability Number";
			} else {
				commandLine = "ENQ COLLAT.EXPIRY";
				enquiryField = "Customer";
			}

			commandLine(commandLine, commandLineAvailable);
			enquiryElements.enquirySearch(enquiryField, "", collateralReference);
			switchToPage(LASTPAGE, false);
			data = versionScreen.dataDisplayTable("DATA_DISPLAY").getText();

			if (data.contains("No Records to Display")) {
				rowCount = "1";
			} else {
				if (COLLATERAL_LINK.equals(collateralType)) {
					enquiryElements.enquiryButtons("Collateral Right Detail").click();
					enquiryElements.enquirySearch("Credit Line No.", "begins with", collateralReference);
					switchToPage(LASTPAGE, false);
					data = versionScreen.dataDisplayTable("DATA_DISPLAY").getText();
					if (data.contains("No Collateral Right Available")) {
						rowCount = "1";
					} else {
						headerMessage = enquiryElements.enqHeaderMsg().getText().split("\\s");
						final String rowNumber = headerMessage[5];
						rowCount = Integer.toString(Integer.parseInt(rowNumber) + 1);
					}
				} else {
					rowCount = Integer.toString(versionScreen.recordList("DATA_DISPLAY").size() + 1);
				}
			}
			return rowCount;

		} else {
			Reporter.log("Performing " + action + " " + collateralType, debugMode);

			switch (collateralType) {

			case COLLATERAL_DETAILS_INVESTMENT:

				commandLine = "COLLATERAL,LBC.INP.INVEST";
				currentSerial = collateral("Count", COLLATERAL_DETAILS, collateralReference, "", "");
				finalID = collateralReference + "." + currentSerial;

				break;
			case COLLATERAL_DETAILS_LOANS:

				commandLine = "COLLATERAL,LBC.B2BPPSA";
				currentSerial = collateral("Count", COLLATERAL_DETAILS, collateralReference, "", "");
				finalID = collateralReference + "." + currentSerial;

				break;
			case COLLATERAL_DETAILS_MACHINERY:

				commandLine = "COLLATERAL,LBC.INP.MACHINE";
				currentSerial = collateral("Count", COLLATERAL_DETAILS, collateralReference, "", "");
				finalID = collateralReference + "." + currentSerial;

				break;
			case COLLATERAL_DETAILS_REAL_ESTATE:

				commandLine = "COLLATERAL,LBC.INP.REALEST";
				currentSerial = collateral("Count", COLLATERAL_DETAILS, collateralReference, "", "");
				finalID = collateralReference + "." + currentSerial;

				break;
			case COLLATERAL_LINK:

				commandLine = "COLLATERAL.RIGHT,INP";
				currentSerial = collateral("Count", COLLATERAL_LINK, collateralReference, "", "");
				finalID = collateralReference + "." + currentSerial;

				break;
			case COLLATERAL_DETAILS:
			default:

				commandLine = "COLLATERAL,LBC.B2BGENERAL.INP";
				currentSerial = collateral("Count", COLLATERAL_DETAILS, collateralReference, "", "");
				finalID = collateralReference + "." + currentSerial;

				break;
			}

			if (!action.contains(CREATE)) {
				finalID = collateralReference;
			}
			if (COLLATERAL_LINK.equals(collateralType)) {
				transactionID = COLLATERAL_LINK;
			} else {
				transactionID = COLLATERAL_DETAILS;
			}

			switch (action) {

			case OPEN:
			case VALIDATE:
			case CREATE:
			case AMEND:
			case VIEW:
			case CREATE_AUTHORISE:
			case AMEND_AUTHORISE:
				if (VIEW.equals(action)) {
					toolsButton = VIEW_CONTRACT;
				} else {
					toolsButton = EDIT_CONTRACT;
				}
				commandLine(commandLine, commandLineAvailable);
				enquiryElements.transactionIdField(transactionID).click();
				enquiryElements.transactionIdField(transactionID).sendKeys(finalID);
				toolElements.toolsButton(toolsButton).click();
				switchToPage(LASTPAGE, false);
				if (VALIDATE.equals(action)) {
					result = multiInputData(fields, values, false);
					toolElements.toolsButton(VALIDATE_DEAL).click();
				} else if (action.contains(CREATE) || action.contains(AMEND)) {
					result = multiInputData(fields, values, false) && inputTable.commitAndOverride();
				}
				if (!result) {
					finalID = "Error while performing " + action + " " + collateralType;
				}

				break;
			case REVERSE:
				result = reverseEntity(finalID, collateralType);
				if (!result) {
					finalID = "Error while performing " + action + " " + collateralType;
				}
				break;
			default:
				Reporter.log("Framework cannot handle action " + action, debugMode);
			}
			if (action.contains(AUTHORISE)) {
				result = authorizeEntity(finalID, collateralType);
				if (!result) {
					finalID = "Error while authorizing " + collateralType;
				}
			}
			return finalID;
		}
	}

	public boolean holdPayment(final String arrangement, final String roleBasedPage, final String productGroup,
			final String product, final String startingOn, final String offsetBy) throws ParseException {
		boolean result;
		String nextBillDate = "";
		String mergedBillDate = "";
		String nextBillDue = "";
		String mergedBillDue = "";
		String endOfHoldPeriod;
		String startOfRegularPeriod;
		String paymentFrequency1;
		String paymentFrequency21;
		String paymentFrequency22;
		String amount;
		final String fields;
		final String values;
		SimpleDateFormat enquiryDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
		SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
		double sum;
		int nextBillDateIndex;
		int mergedBillIndex;
		int paymentsPerPage;
		Date date = null;
		Calendar cal = Calendar.getInstance();

		if (!"".equals(arrangement)) {
			findArrangement(AUTHORISED, arrangement, ARRANGEMENT, roleBasedPage, productGroup, product, CAD);
		}
		switchToPage(LASTPAGE, false);
		versionScreen.linkText(ADDITIONAL_DETAILS, "Schedule").click();
		versionScreen.waitForLoading();
		paymentsPerPage = enquiryElements.enquiryResults("Current Page Last");

		nextBillDateIndex = retrieveNextDuePaymentAfter(startingOn) + 1;

		nextBillDate = enquiryElements.getElementAtCell("Payment Schedule", "AdditionalDetails",
				enquiryElements.getColumnHeaderNumber("Payment Schedule", "Date"), nextBillDateIndex).getText();
		nextBillDue = enquiryElements
				.getElementAtCell("Payment Schedule", "AdditionalDetails",
						enquiryElements.getColumnHeaderNumber("Payment Schedule", "Total Due"), nextBillDateIndex)
				.getText().replaceAll(",", "");

		if (nextBillDateIndex == paymentsPerPage) {
			enquiryElements.clickNextPage();
			mergedBillIndex = 1;

		} else {
			mergedBillIndex = nextBillDateIndex + 1;
		}

		mergedBillDate = enquiryElements.getElementAtCell("Payment Schedule", "AdditionalDetails",
				enquiryElements.getColumnHeaderNumber("Payment Schedule", "Date"), mergedBillIndex).getText();
		mergedBillDue = enquiryElements
				.getElementAtCell("Payment Schedule", "AdditionalDetails",
						enquiryElements.getColumnHeaderNumber("Payment Schedule", "Total Due"), mergedBillIndex)
				.getText().replaceAll(",", "");

		if ("Until Next Bill".equals(offsetBy)) {
			endOfHoldPeriod = mergedBillDate;
			date = enquiryDateFormat.parse(endOfHoldPeriod);
			cal.setTime(date);
			cal.add(Calendar.DATE, 1);
			date = cal.getTime();
			startOfRegularPeriod = inputDateFormat.format(date);

		} else {
			date = enquiryDateFormat.parse(nextBillDate);
			cal.setTime(date);
			cal.add(Calendar.DATE, Integer.parseInt(offsetBy));
			date = cal.getTime();
			endOfHoldPeriod = inputDateFormat.format(date);
			startOfRegularPeriod = "";

		}

		arrangementActivity(OPEN, "Hold Payment", arrangement, productGroup, "", "");

		paymentFrequency1 = readData("PAYMENT.FREQ:1");
		paymentFrequency21 = readData("DUE.FREQ:1:1");
		paymentFrequency22 = readData("DUE.FREQ:1:1");
		sum = Double.sum(Double.parseDouble(nextBillDue), Double.parseDouble(mergedBillDue));
		amount = Double.toString(sum);

		fields = "START.DATE:1:1," + "END.DATE:1:1," + "ACTUAL.AMT:1:1," + "PAYMENT.TYPE+:2," + "PAYMENT.METHOD:2,"
				+ "PAYMENT.FREQ:2," + "PROPERTY:2:1," + "DUE.FREQ:2:1," + "START.DATE:2:1," + "ACTUAL.AMT:2:1,"
				+ "PROPERTY:2+:2," + "DUE.FREQ:2:2,";
		values = endOfHoldPeriod + "," + endOfHoldPeriod + "," + amount + "," + "BLENDED," + "Due," + paymentFrequency1
				+ "," + "ACCOUNT," + paymentFrequency21 + "," + startOfRegularPeriod + "," + mergedBillDue + ","
				+ "PRINCIPALINT," + paymentFrequency22 + ",";

		result = multiInputData(fields, values, false) && inputTable.commitAndOverride();

		if (result) {
			result = authorizeEntity(arrangement, ACTIVITY + "," + productGroup);
		}
		if (!result) {
			Reporter.log("Error while performing holdPayment");
		}

		return result;
	}

	public boolean debitCard(final String action, final String customer, final String customerType,
			final String step1Fields, final String step1Values, final String step2Fields, final String step2Values,
			final String RoleBasedPage) {

		String cardType;
		String finalMessage;
		boolean result = false;

		switch (action) {

		case "Issue":
			commandLine("CARD.ISSUE,CAMB.ISS.BRANCH.OL.CIF", commandLineAvailable);
			if (BUSINESS.equals(customerType)) {
				cardType = "B2BC.NEW";
			} else {
				cardType = "B2BP.NEW";
			}

			enquiryElements.transactionIdField("Issue New Card").sendKeys(cardType);
			toolElements.toolsButton(EDIT_CONTRACT).click();
			result = multiInputData(step1Fields, step1Values, false);
			toolElements.toolsButton(COMMIT_DEAL).click();
			if (!"".equals(step2Fields)) {
				result = multiInputData(step2Fields, step2Values, false);
			}
			toolElements.toolsButton(COMMIT_DEAL).click();

			break;
		default:
		case "Link":
		case "Terminate":

			findCIF(customer, customerType, RoleBasedPage);
			switchToTab("Card Management", "");
			Select dropdown = new Select(compositeScreen.actionDropDown("", "drillbox"));

			if ("Link".equals(action)) {
				dropdown.selectByVisibleText("View or Modify Access & Limits");
			} else {
				dropdown.selectByVisibleText("Terminate Card");
			}

			versionScreen.activityAction("Ordered", "", "Select Drilldown").click();
			switchToPage(LASTPAGE, false);
			result = multiInputData(step1Fields, step1Values, false);
			toolElements.toolsButton(COMMIT_DEAL).click();
			break;
		}

		if (result) {
			finalMessage = readTable.message().getText();
			if (!finalMessage.contains(TXN_COMPLETE)) {
				result = false;
			}
			switchToPage(environmentTitle, true);
		}

		return result;

	}

	public String companyChange(final String action, final String branch, final String fields, final String values) {

		boolean result = false;
		String transitChange;

		commandLine("EB.COMPANY.CHANGE", commandLineAvailable);
		toolElements.toolsButton("New Deal").click();
		switchToPage(LASTPAGE, false);
		multiInputData(fields, values, false);
		inputTable.selectionCriteriaButton("COMPANY.TO").click();
		switchToPage(LASTPAGE, false);
		enquiryElements.enquirySearch("COMPANY.CODE", "contains", branch);
		enquiryElements.linkToSelect("COMPANY.TO").click();
		switchToPage(LASTPAGE, false);

		if (action.contains(CREATE)) {

			toolElements.toolsButton(COMMIT_DEAL).click();
			result = inputTable.verifyAcceptOverride() && readTable.message().getText().contains(TXN_COMPLETE);
			if (result) {
				transitChange = readTable.message().getText().split("\\s+")[2];
			} else {
				transitChange = "Error: Problem changing company";
			}

			if (action.contains(AUTHORISE)) {
				authorizeEntity(transitChange, "Product Transit Change");
			}

		} else {
			transitChange = "Framework cannot handle action: " + action + " on companyChange() function";
		}

		return transitChange;
	}

	public boolean createEscrowAccount(final String arrangement, final String productGroup, final String product,
			final String payee, final String fields, final String values) {
		String actualFields;
		String actualValues;
		boolean result;

		if ("".equals(fields)) {
			actualFields = DefaultVariables.escrowAccountFields + "Default#PAYEE:1," + "Disbursements#PAYEE:1,";
			actualValues = DefaultVariables.escrowAccountValues + payee + "," + payee + ",";
		} else {
			actualFields = fields + "Default#PAYEE:1," + "Disbursements#PAYEE:1,";
			actualValues = values + payee + "," + payee + ",";
		}

		Reporter.log("Creating Escrow Account for " + product + ": " + arrangement, debugMode);

		commandLine("ESCROW.ACCOUNT,NEW I", commandLineAvailable);
		enquiryElements.transactionIdField("Escrow Account").click();
		enquiryElements.transactionIdField("Escrow Account").sendKeys(arrangement);
		toolElements.toolsButton("Edit a contract").click();
		multiInputData(actualFields, actualValues, false);

		toolElements.toolsButton(COMMIT_DEAL).click();
		result = inputTable.verifyAcceptOverride() && DefaultVariables.authorizeEscrowAccount
				? authorizeEntity(arrangement, ESCROW_ACCOUNT)
				: true;

		return result;
	}

	public String syndicationAgreement(final String action, final String agreement, final String fields,
			final String values) {
		String commandLine;
		String message;
		boolean result;

		if ("".equals(agreement)) {
			commandLine = "CAMB.H.SL.DETAILS,LBC.CAMB I F3";
		} else {
			switch (action) {
			case "Close":
				commandLine = "CAMB.H.SL.DETAILS,CAMB.CHANGE I " + agreement;
				break;
			case "Renew":
				commandLine = "CAMB.H.SL.DETAILS,CAMB.MAT I " + agreement;
				break;
			default:
				commandLine = "CAMB.H.SL.DETAILS,LBC.CAMB I " + agreement;
			}
		}

		commandLine(commandLine, commandLineAvailable);
		result = multiInputData(fields, values, false);

		if (!OPEN.equals(action)) {
			result = inputTable.commitAndOverride();
		}

		message = readTable.message().getText();
		if (!message.contains(TXN_COMPLETE)) {
			message = "Error: Problem Creating Syndication Agreement";
		}

		return message.substring(14, 25);
	}

	public String activitySimulations(final String action, final String identifier, final String arrangement,
			final String productGroup, final String fields, final String values) {
		String[] identifierValues;
		String simulationID = identifier;
		String finalMessage = "";
		String simulationStatus;
		Select dropdown;
		boolean result = false;

		if (action.contains("Simulate")) {
			result = arrangementActivity(action, identifier, arrangement, productGroup, fields, values);
			if (action.contains(CREATE)) {
				switchToPage("Sim Runner Status", false);
				simulationID = readTable.elementValue("ENQ-H-ID_AASIMULATIONMONITOR").getText();
			}
			if (!result || simulationID == null || simulationID.contains(ERROR)) {
				simulationID = "Error while performing activity simulation for arrangement: " + arrangement;
				Reporter.log(simulationID, debugMode);
			}
		} else if ("Compare".equals(action)) {

			if (!"".equals(arrangement)) {
				findArrangement(AUTHORISED, arrangement, ARRANGEMENT, "", productGroup, "", "");
			}
			switchToPage(LASTPAGE, false);
			versionScreen.linkText(ADDITIONAL_DETAILS, "Sims").click();
			toolElements.launchButton("launch.gif").click();
			identifierValues = identifier.split(",");
			for (int i = 0; i < identifierValues.length; i++) {

				inputTable.selectionCriteriaButton("SIM.1:" + (i + 1)).click();
				switchToPage(LASTPAGE, false);
				enquiryElements.enquirySearch("Arrangement", "", identifierValues[i]);
				switchToPage("List Simulation Reference", false);
				enquiryElements.linkToSelect(identifierValues[i]).click();
				switchToPage(LASTPAGE, false);
				dropdown = new Select(compositeScreen.actionDropDown("", "SIM.SELECT:" + (i + 1)));
				dropdown.selectByVisibleText("Yes");
				inputTable.multiValueButton("SIM.1:" + (i + 1), "Expand Multi Value").click();
			}
			toolElements.toolsButton(COMMIT_DEAL).click();
			try {
				versionScreen.enquiry("VersionAlone").isDisplayed();
				simulationID = identifier;
			} catch (NoSuchElementException e) {
				simulationID = "Error while comparing simulated activities for arrangement: " + arrangement;
			}
		} else {
			commandLine("ENQ LBC.AA.DETAILS.SIMULATIONS", commandLineAvailable);
			switchToPage(LASTPAGE, false);
			enquiryElements.enquirySearch("@ID", "", identifier);
			switchToPage(LASTPAGE, false);
			enquiryElements.enquiryButtons("View").click();
			switchToPage(LASTPAGE, false);
			if ("Modify".equals(action)) {
				enquiryElements.enquiryButtons("Edit").click();
				switchToPage(LASTPAGE, false);
				dropdown = new Select(compositeScreen.actionDropDown("", "AUTO.RUN"));
				dropdown.selectByVisibleText("Simulate");
				result = multiInputData(fields, values, true);
				toolElements.toolsButton(COMMIT_DEAL).click();
				if (result) {
					finalMessage = readTable.message().getText();
					if (!finalMessage.contains(TXN_COMPLETE)) {
						simulationID = "Error while modifying simulated activity for arrangement: " + arrangement;
						result = false;
					}
				}
			} else if ("Apply".equals(action) || "View".equals(action)) {
				versionScreen.viewSimActivity("r1").click();
				switchToPage(LASTPAGE, false);
				simulationStatus = readTable.readElement("Status", "2").getText();
				if (!"COMPLETED - SUCCESSFULLY".equals(simulationStatus)) {
					simulationID = "Error while Viewing simulated activity for arrangement: " + arrangement;
				}
				switchToPage("Simulation Overview (Lending)", false);
				if ("Apply".equals(action)) {
					enquiryElements.enquiryButtons("Execute").click();
					switchToPage(LASTPAGE, false);
					toolElements.toolsButton(COMMIT_DEAL).click();
					finalMessage = readTable.message().getText();
					if (!finalMessage.contains(TXN_COMPLETE)) {
						simulationID = "Error while applying simulation for arrangement: " + arrangement;
						result = false;
					}
				}
			}
		}
		return simulationID;
	}

	public String arrangementSimulations(final String action, final String customer, final String productGroup,
			final String product, final String roleBasedPage, final String effectiveDate, final String fields,
			final String values) {
		String arrangementID = "";
		String finalMessage = "";
		final String validationFields;
		final String validationValues;
		boolean result = false;

		if ("Simulate".equals(action)) {

			switch (roleBasedPage) {
			case ROLEBASED_LENDING:
				commandLine("COS LBC.LOANSMG.HP", commandLineAvailable);

				break;
			case ROLEBASED_SAE:
				commandLine("COS LBC.SAELOANS.HP", commandLineAvailable);

				break;
			case ROLEBASED_OR:
				commandLine("COS LBC.ADLM.HP", commandLineAvailable);

				break;
			case ROLEBASED_BANKING:
			default:
				commandLine("COS LBC.ACCTDEP.HP", commandLineAvailable);

				break;
			}
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "FRAMETABCSM");
			compositeScreen.switchToFrame(ID, "NAME");
			compositeScreen.switchToFrame(ID, "main");
			tabbedScreen.findTab("Product Catalog", "").click();
			switchToPage(LASTPAGE, false);
			compositeScreen.switchToFrame(ID, "FRAMETABCSM");
			compositeScreen.switchToFrame(ID, "NAME");
			compositeScreen.switchToFrame(ID, "workarea");
			compositeScreen.expandButton(productGroup).click();
			compositeScreen.simulate(product).click();
			switchToPage(LASTPAGE, false);

			validationFields = "CUSTOMER:1,CURRENCY,EFFECTIVE.DATE,";
			validationValues = customer + "," + "CAD," + effectiveDate + ",";

			multiInputData(validationFields, validationValues, true);
			toolElements.toolsButton(VALIDATE_DEAL).click();
			result = multiInputData(fields, values, true);
			result = setupRenewalAndInterest(productGroup, product, fields);

			if (AGENTS.equalsIgnoreCase(productGroup)) {
				arrangementID = readData("ARRANGEMENT");
			} else {
				arrangementID = readData("ACCOUNT.REFERENCE");
			}
			toolElements.toolsButton(COMMIT_DEAL).click();
			result = inputTable.verifyAcceptOverride();

			if (result) {
				finalMessage = readTable.message().getText();
			}
			if (result && finalMessage.contains(TXN_COMPLETE)) {
				switchToPage(environmentTitle, true);
			} else {
				arrangementID = "Error while simulating the Arrangement: " + arrangementID + finalMessage;
				Reporter.log(arrangementID, debugMode);
			}
		} else if (VIEW.equals(action)) {
			arrangementID = findArrangement("New Offers", customer, CIF, "", productGroup, product, "");
		}
		return arrangementID;
	}

	public String retrievePayoffAmount(final String arrangement, final String productGroup, final String product) {
		String result;
		String payoffDate;
		int amountColumnIndex;
		int currentPayoffRowIndex;

		if ("".equals(arrangement)) {
			switchToPage("Arrangement Overview", false);
			driver.navigate().refresh();
			Alert alert = driver.switchTo().alert();
			alert.accept();
		} else {
			findArrangement(AUTHORISED, arrangement, ARRANGEMENT, ROLEBASED_LENDING, productGroup, product, CAD);
		}
		switchToPage(LASTPAGE, false);
		versionScreen.waitForLoading();

		try {
			payoffDate = versionScreen.labelElement("Payoff Statement for").getText();
			versionScreen.activityAction("Payoff Statement for", "", "Payoff Statement").click();
			switchToPage(LASTPAGE, false);
			versionScreen.waitForLoading();
			amountColumnIndex = enquiryElements.getColumnHeaderNumber("Payoff Total", "Amount");
			currentPayoffRowIndex = enquiryElements.getRowNumberMatching("Payoff Total", "Payoff Date", payoffDate);
			result = enquiryElements
					.getElementAtCell("Payoff Total", "PayoffTotal", amountColumnIndex, currentPayoffRowIndex).getText()
					.replaceAll(",", "");
		} catch (NoSuchElementException e) {
			result = "Error retrieving the Payoff Amount";
		}

		return result;
	}

	public boolean skipPayment(final String arrangement, final String roleBasedPage, final String productGroup,
			final String product, final String additionalProperties) throws ParseException {
		final SimpleDateFormat enquiryDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
		final SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
		List<WebElement> listOfPaymentTypes;
		Calendar cal;
		Date date;
		String fields;
		String values;
		String amount;
		String endOfSkipPeriod;
		String nextBillDate = "19000101";
		String nextBillPrincipal = "";
		String nextBillInterest = "";
		String actualArrangement = arrangement;
		int diffDays;
		int nextBillDateIndex;
		boolean result = false;

		Reporter.log("Performing Skip Payment Activity for arrangement: " + arrangement, debugMode);
		if (productGroup.equals(RETAIL_MORTGAGES)) {
			if (!"".equals(arrangement)) {
				actualArrangement = findArrangement(AUTHORISED, arrangement, ARRANGEMENT, roleBasedPage, productGroup,
						product, CAD);
			}
			switchToPage(LASTPAGE, false);
			versionScreen.linkText(ADDITIONAL_DETAILS, "Schedule").click();
			versionScreen.waitForLoading();

			nextBillDateIndex = retrieveNextDuePaymentAfter(inputDateFormat.format(new Date())) + 1;

			nextBillDate = inputDateFormat.format(enquiryDateFormat.parse(enquiryElements
					.getElementAtCell("Payment Schedule", "AdditionalDetails",
							enquiryElements.getColumnHeaderNumber("Payment Schedule", "Date"), nextBillDateIndex)
					.getText()));
			nextBillPrincipal = enquiryElements
					.getElementAtCell("Payment Schedule", "AdditionalDetails",
							enquiryElements.getColumnHeaderNumber("Payment Schedule", "Principal"), nextBillDateIndex)
					.getText().replaceAll(",", "").trim();
			nextBillInterest = enquiryElements
					.getElementAtCell("Payment Schedule", "AdditionalDetails",
							enquiryElements.getColumnHeaderNumber("Payment Schedule", "Interest"), nextBillDateIndex)
					.getText().replaceAll(",", "").trim();

			fields = "PAYMENT.TYPE:1," + "PAYMENT.METHOD:1," + "PROPERTY<:1:1," + "ACTUAL.AMT:1:1,";
			values = "INTEREST.ONLY," + "Capitalise," + "ACCOUNT," + "0.00,";
			result = arrangementActivity(OPEN, "Skip Payment Activity", actualArrangement, productGroup, "", "");

			listOfPaymentTypes = inputTable.listOfFields("PAYMENT.TYPE", "Schedule");
			if (additionalProperties.contains("Escrow")) {
				for (int i = 1; i <= listOfPaymentTypes.size(); i++) {
					if ("ESCROW".equals(readData("PAYMENT.TYPE:" + i))) {
						inputData("PAYMENT.METHOD:" + i, "Capitalise", false);
						break;
					}
				}
			} else if (additionalProperties.contains("Insurance")) {
				for (int i = 1; i <= listOfPaymentTypes.size(); i++) {
					if ("INSURANCE".equals(readData("PAYMENT.TYPE:" + i))) {
						inputData("PAYMENT.METHOD:" + i, "Capitalise", false);
						break;
					}
				}
			}

			result = multiInputData(fields, values, false) && inputTable.commitAndOverride();

			if (result) {
				fields = "FIXED.AMOUNT,";
				values = "0.00,";
				result = arrangementActivity(CREATE_AUTHORISE, "CHANGE ACTIVITY FOR ALSKIPFEE", actualArrangement,
						productGroup, fields, values);
			}

			date = inputDateFormat.parse(nextBillDate);
			cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DATE, 1);
			date = cal.getTime();
			endOfSkipPeriod = inputDateFormat.format(date);

			diffDays = (int) ((inputDateFormat.parse(endOfSkipPeriod).getTime()
					- inputDateFormat.parse(inputDateFormat.format(new Date())).getTime()) / (24 * 60 * 60 * 1000));

			amount = Double.toString(Double.parseDouble(nextBillPrincipal) + Double.parseDouble(nextBillInterest));
			if (result) {
				fields = "PAYMENT.TYPE:1," + "PAYMENT.METHOD:1," + "PROPERTY:1+:2," + "ACTUAL.AMT:1:1,";
				values = "BLENDED," + "Due," + "ACCOUNT," + amount + ",";
				result = arrangementActivity("Open Scheduled +" + diffDays + "d", "Skip Payment Activity",
						actualArrangement, productGroup, fields, values);

				listOfPaymentTypes = inputTable.listOfFields("PAYMENT.TYPE", "Schedule");
				if (additionalProperties.contains("Escrow")) {
					for (int i = 1; i <= listOfPaymentTypes.size(); i++) {
						if ("ESCROW".equals(readData("PAYMENT.TYPE:" + i))) {
							inputData("PAYMENT.METHOD:" + i, "Due", false);
							break;
						}
					}
				} else if (additionalProperties.contains("Insurance")) {
					for (int i = 1; i <= listOfPaymentTypes.size(); i++) {
						if ("INSURANCE".equals(readData("PAYMENT.TYPE:" + i))) {
							inputData("PAYMENT.METHOD:" + i, "Due", false);
							break;
						}
					}
				}

				result = multiInputData(fields, values, false) && inputTable.commitAndOverride();
			}
			if (result) {
				fields = "FIXED.AMOUNT,";
				values = "75.00,";
				result = arrangementActivity(CREATE_AUTHORISE, "CHANGE ACTIVITY FOR ALSKIPFEE", actualArrangement,
						productGroup, fields, values);
			}
		} else if (productGroup.equals(PERSONAL_LOANS)) {
			fields = "HOL.PAYMENT.TYPE:1," + "HOL.START.DATE:1," + "HOL.NUM.PAYMENTS:1,";
			values = "SPECIAL," + "+0d," + "1,";
			result = arrangementActivity(CREATE, "DEFINE.HOLIDAY ACTIVITY FOR SCHEDULE", actualArrangement,
					productGroup, fields, values);
		} else {

			Reporter.log("SkipPayment does not support this productGroup: " + productGroup);
			result = false;
		}
		return result;
	}

	public boolean postingRestrict(final String action, final String restrictType, final String entity,
			final String customerType, final String productGroup, final String fields, final String values) {

		WebElement displayedMessage;
		String postingRestrict;
		boolean result = true;

		Reporter.log(action + " posting restrict on " + entity, debugMode);

		if ("Remove".equals(action)) {

			if (CUSTOMER.equals(restrictType)) {
				findCIF(entity, customerType, "");
				versionScreen.waitForLoading();
				versionScreen.postRestrictionButton().click();
				switchToPage(LASTPAGE, false);
				result = inputData("POSTING.RESTRICT:1", "", true) && inputTable.commitAndOverride();

			} else if (ARRANGEMENT.equals(restrictType)) {
				result = arrangementActivity(CREATE, "Update account", entity, productGroup,
						"Account$POSTING.RESTRICT:1", "");

			} else {
				result = false;
				Reporter.log("Restrict type:" + restrictType + " is invalid for action:" + action);
			}

		} else {

			if ("Menu".equals(restrictType)) {
				commandLine("COS CUST.POST.RESTRICT", commandLineAvailable);
				compositeScreen.switchToFrame(ID, "Enquiry");
				enquiryElements.enquirySearch(CUSTOMER, "", entity);
				switchToPage(LASTPAGE, false);
				compositeScreen.switchToFrame(ID, "Enquiry");
				enquiryElements.enquiryButtons(POSTING_RESTRICT).click();
				switchToPage(LASTPAGE, false);
				compositeScreen.switchToFrame(ID, "Blank");
				result = multiInputData(fields, values, false) && inputTable.commitAndOverride();

				if (result) {
					result = authorizeEntity(entity, POSTING_RESTRICT);
				}

			} else if (CUSTOMER.equals(restrictType)) {
				findCIF(entity, customerType, "");
				versionScreen.waitForLoading();
				versionScreen.postRestrictionButton().click();
				switchToPage(LASTPAGE, false);
				result = multiInputData(fields, values, false) && inputTable.commitAndOverride();

			} else if (ARRANGEMENT.equals(restrictType)) {
				result = arrangementActivity(CREATE, "Update account", entity, productGroup, fields, values);

			} else {
				findCIF(entity, customerType, "");
				switchToTab("Set Restrictions", "");
				enquiryElements.enquirySearch(CUSTOMER, "", entity);
				enquiryElements.linkPR(entity, restrictType).click();
				switchToPage(LASTPAGE, false);
				result = multiInputData(fields, values, false) && inputTable.commitAndOverride();
				try {
					displayedMessage = readTable.message();
					postingRestrict = displayedMessage.getText();
					Reporter.log("Posting Restrict created successfully: " + postingRestrict, debugMode);
				} catch (NoSuchElementException e) {
					result = false;
				}

			}
		}
		return result;
	}

	public boolean bankingCheque(final String action, final String type, final String arrangement, final String product,
			final String fields, final String values) {

		String data = "";
		String commandLine;
		String tableSummary = "";
		String chequeReference = "";
		String referenceField;
		String identifierColumnText = "";
		String transactionField;
		String typeLink;
		String[] listOfFields;
		String[] listOfValues;
		boolean result = true;

		if (action.contains("Revoke")) {
			commandLine = "PAYMENT.STOP,REVOKE.SCV.AR";
			transactionField = "Revoke Stop Payment";
			if (CHEQUE.equals(type)) {
				tableSummary = "Cheques Stopped - by Number";
				identifierColumnText = "Number";
				referenceField = "MOD.PS.CHQ.NO:1";
			} else {
				tableSummary = "Cheques Stopped - by Amount";
				identifierColumnText = "From Amount";
				referenceField = "AMOUNT.FROM:1";
			}
			typeLink = "Revoke Stop";

		} else {
			if (CHEQUE.equals(type)) {
				commandLine = "PAYMENT.STOP,INPUTCHQ.SCV.AR";
				referenceField = "FIRST.CHEQUE.NO:1";
				typeLink = "Stop Cheque";
			} else {
				commandLine = "PAYMENT.STOP,INPUTAMT.SCV.AR";
				referenceField = "AMOUNT.FROM:1";
				typeLink = "Stop Amount";
			}
			transactionField = "Input Stop Payment";
		}

		if (!"".equals(arrangement) && !product.contains(HELOC) && !product.contains("Line of Credit")) {
			findArrangement(AUTHORISED, arrangement, ARRANGEMENT, "", PERSONAL_ACCOUNTS, product, "");
			switchToPage(LASTPAGE, false);
		}

		switch (action) {
		case "Request Book":
			versionScreen.linkText(FACILITIES, CHEQUE).click();
			versionScreen.linkText("New", "Request Cheque Book").click();
			result = inputTable.commitAndOverride();
			versionScreen.linkText("Cheque Enquiries", "Requested").click();

			break;
		case "Issue Book":
			versionScreen.linkText(FACILITIES, CHEQUE).click();
			versionScreen.linkText("Cheque Enquiries", "Requested").click();
			try {
				data = versionScreen.dataDisplayTable("REQUESTED").getText();

				if (data.contains("No Cheques")) {
					result = false;
				} else {
					versionScreen.issueCheque().click();
					switchToPage(LASTPAGE, false);
					result = multiInputData(fields, values, false) && inputTable.commitAndOverride();
					versionScreen.linkText("Cheque Enquiries", "Issued").click();
				}

			} catch (NoSuchElementException e) {
				result = false;
				Reporter.log(e.getMessage(), false);
			}

			break;
		case "Stop":
		case "Revoke Stop":
		default:
			if (product.contains(HELOC) || product.contains("Line of Credit")) {
				commandLine(commandLine, commandLineAvailable);
				enquiryElements.transactionIdField(transactionField).sendKeys(arrangement);
				toolElements.toolsButton(EDIT_CONTRACT).click();
				switchToPage(LASTPAGE, false);

			} else {
				versionScreen.linkText(FACILITIES, CHEQUE).click();
				if (action.contains("Revoke")) {
					versionScreen.linkText("Cheque Enquiries", "Stopped").click();
					data = versionScreen.dataDisplayTable("STOPPED_BY_NUMBER").getText();

					if (data.contains("No Cheques")) {
						result = false;
					} else {
						listOfFields = fields.split(",");
						listOfValues = values.split(",");
						for (int i = 0; i < listOfFields.length; i++) {
							if (listOfFields[i].equals(referenceField)) {
								chequeReference = listOfValues[i];
								break;
							}
						}
						if (enquiryElements.getRowNumberMatching(tableSummary, identifierColumnText,
								chequeReference) <= 0) {
							result = false;
						}
					}
				}
				if (result) {
					versionScreen.linkText("New", typeLink).click();
					switchToPage(LASTPAGE, false);
				}
			}
			result = multiInputData(fields, values, true) && inputTable.commitAndOverride();

			break;
		}
		return result;
	}

	public int retrieveNextDuePaymentAfter(final String startDate) {
		String nextBillDate;
		String nextBillDue;
		int nextBillIndex = 0;
		int numberOfPayments = enquiryElements.enquiryResults("Total");
		int paymentsPerPage = enquiryElements.enquiryResults("Current Page Last");
		boolean nextBillDateFound = false;
		Date date = null;
		SimpleDateFormat enquiryDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
		SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);

		for (int i = 0; i < numberOfPayments && !nextBillDateFound; i++) {
			nextBillIndex = i % paymentsPerPage;
			if (i > 0 && nextBillIndex == 0) {
				enquiryElements.clickNextPage();
			}

			nextBillDate = enquiryElements
					.getElementAtCell("Payment Schedule", "AdditionalDetails",
							enquiryElements.getColumnHeaderNumber("Payment Schedule", "Date"), nextBillIndex + 1)
					.getText();
			try {
				date = enquiryDateFormat.parse(nextBillDate);
			} catch (ParseException e) {
				Reporter.log("Next Bill Date could not parse into date format", debugMode);
			}
			nextBillDate = inputDateFormat.format(date);

			nextBillDue = enquiryElements
					.getElementAtCell("Payment Schedule", "AdditionalDetails",
							enquiryElements.getColumnHeaderNumber("Payment Schedule", "Total Due"), nextBillIndex + 1)
					.getText().replaceAll(",", "");

			nextBillDateFound = nextBillDate.compareTo(startDate) > 0 && !nextBillDue.equals("0.00");
		}

		return nextBillIndex;
	}
}
