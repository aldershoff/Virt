package processAdminServices;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jsonserializers.GetAdminAllVMSerialiser;

import org.json.simple.JSONObject;

import beans.VMBean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import databaseAccessObjects.AdminDAO;

public class ProcessAdminVMData {
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	
	public ProcessAdminVMData(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}
	
	@SuppressWarnings("unchecked")
	public void getAllVM() throws IOException {

		// Making new VMDAO for making connection
		AdminDAO adminDAO = new AdminDAO();
		String userID = null;

		/**
		 * Setting GSON variables
		 */
		Gson gson = new Gson();
		JSONObject jobj = new JSONObject();
		String json = "";
		String error = "";

		// Setting the userID and new arraylist for getting all the VMs
		userID = request.getParameter("userID");
		ArrayList<VMBean> vmBeanArray = adminDAO.getAllVMs(userID);

		try {
			/**
			 * If the array is not null, a new JSON String will be made and will
			 * be send to the output
			 */
			if (vmBeanArray != null) {

				/**
				 * Set the gson serialiser for appropriate data and
				 * converting the customer bean to JSON string
				 */
				final GsonBuilder gsonBuilder = new GsonBuilder();
				gsonBuilder.registerTypeAdapter(VMBean.class,
						new GetAdminAllVMSerialiser());
				gson = gsonBuilder.create();
				json = gson.toJson(vmBeanArray);

			} else {
				error = "Could not connect to database";
				jobj.put("error", error);
			}
		} finally {
			response.setContentType("application/json");
			if (error != "") {
				response.getWriter().write(jobj.toString());

			} else {
				response.getWriter().write(json.toString());
			}
		}
	}

}
