package au.edu.usyd.reviewer.client.admin;

import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.WritingActivity;

import com.google.gwt.junit.client.GWTTestCase;

public class ActivityFormGwtTest extends GWTTestCase {

    private ActivityForm activityForm;

    @Override
    public void gwtSetUp() {
        activityForm = new ActivityForm();
        activityForm.onLoad();
    }

    public void testGetAndSetActivity() {
        Course course = new Course();
        course.setId(new Long(1));
        WritingActivity writingActivity = new WritingActivity();
        writingActivity.setId(new Long(1));
        activityForm.setWritingActivityAndCourse(writingActivity, course);

        Course actualCourse = activityForm.getCourse();
        assertSame(course, actualCourse);

        WritingActivity actualActivity = activityForm.getWritingActivity();
        assertSame(writingActivity, actualActivity);
    }

    @Override
    public String getModuleName() {
        return "au.edu.usyd.reviewer.Admin";
    }
}
