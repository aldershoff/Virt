package businesslogic;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import DAO.UserDAO;
import beans.CustomerBean;

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

		// If virtualmachine page is requested (with id)
		if (userPath.equals("/customer/getVirtualMachine=")) {
			getUserVM(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String userPath = request.getServletPath();

		// If login process is requested
		if (userPath.equals("/login/processlogin")) {
			processLogin(request, response);
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
		// Put data
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

		// Making new bean for the customer
		CustomerBean custBean = new CustomerBean();

		// Getting parameters from form (login.html)
		String getFormUsername = request.getParameter("user");
		String getFormPassword = request.getParameter("password");

		// Making new login Data Access Object
		UserDAO checkLog = new UserDAO();

		// Make connection, fill bean and return it
		custBean = checkLog.login(custBean, getFormUsername, getFormPassword);

		// If bean is not null, a database connection has been initiated
		if (custBean != null) {

			/**
			 * If the user is succesfully authenticated, and the bean has been
			 * filled with information, a new session can be made
			 */
			if (custBean.isValid()) {

				// Make session and fill it with the username
				HttpSession session = request.getSession();
				session.setAttribute("name", custBean.getUsername());

				// If rememberme is checked, a cookie will be exchanged to the
				// user
				if (request.getParameter("rememberme") != null) {

					// Making new cookie from username
					Cookie loginCookie = new Cookie("user",
							custBean.getUsername());

					// setting cookie to expiry in 30 mins
					loginCookie.setMaxAge(2592000);
					response.addCookie(loginCookie);

					// Sending debug message
					session.setAttribute("cookie", "COOKIE INSERTED");

				}

				// Redirect to servlet
				response.sendRedirect("/ProjectVirt/customer/home");

			}

			/**
			 * Else, print that the password is wrong and close the print
			 */
			else if (!custBean.valid) {
				out.print("BAD");
			}
		}

		/**
		 * If returned null, something is wrong with the database
		 */
		else {
			out.print("NO DATABASE");
		}

	}

}
