package swarm.manager.views;


import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import swarm.core.domain.Breakpoint;
import swarm.core.domain.Task;
import swarm.core.server.SwarmServer;
import swarm.core.services.BreakpointService;
import swarm.core.util.WorkbenchUtil;

public class TaskBreakpointView extends ViewPart {

	public static final String ID = "swarm.manager.views.TaskBreakpointView";

	private Browser browser;

	protected Task task;
	
	public void createPartControl(Composite parent) {
		browser = new Browser(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		browser.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent event) {
			}
			
			@Override
			public void keyPressed(KeyEvent event) {
				switch (event.keyCode) {
				case SWT.F5:
					browser.refresh();
					break;
				default:
					break;
				}
			}
		});
		
		browser.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent arg0) {}
			
			@Override
			public void mouseDown(MouseEvent arg0) {
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				//browser.refresh();
				Object jsvar = browser.evaluate("return tapped;");
				if(jsvar != null) {
					boolean isNumber = jsvar.toString().substring(1).matches("^-?\\d+$");
					try {
						IJavaProject javaProject = WorkbenchUtil.getProjectByName("JabRef3.2");
						if(isNumber) {
							int breakpointId = new Integer(jsvar.toString().substring(1));
							Breakpoint breakpoint = BreakpointService.get(breakpointId);
							IType javaType = javaProject.findType(breakpoint.getType().getFullName());
							WorkbenchUtil.openEditor(javaType);
							WorkbenchUtil.moveToLineInEditor(breakpoint.getLineNumber() - 1);					
						} else {
							String typeFullName = jsvar.toString().substring(1);
								IType javaType = javaProject.findType(typeFullName);
								WorkbenchUtil.openEditor(javaType);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}				
			}
		});
		
		browser.addControlListener(new ControlListener() {
			
			@Override
			public void controlResized(ControlEvent arg0) {
				browser.refresh();
			}
			
			@Override
			public void controlMoved(ControlEvent arg0) {}
		});
		
	}
	
	public void setTask(Task task) {
		this.task = task;
		this.setPartName("Breakpoints by " + task.getTitle());
		String url = SwarmServer.getInstance().getServerUrl() + "taskBreakpoints.html?taskId="+task.getId();
		browser.setUrl(url);
		browser.refresh();
	}

	public void setFocus() {
		browser.setFocus();
	}
	
	public void refresh() {
		browser.refresh();
	}
}