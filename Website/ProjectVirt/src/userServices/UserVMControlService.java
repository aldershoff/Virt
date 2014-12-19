package userServices;

import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import json.JsonPOSTParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.json.simple.JSONObject;

/**
 * Service for controlling the virtual machines e.d start, stop, editing and
 * deleting them
 * 
 * @author KjellZijlemaker
 * @version 1.0
 * @since 1.0
 *
 */
public class UserVMControlService {

	/**
	 * Variables that will be used globally
	 */
	private VelocityContext vsl_Context;
	private Template template;
	private PrintWriter out;

	/**
	 * Constructor for initialing the global variables
	 * 
	 * @param vsl_Context
	 * @param template
	 * @param out
	 */
	public UserVMControlService(VelocityContext vsl_Context, Template template,
			PrintWriter out) {
		this.vsl_Context = vsl_Context;
		this.template = template;
		this.out = out;
	}

	/**
	 * Method for giving request to back-end, to start the chosen VM
	 * 
	 * @param request
	 * @param response
	 * @param vmID
	 * @param userID
	 * @param action
	 */
	public void startVM(HttpServletRequest request,
			HttpServletResponse response, String vmID, long userID,
			String action) {

		/**
		 * Set postparameters to give with the request
		 */
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("vmID", vmID));
		postParameters.add(new BasicNameValuePair("userID", Long
				.toString(userID)));
		postParameters.add(new BasicNameValuePair("action", action));

		// Making the JSON object for sending data
		JSONObject json = JsonPOSTParser
				.postJsonFromUrl(
						request,
						"http://localhost:8080/BackEnd/customer/controlpanel/vmcontrol",
						postParameters);

		/**
		 * If json is null, connection could not be made
		 */
		if (json != null) {

			/**
			 * Get success or error from the back-end
			 */
			if (!json.containsKey("error")) {
				vsl_Context.put("success", json.get("success"));
			} else {
				vsl_Context.put("error", json.get("error"));
			}

		}

		else {
			vsl_Context.put("error",
					"Connection with server could not be made..");

		}

	}

	/**
	 * Method for giving request to back-end, to stop the chosen VM
	 * @param request
	 * @param response
	 * @param vmID
	 * @param userID
	 * @param action
	 */
	public void stopVM(HttpServletRequest request,
			HttpServletResponse response, String vmID, long userID,
			String action) {
		
		/**
		 * Set postparameters to give with the request
		 */
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("vmID", vmID));
		postParameters.add(new BasicNameValuePair("userID", Long
				.toString(userID)));
		postParameters.add(new BasicNameValuePair("action", action));

		// Making JSON object for sending request to the back-end
		JSONObject json = JsonPOSTParser
				.postJsonFromUrl(
						request,
						"http://localhost:8080/BackEnd/customer/controlpanel/vmcontrol",
						postParameters);

		/**
		 * If json is null, connection could not be made
		 */
		if (json != null) {

			/**
			 * Getting success or error from the back-end
			 */
			if (!json.containsKey("error")) {
				vsl_Context.put("success", json.get("success"));
			} else {
				vsl_Context.put("error", json.get("error"));
			}

		}

		else {
			vsl_Context.put("error",
					"Connection with server could not be made..");

		}
	}

	/**
	 * Method for giving request to back-end, to edit the chosen VM
	 * @param request
	 * @param response
	 * @param vmID
	 * @param userID
	 * @param action
	 */
	public void editVM(HttpServletRequest request,
			HttpServletResponse response, String vmID, long userID,
			String action) {
		/**
		 * Set postparameters to give with the request
		 */
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("vmID", vmID));
		postParameters.add(new BasicNameValuePair("userID", Long
				.toString(userID)));
		postParameters.add(new BasicNameValuePair("action", action));

		// Making JSON object for sending request to the back-end
		JSONObject json = JsonPOSTParser
				.postJsonFromUrl(
						request,
						"http://localhost:8080/BackEnd/customer/controlpanel/vmcontrol",
						postParameters);

		/**
		 * If json is null, connection could not be made
		 */
		if (json != null) {

			/**
			 * Getting the success or error from the back-end
			 */
			if (!json.containsKey("error")) {
				vsl_Context.put("success", json.get("success"));
			} else {
				vsl_Context.put("error", json.get("error"));
			}

		}

		else {
			vsl_Context.put("error",
					"Connection with server could not be made..");

		}
	}

	/**
	 * Method for giving request to back-end, to stop the chosen VM
	 * @param request
	 * @param response
	 * @param vmID
	 * @param userID
	 * @param action
	 */
	public void deleteVM(HttpServletRequest request,
			HttpServletResponse response, String vmID, long userID,
			String action) {
		/**
		 * Set postparameters to give with the request
		 */
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("vmID", vmID));
		postParameters.add(new BasicNameValuePair("userID", Long
				.toString(userID)));
		postParameters.add(new BasicNameValuePair("action", action));

		// Making JSON Object for sending request tot the back-end
		JSONObject json = JsonPOSTParser
				.postJsonFromUrl(
						request,
						"http://localhost:8080/BackEnd/customer/controlpanel/vmcontrol",
						postParameters);

		/**
		 * If json is null, connection could not be made
		 */
		if (json != null) {

			/**
			 * Getting request or error from the back-end
			 */
			if (!json.containsKey("error")) {
				vsl_Context.put("success", json.get("success"));
			} else {
				vsl_Context.put("error", json.get("error"));
			}

		}

		else {
			vsl_Context.put("error",
					"Connection with server could not be made..");

		}
	}
	
	/**
	 * Method for giving request to back-end, to stop the chosen VM
	 * @param request
	 * @param response
	 * @param vmID
	 * @param userID
	 * @param action
	 */
	public void refreshVMState(HttpServletRequest request,
			HttpServletResponse response, String vmID, long userID,
			String action) {
		/**
		 * Set postparameters to give with the request
		 */
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("vmID", vmID));
		postParameters.add(new BasicNameValuePair("userID", Long
				.toString(userID)));
		postParameters.add(new BasicNameValuePair("action", action));

		// Making JSON Object for sending request tot the back-end
		JSONObject json = JsonPOSTParser
				.postJsonFromUrl(
						request,
						"http://localhost:8080/BackEnd/customer/controlpanel/vmcontrol",
						postParameters);

		/**
		 * If json is null, connection could not be made
		 */
		if (json != null) {

			/**
			 * Getting request or error from the back-end
			 */
			if (!json.containsKey("error")) {
				vsl_Context.put("success", json.get("success"));
			} else {
				vsl_Context.put("error", json.get("error"));
			}

		}

		else {
			vsl_Context.put("error",
					"Connection with server could not be made..");

		}
	}
}
