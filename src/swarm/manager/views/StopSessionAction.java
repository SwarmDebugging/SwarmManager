package swarm.manager.views;

import org.eclipse.jface.action.Action;

import swarm.core.domain.Session;

public class StopSessionAction extends Action {

	private Session session;
	private ManagerView viewer;

	public StopSessionAction(ManagerView viewer) {
		setText("Debug Session");
		setToolTipText("Stop a session");
		setEnabled(false);
		setImageDescriptor(Images.STOP_DESCRIPTOR);

		this.session = viewer.actualSession;
		this.viewer = viewer;
	}

	public void run() {
		viewer.debugTracer.stop();
		if (session != null) {
			try {
				session.stop();
				viewer.actualSession = new Session();
				setEnabled(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setStop() {
		setImageDescriptor(Images.STOP_DESCRIPTOR);
	}
}