package testcases.OOB;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.Reporter;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class OOB_0005 extends testLibs.BaseTest_OOB {

	String step2Fields;
	String step2Values;
	String arrangement;
	String[] listOfValues;
	String[] listOfFields;

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product", "fields1", "values1", "fields2", "values2" })
	public void step1(final String productGroup, final String product, @Optional("") final String fields1,
			@Optional("") final String values1, @Optional("") final String fields2,
			@Optional("") final String values2) {

		stepDescription = "Create an arrangement with following customers and roles: " + values1;
		stepExpected = "An arrangement is created with the proper customers and roles";
		listOfValues = values1.split(",");
		listOfFields = fields1.split(",");

		if (DefaultVariables.productFields.containsKey(product)) {
			step2Fields = DefaultVariables.productFields.get(product);
			step2Values = DefaultVariables.productValues.get(product);
		} else {

			step2Fields = DefaultVariables.productGroupFields.get(productGroup);
			step2Values = DefaultVariables.productGroupValues.get(productGroup);
		}

		if (loginResult) {

			switchToBranch("B2B Branch 817");
			parentHandle = driver.getWindowHandle();

			int j = 1;
			for (int i = 0; i < listOfFields.length; i++) {
				String fielItem = listOfFields[i];
				Pattern pattern = Pattern.compile("CUSTOMER:" + "[0-9]+");
				Matcher matcher = pattern.matcher(fielItem);
				if (matcher.find()) {
					if (productGroup.toLowerCase().contains("personal")) {
						listOfValues[i] = findCIF(listOfValues[i], "", "");
						step2Fields += "TAX.LIABILITY.PERC:" + j + ",";
						if ((j == 1) && (!product.toLowerCase().contains("unlimited chequing"))) {
							step2Values += "100,";
						} else {
							step2Values += "0,";
						}

					} else {
						listOfValues[i] = findCIF(listOfValues[i], BUSINESS, "");
						if (productGroup.toLowerCase().contains("retail mortgages")) {
							step2Fields += "LIFE.INS.FLAG:" + j + "," + "DISABILITY.FLAG:" + j + ",";
							step2Values += "None," + "None,";
						} else if (!productGroup.toLowerCase().contains("commercial")) {
							step2Fields += "PAYIN.SETTLEMENT:1,";
							step2Values += "No,";
						}

					}
					j++;
				}

			}
			arrangement = arrangements(CREATE, productGroup, product, "", listOfValues[0],
					"CUSTOMER:1," + "CURRENCY," + fields1,
					listOfValues[0] + "," + CAD + "," + String.join(",", listOfValues), step2Fields, step2Values);

			if (arrangement.contains("Error")) {
				stepActual = "Error message is present when creating Arrangement:" + product;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
	}

	@Test(priority = 2, enabled = true)
	@Parameters({ "productGroup", "product", "fields1", "values1", "fields2", "values2" })
	public void step2(final String productGroup, final String product, @Optional("") final String fields1,
			@Optional("") final String values1, @Optional("") final String fields2,
			@Optional("") final String values2) {

		stepDescription = "Commit the following additional roles: " + values2;
		stepExpected = "Additional roles have been commited";

		if (loginResult) {

			switchToBranch("B2B Branch 817");
			listOfValues = values2.split(",");
			listOfFields = fields2.split(",");

			if (fields2.isEmpty()) {
				stepResult = StatusAs.NOT_COMPLETED;
				stepActual = "Step was not executed as second fields list was empty";
				Reporter.log(stepActual, true);
			} else {
				int j = 1;
				for (int i = 0; i < listOfFields.length; i++) {
					String fielItem = listOfFields[i];
					Pattern pattern = Pattern.compile("CUSTOMER:" + "[0-9]+");
					Matcher matcher = pattern.matcher(fielItem);
					if (matcher.find()) {
						if (productGroup.toLowerCase().contains("personal")) {
							listOfValues[i] = findCIF(listOfValues[i], "", "");
							step2Fields += "TAX.LIABILITY.PERC:" + j + ",";
							if ((j == 1) && (!product.toLowerCase().contains("unlimited chequing"))) {
								step2Values += "100,";
							} else {
								step2Values += "0,";
							}

						} else {
							listOfValues[i] = findCIF(listOfValues[i], BUSINESS, "");
							if (productGroup.toLowerCase().contains("retail mortgages")) {
								step2Fields += "LIFE.INS.FLAG:" + j + "," + "DISABILITY.FLAG:" + j + ",";
								step2Values += "None," + "None,";
							} else if (!productGroup.toLowerCase().contains("commercial")) {
								step2Fields += "PAYIN.SETTLEMENT:1,";
								step2Values += "No,";
							}

						}
						j++;
					}
				}
				arrangement = arrangements(CREATE, productGroup, product, "", listOfValues[0],
						"CUSTOMER:1," + "CURRENCY," + fields2,
						listOfValues[0] + "," + CAD + "," + String.join(",", listOfValues), step2Fields, step2Values);

				if (arrangement.contains("Error")) {
					stepActual = "Error message is present when creating Arrangement:" + product;
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step was not executed, as login failed";
			throw new SkipException(stepActual);
		}
	}
}
