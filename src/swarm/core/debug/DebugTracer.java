package swarm.core.debug;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.swt.widgets.Shell;

import swarm.core.domain.Session;
import swarm.manager.views.ManagerView;

public class DebugTracer {

	private Session session;
	private boolean isActive;
	private SwarmDebugEventListener debugEventListener;
	private SwarmBreakpointListener breakpointListener;
	
	private Shell shell;
	private ManagerView viewer;

	public DebugTracer(Session session, ManagerView viewer) {
		this.session = session;
		this.viewer = viewer;
	}

	public void activeDebugTracer() {
		debugEventListener = new SwarmDebugEventListener(session, viewer.getStopSessionAction());
		DebugPlugin.getDefault().addDebugEventListener(debugEventListener);

		IBreakpointManager manager = DebugPlugin.getDefault().getBreakpointManager();
		breakpointListener = new SwarmBreakpointListener(session, shell);
		manager.addBreakpointListener(breakpointListener);

		this.isActive = true;
	}

	public void stop() {
		DebugPlugin.getDefault().removeDebugEventListener(debugEventListener);
		DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener(breakpointListener);
		this.isActive = false;
	}

	public boolean isActive() {
		return this.isActive;
	}
}