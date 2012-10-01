package au.edu.usyd.reviewer.client.assignment;

import java.util.Collection;

import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.User;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * <p>The Interface AssignmentService used to manage user activities (Writing 
 * and Reviewing), submit documents and update password for non wasm users.</p>
 * 
 */
@RemoteServiceRelativePath("assignmentService")
public interface AssignmentService extends RemoteService {
	
	/**
	 * Gets the user activities.
	 *
	 * @param semester the semester
	 * @param year the year
	 * @return the user activities
	 * @throws Exception the exception
	 */
	public Collection<Course> getUserActivities(int semester, int year, Long organizationId) throws Exception;

	/**
	 * Gets the user reviewing tasks for the semester and year given.
	 *
	 * @param semester the semester
	 * @param year the year
	 * @param includeFinishedReviews the include finished reviews
	 * @return the user reviewing tasks
	 * @throws Exception the exception
	 */
	public Collection<Course> getUserReviewingTasks(int semester, int year, Boolean includeFinishedReviews, Long organizationId) throws Exception;

	/**
	 * Gets the user writing tasks for the semester and year given.
	 *
	 * @param semester the semester
	 * @param year the year
	 * @return the user writing tasks
	 * @throws Exception the exception
	 */
	public Collection<Course> getUserWritingTasks(int semester, int year,  Long organizationId) throws Exception;

	/**
	 * Submit doc entry.
	 *
	 * @param <D> the generic type
	 * @param docEntry the doc entry
	 * @return the docEntry
	 * @throws Exception the exception
	 */
	public <D extends DocEntry> D submitDocEntry(D docEntry) throws Exception;

	/**
	 * Update doc entry.
	 *
	 * @param <D> the generic type
	 * @param docEntry the doc entry
	 * @return the docEntry
	 * @throws Exception the exception
	 */
	public <D extends DocEntry> D updateDocEntry(D docEntry) throws Exception;
	
	/**
	 * Gets the user details.
	 *
	 * @return the user details
	 * @throws Exception the exception
	 */
	public User getUserDetails() throws Exception;
	
	/**
	 * Update user password.
	 *
	 * @param user the user
	 * @param newPassword the new password
	 * @return the user
	 * @throws Exception the exception
	 */
	public User updateUserPassword(User user, String newPassword) throws Exception;
	
	/** 
	 * Return all the organization
	 * @return collection of organizations
	 * @throws Exception the exception
	 */
	public Collection<Organization> getAllOrganizations() throws Exception;
}
