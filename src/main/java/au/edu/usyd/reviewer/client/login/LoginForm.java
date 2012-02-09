package au.edu.usyd.reviewer.client.login;

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

public class LoginForm extends Composite {
	private TextBox username = new TextBox();
	private PasswordTextBox password = new PasswordTextBox();
	private Label errorLabel = new Label();
	private Button loginButton = new Button("Log in");

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

	private boolean validateLogin(String usename, String password) {
		if (StringUtil.isNotBlank(usename) && StringUtil.isNotBlank(password)) {
			errorLabel.setText("");
			return true;
		} else {
			errorLabel.setText("Invalid username or password.");
			return false;
		}
	}
}