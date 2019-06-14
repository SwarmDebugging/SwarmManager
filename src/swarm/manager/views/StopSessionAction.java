package swarm.manager.views;

import org.eclipse.jface.action.Action;

public class StopSessionAction extends Action {

	private ManagerView viewer;

	public StopSessionAction(ManagerView viewer) {
		setText("Debug Session");
		setToolTipText("Stop a session");
		setEnabled(false);
		setImageDescriptor(Images.STOP_DESCRIPTOR);

		this.viewer = viewer;
	}

	public void run() {
		viewer.debugTracer.stop();
		
		/*
		 Pegar a session ativa no momento
		 session.setFinished(now);
		 session.stop();
		 */
		
		if (viewer.activeSession != null) {
			try {
				viewer.activeSession.stop();
				viewer.activeSession = null;
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