package elements.table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import testLibs.BaseTest;
import testLibs.RepositoryElements;

public class TEMENOS_ListTable extends BaseTest {
	public static WebElement element;
	private final String listTableFile = listTableProperties;
	private final RepositoryElements repoEleListTable;
	public TEMENOS_ListTable(final WebDriver driver) throws IOException {
		repoEleListTable = new RepositoryElements(listTableFile);
	}

	public WebElement elementList() {
		element = driver.findElement(repoEleListTable.byLocator("countryList"));
		return element;
	}

	public WebElement listHeader() {
		element = driver.findElement(repoEleListTable.byLocator("listHeader"));
		return element;
	}

	public WebElement nextBtn() {
		element = driver.findElement(repoEleListTable.byLocator("nextBtn"));
		return element;
	}

	public Boolean isNextBtnPresent(final String headerText) {
		Boolean result = true;
		final String myNumbers = headerText.replaceAll("[^-?0-9]+", " ");
		final String[] myNumbersStr = myNumbers.trim().split(" ");
		if (Integer.parseInt(myNumbersStr[2]) == Integer.parseInt(myNumbersStr[3])) {
			result = false;
		} else {
			result = driver.findElements(repoEleListTable.byLocator("nextBtn")).size() > 0;
		}
		return result;
	}

	@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
	public List<String> getList() {
		boolean nextBtnAvailable = true;
		String imgProps;
		final List<String> list = new ArrayList<String>();
		List<WebElement> listOnPage;
		String headerText = listHeader().getText();

		if (!"No data to display".equals(headerText)) {
			while (nextBtnAvailable) {
				if (isNextBtnPresent(headerText)) {
					imgProps = nextBtn().getAttribute("src");
					if (imgProps.contains("_dis.")) {
						nextBtnAvailable = false;
					}
				} else {
					nextBtnAvailable = false;
				}

				listOnPage = driver.findElements(repoEleListTable.byLocator("countryList"));
				for (int i = 0; i < driver.findElements(repoEleListTable.byLocator("countryList")).size(); i++) {
					list.add(listOnPage.get(i).findElement(By.tagName("b")).getText());
				}

				if (nextBtnAvailable) {
					nextBtn().click();
					headerText = listHeader().getText();
				}
			}
		}
		list.removeIf(item -> item.isEmpty());
		return list;
	}
}
