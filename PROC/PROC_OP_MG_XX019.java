package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.CustomerData;

public class PROC_OP_MG_XX019 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.9";

	private String mainArrangement;
	private String customer;
	private CustomerData customerData;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "customerType", "product" })
	public void preCondition(@Optional("B2B Branch 623") final String branch,
			@Optional(PERSONAL) final String customerType, final String product) {

		stepDescription = "Create customer and " + product + " arrangement";
		stepExpected = "Customer and " + product + " arrangement created successfully";

		ArrangementData arrangementData;

		if (loginResult) {
			switchToBranch(branch);

			arrangementData = new ArrangementData("mainArrangement", RETAIL_MORTGAGES, product).withEffectiveDate("-1m")
					.withTerm("1Y").build();

			mainArrangement = createDefaultArrangement(arrangementData);
			customer = arrangementData.getCustomers();
			customerData = createdCustomers.get(customer);

			if (customer == null || mainArrangement == null || customer.contains(ERROR)
					|| mainArrangement.contains(ERROR)) {
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
	public void updatePropertyInsuranceDetails() {

		stepDescription = "Update property insurance details";
		stepExpected = "Property insurance details updated successfully";

		final String fields = "Insurance details#L.COLL.INS.COMP:1," + "Insurance details#L.COLL.INS.COV:1,"
				+ "Insurance details#L.COLL.INS.TYPE:1," + "Insurance details#L.COLL.INS.EXP:1,"
				+ "Insurance details#L.COLL.INS.PLCY:1," + "Insurance details#L.COLL.PROP.INS,";
		final String values = "State Farm," + customerData.getAddressStreet() + "," + "Fire," + "+10Y," + "526478984,"
				+ "Binder,";
		String limitID;

		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			limitID = collateral(AMEND, COLLATERAL_DETAILS, customer + ".1.1", fields, values);
			if (limitID.contains(ERROR)) {
				stepActual = "Error while updating property insurance details";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
