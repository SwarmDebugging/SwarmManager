package swarm.core.domain;

import java.util.List;

import swarm.core.services.DeveloperService;

public class Developer extends Domain {

	String name;
	boolean logged;
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isLogged() {
		return this.logged;
	}

	public void setLogged(boolean logged) {
		this.logged = logged;
	}	
	
	public static Developer login(String name) {
		return DeveloperService.login(name);
	}
	
	public String toString() {
		return id + ": " + name;
	}
	
	public List<Project> getProjects() {
		return DeveloperService.getProjects(this);
	}
}
