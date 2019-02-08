package testcases.PROC;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import atu.alm.wrapper.enums.StatusAs;

public class PROC_BB_ILA_XX005 extends testLibs.BaseTest_DataGen {

	private static String documentVersion = "Version 2.0";
	private final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
	private final Date date = new Date();
	private final String todayDate = dateFormat.format(date);
	private final String branch = "B2B Branch 817";

	@Test(priority = 1, enabled = true)
	@Parameters({ "productGroup" })
	public void eftClearingReconciliations(final String productGroup) {
		String eftBalanceStart1;
		String eftBalanceEnd1;
		String eftBalanceStart2;
		String eftBalanceEnd2;
		String fields;
		String values;
		String paymentID;
		int balanceColumn;
		int balanceStartRow;
		int balanceEndRow;

		stepDescription = "Perform EFT Clearing Reconciliations";
		stepExpected = "EFT Clearing Reconciliations performed successfully";

		switchToBranch(branch);

		commandLine("ENQ LBC.EFT.DAILY.OUTFILE", commandLineAvailable);
		enquiryElements.enquirySearch("EFT File Date,Company Code", ",", todayDate + ",CA0017000");
		switchToPage(LASTPAGE, false);

		if (!enquiryElements.verifyOptionSaveCSV()) {
			stepActual = "Enquiry results could not be saved as a CSV file for EFT Daily OutFile";
			softVerify.fail(stepActual);
			stepResult = StatusAs.FAILED;
		}

		commandLine("ENQ LBC.OUTGOING.EFT", commandLineAvailable);
		enquiryElements.findButton().click();
		switchToPage(LASTPAGE, false);

		if (!enquiryElements.verifyOptionSaveCSV()) {
			stepActual = "Enquiry results could not be saved as a CSV file for OutGoing EFT";
			softVerify.fail(stepActual);
			stepResult = StatusAs.FAILED;
		}

		commandLine("ENQ STMT.ENT.TODAY", commandLineAvailable);
		enquiryElements.enquirySearch("Account No", "", "CAD1100000017817");
		switchToPage(LASTPAGE, false);
		
		balanceColumn = enquiryElements.getColumnHeaderNumber("Statement Entries Today", "Amount");

		balanceStartRow = enquiryElements.getRowNumberMatching("Statement Entries Today", "Description",
				"Balance at Period Start");
		eftBalanceStart1 = enquiryElements.getElementAtCell("Statement Entries Today", balanceColumn, balanceStartRow)
				.getText();

		balanceEndRow = enquiryElements.getRowNumberMatching("Statement Entries Today", "Description",
				"Balance at Period End");
		eftBalanceEnd1 = enquiryElements.getElementAtCell("Statement Entries Today", balanceColumn, balanceEndRow)
				.getText();

		commandLine("ENQ STMT.ENT.BOOK", commandLineAvailable);
		enquiryElements.enquirySearch("Account,Processing Date", ",", "CAD1100000017817," + todayDate);
		switchToPage(LASTPAGE, false);
		

		balanceColumn = enquiryElements.getColumnHeaderNumber("Account Statement", "Closing Balance");
		
		balanceStartRow = enquiryElements.getRowNumberMatching("Account Statement", "Reference",
				"Balance at Period Start");
		eftBalanceStart2 = enquiryElements.getElementAtCell("Account Statement", balanceColumn, balanceStartRow)
				.getText();

		balanceEndRow = enquiryElements.getRowNumberMatching("Account Statement", "Reference", "Balance at Period End");
		eftBalanceEnd2 = enquiryElements.getElementAtCell("Account Statement", balanceColumn, balanceEndRow)
				.getText();
		
		fields = "CONSOL.INSTRUCTION," + "PRIMARY.ACCOUNT," + "TRANSACTION:1," + "CURRENCY:1," + "SURROGATE.AC:1,"
				+ "AMOUNT:1," + "TRANSACTION:2," + "CURRENCY:2," + "SURROGATE.AC:2," + "AMOUNT:2,";
		values = "No," + "CAD1100000017817," + "Debit," + "CAD," + "CAD1100100017817," + "10.00," + "Credit," + "CAD,"
				+ "CAD1100100017817," + "15.00,";

		paymentID = financialTransaction(CREATE, "Teller Financial Services", fields, values);

		if (paymentID.contains(ERROR)) {
			stepActual = "Error while performing EFT Clearing Reconciliations";
			softVerify.fail(stepActual);
			stepResult = StatusAs.FAILED;
		} else {
			stepActual = "ENQ STMT.ENT.TODAY:" + System.lineSeparator() + "Balance at Period Start: " + eftBalanceStart1
					+ System.lineSeparator() + "Balance at Period End: " + eftBalanceEnd1 + System.lineSeparator()
					+ "ENQ STMT.ENT.BOOK:" + System.lineSeparator() + "Balance at Period Start: " + eftBalanceStart2
					+ System.lineSeparator() + "Balance at Period End: " + eftBalanceEnd2;
		}
	}

