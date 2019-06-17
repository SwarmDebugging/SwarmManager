package swarm.core.domain;

import swarm.core.server.SwarmServer;
import swarm.core.services.ProductService;

public class Product extends Domain {
	
	String name;

	public String getName() {
		return name;
	}

	public void setName(String title) {
		this.name = title;
	}

	public String getURI() {
		return SwarmServer.getInstance().getServerUrl() + SwarmServer.TASKS + "/" + getId();
	}
	
	public String toString() {
		return this.name;
	}
	
	public void create() throws Exception {
		ProductService.create(this);
	}

}
