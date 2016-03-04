package swarm.core.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import swarm.core.domain.Developer;
import swarm.core.domain.Project;
import swarm.core.domain.Session;
import swarm.core.services.DeveloperService;

public class SessionServiceTests {

	@Test
	public void sessionLifeCicle() throws Exception {
		Developer d = DeveloperService.login("Petrillo");
		Session s = new Session();
		s.setTask(d.getTasks().get(0));
		s.setLabel("Creating session ");
		s.setPurpose("Fix a bug");
		s.setDescription("A big sentence that represent a test description.");
		
		Project p = new Project();
		p.setName("ProjectA");
		s.setProject(p);

		s.start();
		
		assertTrue(s.getId() > 0);
		assertTrue(s.isActive());
		assertTrue(s.getStarted() != null && s.getFinished() == null);
		
		s.stop();

		assertFalse(s.isActive());
		assertTrue(s.getFinished() != null);
	}
}
