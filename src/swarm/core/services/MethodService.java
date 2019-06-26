package swarm.core.services;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import swarm.core.domain.Method;
import swarm.core.domain.Namespace;
import swarm.core.domain.Task;
import swarm.core.domain.Type;
import swarm.core.server.ElasticServer;
import swarm.core.server.Neo4JServer;
import swarm.core.server.SwarmServer;

public class MethodService {
	
	public static TypeService typeService;

	public static void create(final Method method) throws Exception {
		SwarmServer server = SwarmServer.getInstance();
		
		String json = getJson(method).toString();
		String response = server.create(SwarmServer.METHODS, json);
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonObject()) {
			int id = element.getAsJsonObject().get("id").getAsInt();
			method.setId(id);

			ElasticServer.createMethod(method);
			Neo4JServer.createMethod(method);
		}
	}

	public static void populate(JsonElement element, Method method) {
		int methodId = element.getAsJsonObject().get("id").getAsInt();

		String response = "";
		try {
			response = SwarmServer.getInstance().get(SwarmServer.METHODS + "/" + methodId + "/type");
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		JsonParser parser = new JsonParser();
		JsonElement typeElement = parser.parse(response);
		
		method.setId(methodId);
		method.setName(element.getAsJsonObject().get("name").getAsString());
		method.setSignature(element.getAsJsonObject().get("signature").getAsString());
		method.setKey(element.getAsJsonObject().get("key").getAsString());
		
		if(method.getType() == null && !element.getAsJsonObject().get("type").isJsonNull()) {
			JsonElement e = element.getAsJsonObject().get("type");
			int type_id = e.getAsJsonObject().get("id").getAsInt();
			Type type = TypeService.get(type_id);
			method.setType(type);
		}		
		
	}
	
	
	public static Method get(long id) {
		SwarmServer server = SwarmServer.getInstance();
		
		String response;
		try {
			response = server.get(SwarmServer.METHODS + "/"+ id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonObject() && element.getAsJsonObject().get("id") != null) {
			Method method = new Method();
			populate(element, method);
			return method;
		} else {
			return null;
		}
	}
	
	public static JsonObject getJson(Method method) {
		
		JsonObject data = new JsonObject();
		data.addProperty("id", method.getId());
		data.addProperty("name", method.getName());
		data.addProperty("signature", method.getSignature());
		data.addProperty("key", method.getKey());
		data.add("type", typeService.getJson(method.getType()));
		
		return data;
		
	}
	
}
