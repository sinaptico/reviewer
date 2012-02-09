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

import com.google.gwt.user.client.rpc.AsyncCallback;

interface AdminServiceAsync {
	public void deleteCourse(Course course, AsyncCallback<Course> callback);

	public void deleteWritingActivity(WritingActivity writingActivity, AsyncCallback<WritingActivity> callback);

	public void getCourses(AsyncCallback<Collection<Course>> callback);

	public void getWritingActivityStats(Long writingActivityId, AsyncCallback<Collection<UserStats>> callback);

	public void mockUser(User user, AsyncCallback<User> callback);

	public void saveCourse(Course course, AsyncCallback<Course> callback);

	public void saveWritingActivity(Long courseId, WritingActivity writingActivity, AsyncCallback<WritingActivity> callback);

	public void updateGrade(Deadline deadline, String userId, Double gradeValue, AsyncCallback<Grade> asyncCallback);

	public void saveReviewTemplate(ReviewTemplate reviewTemplate, AsyncCallback<ReviewTemplate> asyncCallback);
	
	public void getReviewTemplates(AsyncCallback<Collection<ReviewTemplate>> callback);

	public void deleteReviewTemplate(ReviewTemplate reviewTemplate, AsyncCallback<ReviewTemplate> callback);

	public void updateReviewDocEntry(String reviewEntryId, String newDocEntryValue, AsyncCallback<String> asyncCallback);
	
	public void getReviewingActivity(Long reviewingActivityId, AsyncCallback<ReviewingActivity> callback);
	
	public void deleteReviewEntry(String reviewEntryId, AsyncCallback<String> callback);
	
	public void saveNewReviewEntry(String reviewingActivityId, String userId, String docEntryId, AsyncCallback<ReviewEntry> asyncCallback);	
}
