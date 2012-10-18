package au.edu.usyd.reviewer.server.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

public class CalendarUtil {

	
	public static Collection<Integer> getYears(){
		Collection<Integer> years = new ArrayList<Integer>();
		Calendar today = Calendar.getInstance();
		int  todayYear = today.get(Calendar.YEAR);
		years.add(new Integer(todayYear--));
		years.add(new Integer(todayYear--));
		years.add(new Integer(todayYear--));
		years.add(new Integer(todayYear--));
		years.add(new Integer(todayYear--));
		years.add(new Integer(todayYear--));
		return years;
	}
}
