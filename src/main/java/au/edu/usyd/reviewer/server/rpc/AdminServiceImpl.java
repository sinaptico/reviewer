package au.edu.usyd.reviewer.server.rpc;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.usyd.reviewer.client.admin.AdminService;
import au.edu.usyd.reviewer.client.admin.report.UserStats;
import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Deadline;
import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.Grade;
import au.edu.usyd.reviewer.client.core.ReviewEntry;
import au.edu.usyd.reviewer.client.core.ReviewTemplate;
import au.edu.usyd.reviewer.client.core.ReviewingActivity;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.UserGroup;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.server.AssignmentDao;
import au.edu.usyd.reviewer.server.AssignmentManager;
import au.edu.usyd.reviewer.server.Reviewer;
import au.edu.usyd.reviewer.server.report.UserStatsAnalyser;
import au.edu.usyd.reviewer.server.util.CloneUtil;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AdminServiceImpl extends RemoteServiceServlet implements AdminService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private AssignmentManager assignmentManager = Reviewer.getAssignmentManager();
	private AssignmentDao assignmentDao = assignmentManager.getAssignmentDao();

	@Override
	public Course deleteCourse(Course course) throws Exception {
		if (isAdmin()) {
			try {
				assignmentManager.deleteCourse(course);
				return course;
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new Exception("Permission denied");
		}
	}

	@Override
	public WritingActivity deleteWritingActivity(WritingActivity writingActivity) throws Exception {
		if (isAdmin() || isCourseLecturer(assignmentDao.loadCourseWhereWritingActivity(writingActivity))) {
			try {
				assignmentManager.deleteActivity(writingActivity);
				return writingActivity;
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new Exception("Permission denied");
		}
	}

	@Override
	public Collection<Course> getCourses() throws Exception {
		Collection<Course> courses;
		if (isAdmin()) {
			courses = assignmentDao.loadCourses();
		} else {
			courses = assignmentDao.loadLecturerCourses(getUser());
		}
		return CloneUtil.clone(courses);
	}

	public User getUser() {
		User user = new User();
		user.setId(this.getThreadLocalRequest().getUserPrincipal().getName());
		return user;
	}

	@Override
	public Collection<UserStats> getWritingActivityStats(Long writingActivityId) throws Exception {
		WritingActivity writingActivity = assignmentDao.loadWritingActivity(writingActivityId);
		Course course = assignmentDao.loadCourseWhereWritingActivity(writingActivity);
		Set<User> users = new HashSet<User>();
		for(UserGroup studentGroup : course.getStudentGroups()) {
			users.addAll(studentGroup.getUsers());
		}
		UserStatsAnalyser userStatsAnalyser = new UserStatsAnalyser(assignmentManager.getAssignmentRepository().getGoogleDocsServiceImpl());
		return userStatsAnalyser.calculateStats(writingActivity, users);
	}

	public boolean isAdmin() {
		User user = getUser();
		return user == null ? false : Reviewer.getAdminUsers().contains(user.getId());
	}

	public boolean isCourseLecturer(Course course) {
		User user = getUser();
		return user == null ? false : course.getLecturers().contains(user);
	}

	@Override
	public User mockUser(User user) throws Exception {
		if (isAdmin()) {
			logger.info("Mocking user: id=" + user.getId());
			this.getThreadLocalRequest().getSession().setAttribute("user", user);
			return user;
		} else {
			throw new Exception("Permission denied");
		}
	}

	@Override
	public Course saveCourse(Course course) throws Exception {
		if (isAdmin() || isCourseLecturer(assignmentDao.loadCourse(course.getId()))) {
			try {
				return CloneUtil.clone(assignmentManager.saveCourse(course));
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new Exception("Permission denied");
		}
	}

	@Override
	public WritingActivity saveWritingActivity(Long courseId, WritingActivity writingActivity) throws Exception {
		Course course = assignmentDao.loadCourse(courseId);
		if (isAdmin() || isCourseLecturer(course)) {
			try {
				return CloneUtil.clone(assignmentManager.saveActivity(course, writingActivity));
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new Exception("Permission denied");
		}
	}

	@Override
	public Grade updateGrade(Deadline deadline, String userId, Double gradeValue) throws Exception {
		Course course = assignmentDao.loadCourseWhereDeadline(deadline);
		if (isAdmin() || isCourseLecturer(course)) {
			User user = assignmentDao.loadUser(userId);
			Grade grade = assignmentDao.loadGrade(deadline, user);
			if(grade == null) {
				grade = new Grade();
				grade.setDeadline(deadline);
				grade.setUser(user);
			}
			grade.setValue(gradeValue);
			assignmentDao.save(grade);
			
			WritingActivity writingActivity = assignmentDao.loadWritingActivityWhereDeadline(deadline);
			writingActivity.getGrades().add(grade);
			assignmentDao.save(writingActivity);
			return CloneUtil.clone(grade);
		} else {
			throw new Exception("Permission denied");
		}
	}
	
	@Override
	public ReviewTemplate saveReviewTemplate(ReviewTemplate reviewTemplate) throws Exception {
		if (isAdmin()) {
			try {
				return CloneUtil.clone(assignmentManager.saveReviewTemplate(reviewTemplate));
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new Exception("Permission denied");
		}
	}

	@Override	
	public Collection<ReviewTemplate> getReviewTemplates() throws Exception {
		Collection<ReviewTemplate> reviewTemplates = null;
		if (isAdmin()) {
			reviewTemplates = assignmentDao.loadReviewTemplates();
//		} else {
//			reviewTemplates = assignmentDao.loadLecturerCourses(getUser());
		}
		return CloneUtil.clone(reviewTemplates);
	}
	
	@Override
	public ReviewTemplate deleteReviewTemplate(ReviewTemplate reviewTemplate) throws Exception {
		if (isAdmin()) {
			try {
				assignmentManager.deleteReviewTemplate(reviewTemplate);
				return reviewTemplate;
			} catch (Exception e) {
				throw e;
			}
		} else {
			throw new Exception("Permission denied");
		}
	}

	@Override
	public String updateReviewDocEntry(String reviewEntryId, String newDocEntry) throws Exception {
		try {			
			return assignmentManager.updateReviewDocEntry(reviewEntryId, newDocEntry);
		} catch (Exception e) {
			throw e;
		}		
	}

	@Override
	public ReviewingActivity getReviewingActivity(Long reviewingActivityId) throws Exception {
		ReviewingActivity reviewingActivity =null;
		
		try {			
			reviewingActivity = assignmentDao.loadReviewingActivity(reviewingActivityId);
		} catch (Exception e) {
			throw e;
		}
		return CloneUtil.clone(reviewingActivity);
		
	}

	@Override
	public String deleteReviewEntry(String reviewEntryId) throws Exception {
		try {
			assignmentManager.deleteReviewEntry(reviewEntryId);		
			return reviewEntryId;
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public ReviewEntry saveNewReviewEntry(String reviewingActivityId, String userId, String docEntryId) throws Exception {
		try {
			ReviewEntry reviewEntry = assignmentManager.saveNewReviewEntry(reviewingActivityId, userId, docEntryId);		
			return CloneUtil.clone(reviewEntry);
		} catch (Exception e) {
			throw e;
		}
	}	
}
