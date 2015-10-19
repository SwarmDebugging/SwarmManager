package swarm.core.domain;

import swarm.core.server.SwarmServer;
import swarm.core.services.MethodService;

public class Method extends Domain {

	private Type type;

	private String name;
	private String signature;
	private String key;

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String toString() {
		return type.getName() + "." + name + signature;
	}
	
	public boolean equals(Object object) {
		if(object instanceof Method) {
			return key.equals(((Method) object).getKey());
		}
		return false;
	}

	public void create() throws Exception {
		MethodService.create(this);
	}
	
	public String getURI() {
		return SwarmServer.getInstance().getServerUrl() + SwarmServer.METHODS + "/" + getId(); 
	}

}
