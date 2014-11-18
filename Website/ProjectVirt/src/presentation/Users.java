package presentation;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

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
	 * Singleton for initializing the velocity engine
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

		// Getting the template for public use
		getController(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Also for the post, the same method as the get
		getController(request, response);

	}

	private void getController(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {

		try {
			// Setting the writer
			out = response.getWriter();

			String userPath = request.getServletPath();

			// If home page is requested
			if (userPath.equals("/home")) {
				template = Velocity.getTemplate("Velocity/index.html");
			}

			// If about page is requested
			else if (userPath.equals("/about")) {
				template = Velocity.getTemplate("Velocity/about.html");
			}
			// If services page is requested
			else if (userPath.equals("/services")) {
				template = Velocity.getTemplate("Velocity/services.html");
			}
			// If hosts page is requested
			else if (userPath.equals("/hosts")) {
				template = Velocity.getTemplate("Velocity/hosts.html");
			}

			// If login page is requested
			else if (userPath.equals("/login")) {
				template = Velocity.getTemplate("Velocity/login.html");
			}

			// If register page is requested
			else if (userPath.equals("/register")) {
				template = Velocity.getTemplate("Velocity/register.html");
			}

				
			else if(userPath.equals("/register/processregister?name=notallfields")){
				out.print("Not all fields inserted");
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
			out.close();
		}

	}

	
	/**
	 * Not needed anymore, because the fulter can also check these things.
	 * Checking the cookies will be needed also in the future, still working on that..
	 * @param request
	 * @param response
	 * @return
	 */
	private int checkIfLoggedIn(final HttpServletRequest request,
			final HttpServletResponse response) {
		/**
		 * Try to see if anyone logged in, if so, the user should be welcomed
		 * and see different information then the other users
		 */

		// Setting username
		String userName = null;

		// Setting cookies
		Cookie[] cookies = request.getCookies();

		/**
		 * If cookies are not null, the following should be executed
		 */
		if (cookies != null) {
			for (Cookie cookie : cookies) {

				// If the username equals the user
				if (cookie.getName().equals("user")) {

					return 1;
					// Making new hypervisor conn
					// TestVM vm = new TestVM();

					// Make new vm's
					// vm.createDomains();

				}

			}
		} else if (userName == null) {
			return 0;
		}
		return 0;

	}

	/**
	 * Also not needed anymore..
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void getTemp(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {

		// Setting the writer
		out = response.getWriter();

		/**
		 * Try to see if anyone logged in, if so, the user should be welcomed
		 * and see different information then the other users
		 */
		try {
			response.setContentType("text/html;charset=UTF-8");

			// Setting username
			String userName = null;

			// Setting cookies
			Cookie[] cookies = request.getCookies();

			/**
			 * If cookies are not null, the following should be executed
			 */
			if (cookies != null) {
				for (Cookie cookie : cookies) {

					// If the username equals the user
					if (cookie.getName().equals("user")) {

						response.sendRedirect("AuthUsers");
						// Making new hypervisor conn
						// TestVM vm = new TestVM();

						// Make new vm's
						// vm.createDomains();

					}

				}

			}

			/**
			 * If username is null, it means that the user hasn't logged in yet.
			 * Other information must be given of course.
			 */
			if (userName == null) {
				vsl_Context.put("notLogged", "Je bent niet ingelogd");
			}

		}

		catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}

		/**
		 * When done, merge template and close the writer for user
		 */
		finally {
			template = Velocity.getTemplate("Velocity/index.html");
			template.merge(vsl_Context, out);
			out.close();
		}
	}

}
