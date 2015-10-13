package swarm.core.services;

import java.util.HashMap;
import java.util.Map;

import swarm.core.domain.Breakpoint;
import swarm.core.server.ElasticServer;
import swarm.core.server.SwarmServer;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class BreakpointService {

	public static void create(final Breakpoint breakpoint) throws Exception {
		SwarmServer server = SwarmServer.getInstance();

		Map<String, Object> data = new HashMap<>();
		data.put("type", breakpoint.getType().getId());
		data.put("session", breakpoint.getSession().getId());
		data.put("lineNumber", breakpoint.getLineNumber());
		data.put("charStart", breakpoint.getCharStart());
		data.put("charEnd", breakpoint.getCharEnd());

		String json = JSON.build(data);
		String response = server.create(SwarmServer.BREAKPOINTS, json);
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonObject()) {
			int id = element.getAsJsonObject().get("id").getAsInt();
			breakpoint.setId(id);

			ElasticServer.createBreakpoint(breakpoint);
		}
	}
}
