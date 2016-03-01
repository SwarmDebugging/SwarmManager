package swarm.manager.views;

import java.util.Date;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import swarm.core.debug.DebugTracer;
import swarm.core.domain.Project;
import swarm.core.domain.Session;

public class NewSessionAction extends Action {

	private Session session;
	private ManagerView viewer;
	
	private Shell shell;

	public NewSessionAction(ManagerView viewer) {
		setText("New Session");
		setToolTipText("Create a session");
		setEnabled(false);
		setImageDescriptor(Images.SESSION_DESCRIPTOR);

		this.session = viewer.actualSession;
		this.viewer = viewer;
		
		this.shell = viewer.getShell();
	}

	public void run() {
		if (!session.isActive()) {
			try {
				Project project = null;

				Object selection = viewer.getSelectedItem();
				
				if(selection instanceof Project) {
					project = (Project) selection;
				} else {
					MessageDialog.openWarning(shell, "Swarm Debugging", "Select a Swarm project to start a new session.");
					return;
				}
					
//				NewSessionDialog dialog = new NewSessionDialog(viewer.getSite().getShell());
//				
//				if(dialog.open() != Dialog.OK) {
//					return;
//				}
				
				session.setProject(project);
				session.setDeveloper(viewer.developer);
				session.setLabel("Session " + new Date());
//				session.setLabel(dialog.getLabel());
//				session.setDescription(dialog.getDescription());
//				session.setPurpose(dialog.getPurpose());
				
				session.create();
				
				viewer.debugTracer = new DebugTracer(session, viewer);
				viewer.debugTracer.activeDebugTracer();
				viewer.stopSessionAction.setEnabled(true);
					
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
			MessageDialog.openWarning(shell, "Swarm Debugging", "Please, stop the active session " + session.toString() + " before creating a new session");
		}
	}
}