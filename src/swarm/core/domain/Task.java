package swarm.core.domain;

import swarm.core.server.SwarmServer;
import swarm.core.services.TaskService;

public class Task extends Domain {

	String title;
	String url;
	String color;
	Product product;

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

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
	
	public void create() throws Exception {
		TaskService.create(this);
	}
	
}
