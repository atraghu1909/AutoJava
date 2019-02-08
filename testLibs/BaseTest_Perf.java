package testLibs;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import atu.alm.wrapper.enums.StatusAs;

public class BaseTest_Perf extends BaseTest_DataGen {
	final protected List<List<Object>> ldReportContents = new ArrayList<List<Object>>();
	final protected List<List<Object>> lsReportContents = new ArrayList<List<Object>>();
	protected List<Object> reportLine;
	protected SheetWriter perfDetailsReport;
	protected SheetWriter perfSummaryReport;
	protected Timestamp stepStart;
	protected Timestamp stepEnd;
	protected String loggedUser = "UNKNOWN";
	protected String stepName;

	@Override
	@Test(priority = 0, enabled = true)
	public void login() {

		final int loadLogin = 20;
		final boolean enforceLimits = false;
		Timestamp timestampStart = new Timestamp(System.currentTimeMillis());
		Timestamp timestampEnd = new Timestamp(System.currentTimeMillis());
		int loadingTime;
		
		stepDescription = "Measure Login loading time";
		stepExpected = "Login loading time is under " + loadLogin + " seconds";

		timestampStart = new Timestamp(System.currentTimeMillis());
		loginResult = loginPg.login("RaghavT", "654321");

		if (loginResult) {
			timestampEnd = new Timestamp(System.currentTimeMillis());

			switchToFrame(homePg.bannerFrame());
			loggedUser = homePg.bannerText().getText().split(" ")[0];

			loadingTime = logPerformanceLine("Login", timestampStart, timestampEnd);

			stepActual = "User " + loggedUser + " successfully logs in" + System.lineSeparator()
					+ "Login loading time was " + loadingTime + " seconds" + System.lineSeparator() + "Start: "
					+ timestampStart.toString() + System.lineSeparator() + "End: " + timestampEnd.toString();

			if (enforceLimits && loadingTime > loadLogin) {
				softVerify.fail(stepActual);
				stepResult = StatusAs.FAILED;
			} else {
				stepResult = StatusAs.PASSED;
			}

		} else {
			stepResult = StatusAs.NOT_COMPLETED;
			testResult = StatusAs.NOT_COMPLETED;
			stepActual = "Login failed";
			softVerify.fail(stepActual);
		}
		softVerify.assertAll();
	}
	
	@BeforeMethod
	public void setup(final Method method) {
		stepStart = new Timestamp(System.currentTimeMillis());
		stepName = method.getName();
	}
	
	public int logPerformanceLine(final String subStep, final Timestamp startTime, final Timestamp endTime) {
		final double result = (endTime.getTime() - startTime.getTime()) * 1.0 / 1000;
		
		reportLine = new ArrayList<Object>();
		reportLine.add(this.getClass().getName());
		reportLine.add(environmentName);
		reportLine.add(loggedUser);
		reportLine.add(stepName);
		reportLine.add(subStep);
		reportLine.add(Double.toString(result));
		reportLine.add(startTime.toString());
		reportLine.add(endTime.toString());
		reportLine.add(stepResult.toString());
		ldReportContents.add(reportLine);
		
		return (int) result;
	}
	
	@AfterMethod
	public void perfTestTearDown() {
		stepEnd = new Timestamp(System.currentTimeMillis());
		final double result = (stepEnd.getTime() - stepStart.getTime()) * 1.0 / 1000;

		reportLine = new ArrayList<Object>();
		reportLine.add(this.getClass().getName());
		reportLine.add(environmentName);
		reportLine.add(loggedUser);
		reportLine.add(stepName);
		reportLine.add("Total");
		reportLine.add(Double.toString(result));
		reportLine.add(stepStart.toString());
		reportLine.add(stepEnd.toString());
		reportLine.add(stepResult.toString());
		lsReportContents.add(reportLine);
	}
	
	@AfterClass
	public void classTearDown() {
		perfDetailsReport = new SheetWriter("PerfDetails");
		perfSummaryReport = new SheetWriter("PerfSummary");

		perfDetailsReport.writeLine(ldReportContents);
		perfSummaryReport.writeLine(lsReportContents);
	}
	
}