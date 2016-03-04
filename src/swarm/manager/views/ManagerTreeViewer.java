package swarm.manager.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import swarm.core.domain.Developer;
import swarm.core.domain.Session;
import swarm.core.domain.Task;
import swarm.core.services.SessionService;
import swarm.core.services.TaskService;

public class ManagerTreeViewer extends TreeViewer {

	public ManagerTreeViewer(Composite parent) {
		super(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		setContentProvider(new ContentProvider());
		setLabelProvider(new ViewLabelProvider());
		setSorter(new ViewerSorter());
	}

	class ContentProvider implements ITreeContentProvider {

		private final Object[] EMPTY_ARRAY = new Object[] {};
		private Developer developer;
		private List<Task> tasks = new ArrayList<Task>();

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if(inputElement instanceof Developer) {
				developer = (Developer) inputElement;
				tasks.clear();
				tasks.addAll(TaskService.getAll());
			} else if(inputElement instanceof Task) {
				tasks.add((Task) inputElement);
			}

			return tasks.toArray();
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof Task) {
				Task task = (Task) parentElement;
				return SessionService.getSessions(task, developer).toArray();
			} else {
				return EMPTY_ARRAY;
			}
		}

		@Override
		public Object getParent(Object element) {
			if(element instanceof Session) {
				((Session) element).getProject();
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if (element instanceof Task) {
				Task task  = (Task) element;
				return SessionService.getSessions(task, developer).size() > 0;
			}
			return false;
		}
	}

	class ViewLabelProvider extends LabelProvider {

		public String getText(Object item) {
			return item.toString();
		}

		public Image getImage(Object item) {
			if (item instanceof Task) {
				return Images.PROJECT;
			} else if (item instanceof Session) {
				return Images.SESSION;
			} else {
				return Images.ECLIPSE;
			}
		}
	}
}