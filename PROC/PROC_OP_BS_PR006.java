package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.CustomerData;

public class PROC_OP_BS_PR006 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.6";
	private String hisaPersonalArrangement;
	private String hisaBusinessArrangement;
	private String customerPersonal1;
	private String customerPersonal2;
	private String customerBusiness;
	private CustomerData customerPersonalData;
	private CustomerData customerBusinessData;
	private ArrangementData hisaPersonal;
	private ArrangementData hisaBusiness;

	@Test(priority = 1, enabled = true)
	@Parameters("branch")
	public void preCondition(@Optional("B2B Branch 623") final String branch) {

		stepDescription = "Create two personal customers and one business customer";
		stepExpected = "Two personal customers and one business customer created successfully";

		if (loginResult) {
			switchToBranch(branch);

			customerPersonal1 = createDefaultCustomer(PERSONAL, "", ROLEBASED_BANKING);
			customerPersonal2 = createDefaultCustomer(PERSONAL, "", ROLEBASED_BANKING);
			customerPersonalData = createdCustomers.get(customerPersonal1);

			customerBusiness = createDefaultCustomer(BUSINESS, "", ROLEBASED_BANKING);
			customerBusinessData = createdCustomers.get(customerBusiness);

			if (customerPersonal1 == null || customerPersonal2 == null || customerBusiness == null
					|| customerPersonal1.contains(ERROR) || customerPersonal2.contains(ERROR)
					|| customerBusiness.contains(ERROR)) {
				stepActual = "Error while creating personal and business customers";
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
	public void createHISAPersonal() {

		stepDescription = "Create HISA Personal arrangement";
		stepExpected = "HISA Personal arrangement created successfully";

		hisaPersonal = new ArrangementData("hisaPersonalArrangement", PERSONAL_ACCOUNTS, HISA_PERSONAL)
				.withCustomers(customerPersonal1 + "," + customerPersonal2, customerPersonalData,
						"BORROWER.OWNER,OWNER.JTRS", "100,0", "100,0")
				.withAgent("NEW", "Account Dummy Commission Plan", "NEW", "Account Dummy Commission Plan")
				.build();
		
		if (customerPersonal1 == null || customerPersonal2 == null || customerPersonal1.contains(ERROR)
				|| customerPersonal2.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			
			hisaPersonalArrangement = createDefaultArrangement(hisaPersonal);

			if (hisaPersonalArrangement == null || hisaPersonalArrangement.contains(ERROR)) {
				stepActual = "Error while creating HISA Personal arrangement";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	public void createHISABusiness() {

		stepDescription = "Create HISA Business arrangement";
		stepExpected = "HISA Business arrangement created successfully";

		hisaBusiness = new ArrangementData("hisaBusinessArrangement", BUSINESS_ACCOUNTS, HISA_BUSINESS)
				.withCustomers(customerBusiness, customerBusinessData, "BORROWER.OWNER", "100,", "100,")
				.withAgent("NEW", "Account Dummy Commission Plan", "NEW", "Account Dummy Commission Plan")
				.build();
		
		if (customerBusiness == null || customerBusiness.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			hisaBusinessArrangement = createDefaultArrangement(hisaBusiness);

			if (hisaBusinessArrangement == null || hisaBusinessArrangement.contains(ERROR)) {
				stepActual = "Error while creating HISA Business arrangement";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
