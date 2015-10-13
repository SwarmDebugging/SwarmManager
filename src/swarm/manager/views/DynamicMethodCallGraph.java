package swarm.manager.views;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import swarm.core.domain.Method;
import swarm.core.domain.Project;
import swarm.core.services.MethodService;
import swarm.core.util.WorkbenchUtil;

public class DynamicMethodCallGraph extends ViewPart {

	public static final String ID = "swarm.manager.views.DynamicMethodCallGraph";

	private Browser browser;

	protected Project project;
	
	public void createPartControl(Composite parent) {
		browser = new Browser(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		
		browser.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent arg0) {}
			
			@Override
			public void mouseDown(MouseEvent arg0) {
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				//browser.refresh();
				Object jsvar = browser.evaluate("return tappedMethod;");
				if(jsvar != null) {
					int methodId = new Integer(jsvar.toString().substring(1));
					Method method = MethodService.get(methodId);

					try {
						IType javaType = project.getJavaProject().findType(method.getType().getFullName());
						
						IMethod[] methods = javaType.getMethods();
						for (IMethod iMethod : methods) {
							//TODO Issue: problem whether more than one signature
							if(iMethod.getElementName().equals(method.getName())) {
								WorkbenchUtil.openEditor(iMethod);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}				
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

//					try {
//						String jsvar = browser.evaluate("return cy.png();").toString();
//						 
//						File of = new File("/home/petrillo/tmp/output.png");
//						FileOutputStream osf = new FileOutputStream(of);
//						osf.write(jsvar.getBytes());
//						osf.flush();
//						osf.close();
//						
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					
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
		
		browser.addStatusTextListener(new StatusTextListener() {
			@Override
			public void changed(StatusTextEvent event) {
				if (event.text.startsWith("MOUSEDOWN: ")) {
					System.out.println(event.text);
				}
			}
		});
	}
	
	public void setProject(Project project) {
		this.project = project;
	}

	public void setUrl(String url) {
		browser.setUrl(url);
	}
	
	public void setFocus() {
		browser.setFocus();
	}
}