package adminServices;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.naming.ServiceUnavailableException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import json.JsonGETParser;
import json.JsonPOSTParser;
import net.sf.json.JSONException;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;

import beans.CustomerBean;
import security.PasswordService;

/**
 * Service for the account of the user. e.d. like logging in, registering and
 * logging out.
 * 
 * @author KjellZijlemaker
 * @version 1.0
 * @since 1.0
 */
public class AdminAccountService {

	/**
	 * Variables that will be used globally
	 */
	private VelocityContext vsl_Context;
	private Template template;
	private PrintWriter out;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private boolean isAndroid;

	/**
	 * Constructor for initialing the global variables
	 * 
	 * @param vsl_Context
	 * @param template
	 * @param out
	 */
	public AdminAccountService(VelocityContext vsl_Context, Template template,
			PrintWriter out, HttpServletRequest request,
			HttpServletResponse response) {
		this.vsl_Context = vsl_Context;
		this.template = template;
		this.out = out;
		this.request = request;
		this.response = response;
	}

	/**
	 * Method for posting the submit form to the back-end server. It will check
	 * and give a valid JSON, with the user, back. If it will not give any JSON
	 * content back, the user is invalid
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public void loginAdmin() throws IOException {

		/**
		 * If the request contains the pinsubmit, the user is logging in the
		 * without the two-factor authentication pin-submit
		 */
		if (!request.getParameterMap().containsKey("pinsubmit")) {

			if (request.getParameterMap().containsKey("isAndroid")) {
				isAndroid = true;
			}

			/**
			 * Checking if the fields were not left empty
			 */
			if (request.getParameter("user") == ""
					&& request.getParameter("password") == "") {
				vsl_Context.put("error", "Must provide username and password!");
				vsl_Context.put("username", request.getParameter("user"));
				template = Velocity.getTemplate("Velocity/login.html");
				template.merge(vsl_Context, out);
				out.close();

			} else if (request.getParameter("user") == "") {
				vsl_Context.put("error", "Must provide username!");
				vsl_Context.put("username", request.getParameter("user"));
				template = Velocity.getTemplate("Velocity/login.html");
				template.merge(vsl_Context, out);
				out.close();
			} else if (request.getParameter("password") == "") {
				vsl_Context.put("error", "Must provide password!");
				vsl_Context.put("username", request.getParameter("user"));
				template = Velocity.getTemplate("Velocity/login.html");
				template.merge(vsl_Context, out);
				out.close();
			}

			/**
			 * If everything was filled in correctly, the loginprocess can begin
			 */
			else {
				setLoginOneFactor(request, response);

			}
		}

