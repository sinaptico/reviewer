package au.edu.usyd.reviewer.client.core.util;

import java.util.Date;

import au.edu.usyd.reviewer.client.core.Activity;
import au.edu.usyd.reviewer.client.core.WritingActivity;

import com.google.gwt.junit.client.GWTTestCase;

public class StyleLibGwtTest extends GWTTestCase {

    public void testDueDateStyle() {
        WritingActivity writingActivity = new WritingActivity();

        writingActivity.setStatus(Activity.STATUS_START);
        String actualStyle1 = StyleLib.dueDateStyle(writingActivity.getStatus(), Activity.STATUS_FINISH);
        assertEquals("color: green; padding-top: 3px;", actualStyle1);

        writingActivity.setStatus(Activity.STATUS_FINISH);
        String actualStyle2 = StyleLib.dueDateStyle(writingActivity.getStatus(), Activity.STATUS_FINISH);
        assertEquals("color: red; padding-top: 3px;", actualStyle2);
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
