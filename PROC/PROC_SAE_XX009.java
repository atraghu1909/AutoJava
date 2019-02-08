package testcases.PROC;

import java.util.ArrayList;

import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.DefaultVariables;

public class PROC_SAE_XX009 extends testLibs.BaseTest_DataGen {
	private static String documentVersion = "2018-05-31";
	private String customer1;
	private String customer2;
	private String customerHold;
	private String limit;
	private String limitHold;
	private String limitReversed;
	private String ParentLimitForHold;
	private String mainArrangement;
	private String searchField;
	private String searchCriteria;
	private String searchValue;
	private String searchResult;
	private String collateralLink;
	private String collateralLinkHold;
	private String collateralLinkReversed;
	private String collateral;
	private String collateralHold;
	private String collateralReversed;
	private String resultNumber;
	private String customerFIC = "987654321";
	private boolean result;

	@Test(priority = 1, enabled = true)

	@Parameters({ "customerType", "productGroup", "product", "branch" })
	public void preCondition(final String customerType, final String productGroup, final String product,

			@Optional("LAURENTIAN BANK - 523") final String branch) {

		String collateralLinkFields;
		String collateralLinkValues;
		String collateralFields;
		String collateralValues;
		String collateralLinkForHold;
		String collateralLinkForReverse;
		String collateralForReverse;
		String limitForReverse;
		String limitReferenceForReverse;
		String limitSerialForReverse;
		String customerFields;
		String customerValues;

		stepDescription = "Create a Customer with a FIC number, place a Customer Record on Hold, create a secured Loan, create and reverse a set of Limit and Collateral records, and place another set of Limit and Collateral Records on hold";
		stepExpected = "Customer with a FIC number and secured Loan created successfully, Customer record, a set of Limit and Collateral records created and reversed successfully, and another Customer Record, set of Limit and Collateral Records placed on hold successfully";
		if (loginResult) {
			switchToBranch(branch);

			customerFields = DefaultVariables.customerTypeFields.get(customerType)
					+ "External System ID#EXTERN.SYS.ID:1," + "External System ID#EXTERN.CUS.ID:1,";
			customerValues = DefaultVariables.customerTypeValues.get(customerType) + "FIC," + customerFIC + ",";

			customer1 = customer(CREATE, customerType, ROLEBASED_SAE, customerFields, customerValues);
			amendCIF(CREATE, customer1, customerType, ROLEBASED_SAE, "BUSINESS.NAME:1,", "no one,");
			customer2 = createDefaultCustomer(customerType, productGroup, ROLEBASED_SAE);
			if (customer1 == null || customer1.contains(ERROR) || customer2 == null || customer2.contains(ERROR)) {
				stepActual = "Error while creating customers";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			customerHold = customer(OPEN, customerType, ROLEBASED_SAE, "", "");
			toolElements.toolsButton(HOLD).click();

			Reporter.log("customerHold: " + customerHold, debugMode);
			ArrangementData arrangementData = new ArrangementData("mainArrangement", productGroup, product)
					.withCustomers(customer1, createdCustomers.get(customer1), "", "100,", "100,").build();
			mainArrangement = createDefaultArrangement(arrangementData);
			limit = arrangementData.getCompleteLimitId();
			collateralLink = arrangementData.getcollateralLink();
			collateral = arrangementData.getcollateralDetails();

			if (mainArrangement == null || mainArrangement.contains(ERROR)) {
				stepActual = "Error while creating " + product + " Arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			if (limit == null || limit.contains(ERROR)) {
				stepActual = "Error while creating Limit for customer: " + customer1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			if (collateralLink == null || collateralLink.contains(ERROR)) {
				stepActual = "Error while creating collateral Link for customer: " + customer1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			if (collateral == null || collateral.contains(ERROR)) {
				stepActual = "Error while creating collateral for customer: " + customer1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			limitForReverse = customerLimit(CREATE, "Revolving Secured", ROLEBASED_SAE, product, customer1, "2403",
					"01", DefaultVariables.securedSAELimitFields, DefaultVariables.securedSAELimitValues, false);

			limitReferenceForReverse = limitForReverse.substring(13, 17);
			limitSerialForReverse = limitForReverse.length() > 2
					? limitForReverse.substring(limitForReverse.length() - 2) : "";

			collateralLinkFields = "COLLATERAL.CODE," + "COMPANY:1," + "LIMIT.REF.CUST:1," + "LIMIT.REFERENCE:1,";
			collateralLinkValues = "1," + "B2B," + customer1 + "," + limit + ",";
			collateralLinkForReverse = collateral(CREATE, COLLATERAL_LINK, customer1, collateralLinkFields,
					collateralLinkValues);

			collateralFields = DefaultVariables.collateralFields + "Collateral details#CA.ADR.LINE1,"
					+ "Collateral details#TOWN.CITY," + "Collateral details#US.STATE,"
					+ "Collateral details#CA.POST.CODE," + "Collateral#COLLATERAL.CODE," + "COLLATERAL.TYPE,";
			collateralValues = DefaultVariables.collateralValues + "123," + "Toronto," + "ON," + "A2C1E4," + "1,"
					+ "1,";
			collateralForReverse = collateral(CREATE, COLLATERAL_DETAILS, collateralLink, collateralFields,
					collateralValues);

			collateralReversed = collateral(REVERSE, COLLATERAL_DETAILS, collateralForReverse, "", "");
			collateralLinkReversed = collateral(REVERSE, COLLATERAL_LINK, collateralLinkForReverse, "", "");
			limitReversed = customerLimit(REVERSE, "Revolving Secured", ROLEBASED_SAE, product, customer1,
					limitReferenceForReverse, limitSerialForReverse, "", "");

			ParentLimitForHold = customerLimit(CREATE, "Revolving Secured", ROLEBASED_SAE, product, customer2, "2400",
					"", DefaultVariables.securedSAELimitFields, DefaultVariables.securedSAELimitValues, false);
			customerLimit(OPEN, "Revolving Secured", ROLEBASED_SAE, product, customer2, "", "", "", "");
			limitHold = compositeScreen.completeLimitId().getText();
			toolElements.toolsButton(HOLD).click();
			Reporter.log("limitHold: " + limitHold, debugMode);

			collateralLinkFields = "COLLATERAL.CODE," + "COMPANY:1," + "LIMIT.REF.CUST:1," + "LIMIT.REFERENCE:1,";
			collateralLinkValues = "1," + "B2B," + customer2 + "," + ParentLimitForHold + ",";
			collateralLinkForHold = collateral(CREATE, COLLATERAL_LINK, customer2, collateralLinkFields,
					collateralLinkValues);
			collateralLinkHold = collateral(OPEN, COLLATERAL_LINK, customer2, "", "");
			toolElements.toolsButton(HOLD).click();
			Reporter.log("collateralLinkHold: " + collateralLinkHold, debugMode);
			collateralHold = collateral(OPEN, COLLATERAL_DETAILS, collateralLinkForHold, "", "");
			toolElements.toolsButton(HOLD).click();
			Reporter.log("collateralHold: " + collateralHold, debugMode);
			if (collateralReversed == null || collateralReversed.contains(ERROR)) {
				stepActual = "Error while Reversing Collateral for customer: " + customer1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			if (collateralLinkReversed == null || collateralLinkReversed.contains(ERROR)) {
				stepActual = "Error while Reversing Collateral Link for customer: " + customer1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			if (limitReversed == null || limitReversed.contains(ERROR)) {
				stepActual = "Error while Reversing Limit for customer: " + customer1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			if (limitHold == null || limitHold.contains(ERROR)) {
				stepActual = "Error while put the Limit creation on hold customer: " + customer2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			if (collateralLinkHold == null || collateralLinkHold.contains(ERROR)) {
				stepActual = "Error while put the Collateral Link creation on hold customer: " + customer2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			if (collateralHold == null || collateralHold.contains(ERROR)) {
				stepActual = "Error while put the Collateral creation on hold customer: " + customer2;
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

	@Parameters({ "customerType" })
	public void searchClient(final String customerType) {

		stepDescription = "Search Customer: " + customer1;
		stepExpected = "Customer overview has been opened successfully";
		String actualCustomer;

		if (customer1 == null || customer1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			actualCustomer = findCIF(customer1, customerType, ROLEBASED_SAE);
			if (!customer1.equals(actualCustomer)) {
				stepActual = "Error while searching customer: " + customer1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)

	@Parameters({ "customerType" })
	public void searchCustomerByFIC(final String customerType) {

		stepDescription = "Search Customer by FIC: " + customerFIC;
		stepExpected = "customer has been searched successfully by FIC: " + customerFIC;
		String actualCustomer;

		if (customer1 == null || customer1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			toolElements.listAndSearchFile(CUSTOMER, "Search Live File");
			switchToPage(LASTPAGE, false);
			enquiryElements.enquirySearch("EXTERN.SYS.ID,EXTERN.CUS.ID", ",", "FIC," + customerFIC);
			resultNumber = versionScreen.getEnqListCell("r", 0, "2").getText();
			actualCustomer = findCIF(resultNumber, customerType, ROLEBASED_SAE);
			if (!resultNumber.equals(actualCustomer)) {
				stepActual = "Error while searching customer: " + resultNumber;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	public void listAndSearchFile() {
		int rowNumber = 1;
		String headerMessage;
		String pageTitle = null;

		stepDescription = "List and Search Files";
		stepExpected = "Files' Record Found successfully for provided entities and Options";

		if (customer1 == null || customer1.contains(ERROR) || customer2 == null || customer2.contains(ERROR)
				|| customerHold == null || customerHold.contains(ERROR) || limit == null || limit.contains(ERROR)
				|| collateralLink == null || collateralLink.contains(ERROR) || collateral == null
				|| collateral.contains(ERROR) || collateralReversed == null || collateralReversed.contains(ERROR)
				|| collateralLinkReversed == null || collateralLinkReversed.contains(ERROR) || limitReversed == null
				|| limitReversed.contains(ERROR) || limitHold == null || limitHold.contains(ERROR)
				|| collateralLinkHold == null || collateralLinkHold.contains(ERROR) || collateralHold == null
				|| collateralHold.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			final List<String> entities = new ArrayList<String>();
			entities.add(CUSTOMER);
			entities.add(LIMIT);
			entities.add(COLLATERAL_LINK);
			entities.add(COLLATERAL);

			final List<String> options = new ArrayList<String>();
			options.add("List Live File");
			options.add("List Unauth File");
			options.add("List History File");
			options.add("Search Live File");
			options.add("Search Unauth File");
			options.add("Search History File");

			for (final String currentEntity : entities) {
				for (final String currentOption : options) {

					toolElements.listAndSearchFile(currentEntity, currentOption);
					switchToPage(LASTPAGE, false);

					if (currentOption.contains("List")) {
						headerMessage = enquiryElements.enqHeaderMsg().getText();
						if ("No data to display".equals(headerMessage)) {
							stepActual = "Error Searching: " + currentOption + " for " + currentEntity;
							softVerify.fail(stepActual);
							stepResult = StatusAs.FAILED;
						} else {

							Reporter.log(headerMessage + " is displayed for " + currentOption + " for " + currentEntity,
									debugMode);
						}

					} else if (currentOption.contains("Search")) {
						pageTitle = driver.getTitle();
						if (pageTitle.contains("CUSTOMER") || pageTitle.contains("LIMIT")
								|| pageTitle.contains("COLLATERAL")) {
							switch (currentEntity + "," + currentOption) {
							case CUSTOMER + ",Search Live File":
								searchField = "CUSTOMER.NO";
								searchCriteria = "equals";
								searchValue = customer1;
								searchResult = customer1;
								break;
							case CUSTOMER + ",Search Unauth File":
								searchField = "CUSTOMER.NO";
								searchCriteria = "equals";
								searchValue = customerHold;
								searchResult = customerHold;
								break;

							case CUSTOMER + ",Search History File":
								searchField = "CUSTOMER.NO";
								searchCriteria = "contains";
								searchValue = customer1;
								searchResult = customer1;
								break;

							case LIMIT + ",Search Live File":
								searchField = "CREDIT.LINE.NO";
								searchCriteria = "contains";
								searchValue = customer1;
								searchResult = limit;
								break;
							case LIMIT + ",Search Unauth File":
								searchField = "CREDIT.LINE.NO";
								searchCriteria = "contains";
								searchValue = customer2;
								searchResult = limitHold;
								break;

							case LIMIT + ",Search History File":
								searchField = "CREDIT.LINE.NO";
								searchCriteria = "contains";
								searchValue = customer1;
								searchResult = limitReversed;
								break;

							case COLLATERAL_LINK + ",Search Live File":
								searchField = "@ID";
								searchCriteria = "contains";
								searchValue = customer1;
								searchResult = collateralLink;
								break;
							case COLLATERAL_LINK + ",Search Unauth File":
								searchField = "@ID";
								searchCriteria = "contains";
								searchValue = customer2;
								searchResult = collateralLinkHold;
								break;

							case COLLATERAL_LINK + ",Search History File":
								searchField = "@ID";
								searchCriteria = "contains";
								searchValue = customer1;
								searchResult = collateralLinkReversed;
								break;

							case COLLATERAL + ",Search Live File":
								searchField = "@ID";
								searchCriteria = "contains";
								searchValue = customer1;
								searchResult = collateral;
								break;
							case COLLATERAL + ",Search Unauth File":
								searchField = "@ID";
								searchCriteria = "contains";
								searchValue = customer2;
								searchResult = collateralHold;
								break;

							case COLLATERAL + ",Search History File":
								searchField = "@ID";
								searchCriteria = "contains";
								searchValue = customer1;
								searchResult = collateralReversed;
								break;
							}
							enquiryElements.enquirySearch(searchField, searchCriteria, searchValue);

							headerMessage = enquiryElements.enqHeaderMsg().getText();
							if ("No data to display".equals(headerMessage)) {
								stepActual = "Error Searching: " + currentOption + " for " + currentEntity;
								softVerify.fail(stepActual);
								stepResult = StatusAs.FAILED;
							} else {
								int numberofRows = versionScreen.enquiryList("r").size();
								for (int i = 0; i < numberofRows; i++) {
									resultNumber = versionScreen.getEnqListCell("r", i, "2").getText();
									if (resultNumber.contains(searchResult)) {

										Reporter.log(resultNumber + " is displayed for " + currentOption + " for "
												+ currentEntity, debugMode);
										enquiryElements.enquiryByMagnifierGlass(Integer.toString(i + rowNumber))
												.click();
										try {
											result = versionScreen.inputElements().getAttribute("type").equals("text");
											if (result) {
												stepActual = "Read-only version of the input table is not displayed after clicking on magnifier glass";
												softVerify.fail(stepActual);
												stepResult = StatusAs.FAILED;
											} else {
												Reporter.log(
														"Read-only version of the input table is displayed while clicking on magnifier glass",
														debugMode);
											}
										} catch (NoSuchElementException e) {
											Reporter.log("Unable to find element", debugMode);
										}
										break;
									}
								}

							}
						} else {

							stepActual = "Error while" + currentOption + " for " + currentEntity;
							softVerify.fail(stepActual);
							stepResult = StatusAs.FAILED;

						}
					}
					if (!currentOption.contains("History") && !currentEntity.contains(CUSTOMER)) {
						if (!currentOption.contains("List")) {
							switch (currentEntity) {
							case LIMIT:
								pageTitle = "LIMIT";
								break;
							case COLLATERAL_LINK:
								pageTitle = "COLLATERAL.RIGHT";
								break;
							case COLLATERAL:
								pageTitle = "COLLATERAL";
								break;
							}

							readTable.tabs(pageTitle).click();
						}
						try {
							enquiryElements.enquiryByLink(Integer.toString(rowNumber)).click();
						} catch (NoSuchElementException e) {
							Reporter.log("Unable to Find the ID Link element ", debugMode);
						}
						switchToPage(LASTPAGE, false);
						compositeScreen.switchToFrame(ID, "workarea");
						compositeScreen.inputField().sendKeys(Keys.ENTER);
						result = inputTable.inputElement("fieldName").getAttribute("type").equals("text");
						if (!result) {
							stepActual = "Edit version of the input table is not displayed after clicking on the ID Link";
							softVerify.fail(stepActual);
							stepResult = StatusAs.FAILED;
						} else {
							Reporter.log("Edit version of the input table is displayed after clicking on the ID Link",
									debugMode);
						}

					}

				}
			}

		}
		softVerify.assertAll();

	}

	@Test(priority = 5, enabled = true)

	@Parameters({ "productGroup", "product" })
	public void searchForAccountAndLoans(final String productGroup, final String product) {

		stepDescription = "Search for Authorised: " + product
				+ " Arrangement and Validate if all tabs are Switchable for find arrangement enquiry screen ";
		stepExpected = product + " Arrangement is present in Authorised tab and All tabs are Switched Successfully";
		String actualArrangement;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			actualArrangement = findArrangement(AUTHORISED, mainArrangement, ARRANGEMENT, ROLEBASED_BANKING,
					productGroup, product, CAD);
			if (!mainArrangement.equals(actualArrangement)) {
				stepActual = "Error while searching Authorised Arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			String[] tabToSwitch = { UNAUTHORISED, "Pending", "New Offers", "Matured / Closed" };
			commandLine("COS AA.FIND.ARRANGEMENT.AR", commandLineAvailable);
			switchToPage(LASTPAGE, false);

			for (int i = 0; i <= 3; i++) {
				Reporter.log("Switch to " + tabToSwitch[i] + " Tab", debugMode);
				result = switchToTab(tabToSwitch[i], "");
				if (!result) {
					stepActual = "Unable to Switch " + tabToSwitch[i];
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}

		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)

	public void searchScreenFunctions() {

		stepDescription = "Check if Favourite Test Button Can be Added and Deleted ";
		stepExpected = "Favourite Test Button is Added and Deleted successfully";

		if (customer1 == null || customer1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			commandLine("ENQ LBC.LOAN.CUST.SRC.CIF", commandLineAvailable);
			enquiryElements.enquiryElement(TEXTFIELD, CUSTOMER).sendKeys(customer1);
			enquiryElements.favouritesButton("Add", "Favourite Test");
			enquiryElements.favouritesButton("Delete", "Favourite Test");

		}
		softVerify.assertAll();
	}

	@Test(priority = 7, enabled = true)

	public void otherOptions() {

		boolean result;
		String radioButtons = null;
		String displayedMessage;
		String sortByDropdownOptions;
		List<WebElement> radioButtonList;
		stepDescription = "Validate the functionalities of the other options provided after clicking on More Option on enquiry screen for find customer ";
		stepExpected = "Other options validated successfully";

		if (customer1 == null || customer1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			commandLine("ENQ LBC.LOAN.CUST.SRC.CIF", commandLineAvailable);
			versionScreen.hyperLink("More Options").click();

			sortByDropdownOptions = enquiryElements.optionsDropDown("Sort By:", "enqsel");

			if (sortByDropdownOptions.contains("Customer") && sortByDropdownOptions.contains("Last Name")
					&& sortByDropdownOptions.contains("First Name") && sortByDropdownOptions.contains("Account")) {
				Reporter.log(
						"Options(Customer, Last Name, First Name, Account) are  present in the dropdown for Sort By",
						debugMode);
			} else {
				stepActual = "Options(Customer, Last Name, First Name, Account) are not present in the dropdown for Sort By";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			radioButtonList = enquiryElements.optionsRadioButtons("Sort By:");
			for (final WebElement item : radioButtonList) {
				radioButtons = item.getAttribute("value");

				if (radioButtons.contains("A") || radioButtons.contains("D")) {
					Reporter.log("Ascending and Descending radio Buttons available", debugMode);
				} else {
					stepActual = "Ascending and Descending radio Buttons are not available ";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
			inputTable.refreshEveryField().sendKeys("200");
			enquiryElements.enquirySearch(CUSTOMER, "", customer1);
			switchToPage(LASTPAGE, false);
			result = inputTable.inputElement("refreshtime").isEnabled();
			if (!result) {
				Reporter.log("Refresh Time Text field on enquiry result is disable", debugMode);
			} else {

				stepActual = "Refresh Time Text field on enquiry result is enable ";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			enquiryElements.enquiryButtons("Toggle Timer").click();
			result = inputTable.inputElement("refreshtime").isEnabled();
			if (result) {
				Reporter.log("Refresh Time Text field on enquiry result is enabled after clicking stop button",
						debugMode);
			} else {
				stepActual = "Refresh Time Text field on enquiry result is enabled after clicking stop button ";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			versionScreen.hyperLink("Clear Selection").click();
			enquiryElements.findButton().click();
			displayedMessage = readTable.message().getText();
			if (displayedMessage.contains("At least one selection must be made")) {
				Reporter.log("All fields are blank", debugMode);
			} else {
				stepActual = "All fields are not blank after clicking Clear Selection ";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 8, enabled = true)

	public void clientEnquiries() {

		stepDescription = "Enquire customer: " + customer1;
		stepExpected = "Client's " + customer1 + " details Opened successfully";

		if (customer1 == null || customer1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			commandLine("ENQ CUSTOMER.DETS.SCV", commandLineAvailable);
			enquiryElements.enquirySearch("Customer number", "", customer1);
			switchToPage(LASTPAGE, false);
			enquiryElements.enquiryButtons("Single Customer View").click();

			commandLine("ENQ CUSTOMER.LIST", commandLineAvailable);
			enquiryElements.enquirySearch("Customer No", "", customer1);
			switchToPage(LASTPAGE, false);
			enquiryElements.enquiryButtons("View Record").click();

		}
		softVerify.assertAll();
	}
}
