package swarm.core.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import swarm.core.server.SwarmServer;
import swarm.core.services.DeveloperService;
import swarm.core.services.JSON;

public class Developer extends Domain {

	String color;
	String name;
	boolean logged;
	
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

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
	
	public List<Task> getTasks() {
		return DeveloperService.getTasks(this);
	}
	
	public String getURI() {
		return SwarmServer.getInstance().getServerUrl() + SwarmServer.DEVELOPERS + "/" + getId(); 
	}
	
	public Map getData() {
		Map data = new HashMap<>();
		data.put("id", this.getId());
		data.put("color", this.getColor());
		data.put("name", this.getName());
		//data.put("timestamp", this.getTimestamp());
		return data;
	}
	
}
