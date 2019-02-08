package testcases.FT;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class R2_TDTC016 extends testLibs.BaseTest_OOB {

	@Test(priority = 1, enabled = true)
	@Parameters({ "customers", "actionToPerform", "collateralCode", "disbursePercentage", "expectation" })
	public void step1(final String customers, final String actionToPerform,
			@Optional("999") final String collateralCode, @Optional("100") final String disbursePercentage,
			final boolean expectation) {

		String customer1 = "";
		String customer2 = "";
		String actualCustomer1 = "";
		String actualCustomer2 = "";
		String unlimitedChequingAccount;
		String lendingArrangement1 = "";
		String lendingArrangement2 = "";
		String reserveAccount;
		boolean localResult = false;
		String step1Fields;
		String step1Values;
		String step2Fields;
		String step2Values;
		String status = null;
		String customerType = PERSONAL;

		if (loginResult) {
			try {
				switchToBranch("B2B Branch 817");
				if (customers.contains(",")) {

					String[] customer = customers.split(",");
					customer1 = customer[0].toString();
					customer2 = customer[1].toString();

					actualCustomer2 = findCIF(customer2, customerType, "");
					actualCustomer1 = findCIF(customer1, customerType, "");

					lendingArrangement2 = findArrangement(AUTHORISED, actualCustomer2, CIF, "", PERSONAL_LOANS,
							"Personal Loan Secured Fixed Rate", CAD);

					reserveAccount = findArrangement(AUTHORISED, actualCustomer2, CIF, "", SERVICING_ACCOUNTS,
							RESERVE_ACCOUNTS, CAD);

				} else {
					customer1 = customers;
					actualCustomer1 = findCIF(customer1, customerType, "");
					reserveAccount = findArrangement(AUTHORISED, actualCustomer1, CIF, "", SERVICING_ACCOUNTS,
							RESERVE_ACCOUNTS, CAD);
				}

				step1Fields = "CUSTOMER:1,CURRENCY,";
				step1Values = actualCustomer1 + ",CAD,";

				unlimitedChequingAccount = findArrangement(AUTHORISED, actualCustomer1, CIF, "", PERSONAL_ACCOUNTS,
						PERSONAL_CHEQUING, CAD);
				if (!"Disburse Loan to Reserve Account".equalsIgnoreCase(actionToPerform)) {

					lendingArrangement1 = findArrangement(AUTHORISED, actualCustomer1, CIF, "", PERSONAL_LOANS,
							"Personal Loan Secured Fixed Rate", CAD);
				}

				stepDescription = actionToPerform;
				stepExpected = "Activity " + actionToPerform + " is Expected to : " + expectation;

				switch (actionToPerform) {
				case "Link Loan to Reserve Account":

					step2Fields = "L.LINKED.REF.ID";
					step2Values = reserveAccount;
					findArrangement(AUTHORISED, lendingArrangement1, ARRANGEMENT, "", PERSONAL_LOANS,
							"Personal Loan Secured Fixed Rate", CAD);
					localResult = arrangementActivity(CREATE_AUTHORISE, "UPDATE ACTIVITY FOR ACCOUNT",
							lendingArrangement1, PERSONAL_LOANS, step2Fields, step2Values);
					break;

				case "Link Reserve Account to Collateral":

					String collateralLink;
					String completeLimitId = actualCustomer1 + ".0002300.01";
					String colateralLinkFields = "COLLATERAL.CODE,COMPANY:1,LIMIT.REF.CUST:1,LIMIT.REFERENCE:1,";
					String colateralLinkValues = collateralCode + ",B2B," + actualCustomer1 + "," + completeLimitId
							+ ",";
					collateralLink = collateral("Create and Authorise", "Collateral Link", actualCustomer1,
							colateralLinkFields, colateralLinkValues);

					String collateralFields = DefaultVariables.collateralFields + "Collateral details#CA.ADR.LINE1,"
							+ "Collateral details#TOWN.CITY," + "Collateral details#US.STATE,"
							+ "Collateral details#CA.POST.CODE," + "Collateral#COLLATERAL.CODE," + "COLLATERAL.TYPE,";
					String collateralValues = DefaultVariables.collateralValues
							+ createdCustomers.get(customer1).getAddressStreet() + ","
							+ createdCustomers.get(customer1).getAddressCity() + ","
							+ createdCustomers.get(customer1).getAddressProvince() + ","
							+ createdCustomers.get(customer1).getaddressPostalCode() + "," + collateralCode + ","
							+ collateralCode + ",";

					collateral("Create and Authorise", "Collateral Details", collateralLink, collateralFields,
							collateralValues);

					step2Fields = "COLLATERAL.LINKED,";
					findArrangement(AUTHORISED, reserveAccount, ARRANGEMENT, "", SERVICING_ACCOUNTS, RESERVE_ACCOUNTS,
							CAD);
					localResult = arrangementActivity(CREATE_AUTHORISE, "Update account", reserveAccount,
							SERVICING_ACCOUNTS, step2Fields, actualCustomer1 + ",");
					break;

				case "Change Customer of Loan linked to Reserve Account":

					step2Fields = "L.LINKED.REF.ID";
					step2Values = reserveAccount;
					findArrangement(AUTHORISED, lendingArrangement2, ARRANGEMENT, "", PERSONAL_LOANS,
							"Personal Loan Secured Fixed Rate", CAD);
					localResult = arrangementActivity(CREATE_AUTHORISE, "UPDATE ACTIVITY FOR ACCOUNT",
							lendingArrangement2, PERSONAL_LOANS, step2Fields, step2Values);
					localResult = arrangementActivity(CREATE_AUTHORISE, "CHANGE ACTIVITY FOR CUSTOMER",
							lendingArrangement2, PERSONAL_LOANS, "CUSTOMER:1", actualCustomer1);
					break;

				case "Change Customer of Reserve Account linked to Loan":

					step2Fields = "L.LINKED.REF.ID";
					step2Values = reserveAccount;
					findArrangement(AUTHORISED, lendingArrangement2, ARRANGEMENT, "", PERSONAL_LOANS,
							"Personal Loan Secured Fixed Rate", CAD);
					localResult = arrangementActivity(CREATE_AUTHORISE, "UPDATE ACTIVITY FOR ACCOUNT",
							lendingArrangement2, PERSONAL_LOANS, step2Fields, step2Values);
					findArrangement(AUTHORISED, reserveAccount, ARRANGEMENT, "", SERVICING_ACCOUNTS, RESERVE_ACCOUNTS,
							CAD);
					localResult = arrangementActivity(CREATE_AUTHORISE, "CHANGE ACTIVITY FOR CUSTOMER", reserveAccount,
							PERSONAL_LOANS, "CUSTOMER:1", actualCustomer1);
					break;

				case "Change Loan linked to Reserve Account":

					step2Fields = DefaultVariables.loanFields + "L.LINKED.REF.ID,";
					step2Values = DefaultVariables.loanValues + reserveAccount + ",";
					findArrangement(AUTHORISED, lendingArrangement1, ARRANGEMENT, "", PERSONAL_LOANS,
							"Personal Loan Secured Fixed Rate", CAD);
					localResult = arrangementActivity(CREATE_AUTHORISE, "UPDATE ACTIVITY FOR ACCOUNT",
							lendingArrangement1, PERSONAL_LOANS, step2Fields, step2Values);
					lendingArrangement2 = arrangements("Create and Authorise", PERSONAL_LOANS,
							"Personal Loan Unsecured Fixed Rate", ROLEBASED_LENDING, actualCustomer1, step1Fields,
							step1Values, step2Fields, step2Values);

					findArrangement(AUTHORISED, reserveAccount, ARRANGEMENT, "", SERVICING_ACCOUNTS, RESERVE_ACCOUNTS,
							CAD);
					localResult = arrangementActivity(CREATE_AUTHORISE, "Update account", reserveAccount,
							SERVICING_ACCOUNTS, "LOAN.LINKED", lendingArrangement2);
					break;

				case "Disburse Loan to Reserve Account":
				case "Disburse Loan to Reserve Account and Reverse Disbursement":

					step2Fields = "SINGLE.LIMIT," + "AMOUNT," + "TERM," + "Basic#PAYIN.SETTLEMENT:1,"
							+ "PAYMENT.TYPE:2," + "PAYMENT.METHOD:2," + "PROPERTY:2:1," + "START.DATE:2:1,"
							+ "BILL.TYPE:2," + "PAYOUT.SETTLEMENT:1," + "PAYOUT.ACCOUNT:1:1," + "PAYOUT.PERCENTAGE:1:1";
					step2Values = "N," + "100000," + "1Y," + "No," + "CHARGE," + "Due," + "ACCOUNT," + "D_20180825,"
							+ "PAYMENT," + "Yes," + reserveAccount + "," + disbursePercentage;

					lendingArrangement1 = arrangements("Create and Authorise", PERSONAL_LOANS,
							"Personal Loan Unsecured Fixed Rate", ROLEBASED_LENDING, actualCustomer1, step1Fields,
							step1Values, step2Fields, step2Values);

					findArrangement(AUTHORISED, lendingArrangement1, ARRANGEMENT, "", PERSONAL_LOANS,
							"Personal Loan Unsecured Fixed Rate", CAD);
					try {
						status = versionScreen.statusElement("Status").getText();
						if (("current").equalsIgnoreCase(status)) {
							Reporter.log("status is:" + status);
							localResult = true;
						}
					} catch (NoSuchElementException e) {
						Reporter.log("Status is : " + status + " status should be 'current'");
						localResult = false;
					}
					if ("Reverse Disbursement".contains(actionToPerform)) {
						arrangementActivity(REVERSE_AUTHORISE, "Reverse the Disbursement Activity ",
								lendingArrangement1, PERSONAL_LOANS, "", "");
						findArrangement(AUTHORISED, lendingArrangement1, ARRANGEMENT, "", PERSONAL_LOANS,
								"Personal Loan Unsecured Fixed Rate", CAD);
						try {
							status = versionScreen.statusElement("Status").getText();
							if (("Not Disbursed").equalsIgnoreCase(status)) {
								Reporter.log("status is:" + status);
								localResult = true;
							}
						} catch (NoSuchElementException e) {
							Reporter.log("Status is : " + status + "status should be 'Not Disburse'");
							localResult = false;
						}
					}
					break;

				default:
					break;
				}

				if (localResult ^ expectation) {
					stepResult = StatusAs.FAILED;
					softVerify.fail();
				} else {
					stepResult = StatusAs.PASSED;
					stepActual = "Activity: " + actionToPerform + " Performed as Expected for Expectation : "
							+ expectation;
				}

			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "Loan and Reserve Accounts are not created Successfully ";
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