		/**
		 * Set the Pin for the two factor authentication
		 */
		else {
			setLoginTwoFactorSetPin(request, response);
		}

	}

	/**
	 * Method for the one factor login. In this method, if the user requested
	 * two factor, the service will call the two-factor login method
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked" })
	private void setLoginOneFactor(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		/**
		 * Set postparameters to give with the request
		 */
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("user", request
				.getParameter("user")));

		postParameters.add(new BasicNameValuePair("password", request
				.getParameter("password")));

		/**
		 * Set the url for JSON + the postparameters
		 */
		JSONObject json = JsonPOSTParser.postJsonFromUrl(request,
				"http://localhost:8080/BackEnd/login/processlogin",
				postParameters);

		JSONObject jobj = new JSONObject();

		try {

			/**
			 * If json is null, connection could not be made
			 */
			if (json != null) {

				/**
				 * Checking if the key does not contain an error parameter and
				 * an extra check if the user is given valid
				 */
				if (!json.containsKey("error")) {

					// Setting boolean for validating the user (extra
					// check)
					boolean isValid = (Boolean) json.get("valid");
					String userType = (String) json.get("userType");

					/**
					 * Check if the user is admin or not...
					 */
					if (userType.equals("admin")) {

						/**
						 * Check if the login is android and valid, then proceed
						 */
						checkAdminLogin(json, jobj, isValid);

					}
					/**
					 * Else, the user is not permitted to enter the
					 * authenticated admin page
					 */
					else {
						if (isAndroid) {
							jobj.put("error", "You are not permitted to login");
						} else {
							vsl_Context.put("error",
									"You are not permitted to login");
						}
					}
				}

				else {
					/**
					 * Checking if json response or Velocity response should be
					 * given
					 */
					if (isAndroid) {
						jobj.put("error", json.get("error"));
					} else {
						vsl_Context.put("error", json.get("error"));
					}
				}
			} else {
				/**
				 * Checking if json response or Velocity response should be
				 * given
				 */
				if (isAndroid) {
					jobj.put("error", "Can't connect with server");
				} else {
					vsl_Context.put("error", "Can't connect with server");
				}

			}

			/**
			 * Save user input when error code appeared
			 */
		} catch (ServiceUnavailableException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			/**
			 * Send the response with JSON, or print it with Velocity
			 */
			if (isAndroid) {
				response.setContentType("application/json");
				response.getWriter().write(jobj.toString());
			} else {
				vsl_Context.put("username", request.getParameter("user"));
				vsl_Context.put("baseUrl", request.getContextPath());
				template = Velocity.getTemplate("Velocity/login.html");
				template.merge(vsl_Context, out);
				out.close();
			}
		}
	}

	/**
	 * Check if the login is valid and is android
	 * @param json
	 * @param jobj
	 * @param isValid
	 * @throws ServiceUnavailableException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private void checkAdminLogin(JSONObject json, JSONObject jobj,
			boolean isValid) throws ServiceUnavailableException, IOException {
		/**
		 * Doing extra check to be sure that a session will be made IF and ONLY
		 * IF the user is declared valid. Will also check if Android requested
		 * the login service.
		 */
		if (isValid && !isAndroid) {
			// Set the user session and send the twofactor auth
			// message if checked
			setLoginAdminSession(request, response, json);

		}

		/**
		 * If the request came from Android, the user wants to register for the
		 * two factor authentication, the method will be called
		 */
		else if (isAndroid) {

			/**
			 * Get the JSON variables
			 */
			long userID = (long) json.get("userID");

			// Call the Android login authentication
			setLoginAndroidTwoFactor(request, response, userID);

		} else {
			/**
			 * Checking if json response or Velocity response should be given
			 */
			if (isAndroid) {
				jobj.put("error", json.get("error"));
			} else {
				vsl_Context.put("error", json.get("error"));
			}
		}

	}

	/**
	 * Set the session for the user when JSON checks were passed. This will only
	 * occur when request is not from Android
	 * 
	 * @param request
	 * @param response
	 * @param json
	 * @throws ServiceUnavailableException
	 * @throws IOException
	 */
	private void setLoginAdminSession(HttpServletRequest request,
			HttpServletResponse response, JSONObject json)
			throws ServiceUnavailableException, IOException {
		/**
		 * Get the JSON variables
		 */
		long userID = (long) json.get("userID");

		/**
		 * If the user is valid, a session will be made and the session
		 * variables will be set
		 */
		HttpSession session = request.getSession();
		session.setAttribute("userID", userID);
		session.setAttribute("username", (String) json.get("username"));

		long isTwoFactor = (long) json.get("userTwoFactor");

		/**
		 * If twofactor was chosen by the user, proceed with implementation with
		 * two factor authentication
		 */
		if (isTwoFactor == 1) {

			// Call the two-factor login authentication
			setLoginTwoFactorSendMessage(session, request, userID);

		}

		/**
		 * Else, just login with redirection
		 */
		else {
			response.sendRedirect("/ProjectVirtAdmin/admin/home");
		}
	}

	// GCM login / register shizzle
	// ==========================================================================

	/**
	 * If the user's authentication was OK, the user will now get a message send
	 * to his Android device
	 * 
	 * @param session
	 * @param request
	 * @param userID
	 * @throws ServiceUnavailableException
	 * @throws IOException
	 */
	private void setLoginTwoFactorSendMessage(HttpSession session,
			HttpServletRequest request, long userID)
			throws ServiceUnavailableException, IOException {

		/**
		 * Removing variables if they exist from other session
		 */
		vsl_Context.remove("success");
		vsl_Context.remove("error");

		String result = loginTwoFactorReadFromDatabase(Long.toString(userID),
				request);

		if (!result.isEmpty()) {
			// Set the pin
			String pin = (RandomStringUtils.random(6, true, true));

			// encrypt pin before putting it in the session
			session.setAttribute("pincode", new PasswordService().getInstance()
					.encrypt(pin));

			/**
			 * Making new post message for giving to the GCM server. Adding the
			 * userID and pin for authentication
			 */
			ArrayList<NameValuePair> GCMMessage = new ArrayList<NameValuePair>();
			GCMMessage.add(new BasicNameValuePair("userID", Long
					.toString(userID)));
			GCMMessage
					.add(new BasicNameValuePair("message", "Pincode: " + pin));
			GCMMessage.add(new BasicNameValuePair("regId", result));

			/**
			 * Sending message to GCM server
			 */
			JSONObject responseMessage = JsonPOSTParser.postJsonFromUrl(
					request,
					"http://localhost:8080/GCM-Server/GCMNotification",
					GCMMessage);

			if (responseMessage.containsKey("error")) {
				vsl_Context.put("error", responseMessage.get("error"));
			} else if (responseMessage.containsKey("success")) {

				vsl_Context.put("success", responseMessage.get("success"));

				// Making new pin variable for showing
				// button
				vsl_Context.put("pin", "1");
			}
		}

		/**
		 * If nothing found, the error message will be displayed
		 */
		else {
			vsl_Context
					.put("error",
							"No android device found. Please download the app and login.");
			session.invalidate();
		}
	}

	/**
	 * If the user submitted the pin (after 1st factor login) this method will
	 * check if the pin was valid, by checking the random generated pin with the
	 * one inserted
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void setLoginTwoFactorSetPin(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession();
		String sessionPin = (String) session.getAttribute("pincode");

		/**
		 * If the session pin is not null, the user will still have to insert
		 * the pin
		 */
		if (sessionPin != null) {
			/**
			 * Encrypting the pin from parameter to see if they equal to each
			 * other
			 */
			String userPin = null;
			try {
				userPin = new PasswordService().getInstance().encrypt(
						(String) request.getParameter("pin"));
			} catch (ServiceUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			;

			/**
			 * If the pin equals to each other, authentication was successful
			 * Otherwise, a response message will be printed
			 */
			if (sessionPin.equals(userPin)) {
				session.removeAttribute("pincode");
				response.sendRedirect("/ProjectVirt/customer/home");
			} else {
				session.invalidate();
				vsl_Context.remove("success");
				vsl_Context.remove("pin");
				vsl_Context.put("error", "Pin incorrect, try to login again!");
				template = Velocity.getTemplate("Velocity/login.html");
				template.merge(vsl_Context, out);
				out.close();
			}
		} else {
			response.sendRedirect("/ProjectVirt/customer/home");
		}
	}

	/**
	 * Method for logging in with an Android device. This is for getting the ID
	 * from the GCM service and inserting it into the database for sending PIN
	 * messages
	 * 
	 * @param request
	 * @param response
	 * @param json
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private void setLoginAndroidTwoFactor(HttpServletRequest request,
			HttpServletResponse response, long userID) throws IOException {

		String regId = request.getParameter("regId");

		// Send the request for writing to the method
		long result = loginTwoFactorWriteToDatabase(regId,
				Long.toString(userID), request);
		JSONObject jsobj = new JSONObject();

		/**
		 * Getting the results and print it for the Android device
		 */
		try {
			if (result == 1) {
				jsobj.put("success",
						"Successfully shared your ID with PlainTech!");

			} else if (result == 2) {
				jsobj.put("error", "No database connection");
			} else {
				jsobj.put("error",
						"Something went wrong within the database...");
			}
		} finally {
			response.getWriter().write(jsobj.toString());
		}
	}

	/**
	 * Write the registration ID from the GCM server to the database for lookup
	 * 
	 * @param regId
	 * @param userID
	 * @param request
	 * @return
	 * @throws IOException
	 */
	private long loginTwoFactorWriteToDatabase(String regId, String userID,
			HttpServletRequest request) throws IOException {

		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("regID", regId));
		postParameters.add(new BasicNameValuePair("userID", userID));

		JSONObject json = JsonPOSTParser.postJsonFromUrl(request,
				"http://localhost:8080/BackEnd/customer/insertUserMobileID",
				postParameters);

		return (long) json.get("result");

	}

	/**
	 * Read and check the mobileID from the database..
	 * 
	 * @param regId
	 * @param userID
	 * @param request
	 * @return
	 * @throws IOException
	 */
	private String loginTwoFactorReadFromDatabase(String userID,
			HttpServletRequest request) throws IOException {
		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("userID", userID));

		JSONObject json = JsonPOSTParser.postJsonFromUrl(request,
				"http://localhost:8080/BackEnd/customer/getUserMobileID",
				postParameters);

		return (String) json.get("result");

	}

	public void getUserDetails(long sessionUserID) {

		// Setting the URL for getting data from the back-end
		String getUserDetailsUrl = "http://localhost:8080/BackEnd/customer/getprofiledetails?userID="
				+ sessionUserID;

		// Setting up the bean
		CustomerBean custBean = null;

		/**
		 * Try to parse object from the given URL
		 */
		try {
			JSONObject json = JsonGETParser
					.readJsonObjectFromUrl(getUserDetailsUrl);

			/**
			 * If the JSON object is not null, a connection could be made
			 */
			if (json != null) {

				/**
				 * Check if the json String contains an error
				 */
				if (!json.containsKey("error")) {
					Gson gson = new Gson();
					custBean = gson.fromJson(json.toString(),
							CustomerBean.class);
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
			vsl_Context.put("userDetails", custBean);


		}
	}

	@SuppressWarnings("null")
	public void updateUserProfileDetails(long sessionUserID) {

		// For putting data back to velocity
		CustomerBean custBean = new CustomerBean();

		/**
		 * Try to get all parameters and if not, show the error
		 */
		try {

			/**
			 * Set postparameters to give with the request
			 */
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("firstname", request
					.getParameter("firstname")));
			postParameters.add(new BasicNameValuePair("lastname", request
					.getParameter("lastname")));
			postParameters.add(new BasicNameValuePair("company", request
					.getParameter("company")));
			postParameters.add(new BasicNameValuePair("email", request
					.getParameter("email")));
			postParameters.add(new BasicNameValuePair("phone", request
					.getParameter("phone")));
			postParameters.add(new BasicNameValuePair("address", request
					.getParameter("address")));
			postParameters.add(new BasicNameValuePair("zipcode", request
					.getParameter("zipcode")));
			postParameters.add(new BasicNameValuePair("twofactor", request
					.getParameter("twofactor")));
			postParameters.add(new BasicNameValuePair("userID", Long
					.toString(sessionUserID)));
			postParameters.add(new BasicNameValuePair("username", request
					.getParameter("username")));

			JSONObject json = JsonPOSTParser
					.postJsonFromUrl(
							request,
							"http://localhost:8080/BackEnd/customer/profile/updateprofiledetails",
							postParameters);

			/**
			 * If json is null, connection could not be made
			 */
			if (json != null) {

				/**
				 * If the json String does not contain an error message, proceed
				 * with session
				 */
				if (!json.containsKey("errorprofile")) {

					Gson gson = new Gson();
					custBean = gson.fromJson(json.toString(),
							CustomerBean.class);
					vsl_Context.put("successprofile",
							"Successfully changed your profile settings!");

				}

			} else {
				vsl_Context.put("error", json.get("errorprofile"));

			}

		}

		/**
		 * Finally statement for saving the user input when an error code
		 * appeared
		 */
		finally {
			vsl_Context.put("userDetails", custBean);
			
		}
	}

	/**
	 * Method for updating the account details!
	 * 
	 * @param sessionUserID
	 */
	@SuppressWarnings("null")
	public void updateUserAccountDetails(long sessionUserID) {

		// For putting data back to velocity
		CustomerBean custBean = new CustomerBean();

		try {
			if (!request.getParameter("password").equals(
					request.getParameter("passwordconfirm"))) {
				vsl_Context.put("erroraccount", "Passwords didn't match!");
				custBean.setFirstName(request.getParameter("firstname"));
				custBean.setLastName(request.getParameter("lastname"));
				custBean.setCompany(request.getParameter("company"));
				custBean.setEmail(request.getParameter("email"));
				custBean.setEmail(request.getParameter("phone"));
				custBean.setEmail(request.getParameter("address"));
				custBean.setEmail(request.getParameter("zipcode"));
				custBean.setUserName(request.getParameter("username"));
			} else if (request.getParameter("password").isEmpty()
					|| request.getParameter("passwordconfirm").isEmpty()) {
				vsl_Context.put("erroraccount", "Fields are empty!");
				custBean.setFirstName(request.getParameter("firstname"));
				custBean.setLastName(request.getParameter("lastname"));
				custBean.setCompany(request.getParameter("company"));
				custBean.setEmail(request.getParameter("email"));
				custBean.setEmail(request.getParameter("phone"));
				custBean.setEmail(request.getParameter("address"));
				custBean.setEmail(request.getParameter("zipcode"));
				custBean.setUserName(request.getParameter("username"));
			} else {

				/**
				 * Set postparameters to give with the request
				 */
				ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
				postParameters.add(new BasicNameValuePair("username", request
						.getParameter("username")));
				postParameters.add(new BasicNameValuePair("firstname", request
						.getParameter("firstname")));
				postParameters.add(new BasicNameValuePair("lastname", request
						.getParameter("lastname")));
				postParameters.add(new BasicNameValuePair("company", request
						.getParameter("company")));
				postParameters.add(new BasicNameValuePair("email", request
						.getParameter("email")));
				postParameters.add(new BasicNameValuePair("phone", request
						.getParameter("phone")));
				postParameters.add(new BasicNameValuePair("address", request
						.getParameter("address")));
				postParameters.add(new BasicNameValuePair("zipcode", request
						.getParameter("zipcode")));
				postParameters.add(new BasicNameValuePair("twofactor", request
						.getParameter("twofactor")));
				postParameters.add(new BasicNameValuePair("userID", Long
						.toString(sessionUserID)));
				postParameters.add(new BasicNameValuePair("password", request
						.getParameter("password")));

				JSONObject json = JsonPOSTParser
						.postJsonFromUrl(
								request,
								"http://localhost:8080/BackEnd/customer/profile/updateaccountdetails",
								postParameters);

				/**
				 * If json is null, connection could not be made
				 */
				if (json != null) {

					/**
					 * If the json String does not contain an error message,
					 * proceed with session
					 */
					if (!json.containsKey("error")) {

						Gson gson = new Gson();
						custBean = gson.fromJson(json.toString(),
								CustomerBean.class);
						vsl_Context.put("successaccount",
								"Successfully changed your account settings!");
					}

				} else {
					vsl_Context.put("erroraccount", json.get("error"));

				}

			}

		}
		/**
		 * Finally statement for saving the user input when an error code
		 * appeared
		 */
		finally {
			vsl_Context.put("userDetails", custBean);
			template = Velocity.getTemplate("Velocity/customers/profile.html");
		}
	}

	/**
	 * Method for destroying the usersession when logging out
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServiceUnavailableException
	 */
	public void logoutUser() throws IOException {

		HttpSession session = request.getSession();
		if (session != null) {
			session.invalidate();
		}

		// Setting cookie
		Cookie loginCookie = null;

		// Getting cookie
		Cookie[] cookies = request.getCookies();

		/**
		 * If not null, set logincookie to cookie for destroying it
		 */
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("user")) {
					loginCookie = cookie;
					break;
				}
			}
		}

		/**
		 * Setting the age to 0 and add cookie cookie to the response again. The
		 * cookie is now destroyed
		 */
		if (loginCookie != null) {
			loginCookie.setMaxAge(0);
			response.addCookie(loginCookie);
		}

		// Redirect to home page
		response.sendRedirect("/ProjectVirtAdmin/login");

		return;
	}

}
