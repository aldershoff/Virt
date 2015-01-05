package processUserServices;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jsonserializers.GetUserDetailsSerialiser;
import jsonserializers.GetUserVMSerialiser;
import libvirtAccessObject.VMmanagementTest;

import org.json.simple.JSONObject;
import org.libvirt.LibvirtException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import beans.CustomerBean;
import beans.VMBean;
import databaseAccessObjects.VMDAO;

public class ProcessUserVMControlService {

	private HttpServletRequest request;
	private HttpServletResponse response;

	
	public ProcessUserVMControlService(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	@SuppressWarnings("unchecked")
	public void startVM() throws IOException {
		JSONObject jobj = new JSONObject();
		String error = "";
		
		String userID = request.getParameter("userID");
		String vmID = request.getParameter("vmID");

		/**
		 * Making new DAO and get the results
		 */
		VMDAO startVM = new VMDAO();
		

		/**
		 * Function for also making connection with libvirt API
		 */
//		VMmanagementTest vmManagement = new VMmanagementTest();
//		
//		int resultLibVirt = vmManagement.deleteVM("VMUUID");
//		
//			if(resultLibVirt != 2){
//				if(resultLibVirt == 1){
//					int result = deleteVM.deleteSpecificVM(vmID, userID);
//					try {
//						if (result != 2) {
//
//							/**
//							 * If the bean is valid, it will return a new JSON response
//							 */
//							if (result == 1) {
//								jobj.put("success", "VM succesfully deleted!");
//
//							} else {
//								error = "Could not delete VM...";
//								jobj.put("error", error);
//							}
//						} else {
//							error = "Can't connect with database";
//							jobj.put("error", error);
//
//						}
//
//					} finally {
//						response.setContentType("application/json");
//						response.getWriter().write(jobj.toString());
//
//					}
//				}
//			}
//			else{
//				error = "Can't connect with libVirt!";
//				jobj.put("error", error);
//			}
		
		
		int result = startVM.startSpecificVMState(vmID, userID);

		
		try {
			if (result != 2) {

				/**
				 * If the bean is valid, it will return a new JSON response
				 */
				if (result == 1) {
					jobj.put("success", "VM succesfully started!");

				} else {
					error = "Could not start VM...";
					jobj.put("error", error);
				}
			} else {
				error = "Can't connect with database";
				jobj.put("error", error);

			}

		} finally {
			response.setContentType("application/json");
			response.getWriter().write(jobj.toString());

		}
	}

	@SuppressWarnings("unchecked")
	public void editVM() throws IOException {
		JSONObject jobj = new JSONObject();
		String error = "";
		String json = "";
		
		VMBean editVMBean = new VMBean();
		String userID = request.getParameter("userID");
		editVMBean.setVMID(Integer.parseInt(request.getParameter("vmID")));
		editVMBean.setVMName(request.getParameter("vmName"));
		editVMBean.setVMCPU(request.getParameter("vmCPU"));
		editVMBean.setVMMemory(request.getParameter("vmRAM"));
		editVMBean.setVMDiskSpace(request.getParameter("vmHDD"));
		editVMBean.setVMSLA(request.getParameter("vmSLA"));
		
		
		/**
		 * Making new DAO and get the results
		 */
		VMDAO editVM = new VMDAO();
		

		/**
		 * Function for also making connection with libvirt API
		 */
//		VMmanagementTest vmManagement = new VMmanagementTest();
//		
//		int resultLibVirt = vmManagement.deleteVM("VMUUID");
//		
//			if(resultLibVirt != 2){
//				if(resultLibVirt == 1){
//					int result = deleteVM.deleteSpecificVM(vmID, userID);
//					try {
//						if (result != 2) {
//
//							/**
//							 * If the bean is valid, it will return a new JSON response
//							 */
//							if (result == 1) {
//								jobj.put("success", "VM succesfully deleted!");
//
//							} else {
//								error = "Could not delete VM...";
//								jobj.put("error", error);
//							}
//						} else {
//							error = "Can't connect with database";
//							jobj.put("error", error);
//
//						}
//
//					} finally {
//						response.setContentType("application/json");
//						response.getWriter().write(jobj.toString());
//
//					}
//				}
//			}
//			else{
//				error = "Can't connect with libVirt!";
//				jobj.put("error", error);
//			}
		
		
		int result = editVM.editSpecificVM(userID, editVMBean);

		
		try {
			if (result != 2) {

				/**
				 * If the bean is valid, it will return a new JSON response
				 */
				if (result == 1) {
					final GsonBuilder gsonBuilder = new GsonBuilder();
					gsonBuilder.registerTypeAdapter(VMBean.class,
							new GetUserVMSerialiser());
					Gson gson = gsonBuilder.create();
					json = gson.toJson(editVMBean);

				} else {
					error = "Could not edit VM...";
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

	@SuppressWarnings("unchecked")
	public void stopVM() throws IOException {
		JSONObject jobj = new JSONObject();
		String error = "";

		String userID = request.getParameter("userID");
		String vmID = request.getParameter("vmID");
		
		/**
		 * Making new DAO and get the results
		 */
		VMDAO stopVM = new VMDAO();
		

		/**
		 * Function for also making connection with libvirt API
		 */
//		VMmanagementTest vmManagement = new VMmanagementTest();
//		
//		int resultLibVirt = vmManagement.deleteVM("VMUUID");
//		
//			if(resultLibVirt != 2){
//				if(resultLibVirt == 1){
//					int result = deleteVM.deleteSpecificVM(vmID, userID);
//					try {
//						if (result != 2) {
//
//							/**
//							 * If the bean is valid, it will return a new JSON response
//							 */
//							if (result == 1) {
//								jobj.put("success", "VM succesfully deleted!");
//
//							} else {
//								error = "Could not delete VM...";
//								jobj.put("error", error);
//							}
//						} else {
//							error = "Can't connect with database";
//							jobj.put("error", error);
//
//						}
//
//					} finally {
//						response.setContentType("application/json");
//						response.getWriter().write(jobj.toString());
//
//					}
//				}
//			}
//			else{
//				error = "Can't connect with libVirt!";
//				jobj.put("error", error);
//			}
		
		
		int result = stopVM.stopSpecificVM(vmID, userID);

		
		try {
			if (result != 2) {

				/**
				 * If the bean is valid, it will return a new JSON response
				 */
				if (result == 1) {
					jobj.put("success", "VM succesfully edited!");

				} else {
					error = "Could not stop VM...";
					jobj.put("error", error);
				}
			} else {
				error = "Can't connect with database";
				jobj.put("error", error);

			}

		} finally {
			response.setContentType("application/json");
			response.getWriter().write(jobj.toString());

		}
	}

	@SuppressWarnings("unchecked")
	public void deleteVM() throws IOException {

		JSONObject jobj = new JSONObject();
		String error = "";

		String userID = request.getParameter("userID");
		String vmID = request.getParameter("vmID");
		
		/**
		 * Making new DAO and get the results
		 */
		VMDAO deleteVM = new VMDAO();
		

		/**
		 * Function for also making connection with libvirt API
		 */
//		VMmanagementTest vmManagement = new VMmanagementTest();
//		
//		int resultLibVirt = vmManagement.deleteVM("VMUUID");
//		
//			if(resultLibVirt != 2){
//				if(resultLibVirt == 1){
//					int result = deleteVM.deleteSpecificVM(vmID, userID);
//					try {
//						if (result != 2) {
//
//							/**
//							 * If the bean is valid, it will return a new JSON response
//							 */
//							if (result == 1) {
//								jobj.put("success", "VM succesfully deleted!");
//
//							} else {
//								error = "Could not delete VM...";
//								jobj.put("error", error);
//							}
//						} else {
//							error = "Can't connect with database";
//							jobj.put("error", error);
//
//						}
//
//					} finally {
//						response.setContentType("application/json");
//						response.getWriter().write(jobj.toString());
//
//					}
//				}
//			}
//			else{
//				error = "Can't connect with libVirt!";
//				jobj.put("error", error);
//			}
		
		
		int result = deleteVM.deleteSpecificVM(vmID, userID);

		
		try {
			if (result != 2) {

				/**
				 * If the bean is valid, it will return a new JSON response
				 */
				if (result == 1) {
					jobj.put("success", "VM succesfully deleted!");

				} else {
					error = "Could not delete VM...";
					jobj.put("error", error);
				}
			} else {
				error = "Can't connect with database";
				jobj.put("error", error);

			}

		} finally {
			response.setContentType("application/json");
			response.getWriter().write(jobj.toString());

		}
	}
	@SuppressWarnings("unchecked")
	public void refreshVMState() throws IOException, LibvirtException {

		JSONObject jobj = new JSONObject();
		String error = "";
		
		String vmID = request.getParameter("vmID");

		/**
		 * Making new DAO and get the results
		 */
		VMDAO refreshVMState = new VMDAO();
		
		UUID UUID = java.util.UUID.fromString(refreshVMState.getVMUUID(vmID));
		/**
		 * Function for also making connection with libvirt API
		 */
		VMmanagementTest vmManagement = new VMmanagementTest();
		
		int resultLibVirt = vmManagement.checkState(UUID);
		
		try{
			if(resultLibVirt != 2){
				if(resultLibVirt == 1){
					int result = refreshVMState.refreshSpecificVM(vmID);
					
						if (result != 2) {

							/**
							 * If the bean is valid, it will return a new JSON response
							 */
							if (result == 1) {
								jobj.put("success", "Running");

							} else {
								error = "Stopped";
								jobj.put("error", error);
							}
						} else {
							error = "Error";
							jobj.put("error", error);

						}

					} 
				
			}
			else{
				error = "Can't connect with libVirt!";
				jobj.put("error", error);
			}
		}
		finally {
			response.setContentType("application/json");
			response.getWriter().write(jobj.toString());

		}		
	}
}
