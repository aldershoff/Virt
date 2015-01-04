package processAdminServices;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import databaseAccessObjects.AdminDAO;

public class ProcessAdminPermissionsService {

	private HttpServletRequest request;
	private HttpServletResponse response;

	public ProcessAdminPermissionsService(HttpServletRequest request,
			HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	@SuppressWarnings("unchecked")
	public void setPermissions(String userType) throws IOException, NumberFormatException {

		/**
		 * Getting all parameters needed for creating VM
		 */
		String userID = request.getParameter("userID");

		// Make new DAO
		AdminDAO adminDAO = new AdminDAO();

		int result = 0;
		
		if(userType.equals("user")){
			result = adminDAO.setAdminToUser(userID);
		}
		else{
			result = adminDAO.setAdmin(userID);
		}

		JSONObject jobj = new JSONObject();

		try {
			if (result == 1) {
				jobj.put("success", "Successfully updated user permissions!");
			} else if (result == 2) {
				jobj.put("error", "Could not connect with database..");
			} else {
				jobj.put("error", "Something went wrong...");
			}
		} finally {
			response.getWriter().write(jobj.toString());
		}

	}
	
	
}
