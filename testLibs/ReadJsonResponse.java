package testLibs;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

public class ReadJsonResponse {

	public static String callData(String url) throws Exception {
		InputStream is = new URL(url).openStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		return response.toString();
	}
}
