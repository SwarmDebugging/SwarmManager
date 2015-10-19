package swarm.core.services;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import swarm.core.domain.Event;
import swarm.core.server.ElasticServer;
import swarm.core.server.SwarmServer;

public class EventService {

	public static void create(final Event event) throws Exception {
		SwarmServer server = SwarmServer.getInstance();

		Map<String, Object> data = new HashMap<>();
		data.put("session", event.getSession().getURI());
		data.put("method", event.getMethod().getURI());
		data.put("detail", event.getDetail());
		data.put("kind", event.getKind());
		data.put("charEnd", event.getCharEnd());
		data.put("charStart", event.getCharStart());
		data.put("lineNumber", event.getLineNumber());

		String json = JSON.build(data);
		String response = server.create(SwarmServer.EVENTS, json);
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonObject()) {
			int id = element.getAsJsonObject().get("id").getAsInt();
			event.setId(id);
		}

		ElasticServer.createEvent(event);
	}
}
