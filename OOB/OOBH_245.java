
package testcases.OOB;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class OOBH_245 extends testLibs.BaseTest_OOB {

	@Test(priority = 1, enabled = true)
	@Parameters({ "numberOfCustomers", "numberOfCollaterals", "provinceOfPrimaryCIF", "productGroup", "product" })
	public void step1(final String numberOfCustomers, final String numberOfCollaterals,
			final String provinceOfPrimaryCIF, final String productGroup, final String product) {
		stepDescription = "Triggering Activity Lending Discharge Collateral and Capitalising Fee depending on Province of Customer";
		stepExpected = "Charge amount should be updated in the generated bill depending on Province of Customer";
		final int collateral = Integer.valueOf(numberOfCollaterals);
		String newfieldsCIF;
		String newValuesCIF;
		String newValuesCIF2;
		String step2Fields;
		String step2Values;
		String step1Fields;
		String step1Values;
		String customer1 = null;
		String customer2;
		String personalAccount = null;
		String loan = null;

		if (productGroup.contains("Mortgages")) {

			step2Fields = DefaultVariables.mortgageFields + "," + "AMOUNT," + "TERM";
			step2Values = DefaultVariables.mortgageValues + "," + "10000000," + "15Y";

		} else {
			step2Fields = DefaultVariables.loanFields + "," + "AMOUNT," + "TERM";
			step2Values = DefaultVariables.loanValues + "," + "10000000," + "15Y";
		}

		newfieldsCIF = DefaultVariables.personalCIFFields + "," + "US.STATE";

		String provinceOfSecondaryCIF = null;
		final List<String> list = new ArrayList<String>();
		list.add("ON");
		list.add("QC");
		list.add("BC");
		list.add("AB");
		for (int i = 0; i < list.size(); i++) {
			provinceOfSecondaryCIF = list.get(new Random().nextInt(list.size()));
			if (!provinceOfSecondaryCIF.equals(provinceOfPrimaryCIF)) {
				break;
			}
		}

		if (loginResult) {
			switchToBranch("B2B Branch 817");
			if ("None".equals(provinceOfPrimaryCIF)) {
				newValuesCIF = DefaultVariables.personalCIFValues + "," + "    ";
				customer1 = customer("Create", PERSONAL, ROLEBASED_BANKING, newfieldsCIF, newValuesCIF);
				Reporter.log("Cannot Create CIF without Province", debugMode);
			} else {
				switch (numberOfCustomers) {

				case "1":
					newValuesCIF = DefaultVariables.personalCIFValues + "," + provinceOfPrimaryCIF;
					customer1 = customer("Create", PERSONAL, ROLEBASED_BANKING, newfieldsCIF, newValuesCIF);
					personalAccount = arrangements(CREATE, PERSONAL_ACCOUNTS, PERSONAL_CHEQUING, "", customer1,
							"CUSTOMER:1," + "CURRENCY,", customer1 + "," + CAD + ",", DefaultVariables.bankingFields,
							DefaultVariables.bankingValues);
					loan = arrangements(CREATE, productGroup, product, "", customer1, "CUSTOMER:1," + "CURRENCY,",
							customer1 + "," + CAD + ",", step2Fields, step2Values);
					Reporter.log("Customer1: " + customer1 + ", Arrangement: " + personalAccount + ", " + productGroup
							+ ":" + loan, debugMode);
					break;

				case "2":

					newValuesCIF = DefaultVariables.personalCIFValues + "," + provinceOfPrimaryCIF;
					customer1 = customer("Create", PERSONAL, ROLEBASED_BANKING, newfieldsCIF, newValuesCIF);
					newValuesCIF2 = DefaultVariables.personalCIFValues + "," + provinceOfSecondaryCIF;
					customer2 = customer("Create", PERSONAL, ROLEBASED_BANKING, newfieldsCIF, newValuesCIF);
					if (productGroup.contains("Mortgages")) {

						step2Fields = DefaultVariables.mortgageFields + "," + "AMOUNT," + "TERM,"
								+ "Insurance#LIFE.INS.FLAG:2," + "Insurance#DISABILITY.FLAG:2";
						step2Values = DefaultVariables.mortgageValues + "," + "10000000," + "15Y,"
								+ "Rejected.By.Insurance," + "Rejected.By.Insurance";

					}
					step1Fields = "CUSTOMER:1," + "CURRENCY," + "+CUSTOMER:2," + "CUSTOMER.ROLE:1," + "CUSTOMER.ROLE:2";
					step1Values = customer1 + "," + CAD + "," + customer2 + "," + "OWNER," + "JOINT.OWNER";
					personalAccount = arrangements(CREATE, PERSONAL_ACCOUNTS, PERSONAL_CHEQUING, "", customer1,
							step1Fields, step1Values, DefaultVariables.bankingFields, DefaultVariables.bankingValues);
					loan = arrangements(CREATE, productGroup, product, "", customer1, step1Fields, step1Values,
							step2Fields, step2Values);
					Reporter.log("Customer1: " + customer1 + ", Customer2: " + customer2 + ", Arrangement: "
							+ personalAccount + "," + productGroup + ":" + loan, debugMode);
					break;
				}

				for (int i = 0; i < collateral; i++) {
					String collateralLink;
					String completeLimitId = "";
					String colateralLinkFields = "COLLATERAL.CODE,COMPANY:1,LIMIT.REF.CUST:1,LIMIT.REFERENCE:1,";
					String colateralLinkValues = "400,B2B," + customer1 + "," + completeLimitId + ",";
					collateralLink = collateral("Create and Authorise", "Collateral Link", customer1,
							colateralLinkFields, colateralLinkValues);

					String collateralFields = DefaultVariables.collateralFields + "Collateral details#CA.ADR.LINE1,"
							+ "Collateral details#TOWN.CITY," + "Collateral details#US.STATE,"
							+ "Collateral details#CA.POST.CODE," + "Collateral#COLLATERAL.CODE," + "COLLATERAL.TYPE,";
					String collateralValues = DefaultVariables.collateralValues
							+ createdCustomers.get(customer1).getAddressStreet() + ","
							+ createdCustomers.get(customer1).getAddressCity() + ","
							+ createdCustomers.get(customer1).getAddressProvince() + ","
							+ createdCustomers.get(customer1).getaddressPostalCode() + ",400,400,";

					collateral("Create and Authorise", "Collateral Details", collateralLink, collateralFields,
							collateralValues);
				}

				findArrangement(AUTHORISED, loan, ARRANGEMENT, "", productGroup, product, CAD);
				if ("Not Disbursed".equals(versionScreen.statusElement("Status").getText())) {

					final String disbursementFields = "CREDIT.ACCT.NO,DEBIT.CURRENCY,DEBIT.ACCT.NO,DEBIT.AMOUNT";
					final String disbursementValues = personalAccount + "," + CAD + "," + loan + ",10000";
					financialTransaction(CREATE_AUTHORISE, "AA Disbursement", disbursementFields, disbursementValues);

				}
				findArrangement(AUTHORISED, loan, ARRANGEMENT, "", productGroup, product, CAD);
				arrangementActivity(CREATE_AUTHORISE, "Discharge Collateral", loan, productGroup, "", "");
				findArrangement(AUTHORISED, loan, ARRANGEMENT, "", productGroup, product, CAD);
				versionScreen.linkText(ADDITIONAL_DETAILS, "Bills").click();
				try {
					final String fee = compositeScreen.feeCharged("Discharge").getText();
					stepActual = "Amount Charge for customer with Province :" + provinceOfPrimaryCIF + "is " + fee;
				} catch (NoSuchElementException e) {
					stepResult = StatusAs.FAILED;
					stepActual = "There is no Fee posted on the arrangement ";
					softVerify.fail(stepActual);
				}
			}
		}

		else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
	}

}
