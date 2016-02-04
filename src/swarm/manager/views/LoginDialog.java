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

import swarm.core.domain.Developer;

public class LoginDialog extends TitleAreaDialog {

	private Text userNameText;
	private Text passwordText;

	private String userName;
	private String password;
	private Developer developer;

	public LoginDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public void create() {
		super.create();
		setTitle("Swarm Debugging login");
		setMessage("Please, fill user name and password to connect on server.",
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

		createUserName(container);
		//createPassword(container);

		return area;
	}

	private void createUserName(Composite container) {
		Label userNameLabel = new Label(container, SWT.NONE);
		userNameLabel.setText("Username");

		GridData dataLabel = new GridData();
		dataLabel.grabExcessHorizontalSpace = true;
		dataLabel.horizontalAlignment = GridData.FILL;

		userNameText = new Text(container, SWT.BORDER);
		userNameText.setLayoutData(dataLabel);
		Label space = new Label(container, SWT.NONE);
		space.setText("");
	}

	private void createPassword(Composite container) {
		Label passwordLabel = new Label(container, SWT.NONE);
		passwordLabel.setText("Password");

		GridData dataLabel = new GridData();
		dataLabel.grabExcessHorizontalSpace = true;
		dataLabel.horizontalAlignment = GridData.FILL;

		passwordText = new Text(container, SWT.PASSWORD | SWT.BORDER);
		passwordText.setLayoutData(dataLabel);
	}

	@Override
	protected boolean isResizable() {
		return false;
	}


	private boolean saveInput() {
		userName = userNameText.getText();
		//password = passwordText.getText();
		
		if(userName != null && userName.length() > 0 ) { //&& password != null && password.length() > 0) {

			developer = Developer.login(userName);
			
			if(developer == null) {
				MessageDialog.openWarning(this.getShell(), "Swarm Debugging", userName + " login failed. ");
				return false;
			}

			return true;
		} else {
			MessageDialog.openWarning(this.getShell(), "Swarm Debugging", "Please, inform username and password fields.");
			return false;
		}
	}

	@Override
	protected void okPressed() {
		if(saveInput()) {
			super.okPressed();
		}
	}


	public Developer getLogged() {
		return developer;
	}
}