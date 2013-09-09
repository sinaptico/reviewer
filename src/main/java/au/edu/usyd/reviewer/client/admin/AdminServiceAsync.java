package au.edu.usyd.reviewer.client.admin;

import java.util.Collection;

import au.edu.usyd.reviewer.client.admin.report.UserStats;
import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Deadline;
import au.edu.usyd.reviewer.client.core.Grade;
import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.ReviewEntry;
import au.edu.usyd.reviewer.client.core.ReviewTemplate;
import au.edu.usyd.reviewer.client.core.ReviewingActivity;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.WritingActivity;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The Interface AdminService (Asynchronous) used to manage the courses, writing activities, reviews and review templates from the Administration module.
 */
interface AdminServiceAsync {
	
	/**
	 * Delete course.
	 *
	 * @param course the course
	 * @param callback the callback
	 */
	public void deleteCourse(Course course, AsyncCallback<Course> callback);

	/**
	 * Delete writing activity.
	 *
	 * @param writingActivity the writing activity
	 * @param callback the callback
	 */
	public void deleteWritingActivity(WritingActivity writingActivity, AsyncCallback<WritingActivity> callback);

	/**
	 * Gets the courses.
	 *
	 * @param semester the semester
	 * @param year the year
	 * @param callback the callback 
	 */
	public void getCourses(Integer semester, Integer year, Long organizationId, AsyncCallback<Collection<Course>> callback);

	/**
	 * Gets the writing activity stats.
	 *
	 * @param writingActivityId the writing activity id
	 * @param callback the callback
	 */
	public void getWritingActivityStats(Long writingActivityId, AsyncCallback<Collection<UserStats>> callback);

	/**
	 * Mock user.
	 *
	 * @param username the username of the user to mock
	 * @param callback the callback
	 */
	public void mockUser(User user, AsyncCallback<User> callback);

	/**
	 * Save course.
	 *
	 * @param course the course
	 * @param callback the callback
	 */
	public void saveCourse(Course course, AsyncCallback<Course> callback);

	/**
	 * Save writing activity.
	 *
	 * @param courseId the course id
	 * @param writingActivity the writing activity
	 * @param callback the callback
	 */
	public void saveWritingActivity(Long courseId, WritingActivity writingActivity, AsyncCallback<WritingActivity> callback);

	/**
	 * Update grade.
	 *
	 * @param deadline the deadline
	 * @param userId the user id
	 * @param gradeValue the grade value
	 * @param asyncCallback the async callback
	 */
	public void updateGrade(Deadline deadline, String userId, Double gradeValue, AsyncCallback<Grade> asyncCallback);

	/**
	 * Save review template.
	 *
	 * @param reviewTemplate the review template
	 * @param asyncCallback the async callback
	 */
	public void saveReviewTemplate(ReviewTemplate reviewTemplate, AsyncCallback<ReviewTemplate> asyncCallback);
	
	/**
	 * Gets the review templates.
	 *
	 * @param callback the callback
	 */
	public void getReviewTemplates(Long organizationId, AsyncCallback<Collection<ReviewTemplate>> callback);

	/**
	 * Delete review template.
	 *
	 * @param reviewTemplate the review template
	 * @param callback the callback
	 */
	public void deleteReviewTemplate(ReviewTemplate reviewTemplate, AsyncCallback<ReviewTemplate> callback);

	/**
	 * Update review doc entry.
	 *
	 * @param reviewEntryId the review entry id
	 * @param newDocEntryValue the new doc entry value
	 * @param asyncCallback the async callback
	 */
	public void updateReviewDocEntry(String reviewEntryId, String newDocEntryValue, AsyncCallback<String> asyncCallback);
	
	/**
	 * Gets the reviewing activity.
	 *
	 * @param reviewingActivityId the reviewing activity id
	 * @param callback the callback
	 */
	public void getReviewingActivity(Long reviewingActivityId, AsyncCallback<ReviewingActivity> callback);
	
	/**
	 * Delete review entry.
	 *
	 * @param reviewEntryId the review entry id
	 * @param callback the callback
	 */
	public void deleteReviewEntry(String reviewEntryId, AsyncCallback<String> callback);
	
	/**
	 * Save new review entry.
	 *
	 * @param reviewingActivityId the reviewing activity id
	 * @param userId the user id
	 * @param docEntryId the doc entry id
	 * @param asyncCallback the async callback
	 */
	public void saveNewReviewEntry(String reviewingActivityId, String userId, String docEntryId, AsyncCallback<ReviewEntry> asyncCallback);
	
	
	/**
	 * Returns logged user
	 * @param asyncCallback logged user
	 */
	public void getLoggedUser(AsyncCallback<User> asyncCallback);
	
	/**
	 * Return all the organizations 
	 * @param callback collection of organizations
	 */
	public void getOrganizations(AsyncCallback<Collection<Organization>> callback);

	/**
	 * Returns a collection of integers with the current year and 5 years ago.
	 * @param callback is the collection of integer
	 */
	public void getYears(AsyncCallback<Collection<Integer>> callback);
	
	
	public void logout(AsyncCallback<Void> callback);
	
	
	/**
	 * Share a review template with the user with the email received as parameter
	 */
	public void shareReviewTemplateWith(ReviewTemplate reviewTemplate, String email, AsyncCallback<ReviewTemplate> asyncCallback);
	
	/**
	 * Remove a user from the list of users that share the review template
	 */	public void noShareReviewTemplateWith(ReviewTemplate reviewTemplate, String email, AsyncCallback<ReviewTemplate> asyncCallback);

	 public void getGoogleAuthorizationUrl(String currentUrl, AsyncCallback<String> asyncCallback);
	 public void getUserTokens(String code,String state, String currentUrl, AsyncCallback<User> asyncCallback);
	 
}
