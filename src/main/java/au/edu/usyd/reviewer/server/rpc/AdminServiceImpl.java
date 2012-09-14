package au.edu.usyd.reviewer.server.rpc;

import java.security.Principal;
import java.util.Collection;


import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.usyd.reviewer.client.admin.AdminService;
import au.edu.usyd.reviewer.client.admin.report.UserStats;
import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Deadline;
import au.edu.usyd.reviewer.client.core.Grade;
import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.ReviewEntry;
import au.edu.usyd.reviewer.client.core.ReviewTemplate;
import au.edu.usyd.reviewer.client.core.ReviewingActivity;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.UserGroup;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.server.AssignmentDao;
import au.edu.usyd.reviewer.server.AssignmentManager;
import au.edu.usyd.reviewer.server.Reviewer;
import au.edu.usyd.reviewer.server.UserDao;
import au.edu.usyd.reviewer.server.report.UserStatsAnalyser;
import au.edu.usyd.reviewer.server.util.CloneUtil;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AdminServiceImpl extends RemoteServiceServlet implements AdminService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private AssignmentManager assignmentManager = Reviewer.getAssignmentManager( getUser().getOrganization());
	private AssignmentDao assignmentDao = assignmentManager.getAssignmentDao();
	private UserDao userDao = UserDao.getInstance();

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
	public Collection<Course> getCourses(Integer semester, Integer year) throws Exception {
		Collection<Course> courses;
		if (isAdmin()) {
			courses = assignmentDao.loadCourses(semester, year);
		} else {
			courses = assignmentDao.loadLecturerCourses(semester, year, getUser());
		}
		return CloneUtil.clone(courses);
	}

	private User getUser() {
		UserDao userDao = UserDao.getInstance();
		User user = null;
		try {
//			request.getSession().setAttribute("user", user);
			HttpServletRequest request = this.getThreadLocalRequest();
			Principal principal = request.getUserPrincipal(); 
//			this.getThreadLocalRequest().getUserPrincipal().getName()
			user = userDao.getUserByEmail(principal.getName());
		} catch (MessageException e) {
			e.printStackTrace();
		}
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
		return user == null ? false : user.isManager() || user.isTeacher();
	}

	public boolean isCourseLecturer(Course course) {
		User user = getUser();
		return user == null ? false : course.getLecturers().contains(user);
	}

	@Override
	public User mockUser(User user) throws Exception {
		if (isAdmin()) {
			logger.info("Mocking user: email=" + user.getEmail());
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
				// Before save the course set its organization
				course = setOrganizationInCourse(course);
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
			User user = userDao.getUserByUsername(userId, course.getOrganization());
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
				// Before save the review template, set its organization
				reviewTemplate = setOrganizationInReviewTemplate(reviewTemplate);
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
			ReviewEntry reviewEntry = assignmentManager.saveNewReviewEntry(reviewingActivityId, userId, docEntryId, getUser().getOrganization());		
			return CloneUtil.clone(reviewEntry);
		} catch (Exception e) {
			throw e;
		}
	}	
	
	/**
	 * if the user is a teacher or a student then set the organization to the course 
	 * @param course course to set organization
	 * @return if user is not a manager, the course has the organization
	 */
	private Course setOrganizationInCourse(Course course){
		User user = getUser();
		if (!user.isManager()){
			Organization organization = user.getOrganization();
			course.setOrganization(organization);
		}
		return course;
	}

	/**
	 * if the user is a teacher or a student then set the organization to the review template 
	 * @param reviewTemplate  review template to set organization
	 * @return if user is not a manager, the review template has the organization
	 */
	private ReviewTemplate setOrganizationInReviewTemplate(ReviewTemplate reviewTemplate){
		User user = getUser();
		if (!user.isManager()){
			Organization organization = user.getOrganization();
			reviewTemplate.setOrganization(organization);
		}
		return reviewTemplate;
	}	
}
