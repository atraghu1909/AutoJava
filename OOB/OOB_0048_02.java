package testcases.OOB;

import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class OOB_0048_02 extends testLibs.BaseTest_OOB {

	private String newArrangement;

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product", "customer" })
	public void step1(final String productGroup, final String product, final String customer) {

		stepDescription = "Verify we can change status of " + product + " to Current after Fund Transfer Activity";
		stepExpected = "Able to successfully change status of " + product + " to Current after Fund Transfer Activity";
		String actualCIF;

		if (loginResult) {

			switchToBranch("B2B Branch 817");

			actualCIF = findCIF(customer, "", "");
			if (!actualCIF.equals(customer)) {
				Reporter.log("CIF received as parameter (" + customer + ") was not found. Using new CIF instead: "
						+ actualCIF, debugMode);

			}

			newArrangement = arrangements(CREATE, productGroup, product, "", actualCIF, "CUSTOMER:1," + "CURRENCY,",
					actualCIF + "," + CAD + ",", DefaultVariables.gicFields, DefaultVariables.gicValues);

			Reporter.log("Arrangement created: " + newArrangement, debugMode);

			final String fundTransferFields = "DEBIT.ACCT.NO," + "DEBIT.CURRENCY," + "DEBIT.AMOUNT," + "CREDIT.ACCT.NO";
			final String fundTransferValues = "AA162157RF5P," + CAD + "," + "10000," + newArrangement;

			financialTransaction(CREATE, "Account Transfer - Transfer between Accounts", fundTransferFields,
					fundTransferValues);
			authorizeEntity(newArrangement, ACTIVITY + "," + productGroup);
			findArrangement(AUTHORISED, newArrangement, ARRANGEMENT, "", productGroup, product, CAD);
			final String status = versionScreen.statusElement("Status").getText();

			if (!"Current".equals(status)) {
				stepResult = StatusAs.FAILED;
				stepActual = "Arrangement Status of " + product
						+ " is not set to Current after Funds Transfer Activity";
				softVerify.fail(stepActual);
			}

		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}
	}
}
