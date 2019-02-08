package testcases.DataGen;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import org.testng.Reporter;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class DataGen_testPayment extends testLibs.BaseTest_DataGen {

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product", "quantity", "effectiveDate", "branch", "authorizer" })
	public void step1(final String productGroup, final String product, final String quantity,
			@Optional("") final String effectiveDate, @Optional("Default") final String branch,
			@Optional("true") final String authorizer) {
		final Random randomizer = new Random();
		final String customerType = DefaultVariables.productGroupCustomerType.get(productGroup);
		String[] customer = new String[3];
		String step1Fields = "";
		String step1Values = "";
		String step2Fields = "";
		String step2Values = "";
		String defaultStep2Values = "";
		String defaultStep2Fields = "";
		String arrangementDetails = "";
		String mainArrangement = "";
		String accountNumber;
		String arrangementType;
		String beneficiary;
		String benePrefProd;
		String actualEffectiveDate = effectiveDate;
		String cheqStep1Fields = "";
		String cheqStep1Values = "";
		String ownership;
		String paymentOrderFields;
		String paymentOrderValues;
		String beneficiaryFields;
		String beneficiaryValues;
		String status;
		String day;
		int randomChanceRoll;
		boolean authorizedFlag = false;
		SimpleDateFormat sdf;

		final StringBuilder step1FieldsBuilder = new StringBuilder(step1Fields);
		final StringBuilder step1ValuesBuilder = new StringBuilder(step1Values);
		final StringBuilder cheqStep1FieldsBuilder = new StringBuilder(cheqStep1Fields);
		final StringBuilder cheqStep1ValuesBuilder = new StringBuilder(cheqStep1Values);
		final StringBuilder arrangementDetailsBuilder = new StringBuilder(arrangementDetails);

		final int iterations = Integer.valueOf(quantity);

		if (!"Default".equals(branch)) {
			switchToBranch(branch);
		}

		authorizedFlag = authorizer.equals("true");

		if (DefaultVariables.productFields.containsKey(product)) {
			defaultStep2Fields = DefaultVariables.productFields.get(product);
			defaultStep2Values = DefaultVariables.productValues.get(product);
		} else {
			defaultStep2Fields = DefaultVariables.productGroupFields.get(productGroup);
			defaultStep2Values = DefaultVariables.productGroupValues.get(productGroup);
		}
		arrangementType = DefaultVariables.productGroupArrangementType.get(productGroup);

		stepDescription = "Create " + quantity + " customers with " + product + " disburse and setup ad hoc payments";
		stepExpected = "Ad hoc payments are set up successfully";

		final StringBuilder step2FieldsBuilder = new StringBuilder(defaultStep2Fields);
		final StringBuilder step2ValuesBuilder = new StringBuilder(defaultStep2Values);

		for (int i = 1; i <= iterations; i++) {

			randomChanceRoll = randomizer.nextInt(100);

			if (iterations > 5) {
				randomChanceRoll = (i + randomChanceRoll) * 20 % 100;
			}

			if (randomChanceRoll < 40) {
				ownership = "Single";
			} else if (randomChanceRoll < 80) {
				ownership = "Two";
			} else {
				ownership = "Four";
			}

			// Effective Date
			if (!"".equals(actualEffectiveDate)) {
				step1FieldsBuilder.append("EFFECTIVE.DATE,");
				step1Fields = step1FieldsBuilder.toString();
				step1ValuesBuilder.append(actualEffectiveDate).append(',');
				step1Values = step1ValuesBuilder.toString();

				cheqStep1FieldsBuilder.append("EFFECTIVE.DATE,");
				cheqStep1Fields = cheqStep1FieldsBuilder.toString();
				cheqStep1ValuesBuilder.append(actualEffectiveDate).append(',');
				cheqStep1Values = cheqStep1ValuesBuilder.toString();
			}

			switch (ownership) {
			default:
			case "Single":
				arrangementDetailsBuilder.append("[Single Owner]");
				arrangementDetails = arrangementDetailsBuilder.toString();
				customer[0] = createDefaultCustomer(customerType, productGroup, ROLEBASED_BANKING);

				step2Fields = step2FieldsBuilder.toString();
				step2Values = step2ValuesBuilder.toString();

				stepActual += "Customer: " + customer[0];

				break;
			case "Two":
				arrangementDetailsBuilder.append("[Two Owners]");
				arrangementDetails = arrangementDetailsBuilder.toString();
				customer[0] = createDefaultCustomer(customerType, productGroup, ROLEBASED_BANKING);
				customer[1] = createDefaultCustomer(customerType, productGroup, ROLEBASED_BANKING);

				step1FieldsBuilder.append("+CUSTOMER:2,CUSTOMER.ROLE:1,CUSTOMER.ROLE:2,");
				step1Fields = step1FieldsBuilder.toString();
				step1ValuesBuilder.append(customer[1]).append(",OWNER,COBORROWER,");
				step1Values = step1ValuesBuilder.toString();

				step2FieldsBuilder.append("TAX.LIABILITY.PERC:1,TAX.LIABILITY.PERC:2,");
				step2Fields = step2FieldsBuilder.toString();
				step2ValuesBuilder.append("50.00,50.00,");
				step2Values = step2ValuesBuilder.toString();

				stepActual += "Customers: " + customer[0] + ", " + customer[1];

				break;
			case "Four":
				arrangementDetailsBuilder.append("[Four Owners]");
				arrangementDetails = arrangementDetailsBuilder.toString();
				customer[0] = createDefaultCustomer(customerType, productGroup, ROLEBASED_BANKING);
				customer[1] = createDefaultCustomer(customerType, productGroup, ROLEBASED_BANKING);
				customer[2] = createDefaultCustomer(customerType, productGroup, ROLEBASED_BANKING);
				customer[3] = createDefaultCustomer(customerType, productGroup, ROLEBASED_BANKING);

				step1FieldsBuilder.append(
						"+CUSTOMER:2,+CUSTOMER:3,+CUSTOMER:4,CUSTOMER.ROLE:1,CUSTOMER.ROLE:2,CUSTOMER.ROLE:3,CUSTOMER.ROLE:4,");
				step1Fields = step1FieldsBuilder.toString();
				step1ValuesBuilder.append(customer[1]).append(',').append(customer[2]).append(',').append(customer[3])
						.append(",OWNER,COBORROWER,COBORROWER,COBORROWER,");
				step1Values = step1ValuesBuilder.toString();

				step2FieldsBuilder
						.append("TAX.LIABILITY.PERC:1,TAX.LIABILITY.PERC:2,TAX.LIABILITY.PERC:3,TAX.LIABILITY.PERC:4,");
				step2Fields = step2FieldsBuilder.toString();
				step2ValuesBuilder.append("25.00,25.00,25.00,25.00,");
				step2Values = step2ValuesBuilder.toString();

				stepActual += "Customers: " + customer[0] + ", " + customer[1] + "," + customer[2] + ", " + customer[3];

				break;
			}
			arrangementDetailsBuilder.append("[").append(customer[0]).append("]");
			stepActual += System.lineSeparator();

			accountNumber = arrangements("Create and Authorise", PERSONAL_ACCOUNTS, PERSONAL_CHEQUING,
					ROLEBASED_BANKING, customer[0], cheqStep1Fields, cheqStep1Values, DefaultVariables.bankingFields,
					DefaultVariables.bankingValues);

			randomChanceRoll = randomizer.nextInt(100);

			if (randomChanceRoll < 60) {
				status = "Delinquent";
				benePrefProd = "ACHCREDIT";
			} else {
				status = "Current With Payment";
				benePrefProd = "ACHUNCDR";
			}

			beneficiaryFields = DefaultVariables.beneficiaryFields + "OWNING.CUSTOMER," + "BEN.CUSTOMER,";
			beneficiaryValues = DefaultVariables.beneficiaryValues + customer[0] + ","
					+ createdCustomers.get(customer[0]).getCustomerName() + ",";

			beneficiary = beneficiaryCode(CREATE, "EFT Client", "", beneficiaryFields, beneficiaryValues);

			step2FieldsBuilder.append(
					"Basic#PAYIN.SETTLEMENT:1,Advanced - Pay In#PAYIN.PO.PRODUCT:1:1,Advanced - Pay In#PAYIN.BENEFICIARY:1:1,Advanced - Pay Out#PAYOUT.PO.PRODUCT:1:1,Advanced - Pay Out#PAYOUT.BENEFICIARY:1:1,Advanced - Pay In#PAYIN.ACTIVITY:1:1,Advanced - Pay Out#PAYOUT.ACTIVITY:1:1,");
			step2Fields = step2FieldsBuilder.toString();
			step2ValuesBuilder.append("Yes,").append(benePrefProd).append(',').append(beneficiary).append(',')
					.append(benePrefProd).append(',').append(beneficiary).append(", , ,");
			step2Values = step2ValuesBuilder.toString();

			sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
			Date date = new Date();

			day = sdf.format(date).substring(8, 10);

			// Frequency
			step2FieldsBuilder.append("PAYMENT.FREQ:2,").append("START.DATE:2:1,");
			step2Fields = step2FieldsBuilder.toString();
			step2ValuesBuilder.append("e0Y e1M e0W o" + day + "D e0F,").append(sdf.format(date)).append(",");
			step2Values = step2ValuesBuilder.toString();

			// Create Loan
			mainArrangement = dataGenCreateLoan(customer[0], productGroup, product, CAD, step1Fields, step1Values,
					step2Fields, step2Values, status, effectiveDate);

			if (status.contains("Delinquent")) {

				paymentOrderFields = "CREDIT.ACCOUNT,PAYMENT.ORDER.PRODUCT,DEBIT.CCY,PAYMENT.CURRENCY,PAYMENT.AMOUNT,ORDERING.CUSTOMER.SSI,";
				paymentOrderValues = mainArrangement + "," + benePrefProd + ",CAD,CAD,500," + beneficiary + ",";

				createAdHocPayment("Create", "EFT", mainArrangement, paymentOrderFields, paymentOrderValues);

			} else {

				paymentOrderFields = "DEBIT.ACCOUNT,PAYMENT.ORDER.PRODUCT,DEBIT.CCY,PAYMENT.CURRENCY,PAYMENT.AMOUNT,BENEFICIARY.ID,";
				paymentOrderValues = mainArrangement + "," + benePrefProd + ",CAD,CAD,500," + beneficiary + ",";

				createAdHocPayment("Create", "EFT", mainArrangement, paymentOrderFields, paymentOrderValues);

			}

			arrangementDetailsBuilder.append("[Product: ").append(product).append(']');
			arrangementDetails = arrangementDetailsBuilder.toString();

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
