package au.edu.usyd.reviewer.client.login;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LoginEntryPoint implements EntryPoint {

	@Override
	public void onModuleLoad() {
		HTML info = new HTML();
		info.setHTML("This login page is for non-UniKey access to iWrite only. If you have a UniKey, please <a href='/reviewer/Assignments.html'>login using the university authentication system</a> instead.");

		LoginForm loginForm = new LoginForm();

		VerticalPanel loginPanel = new VerticalPanel();
		loginPanel.add(new HTML("<h1>Authentication Required</h1>"));
		loginPanel.add(info);
		loginPanel.add(loginForm);
		RootPanel.get("loginPanel").add(loginPanel);
	}
}