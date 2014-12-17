package processUserServices;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jsonserializers.GetUserVMSerialiser;

import org.json.simple.JSONObject;

import beans.VMBean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import databaseAccessObjects.VMDAO;

public class ProcessUserVMDataService {
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	public ProcessUserVMDataService(HttpServletRequest request, HttpServletResponse response){
		this.request = request;
		this.response = response;
	}
	
	
	@SuppressWarnings("unchecked")
	public void getAllVM() throws IOException{

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
	}
	
	@SuppressWarnings("unchecked")
	public void getSpecificVM() throws IOException{
		
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

		}
		finally {
			response.setContentType("application/json");
			if (error != "") {
				response.getWriter().write(jobj.toString());
				
			} else {
				response.getWriter().write(json.toString());
			}
		}
	}
}
	

