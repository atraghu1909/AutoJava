package testcases.DataGen;

import java.util.Locale;
import java.util.Random;

import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class DataGen_createComplexCustomer extends testLibs.BaseTest_DataGen {

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "quantity", "status", "listOfProducts", "ownership", "effectiveDate", "dealerCode",
			"language", "addressDetails", "commitment", "principal", "authorizer", "escrowPayee" })
	public void step1(@Optional("Default") final String branch, final String quantity,
			@Optional("Single") final String status, final String listOfProducts, @Optional("") final String ownership,
			@Optional("") final String effectiveDate, @Optional("") final String dealerCode,
			@Optional("EN") final String language, @Optional("") final String addressDetails,
			@Optional("") final String commitment, @Optional("") final String principal,
			@Optional("true") final String authorizer, @Optional("") final String escrowPayee) {
		String customerType = "";
		String productGroup;
		String product;
		String customer1 = "";
		String customer2 = "";
		String customer3 = "";
		String customer4 = "";
		String step1Fields = "";
		String step1Values = "";
		String step2Fields = "";
		String step2Values = "";
		String defaultStep2Values = "";
		String defaultStep2Fields = "";
		String arrangementDetails = "";
		String mainArrangement = "";
		String arrangementType;
		String values = "";
		String fields = "";
		String cheqStep1Fields = "";
		String cheqStep1Values = "";
		String accountNumber = "";
		String agentValues;
		String actualCommitment = commitment;
		String defaultCIFFields;
		String defaultCIFValues;
		int randomChanceRoll;
		boolean authorizedFlag = false;
		final Random randomizer = new Random();
		final StringBuilder step1FieldsBuilder = new StringBuilder(step1Fields);
		final StringBuilder step1ValuesBuilder = new StringBuilder(step1Values);
		final StringBuilder cheqStep1FieldsBuilder = new StringBuilder(cheqStep1Fields);
		final StringBuilder cheqStep1ValuesBuilder = new StringBuilder(cheqStep1Values);
		final boolean hasPersonalProduct = listOfProducts.contains(PERSONAL_ACCOUNTS)
				|| listOfProducts.contains(RETAIL_DEPOSITS) || listOfProducts.contains(PERSONAL_LOC)
				|| listOfProducts.contains(PERSONAL_LOANS) || listOfProducts.contains(RETAIL_MORTGAGES);
		final boolean hasNonPersonalProduct = listOfProducts.contains(BUSINESS_ACCOUNTS)
				|| listOfProducts.contains(COMMERCIAL_LOANS);

		final StringBuilder arrangementDetailsBuilder = new StringBuilder(arrangementDetails);

		authorizedFlag = authorizer.equals("true");

		if (hasNonPersonalProduct && hasPersonalProduct) {
			stepActual = "Cannot create CIF as list of products has both Personal and Non Personal products";
			softVerify.fail(stepActual);
			stepResult = StatusAs.FAILED;
		} else if (hasPersonalProduct) {
			customerType = PERSONAL;
		} else if (hasNonPersonalProduct) {
			customerType = BUSINESS;
		} else {
			stepActual = "Cannot create CIF as list of products has neither Personal nor Non Personal products";
			softVerify.fail(stepActual);
			stepResult = StatusAs.FAILED;
		}

		defaultCIFFields = DefaultVariables.customerTypeFields.get(customerType);
		defaultCIFValues = DefaultVariables.customerTypeValues.get(customerType);

		final String[] listOfPairs = listOfProducts.split(",");
		final int iterations = Integer.valueOf(quantity);

		if (!"Default".equals(branch)) {
			switchToBranch(branch);
		}
		stepDescription = "Create " + quantity + " customers with " + listOfPairs.length + " Arrangements each";
		stepExpected = "Customers and All Arrangements are created successfully";
		for (int i = 1; i <= iterations; i++) {
			try {
				if ("EN".equals(language)) {
					fields = defaultCIFFields + "LANGUAGE,";
					values = defaultCIFValues + "1,";
				} else if ("FR".equals(language)) {
					fields = defaultCIFFields + "LANGUAGE,";
					values = defaultCIFValues + "2,";
				}

				switch (ownership) {
				default:
				case "Single":
					arrangementDetailsBuilder.append("[Single Owner]");
					arrangementDetails = arrangementDetailsBuilder.toString();
					customer1 = customer("Create", customerType, ROLEBASED_BANKING, fields, values);

					Reporter.log("Customer: " + customer1, debugMode);
					stepActual += "Customer: " + customer1;

					break;
				case "Two":
					arrangementDetailsBuilder.append("[Two Owners]");
					arrangementDetails = arrangementDetailsBuilder.toString();
					if ("Both".equals(language)) {
						fields = defaultCIFFields + "LANGUAGE,";
						values = defaultCIFValues + "1,";
						customer1 = customer("Create", customerType, ROLEBASED_BANKING, fields, values);
						fields = defaultCIFFields + "LANGUAGE,";
						values = defaultCIFValues + "2,";
						customer2 = customer("Create", customerType, ROLEBASED_BANKING, fields, values);
					} else {
						customer1 = customer("Create", customerType, ROLEBASED_BANKING, fields, values);
						customer2 = customer("Create", customerType, ROLEBASED_BANKING, fields, values);
					}

					cheqStep1FieldsBuilder.append("CUSTOMER+:2,CUSTOMER.ROLE:1,CUSTOMER.ROLE:2,");
					cheqStep1Fields = cheqStep1FieldsBuilder.toString();
					cheqStep1ValuesBuilder.append(customer2).append(",OWNER,BENEFICIARY,");
					cheqStep1Values = cheqStep1ValuesBuilder.toString();

					Reporter.log("Customers: " + customer1 + ", " + customer2, debugMode);
					stepActual += "Customers: " + customer1 + ", " + customer2;

					break;
				case "Four":
					arrangementDetailsBuilder.append("[Four Owners]");
					arrangementDetails = arrangementDetailsBuilder.toString();
					if ("Both".equals(language)) {
						fields = defaultCIFFields + "LANGUAGE,";
						values = defaultCIFValues + "1,";
						customer1 = customer("Create", customerType, ROLEBASED_BANKING, fields, values);
						customer2 = customer("Create", customerType, ROLEBASED_BANKING, fields, values);

						fields = defaultCIFFields + "LANGUAGE,";
						values = defaultCIFValues + "2,";
						customer3 = customer("Create", customerType, ROLEBASED_BANKING, fields, values);
						customer4 = customer("Create", customerType, ROLEBASED_BANKING, fields, values);
					} else {
						customer1 = customer("Create", customerType, ROLEBASED_BANKING, fields, values);
						customer2 = customer("Create", customerType, ROLEBASED_BANKING, fields, values);
						customer3 = customer("Create", customerType, ROLEBASED_BANKING, fields, values);
						customer4 = customer("Create", customerType, ROLEBASED_BANKING, fields, values);
					}

					Reporter.log("Customers: " + customer1 + ", " + customer2 + "," + customer3 + ", " + customer4,
							debugMode);
					stepActual += "Customers: " + customer1 + ", " + customer2 + "," + customer3 + ", " + customer4;

					break;
				}
				stepActual += System.lineSeparator();

				// Effective Date
				if (!"".equals(effectiveDate)) {
					cheqStep1FieldsBuilder.append("EFFECTIVE.DATE,");
					cheqStep1Fields = cheqStep1FieldsBuilder.toString();
					cheqStep1ValuesBuilder.append(effectiveDate).append(',');
					cheqStep1Values = cheqStep1ValuesBuilder.toString();
				}

				if (!effectiveDate.toUpperCase(Locale.ENGLISH).contains("Y")) {

					accountNumber = arrangements("Create and Authorise", PERSONAL_ACCOUNTS, PERSONAL_CHEQUING,
							ROLEBASED_BANKING, customer1, cheqStep1Fields, cheqStep1Values,
							DefaultVariables.bankingFields, DefaultVariables.bankingValues);
				}

				for (int j = 0; j < listOfPairs.length; j++) {
					final String[] listOfType = listOfPairs[j].split(">");
					productGroup = listOfType[0];
					product = listOfType[1];

					// Effective Date
					if (!"".equals(effectiveDate)) {
						step1FieldsBuilder.append("EFFECTIVE.DATE,");
						step1Fields = step1FieldsBuilder.toString();
						step1ValuesBuilder.append(effectiveDate).append(',');
						step1Values = step1ValuesBuilder.toString();
					}

					// Agent Dealer Code
					if (!"".equals(dealerCode)) {
						agentValues = setUpAgent(productGroup, dealerCode, product, "");
						step1FieldsBuilder.append(DefaultVariables.agentRequiredFields).append(',');
						step1Fields = step1FieldsBuilder.toString();
						step1ValuesBuilder.append(agentValues).append(',');
						step1Values = step1ValuesBuilder.toString();

						arrangementDetailsBuilder.append("[Agent: ")
								.append(agentValues.substring(0, agentValues.indexOf(','))).append(']');
						arrangementDetails = arrangementDetailsBuilder.toString();
					}

					if (DefaultVariables.productFields.containsKey(product)) {
						defaultStep2Fields = DefaultVariables.productFields.get(product);
						defaultStep2Values = DefaultVariables.productValues.get(product);
					} else {
						defaultStep2Fields = DefaultVariables.productGroupFields.get(productGroup);
						defaultStep2Values = DefaultVariables.productGroupValues.get(productGroup);
					}
					arrangementType = DefaultVariables.productGroupArrangementType.get(productGroup);

					final StringBuilder step2FieldsBuilder = new StringBuilder(defaultStep2Fields);
					final StringBuilder step2ValuesBuilder = new StringBuilder(defaultStep2Values);

					// Amount
					if ("".equals(commitment)) {
						randomChanceRoll = randomizer.nextInt(100000) + 100000;
						actualCommitment = Integer.toString(randomChanceRoll);
					}
					step2FieldsBuilder.append("AMOUNT,");
					step2Fields = step2FieldsBuilder.toString();
					step2ValuesBuilder.append(actualCommitment).append(',');
					step2Values = step2ValuesBuilder.toString();
					arrangementDetailsBuilder.append("[Amount: " + actualCommitment + "]");
					arrangementDetails = arrangementDetailsBuilder.toString();

					switch (ownership) {
					default:
					case "Single":

						step2Fields = step2FieldsBuilder.toString();
						step2Values = step2ValuesBuilder.toString();
						if ("Lending".equals(arrangementType) || "Mortgages".equals(arrangementType)) {
							step2FieldsBuilder.append("Basic#PAYIN.SETTLEMENT:1,");
							step2Fields = step2FieldsBuilder.toString();
							step2ValuesBuilder.append("No,");
							step2Values = step2ValuesBuilder.toString();
						}

						break;
					case "Two":

						if ("Lending".equals(arrangementType) || "Mortgages".equals(arrangementType)) {
							step1FieldsBuilder.append("CUSTOMER+:2,CUSTOMER.ROLE:1,CUSTOMER.ROLE:2,");
							step1Fields = step1FieldsBuilder.toString();
							step1ValuesBuilder.append(customer2).append(",OWNER,COBORROWER,");
							step1Values = step1ValuesBuilder.toString();

							step2FieldsBuilder
									.append("Basic#PAYIN.SETTLEMENT:1,TAX.LIABILITY.PERC:1,TAX.LIABILITY.PERC:2,");
							step2Fields = step2FieldsBuilder.toString();
							step2ValuesBuilder.append("No,50.00,50.00,");
							step2Values = step2ValuesBuilder.toString();
						} else {
							step1FieldsBuilder.append("CUSTOMER+:2,CUSTOMER.ROLE:1,CUSTOMER.ROLE:2,");
							step1Fields = step1FieldsBuilder.toString();
							step1ValuesBuilder.append(customer2).append(",OWNER,BENEFICIARY,");
							step1Values = step1ValuesBuilder.toString();
						}

						break;
					case "Four":

						if ("Lending".equals(arrangementType) || "Mortgages".equals(arrangementType)) {
							step1FieldsBuilder.append(
									"CUSTOMER+:2,CUSTOMER+:3,CUSTOMER+:4,CUSTOMER.ROLE:1,CUSTOMER.ROLE:2,CUSTOMER.ROLE:3,CUSTOMER.ROLE:4,");
							step1Fields = step1FieldsBuilder.toString();
							step1ValuesBuilder.append(customer2).append(",OWNER,COBORROWER,COBORROWER,COBORROWER,");
							step1Values = step1ValuesBuilder.toString();

							step2FieldsBuilder.append(
									"Basic#PAYIN.SETTLEMENT:1,TAX.LIABILITY.PERC:1,TAX.LIABILITY.PERC:2,TAX.LIABILITY.PERC:3,TAX.LIABILITY.PERC:4,");
							step2Fields = step2FieldsBuilder.toString();
							step2ValuesBuilder.append("No,25.00,25.00,25.00,25.00,");
							step2Values = step2ValuesBuilder.toString();
						} else {
							step1FieldsBuilder.append(
									"CUSTOMER+:2,CUSTOMER+:3,CUSTOMER+:4,CUSTOMER.ROLE:1,CUSTOMER.ROLE:2,CUSTOMER.ROLE:3,CUSTOMER.ROLE:4,");
							step1Fields = step1FieldsBuilder.toString();
							step1ValuesBuilder.append(customer2).append(",OWNER,BENEFICIARY,BENEFICIARY,BENEFICIARY,");
							step1Values = step1ValuesBuilder.toString();
						}

						break;
					}
					switch (arrangementType) {
					default:
					case "Banking":
						mainArrangement = dataGenCreateBanking(customer1, productGroup, product, CAD, step1Fields,
								step1Values, step2Fields, step2Values, status);
						break;

					case "Deposit":
						mainArrangement = dataGenCreateDeposit(customer1, productGroup, product, CAD, step1Fields,
								step1Values, step2Fields, step2Values, status, effectiveDate, "");
						break;

					case "Lending":
						mainArrangement = dataGenCreateLoan(customer1, productGroup, product, CAD, step1Fields,
								step1Values, step2Fields, step2Values, status, effectiveDate);
						break;

					case "Mortgages":
						mainArrangement = dataGenCreateMortgage(customer1, productGroup, product, CAD, step1Fields,
								step1Values, step2Fields, step2Values, status, effectiveDate, "", escrowPayee);

						break;
					}
					Reporter.log("Arrangement " + j + " " + product + ": " + mainArrangement, debugMode);
					stepActual += "Arrangement " + j + " " + product + ": " + mainArrangement;
					stepActual += System.lineSeparator();
					if (mainArrangement.contains("Error")) {
						softVerify.fail(mainArrangement + " " + ownership);
						stepResult = StatusAs.FAILED;
					}

					step1Fields = "";
					step1Values = "";
					step2Fields = "";
					step2Values = "";
					step1FieldsBuilder.setLength(0);
					step1ValuesBuilder.setLength(0);
					step2FieldsBuilder.setLength(defaultStep2Fields.length());
					step2ValuesBuilder.setLength(defaultStep2Values.length());
				}

			} catch (ElementNotVisibleException | NoSuchElementException e) {
				Reporter.log("Exception caught in iteration " + i + ". Continue with next iteration", debugMode);

			}

			softVerify.assertAll();
		}
	}
}
