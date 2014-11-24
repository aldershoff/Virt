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

import beans.CustomerBean;
import beans.VMBean;

/**
 * Servlet implementation class AuthUsers. This servlet will handle all the
 * users that are succesfully logged into the website and will get other views
 * than the Users servlet
 */
@WebServlet(name = "/AuthUsers")
public class AuthUsers extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private VelocityContext vsl_Context = new VelocityContext();
	private Template template;
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

		try {

			// Making new writer from the, already existed, print variable
			out = response.getWriter();
			String userPath = request.getServletPath();

			// Making new session and getting the attribute for sending it to
			// the HTML pages
			HttpSession session = request.getSession(false);
			CustomerBean custBean = (CustomerBean) session
					.getAttribute("customer");

			
			// Making new session and getting the attribute for sending it to
			// the HTML pages
			VMBean vmBean = (VMBean) session.getAttribute("virtualmachine");
						
			// Calling controller for passing path and bean
			setGetControllerUrls(userPath, custBean, request, response, session, vmBean);
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

	}

	/**
	 * Method for handling the GET userPath details for putting into the templates
	 * @param userPath
	 * @param custBean
	 * @throws IOException 
	 */
	private void setGetControllerUrls(String userPath, CustomerBean custBean, HttpServletRequest request, HttpServletResponse response, HttpSession session, VMBean vmBean) throws IOException {
		switch (userPath) {
		case "/customer/home":
			vsl_Context.put("name", custBean.getUsername());
			vsl_Context.put("id", custBean.getUserID());
			template = Velocity.getTemplate("Velocity/customers/index.html");
			break;
			
		case "/customer/controlpanel":
			
			// Checking if the bean is not null, else the data is not properly loaded inside the backend serv
			if(vmBean != null){
				vsl_Context.put("name", custBean.getUsername());
				vsl_Context.put("id", custBean.getUserID());
				vsl_Context.put("vmname", vmBean.getVMName());
				template = Velocity.getTemplate("Velocity/customers/controlpanel.html");
			}
			else{
				response.sendRedirect(request.getRequestURI() + "/getvms");
			}
			break;	
		case "/customer/marketplace":
			template = Velocity.getTemplate("Velocity/customers/marketplace.html");
			break;	
		

		default:
			break;
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
