package swarm.core.domain;

import swarm.core.server.SwarmServer;

public class Task extends Domain {

	String title;
	
	String description;
	
	String url;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
}