	@Test(priority = 2, enabled = true)
	public void chequeClearingReconciliations() {
		String chequeBalanceStart;
		String chequeBalanceEnd;
		String fields;
		String values;
		String transactionID;
		int balanceColumn;
		int balanceStartRow;
		int balanceEndRow;

		stepDescription = "Perform Cheque Clearing Reconciliations";
		stepExpected = "Cheque Clearing Reconciliations performed successfully";

		commandLine("ENQ STMT.ENT.BOOK", commandLineAvailable);
		enquiryElements.enquirySearch("Account,Processing Date,Reversal Marker", ",,",
				"CAD1100100017817," + todayDate + ",R");
		switchToPage(LASTPAGE, false);
		
		balanceColumn = enquiryElements.getColumnHeaderNumber("Account Statement", "Closing Balance");
		
		balanceStartRow = enquiryElements.getRowNumberMatching("Account Statement", "Reference",
				"Balance at Period Start");
		chequeBalanceStart = enquiryElements.getElementAtCell("Account Statement", balanceColumn, balanceStartRow)
				.getText();

		balanceEndRow = enquiryElements.getRowNumberMatching("Account Statement", "Reference", "Balance at Period End");
		chequeBalanceEnd = enquiryElements.getElementAtCell("Account Statement", balanceColumn, balanceEndRow)
				.getText();

		fields = "DEBIT.ACCT.NO," + "DEBIT.CURRENCY," + "DEBIT.AMOUNT," + "ORDERING.CUST:1," + "CREDIT.ACCT.NO,";
		values = "CAD1100100017817," + "CAD," + "10.00," + "Cheque Clearing Reconciliations," + "CAD1110200017817,";

		transactionID = financialTransaction(CREATE, "Account Transfer - Transfer between Accounts", fields, values);
		if (transactionID.contains(ERROR)) {
			stepActual = "Error while performing Cheque Clearing Reconciliations";
			softVerify.fail(stepActual);
			stepResult = StatusAs.FAILED;
		} else {
			stepActual = "Balance at Period Start: " + chequeBalanceStart + System.lineSeparator()
					+ "Balance at Period End: " + chequeBalanceEnd;
		}
	}

	@Test(priority = 3, enabled = true)
	public void monetisAndGeneralReconciliations() {
		String fields;
		String values;
		String transactionID;
		boolean result;

		stepDescription = "Perform Monetis and General Reconciliations";
		stepExpected = "Monetis and General Reconciliations performed successfully";

		fields = "ORDERING.BANK:1," + "DEBIT.ACCT.NO," + "CREDIT.ACCT.NO," + "CREDIT.CURRENCY," + "CREDIT.AMOUNT,";
		values = "B2B," + "CAD1100000017817," + "CAD1120300017817," + "CAD," + "10.00,";
		transactionID = financialTransaction(CREATE, "Account Transfer - Transfer between Accounts", fields, values);

		if (transactionID.contains(ERROR)) {
			stepActual = "Error while performing FundsTransfer";
			softVerify.fail(stepActual);
			stepResult = StatusAs.FAILED;
		} else {
			result = reverseEntity(transactionID, FUNDSTRANSFER);
			if (!result) {
				stepActual = "Error while reversing FundsTransfer: " + transactionID;
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			}
		}
	}
}
