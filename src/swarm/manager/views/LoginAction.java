package swarm.manager.views;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;

import swarm.core.domain.Developer;

public class LoginAction extends Action {

	private Developer developer;
	private ManagerView viewer;
	private Shell shell;

	public LoginAction(Developer developer, ManagerView viewer) {
		this.developer = developer;
		this.viewer = viewer;
		this.shell = viewer.getViewSite().getShell();

		setText("Developer Login");
		setToolTipText("Login to Swarm Server");
		setImageDescriptor(Images.LOGIN_DESCRIPTOR);
	}

	public void run() {
		try {
			LoginDialog dialog = new LoginDialog(this.shell);
			dialog.open();
			developer = dialog.getLogged();
			if (developer != null && developer.isLogged()) {
				viewer.setDeveloper(developer);
				viewer.logged(true);
			} else {
				viewer.logged(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
