package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;

public class PROC_BB_ILA_OP003 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version: 3.3";
	private int numberOfArrangement = 4;
	private String[] varArrangement = new String[numberOfArrangement];
	private String fixArrangement1;
	private String fixArrangement2;
	private String fields;
	private String values;
	private boolean result;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "productGroup", "fixProduct", "varProduct" })
	public void preCondition(@Optional("B2B Branch 817") final String branch, final String productGroup,
			final String fixProduct, final String varProduct) {

		stepDescription = "Create four " + fixProduct + " and two " + varProduct + " arrangements";
		stepExpected = varProduct + " and " + fixProduct + " Arrangements created successfully";
		String loanType = null;
		String amount = null;

		if (loginResult) {
			switchToBranch(branch);

			for (int i = 0; i < numberOfArrangement; i++) {

				switch (i) {
				case 0:
					amount = "240T";
					loanType = "100";
					break;
				case 1:
					amount = "195T";
					loanType = "3.for.1";
					break;
				case 2:
					amount = "170T";
					loanType = "2.for.1";
					break;
				case 3:
					amount = "170T";
					loanType = "1.for.1";
					break;
				}
				final ArrangementData varArrangementData = new ArrangementData("varArrangement", productGroup,
						varProduct)
						.withEffectiveDate("-1m")
						.withCommitmentAmount(amount)
						.withMarginCallDetails("YES", loanType)
						.withDisbursement()
						.withRepayments()
						.build();
				varArrangement[i] = createDefaultArrangement(varArrangementData);

				if (varArrangement[i] == null || varArrangement[i].contains(ERROR)) {
					stepActual = "Error while creating " + varProduct + " Arrangement";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
			final ArrangementData fixArrangement1Data = new ArrangementData("fixArrangement1", productGroup, fixProduct)
					.withEffectiveDate("-1m")
					.withTerm("3M")
					.withDisbursement()
					.withRepayments()
					.build();
			fixArrangement1 = createDefaultArrangement(fixArrangement1Data);

			final ArrangementData fixArrangement2Data = new ArrangementData("fixArrangement2", productGroup, fixProduct)
					.withEffectiveDate("-1m")
					.withTerm("3M")
					.withDisbursement()
					.withRepayments()
					.build();
			fixArrangement2 = createDefaultArrangement(fixArrangement2Data);

			if (fixArrangement1 == null || fixArrangement1.contains(ERROR) || fixArrangement2 == null
					|| fixArrangement2.contains(ERROR)) {
				stepActual = "Error while creating " + fixProduct + " arrangement";
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
	@Parameters({ "productGroup", "varProduct" })
	public void rectifyMarginCall(final String productGroup, final String varProduct) {

		stepDescription = "Change product for " + varProduct + " Arrangement";
		stepExpected = "product changed successfully for " + varProduct + " Arrangement";

		if (varArrangement[0] == null || varArrangement[0].contains(ERROR) || varArrangement[1] == null
				|| varArrangement[1].contains(ERROR) || varArrangement[2] == null || varArrangement[2].contains(ERROR)
				|| varArrangement[3] == null || varArrangement[3].contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			for (int i = 0; i < numberOfArrangement; i++) {

				findArrangement(AUTHORISED, varArrangement[i], ARRANGEMENT, "", productGroup, "", "");
				switchToPage(LASTPAGE, false);
				versionScreen.hyperLink("New Activity").click();
				switchToPage(LASTPAGE, false);
				compositeScreen.textAction("AUTO Change Product for Multi-Resid", "Do Activity Today").click();
				switchToPage(LASTPAGE, false);
				inputData("PRODUCT", "AL.INVS.PL.FIX.RATE", false);
				toolElements.toolsButton(VALIDATE_DEAL).click();
				fields = "CHANGE.DATE.TYPE," + "CHANGE.PERIOD,";
				values = "Period," + "2Y,";
				result = multiInputData(fields, values, false) && inputTable.commitAndOverride();

				if (!result) {
					stepActual = "Error while Changing products for " + varProduct + " Arrangement";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "productGroup", "fixProduct" })
	public void renewFixedRateInvestmentLoan(final String productGroup, final String fixProduct) {

		stepDescription = "Renew " + fixProduct + "Arrangement: " + fixArrangement1;
		stepExpected = fixProduct + " Renewed successfully ";
		if (fixArrangement1 == null || fixArrangement1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			result = arrangementActivity(CREATE_AUTHORISE, "Update I Rate&Pay Amt Post Rollover", fixArrangement1,
					productGroup, "", "");
			if (!result) {
				stepActual = "Error while renewing " + fixProduct + " Arrangement";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters({ "productGroup", "fixProduct" })
	public void convertFixedRateLoanToVariableRateInvestmentLoan(final String productGroup, final String fixProduct) {

		stepDescription = "Convert " + fixProduct + " " + fixArrangement2 + " to variable rate investment loan ";
		stepExpected = fixProduct + " " + fixArrangement2 + " converted successfully to variable rate investment loan";
		if (fixArrangement2 == null || fixArrangement2.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			findArrangement(AUTHORISED, fixArrangement2, ARRANGEMENT, ROLEBASED_LENDING, productGroup, fixProduct, CAD);
			switchToPage(LASTPAGE, false);
			versionScreen.hyperLink("New Activity").click();
			switchToPage(LASTPAGE, false);
			compositeScreen.textAction("AUTO Change Product for Multi-Resid", "Do Activity Today").click();
			switchToPage(LASTPAGE, false);
			inputData("PRODUCT", "AL.INVS.PL.VAR.RATE", false);
			toolElements.toolsButton(VALIDATE_DEAL).click();
			fields = "MARGIN.TYPE:1:1," + "MARGIN.OPER:1:1," + "MARGIN.RATE:1:1,";
			values = " Single Margin," + "Add," + "3.00,";
			result = multiInputData(fields, values, false) && inputTable.commitAndOverride();

			if (!result) {
				stepActual = "Error while converting fix rate to variable Investment Loan";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
