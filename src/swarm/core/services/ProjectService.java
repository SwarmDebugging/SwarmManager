package swarm.core.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IJavaProject;

import swarm.core.domain.Developer;
import swarm.core.domain.Method;
import swarm.core.domain.Project;
import swarm.core.domain.Session;
import swarm.core.server.SwarmServer;
import swarm.core.util.WorkbenchUtil;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class ProjectService {

	public static void create(Project project) throws Exception {
		SwarmServer server = SwarmServer.getInstance();

		Map<String, Object> data = new HashMap<>();
		data.put("name", project.getName());

		String json = JSON.build(data);
		String response = server.create(SwarmServer.PROJECTS, json);
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonObject()) {
			int id = element.getAsJsonObject().get("id").getAsInt();
			project.setId(id);
		}
	}

	public static void populate(JsonElement element, Project project) {
		project.setId(element.getAsJsonObject().get("id").getAsInt());
		project.setName(element.getAsJsonObject().get("name").getAsString());

		try {
			IJavaProject iProject = (IJavaProject) WorkbenchUtil.getProjectByName(project.getName());
			project.setJavaProject(iProject);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static Project get(int id) {
		SwarmServer server = SwarmServer.getInstance();

		String response;
		try {
			response = server.get(SwarmServer.PROJECTS + "/" + id);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response);

		if (element.isJsonObject() && element.getAsJsonObject().get("id") != null) {
			Project project = new Project();
			ProjectService.populate(element, project);
			return project;
		} else {
			return null;
		}
	}

	public static List<Project> getProjects() {
		List<Project> projects = new ArrayList<Project>();
		SwarmServer server = SwarmServer.getInstance();

		String response;
		try {
			response = server.get(SwarmServer.PROJECTS);
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
	
	public static List<Method> getStartingMethods(Project project) {
		List<Method> methods = new ArrayList<Method>();

		List<Developer> developers = DeveloperService.getDevelopers();
		for (Developer developer : developers) {
			List<Session> sessions = SessionService.getSessions(project, developer);
			for (Session session : sessions) {
				List<Method> starting = session.getStartingMethods();
				for (Method method : starting) {
					if(!methods.contains(method)) {
						methods.add(method);
					}
				}
			}
		}
			
		return methods;
	}
	
	public static List<Method> getEndingMethods(Project project) {
		List<Method> methods = new ArrayList<Method>();

		List<Developer> developers = DeveloperService.getDevelopers();
		for (Developer developer : developers) {
			List<Session> sessions = SessionService.getSessions(project, developer);
			for (Session session : sessions) {
				List<Method> ending = session.getEndingMethods();
				for (Method method : ending) {
					if(!methods.contains(method)) {
						methods.add(method);
					}
				}
			}
		}
			
		return methods;
	}
}