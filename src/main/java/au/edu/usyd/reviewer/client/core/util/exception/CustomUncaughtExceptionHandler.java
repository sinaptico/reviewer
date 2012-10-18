package au.edu.usyd.reviewer.client.core.util.exception;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.user.client.Window;

/**
 * Uncaught exception handler
 * @author mdagrace
 *
 */
public class CustomUncaughtExceptionHandler implements UncaughtExceptionHandler {

	private String ERROR_TITLE = "Error";
	private static String ERROR_MESSAGE = "There was an error in the application. Please try again. ";
		
	@Override
	public void onUncaughtException( Throwable e ) {
		// Get rid of UmbrellaException
	    Throwable exceptionToDisplay = getExceptionToDisplay( e );
	    Window.setTitle(ERROR_TITLE);
	    Window.alert( exceptionToDisplay.getMessage());
	}
	
	private static Throwable getExceptionToDisplay( Throwable throwable ) {
		Throwable result = throwable;
		if (throwable instanceof UmbrellaException && ((UmbrellaException) throwable).getCauses().size() == 1) {
			result = new Throwable(ERROR_MESSAGE);
		}
		GWT.log(throwable.getMessage(), throwable);
		return result;
	}
}
