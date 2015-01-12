package json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Made for also giving Postparameters with the JSON request, and parsing the
 * newly made String. Still in development!!
 * 
 * @author KjellZijlemaker
 *
 */
public class JsonPOSTParser {
	public static JSONObject postJsonFromUrl(HttpServletRequest request,
			String url, ArrayList<NameValuePair> postParameters) {

		try {
			/**
			 * Setting client and post
			 */
			HttpClient c = new DefaultHttpClient();
			HttpPost p = new HttpPost(url);
			p.setEntity(new UrlEncodedFormEntity(postParameters));

			// Execute HTTP Post Request
			HttpResponse responseNewURL = c.execute(p);
			HttpEntity entity = responseNewURL.getEntity();
			InputStream content = entity.getContent();

			/**
			 * Making the new String 
			 */
			BufferedReader bReader = new BufferedReader(new InputStreamReader(
					content));
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = bReader.readLine()) != null) {
				builder.append(line);
			}

			/**
			 * Parsing the String and returning the JSON object
			 */
			JSONObject json = (JSONObject) new JSONParser().parse(builder
					.toString());
			return json;
		} catch (ParseException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}
		return null;
	}
}
