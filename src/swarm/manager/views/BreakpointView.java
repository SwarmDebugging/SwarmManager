package swarm.manager.views;

import java.util.List;
import java.util.ArrayList;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import swarm.core.domain.Breakpoint;
import swarm.core.domain.Developer;
import swarm.core.domain.Project;
import swarm.core.domain.Session;
import swarm.core.domain.Task;
import swarm.core.server.ElasticServer;
import swarm.core.services.SessionService;
import swarm.core.services.TaskService;
import swarm.core.util.WorkbenchUtil;


public class BreakpointView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "swarm.manager.views.BreakpointView";

	private TableViewer viewer;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;

	Developer developer;

	private Combo projectCombo;

	class NameSorter extends ViewerSorter {}

	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout(4, false);
		parent.setLayout(layout);

		Label projectLabel = new Label(parent, SWT.NONE);
		projectLabel.setText("Project ");
		projectCombo = new Combo(parent, SWT.BORDER | SWT.SEARCH | SWT.READ_ONLY);
		populateTaskCombo();

		projectCombo.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

		Label searchLabel = new Label(parent, SWT.NONE);
		searchLabel.setText("Search ");
		final Text searchText = new Text(parent, SWT.BORDER | SWT.SEARCH);
		searchText.setText("*");
		searchText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		
		
		projectCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				Project project = new Project();
				project.setName(projectCombo.getText());
				try {
					List<Breakpoint> breakpoints = ElasticServer.getBreakpoints(project,searchText.getText().trim());
					viewer.setInput(breakpoints.toArray());
				} catch (Exception e) {
					e.printStackTrace();
				}			
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent event) {}
		});
		
		searchText.addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(TraverseEvent event) {
				if(event.detail == SWT.TRAVERSE_RETURN) {
					try {
						Project project = new Project();
						String projectName = projectCombo.getText();
						project.setName(projectName);
						List<Breakpoint> breakpoints = ElasticServer.getBreakpoints(project, searchText.getText().trim());
						viewer.setInput(breakpoints.toArray());
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		createViewer(parent);

		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "SwarmManager.viewer");
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}
	
	
	private void createViewer(Composite parent) {
		viewer = new TableViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns(parent, viewer);
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(false);

		viewer.setContentProvider(new ArrayContentProvider());

		getSite().setSelectionProvider(viewer);

		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 4;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		
		viewer.getControl().setLayoutData(gridData);		

	}
	

	private void populateTaskCombo() {
		if (developer != null) {
			List<Session> sessions = SessionService.getAll();
			List<String> projectNames = new ArrayList<String>();

			for (int i = 0; i < sessions.size(); i++) {
				if(sessions.get(i).getDeveloper().getId() == developer.getId()) {
					projectNames.add(sessions.get(i).getProject().toString());
				}
			}
			
			String[] itemsArray = new String[projectNames.size()];
		    itemsArray = projectNames.toArray(itemsArray);

			projectCombo.setItems(itemsArray);
		}
	}



	private void createColumns(final Composite parent, final TableViewer viewer) {
		String[] titles = { "Developer", "Breakpoint Code", "Label", "Purpose", "Type", "Description"};
		int[] bounds = { 100,  500, 300, 250, 200, 500};

		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Breakpoint b = (Breakpoint) element;
				return b.getSession().getDeveloper().getName();
			}
		});

		col = createTableViewerColumn(titles[1], bounds[1], 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Breakpoint b = (Breakpoint) element;
				return b.getCode();
			}
		});

		col = createTableViewerColumn(titles[2], bounds[2], 2);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Breakpoint b = (Breakpoint) element;
				return b.getSession().getLabel();
			}
		});
		
		col = createTableViewerColumn(titles[3], bounds[3], 3);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Breakpoint b = (Breakpoint) element;
				return b.getSession().getPurpose();
			}
		});
		
		col = createTableViewerColumn(titles[4], bounds[4], 4);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Breakpoint b = (Breakpoint) element;
				return b.getType().getFullName();
			}
		});
		
		col = createTableViewerColumn(titles[5], bounds[5], 5);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Breakpoint b = (Breakpoint) element;
				return b.getSession().getDescription();
			}
		});
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound, final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				BreakpointView.this.fillContextMenu(manager);
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
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				
				if(obj instanceof Breakpoint) {
					Breakpoint breakpoint = (Breakpoint) obj;
					IJavaProject javaProject;
					try {
						javaProject = WorkbenchUtil.getProjectByName(projectCombo.getText());
						IType javaType = javaProject.findType(breakpoint.getType().getFullName());

						WorkbenchUtil.openEditor(javaType);
						WorkbenchUtil.moveToLineInEditor(breakpoint.getLineNumber() - 1);
						
					} catch (Exception e) {
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

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(), "Starting Method", message);
	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void setDeveloper(Developer developer) {
		this.developer = developer;
		populateTaskCombo();
	}
}