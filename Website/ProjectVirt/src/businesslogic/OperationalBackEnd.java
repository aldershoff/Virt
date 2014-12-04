package businesslogic;

import java.io.IOException;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.naming.ServiceUnavailableException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jsonserializers.CustomerSerialiser;
import jsonserializers.GetUserVMSerialiser;
import security.PasswordService;
import beans.CustomerBean;
import beans.VMBean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import databaseAccessObjects.BuyDAO;
import databaseAccessObjects.UserDAO;
import databaseAccessObjects.VMDAO;

/**
 * The servlet for getting and Posting information through and from the database
 * and KVM. This servlet will handle all the operations needed to get all the
 * information back to the user servlet. Basicly, this is the REST server
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

		// Making new VMDAO for making connection
		VMDAO VMDAO = new VMDAO();

		/**
		 * Getting the requested URL and userID from the parameters
		 */
		String urlRequest = request.getParameter("request");
		String userID = null;

		/**
		 * Switch for checking which service has been requested
		 */
		switch (urlRequest) {

		/**
		 * Service for getting all possible VMs from the user
		 */
		case "getallvm":

			// Setting the userID and new arraylist for getting all the VMs
			userID = request.getParameter("userID");
			ArrayList<VMBean> vmBeanArray = VMDAO.getVMs(userID);

			/**
			 * If the array is not null, a new JSON String will be made and will
			 * be send to the output
			 */
			if (vmBeanArray != null) {

				Gson gson = new Gson();
				String jsonVMBeanArray = gson.toJson(vmBeanArray);
				response.setContentType("application/json");
				response.getWriter().write(jsonVMBeanArray.toString());

			}
			break;

		/**
		 * Service for getting a specific VM for the user
		 */
		case "getvm":

			/**
			 * Setting the parameters for the VM and User ID
			 */
			String vmID = request.getParameter("vmid");
			userID = request.getParameter("userID");
			Gson gson = null;
			
			/**
			 * Making new bean and fill it with data
			 */
			VMBean vmBean = new VMBean();
			vmBean = VMDAO.getSpecificVM(vmBean, userID, vmID);

			/**
			 * If the bean is valid, it will return a new JSON response
			 */
			if (vmBean.isValid()) {

				try {

					/**
					 * Setting builder and serialiser for appropiate data
					 */
					final GsonBuilder gsonBuilder = new GsonBuilder();
					gsonBuilder.registerTypeAdapter(VMBean.class,
							new GetUserVMSerialiser());
					gson = gsonBuilder.create();

				} finally {

					/**
					 * Convert bean to json and send it to as a String
					 */
					String json = gson.toJson(vmBean);
					response.setContentType("application/json");
					response.getWriter().write(json.toString());
				}

			}

			break;

		default:
			break;
		}
	}

	/**
	 * Method for getting the specific VM data for monitoring the VM. This data
	 * will also have real time specifications Still working on this one!
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void getMonitorUserVM(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {

		/**
		 * Setting VMDAO and bean for filling data
		 */
		VMDAO VMDAO = new VMDAO();
		VMBean vmBean = null;
		Gson gson = null;

		/**
		 * Set parameters
		 */
		String vmID = request.getParameter("vmid");
		String userID = request.getParameter("userID");

		/**
		 * Fill the data
		 */
		vmBean = new VMBean();
		vmBean = VMDAO.getSpecificVM(vmBean, userID, vmID);

		/**
		 * If bean is valid, send new vmBean back as response.
		 */
		if (vmBean.isValid()) {
			
			/*
			 * Set realtime data here, preverably from Libvirt
			 */
			
		}

	}

	/**
	 * Must finish this method, does not work yet!!
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void buyUserVM(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {
		BuyDAO buyDAO = new BuyDAO();

		VMBean vmBean = new VMBean();
		// vmBean = buyDAO.addVM(vmBean, custBean.getUserID());

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

		String parameterUsername = request.getParameter("user");
		String parameterPassword = request.getParameter("password");
		String parameterRememberme = request.getParameter("rememberme");

		// Getting parameters from form (login.html)
		custBean.setUserName(parameterUsername);
		try {
			custBean.setPassword(new PasswordService().getInstance().encrypt(
					parameterPassword));
		} catch (ServiceUnavailableException e) {
			e.printStackTrace();
		}

		// Making new login Data Access Object
		UserDAO checkLog = new UserDAO();

		// Make connection, fill bean and return it
		custBean = checkLog.login(custBean);

		/**
		 * If rememberMe is checked, a cookie will be exchanged to the user
		 */
		if (parameterRememberme != null) {

			custBean.setRememberMe(true);
		}

		/**
		 * Set the gson serialiser for appropriate data and converting the customer bean to JSON string
		 */
		try {

			final GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(CustomerBean.class,
					new CustomerSerialiser());
			gson = gsonBuilder.create();

		} 
		
		/**
		 * Do the converting
		 */
		finally {

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

		// Making new bean for the customer
		CustomerBean custBean = new CustomerBean();

		// Getting parameters from form (login.html)
		custBean.setUserName(request.getParameter("user"));

		try {
			custBean.setPassword(new PasswordService().getInstance().encrypt(
					request.getParameter("password")));
		} catch (ServiceUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/**
		 * Setting all the requests into the bean for processing
		 */
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

		// Setting GSON
		Gson gson = null;

		// If bean is not null, a database connection has been initiated
		if (custBean != null) {

			/**
			 * If the user is succesfully authenticated, and the bean has been
			 * filled with information, a new session can be made
			 */

			if (custBean.isValid()) {
				try {
					final GsonBuilder gsonBuilder = new GsonBuilder();
					gsonBuilder.registerTypeAdapter(CustomerBean.class,
							new CustomerSerialiser());
					gson = gsonBuilder.create();

				} finally {

					String json = gson.toJson(custBean);
					response.setContentType("application/json");
					response.getWriter().write(json.toString());
				}
			}

		}
	}


	
	
}
