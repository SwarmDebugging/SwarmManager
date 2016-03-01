package swarm.core.domain;

import swarm.core.server.SwarmServer;
import swarm.core.services.InvocationService;

public class Invocation extends Domain {

	private Session session;
	
	private Method invoking;
	private Method invoked;
	
	public Session getSession() {
		return session;
	}


	public void setSession(Session session) {
		this.session = session;
	}


	public Method getInvoking() {
		return invoking;
	}


	public void setInvoking(Method invoking) {
		this.invoking = invoking;
	}


	public Method getInvoked() {
		return invoked;
	}


	public void setInvoked(Method invoked) {
		this.invoked = invoked;
	}


	public void create() throws Exception {
		InvocationService.create(this);
	}


	public boolean equals(Object object) {
		if(object != null && object instanceof Invocation) {
			Invocation i = (Invocation) object;
			return i.session.id == session.id && i.invoking.id == invoking.id && i.invoked.id == invoked.id;
		}
		return false;
	}
	
	public String getURI() {
		return SwarmServer.getInstance().getServerUrl() + SwarmServer.INVOCATIONS + "/" + getId(); 
	}

}

