package processAdminServices;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jsonserializers.GetUserDetailsSerialiser;

import org.json.simple.JSONObject;

import beans.CustomerBean;
import beans.VMBean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import databaseAccessObjects.AdminDAO;
import databaseAccessObjects.UserDAO;

public class ProcessAdminUserOverviewService {

	private HttpServletRequest request;
	private HttpServletResponse response;

	public ProcessAdminUserOverviewService(HttpServletRequest request,
			HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	@SuppressWarnings("unchecked")
	public void getAllUsers() throws IOException {

		// Making new VMDAO for making connection
		AdminDAO adminDAO = new AdminDAO();

		/**
		 * Setting GSON variables
		 */
		Gson gson = new Gson();
		JSONObject jobj = new JSONObject();
		String json = "";
		String error = "";

		/**
		 * Making new bean and fill it with data
		 */
		ArrayList<CustomerBean> customerBeanArray = new ArrayList<CustomerBean>();
		;

		customerBeanArray = adminDAO.getUserOverview();

		try {
			/**
			 * If the array is not null, a new JSON String will be made and will
			 * be send to the output
			 */
			if (customerBeanArray != null) {

				json = gson.toJson(customerBeanArray);

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
