package swarm.manager.views;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import swarm.manager.Activator;

public class Images {

	public static final Image SESSION;
	public static final ImageDescriptor SESSION_DESCRIPTOR;
	
	public static final Image PROJECT;
	public static final ImageDescriptor PROJECT_DESCRIPTOR;

	public static final Image NEWPROJECT;
	public static final ImageDescriptor NEWPROJECT_DESCRIPTOR;

	public static final Image NEW;
	public static final ImageDescriptor NEW_DESCRIPTOR;	
	
	public static final Image LOGIN;
	public static final ImageDescriptor LOGIN_DESCRIPTOR;
	
	public static final Image OPEN;
	public static final ImageDescriptor OPEN_DESCRIPTOR;

	public static final Image RESTART;
	public static final ImageDescriptor RESTART_DESCRIPTOR;

	public static final Image STOP;
	public static final ImageDescriptor STOP_DESCRIPTOR;

	public static final Image REC;
	public static final ImageDescriptor REC_DESCRIPTOR;
	
	public static final Image ECLIPSE;
	public static final ImageDescriptor ECLIPSE_DESCRIPTOR;
	
	static {
		SESSION_DESCRIPTOR = getImageDescriptor("session_manager.png");
		SESSION = SESSION_DESCRIPTOR.createImage();
		
		NEWPROJECT_DESCRIPTOR = getImageDescriptor("newproject.gif");
		NEWPROJECT = NEWPROJECT_DESCRIPTOR.createImage();

		PROJECT_DESCRIPTOR = getImageDescriptor("project.gif");
		PROJECT = PROJECT_DESCRIPTOR.createImage();

		NEW_DESCRIPTOR = getImageDescriptor("new.gif");
		NEW = NEW_DESCRIPTOR.createImage();

		LOGIN_DESCRIPTOR = getImageDescriptor("login.gif");
		LOGIN = LOGIN_DESCRIPTOR.createImage();
		
		OPEN_DESCRIPTOR = getImageDescriptor("opentype.gif");
		OPEN = OPEN_DESCRIPTOR.createImage();

		RESTART_DESCRIPTOR = getImageDescriptor("start.gif");
		RESTART = RESTART_DESCRIPTOR.createImage();

		STOP_DESCRIPTOR = getImageDescriptor("stop.gif");
		STOP = STOP_DESCRIPTOR.createImage();

		REC_DESCRIPTOR = getImageDescriptor("rec.gif");
		REC = REC_DESCRIPTOR.createImage();
		
		ECLIPSE_DESCRIPTOR = getImageDescriptor("sample.gif");
		ECLIPSE = ECLIPSE_DESCRIPTOR.createImage();
		
	}
	
	public static ImageDescriptor getImageDescriptor(String imageName) {
		  if (imageName == null) {
		    throw new IllegalArgumentException("Image name can not be null");
		  }
		  
		  ImageDescriptor descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/" + imageName);
		  return descriptor;
	}
}