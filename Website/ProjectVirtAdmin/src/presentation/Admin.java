package presentation;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.json.simple.parser.ParseException;

import adminServices.AdminAccountService;
import adminServices.AdminPermissionService;
import adminServices.AdminUserOverviewService;
import adminServices.AdminVMOverviewService;


@WebServlet(name = "/Admin", loadOnStartup = 1)
/**
 * Servlet implementation class Users.
 * This Servlet is for all users and can access
 * public information
 * @author kjellzijlemaker
 *
 */
public class Admin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private VelocityContext vsl_Context = new VelocityContext();
	private Template template = new Template();
	private PrintWriter out;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Admin() {
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
			Logger.getLogger(Admin.class.getName()).log(Level.SEVERE, null, ex);
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
			if (!userPath.startsWith("/admin")) {
				setGETAdminUrlTemplates(userPath);
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
				setGETAdminAuthenticatedUrlTemplates(userPath, session,
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

			/**
			 * If the userpath does not start with customer, the user will
			 * browse through this method
			 */
			if (!userPath.startsWith("/admin")) {
				setPOSTAdminUrls(userPath, response, request);
				
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
				setPOSTAdminAuthenticatedUrls(userPath, session, response,
						request);
				
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
	private void setGETAdminUrlTemplates(String userPath) {
		switch (userPath) {
		case "/home":
			template = Velocity.getTemplate("Velocity/index.html");
			break;

		case "/login":
			template = Velocity.getTemplate("Velocity/login.html");
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
	private void setGETAdminAuthenticatedUrlTemplates(String userPath,
			HttpSession session, HttpServletResponse response,
			HttpServletRequest request) throws IllegalStateException,
			UnsupportedEncodingException, IOException, RuntimeException,
			ParseException {

		/**
		 * Getting the session username and userID
		 */
		String sessionUsername = (String) session.getAttribute("username");
		long sessionUserID = (long) session.getAttribute("userID");

		AdminUserOverviewService customerData = null;
		
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

			case "/admin/home":
				template = Velocity
						.getTemplate("Velocity/admin/index.html");
				break;
				
			case "/admin/profile":
				template = Velocity
				.getTemplate("Velocity/admin/profile.html");
				
				// Making new object
				AdminAccountService userDetails = new AdminAccountService(vsl_Context, template, out, request, response);
				
				// Calling the getUserDetails method for getting the details for user
				userDetails.getUserDetails(sessionUserID);
				break;

				/**
				 * In this URL, all the users will be called or one specific user will
				 * be called
				 */
				case "/admin/overview":

					template = Velocity
							.getTemplate("Velocity/admin/overview.html");

					customerData = new AdminUserOverviewService(vsl_Context);
					/**
					 * If the request contains the parameter with getuser, not all the
					 * users should be requested
					 */
					if (request.getParameterMap().containsKey("request")
							&& request.getParameter("request").contains("getalluservms")) {
						AdminVMOverviewService userVMData = new AdminVMOverviewService(vsl_Context, request);
						String overviewuserID = request.getParameter("userID");
						
						userVMData.getAllUserVMs(Long.parseLong(overviewuserID));
					}

					/**
					 * If the request does not contain a parameter with getallusers, all
					 * VMs will be called
					 */
					else {
						customerData.getAllUsers();
					}

					break;
				
				
				
			case "/admin/controlpanel/vmcontrol/editvm":
				AdminVMOverviewService getVMData = new AdminVMOverviewService(vsl_Context, request);
				template = Velocity.getTemplate("Velocity/customers/editvm.html");
				getVMData.getUserVMs(sessionUserID);
				break;


			case "/admin/logout":
				AdminAccountService logout = new AdminAccountService(vsl_Context,
						template, out, request, response);
				logout.logoutUser();
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
	private void setPOSTAdminUrls(String userPath,
			HttpServletResponse response, HttpServletRequest request)
			throws IOException {

		/**
		 * Switch for determing the users path
		 */
		switch (userPath) {

		case "/login":
			AdminAccountService userAccount = new AdminAccountService(vsl_Context, template, out, request, response);
			userAccount.loginAdmin();
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
	 * @throws IOException 
	 */
	private void setPOSTAdminAuthenticatedUrls(String userPath,
			HttpSession session, HttpServletResponse response,
			HttpServletRequest request) throws IOException {

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
						
			case "/admin/profile/updateprofile":
				AdminAccountService updateProfile = new AdminAccountService(vsl_Context, template, out, request, response);
				template = Velocity.getTemplate("Velocity/admin/profile.html");
				updateProfile.updateUserProfileDetails(sessionUserID);
				break;
				
			case "/admin/profile/updateaccount":
				AdminAccountService updateAccount = new AdminAccountService(vsl_Context, template, out, request, response);
				template = Velocity.getTemplate("Velocity/admin/profile.html");
				updateAccount.updateUserAccountDetails(sessionUserID);
				break;
				
			
			case "/customer/controlpanel/vmcontrol":
				
//				AdminVMControlService vmControl = new AdminVMControlService(vsl_Context, template, out, request, response);
//				String vmID = request.getParameter("vmid");
//				switch (request.getParameter("action")) {
//				case "Start":
//					template = Velocity.getTemplate("Velocity/customers/controlpanel.html");
//					vmControl.startVM(vmID, sessionUserID, "Start");
//					break;
//
//				case "Stop":
//					template = Velocity.getTemplate("Velocity/customers/controlpanel.html");
//					vmControl.stopVM(vmID, sessionUserID, "Stop");
//					break;
//					
//				case "Delete":
//					template = Velocity.getTemplate("Velocity/customers/controlpanel.html");
//					vmControl.deleteVM(vmID, sessionUserID, "Delete");
//					break;
//				
//				case "Edit":
//					template = Velocity.getTemplate("Velocity/customers/editvm.html");
//					vmControl.editVM(vmID, sessionUserID, "Edit");
//					break;
//					
//				case "RefreshState":
//					template = Velocity.getTemplate("Velocity/customers/controlpanel.html");
//					vmControl.refreshVMState(vmID, sessionUserID, "RefreshState");
//					break;
//				default:
//					break;
				//}
			case "/admin/overview/userrights":
				AdminPermissionService changePerm = new AdminPermissionService(request, vsl_Context);
				String permUserID = request.getParameter("userid");
				template = Velocity.getTemplate("Velocity/admin/overview.html");
				switch (request.getParameter("request")) {
				case "user":
					changePerm.setPermissions(permUserID, "user");
					break;
				case "admin":
					changePerm.setPermissions(permUserID, "admin");
					break;
				default:
					break;
				}
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
