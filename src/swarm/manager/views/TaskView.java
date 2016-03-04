package swarm.manager.views;


import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import swarm.core.domain.Task;

public class TaskView extends ViewPart {

	public static final String ID = "swarm.manager.views.TaskView";

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
		browser.setUrl(task.getUrl());
		browser.refresh();
	}

	public void setFocus() {
		browser.setFocus();
	}
}