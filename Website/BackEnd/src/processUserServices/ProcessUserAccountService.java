package processUserServices;

import java.io.IOException;

import javax.naming.ServiceUnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jsonserializers.CustomerSerialiser;

import org.json.simple.JSONObject;

import security.PasswordService;
import beans.CustomerBean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import databaseAccessObjects.UserDAO;

public class ProcessUserAccountService {
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	public ProcessUserAccountService(HttpServletRequest request, HttpServletResponse response){
		this.request = request;
		this.response = response;
	}
	
	
	/**
	 * Method for processing the login from the Users presentation servlet. When
	 * authentication is completed, the user will be able to gain access to the
	 * AuthUsers servlet
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void processLogin() throws IOException {

		// Making new bean for the customer
		CustomerBean custBean = new CustomerBean();

		String parameterUsername = request.getParameter("user");
		String parameterPassword = request.getParameter("password");
		String parameterRememberme = request.getParameter("rememberme");

		// Getting parameters from form (login.html)
		custBean.setUserName(parameterUsername);
		try {
			custBean.setPassword(new PasswordService().getInstance().encrypt(
					parameterPassword));
		} catch (ServiceUnavailableException e) {
			e.printStackTrace();
		}

		// Making new login Data Access Object
		UserDAO checkLog = new UserDAO();

		// Make connection, fill bean and return it
		custBean = checkLog.login(custBean);

		/**
		 * If rememberMe is checked, a cookie will be exchanged to the user
		 */
		if (parameterRememberme != null) {

			custBean.setRememberMe(true);
		}

		Gson gson = new Gson();
		JSONObject jobj = new JSONObject();
		String error = "";
		String json = "";

		try {
			if (custBean != null) {

				if(custBean.isValid()){
					/**
					 * Set the gson serialiser for appropriate data and
					 * converting the customer bean to JSON string
					 */
					final GsonBuilder gsonBuilder = new GsonBuilder();
					gsonBuilder.registerTypeAdapter(CustomerBean.class,
							new CustomerSerialiser());
					gson = gsonBuilder.create();
					json = gson.toJson(custBean);
				}
					
					else{
						error = "Wrong username or password";
						jobj.put("error", error);
					}
			} else {
				error = "Could not connect to database";
				jobj.put("error", error);
			}
			
			
		} finally {
			response.setContentType("application/json");
			if(error != ""){
				response.getWriter().write(jobj.toString());		
			}
			else{
				response.getWriter().write(json.toString());
			}
		}
	}
	
	
	
	/**
	 * Method for processing the register from the Users presentation servlet.
	 * When registering is complete, the user can succesfully login into his own
	 * profile
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServiceUnavailableException
	 */
	@SuppressWarnings("unchecked")
	public void processRegister() throws IOException {

		// Making new bean for the customer
		CustomerBean custBean = new CustomerBean();

		// Getting parameters from form (login.html)
		custBean.setUserName(request.getParameter("user"));

		try {
			custBean.setPassword(new PasswordService().getInstance().encrypt(
					request.getParameter("password")));
		} catch (ServiceUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/**
		 * Setting all the requests into the bean for processing
		 */
		custBean.setEmail(request.getParameter("email"));

		// Making new login Data Access Object
		UserDAO checkLog = new UserDAO();

		// Make connection, fill bean and return it
		custBean = checkLog.register(custBean);

		Gson gson = new Gson();
		JSONObject jobj = new JSONObject();
		String error = "";
		String json = "";

		try {

			// If bean is not null, a database connection has been initiated
			if (custBean != null) {

				/**
				 * If the user is succesfully authenticated, and the bean has
				 * been filled with information, a new session can be made
				 */

				if (custBean.isValid()) {
					final GsonBuilder gsonBuilder = new GsonBuilder();
					gsonBuilder.registerTypeAdapter(CustomerBean.class,
							new CustomerSerialiser());
					gson = gsonBuilder.create();
					json = gson.toJson(custBean);

				} else {
					error = "Something went wrong while registering..";
					jobj.put("error", error);
				}
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
