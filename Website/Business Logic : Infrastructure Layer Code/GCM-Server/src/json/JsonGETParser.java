package json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Class for getting JSON responses and parse them to readable text.
 * This class is only for getting information, without giving parameters or JSON strings with them
 * @author KjellZijlemaker
 *
 */
public class JsonGETParser {

	static InputStream is = null;
	static JSONArray jsonArray;
	static String json = "";
	static JSONObject jsonObject;

	// constructor
	public JsonGETParser() {

	}

	/**
	 * Read all Strings and append them to the StringBuilder, so a new String will be formed
	 * @param rd
	 * @return
	 * @throws IOException
	 */
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	/**
	 * Read the JSON object from the inserted URL
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static JSONObject readJsonObjectFromUrl(String url)
			throws IOException, ParseException {
		InputStream is = new URL(url).openStream();
		
		/**
		 * Try to parse the newly made text and return it 
		 */
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is,
					Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = (JSONObject) new JSONParser().parse(jsonText);

			return json;
		} finally {
			is.close();
		}
	}

	/**
	 * Same, but this is for giving back an Array
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 * @throws ParseException
	 */
	public static JSONArray readJsonArrayFromUrl(String url)
			throws IOException, ParseException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is,
					Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONArray json = (JSONArray) new JSONParser().parse(jsonText);

			return json;
		} finally {
			is.close();
		}
	}

}
