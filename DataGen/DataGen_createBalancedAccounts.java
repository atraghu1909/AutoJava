package testcases.DataGen;

import java.util.Random;

import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class DataGen_createBalancedAccounts extends testLibs.BaseTest_DataGen {

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product", "quantity" })
	public void step1(final String productGroup, final String product, final String quantity) {
		final int iterations = Integer.valueOf(quantity);
		int i, j;
		String customer = null;
		String[] arrangement = new String[iterations];
		final String fundTransferFields = "DEBIT.ACCT.NO," + "DEBIT.CURRENCY," + "DEBIT.AMOUNT," + "DEBIT.VALUE.DATE,"
				+ "CREDIT.ACCT.NO";
		String fundTransferValues = null;

		stepDescription = "Create Balanced Accounts for  " + quantity + " " + product + " Arrangements";
		stepExpected = "Balanced Accounts created successfully";

		final Random rand = new Random();
		final int min = 5000;
		final int max = 100000;
		final int amount = rand.nextInt(max - min + 1) + min;

		if (loginResult && iterations > 1) {

			for (j = 0, i = 1; i <= iterations; i++, j++) {
				customer = customer("Create", PERSONAL, ROLEBASED_BANKING, DefaultVariables.personalCIFFields,
						DefaultVariables.personalCIFValues);
				arrangement[j] = createDefaultArrangement(productGroup, product, ROLEBASED_BANKING, customer, "-2m");

				Reporter.log("Customer: " + customer + ", Arrangement: " + arrangement[j], debugMode);
				stepActual += "Customer: " + customer + ", Arrangement: " + arrangement[j];
				stepActual += System.lineSeparator();
			}

			for (j = 0; j < arrangement.length - 1; j++) {

				fundTransferValues = arrangement[j] + "," + CAD + "," + amount + "," + "-1m" + "," + arrangement[j + 1];
				financialTransaction(CREATE, "Account Transfer - Transfer between Accounts", fundTransferFields,
						fundTransferValues);
				authorizeEntity(arrangement[j], ACTIVITY + "," + productGroup);

				stepActual += "Fund Transfer is Successfully completed for Customer: " + customer + ", Debit Account: "
						+ arrangement[j] + ", Credit Account: " + arrangement[j + 1];
				stepActual += System.lineSeparator();

			}

			fundTransferValues = arrangement[arrangement.length - 1] + "," + CAD + "," + amount + "," + "-1m" + ","
					+ arrangement[0];
			financialTransaction(CREATE, "Account Transfer - Transfer between Accounts", fundTransferFields,
					fundTransferValues);
			authorizeEntity(arrangement[arrangement.length - 1], ACTIVITY + "," + productGroup);

			softVerify.assertAll();

		} else {

			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed ";
			throw new SkipException(stepActual);
		}
	}
}
