package processUserServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jsonserializers.GetUserVMSerialiser;
import libvirtAccessObject.VMmanagementTest;

import org.json.simple.JSONObject;
import org.libvirt.LibvirtException;

import beans.VMBean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import databaseAccessObjects.VMDAO;

public class ProcessUserVMDataService {

	private HttpServletRequest request;
	private HttpServletResponse response;

	public ProcessUserVMDataService(HttpServletRequest request,
			HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	@SuppressWarnings("unchecked")
	public void getAllVM() throws IOException {

		// Making new VMDAO for making connection
		VMDAO VMDAO = new VMDAO();
		String userID = null;

		/**
		 * Setting GSON variables
		 */
		Gson gson = new Gson();
		JSONObject jobj = new JSONObject();
		String json = "";
		String error = "";

		// Setting the userID and new arraylist for getting all the VMs
		userID = request.getParameter("userID");
		ArrayList<VMBean> vmBeanArray = VMDAO.getVMs(userID);

		try {
			/**
			 * If the array is not null, a new JSON String will be made and will
			 * be send to the output
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
	}
	
	
	/**
	 * Method for getting the realtime VM data, with an connection with LibVirt
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void getRealtimeVMData() throws IOException {

		/**
		 * Making variables
		 */
		JSONObject jobj = new JSONObject();
		String error = "";
		String json = "";

		/**
		 * Getting parameters
		 */
		String vmID = request.getParameter("vmID");
		String userID = request.getParameter("userID");

		/**
		 * Making new DAO and get the static data from database
		 */
		VMDAO refreshRealtimeDAO = new VMDAO();

		/**
		 * Getting the UUID and storage key from the database (for the realtime data)
		 */
		UUID UUID = java.util.UUID.fromString(refreshRealtimeDAO
				.getVMUUID(vmID));
		String storageKey = refreshRealtimeDAO.getStorKey(vmID);

		/**
		 * Try to get all the realtime data from libvirt
		 */
		try {
			if (UUID != null) {

				// Making a new LibVirt API object
				VMmanagementTest libVirtAPIObject = new VMmanagementTest();

				// Getting the storage variables directly from LibVirt
				String[] storage = libVirtAPIObject.getVolume(storageKey);

				/**
				 * Getting the storage capacity
				 */
				String storageCapacity;
				String storageAllocation;
				storageCapacity = storage[0];
				storageAllocation = storage[1];

				// Making a new LibVirt Bean for returning to user
				VMBean resultLibVirtBean = new VMBean();

				// Fill the LibVirtBean from the database and get the data that
				// is static
				resultLibVirtBean = refreshRealtimeDAO.getSpecificVM(
						resultLibVirtBean, userID, vmID);

				// Setting the Disk Space
				resultLibVirtBean.setVMDiskSpace(storageCapacity);

				// Setting the storage allocation
				resultLibVirtBean.setVMMonthlyPrice(storageAllocation);
				
				// Get all the live data
				resultLibVirtBean = libVirtAPIObject.getLiveData(UUID,
						resultLibVirtBean);

				/**
				 * Setting builder and serialiser for appropiate data
				 */
				final GsonBuilder gsonBuilder = new GsonBuilder();
				gsonBuilder.registerTypeAdapter(VMBean.class,
						new GetUserVMSerialiser());
				Gson gson = gsonBuilder.create();
				json = gson.toJson(resultLibVirtBean);
			} else {
				error = "Could not add UUID";
				jobj.put("error", error);
			}
		} catch (LibvirtException e) {
			error = e.getError().getMessage().toString();
			jobj.put("error", error);
		}
		/**
		 * Write it back to the user
		 */
		finally {
			response.setContentType("application/json");
			if (error != "") {
				response.getWriter().write(jobj.toString());

			} else {
				response.getWriter().write(json.toString());
			}

		}
	}
	
	
	//+++++++++++++++ COuld BE handy later

	@SuppressWarnings("unchecked")
	public void getSpecificVM() throws IOException {

		// Making new VMDAO for making connection
		VMDAO VMDAO = new VMDAO();
		String userID = null;

		/**
		 * Setting GSON variables
		 */
		Gson gson = new Gson();
		JSONObject jobj = new JSONObject();
		String json = "";
		String error = "";

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

		} finally {
			response.setContentType("application/json");
			if (error != "") {
				response.getWriter().write(jobj.toString());

			} else {
				response.getWriter().write(json.toString());
			}
		}
	}
}
