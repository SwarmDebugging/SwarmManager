package swarm.manager.views;


import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class DashboardBrowser extends ViewPart {

	public static final String ID = "swarm.manager.views.DashboardBrowser";

	private Browser browser;
	
	public void createPartControl(Composite parent) {
		browser = new Browser(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		//TODO Pass by configuration
		browser.setUrl("http://localhost:5601/#/dashboard/Dashboard?_g=()&_a=(filters:!(),panels:!((col:7,id:Events-by-Type,row:1,size_x:3,size_y:5,type:visualization),(col:1,id:Breakpoints-by-Developer,row:1,size_x:3,size_y:2,type:visualization),(col:4,id:Breakpoints-by-Developer-and-Purpose,row:1,size_x:3,size_y:5,type:visualization),(col:1,id:Breakpoints-by-Project,row:3,size_x:3,size_y:3,type:visualization),(col:7,id:Event-Histogram,row:6,size_x:6,size_y:3,type:visualization),(col:1,id:Invocations-by-Developer,row:6,size_x:3,size_y:3,type:visualization),(col:1,id:Invocations-by-invoked-type-and-project,row:14,size_x:12,size_y:5,type:visualization),(col:1,id:Invocations-by-invoking-type-and-project,row:9,size_x:12,size_y:5,type:visualization),(col:10,id:Invocations-by-Method,row:1,size_x:3,size_y:5,type:visualization),(col:4,id:Invocations-by-Project,row:6,size_x:3,size_y:3,type:visualization)),query:(query_string:(analyze_wildcard:!t,query:'*')),title:Dashboard)");
		
		browser.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent arg0) {}
			
			@Override
			public void mouseDown(MouseEvent arg0) {}
			
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				browser.refresh();
			}
		});
		
		browser.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(KeyEvent event) {
				switch (event.keyCode) {
				case SWT.F10:
					browser.evaluate("prev();");
					break;
				case SWT.F9:
					browser.evaluate("next();");
					break;
				default:
					break;
				}
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
//		
//		browser.addControlListener(new ControlListener() {
//			
//			@Override
//			public void controlResized(ControlEvent arg0) {
//				browser.refresh();
//			}
//			
//			@Override
//			public void controlMoved(ControlEvent arg0) {}
//		});
		
		browser.addStatusTextListener(new StatusTextListener() {
			@Override
			public void changed(StatusTextEvent event) {
				if (event.text.startsWith("MOUSEDOWN: ")) {
					System.out.println(event.text);
				}
			}
		});
	}
	
	public void setFocus() {
		browser.setFocus();
	}
}