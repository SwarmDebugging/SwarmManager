package swarm.manager.views;

import java.util.Date;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import swarm.core.debug.DebugTracer;
import swarm.core.domain.Project;
import swarm.core.domain.Session;
import swarm.core.domain.Task;
import swarm.core.util.WorkbenchUtil;

public class NewSessionAction extends Action {

	private ManagerView viewer;
	
	private Shell shell;

	public NewSessionAction(ManagerView viewer) {
		setText("New Session");
		setToolTipText("Create a session");
		setEnabled(false);
		setImageDescriptor(Images.SESSION_DESCRIPTOR);

		this.viewer = viewer;
		
		this.shell = viewer.getShell();
	}
	
	public void run() {
		if (viewer.activeSession == null || (viewer.activeSession != null && !viewer.activeSession.isActive())) {
			try {
				Task task = null;

				Object selection = viewer.getSelectedItem();
				
				if(selection instanceof Task) {
					task = (Task) selection;
				} else {
					MessageDialog.openWarning(shell, "Swarm Debugging", "Select a task to start a new session.");
					return;
				}
				
				IJavaProject javaProject = getProject();
				
				if(javaProject == null) {
					MessageDialog.openWarning(shell, "Swarm Debugging", "Please, select a Java Project in your Workspace.");
					return;
				}
				
				
				
				Project project = new Project();
				project.setName(javaProject.getProject().getName());
				project.setJavaProject(javaProject);
				
				Session session = new Session();
				session.setTask(task);
				session.setDeveloper(viewer.developer);
				session.setLabel("Session " + new Date());
				session.setProject(project);
				/** Commented only to ease the tests, but this part is working
				NewSessionDialog dialog = new NewSessionDialog(viewer.getSite().getShell());
				dialog.open();
				session.setLabel(dialog.getLabel());
				session.setDescription(dialog.getDescription());
				session.setPurpose(dialog.getPurpose());
				**/
				
				session.create();
				
				viewer.debugTracer = new DebugTracer(session, viewer);
				viewer.debugTracer.activeDebugTracer();
				viewer.stopSessionAction.setEnabled(true);
				viewer.activeSession = session;
				viewer.addSession(session);
				
//				DynamicMethodCallGraph graphBrowser  = (DynamicMethodCallGraph) WorkbenchUtil.findView(DynamicMethodCallGraph.ID);
//				String graphUrl = SwarmServer.getInstance().getServerUrl() + "graph.html?sessionId="+session.getId();
//				graphBrowser.setUrl(graphUrl);
//				graphBrowser.setProject(session.getProject());
//				WorkbenchUtil.showView(DynamicMethodCallGraph.ID);
//				
//				SequencePathView sequenceBrowser = (SequencePathView) WorkbenchUtil.findView(SequencePathView.ID);
//				String sequenceUrl = SwarmServer.getInstance().getServerUrl() + "stack.html?sessionId="+session.getId();
//				sequenceBrowser.setUrl(sequenceUrl);
//				sequenceBrowser.setProject(session.getProject());
//				
//				//Opening Debug Perspective
//				PlatformUI.getWorkbench().showPerspective("org.eclipse.debug.ui.DebugPerspective", 
//												PlatformUI.getWorkbench().getActiveWorkbenchWindow());
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openWarning(shell, "Swarm Debugging", "Creating a session failed.");
			}
		} else {
			MessageDialog.openWarning(shell, "Swarm Debugging", "Please, stop the active session " + viewer.activeSession.toString() + " before creating a new session");
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
}