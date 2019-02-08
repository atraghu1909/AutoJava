package testcases.PROC;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.Select;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PROC_BB_ILA_OP002 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 4.0";
	private String mainArrangement;
	private String customer;
	private String branch = "B2B Branch 817";

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void preCondition(final String productGroup, final String product) {

		stepDescription = "Create customer and " + product + " arrangement";
		stepExpected = "Customer and " + product + " arrangement created successfully";

		if (loginResult) {
			switchToBranch(branch);

			customer = createDefaultCustomer("", productGroup, ROLEBASED_LENDING);
			if (customer == null || customer.contains(ERROR)) {
				stepActual = "Error while creating customer";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {

				mainArrangement = dataGenCreateLoan(customer, productGroup, product, CAD, "", "",
						DefaultVariables.loanFields, DefaultVariables.loanValues, "Current", "-1m");
				if (mainArrangement == null || mainArrangement.contains(ERROR)) {
					stepActual = "Error while creating " + product + " arrangement";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
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
	public void holdPayoutLetterForWriteOffs(final String productGroup) {

		stepDescription = "Hold Payout Letter for arrangement: " + mainArrangement;
		stepExpected = "Payout Letter for arrangement: " + mainArrangement + " hold successfully";

		Select dropdown;
		boolean result = false;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			arrangementActivity(OPEN, "UPDATE ACTIVITY FOR MESSAGING", mainArrangement, productGroup, "", "");

			dropdown = new Select(enquiryElements.activityMessaging("LENDING-CLOSE-ARRANGEMENT"));
			dropdown.selectByVisibleText("No");

			result = inputTable.commitAndOverride() && authorizeEntity(mainArrangement, ACTIVITY + "," + productGroup);

			if (!result) {
				stepActual = "Error while Holding Payout Letter for arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;

			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void writeOffActivity(final String productGroup, final String product) {

		stepDescription = "Perform Write Off Activity for arrangement: " + mainArrangement;
		stepExpected = "Write Off Activity for arrangement: " + mainArrangement + " performed successfully";

		String fields;
		String values;
		String dischargeRequest;
		String suffixCURACCOUNT;
		String curaccountAttribute;
		String suffixACCPRINCIPALINT;
		String accprincipalintAttribute;
		String outstandingBillAmount;
		boolean result = false;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			dischargeRequest = generateStatement("", customer, productGroup, product, "", "");
			String[] dischargeResult = dischargeRequest.split("&");

			arrangementActivity(OPEN, "WRITE.OFF ACTIVITY FOR BALANCE.MAINTENANCE", mainArrangement, productGroup, "",
					"");

			fields = "ADJUST.DESC," + "Bill Adjustment#BILL.ADJ.NARR:1,";
			values = "Test Write Off," + "Write off as per Account Management,";

			curaccountAttribute = readTable.tabs("CURACCOUNT").getAttribute(ID);
			accprincipalintAttribute = readTable.tabs("ACCPRINCIPALINT").getAttribute(ID);
			suffixCURACCOUNT = curaccountAttribute.split(":")[1] + ":" + curaccountAttribute.split(":")[2];
			suffixACCPRINCIPALINT = accprincipalintAttribute.split(":")[1] + ":"
					+ accprincipalintAttribute.split(":")[2];
			inputData("WRITE.OFF:" + suffixCURACCOUNT, dischargeResult[0], false);
			inputData("WRITE.OFF:" + suffixACCPRINCIPALINT,
					Double.toString(Double.parseDouble(dischargeResult[1]) - 50.00), false);
			switchToTab("Bill Adjustment", "");
			outstandingBillAmount = readData("OS.BILL.AMT:1");
			inputData("Bill Adjustment#WRITE.OFF.BILL:1,", outstandingBillAmount, false);
			multiInputData(fields, values, false);
			if (toolElements.toolsButton(COMMIT_DEAL).getAttribute("src").contains("txncommit_dis")) {
				toolElements.toolsButton(VALIDATE).click();
			}
			toolElements.toolsButton(COMMIT_DEAL).click();
			result = inputTable.verifyAcceptOverride();
			result = authorizeEntity(mainArrangement, ACTIVITY + "," + productGroup);

			if (!result) {
				stepActual = "Error while creating WRITE.OFF ACTIVITY FOR BALANCE.MAINTENANCE activity for arrangement: "
						+ mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			findArrangement(AUTHORISED, mainArrangement, ARRANGEMENT, ROLEBASED_LENDING, productGroup, product, CAD);
			switchToPage(LASTPAGE, false);
			try {
				String amount = versionScreen.statusElement("Principal Interest (Accrued)").getText();
				fields = "NEW.BAL.AMT:" + suffixACCPRINCIPALINT + ",";
				values = amount + ",";

				result = arrangementActivity(CREATE_AUTHORISE, "ADJUST.BALANCE ACTIVITY FOR BALANCE.MAINTENANCE",
						mainArrangement, productGroup, fields, values);

				if (!result) {
					stepActual = "Error while creating ADJUST.BALANCE ACTIVITY FOR BALANCE.MAINTENANCE activity for arrangement: "
							+ mainArrangement;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			} catch (NoSuchElementException e) {
				Reporter.log("No addition charges pending");
			}
		}
		softVerify.assertAll();
	}
}
