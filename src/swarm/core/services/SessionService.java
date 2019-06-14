package swarm.core.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IJavaProject;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;

import swarm.core.domain.Breakpoint;
import swarm.core.domain.Developer;
import swarm.core.domain.Event;
import swarm.core.domain.Method;
import swarm.core.domain.Project;
import swarm.core.domain.Session;
import swarm.core.domain.Task;
import swarm.core.domain.Type;
import swarm.core.server.SwarmServer;
import swarm.core.util.WorkbenchUtil;

public class SessionService {

	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static DeveloperService developerService;
	public static TaskService taskService;

	public static void create(Session session) throws Exception {
		SwarmServer server = SwarmServer.getInstance();
		
		String json = getJson(session).toString();
		String response = server.create(SwarmServer.SESSIONS, json);
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonObject()) {
			int id = element.getAsJsonObject().get("id").getAsInt();
			session.setId(id);
		}
	}
	
	
	public static List<Session> getSessions(Task task, Developer developer) {
		List<Session> sessions = new ArrayList<Session>();
		
		SwarmServer server = SwarmServer.getInstance();

		String response;
		try {
			response = server.get("sessions/find?taskId=" +task.getId()+"&developerId="+developer.getId());
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
				session.setTask(task);
				session.setDeveloper(developer);
				SessionService.populate(sessionElement, session);
				sessions.add(session);
			}
		}
		
 		return sessions;
	}
	
	public static List<Session> getSessions(Task task) {
		List<Session> sessions = new ArrayList<Session>();
		
		SwarmServer server = SwarmServer.getInstance();

		String response;
		try {
			response = server.get("sessions/find?taskId=" +task.getId());
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
				session.setTask(task);
				
				SessionService.populate(sessionElement, session);
				sessions.add(session);
			}
		}
		
 		return sessions;
	}

	
	
	public static List<Session> getAll() {
		List<Session> sessions = new ArrayList<Session>();
		
		SwarmServer server = SwarmServer.getInstance();

		String response;
		try {
			response = server.get("sessions/all");
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
		session.setStarted(now);
		DateFormat df = new SimpleDateFormat(DATE_FORMAT);
		
		data.put("project", session.getProject());
		data.put("developer", session.getDeveloper().getURI());
		data.put("purpose", session.getPurpose());
		data.put("description", session.getDescription());
		data.put("label", session.getLabel());		
		data.put("started", df.format(now));
		
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
		
		Date now = Calendar.getInstance().getTime();
//		DateFormat df = new SimpleDateFormat(DATE_FORMAT);
		session.setFinished(now);
	
		
		String json = getJson(session).toString();
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
		
		if(element.getAsJsonObject().has("description") && !element.getAsJsonObject().get("description").isJsonNull()) {
			session.setDescription(element.getAsJsonObject().get("description").getAsString());
		}

		if(element.getAsJsonObject().has("purpose") && !element.getAsJsonObject().get("purpose").isJsonNull()) {
			session.setPurpose(element.getAsJsonObject().get("purpose").getAsString());
		}
		
		try {
			DateFormat format = new SimpleDateFormat(DATE_FORMAT);

			if(element.getAsJsonObject().has("started") && !element.getAsJsonObject().get("started").isJsonNull()) {
				String string = element.getAsJsonObject().get("started").getAsString();
     			Date date = format.parse(string);
				session.setStarted(date);
			}

			if(element.getAsJsonObject().has("finished") && !element.getAsJsonObject().get("finished").isJsonNull()) {
				String string = element.getAsJsonObject().get("finished").getAsString();
				Date date = format.parse(string);
				session.setFinished(date);
			}
			
			if(session.getProject() == null && !element.getAsJsonObject().get("project").isJsonNull()) {
				String projectName = element.getAsJsonObject().get("project").getAsString();
				Project p = new Project();
				p.setName(projectName);
				IJavaProject javaProject = WorkbenchUtil.getProjectByName(projectName);
				p.setJavaProject(javaProject);
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
			response = server.get("types/getBySessionId?sessionId=" + session.getId());
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
			response = server.get("sessions/startingMethods?sessionId=" + session.getId());
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
			response = server.get("sessions/endingMethods?sessionId=" + session.getId());
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
	
	public static JsonObject getJson(Session session) {
		
		JsonObject data = new JsonObject();
		data.addProperty("id", Integer.toString(session.getId()));
		data.addProperty("description", session.getDescription());
		//if(session.getFinished() != null)
		//data.addProperty("started", session.getStarted().toString());
		if(session.getFinished() != null)
			data.addProperty("finished", session.getFinished().getTime());
		data.addProperty("label", session.getLabel());
		data.addProperty("project", session.getProject().getName());
		data.addProperty("purpose", session.getPurpose());
		data.add("developer", developerService.getJson(session.getDeveloper()));
		data.add("task", taskService.getJson(session.getTask()));
		
		return data;
		
	}
}