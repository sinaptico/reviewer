package au.edu.usyd.reviewer.client.core.util;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

public class StyleLib {

	public static String dateFormat(Date date) {
		return DateTimeFormat.getFormat("dd/MM/yyyy h:mm a").format(date);
	}

	public static String dueDateFormat(Date date) {
		String formatedDate = DateTimeFormat.getFormat("E d MMM - h:mm a").format(date);
		formatedDate = formatedDate.replace("PM", "pm");
		formatedDate = formatedDate.replace("AM", "am");
		return formatedDate;
	}
	
	public static String submitDateFormat(Date date) {
		String formatedDate = DateTimeFormat.getFormat("MMMM d").format(date);
		return formatedDate;
	}	

	/**
	 * This method determines the style of an Activity due date based on the
	 * amount of time remaining until the specified deadline.
	 */
	public static String dueDateStyle(int status, int deadline) {
		if (status < deadline) {
			return "color: green; padding-top: 4.5px;";
		} else {
			return "color: red; padding-top: 4.5px;";
		}
	}

	public static String longDateFormat(Date date) {
		return DateTimeFormat.getFormat("E d MMM h:mm:ss a").format(date);
	}
}
