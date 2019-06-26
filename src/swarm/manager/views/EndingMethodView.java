package swarm.manager.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
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

import swarm.core.domain.Developer;
import swarm.core.domain.Method;
import swarm.core.domain.Session;
import swarm.core.domain.Task;
import swarm.core.services.SessionService;
import swarm.core.services.TaskService;
import swarm.core.util.WorkbenchUtil;

public class EndingMethodView extends ViewPart {

	public static final String ID = "swarm.manager.views.EndingMethodView";

	private TableViewer viewer;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;

	Developer developer;

	private Combo taskCombo;
	private List<Task> tasksToSelect;

	class NameSorter extends ViewerSorter {
	}

	public EndingMethodView() {
	}

	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout(4, false);
		parent.setLayout(layout);

		Label projectLabel = new Label(parent, SWT.NONE);
		projectLabel.setText("Task ");
		taskCombo = new Combo(parent, SWT.BORDER | SWT.SEARCH | SWT.READ_ONLY);
		populateTaskCombo();

		taskCombo.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

		Label searchLabel = new Label(parent, SWT.NONE);
		searchLabel.setText("Search ");
		final Text searchText = new Text(parent, SWT.BORDER | SWT.SEARCH);
		searchText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		
		
		taskCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				try {
					Task task = new Task();
					int taskIndex = taskCombo.getSelectionIndex();
					task = tasksToSelect.get(taskIndex);
					List<Method> methods = getMethods(searchText, task);	
					
					viewer.setInput(methods.toArray());
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
						Task task = new Task();
						int taskIndex = taskCombo.getSelectionIndex();
						task = tasksToSelect.get(taskIndex);
						List<Method> methods = getMethods(searchText, task);						
						
						viewer.setInput(methods.toArray());
						
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

	private void populateTaskCombo() {
		if (developer != null) {
			List<Session> sessions = SessionService.getAll();
			tasksToSelect = new ArrayList<Task>();
			List<String> taskNames = new ArrayList<String>();

			for (int i = 0; i < sessions.size(); i++) {
				if(sessions.get(i).getDeveloper().getId() == developer.getId()) {
					tasksToSelect.add(sessions.get(i).getTask());
					taskNames.add(sessions.get(i).getTask().getTitle().toString());
				}
			}
			
			String[] itemsArray = new String[taskNames.size()];
		    itemsArray = taskNames.toArray(itemsArray);

			taskCombo.setItems(itemsArray);
		}
	}

	private void createViewer(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		createColumns(parent, viewer);
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

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

	private void createColumns(final Composite parent, final TableViewer viewer) {
		String[] titles = { "Type", "Method Name", "Key" };
		int[] bounds = { 500, 250, 200 };

		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0], 0);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Method m = (Method) element;
				return m.getType().getFullName();
			}
		});

		// second column is for the last name
		col = createTableViewerColumn(titles[1], bounds[1], 1);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Method m = (Method) element;
				return m.getName();
			}
		});

		// now the gender
		col = createTableViewerColumn(titles[2], bounds[2], 2);
		col.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Method m = (Method) element;
				return m.getKey();
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
				EndingMethodView.this.fillContextMenu(manager);
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
				
				if(obj instanceof Method) {
					Method method = (Method) obj;
					IJavaProject javaProject;
					try {
						Task taskSelected = tasksToSelect.get(taskCombo.getSelectionIndex());
						List<Session> sessions = new ArrayList<Session>();
						sessions = SessionService.getSessions(taskSelected);
						String projectName = sessions.get(0).getProject().toString();
						
						javaProject = WorkbenchUtil.getProjectByName(projectName);
						
						IType javaType = javaProject.findType(method.getType().getFullName());
						
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

	private List<Method> getMethods(final Text searchText, Task task) {
		List<Method> methods = new ArrayList<Method>();
		if(searchText.getText().trim().isEmpty()) {
			methods.addAll(TaskService.getEndingMethods(task));
		} else {
			for(Method method : TaskService.getEndingMethods(task)) {
				if(method.toString().toLowerCase().contains(searchText.getText().toLowerCase())) {
					methods.add(method);
				}
			}
		}
		return methods;
	}
}