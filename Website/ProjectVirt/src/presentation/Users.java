package presentation;

import infrastructure.TestVM.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
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
import org.libvirt.LibvirtException;

import beans.CustomerBean;
import beans.VMBean;

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
		System.setProperty("jna.library.path",
				"/home/sne/workspace/ProjectVirt");

		try {
			// test for creating VM
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

			// infrastructure.TestVM.createVM("qemu", "test02", 1048576, 2, 1 );

			/**
			 * Checking if the parametermapping is empty. If so, the request is
			 * an usual request from the menu within the website
			 */
			if (request.getParameterMap().isEmpty()) {
				setGetControllerUrls(userPath);
			}

			/**
			 * If there is an error given by the back-end, process it through the following method
			 */
			else if (request.getParameterMap().containsKey("error")) {
				setGetErrorParameters(request, response, userPath);
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
		try {
			infrastructure.TestVM.createVM("qemu", "test02", 1048576, 2, 1);
		} catch (LibvirtException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	 * Method for setting the error parameters given by another Servlet (or by
	 * the user) It will assign the designated String accordingly.
	 * 
	 * @param parameter
	 */
	private void setGetErrorParameters(HttpServletRequest request,
			HttpServletResponse response, String userPath) {
		ServletContext context = request.getSession().getServletContext();
		if (context.getAttribute("formUsername") != null) {
			vsl_Context.put("formUsername",
					context.getAttribute("formUsername"));

		}
		vsl_Context.put("error", context.getAttribute("error"));

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
