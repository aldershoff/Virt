package userServices;

import java.io.IOException;
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

import com.google.gson.Gson;

import beans.CustomerBean;
import beans.VMBean;

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
	private HttpServletRequest request;
	private HttpServletResponse response;

	/**
	 * Constructor for initialing the global variables
	 * 
	 * @param vsl_Context
	 * @param template
	 * @param out
	 */
	public UserVMControlService(VelocityContext vsl_Context, Template template,
			PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
		this.vsl_Context = vsl_Context;
		this.template = template;
		this.out = out;
		this.request = request;
		this.response = response;
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
	public void startVM(String vmID, long userID,
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

		// Else, the connection could not be made
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
	public void stopVM(String vmID, long userID,
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

		// Else, the connection could not be made
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
	public void editVM(String vmID, long userID,
			String action) {

		// Setting new bean to fill the data
		VMBean editVM = new VMBean();
		try{
			
		/**
		 * Set postparameters to give with the request
		 */
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("vmID", vmID));
		postParameters.add(new BasicNameValuePair("vmName", request.getParameter("vmName")));
		postParameters.add(new BasicNameValuePair("vmCPU", request.getParameter("VMCPU")));
		postParameters.add(new BasicNameValuePair("vmRAM", request.getParameter("VMRAM")));
		postParameters.add(new BasicNameValuePair("vmHDD", request.getParameter("VMHDD")));
		postParameters.add(new BasicNameValuePair("vmSLA", request.getParameter("VMSLA")));
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
				Gson gson = new Gson();
				editVM = gson.fromJson(json.toString(),
						VMBean.class);
				vsl_Context.put("success",
						"Successfully changed your VM settings!");
			} else {
				vsl_Context.put("error", json.get("error"));
			}

		}
		
		//Else, the connection could not be made
		else {
			vsl_Context.put("error",
					"Connection with server could not be made..");

		}
		}
		finally{
			vsl_Context.put("vm", editVM);
		}
	}

	/**
	 * Method for giving request to back-end, to stop the chosen VM
	 * @param request
	 * @param response
	 * @param vmID
	 * @param userID
	 * @param action
	 * @throws IOException 
	 */
	public void deleteVM(String vmID, long userID,
			String action) throws IOException {
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
				response.sendRedirect("http://localhost:8080/ProjectVirt/customer/controlpanel");
			} else {
				vsl_Context.put("error", json.get("error"));
			}

		}

		// Else, the connection could not be made
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
	public void refreshVMState(String vmID, long userID,
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

		// Else, the connection could not be made
		else {
			vsl_Context.put("error",
					"Connection with server could not be made..");

		}
	}
}
