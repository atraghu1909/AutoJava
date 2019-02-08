package testcases.FT;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

@Test
public class R2_TDTC001 extends testLibs.BaseTest_OOB {

	@Parameters({ "customer", "productGroup", "product", "actionToPerform" })
	public void step1(final String customer, final String productGroup, final String product,
			@Optional("") final String actionToPerform) {

		String commitmentRow;
		String commitmentAmountString;
		int totalCommitment;
		int amount;
		String fieldsForAADisbursement;
		String fieldsForAARepayment;
		String values;
		String postingDate;
		String effectiveDate;
		String activityTitle;
		String activityFields = "FIXED.AMOUNT";
		String activityValues = "25.00";
		String loanAccount = "";
		String creditAccount = "";
		String step1Fields = "CUSTOMER:1,CURRENCY,EFFECTIVE.DATE,";
		String step1Values = customer + ",CAD,-2m,";

		if (loginResult) {

			switchToBranch("B2B Branch 817");

			stepDescription = "Verify that system opens loan transaction history and perform " + actionToPerform;
			stepExpected = "System opens loan transaction history and " + actionToPerform
					+ " is successfully performed";

			try {

				loanAccount = arrangements("Create and Authorise", productGroup, product, ROLEBASED_LENDING, customer,
						step1Fields, step1Values, DefaultVariables.loanFields, DefaultVariables.loanValues);

				arrangementActivity(CREATE_AUTHORISE, "Change and Due ALNSFFEE fee", loanAccount, productGroup,
						activityFields, activityValues);

				creditAccount = arrangements("Create and Authorise", PERSONAL_ACCOUNTS, PERSONAL_CHEQUING,
						ROLEBASED_BANKING, customer, step1Fields, step1Values, DefaultVariables.bankingFields,
						DefaultVariables.bankingValues);

				findArrangement(AUTHORISED, customer, CIF, "", productGroup, product, CAD);

				commitmentRow = versionScreen.accrualCategory("id", "FinancialSummary", "Commitment (Total)").getText();
				commitmentAmountString = commitmentRow.split(" ")[2].replaceAll(",", "").replaceAll("\\.0*$", "");
				totalCommitment = Integer.parseInt(commitmentAmountString);
				System.out.println("value of total commitment is:" + totalCommitment);

				fieldsForAADisbursement = "CREDIT.ACCT.NO,DEBIT.CURRENCY,DEBIT.ACCT.NO,DEBIT.AMOUNT,DEBIT.VALUE.DATE";
				amount = totalCommitment;
				values = creditAccount + "," + CAD + "," + loanAccount + "," + amount + "," + "-1m";
				financialTransaction(CREATE_AUTHORISE, "AA Disbursement", fieldsForAADisbursement, values);
				Reporter.log("Disbursement was performed successfully", debugMode);

				fieldsForAARepayment = "CREDIT.ACCT.NO,CREDIT.CURRENCY,DEBIT.ACCT.NO,CREDIT.AMOUNT,CREDIT.VALUE.DATE";
				amount = (int) (totalCommitment * 0.50);
				values = loanAccount + "," + CAD + "," + creditAccount + "," + amount + "," + "-1m";
				financialTransaction(CREATE_AUTHORISE, "AA Repayment", fieldsForAARepayment, values);
				Reporter.log("Repayment was performed successfully", debugMode);

				findArrangement(AUTHORISED, customer, CIF, "", productGroup, product, CAD);

				switch (actionToPerform) {
				case "Open Txn History":
					versionScreen.linkText(ADDITIONAL_DETAILS, ACTIVITY).click();
					versionScreen.linkText("Activity Log", "Txn History").click();
					break;

				case "View Txn History Headers":
					versionScreen.linkText(ADDITIONAL_DETAILS, ACTIVITY).click();
					versionScreen.linkText("Activity Log", "Txn History").click();
					versionScreen.dataDisplay("id", "headingdisplay_Log").getText();
					break;

				case "View Txn History Values":
					versionScreen.linkText(ADDITIONAL_DETAILS, ACTIVITY).click();
					versionScreen.linkText("Activity Log", "Txn History").click();
					versionScreen.dataDisplay("id", "datadisplay_Log").getText();
					break;

				case "Search Txn History by Date":
					versionScreen.linkText(ADDITIONAL_DETAILS, ACTIVITY).click();
					versionScreen.linkText("Activity Log", "Txn History").click();
					postingDate = versionScreen.rowCell("r1_Log", "1").getText();
					effectiveDate = versionScreen.rowCell("r1_Log", "2").getText();

					DateFormat calenderFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
					DateFormat originalFormat = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
					String calFormPostingDate = null;
					String calFormEffectiveDate = null;
					try {
						calFormPostingDate = calenderFormat.format(originalFormat.parse(postingDate));
						calFormEffectiveDate = calenderFormat.format(originalFormat.parse(effectiveDate));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					enquiryElements.enquiryButtons(SELECTION_SCREEN).click();
					enquiryElements.enquiryElement("textField", "Start Date").clear();
					enquiryElements.enquiryElement("textField", "End Date").clear();
					enquiryElements.enquiryElement("textField", "Start Date").sendKeys(calFormPostingDate);
					enquiryElements.enquiryElement("textField", "End Date").sendKeys(calFormPostingDate);
					enquiryElements.findButton().click();
					versionScreen.dataDisplay("id", "datadisplay_Log").getText();
					enquiryElements.enquiryButtons(SELECTION_SCREEN).click();
					enquiryElements.enquiryElement("textField", "Start Date").clear();
					enquiryElements.enquiryElement("textField", "End Date").clear();
					enquiryElements.enquiryElement("textField", "Start Date").sendKeys(calFormEffectiveDate);
					enquiryElements.enquiryElement("textField", "End Date").sendKeys(calFormEffectiveDate);
					enquiryElements.findButton().click();
					versionScreen.dataDisplay("id", "datadisplay_Log").getText();
					break;

				case "Search Txn History by Activity":
					versionScreen.linkText(ADDITIONAL_DETAILS, ACTIVITY).click();
					versionScreen.linkText("Activity Log", "Txn History").click();
					activityTitle = versionScreen.rowCell("r1_Log", "5").getText();
					enquiryElements.enquiryButtons(SELECTION_SCREEN).click();
					enquiryElements.enquiryElement("textField", ACTIVITY).clear();
					enquiryElements.enquiryElement("textField", ACTIVITY).sendKeys(activityTitle);
					enquiryElements.findButton().click();
					versionScreen.dataDisplay("id", "datadisplay_Log").getText();
					break;

				case "Drill Down":
					versionScreen.linkText(ADDITIONAL_DETAILS, ACTIVITY).click();
					versionScreen.linkText("Activity Log", "Txn History").click();
					compositeScreen.actionDropDown("", "drillbox:1_1_Log").sendKeys("View Transaction");
					compositeScreen.buttonLink("href", "drillbox:1_1").click();
					switchToPage(LASTPAGE, false);
					versionScreen.dataDisplay("id", "alltab").getText();
					switchToPage("Arrangement Overview (Lending)", false);
					compositeScreen.actionDropDown("", "drillbox:1_1_Log").sendKeys("View Charge Details");
					compositeScreen.buttonLink("href", "drillbox:1_1").click();
					switchToPage(LASTPAGE, false);
					versionScreen.dataDisplay("id", "enquiryResponseData").getText();
					switchToPage("Arrangement Overview (Lending)", false);
					break;

				default:
					break;
				}

			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "Unable to perform required action";
				softVerify.fail(stepActual);

			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
	}
}
