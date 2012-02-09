package au.edu.usyd.reviewer.client.admin.glosser;

import com.google.gwt.libideas.validation.client.ErrorHandler;
import com.google.gwt.libideas.validation.client.Subject;

public class MyErrorHandler extends ErrorHandler {

	@Override
	public void reportError(String errorMessage) {

	}

	@Override
	public void reportError(Subject subject, String errorMessage) {
		super.reportError(subject, errorMessage);
		// subject.setValue(errorMessage);
	}

}
