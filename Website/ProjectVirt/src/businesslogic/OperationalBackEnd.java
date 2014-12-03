package businesslogic;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.naming.ServiceUnavailableException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jsonserializers.CustomerSerialiser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import net.sf.json.JSONObject;
import security.PasswordService;
import beans.CustomerBean;
import beans.VMBean;
import databaseAccessObjects.BuyDAO;
import databaseAccessObjects.UserDAO;
import databaseAccessObjects.VMDAO;

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

		// Let method with switch decide which method to call for processing the
		// GET calls, like getting VMS
		setGetControllerUrls(userPath, request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// Setting the userPath from servlet
		String userPath = request.getServletPath();

		// Let the method with switch decide which method to call for processing
		// the login / register, etc.
		setPostControllerUrls(userPath, request, response);

	}

	/**
	 * Method for acting as the controller with a switch statement, for GET
	 * calls
	 * 
	 * @param userPath
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void setGetControllerUrls(String userPath,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		switch (userPath) {
		case "/customer/controlpanel/getvms":
			getUserVM(request, response);
			break;
		case "/customer/controlpanel/monitorvms":
			getMonitorUserVM(request, response);
			break;
		case "/customer/logout":
			processLogout(request, response);
			break;	
		default:
			break;
		}
	}

	/**
	 * Method for acting as the controller with a switch statement, for POST
	 * calls
	 * 
	 * @param userPath
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void setPostControllerUrls(String userPath,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		switch (userPath) {
		case "/login/processlogin":
			processLogin(request, response);
			break;

		case "/register/processregister":
			processRegister(request, response);
			break;

		case "/customer/marketplace/buy/processbuy":
			buyUserVM(request, response);
			break;
		default:
			break;
		}
	}

	/**
	 * Method for getting the userVM information
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServletException
	 */
	private void getUserVM(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {

		VMDAO VMDAO = new VMDAO();

		HttpSession session = request.getSession(false);
		CustomerBean custBean = (CustomerBean) session.getAttribute("customer");
			
			String urlRequest = request.getParameter("request");
			
			switch (urlRequest) {
		
			case "getallvm":
				/**
				 * Checking if the attribute is null If so, the query needs to be
				 * runned. If not, the user should give permission to refresh the
				 * servers and get fresh output
				 */
					ArrayList<VMBean> vmBeanArray = VMDAO.getVMs(custBean.getUserID());

					if(vmBeanArray!= null){
						
						Gson gson = new Gson();
				        String jsonVMBeanArray = gson.toJson(vmBeanArray);
				        response.setContentType("application/json");
				        session.setAttribute("json", jsonVMBeanArray);
				       
				        response.getWriter().write(jsonVMBeanArray.toString());
					}	

				
				break;
			
//				 response.sendRedirect("/ProjectVirt/customer/controlpanel?response=getallvm");
//				String userListTable() throws JSONException {
//			        List<User> foundUser = userService.getUsers();
//
//			        JSONArray data = new JSONArray();
//			        JSONObject JSONObject = new JSONObject();
//
//			        JSONObject.put("sEcho", 1);
//			        JSONObject.put("iTotalRecords", foundUser.size());
//			        JSONObject.put("iTotalDisplayRecords", foundUser.size());
//
//			        for (User user : foundUser) {
//			            JSONArray row = new JSONArray();
//			            row.put(user.getUserDetail().getFirstName());
//			            row.put(user.getUserDetail().getLastName());
//			            row.put(user.getBusinessUnit().getName());
//			            row.put(user.getUserDetail().getUserId());
//			                        row.put("");
//			            data.put(row);
//			        }
//			        JSONObject.put("aaData", data);
//			        return ;
//			    }
				
		case "getvm":
			String vmID = request.getParameter("vmid");
			VMBean vmBean = new VMBean();
			vmBean = VMDAO.getSpecificVM(vmBean, custBean.getUserID(), vmID);

			if (vmBean.isValid()) {

				Gson gson = new Gson();
				String jsonVMBean = gson.toJson(vmBean);
				response.setContentType("application/json");
				session.setAttribute("json", jsonVMBean);
				response.sendRedirect("/ProjectVirt/customer/controlpanel?response=getvm");

			}

			break;
				
			default:
				break;
			}
		}
	
	private void getMonitorUserVM(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {
		VMDAO VMDAO = new VMDAO();
		VMBean vmBean = null;

		HttpSession session = request.getSession(false);
		CustomerBean custBean = (CustomerBean) session.getAttribute("customer");

				String vmID = request.getParameter("vmid");
			
					vmBean = new VMBean();
					vmBean = VMDAO.getSpecificVM(vmBean, custBean.getUserID(), vmID);
				
					
					if(vmBean.isValid()){
						JsonGenerator _JsonGenerator = Json.createGenerator(response.getWriter());
						
						_JsonGenerator
							.writeStartObject()
								.write("name", vmBean.getVMName())
								.write("vmid", vmBean.getVmID())
							.writeEnd();
						_JsonGenerator.close();
					}	

		


		
	}

		/**
		 * Must finish this method, does not work yet!!
		 * @param request
		 * @param response
		 * @throws IOException
		 */
	private void buyUserVM(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {
		BuyDAO buyDAO = new BuyDAO();

		HttpSession session = request.getSession(false);
		CustomerBean custBean = (CustomerBean) session.getAttribute("customer");
			
		VMBean vmBean = new VMBean();
		vmBean = buyDAO.addVM(vmBean, custBean.getUserID());

		
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

		Gson gson = null;

		// Making new bean for the customer
		CustomerBean custBean = new CustomerBean();

		// Getting parameters from form (login.html)
		custBean.setUserName(request.getParameter("user"));
		try {
			custBean.setPassword(new PasswordService().getInstance().encrypt(
					request.getParameter("password")));
		} catch (ServiceUnavailableException e) {
			e.printStackTrace();
		}

		// Making new login Data Access Object
		UserDAO checkLog = new UserDAO();

		// Make connection, fill bean and return it
		custBean = checkLog.login(custBean);

		try {
			
			final GsonBuilder gsonBuilder = new GsonBuilder();
		    gsonBuilder.registerTypeAdapter(CustomerBean.class, new CustomerSerialiser());
		    gsonBuilder.setPrettyPrinting();
		    gson = gsonBuilder.create();



			/**
			 * If rememberMe is checked, a cookie will be exchanged to the user
			 */
			if (request.getParameter("rememberme") != null) {

				custBean.setRememberMe(true);
			}
		} finally {

			String json = gson.toJson(custBean);
			response.setContentType("application/json");
			response.getWriter().write(json.toString());
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
	 * @throws ServiceUnavailableException
	 */
	private void processRegister(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {

		if (request.getParameter("user") == ""
				|| request.getParameter("password") == ""
				|| request.getParameter("firstname") == ""
				|| request.getParameter("lastname") == ""
				|| request.getParameter("user") == "") {
			response.sendRedirect("/ProjectVirt/register?error=fieldsnotfilled");


		}

		else{
			// Making new bean for the customer
			CustomerBean custBean = new CustomerBean();

			// Getting parameters from form (login.html)
			custBean.setUserName(request.getParameter("user"));

			try {
				custBean.setPassword(new PasswordService().getInstance()
						.encrypt(request.getParameter("password")));
			} catch (ServiceUnavailableException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			custBean.setDateOfBirth(request.getParameter("dateofbirth"));
			custBean.setFirstName(request.getParameter("firstname"));
			custBean.setLastName(request.getParameter("lastname"));
			custBean.setEmail(request.getParameter("email"));
			custBean.setPhone(request.getParameter("phone"));
			custBean.setAddress(request.getParameter("address"));
			custBean.setZipCode(request.getParameter("zipcode"));

			// Making new login Data Access Object
			UserDAO checkLog = new UserDAO();

			// Make connection, fill bean and return it
			custBean = checkLog.register(custBean);
			// If bean is not null, a database connection has been initiated
			if (custBean != null) {

				/**
				 * If the user is succesfully authenticated, and the bean has
				 * been filled with information, a new session can be made
				 */
				if (custBean.isValid()) {

					// Set parameter succesful back!

				}

				
				
			}
			/**
			 * If returned null, something is wrong with the database
			 */
			else {
				response.sendRedirect(request.getRequestURI()
						+ "/error?response=nodatabase");
			}
		}
		}
			

	/**
	 * Method for destroying the session
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServiceUnavailableException
	 */
	private void processLogout(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {

		// Get session from other servlet
		HttpSession session = request.getSession(false);

			session.invalidate();
			
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
			 * Setting the age to 0 and add cookie cookie to the response again.
			 * The cookie is now destroyed
			 */
			if (loginCookie != null) {
				loginCookie.setMaxAge(0);
				response.addCookie(loginCookie);
			}

			// Redirect to home page
			response.sendRedirect(request.getRequestURI() + "/home");

			return; // <--- Here.
		}


}
