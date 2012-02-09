package au.edu.usyd.reviewer.client.admin.glosser;

import com.google.gwt.libideas.validation.client.ErrorHandler;
import com.google.gwt.libideas.validation.client.Subject;
import com.google.gwt.libideas.validation.client.validator.BuiltInValidator;

public class SiteValidator extends BuiltInValidator {

	@Override
	public void checkValid(Subject subject, ErrorHandler handler) {

		// handler.reportError(subject, new ValidationException("Test"));
	}

}
