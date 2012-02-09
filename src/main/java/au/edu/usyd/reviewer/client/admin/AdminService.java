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
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("adminService")
public interface AdminService extends RemoteService {
	public Course deleteCourse(Course course) throws Exception;

	public WritingActivity deleteWritingActivity(WritingActivity writingActivity) throws Exception;

	public Collection<Course> getCourses() throws Exception;

	public Collection<UserStats> getWritingActivityStats(Long writingActivityId) throws Exception;

	public User mockUser(User user) throws Exception;

	public Course saveCourse(Course course) throws Exception;

	public WritingActivity saveWritingActivity(Long courseId, WritingActivity writingActivity) throws Exception;
	
	public Grade updateGrade(Deadline deadline, String userId, Double gradeValue) throws Exception;
	
	public ReviewTemplate saveReviewTemplate(ReviewTemplate reviewTemplate) throws Exception;
	
	public Collection<ReviewTemplate> getReviewTemplates() throws Exception;	
	
	public ReviewTemplate deleteReviewTemplate(ReviewTemplate reviewTemplate) throws Exception;
	
	public String updateReviewDocEntry(String reviewEntryId, String newDocEntryValue) throws Exception;
	
	public ReviewingActivity getReviewingActivity(Long reviewingActivityId) throws Exception;
	
	public String deleteReviewEntry(String reviewEntryId) throws Exception;
	
	public ReviewEntry saveNewReviewEntry(String reviewingActivityId, String userId, String docEntryId) throws Exception;	
}
