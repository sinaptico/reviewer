package au.edu.usyd.reviewer.client.admin;

import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.util.StringUtil;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * The User Form used for password and user details changes. It is also re-used for the Administration module to capture details of an user in order to mock its login in the system. 
 */
public class UserForm extends Composite {

	/** The main panel. */
	private VerticalPanel mainPanel = new VerticalPanel();
	
	/** TextBox with user's username. */
	private final TextBox username = new TextBox();
	
	/** TextBox with user's first name. */
	private final TextBox firstname = new TextBox();
	
	/** TextBox with user's last name. */
	private final TextBox lastname = new TextBox();
	
	/** TextBox with user's email. */
	private final TextBox email = new TextBox();
	
	/** PasswordTextBox with user's current password. */
	private final PasswordTextBox currentPassword = new PasswordTextBox();
	
	/** PasswordTextBox with user's new password. */
	private final PasswordTextBox newPassword = new PasswordTextBox();
	
	/** PasswordTextBox with user's repeated new password. */
	private final PasswordTextBox newPassword2 = new PasswordTextBox();
	
	/** The error label where validation messages are shown. */
	private final Label errorLabel = new Label();
	
	//private final Label infoLabel = new Label();
	
	/** The user that is managed by the form. */
	private User user = new User();

	private Label organizationLabel;
	private Organization organization;

	private boolean mockUser = false;

	
	private User loggedUser = null;
	
	/**
	 * Instantiates a new user form.
	 */
	public UserForm() {
		initWidget(mainPanel);
	}

	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	public User getUser() {
		user.setUsername(username.getValue());
		user.setFirstname(firstname.getValue());
		user.setLastname(lastname.getValue());
		user.setEmail(email.getValue());
		user.setOrganization(organization);
		user.setPassword(currentPassword.getValue());
		return user;
	}
	
	public String getUsername(){
		return username.getValue();
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.Widget#onLoad()
	 */
	@Override
	public void onLoad() {
		errorLabel.setStyleName("error");
		Grid grid = null;
		if (!isMockUser()){
			grid = new Grid(10, 2);
			grid.setWidget(0, 0, new Label("Username:"));
			grid.setWidget(0, 1, username);
			grid.setWidget(1, 0, new Label("Firstname:"));
			grid.setWidget(1, 1, firstname);
			grid.setWidget(2, 0, new Label("Lastname:"));
			grid.setWidget(2, 1, lastname);
			grid.setWidget(3, 0, new Label("Email:"));
			email.setWidth("250px");
			grid.setWidget(3, 1, email);
			grid.setWidget(4, 0, new Label("Organization:"));
			grid.setWidget(4, 1, organizationLabel);
			grid.setWidget(5, 0, new Label("Current password:"));
			grid.setWidget(5, 1, currentPassword);
			grid.setWidget(6, 0, new Label("New password:"));
			grid.setWidget(6, 1, newPassword);
			grid.setWidget(7, 1, new Label("* At least 5 characters."));
			grid.setWidget(8, 0, new Label("Retype new password:"));
			grid.setWidget(8, 1, newPassword2);		
			grid.setWidget(9, 1, errorLabel);
		} else { // if there is not mocked user then show a few fields to impersonate
			grid = new Grid(3, 2);
			grid.setWidget(0, 0, new Label("Username:"));
			username.setWidth("150px");
			grid.setWidget(0, 1, username);
			grid.setWidget(1, 0, new Label("Email:"));
			email.setWidth("250px");
			grid.setWidget(1, 1, email);
			grid.setWidget(2, 1, errorLabel);
		}
		mainPanel.add(grid);
		mainPanel.add(new HTML("<br/>"));
	}

	/**
	 * Sets the user.
	 *
	 * @param user the new user
	 */
	public void setUser(User user) {
		this.user = user;
		username.setText(user.getUsername());
		firstname.setValue(user.getFirstname());
		lastname.setValue(user.getLastname());
		email.setValue(user.getEmail());
		organizationLabel = new Label(user.getOrganization().getName());
	}
	
	/**
	 * Disable not updatable fileds.
	 */
	public void disableNotUpdatableFileds(){
//		username.setEnabled(false);
		firstname.setEnabled(false);
		lastname.setEnabled(false);
		email.setEnabled(false);
	}
	
	/**
	 * Validate new password.
	 *
	 * @return true, if successful
	 */
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
	
	/**
	 * Validate current password.
	 *
	 * @return true, if successful
	 */
	public boolean validateCurrentPassword() {
		if (StringUtil.isNotBlank(currentPassword.getText())) {
			errorLabel.setText("");
			return true;
		} else {
			errorLabel.setText("Please type your current password.");
			return false;
		}
	}

	/**
	 * Gets the new password.
	 *
	 * @return the new password
	 */
	public String getNewPassword() {
		return newPassword.getText();
	}
	
	public void setMockUser(boolean mock){
		this.mockUser = mock;
	}
	
	private boolean isMockUser(){
		return this.mockUser;
	}
	
	public void setLoggedUser(User aUser){
		this.loggedUser = aUser;
	}
}
