
package testcases.FT;

import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class R1_0_TDTC0007 extends testLibs.BaseTest_OOB {

	@Test(priority = 1, enabled = true)
	@Parameters({ "CIFType", "testType", "blockCardIssuance", "fields", "values" })
	public void step1(final String CIFType, final String testType, @Optional("Default") final String blockCardIssuance,
			@Optional("") final String fields, @Optional("") final String values) {

		String customer = "";
		String amendCIF = "";
		String newArrangement = "";
		String defaultCIFFields = DefaultVariables.customerTypeFields.get(CIFType);
		String defaultCIFValues = DefaultVariables.customerTypeValues.get(CIFType);
		String finalCIFFields = defaultCIFFields + fields;
		String finalCIFValues = defaultCIFValues + values;

		String result;

		if (loginResult) {

			if ("Yes".equalsIgnoreCase(blockCardIssuance)) {
				customer = customer("Create", CIFType, ROLEBASED_BANKING, finalCIFFields, finalCIFValues);
			} else {
				customer = customer("Create", CIFType, ROLEBASED_BANKING, defaultCIFFields, defaultCIFValues);
			}

			if (!"Error: Problem creating CIF".equalsIgnoreCase(customer)) {
				Reporter.log("New CIF " + customer + " is created successfully", debugMode);

				switch (testType) {

				case "Create":
					stepDescription = "Create a new CIF and Commit for blockCardIssuance :" + blockCardIssuance;
					stepExpected = "New CIF Should be Created successfully ";
					stepActual = "CIF:  " + customer + " was created successfully for blockCardIssuance  :"
							+ blockCardIssuance;
					break;

				case AMEND:
					stepDescription = "Create a CIF and Commit for default values , than amend the created CIF and commit for the updated value: "
							+ values + " for the Field:  " + fields;
					stepExpected = "New CIF Should be amended successfully for updated value";
					amendCIF = amendCIF(AMEND, customer, CIFType, ROLEBASED_BANKING, fields, values);
					if ("Error: Problem while amending CIF".equalsIgnoreCase(customer)) {

						stepResult = StatusAs.FAILED;
						stepActual = "There was a problem amending the CIF";
						softVerify.fail(stepActual);

					} else {
						stepResult = StatusAs.PASSED;
						stepActual = "Amend customer activity was successfully done for CIF:  " + amendCIF
								+ " for updated value  :" + values + " for the Field: " + fields;
					}
					break;

				case "View":
					stepDescription = "Create a new CIF and create new arrangement for unlimite chequing Account  createdCIF and edit a contract for the same ";
					stepExpected = "Contract should be successfully added if the  Value is Default  for:  " + fields
							+ "field and error message should be displayed if Value is YES";

					newArrangement = arrangements("Create and Authorise", PERSONAL_ACCOUNTS, PERSONAL_CHEQUING,
							ROLEBASED_BANKING, customer, "", "", DefaultVariables.bankingFields,
							DefaultVariables.bankingValues);
					Reporter.log(newArrangement);

					result = orderNewPersonalCard(PERSONAL_ACCOUNTS, PERSONAL_CHEQUING, CIFType, customer,
							DefaultVariables.bankingFields, DefaultVariables.bankingValues);

					if (("Yes").equalsIgnoreCase(blockCardIssuance)) {

						if (result.contains(TXN_COMPLETE)) {
							stepResult = StatusAs.FAILED;
							stepActual = "Contract was not added successfully for Value: " + values + "for field : "
									+ fields;
							softVerify.fail(stepActual);

						} else {
							stepResult = StatusAs.PASSED;
							stepActual = "Contract was added for Value: " + values + "for field : " + fields;
							Reporter.log(result, debugMode);
						}
					} else {

						if (result.contains(TXN_COMPLETE)) {
							stepResult = StatusAs.PASSED;
							stepActual = "Contract was added successfully for Value: " + values + "for field : "
									+ fields;
							Reporter.log(result, debugMode);
						} else {
							stepResult = StatusAs.FAILED;
							stepActual = "Contract was added successfully for Value: " + values + "for field : "
									+ fields;
							softVerify.fail(stepActual);
						}
					}

				default:
					break;
				}
			} else {
				stepResult = StatusAs.FAILED;
				stepActual = "There was a problem creating the CIF";
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