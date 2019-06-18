package swarm.core.services;

import com.google.gson.JsonObject;

import swarm.core.domain.TypeWrapper;

public class TypeWrapperService {
	
	public static TypeService typeService;
	
	public static JsonObject getJson(TypeWrapper typeWrapper) {
		
		JsonObject data = new JsonObject();
		data.add("type", typeService.getJson(typeWrapper.getType()));
		data.addProperty("source", typeWrapper.getSource());

		return data;
		
	}
	
}
