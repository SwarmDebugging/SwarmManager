package swarm.core.services;

import java.util.HashMap;
import java.util.Map;

import swarm.core.domain.Method;
import swarm.core.domain.Type;
import swarm.core.server.ElasticServer;
import swarm.core.server.Neo4JServer;
import swarm.core.server.SwarmServer;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class MethodService {

	public static void create(final Method method) throws Exception {
		SwarmServer server = SwarmServer.getInstance();

		Map<String, Object> data = new HashMap<>();
		data.put("type", method.getType().getId());
		data.put("name", method.getName());
		data.put("signature", method.getSignature());
		data.put("key", method.getKey());

		String json = JSON.build(data);
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
		int typeId = element.getAsJsonObject().get("type").getAsJsonObject().get("id").getAsInt();
		Type type = TypeService.get(typeId);
		
		method.setId(element.getAsJsonObject().get("id").getAsInt());
		method.setName(element.getAsJsonObject().get("name").getAsString());
		method.setSignature(element.getAsJsonObject().get("signature").getAsString());
		method.setKey(element.getAsJsonObject().get("key").getAsString());
		
		method.setType(type);
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
}
