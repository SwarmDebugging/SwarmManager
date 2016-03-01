package swarm.core.services;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import swarm.core.domain.Namespace;

public class NamespaceServiceTests {
	
	@Test
	public void getNamespaceByFullPath() throws Exception {
		Date d = new Date();
		String fullPath = "myProject/myOrganisation/project/package" + d;
		Namespace n = new Namespace();
		n.setName("Test " + d);
		n.setFullPath(fullPath);

		NamespaceService.create(n);
		
		Namespace n2 = NamespaceService.getNamespaceByFullPath(fullPath);
		
		assertTrue(n.equals(n2));
	}
	
	public void get() throws Exception {
		Date d = new Date();
		String fullPath = "myProject/myOrganisation/project/package" + d;
		Namespace n = new Namespace();
		n.setName("Test " + d);
		n.setFullPath(fullPath);

		NamespaceService.create(n);
		
		Namespace n2 = NamespaceService.get(n.getId());
		
		assertTrue(n.equals(n2));
	}

}
