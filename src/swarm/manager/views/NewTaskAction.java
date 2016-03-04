package swarm.manager.views;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import swarm.core.domain.Developer;
import swarm.core.util.WorkbenchUtil;

public class NewTaskAction extends Action {

	private ManagerView viewer;

	private Shell shell;
	private Developer developer;

	public NewTaskAction(ManagerView viewer) {
		setText("New Task");
		setToolTipText("Create a new Task.");
		setEnabled(false);
		setImageDescriptor(Images.NEWPROJECT_DESCRIPTOR);
		this.developer = viewer.developer;
		this.viewer = viewer;

		this.shell = viewer.getShell();
	}

	public void run() {
		try {
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openWarning(shell, "Swarm Debugging", e.getMessage());
		}
	}

	public void setDeveloper(Developer developer) {
		this.developer = developer;
	}
}