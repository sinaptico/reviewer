package au.edu.usyd.reviewer.client.login;

//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.StringUtil;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * <p>Class with the login fields for non-UniKey access.</p>
 */
public class LoginForm extends Composite {
	
	/** TextBox with email  */
	private TextBox username = new TextBox();
	
	/** Password TextBox. */
	private PasswordTextBox password = new PasswordTextBox();
	
	/** Label for error messages. */
	private Label errorLabel = new Label();
	
	/** The login button. */
	private Button loginButton = new Button("Log in");

	/**
	 * Instantiates a new login form.
	 */
	public LoginForm() {

		Grid grid = new Grid(4, 2);
		grid.setWidget(0, 1, errorLabel);
		grid.setWidget(1, 0, new Label("Login name:"));
		grid.setWidget(1, 1, username);
		grid.setWidget(2, 0, new Label("Password:"));
		grid.setWidget(2, 1, password);
		grid.setWidget(3, 1, loginButton);

		username.setName("j_username");
		password.setName("j_password");
		errorLabel.setStyleName("error");

		final FormPanel form = new FormPanel();
		form.setAction("j_security_check");
		form.setMethod(FormPanel.METHOD_POST);
		form.setWidget(grid);
		form.addSubmitCompleteHandler(new SubmitCompleteHandler() {
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {
				Window.Location.reload();
			}
		});

		loginButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (validateLogin(username.getText(), password.getText())) {
					form.submit();
				}
			}
		});
		initWidget(form);
	}

	/**
	 * Validates login.
	 *
	 * @param email the email of the user
	 * @param password the password of the user
	 * @return true, if successful
	 */
	private boolean validateLogin(String username, String password) {
		if (StringUtil.isNotBlank(username) && StringUtil.isNotBlank(password)) {
			errorLabel.setText("");
			return true;
		} else {
			errorLabel.setText(Constants.EXCEPTION_INVALID_LOGIN);
			return false;
		}
	}
	
}