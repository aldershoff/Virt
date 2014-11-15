package presentation;

import java.io.IOException;
import java.io.PrintWriter;
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

/**
 * Servlet implementation class AuthUsers. This servlet will handle all the
 * users that are succesfully logged into the website and will get other views
 * than the Users servlet
 */
@WebServlet(name = "/AuthUsers")
public class AuthUsers extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private VelocityContext vsl_Context = new VelocityContext();
	private Template template = new Template();
	private PrintWriter out;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AuthUsers() {
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

		// Executing renderTemp when getting a GET method from HTTP browser
		renderTemp(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// Executing processTemp when getting a POST method from the HTTP
		// browser
		processTemp(request, response);
	}

	/**
	 * When getting a POST inside the authusers servlet, this method must
	 * execute. At this moment no yet no content
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void processTemp(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
		HttpSession session = request.getSession(false);
		session.invalidate();
	}

	/**
	 * Will execute when HTTP browser will execute a GET method. First it will
	 * of course check if user may be present in this authenticated part of the
	 * website
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void renderTemp(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {

		try {
			// Making new writer from the, already existed, print variable
			out = response.getWriter();
			String userPath = request.getServletPath();

			// Making new session and getting the attribute for sending it to the HTML pages
			HttpSession session = request.getSession(false);
			String name = (String) session.getAttribute("name");
			String cookie = (String) session.getAttribute("cookie");
			
			/**
			 * If the home page is requested, the name is redirected through the Velocity template
			 */
			if (userPath.equals("/customer/home")) {
				vsl_Context.put("name", name);
				vsl_Context.put("cookie", cookie);
				template = Velocity
						.getTemplate("Velocity/customers/index.html");
			}

			/**
			 * Logout page control
			 */
			if (userPath.equals("/customer/logout")) {
				template = Velocity
						.getTemplate("Velocity/customers/logout.html");
			}

		}

		/**
		 * Catch exceptions
		 */
		catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}

		/**
		 * When done, merge template and close the writer for user
		 */
		finally {
			template.merge(vsl_Context, out);
			out.close();
		}

		//
		// // Making new cookie request (from the login servlet)
		// Cookie[] cookies = request.getCookies();
		//
		// /**
		// * This try statement is for checking if cookie is present or not
		// */
		// try {
		// if (cookies != null) {
		// // If cookie not null, go through all cookies
		// for (Cookie cookie : cookies) {
		//
		// // If the username equals the user cookie, execute
		// // statements
		// if (cookie.getName().equals("user")) {
		//
		// // Get the username from the cookie
		// userName = cookie.getValue();
		// out.print(userName);
		// // Making the new logout form
		// String form = "<form action='Logout'>"
		// + "<input type='submit' value='Logout' />"
		// + "</form>";
		//
		// // putting it in the velocity context engine for HTML
		// vsl_Context.put("formulier", form);
		//
		// // Putting welcome message in the velocity context
		// // engine for HTML
		// vsl_Context.put("logged", "Hallo " + userName);
		//
		// // Getting template
		// template = Velocity.getTemplate("Velocity/index.vm");
		//
		// // Merge template
		// template.merge(vsl_Context, out);
		//
		// }
		// }
		// }
		// else{
		// out.print("Something went wrong");
		// }

		/**
		 * If username == null, redirect to the login page. User hasn't logged
		 * in yet
		 */
		// if (userName == null) {
		// response.sendRedirect("Login");
		// }
	}

	/**
	 * Finally, close the print writer
	 */
	// finally {
	//
	// out.close();
	// }

	// }

}
