package businesslogic;

import java.io.IOException;
import java.util.ArrayList;
import javax.naming.ServiceUnavailableException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.RandomStringUtils;
import org.json.simple.JSONObject;

import jsonserializers.BuyCustomerVMSerialiser;
import jsonserializers.CustomerSerialiser;
import jsonserializers.GetUserVMSerialiser;
import security.PasswordService;
import beans.CustomerBean;
import beans.VMBean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import databaseAccessObjects.BuyDAO;
import databaseAccessObjects.TwoFactorDAO;
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
@SuppressWarnings({ "unchecked" })
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
	@Override
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
	@Override
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

		case "/customer/insertUserMobileID":
			insertRegIDMobile(request, response);
			break;

		case "/customer/getUserMobileID":
			getRegIDMobile(request, response);
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
		 * Setting GSON variables
		 */
		Gson gson = new Gson();
		JSONObject jobj = new JSONObject();
		String json = "";
		String error = "";

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

			try {
				/**
				 * If the array is not null, a new JSON String will be made and
				 * will be send to the output
				 */
				if (vmBeanArray != null) {

					json = gson.toJson(vmBeanArray);

				} else {
					error = "Could not connect to database";
					jobj.put("error", error);
				}
			} finally {
				response.setContentType("application/json");
				if (error != "") {
					response.getWriter().write(jobj.toString());
					
				} else {
					response.getWriter().write(json.toString());
				}
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

			/**
			 * Making new bean and fill it with data
			 */
			VMBean vmBean = new VMBean();
			vmBean = VMDAO.getSpecificVM(vmBean, userID, vmID);

			try {
				if (vmBean != null) {

					/**
					 * If the bean is valid, it will return a new JSON response
					 */
					if (vmBean.isValid()) {

						/**
						 * Setting builder and serialiser for appropiate data
						 */
						final GsonBuilder gsonBuilder = new GsonBuilder();
						gsonBuilder.registerTypeAdapter(VMBean.class,
								new GetUserVMSerialiser());
						gson = gsonBuilder.create();
						json = gson.toJson(vmBean);
					} else {
						error = "Something is wrong with the VM..";
						jobj.put("error", error);
					}
				} else {
					error = "Can't connect with database";
					jobj.put("error", error);

				}
			}

			/**
			 * Convert bean to json and send it to as a String
			 */
			finally {
				response.setContentType("application/json");
				if (error != "") {
					response.getWriter().write(jobj.toString());

				} else {
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
	 * Method for adding new VM for user
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void buyUserVM(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {

		/**
		 * Getting all parameters needed for creating VM
		 */
		String userID = request.getParameter("userID");

		// Make new DAO
		BuyDAO buyDAO = new BuyDAO();

		/**
		 * Make new bean and insert DAO with all parameters
		 */
		VMBean vmBean = new VMBean();

		/**
		 * Setting all parameters from user inside the vmBean
		 */
		vmBean.setVMOS(request.getParameter("VMOS"));
		vmBean.setVMMemory(request.getParameter("VMRAM"));
		vmBean.setVMCPU(request.getParameter("VMCPU"));
		vmBean.setVMDiskSpace(request.getParameter("VMHDD"));
		vmBean.setVMSLA(request.getParameter("VMSLA"));

		switch (vmBean.getVMOS()) {
		case "debian":
			vmBean.setVMName("Debian VM - "
					+ (RandomStringUtils.random(8, true, true)));
			break;
		case "windows":
			vmBean.setVMName("Windows VM - "
					+ (RandomStringUtils.random(8, true, true)));
			break;
		case "slackware":
			vmBean.setVMName("SlackWare VM - "
					+ (RandomStringUtils.random(8, true, true)));
			break;

		}

		// Set the bean and fill it
		vmBean = buyDAO.addVM(vmBean, userID);

		Gson gson = new Gson();
		JSONObject jobj = new JSONObject();
		String error = "";
		String json = "";

		try {

			// If bean is not null, a database connection has been initiated
			if (vmBean != null) {

				/**
				 * If the VM is succesfully added to the database, the user will
				 * get a response back
				 */

				if (vmBean.isValid()) {

					final GsonBuilder gsonBuilder = new GsonBuilder();
					gsonBuilder.registerTypeAdapter(VMBean.class,
							new BuyCustomerVMSerialiser());
					gson = gsonBuilder.create();

					json = gson.toJson(vmBean);

				}

				else {
					error = "Something went wrong with buying the VM";
					jobj.put("error", error);
				}
			}

			else {
				error = "Could not connect to database..";
				jobj.put("error", error);
			}
		} finally {
			response.setContentType("application/json");
			if (error != "") {
				response.getWriter().write(jobj.toString());
			} else {
				response.getWriter().write(json.toString());
			}
		}

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

		Gson gson = new Gson();
		JSONObject jobj = new JSONObject();
		String error = "";
		String json = "";

		try {
			if (custBean != null) {

				if(custBean.isValid()){
					/**
					 * Set the gson serialiser for appropriate data and
					 * converting the customer bean to JSON string
					 */
					final GsonBuilder gsonBuilder = new GsonBuilder();
					gsonBuilder.registerTypeAdapter(CustomerBean.class,
							new CustomerSerialiser());
					gson = gsonBuilder.create();
					json = gson.toJson(custBean);
				}
					
					else{
						error = "Wrong username or password";
						jobj.put("error", error);
					}
			} else {
				error = "Could not connect to database";
				jobj.put("error", error);
			}
			
			
		} finally {
			response.setContentType("application/json");
			if(error != ""){
				response.getWriter().write(jobj.toString());		
			}
			else{
				response.getWriter().write(json.toString());
			}
			
				
			
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
		custBean.setEmail(request.getParameter("email"));

		// Making new login Data Access Object
		UserDAO checkLog = new UserDAO();

		// Make connection, fill bean and return it
		custBean = checkLog.register(custBean);

		Gson gson = new Gson();
		JSONObject jobj = new JSONObject();
		String error = "";
		String json = "";

		try {

			// If bean is not null, a database connection has been initiated
			if (custBean != null) {

				/**
				 * If the user is succesfully authenticated, and the bean has
				 * been filled with information, a new session can be made
				 */

				if (custBean.isValid()) {
					final GsonBuilder gsonBuilder = new GsonBuilder();
					gsonBuilder.registerTypeAdapter(CustomerBean.class,
							new CustomerSerialiser());
					gson = gsonBuilder.create();
					json = gson.toJson(custBean);

				} else {
					error = "Something went wrong while registering..";
					jobj.put("error", error);
				}
			} else {
				error = "Could not connect to database";
				jobj.put("error", error);
			}

		} finally {
			response.setContentType("application/json");
			if (error != "") {
				response.getWriter().write(jobj.toString());
			} else {
				response.getWriter().write(json.toString());
			}
		}
	}

	
	
	
	
	// GCM Development
	// ===========================================================================
	// ===========================================================================

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
	private void insertRegIDMobile(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {

		String regID = request.getParameter("regID");

		TwoFactorDAO mobile = new TwoFactorDAO();

		int result = mobile.registerMobileID(regID);

		Gson gson = null;
		// If bean is not null, a database connection has been initiated
		if (result != 2) {

			/**
			 * If the user is succesfully authenticated, and the bean has been
			 * filled with information, a new session can be made
			 */

			if (result == 1) {
				try {
					final GsonBuilder gsonBuilder = new GsonBuilder();
					gsonBuilder.registerTypeAdapter(CustomerBean.class,
							new CustomerSerialiser());
					gson = gsonBuilder.create();

				} finally {

					String json = gson.toJson(result);
					response.setContentType("application/json");
					response.getWriter().write(json.toString());
				}
			}

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
	private void getRegIDMobile(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {

		String regID = request.getParameter("regID");
		String userID = request.getParameter("userID");
		TwoFactorDAO mobile = new TwoFactorDAO();

		int result = mobile.getMobileID(regID, userID);

		Gson gson = null;
		// If bean is not null, a database connection has been initiated
		if (result != 2) {

			/**
			 * If the user is succesfully authenticated, and the bean has been
			 * filled with information, a new session can be made
			 */
			try {
				final GsonBuilder gsonBuilder = new GsonBuilder();
				gsonBuilder.registerTypeAdapter(CustomerBean.class,
						new CustomerSerialiser());
				gson = gsonBuilder.create();

			} finally {

				String json = gson.toJson(result);
				response.setContentType("application/json");
				response.getWriter().write(json.toString());
			}
		}
	}

}
