package swarm.core.domain;

import swarm.core.services.InvocationService;

public class Invocation extends Domain {

	private Session session;
	
	private Method invoking;
	private Method invoked;

	private Event event;
	
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


	public Event getEvent() {
		return this.event;
	}
	
	public void setEvent(Event event) {
		this.event = event;
	}
	
	public boolean equals(Object object) {
		if(object != null && object instanceof Invocation) {
			Invocation i = (Invocation) object;
			return i.session.id == session.id && i.invoking.id == invoking.id && i.invoked.id == invoked.id;
		}
		return false;
	}	
}

