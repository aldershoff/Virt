package processUserServices;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jsonserializers.BuyCustomerVMSerialiser;
import libvirtAccessObject.TestVM;

import org.apache.commons.lang.RandomStringUtils;
import org.json.simple.JSONObject;
import org.libvirt.LibvirtException;

import beans.VMBean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import databaseAccessObjects.BuyDAO;

public class ProcessUserBuyService {

	
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	public ProcessUserBuyService(HttpServletRequest request, HttpServletResponse response){
		this.request = request;
		this.response = response;
	}
	
	
	
	/**
	 * Method for adding new VM for user
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws LibvirtException 
	 * @throws NumberFormatException 
	 */
	@SuppressWarnings("unchecked")
	public void buyUserVM() throws IOException, NumberFormatException {

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
		
	
		UUID makeVMLibvirtResult = null;
		try {
			makeVMLibvirtResult = TestVM.createVM(vmBean.getVMOS(), vmBean.getVMName(), Integer.parseInt(vmBean.getVMMemory()), Integer.parseInt(vmBean.getVMDiskSpace()), Integer.parseInt(vmBean.getVMCPU()));
		} catch (LibvirtException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JSONObject jobj = new JSONObject();
		String error = "";
		String json = "";
		
		if(makeVMLibvirtResult != null){
			// Set the bean and fill it
			vmBean = buyDAO.addVM(vmBean, userID, makeVMLibvirtResult);

			Gson gson = new Gson();
			

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
		else{
			response.setContentType("application/json");
			jobj.put("error", "LIBVIRT ERROR");
			response.getWriter().write(jobj.toString());
		}
		

	}
	
}
