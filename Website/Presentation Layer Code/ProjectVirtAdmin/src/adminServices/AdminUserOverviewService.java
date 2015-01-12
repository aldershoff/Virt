package adminServices;

import java.io.IOException;
import java.util.ArrayList;

import json.JsonGETParser;
import net.sf.json.JSONException;

import org.apache.velocity.VelocityContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import beans.CustomerBean;

import com.google.gson.Gson;

public class AdminUserOverviewService {
	
	private VelocityContext vsl_Context;
	
	public AdminUserOverviewService(VelocityContext vsl_Context){
		this.vsl_Context = vsl_Context;
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
	public void getAllUsers() {
		/**
		 * Setting URL and Bean for giving data to Velocity
		 */
		String getAllUsersUrl = "http://localhost:8080/BackEnd/admin/overview/getusers?request=getallusers";
		ArrayList<CustomerBean> customerBeanArray = null;

		/**
		 * Try to get the JSON data and parse it
		 */
		try {

			// Getting a JSON array from an URL
			JSONArray json = JsonGETParser.readJsonArrayFromUrl(getAllUsersUrl);

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
					customerBeanArray = new ArrayList<CustomerBean>();
					for (int i = 0; i < listdata.size(); i++) {
						CustomerBean bean = new CustomerBean();
						Gson gson = new Gson();
						bean = gson.fromJson(listdata.get(i).toString(), CustomerBean.class);
						customerBeanArray.add(bean);
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
			vsl_Context.put("customerBeanArray", customerBeanArray);
		}
	}

}
