package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.DefaultVariables;

public class PROC_OP_MG_XX015 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 4.0";
	private String customer;
	private String mainArrangement;
	private String heloc;
	private ArrangementData arrangementData;
	private ArrangementData hellocData;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "customerType", "product" })
	public void preCondition(@Optional("B2B Branch 623") final String branch,
			@Optional(PERSONAL) final String customerType, final String product) {

		stepDescription = "Create customer, HOK with " + product + " arrangement and HELOC";
		stepExpected = "Customer, HOK with " + product + " arrangement and HELOC created successfully";

		if (loginResult) {
			switchToBranch(branch);

			hellocData = new ArrangementData("hellocArrangement", RETAIL_MORTGAGES, HELOC).withEffectiveDate("-1m")
					.withCommitmentAmount("400000").withCollateralValue("500000").withDisbursement("100000")
					.withRepayments().build();

			arrangementData = new ArrangementData("mainArrangement", RETAIL_MORTGAGES, product).withEffectiveDate("-1m")
					.withTerm("1Y").withCommitmentAmount("600000").withCollateralValue("1000000").withHOK()
					.withHOKProduct(hellocData).withDisbursement().withRepayments().build();
			mainArrangement = createDefaultArrangement(arrangementData);
			customer = arrangementData.getCustomers();
			heloc = hellocData.getArrangementID();

			if (mainArrangement == null || heloc == null || mainArrangement.contains(ERROR) || heloc.contains(ERROR)) {
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
	@Parameters("product")
	public void openNewHOKMortgageByReducingHELOCLimit(final String product) {

		stepDescription = "Open new HOK mortgage by reducing HELOC limit";
		stepExpected = "HELOC limit is reduced successfully to open new HOK mortgage";

		String fields = "INTERNAL.AMOUNT," + "MAXIMUM.TOTAL," + "MAXIMUM.SECURED:1,";
		String values = "200000," + "200000," + "200000,";
		String limitID;
		boolean result;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			limitID = customerLimit(AMEND, hellocData.getLimitType(), ROLEBASED_LENDING, HELOC, customer, "2103", "01",
					fields, values, false);
			if (limitID.contains(ERROR)) {
				stepActual = "Error while amending HOK limit amount";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			fields = "CHANGE.AMOUNT,";
			values = "-200000,";
			result = arrangementActivity(CREATE_AUTHORISE, "DECREASE ACTIVITY FOR COMMITMENT",
					hellocData.getArrangementID(), RETAIL_MORTGAGES, fields, values);
			if (!result) {
				stepActual = "Error while performing DECREASE ACTIVITY FOR COMMITMENT";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			fields = "INTERNAL.AMOUNT," + "MAXIMUM.TOTAL," + "MAXIMUM.SECURED:1,";
			values = "200000," + "200000," + "200000,";
			limitID = customerLimit(AMEND, hellocData.getLimitType(), ROLEBASED_LENDING, product, customer, "2101",
					"01", fields, values, false);
			if (limitID.contains(ERROR)) {
				stepActual = "Error while creating HOK limit";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters("product")
	public void increaseOverallHOKLimit(final String product) {

		stepDescription = "Increase overall HOK limit";
		stepExpected = "HOK limit increased successfully";

		String fields = "INTERNAL.AMOUNT," + "MAXIMUM.TOTAL," + "MAXIMUM.SECURED:1,";
		String values = "400000," + "400000," + "400000,";
		String limitID;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			limitID = customerLimit(AMEND, hellocData.getLimitType(), ROLEBASED_LENDING, product, customer, "2100",
					"01", fields, values, false);
			if (limitID.contains(ERROR)) {
				stepActual = "Error while increasing overall HOK limit";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters("product")
	public void increaseExistingHOKHELOCLimit(final String product) {

		stepDescription = "Increase existing HOK HELOC limit";
		stepExpected = "Existing HOK HELOC limit increased successfully";

		String fields = "INTERNAL.AMOUNT," + "MAXIMUM.TOTAL," + "MAXIMUM.SECURED:1,";
		String values = "400000," + "400000," + "400000,";
		String limitID;
		boolean result;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			limitID = customerLimit(AMEND, hellocData.getLimitType(), ROLEBASED_LENDING, HELOC, customer, "2103", "01",
					fields, values, false);
			if (limitID.contains(ERROR)) {
				stepActual = "Error while amending HOK HELOC limit";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			fields = "CHANGE.AMOUNT,";
			values = "200000,";
			result = arrangementActivity(CREATE_AUTHORISE, "INCREASE ACTIVITY FOR COMMITMENT",
					hellocData.getArrangementID(), RETAIL_MORTGAGES, fields, values);
			if (!result) {
				stepActual = "Error while increasing existing HOK HELOC limit";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	@Parameters("product")
	public void limitEnquiry(final String product) {

		stepDescription = "Perform limit enquiry for the client";
		stepExpected = "Limit enquiry for the client performed successfully";

		boolean result;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			commandLine("COS LIAB.POSITION", commandLineAvailable);
			result = enquiryElements.enquirySearch("Liability Number", "", customer);

			if (!result) {
				stepActual = "Error while performing limit enquiry for the client";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
