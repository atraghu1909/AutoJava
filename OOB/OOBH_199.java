
package testcases.OOB;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class OOBH_199 extends testLibs.BaseTest_OOB {

	private String newArrangement;

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product", "customer", "fields", "values", "payingAccount", "CCY" })
	public void step1(final String productGroup, final String product, final String customer, final String fields,
			final String values, final @Optional("") String payingAccount, @Optional(CAD) final String currency) {
		final String limitFields = "LIMIT.CURRENCY," + "INTERNAL.AMOUNT";
		final String limitValues = CAD + "," + "20000";
		String limitType;
		String actualPayinAccount;
		String actualCIF;

		stepDescription = "Create an arrangement and validate Payment Schedule table has data";
		stepExpected = "Arrangement was created successfully and Payment Schedule table had data";

		if (loginResult) {
			switchToBranch("B2B Branch 817");
			actualCIF = findCIF(customer, "", "");
			if (!actualCIF.equals(customer)) {
				Reporter.log("CIF received as parameter (" + customer + ") was not found. Using new CIF instead: "
						+ actualCIF, debugMode);

			}

			if ("".equals(payingAccount)) {
				actualPayinAccount = findArrangement(AUTHORISED, actualCIF, CIF, "", PERSONAL_ACCOUNTS,
						B2B_BANK_CHEQUING_ACCOUNT, CAD);
			} else {
				actualPayinAccount = findArrangement(AUTHORISED, payingAccount, ARRANGEMENT, "", PERSONAL_ACCOUNTS,
						B2B_BANK_CHEQUING_ACCOUNT, CAD);
			}

			final String loanFields = DefaultVariables.loanFields + "," + fields + ",PAYIN.ACCOUNT:1:1";
			final String loanValues = DefaultVariables.loanValues + "," + values + "," + actualPayinAccount;

			limitType = product.contains("Secured") ? "Secured" : "Unsecured";
			customerLimit("Create and Authorise", limitType, "", product, actualCIF, "", "", limitFields, limitValues);

			newArrangement = arrangements(CREATE, productGroup, product, "", actualCIF, "CUSTOMER:1," + "CURRENCY,",
					actualCIF + "," + currency + ",", loanFields, loanValues);

			if (newArrangement.contains("Error")) {
				stepResult = StatusAs.FAILED;
				stepActual = "Could not create new arrangement";
				softVerify.fail(stepActual);
			} else {
				findArrangement(AUTHORISED, newArrangement, ARRANGEMENT, "", productGroup, product, currency);
				versionScreen.linkText(ADDITIONAL_DETAILS, "Schedule").click();
				final List<WebElement> rowCount = versionScreen.dataRow("summary", "Payment Schedule");

				if (rowCount.size() > 2) {
					stepActual = "Arrangement" + newArrangement + " created successfully and Payment Schedule has data";
				} else {
					stepResult = StatusAs.FAILED;
					stepActual = "Arrangement" + newArrangement
							+ "created successfully and Payment Schedule does not have data";
					softVerify.fail(stepActual);
				}
			}

		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}

		softVerify.assertAll();
	}
}
