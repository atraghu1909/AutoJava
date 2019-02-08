package testcases.PROC;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.CustomerData;
import testLibs.DefaultVariables;

public class PROC_SP_IL_PR110 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.3";
	private String mainArrangement;
	private String customer;
	private String beneficiary;
	private ArrangementData arrangementData;
	private DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	private Date date = new Date();

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "customerType", "product" })
	public void preCondition(@Optional("B2B Branch 817") final String branch,
			@Optional("Personal") final String customerType,
			@Optional("Investment Loan Fixed Rate") final String product) {

		stepDescription = "Create customer and " + product + " arrangement";
		stepExpected = "Customer and " + product + " arrangement created successfully";

		if (loginResult) {

			switchToBranch(branch);
			arrangementData = new ArrangementData("mainArrangement", PERSONAL_LOANS, product)
					.withEffectiveDate("-1m").build();

			mainArrangement = createDefaultArrangement(arrangementData);
			customer = arrangementData.getCustomers();

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
	@Parameters({ "product" })
	public void newRegistration(@Optional("Investment Loan Fixed Rate") final String product) {

		stepDescription = "Create new registration for " + product + "arrangement";
		stepExpected = "New registration created successfully";

		String collateralFields;
		String collateralValues;
		String collateralID;
		boolean result = true;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			collateralFields = "Registration details#CA.PPSA.NUM,Registration details#L.COLL.REG.TYPE:1,Registration details#L.COLL.P.REG.DT:1,Collateral#EXPIRY.DATE,";
			collateralValues = "123456789,New,+0d,+10y,";

			collateralID = collateral(AMEND, COLLATERAL_DETAILS_LOANS, customer + ".1.1", collateralFields,
					collateralValues);

			if (collateralID == null || collateralID.contains(ERROR)) {
				stepActual = "Error while creating new registration";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "product" })
	public void renewal(@Optional("Investment Loan Fixed Rate") final String product) {

		stepDescription = "Renew Registration for " + product + "arrangement";
		stepExpected = "Registration renewed successfully";
		String collateralFields;
		String collateralValues;
		String collateralID;
		boolean result = true;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			collateralFields = "Registration details#CA.PPSA.NUM,+L.COLL.REG.TYPE:2,Registration details#L.COLL.P.REG.DT:2,Collateral#EXPIRY.DATE,";
			collateralValues = "555333000,Renewal,+5y,+15y,";

			collateralID = collateral(AMEND, COLLATERAL_DETAILS_LOANS, customer + ".1.1", collateralFields,
					collateralValues);

			if (collateralID == null || collateralID.contains(ERROR)) {
				stepActual = "Error while renewing Registration ";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters({ "product" })
	public void renewalFee(@Optional("Investment Loan Fixed Rate") final String product) {

		stepDescription = "Add renewal fee to " + product + "arrangement";
		stepExpected = "Renewal fee added successfully";

		String activityFields;
		String activityValues;
		boolean result;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			activityFields = "PPSA Fee$FIXED.AMOUNT,Schedule$+PAYMENT.TYPE:3,Schedule$PAYMENT.METHOD:3,Schedule$PAYMENT.FREQ:3,"
					+ "Schedule$PROPERTY:3:1,Schedule$START.DATE:3:1,Schedule$BILL.TYPE:3,";
			activityValues = "50.00,CHARGE,Due,e0Y e1M e0W e0D e0F,ALPPSAFEE,D_" + dateFormat.format(date)
					+ ",PAYMENT,";

			result = arrangementActivity(CREATE, "Change and Due ALPPSAFEE fee", mainArrangement, PERSONAL_LOANS,
					activityFields, activityValues);

			if (!result) {
				stepActual = "Error while adding renewal fee to " + product + "arrangement";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;

			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	@Parameters({ "product" })
	public void discharge(@Optional("Investment Loan Fixed Rate") final String product) {

		stepDescription = "Create discharge for " + product + "arrangement";
		stepExpected = "Discharge created successfully";

		String dischargeFields;
		String dischargeValues;
		String collateralID;
		String province;
		String activityFields;
		String activityValues;
		boolean result;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			dischargeFields = "Registration details#+L.COLL.REG.TYPE:3,Registration details#L.COLL.P.REG.DT:3,Collateral#EXPIRY.DATE,";
			dischargeValues = "Discharge,+7y,+7y,";

			collateralID = collateral(AMEND, COLLATERAL_DETAILS_LOANS, customer + ".1.1", dischargeFields,
					dischargeValues);
			province = createdCustomers.get(customer).getProvince();

			if ("NL".equals(province)) {

				activityFields = "PPSA Fee$FIXED.AMOUNT,Schedule$+PAYMENT.TYPE:3,Schedule$PAYMENT.METHOD:3,Schedule$PAYMENT.FREQ:3,"
						+ "Schedule$PROPERTY:3:1,Schedule$START.DATE:3:1,Schedule$BILL.TYPE:3,";
				activityValues = "50.00,CHARGE,Due,e0Y e1M e0W e0D e0F,ALPPSAFEE,D_" + dateFormat.format(date)
						+ ",PAYMENT,";

				result = arrangementActivity(CREATE, "Change and Due ALPPSAFEE fee", mainArrangement, PERSONAL_LOANS,
						activityFields, activityValues);
				if(!result){
					stepActual = "Error while performing Change and Due PPSA Fees Activity";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
					
				}
			}

			if (collateralID == null || collateralID.contains(ERROR)) {
				stepActual = "Error while creating discharge";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

}
