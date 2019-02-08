package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PROC_SAE_XX003 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "2018-06-06";
	private String customer;
	private String limitReference;
	private String revolvingParentLimit;
	private String nonRevolvingParentLimit;
	private String customerLimit;
	private String fXLineLimit;
	private String lineOfCreditLimit;
	private String letterOfCreditLimit;
	private String letterGuaranteeLimit;
	private String bankersAcceptanceLimit;
	private String visaLimit;

	@Test(priority = 1, enabled = true)
	@Parameters({ "customerType", "product" })
	public void preCondition(final String customerType, final String product) {

		stepDescription = "Create " + customerType + " Customer";
		stepExpected = customerType + " Customer created successfully";

		if (loginResult) {

			switchToBranch("LAURENTIAN BANK - 523");

			customer = createDefaultCustomer(customerType, "", ROLEBASED_SAE);

			limitReference = DefaultVariables.revolvingLimitRefs.get(product);

			if (customer == null || customer.contains(ERROR)) {
				stepActual = "Error while creating " + customerType + " customer";
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
	@Parameters("product")
	public void createParentLimit(final String product) {

		stepDescription = "Create Parent Limit";
		stepExpected = "Parent Limit created successfully";

		String fields = "LIMIT.CURRENCY," + "INTERNAL.AMOUNT," + "FIXED.VARIABLE," + "EXPIRY.DATE,";
		String values = "CAD," + "500000," + "Fixed," + "+3y,";

		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			fields = "LIMIT.CURRENCY," + "INTERNAL.AMOUNT," + "FIXED.VARIABLE," + "EXPIRY.DATE,";
			values = "CAD," + "500000," + "FIXED," + "+3y,";

			revolvingParentLimit = customerLimit(CREATE_AUTHORISE, SECURED_CHILD, ROLEBASED_SAE, product, customer,
					"2400", "01", fields, values);

			if (revolvingParentLimit == null || revolvingParentLimit.contains(ERROR)) {
				stepActual = "Error while creating Revolving Parent Limit";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			fields = "LIMIT.CURRENCY," + "INTERNAL.AMOUNT," + "FIXED.VARIABLE," + "EXPIRY.DATE," + "L.LINKED.LIMIT,";
			values = "CAD," + "500000," + "FIXED," + "+3y," + revolvingParentLimit + ",";

			nonRevolvingParentLimit = customerLimit(CREATE_AUTHORISE, SECURED_CHILD, ROLEBASED_SAE, product, customer,
					"2500", "01", fields, values);

			if (nonRevolvingParentLimit == null || nonRevolvingParentLimit.contains(ERROR)) {
				stepActual = "Error while creating Non-Revolving Parent Limit";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters("product")
	public void createLimit(final String product) {
		stepDescription = "Create Limit";
		stepExpected = "Limit Created successfully";

		String fields = "LIMIT.CURRENCY," + "INTERNAL.AMOUNT," + "FIXED.VARIABLE," + "EXPIRY.DATE,";
		String values = "CAD," + "500000," + "FIXED," + "+3y,";

		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			customerLimit = customerLimit(CREATE_AUTHORISE, SECURED, ROLEBASED_SAE, product, customer, limitReference,
					"01", fields, values);

			if (customerLimit == null || customerLimit.contains(ERROR)) {
				stepActual = "Error while creating Limit";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters("product")
	public void modifyLimit(final String product) {

		stepDescription = "Modify Limit: " + customerLimit;
		stepExpected = "Limit: " + customerLimit + " modified successfully";

		String fields = "INTERNAL.AMOUNT," + "MAXIMUM.TOTAL,";
		String values = "20000," + "20000,";

		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			customerLimit = customerLimit(AMEND, SECURED, ROLEBASED_SAE, product, customer, limitReference, "01",
					fields, values);

			if (customerLimit == null || customerLimit.contains(ERROR)) {
				stepActual = "Error while modifying Limit: " + customerLimit;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 7, enabled = true)
	public void authoriseLimit() {

		stepDescription = "Authorise Limit: " + customer + ".000" + limitReference + "01";
		stepExpected = "Limit: " + customer + ".000" + limitReference + "01 authorised successfully";

		boolean result;

		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = authorizeEntity(customer + ".000" + limitReference + "01", LIMIT_SAE);

			if (!result) {
				stepActual = "Error while authorising Limit: " + customer + ".000" + limitReference + "01";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 8, enabled = true)
	@Parameters("product")
	public void collateralReverse(final String product) {

		stepDescription = "Reverse Collateral Details and Collateral Link: " + customer + ".1";
		stepExpected = "Collateral Details and Collateral Link: " + customer + ".1 reversed successfully";

		boolean result;

		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = reverseEntity(customer + ".1", COLLATERAL_DETAILS);

			if (!result) {
				stepActual = "Error while reversing Collateral Details: " + customer + ".1";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			result = reverseEntity(customer + ".1", COLLATERAL_LINK);

			if (!result) {
				stepActual = "Error while reversing Collateral Link: " + customer + ".1";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 9, enabled = true)
	@Parameters("product")
	public void limitReverse(final String product) {

		stepDescription = "Reverse Limit: " + customerLimit;
		stepExpected = "Limit: " + customerLimit + " reversed successfully";

		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			customerLimit = customerLimit(REVERSE, SECURED, ROLEBASED_SAE, product, customer, limitReference, "01", "",
					"");

			if (customerLimit == null || customerLimit.contains(ERROR)) {
				stepActual = "Error while reversing Limit: " + customerLimit;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 10, enabled = true)
	@Parameters("product")
	public void createShadowSubLimits(final String product) {

		stepDescription = "Create Shadow Sub Limits";
		stepExpected = "Shadow Sub Limits created successfully";

		String fields;
		String values;

		if (customer == null || customer.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			fields = "LIMIT.CURRENCY," + "INTERNAL.AMOUNT," + "FIXED.VARIABLE," + "EXPIRY.DATE,"
					+ "workarea%Shadow Limits%workarea#L.SL.SYSTEM:1,"
					+ "workarea%Shadow Limits%workarea#L.SL.CUS.ID:1,"
					+ "workarea%Shadow Limits%workarea#L.SL.UTIL.AMT:1,";
			values = "CAD," + "10000," + "FIXED," + "+3y," + "OBJ," + customer + "," + "0,";

			fXLineLimit = customerLimit(CREATE_AUTHORISE, SECURED_CHILD, ROLEBASED_SAE, "FX Line", customer, "", "01",
					fields, values);

			if (fXLineLimit == null || fXLineLimit.contains(ERROR)) {
				stepActual = "Error while creating Shadow Sub Limit for FX Line";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			fields = "LIMIT.CURRENCY," + "INTERNAL.AMOUNT," + "FIXED.VARIABLE," + "EXPIRY.DATE,"
					+ "workarea%Shadow Limits%workarea#L.SL.SYSTEM:1,"
					+ "workarea%Shadow Limits%workarea#L.SL.CUS.ID:1,"
					+ "workarea%Shadow Limits%workarea#L.SL.ACC.ID:1,"
					+ "workarea%Shadow Limits%workarea#L.SL.UTIL.AMT:1,";
			values = "CAD," + "10000," + "FIXED," + "+3y," + "CUL," + customer + "," + "9876543210," + "0,";

			lineOfCreditLimit = customerLimit(CREATE_AUTHORISE, SECURED_CHILD, ROLEBASED_SAE, "Line of Credit",
					customer, "", "01", fields, values);

			if (lineOfCreditLimit == null || lineOfCreditLimit.contains(ERROR)) {
				stepActual = "Error while creating Shadow Sub Limit for Line of Credit";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			fields = "LIMIT.CURRENCY," + "INTERNAL.AMOUNT," + "FIXED.VARIABLE," + "EXPIRY.DATE,"
					+ "workarea%Shadow Limits%workarea#L.SL.SYSTEM:1,"
					+ "workarea%Shadow Limits%workarea#L.SL.CUS.ID:1,"
					+ "workarea%Shadow Limits%workarea#L.SL.ACC.ID:1,"
					+ "workarea%Shadow Limits%workarea#L.SL.UTIL.AMT:1,";
			values = "CAD," + "10000," + "FIXED," + "+3y," + "BTRLC," + customer + "," + "9876543210," + "0,";

			letterOfCreditLimit = customerLimit(CREATE_AUTHORISE, SECURED_CHILD, ROLEBASED_SAE, "Letter of Credit",
					customer, "", "01", fields, values);

			if (letterOfCreditLimit == null || letterOfCreditLimit.contains(ERROR)) {
				stepActual = "Error while creating Shadow Sub Limit for Letter of Credit";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			fields = "LIMIT.CURRENCY," + "INTERNAL.AMOUNT," + "FIXED.VARIABLE," + "EXPIRY.DATE,"
					+ "workarea%Shadow Limits%workarea#L.SL.SYSTEM:1,"
					+ "workarea%Shadow Limits%workarea#L.SL.CUS.ID:1,"
					+ "workarea%Shadow Limits%workarea#L.SL.ACC.ID:1,"
					+ "workarea%Shadow Limits%workarea#L.SL.UTIL.AMT:1,";
			values = "CAD," + "10000," + "FIXED," + "+3y," + "BTRLG," + customer + "," + "9876543210," + "0,";

			letterGuaranteeLimit = customerLimit(CREATE_AUTHORISE, SECURED_CHILD, ROLEBASED_SAE, "Letter Guarantee",
					customer, "", "01", fields, values);

			if (letterGuaranteeLimit == null || letterGuaranteeLimit.contains(ERROR)) {
				stepActual = "Error while creating Shadow Sub Limit for Letter Guarantee";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			fields = "LIMIT.CURRENCY," + "INTERNAL.AMOUNT," + "FIXED.VARIABLE," + "EXPIRY.DATE,"
					+ "workarea%Shadow Limits%workarea#L.SL.SYSTEM:1,"
					+ "workarea%Shadow Limits%workarea#L.SL.CUS.ID:1,"
					+ "workarea%Shadow Limits%workarea#L.SL.ACC.ID:1,"
					+ "workarea%Shadow Limits%workarea#L.SL.UTIL.AMT:1,";
			values = "CAD," + "10000," + "FIXED," + "+3y," + "LUC," + customer + "," + "9876543210," + "0,";

			bankersAcceptanceLimit = customerLimit(CREATE_AUTHORISE, SECURED_CHILD, ROLEBASED_SAE, "Bankers Acceptance",
					customer, "", "01", fields, values);

			if (bankersAcceptanceLimit == null || bankersAcceptanceLimit.contains(ERROR)) {
				stepActual = "Error while creating Shadow Sub Limit for Bankers Acceptance";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			fields = "LIMIT.CURRENCY," + "INTERNAL.AMOUNT," + "FIXED.VARIABLE," + "EXPIRY.DATE,";
			values = "CAD," + "10000," + "FIXED," + "+3y,";

			visaLimit = customerLimit(CREATE_AUTHORISE, SECURED_CHILD, ROLEBASED_SAE, "Visa", customer, "", "01",
					fields, values);

			if (visaLimit == null || visaLimit.contains(ERROR)) {
				stepActual = "Error while creating Shadow Sub Limit for Visa";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 11, enabled = true)
	@Parameters("product")
	public void closeLimit(final String product) {

		stepDescription = "Close Limit: " + customerLimit;
		stepExpected = "Limit: " + customerLimit + " closed successfully";

		String fields = "EXPIRY.DATE,";
		String values = "+1d,";

		if (customer == null || visaLimit == null || customer.contains(ERROR) || visaLimit.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			customerLimit = customerLimit(AMEND_AUTHORISE, SECURED, ROLEBASED_SAE, "Visa", customer, "", "01", fields,
					values);

			if (customerLimit == null || customerLimit.contains(ERROR)) {
				stepActual = "Error while closing Limit: " + customerLimit;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 12, enabled = true)
	@Parameters("customerType")
	public void limitEnquiries(final String customerType) {

		stepDescription = "Verify the Limits for customer:" + customer;
		stepExpected = "Limits for customer:" + customer + " verified successfully";

		String limit;
		boolean result = false;

		if (fXLineLimit == null || lineOfCreditLimit == null || letterOfCreditLimit == null
				|| letterGuaranteeLimit == null || bankersAcceptanceLimit == null || revolvingParentLimit == null
				|| nonRevolvingParentLimit == null || nonRevolvingParentLimit.contains(ERROR)
				|| fXLineLimit.contains(ERROR) | lineOfCreditLimit.contains(ERROR)
				|| bankersAcceptanceLimit.contains(ERROR) | revolvingParentLimit.contains(ERROR)
				|| letterOfCreditLimit.contains(ERROR) | letterGuaranteeLimit.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			findCIF(customer, customerType, ROLEBASED_SAE);
			switchToTab("main%Limit/Collateral%workarea", "");

			limit = versionScreen.enquiry("ENQUIRY").getText();

			if (limit.contains(fXLineLimit) && limit.contains(lineOfCreditLimit) && limit.contains(letterOfCreditLimit)
					&& limit.contains(letterGuaranteeLimit) && limit.contains(bankersAcceptanceLimit)
					&& limit.contains(revolvingParentLimit)) {
				result = true;
			}

			commandLine("ENQ LBC.LINKED.LIMIT", commandLineAvailable);
			enquiryElements.enquirySearch("Customer Number", "", customer);

			if (!result) {
				stepActual = "Error while verifying limits for customer: " + customer;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

}
