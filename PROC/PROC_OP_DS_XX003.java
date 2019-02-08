package testcases.PROC;

import org.openqa.selenium.support.ui.Select;
import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.DefaultVariables;

public class PROC_OP_DS_XX003 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version: 3.3";
	private String nominee1;
	private String nominee2;
	private String arrangementExternal;
	private String arrangementInternal;
	private String arrangementSplitFull;
	private String arrangementSplitPartial;
	private String arrangementLegacySimple;
	private String arrangementLegacyCompound;
	private String newArrangement1;
	private String newArrangement2;
	private ArrangementData newGIC1;
	private ArrangementData newGIC2;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "productGroup", "productSimple", "productCompound" })
	public void preCondition(@Optional("B2B Branch 607") final String branch,
			@Optional(BROKER_NONREG_DEPOSITS) final String productGroup, final String productSimple,
			final String productCompound) {

		stepDescription = "Create two nominee, five Simple and one Compound deposits";
		stepExpected = "Two nominee, five Simple and one Compound deposits created successfully";

		if (loginResult) {

			switchToBranch(branch);

			nominee1 = createNominee("-13m");

			nominee2 = createNominee("-13m");

			if (nominee1 == null || nominee2 == null || nominee1.contains(ERROR) || nominee2.contains(ERROR)) {
				stepActual = "Error while creating Nominee";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			ArrangementData gic1Data = new ArrangementData("gic1", productGroup, productSimple)
					.withCustomers(nominee1, createdCustomers.get(nominee1), "", "100,", "100,")
					.withEffectiveDate("-1m").withFunding().build();
			arrangementExternal = createDefaultArrangement(gic1Data);

			ArrangementData gic2Data = new ArrangementData("gic2", productGroup, productSimple)
					.withCustomers(nominee1, createdCustomers.get(nominee1), "", "100,", "100,")
					.withEffectiveDate("-1m").withFunding().build();
			arrangementInternal = createDefaultArrangement(gic2Data);

			ArrangementData gic3Data = new ArrangementData("gic3", productGroup, productSimple)
					.withCustomers(nominee1, createdCustomers.get(nominee1), "", "100,", "100,")
					.withEffectiveDate("-1m").withFunding().build();
			arrangementSplitFull = createDefaultArrangement(gic3Data);

			ArrangementData gic4Data = new ArrangementData("gic4", productGroup, productSimple)
					.withCustomers(nominee1, createdCustomers.get(nominee1), "", "100,", "100,")
					.withEffectiveDate("-1m").withFunding().build();
			arrangementSplitPartial = createDefaultArrangement(gic4Data);

			ArrangementData gic5Data = new ArrangementData("gic5", productGroup, productSimple)
					.withCustomers(nominee2, createdCustomers.get(nominee2), "", "100,", "100,")
					.withEffectiveDate("-2m").withFunding().build();
			arrangementLegacySimple = createDefaultArrangement(gic5Data);

			ArrangementData gic6Data = new ArrangementData("gic6", productGroup, productCompound)
					.withCustomers(nominee2, createdCustomers.get(nominee2), "", "100,", "100,")
					.withEffectiveDate("-13m").withMaturityInstructions("Interest Only").withFunding().build();
			arrangementLegacyCompound = createDefaultArrangement(gic6Data);

			if (arrangementExternal == null || arrangementInternal == null || arrangementSplitFull == null
					|| arrangementSplitPartial == null || arrangementLegacySimple == null
					|| arrangementLegacyCompound == null || arrangementExternal.contains(ERROR)
					|| arrangementInternal.contains(ERROR) || arrangementSplitFull.contains(ERROR)
					|| arrangementSplitPartial.contains(ERROR) || arrangementLegacySimple.contains(ERROR)
					|| arrangementLegacyCompound.contains(ERROR)) {
				stepActual = "Error while creating " + productGroup + " deposit";
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
	@Parameters("productGroup")
	public void external(@Optional(BROKER_NONREG_DEPOSITS) final String productGroup) {

		stepDescription = "Perform external GIC re-registration for arrangement: " + arrangementExternal;
		stepExpected = "External GIC re-registration for arrangement: " + arrangementExternal
				+ " performed successfully";

		String fields = "CUSTOMER:1,";
		String values = nominee2 + ",";
		Select dropdown;
		boolean result;

		if (arrangementExternal == null || arrangementExternal.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementActivity(OPEN, "CHANGE ACTIVITY FOR CUSTOMER", arrangementExternal, productGroup,
					fields, values);

			result = multiInputData(fields, values, false);
			toolElements.toolsButton(VALIDATE_DEAL).click();
			fields = "TAX.LIABILITY.PERC:1," + "LIMIT.ALLOC.PERC:1," + "GL.ALLOC.PERC:1,";
			values = "100.00," + "100.00," + "100.00,";
			result = multiInputData(fields, values, false) && inputTable.commitAndOverride();

			if (!result) {
				stepActual = "Error while performing Nominee external re-registration for arrangement: "
						+ arrangementExternal;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			fields = "Commitment$SECURITY.NO:1," + "Commitment$NAME:1,";
			values = "SJ78571," + "JOHN DOE,";
			result = arrangementActivity(OPEN, "RENEGOTIATE ACTIVITY FOR ARRANGEMENT", arrangementExternal,
					productGroup, fields, values);
			dropdown = new Select(enquiryElements.activityMessaging("DEPOSITS-REDEEM-ARRANGEMENT"));
			dropdown.selectByVisibleText("No");
			result = multiInputData(fields, values, false) && inputTable.commitAndOverride();

			if (!result) {
				stepActual = "Error while performing External GIC re-registration for arrangement: "
						+ arrangementExternal;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	@Parameters("productGroup")
	public void Internal(@Optional(BROKER_NONREG_DEPOSITS) final String productGroup) {

		stepDescription = "Perform internal GIC re-registration for arrangement: " + arrangementInternal;
		stepExpected = "Internal GIC re-registration for arrangement: " + arrangementInternal
				+ " performed successfully";

		String fields = "Commitment$SECURITY.NO:1," + "Commitment$NAME:1,";
		String values = "SJ78571," + "JOHN DOE,";
		Select dropdown;
		boolean result;

		if (arrangementInternal == null || arrangementInternal.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementActivity(OPEN, "RENEGOTIATE ACTIVITY FOR ARRANGEMENT", arrangementInternal,
					productGroup, fields, values);
			dropdown = new Select(enquiryElements.activityMessaging("DEPOSITS-REDEEM-ARRANGEMENT"));
			dropdown.selectByVisibleText("No");
			result = multiInputData(fields, values, false) && inputTable.commitAndOverride();

			if (!result) {
				stepActual = "Error while performing internal GIC re-registration for arrangement: "
						+ arrangementInternal;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	@Parameters({ "productGroup", "newProduct" })
	public void interimLegacySimpleInternalWithSplit(@Optional(BROKER_NONREG_DEPOSITS) final String productGroup,
			@Optional("Non-Redeemable Simple Semi-Annually") final String newProduct) {

		stepDescription = "Perform Interim Legacy Simple Internal GIC re-registration for arrangement: "
				+ arrangementLegacySimple;
		stepExpected = "Interim Legacy Simple Internal GIC re-registration for arrangement: " + arrangementLegacySimple
				+ " performed successfully";

		String fields = "NEW.BAL.AMT:2:1,";
		String values = "00.00,";
		boolean result;

		if (arrangementLegacySimple == null || nominee2 == null || arrangementLegacySimple.contains(ERROR)
				|| nominee2.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementActivity(CREATE_AUTHORISE, "ADJUST.BALANCE ACTIVITY FOR BALANCE.MAINTENANCE",
					arrangementLegacySimple, productGroup, fields, values);

			if (!result) {
				stepActual = "Error while adjusting balance for simple pay GIC: " + arrangementLegacySimple;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			fields = "CLOSURE.REASON," + "Advanced - Pay Out#PAYOUT.SETTLEMENT:1,"
					+ "Advanced - Pay Out#PAYOUT.ACCOUNT:1:1," + "Advanced - Pay Out#PAYOUT.BENEFICIARY:1:1,"
					+ "Advanced - Pay Out#PAYOUT.PO.PRODUCT:1:1,";
			values = "Bulk Split," + "Yes," + "CAD11102," + " ," + " ,";
			result = arrangementActivity(CREATE_AUTHORISE, "REDEEM ACTIVITY FOR ARRANGEMENT", arrangementLegacySimple,
					productGroup, fields, values);

			if (!result) {
				stepActual = "Error while redeeming GIC: " + arrangementLegacySimple;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			newGIC1 = new ArrangementData("newArrangement1", productGroup, newProduct)
					.withCustomers(nominee2, createdCustomers.get(nominee2), "", "100,", "100,")
					.withReason("MASTER GIC " + arrangementLegacySimple).withFunding().build();
			newArrangement1 = createDefaultArrangement(newGIC1);

			newGIC2 = new ArrangementData("newArrangement2", productGroup, newProduct)
					.withCustomers(nominee2, createdCustomers.get(nominee2), "", "100,", "100,")
					.withReason("MASTER GIC " + arrangementLegacySimple).withFunding().build();
			newArrangement2 = createDefaultArrangement(newGIC2);

			if (newArrangement1 == null || newArrangement2 == null || newArrangement1.contains(ERROR)
					|| newArrangement2.contains(ERROR)) {
				stepActual = "Error while creating " + newProduct + " deposit for nominee: " + nominee2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 5, enabled = true)
	@Parameters({ "productGroup", "newProduct" })
	public void interimLegacyCompoundInternalWithSplit(@Optional(BROKER_NONREG_DEPOSITS) final String productGroup,
			@Optional("Non-Redeemable Simple Semi-Annually") final String newProduct) {

		stepDescription = "Perform Interim Legacy Compound Internal GIC re-registration for arrangement: "
				+ arrangementLegacyCompound;
		stepExpected = "Interim Legacy Compound Internal GIC re-registration for arrangement: "
				+ arrangementLegacyCompound + " performed successfully";

		String fields = "NEW.BAL.AMT:2:1,";
		String values = "105.73,";
		boolean result;

		if (arrangementLegacyCompound == null || nominee2 == null || arrangementLegacyCompound.contains(ERROR)
				|| nominee2.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementActivity(CREATE_AUTHORISE, "ADJUST.BALANCE ACTIVITY FOR BALANCE.MAINTENANCE",
					arrangementLegacyCompound, productGroup, fields, values);

			if (!result) {
				stepActual = "Error while adjusting balance for simple pay GIC: " + arrangementLegacyCompound;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			fields = "CLOSURE.REASON," + "Advanced - Pay Out#PAYOUT.SETTLEMENT:1,"
					+ "Advanced - Pay Out#PAYOUT.ACCOUNT:1:1," + "Advanced - Pay Out#PAYOUT.BENEFICIARY:1:1,"
					+ "Advanced - Pay Out#PAYOUT.PO.PRODUCT:1:1," + "Advanced - Pay Out#PAYOUT.SETTLEMENT:2,"
					+ "Advanced - Pay Out#PAYOUT.ACCOUNT:2:1," + "Advanced - Pay Out#PAYOUT.BENEFICIARY:2:1,"
					+ "Advanced - Pay Out#PAYOUT.PO.PRODUCT:2:1,";
			values = "Bulk Split," + "Yes," + "CAD11102," + " ," + " ," + "Yes," + "CAD11102," + " ," + " ,";
			result = arrangementActivity(CREATE_AUTHORISE, "REDEEM ACTIVITY FOR ARRANGEMENT", arrangementLegacyCompound,
					productGroup, fields, values);

			if (!result) {
				stepActual = "Error while redeeming GIC: " + arrangementLegacyCompound;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			newGIC1 = new ArrangementData("newArrangement1", productGroup, newProduct)
					.withCustomers(nominee2, createdCustomers.get(nominee2), "", "100,", "100,")
					.withReason("MASTER GIC " + arrangementLegacyCompound).withFunding().build();
			newArrangement1 = createDefaultArrangement(newGIC1);

			newGIC2 = new ArrangementData("newArrangement2", productGroup, newProduct)
					.withCustomers(nominee2, createdCustomers.get(nominee2), "", "100,", "100,")
					.withReason("MASTER GIC " + arrangementLegacyCompound).withFunding().build();
			newArrangement2 = createDefaultArrangement(newGIC2);

			if (newArrangement1 == null || newArrangement2 == null || newArrangement1.contains(ERROR)
					|| newArrangement2.contains(ERROR)) {
				stepActual = "Error while creating " + newProduct + " deposit for nominee: " + nominee2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 6, enabled = true)
	@Parameters({ "productGroup", "newProduct" })
	public void fullRedemptionInternalWithSplit(@Optional(BROKER_NONREG_DEPOSITS) final String productGroup,
			@Optional("Non-Redeemable Simple Semi-Annually") final String newProduct) {

		stepDescription = "Perform Full Redemption Internal GIC re-registration for arrangement: "
				+ arrangementSplitFull;
		stepExpected = "Full Redemption Internal GIC re-registration for arrangement: " + arrangementSplitFull
				+ " performed successfully";

		String fields = "CLOSURE.REASON," + "Advanced - Pay Out#PAYOUT.SETTLEMENT:1,"
				+ "Advanced - Pay Out#PAYOUT.ACCOUNT:1:1," + "Advanced - Pay Out#PAYOUT.BENEFICIARY:1:1,"
				+ "Advanced - Pay Out#PAYOUT.PO.PRODUCT:1:1," + "Advanced - Pay Out#PAYOUT.SETTLEMENT:2,"
				+ "Advanced - Pay Out#PAYOUT.ACCOUNT:2:1," + "Advanced - Pay Out#PAYOUT.BENEFICIARY:2:1,"
				+ "Advanced - Pay Out#PAYOUT.PO.PRODUCT:2:1,";
		String values = "Bulk Split," + "Yes," + "CAD11102," + " ," + " ," + "Yes," + "CAD11102," + " ," + " ,";
		boolean result;

		if (arrangementSplitFull == null || arrangementSplitFull.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			result = arrangementActivity(CREATE_AUTHORISE, "REDEEM ACTIVITY FOR ARRANGEMENT", arrangementSplitFull,
					productGroup, fields, values);

			if (!result) {
				stepActual = "Error while redeeming GIC: " + arrangementSplitFull;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			newGIC1 = new ArrangementData("newArrangement1", productGroup, newProduct)
					.withCustomers(nominee2, createdCustomers.get(nominee2), "", "100,", "100,")
					.withReason("MASTER GIC " + arrangementSplitFull).withFunding().build();
			newArrangement1 = createDefaultArrangement(newGIC1);

			newGIC2 = new ArrangementData("newArrangement2", productGroup, newProduct)
					.withCustomers(nominee2, createdCustomers.get(nominee2), "", "100,", "100,")
					.withReason("MASTER GIC " + arrangementSplitFull).withFunding().build();
			newArrangement2 = createDefaultArrangement(newGIC2);

			if (newArrangement1 == null || newArrangement2 == null || newArrangement1.contains(ERROR)
					|| newArrangement2.contains(ERROR)) {
				stepActual = "Error while creating " + newProduct + " deposit for nominee: " + nominee2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 7, enabled = true)
	@Parameters({ "productGroup", "newProduct" })
	public void partialRedemptionInternalWithSplit(@Optional(BROKER_NONREG_DEPOSITS) final String productGroup,
			@Optional("Non-Redeemable Simple Semi-Annually") final String newProduct) {

		stepDescription = "Debiting master GIC: " + arrangementSplitPartial + " for subsequent child GIC purchase";
		stepExpected = "Master GIC: " + arrangementSplitPartial
				+ " for subsequent child GIC purchase debited successfully";

		String fields = "DEBIT.ACCT.NO," + "DEBIT.CURRENCY," + "DEBIT.AMOUNT," + "CREDIT.ACCT.NO,";
		String values = arrangementSplitPartial + "," + CAD + "," + "50000," + "CAD11102,";
		String financialTransaction;

		if (nominee2 == null || arrangementSplitPartial == null || nominee2.contains(ERROR)
				|| arrangementSplitPartial.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			financialTransaction = financialTransaction(CREATE_AUTHORISE, "Deposit Partial Redemption - No Clawback",
					fields, values);
			if (financialTransaction == null || financialTransaction.contains(ERROR)) {
				stepActual = "Error while Debiting master GIC: " + arrangementSplitPartial
						+ " for subsequent child GIC purchase";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			newGIC1 = new ArrangementData("newArrangement1", productGroup, newProduct)
					.withCustomers(nominee2, createdCustomers.get(nominee2), "", "100,", "100,")
					.withReason("MASTER GIC " + arrangementSplitPartial).withFunding("50000").build();
			newArrangement1 = createDefaultArrangement(newGIC1);

			if (newArrangement1 == null || newArrangement1.contains(ERROR)) {
				stepActual = "Error while creating " + newProduct + " deposit for nominee: " + nominee2;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}
