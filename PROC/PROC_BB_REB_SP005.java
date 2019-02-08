package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class PROC_BB_REB_SP005 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 1.0";
	private String customer;
	private String branch = "B2B Branch 817";

	@Test(priority = 1, enabled = true)
	@Parameters({ "customerType" })
	public void preCondition(final String customerType) {

		stepDescription = "Create " + customerType + " customer";
		stepExpected = customerType + " customer created successfully";

		switchToBranch(branch);

		customer = createDefaultCustomer(customerType, "", ROLEBASED_BANKING);

		if (customer.contains(ERROR)) {
			stepActual = "Error while creating customer: " + customer;
			softVerify.fail(stepActual);
			stepResult = StatusAs.FAILED;
		}
		softVerify.assertAll();
	}

	@Test(priority = 2, enabled = true)
	public void activateHoldOutput() {

		stepDescription = "Activate Hold Output and Returned Mail on Client’s address for customer: " + customer;
		stepExpected = "Hold Output and Returned Mail on Client’s address were activated successfully for customer: "
				+ customer;

		if (customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			boolean result;

			result = holdOutput(customer);
			if (!result) {
				stepActual = "Error while activating Hold Output and Returned Mail on Client’s address for customer: "
						+ customer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
