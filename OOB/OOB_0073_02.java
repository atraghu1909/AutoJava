package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class OOB_0073_02 extends testLibs.BaseTest_OOB {

	String actualCIF;

	public String findAccrual(final String customer, final String arrangement, final String productGroup,
			final String product, final String interestCategory) {
		String accruals;
		String debitAccount;

		findArrangement(AUTHORISED, arrangement, ARRANGEMENT, "", productGroup, product, CAD);
		switch (productGroup) {
		default:
		case PERSONAL_ACCOUNTS:

			versionScreen.linkText("Account Details", "Accruals").click();
			accruals = versionScreen.accrualCategory("id", "AccountDetails", interestCategory).getText();

			break;
		case RETAIL_DEPOSITS:
		case PERSONAL_LOANS:
			if ("Not Disbursed".equals(versionScreen.statusElement("Status").getText())) {

				debitAccount = findArrangement(AUTHORISED, customer, CIF, "", PERSONAL_ACCOUNTS, PERSONAL_CHEQUING,
						CAD);
				final String disbursementFields = "CREDIT.ACCT.NO,DEBIT.CURRENCY,DEBIT.ACCT.NO,DEBIT.AMOUNT";
				final String disbursementValues = arrangement + "," + CAD + "," + debitAccount + ",200";
				financialTransaction(CREATE_AUTHORISE, "AA Disbursement", disbursementFields, disbursementValues);
			}
			findArrangement(AUTHORISED, arrangement, ARRANGEMENT, "", productGroup, product, CAD);
			accruals = versionScreen.accrualCategory("id", "FinancialSummary", interestCategory).getText();
			break;

		}
		return accruals;

	}

	@Test(priority = 1, enabled = true)
	@Parameters({ "customer", "productGroup", "product", "interestCategory", "actionToPerform" })
	public void step1(final String customer, final String productGroup, final String product,
			@Optional("") final String interestCategory, @Optional("") final String actionToPerform) {
		stepDescription = "Verify System Debit and Credit Interest Accruals before and after performing actions are different  ";
		stepExpected = "Debit and Credit Interest Accruals before and after performing actions are different";

		String step2Fields;
		String step2Values;
		String arrangement;
		String accrualBefore = null;
		String accrualAfter = null;
		String backDatedAccount = "";
		String fundTransferFields;
		String fundTransferValues;
		String customerType = PERSONAL;

		if (loginResult) {

			switchToBranch("B2B Branch 817");
			if (BUSINESS_ACCOUNTS.equals(productGroup) || COMMERCIAL_LOANS.equals(productGroup)) {
				customerType = BUSINESS;
			}
			actualCIF = findCIF(customer, customerType, "");
			if (!actualCIF.equals(customer)) {
				Reporter.log("CIF received as parameter (" + customer + ") was not found. Using new CIF instead: "
						+ actualCIF, debugMode);

			}

			try {

				if (DefaultVariables.productFields.containsKey(product)) {
					step2Fields = DefaultVariables.productFields.get(product);
					step2Values = DefaultVariables.productValues.get(product);
				} else {
					step2Fields = DefaultVariables.productGroupFields.get(productGroup);
					step2Values = DefaultVariables.productGroupValues.get(productGroup);
				}

				arrangement = arrangements(CREATE, productGroup, product, "", actualCIF,
						"CUSTOMER:1," + "CURRENCY," + "EFFECTIVE.DATE,", actualCIF + "," + CAD + "," + "-3m,",
						step2Fields, step2Values);

				if (!actionToPerform.startsWith("Reverse")) {
					accrualBefore = findAccrual(actualCIF, arrangement, productGroup, product, interestCategory);
				}
				backDatedAccount = arrangements(CREATE, PERSONAL_ACCOUNTS, "HISA Personal", "", actualCIF,
						"CUSTOMER:1," + "CURRENCY," + "EFFECTIVE.DATE,", actualCIF + "," + CAD + "," + "-2m,",
						DefaultVariables.bankingFields, DefaultVariables.bankingValues);
				switch (actionToPerform) {
				case "None":

					break;

				case "Backdated Debit":
					fundTransferFields = "DEBIT.ACCT.NO," + "DEBIT.CURRENCY," + "DEBIT.AMOUNT," + "DEBIT.VALUE.DATE,"
							+ "CREDIT.ACCT.NO";
					fundTransferValues = backDatedAccount + "," + CAD + "," + "5000.00" + "," + "-1m" + ","
							+ arrangement;
					financialTransaction(CREATE, "Account Transfer - Transfer between Accounts", fundTransferFields,
							fundTransferValues);
					authorizeEntity(arrangement, ACTIVITY + "," + productGroup);

					break;
				case "Backdated Credit":

					fundTransferFields = "CREDIT.ACCT.NO," + "CREDIT.CURRENCY," + "CREDIT.AMOUNT," + "CREDIT.VALUE.DATE"
							+ "DEBIT.ACCT.NO";
					fundTransferValues = backDatedAccount + "," + CAD + "," + "5000.00" + "," + "-1m" + ","
							+ arrangement;
					financialTransaction(CREATE, "Account Transfer - Transfer between Accounts", fundTransferFields,
							fundTransferValues);
					authorizeEntity(arrangement, ACTIVITY + "," + productGroup);

					break;
				case "Rate Change":
					switch (interestCategory) {
					default:
					case "Credit Interest":
						arrangementActivity(CREATE_AUTHORISE, "CHANGE ACTIVITY FOR CRINTEREST", arrangement,
								productGroup, "FIXED.RATE:1", "1.5");
						break;

					case "Debit Interest":
						arrangementActivity(CREATE_AUTHORISE, "CHANGE ACTIVITY FOR DRINTEREST", arrangement,
								productGroup, "FIXED.RATE:1", "1.5");
						break;
					case "Principal Interest":
						arrangementActivity(CREATE_AUTHORISE, "CHANGE ACTIVITY FOR PRINCIPALINT", arrangement,
								productGroup, "FIXED.RATE:1", "1.5");
						break;
					case "Deposit Interest":
						arrangementActivity(CREATE_AUTHORISE, "CHANGE ACTIVITY FOR DEPOSITINT", arrangement,
								productGroup, "PERIODIC.RATE:1", "1.5");
						break;
					}

					break;
				case "Set Rate to Zero":
					switch (interestCategory) {
					default:
					case "Credit Interest":
						arrangementActivity(CREATE_AUTHORISE, "CHANGE ACTIVITY FOR CRINTEREST", arrangement,
								productGroup, "FIXED.RATE:1", "0");
						break;

					case "Debit Interest":
						arrangementActivity(CREATE_AUTHORISE, "CHANGE ACTIVITY FOR DRINTEREST", arrangement,
								productGroup, "FIXED.RATE:1", "0");
						break;
					case "Principal Interest":
						arrangementActivity(CREATE_AUTHORISE, "CHANGE ACTIVITY FOR PRINCIPALINT", arrangement,
								productGroup, "FIXED.RATE:1", "0");
						break;
					case "Deposit Interest":
						arrangementActivity(CREATE_AUTHORISE, "CHANGE ACTIVITY FOR DEPOSITINT", arrangement,
								productGroup, "PERIODIC.RATE:1", "0");
						break;
					}

					break;
				case "Reverse Debit Interest":
					fundTransferFields = "DEBIT.ACCT.NO," + "DEBIT.CURRENCY," + "DEBIT.AMOUNT," + "DEBIT.VALUE.DATE,"
							+ "CREDIT.ACCT.NO";
					fundTransferValues = backDatedAccount + "," + CAD + "," + "5000.00" + "," + "-1m" + ","
							+ arrangement;
					financialTransaction(CREATE, "Account Transfer - Transfer between Accounts", fundTransferFields,
							fundTransferValues);
					authorizeEntity(arrangement, ACTIVITY + "," + productGroup);
					accrualBefore = findAccrual(actualCIF, arrangement, productGroup, product, interestCategory);

					arrangementActivity(REVERSE_AUTHORISE, "Transfer", arrangement, productGroup, "", "");

					break;
				case "Reverse Credit Interest":
					fundTransferFields = "CREDIT.ACCT.NO," + "CREDIT.CURRENCY," + "CREDIT.AMOUNT," + "CREDIT.VALUE.DATE"
							+ "DEBIT.ACCT.NO";
					fundTransferValues = backDatedAccount + "," + CAD + "," + "5000.00" + "," + "-1m" + ","
							+ arrangement;
					financialTransaction(CREATE, "Account Transfer - Transfer between Accounts", fundTransferFields,
							fundTransferValues);
					authorizeEntity(arrangement, ACTIVITY + "," + productGroup);
					accrualBefore = findAccrual(actualCIF, arrangement, productGroup, product, interestCategory);
					arrangementActivity(REVERSE_AUTHORISE, "Transfer", arrangement, productGroup, "", "");

					break;
				default:
					break;

				}
				accrualAfter = findAccrual(actualCIF, arrangement, productGroup, product, interestCategory);
				if (!accrualBefore.equals(accrualAfter)) {
					Reporter.log("Debit and Credit Interest Accruals before and after performing actions are different",
							debugMode);
				}
			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "Debit and Credit Interest Accruals before and after performing actions are the Same ";
				softVerify.fail(stepActual);
				Reporter.log(stepActual, debugMode);
			}

		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}
	}
}
