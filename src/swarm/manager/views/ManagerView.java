package swarm.manager.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

import swarm.core.debug.DebugTracer;
import swarm.core.domain.Developer;
import swarm.core.domain.Project;
import swarm.core.domain.Session;
import swarm.core.domain.Task;
import swarm.core.server.SwarmServer;
import swarm.core.util.WorkbenchUtil;

public class ManagerView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "swarm.manager.views.ManagerView";

	private ManagerTreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;

	protected LoginAction loginAction;
	protected NewSessionAction newSessionAction;
	protected StopSessionAction stopSessionAction;
	protected NewTaskAction newProjectAction;

	private Action doubleClickAction;

	protected Developer developer;
	protected Session activeSession;
	
	protected DebugTracer debugTracer;
	
	protected Composite parent;

	protected Shell shell;


	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		this.parent = parent;
		this.shell = parent.getShell();
		
		ManagerView me = this;
		
		viewer = new ManagerTreeViewer(parent);
		drillDownAdapter = new DrillDownAdapter(viewer);
		
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
		
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if(event.getSelection() instanceof TreeSelection) {
					Object item = ((TreeSelection) event.getSelection()).getFirstElement();
					
					newSessionAction.setEnabled(item instanceof Task);
					
					if(item instanceof Session) {
						Session s = (Session) item;
						
						if(s.equals(activeSession)) {
							stopSessionAction.setEnabled(s.isActive());
							return;
						} else if(s.isActive() && debugTracer == null) {
							activeSession = s;
							activeSession.setDeveloper(developer);
							debugTracer = new DebugTracer(activeSession, me	);
							debugTracer.activeDebugTracer();
							stopSessionAction.setEnabled(true);
						} 
					}
				}
			}
		});
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ManagerView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(loginAction);
		manager.add(newProjectAction);
		manager.add(new Separator());
		manager.add(newSessionAction);
		manager.add(stopSessionAction);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(loginAction);
		manager.add(newProjectAction);
		manager.add(newSessionAction);
		manager.add(stopSessionAction);

		manager.add(new Separator());

		drillDownAdapter.addNavigationActions(manager);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(loginAction);
		manager.add(newProjectAction);
		manager.add(newSessionAction);
		manager.add(stopSessionAction);

		manager.add(new Separator());

		drillDownAdapter.addNavigationActions(manager);
	}

	private void makeActions() {
		loginAction = new LoginAction(developer, this);
		newProjectAction = new NewTaskAction(this);
		
		newSessionAction = new NewSessionAction(this);
		stopSessionAction = new StopSessionAction(this);
		
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				
				if(obj instanceof Task) {
					TaskView browser;
					try {
						browser = (TaskView) WorkbenchUtil.showView(TaskView.ID);
						browser.setTask((Task) obj);
						browser.refresh();
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					TaskBreakpointView breakpoint;
					try {
						breakpoint = (TaskBreakpointView) WorkbenchUtil.showView(TaskBreakpointView.ID);
						breakpoint.setTask((Task) obj);
						breakpoint.refresh();
						
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				else if(obj instanceof Session) {
					try {
						Session s = (Session) obj;

						DynamicMethodCallGraph graphBrowser  = (DynamicMethodCallGraph) WorkbenchUtil.findView(DynamicMethodCallGraph.ID);
						graphBrowser  = (DynamicMethodCallGraph) WorkbenchUtil.showView(DynamicMethodCallGraph.ID);
						String graphUrl = SwarmServer.getInstance().getServerUrl() + "graph.html?sessionId="+s.getId();
						System.out.println(graphUrl);
						graphBrowser.setUrl(graphUrl);
						graphBrowser.setProject(s.getProject());
						WorkbenchUtil.showView(DynamicMethodCallGraph.ID);
						
//						SequencePathView sequenceBrowser = (SequencePathView) WorkbenchUtil.findView(SequencePathView.ID);
//						String sequenceUrl = SwarmServer.getInstance().getServerUrl() + "stack.html?sessionId="+s.getId();
//						System.out.println(graphUrl);
//						sequenceBrowser.setUrl(sequenceUrl);
//						sequenceBrowser.setProject(s.getProject());
//						WorkbenchUtil.showView(SequencePathView.ID);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}


	public void logged(boolean sucess) {
		newProjectAction.setEnabled(sucess);
		newSessionAction.setEnabled(false);
		stopSessionAction.setEnabled(false);
		
		if(sucess) {
			viewer.setInput(developer);
			newProjectAction.setDeveloper(developer);
			
			
//			try {
//				IViewPart view = WorkbenchUtil.findView(BreakpointView.ID);
//				BreakpointView breakpointView = (BreakpointView) view;
//				breakpointView.setDeveloper(developer);
//				
//				view = WorkbenchUtil.findView(StartingMethodView.ID);
//				StartingMethodView startingView = (StartingMethodView) view;
//				startingView.setDeveloper(developer);
//				
//				view = WorkbenchUtil.findView(EndingMethodView.ID);
//				EndingMethodView endingView = (EndingMethodView) view;
//				endingView.setDeveloper(developer);				
//
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}

	public void addProject(Project project) {
		viewer.setInput(project);
	}
	
	public void addSession(Session session) {
		viewer.setInput(session);
	}
	
	public void setDeveloper(Developer developer) {
		this.developer = developer;
	}

	public Object getSelectedItem() {
		return ((ITreeSelection) viewer.getSelection()).getFirstElement();
	}

	public Shell getShell() {
		return shell;
	}
	
	public StopSessionAction getStopSessionAction() {
		return stopSessionAction;
	}
}