package testLibs;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.testng.Reporter;

public class CustomerData {

	private final Map<String, String> customerDataFields = new ConcurrentHashMap<String, String>() {
		{
		}
	};

	private final Map<String, String> provincesMap = new ConcurrentHashMap<String, String>() {
		{
			put("British Columbia", "BC");
			put("Ontario", "ON");
			put("Québec", "QC");
			put("Alberta", "AB");
			put("Saskatchewan", "SK");
			put("Manitoba", "MB");
			put("Newfoundland and Labrador", "NL");
			put("New Brunswick", "NB");
			put("NorthWest Territories", "NT");
			put("Nova Scotia", "NS");
			put("Nunavut", "NU");
			put("Prince Edward Island", "PE");
			put("Yukon Territory", "YT");
		}
	};

	private String[] individualElement;
	private String mainText;

	protected String firstName;
	protected String lastName;
	protected String addressStreet;
	protected String addressCity;
	protected String addressProvince;
	protected String addressPostalCode;
	protected String motherMaidenName;
	protected String sinNumber;
	protected String phoneMain;
	protected String phoneOffice;
	protected String phoneSms;
	protected String phoneFax;
	protected String dateOfBirth;
	protected String emailAddress;
	protected String employerName;
	protected String employerAddressStreet;
	protected String employmentAddressCity;
	protected String employmentAddressProvince;
	protected String employmentAddressPostalCode;
	protected String gender;
	protected String url = "https://my.api.mockaroo.com/test_clients.json?key=80292450";
	protected boolean successfulRetrieve;

	CustomerData() {
		try {
			mainText = ReadJsonResponse.callData(url);
			successfulRetrieve = !mainText.isEmpty();
			individualElement = mainText.split(",");

			for (int i = 0; i < individualElement.length; i++) {

				customerDataFields.put(individualElement[i].split(":")[0].replaceAll("[\"{]", ""),
						individualElement[i].split(":")[1].replaceAll("[\"}]", ""));

			}

			lastName = customerDataFields.get("lastName");
			firstName = customerDataFields.get("firstName");
			dateOfBirth = customerDataFields.get("dateOfBirth").replaceAll("-", "");
			sinNumber = customerDataFields.get("sinNumber").replaceAll("-", "");
			gender = customerDataFields.get("gender");
			addressStreet = customerDataFields.get("addressStreet");
			addressCity = customerDataFields.get("addressCity");
			addressProvince = provincesMap.get(customerDataFields.get("addressProvince"));
			addressPostalCode = customerDataFields.get("addressPostalCode") + " 1A1";
			phoneMain = customerDataFields.get("phoneMain");
			phoneSms = customerDataFields.get("phoneSms");
			phoneFax = customerDataFields.get("phoneFax");
			phoneOffice = customerDataFields.get("phoneOffice");
			emailAddress = customerDataFields.get("emailAddress");
			employerName = customerDataFields.get("employerName");
			employerAddressStreet = customerDataFields.get("employerAddressStreet");
			employmentAddressCity = customerDataFields.get("employerAddressCity");
			employmentAddressPostalCode = customerDataFields.get("employerAddressPostalCode") + " 1A1";
			employmentAddressProvince = provincesMap.get(customerDataFields.get("employerAddressProvince"));

		} catch (Exception e) {
			Reporter.log("Error while interacting with CustomerData Fields");
			Reporter.log(e.getMessage(), false);
		}

	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getProvince() {
		return addressProvince;
	}

	public String getEmployerName() {
		return employerName;
	}

	public String getAddressStreet() {
		return addressStreet;
	}

	public String getAddressCity() {
		return addressCity;
	}

	public String getAddressProvince() {
		return addressProvince;
	}

	public String getaddressPostalCode() {
		return addressPostalCode;
	}

	public String getCustomerName() {
		return firstName + " " + lastName;
	}

}
