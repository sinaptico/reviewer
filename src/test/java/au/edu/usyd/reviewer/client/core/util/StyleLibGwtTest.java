package au.edu.usyd.reviewer.client.core.util;

import java.util.Date;

import au.edu.usyd.reviewer.client.core.Activity;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.client.core.Deadline;
import java.util.List;
import java.util.LinkedList;

import com.google.gwt.junit.client.GWTTestCase;

public class StyleLibGwtTest extends GWTTestCase {

    public void testDueDateStyle() {
        WritingActivity writingActivity = new WritingActivity();
        Date dateTime=new Date();

//        writingActivity.setStatus(Activity.STATUS_START);
//        String actualStyle1 = StyleLib.dueDateStyle(writingActivity.getStatus(), Activity.STATUS_FINISH);
//        assertEquals("color: green; padding-top: 3px;", actualStyle1);
//
//        writingActivity.setStatus(Activity.STATUS_FINISH);
//        String actualStyle2 = StyleLib.dueDateStyle(writingActivity.getStatus(), Activity.STATUS_FINISH);
//        assertEquals("color: red; padding-top: 3px;", actualStyle2);

      List<Deadline> deadlines = new LinkedList<Deadline>();
      Deadline deadline1 = new Deadline();
      deadline1.setFinishDate(new Date(0));
      deadlines.add(deadline1);
        
      writingActivity.setDeadlines(deadlines);
      String actualStyle2 = StyleLib.dueDateStyle(dateTime, writingActivity.getDeadlines().get(0).getFinishDate());
      assertEquals("color: #666666; padding-top: 4.5px;", actualStyle2);

//		"color: black; padding-top: 4.5px;";
//		"color: red; padding-top: 4.5px;";
    }
    
    public void testDueDateFormat() {
    	Date date = new Date(0);
    	assertEquals("Thu 1 Jan 10:00AM", StyleLib.dueDateFormat(date));
    }

    @Override
    public String getModuleName() {
        return "au.edu.usyd.reviewer.Core";
    }
}
