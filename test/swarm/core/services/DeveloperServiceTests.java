package swarm.core.services;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import swarm.core.domain.Developer;

public class DeveloperServiceTests {
	
	@Test
	public void loginTest() throws Exception {
		Developer d = DeveloperService.login("petrillo");
		assertEquals("petrillo", d.getName());
	}
}
