package testcases.PROC;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.google.common.base.Preconditions;

import atu.alm.wrapper.enums.StatusAs;
import elements.TEMENOS_Enquiry;
import elements.screen.TEMENOS_CompositeScreen;
import elements.screen.TEMENOS_VersionScreen;
import elements.table.TEMENOS_InputTable;
import testLibs.DefaultVariables;

public class PROC_SAE_XX010 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.5";
	private String mainArrangement;
	private String customer;
	private String defaultCIFFields;
	private String defaultCIFValues;
	private String step1Fields;
	private String step1Values;
	private String step2Fields;
	private String step2Values;
	private String branch = "LAURENTIAN BANK - 523";
	private String beneficiary;
	private String vostroBalance = "500.00";
	private boolean result;
	String beneficiaryFields;
	String beneficiaryValues;

	@Test(priority = 1, enabled = true)
	@Parameters({ "customerType" })
	public void preCondition(final String customerType) {

		stepDescription = "Switch branch, create a customer and beneficiary for the customer";
		stepExpected = "Switch branch, customer and beneficiary for the customer created successfully";

		if (loginResult) {

			switchToBranch(branch);
			defaultCIFFields = DefaultVariables.customerTypeFields.get(customerType);
			defaultCIFValues = DefaultVariables.customerTypeValues.get(customerType);

			customer = customer(CREATE, customerType, SERVICING_ACCOUNTS, defaultCIFFields, defaultCIFValues);

			beneficiaryFields = DefaultVariables.beneficiaryFields + "OWNING.CUSTOMER," + "BEN.CUSTOMER,";
			beneficiaryValues = DefaultVariables.beneficiaryValues + customer + ","
					+ createdCustomers.get(customer).getCustomerName() + ",";

			beneficiary = beneficiaryCode(CREATE, "EFT Client", "", beneficiaryFields, beneficiaryValues);

			if (customer == null || customer.contains(ERROR)) {
				stepActual = "Error while creating customer";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else if (beneficiary == null || beneficiary.contains(ERROR)) {
				stepActual = "Error while creating beneficiary";
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

	@Test(priority = 2, dependsOnMethods = { "preCondition" }, enabled = true)
	public void createVostroAccount() {

		String fields;
		String values;
		stepDescription = "Create vostro Account";
		stepExpected = "Vostro Account created successfully";

		if (customer == null || beneficiary == null || customer.contains(ERROR) || beneficiary.contains(ERROR)) {

			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as preconditon failed";
			throw new SkipException(stepActual);

		} else {
			step1Fields = "CUSTOMER:1,CURRENCY,CUSTOMER.ROLE:1,";
			step1Values = customer + ",CAD," + "BENIFICIAL.OWNER,";
			step2Fields = "PRIMARY.OFFICER," + "CA.INTEND.USE," + "CA.THIRD.PARTY,";
			step2Values = "1," + "12," + "N,";

			mainArrangement = arrangements(CREATE, SERVICING_ACCOUNTS, "Vostro Account", ROLEBASED_SAE, customer,
					step1Fields, step1Values, step2Fields, step2Values);

			createDefaultArrangement(BUSINESS_ACCOUNTS, HISA_BUSINESS, ROLEBASED_BANKING, customer, "+0d");

			if (mainArrangement == null || mainArrangement.contains(ERROR)) {
				stepActual = "Error while creating Vostro account Arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {
				fields = "DEBIT.ACCT.NO," + "DEBIT.CURRENCY," + "CREDIT.CURRENCY," + "ORDERING.CUST:1,"
						+ "CREDIT.ACCT.NO," + "DEBIT.AMOUNT,";
				values = "CAD1100100017000," + "CAD," + "CAD," + "Test Client," + mainArrangement + "," + vostroBalance
						+ ",";
				financialTransaction(CREATE, "Account Transfer - Transfer between Accounts", fields, values);
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	public void viewVostroAccount() {

		String arranmentId;
		String productName;
		String beneficialOwner;
		String accountId;
		String arrangementDate;
		String status;
		String transit;
		String workingBalance;
		result = false;

		stepDescription = "View Vostro account";
		stepExpected = "Vostro Account validated successfully";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {

			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as failed to create vostro account";
			throw new SkipException(stepActual);
		} else {

			findArrangement(AUTHORISED, customer, CIF, "", SERVICING_ACCOUNTS, "Vostro Account", CAD);

			try {

				arranmentId = versionScreen.labelElement("Arrangement").getText();
				productName = versionScreen.labelElement("Product").getText();
				beneficialOwner = versionScreen.labelElement("Beneficial Owner").getText();
				accountId = versionScreen.labelElement("Account").getText();
				arrangementDate = versionScreen.labelElement("Arrangement Date").getText();
				status = versionScreen.labelElement("Status").getText();
				transit = versionScreen.labelElement("Transit").getText();

				result = !"".equals(arranmentId) && !"".equals(productName) && !"".equals(beneficialOwner)
						&& !"".equals(accountId) && !"".equals(arrangementDate) && !"".equals(status)
						&& !"".equals(transit);

				if (!result) {
					stepActual = "One or more elements of Arrangement overview were not displayed successfully, or had an invalid value";
				}

				workingBalance = versionScreen.labelElement("Working").getText();

				result = versionScreen.linkText("Account Details", "Cashflow").isDisplayed()
						&& !vostroBalance.equals(workingBalance);
				if (!result) {
					stepActual = "One or more elements of Account Details section were not displayed successfully, or had an invalid value";
				}

				versionScreen.linkText("Additional Details", "Transaction").click();
				result = versionScreen.columnHeader("columnHeader", "Booking Date").isDisplayed()
						&& versionScreen.columnHeader("columnHeader", "Value Date").isDisplayed()
						&& versionScreen.columnHeader("columnHeader", "Debit").isDisplayed()
						&& versionScreen.columnHeader("columnHeader", "Credit").isDisplayed()
						&& versionScreen.columnHeader("columnHeader", "Balance").isDisplayed()
						&& versionScreen.enquiryList("r1_Log").size() > 0;

				versionScreen.linkText("Additional Details", "Bills").click();
				versionScreen.linkText("Additional Details", "Charges").click();

				result = result && versionScreen.linkText("Charges", "Date").isDisplayed()
						&& versionScreen.linkText("Charges", "Type").isDisplayed();

				versionScreen.linkText("Additional Details", "Correspondence").click();
				versionScreen.linkText("Additional Details", "Activity").click();

				result = result && versionScreen.columnHeader("columnHeader", "Date").isDisplayed()
						&& versionScreen.columnHeader("columnHeader", "Type").isDisplayed()
						&& versionScreen.columnHeader("columnHeader", "Txn Amt").isDisplayed()
						&& versionScreen.columnHeader("columnHeader", "Status").isDisplayed();

				versionScreen.linkText("Additional Details", "Payment Orders").click();

				result = result && versionScreen.linkText("Payment Orders", "Receipts").isDisplayed()
						&& versionScreen.linkText("Payment Orders", "Payments").isDisplayed();

				versionScreen.linkText("Additional Details", "Statement").click();
				switchToPage("Account Statement", false);
				result = result && versionScreen.dataDisplayTable("ISSUED").isDisplayed();
				switchToPage("Arrangement Overview", false);

				if (!result) {
					stepActual = "One or more elements of Additional Details section were not displayed successfully, or had an invalid value";
				}

				versionScreen.linkText("Facilities and Conditions", "Facilities").click();

				result = versionScreen.linkText("Facilities", "Block Funds").isDisplayed()
						&& versionScreen.linkText("Facilities", "Statement Freq").isDisplayed()
						&& compositeScreen.textAction("Request Closure", "Run").isDisplayed();

				versionScreen.linkText("Facilities and Conditions", "Conditions").click();
				enquiryElements.enquiryButtons("View Arrangement").click();
				switchToPage("AA ARRANGEMENT ACTIVITY", false);
				inputTable.returnButton().click();
				switchToPage("Arrangement Overview", false);

				if (!result) {
					stepActual = "One or more elements of Facilities and Condition section were not displayed successfully, or had an invalid value";
				}

			} catch (NoSuchElementException e) {
				stepActual = "Field or label was not found while validating vostro account details ";
				result = false;
			}

			if (!result) {
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	public void modifyVostroAccount() {

		String modifiedFields;
		String modifiedValues;
		result = false;

		stepDescription = "Modify Vostro account";
		stepExpected = "Vostro Account modified successfully";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {

			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as failed to create vostro account";
			throw new SkipException(stepActual);
		} else {

			modifiedFields = "NOTES:1,";
			modifiedValues = "Test amendment,";

			result = arrangementActivity(CREATE, "RENEGOTIATE ACTIVITY FOR ARRANGEMENT", mainArrangement,
					SERVICING_ACCOUNTS, modifiedFields, modifiedValues);

			if (!result) {
				stepActual = "Unable to modify Vostro account";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();

	}

	@Test(priority = 5, enabled = true)
	public void closeVostroAccount() {

		String closureFields;
		String closureValues;
		String settlementFields;
		String settlementValues;
		result = false;

		stepDescription = "Close Vostro account";
		stepExpected = "Vostro Account closed successfully";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {

			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as failed to create vostro account";
			throw new SkipException(stepActual);
		} else {

			closureFields = "CLOSURE.REASON," + "EFFECTIVE.DATE";
			closureValues = "Other," + "+1d";
			settlementFields = "PAYMENT.ORDER.PRODUCT," + "BENEFICIARY.ID";
			settlementValues = "ACHACCL," + beneficiary;

			result = requestClosure(COMPLETE_AUTHORISE, mainArrangement, "Request Closure", SERVICING_ACCOUNTS,
					closureFields, closureValues, "Settle by Payment Order", settlementFields, settlementValues);

			if (!result) {
				stepActual = "Unable to close Vostro account";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	public void viewClosedAccount() {

		String foundArrangement;

		if (!result) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as failed to close Vostro account";
			throw new SkipException(stepActual);
		} else {

			foundArrangement = findArrangement(MATURED_CLOSED, mainArrangement, ARRANGEMENT, ROLEBASED_SAE,
					SERVICING_ACCOUNTS, "Vostro Account", CAD);

			if (!foundArrangement.contains(ERROR)) {
				stepActual = "Unable to view closed Vostro account";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

		}
		softVerify.assertAll();
	}

}
