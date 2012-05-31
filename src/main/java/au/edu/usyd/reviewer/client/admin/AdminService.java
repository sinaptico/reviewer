package au.edu.usyd.reviewer.client.admin;

import java.util.Collection;

import au.edu.usyd.reviewer.client.admin.report.UserStats;
import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Deadline;
import au.edu.usyd.reviewer.client.core.Grade;
import au.edu.usyd.reviewer.client.core.ReviewEntry;
import au.edu.usyd.reviewer.client.core.ReviewTemplate;
import au.edu.usyd.reviewer.client.core.ReviewingActivity;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.WritingActivity;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The Interface AdminService used to manage the courses, writing activities, reviews and review templates from the Administration module.
 */
@RemoteServiceRelativePath("adminService")
public interface AdminService extends RemoteService {
	
	/**
	 * Delete course.
	 *
	 * @param course the course
	 * @return the course
	 * @throws Exception the exception
	 */
	public Course deleteCourse(Course course) throws Exception;

	/**
	 * Delete writing activity.
	 *
	 * @param writingActivity the writing activity
	 * @return the writing activity
	 * @throws Exception the exception
	 */
	public WritingActivity deleteWritingActivity(WritingActivity writingActivity) throws Exception;

	/**
	 * Gets the courses.
	 *
	 * @param semester the semester
	 * @param year the year
	 * @return the courses
	 * @throws Exception the exception
	 */
	public Collection<Course> getCourses(Integer semester, Integer year) throws Exception;

	/**
	 * Gets the writing activity stats.
	 *
	 * @param writingActivityId the writing activity id
	 * @return the writing activity stats
	 * @throws Exception the exception
	 */
	public Collection<UserStats> getWritingActivityStats(Long writingActivityId) throws Exception;

	/**
	 * Mock user.
	 *
	 * @param user the user
	 * @return the user
	 * @throws Exception the exception
	 */
	public User mockUser(User user) throws Exception;

	/**
	 * Save course.
	 *
	 * @param course the course
	 * @return the course
	 * @throws Exception the exception
	 */
	public Course saveCourse(Course course) throws Exception;

	/**
	 * Save writing activity.
	 *
	 * @param courseId the course id
	 * @param writingActivity the writing activity
	 * @return the writing activity
	 * @throws Exception the exception
	 */
	public WritingActivity saveWritingActivity(Long courseId, WritingActivity writingActivity) throws Exception;
	
	/**
	 * Update grade.
	 *
	 * @param deadline the deadline
	 * @param userId the user id
	 * @param gradeValue the grade value
	 * @return the grade
	 * @throws Exception the exception
	 */
	public Grade updateGrade(Deadline deadline, String userId, Double gradeValue) throws Exception;
	
	/**
	 * Save review template.
	 *
	 * @param reviewTemplate the review template
	 * @return the review template
	 * @throws Exception the exception
	 */
	public ReviewTemplate saveReviewTemplate(ReviewTemplate reviewTemplate) throws Exception;
	
	/**
	 * Gets the review templates.
	 *
	 * @return the review templates
	 * @throws Exception the exception
	 */
	public Collection<ReviewTemplate> getReviewTemplates() throws Exception;	
	
	/**
	 * Delete review template.
	 *
	 * @param reviewTemplate the review template
	 * @return the review template
	 * @throws Exception the exception
	 */
	public ReviewTemplate deleteReviewTemplate(ReviewTemplate reviewTemplate) throws Exception;
	
	/**
	 * Update review doc entry.
	 *
	 * @param reviewEntryId the review entry id
	 * @param newDocEntryValue the new doc entry value
	 * @return the string
	 * @throws Exception the exception
	 */
	public String updateReviewDocEntry(String reviewEntryId, String newDocEntryValue) throws Exception;
	
	/**
	 * Gets the reviewing activity.
	 *
	 * @param reviewingActivityId the reviewing activity id
	 * @return the reviewing activity
	 * @throws Exception the exception
	 */
	public ReviewingActivity getReviewingActivity(Long reviewingActivityId) throws Exception;
	
	/**
	 * Delete review entry.
	 *
	 * @param reviewEntryId the review entry id
	 * @return the string
	 * @throws Exception the exception
	 */
	public String deleteReviewEntry(String reviewEntryId) throws Exception;
	
	/**
	 * Save new review entry.
	 *
	 * @param reviewingActivityId the reviewing activity id
	 * @param userId the user id
	 * @param docEntryId the doc entry id
	 * @return the review entry
	 * @throws Exception the exception
	 */
	public ReviewEntry saveNewReviewEntry(String reviewingActivityId, String userId, String docEntryId) throws Exception;	
}
