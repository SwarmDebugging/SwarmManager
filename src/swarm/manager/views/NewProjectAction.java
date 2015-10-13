package swarm.manager.views;

import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import swarm.core.domain.Developer;
import swarm.core.domain.Project;
import swarm.core.services.ProjectService;
import swarm.core.util.WorkbenchUtil;

public class NewProjectAction extends Action {

	private ManagerView viewer;

	private Shell shell;
	private Developer developer;

	public NewProjectAction(ManagerView viewer) {
		setText("New Project");
		setToolTipText("Create a new Swarm project.");
		setEnabled(false);
		setImageDescriptor(Images.NEWPROJECT_DESCRIPTOR);
		this.developer = viewer.developer;
		this.viewer = viewer;

		this.shell = viewer.getShell();
	}

	public void run() {
		try {
			Project project = null;

			IJavaProject javaProject = getProject();
			
			if(javaProject == null) {
				MessageDialog.openWarning(shell, "Swarm Debugging", "Please, select a Java Project in your Workspace.");
				return;
			}
			
			List<Project> projects = developer.getProjects();
			for (Project p : projects) {
				if (p.getName().equals(javaProject.getProject().getName())) {
					MessageDialog.openWarning(shell, "Swarm Debugging", "The project " + javaProject.getProject().getName()
							+ " is already in your Swarm Manager.");
					return;
				}
			}
			
			projects = ProjectService.getProjects();
			for (Project p : projects) {
				if (p.getName().equals(javaProject.getProject().getName())) {
					project = p;
					break;
				}
			}
			
			if (project == null) {
				project = new Project();
				project.setName(javaProject.getProject().getName());
				project.create();
			}
			project.setJavaProject(javaProject);

			viewer.addProject(project);
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openWarning(shell, "Swarm Debugging", e.getMessage());
		}
	}

	private IJavaProject getProject() throws Exception {
		Object project = WorkbenchUtil.getSelectedProject();
		if (project != null && project instanceof IJavaProject) {
			return (IJavaProject) project;
		} else {
			return null;
		}
	}

	public void setDeveloper(Developer developer) {
		this.developer = developer;
	}
}