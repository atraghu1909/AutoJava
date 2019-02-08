package testcases.PROC;

import org.testng.SkipException;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;
import testLibs.ArrangementData;
import testLibs.DefaultVariables;

public class PROC_OP_MG_XX013 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 2.0";
	private String customer;
	private String mainArrangement;

	@Test(priority = 1, enabled = true)
	@Parameters({ "branch", "customerType", "product" })
	public void preCondition(@Optional("B2B Branch 623") final String branch,
			@Optional(BUSINESS) final String customerType, final String product) {

		stepDescription = "Create a " + customerType + " customer and " + product + " arrangement";
		stepExpected = customerType + " customer and a " + product + " arrangement created successfully";

		if (loginResult) {
			switchToBranch(branch);

			customer = createDefaultCustomer(customerType, COMMERCIAL_LOANS, ROLEBASED_LENDING);

			if (customer == null || customer.contains(ERROR)) {
				stepActual = "Error while creating a " + customerType + " customer";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {
				final ArrangementData mortgageData = new ArrangementData("mortgageArrangement", COMMERCIAL_LOANS,
						product).withCustomers(customer, createdCustomers.get(customer), "", "100,", "100,")
								.withEffectiveDate("-2m")
								.withTerm("1Y")
								.withEscrow("NEW")
								.withDisbursement()
								.withRepayments()
								.withInsurance("Cmhc", "12345678", "None", "", "None", "")
								.build();

				mainArrangement = createDefaultArrangement(mortgageData);

				if (mainArrangement == null || mainArrangement.contains(ERROR)) {
					stepActual = "Error while creating " + product + " arrangement";
					softVerify.fail(stepActual);
					stepResult = StatusAs.FAILED;
				}
			}
		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Login failed";
			throw new SkipException(stepActual);
		}
		softVerify.assertAll();
	}

	@Test(priority = 2, enabled = true)
	@Parameters({ "product" })
	public void underwriting(final String product) {

		stepDescription = "Retrieve Information from Arrangement Overview for arrangement " + mainArrangement;
		stepExpected = "Information from Arrangement Overview for arrangement " + mainArrangement
				+ " retrieved successfully";

		String customerID;
		String totalDeliquentBalance;
		String term;
		String interestRate;
		String lastPaymentDate;
		String nextPaymentDate;
		String lendingRenewalPeriod;
		String lendingRenewalInitiation;
		String paymentScheduleFrequency;
		String scheduledPaymentAmount;
		String mortgageInsuranceNum;
		String collateralDetailsAddLine1;
		String collateralDetailsAddCity;
		String collateralDetailsAddPostalCode;
		String collateralDetailsAddState;

		if (mainArrangement == null || mainArrangement.contains(ERROR)) {
			stepResult = StatusAs.NOT_COMPLETED;
			stepActual = "Step not run, as Pre-Condition failed";
			throw new SkipException(stepActual);
		} else {

			findArrangement(AUTHORISED, mainArrangement, ARRANGEMENT, ROLEBASED_LENDING, COMMERCIAL_LOANS, product,
					CAD);

			customerID = versionScreen.labelElement("CIF").getText();
			totalDeliquentBalance = versionScreen.labelElement("Total Delinquent Balance").getText();
			term = versionScreen.versionElement("Commitment", "Term", "1").getText();
			interestRate = versionScreen.innerElement("6", "3").getText();
			lastPaymentDate = versionScreen.labelElement("Start Date").getText();
			nextPaymentDate = versionScreen.labelElement("Next Payment").getText();
			
			if (customerID == null || totalDeliquentBalance == null || term == null || interestRate == null
					|| nextPaymentDate == null || lastPaymentDate == null || "".equals(customerID) || "".equals(totalDeliquentBalance)
					|| "".equals(term) || "".equals(interestRate) || "".equals(nextPaymentDate) || "".equals(lastPaymentDate)) {
				stepActual = "Error while retrieving Basic Information from Arrangenment Overview";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			enquiryElements.enquiryButtons("View Arrangement").click();
			switchToPage(LASTPAGE, false);
			lendingRenewalPeriod = readTable.verifyActivity("Renewal Period").getText();
			lendingRenewalInitiation = readTable.verifyActivity("Initiation").getText();
			paymentScheduleFrequency = readTable.paymentScheduleFrequency("Payment.1").getText();
			scheduledPaymentAmount = readTable.scheduledPaymentAmount("Payment.1").getText();
			mortgageInsuranceNum = readTable.verifyActivity("Insurance Certification No").getText();

			if (lendingRenewalPeriod == null || lendingRenewalInitiation == null || paymentScheduleFrequency == null
					|| scheduledPaymentAmount == null || mortgageInsuranceNum == null || "".equals(lendingRenewalPeriod)
					|| "".equals(lendingRenewalInitiation) || "".equals(paymentScheduleFrequency)
					|| "".equals(scheduledPaymentAmount) || "".equals(mortgageInsuranceNum)) {
				stepActual = "Error while retrieving Information from View Arrangement Details";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}

			switchToPage("Arrangement Overview (Lending)", false);
			enquiryElements.enquiryButtons("View Collateral").click();
			switchToPage(LASTPAGE, false);

			collateralDetailsAddLine1 = readTable.verifyActivity("Ca Adr Line1").getText();
			collateralDetailsAddCity = readTable.verifyActivity("Town City").getText();
			collateralDetailsAddPostalCode = readTable.verifyActivity("Us State").getText();
			collateralDetailsAddState = readTable.verifyActivity("Ca Post Code").getText();

			if (collateralDetailsAddLine1 == null || collateralDetailsAddCity == null
					|| collateralDetailsAddPostalCode == null || collateralDetailsAddState == null
					|| "".equals(collateralDetailsAddLine1) || "".equals(collateralDetailsAddCity)
					|| "".equals(collateralDetailsAddPostalCode) || "".equals(collateralDetailsAddState)) {
				stepActual = "Error while retrieving Address for Collateral Details";
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
		softVerify.assertAll();
	}
}