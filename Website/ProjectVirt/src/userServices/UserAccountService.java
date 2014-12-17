package userServices;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.naming.ServiceUnavailableException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import json.JsonPOSTParser;
import net.sf.json.JSONException;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.json.simple.JSONObject;

import security.PasswordService;

public class UserAccountService {

	private VelocityContext vsl_Context;
	private Template template;
	private PrintWriter out;

	public UserAccountService(VelocityContext vsl_Context, Template template,
			PrintWriter out) {
		this.vsl_Context = vsl_Context;
		this.template = template;
		this.out = out;
	}

	/**
	 * Method for registering the user with the form
	 * 
	 * @param request
	 * @param response
	 */
	public void registerUser(HttpServletRequest request,
			HttpServletResponse response) {

		/**
		 * Try to get all parameters and if not, show the error
		 */
		try {
			if (request.getParameter("user") == ""
					|| request.getParameter("password") == ""
					|| request.getParameter("firstname") == ""
					|| request.getParameter("lastname") == ""
					|| request.getParameter("user") == "") {
				vsl_Context.put("error", "Not all fields are filled!");
			} else if (request.getParameter("accept") == null) {
				vsl_Context
						.put("error", "Please accept the terms of agreement");
			}

			else {
				/**
				 * Set postparameters to give with the request
				 */
				ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
				postParameters.add(new BasicNameValuePair("user", request
						.getParameter("user")));
				postParameters.add(new BasicNameValuePair("password", request
						.getParameter("password")));
				postParameters.add(new BasicNameValuePair("dateofbirth",
						request.getParameter("dateofbirth")));
				postParameters.add(new BasicNameValuePair("firstname", request
						.getParameter("firstname")));
				postParameters.add(new BasicNameValuePair("lastname", request
						.getParameter("lastname")));
				postParameters.add(new BasicNameValuePair("email", request
						.getParameter("email")));
				postParameters.add(new BasicNameValuePair("phone", request
						.getParameter("phone")));
				postParameters.add(new BasicNameValuePair("address", request
						.getParameter("address")));
				postParameters.add(new BasicNameValuePair("zipcode", request
						.getParameter("zipcode")));

				JSONObject json = JsonPOSTParser
						.postJsonFromUrl(
								request,
								"http://localhost:8080/BackEnd/register/processregister",
								postParameters);

				/**
				 * Try to parse the JSON Object, given by the server
				 */
				try {

					/**
					 * If json is null, connection could not be made
					 */
					if (json != null) {

						/**
						 * If the json String does not contain an error message,
						 * proceed with session
						 */
						if (!json.containsKey("error")) {

							// Boolean for extra check
							boolean userIsValid = (boolean) json.get("valid");

							/**
							 * If the user is valid, a session will be made and
							 * the session variables will be set
							 */
							if (userIsValid) {

								vsl_Context.put("success",
										"Register success! You can now login.");
							} else {
								vsl_Context.put("error", json.get("error"));
							}
						}

					} else {
						vsl_Context.put("error",
								"Connection with server could not be made..");

					}
				} finally {
					template = Velocity.getTemplate("Velocity/register.html");
				}
			}

			/**
			 * Finally statement for saving the user input when an error code
			 * appeared
			 */
		} finally {
			vsl_Context.put("username", request.getParameter("user"));
			vsl_Context.put("dateofbirth", request.getParameter("dateofbirth"));
			vsl_Context.put("firstname", request.getParameter("firstname"));
			vsl_Context.put("lastname", request.getParameter("lastname"));
			vsl_Context.put("email", request.getParameter("email"));
			vsl_Context.put("phone", request.getParameter("phone"));
			vsl_Context.put("address", request.getParameter("address"));
			vsl_Context.put("zipcode", request.getParameter("zipcode"));
			template = Velocity.getTemplate("Velocity/register.html");
		}

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
	public void loginUser(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		if (!request.getParameterMap().containsKey("pinsubmit")) {

			/**
			 * Checking if the fields were not left empty
			 */

			if (request.getParameter("user") == ""
					&& request.getParameter("password") == "") {
				vsl_Context.put("error", "Must provide username and password!");

			} else if (request.getParameter("user") == "") {
				vsl_Context.put("error", "Must provide username!");

			} else if (request.getParameter("password") == "") {
				vsl_Context.put("error", "Must provide password!");
			}

			/**
			 * If everything was filled in correctly, the loginprocess can begin
			 */
			else {

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

				try {
					/**
					 * If json is null, connection could not be made
					 */
					if (json != null) {

						/**
						 * Checking if the key does not contain an error
						 * parameter and an extra check if the user is given
						 * valid
						 */
						if (!json.containsKey("error")) {

							// Setting boolean for validating the user (extra
							// check)
							boolean isValid = (Boolean) json.get("valid");

							/**
							 * Doing extra check to be sure that a session will
							 * be made IF and ONLY IF the user is declared
							 * valid. Will also check if Android requested the
							 * login service
							 */
							if (isValid
									&& !request.getParameterMap().containsKey(
											"shareRegId")) {

								/**
								 * Get the JSON variables
								 */
								long userID = (long) json.get("userID");
								String username = (String) json.get("username");

								/**
								 * If the user is valid, a session will be made
								 * and the session variables will be set
								 */
								HttpSession session = request.getSession();
								session.setAttribute("userID",
										(long) json.get("userID"));
								session.setAttribute("username",
										(String) json.get("username"));

								/**
								 * If twofactor was chosen by the user, proceed
								 * with implementation with two factor
								 * authentication
								 */
								if (request.getParameter("twofactor") != null) {

									setLoginTwoFactorSendMessage(session,
											request, userID);

								}

								/**
								 * Else, just login with redirection
								 */
								else {
									response.sendRedirect("/ProjectVirt/customer/home");
								}

							}

							/**
							 * If it contains the requested URL parameter
							 * shareRegId, given by Android, it will respond
							 * with a success message
							 */
							else if (request.getParameterMap().containsKey(
									"shareRegId")
									&& request.getParameter("shareRegId")
											.equals("1")) {

								setLoginAndroidTwoFactor(request, response,
										json);

							}

						} else {
							vsl_Context.put("error", json.get("error"));
						}
					} else {
						vsl_Context.put("error", "Can't connect with server");
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
					vsl_Context.put("username", request.getParameter("user"));
					template = Velocity.getTemplate("Velocity/login.html");
				}
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
	 * If the user's authentication was OK, the user will now get a message send to
	 * his Android device
	 * @param session
	 * @param request
	 * @param userID
	 * @throws ServiceUnavailableException
	 */
	private void setLoginTwoFactorSendMessage(HttpSession session,
			HttpServletRequest request, long userID)
			throws ServiceUnavailableException {
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
		GCMMessage.add(new BasicNameValuePair("userID", Long.toString(userID)));
		GCMMessage.add(new BasicNameValuePair("message", "Pincode: " + pin));

		/**
		 * Sending message to GCM server
		 */
		JSONObject responseMessage = JsonPOSTParser.postJsonFromUrl(request,
				"http://145.92.14.10:8080/GCM-Server/GCMNotification",
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
	 * If the user submitted the pin (after 1st factor login)
	 * this method will check if the pin was valid, by checking
	 * the random generated pin with the one inserted
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
		 * Encrypting the pin from parameter to see if they equal to each other
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
			vsl_Context.put("error", "pin incorrect");
			template = Velocity.getTemplate("Velocity/login.html");
		}
	}

	/**
	 * Method for logging in with an Android device. This is for getting the ID from
	 * the GCM service and inserting it into the database for sending PIN messages
	 * 
	 * @param request
	 * @param response
	 * @param json
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private void setLoginAndroidTwoFactor(HttpServletRequest request,
			HttpServletResponse response, JSONObject json) throws IOException {
		long userID = (long) json.get("userID");

		String regId = request.getParameter("regId");

		/**
		 * Send request for writing the key into the database
		 */
		ArrayList<NameValuePair> gcmLoginMessagePostParameter = new ArrayList<NameValuePair>();
		gcmLoginMessagePostParameter
				.add(new BasicNameValuePair("regId", regId));
		gcmLoginMessagePostParameter.add(new BasicNameValuePair("userID", Long
				.toString(userID)));
		JSONObject gcmLoginMessage = JsonPOSTParser
				.postJsonFromUrl(
						request,
						"http://145.92.14.10:8080/GCM-Server/GCMNotification?shareRegId=1",
						gcmLoginMessagePostParameter);

		JSONObject jobj = new JSONObject();
		if (gcmLoginMessage.get("success").equals("1")) {

			jobj.put("success", "1");
			response.getWriter().write(jobj.toString());
		} else if (gcmLoginMessage.get("success").equals("2")) {
			jobj.put("success", "2");
			response.getWriter().write(jobj.toString());
		} else {
			jobj.put("success", "0");
			response.getWriter().write(jobj.toString());
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
	public void logoutUser(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {

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
		response.sendRedirect(request.getRequestURI() + "/home");

		return;
	}

}
