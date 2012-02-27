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
	public static String dueDateStyle(Date date1, Date date2, boolean logbook) {
		long diffInMillis = date2.getTime() - date1.getTime();
		long diffInDays  = diffInMillis/1000/86400;
		String paddingtop = ""; 
		
		if (logbook){
			paddingtop = "padding-top: 7.5px;";
		}else{
			paddingtop = "padding-top: 4.5px;";
		}
		
		if ((diffInDays < 0) || (diffInMillis < 0)){
			return "color: #666666; "+paddingtop;
		}else{		
			if (diffInDays > 3) {
				return "color: black; "+paddingtop;
			} else {
				return "color: red; "+paddingtop;
			}
		}
	}	

	public static String dueDateStyle(Date date1, Date date2) {
		long diffInMillis = date2.getTime() - date1.getTime();
		long diffInDays  = diffInMillis/1000/86400;
		
		if ((diffInDays < 0) || (diffInMillis < 0)){
			return "color: #666666; padding-top: 4.5px;";
		}else{		
			if (diffInDays > 3) {
				return "color: black; padding-top: 4.5px;";
			} else {
				return "color: red; padding-top: 4.5px;";
			}
		}
	}	
		
	
	

	public static String longDateFormat(Date date) {
		return DateTimeFormat.getFormat("E d MMM h:mm:ss a").format(date);
	}
}
