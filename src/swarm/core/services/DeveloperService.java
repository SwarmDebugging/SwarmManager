package swarm.core.services;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import swarm.core.domain.Project;
import swarm.core.server.SwarmServer;
import swarm.core.domain.Developer;

public class DeveloperService {

	public static Developer login(String name) {
		SwarmServer server = SwarmServer.getInstance();

		if(!server.isOk()) {
			return null;
		}
		
		String response;
		try {
			response = server.get("login?name=" +name);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonObject() && element.getAsJsonObject().get("id") != null) {
			Developer developer = new Developer();
			populate(element, developer);
			developer.setLogged(true);
			return developer;
		} else {
	 		return null;
		}
	}

	public static void populate(JsonElement element, Developer developer) {
		developer.setId(element.getAsJsonObject().get("id").getAsInt());
		developer.setName(element.getAsJsonObject().get("name").getAsString());
	}

	public static List<Project> getProjects(Developer developer) {
		List<Project> projects = new ArrayList<Project>();
		SwarmServer server = SwarmServer.getInstance();

		String response;
		try {
			response = server.get("/projects/getByDeveloperId?developerId=" + developer.getId());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonArray()) {
			JsonArray jsonTypes = (JsonArray) element.getAsJsonArray();
			for (JsonElement typeElement : jsonTypes) {
				Project project = new Project();
				ProjectService.populate(typeElement, project);
				projects.add(project);
			}
			return projects;
		} else {
	 		return null;
		}
	}

	public static Developer get(int id) {
		SwarmServer server = SwarmServer.getInstance();

		String response;
		try {
			response = server.get(SwarmServer.DEVELOPERS + "/" +id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonObject() && element.getAsJsonObject().get("id") != null) {
			Developer d = new Developer();
			DeveloperService.populate(element, d);
			return d;
		} else {
	 		return null;
		}	
	}
	
	public static List<Developer> getDevelopers() {
		List<Developer> developers = new ArrayList<Developer>();
		SwarmServer server = SwarmServer.getInstance();

		String response;
		try {
			response = server.get(SwarmServer.DEVELOPERS);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonArray()) {
			JsonArray jsonTypes = (JsonArray) element.getAsJsonArray();
			for (JsonElement typeElement : jsonTypes) {
				Developer developer = new Developer();
				populate(typeElement, developer);
				developers.add(developer);
			}
			return developers;
		} else {
			return null;
		}
	}	
}