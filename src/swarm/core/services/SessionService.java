package swarm.core.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import swarm.core.domain.Breakpoint;
import swarm.core.domain.Developer;
import swarm.core.domain.Event;
import swarm.core.domain.Method;
import swarm.core.domain.Project;
import swarm.core.domain.Session;
import swarm.core.domain.Type;
import swarm.core.server.SwarmServer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class SessionService {

	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	public static void create(Session session) throws Exception {
		SwarmServer server = SwarmServer.getInstance();
		
		Map<String, Object> data = new HashMap<>();
		data.put("project", session.getProject().getId());
		data.put("developer", session.getDeveloper().getId());
		data.put("purpose", session.getPurpose());
		data.put("description", session.getDescription());
		data.put("label", session.getLabel());
		
		String json = JSON.build(data);
		String response = server.create(SwarmServer.SESSIONS, json);
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonObject()) {
			int id = element.getAsJsonObject().get("id").getAsInt();
			session.setId(id);
		}
	}
	
	
	public static List<Session> getSessions(Project project, Developer developer) {
		List<Session> sessions = new ArrayList<Session>();
		
		SwarmServer server = SwarmServer.getInstance();

		String response;
		try {
			response = server.get("session/sessions?idProject=" + project.getId()+"&idDeveloper="+developer.getId());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonArray()) {
			JsonArray array = element.getAsJsonArray();
			for (JsonElement sessionElement : array) {
				Session session = new Session();
				session.setProject(project);
				session.setDeveloper(developer);
				SessionService.populate(sessionElement, session);
				sessions.add(session);
			}
		}
		
 		return sessions;
	}
	
	public static void start(Session session) throws Exception {
		SwarmServer server = SwarmServer.getInstance();
		
		Map<String, Object> data = new HashMap<>();
		Date now = Calendar.getInstance().getTime();
		
		DateFormat df = new SimpleDateFormat(DATE_FORMAT);
		String date = df.format(now);
		data.put("started", date);
		
		String json = JSON.build(data);
		String response = server.update(SwarmServer.SESSIONS + "/" + session.getId(), json);
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonObject()) {
			int id = element.getAsJsonObject().get("id").getAsInt();
			if(id == session.getId()) {
				session.setStarted(now);
			}
		}		
	}
	
	public static void finish(Session session) throws Exception {
		SwarmServer server = SwarmServer.getInstance();
		
		Map<String, Object> data = new HashMap<>();
		Date now = Calendar.getInstance().getTime();
		
		DateFormat df = new SimpleDateFormat(DATE_FORMAT);
		String date = df.format(now);
		data.put("finished", date);
		
		String json = JSON.build(data);
		String response = server.update(SwarmServer.SESSIONS + "/" + session.getId(), json);
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonObject()) {
			int id = element.getAsJsonObject().get("id").getAsInt();
			if(id == session.getId()) {
				session.setFinished(now);
			}
		}		
	}

	public static void populate(JsonElement element, Session session) {
		session.setId(element.getAsJsonObject().get("id").getAsInt());
		session.setLabel(element.getAsJsonObject().get("label").getAsString());
		
		if(!element.getAsJsonObject().get("description").isJsonNull()) {
			session.setDescription(element.getAsJsonObject().get("description").getAsString());
		}

		if(!element.getAsJsonObject().get("purpose").isJsonNull()) {
			session.setPurpose(element.getAsJsonObject().get("purpose").getAsString());
		}
		
		try {
			DateFormat format = new SimpleDateFormat(DATE_FORMAT);

			if(!element.getAsJsonObject().get("started").isJsonNull()) {
				String string = element.getAsJsonObject().get("started").getAsString();
     			Date date = format.parse(string);
				session.setStarted(date);
			}

			if(!element.getAsJsonObject().get("finished").isJsonNull()) {
				String string = element.getAsJsonObject().get("finished").getAsString();
				Date date = format.parse(string);
				session.setFinished(date);
			}
			
			if(session.getProject() == null && !element.getAsJsonObject().get("project").isJsonNull()) {
				int id = element.getAsJsonObject().get("project").getAsInt();
				Project p = ProjectService.get(id);
				session.setProject(p);
			}

			if(session.getDeveloper() == null && !element.getAsJsonObject().get("developer").isJsonNull()) {
				int id = element.getAsJsonObject().get("developer").getAsInt();
				Developer d = DeveloperService.get(id);
				session.setDeveloper(d);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<Type> getTypes(Session session) {
		List<Type> types = new ArrayList<Type>();
		SwarmServer server = SwarmServer.getInstance();

		String response;
		try {
			response = server.get("session/types?idSession=" + session.getId());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonArray()) {
			JsonArray jsonTypes = (JsonArray) element.getAsJsonArray();
			for (JsonElement typeElement : jsonTypes) {
				Type type = new Type();
				TypeService.populate(typeElement, type, session);
				types.add(type);
			}
			return types;
		} else {
	 		return null;
		}
	}	
	
	public static List<Method> getStartingMethods(Session session) {
		List<Method> methods = new ArrayList<Method>();
		SwarmServer server = SwarmServer.getInstance();

		String response;
		try {
			response = server.get("session/startingMethods?sessionId=" + session.getId());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonArray()) {
			JsonArray jsonTypes = (JsonArray) element.getAsJsonArray();
			for (JsonElement typeElement : jsonTypes) {
				Method method = new Method();
				MethodService.populate(typeElement, method);
				methods.add(method);
			}
			return methods;
		} else {
			return null;
		}
	}
	
	public static List<Method> getEndingMethods(Session session) {
		List<Method> methods = new ArrayList<Method>();
		SwarmServer server = SwarmServer.getInstance();

		String response;
		try {
			response = server.get("session/endingMethods?sessionId=" + session.getId());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonArray()) {
			JsonArray jsonTypes = (JsonArray) element.getAsJsonArray();
			for (JsonElement typeElement : jsonTypes) {
				Method method = new Method();
				MethodService.populate(typeElement, method);
				methods.add(method);
			}
			return methods;
		} else {
			return null;
		}
	}


	public static List<Breakpoint> getBreakpoints(Session session) {
		// TODO Auto-generated method stub
		return null;
	}


	public static List<Event> getEvents(Session session) {
		// TODO Auto-generated method stub
		return null;
	}
}