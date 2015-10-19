package swarm.core.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import swarm.core.domain.Invocation;
import swarm.core.domain.Method;
import swarm.core.domain.Session;
import swarm.core.server.ElasticServer;
import swarm.core.server.Neo4JServer;
import swarm.core.server.SwarmServer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class InvocationService {

	public static void create(Invocation invocation) throws Exception {
		SwarmServer server = SwarmServer.getInstance();
		
		Map<String, Object> data = new HashMap<>();
		data.put("invoked", invocation.getInvoked().getURI());
		data.put("invoking", invocation.getInvoking().getURI());
		data.put("session", invocation.getSession().getURI());
		data.put("event", invocation.getEvent().getURI());

		String json = JSON.build(data);
		String response = server.create(SwarmServer.INVOCATIONS, json);
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonObject()) {
			int id = element.getAsJsonObject().get("id").getAsInt();
			invocation.setId(id);
			
			ElasticServer.createInvocation(invocation);
			
			if(!Neo4JServer.containsInvocation(invocation)) {
				Neo4JServer.createInvocation(invocation);
			}
		}		
	}
	
	public static List<Invocation> getInvocationsByMethods(Session session, Method invoking, Method invoked) {
		List<Invocation> invocations = new ArrayList<Invocation>();
 		SwarmServer server = SwarmServer.getInstance();
		
		String response;
		try {
			response = server.get("invocations/getInvocationsByMethods?sessionId="+session.getId()+"&invokingId=" + invoking.getId() + "&invokedId="+invoked.getId());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);
		
		if (element.isJsonArray()) {
			JsonArray array = element.getAsJsonArray();
			for (JsonElement invocationElement : array) {
				if (invocationElement.isJsonObject() && invocationElement.getAsJsonObject().get("id") != null) {
					Invocation invocation = new Invocation();
					populate(invocationElement, invocation);
					invocations.add(invocation);
				}
			}
		}
		
		return invocations;
	}

	private static void populate(JsonElement element, Invocation invocation) {
		invocation.setId(element.getAsJsonObject().get("id").getAsInt());
		//TODO populate methods and Session and Event
	}

	public static boolean contains(Invocation invocation) {
		//TODO Create a specific query for counting invocations. 
		List<Invocation> list = getInvocationsByMethods(invocation.getSession(), invocation.getInvoking(),invocation.getInvoked()); 
		return list.size() > 0;
	}
}
