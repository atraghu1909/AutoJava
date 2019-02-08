package testcases.PROC;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PROC_OP_DS_XX002 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version: 3.2";
	private String mainArrangement;
	private String customer;
	private boolean result;
	private String retailArrangement;
	private String retailCustomer;
	private String fields;
	private String values;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "customerType", "productGroup", "product" })
	public void preCondition(@Optional("B2B Branch 607") final String branch, final String customerType,
			@Optional(BROKER_NONREG_DEPOSITS) final String productGroup, final String product) {

		stepDescription = "Create a default customer and arrangement";
		stepExpected = "Default customer and arrangement created successfully";

		if (loginResult) {

			switchToBranch(branch);
			customer = createDefaultCustomer(customerType, productGroup, ROLEBASED_BANKING);

			mainArrangement = createDefaultArrangement(productGroup, product, ROLEBASED_BANKING, customer, "+0d");

			if (customer == null || customer.contains(ERROR) || mainArrangement == null
					|| mainArrangement.contains(ERROR)) {
				stepActual = "Error while creating customer and arrangement";
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
	@Parameters({ "newProduct ", "productGroup" })
	public void updateArrangement(@Optional("Non-Redeemable Simple Semi-Annually") final String newProduct,
			@Optional(BROKER_NONREG_DEPOSITS) final String productGroup) {

		stepDescription = "Change product or Interest Frequency";
		stepExpected = "Product or Interest Frequency updated successfully";

		if (customer == null || customer.contains(ERROR) || mainArrangement == null
				|| mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			fields = "PRODUCT,";
			values = DefaultVariables.productCodes.get(newProduct) + ",";

			result = arrangementActivity(OPEN, "CHANGE.PRODUCT ACTIVITY FOR ARRANGEMENT", mainArrangement, productGroup,
					fields, values);
			multiInputData(fields, values, false);
			toolElements.toolsButton(VALIDATE_DEAL).click();
			toolElements.toolsButton(COMMIT_DEAL).click();

			if (!result) {
				stepActual = "Error while updating arrangement";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters({ "customerType", "productGroup", "product" })
	public void changePayoutInstructions(final String customerType, final String productGroup, final String product) {

		stepDescription = "Change Payout Instructions";
		stepExpected = "Payout Instructions changed successfully";
		String beneficiaryFields;
		String beneficiaryValues;
		String beneficiary;

		if (customer == null || customer.contains(ERROR) || mainArrangement == null
				|| mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {
			beneficiaryFields = DefaultVariables.beneficiaryFields + "OWNING.CUSTOMER," + "BEN.CUSTOMER,";
			beneficiaryValues = DefaultVariables.beneficiaryValues + customer + ","
					+ createdCustomers.get(customer).getCustomerName() + ",";

			beneficiary = beneficiaryCode(CREATE, "EFT Client", "", beneficiaryFields, beneficiaryValues);

			fields = "Advanced - Pay Out#PAYOUT.SETTLEMENT:1," + "Advanced - Pay Out#PAYOUT.PO.PRODUCT:1:1,"
					+ "Advanced - Pay Out#PAYOUT.BENEFICIARY:1:1," + "Advanced - Pay Out#PAYOUT.SETTLEMENT:2,"
					+ "Advanced - Pay Out#PAYOUT.PO.PRODUCT:2:1," + "Advanced - Pay Out#PAYOUT.BENEFICIARY:2:1,";
			values = "Yes,ACHDEBIT," + beneficiary + "," + "Yes," + "ACHDEBIT," + beneficiary + ",";

			result = arrangementActivity(CREATE, "UPDATE ACTIVITY FOR SETTLEMENT", mainArrangement, productGroup,
					fields, values);

			if (!result) {
				stepActual = "Error while changing payout instructions";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters({ "productGroup", "product" })
	public void changeRollover(final String productGroup, final String product) {

		String scheduleFields;
		String scheduleValues;

		stepDescription = "Change Rollover / Maturity Instructions";
		stepExpected = "Rollover / Maturity Instructions changed successfully";

		if (customer == null || customer.contains(ERROR) || mainArrangement == null
				|| mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			if (product.contains("Compound") || product.contains("Maturity")) {

				scheduleFields = "PAYMENT.METHOD:1," + "START.DATE:1:1,";
				scheduleValues = "Pay," + "R_RENEWAL +,";

			} else if (product.contains("Simple")) {

				scheduleFields = "";
				scheduleValues = "";
			} else {

				scheduleFields = "PAYMENT.METHOD:1," + "START.DATE:1:1,";
				scheduleValues = "Capitalise," + "R_RENEWAL +,";
			}

			fields = "TERM," + "MATURITY.DATE," + "CHANGE.DATE.TYPE," + "CHANGE.PERIOD," + "CHANGE.ACTIVITY,"
					+ "INITIATION.TYPE," + "CHANGE.DATE," + scheduleFields;
			values = "," + "," + "Period," + "3Y," + "DEPOSITS-ROLLOVER-ARRANGEMENT," + "Auto," + "+1y,"
					+ scheduleValues;

			result = arrangementActivity(CREATE_AUTHORISE, "CHANGE.TERM ACTIVITY FOR COMMITMENT", mainArrangement,
					productGroup, fields, values);

			if (!result) {
				stepActual = "Error while changing Rollover / Maturity Instructions for arrangement";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			softVerify.assertAll();
		}
	}

	@Test(priority = 5, enabled = true)
	@Parameters({ "customerType", "productGroup", "product", "retailBranch1", "retailBranch2" })
	public void changeLBCFSBranch(final String customerType, final String productGroup, final String product,
			@Optional("BLC Branch 673") final String retailBranch1,
			@Optional("BLC Branch 685") final String retailBranch2) {

		Pattern pattern;
		Matcher matcher;
		String branchNum = "";

		stepDescription = "Create retail arrangement and update BOB";
		stepExpected = "Arrangement created and BOB updated successfully";

		switchToBranch(retailBranch1);
		retailCustomer = createDefaultCustomer(customerType, productGroup, ROLEBASED_BANKING);

		retailArrangement = createDefaultArrangement(productGroup, product, ROLEBASED_BANKING, retailCustomer, "+0d");

		fields = "APPLICATION," + "CONTRACT.KEY," + "EFFECTIVE.DATE,";
		values = "ACCOUNT," + retailArrangement + "," + "+0d,";

		companyChange(CREATE_AUTHORISE, retailBranch2, fields, values);

		if ("BLC Branch 685".equals(retailBranch1) || "BLC Branch 685".equals(retailBranch2)
				|| "TLL Branch 376".equals(retailBranch1) || "TLL Branch 376".equals(retailBranch2)) {

			pattern = Pattern.compile("\\b[0-9][0-9][0-9]\\b");
			matcher = pattern.matcher(retailBranch2);

			if (matcher.find()) {
				branchNum = retailBranch2.substring(matcher.start(), matcher.end()).trim();
			}

			fields = "BOB,";
			values = branchNum + ",";

			result = arrangementActivity(CREATE, "Update BOB", retailArrangement, productGroup, fields, values);
		}

		if (!result) {
			stepActual = "Error while updating BOB";
			softVerify.fail(stepActual);
			stepResult = StatusAs.FAILED;
		}

		softVerify.assertAll();

	}

	@Test(priority = 6, enabled = true)
	@Parameters({ "productGroup", "product", "retailBranch1" })
	public void changeRolloverMature(final String productGroup, final String product,
			@Optional("BLC Branch 673") final String retailBranch1) {

		stepDescription = "Change Rollover / Maturity Instructions for retail arrangement";
		stepExpected = "Rollover / Maturity Instructions for retail arrangement changed successfully";

		if (retailArrangement == null || retailArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as failed to create retailArrangement";
			throw new SkipException(stepActual);
		} else {

			fields = "CHANGE.DATE.TYPE," + "CHANGE.PERIOD," + "INITIATION.TYPE," + "CHANGE.DATE," + "START.DATE:1:1,";
			values = "," + "," + "," + "," + "R_MATURITY +,";

			switchToBranch(retailBranch1);
			result = arrangementActivity(CREATE, "CHANGE.TERM ACTIVITY FOR COMMITMENT", retailArrangement, productGroup,
					fields, values);
		}

		if (!result) {
			stepActual = "Error while changing Rollover / Maturity Instructions for arrangement";
			softVerify.fail(stepActual);
			stepResult = StatusAs.FAILED;
		}

		softVerify.assertAll();

	}
}