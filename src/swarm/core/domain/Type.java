package swarm.core.domain;

import java.util.List;

import swarm.core.server.SwarmServer;
import swarm.core.services.TypeService;

public class Type extends Domain {

	private Session session;
	private Namespace namespace;
	
	private String name;
	private String fullName;
	private String fullPath;

	private String source;

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public Namespace getNamespace() {
		return namespace;
	}

	public void setNamespace(Namespace namespace) {
		this.namespace = namespace;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	

	public void create() throws Exception {
		TypeService.create(this);
	}
	
	public List<Method> getMethods() throws Exception {
		return TypeService.getMethods(this);
	}
	
	public String toString() {
		return this.fullName;
	}
	
	public String getURI() {
		return SwarmServer.getInstance().getServerUrl() + SwarmServer.TYPES + "/" + getId(); 
	}
}
