package businesslogic;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Logout. For logging out of the application, it
 * will destroy the users session and cookie
 */
@WebServlet("/Logout")
public class LogoutServ extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LogoutServ() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// Talking to the logout method for destoying
		logOut(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * When logging out, the following method must execute. It will make sure
	 * that the user his cookie will no longer work and the user will be forced
	 * to login again.
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void logOut(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {

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

		// Redirect to the login page
		response.sendRedirect("Login");
	}
}
