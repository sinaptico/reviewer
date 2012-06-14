package au.edu.usyd.reviewer.client.assignment;

import java.util.Collection;

import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.User;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The Interface AssignmentService (Asynchronous) used to manage user activities (Writing and Reviewing), submit documents and update password for non wasm users.
 * 
 */
interface AssignmentServiceAsync {
	
	/**
	 * Gets the user activities for the semester and year given.
	 *
	 * @param semester the semester
	 * @param year the year
	 * @param asyncCallback the callback
	 */
	public void getUserActivities(int semester, int year,AsyncCallback<Collection<Course>> asyncCallback);

	/**
	 * Gets the user reviewing tasks for the semester and year given.
	 *
	 * @param semester the semester
	 * @param year the year
	 * @param includeFinishedReviews the include finished reviews
	 * @param asyncCallback the callback
	 */
	public void getUserReviewingTasks(int semester, int year,Boolean includeFinishedReviews, AsyncCallback<Collection<Course>> asyncCallback);

	/**
	 * Gets the user writing tasks for the semester and year given.
	 *
	 * @param semester the semester
	 * @param year the year
	 * @param asyncCallback the callback
	 */
	public void getUserWritingTasks(int semester, int year, AsyncCallback<Collection<Course>> asyncCallback);

	/**
	 * Submit doc entry.
	 *
	 * @param <D> the generic type
	 * @param docEntry the doc entry
	 * @param asyncCallback the callback
	 */
	public <D extends DocEntry> void submitDocEntry(D docEntry, AsyncCallback<D> asyncCallback);

	/**
	 * Update doc entry.
	 *
	 * @param <D> the generic type
	 * @param docEntry the doc entry
	 * @param asyncCallback the callback
	 */
	public <D extends DocEntry> void updateDocEntry(D docEntry, AsyncCallback<D> asyncCallback);

	/**
	 * Gets the user details.
	 *
	 * @param callback the callback
	 */
	public void getUserDetails(AsyncCallback<User> callback);

	/**
	 * Update user password.
	 *
	 * @param user the user
	 * @param newPassword the new password
	 * @param asyncCallback the callback
	 */
	public void updateUserPassword(User user, String newPassword, AsyncCallback<User> asyncCallback);
}
