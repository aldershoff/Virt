package processUserServices;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import libvirtAccessObject.VMmanagementTest;

import org.json.simple.JSONObject;

import databaseAccessObjects.VMDAO;

public class ProcessUserVMControlService {

	private HttpServletRequest request;
	private HttpServletResponse response;

	
	public ProcessUserVMControlService(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	@SuppressWarnings("unchecked")
	public void startVM(String userID, String vmID) throws IOException {
		JSONObject jobj = new JSONObject();
		String error = "";

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
	public void stopVM(String userID, String vmID) throws IOException {
		JSONObject jobj = new JSONObject();
		String error = "";

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
					jobj.put("success", "VM succesfully stopped!");

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

	public void editVM(String userID, String vmID) {

	}

	@SuppressWarnings("unchecked")
	public void deleteVM(String userID, String vmID) throws IOException {

		JSONObject jobj = new JSONObject();
		String error = "";

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
}
