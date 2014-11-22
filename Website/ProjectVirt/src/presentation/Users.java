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

		try {
			// Setting the writer
			out = response.getWriter();

			// Setting the servletpath and sending it to the Switch method
			String userPath = request.getServletPath();

			// If the userPath does not contains the error String, the link is
			// for the the main website
			if (!userPath.contains("error")) {
				setGetControllerUrls(userPath);

			}

			// Else, the userPath DOES contains the error String, and will look
			// for it in the error switch
			else {
				setGetErrorUrls(userPath, request);
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


	}


	/**
	 * Controller method for the User part of the website. This method will only
	 * be executed when the userPath contains no "error" or other String similar
	 * to "error"
	 * 
	 * @param userPath
	 */
	private void setGetControllerUrls(String userPath) {
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

		default:
			break;
		}
	}

	/**
	 * Controller method for the error part of the main website. This will only
	 * execute if the userPath contains the "error" String. It will also call
	 * the other methods for setting the parameter callbacks.
	 * 
	 * @param userPath
	 * @param request
	 */
	private void setGetErrorUrls(String userPath, HttpServletRequest request) {
		switch (userPath) {
		case "/register/processregister/error":
			setGetRegisterErrorParameters(request.getParameter("name"));
			template = Velocity.getTemplate("Velocity/register.html");
			break;

		case "/login/processlogin/error":
			setGetLoginErrorParameters(request.getParameter("name"));
			template = Velocity.getTemplate("Velocity/login.html");
			break;

		default:
			break;
		}
	}

	/**
	 * Method for setting the error parameters given by another Servlet (or by
	 * the user) It will assign the designated String accordingly.
	 * 
	 * @param parameter
	 */
	private void setGetLoginErrorParameters(String parameter) {
		String error = null;

		switch (parameter) {
		case "emptyusername":
			error = "Username not filled in!";
			break;

		case "emptypassword":
			error = "Password not filled in!";
			break;

		case "bothfieldsempty":
			error = "Both fields are empty!";
			break;

		case "nodatabase":
			error = "No database found. Please contact Willem!";
			break;

		case "wrongusernamepassword":
			error = "Wrong username or password!";
			break;

		default:
			break;
		}

		vsl_Context.put("error", error);

	}

	/**
	 * Method for setting the error parameters given by another Servlet, or by
	 * the user It will assign the designated String accordingly.
	 * 
	 * @param parameter
	 */
	private void setGetRegisterErrorParameters(String parameter) {
		String error = null;

		switch (parameter) {
		case "fieldsnotfilled":
			error = "Not everything filled in!";
			break;

		case "nodatabase":
			error = "No database found. Please contact Willem!";
			break;

		default:
			break;
		}

		vsl_Context.put("error", error);

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
