package swarm.core.services;

import java.util.List;

import org.junit.Test;

import swarm.core.domain.Developer;
import swarm.core.domain.Method;
import swarm.core.domain.Project;

public class ProjectServiceTests {
	
	@Test
	public void getStartingMethodsTest() throws Exception {
		Developer d = DeveloperService.login("petrillo");
		Project p = d.getProjects().get(0);
		List<Method> methods = ProjectService.getStartingMethods(p);
		System.out.println(methods.size());
		for (Method method : methods) {
			System.out.println(method.getKey());
		}
	}

}
