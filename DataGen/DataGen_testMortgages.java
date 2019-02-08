package testcases.DataGen;

import java.util.Random;

import org.testng.Reporter;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class DataGen_testMortgages extends testLibs.BaseTest_DataGen {

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "quantity", "status", "effectiveDate", "authorizeFlag", "productGroup", "product",
			"escrowPayee" })

	public void step1(@Optional("Default") final String branch, final String quantity,
			@Optional("Delinquent") final String status, @Optional("") final String effectiveDate,
			@Optional("true") final String authorizeFlag, final String productGroup, final String product,
			@Optional("") final String escrowPayee) {

		final Random randomizer = new Random();
		String customerType = "";
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
		int randomChanceRoll;
		String agentValues;
		boolean authorizeFlagBoolean = false;
		String ownership;
		final StringBuilder step1FieldsBuilder = new StringBuilder(step1Fields);
		final StringBuilder step1ValuesBuilder = new StringBuilder(step1Values);
		final StringBuilder arrangementDetailsBuilder = new StringBuilder(arrangementDetails);

		final int iterations = Integer.valueOf(quantity);

		if (!"Default".equals(branch)) {
			switchToBranch(branch);
		}

		if (authorizeFlag.equals("true")) {
			authorizeFlagBoolean = true;
		}

		stepDescription = "Create " + quantity + " customers with Fixed Rate Closed Term Mortgage";
		stepExpected = "Customers and All Arrangements are created successfully";

		defaultStep2Fields = DefaultVariables.mortgageFields;
		defaultStep2Values = DefaultVariables.mortgageValues;

		final StringBuilder step2FieldsBuilder = new StringBuilder(defaultStep2Fields);
		final StringBuilder step2ValuesBuilder = new StringBuilder(defaultStep2Values);

		for (int i = 1; i <= iterations; i++) {

			randomChanceRoll = randomizer.nextInt(100);
			if (iterations > 5) {
				randomChanceRoll = (i + randomChanceRoll) * 20 % 100;
			}
			if (randomChanceRoll < 60) {
				ownership = "Single";
			} else if (randomChanceRoll < 80) {
				ownership = "Two";
			} else {
				ownership = "Four";
			}

			switch (ownership) {
			default:
			case "Single":
				arrangementDetailsBuilder.append("[Single Owner]");
				arrangementDetails = arrangementDetailsBuilder.toString();
				customer1 = createDefaultCustomer(customerType, productGroup, ROLEBASED_LENDING);

				stepActual += "Customer: " + customer1;
				arrangementDetailsBuilder.append('[').append(customer1).append(']');

				break;
			case "Two":
				arrangementDetailsBuilder.append("[Two Owners]");
				arrangementDetails = arrangementDetailsBuilder.toString();
				customer1 = createDefaultCustomer(customerType, productGroup, ROLEBASED_LENDING);
				customer2 = createDefaultCustomer(customerType, productGroup, ROLEBASED_LENDING);
				stepActual += "Customers: " + customer1 + ", " + customer2;
				arrangementDetailsBuilder.append('[').append(customer1).append(']').append('[').append(customer2)
						.append(']');

				break;
			case "Four":
				arrangementDetailsBuilder.append("[Four Owners]");
				arrangementDetails = arrangementDetailsBuilder.toString();
				customer1 = createDefaultCustomer(customerType, productGroup, ROLEBASED_LENDING);
				customer2 = createDefaultCustomer(customerType, productGroup, ROLEBASED_LENDING);
				customer3 = createDefaultCustomer(customerType, productGroup, ROLEBASED_LENDING);
				customer4 = createDefaultCustomer(customerType, productGroup, ROLEBASED_LENDING);
				Reporter.log("Customers: " + customer1 + ", " + customer2 + "," + customer3 + "," + customer4,
						debugMode);
				stepActual += "Customers: " + customer1 + ", " + customer2 + "," + customer3 + "," + customer4;
				arrangementDetailsBuilder.append('[').append(customer1).append(']').append('[').append(customer2)
						.append(']').append('[').append(customer3).append(']').append('[').append(customer4)
						.append(']');
				break;
			}

			stepActual += System.lineSeparator();

			chequingArrangement = arrangements("Create and Authorise", PERSONAL_ACCOUNTS, PERSONAL_CHEQUING,
					ROLEBASED_BANKING, customer1, chequingStep1Fields, chequingStep1Values,
					DefaultVariables.bankingFields, DefaultVariables.bankingValues);

			step2FieldsBuilder
					.append("PAYIN.SETTLEMENT:1,PAYIN.ACCOUNT:1:1,Advanced - Pay In#PAYIN.AC.DB.RULE:1,TERM,");
			step2Fields = step2FieldsBuilder.toString();
			step2ValuesBuilder.append("Yes,").append(chequingArrangement).append(',').append("Full,").append("10Y,");
			step2Values = step2ValuesBuilder.toString();

			arrangementDetailsBuilder.append("[Product: ").append(product).append(']');
			arrangementDetails = arrangementDetailsBuilder.toString();

			// Agent
			if (branch.equals("Branch 603")) {
				agentValues = setUpAgent(productGroup, "200000349", product, "");
			} else {
				agentValues = setUpAgent(productGroup, "100090983", product, "");
			}

			step1FieldsBuilder.append(DefaultVariables.agentRequiredFields).append(',');
			step1Fields = step1FieldsBuilder.toString();
			step1ValuesBuilder.append(agentValues).append(',');
			step1Values = step1ValuesBuilder.toString();

			arrangementDetailsBuilder.append("[Agent: ").append(agentValues.substring(0, agentValues.indexOf(',')))
					.append(']');
			arrangementDetails = arrangementDetailsBuilder.toString();

			// Amount
			randomChanceRoll = randomizer.nextInt(100) + 100;

			step2FieldsBuilder.append("AMOUNT,");
			step2Fields = step2FieldsBuilder.toString();
			step2ValuesBuilder.append(randomChanceRoll).append("000,");
			step2Values = step2ValuesBuilder.toString();
			arrangementDetailsBuilder.append("[Amount: " + randomChanceRoll + ",000]");
			arrangementDetails = arrangementDetailsBuilder.toString();

			// Customer Type
			customerType = PERSONAL;

			// Offer ID

			final int valueOfferId = randomizer.nextInt(10);

			final String offerIdType;
			switch (valueOfferId) {
			default:
			case 0:
				offerIdType = "B2b Bank (sd & Deposits)";
				break;
			case 1:
				offerIdType = "Ci Investments";
				break;
			case 2:
				offerIdType = "Dynamic (goodman & Company)";
				break;
			case 3:
				offerIdType = "Foresters Fininv Mgmt Company Of Ca";
				break;
			case 4:
				offerIdType = "Sunlife (clarica Guaranteed)";
				break;
			case 5:
				offerIdType = "Canada Life";
				break;
			case 6:
				offerIdType = "Ssq";
				break;
			case 7:
				offerIdType = "Queensbury";
				break;
			case 8:
				offerIdType = "Lbs";
				break;
			case 9:
				offerIdType = "Equitable Life";
				break;
			}

			step2FieldsBuilder.append("L.LOAN.OFFER.ID,");
			step2Fields = step2FieldsBuilder.toString();
			step2ValuesBuilder.append(offerIdType).append(",");
			step2Values = step2ValuesBuilder.toString();

			arrangementDetailsBuilder.append("[Offer ID = ").append(offerIdType).append("]");
			arrangementDetails = arrangementDetailsBuilder.toString();

			if (i % 2 == 0) {
				final Random generator = new Random();
				int num0;
				num0 = generator.nextInt(89999999) + 10000000;
				final String number = Integer.toString(num0);

				step2FieldsBuilder.append("L.INS.COMP.NAME,CA.CMHC.NO,");
				step2Fields = step2FieldsBuilder.toString();
				step2ValuesBuilder.append("Cmhc").append(',').append(number).append(',');
				step2Values = step2ValuesBuilder.toString();
			} else {
				final Random generator = new Random();
				int num0;
				int num1;

				num0 = generator.nextInt(89999) + 10000;
				num1 = generator.nextInt(89999) + 10000;

				final String number0 = Integer.toString(num0);
				final String number1 = Integer.toString(num1);

				final String number = number0 + number1;

				step2FieldsBuilder.append("L.INS.COMP.NAME,CA.CMHC.NO,");
				step2Fields = step2FieldsBuilder.toString();
				step2ValuesBuilder.append("Genworth").append(',').append(number).append(',');
				step2Values = step2ValuesBuilder.toString();
			}

			// Insurance Fee
			arrangementDetailsBuilder.append("[With Insurance]");
			arrangementDetails = arrangementDetailsBuilder.toString();
			step2FieldsBuilder.append(
					"Insurance#LIFE.INS.FLAG:1,Insurance#DISABILITY.FLAG:1,LIFE.INS.AMT:1,DISABILITY.AMT:1,FIXED.AMOUNT,+PAYMENT.TYPE:2,PAYMENT.METHOD:2,PROPERTY:2:1,");
			step2Fields = step2FieldsBuilder.toString();
			step2ValuesBuilder.append("Accepted,Accepted,100,100,50,INSURANCE,Due,ALINSURANCEFEE,");
			step2Values = step2ValuesBuilder.toString();

			// Statement Frequency
			final Random generator = new Random();
			int day;
			day = generator.nextInt(7) + 1;
			String frequency = "e0Y e0M e1W o" + day + "D e0F";

			step2FieldsBuilder.append("PAYMENT.FREQ:1,DUE.FREQ:1:1,PAYMENT.FREQ:2,DUE.FREQ:2:1,");
			step2Fields = step2FieldsBuilder.toString();
			step2ValuesBuilder.append(frequency).append(',').append(frequency).append(',').append(frequency).append(',')
					.append(frequency).append(',');
			step2Values = step2ValuesBuilder.toString();

			switch (ownership) {
			default:
			case "Single":
				step1FieldsBuilder.append("CUSTOMER.ROLE:1,");
				step1Fields = step1FieldsBuilder.toString();
				step1ValuesBuilder.append("BENIFICIAL.OWNER,");
				step1Values = step1ValuesBuilder.toString();

				step2Fields = step2FieldsBuilder.toString();
				step2Values = step2ValuesBuilder.toString();

				break;
			case "Two":
				step1FieldsBuilder.append("+CUSTOMER:2,CUSTOMER.ROLE:1,CUSTOMER.ROLE:2,");
				step1Fields = step1FieldsBuilder.toString();
				step1ValuesBuilder.append(customer2).append(",BENIFICIAL.OWNER,COBORROWER,");
				step1Values = step1ValuesBuilder.toString();

				step2FieldsBuilder.append(
						"Customer#TAX.LIABILITY.PERC:1,TAX.LIABILITY.PERC:2,Insurance#LIFE.INS.FLAG:2,Insurance#DISABILITY.FLAG:2,LIFE.INS.AMT:2,DISABILITY.AMT:2,");
				step2Fields = step2FieldsBuilder.toString();
				step2ValuesBuilder.append("50.00,50.00,Accepted,Accepted,100,100,");
				step2Values = step2ValuesBuilder.toString();

				break;
			case "Four":
				step1FieldsBuilder.append(
						"+CUSTOMER:2,+CUSTOMER:3,+CUSTOMER:4,CUSTOMER.ROLE:1,CUSTOMER.ROLE:2,CUSTOMER.ROLE:3,CUSTOMER.ROLE:4,");
				step1Fields = step1FieldsBuilder.toString();
				step1ValuesBuilder.append(customer2).append(',').append(customer3).append(',').append(customer4)
						.append(",BENIFICIAL.OWNER,COBORROWER,COBORROWER,COBORROWER,");
				step1Values = step1ValuesBuilder.toString();

				step2FieldsBuilder.append(
						"Customer#TAX.LIABILITY.PERC:1,TAX.LIABILITY.PERC:2,TAX.LIABILITY.PERC:3,TAX.LIABILITY.PERC:4,Insurance#LIFE.INS.FLAG:2,Insurance#DISABILITY.FLAG:2,Insurance#LIFE.INS.FLAG:3,Insurance#DISABILITY.FLAG:3,Insurance#LIFE.INS.FLAG:4,Insurance#DISABILITY.FLAG:4,LIFE.INS.AMT:2,DISABILITY.AMT:2,LIFE.INS.AMT:3,DISABILITY.AMT:3,LIFE.INS.AMT:4,DISABILITY.AMT:4,");
				step2Fields = step2FieldsBuilder.toString();
				step2ValuesBuilder.append(
						"25.00,25.00,25.00,25.00,Accepted,Accepted,Accepted,Accepted,Accepted,Accepted,100,100,100,100,100,100,");
				step2Values = step2ValuesBuilder.toString();

				break;
			}

			mainArrangement = dataGenCreateMortgage(customer1, productGroup, product, CAD, step1Fields, step1Values,
					step2Fields, step2Values, status, effectiveDate, "", escrowPayee);
			Reporter.log("Arrangement " + product + ": " + mainArrangement + " " + arrangementDetails, debugMode);
			stepActual += "Arrangement " + product + ": " + mainArrangement + " " + arrangementDetails;
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
			step1FieldsBuilder.setLength(0);
			step1ValuesBuilder.setLength(0);

			softVerify.assertAll();
		}
	}
}
