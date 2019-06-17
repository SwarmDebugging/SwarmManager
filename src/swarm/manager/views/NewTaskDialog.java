package swarm.manager.views;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class NewTaskDialog extends TitleAreaDialog {

	private Text nameText;
	private Text colorText;
	private Text titleText;
	private Text urlText;
	
	private String name;
	private String color;
	private String title;
	private String url;

	public NewTaskDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public void create() {
		super.create();
		setTitle("New Swarm Debugging product");
		setMessage("Please, fill in all the information to create a new task",
				IMessageProvider.INFORMATION);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(1, false);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 10, 10));
		container.setLayout(layout);

		createTitle(container);
		createColor(container);
		createUrl(container);
		createName(container);

		return area;
	}

	private void createName(Composite container) {
		Label productName = new Label(container, SWT.NONE);
		productName.setText("Product name");

		GridData dataLabel = new GridData();
		dataLabel.grabExcessHorizontalSpace = true;
		dataLabel.horizontalAlignment = GridData.FILL;

		nameText = new Text(container, SWT.BORDER);
		nameText.setLayoutData(dataLabel);
		Label space = new Label(container, SWT.NONE);
		space.setText("");
	}
	
	private void createColor(Composite container) {
		Label taskColor = new Label(container, SWT.NONE);
		taskColor.setText("Task color");

		GridData dataLabel = new GridData();
		dataLabel.grabExcessHorizontalSpace = true;
		dataLabel.horizontalAlignment = GridData.FILL;

		colorText = new Text(container, SWT.BORDER);
		colorText.setLayoutData(dataLabel);
		Label space = new Label(container, SWT.NONE);
		space.setText("");
	}
	
	private void createTitle(Composite container) {
		Label taskTitle = new Label(container, SWT.NONE);
		taskTitle.setText("Task title");

		GridData dataLabel = new GridData();
		dataLabel.grabExcessHorizontalSpace = true;
		dataLabel.horizontalAlignment = GridData.FILL;

		titleText = new Text(container, SWT.BORDER);
		titleText.setLayoutData(dataLabel);
		Label space = new Label(container, SWT.NONE);
		space.setText("");
	}
	
	private void createUrl(Composite container) {
		Label taskUrl = new Label(container, SWT.NONE);
		taskUrl.setText("Task url");

		GridData dataLabel = new GridData();
		dataLabel.grabExcessHorizontalSpace = true;
		dataLabel.horizontalAlignment = GridData.FILL;

		urlText = new Text(container, SWT.BORDER);
		urlText.setLayoutData(dataLabel);
		Label space = new Label(container, SWT.NONE);
		space.setText("");
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	// ARRUMAR
	private boolean saveInput() {
		name = nameText.getText();
		color = colorText.getText();
		title = titleText.getText();
		url = urlText.getText();
		
		if(name != null && name.length() > 0) {
			return true;
		} else {
			MessageDialog.openWarning(this.getShell(), "Swarm Debugging", "Please, fill the name field.");
			return false;
		}
	}

	@Override
	protected void okPressed() {
		if(saveInput()) {
			super.okPressed();
		}
	}


	public String getName() {
		return name;
	}
	
	public String getColor() {
		return color;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getUrl() {
		return url;
	}

}