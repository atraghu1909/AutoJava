package testcases.OOB;

import java.util.NoSuchElementException;

import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class OOBH_120 extends testLibs.BaseTest_OOB {

	@Test(priority = 1, enabled = true)
	@Parameters({ "customer", "productGroup", "product", "activity", "fieldsBefore", "valuesBefore", "fieldsAfter",
			"valuesAfter" })
	public void step1(final String customer, final String productGroup, final String product, final String activity,
			@Optional("") final String fieldsBefore, @Optional("") final String valuesBefore,
			@Optional("") final String fieldsAfter, @Optional("") final String valuesAfter) {

		if (loginResult) {
			switchToBranch("B2B Branch 817");

			try {

				final String arrangement = findArrangement(AUTHORISED, customer, CIF, "", productGroup, product, CAD);

				arrangementActivity(CREATE, activity, arrangement, productGroup, fieldsBefore, valuesBefore);

				findArrangement(AUTHORISED, arrangement, ARRANGEMENT, "", productGroup, product, CAD);

				compositeScreen.actionDropDown("", "drillbox").sendKeys("Edit");
				compositeScreen.actionButton("").click();
				switchToPage(LASTPAGE, false);

				multiInputData(fieldsAfter, valuesAfter, false);

				toolElements.toolsButton(COMMIT_DEAL).click();
				authorizeEntity(arrangement, "Activity," + productGroup);

			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "Unable to perform required action";
				softVerify.fail(stepActual);

			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
	}
}
