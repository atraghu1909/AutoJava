package testcases.OOB;

import java.math.BigDecimal;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class OOBH_41 extends testLibs.BaseTest_OOB {

	private BigDecimal amount;

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product", "customer", "creditAccount" })
	public void step1(final String productGroup, final String product, final String customer,
			final String creditAccount) {

		final String fundTransferFields = "DEBIT.ACCT.NO," + "DEBIT.CURRENCY," + "CREDIT.AMOUNT," + "CREDIT.ACCT.NO";
		String fundTransferValues;
		String createdMortgage;

		stepDescription = "Disburse Loan and Check Collateral Status";
		stepExpected = "Loan Disbursed successfully and Collateral found";

		if (loginResult) {
			switchToBranch("B2B Branch 817");

			amount = new BigDecimal("200.00");
			if (productGroup.contains("Loans")) {
				createdMortgage = arrangements(CREATE, productGroup, product, "", customer, "CUSTOMER:1," + "CURRENCY,",
						customer + "," + CAD + ",", DefaultVariables.loanFields, DefaultVariables.loanValues);
			} else {
				createdMortgage = arrangements(CREATE, productGroup, product, "", customer, "CUSTOMER:1," + "CURRENCY,",
						customer + "," + CAD + ",", DefaultVariables.mortgageFields, DefaultVariables.mortgageValues);
			}

			fundTransferValues = createdMortgage + "," + CAD + "," + amount + "," + creditAccount;
			financialTransaction(CREATE, "Account Transfer - Transfer between Accounts", fundTransferFields,
					fundTransferValues);

			findArrangement(AUTHORISED, createdMortgage, ARRANGEMENT, "", productGroup, product, CAD);

			try {
				driver.findElement(By.xpath("//table[contains(@id,'datadisplay_Limit')]"));
				stepActual = "Limit Collateral is present";
			}

			catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "Limit Collateral is not present";
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
