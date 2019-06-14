package swarm.core.domain;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import swarm.core.server.SwarmServer;
import swarm.core.services.JSON;

public class Task extends Domain {

	String title;
	String url;
	String color;

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getURI() {
		return SwarmServer.getInstance().getServerUrl() + SwarmServer.TASKS + "/" + getId();
	}
	
	public String toString() {
		return this.title;
	}
	
	public Map getData() {
		Map data = new HashMap<>();
		data.put("id", this.getId());
		data.put("color", this.getColor());
		data.put("title", this.getTitle());
		data.put("url", this.getUrl());
		return data;
	}
	
}
