package au.edu.usyd.reviewer.client.core.util;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * <p>Class with String format methods; Includes:</p>
 * <ul>
 * 	<li>Date format.</li>  
 *  <li>'Due' date format and css style.</li>  
 *  <li>'Submit' date format.</li>
 * </ul>
 */
public class StyleLib {

	/**
	 * Date format.
	 *
	 * @param date the date
	 * @return the string
	 */
	public static String dateFormat(Date date) {
		return DateTimeFormat.getFormat("dd/MM/yyyy h:mm a").format(date);
	}

	/**
	 * Due date format.
	 *
	 * @param date the date
	 * @return the string
	 */
	public static String dueDateFormat(Date date) {
		String formatedDate = DateTimeFormat.getFormat("E d MMM - h:mm a").format(date);
		formatedDate = formatedDate.replace("PM", "pm");
		formatedDate = formatedDate.replace("AM", "am");
		return formatedDate;
	}
	
	/**
	 * Submit date format.
	 *
	 * @param date the date
	 * @return the string
	 */
	public static String submitDateFormat(Date date) {
		String formatedDate = DateTimeFormat.getFormat("MMMM d").format(date);
		return formatedDate;
	}	

	/**
	 * This method determines the style of an Activity due date based on the
	 * amount of time remaining until the specified deadline.
	 *
	 * @param date1 the date1
	 * @param deadline the deadline
	 * @param logbook the logbook
	 * @return the string
	 */
	public static String dueDateStyle(Date date1, Date deadline, boolean logbook) {
		long diffInMillis = deadline.getTime() - date1.getTime();
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

	/**
	 * Due date style.
	 *
	 * @param date1 the date1
	 * @param deadline the deadline
	 * @return the string
	 */
	public static String dueDateStyle(Date date1, Date deadline) {
		long diffInMillis = deadline.getTime() - date1.getTime();
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
	

	/**
	 * Long date format.
	 *
	 * @param date the date
	 * @return the string
	 */
	public static String longDateFormat(Date date) {
		return DateTimeFormat.getFormat("E d MMM h:mm:ss a").format(date);
	}
}
