package swarm.manager.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class NewSessionDialog extends TitleAreaDialog {

	private Text labelText;
	private Text descriptionText;

	private List<Button> buttons = new ArrayList<>();

	
	private String label;
	private String description;
	private String purpose;

	public NewSessionDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public void create() {
		super.create();
		setTitle("New Swarm Debugging session");
		setMessage("Please, fill these form to create a new session",
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

		createLabel(container);
		createPurpose(container);
		createDescription(container);

		return area;
	}

	private void createLabel(Composite container) {
		Label sessionLabel = new Label(container, SWT.NONE);
		sessionLabel.setText("Session label");

		GridData dataLabel = new GridData();
		dataLabel.grabExcessHorizontalSpace = true;
		dataLabel.horizontalAlignment = GridData.FILL;

		labelText = new Text(container, SWT.BORDER);
		labelText.setLayoutData(dataLabel);
		Label space = new Label(container, SWT.NONE);
		space.setText("");
	}

	private void createPurpose(Composite container) {
		Label sessionLabel = new Label(container, SWT.NONE);
		sessionLabel.setText("Session purpose");

		Composite radioContainer = new Composite(container, SWT.NONE);
		radioContainer.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(2, false);
		radioContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		radioContainer.setLayout(layout);

		String[] purposes = new String[] {"New feature", "Refactoring", "Fix bug", "Other", "Comprehension" };
		for (String purpose : purposes) {
			Button button = new Button (radioContainer, SWT.RADIO);
			button.setText (purpose);
			buttons.add(button);
		}
		Label space = new Label(container, SWT.NONE);
		space.setText("");
	}
	
	private void createDescription(Composite container) {
		Label descriptionLabel = new Label(container, SWT.NONE);
		descriptionLabel.setText("Description");

		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;		
		descriptionText = new Text(container, SWT.MULTI | SWT.BORDER);
		data.heightHint = 5 * descriptionText.getLineHeight();		
		descriptionText.setLayoutData(data);
		
		//GORM Session Description limited -> varchar(10000)
		descriptionText.setTextLimit(10000);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}


	private boolean saveInput() {
		label = labelText.getText();
		
		if(label != null && label.length() > 0) {
			description = descriptionText.getText();
			
			for (Button button : buttons) {
				if(button.getSelection()) {
					purpose = button.getText();
				}
			}
			return true;
		} else {
			MessageDialog.openWarning(this.getShell(), "Swarm Debugging", "Please, fill the label field.");
			return false;
		}
	}

	@Override
	protected void okPressed() {
		if(saveInput()) {
			super.okPressed();
		}
	}


	public String getLabel() {
		return label;
	}

	public String getDescription() {
		return description;
	}

	public String getPurpose() {
		return purpose;
	}
}