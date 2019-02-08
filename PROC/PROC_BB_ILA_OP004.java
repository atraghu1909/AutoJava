package testcases.PROC;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PROC_BB_ILA_OP004 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 3.5";
	private String mainArrangement;
	private String customer;
	private String branch = "B2B Branch 817";
	private String defaultStep2Fields = "";
	private String defaultStep2Values = "";

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void preCondition(final String productGroup, final String product) {

		stepDescription = "Create Current " + product + " Arrangement";
		stepExpected = "Current " + product + " Arrangement created successfully";

		String customerType;

		if (loginResult) {

			switchToBranch(branch);

			customerType = DefaultVariables.productGroupCustomerType.get(productGroup);

			if (DefaultVariables.productFields.containsKey(product)) {
				defaultStep2Fields = DefaultVariables.productFields.get(product);
				defaultStep2Values = DefaultVariables.productValues.get(product);
			} else {
				defaultStep2Fields = DefaultVariables.productGroupFields.get(productGroup);
				defaultStep2Values = DefaultVariables.productGroupValues.get(productGroup);
			}

			customer = createDefaultCustomer(customerType, productGroup, ROLEBASED_LENDING);

			mainArrangement = dataGenCreateLoan(customer, productGroup, product, CAD, "", "", defaultStep2Fields,
					defaultStep2Values, "Current via Cheque", "+0d");

			if (mainArrangement == null || mainArrangement.contains(ERROR)) {
				stepActual = "Error while creating " + product + " Arrangement: " + mainArrangement;
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
	@Parameters({ "productGroup" })
	public void changeProgramType(final String productGroup) {

		stepDescription = "Change Program Type for arrangement: " + mainArrangement;
		stepExpected = "Program Type was changed successfully for arrangement: " + mainArrangement;

		boolean result;
		String fields = "L.LOAN.OFFER.ID,";
		String values = "B2b Bank (sd & Deposits),";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementActivity(CREATE_AUTHORISE, "UPDATE ACTIVITY FOR ACCOUNT", mainArrangement, productGroup,
					fields, values);

			if (result) {
				fields = "MEMO.TEXT,";
				values = "Program Type has been updated for this arrangement,";
				result = arrangementMemo(CREATE, mainArrangement, productGroup, fields, values);
			}

			if (!result) {
				stepActual = "Error while changing Program Type for arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	public void changeAdminCode() {
		stepDescription = "Change Admin Code for arrangement: " + mainArrangement;
		stepExpected = "Admin Code was changed successfully for arrangement: " + mainArrangement;

		String collateralID;
		String collateralFields = "Registration details#L.COLL.ADM.CODE,";
		String collateralValues = "B2b Investment Loan,";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			collateralID = collateral(AMEND_AUTHORISE, COLLATERAL_DETAILS_LOANS, customer + ".1.1", collateralFields,
					collateralValues);

			if (collateralID == null || collateralID.contains(ERROR)) {
				stepActual = "Error while changing Admin Code for arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters()
	public void changeCPSAccount() {

		stepDescription = "Change CPS Account for arrangement: " + mainArrangement;
		stepExpected = "CPS Account was changed successfully for arrangement: " + mainArrangement;

		String result;
		String collateralFields = "Registration details#L.COLL.EXT.NO,";
		String collateralValues = "123454321,";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = collateral(AMEND_AUTHORISE, COLLATERAL_DETAILS_LOANS, customer + ".1.1", collateralFields,
					collateralValues);

			if (result == null || result.contains(ERROR)) {
				stepActual = "Error while changing CPS Account for arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	@Parameters({ "productGroup" })
	public void decreaseAmortization(final String productGroup) {

		stepDescription = "Decrease Amortization for arrangement: " + mainArrangement;
		stepExpected = "Amortization was decreased successfully for arrangement: " + mainArrangement;

		List<WebElement> listOfPaymentTypes;
		boolean result;
		String fields = "TERM,";
		String values = "2Y,";

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			arrangementActivity(CREATE_AUTHORISE, "CHANGE.TERM ACTIVITY FOR COMMITMENT", mainArrangement, productGroup,
					fields, values);

			arrangementActivity(OPEN, "CHANGE ACTIVITY FOR SCHEDULE", mainArrangement, productGroup, "", "");
			listOfPaymentTypes = inputTable.listOfFields("PAYMENT.TYPE", "Schedule");
			for (int i = 1; i <= listOfPaymentTypes.size(); i++) {
				if ("SPECIAL".equals(readData("PAYMENT.TYPE:" + i))) {
					fields = "ACTUAL.AMT:" + i + ":1,";
					values = "1000,";
					break;
				}
			}

			result = multiInputData(fields, values, false);
			if (toolElements.toolsButton(COMMIT_DEAL).getAttribute("src").contains("txncommit_dis")) {
				toolElements.toolsButton(VALIDATE_DEAL).click();
			}
			toolElements.toolsButton(COMMIT_DEAL).click();
			result = inputTable.verifyAcceptOverride();

			if (result) {
				result = authorizeEntity(mainArrangement, ACTIVITY + "," + productGroup);
			}

			if (result) {
				fields = "MEMO.TEXT,";
				values = "Amortization has been updated for this arrangement,";
				result = arrangementMemo("Create", mainArrangement, productGroup, fields, values);
			}

			if (!result) {
				stepActual = "Error while decreasing Amortization for arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
