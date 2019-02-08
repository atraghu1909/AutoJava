package testLibs;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestListenerAdapter;

import atu.alm.wrapper.enums.StatusAs;

public class ListenerScreen extends TestListenerAdapter {

	private static String fileSeparator = System.getProperty("file.separator");

	private String screenShotPath = "";

	@Override
	public void onTestFailure(final ITestResult result) {
		BaseTest.testResult = StatusAs.FAILED;
		BaseTest.stepResult = StatusAs.FAILED;
		if ("".equals(BaseTest.stepActual)) {
			BaseTest.stepActual = result.getName() + " ended prematurely";
		}

		final WebDriver driver = BaseTest.driver;
		final String testClassName = getTestClassName(result.getInstanceName()).trim();
		final String testMethodName = result.getName().toString().trim();

		if (driver != null) {

			final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.ENGLISH);
			final String ntime = sdf.format(new Date());
			final String ndate = ntime.split("-")[0];
			final String nhour = ntime.split("-")[1];
			screenShotPath = "testReport/screenshots" + fileSeparator + ndate + fileSeparator + "FailureScreen"
					+ fileSeparator + testClassName + fileSeparator;
			final String screenShotName = "S" + nhour + "_" + testMethodName + ".png";

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				Reporter.log(e.getMessage(), false);
			}
			takeScreenShot(driver, screenShotPath, screenShotName);

			BaseTest.latestScreenshot = screenShotPath + fileSeparator + screenShotName;
			Reporter.log("Screenshot can be found : in " + screenShotPath, false);
		}
	}

	public void takeScreenShot(final WebDriver driver, final String screenShotPath, final String screenShotName) {

		try {
			final File file = new File(screenShotPath);
			if (!file.exists()) {
				// System.out.println("File created " + file);
				file.mkdir();
			}

			final File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			final File targetFile = new File(screenShotPath, screenShotName);
			FileUtils.copyFile(screenshotFile, targetFile);

		} catch (WebDriverException e) {
			Reporter.log("An exception occured while taking screenshot " + e.getClass().getName(), false);
		} catch (Exception e) {
			Reporter.log("An exception occured while taking screenshot " + e.getClass().getName(), false);
		}
	}

	public String getTestClassName(final String testName) {
		final String[] reqTestClassname = testName.split("\\.");
		final int index = reqTestClassname.length - 1;
		return reqTestClassname[index];
	}
}