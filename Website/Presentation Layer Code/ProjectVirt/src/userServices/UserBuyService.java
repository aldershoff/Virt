package userServices;

import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import json.JsonPOSTParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.json.simple.JSONObject;

/**
 * Service for buying on the website. This can be all sort of things
 * like virtual machines, or extra SLA contracts
 * @author KjellZijlemaker
 * @version 1.0
 * @since 1.0
 */
public class UserBuyService {

	/**
	 * Variables that will be used globally
	 */
	private VelocityContext vsl_Context;
	private HttpServletRequest request;

	/**
	 * Constructor for initialing the global variables
	 * 
	 * @param vsl_Context
	 * @param template
	 * @param out
	 */
	public UserBuyService(VelocityContext vsl_Context, Template template, PrintWriter out, HttpServletRequest request){
		this.vsl_Context = vsl_Context;
		this.request = request;
	}
	
	/**
	 * Method for buying a new VM
	 * 
	 * @param request
	 * @param response
	 * @param sessionUserID
	 */
	public void buyCustomerVM(long sessionUserID) {
		/**
		 * Checking if the fields were not left empty
		 */
		if (request.getParameter("VMRAM").equals("vmRAM")
				|| request.getParameter("VMCPU").equals("vmCPU")
				|| request.getParameter("VMHDD").equals("vmHDD")
				|| request.getParameter("VMSLA").equals("vmSLA")) {
			vsl_Context.put("error", "Please provide input in each field");
		} else if (request.getParameter("accept") == null) {
			vsl_Context.put("error", "Please accept terms of condition");
		} else {

			/**
			 * Set postparameters to give with the request
			 */
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("VMOS", request
					.getParameter("VMOS")));
			postParameters.add(new BasicNameValuePair("VMRAM", request
					.getParameter("VMRAM")));
			postParameters.add(new BasicNameValuePair("VMCPU", request
					.getParameter("VMCPU")));
			postParameters.add(new BasicNameValuePair("VMHDD", request
					.getParameter("VMHDD")));
			postParameters.add(new BasicNameValuePair("VMSLA", request
					.getParameter("VMSLA")));

			/**
			 * Set the url for JSON + the postparameters
			 */
			JSONObject json = JsonPOSTParser.postJsonFromUrl(request,
					"http://localhost:8080/BackEnd/customer/marketplace/buy/processbuy?userID="
							+ sessionUserID, postParameters);

			/**
			 * Try to parse the JSON Object, given by the server
			 */
			try {

				/**
				 * If json is null, connection could not be made
				 */
				if (json != null) {

					/**
					 * Check if json String contains error key, for showing
					 * error. If not, process continues
					 */
					if (!json.containsKey("error")) {

						vsl_Context
								.put("success",
										"Succesfully added VM. It will take a minute or 30 before the VM is monitorable!");
						
						if((long)json.get("vmIPIsActive") == 0){
							vsl_Context.put("error", "No more IP addresses available.. Please contact support!");
						}
					} else {
						vsl_Context.put("error", json.get("error"));
					}

				} else {
					vsl_Context.put("error",
							"Connection with server could not be made..");

				}
			} finally {

				/**
				 * Check which template to load again
				 */
				switch (request.getParameter("VMOS")) {
				case "debian":
					Velocity
							.getTemplate("Velocity/customers/debian.html");
					break;
				case "windows":
					Velocity
							.getTemplate("Velocity/customers/windows.html");
					break;
				case "slackware":
					Velocity
							.getTemplate("Velocity/customers/slackware.html");
					break;
				}

			}

		}

	}
	
}
