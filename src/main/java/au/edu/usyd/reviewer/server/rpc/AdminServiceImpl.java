package au.edu.usyd.reviewer.server.rpc;

import java.security.Principal;



import java.util.Collection;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

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
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.StringUtil;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;

import au.edu.usyd.reviewer.server.AssignmentManager;
import au.edu.usyd.reviewer.server.CourseManager;
import au.edu.usyd.reviewer.server.OrganizationManager;
import au.edu.usyd.reviewer.server.Reviewer;
import au.edu.usyd.reviewer.server.report.UserStatsAnalyser;
import au.edu.usyd.reviewer.server.servlet.LogoutServlet;
import au.edu.usyd.reviewer.server.util.CalendarUtil;
import au.edu.usyd.reviewer.server.util.ConnectionUtil;


import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AdminServiceImpl extends ReviewerServiceImpl implements AdminService {
	
	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public Course deleteCourse(Course course) throws Exception {
		initialize();
		if (isAdminOrSuperAdmin() || isStaff(course)) {
			try {
				if (course != null){
					if (course.getOrganization() == null){
						course.setOrganization(organization);
					}
					courseManager.setAssignmentManager(assignmentManager);
					courseManager.deleteCourse(course);
					course = course.clone();
				} else {
					throw new MessageException(Constants.EXCEPTION_COURSE_NOT_FOUND);
				}
				return course;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
	}

	@Override
	public WritingActivity deleteWritingActivity(WritingActivity writingActivity) throws Exception {
		initialize();
		if (isAdminOrSuperAdmin() ||  isStaff(assignmentManager.loadCourseWhereWritingActivity(writingActivity))) { 
			try {
				if (writingActivity != null){
					assignmentManager.deleteActivity(writingActivity);
					writingActivity = writingActivity.clone();
				} else {
					throw new MessageException(Constants.EXCEPTION_WRITING_ACTIVITY_COURSE_NOT_FOUND);
				}
				return writingActivity;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
	}

	@Override
	public Collection<Course> getCourses(Integer semester, Integer year, Long organizationId) throws Exception {
		initialize();	
		Collection<Course> courses = new ArrayList<Course>();
		if (isAdminOrSuperAdmin()) {
			Organization organizationSelected = null;
			if ( organizationId == null || (isSuperAdmin() && user.getOrganization().equals(organizationId))){
				organizationSelected = organization;
			} else if (organizationId != null){
				organizationSelected = organizationManager.getOrganization(organizationId);
			}
			if ( year == null){
				Calendar today = Calendar.getInstance();
				year = today.get(Calendar.YEAR);
			}
			if (organizationSelected != null){
				courses = courseDao.loadCourses(semester, year, organizationSelected);
			}
		} else if (isStaff()){ // staff == lecturer or Tutor
			courses = assignmentDao.loadStaffCourses(semester, year, user);
		}
		return courses;
	}

	

	@Override
	public Collection<UserStats> getWritingActivityStats(Long writingActivityId) throws Exception {
		initialize();
		WritingActivity writingActivity = assignmentDao.loadWritingActivity(writingActivityId);
		if (isAdminOrSuperAdmin() || isStaff(assignmentDao.loadCourseWhereWritingActivity(writingActivity))) {
			
			Course course = assignmentDao.loadCourseWhereWritingActivity(writingActivity);
			Set<User> users = new HashSet<User>();
			for(UserGroup studentGroup : course.getStudentGroups()) {
				users.addAll(studentGroup.getUsers());
			}
			UserStatsAnalyser userStatsAnalyser = new UserStatsAnalyser(assignmentManager.getAssignmentRepository().getGoogleDocsServiceImpl());
			return userStatsAnalyser.calculateStats(writingActivity, users);
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
	}

	
	
	@Override
	public Course saveCourse(Course course) throws Exception {
		initialize();
		if (isAdminOrSuperAdmin() || (course.getId() == null && isStaff()) || (course.getId() !=null && isStaff(courseDao.loadCourse(course.getId())))) {
			try {
				// Before save the course set its organization
				if (course.getOrganization() == null){
					course.setOrganization(organization);
				}
				course = assignmentManager.saveCourse(course, user);
				return course;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
	}

	@Override
	public WritingActivity saveWritingActivity(Long courseId, WritingActivity writingActivity) throws Exception {
		initialize();
		Course course = courseDao.loadCourse(courseId);
		if (isAdminOrSuperAdmin() || isStaff(courseDao.loadCourse(course.getId()))) {
			try {
				return assignmentManager.saveActivity(course, writingActivity);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
	}

	@Override
	//@TODO
	public Grade updateGrade(Deadline deadline, String userId, Double gradeValue) throws Exception {
		initialize();
		Course course = assignmentDao.loadCourseWhereDeadline(deadline);
		if (isAdminOrSuperAdmin() || isStaff(courseDao.loadCourse(course.getId()))) {
			User user = userDao.getUserByUsername(userId, course.getOrganization());
			Grade grade = assignmentDao.loadGrade(deadline, user);
			if(grade == null) {
				grade = new Grade();
				grade.setDeadline(deadline);
				grade.setUser(user);
			}
			grade.setValue(gradeValue);
			grade = assignmentDao.save(grade);
			
			WritingActivity writingActivity = assignmentDao.loadWritingActivityWhereDeadline(deadline);
			writingActivity.getGrades().add(grade);
			writingActivity = assignmentDao.save(writingActivity);
			if (grade != null){
				grade = grade.clone();
			}
			return grade;
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
	}
	
	@Override
	public ReviewTemplate saveReviewTemplate(ReviewTemplate reviewTemplate) throws Exception {
		initialize();
		if (isAdminOrSuperAdmin() || isStaff()) {
			try {
				// Before save the review template, set its organization
				if (reviewTemplate.getOrganization() == null){
					reviewTemplate.setOrganization(organization);
				}
				return assignmentManager.saveReviewTemplate(reviewTemplate, user);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
	}

	@Override	
	public Collection<ReviewTemplate> getReviewTemplates(Long organizationId) throws Exception {
		initialize();
		
		Collection<ReviewTemplate> reviewTemplates = new ArrayList<ReviewTemplate>();
		/*
		 * If logged user is not staff or admin then permission denied
		 * If logged user is superAdmin and his/her organization is equal to the organization received as parameter then use it to obtain the templates otherwise 
		 * if the organization received as parameter is not null, obtain the organization details and use it
		 * to get the templates
		 * if the user is staff then return only the template created by him/her
		 */
		if (isAdminOrSuperAdmin() || isStaff()){
			Organization organizationSelected = null;
			if (organizationId == null || (isSuperAdmin() && user.getOrganization().equals(organizationId) )){
				organizationSelected = organization;
			} else if (organizationId != null ){
				organizationSelected = organizationDao.load(organizationId);
			} 
			if (organizationSelected != null) {
				reviewTemplates = assignmentManager.loadReviewTemplates(organizationSelected, user);
			}
		} 
		return reviewTemplates;
	}
	
	@Override
	public ReviewTemplate deleteReviewTemplate(ReviewTemplate reviewTemplate) throws Exception {
		initialize();
		if (isAdminOrSuperAdmin() || isStaff()) {
			try {
				if ( reviewTemplate != null){
					if (reviewTemplate.getOrganization() == null){
						reviewTemplate.setOrganization(organization);
					}
					assignmentManager.deleteReviewTemplate(reviewTemplate);
					reviewTemplate = reviewTemplate.clone();
				} else {
					throw new MessageException(Constants.EXCEPTION_REVIEW_TEMPLATE_NOT_FOUND);
				}
				return reviewTemplate;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
	}

	@Override
	// @TODO
	public String updateReviewDocEntry(String reviewEntryId, String newDocEntry) throws Exception {
		initialize();
		if (isAdminOrSuperAdmin() || isStaff()) { 
			try {			
				return assignmentManager.updateReviewDocEntry(reviewEntryId, newDocEntry);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
	}

	@Override
	public ReviewingActivity getReviewingActivity(Long reviewingActivityId) throws Exception {
		initialize();
		ReviewingActivity reviewingActivity =null;
		if (isAdminOrSuperAdmin() || isStaff()) {
			try {			
				reviewingActivity = assignmentDao.loadReviewingActivity(reviewingActivityId);
				if (reviewingActivity != null){
					reviewingActivity = reviewingActivity.clone();
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
		return reviewingActivity;
		
	}

	@Override
	public String deleteReviewEntry(String reviewEntryId) throws Exception {
		initialize();
		try {
			if (isAdminOrSuperAdmin() || isStaff()) {
				assignmentManager.deleteReviewEntry(reviewEntryId);
				return reviewEntryId;
			} else {
				throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	//@TODO
	public ReviewEntry saveNewReviewEntry(String reviewingActivityId, String userId, String docEntryId) throws Exception {
		initialize();
		if (isAdminOrSuperAdmin() || isStaff()) {
			try {
				ReviewEntry reviewEntry = assignmentManager.saveNewReviewEntry(reviewingActivityId, userId, docEntryId, organization);
				if (reviewEntry != null){
					reviewEntry = reviewEntry.clone();
				}
				return reviewEntry;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
	}	
	
	/**
	 * Returns a collection of all the organizations
	 */
	public Collection<Organization> getOrganizations() throws Exception{

		initialize();
		Collection organizations = new ArrayList<Organization>();
		if (isSuperAdmin()){
			organizations = organizationManager.getOrganizations();
		} 
		return organizations;
	}
	

	public Collection<Integer> getYears(){
		return CalendarUtil.getYears();
	}
	

	public User mockUser(User aUser) throws Exception {
		initialize();
		if (isAdminOrSuperAdmin() || isStaff()) {
			String email = aUser.getEmail();
			String username = aUser.getUsername();
			User mockedUser= null;
			if (email != null && !StringUtil.isBlank(email)){
				mockedUser = userDao.getUserByEmail(email);
			} else if (username != null && !StringUtil.isBlank(username)){
				username = username.toLowerCase();
				for (User userDB :userDao.getUserByUsername(username)){
					if (organization.domainBelongsToEmailsDomain(userDB.getDomain())){
						mockedUser = userDB.clone();
					}
				}
			} else {
				throw new MessageException(Constants.EXCEPTION_USERNAME_OR_EMAIL_NO_EXIST);
			}
			
			if (mockedUser != null){
				if (!organization.domainBelongsToEmailsDomain(user.getDomain())){
					throw new MessageException(Constants.EXCEPTION_WRONG_ORGANIZATION_DOMAIN);
				}
				this.getThreadLocalRequest().getSession().setAttribute("mockedUser", mockedUser);
				return mockedUser;
			} else{
				throw new MessageException(Constants.EXCEPTION_USERNAME_OR_EMAIL_NO_EXIST);
			}	
		} else {
			throw new MessageException( Constants.EXCEPTION_PERMISSION_DENIED);
		}
	}

	public ReviewTemplate shareReviewTemplateWith(ReviewTemplate reviewTemplate, String email) throws Exception {
		initialize();
		if (isAdminOrSuperAdmin() || isStaff()) {
			try {
				if (StringUtil.isValidateEmail(email)){
					if (reviewTemplate.getOrganization()== null){
						reviewTemplate.setOrganization(organization);
					}
					reviewTemplate = assignmentManager.shareReviewTemplateWith(reviewTemplate, email);
					return reviewTemplate;
				} else {
					throw new MessageException(Constants.EXCEPTION_INVALID_EMAIL);
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
	}
	
	public ReviewTemplate noShareReviewTemplateWith(ReviewTemplate reviewTemplate, String email) throws Exception {
		initialize();
		if (isAdminOrSuperAdmin() || isStaff()) {
			try {
				reviewTemplate = assignmentManager.noShareReviewTemplateWith(reviewTemplate, email);
				return reviewTemplate;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
	}
	
}
