package jsonserializers;

import java.lang.reflect.Type;

import beans.VMBean;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Serialiser for adding the data needed to send from the Bean If this is not
 * done this way, all the information, maybe not needed information, would be
 * send back with JSON
 * 
 * @author KjellZijlemaker
 *
 */
public class BuyCustomerVMSerialiser implements JsonSerializer<VMBean> {

	@Override
	public JsonElement serialize(VMBean vmBean, Type typeOfSrc,
			JsonSerializationContext context) {

		/**
		 * Set the properties and the customer bean members that will be needed
		 * to serialise to JSON and return it
		 */
		final JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("valid", vmBean.isValid());
		jsonObject.addProperty("vmIPIsActive", vmBean.getVmIPIsActive());

		return jsonObject;
	}
}