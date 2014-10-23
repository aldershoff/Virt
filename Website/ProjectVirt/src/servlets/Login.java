package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import queries.CheckLogin;
import queries.DBConnection;

/**
 * Servlet implementation class Login This servlet is mainly for processing user
 * login and to get database query info, separated from the Users servlet.
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private VelocityContext vsl_Context = new VelocityContext();
	private Template template = new Template();
	private PrintWriter out;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Login() {
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
		getLogin(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doLogin(request, response);
	}

	/**
	 * Getting the login template and showing it to the user
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void getLogin(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
		try {
			response.setContentType("text/html;charset=UTF-8");

			// Make context
			VelocityContext vsl_Context = new VelocityContext();

			// Getting template
			Template template = Velocity.getTemplate("Velocity/login.html");

			// Writing to screen
			out = response.getWriter();
			template.merge(vsl_Context, out);
			out.close();

		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
	}

	/**
	 * Method for processing login information. This version does not yet
	 * connect to the database, but makes a dummy users with cookie to follow
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void doLogin(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {

		// getting new responsewriter and setting it into the already made
		// variable
		out = response.getWriter();

		// Getting parameters from form (login.html)
		String username = request.getParameter("user");
		String password = request.getParameter("password");

		DBConnection dbConn = new DBConnection();
		Connection conn = dbConn.returnConnection();
		int check = 0;
		
		if(conn != null){
		CheckLogin checkLog = new CheckLogin(conn, username, password);

		 check = checkLog.runQuery();
		}
		/**
		 * If the name and password equals to each other, the redirect will take
		 * place. This is also the place where database query content will be
		 * filtered
		 */
		if (check == 1) {

			// Making new cookie from username
			Cookie loginCookie = new Cookie("user", username);

			// setting cookie to expiry in 30 mins
			loginCookie.setMaxAge(30 * 60);
			response.addCookie(loginCookie);

			// Redirect to servlet
			response.sendRedirect("AuthUsers");
			// template = Velocity.getTemplate("Velocity/index.vm");
			// vsl_Context.put("logged", "Hallo " + username);
			// template.merge(vsl_Context, out);
		}

		/**
		 * Else, print that the password is wrong and close the print
		 */
		else {
			template = Velocity.getTemplate("Velocity/login.html");
			out.println("<font color=red>Either user name or password is wrong.</font><br />");
			template.merge(vsl_Context, out);
			out.close();
		}
	}

	public int checkLogin(Connection conn, String username, String password) {
		Statement stmt = null;

		ResultSet rs = null;
		String checkUsername = null;
		String checkPassword = null;

		try

		{

			stmt = conn.createStatement();

			rs = stmt
					.executeQuery("SELECT username, password FROM Users WHERE username = "
							+ "\"" + username + "\"" + " AND id > 0");

			while (rs.next()) {

				checkUsername = rs.getString("username");

				checkPassword = rs.getString("password");

				// checkAccessPower = rs.getInt(“accesspower”);

			}

			if (checkUsername != null) {
				if (checkPassword.equals(password)) {
					return 1;
				} else {
					return 0;
				}
			}
			conn.close();
		}

		catch (SQLException ex)

		{

			// handle any errors

			System.out.println("SQLException: " + ex.getMessage());

			System.out.println("SQLState: " + ex.getSQLState());

			System.out.println("VendorError: " + ex.getErrorCode());

		}
		return 0;
	}
}
