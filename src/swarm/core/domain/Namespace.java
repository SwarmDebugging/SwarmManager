package swarm.core.domain;

import swarm.core.server.SwarmServer;
import swarm.core.services.NamespaceService;

public class Namespace extends Domain {

	private String name;
	private String fullPath;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}

	public boolean equals(Object object) {
		if(object != null && object instanceof Namespace) {
			Namespace n = (Namespace) object;
			return id == n.getId() && fullPath.equals(n.getFullPath());
			
		}
		
		return false;
	}

	public void create() throws Exception {
		NamespaceService.create(this);
	}
	
	public String getURI() {
		return SwarmServer.getInstance().getServerUrl() + SwarmServer.NAMESPACES + "/" + getId(); 
	}
}
