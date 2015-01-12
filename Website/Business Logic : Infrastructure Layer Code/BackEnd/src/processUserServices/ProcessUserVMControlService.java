package processUserServices;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jsonserializers.GetUserVMSerialiser;
import libvirtAccessObject.VMmanagementTest;

import org.json.simple.JSONObject;
import org.libvirt.LibvirtException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import beans.VMBean;
import databaseAccessObjects.VMDAO;

public class ProcessUserVMControlService {

	private HttpServletRequest request;
	private HttpServletResponse response;

	public ProcessUserVMControlService(HttpServletRequest request,
			HttpServletResponse response) {
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

		UUID UUID = java.util.UUID.fromString(startVM.getVMUUID(vmID));
		try {
			/**
			 * Function for also making connection with libvirt API
			 */
			VMmanagementTest vmManagement = new VMmanagementTest();
			int resultLibVirt = 0;

			resultLibVirt = vmManagement.startVM(UUID);
			int result = 0;

			if (resultLibVirt != -1) {
				if (resultLibVirt == 1) {
					result = startVM.startSpecificVMState(vmID, userID, 1);
				} else if (resultLibVirt == 0) {
					result = startVM.startSpecificVMState(vmID, userID, 0);
				}

				if (result != 2) {

					/**
					 * If the bean is valid, it will return a new JSON response
					 */
					if (result == 1) {
						if (resultLibVirt == 1) {
							jobj.put("success", "VM succesfully started!");
						} else {
							jobj.put("error",
									"VM is still started.. Please contact support");
						}

					} else {
						error = "Could not start VM...";
						jobj.put("error", error);
					}
				} else {
					error = "Can't connect with database";
					jobj.put("error", error);

				}

			} else {
				error = "Can't connect with libVirt!";
				jobj.put("error", error);
			}

		} catch (LibvirtException e) {
			error = e.getError().getMessage().toString();
			jobj.put("error", error);
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
		// VMmanagementTest vmManagement = new VMmanagementTest();
		//
		// int resultLibVirt = vmManagement.deleteVM("VMUUID");
		//
		// if(resultLibVirt != 2){
		// if(resultLibVirt == 1){
		// int result = deleteVM.deleteSpecificVM(vmID, userID);
		// try {
		// if (result != 2) {
		//
		// /**
		// * If the bean is valid, it will return a new JSON response
		// */
		// if (result == 1) {
		// jobj.put("success", "VM succesfully deleted!");
		//
		// } else {
		// error = "Could not delete VM...";
		// jobj.put("error", error);
		// }
		// } else {
		// error = "Can't connect with database";
		// jobj.put("error", error);
		//
		// }
		//
		// } finally {
		// response.setContentType("application/json");
		// response.getWriter().write(jobj.toString());
		//
		// }
		// }
		// }
		// else{
		// error = "Can't connect with libVirt!";
		// jobj.put("error", error);
		// }

		// int result = editVM.editSpecificVM(userID, editVMBean);
		//
		// try {
		// if (result != 2) {
		//
		// /**
		// * If the bean is valid, it will return a new JSON response
		// */
		// if (result == 1) {
		// final GsonBuilder gsonBuilder = new GsonBuilder();
		// gsonBuilder.registerTypeAdapter(VMBean.class,
		// new GetUserVMSerialiser());
		// Gson gson = gsonBuilder.create();
		// json = gson.toJson(editVMBean);
		//
		// } else {
		// error = "Could not edit VM...";
		// jobj.put("error", error);
		// }
		// } else {
		// error = "Can't connect with database";
		// jobj.put("error", error);
		//
		// }
		//
		// } finally {
		// response.setContentType("application/json");
		// if (error != "") {
		// response.getWriter().write(jobj.toString());
		// } else {
		// response.getWriter().write(json.toString());
		// }
		// }

		UUID UUID = java.util.UUID.fromString(editVM.getVMUUID(Long
				.toString(editVMBean.getVMID())));

		try {
			if (UUID != null) {

				/**
				 * Function for also making connection with libvirt API
				 */
				VMmanagementTest vmManagement = new VMmanagementTest();

				int resultLibVirt = 0;
				resultLibVirt = vmManagement.editVM(UUID,
						Integer.parseInt(editVMBean.getVMMemory()),
						Integer.parseInt(editVMBean.getVMCPU()));
				int result = 0;

				if (resultLibVirt != -1) {

					if (resultLibVirt == 1 || resultLibVirt == 0) {
						result = editVM.editSpecificVM(userID, editVMBean);
					}

					if (result != 2) {

						/**
						 * If the bean is valid, it will return a new JSON
						 * response
						 */
						if (result == 1) {
							if (resultLibVirt == 1) {
								jobj.put("success",
										"Successfully edited the VM!");
								

							} else {
								jobj.put("error",
										"Your VM has not been edited, please contact support");
							}

						} else {
							error = "Could not edit VM";
							jobj.put("error", error);
						}
					} else {
						error = "Can't connect with database";
						jobj.put("error", error);

					}

				} else {
					error = "Something went wrong within libvirt";
					jobj.put("error", error);
				}
			} else {
				error = "Could not add UUID";
				jobj.put("error", error);
			}
		} catch (LibvirtException e) {
			error = e.getError().getMessage().toString();
			jobj.put("error", error);
		} finally {
				final GsonBuilder gsonBuilder = new GsonBuilder();
				gsonBuilder.registerTypeAdapter(VMBean.class,
						new GetUserVMSerialiser());
				Gson gson = gsonBuilder.create();
				json = gson.toJson(editVMBean);
				response.getWriter().write(json.toString());
			

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

		UUID UUID = java.util.UUID.fromString(stopVM.getVMUUID(vmID));

		try {
			if (UUID != null) {

				/**
				 * Function for also making connection with libvirt API
				 */
				VMmanagementTest vmManagement = new VMmanagementTest();

				int resultLibVirt = 0;
				resultLibVirt = vmManagement.stopVM(UUID);
				int result = 0;

				if (resultLibVirt != -1) {

					if (resultLibVirt == 1) {
						result = stopVM.stopSpecificVM(vmID, userID, 1);
					} else if (resultLibVirt == 0) {
						result = stopVM.stopSpecificVM(vmID, userID, 0);
					}

					if (result != 2) {

						/**
						 * If the bean is valid, it will return a new JSON
						 * response
						 */
						if (result == 1) {
							if (resultLibVirt == 0) {
								jobj.put("success", "Your VM is stopped");
							} else {
								jobj.put("error",
										"Your VM is still not stopped.. Please contact support");
							}

						} else {
							error = "Could not stop VM...";
							jobj.put("error", error);
						}
					} else {
						error = "Can't connect with database";
						jobj.put("error", error);

					}

				} else {
					error = "Something went wrong within libvirt";
					jobj.put("error", error);
				}
			} else {
				error = "Could not add UUID";
				jobj.put("error", error);
			}
		} catch (LibvirtException e) {
			error = e.getError().getMessage().toString();
			jobj.put("error", error);
		} finally {
			response.setContentType("application/json");
			response.getWriter().write(jobj.toString());

		}
	}

	@SuppressWarnings("unchecked")
	public void refreshVMState() throws IOException {
	
		JSONObject jobj = new JSONObject();
		String error = "";
	
		String vmID = request.getParameter("vmID");
	
		/**
		 * Making new DAO and get the results
		 */
		VMDAO refreshVMState = new VMDAO();
	
		UUID UUID = java.util.UUID.fromString(refreshVMState.getVMUUID(vmID));
	
		try {
			if (UUID != null) {
	
				/**
				 * Function for also making connection with libvirt API
				 */
				VMmanagementTest vmManagement = new VMmanagementTest();
	
				int resultLibVirt = 0;
				resultLibVirt = vmManagement.checkState(UUID);
				int result = 0;
	
				if (resultLibVirt != -1) {
	
					if (resultLibVirt == 1) {
						result = refreshVMState.refreshSpecificVM(vmID, 1);
					} else if (resultLibVirt == 0) {
						result = refreshVMState.refreshSpecificVM(vmID, 0);
					}
	
					if (result != 2) {
	
						/**
						 * If the bean is valid, it will return a new JSON
						 * response
						 */
						if (result == 1) {
							if (resultLibVirt == 1) {
								jobj.put("success", "Your VM is running");
							} else {
								jobj.put("success", "Your VM is not running");
							}
	
						} else {
							jobj.put("error",
									"Could not connect to the database");
						}
					} else {
						error = "Something went wrong with the database";
						jobj.put("error", error);
	
					}
	
				} else {
					error = "Something went wrong within libvirt";
					jobj.put("error", error);
				}
			} else {
				error = "Could not add UUID";
				jobj.put("error", error);
			}
		} catch (LibvirtException e) {
			error = e.getError().getMessage().toString();
			jobj.put("error", error);
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
		// VMmanagementTest vmManagement = new VMmanagementTest();
		//
		// int resultLibVirt = vmManagement.deleteVM("VMUUID");
		//
		// if(resultLibVirt != 2){
		// if(resultLibVirt == 1){
		// int result = deleteVM.deleteSpecificVM(vmID, userID);
		// try {
		// if (result != 2) {
		//
		// /**
		// * If the bean is valid, it will return a new JSON response
		// */
		// if (result == 1) {
		// jobj.put("success", "VM succesfully deleted!");
		//
		// } else {
		// error = "Could not delete VM...";
		// jobj.put("error", error);
		// }
		// } else {
		// error = "Can't connect with database";
		// jobj.put("error", error);
		//
		// }
		//
		// } finally {
		// response.setContentType("application/json");
		// response.getWriter().write(jobj.toString());
		//
		// }
		// }
		// }
		// else{
		// error = "Can't connect with libVirt!";
		// jobj.put("error", error);
		// }

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
	public void refreshVMRealtime() throws IOException {

		JSONObject jobj = new JSONObject();
		String error = "";
		String json ="";

		String vmID = request.getParameter("vmID");
		String userID = request.getParameter("userID");

		/**
		 * Making new DAO and get the results
		 */
		VMDAO refreshRealtimeDAO = new VMDAO();

		UUID UUID = java.util.UUID.fromString(refreshRealtimeDAO
				.getVMUUID(vmID));

		String storageKey = refreshRealtimeDAO.getStorKey(vmID);
		
		try {
			if (UUID != null) {

				/**
				 * Function for also making connection with libvirt API
				 */
				VMmanagementTest vmManagement = new VMmanagementTest();
				String[] storage = vmManagement.getVolume(storageKey);
				
				String storageCapacity;
				String storageAllocation;
				storageCapacity = storage[0];
				storageAllocation = storage[1];
				
				VMBean resultLibVirtBean = new VMBean();
				resultLibVirtBean.setVMDiskSpace(storageCapacity);
				
				//Is storage allocation!!
				resultLibVirtBean.setVMMonthlyPrice(storageAllocation);
				
				// First fill it from the database
				resultLibVirtBean = refreshRealtimeDAO.getSpecificVM(
						resultLibVirtBean, userID, vmID);
				

					/**
					 * If the data has been filled, fill it with libvirt data
					 */
					if (resultLibVirtBean != null) {
						resultLibVirtBean = vmManagement.getLiveData(UUID, resultLibVirtBean);
						
						/**
						 * Setting builder and serialiser for appropiate data
						 */
						final GsonBuilder gsonBuilder = new GsonBuilder();
						gsonBuilder.registerTypeAdapter(VMBean.class,
								new GetUserVMSerialiser());
						Gson gson = gsonBuilder.create();
						json = gson.toJson(resultLibVirtBean);
					}

				 else {
					jobj.put("error", "Could not connect to the database");
				}
			}
			 else {
				error = "Could not add UUID";
				jobj.put("error", error);
			}
		} catch (LibvirtException e) {
			error = e.getError().getMessage().toString();
			jobj.put("error", error);
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
