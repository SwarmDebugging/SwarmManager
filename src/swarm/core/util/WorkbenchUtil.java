package swarm.core.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.ITextEditor;

public class WorkbenchUtil {
	
	public static void moveToLineInEditor(int line) throws Exception {
		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();

		if (editor instanceof ITextEditor) {
		    ITextEditor textEditor = (ITextEditor) editor ;
		    IDocument document= textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
		    textEditor.selectAndReveal(document.getLineOffset(line), document.getLineLength(line));
		}			
	}

	public static ImageDescriptor getImageDescriptor(String imageName) {
		if (imageName == null) {
			throw new IllegalArgumentException("Image name can not be null");
		}

		return AbstractUIPlugin.imageDescriptorFromPlugin("SoftwarePathfinder", "$nl$/icons/" + imageName);
	}

	public static IJavaProject getSelectedProject() throws Exception {
		IJavaProject project = null;
		IWorkbench workbench = PlatformUI.getWorkbench();

		ISelectionService ss = workbench.getActiveWorkbenchWindow().getSelectionService();
		String projExpIDs[] = { "org.eclipse.ui.navigator.ProjectExplorer", "org.eclipse.jdt.ui.PackageExplorer" };

		for (String projExpID : projExpIDs) {
			ISelection selection = ss.getSelection(projExpID);

			if (selection != null && selection instanceof TreeSelection) {
				Object selectProject = ((TreeSelection) selection).getFirstElement();

				if (selectProject instanceof IJavaProject) {
					project = (IJavaProject) selectProject;
					
				} else if (selectProject instanceof IProject) {
					IProject iProject = (IProject) selectProject;
					try {
						if (iProject.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
							project = JavaCore.create(iProject);
						}
					} catch (CoreException e) {
						throw new Exception("This selected project don't is a Java Project.");
					}
				}
				break;
			}
		}

		return project;
	}

	public static void openEditor(IJavaElement element) throws Exception {
			JavaUI.openInEditor(element, true, true);
	}

	public static IViewPart showView(String id) throws Exception {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(id);
	}

	public static IJavaProject getProjectByName(String name) throws Exception {
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject iProject : projects) {
			if(iProject.getName().equals(name)) {
				if (iProject.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
					return JavaCore.create(iProject);
				}
			}
		}
		return null;
	}
}