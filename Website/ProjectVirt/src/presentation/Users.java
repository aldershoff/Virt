package presentation;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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

import userServices.UserAccountService;
import userServices.UserBuyService;
import userServices.UserVMControlService;
import userServices.UserVMDataService;

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

		// Creating new function for getting the virtual machines
		UserVMDataService userVM = null;

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

				template = Velocity
						.getTemplate("Velocity/customers/controlpanel.html");

				userVM = new UserVMDataService(vsl_Context, template,
						out);
				/**
				 * If the request contains the parameter with getVM, not all the
				 * VMS should be requested
				 */
				if (request.getParameterMap().containsKey("request")
						&& request.getParameter("request").contains("getvm")) {
					userVM.getUserVMs(request, response, sessionUserID);
				}

				/**
				 * If the request does not contain a parameter with getvm, all
				 * VMs will be called
				 */
				else {
					userVM.getAllUserVMs(request, response, sessionUserID);
				}

				break;

			case "/customer/controlpanel/monitor":
				
				UserVMDataService vmData = new UserVMDataService(vsl_Context, template, out);
				vmData.getRealtimeVMData(request, response, sessionUserID);
				
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
				UserAccountService logout = new UserAccountService(vsl_Context,
						template, out);
				logout.logoutUser(request, response);
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
		UserAccountService userAccount = null;

		/**
		 * Switch for determing the users path
		 */
		switch (userPath) {

		case "/login":
			userAccount = new UserAccountService(vsl_Context, template, out);
			userAccount.loginUser(request, response);
			break;
		case "/register":
			userAccount = new UserAccountService(vsl_Context, template, out);
			userAccount.registerUser(request, response);
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
				UserBuyService userBuy = new UserBuyService(vsl_Context, template, out);
				userBuy.buyCustomerVM(request, response, sessionUserID);
				break;
				
			case "/customer/controlpanel/vmcontrol":
				
				UserVMControlService vmControl = new UserVMControlService(vsl_Context, template, out);
				String vmID = request.getParameter("vmid");
				switch (request.getParameter("action")) {
				case "Start":
					vmControl.startVM(request, response, vmID, sessionUserID, "Start");
					break;

				case "Stop":
					vmControl.stopVM(request, response, vmID, sessionUserID, "Stop");
					break;
					
				case "Edit":
					vmControl.editVM(request, response, vmID, sessionUserID, "Edit");
					break;
					
				case "Delete":
					vmControl.deleteVM(request, response, vmID, sessionUserID, "Delete");
					break;
					
				case "RefreshState":
					vmControl.deleteVM(request, response, vmID, sessionUserID, "RefreshState");
					break;
				default:
					break;
				}
				
				
				break;
			}
		}
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
