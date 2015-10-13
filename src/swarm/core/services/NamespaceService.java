package swarm.core.services;

import java.util.HashMap;
import java.util.Map;

import swarm.core.domain.Namespace;
import swarm.core.server.SwarmServer;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class NamespaceService {

	public static Namespace getNamespaceByFullPath(String namespacePath) {
		SwarmServer server = SwarmServer.getInstance();

		String response;
		try {
			response = server.get("namespace/byFullPath?path=" + namespacePath);
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
		
		Map<String, Object> data = new HashMap<>();
		data.put("name", namespace.getName());
		data.put("fullPath", namespace.getFullPath());

		String json = JSON.build(data);
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
}
