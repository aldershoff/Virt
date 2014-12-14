package presentation;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.ServiceUnavailableException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import json.JsonGETParser;
import json.JsonPOSTParser;
import net.sf.json.JSONException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import beans.VMBean;

@WebServlet(name = "/Users", loadOnStartup = 1)

/**
 * Servlet implementation class Users.
 * This Servlet is for all users and can access
 * public information
 * @author kjellzijlemaker
 *
 */
public class Users extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private VelocityContext vsl_Context = new VelocityContext();
	private Template template = new Template();
	private PrintWriter out;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Users() {
		super();
	}

	/**
	 * Making new singleton velocity template processor
	 */
	@Override
	public void init() {
		Properties _Properties = new Properties();
		_Properties.setProperty("resource.loader", "webapp");
		_Properties
				.setProperty("webapp.resource.loader.class",
						"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		ServletContext _ServletContext = getServletContext();
		Velocity.setApplicationAttribute("javax.servlet.ServletContext",
				_ServletContext);
		try {
			Velocity.init(_Properties);
		} catch (Exception ex) {
			Logger.getLogger(Users.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		try {
			// Setting the writer
			out = response.getWriter();

			// Setting the servletpath and sending it to the Switch method
			String userPath = request.getRequestURI().substring(
					request.getContextPath().length());

			/**
			 * If the userpath does not start with customer, the user will
			 * browse through this method
			 */
			if (!userPath.startsWith("/customer")) {
				setGETCustomerUrlTemplates(userPath);
			}

			/**
			 * If the userpath starts with customer, an authenticated url has
			 * been requested. In this case, the filter will be activated and
			 * will check for a legit session. If the session is legit, the
			 * session will be made
			 */
			else {
				// Making session
				HttpSession session = request.getSession(false);

				// Go to the authenticatedUrlTemplates
				setGETCustomerAuthenticatedUrlTemplates(userPath, session,
						response, request);
			}

		}

		catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}

		/**
		 * When done, merge template and close the writer for user
		 */
		finally {
			template.merge(vsl_Context, out);
			cleanVelocity();
			vsl_Context.put("baseUrl", request.getContextPath());
			out.close();

		}

	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Setting the writer
		out = response.getWriter();

		// Setting the servletpath and sending it to the Switch method
		String userPath = request.getRequestURI().substring(
				request.getContextPath().length());

		try {
			/**
			 * If the userpath does not start with customer, the user will
			 * browse through this method
			 */
			if (!userPath.startsWith("/customer")) {
				setPOSTCustomerUrls(userPath, response, request);
			}

			/**
			 * If the userpath starts with customer, an authenticated url has
			 * been requested. In this case, the filter will be activated and
			 * will check for a legit session. If the session is legit, the
			 * session will be made
			 */
			else {
				// Making session
				HttpSession session = request.getSession(false);

				// Go to the authenticatedUrlTemplates
				setPOSTCustomerAuthenticatedUrls(userPath, session, response,
						request);

			}
		} finally {
			template.merge(vsl_Context, out);
			cleanVelocity();
			vsl_Context.put("baseUrl", request.getContextPath());
			out.close();
		}

	}

	/**
	 * Setting the non-privileged URLS
	 * 
	 * @param userPath
	 */
	private void setGETCustomerUrlTemplates(String userPath) {
		switch (userPath) {
		case "/home":
			template = Velocity.getTemplate("Velocity/index.html");
			break;

		case "/about":
			template = Velocity.getTemplate("Velocity/about.html");
			break;

		case "/services":
			template = Velocity.getTemplate("Velocity/services.html");
			break;

		case "/hosts":
			template = Velocity.getTemplate("Velocity/hosts.html");
			break;

		case "/login":
			template = Velocity.getTemplate("Velocity/login.html");
			break;

		case "/register":
			template = Velocity.getTemplate("Velocity/register.html");
			break;
		}
	}

	/**
	 * Setting the privileged URLS. Here, the services will also be called upon
	 * 
	 * @param userPath
	 * @param session
	 * @param response
	 * @param request
	 * @throws IllegalStateException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 * @throws RuntimeException
	 * @throws ParseException
	 */
	private void setGETCustomerAuthenticatedUrlTemplates(String userPath,
			HttpSession session, HttpServletResponse response,
			HttpServletRequest request) throws IllegalStateException,
			UnsupportedEncodingException, IOException, RuntimeException,
			ParseException {

		/**
		 * Getting the session username and userID
		 */
		String sessionUsername = (String) session.getAttribute("username");
		long sessionUserID = (long) session.getAttribute("userID");

		/**
		 * Making sure that the user has a valid session. If these variables are
		 * null, the user has not logged in properly
		 */
		if (sessionUsername != null && sessionUserID != 0) {

			// putting the username and ID in the context
			vsl_Context.put("name", sessionUsername);
			vsl_Context.put("id", sessionUserID);

			/**
			 * Switch for determing the users path
			 */
			switch (userPath) {

			case "/customer/home":
				template = Velocity
						.getTemplate("Velocity/customers/index.html");
				break;

			/**
			 * In this URL, all the VMs will be called or one specific VM will
			 * be called
			 */
			case "/customer/controlpanel":

				/**
				 * If the request contains the parameter with getVM, not all the
				 * VMS should be requested
				 */
				if (request.getParameterMap().containsKey("request")
						&& request.getParameter("request").contains("getvm")) {
					getUserVMs(request, response, sessionUserID);
				}

				/**
				 * If the request does not contain a parameter with getvm, all
				 * VMs will be called
				 */
				else {
					getAllUserVMs(request, response, sessionUserID);
				}

				break;

			case "/customer/controlpanel/monitor":
				template = Velocity
						.getTemplate("Velocity/customers/monitor/index.html");
				break;

			case "/customer/marketplace":
				template = Velocity
						.getTemplate("Velocity/customers/marketplace.html");
				break;
			case "/customer/marketplace/windows":
				template = Velocity
						.getTemplate("Velocity/customers/windows.html");
				break;
			case "/customer/marketplace/debian":
				template = Velocity
						.getTemplate("Velocity/customers/debian.html");
				break;
			case "/customer/marketplace/slackware":
				template = Velocity
						.getTemplate("Velocity/customers/slackware.html");
				break;

			case "/customer/logout":
				processLogout(request, response);
				break;
			}
		}
	}

	/**
	 * Non privileged POSTS will be processed here
	 * 
	 * @param userPath
	 * @param response
	 * @param request
	 * @throws IOException
	 */
	private void setPOSTCustomerUrls(String userPath,
			HttpServletResponse response, HttpServletRequest request)
			throws IOException {

		/**
		 * Switch for determing the users path
		 */
		switch (userPath) {
		case "/login":
			postSubmitForm(request, response);
			break;
		case "/register":
			postRegisterForm(request, response);
			break;
		}

	}

	/**
	 * Privileged POST requests will be processed here
	 * 
	 * @param userPath
	 * @param session
	 * @param response
	 * @param request
	 */
	private void setPOSTCustomerAuthenticatedUrls(String userPath,
			HttpSession session, HttpServletResponse response,
			HttpServletRequest request) {

		/**
		 * Getting the session username and userID
		 */
		String sessionUsername = (String) session.getAttribute("username");
		long sessionUserID = (long) session.getAttribute("userID");

		/**
		 * Making sure that the user has a valid session. If these variables are
		 * null, the user has not logged in properly
		 */
		if (sessionUsername != null && sessionUserID != 0) {

			// putting the username and ID in the context
			vsl_Context.put("name", sessionUsername);
			vsl_Context.put("id", sessionUserID);

			/**
			 * Switch for determing the users path
			 */
			switch (userPath) {
			case "/customer/marketplace/buy":
				postBuyCustomerVM(request, response, sessionUserID);
				break;
			}
		}
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
	private void getAllUserVMs(HttpServletRequest request,
			HttpServletResponse response, long sessionUserID) {
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
						bean.setVMName((String) listdata.get(i).get("vmName"));
						bean.setVMID((long) listdata.get(i).get("vmID"));
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

			template = Velocity
					.getTemplate("Velocity/customers/controlpanel.html");
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
	private void getUserVMs(HttpServletRequest request,
			HttpServletResponse response, long sessionUserID) {

		// Get VMID from parameter
		String ParameterVMID = request.getParameter("vmid");

		// Setting the URL for getting data from the back-end
		String getSpecifiedVMUrl = "http://localhost:8080/BackEnd/customer/controlpanel/getvms?request=getvm&vmid="
				+ ParameterVMID + "&userID=" + sessionUserID;

		// Setting variables used later
		long vmID = 0;
		String vmName = null;

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
					vmID = (long) json.get("vmID");
					vmName = (String) json.get("vmName");

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
			vsl_Context.put("vmName", vmName);
			vsl_Context.put("vmID", vmID);
			vsl_Context.put("userID", sessionUserID);

			template = Velocity
					.getTemplate("Velocity/customers/controlpanel.html");
		}
	}

	/**
	 * Method for registering the user with the form
	 * @param request
	 * @param response
	 */
	private void postRegisterForm(HttpServletRequest request,
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
			 * Finally statement for saving the user input when an error code appeared
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
	private void postSubmitForm(HttpServletRequest request,
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
			 * If everything was filled in correctly, the process can begin
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
							 * be made IF and ONLY IF the user is declared valid
							 */
							if (isValid) {

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
								session.setAttribute("userID", userID);
								session.setAttribute("username", username);

								String pin = "9281";

								session.setAttribute("pincode", pin);

								ArrayList<NameValuePair> GCMMessage = new ArrayList<NameValuePair>();
								GCMMessage.add(new BasicNameValuePair("userID",
										"1"));
								
								vsl_Context.put("pin", "1");
								// GCMMessage.add(new
								// BasicNameValuePair("message", "Pincode: " +
								// pin));

								// JSONObject giveMessage =
								// JsonPOSTParser.postJsonFromUrl(request,
								// "http://145.92.14.10:8080/GCM-Server/GCMNotification",
								// GCMMessage);

								// jobj.put("user", userID);
								// jobj.put("success", "1");

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
				} finally {
					vsl_Context.put("username", request.getParameter("user"));
					template = Velocity.getTemplate("Velocity/login.html");
				}
			}
		} else {
			HttpSession session = request.getSession();
			String sessionPin = (String) session.getAttribute("pincode");
			String userPin = request.getParameter("pin");

			if (sessionPin.equals(userPin)) {
				session.removeAttribute("pincode");
				response.sendRedirect("/ProjectVirt/customer/home");
			} else {
				vsl_Context.put("error", "pin incorrect");
				template = Velocity.getTemplate("Velocity/login.html");
			}
		}
	}

	/**
	 * When user has requested a new VM, the process will begin here
	 * 
	 * @param request
	 * @param response
	 * @param sessionUserID
	 */
	private void postBuyCustomerVM(HttpServletRequest request,
			HttpServletResponse response, long sessionUserID) {
		/**
		 * Checking if the fields were not left empty
		 */
		if (request.getParameter("VMRAM").equals("vmRAM")
				|| request.getParameter("VMCPU").equals("vmCPU")
				|| request.getParameter("VMHDD").equals("vmHDD")
				|| request.getParameter("VMSLA").equals("vmSLA")) {
			vsl_Context.put("error", "Please provide input in each field");
		} else if (request.getParameter("accept") == null) {
			vsl_Context.put("error", "Please accept terms of condition");
		} else {

			/**
			 * Set postparameters to give with the request
			 */
			ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
			postParameters.add(new BasicNameValuePair("VMOS", request
					.getParameter("VMOS")));
			postParameters.add(new BasicNameValuePair("VMRAM", request
					.getParameter("VMRAM")));
			postParameters.add(new BasicNameValuePair("VMCPU", request
					.getParameter("VMCPU")));
			postParameters.add(new BasicNameValuePair("VMHDD", request
					.getParameter("VMHDD")));
			postParameters.add(new BasicNameValuePair("VMSLA", request
					.getParameter("VMSLA")));

			/**
			 * Set the url for JSON + the postparameters
			 */
			JSONObject json = JsonPOSTParser.postJsonFromUrl(request,
					"http://localhost:8080/BackEnd/customer/marketplace/buy/processbuy?userID="
							+ sessionUserID, postParameters);

			/**
			 * Try to parse the JSON Object, given by the server
			 */
			try {

				/**
				 * If json is null, connection could not be made
				 */
				if (json != null) {

					/**
					 * Check if json String contains error key, for showing
					 * error. If not, process continues
					 */
					if (!json.containsKey("error")) {

						vsl_Context
								.put("success",
										"Succesfully added VM. It will take a minute or 30 before the VM is monitorable!");
					} else {
						vsl_Context.put("error", json.get("error"));
					}

				} else {
					vsl_Context.put("error",
							"Connection with server could not be made..");

				}
			} finally {

				/**
				 * Check which template to load again
				 */
				switch (request.getParameter("VMOS")) {
				case "debian":
					template = Velocity
							.getTemplate("Velocity/customers/debian.html");
					break;
				case "windows":
					template = Velocity
							.getTemplate("Velocity/customers/windows.html");
					break;
				case "slackware":
					template = Velocity
							.getTemplate("Velocity/customers/slackware.html");
					break;
				}

			}

		}

	}

	/**
	 * Method for destroying the session
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServiceUnavailableException
	 */
	private void processLogout(final HttpServletRequest request,
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

	/**
	 * Delete all the velocity objects, so objects can be called again when
	 * reloading the Users Servlet
	 */
	private void cleanVelocity() {
		Object[] keys = vsl_Context.getKeys();
		for (Object object : keys) {
			vsl_Context.remove(object.toString());
		}
	}

}
