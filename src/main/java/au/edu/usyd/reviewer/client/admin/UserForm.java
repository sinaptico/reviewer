package au.edu.usyd.reviewer.client.admin;

import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.util.StringUtil;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UserForm extends Composite {

	private VerticalPanel mainPanel = new VerticalPanel();
	private final TextBox id = new TextBox();
	private final TextBox firstname = new TextBox();
	private final TextBox lastname = new TextBox();
	private final TextBox email = new TextBox();
	private final PasswordTextBox currentPassword = new PasswordTextBox();
	private final PasswordTextBox newPassword = new PasswordTextBox();
	private final PasswordTextBox newPassword2 = new PasswordTextBox();
	private final Label errorLabel = new Label();
	private final Label infoLabel = new Label();
	private User user = new User();

	public UserForm() {
		initWidget(mainPanel);
	}

	public User getUser() {
		user.setId(id.getValue());
		user.setFirstname(firstname.getValue());
		user.setLastname(lastname.getValue());
		user.setEmail(email.getValue());
		user.setPassword(currentPassword.getValue());
		return user;
	}

	@Override
	public void onLoad() {
		errorLabel.setStyleName("error");
		Grid grid = new Grid(9, 2);
		grid.setWidget(0, 0, new Label("ID:"));
		grid.setWidget(0, 1, id);
		grid.setWidget(1, 0, new Label("Firstname:"));
		grid.setWidget(1, 1, firstname);
		grid.setWidget(2, 0, new Label("Lastname:"));
		grid.setWidget(2, 1, lastname);
		grid.setWidget(3, 0, new Label("Email:"));
		grid.setWidget(3, 1, email);
		grid.setWidget(4, 0, new Label("Current password:"));
		grid.setWidget(4, 1, currentPassword);
		grid.setWidget(5, 0, new Label("New password:"));
		grid.setWidget(5, 1, newPassword);
		grid.setWidget(6, 1, new Label("* At least 5 characters."));
		grid.setWidget(7, 0, new Label("Retype new password:"));
		grid.setWidget(7, 1, newPassword2);		
		grid.setWidget(8, 1, errorLabel);		
		mainPanel.add(grid);
		mainPanel.add(new HTML("<br/>"));
	}

	public void setUser(User user) {
		this.user = user;
		id.setValue(user.getId());
		firstname.setValue(user.getFirstname());
		lastname.setValue(user.getLastname());
		email.setValue(user.getEmail());
	}
	
	public void disableNotUpdatableFileds(){
		id.setEnabled(false);
		firstname.setEnabled(false);
		lastname.setEnabled(false);
		email.setEnabled(false);
	}
	
	public boolean validateNewPassword() {
		if (StringUtil.isNotBlank(newPassword.getText()) && StringUtil.isNotBlank(newPassword2.getText())) {
			if (newPassword.getText().length() > 4) {
				if (newPassword.getText().equalsIgnoreCase(newPassword2.getText())) {
					errorLabel.setText("");
					return true;
				} else {
					errorLabel.setText("New paswords don't match.");
					return false;
				}				
			} else {
				errorLabel.setText("New password too short.");
				return false;
			}
		} else {
			errorLabel.setText("Please type your new password.");
			return false;
		}
	}	
	
	public boolean validateCurrentPassword() {
		if (StringUtil.isNotBlank(currentPassword.getText())) {
			errorLabel.setText("");
			return true;
		} else {
			errorLabel.setText("Please type your current password.");
			return false;
		}
	}

	public String getNewPassword() {
		return newPassword.getText();
	}	
}
