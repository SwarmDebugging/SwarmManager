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
import swarm.core.domain.Project;
import swarm.core.domain.Session;
import swarm.core.services.SessionService;

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
		private List<Project> projects = new ArrayList<Project>();

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
				projects.clear();
				projects.addAll(developer.getProjects());
			} else if(inputElement instanceof Project) {
				projects.add((Project) inputElement);
			}

			return projects.toArray();
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof Project) {
				//Populate project sessions
				Project p = (Project) parentElement;
				return SessionService.getSessions(p, developer).toArray();
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
			if (element instanceof Project) {
				Project p = (Project) element;
				return SessionService.getSessions(p, developer).size() > 0;
			}
			return false;
		}
	}

	class ViewLabelProvider extends LabelProvider {

		public String getText(Object item) {
			return item.toString();
		}

		public Image getImage(Object item) {
			if (item instanceof Project) {
				return Images.PROJECT;
			} else if (item instanceof Session) {
				return Images.SESSION;
			} else {
				return Images.ECLIPSE;
			}
		}
	}
}