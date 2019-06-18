package swarm.core.services;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import swarm.core.domain.Breakpoint;
import swarm.core.domain.Session;
import swarm.core.domain.Type;
import swarm.core.server.ElasticServer;
import swarm.core.server.SwarmServer;

public class BreakpointService {

	public static TypeService typeService;
	
	public static void create(final Breakpoint breakpoint) throws Exception {
		SwarmServer server = SwarmServer.getInstance();
		
		String json = getJson(breakpoint).toString();
		String response = server.create(SwarmServer.BREAKPOINTS, json);
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonObject()) {
			int id = element.getAsJsonObject().get("id").getAsInt();
			breakpoint.setId(id);

			//ElasticServer.createBreakpoint(breakpoint);
		}
	}
	
	public static Breakpoint get(int id) {
		SwarmServer server = SwarmServer.getInstance();
		
		String response;
		try {
			response = server.get(SwarmServer.BREAKPOINTS + "/"+ id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonObject() && element.getAsJsonObject().get("id") != null) {
			Breakpoint breakpoint = new Breakpoint();
			populate(element, breakpoint);
			return breakpoint;
		} else {
			return null;
		}
	}
	
	private static void populate(JsonElement element, Breakpoint breakpoint) {
		breakpoint.setId(element.getAsJsonObject().get("id").getAsInt());
		breakpoint.setCharEnd(element.getAsJsonObject().get("charEnd").getAsInt());
		breakpoint.setCharStart(element.getAsJsonObject().get("charStart").getAsInt());
		//breakpoint.setCode(element.getAsJsonObject().get("code").getAsString());
		breakpoint.setLineNumber(element.getAsJsonObject().get("lineNumber").getAsInt());
		
		String typeUrl = element.getAsJsonObject().get("_links").getAsJsonObject().getAsJsonObject("type").get("href").getAsString();
		
		SwarmServer server = SwarmServer.getInstance();
		
		String response;
		try {
			response = server.get(typeUrl, true);
			JsonParser parser = new JsonParser();
			JsonElement elementType = parser.parse(response);
			
			int typeId = elementType.getAsJsonObject().get("id").getAsInt();   
			Type type = TypeService.get(typeId);	
			
			breakpoint.setType(type);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		//TODO Populate Session
	}
	
	public static JsonObject getJson(Breakpoint breakpoint) {
		
		JsonObject data = new JsonObject();
		data.addProperty("id", Integer.toString(breakpoint.getId()));
		data.addProperty("lineNumber", breakpoint.getLineNumber());
		data.addProperty("charStart", breakpoint.getCharStart());
		data.addProperty("charEnd", breakpoint.getCharEnd());
		data.add("type", typeService.getJson(breakpoint.getType()));

		return data;
		
	}
	
}
