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

public class DataGen_createArrangement extends testLibs.BaseTest_DataGen {

	private String customer1 = "";
	private String customer2 = "";
	private String mainArrangement = "";
	private String step1Fields = "";
	private String step1Values = "";
	private String cheqStep1Fields = "";
	private String cheqStep1Values = "";
	private String step2Fields = "";
	private String step2Values = "";
	private String defaultStep2Fields = "";
	private String defaultStep2Values = "";
	private String arrangementDetails = "";
	private StringBuilder step2FieldsBuilder;
	private StringBuilder step2ValuesBuilder;
	private final StringBuilder step1FieldsBuilder = new StringBuilder(step1Fields);
	private final StringBuilder step1ValuesBuilder = new StringBuilder(step1Values);
	private final StringBuilder cheqStep1FieldsBuilder = new StringBuilder(cheqStep1Fields);
	private final StringBuilder cheqStep1ValuesBuilder = new StringBuilder(cheqStep1Values);
	private final StringBuilder arrangementDetailsBuilder = new StringBuilder(arrangementDetails);

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup", "product", "quantity", "effectiveDate", "status", "amortization", "renewalTerm",
			"branch", "authorizeFlag", "commitment", "principal", "escrowPayee" })
	public void step1(final String productGroup, final String product, final String quantity,
			@Optional("") final String effectiveDate, @Optional("") final String status,
			@Optional("25Y") final String amortization, @Optional("5Y") final String renewalTerm,
			@Optional("Default") final String branch, @Optional("true") final String authorizeFlag,
			@Optional("") final String commitment, @Optional("") final String principal,
			@Optional("") final String escrowPayee) {
		final int iterations = Integer.valueOf(quantity);
		final Random randomizer = new Random();
		final boolean trainingVariations = false;
		String collateralFields = "";
		String collateralValues = "";
		String amendCIFFields = "";
		String amendCIFValues = "";
		String arrangementType = "";
		String customerType;
		String maturityInstruction;
		String agentRequiredValues;
		String actualStatus = status;
		String actualEffectiveDate = effectiveDate;
		String arrangementBanking = "";
		String benePrefProd = "";
		String accountNumber = "";
		String beneficiary;
		String problem;
		String updateActivity = "";
		String acitivityUpdateFields = "";
		String acitivityUpdateValues = "";
		String reserveAccount;
		String actualCommitment = commitment;
		int randomChanceRoll;
		boolean authorizeFlagBoolean = false;
		String activity = "";
		String[] fieldsValues;
		String beneficiaryFields;
		String beneficiaryValues;

		final StringBuilder collateralFieldsBuilder = new StringBuilder(collateralFields);
		final StringBuilder collateralValuesBuilder = new StringBuilder(collateralValues);

		if (!"Default".equals(branch)) {
			switchToBranch(branch);
		}

		authorizeFlagBoolean = "true".equalsIgnoreCase(authorizeFlag);

		customerType = DefaultVariables.productGroupCustomerType.get(productGroup);

		if (DefaultVariables.productFields.containsKey(product)) {
			defaultStep2Fields = DefaultVariables.productFields.get(product);
			defaultStep2Values = DefaultVariables.productValues.get(product);
		} else {
			defaultStep2Fields = DefaultVariables.productGroupFields.get(productGroup);
			defaultStep2Values = DefaultVariables.productGroupValues.get(productGroup);
		}
		arrangementType = DefaultVariables.productGroupArrangementType.get(productGroup);

		step2FieldsBuilder = new StringBuilder(defaultStep2Fields);
		step2ValuesBuilder = new StringBuilder(defaultStep2Values);
		step2Fields = step2FieldsBuilder.toString();
		step2Values = step2ValuesBuilder.toString();

		stepDescription = "Create " + quantity + " " + status + " " + product + " Arrangements";
		stepExpected = "All Arrangements are created successfully";

		for (int i = 1; i <= iterations; i++) {

			try {

				// Status
				if ("".equals(status)) {
					switch (arrangementType) {
					default:
					case "Banking":
						break;
					case "Deposit":
						break;
					case "Lending":
						randomChanceRoll = randomizer.nextInt(100);
						if (iterations > 20) {
							randomChanceRoll = (i + randomChanceRoll) * 5 % 100;
						}
						if (randomChanceRoll < 5) {
							actualStatus = "Not Disbursed";
						} else if (randomChanceRoll < 10) {
							actualStatus = "Delinquent";
							actualEffectiveDate = "-3m";
						} else if (randomChanceRoll < 15) {
							actualStatus = "Delinquent via Cheque";
							arrangementDetailsBuilder.append("[Cheque Issued]");
							arrangementDetails = arrangementDetailsBuilder.toString();
							actualEffectiveDate = "-3m";
						} else if (randomChanceRoll < 20) {
							actualStatus = "Paid Out";
						} else if (randomChanceRoll < 25) {
							actualStatus = "Rewritten";
						} else if (randomChanceRoll < 30) {
							actualStatus = "Small Balance";
						} else if (randomChanceRoll < 65) {
							actualStatus = "Current via Cheque";
							arrangementDetailsBuilder.append("[Cheque Issued][Cheque Applied]");
							arrangementDetails = arrangementDetailsBuilder.toString();
						} else if (randomChanceRoll < 75) {
							actualStatus = "RTP";
						} else {
							randomChanceRoll = randomizer.nextInt(100);
							if (randomChanceRoll < 45) {
								actualStatus = "Current";
							} else if (randomChanceRoll < 90) {
								actualStatus = "Current with NSF Fee posted";
							} else {
								actualStatus = "Current with NSF Fee posted and reversed";
							}
							actualEffectiveDate = "-3m";
						}
						arrangementDetailsBuilder.append('[').append(actualStatus).append(']');
						arrangementDetails = arrangementDetailsBuilder.toString();

						break;
					case "Mortgage":
						break;
					}
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

				// Ownership
				randomChanceRoll = randomizer.nextInt(100);
				if (iterations > 4) {
					randomChanceRoll = (i + randomChanceRoll) * 25 % 100;
				}

				if (randomChanceRoll < 75 || !trainingVariations) {
					arrangementDetailsBuilder.append("[Single Owner]");
					arrangementDetails = arrangementDetailsBuilder.toString();
					customer1 = createDefaultCustomer(customerType, productGroup, ROLEBASED_BANKING);
				} else {
					arrangementDetailsBuilder.append("[Multi Owner]");
					arrangementDetails = arrangementDetailsBuilder.toString();
					customer1 = createDefaultCustomer(customerType, productGroup, ROLEBASED_BANKING);
					customer2 = createDefaultCustomer(customerType, productGroup, ROLEBASED_BANKING);

					if ("Lending".equals(arrangementType) || "Mortgages".equals(arrangementType)) {
						step1FieldsBuilder.append("CUSTOMER+:2,CUSTOMER.ROLE:1,CUSTOMER.ROLE:2,");
						step1Fields = step1FieldsBuilder.toString();
						step1ValuesBuilder.append(customer2).append(",OWNER,COBORROWER,");
						step1Values = step1ValuesBuilder.toString();

						step2FieldsBuilder.append("Customer#TAX.LIABILITY.PERC:1,Customer#TAX.LIABILITY.PERC:2,");
						step2Fields = step2FieldsBuilder.toString();
						step2ValuesBuilder.append("50.00,50.00,");
						step2Values = step2ValuesBuilder.toString();
					} else {
						step1FieldsBuilder.append("CUSTOMER+:2,CUSTOMER.ROLE:1,CUSTOMER.ROLE:2,");
						step1Fields = step1FieldsBuilder.toString();
						step1ValuesBuilder.append(customer2).append(",OWNER,BENEFICIARY,");
						step1Values = step1ValuesBuilder.toString();
					}

					cheqStep1FieldsBuilder.append("CUSTOMER+:2,CUSTOMER.ROLE:1,CUSTOMER.ROLE:2,");
					cheqStep1Fields = cheqStep1FieldsBuilder.toString();
					cheqStep1ValuesBuilder.append(customer2).append(",OWNER,BENEFICIARY,");
					cheqStep1Values = cheqStep1ValuesBuilder.toString();

					if (RETAIL_MORTGAGES.equals(productGroup) && !product.contains("HELOC")) {
						step2FieldsBuilder.append("Insurance#LIFE.INS.FLAG:2,Insurance#DISABILITY.FLAG:2,");
						step2Fields = step2FieldsBuilder.toString();
						step2ValuesBuilder.append("Rejected.By.Insurance,Rejected.By.Insurance,");
						step2Values = step2ValuesBuilder.toString();
					}
				}

				beneficiaryFields = DefaultVariables.beneficiaryFields + "OWNING.CUSTOMER," + "BEN.CUSTOMER,";
				beneficiaryValues = DefaultVariables.beneficiaryValues + customer1 + ","
						+ createdCustomers.get(customer1).getCustomerName() + ",";

				// Client Contact Number Update
				randomChanceRoll = randomizer.nextInt(100);
				if (iterations > 4) {
					randomChanceRoll = (i + randomChanceRoll) * 25 % 100;
				}
				if (randomChanceRoll < 25 && trainingVariations) {
					final Random generator = new Random();
					int num0;
					int num1;
					int num2;
					num0 = generator.nextInt(899) + 100;
					num1 = generator.nextInt(899) + 100;
					num2 = generator.nextInt(8999) + 1000;
					final String number0 = Integer.toString(num0);
					final String number1 = Integer.toString(num1);
					final String number2 = Integer.toString(num2);
					final String number = number0 + "-" + number1 + "-" + number2;

					amendCIFFields = "Communication Details#PHONE.1:1";
					amendCIFValues = number;
					amendCIF(AMEND, customer1, customerType, ROLEBASED_BANKING, amendCIFFields, amendCIFValues);
					arrangementDetailsBuilder.append("[Phone Update:").append(number).append(']');
					arrangementDetails = arrangementDetailsBuilder.toString();
				}

				if (!"Banking".equals(arrangementType)) {
					// Amount

					if ("".equals(commitment)) {
						randomChanceRoll = randomizer.nextInt(100000) + 100000;
						actualCommitment = Integer.toString(randomChanceRoll);
					}
					step2FieldsBuilder.append("AMOUNT,");
					step2Fields = step2FieldsBuilder.toString();
					step2ValuesBuilder.append(actualCommitment).append(',');
					step2Values = step2ValuesBuilder.toString();
					arrangementDetailsBuilder.append("[Amount: " + actualCommitment + "]");
					arrangementDetails = arrangementDetailsBuilder.toString();

					if (status.contains("via B2B Chequing")) {
						accountNumber = arrangements("Create and Authorise", PERSONAL_ACCOUNTS, PERSONAL_CHEQUING,
								ROLEBASED_BANKING, customer1, cheqStep1Fields, cheqStep1Values,
								DefaultVariables.bankingFields, DefaultVariables.bankingValues);
					}

				}

				// Primary Officer
				if (trainingVariations) {
					randomChanceRoll = randomizer.nextInt(10) + 10;
					arrangementDetailsBuilder.append("[Primary Officer " + randomChanceRoll + "]");
					arrangementDetails = arrangementDetailsBuilder.toString();
					step2FieldsBuilder.append("PRIMARY.OFFICER,");
					step2Fields = step2FieldsBuilder.toString();
					step2ValuesBuilder.append(randomChanceRoll).append(',');
					step2Values = step2ValuesBuilder.toString();
				}

				// Reserve Account
				randomChanceRoll = randomizer.nextInt(100);
				if (iterations > 10) {
					randomChanceRoll = (i + randomChanceRoll) * 10 % 100;
				}
				if (randomChanceRoll < 10 && trainingVariations) {
					reserveAccount = arrangements("Create and Authorise", SERVICING_ACCOUNTS, RESERVE_ACCOUNTS,
							ROLEBASED_BANKING, customer1, step1Fields, step1Values,
							DefaultVariables.reserveAccountFields, DefaultVariables.reserveAccountValues);
					step2FieldsBuilder.append("L.LINKED.REF.ID,");
					step2Fields = step2FieldsBuilder.toString();
					step2ValuesBuilder.append(reserveAccount).append(',');
					step2Values = step2ValuesBuilder.toString();
				}

				// Agent
				randomChanceRoll = randomizer.nextInt(100);
				if (iterations > 5) {
					randomChanceRoll = (i + randomChanceRoll) * 20 % 100;
				}

				if (randomChanceRoll < 80 && trainingVariations) {
					agentRequiredValues = setUpAgent(productGroup, "", product, actualEffectiveDate);

					step1FieldsBuilder.append(DefaultVariables.agentRequiredFields).append(',');
					step1Fields = step1FieldsBuilder.toString();
					step1ValuesBuilder.append(agentRequiredValues).append(',');
					step1Values = step1ValuesBuilder.toString();

					step2FieldsBuilder.append("Lending Commission$FIXED.AMOUNT:1:1,");
					step2Fields = step2FieldsBuilder.toString();
					step2ValuesBuilder.append("5000,");
					step2Values = step2ValuesBuilder.toString();

					arrangementDetailsBuilder.append("[Agent: ")
							.append(agentRequiredValues.substring(0, agentRequiredValues.indexOf(','))).append(']');
					arrangementDetails = arrangementDetailsBuilder.toString();
				} else {
					// B2B Branch 817: 200000868 - AA17333JFSBN
					// B2B Branch 623: 100000460 - AA17304GSXQF
					// agentRequiredValues = "200000868,AA17333JFSBN,Broker,";

					// step1FieldsBuilder.append(agentRequiredFields).append(',');
					// step1Fields = step1FieldsBuilder.toString();
					// step1ValuesBuilder.append(agentRequiredValues).append(',');
					// step1Values = step1ValuesBuilder.toString();

					// step2FieldsBuilder.append("Lending
					// Commission$FIXED.AMOUNT:1:1,");
					// step2Fields = step2FieldsBuilder.toString();
					// step2ValuesBuilder.append("5000,");
					// step2Values = step2ValuesBuilder.toString();

					// arrangementDetailsBuilder.append("[Agent: 200000868]");
					// arrangementDetails =
					// arrangementDetailsBuilder.toString();
				}

				// Notes
				randomChanceRoll = randomizer.nextInt(100);
				if (iterations > 5) {
					randomChanceRoll = (i + randomChanceRoll) * 20 % 100;
				}
				if (randomChanceRoll < 25 && trainingVariations) {
					step2FieldsBuilder.append("NOTES:1,");
					step2Fields = step2FieldsBuilder.toString();
					step2ValuesBuilder.append("Test Note,");
					step2Values = step2ValuesBuilder.toString();

					arrangementDetailsBuilder.append("[Arrangement created with Note]");
					arrangementDetails = arrangementDetailsBuilder.toString();
				}

				// Client Contact Log
				randomChanceRoll = randomizer.nextInt(100);
				if (iterations > 4) {
					randomChanceRoll = (i + randomChanceRoll) * 25 % 100;
				}

				if (randomChanceRoll < 10 && trainingVariations) {
					final String contactLogFields = "CONTACT.CHANNEL,CONTACT.DESC,CONTACT.DATE,CONTACT.TIME";
					final String contactLogValues = "BRANCH,Test Note,+0D,11:45";

					customerIntervention(customer1, "Create", contactLogFields, contactLogValues, customerType);

					arrangementDetailsBuilder.append("[Created Contact Log]");
					arrangementDetails = arrangementDetailsBuilder.toString();
				}

				// Banking Information
				if (trainingVariations) {
					randomChanceRoll = randomizer.nextInt(100);
					if (iterations > 4) {
						randomChanceRoll = (i + randomChanceRoll) * 25 % 100;
					}
					if (randomChanceRoll < 25) {
						arrangementBanking = "Internal";
					} else if (randomChanceRoll < 50) {
						arrangementBanking = "Beneficiary";
						benePrefProd = "CHQ";
					} else if (randomChanceRoll < 75) {
						arrangementBanking = "Beneficiary";
						benePrefProd = "ACH";
					} else {
						arrangementBanking = "Beneficiary";
						benePrefProd = "ACOTHER";
					}

					arrangementDetailsBuilder.append('[').append(benePrefProd).append(arrangementBanking).append(']');
					arrangementDetails = arrangementDetailsBuilder.toString();
				} else {
					if (status.contains("via Cheque")) {
						arrangementBanking = "Beneficiary";
						benePrefProd = "CHQ";
					} else if (status.contains("via EFT")) {
						arrangementBanking = "Beneficiary";
						benePrefProd = "ACHCREDIT";
					} else {
						arrangementBanking = "";
					}
				}

				switch (arrangementType) {
				default:
				case "Banking":
					mainArrangement = dataGenCreateBanking(customer1, productGroup, product, CAD, step1Fields,
							step1Values, step2Fields, step2Values, status);
					break;
				case "Deposit":
					// Maturity Instructions
					if (i % 2 == 0) {
						maturityInstruction = "Redeem";
						arrangementDetailsBuilder.append("[Redeem upon Maturity]");
						arrangementDetails = arrangementDetailsBuilder.toString();
					} else {
						maturityInstruction = "Rollover";
						arrangementDetailsBuilder.append("[Rollover upon Maturity]");
						arrangementDetails = arrangementDetailsBuilder.toString();
					}

					// Deposit Banking Information
					if (!"".equals(arrangementBanking)) {
						step2FieldsBuilder.append(
								"Advanced - Pay Out#PAYOUT.SETTLEMENT:1,Advanced - Pay Out#PAYOUT.SETTLEMENT:2,");
						step2Fields = step2FieldsBuilder.toString();
						step2ValuesBuilder.append("Yes,Yes,");
						step2Values = step2ValuesBuilder.toString();
					}

					if ("Internal".equalsIgnoreCase(arrangementBanking)) {
						step2FieldsBuilder
								.append("Advanced - Pay Out#PAYOUT.ACCOUNT:1:1,Advanced - Pay Out#PAYOUT.ACCOUNT:2:1,");
						step2Fields = step2FieldsBuilder.toString();
						step2ValuesBuilder.append(accountNumber).append(',').append(accountNumber).append(',');
						step2Values = step2ValuesBuilder.toString();
					} else {
						beneficiary = beneficiaryCode(CREATE, "EFT Client", "", beneficiaryFields, beneficiaryValues);
						step2FieldsBuilder.append("Advanced - Pay Out#PAYOUT.PO.PRODUCT:1:1,")
								.append("Advanced - Pay Out#PAYOUT.BENEFICIARY:1:1,")
								.append("Advanced - Pay Out#PAYOUT.PO.PRODUCT:2:1,")
								.append("Advanced - Pay Out#PAYOUT.BENEFICIARY:2:1,");

						step2Fields = step2FieldsBuilder.toString();
						step2ValuesBuilder.append(benePrefProd).append(',').append(beneficiary).append(',')
								.append(benePrefProd).append(',').append(beneficiary).append(',');
						step2Values = step2ValuesBuilder.toString();
					}

					mainArrangement = dataGenCreateDeposit(customer1, productGroup, product, CAD, step1Fields,
							step1Values, step2Fields, step2Values, status, actualEffectiveDate, maturityInstruction);
					break;
				case "Lending":

					// Amortization
					step2FieldsBuilder.append("TERM,");
					step2Fields = step2FieldsBuilder.toString();
					step2ValuesBuilder.append(amortization).append(',');
					step2Values = step2ValuesBuilder.toString();

					// Account Info
					fieldsValues = updateAccountInfo().split("@");

					step2FieldsBuilder.append(fieldsValues[0]);
					step2Fields = step2FieldsBuilder.toString();
					step2ValuesBuilder.append(fieldsValues[1]);
					step2Values = step2ValuesBuilder.toString();

					// Loan Banking Details
					if (!"".equals(arrangementBanking)) {
						step2FieldsBuilder.append("Advanced - Pay In#PAYIN.SETTLEMENT:1,");
						step2Fields = step2FieldsBuilder.toString();
						step2ValuesBuilder.append("Yes,");
						step2Values = step2ValuesBuilder.toString();
					}

					if ("Internal".equalsIgnoreCase(arrangementBanking)) {
						step2FieldsBuilder.append(
								"Advanced - Pay Out#PAYOUT.SETTLEMENT:1,Advanced - Pay In#PAYIN.ACCOUNT:1:1,Advanced - Pay Out#PAYOUT.ACCOUNT:1:1,");
						step2Fields = step2FieldsBuilder.toString();
						step2ValuesBuilder.append("Yes,").append(accountNumber).append(',').append(accountNumber)
								.append(',');
						step2Values = step2ValuesBuilder.toString();
					} else {
						beneficiary = beneficiaryCode(CREATE, "EFT Client", "", beneficiaryFields, beneficiaryValues);
						Reporter.log("Created Beneficiary::" + beneficiary, true);

						step2FieldsBuilder.append("Advanced - Pay In#PAYIN.PO.PRODUCT:1:1,")
								.append("Advanced - Pay In#PAYIN.BENEFICIARY:1:1,")
								.append("Advanced - Pay In#PAYIN.AC.DB.RULE:1,");
						step2Fields = step2FieldsBuilder.toString();
						step2ValuesBuilder.append(benePrefProd).append(',').append(beneficiary).append(",Full,");
						step2Values = step2ValuesBuilder.toString();
					}

					// Collateral with Admin Code
					randomChanceRoll = randomizer.nextInt(100);
					if (iterations > 4) {
						randomChanceRoll = (i + randomChanceRoll) * 25 % 100;
					}
					if (randomChanceRoll < 25 && trainingVariations) {
						step2FieldsBuilder.append("LIMIT.TYPE,");
						step2Fields = step2FieldsBuilder.toString();
						step2ValuesBuilder.append("SECURED,");
						step2Values = step2ValuesBuilder.toString();

						String adminCodeDescription;
						final int adminCode = randomizer.nextInt(10);
						switch (adminCode) {
						default:
						case 0:
							adminCodeDescription = "B2b Investment Loan";
							break;
						case 1:
							adminCodeDescription = "Canada Life";
							break;
						case 2:
							adminCodeDescription = "London Life";
							break;
						case 3:
							adminCodeDescription = "Ven Only Vengrowth Only";
							break;
						case 4:
							adminCodeDescription = "Vmbl-laurentian Bk S";
							break;
						case 5:
							adminCodeDescription = "Non-rsp Non-rsp";
							break;
						case 6:
							adminCodeDescription = "Nbcn Counsel";
							break;
						case 7:
							adminCodeDescription = "Industrial Alliance";
							break;
						case 8:
							adminCodeDescription = "Gmp Securities L.p.";
							break;
						case 9:
							adminCodeDescription = "Fid/echelon Wealth";
							break;
						}
						collateralFieldsBuilder.append("Registration details#L.COLL.ADM.CODE,");
						collateralValuesBuilder.append(adminCodeDescription).append(',');
						collateralFields = collateralFieldsBuilder.toString();
						collateralValues = collateralValuesBuilder.toString();
						arrangementDetailsBuilder.append("[Admin Code: ").append(adminCodeDescription).append(']');
						arrangementDetails = arrangementDetailsBuilder.toString();
					}

					// Collateral with Pledge Account Number
					randomChanceRoll = randomizer.nextInt(100);
					if (iterations > 4) {
						randomChanceRoll = (i + randomChanceRoll) * 25 % 100;
					}
					if (randomChanceRoll < 25 && trainingVariations) {
						step2FieldsBuilder.append("LIMIT.TYPE,");
						step2Fields = step2FieldsBuilder.toString();
						step2ValuesBuilder.append("SECURED,");
						step2Values = step2ValuesBuilder.toString();
						collateralValuesBuilder.append("123456,");
						collateralFieldsBuilder.append("Registration details#L.COLL.EXT.NO,");
						collateralFields = collateralFieldsBuilder.toString();
						collateralValues = collateralValuesBuilder.toString();
						collateral("Amend and Authorise", "Collateral Details - Loans", customer1 + ".1.1",
								collateralFields, collateralValues);
						arrangementDetailsBuilder.append("[Pledge Account Number]");
						arrangementDetails = arrangementDetailsBuilder.toString();
					}

					// Collateral with PPSA
					randomChanceRoll = randomizer.nextInt(100);
					if (iterations > 20) {
						randomChanceRoll = (i + randomChanceRoll) * 5 % 100;
					}
					String ppsaValue = "";

					if (randomChanceRoll < 15) {
						ppsaValue = "Amendment Registration";
					} else if (randomChanceRoll < 25) {
						ppsaValue = "Corp Search Registration";
					} else if (randomChanceRoll < 40) {
						ppsaValue = "Discharge Registration";
					} else if (randomChanceRoll < 55) {
						ppsaValue = "New Registration";
					} else if (randomChanceRoll < 70) {
						ppsaValue = "PPSA Search Registration";
					} else if (randomChanceRoll < 85) {
						ppsaValue = "Renewal Registration";
					}

					if (randomChanceRoll < 85 && trainingVariations) {
						step2FieldsBuilder.append("LIMIT.TYPE,");
						step2Fields = step2FieldsBuilder.toString();
						step2ValuesBuilder.append("SECURED,");
						step2Values = step2ValuesBuilder.toString();
						collateralFieldsBuilder.append("Registration details#CA.PPSA.NUM,");
						collateralValuesBuilder.append(ppsaValue).append(',');
						collateralFields = collateralFieldsBuilder.toString();
						collateralValues = collateralValuesBuilder.toString();
						arrangementDetailsBuilder.append("[PPSA: ").append(ppsaValue).append(']');
						arrangementDetails = arrangementDetailsBuilder.toString();
					}

					// Create Loan
					mainArrangement = dataGenCreateLoan(customer1, productGroup, product, CAD, step1Fields, step1Values,
							step2Fields, step2Values, actualStatus, effectiveDate);

					// Update Loan Collateral
					if (!"".equals(collateralFields)) {
						collateral("Amend and Authorise", "Collateral Details", customer1 + ".1.1", collateralFields,
								collateralValues);
					}

					// Ad-Hoc Payment Schedule
					randomChanceRoll = randomizer.nextInt(100);
					if (iterations > 10) {
						randomChanceRoll = (i + randomChanceRoll) * 10 % 100;
					}

					if (randomChanceRoll < 10) {
						if (i % 2 == 0 && trainingVariations) {

							collateralFields = "PAYMENT.TYPE+:3,PAYMENT.METHOD:3,PAYMENT.FREQ:3,PROPERTY:3:1,DUE.FREQ:3:1,BILL.TYPE:3,ACTUAL.AMT:3:1";
							collateralValues = "SPECIAL,e0Y e0M e0W e1D e0F,ACCOUNT,e0Y e0M e0W e1D e0F,INSTALLMENT,10000";

							arrangementActivity(CREATE_AUTHORISE, "CHANGE ACTIVITY FOR SCHEDULE", mainArrangement,
									productGroup, collateralFields, collateralValues);

							arrangementDetailsBuilder.append("[Multiple Payments - Same Banking Info]");
							arrangementDetails = arrangementDetailsBuilder.toString();
						} else if (trainingVariations) {

							collateralFields = "DEBIT.ACCOUNT,PAYMENT.ORDER.PRODUCT,DEBIT.CCY,PAYMENT.CURRENCY,PAYMENT.AMOUNT";
							collateralValues = mainArrangement + ",ACHDEBIT,CAD,CAD,5000";

							createAdHocPayment("Create", "EFT", mainArrangement, collateralFields, collateralValues);

							arrangementDetailsBuilder.append("[Multiple Payments - Different Banking Info]");
							arrangementDetails = arrangementDetailsBuilder.toString();
						}
					}

					// NSF Fees
					String fields = "FIXED.AMOUNT,";
					String values = "100,";
					if (actualStatus.contains("NSF Fee")) {
						arrangementActivity(CREATE_AUTHORISE, "Change and Capitalise ALNSFFEE fee", mainArrangement,
								productGroup, fields, values);
						arrangementDetailsBuilder.append("[NSF Fee posted]");
						arrangementDetails = arrangementDetailsBuilder.toString();
						if (actualStatus.contains("reversed")) {
							arrangementActivity(REVERSE_AUTHORISE, "Change and Capitalise ALNSFFEE fee",
									mainArrangement, productGroup, "", "");
							arrangementDetailsBuilder.append("[NSF Fee reversed]");
							arrangementDetails = arrangementDetailsBuilder.toString();
						}
					}

					// Loan Memo
					randomChanceRoll = randomizer.nextInt(100);
					if (iterations > 10) {
						randomChanceRoll = (i + randomChanceRoll) * 10 % 100;
					}
					if (randomChanceRoll < 10 && trainingVariations) {
						arrangementMemo("Create", mainArrangement, productGroup, "MEMO.TEXT", "Test Note");
						arrangementDetailsBuilder.append("[Arrangement updated with Memo]");
						arrangementDetails = arrangementDetailsBuilder.toString();
					}

					// Request Payoff
					randomChanceRoll = randomizer.nextInt(100);
					if (iterations > 10) {
						randomChanceRoll = (i + randomChanceRoll) * 10 % 100;
					}
					if (randomChanceRoll < 10 && "Current".equals(actualStatus) && trainingVariations) {
						requestPayOff(mainArrangement, productGroup);
						arrangementDetailsBuilder.append("[Payoff Requested. Interest: ").append(payoffInterest)
								.append(" Amount: ").append(payoffAmount).append(']');
						arrangementDetails = arrangementDetailsBuilder.toString();
					}

					// Update Activity
					randomChanceRoll = randomizer.nextInt(100);

					if (iterations > 20) {
						randomChanceRoll = (i + randomChanceRoll) * 5 % 100;
					}

					if (randomChanceRoll < 5) {
						updateActivity = "AGENT";
					} else if (randomChanceRoll < 10) {
						updateActivity = "OFFICER";
					} else if (randomChanceRoll < 15) {
						updateActivity = "SCHEDULE";
					} else if (randomChanceRoll < 20) {
						updateActivity = "ACCOUNT INFO";
					} else if (randomChanceRoll < 25) {
						updateActivity = "CHANGE CUSTOMER";
					}

					if (!"".equals(updateActivity) && trainingVariations) {

						switch (updateActivity) {

						case "AGENT":
							actualEffectiveDate = "-3m";

							activity = "UPDATE ACTIVITY FOR AL.COMMISSION";
							acitivityUpdateFields = "AGENT.ID:1," + "AGENT.ARR.ID:1," + "AGENT.ROLE:1,";
							acitivityUpdateValues = setUpAgent(productGroup, "", product, actualEffectiveDate);
							break;

						case "OFFICER":
							activity = "UPDATE ACTIVITY FOR OFFICERS";
							randomChanceRoll = randomizer.nextInt(10) + 10;
							acitivityUpdateFields = "PRIMARY.OFFICER,";
							acitivityUpdateValues = randomChanceRoll + ",";
							break;

						case "SCHEDULE":
							activity = "CHANGE ACTIVITY FOR SCHEDULE";
							acitivityUpdateFields = "ACTUAL.AMT:2:1,";
							acitivityUpdateValues = "11000,";
							break;

						case "ACCOUNT INFO":
							fieldsValues = updateAccountInfo().split("@");

							activity = "UPDATE ACTIVITY FOR ACCOUNT";
							acitivityUpdateFields = fieldsValues[0];
							acitivityUpdateValues = fieldsValues[1];
							break;

						case "CHANGE CUSTOMER":
							customer2 = createDefaultCustomer(customerType, productGroup, ROLEBASED_BANKING);

							activity = "CHANGE ACTIVITY FOR CUSTOMER";
							acitivityUpdateFields = "CUSTOMER+:2,CUSTOMER.ROLE:1,CUSTOMER.ROLE:2,";
							acitivityUpdateValues = customer2 + ",OWNER,JOINT.OWNER,";
							break;

						}

						arrangementActivity(CREATE_AUTHORISE, activity, mainArrangement, productGroup,
								acitivityUpdateFields, acitivityUpdateValues);

						arrangementDetailsBuilder.append('[').append(updateActivity).append(']');
						arrangementDetails = arrangementDetailsBuilder.toString();

					}

					break;
				case "Mortgages":

					if (!"Bridge Loan".equals(product) && !product.contains("HELOC")) {
						step2FieldsBuilder.append("TERM,CHANGE.PERIOD,");
						step2Fields = step2FieldsBuilder.toString();
						step2ValuesBuilder.append(amortization).append(',').append(renewalTerm).append(',');
						step2Values = step2ValuesBuilder.toString();
					}

					// Mortgage Banking Details
					if (!"".equals(arrangementBanking)) {
						step2FieldsBuilder
								.append("Advanced - Pay In#PAYIN.SETTLEMENT:1,Advanced - Pay Out#PAYOUT.SETTLEMENT:1,");
						step2Fields = step2FieldsBuilder.toString();
						step2ValuesBuilder.append("Yes,Yes,");
						step2Values = step2ValuesBuilder.toString();
					}

					if ("Internal".equalsIgnoreCase(arrangementBanking)) {
						step2FieldsBuilder
								.append("Advanced - Pay In#PAYIN.ACCOUNT:1:1,Advanced - Pay Out#PAYOUT.ACCOUNT:1:1,");
						step2Fields = step2FieldsBuilder.toString();
						step2ValuesBuilder.append(accountNumber).append(',').append(accountNumber).append(',');
						step2Values = step2ValuesBuilder.toString();
					} else if ("Beneficiary".equalsIgnoreCase(arrangementBanking)) {
						beneficiary = beneficiaryCode(CREATE, "EFT Client", "", beneficiaryFields, beneficiaryValues);
						Reporter.log("Created Beneficiary::" + beneficiary, true);

						step2FieldsBuilder.append(
								"Advanced - Pay In#PAYIN.PO.PRODUCT:1:1,Advanced - Pay In#PAYIN.BENEFICIARY:1:1,Advanced - Pay Out#PAYOUT.PO.PRODUCT:1:1,Advanced - Pay Out#PAYOUT.BENEFICIARY:1:1,Advanced - Pay In#PAYIN.ACTIVITY:1:1,Advanced - Pay Out#PAYOUT.ACTIVITY:1:1,");
						step2Fields = step2FieldsBuilder.toString();
						step2ValuesBuilder.append(benePrefProd).append(',').append(beneficiary).append(',')
								.append(benePrefProd).append(',').append(beneficiary).append(", , ,");
						step2Values = step2ValuesBuilder.toString();
					}

					mainArrangement = dataGenCreateMortgage(customer1, productGroup, product, CAD, step1Fields,
							step1Values, step2Fields, step2Values, status, actualEffectiveDate, principal, escrowPayee);

					// Mortgage Memo
					randomChanceRoll = randomizer.nextInt(100);
					if (iterations > 10) {
						randomChanceRoll = (i + randomChanceRoll) * 10 % 100;
					}
					if (randomChanceRoll < 10 && trainingVariations && !mainArrangement.contains("Error")) {
						arrangementMemo("Create", mainArrangement, productGroup, "MEMO.TEXT", "Test Note");
					}

					// Mortgage PAP Credit
					randomChanceRoll = randomizer.nextInt(100);
					if (iterations > 10) {
						randomChanceRoll = (i + randomChanceRoll) * 10 % 100;
					}

					if (randomChanceRoll < 10 && trainingVariations && !mainArrangement.contains("Error")) {
						createPAPCredit(mainArrangement, productGroup, product, "Fixed");
					}

					break;
				}

				printResultsAndReset();
				if (mainArrangement.contains("Error")) {
					softVerify.fail(mainArrangement + " " + arrangementDetails);
					stepResult = StatusAs.FAILED;
				}

			} catch (ElementNotVisibleException | NoSuchElementException e) {
				try {
					problem = "Iteration " + i + ": " + versionScreen.errorMessage().getText()
							+ " Continue with next iteration";
					stepActual += problem;
					versionScreen.errorMessage().click();
					Reporter.log(e.getMessage(), false);
				} catch (NoSuchElementException e2) {
					problem = "Exception caught in iteration " + i + ". Continue with next iteration";
					stepActual += problem;
					Reporter.log(e.getMessage(), false);
				}
				stepResult = StatusAs.FAILED;
				softVerify.fail(problem);
				Reporter.log(problem, debugMode);
				printResultsAndReset();
				login();
				switchToBranch(branch);
			}
		}
		softVerify.assertAll();
	}

	public void printResultsAndReset() {
		stepActual += System.lineSeparator();
		if (arrangementDetails.contains("Multi Owner")) {
			Reporter.log("Customers: " + customer1 + ", " + customer2 + ", Arrangement: " + mainArrangement + " "
					+ arrangementDetails, debugMode);
			stepActual += "Customers: " + customer1 + ", " + customer2 + ", Arrangement: " + mainArrangement + " "
					+ arrangementDetails;
		} else {
			Reporter.log("Customer: " + customer1 + ", Arrangement: " + mainArrangement + " " + arrangementDetails,
					debugMode);
			stepActual += "Customer: " + customer1 + ", Arrangement: " + mainArrangement + " " + arrangementDetails;
		}

		step1Fields = "";
		step1Values = "";
		cheqStep1Fields = "";
		cheqStep1Values = "";
		step2Fields = "";
		step2Values = "";
		arrangementDetails = "";
		arrangementDetailsBuilder.setLength(0);
		step1FieldsBuilder.setLength(0);
		step1ValuesBuilder.setLength(0);
		cheqStep1FieldsBuilder.setLength(0);
		cheqStep1ValuesBuilder.setLength(0);
		step2FieldsBuilder.setLength(defaultStep2Fields.length());
		step2ValuesBuilder.setLength(defaultStep2Values.length());

	}

	public String updateAccountInfo() {
		String acitivityUpdateFields = "";
		String acitivityUpdateValues = "";
		int randomChanceRoll;
		final Random randomizer = new Random();

		randomChanceRoll = randomizer.nextInt(6);

		switch (randomChanceRoll) {
		default:
		case 0:

			randomChanceRoll = randomizer.nextInt(100);

			acitivityUpdateFields = "L.MRG.CALL.IND,";

			if (randomChanceRoll % 2 == 0) {
				acitivityUpdateValues = "YES,";
			} else {
				acitivityUpdateValues = "NO,";
			}
			break;
		case 1:
			final int num = randomizer.nextInt(9000000) + 2000000;
			final String altID1 = "AA" + num;

			acitivityUpdateFields = "ALT.ID:1,";
			acitivityUpdateValues = altID1 + ",";
			break;
		case 2:
			final int valueForOfferId = randomizer.nextInt(8);

			final String offerIdType;
			switch (valueForOfferId) {
			default:
			case 0:
				offerIdType = "Canada Life";
				break;
			case 1:
				offerIdType = "Holliswealth";
				break;
			case 2:
				offerIdType = "Ia Clarington";
				break;
			case 3:
				offerIdType = "Meritas";
				break;
			case 4:
				offerIdType = "Pip";
				break;
			case 5:
				offerIdType = "Invesco";
				break;
			case 6:
				offerIdType = "Excel Private Wealth";
				break;
			case 7:
				offerIdType = "Rbc Life Insurance";
				break;
			}

			acitivityUpdateFields = "L.LOAN.OFFER.ID,";
			acitivityUpdateValues = offerIdType + ",";
			break;
		case 3:

			final int value = randomizer.nextInt(7);
			final String loanType;
			switch (value) {
			default:
			case 0:
				loanType = "100 Mf Mca Pi Var";
				break;
			case 1:
				loanType = "100 Mf Nmc Pi Var";
				break;
			case 2:
				loanType = "Rsp Fix";
				break;
			case 3:
				loanType = "Rsp Var";
				break;
			case 4:
				loanType = "Suspense Account";
				break;
			case 5:
				loanType = "Tfsa Var";
				break;
			case 6:
				loanType = "Tfsa Fix";
				break;
			}

			acitivityUpdateFields = "L.LOAN.TYPE,";
			acitivityUpdateValues = loanType + ",";
			break;
		case 4:

			final int investmentValue = randomizer.nextInt(4);
			final String investmentLoan;
			switch (investmentValue) {
			default:
			case 0:
				investmentLoan = "1.for.1";
				break;
			case 1:
				investmentLoan = "100";
				break;
			case 2:
				investmentLoan = "2.for.1";
				break;
			case 3:
				investmentLoan = "3.for.1";
				break;
			}

			acitivityUpdateFields = "L.INV.LOAN.TYPE,";
			acitivityUpdateValues = investmentLoan + ",";
			break;
		case 5:

			acitivityUpdateFields = "L.ACQ.PORTFOLIO,";
			acitivityUpdateValues = "AGF,";
			break;
		}

		return acitivityUpdateFields + "@" + acitivityUpdateValues;

	}

}