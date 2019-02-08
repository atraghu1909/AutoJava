package testcases.DataGen;

import testLibs.ArrangementData;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class DataGen_createArrangementData extends testLibs.BaseTest_DataGen {

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "agentCommissionPlan", "advisorCommissionPlan", "owners", "customerType", "effectiveDate",
			"collateralValue", "term", "product", "collateralCode", "collateralOccupancy", "collateralNumberOfApts",
			"withSettleViaBanking", "productGroup", "insuranceCompany", "insuranceNumber", "commitment",
			"renewalPeriod", "marginSpread", "postedRate", "paymentFrequency", "paymentType", "withDisbursement",
			"withFixedPayment", "withIAD", "withEscrow", "withReserveAccount", "ownerRoles", "taxLiabilities",
			"limitAllocations", "lifeInsurance", "disabilityInsurance", "gds", "tds" })
	public void preCondition(@Optional("B2B Branch 623") final String branch,
			@Optional("Dummy Commission Plan") final String agentCommissionPlan,
			@Optional("Dummy Commission Plan") final String advisorCommissionPlan, @Optional("1") final String owners,
			@Optional(PERSONAL) final String customerType, @Optional("+0d") final String effectiveDate,
			@Optional("200000") final String collateralValue, @Optional("25Y") final String term,
			@Optional("") final String product, @Optional("1") final String collateralCode,
			@Optional("Owner") final String collateralOccupancy, @Optional("1") final String collateralNumberOfApts,
			@Optional("false") final String withSettleViaBanking, @Optional(RETAIL_MORTGAGES) final String productGroup,
			@Optional("Cmhc") final String insuranceCompany, @Optional("12345678") final String insuranceNumber,
			@Optional("100000") final String commitment, @Optional("1Y") final String renewalPeriod,
			@Optional("") final String marginSpread, @Optional("4.79") final String postedRate,
			@Optional("e0Y e1M e0W e0D e0F") final String paymentFrequency, @Optional("") final String paymentType,
			@Optional("true") final String withDisbursement, @Optional("false") final String withFixedPayment,
			@Optional("false") final String withIAD, @Optional("false") final String withEscrow,
			@Optional("false") final String withReserveAccount, @Optional("BORROWER.OWNER,") final String ownerRoles,
			@Optional("100.00,") final String taxLiabilities, @Optional("100.00,") final String limitAllocations,
			@Optional("None") final String lifeInsurance, @Optional("None") final String disabilityInsurance,
			@Optional("") final String gds, @Optional("") final String tds) {

		stepDescription = "Data Generation Request: " + product + " Arrangement";
		stepExpected = "Arrangement is created successfully";

		String mainArrangement;
		String customers = "NEW";
		final int ownerCount = Integer.parseInt(owners);
		
		for (int i = 1; i < ownerCount; i++) {
			customers += ",NEW";
		}

		ArrangementData arrangementData = new ArrangementData("mainArrangement", productGroup, product)
				.withCustomers(customers, null, ownerRoles, taxLiabilities, limitAllocations)
				.withEffectiveDate(effectiveDate)
				.withTerm(term)
				.withCommitmentAmount(commitment)
				.withCollateralValue(collateralValue)
				.withCollateralSpecs(collateralCode, "1", collateralOccupancy, collateralNumberOfApts)
				.withMarginSpread(marginSpread, postedRate)
				.withPaymentFrequency(paymentFrequency)
				.withRenewalPeriod(renewalPeriod);

		if (RETAIL_MORTGAGES.equals(productGroup) && !"Bridge Loan".equals(product)) {
			arrangementData = arrangementData.withGdsTds(gds, tds)
					.withInsurance(insuranceCompany, insuranceNumber, lifeInsurance, "100", disabilityInsurance, "100");
		}

		if (!"".equals(paymentType)) {
			arrangementData = arrangementData.withPaymentType(paymentType);
		}

		if ("true".equals(withDisbursement)) {
			arrangementData = arrangementData.withDisbursement();;
		}
		
		if ("true".equals(withEscrow)) {
			arrangementData = arrangementData.withEscrow(MUNICIPALITY);
		}

		if ("true".equals(withSettleViaBanking)) {
			arrangementData = arrangementData.withSettlement("Banking", "NEW");
		}

		if ("true".equals(withReserveAccount)) {
			arrangementData = arrangementData.withReserveAccount("NEW", true);
		}
		
		
		arrangementData = arrangementData.build();
		
		if (loginResult) {
			switchToBranch(branch);
			mainArrangement = createDefaultArrangement(arrangementData);

			if (mainArrangement == null || mainArrangement.contains(ERROR)) {
				stepActual = "Error while creating " + product + " Arrangement: " + mainArrangement;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
			else {
				stepActual = "Customer: " + arrangementData.getCustomers() + "; Arrangement: " + mainArrangement;
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Login failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
	}

}
