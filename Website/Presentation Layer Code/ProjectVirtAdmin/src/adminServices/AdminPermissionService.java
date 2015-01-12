package adminServices;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import json.JsonPOSTParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.velocity.VelocityContext;
import org.json.simple.JSONObject;

public class AdminPermissionService {

	private HttpServletRequest request;
	private VelocityContext vsl_Context;

	public AdminPermissionService(HttpServletRequest request,
			VelocityContext vsl_Context) {
		this.request = request;
		this.vsl_Context = vsl_Context;
	}

	public void setPermissions(String userID, String userType) {

		/**
		 * Set postparameters to give with the request
		 */
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("userID", userID));
		postParameters.add(new BasicNameValuePair("request", userType));

		/**
		 * Set the url for JSON + the postparameters
		 */
		JSONObject json = JsonPOSTParser
				.postJsonFromUrl(
						request,
						"http://localhost:8080/BackEnd/admin/overview/processuserrights",
						postParameters);

		/**
		 * If json is null, connection could not be made
		 */
		if (json != null) {

			/**
			 * Check if json String contains error key, for showing error. If
			 * not, process continues
			 */
			if (!json.containsKey("error")) {

				vsl_Context
						.put("success",
								json.get("success"));

			} else {
				vsl_Context.put("error", json.get("error"));
			}

		} else {
			vsl_Context.put("error",
					"Connection with server could not be made..");

		}

	}

	
}
