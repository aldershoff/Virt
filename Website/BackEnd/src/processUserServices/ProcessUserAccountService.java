package processUserServices;

import java.io.IOException;

import javax.naming.ServiceUnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jsonserializers.GetUserDetailsSerialiser;
import jsonserializers.LoginUserSerialiser;

import org.json.simple.JSONObject;

import security.PasswordService;
import beans.CustomerBean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import databaseAccessObjects.UserDAO;

public class ProcessUserAccountService {

	private HttpServletRequest request;
	private HttpServletResponse response;

	public ProcessUserAccountService(HttpServletRequest request,
			HttpServletResponse response) {
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

				if (custBean.isValid()) {
					/**
					 * Set the gson serialiser for appropriate data and
					 * converting the customer bean to JSON string
					 */
					final GsonBuilder gsonBuilder = new GsonBuilder();
					gsonBuilder.registerTypeAdapter(CustomerBean.class,
							new LoginUserSerialiser());
					gson = gsonBuilder.create();
					json = gson.toJson(custBean);
				}

				else {
					error = "Wrong username or password";
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
		custBean.setFirstName(request.getParameter("firstname"));
		custBean.setLastName(request.getParameter("lastname"));
		custBean.setEmail(request.getParameter("email"));
		custBean.setCompany(request.getParameter("company"));
		custBean.setPhone(request.getParameter("phone"));
		custBean.setAddress(request.getParameter("address"));
		custBean.setZipCode(request.getParameter("zipcode"));
		
		/**
		 * Setting the twofactor if checked
		 */
		if (request.getParameter("twofactor").equals("1")) {
			custBean.setTwoFactor(1);
		} else {
			custBean.setTwoFactor(0);
		}

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
							new LoginUserSerialiser());
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

	@SuppressWarnings("unchecked")
	public void processGetUserDetails() throws IOException {
		// Making new VMDAO for making connection
		UserDAO userDAO = new UserDAO();
		String userID = null;

		/**
		 * Setting GSON variables
		 */
		Gson gson = new Gson();
		JSONObject jobj = new JSONObject();
		String json = "";
		String error = "";

		/**
		 * Setting the parameters for the User ID
		 */
		userID = request.getParameter("userID");

		/**
		 * Making new bean and fill it with data
		 */
		CustomerBean custBean = new CustomerBean();
		custBean.setUserID(Integer.parseInt(userID));

		custBean = userDAO.getUserDetails(custBean);

		try {
			if (custBean != null) {

				/**
				 * If the bean is valid, it will return a new JSON response
				 */
				if (custBean.isValid()) {

					/**
					 * Setting builder and serialiser for appropiate data
					 */
					final GsonBuilder gsonBuilder = new GsonBuilder();
					gsonBuilder.registerTypeAdapter(CustomerBean.class,
							new GetUserDetailsSerialiser());
					gson = gsonBuilder.create();
					json = gson.toJson(custBean);
				} else {
					error = "Something is wrong with your profile...";
					jobj.put("error", error);
				}
			} else {
				error = "Can't connect with database";
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

	@SuppressWarnings("unchecked")
	public void processUpdateUserProfileDetails() throws IOException {
		// Making new bean for the customer
		CustomerBean custBean = new CustomerBean();

		/**
		 * Setting all the requests into the bean for processing
		 */
		custBean.setFirstName(request.getParameter("firstname"));
		custBean.setLastName(request.getParameter("lastname"));
		custBean.setCompany(request.getParameter("company"));
		custBean.setEmail(request.getParameter("email"));
		custBean.setPhone(request.getParameter("phone"));
		custBean.setAddress(request.getParameter("address"));
		custBean.setZipCode(request.getParameter("zipcode"));
		custBean.setUserID(Integer.parseInt(request.getParameter("userID")));
		custBean.setUserName(request.getParameter("username"));

		/**
		 * Setting the twofactor if checked
		 */
		if (request.getParameter("twofactor").equals("yes")) {
			custBean.setTwoFactor(1);
		} else {
			custBean.setTwoFactor(0);
		}

		// Making new login Data Access Object
		UserDAO setUserProfile = new UserDAO();

		// Make connection, fill bean and return it
		custBean = setUserProfile.updateUserProfileDetails(custBean);

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
							new GetUserDetailsSerialiser());
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

	/**
	 * Method for processing the request for updating the account details
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void processUpdateUserAccountDetails() throws IOException {
		// Making new bean for the customer
		CustomerBean custBean = new CustomerBean();

		
		/**
		 * Setting all the requests into the bean for processing
		 */
		custBean.setFirstName(request.getParameter("firstname"));
		custBean.setLastName(request.getParameter("lastname"));
		custBean.setCompany(request.getParameter("company"));
		custBean.setEmail(request.getParameter("email"));
		custBean.setPhone(request.getParameter("phone"));
		custBean.setAddress(request.getParameter("address"));
		custBean.setZipCode(request.getParameter("zipcode"));
		custBean.setUserID(Integer.parseInt(request.getParameter("userID")));
		custBean.setUserName(request.getParameter("username"));

		/**
		 * Setting the twofactor if checked
		 */
		if (request.getParameter("twofactor").equals("yes")) {
			custBean.setTwoFactor(1);
		} else {
			custBean.setTwoFactor(0);
		}
		
		try {
			custBean.setPassword(new PasswordService().getInstance().encrypt(
					request.getParameter("password")));
		} catch (ServiceUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Making new login Data Access Object
		UserDAO updateAccount = new UserDAO();

		// Make connection, fill bean and return it
		custBean = updateAccount.updateUserAccountDetails(custBean);

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
							new GetUserDetailsSerialiser());
					gson = gsonBuilder.create();
					json = gson.toJson(custBean);

				} else {
					error = "Something went wrong while updating your account..";
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
