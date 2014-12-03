package presentation;

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

			

			/**
			 * Checking if the parametermapping is empty. If so, the request is
			 * an usual request from the menu within the website
			 */
			if (request.getParameterMap().isEmpty()) {
				setGetControllerUrls(userPath, request, response,
						session);
			}

			/**
			 * If the parameter has a request parameter inside it's url, the
			 * request is comming from a HTML link like a HREF (or redirection)
			 */
			else if (request.getParameterMap().containsKey("request")) {
				getUserRequest(request, response);
				template.merge(vsl_Context, out);
				cleanVelocity();
				vsl_Context.put("baseUrl", request.getContextPath());
				out.close();
			}

//			/**
//			 * If the parameter contains a response keyword, the data is comming
//			 * from the back-end servlet Mostly, the servlet will get data back
//			 * from the back-end
//			 */
//			else if (request.getParameterMap().containsKey("response")) {
//
//				getBackEndResponse(request, response,session, custBean);
//			}
//			
			else if(request.getParameterMap().containsKey("error")){
				getErrorResponse(request, response, session);
				
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {


	}

	/**
	 * Method for handling the GET requests, done by clicking on the menu. These
	 * requests will forward to the back-end if needed
	 * 
	 * @param userPath
	 * @param custBean
	 * @param vmBeanArrayList
	 * @throws IOException
	 */
	private void setGetControllerUrls(String userPath,
			HttpServletRequest request, HttpServletResponse response,
			HttpSession session) throws IOException {
		vsl_Context.put("name", session.getAttribute("username"));
		vsl_Context.put("id", session.getAttribute("userID"));

		switch (userPath) {

		case "/customer/home":
			template = Velocity.getTemplate("Velocity/customers/index.html");
			break;

		case "/customer/controlpanel":
			template = Velocity
					.getTemplate("Velocity/customers/controlpanel.html");
			break;

		case "/customer/marketplace":
			template = Velocity
					.getTemplate("Velocity/customers/marketplace.html");
			break;
		case "/customer/marketplace/windows":
			template = Velocity.getTemplate("Velocity/customers/windows.html");
			break;
		case "/customer/marketplace/debian":
			template = Velocity.getTemplate("Velocity/customers/debian.html");
			break;
		case "/customer/marketplace/slackware":
			template = Velocity.getTemplate("Velocity/customers/windows.html");
			break;

		default:
			break;
		}
	}

	/**
	 * Method for handling user requests, like links (href) from HTML web pages.
	 * Parameters will be given with useful information for forwarding to the
	 * back-end
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void getUserRequest(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		String UrlRequest = request.getParameter("request");


		switch (UrlRequest) {
		case "getvm":

			/**
			 * Send request to the back-end servlet for getting the specific VM
			 * data
			 */
			template = Velocity
					.getTemplate("Velocity/customers/controlpanel.html");
			
//			response.sendRedirect("/ProjectVirt/customer/controlpanel/getvms?request="
//							+ sendRequest + "&vmid=" + vmID);
			
			break;

		case "monitor":

			/**
			 * Send request to the back-end servlet for getting the monitor data
			 */
			template = Velocity
					.getTemplate("Velocity/customers/monitor/index.html");
			break;

		case "buy":

			/**
			 * Checking if the fields were not left empty
			 */
			if (request.getParameter("RAM") == "vmOS"
					|| request.getParameter("CPU") == "vmCPU"
					|| request.getParameter("HDD") == "vmHDD"
					|| request.getParameter("SLA") == "vmSLA") {
				response.sendRedirect(request.getRequestURI()
						+ "/error?name=fieldsnotfilled");
			} else {

				/**
				 * Send request to the back-end servlet for getting the buy data
				 */
				
				RequestDispatcher dispatcher = request.getRequestDispatcher("/customer/marketplace/buy/processbuy");
				try {
					dispatcher.forward(request, response);
				} catch (ServletException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			break;

		default:
			break;
		}

	}
////
//	/**
//	 * Method for getting responses from the back-end servlet. These responses
//	 * are usually beans, filled with data These beans can therefore be
//	 * delivered to the Velocity template engine
//	 * 
//	 * @param request
//	 * @param response
//	 * @throws IOException
//	 */
//	private void getBackEndResponse(HttpServletRequest request,
//			HttpServletResponse response, HttpSession session,CustomerBean custBean)
//			throws IOException {
//		vsl_Context.put("name", custBean.getUsername());
//		vsl_Context.put("id", custBean.getUserID());
//
//		
//		String backEndResponse = request.getParameter("response");
//		VMBean vmBean = null;
//		String jsonObject = null;
//		
//		switch (backEndResponse) {
//		case "getallvm":
//			/**
//			 * If the message is OK, the data has been processed correctly and
//			 * the data can be transfered to a local data type
//			 */
//
//			jsonObject = (String) session.getAttribute("json");
//			session.removeAttribute("json");
//	        response.getWriter().write(jsonObject.toString());
//
//			break;
//
//		case "getvm":
//
//			/**
//			 * If the data was correctly processed, the servlet will process the
//			 * bean
//			 */
//			jsonObject = (String) session.getAttribute("json");
//			session.removeAttribute("json");
//	        response.getWriter().write(jsonObject.toString());
//			break;
//			
//		case "monitor":
//
//			/**
//			 * If the data was correctly processed, the servlet will process the
//			 * bean
//			 */
//			vmBean = (VMBean) session.getAttribute("vmBean");
//			session.removeAttribute("vmBean");
//			vsl_Context.put("vmBean", vmBean);
//			template = Velocity
//					.getTemplate("Velocity/customers/monitor/index.html");
//			
//			break;
//		}
//		
//		
//
//	}


		private void getErrorResponse(HttpServletRequest request,
				HttpServletResponse response, HttpSession session){
			vsl_Context.put("name", session.getAttribute("username"));
			vsl_Context.put("id", session.getAttribute("userID"));
			String backEndResponse = request.getParameter("error");
			String error = null;
			
			switch (backEndResponse) {
			case "getallvm":
				error = "Something went wrong";
				vsl_Context.put("error", error);
				template = Velocity
						.getTemplate("Velocity/customers/controlpanel.html");
				break;
				
			case "getvm":
				error = "Something went wrong";
				vsl_Context.put("error", error);
				template = Velocity
						.getTemplate("Velocity/customers/controlpanel.html");
				
				

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
