package businesslogic;

import java.io.IOException;
import java.io.PrintWriter;

import javax.naming.ServiceUnavailableException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import databaseAccessObjects.UserDAO;
import databaseAccessObjects.VMDAO;
import security.PasswordService;
import beans.CustomerBean;
import beans.VMBean;

/**
 * The servlet for getting and Posting information through and from the database
 * and KVM. This servlet will handle all the operations needed to get all the
 * information back to the user
 * 
 * @author kjellzijlemaker
 *
 */
@WebServlet(name = "/BackEnd")
public class OperationalBackEnd extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private PrintWriter out;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OperationalBackEnd() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// Setting the userpath, so the absolute path is correct from browser
		String userPath = request.getServletPath();

		// Let method with switch decide which method to call for processing the
		// GET calls, like getting VMS
		setGetControllerUrls(userPath, request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// Setting the userPath from servlet
		String userPath = request.getServletPath();

		// Let the method with switch decide which method to call for processing
		// the login / register, etc.
		setPostControllerUrls(userPath, request, response);

	}

	/**
	 * Method for acting as the controller with a switch statement, for GET
	 * calls
	 * 
	 * @param userPath
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void setGetControllerUrls(String userPath,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		switch (userPath) {
		case "/customer/controlpanel/getvms":
			getUserVM(request, response);
			break;
		case "/customer/logout":
			processLogout(request, response);
			break;	
		default:
			break;
		}
	}

	/**
	 * Method for acting as the controller with a switch statement, for POST
	 * calls
	 * 
	 * @param userPath
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void setPostControllerUrls(String userPath,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		switch (userPath) {
		case "/login/processlogin":
			processLogin(request, response);
			break;

		case "/register/processregister":
			processRegister(request, response);
			break;

		default:
			break;
		}
	}

	/**
	 * Method for getting the userVM information
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void getUserVM(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {
		
		VMBean vmBean = new VMBean();

		VMDAO VMDAO = new VMDAO();

		HttpSession session = request.getSession(false);
		CustomerBean custBean = (CustomerBean) session
				.getAttribute("customer");
		
		
		/**
		 * Checking if the attribute is null
		 * If so, the query needs to be runned. If not, the user should give
		 * permission to refresh the servers and get fresh output
		 */
		try {
			if(session.getAttribute("virtualmachine") == null){
				vmBean = VMDAO.getVMs(vmBean, custBean.getUserID());
				session.setAttribute("virtualmachine", vmBean);
			}
			
		} 
		finally {
			response.sendRedirect("/ProjectVirt/customer/controlpanel");
		}
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
	private void processLogin(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {

		// Setting writer
		out = response.getWriter();

		/**
		 * Checking if the fields were not left empty
		 */
		if (request.getParameter("user") == ""
				&& request.getParameter("password") == "") {
			response.sendRedirect(request.getRequestURI()
					+ "/error?name=bothfieldsempty");
		} else if (request.getParameter("user") == "") {
			response.sendRedirect(request.getRequestURI()
					+ "/error?name=emptyusername");
		} else if (request.getParameter("password") == "") {
			response.sendRedirect(request.getRequestURI()
					+ "/error?name=emptypassword");
		}

		/**
		 * If the fields are not empty
		 */
		else {

			// Making new bean for the customer
			CustomerBean custBean = new CustomerBean();

			// Getting parameters from form (login.html)
			custBean.setUserName(request.getParameter("user"));
			try {
				custBean.setPassword(new PasswordService().getInstance()
						.encrypt(request.getParameter("password")));
			} catch (ServiceUnavailableException e) {
				e.printStackTrace();
			}

			// Making new login Data Access Object
			UserDAO checkLog = new UserDAO();

			// Make connection, fill bean and return it
			custBean = checkLog.login(custBean);

			// If bean is not null, a database connection has been initiated
			if (custBean != null) {

				/**
				 * If the user is succesfully authenticated, and the bean has
				 * been filled with information, a new session can be made
				 */
				if (custBean.isValid()) {

					// Get session and fill it with the username
					HttpSession session = request.getSession();
					session.setAttribute("customer", custBean);

					/**
					 * If rememberMe is checked, a cookie will be exchanged to
					 * the user
					 */
					if (request.getParameter("rememberme") != null) {

						// Making new cookie from username
						Cookie loginCookie = new Cookie("user",
								custBean.getUsername());

						// setting cookie to expiry in 30 mins
						loginCookie.setMaxAge(2592000);
						response.addCookie(loginCookie);

					}

					// Redirect to servlet
					response.sendRedirect(request.getRequestURI() + "/customer/home");

				}

				/**
				 * Else, print that the password is wrong and close the print
				 */
				else if (!custBean.valid) {
					response.sendRedirect(request.getRequestURI()
							+ "/error?name=wrongusernamepassword");
				}
			}

			/**
			 * If returned null, something is wrong with the database
			 */
			else {
				response.sendRedirect(request.getRequestURI()
						+ "/error?name=nodatabase");
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
	private void processRegister(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {

		// Setting writer
		out = response.getWriter();

		/**
		 * If not all fields are filled, a redirect will take place
		 */
		if (request.getParameter("user") == ""
				|| request.getParameter("password") == ""
				|| request.getParameter("firstname") == ""
				|| request.getParameter("lastname") == ""
				|| request.getParameter("user") == "") {

			response.sendRedirect(request.getRequestURI()
					+ "/error?name=fieldsnotfilled");

		}

		/**
		 * Else, the user registration will continue
		 */
		else {

			// Making new bean for the customer
			CustomerBean custBean = new CustomerBean();

			// Getting parameters from form (login.html)
			custBean.setUserName(request.getParameter("user"));

			try {
				custBean.setPassword(new PasswordService().getInstance()
						.encrypt(request.getParameter("password")));
			} catch (ServiceUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			custBean.setDateOfBirth(request.getParameter("dateofbirth"));
			custBean.setFirstName(request.getParameter("firstname"));
			custBean.setLastName(request.getParameter("lastname"));
			custBean.setEmail(request.getParameter("email"));
			custBean.setPhone(request.getParameter("phone"));
			custBean.setAddress(request.getParameter("address"));
			custBean.setZipCode(request.getParameter("zipcode"));

			// Making new login Data Access Object
			UserDAO checkLog = new UserDAO();

			// Make connection, fill bean and return it
			custBean = checkLog.register(custBean);
			// If bean is not null, a database connection has been initiated
			if (custBean != null) {

				/**
				 * If the user is succesfully authenticated, and the bean has
				 * been filled with information, a new session can be made
				 */
				if (custBean.isValid()) {

					// Get session and fill it with the username
					HttpSession session = request.getSession();
					session.setAttribute("customer", custBean);

				}

				// Redirect to servlet
				response.sendRedirect(request.getRequestURI() + "/customer/home");
				
			}
			/**
			 * If returned null, something is wrong with the database
			 */
			else {
				response.sendRedirect(request.getRequestURI()
						+ "/error?name=nodatabase");
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

		// Get session from other servlet
		HttpSession session = request.getSession();

			session.invalidate();

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
			 * Setting the age to 0 and add cookie cookie to the response again.
			 * The cookie is now destroyed
			 */
			if (loginCookie != null) {
				loginCookie.setMaxAge(0);
				response.addCookie(loginCookie);
			}

			// Redirect to home page
			response.sendRedirect(request.getRequestURI() + "/home");

			return; // <--- Here.
		}


}
