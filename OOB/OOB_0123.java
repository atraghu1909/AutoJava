
package testcases.OOB;

import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class OOB_0123 extends testLibs.BaseTest_OOB {

	private String newArrangement;

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product", "customer", "limitAmount", "amount", "payingAccount", "CCY" })
	public void step1(final String productGroup, final String product, final String customer, final String limitAmount,
			final String amount, @Optional("") final String payingAccount, @Optional(CAD) final String currency) {
		String actualPayingAccount;
		String actualCIF;
		stepDescription = "Create a " + currency + " " + product;
		stepExpected = "Arrangement is created successfully";
		if (loginResult) {
			switchToBranch("B2B Branch 817");

			actualCIF = findCIF(customer, "", "");
			if (!actualCIF.equals(customer)) {
				Reporter.log("CIF received as parameter (" + customer + ") was not found. Using new CIF instead: "
						+ actualCIF, debugMode);

			}

			if ("".equals(payingAccount)) {
				actualPayingAccount = findArrangement(AUTHORISED, actualCIF, CIF, "", PERSONAL_ACCOUNTS,
						PERSONAL_CHEQUING, CAD);
			} else {
				actualPayingAccount = findArrangement(AUTHORISED, payingAccount, ARRANGEMENT, "", PERSONAL_ACCOUNTS,
						PERSONAL_CHEQUING, CAD);
			}

			if (actualPayingAccount.contains("Error")) {
				actualPayingAccount = findArrangement(AUTHORISED, actualCIF, CIF, "", PERSONAL_ACCOUNTS,
						PERSONAL_CHEQUING, CAD);
			}

			if (!actualPayingAccount.equals(payingAccount) && !actualPayingAccount.contains("Error")) {
				updatedValues.add("payingAccount," + actualPayingAccount);
			}

			final String personalLOCFields = "FIXED.RATE:1," + "SINGLE.LIMIT," + "PROPERTY:1:1," + "PAYMENT.METHOD:1,"
					+ "PAYMENT.TYPE:1," + "AMOUNT," + "PAYIN.ACCOUNT:1:1";
			final String personalLOCValues = "1.5," + "N," + "ACCOUNT," + "Due," + "PRINCIPAL," + amount + ","
					+ actualPayingAccount;

			final String commercialLoanFields = "SINGLE.LIMIT," + "PROPERTY:1:1," + "PAYMENT.METHOD:1,"
					+ "PAYMENT.TYPE:1," + "AMOUNT," + "TERM," + "FIXED.RATE:1," + "REVOLVING," + "PAYIN.SETTLEMENT:1";

			final String commercialLoanValues = "N," + "ACCOUNT," + "Due," + "BLENDED," + "100000," + "1Y," + "1.5,"
					+ "PAYMENT," + "No";

			switch (productGroup) {
			default:
			case PERSONAL_LOC:
				newArrangement = arrangements(CREATE, productGroup, product, "", actualCIF, "CUSTOMER:1," + "CURRENCY,",
						actualCIF + "," + currency + ",", personalLOCFields, personalLOCValues);
				break;

			case COMMERCIAL_LOANS:
				newArrangement = arrangements(CREATE, productGroup, product, "", actualCIF, "CUSTOMER:1," + "CURRENCY,",
						actualCIF + "," + currency + ",", commercialLoanFields, commercialLoanValues);
				break;

			case PERSONAL_LOANS:
			case RETAIL_MORTGAGES:
				newArrangement = arrangements(CREATE, productGroup, product, "", actualCIF, "CUSTOMER:1," + "CURRENCY,",
						actualCIF + "," + currency + ",", DefaultVariables.productGroupFields.get(productGroup),
						DefaultVariables.productGroupValues.get(productGroup));
				break;
			}
			if (newArrangement.contains("Error")) {
				stepResult = StatusAs.FAILED;
				stepActual = "Could not create new arrangement";
				softVerify.fail(stepActual);
				Reporter.log(stepActual, debugMode);
			} else {
				stepActual = "Arrangement created successfully: " + newArrangement;
				Reporter.log(stepActual, debugMode);
			}

		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}
}
