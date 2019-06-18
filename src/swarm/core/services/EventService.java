package swarm.core.services;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import swarm.core.domain.Event;
import swarm.core.domain.Invocation;
import swarm.core.server.ElasticServer;
import swarm.core.server.SwarmServer;

public class EventService {

	public static MethodService methodService;
	public static SessionService sessionService;
	
	public static void create(final Event event) throws Exception {
		SwarmServer server = SwarmServer.getInstance();
		
		String json = getJson(event).toString();
		String response = server.create(SwarmServer.EVENTS, json);
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonObject()) {
			int id = element.getAsJsonObject().get("id").getAsInt();
			event.setId(id);
		}

		//ElasticServer.createEvent(event);
	}
	
	public static JsonObject getJson(Event event) {
		
		JsonObject data = new JsonObject();
		data.addProperty("id", event.getId());
		data.addProperty("detail", event.getDetail());
		data.addProperty("kind", event.getKind());
		data.addProperty("charEnd", event.getCharEnd());
		data.addProperty("charStart", event.getCharStart());
		data.addProperty("lineNumber", event.getLineNumber());
		data.add("method", methodService.getJson(event.getMethod()));
		data.add("session", sessionService.getJson(event.getSession()));
		
		return data;
		
	}
	
}
