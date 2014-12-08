package jsonserializers;

import java.lang.reflect.Type;

import beans.VMBean;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Class for sending appropiate data for the VM's of the User
 * 
 * @author KjellZijlemaker
 *
 */
public class GetUserVMSerialiser implements JsonSerializer<VMBean> {

	@Override
	public JsonElement serialize(VMBean vmBean, Type typeOfSrc,
			JsonSerializationContext context) {

		/**
		 * Set the properties and the VMBean members that will be needed
		 * to serialise to JSON and return it
		 */
		final JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("vmID", vmBean.getVMID());
		jsonObject.addProperty("vmName", vmBean.getVMName());
		jsonObject.addProperty("vmCPU", vmBean.getVMCPU());
		jsonObject.addProperty("vmMemory", vmBean.getVMMemory());
		jsonObject.addProperty("vmDiskSpace", vmBean.getVMDiskSpace());
		jsonObject.addProperty("vmIP", vmBean.getVMIP());
		jsonObject.addProperty("valid", vmBean.isValid());

		return jsonObject;
	}
}
