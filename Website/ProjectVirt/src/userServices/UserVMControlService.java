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

public class UserVMControlService {

	private VelocityContext vsl_Context;
	private Template template;
	private PrintWriter out;
	
	public UserVMControlService(VelocityContext vsl_Context, Template template, PrintWriter out){
		this.vsl_Context = vsl_Context;
		this.template = template;
		this.out = out;
	}
	
	public void startVM(HttpServletRequest request, HttpServletResponse response, String vmID, long userID, String action){
		/**
		 * Set postparameters to give with the request
		 */
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("vmID", vmID));
		postParameters.add(new BasicNameValuePair("userID", Long.toString(userID)));
		postParameters.add(new BasicNameValuePair("action", action));

		
		JSONObject json = JsonPOSTParser
				.postJsonFromUrl(
						request,
						"http://localhost:8080/BackEnd/customer/controlpanel/vmcontrol",
						postParameters);

			/**
			 * If json is null, connection could not be made
			 */
			if (json != null) {
			
				if(!json.containsKey("error")){
				vsl_Context.put("success", json.get("success"));
				}
				else{
					vsl_Context.put("error", json.get("error"));
				}
				
			}
			

				else {
				vsl_Context.put("error",
						"Connection with server could not be made..");

			}
		
		
	}
	
	public void stopVM(HttpServletRequest request, HttpServletResponse response, String vmID, long userID, String action){
		/**
		 * Set postparameters to give with the request
		 */
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("vmID", vmID));
		postParameters.add(new BasicNameValuePair("userID", Long.toString(userID)));
		postParameters.add(new BasicNameValuePair("action", action));

		
		JSONObject json = JsonPOSTParser
				.postJsonFromUrl(
						request,
						"http://localhost:8080/BackEnd/customer/controlpanel/vmcontrol",
						postParameters);

			/**
			 * If json is null, connection could not be made
			 */
			if (json != null) {
			
				if(!json.containsKey("error")){
				vsl_Context.put("success", json.get("success"));
				}
				else{
					vsl_Context.put("error", json.get("error"));
				}
				
			}
			

				else {
				vsl_Context.put("error",
						"Connection with server could not be made..");

			}
	}
	
	public void editVM(HttpServletRequest request, HttpServletResponse response, String vmID, long userID, String action){
		/**
		 * Set postparameters to give with the request
		 */
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("vmID", vmID));
		postParameters.add(new BasicNameValuePair("userID", Long.toString(userID)));
		postParameters.add(new BasicNameValuePair("action", action));

		
		JSONObject json = JsonPOSTParser
				.postJsonFromUrl(
						request,
						"http://localhost:8080/BackEnd/customer/controlpanel/vmcontrol",
						postParameters);

			/**
			 * If json is null, connection could not be made
			 */
			if (json != null) {
			
				if(!json.containsKey("error")){
				vsl_Context.put("success", json.get("success"));
				}
				else{
					vsl_Context.put("error", json.get("error"));
				}
				
			}
			

				else {
				vsl_Context.put("error",
						"Connection with server could not be made..");

			}
	}
	
	public void deleteVM(HttpServletRequest request, HttpServletResponse response, String vmID, long userID, String action){
		/**
		 * Set postparameters to give with the request
		 */
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("vmID", vmID));
		postParameters.add(new BasicNameValuePair("userID", Long.toString(userID)));
		postParameters.add(new BasicNameValuePair("action", action));

		
		JSONObject json = JsonPOSTParser
				.postJsonFromUrl(
						request,
						"http://localhost:8080/BackEnd/customer/controlpanel/vmcontrol",
						postParameters);

			/**
			 * If json is null, connection could not be made
			 */
			if (json != null) {
			
				if(!json.containsKey("error")){
				vsl_Context.put("success", json.get("success"));
				}
				else{
					vsl_Context.put("error", json.get("error"));
				}
				
			}
			

				else {
				vsl_Context.put("error",
						"Connection with server could not be made..");

			}
	}
}
