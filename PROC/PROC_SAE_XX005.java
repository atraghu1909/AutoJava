package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PROC_SAE_XX005 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "2018-03-29";
	private final String defaultStep2Fields = DefaultVariables.saeLoanFields;
	private final String defaultStep2Values = DefaultVariables.saeLoanValues;
	private final String limitFields = DefaultVariables.securedSAELimitFields;
	private final String limitValues = DefaultVariables.securedSAELimitValues;
	private final String step1Fields = "CURRENCY,";
	private final String step1Values = "CAD,";
	private String mainArrangement1;
	private String mainArrangement2;
	private String mainArrangement3;
	private String mainArrangement4;
	private String mainArrangement5;
	private String mainArrangement6;
	private String mainArrangement7;
	private String actualStep2Fields;
	private String actualStep2Values;
	private String limit;
	private String parentLimit;
	private String limitReference;
	private String limitSerial;
	private String customer1;
	private String customer2;
	private String customer3;
	private String customer4;
	private String customer5;
	private String customer6;
	private String customer7;

	@Test(priority = 1, enabled = true)
	@Parameters({ "customerType", "productGroup", "product", "branch" })
	public void preCondition(final String customerType, final String productGroup, final String product,
			@Optional("LAURENTIAN BANK - 523") final String branch) {

		stepDescription = "Create Seven " + customerType + " Customers";
		stepExpected = customerType + " Customers created successfully";
		if (loginResult) {
			switchToBranch(branch);

			customer1 = createDefaultCustomer(customerType, productGroup, ROLEBASED_SAE);
			customer2 = createDefaultCustomer(customerType, productGroup, ROLEBASED_SAE);
			customer3 = createDefaultCustomer(customerType, productGroup, ROLEBASED_SAE);
			customer4 = createDefaultCustomer(customerType, productGroup, ROLEBASED_SAE);
			customer5 = createDefaultCustomer(customerType, productGroup, ROLEBASED_SAE);
			customer6 = createDefaultCustomer(customerType, productGroup, ROLEBASED_SAE);
			customer7 = createDefaultCustomer(customerType, productGroup, ROLEBASED_SAE);

			if (customer1 == null || customer1.contains(ERROR) || customer2 == null || customer2.contains(ERROR)
					|| customer3 == null || customer3.contains(ERROR) || customer4 == null || customer4.contains(ERROR)
					|| customer5 == null || customer5.contains(ERROR) || customer6 == null || customer6.contains(ERROR)
					|| customer7 == null || customer7.contains(ERROR)) {
				stepActual = "Error while creating customers";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Login failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
	}

	@Test(priority = 2, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void createNonRevolvingLoan(final String productGroup, final String product) {

		stepDescription = "Create Non-revolving : " + product + " Arrangement";
		stepExpected = "Non-Revolving " + product + " created successfully";

		if (customer1 == null || customer1.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			parentLimit = customerLimit(CREATE, "Non-Revolving Secured", "SAE Loans and Mortgages", product,
					customer1, "2500", "", limitFields, limitValues);
			limit = customerLimit(CREATE, "Non-Revolving Secured", "SAE Loans and Mortgages", product, customer1, "",
					"", limitFields, limitValues);
			limitReference = limit.substring(13, 17);
			limitSerial = limit.length() > 2 ? limit.substring(limit.length() - 2) : "";
			actualStep2Fields = defaultStep2Fields + "REVOLVING," + "LIMIT.REFERENCE," + "LIMIT.SERIAL,";
			actualStep2Values = defaultStep2Values + "NO," + limitReference + "," + limitSerial + ",";
			mainArrangement1 = arrangements(CREATE, productGroup, product, ROLEBASED_SAE, customer1,
					step1Fields, step1Values, actualStep2Fields, actualStep2Values);
			if (parentLimit == null || parentLimit.contains(ERROR)) {
				stepActual = "Error while creating parent Limit for Customer: " + customer1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else if (limit == null || limit.contains(ERROR)) {
				stepActual = "Error while creating Limit for Customer: " + customer1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else if (mainArrangement1 == null || mainArrangement1.contains(ERROR)) {
				stepActual = "Error while creating " + product + " Arrangement for customer" + customer1;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void createRevolvingWithPaymentLoan(final String productGroup, final String product) {

		stepDescription = "Create Revolving with Payment" + product + " Arrangement";
		stepExpected = "Revolving with Payment " + product + " Arrangement created successfully";

		if (customer2 == null || customer2.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			parentLimit = customerLimit(CREATE, "Revolving Secured", "SAE Loans and Mortgages", product, customer2,
					"2400", "", limitFields, limitValues);
			limit = customerLimit(CREATE, "Revolving Secured", "SAE Loans and Mortgages", product, customer2, "", "",
					limitFields, limitValues);
			limitReference = limit.substring(13, 17);
			limitSerial = limit.length() > 2 ? limit.substring(limit.length() - 2) : "";
			actualStep2Fields = defaultStep2Fields + "REVOLVING," + "LIMIT.REFERENCE," + "LIMIT.SERIAL,";
			actualStep2Values = defaultStep2Values + "PAYMENT," + limitReference + "," + limitSerial + ",";
			mainArrangement2 = arrangements(CREATE, productGroup, product, ROLEBASED_SAE, customer2,
					step1Fields, step1Values, actualStep2Fields, actualStep2Values);
			if (parentLimit == null || parentLimit.contains(ERROR)) {
				stepActual = "Error while creating parent Limit for Customer: " + customer2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else if (limit == null || limit.contains(ERROR)) {
				stepActual = "Error while creating Limit for Customer: " + customer2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else if (mainArrangement2 == null || mainArrangement2.contains("Error")) {
				stepActual = "Error while creating " + product + " Arrangement for customer" + customer2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void createRevolvingWithPrepaymentLoan(final String productGroup, final String product) {

		stepDescription = "Create Revolving with PrePayment" + product + " Arrangement";
		stepExpected = "Revolving with PrePayment " + product + " Arrangement created successfully";

		if (customer3 == null || customer3.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			parentLimit = customerLimit(CREATE, "Revolving Secured", "SAE Loans and Mortgages", product, customer3,
					"2400", "", limitFields, limitValues);
			limit = customerLimit(CREATE, "Revolving Secured", "SAE Loans and Mortgages", product, customer3, "", "",
					limitFields, limitValues);
			limitReference = limit.substring(13, 17);
			limitSerial = limit.length() > 2 ? limit.substring(limit.length() - 2) : "";
			actualStep2Fields = defaultStep2Fields + "REVOLVING," + "LIMIT.REFERENCE," + "LIMIT.SERIAL,";
			actualStep2Values = defaultStep2Values + "PREPAYMENT," + limitReference + "," + limitSerial + ",";
			mainArrangement3 = arrangements(CREATE, productGroup, product, ROLEBASED_SAE, customer3,
					step1Fields, step1Values, actualStep2Fields, actualStep2Values);
			if (parentLimit == null || parentLimit.contains(ERROR)) {
				stepActual = "Error while creating parent Limit for Customer: " + customer3;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else if (limit == null || limit.contains(ERROR)) {
				stepActual = "Error while creating Limit for Customer: " + customer3;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else if (mainArrangement3 == null || mainArrangement3.contains("Error")) {
				stepActual = "Error while creating " + product + " Arrangement for customer" + customer3;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void createLoanBlendedPayment(final String productGroup, final String product) {

		stepDescription = "Create Non-Revolving" + product + " Arrangement with Blended Payments";
		stepExpected = "Non-Revolving " + product + " Arrangement with Blended Payments type created successfully";

		if (customer4 == null || customer4.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			parentLimit = customerLimit(CREATE, "Non-Revolving Secured", "SAE Loans and Mortgages", product,
					customer4, "2500", "", limitFields, limitValues);
			limit = customerLimit(CREATE, "Non-Revolving Secured", "SAE Loans and Mortgages", product, customer4, "",
					"", limitFields, limitValues);
			limitReference = limit.substring(13, 17);
			limitSerial = limit.length() > 2 ? limit.substring(limit.length() - 2) : "";
			actualStep2Fields = defaultStep2Fields + "REVOLVING," + "LIMIT.REFERENCE," + "LIMIT.SERIAL,"
					+ "PAYMENT.TYPE:1," + "PAYMENT.METHOD:1," + "PROPERTY:1:1," + "BILL.TYPE:1," + "ACTUAL.AMT:1:1,"
					+ "PROPERTY:1:2,";
			actualStep2Values = defaultStep2Values + "NO," + limitReference + "," + limitSerial + "," + "BLENDED,"
					+ "Due," + "ACCOUNT," + "INSTALLMENT," + "," + "PRINCIPALINT,";
			mainArrangement4 = arrangements(CREATE, productGroup, product, ROLEBASED_SAE, customer4,
					step1Fields, step1Values, actualStep2Fields, actualStep2Values);
			if (parentLimit == null || parentLimit.contains(ERROR)) {
				stepActual = "Error while creating parent Limit for Customer: " + customer4;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else if (limit == null || limit.contains(ERROR)) {
				stepActual = "Error while creating Limit for Customer: " + customer4;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else if (mainArrangement4 == null || mainArrangement4.contains("Error")) {
				stepActual = "Error while creating " + product + " Arrangement for customer" + customer4;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void createLoanLinearPayment(final String productGroup, final String product) {
		stepDescription = "Create Non-Revolving " + product + " Arrangement with Linear Payments";
		stepExpected = "Non-Revolving " + product + " Arrangement with Linear Payments type created successfully";

		if (customer5 == null || customer5.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			parentLimit = customerLimit(CREATE, "Non-Revolving Secured", "SAE Loans and Mortgages", product,
					customer5, "2500", "", limitFields, limitValues);
			limit = customerLimit(CREATE, "Non-Revolving Secured", "SAE Loans and Mortgages", product, customer5, "",
					"", limitFields, limitValues);
			limitReference = limit.substring(13, 17);
			limitSerial = limit.length() > 2 ? limit.substring(limit.length() - 2) : "";
			actualStep2Fields = defaultStep2Fields + "REVOLVING," + "LIMIT.REFERENCE," + "LIMIT.SERIAL,"
					+ "PAYMENT.TYPE:1," + "PAYMENT.METHOD:1," + "PROPERTY:1:1," + "BILL.TYPE:1," + "ACTUAL.AMT:1:1,"
					+ "PROPERTY:1<:2," + "PAYMENT.TYPE+:2," + "PAYMENT.METHOD:2," + "PROPERTY:2:1," + "BILL.TYPE:2,"
					+ "ACTUAL.AMT:2:1," + "PAYMENT.FREQ:2,";
			actualStep2Values = defaultStep2Values + "NO," + limitReference + "," + limitSerial + "," + "LINEAR,"
					+ "Due," + "ACCOUNT," + "INSTALLMENT," + " ," + " ," + "INTEREST.ONLY," + "Due," + "PRINCIPALINT,"
					+ "INSTALLMENT," + " ," + "e0Y e1M e0W e0D e0F,";
			mainArrangement5 = arrangements(CREATE, productGroup, product, ROLEBASED_SAE, customer5,
					step1Fields, step1Values, actualStep2Fields, actualStep2Values);
			if (parentLimit == null || parentLimit.contains(ERROR)) {
				stepActual = "Error while creating parent Limit for Customer: " + customer5;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else if (limit == null || limit.contains(ERROR)) {
				stepActual = "Error while creating Limit for Customer: " + customer5;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else if (mainArrangement5 == null || mainArrangement5.contains("Error")) {
				stepActual = "Error while creating " + product + " Arrangement for customer" + customer5;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

		}
		softVerify.assertAll();
	}

	@Test(priority = 7, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void createLoanInterestOnlyPayments(final String productGroup, final String product) {

		stepDescription = "Create Non-Revolving " + product + " Arrangement with Payment Type- Interest Only";
		stepExpected = " Non-Revolving " + product
				+ "  Arrangement with Payment Type- Interest Only Created successfully";

		if (customer6 == null || customer6.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			parentLimit = customerLimit(CREATE, "Non-Revolving Secured", "SAE Loans and Mortgages", product,
					customer6, "2500", "", limitFields, limitValues);

			limit = customerLimit(CREATE, "Non-Revolving Secured", "SAE Loans and Mortgages", product, customer6, "",
					"", limitFields, limitValues);
			limitReference = limit.substring(13, 17);
			limitSerial = limit.length() > 2 ? limit.substring(limit.length() - 2) : "";
			actualStep2Fields = defaultStep2Fields + "REVOLVING," + "LIMIT.REFERENCE," + "LIMIT.SERIAL,"
					+ "PAYMENT.TYPE:1," + "PAYMENT.METHOD:1," + "PROPERTY:1:1," + "BILL.TYPE:1," + "ACTUAL.AMT:1:1,"
					+ "PROPERTY:1<:2,";
			actualStep2Values = defaultStep2Values + "NO," + limitReference + "," + limitSerial + "," + "INTEREST.ONLY,"
					+ "Due," + "PRINCIPALINT," + "INSTALLMENT," + " ," + " ,";
			mainArrangement6 = arrangements(CREATE, productGroup, product, ROLEBASED_SAE, customer6,
					step1Fields, step1Values, actualStep2Fields, actualStep2Values);
			if (parentLimit == null || parentLimit.contains(ERROR)) {
				stepActual = "Error while creating parent Limit for Customer: " + customer6;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else if (limit == null || limit.contains(ERROR)) {
				stepActual = "Error while creating Limit for Customer: " + customer6;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else if (mainArrangement6 == null || mainArrangement6.contains("Error")) {
				stepActual = "Error while creating " + product + " Arrangement for customer" + customer6;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 8, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void createLoanCapitaliseInterests(final String productGroup, final String product) {

		stepDescription = "Create Non-Revolving " + product + " Arrangement with Payment Method - Capitalise Interests";
		stepExpected = "Non-Revolving " + product
				+ " Arrangement with Payment Method - Capitalise Interests created successfully";

		if (customer7 == null || customer7.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			parentLimit = customerLimit(CREATE, "Non-Revolving Secured", "SAE Loans and Mortgages", product,
					customer7, "2500", "", limitFields, limitValues);
			limit = customerLimit(CREATE, "Non-Revolving Secured", "SAE Loans and Mortgages", product, customer7, "",
					"", limitFields, limitValues);
			limitReference = limit.substring(13, 17);
			limitSerial = limit.length() > 2 ? limit.substring(limit.length() - 2) : "";
			actualStep2Fields = defaultStep2Fields + "REVOLVING," + "LIMIT.REFERENCE," + "LIMIT.SERIAL,"
					+ "PAYMENT.TYPE:1," + "PAYMENT.METHOD:1," + "PROPERTY:1:1," + "BILL.TYPE:1," + "ACTUAL.AMT:1:1,"
					+ "PROPERTY:1<:2,";
			actualStep2Values = defaultStep2Values + "NO," + limitReference + "," + limitSerial + "," + "INTEREST,"
					+ "Capitalise," + "PRINCIPALINT," + "INSTALLMENT," + " ," + " ,";
			mainArrangement7 = arrangements(CREATE, productGroup, product, ROLEBASED_SAE, customer7,
					step1Fields, step1Values, actualStep2Fields, actualStep2Values);
			if (parentLimit == null || parentLimit.contains(ERROR)) {
				stepActual = "Error while creating parent Limit for Customer: " + customer7;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else if (limit == null || limit.contains(ERROR)) {
				stepActual = "Error while creating Limit for Customer: " + customer7;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else if (mainArrangement7 == null || mainArrangement7.contains("Error")) {
				stepActual = "Error while creating " + product + " Arrangement for customer" + customer7;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}