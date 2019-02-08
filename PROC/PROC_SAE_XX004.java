package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.DefaultVariables;

public class PROC_SAE_XX004 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "2018-02-06";
	private String customer1;
	private String customer2;
	private String customer3;
	private String customer4;
	private String branch = "LAURENTIAN BANK - 523";
	private String customer1LimitId;
	private String customer2LimitId;
	private String customer3LimitId;
	private String customer4LimitId;
	private String collateralLink1;
	private String collateralLink2;
	private String collateralLink3;

	@Test(priority = 1, enabled = true)
	@Parameters({ "customerType", "productGroup", "product" })
	public void preCondition(final String customerType, final String productGroup, final String product) {

		final String defaultCIFFields = DefaultVariables.customerTypeFields.get(customerType);
		final String defaultCIFValues = DefaultVariables.customerTypeValues.get(customerType);
		final String limitFields = DefaultVariables.securedSAELimitFields;
		final String limitValues = DefaultVariables.securedSAELimitValues;

		stepDescription = "Create four " + customerType + " Customers and Customer Limit for " + product;
		stepExpected = "All Four Customers and Customer Limit created successfully";

		if (loginResult) {

			switchToBranch(branch);

			customer1 = customer(CREATE, customerType, ROLEBASED_SAE, defaultCIFFields, defaultCIFValues);
			customer2 = customer(CREATE, customerType, ROLEBASED_SAE, defaultCIFFields, defaultCIFValues);
			customer3 = customer(CREATE, customerType, ROLEBASED_SAE, defaultCIFFields, defaultCIFValues);
			customer4 = customer(CREATE, customerType, ROLEBASED_SAE, defaultCIFFields, defaultCIFValues);

			if (customer1 == null || customer1.contains(ERROR)) {
				stepActual = "Error while creating one or more customers";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {

				customerLimit(CREATE, SECURED_CHILD, ROLEBASED_SAE, product, customer1, "2400", "", limitFields,
						limitValues);
				customer1LimitId = customerLimit(CREATE, SECURED, ROLEBASED_SAE, product, customer1, "", "",
						limitFields, limitValues);
			}

			if (customer2 == null || customer2.contains(ERROR)) {
				stepActual = "Error while creating one or more customers";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {

				customerLimit(CREATE, SECURED_CHILD, ROLEBASED_SAE, product, customer2, "2400", "", limitFields,
						limitValues);
				customer2LimitId = customerLimit(CREATE, SECURED, ROLEBASED_SAE, product, customer2, "", "",
						limitFields, limitValues);
			}

			if (customer3 == null || customer3.contains(ERROR)) {
				stepActual = "Error while creating one or more customers";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {

				customerLimit(CREATE, SECURED_CHILD, ROLEBASED_SAE, product, customer3, "2400", "", limitFields,
						limitValues);

				customer3LimitId = customerLimit(CREATE, SECURED, ROLEBASED_SAE, product, customer3, "", "",
						limitFields, limitValues);
			}

			if (customer4 == null || customer4.contains(ERROR)) {
				stepActual = "Error while creating one or more customers";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {

				customerLimit(CREATE, SECURED_CHILD, ROLEBASED_SAE, product, customer4, "2400", "", limitFields,
						limitValues);

				customer4LimitId = customerLimit(CREATE, SECURED, ROLEBASED_SAE, product, customer4, "", "",
						limitFields, limitValues);
			}
			if (customer1LimitId == null || customer2LimitId == null || customer3LimitId == null
					|| customer4LimitId == null || customer1LimitId.contains(ERROR) || customer2LimitId.contains(ERROR)
					|| customer3LimitId.contains(ERROR) || customer4LimitId.contains(ERROR)) {
				stepActual = "Error while creating customer limit";
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
	public void createCollateralLink() {

		String collateralLinkFields;
		String collateralLinkValues;
		boolean partialSkip = false;
		boolean partialFailure = false;

		stepDescription = "Create Collateral Link";
		stepExpected = "Collateral Link Created successfully";

		if ((customer1LimitId == null || customer1LimitId.contains(ERROR))
				&& (customer2LimitId == null || customer2LimitId.contains(ERROR))
				&& (customer3LimitId == null || customer3LimitId.contains(ERROR))) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);

		} else {
			if (customer1LimitId == null || customer1LimitId.contains(ERROR)) {
				partialSkip = true;

			} else {

				collateralLinkFields = "COLLATERAL.CODE," + "LIMIT.REFERENCE:1,";
				collateralLinkValues = "326," + customer1LimitId + ",";

				collateralLink1 = collateral(CREATE_AUTHORISE, COLLATERAL_LINK, customer1, collateralLinkFields,
						collateralLinkValues);

				if (collateralLink1 == null || collateralLink1.contains(ERROR)) {
					partialFailure = true;
				}
			}
			if (customer2LimitId == null || customer2LimitId.contains(ERROR)) {
				partialSkip = true;

			} else {

				collateralLinkFields = "COLLATERAL.CODE," + "LIMIT.REFERENCE:1,";
				collateralLinkValues = "501," + customer2LimitId + ",";

				collateralLink2 = collateral(CREATE_AUTHORISE, COLLATERAL_LINK, customer2, collateralLinkFields,
						collateralLinkValues);

				if (collateralLink2 == null || collateralLink2.contains(ERROR)) {
					partialFailure = true;
				}
			}
			if (customer3LimitId == null || customer3LimitId.contains(ERROR)) {
				partialSkip = true;

			} else {

				collateralLinkFields = "COLLATERAL.CODE," + "LIMIT.REFERENCE:1," + "LIMIT.REFERENCE:2,";
				collateralLinkValues = "151," + customer3LimitId + "," + customer4LimitId + ",";

				collateralLink3 = collateral(CREATE_AUTHORISE, COLLATERAL_LINK, customer3, collateralLinkFields,
						collateralLinkValues);

				if (collateralLink3 == null || collateralLink3.contains(ERROR)) {
					partialFailure = true;
				}
			}
			if (partialSkip) {
				stepActual = "Not all customer limits were created successfully as a pre-condition";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else if (partialFailure) {
				stepActual = "Not all collateral links were created successfully";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 3, enabled = true)
	public void createCollateralDetail() {

		String collateralDetailFields;
		String collateralDetailValues;
		String collateral1;
		String collateral2;
		String collateral3;
		boolean partialSkip = false;
		boolean partialFailure = false;

		stepDescription = "Create Collateral Detail";
		stepExpected = "Collateral Link Detail Created successfully";

		if ((collateralLink1 == null || collateralLink1.contains(ERROR))
				&& (collateralLink2 == null || collateralLink2.contains(ERROR))
				&& (collateralLink3 == null || collateralLink3.contains(ERROR))) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);

		} else {

			if (collateralLink1 == null || collateralLink1.contains(ERROR)) {
				partialSkip = true;

			} else {

				collateralDetailFields = "Real Estate#COLLATERAL.TYPE," + "Real Estate#COLLATERAL.CODE,"
						+ "Real Estate#DESCRIPTION:1," + "Real Estate#CURRENCY," + "Real Estate#COUNTRY,"
						+ "Real Estate#NOMINAL.VALUE," + "Collateral details#COLL.VALUE,"
						+ "Collateral details#CA.ADR.LINE1," + "Collateral details#TOWN.CITY,"
						+ "Collateral details#US.STATE," + "Collateral details#CA.POST.CODE,"
						+ "Collateral details#L.COLL.BLD.UNTS," + "Collateral details#L.COLL.BLD.STRS,"
						+ "Collateral details#tab2:L.COLL.RENTAL," + "Collateral details#L.COLL.COMM.INC,"
						+ "Registration details#CA.REG.NUMBER," + "Registration details#CA.PPSA.EXP.DAT,"
						+ "Registration details#L.COLL.REG.TYPE:1," + "Registration details#L.COLL.P.REG.DT:1";

				collateralDetailValues = "325," + "326," + "TEST DESCRIPTION IN CAPS," + "CAD," + "CA," + "10000,"
						+ "10T," + "1234 TEMENOS STREET," + "MONTREAL," + "QC," + "H3H5Y9," + "1," + "0," + "No,"
						+ "50," + "TEST REF#," + "+20y," + "New," + "-1m";

				collateral1 = collateral(CREATE_AUTHORISE, COLLATERAL_DETAILS_REAL_ESTATE, collateralLink1,
						collateralDetailFields, collateralDetailValues);

				if (collateral1 == null || collateral1.contains(ERROR)) {
					partialFailure = true;
				}
			}

			if (collateralLink2 == null || collateralLink2.contains(ERROR)) {
				partialSkip = true;

			} else {

				collateralDetailFields = "Machinery#COLLATERAL.TYPE," + "Machinery#COLLATERAL.CODE,"
						+ "Machinery#DESCRIPTION:1," + "Machinery#CURRENCY," + "Machinery#COUNTRY,"
						+ "Machinery#NOMINAL.VALUE," + "Collateral details#CA.SG.MODEL,"
						+ "Collateral details#CA.SG.YEAR," + "Collateral details#CA.SG.SERIAL,"
						+ "Collateral details#COLL.DESC," + "Registration details#CA.PPSA.NUM,"
						+ "Registration details#CA.PPSA.EXP.DAT," + "Registration details#L.COLL.REG.DATE:1";

				collateralDetailValues = "500," + "501," + "TEST DESCRIPTION IN CAPS," + "CAD," + "CA," + "10000,"
						+ "Fiat," + "2005," + "123456789," + "Personal Vehicle," + "987654321," + "+20y," + "-1m";

				collateral2 = collateral(CREATE_AUTHORISE, COLLATERAL_DETAILS_MACHINERY, collateralLink2,
						collateralDetailFields, collateralDetailValues);

				if (collateral2 == null || collateral2.contains(ERROR)) {
					partialFailure = true;
				}
			}

			if (collateralLink3 == null || collateralLink1 == null || collateralLink3.contains(ERROR)
					|| collateralLink1.contains(ERROR)) {
				partialSkip = true;

			} else {

				collateralDetailFields = "Investment#COLLATERAL.TYPE," + "Investment#COLLATERAL.CODE,"
						+ "Investment#DESCRIPTION:1," + "Investment#CURRENCY," + "Investment#COUNTRY,"
						+ "Investment#NOMINAL.VALUE";
				collateralDetailValues = "150," + "151," + "TEST DESCRIPTION IN CAPS / GUARANTOR BOB," + "CAD," + "CA,"
						+ "10000";

				collateral(CREATE_AUTHORISE, COLLATERAL_DETAILS_INVESTMENT, collateralLink3, collateralDetailFields,
						collateralDetailValues);

				collateralDetailFields = "Real Estate#COLLATERAL.TYPE," + "Real Estate#COLLATERAL.CODE,"
						+ "Real Estate#DESCRIPTION:1," + "Real Estate#CURRENCY," + "Real Estate#COUNTRY,"
						+ "Real Estate#NOMINAL.VALUE," + "Collateral details#COLL.VALUE,"
						+ "Collateral details#CA.ADR.LINE1," + "Collateral details#TOWN.CITY,"
						+ "Collateral details#US.STATE," + "Collateral details#CA.POST.CODE,"
						+ "Collateral details#L.COLL.BLD.UNTS," + "Collateral details#L.COLL.BLD.STRS,"
						+ "Collateral details#tab2:L.COLL.RENTAL," + "Collateral details#L.COLL.COMM.INC,"
						+ "Registration details#CA.REG.NUMBER," + "Registration details#CA.PPSA.EXP.DAT,"
						+ "Registration details#L.COLL.REG.TYPE:1," + "Registration details#L.COLL.P.REG.DT:1,";
				collateralDetailValues = "325," + "326," + "ANOTHER TEST," + "CAD," + "CA," + "10000," + "10T,"
						+ "4321 TEMENOS STREET," + "GATINEAU," + "QC," + "H3H5Y9," + "1," + "0," + "No," + "50,"
						+ "TEST REF#," + "+20y," + "New," + "-1m,";

				collateral3 = collateral(CREATE, COLLATERAL_DETAILS_REAL_ESTATE, collateralLink1,
						collateralDetailFields, collateralDetailValues);
				authorizeEntity(collateral3, COLLATERAL);

				if (collateral3 == null || collateral3.contains(ERROR)) {
					partialFailure = true;
				}
			}
			if (partialSkip) {
				stepActual = "Not all collateral Links were created successfully as a pre-condition";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else if (partialFailure) {
				stepActual = "Not all collateral were created successfully";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

	@Test(priority = 4, enabled = true)
	public void expireCollateralLink() {

		String expiredLinkFields;
		String expiredLinkValues;
		String collateralAmended;

		stepDescription = "Expire Collateral Link " + collateralLink1;
		stepExpected = "Collateral Link " + collateralLink1 + " expired successfully";

		if (collateralLink1 == null || collateralLink1.contains(ERROR)) {

			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as failed to create collateral link";
			throw new SkipException(stepActual);
		} else {

			expiredLinkFields = "Collateral Link#EXPIRY.DATE";
			expiredLinkValues = "+1d";

			collateralAmended = collateral(AMEND, COLLATERAL_LINK, collateralLink1, expiredLinkFields,
					expiredLinkValues);

			if (collateralAmended.contains(ERROR)) {

				stepActual = "Error while amending collateral ";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}

}
