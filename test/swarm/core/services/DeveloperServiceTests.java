package swarm.core.services;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import swarm.core.domain.Developer;

public class DeveloperServiceTests {

	@Test
	public void login() throws Exception {
		Developer d = DeveloperService.login("Petrillo");
		if(d != null) {
			System.out.println(d.toString());
			assertTrue(d.isLogged());
		} else {
			fail("Login returned a null Developer!");
		}
		
		d = DeveloperService.login("xxxxx");
		if(d != null) {
			fail("Login must be returned a null Developer!");
		}
	}
}
