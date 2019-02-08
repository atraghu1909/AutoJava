package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;

public class PROC_OP_MG_XX016 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 4.0";

	private String mainArrangement;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "customerType", "product" })
	public void preCondition(@Optional("B2B Branch 623") final String branch,
			@Optional(PERSONAL) final String customerType, final String product) {

		stepDescription = "Create customer and two current " + product + " arrangements";
		stepExpected = "Customer and two current " + product + " arrangements created successfully";

		ArrangementData arrangementData;

		if (loginResult) {
			switchToBranch(branch);

			arrangementData = new ArrangementData("mainArrangement", RETAIL_MORTGAGES, product).withEffectiveDate("-1m")
					.withTerm("1Y").withCommitmentAmount("100T").withCollateralValue("100T").withDisbursement("90T")
					.withRepayments().build();
			mainArrangement = createDefaultArrangement(arrangementData);

			if (mainArrangement == null || mainArrangement.contains(ERROR)) {
				stepActual = "Error while creating " + product + " arrangement";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Login failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
	}

	@Test(priority = 2, enabled = true)
	public void releaseOfHomeImprovementHoldback() {

		stepDescription = "Release home improvement holdback for arrangement: " + mainArrangement;
		stepExpected = "Home improvement holdback" + mainArrangement + " released successfully";

		final String fields = "PRIMARY.ACCOUNT," + "TRANSACTION:1," + "SURROGATE.AC:1," + "CURRENCY:1," + "AMOUNT:1,"
				+ "CR.VALUE.DATE:1,";
		final String values = "CAD1100300017623," + "Disburse-ft," + mainArrangement + "," + "CAD," + "10T," + "+0d,";
		String transactionID;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			transactionID = financialTransaction(CREATE, "Teller Financial Services", fields, values);
			if (transactionID.contains(ERROR)) {
				stepActual = "Error while releasing home improvement holdback: " + transactionID;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
