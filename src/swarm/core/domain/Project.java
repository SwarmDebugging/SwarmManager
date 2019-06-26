package swarm.core.domain;

import org.eclipse.jdt.core.IJavaProject;

public class Project {
	
	private String name;

	private IJavaProject javaProject;
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	public IJavaProject getJavaProject() {
		return javaProject;
	}


	public void setJavaProject(IJavaProject javaProject) {
		this.javaProject = javaProject;
	}

//	public void create() throws Exception {
//		ProjectService.create(this);
//	}
	
	public String toString() {
		return this.name;
	}
	
	public boolean equals(Object object) {
		if(object instanceof Project) {
			return name == ((Project) object).getName();
		}
		return false;
	}

}