package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Test;
import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class OOB_0143_01 extends testLibs.BaseTest_OOB {
	private String completeLimitId;
	private String limitSerial;
	private String customer = "";
	private String personalChequing = "";
	private String createdLoanId = "";

	@Test(priority = 1, enabled = true)
	public void step1() {
		String limitReference;
		stepDescription = "Line of Credit - Payout Account";
		stepExpected = "Line of Credit should be created";

		if (loginResult) {
			switchToBranch("B2B Branch 817");
			final String limitFields = "LIMIT.CURRENCY," + "INTERNAL.AMOUNT,";
			final String limitValues = "CAD" + "," + "200000";
			customer = customer("Create", PERSONAL, ROLEBASED_BANKING, DefaultVariables.personalCIFFields,
					DefaultVariables.personalCIFValues);
			personalChequing = arrangements(CREATE, PERSONAL_ACCOUNTS, B2B_BANK_CHEQUING_ACCOUNT, "", customer,
					"CUSTOMER:1," + "CURRENCY,", customer + "," + CAD + ",", DefaultVariables.bankingFields,
					DefaultVariables.bankingValues);
			completeLimitId = customerLimit("Create and Authorise", UNSECURED, "", "Unsecured Line of Credit", customer,
					"", "01", limitFields, limitValues);

			limitReference = completeLimitId.substring(10, 14);

			limitSerial = completeLimitId.length() > 2 ? completeLimitId.substring(completeLimitId.length() - 2) : "";

			switchToPage("New Arrangement", false);

			final String step1Fields = "CUSTOMER:1,CURRENCY";
			final String step1Values = customer + ",CAD";
			arrangements("Validate", PERSONAL_LOC, "Unsecured Line of Credit", "", customer, step1Fields, step1Values,
					"", "");

			inputData("LIMIT.SERIAL", limitSerial, false);
			if (readData("LIMIT.REFERENCE").isEmpty()) {
				inputData("LIMIT.REFERENCE", limitReference, false);
			}

			multiInputData(DefaultVariables.loanFields, DefaultVariables.loanValues, false);

			try {
				inputData("PAYOUT.ACCOUNT:1:1", personalChequing, false);
				stepActual = "Able to populate the field fieldName:PAYOUT.ACCOUNT:1:1 with the created Personal Chequing Account"
						+ personalChequing;

			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "Not able to populate the field fieldName:PAYOUT.ACCOUNT:1:1 with the created Personal Chequing Account";
				softVerify.fail(stepActual);
				Reporter.log(e.getMessage(), false);

			}
			createdLoanId = readData("ACCOUNT.REFERENCE");
			toolElements.toolsButton(COMMIT_DEAL).click();

			inputTable.verifyAcceptOverride();

			switchToPage(environmentTitle, true);
			authorizeEntity(createdLoanId, "Arrangement," + PERSONAL_LOC);

		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}

	}

	@Test(priority = 2, enabled = true)
	public void step2() {
		stepDescription = "Line of Credit - Payout Account";
		stepExpected = "Line of Credit should be attached to the Payout Account successfully";
		if (loginResult) {
			switchToBranch("B2B Branch 817");

			findArrangement(AUTHORISED, createdLoanId, ARRANGEMENT, "", PERSONAL_LOC, "Unsecured Line of Credit", CAD);

			versionScreen.viewArrangement().click();
			switchToPage(LASTPAGE, false);

			if (personalChequing.equals(readTable.settelmentAccount().getText())) {
				stepResult = StatusAs.PASSED;
				stepActual = "Able link loan and personal account";
			} else {
				stepResult = StatusAs.FAILED;
				stepActual = "Not able link loan and personal account";
				throw new SkipException(stepActual);
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}
	}
}