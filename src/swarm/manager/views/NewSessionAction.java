package swarm.manager.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

import swarm.core.debug.DebugTracer;
import swarm.core.domain.Project;
import swarm.core.domain.Session;
import swarm.core.server.SwarmServer;
import swarm.core.util.WorkbenchUtil;

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
					
				NewSessionDialog dialog = new NewSessionDialog(viewer.getSite().getShell());
				
				if(dialog.open() != Dialog.OK) {
					return;
				}
				
				session.setProject(project);
				session.setDeveloper(viewer.developer);
				session.setLabel(dialog.getLabel());
				session.setDescription(dialog.getDescription());
				session.setPurpose(dialog.getPurpose());
				
				session.create();
				
				viewer.debugTracer = new DebugTracer(session, viewer);
				viewer.debugTracer.activeDebugTracer();
				viewer.stopSessionAction.setEnabled(true);
					
				viewer.addSession(session);
				
				IViewPart view = WorkbenchUtil.showView(DynamicMethodCallGraph.ID);
				DynamicMethodCallGraph browser = (DynamicMethodCallGraph) view;
				String url = SwarmServer.getInstance().getServerUrl() + 
							"graphSession?idSession="+session.getId() +
						     "&addType=false&layout=dagre&rankDir=TB";
				browser.setUrl(url);
				browser.setProject(project);
				
				view = WorkbenchUtil.showView(SequencePathView.ID);
				SequencePathView path = (SequencePathView) view;
				url = SwarmServer.getInstance().getServerUrl() + 
							"sequencePath?rankDir=LR&sessionId="+session.getId();
				path.setUrl(url);
				path.setProject(project);

				//Opening Debug Perspective
				PlatformUI.getWorkbench().showPerspective("org.eclipse.debug.ui.DebugPerspective", 
												PlatformUI.getWorkbench().getActiveWorkbenchWindow());
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openWarning(shell, "Swarm Debugging", "Creating a session failed.");
			}
		} else {
			MessageDialog.openWarning(shell, "Swarm Debugging", "Please, stop the active session " + session.toString() + " before creating a new session");
		}
	}
}