package au.edu.usyd.reviewer.client.admin;

import au.edu.usyd.reviewer.client.core.Course;

import com.google.gwt.junit.client.GWTTestCase;

public class CourseFormGwtTest extends GWTTestCase {

    private CourseForm courseForm;

    @Override
    public void gwtSetUp() {
        courseForm = new CourseForm();
        courseForm.onLoad();
    }

    public void testGetAndSetCourse() {
//        Course course = new Course();
//        course.setId(new Long(1));
////        courseForm.setCourse(course);
//
////        Course actualCourse = courseForm.getCourse();
//        assertSame(actualCourse, actualCourse);
    }

    @Override
    public String getModuleName() {
        return "au.edu.usyd.reviewer.Admin";
    }
}
