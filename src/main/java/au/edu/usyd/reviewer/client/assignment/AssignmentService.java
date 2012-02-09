package au.edu.usyd.reviewer.client.assignment;

import java.util.Collection;

import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.User;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("assignmentService")
public interface AssignmentService extends RemoteService {
	public Collection<Course> getUserActivities(int semester, int year) throws Exception;

	public Collection<Course> getUserReviewingTasks(int semester, int year) throws Exception;

	public Collection<Course> getUserWritingTasks(int semester, int year) throws Exception;

	public <D extends DocEntry> D submitDocEntry(D docEntry) throws Exception;

	public <D extends DocEntry> D updateDocEntry(D docEntry) throws Exception;
	
	public User getUserDetails() throws Exception;
	
	public User updateUserPassword(User user, String newPassword) throws Exception;
}
