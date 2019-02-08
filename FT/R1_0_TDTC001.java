package testcases.FT;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class R1_0_TDTC001 extends testLibs.BaseTest_OOB {

	private String actualCIF;
	String[] listOfValues;
	String limitId;
	String[] limitIdParts;
	boolean updateLimitResult;
	boolean reverseLimitResult;
	String completeId;
	boolean overrideMsgResult;
	String localResult;
	String updateLimitFields;
	String updateLimitValues;
	String customerType = PERSONAL;

	@Test(priority = 1, enabled = true)
	@Parameters({ "customer", "productGroup", "product", "limitUtilisation", "fields", "values", "actionToPerform",
			"expectation" })
	public void step1(final String customer, final String productGroup, final String product,
			@Optional("") final String limitUtilisation, final String fields, final String values,
			final String actionToPerform, final String expectation) {

		final String step1Fields = "CUSTOMER:1,CURRENCY,";
		final String step1Values = customer + ",CAD,";
		String step2Fields = DefaultVariables.loanFields;
		String step2Values = DefaultVariables.loanValues;

		stepDescription = actionToPerform + " action should be performed with given expectation as " + expectation;
		stepExpected = actionToPerform + " action has been performed with given expectation";
		if (BUSINESS_ACCOUNTS.equals(productGroup) || COMMERCIAL_LOANS.equals(productGroup)) {
			customerType = BUSINESS;
		}
		if (loginResult) {
			try {
				switch (actionToPerform) {
				case "createLimit":
					actualCIF = findCIF(customer, customerType, "");

					completeId = customerLimit("Create and Authorise", UNSECURED, "", product, actualCIF, "", "",
							fields, values);
					if (completeId.contains(customer)) {
						localResult = "pass";
					}
					break;

				case "updateLimit":
					actualCIF = findCIF(customer, customerType, "");

					limitId = customerLimit("Create and Authorise", UNSECURED, "", product, actualCIF, "", "", fields,
							values);

					listOfValues = values.split(",");
					int internalAmount = Integer.parseInt(listOfValues[1]);
					int halfInternalAmount = internalAmount / 2;
					String requiredAmount = Integer.toString(halfInternalAmount);

					if (limitUtilisation.contains("100000")) {

						int utilisationValue = Integer.parseInt(limitUtilisation);
						int halfUtilisationValue = utilisationValue / 2;
						String requiredUtilisationValue = Integer.toString(halfUtilisationValue);

						step2Fields = step2Fields + "AMOUNT,";
						step2Values = step2Values + limitUtilisation + ",";

						arrangements("Create and Authorise", productGroup, product, "", customer, step1Fields,
								step1Values, step2Fields, step2Values);

						updateLimitFields = "INTERNAL.AMOUNT," + "MAXIMUM.TOTAL,";
						updateLimitValues = requiredUtilisationValue + "," + requiredUtilisationValue + ",";

						updateLimitResult = !customerLimit(AMEND, "", "", "", limitId.split(".")[0],
								limitId.split(".")[1].substring(3), limitId.split(".")[2], updateLimitFields,
								updateLimitValues, false).contains(ERROR);
					} else {
						updateLimitFields = "INTERNAL.AMOUNT," + "MAXIMUM.TOTAL,";
						updateLimitValues = requiredAmount + "," + requiredAmount + ",";

						updateLimitResult = !customerLimit(AMEND, "", "", "", limitId.split(".")[0],
								limitId.split(".")[1].substring(3), limitId.split(".")[2], updateLimitFields,
								updateLimitValues, false).contains(ERROR);
					}

					if (updateLimitResult) {
						localResult = "pass";
					} else {
						overrideMsgResult = inputTable.verifyAcceptOverride();
						if (overrideMsgResult) {
							localResult = "override";
						}
					}
					break;

				case "reverseLimit":
					actualCIF = findCIF(customer, customerType, "");

					limitId = customerLimit("Create and Authorise", UNSECURED, "", product, actualCIF, "", "", fields,
							values);

					if (limitUtilisation.contains("100000")) {

						step2Fields = step2Fields + "AMOUNT,";
						step2Values = step2Values + limitUtilisation + ",";

						arrangements("Create and Authorise", productGroup, product, "", customer, step1Fields,
								step1Values, step2Fields, step2Values);
					}
					reverseLimitResult = reverseEntity(limitId, LIMIT);
					if (reverseLimitResult) {
						localResult = "pass";
					} else {
						localResult = "fail";
					}
					break;
				default:
					break;
				}
				if (localResult.equals(expectation)) {
					stepResult = StatusAs.PASSED;
					stepActual = actionToPerform + " has been performed with given expectation";
				} else {
					stepResult = StatusAs.FAILED;
					stepActual = actionToPerform + " has not been performed with given expectation";
					softVerify.fail(stepActual);
				}

			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = actionToPerform + " has not been performed with given expectation";
				softVerify.fail(stepActual);
				Reporter.log(e.getMessage(), false);
			}
		} else {

			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);

		}
		softVerify.assertAll();
	}
}
