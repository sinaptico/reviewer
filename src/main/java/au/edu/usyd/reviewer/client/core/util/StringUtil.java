package au.edu.usyd.reviewer.client.core.util;

import java.util.Collection;


import java.util.HashSet;
import java.util.Set;

import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;


/**
 * <p>Class with String management methods; Includes:</p>
 * <ul>
 * 	<li>Conversion of csv lines into Set<String> and vice versa.</li>  
 *  <li>Conversion of csv lines into Users and vice versa.</li>  
 * </ul>  
 */
public class StringUtil {

	/**
	 * Conversion of csv lines into a Set of strings.
	 *
	 * @param csv the csv
	 * @return the sets the
	 */
	public static Set<String> csvToStrings(String csv) {
		Set<String> strings = new HashSet<String>();
		for (String string : csv.split(",")) {
			strings.add(string.trim());
		}
		return strings;
	}

	/**
	 * Conversion of csv lines into a Set of users, the lines should have the format: "id,name,surname,email".
	 *
	 * @param csv the csv
	 * @return the sets the
	 */
	public static Set<User> csvToUsers(String csv) throws MessageException {
		Set<User> users = new HashSet<User>();
		for (String row : csv.split("[\\n]+")) {
			String[] details = row.split(",");
			if (details.length == 4) {
				User user = new User();
//				user.setUsername(details[0].trim());
				user.setFirstname(details[1].trim());
				user.setLastname(details[2].trim());
				user.setEmail(details[3].trim());
				users.add(user);
			} else if (details.length !=  1 || (details.length == 1 && !StringUtil.isBlank(details[0].trim()))){
				throw new MessageException(Constants.EXCEPTION_COURSE_LECTURERS_TUTORS);
			}
		}
		return users;
	}

	/**
	 * Checks if a string is blank.
	 *
	 * @param s the s
	 * @return true, if is blank
	 */
	public static boolean isBlank(String s) {
		return s == null || s.trim().isEmpty();
	}

	/**
	 * Checks if a string is not blank.
	 *
	 * @param s the s
	 * @return true, if is not blank
	 */
	public static boolean isNotBlank(String s) {
		return !isBlank(s);
	}

	/**
	 * Strings to csv.
	 *
	 * @param strings the strings
	 * @return the string
	 */
	public static String stringsToCsv(Collection<String> strings) {
		String csv = new String();
		for (String string : strings) {
			if (!csv.equals(new String()))
				csv += ",";
			csv += string;
		}
		return csv;
	}

	/**
	 * Users to csv.
	 *
	 * @param users the users
	 * @return the string
	 */
	public static String usersToCsv(Collection<User> users) {
		String csv = new String();
		for (User user : users) {
			if (!csv.equals(new String()))
				csv += "\n";
			csv += user.getUsername() + "," + user.getFirstname() + "," + user.getLastname() + "," + user.getEmail();
		}
		return csv;
	}
	
	public static boolean stringToBool(String s) {
		if (s!= null && s.toUpperCase().equals(Constants.YES)){
			return true;
		} else { //if (s.equals("0"))
		    return false;
		}
	}

	public static boolean isValidateEmail(String email) throws MessageException {
		boolean isValidEmail = !StringUtil.isBlank(email);
		isValidEmail |= email.matches("[a-zA-Z0-9_.]*@[a-zA-Z]*.[a-zA-Z]*");
        return isValidEmail;
	}
}
