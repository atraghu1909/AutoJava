package testcases.OOB;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class OOB_0193_01 extends testLibs.BaseTest_OOB {

	private String actualCIF;

	@Test(priority = 2, enabled = true)
	@Parameters({ "actionToPerform", "customer" })
	public void step1(final String actionToPerform, final String customer) {
		final String collateralCode = "1";

		stepDescription = "Perform the action, " + actionToPerform;
		stepExpected = actionToPerform + ", has been performed successfully";

		if (loginResult) {
			switchToBranch("B2B Branch 817");
			String customerType = PERSONAL;
			String completeLimitId = "";
			try {

				actualCIF = findCIF(customer, customerType, "");
				if (!actualCIF.equals(customer)) {
					Reporter.log("CIF received as parameter (" + customer + ") was not found. Using new CIF instead: "
							+ actualCIF, debugMode);
				}

				switchToPage(environmentTitle, true);

				if ("addCollateral".equalsIgnoreCase(actionToPerform)) {
					String collateralLink;
					String colateralLinkFields = "COLLATERAL.CODE,COMPANY:1,LIMIT.REF.CUST:1,LIMIT.REFERENCE:1,";
					String colateralLinkValues = collateralCode + ",B2B," + actualCIF + "," + completeLimitId + ",";
					collateralLink = collateral("Create and Authorise", "Collateral Link", actualCIF,
							colateralLinkFields, colateralLinkValues);

					String collateralFields = DefaultVariables.collateralFields + "Collateral details#CA.ADR.LINE1,"
							+ "Collateral details#TOWN.CITY," + "Collateral details#US.STATE,"
							+ "Collateral details#CA.POST.CODE," + "Collateral#COLLATERAL.CODE," + "COLLATERAL.TYPE,";
					String collateralValues = DefaultVariables.collateralValues
							+ createdCustomers.get(actualCIF).getAddressStreet() + ","
							+ createdCustomers.get(actualCIF).getAddressCity() + ","
							+ createdCustomers.get(actualCIF).getAddressProvince() + ","
							+ createdCustomers.get(actualCIF).getaddressPostalCode() + "," + collateralCode + ","
							+ collateralCode + ",";

					collateral("Create and Authorise", "Collateral Details", collateralLink, collateralFields,
							collateralValues);
					stepActual = "Collateral added successfully";
				} else if ("viewCollateralDetails".equalsIgnoreCase(actionToPerform)) {
					collateral("Count", "Collateral Link", actualCIF, "", "");
					stepActual = "Collateral summary detail page launch successfully ";
				}

			} catch (NoSuchElementException e) {
				stepResult = StatusAs.FAILED;
				stepActual = "Unable to perform required action";
				softVerify.fail(stepActual);
				Reporter.log(e.getMessage(), false);
			}

		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}
	}

}