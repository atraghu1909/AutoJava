package testcases.OOB;

import java.math.BigDecimal;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class OOBH_33 extends testLibs.BaseTest_OOB {

	private BigDecimal amount;
	private String actualCIF;

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product", "customer", "creditAccount", "CCY" })
	public void step1(final String productGroup, final String product, final String customer,
			final String creditAccount, @Optional(CAD) final String currency) {

		final String fundTransferFields = "DEBIT.ACCT.NO," + "DEBIT.CURRENCY," + "DEBIT.AMOUNT," + "CREDIT.ACCT.NO";

		final String commercialLoanFields = "SINGLE.LIMIT," + "PROPERTY:1:1," + "PAYMENT.METHOD:1," + "PAYMENT.TYPE:1,"
				+ "RATE.TIER.TYPE," + "AMOUNT," + "TERM," + "FIXED.RATE:1," + "PAYIN.SETTLEMENT:1," + "REVOLVING";
		final String commercialLoanValues = "N," + "ACCOUNT," + "Due," + "PRINCIPAL," + "Single," + "100000," + "1Y,"
				+ "1.5," + "No," + "PAYMENT";

		String fundTransferValues;
		String createdArrangement;
		String customerType = PERSONAL;

		stepDescription = "Create " + currency + " " + product + " and Disburse it via Funds Transfer";
		stepExpected = currency + " " + product + " and Disbursement successfully created via Funds Transfer";

		if (loginResult) {
			switchToBranch("B2B Branch 817");
			try {

				if (BUSINESS_ACCOUNTS.equals(productGroup) || COMMERCIAL_LOANS.equals(productGroup)) {
					customerType = BUSINESS;
				}
				actualCIF = findCIF(customer, customerType, "");
				if (!actualCIF.equals(customer)) {
					Reporter.log("CIF received as parameter (" + customer + ") was not found. Using new CIF instead: "
							+ actualCIF, debugMode);
				}

				amount = new BigDecimal("200.00");
				if (productGroup.contains("Loans")) {
					if (productGroup.contains("Commercial")) {

						if (currency.contains("USD")) {
							createdArrangement = arrangements(CREATE, productGroup, product, "", actualCIF,
									"CUSTOMER:1," + "CURRENCY,", actualCIF + "," + currency + ",",
									commercialLoanFields + ",FLOATING.INDEX:1",
									commercialLoanValues + ",14 US Base Rate");
						} else {
							createdArrangement = arrangements(CREATE, productGroup, product, "", actualCIF,
									"CUSTOMER:1," + "CURRENCY,", actualCIF + "," + currency + ",", commercialLoanFields,
									commercialLoanValues);
						}

					} else {
						createdArrangement = arrangements(CREATE, productGroup, product, "", actualCIF,
								"CUSTOMER:1," + "CURRENCY,", actualCIF + "," + currency + ",",
								DefaultVariables.loanFields, DefaultVariables.loanValues);
					}
				} else {
					createdArrangement = arrangements(CREATE, productGroup, product, "", actualCIF,
							"CUSTOMER:1," + "CURRENCY,", actualCIF + "," + currency + ",",
							DefaultVariables.mortgageFields, DefaultVariables.mortgageValues);
				}
				fundTransferValues = createdArrangement + "," + currency + "," + amount + "," + creditAccount;
				financialTransaction(CREATE, "Account Transfer - Transfer between Accounts", fundTransferFields,
						fundTransferValues);

			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "User could not create " + currency + " " + product
						+ " and Disburse it via Funds Transfer.";
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
