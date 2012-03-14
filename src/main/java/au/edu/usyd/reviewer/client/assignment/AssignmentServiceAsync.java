package au.edu.usyd.reviewer.client.assignment;

import java.util.Collection;

import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.User;

import com.google.gwt.user.client.rpc.AsyncCallback;

interface AssignmentServiceAsync {
	public void getUserActivities(int semester, int year,AsyncCallback<Collection<Course>> asyncCallback);

	public void getUserReviewingTasks(int semester, int year,Boolean includeFinishedReviews, AsyncCallback<Collection<Course>> asyncCallback);

	public void getUserWritingTasks(int semester, int year, AsyncCallback<Collection<Course>> asyncCallback);

	public <D extends DocEntry> void submitDocEntry(D docEntry, AsyncCallback<D> asyncCallback);

	public <D extends DocEntry> void updateDocEntry(D docEntry, AsyncCallback<D> asyncCallback);

	public void getUserDetails(AsyncCallback<User> callback);

	public void updateUserPassword(User user, String newPassword, AsyncCallback<User> asyncCallback);
}
