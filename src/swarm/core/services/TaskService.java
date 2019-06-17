package swarm.core.services;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import swarm.core.domain.Developer;
import swarm.core.domain.Method;
import swarm.core.domain.Session;
import swarm.core.domain.Task;
import swarm.core.domain.Product;
import swarm.core.server.SwarmServer;

public class TaskService {
	
	public static ProductService productService;

	public static void create(final Task task) throws Exception {
		SwarmServer server = SwarmServer.getInstance();
		
		String json = getJson(task).toString();
		String response = server.create(SwarmServer.TASKS, json);
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
		if(element.getAsJsonObject().has("color") && !element.getAsJsonObject().get("color").isJsonNull()) {
			task.setColor(element.getAsJsonObject().get("color").getAsString());
		}
		task.setTitle(element.getAsJsonObject().get("title").getAsString());
		task.setUrl(element.getAsJsonObject().get("url").getAsString());
		if(task.getProduct() == null && !element.getAsJsonObject().get("product").isJsonNull()) {
			JsonElement e = element.getAsJsonObject().get("product");
			int task_id = e.getAsJsonObject().get("id").getAsInt();
			Product p = ProductService.get(task_id);
			task.setProduct(p);
		}
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
	
	public static Task get(int id) {
		SwarmServer server = SwarmServer.getInstance();

		String response;
		try {
			response = server.get(SwarmServer.TASKS + "/" +id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonObject() && element.getAsJsonObject().get("id") != null) {
			Task t = new Task();
			TaskService.populate(element, t);
			return t;
		} else {
	 		return null;
		}	
	}
	
	public static JsonObject getJson(Task task) {
		
		JsonObject data = new JsonObject();
		data.addProperty("id", task.getId());
		data.addProperty("color", task.getColor());
		data.addProperty("title", task.getTitle());
		data.addProperty("url", task.getUrl());
		data.add("product", productService.getJson(task.getProduct()));
		
		return data;
		
	}
	
}