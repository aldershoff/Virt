package businesslogic;

import java.io.IOException;
import java.util.ArrayList;

import javax.naming.ServiceUnavailableException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.manager.util.SessionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.json.simple.JSONObject;

import processUserServices.ProcessUserAccountService;
import processUserServices.ProcessUserBuyService;
import processUserServices.ProcessUserVMControlService;
import processUserServices.ProcessUserVMDataService;
import jsonserializers.BuyCustomerVMSerialiser;
import jsonserializers.LoginUserSerialiser;
import jsonserializers.GetUserVMSerialiser;
import libvirtAccessObject.TestVM;
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
			ProcessUserVMDataService vmData = new ProcessUserVMDataService(
					request, response);

			switch (request.getParameter("request")) {
			case "getallvm":
				vmData.getAllVM();
				break;
			case "getvm":
				vmData.getSpecificVM();
				break;
			default:
				break;
			}
			break;
		case "/customer/controlpanel/monitorvms":
			getRealtimeUserVM(request, response);
			break;
		case "/customer/getprofiledetails":
			
			/**
			 * Call the userdetails method
			 */
			ProcessUserAccountService getUserDetails = new ProcessUserAccountService(request, response);
			getUserDetails.processGetUserDetails();
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
		ProcessUserAccountService userAccount = null;

		switch (userPath) {
		case "/login/processlogin":
			userAccount = new ProcessUserAccountService(request, response);
			userAccount.processLogin();
			break;

		case "/register/processregister":
			userAccount = new ProcessUserAccountService(request, response);
			userAccount.processRegister();
			break;

		case "/customer/marketplace/buy/processbuy":
			ProcessUserBuyService userBuy = new ProcessUserBuyService(request, response);
			userBuy.buyUserVM();
			break;

		case "/customer/controlpanel/vmcontrol":

			ProcessUserVMControlService vmControl = new ProcessUserVMControlService(
					request, response);
			

			switch (request.getParameter("action")) {
			case "Start":
				vmControl.startVM();
				break;
			case "Stop":
				vmControl.stopVM();
				break;
			case "Edit":
				vmControl.editVM();
				break;
			case "Delete":
				vmControl.deleteVM();
				break;
			default:
				break;
			}

			break;

		case "/customer/insertUserMobileID":
			insertRegIDMobile(request, response);
			break;

		case "/customer/getUserMobileID":
			getRegIDMobile(request, response);
			break;
			
		case "/customer/profile/updateprofiledetails":
			ProcessUserAccountService updateProfile = new ProcessUserAccountService(request, response);
			updateProfile.processUpdateUserProfileDetails();
			break;
		
		case "/customer/profile/updateaccountdetails":
			ProcessUserAccountService updateAccount = new ProcessUserAccountService(request, response);
			updateAccount.processUpdateUserAccountDetails();
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
	private void getRealtimeUserVM(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {

		/**
		 * Get the data from libvirt object example
		 */
//		VMBean vmBean = new VMBean();
//		Gson gson = new Gson();
//		String json = "";
//		TestVM vmData = new TestVM();
//		
//		try{
//		vmBean = vmData.getData(vmBean);
//		
//		if(vmBean != null){
//			json = gson.toJson(vmBean);
//		}
//		
//		
//		}
//		finally{
//			response.setContentType("application/json");
//			response.getWriter().write(json.toString());
//		}
		
//		
//		/**
//		 * Set parameters
//		 */
//		String vmID = request.getParameter("vmid");
//		String userID = request.getParameter("userID");
//
//		/**
//		 * Fill the data
//		 */
//		vmBean = new VMBean();
//		vmBean = VMDAO.getSpecificVM(vmBean, userID, vmID);
//
//		/**
//		 * If bean is valid, send new vmBean back as response.
//		 */
//		if (vmBean.isValid()) {
//
//			/*
//			 * Set realtime data here, preverably from Libvirt
//			 */
//
//		}

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

		/**
		 * Getting the parameters
		 */
		String regID = request.getParameter("regID");
		String userID = request.getParameter("userID");

		/**
		 * Making contact with database and get result
		 */
		TwoFactorDAO mobile = new TwoFactorDAO();
		int result = mobile.registerMobileID(regID, userID);

		/**
		 * Sending back the result
		 */
		JSONObject jobj = new JSONObject();
		jobj.put("result", result);
		response.setContentType("application/json");
		response.getWriter().write(jobj.toString());
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

		/**
		 * Getting the parameters
		 */
		String userID = request.getParameter("userID");

		/**
		 * Making contact with database and get result
		 */
		TwoFactorDAO mobile = new TwoFactorDAO();
		String result = mobile.getMobileID(userID);


		/**
		 * Sending back the result
		 */
		JSONObject jobj = new JSONObject();
		jobj.put("result", result);
		response.setContentType("application/json");
		response.getWriter().write(jobj.toString());

	}

}
