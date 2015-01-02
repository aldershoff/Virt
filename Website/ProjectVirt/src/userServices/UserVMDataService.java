package userServices;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import json.JsonGETParser;
import net.sf.json.JSONException;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import beans.VMBean;

import com.google.gson.Gson;

/**
 * Service for getting all the data for the virtual machines.
 * @author KjellZijlemaker
 * @version 1.0
 * @since 1.0
 *
 */
public class UserVMDataService {
	
	/**
	 * Variables that will be used globally
	 */
	private VelocityContext vsl_Context;
	private Template template;
	private PrintWriter out;
	private HttpServletRequest request;
	
	/**
	 * Constructor for initialing the global variables
	 * @param vsl_Context
	 * @param template
	 * @param out
	 */
	public UserVMDataService(VelocityContext vsl_Context, Template template, PrintWriter out, HttpServletRequest request){
		this.vsl_Context = vsl_Context;
		this.template = template;
		this.out = out;
		this.request = request;
	}
	
	/**
	 * This method is for calling all the user VMs. This will be done with
	 * making a connection to the back-end servlet that will respond with JSON
	 * The VMS will be called with the use of the sessions user ID
	 * 
	 * @param request
	 * @param response
	 * @param sessionUserID
	 */
	public void getAllUserVMs(long sessionUserID) {
		/**
		 * Setting URL and Bean for giving data to Velocity
		 */
		String getAllVMUrl = "http://localhost:8080/BackEnd/customer/controlpanel/getvms?request=getallvm&userID="
				+ sessionUserID;
		ArrayList<VMBean> vmBeanArray = null;

		/**
		 * Try to get the JSON data and parse it
		 */
		try {

			// Getting a JSON array from an URL
			JSONArray json = JsonGETParser.readJsonArrayFromUrl(getAllVMUrl);

			/**
			 * If JSON is not null, a connection could be made
			 */
			if (json != null) {

				/**
				 * Check if the json String contains an error
				 */
				if (!json.contains("error")) {

					// Setting JSON object for the arrayList, to localy safe the
					// data
					ArrayList<JSONObject> listdata = new ArrayList<JSONObject>();
					JSONArray jArray = json;

					// If the array is not null, save the data
					if (jArray != null) {
						for (int i = 0; i < jArray.size(); i++) {
							listdata.add((JSONObject) jArray.get(i));
						}
					}

					/**
					 * Saving the data inside a new Bean for simple calling
					 * inside Velocity
					 */
					vmBeanArray = new ArrayList<VMBean>();
					for (int i = 0; i < listdata.size(); i++) {
						VMBean bean = new VMBean();
						Gson gson = new Gson();
						bean = gson.fromJson(listdata.get(i).toString(), VMBean.class);
						vmBeanArray.add(bean);
					}

				} else {
					vsl_Context.put("error", json.get(0).equals("error"));
				}
			} else {
				vsl_Context.put("error",
						"Connection with server could not be made..");
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/**
		 * Finally, put the array inside Velocity to work with and get the
		 * template again
		 */
		finally {
			vsl_Context.put("vmBeanArray", vmBeanArray);
		}
	}

	/**
	 * This method is for calling a specific user VM. This will be done with
	 * making a connection to the back-end servlet that will respond with JSON
	 * The VMS will be called with the use of the sessions user ID and the ID
	 * for the virtual machine (taken from the bean in the last session)
	 * 
	 * @param request
	 * @param response
	 * @param sessionUserID
	 */
	public void getUserVMs(long sessionUserID) {

		// Get VMID from parameter
		String parameterVMID = request.getParameter("vmid");

		// Setting the URL for getting data from the back-end
		String getSpecifiedVMUrl = "http://localhost:8080/BackEnd/customer/controlpanel/getvms?request=getvm&vmid="
				+ parameterVMID + "&userID=" + sessionUserID;

		// Setting up the bean
		VMBean vmBean = null;
		
		/**
		 * Try to parse object from the given URL
		 */
		try {
			JSONObject json = JsonGETParser
					.readJsonObjectFromUrl(getSpecifiedVMUrl);

			/**
			 * If the JSON object is not null, a connection could be made
			 */
			if (json != null) {

				/**
				 * Check if the json String contains an error
				 */
				if (!json.containsKey("error")) {
					Gson gson = new Gson();
					vmBean = gson.fromJson(json.toString(), VMBean.class);

				} else {
					vsl_Context.put("error", json.get("error"));
				}
			}

			/**
			 * Else, show an error
			 */
			else {
				vsl_Context.put("error",
						"Connection with server could not be made..");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/**
		 * Finally, put the objects inside the template
		 */
		finally {
			vsl_Context.put("vm", vmBean);
		}
	}
	
	/**
	 * Method for getting realtime data from the back-end servlet. The back-end will make a connection with the libvirt server
	 * @param request
	 * @param response
	 * @param sessionUserID
	 */
	public void getRealtimeVMData(long sessionUserID){
		// Get VMID from parameter
		String parameterVMID = request.getParameter("vmid");
		
		// Setting the URL for getting data from the back-end
		String getRealtimeVMDataUrl = "http://localhost:8080/BackEnd/customer/controlpanel/monitorvms?vmid=" + parameterVMID + "&userID=" +sessionUserID;

		// Setting up the bean
		VMBean vmBean = null;
		
		/**
		 * Try to parse object from the given URL
		 */
		try {
			JSONObject json = JsonGETParser
					.readJsonObjectFromUrl(getRealtimeVMDataUrl);

			/**
			 * If the JSON object is not null, a connection could be made
			 */
			if (json != null) {

				/**
				 * Check if the json String contains an error
				 */
				if (!json.containsKey("error")) {
					Gson gson = new Gson();
					vmBean = gson.fromJson(json.toString(), VMBean.class);

				} else {
					vsl_Context.put("error", json.get("error"));
				}
			}

			/**
			 * Else, show an error
			 */
			else {
				vsl_Context.put("error",
						"Connection with server could not be made..");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/**
		 * Finally, put the objects inside the template
		 */
		finally {
			vsl_Context.put("vm", vmBean);
		}
		
		
		
	}
	
}
