package au.edu.usyd.reviewer.server.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.WritingActivity;

public class CloneUtilUnitTest {
	
	private class MockSet<E> extends HashSet<E>{};
	private class MockList<E> extends ArrayList<E>{};
	
	@Test 
	public void shouldCloneCollection() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IntrospectionException, InstantiationException {
		// setup courses
		WritingActivity writingActivity = new WritingActivity();
		writingActivity.setName("activity");
		writingActivity.setGroups(true);
		writingActivity.setStartDate(new Date());
		writingActivity.setEntries(new MockSet<DocEntry>());
		Set<WritingActivity> writingActivities = new MockSet<WritingActivity>();
		writingActivities.add(writingActivity);
		Course course = new Course();
		course.setName("course");
		course.setSemester(3);
		course.setYear(99);
		course.setWritingActivities(writingActivities);
		List<Course> courses = new MockList<Course>();
		courses.add(course);
		
		// clone courses
		List<Course> cloneCourses = CloneUtil.clone(courses);
		
		// check courses
		assertThat(cloneCourses instanceof MockList<?>, is(false));
		Course cloneCourse = cloneCourses.iterator().next();
		assertThat(cloneCourse, equalTo(course));
		assertThat(cloneCourse.getName(), equalTo(course.getName()));
		assertThat(cloneCourse.getSemester(), equalTo(course.getSemester()));
		assertThat(cloneCourse.getYear(), equalTo(course.getYear()));
		assertThat(cloneCourse.getWritingActivities() instanceof MockSet<?>, is(false));
		WritingActivity cloneActivity = cloneCourse.getWritingActivities().iterator().next();
		assertThat(cloneActivity, equalTo(writingActivity));
		assertThat(cloneActivity.getName(), equalTo(writingActivity.getName()));
		assertThat(cloneActivity.getGroups(), equalTo(writingActivity.getGroups()));
		assertThat(cloneActivity.getStartDate(), equalTo(writingActivity.getStartDate()));
		assertThat(cloneActivity.getEntries() instanceof MockSet<?>, is(false));
	}
}
