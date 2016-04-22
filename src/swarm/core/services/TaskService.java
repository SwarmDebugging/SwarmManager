package swarm.core.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import swarm.core.domain.Developer;
import swarm.core.domain.Method;
import swarm.core.domain.Session;
import swarm.core.domain.Task;
import swarm.core.server.SwarmServer;

public class TaskService {

	public static void create(final Task task) throws Exception {
		SwarmServer server = SwarmServer.getInstance();

		Map<String, Object> data = new HashMap<>();
		data.put("title", task.getTitle());
		data.put("url", task.getUrl());

		String json = JSON.build(data);
		String response = server.create(SwarmServer.METHODS, json);
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonObject()) {
			int id = element.getAsJsonObject().get("id").getAsInt();
			task.setId(id);

			// ElasticServer.createTask(task);
			// Neo4JServer.createTask(method); 	
		}
	}

	public static void populate(JsonElement element, Task task) {
		task.setId(element.getAsJsonObject().get("id").getAsInt());
		task.setTitle(element.getAsJsonObject().get("title").getAsString());
		task.setUrl(element.getAsJsonObject().get("url").getAsString());
	}

	public static List<Task> getAll() {
		List<Task> tasks = new ArrayList<Task>();
		SwarmServer server = SwarmServer.getInstance();

		String response;
		try {
			response = server.get(SwarmServer.TASKS + "/all");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonArray()) {
			JsonArray jsonTypes = (JsonArray) element.getAsJsonArray();
			for (JsonElement typeElement : jsonTypes) {
				Task task = new Task();
				TaskService.populate(typeElement, task);
				tasks.add(task);
			}
		} else {
			return null;
		}

		return tasks;
	}


	public static List<Method> getStartingMethods(Task task) {
		List<Method> methods = new ArrayList<Method>();

		//TODO Optimize this method
		List<Developer> developers = DeveloperService.getDevelopers();
		for (Developer developer : developers) {
			List<Session> sessions = SessionService.getSessions(task, developer);
			for (Session session : sessions) {
				List<Method> ending = session.getStartingMethods();
				for (Method method : ending) {
					if (!methods.contains(method)) {
						methods.add(method);
					}
				}
			}
		}

		return methods;
	}	

	public static List<Method> getEndingMethods(Task task) {
		List<Method> methods = new ArrayList<Method>();

		//TODO Optimize this method		
		List<Developer> developers = DeveloperService.getDevelopers();
		for (Developer developer : developers) {
			List<Session> sessions = SessionService.getSessions(task, developer);
			for (Session session : sessions) {
				List<Method> ending = session.getEndingMethods();
				for (Method method : ending) {
					if (!methods.contains(method)) {
						methods.add(method);
					}
				}
			}
		}

		return methods;
	}
}