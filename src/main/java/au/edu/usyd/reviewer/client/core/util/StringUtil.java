package au.edu.usyd.reviewer.client.core.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import au.edu.usyd.reviewer.client.core.User;

public class StringUtil {

	public static Set<String> csvToStrings(String csv) {
		Set<String> strings = new HashSet<String>();
		for (String string : csv.split(",")) {
			strings.add(string.trim());
		}
		return strings;
	}

	public static Set<User> csvToUsers(String csv) {
		Set<User> users = new HashSet<User>();
		for (String row : csv.split("[\\n]+")) {
			String[] details = row.split(",");
			if (details.length == 4) {
				User user = new User();
				user.setId(details[0].trim());
				user.setFirstname(details[1].trim());
				user.setLastname(details[2].trim());
				user.setEmail(details[3].trim());
				users.add(user);
			}
		}
		return users;
	}

	public static boolean isBlank(String s) {
		return s == null || s.trim().isEmpty();
	}

	public static boolean isNotBlank(String s) {
		return !isBlank(s);
	}

	public static String stringsToCsv(Collection<String> strings) {
		String csv = new String();
		for (String string : strings) {
			if (!csv.equals(new String()))
				csv += ",";
			csv += string;
		}
		return csv;
	}

	public static String usersToCsv(Collection<User> users) {
		String csv = new String();
		for (User user : users) {
			if (!csv.equals(new String()))
				csv += "\n";
			csv += user.getId() + "," + user.getFirstname() + "," + user.getLastname() + "," + user.getEmail();
		}
		return csv;
	}
}
