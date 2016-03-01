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

public class Neo4JBrowser extends ViewPart {

	public static final String ID = "swarm.manager.views.Neo4JBrowser";

	private Browser browser;
	
	public void createPartControl(Composite parent) {
		browser = new Browser(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		//TODO Pass by configuration
		browser.setUrl("http://localhost:7474/browser/");
		
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