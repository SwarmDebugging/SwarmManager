package swarm.core.debug;

import java.util.List;

import org.eclipse.core.internal.resources.Marker;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.IBreakpointListener;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import swarm.core.domain.Breakpoint;
import swarm.core.domain.Namespace;
import swarm.core.domain.Session;
import swarm.core.domain.Type;
import swarm.core.services.NamespaceService;
import swarm.core.services.TypeService;

@SuppressWarnings("restriction")
public final class SwarmBreakpointListener implements IBreakpointListener {

	private Session session;
	private Shell shell;

	public SwarmBreakpointListener(Session session, Shell shell) {
		this.session = session;
		this.shell = shell;
	}

	@Override
	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		System.out.println("Static BreakpointManager -> BreakpointRemoved Event");
	}

	@Override
	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
		System.out.println("Static BreakpointManager -> BreakpointChanged Event");
	}

	@Override
	public void breakpointAdded(IBreakpoint breakpoint) {
		System.out.println("Static BreakpointManager -> BreakpointAdded Event");
		IProject actualProject = breakpoint.getMarker().getResource().getProject();

	    if (session.getProject().getJavaProject() == null) {
	    	MessageDialog.openWarning(shell, "Swarm Debugging", "Sorry, I can not add this breakpoint. You are debugging " + 
	    							  actualProject.getName()	+ 
									  ", but the active Swarm project is " + session.getProject().getName());
	    	return;
		}

				
		if (!actualProject.equals(session.getProject().getJavaProject().getProject())) {
			MessageDialog.openWarning(shell, "Swarm Debugging", "Sorry, I can not add this breakpoint. You are debugging " + 
										actualProject.getName()	+ 
										", but the active Swarm project is " + session.getProject().getName());
			return;
		}

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					addBreakpoint(breakpoint);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
			
		thread.start();

	}

	private void addBreakpoint(IBreakpoint iBreakpoint) throws Exception {
		// TODO: See MarkerDelta!
		Breakpoint breakpoint = new Breakpoint();
		breakpoint.setSession(session);
		
		IMarker marker = iBreakpoint.getMarker();
		breakpoint.setLineNumber(Integer.parseInt(marker.getAttribute(Marker.LINE_NUMBER).toString()));
		breakpoint.setCharStart(Integer.parseInt(marker.getAttribute(Marker.CHAR_START).toString()));
		breakpoint.setCharEnd(Integer.parseInt(marker.getAttribute(Marker.CHAR_END).toString()));

		IResource resource = marker.getResource();

		String typePath = resource.getFullPath().toString();
		List<Type> types = session.getTypes();

		Type type = null;

		for (Type t : types) {
			if (t.getFullPath().equals(typePath)) {
				type = t;
				break;
			}
		}

		if (type == null) {
			String namespacePath = resource.getParent().getFullPath().toString();
			Namespace namespace = NamespaceService.getNamespaceByFullPath(namespacePath);

			if (namespace == null) {
				namespace = new Namespace();
				if(resource.getParent() instanceof IPackageFragment) {
					namespace.setName(((IPackageFragment) resource.getParent()).getElementName());
				} else {
					namespace.setName(resource.getParent().getName());
				}

				namespace.setFullPath(resource.getParent().getFullPath().toString());
				namespace.create();

				if (!namespace.isLoaded()) {
					throw new Exception("Problem to create the new namespace");
				}
			}
			String s = resource.getFullPath().toString();
			type = TypeService.createByPath(session,s);
		}

		breakpoint.setType(type);
		breakpoint.create();
	}
}