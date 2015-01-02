package jsonserializers;

import java.lang.reflect.Type;

import beans.CustomerBean;

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
public class GetUserDetailsSerialiser implements JsonSerializer<CustomerBean> {

	@Override
	public JsonElement serialize(CustomerBean customer, Type typeOfSrc,
			JsonSerializationContext context) {

		/**
		 * Set the properties and the customer bean members that will be needed
		 * to serialise to JSON and return it
		 */
		final JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("userID", customer.getUserID());
		jsonObject.addProperty("username", customer.getUsername());
		jsonObject.addProperty("firstName", customer.getFirstName());
		jsonObject.addProperty("lastName", customer.getLastName());
		jsonObject.addProperty("company", customer.getCompany());
		jsonObject.addProperty("email", customer.getEmail());		
		jsonObject.addProperty("phone", customer.getPhone());
		jsonObject.addProperty("address", customer.getAddress());
		jsonObject.addProperty("zipcode", customer.getZipCode());
		jsonObject.addProperty("twoFactor", customer.isTwoFactor());
		jsonObject.addProperty("valid", customer.isValid());

		return jsonObject;
	}

}