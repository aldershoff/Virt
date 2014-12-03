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
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JSONPosts {

	public JSONObject postLoginJson(HttpServletRequest request) {

		try {
			HttpClient c = new DefaultHttpClient();
			HttpPost p = new HttpPost(
					"http://localhost:8080/ProjectVirt/login/processlogin");
			ArrayList<NameValuePair> postParameters;

			postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("user", request
					.getParameter("user")));
			postParameters.add(new BasicNameValuePair("password", request
					.getParameter("password")));

			p.setEntity(new UrlEncodedFormEntity(postParameters));

			// Execute HTTP Post Request
			HttpResponse responseNewURL = c.execute(p);
			HttpEntity entity = responseNewURL.getEntity();
			InputStream content = entity.getContent();

			// (1)
			BufferedReader bReader = new BufferedReader(new InputStreamReader(
					content));
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = bReader.readLine()) != null) {
				builder.append(line);
			}

			JSONObject json = (JSONObject) new JSONParser().parse(builder
					.toString());
			return json;
		} catch (ParseException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		} catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
