package swarm.core.services;

import java.util.HashMap;
import java.util.Map;

import swarm.core.domain.Namespace;
import swarm.core.domain.Type;
import swarm.core.server.SwarmServer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class NamespaceService {

	public static Namespace getNamespaceByFullPath(String namespacePath) {
		SwarmServer server = SwarmServer.getInstance();

		String response;
		try {
			response = server.get("namespaces/findByFullPath?fullPath=" + namespacePath);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonObject() && element.getAsJsonObject().get("id") != null) {
			Namespace namespace = new Namespace();
			populate(element, namespace);
			return namespace;
		} else {
			return null;
		}
		
	}

	private static void populate(JsonElement element, Namespace namespace) {
		namespace.setId(element.getAsJsonObject().get("id").getAsInt());
		namespace.setFullPath(element.getAsJsonObject().get("fullPath").getAsString());
		namespace.setName(element.getAsJsonObject().get("name").getAsString());
	}

	public static void create(Namespace namespace) throws Exception {
		SwarmServer server = SwarmServer.getInstance();
		
		String json = getJson(namespace).toString();
		String response = server.create(SwarmServer.NAMESPACES, json);
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonObject()) {
			int id = element.getAsJsonObject().get("id").getAsInt();
			namespace.setId(id);

		}
	}

	public static Namespace get(long idNamespace) {
		SwarmServer server = SwarmServer.getInstance();
		
		String response;
		try {
			response = server.get(SwarmServer.NAMESPACES + "/"+ idNamespace);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonObject() && element.getAsJsonObject().get("id") != null) {
			Namespace namespace = new Namespace();
			populate(element, namespace);
			return namespace;
		} else {
			return null;
		}
	}
	
	public static JsonObject getJson(Namespace namespace) {
		
		JsonObject data = new JsonObject();
		data.addProperty("id", namespace.getId());
		data.addProperty("name", namespace.getName());
		data.addProperty("fullPath", namespace.getFullPath());

		return data;
		
	}
	
}
