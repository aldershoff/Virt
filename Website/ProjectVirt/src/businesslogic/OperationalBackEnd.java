package businesslogic;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
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
		// If login process is requested
		else if (userPath.equals("/login/processregister")) {
			processRegister(request, response);
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
		custBean.setUserName(request.getParameter("user"));
		custBean.setPassword(request.getParameter("password"));

		if (custBean.getUsername() != "" && custBean.getPassword() != "") {

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

					// Make session and fill it with the username
					HttpSession session = request.getSession();
					session.setAttribute("name", custBean.getUsername());

					// If rememberme is checked, a cookie will be exchanged to
					// the
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
		} else if(custBean.getUsername() == "" && custBean.getPassword() == "") {
			out.print("No username given or password given");
		} else if(custBean.getUsername() == ""){
			out.print("No username given");
		} else if(custBean.getPassword() == ""){
			out.print("No pasword given");
		} else{
			out.print("No username and password given");
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
	 */
	private void processRegister(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {

		// Setting writer
		out = response.getWriter();

		// Making new bean for the customer
		CustomerBean custBean = new CustomerBean();

		// Getting parameters from form (login.html)
		custBean.setUserName(request.getParameter("user"));
		custBean.setPassword(request.getParameter("password"));
		custBean.setDateOfBirth(request.getParameter("dateofbirth"));
		custBean.setFirstName(request.getParameter("firstname"));
		custBean.setLastName(request.getParameter("lastname"));
		custBean.setEmail(request.getParameter("email"));
		custBean.setPhone(request.getParameter("phone"));
		custBean.setAddress(request.getParameter("address"));
		custBean.setZipCode(request.getParameter("zipcode"));

		if (custBean.getUsername() != "" && custBean.getPassword() != ""&&
			custBean.getFirstName() != "" && custBean.getLastName() != "" && custBean.getEmail() != "") {
		
		// Making new login Data Access Object
		UserDAO checkLog = new UserDAO();

		// Make connection, fill bean and return it
		custBean = checkLog.register(custBean);
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
		else{
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/register/processregister?name=notallfields");
			try {
				dispatcher.forward(request, response);
			} catch (ServletException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
		}

	}

}
