package testcases.PROC;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.DefaultVariables;

public class PROC_OP_IL_CO022 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 4.0";
	private String mainArrangement;
	private String customer;
	private String branch = "B2B Branch 817";

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void preCondition(final String productGroup, final String product) {

		stepDescription = "Create customer and " + product + " arrangement with repayment";
		stepExpected = "Customer and " + product + " arrangement with repayment created successfully";

		boolean result;

		if (loginResult) {
			switchToBranch(branch);

			final ArrangementData loanData = new ArrangementData("mainArrangement", productGroup, product)
					.withCustomers("NEW,", null, "", "100,", "100,").withEffectiveDate("-2m").withDisbursement()
					.build();

			mainArrangement = createDefaultArrangement(loanData);
			customer = loanData.getCustomers();

			if (mainArrangement == null || mainArrangement.contains(ERROR)) {
				stepActual = "Error while creating " + product + " arrangement";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {

				result = repayArrangement("CAD1100100017817", mainArrangement, CAD, "", "-1m", customer, productGroup,
						product);
				if (!result) {
					stepActual = "Error while performing repayment of arrangement: " + mainArrangement;
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
	@Parameters({ "productGroup", "product" })
	public void missedPaymentCalculations(final String productGroup, final String product) {

		stepDescription = "Calculate missed payment for arrangement: " + mainArrangement;
		stepExpected = "Missed payment for arrangement: " + mainArrangement + " calculated successfully";

		String principal;
		String interestRate;
		String lastPaymentDate;
		String regularPayment;
		String resetDate;
		String firstPaymentDate;
		String effectiveDate;
		String term;
		int lastPaymentRow;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			findArrangement(AUTHORISED, mainArrangement, ARRANGEMENT, ROLEBASED_LENDING, productGroup, product, CAD);
			try {
				principal = versionScreen.labelElement("Total Principal").getText();
				interestRate = versionScreen.versionElement("Principal Interest", "Single Rate", "2").getText();
				regularPayment = versionScreen.versionElement("Schedule", "Special Payments", "2").getText();
				term = versionScreen.versionElement("Commitment", "Term", "1").getText();
				effectiveDate = versionScreen.labelElement("Effective Date").getText();
				versionScreen.linkText(ADDITIONAL_DETAILS, "Bills").click();
				versionScreen.waitForLoading();

				lastPaymentRow = enquiryElements.getRowsMatching("Repayments", "Type", "Installment", true).size();

				lastPaymentDate = enquiryElements.getElementAtCell("Repayments", "Due", 1, lastPaymentRow + 1)
						.getText();
				resetDate = enquiryElements.getElementAtCell("Capitalised", "Capitalised", 1, 2).getText();
				firstPaymentDate = enquiryElements.getElementAtCell("Repayments", "Due", 1, 2).getText();

				stepActual += System.lineSeparator();
				stepActual += "Principal + Accrued Interest = (" + principal + ")";
				stepActual += System.lineSeparator();
				stepActual += "Interest Rate = (" + interestRate + ")";
				stepActual += System.lineSeparator();
				stepActual += "Last Payment Date = (" + lastPaymentDate + ")";
				stepActual += System.lineSeparator();
				stepActual += "Regular Payment = (" + regularPayment + ")";
				stepActual += System.lineSeparator();
				stepActual += "Reset Date = (" + resetDate + ")";
				stepActual += System.lineSeparator();
				stepActual += "First Payment Date = (" + firstPaymentDate + ")";
				stepActual += System.lineSeparator();
				stepActual += "Effective Date = (" + effectiveDate + ")";
				stepActual += System.lineSeparator();
				stepActual += "Term  = (" + term + ")";
				Reporter.log("StepActual = " + stepActual, debugMode);

				if ("".equals(principal) || "".equals(interestRate) || "".equals(regularPayment) || "".equals(term)
						|| "".equals(effectiveDate) || "".equals(lastPaymentDate) || "".equals(resetDate)
						|| "".equals(firstPaymentDate)) {
					stepActual += System.lineSeparator();
					stepActual += "Arrangement details required for calculating the missed payment information is displayed as blank on the Arrangement Overview: "
							+ mainArrangement;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}

			} catch (NoSuchElementException e) {
				stepActual = "Arrangement details required for calculating the missed payment information is displayed as blank on the Arrangement Overview: "
						+ mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
