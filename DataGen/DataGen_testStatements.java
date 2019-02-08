package testcases.DataGen;

import java.util.Random;

import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.testng.Reporter;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class DataGen_testStatements extends testLibs.BaseTest_DataGen {

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "quantity", "status", "numberOfProducts", "ownership", "effectiveDate", "language",
			"addressDetails", "authorizer", "escrowPayee" })
	public void step1(@Optional("Default") final String branch, final String quantity,
			@Optional("Single") final String status, @Optional("1") final String numberOfProducts,
			@Optional("") final String ownership, @Optional("") final String effectiveDate,
			@Optional("EN") final String language, @Optional("") final String addressDetails,
			@Optional("true") final String authorizer, @Optional("") final String escrowPayee) {
		final Random randomizer = new Random();
		String customerType;
		String productGroup = "";
		String product = "";
		String customer1 = "";
		String customer2 = "";
		String customer3 = "";
		String customer4 = "";
		String step1Fields = "";
		String step1Values = "";
		String chequingStep1Fields = "";
		String chequingStep1Values = "";
		String step2Fields = "";
		String step2Values = "";
		String defaultStep2Values = "";
		String defaultStep2Fields = "";
		String arrangementDetails = "";
		String mainArrangement = "";
		String chequingArrangement;
		String arrangementType;
		String values = DefaultVariables.personalCIFValues;
		String fields = DefaultVariables.personalCIFFields;
		String fundsTransferFields;
		String fundsTransferValues;
		String agentValues;
		String dealerCode = "";
		String result;
		int randomChanceRoll;
		int totalProducts;

		boolean authorizedFlag = false;
		final StringBuilder chequingStep1FieldsBuilder = new StringBuilder(chequingStep1Fields);
		final StringBuilder chequingStep1ValuesBuilder = new StringBuilder(chequingStep1Values);
		final StringBuilder step1FieldsBuilder = new StringBuilder(step1Fields);
		final StringBuilder step1ValuesBuilder = new StringBuilder(step1Values);
		final StringBuilder fieldsBuilder = new StringBuilder(fields);
		final StringBuilder valuesBuilder = new StringBuilder(values);
		final StringBuilder arrangementDetailsBuilder = new StringBuilder(arrangementDetails);
		totalProducts = Integer.valueOf(numberOfProducts);
		final int iterations = Integer.valueOf(quantity);

		if (!"Default".equals(branch)) {
			switchToBranch(branch);
		}

		if (authorizer.equals("true")) {
			authorizedFlag = true;
		}

		stepDescription = "Create " + quantity + " customers with " + totalProducts + " Arrangements each";
		stepExpected = "Customers and All Arrangements are created successfully";
		for (int i = 1; i <= iterations; i++) {

			try {

				if ("EN".equals(language)) {
					fieldsBuilder.append("LANGUAGE,");
					fields = fieldsBuilder.toString();
					valuesBuilder.append("1,");
					values = valuesBuilder.toString();

				} else if ("FR".equals(language)) {
					fieldsBuilder.append("LANGUAGE,");
					fields = fieldsBuilder.toString();
					valuesBuilder.append("2,");
					values = valuesBuilder.toString();
				}

				switch (addressDetails) {
				case "Out Of Province":
					fieldsBuilder.append(
							"ADDRESS:1:1,STREET:1,TOWN.COUNTRY:1,ADDR.CNTRY.ID,US.STATE,POST.CODE:1,CITY,RECORD.REFRESH,");
					fields = fieldsBuilder.toString();
					valuesBuilder.append("9,The Hulk Street,Unit 01,CA,BC,V8P 1A1,Victoria,Y,");
					values = valuesBuilder.toString();

					break;
				case "U.S Address":
					fieldsBuilder.append(
							"ADDRESS:1:1,STREET:1,TOWN.COUNTRY:1,ADDR.CNTRY.ID,US.STATE,POST.CODE:1,CITY,RESIDENCE,RECORD.REFRESH,");
					fields = fieldsBuilder.toString();
					valuesBuilder.append("7,Bond Street,Unit 007,US,MI,JB007,Detroit,US,Y,");
					values = valuesBuilder.toString();

					break;
				case "International":
					fieldsBuilder.append(
							"ADDRESS:1:1,STREET:1,TOWN.COUNTRY:1,ADDR.CNTRY.ID,US.STATE,POST.CODE:1,CITY,RESIDENCE,RECORD.REFRESH,");
					fields = fieldsBuilder.toString();
					valuesBuilder.append("5,Bonjour Street,Unit 05,FR,XX,75000,Paris,FR,Y,");
					values = valuesBuilder.toString();

					break;
				default:
				}

				arrangementDetailsBuilder.append("[AddressType:").append(addressDetails).append(']');
				arrangementDetails = arrangementDetailsBuilder.toString();

				// Customer Type
				customerType = PERSONAL;

				switch (ownership) {
				default:
				case "Single":
					arrangementDetailsBuilder.append("[Single Owner]");
					arrangementDetails = arrangementDetailsBuilder.toString();
					customer1 = customer("Create", customerType, ROLEBASED_BANKING, fields, values);

					result = "Customer" + i + " " + ": " + customer1;
					stepActual += result;
					Reporter.log(result, debugMode);

					break;
				case "Two":
					arrangementDetailsBuilder.append("[Two Owners]");
					arrangementDetails = arrangementDetailsBuilder.toString();
					if ("Both".equals(language)) {
						fieldsBuilder.append("LANGUAGE,");
						fields = fieldsBuilder.toString();
						valuesBuilder.append("1,");
						values = valuesBuilder.toString();
						customer1 = customer("Create", customerType, ROLEBASED_BANKING, fields, values);
						if ("Different Addresses".equals(addressDetails)) {
							fieldsBuilder.append(
									"LANGUAGE,ADDRESS:1:1,STREET:1,TOWN.COUNTRY:1,ADDR.CNTRY.ID,US.STATE,POST.CODE:1,CITY,RECORD.REFRESH,");
							fields = fieldsBuilder.toString();
							valuesBuilder.append("2,9,The Hulk Street,Unit 01,CA,BC,V8P 1A1,Victoria,Y,");
							values = valuesBuilder.toString();
						} else {
							fieldsBuilder.append("LANGUAGE,");
							fields = fieldsBuilder.toString();
							valuesBuilder.append("2,");
							values = valuesBuilder.toString();
						}
						customer2 = customer("Create", customerType, ROLEBASED_BANKING, fields, values);
					} else {
						customer1 = customer("Create", customerType, ROLEBASED_BANKING, fields, values);
						if ("Different Addresses".equals(addressDetails)) {
							fieldsBuilder.append(
									"ADDRESS:1:1,STREET:1,TOWN.COUNTRY:1,ADDR.CNTRY.ID,US.STATE,POST.CODE:1,CITY,RECORD.REFRESH,");
							fields = fieldsBuilder.toString();
							valuesBuilder.append("9,The Hulk Street,Unit 01,CA,BC,V8P 1A1,Victoria,Y,");
							values = valuesBuilder.toString();
							customer2 = customer("Create", customerType, ROLEBASED_BANKING, fields, values);
						} else {
							customer2 = customer("Create", customerType, ROLEBASED_BANKING, fields, values);
						}
					}
					result = "Customers" + i + " " + ": " + customer1 + ", " + customer2;
					stepActual += result;
					Reporter.log(result, debugMode);

					break;
				case "Four":
					arrangementDetailsBuilder.append("[Four Owners]");
					arrangementDetails = arrangementDetailsBuilder.toString();
					if ("Both".equals(language)) {
						fieldsBuilder.append("LANGUAGE,");
						fields = fieldsBuilder.toString();
						valuesBuilder.append("1,");
						values = valuesBuilder.toString();
						customer1 = customer("Create", customerType, ROLEBASED_BANKING, fields, values);
						customer2 = customer("Create", customerType, ROLEBASED_BANKING, fields, values);

						fieldsBuilder.append("LANGUAGE,");
						fields = fieldsBuilder.toString();
						valuesBuilder.append("2,");
						values = valuesBuilder.toString();
						customer3 = customer("Create", customerType, ROLEBASED_BANKING, fields, values);
						customer4 = customer("Create", customerType, ROLEBASED_BANKING, fields, values);
					} else {
						customer1 = customer("Create", customerType, ROLEBASED_BANKING, fields, values);
						customer2 = customer("Create", customerType, ROLEBASED_BANKING, fields, values);
						customer3 = customer("Create", customerType, ROLEBASED_BANKING, fields, values);
						customer4 = customer("Create", customerType, ROLEBASED_BANKING, fields, values);
					}
					result = "Customers" + i + " " + ": " + customer1 + ", " + customer2 + ", " + customer3 + ", "
							+ customer4;
					stepActual += result;
					Reporter.log(result, debugMode);

					break;
				}
				stepActual += System.lineSeparator();

				// Effective Date
				if (!"".equals(effectiveDate)) {
					chequingStep1FieldsBuilder.append("EFFECTIVE.DATE,");
					chequingStep1Fields = chequingStep1FieldsBuilder.toString();
					chequingStep1ValuesBuilder.append(effectiveDate).append(',');
					chequingStep1Values = chequingStep1ValuesBuilder.toString();
				}

				chequingArrangement = arrangements("Create and Authorise", PERSONAL_ACCOUNTS, PERSONAL_CHEQUING,
						ROLEBASED_BANKING, customer1, chequingStep1Fields, chequingStep1Values,
						DefaultVariables.bankingFields, DefaultVariables.bankingValues);

				for (int j = 0; j < totalProducts; j++) {

					// Effective Date
					if (!"".equals(effectiveDate)) {
						step1FieldsBuilder.append("EFFECTIVE.DATE,");
						step1Fields = step1FieldsBuilder.toString();
						step1ValuesBuilder.append(effectiveDate).append(',');
						step1Values = step1ValuesBuilder.toString();
					}

					// Determine Product
					randomChanceRoll = randomizer.nextInt(100);
					if (iterations > 6) {
						randomChanceRoll = (i + randomChanceRoll) * 16 % 100;
					}
					if (randomChanceRoll < 16) {
						productGroup = PERSONAL_LOANS;
						product = "Investment Loan Fixed Rate";
					} else if (randomChanceRoll < 32) {
						productGroup = PERSONAL_LOANS;
						product = "Investment Loan Variable Rate";
					} else if (randomChanceRoll < 48) {
						productGroup = PERSONAL_LOANS;
						product = "RRSP Personal Loan Fixed Rate";
					} else if (randomChanceRoll < 64) {
						productGroup = PERSONAL_LOANS;
						product = "RRSP Personal Loan Variable Rate";
					} else if (randomChanceRoll < 80) {
						productGroup = PERSONAL_LOANS;
						product = "TFSA Loan Fixed Rate";
					} else {
						productGroup = PERSONAL_LOANS;
						product = "TFSA Loan Variable Rate";
					}
					arrangementDetailsBuilder.append("[Product: ").append(product).append(']');
					arrangementDetails = arrangementDetailsBuilder.toString();

					// Determine Dealer Code
					if ("Courier".equals(addressDetails)) {
						dealerCode = "100091750";
					} else {
						randomChanceRoll = randomizer.nextInt(100);
						if (iterations > 10) {
							randomChanceRoll = (i + randomChanceRoll) * 10 % 100;
						}
						if (randomChanceRoll < 10) {
							dealerCode = "100091741";
						} else if (randomChanceRoll < 20) {
							dealerCode = "100091742";
						} else if (randomChanceRoll < 30) {
							dealerCode = "100091744";
						} else if (randomChanceRoll < 40) {
							dealerCode = "100091745";
						} else if (randomChanceRoll < 50) {
							dealerCode = "100091746";
						} else if (randomChanceRoll < 60) {
							dealerCode = "100091748";
						} else if (randomChanceRoll < 70) {
							dealerCode = "100091749";
						} else if (randomChanceRoll < 80) {
							dealerCode = "100091751";
						} else if (randomChanceRoll < 90) {
							dealerCode = "100091752";
						} else {
							dealerCode = "100091753";
						}
					}

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

					switch (ownership) {
					default:
					case "Single":

						step2Fields = step2FieldsBuilder.toString();
						step2Values = step2ValuesBuilder.toString();

						break;
					case "Two":
						if ("Different Disclosure Preference".equals(addressDetails)) {
							step2FieldsBuilder.append("DELIVERY.REQD:2,");
							step2Fields = step2FieldsBuilder.toString();
							step2ValuesBuilder.append(",");
							step2Values = step2ValuesBuilder.toString();
						}
						if ("Lending".equals(arrangementType) || "Mortgages".equals(arrangementType)) {
							step1FieldsBuilder.append("+CUSTOMER:2,CUSTOMER.ROLE:1,CUSTOMER.ROLE:2,");
							step1Fields = step1FieldsBuilder.toString();
							step1ValuesBuilder.append(customer2).append(",OWNER,COBORROWER,");
							step1Values = step1ValuesBuilder.toString();

							step2FieldsBuilder.append("TAX.LIABILITY.PERC:1,TAX.LIABILITY.PERC:2,");
							step2Fields = step2FieldsBuilder.toString();
							step2ValuesBuilder.append("50.00,50.00,");
							step2Values = step2ValuesBuilder.toString();
						} else {
							step1FieldsBuilder.append("+CUSTOMER:2,CUSTOMER.ROLE:1,CUSTOMER.ROLE:2,");
							step1Fields = step1FieldsBuilder.toString();
							step1ValuesBuilder.append(customer2).append(",OWNER,BENEFICIARY,");
							step1Values = step1ValuesBuilder.toString();
						}

						if (RETAIL_MORTGAGES.equals(productGroup)) {
							step2FieldsBuilder.append(",Insurance#LIFE.INS.FLAG:2,Insurance#DISABILITY.FLAG:2");
							step2Fields = step2FieldsBuilder.toString();
							step2ValuesBuilder.append(",Rejected.By.Insurance,Rejected.By.Insurance");
							step2Values = step2ValuesBuilder.toString();
						}
						break;
					case "Four":

						if ("Lending".equals(arrangementType) || "Mortgages".equals(arrangementType)) {
							step1FieldsBuilder.append(
									"+CUSTOMER:2,+CUSTOMER:3,+CUSTOMER:4,CUSTOMER.ROLE:1,CUSTOMER.ROLE:2,CUSTOMER.ROLE:3,CUSTOMER.ROLE:4,");
							step1Fields = step1FieldsBuilder.toString();
							step1ValuesBuilder.append(customer2).append(',').append(customer3).append(',')
									.append(customer4).append(",OWNER,COBORROWER,COBORROWER,COBORROWER,");
							step1Values = step1ValuesBuilder.toString();

							step2FieldsBuilder.append(
									"TAX.LIABILITY.PERC:1,TAX.LIABILITY.PERC:2,TAX.LIABILITY.PERC:3,TAX.LIABILITY.PERC:4,");
							step2Fields = step2FieldsBuilder.toString();
							step2ValuesBuilder.append("25.00,25.00,25.00,25.00,");
							step2Values = step2ValuesBuilder.toString();

						} else {
							step1FieldsBuilder.append(
									"+CUSTOMER:2,+CUSTOMER:3,+CUSTOMER:4,CUSTOMER.ROLE:1,CUSTOMER.ROLE:2,CUSTOMER.ROLE:3,CUSTOMER.ROLE:4,");
							step1Fields = step1FieldsBuilder.toString();
							step1ValuesBuilder.append(customer2).append(',').append(customer3).append(',')
									.append(customer4).append(",OWNER,BENEFICIARY,BENEFICIARY,BENEFICIARY,");
							step1Values = step1ValuesBuilder.toString();
						}

						if (RETAIL_MORTGAGES.equals(productGroup)) {
							step2FieldsBuilder.append(",Insurance#LIFE.INS.FLAG:2,Insurance#DISABILITY.FLAG:2");
							step2Fields = step2FieldsBuilder.toString();
							step2ValuesBuilder.append(",Rejected.By.Insurance,Rejected.By.Insurance");
							step2Values = step2ValuesBuilder.toString();
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
						if (status.contains("with Multiple Activities")) {
							fundsTransferFields = "CREDIT.ACCT.NO,CREDIT.CURRENCY,DEBIT.ACCT.NO,CREDIT.AMOUNT,";
							fundsTransferValues = chequingArrangement + "," + CAD + "," + mainArrangement + "," + "10,";

							for (int k = 0; k <= 20; k++) {
								financialTransaction(CREATE_AUTHORISE, "AA Principal Decrease", fundsTransferFields,
										fundsTransferValues);
							}
						}
						break;

					case "Mortgages":
						mainArrangement = dataGenCreateMortgage(customer1, productGroup, product, CAD, step1Fields,
								step1Values, step2Fields, step2Values, status, effectiveDate, "", escrowPayee);
						break;
					}
					Reporter.log("Arrangement " + (j + 1) + " " + product + ": " + mainArrangement + " "
							+ arrangementDetails, debugMode);
					stepActual += "Arrangement " + (j + 1) + " " + product + ": " + mainArrangement + " "
							+ arrangementDetails;
					stepActual += System.lineSeparator();
					if (mainArrangement.contains("Error")) {
						softVerify.fail(mainArrangement + " " + ownership);
						stepResult = StatusAs.FAILED;
					}

					step2FieldsBuilder.setLength(defaultStep2Fields.length());
					step2ValuesBuilder.setLength(defaultStep2Values.length());

					step1Fields = "";
					step1Values = "";
					step2Fields = "";
					step2Values = "";
					arrangementDetails = "";
					arrangementDetailsBuilder.setLength(0);
					step1FieldsBuilder.setLength(0);
					step1ValuesBuilder.setLength(0);
				}
			} catch (ElementNotVisibleException | NoSuchElementException e) {
				Reporter.log("Exception caught in iteration " + i + ". Continue with next iteration.", debugMode);
			}

			softVerify.assertAll();

			fields = DefaultVariables.personalCIFFields;
			values = DefaultVariables.personalCIFValues;
			fieldsBuilder.setLength(fields.length());
			valuesBuilder.setLength(values.length());
		}
	}
}