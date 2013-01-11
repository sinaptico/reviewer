package au.edu.usyd.reviewer.client.login;
        
import com.google.gwt.core.client.EntryPoint;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * <p>Starting point of the {@link LoginForm Login Form} (for non-UniKey access).</p> 
 */
public class LoginEntryPoint implements EntryPoint {

	/**
	 * Method that loads the login form.
	 */
	@Override
	public void onModuleLoad() {

		LoginForm loginForm = new LoginForm();
		VerticalPanel loginPanel = new VerticalPanel();
		loginPanel.add(new HTML("<h1>Authentication Required</h1>"));
		loginPanel.add(loginForm);
		RootPanel.get("loginPanel").add(loginPanel);
	}
}