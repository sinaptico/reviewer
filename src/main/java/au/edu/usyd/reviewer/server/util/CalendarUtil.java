package au.edu.usyd.reviewer.server.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import edu.emory.mathcs.backport.java.util.Collections;

public class CalendarUtil {

	
	public static List<Integer> getYears(){
		List<Integer> years = new ArrayList<Integer>();
		Calendar today = Calendar.getInstance();
		int  todayYear = today.get(Calendar.YEAR);
		for(int i=0;i<5;i++){
			int year = new Integer(todayYear--);
			if (!years.contains(year)){
				years.add(year);
			}
		}
		for(int i=0;i<2;i++){
			int year = new Integer(todayYear++);
			if (!years.contains(year)){
				years.add(year);
			}
		}
		Comparator comparador = Collections.reverseOrder();
		Collections.sort(years, comparador);
		return years;
	}
	
	
	public static String convertDateWithTimeZone(Date date, String timeZone) {
		String sDate = "";
		try{
			DateFormat dateFormat = new SimpleDateFormat("E d MMM h:mma");
			dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
			sDate = dateFormat.format(date);
		} catch(Exception e){
			e.printStackTrace();
		}
		return sDate;
	}
}
