package swarm.core.services;

import static org.junit.Assert.*;

import org.eclipse.debug.core.DebugEvent;
import org.junit.Test;

import swarm.core.domain.Event;
import swarm.core.domain.Session;

public class EventServiceTests {
	
	@Test
	public void createEvent() throws Exception {
		Event event = new Event();
		event.setDetail(DebugEvent.CREATE);
		event.setKind(DebugEvent.STEP_INTO);
		
		Session s = new Session();
		s.setId(254);
		event.setSession(s);
		
		event.create();
		
		assertTrue(event.isLoaded());
	}

}
