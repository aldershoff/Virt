package jsonserializers;

import java.lang.reflect.Type;

import beans.CustomerBean;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class CustomerSerialiser implements JsonSerializer<CustomerBean> {



	@Override
	public JsonElement serialize(CustomerBean customer, Type typeOfSrc,
			JsonSerializationContext context) {
		final JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("userID", customer.getUserID());
		jsonObject.addProperty("username", customer.getUsername());
		jsonObject.addProperty("valid", customer.isValid());

		return jsonObject;
	}


}