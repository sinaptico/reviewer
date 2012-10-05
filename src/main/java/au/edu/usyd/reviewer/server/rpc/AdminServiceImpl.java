package au.edu.usyd.reviewer.server.rpc;

import java.security.Principal;

import java.util.Collection;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

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
import au.edu.usyd.reviewer.server.AssignmentDao;
import au.edu.usyd.reviewer.server.AssignmentManager;
import au.edu.usyd.reviewer.server.CourseDao;
import au.edu.usyd.reviewer.server.OrganizationDao;
import au.edu.usyd.reviewer.server.OrganizationManager;
import au.edu.usyd.reviewer.server.Reviewer;
import au.edu.usyd.reviewer.server.UserDao;
import au.edu.usyd.reviewer.server.report.UserStatsAnalyser;
import au.edu.usyd.reviewer.server.util.CalendarUtil;


import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AdminServiceImpl extends RemoteServiceServlet implements AdminService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private AssignmentManager assignmentManager = Reviewer.getAssignmentManager();
	private AssignmentDao assignmentDao = new AssignmentDao(Reviewer.getHibernateSessionFactory());
	private UserDao userDao = UserDao.getInstance();
	private CourseDao courseDao = CourseDao.getInstance();
	// logged user
	private User user = null;
	// logged user organization
	private Organization organization = null;

		
	@Override
	public Course deleteCourse(Course course) throws Exception {
		initialize();
		if (isAdminOrSuperAdmin()) {
			try {
				assignmentManager.deleteCourse(course);
				return course;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			throw new Exception("Permission denied");
		}
	}

	@Override
	public WritingActivity deleteWritingActivity(WritingActivity writingActivity) throws Exception {
		initialize();
		if (isAdminOrSuperAdmin() || isCourseLecturer(assignmentDao.loadCourseWhereWritingActivity(writingActivity))) {
			try {
				assignmentManager.deleteActivity(writingActivity);
				return writingActivity;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			throw new Exception("Permission denied");
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
				OrganizationDao organizationDao = OrganizationDao.getInstance();
				organizationSelected = organizationDao.load(organizationId);
			}
			if ( year == null){
				Calendar today = Calendar.getInstance();
				year = today.get(Calendar.YEAR);
			}
			if (organizationSelected != null){
				courses = courseDao.loadCourses(semester, year, organizationSelected);
			}
		} else {
			courses = assignmentDao.loadLecturerCourses(semester, year, user);
		}
		return courses;
	}

	public User getUser() {
		try {
			HttpServletRequest request = this.getThreadLocalRequest();
			Object obj = request.getSession().getAttribute("user");
			
			if (obj != null)
			{
				user = (User) obj;
			}
			Principal principal = request.getUserPrincipal();
			UserDao userDao = UserDao.getInstance();
			if  (user == null){
				user = userDao.getUserByEmail(principal.getName());
				request.getSession().setAttribute("user", user);
			} else if (principal.getName() != null && !principal.getName().equals(user.getEmail())){
				user = userDao.getUserByEmail(principal.getName());
				request.getSession().setAttribute("user", user);
		}
		} catch (MessageException e) {
			e.printStackTrace();
		}
		return user;
	}

	@Override
	public Collection<UserStats> getWritingActivityStats(Long writingActivityId) throws Exception {
		initialize();
		WritingActivity writingActivity = assignmentDao.loadWritingActivity(writingActivityId);
		Course course = assignmentDao.loadCourseWhereWritingActivity(writingActivity);
		Set<User> users = new HashSet<User>();
		for(UserGroup studentGroup : course.getStudentGroups()) {
			users.addAll(studentGroup.getUsers());
		}
		UserStatsAnalyser userStatsAnalyser = new UserStatsAnalyser(assignmentManager.getAssignmentRepository().getGoogleDocsServiceImpl());
		return userStatsAnalyser.calculateStats(writingActivity, users);
	}

	private boolean isAdmin() {
		return user == null ? false : user.isAdmin();
	}
	
	private boolean isSuperAdmin(){
		return user == null? false : user.isSuperAdmin();
	}
	
	private boolean isAdminOrSuperAdmin(){
		return this.isAdmin() || this.isSuperAdmin();
	}
	
	public boolean isCourseLecturer(Course course) {
		return user == null ? false : course.getLecturers().contains(user);
	}

	@Override
	public User mockUser(User aUser) throws Exception {
		initialize();
		if (isAdminOrSuperAdmin()) {
			String email = null;
			if ( aUser.getEmail() != null && !StringUtil.isBlank(aUser.getEmail())){
				email = aUser.getEmail();
			} else if (aUser.getUsername() != null && !StringUtil.isBlank(aUser.getUsername())){
				email = aUser.getUsername() + "@" + organization.getGoogleDomain();
			} else {
				throw new MessageException(Constants.EXCEPTION_USERNAME_OR_EMAIL_NO_EXIST);
			}
			User mockedUser = userDao.getUserByEmail(email);
			if (mockedUser != null){
				logger.info("Mocking user: " + mockedUser.getEmail());
				this.getThreadLocalRequest().getSession().setAttribute("mockedUser", mockedUser);
				return mockedUser;
			} else{
				throw new MessageException(Constants.EXCEPTION_USERNAME_OR_EMAIL_NO_EXIST);
			}	
		} else {
			throw new MessageException( Constants.EXCEPTION_PERMISSION_DENIED);
		}
	}

	@Override
	public Course saveCourse(Course course) throws Exception {
		initialize();
		if (isAdminOrSuperAdmin()|| isCourseLecturer(courseDao.loadCourse(course.getId()))) {
			try {
				// Before save the course set its organization
				if (course.getOrganization() == null){
					course.setOrganization(organization);
				}
				return assignmentManager.saveCourse(course);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			throw new MessageException("Permission denied");
		}
	}

	@Override
	public WritingActivity saveWritingActivity(Long courseId, WritingActivity writingActivity) throws Exception {
		initialize();
		Course course = courseDao.loadCourse(courseId);
		if (isAdminOrSuperAdmin() || isCourseLecturer(course)) {
			try {
				return assignmentManager.saveActivity(course, writingActivity);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			throw new Exception("Permission denied");
		}
	}

	@Override
	public Grade updateGrade(Deadline deadline, String userId, Double gradeValue) throws Exception {
		initialize();
		Course course = assignmentDao.loadCourseWhereDeadline(deadline);
		if (isAdminOrSuperAdmin() || isCourseLecturer(course)) {
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
			if (grade != null){
				grade = grade.clone();
			}
			return grade;
		} else {
			throw new Exception("Permission denied");
		}
	}
	
	@Override
	public ReviewTemplate saveReviewTemplate(ReviewTemplate reviewTemplate) throws Exception {
		initialize();
		if (isAdminOrSuperAdmin()) {
			try {
				// Before save the review template, set its organization
				if (reviewTemplate.getOrganization() == null){
					reviewTemplate.setOrganization(organization);
				}
				return assignmentManager.saveReviewTemplate(reviewTemplate);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			throw new Exception("Permission denied");
		}
	}

	@Override	
	public Collection<ReviewTemplate> getReviewTemplates(Long organizationId) throws Exception {
		initialize();
		
		Collection<ReviewTemplate> reviewTemplates = new ArrayList<ReviewTemplate>();
		/*
		 * If logged user is not teacher o manager then permission denied
		 * If logged user is manager and his/her organization is equal to the organization received as 
		 * parameter then use it to obtain the templates otherwise 
		 * if the organization received as parameter is not null, obtain the organization details and use it
		 * to get the templates.
		 */
		if (isAdminOrSuperAdmin()) {
			Organization organizationSelected = null;
			if (organizationId == null || (isSuperAdmin() && user.getOrganization().equals(organizationId) )){
				organizationSelected = organization;
			} else if (organizationId != null ){
				OrganizationDao organizationDao = OrganizationDao.getInstance();
				organizationSelected = organizationDao.load(organizationId);
			} 
			if (organizationSelected != null){
				reviewTemplates = assignmentDao.loadReviewTemplates(organizationSelected);
			}
		} 
		return reviewTemplates;
	}
	
	@Override
	public ReviewTemplate deleteReviewTemplate(ReviewTemplate reviewTemplate) throws Exception {
		initialize();
		if (isAdminOrSuperAdmin()) {
			try {
				assignmentManager.deleteReviewTemplate(reviewTemplate);
				return reviewTemplate;
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		} else {
			throw new Exception("Permission denied");
		}
	}

	@Override
	public String updateReviewDocEntry(String reviewEntryId, String newDocEntry) throws Exception {
		initialize();
		try {			
			return assignmentManager.updateReviewDocEntry(reviewEntryId, newDocEntry);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}		
	}

	@Override
	public ReviewingActivity getReviewingActivity(Long reviewingActivityId) throws Exception {
		initialize();
		ReviewingActivity reviewingActivity =null;
		try {			
			reviewingActivity = assignmentDao.loadReviewingActivity(reviewingActivityId);
			if (reviewingActivity != null){
				reviewingActivity = reviewingActivity.clone();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return reviewingActivity;
		
	}

	@Override
	public String deleteReviewEntry(String reviewEntryId) throws Exception {
		initialize();
		try {
			assignmentManager.deleteReviewEntry(reviewEntryId);		
			return reviewEntryId;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public ReviewEntry saveNewReviewEntry(String reviewingActivityId, String userId, String docEntryId) throws Exception {
		initialize();
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
	}	

	/**
	 * Get logger user, its organization an initialize Reviewer with it
	 */
	private void initialize() throws Exception{
		user = getUser();
		organization = user.getOrganization();	
		Reviewer.initializeAssignmentManager(organization);	
	}

	
	public Collection<Organization> getAllOrganizations() throws Exception{

		initialize();
		Collection organizations = new ArrayList<Organization>();
		if (isSuperAdmin()){
			OrganizationManager organizationManager = OrganizationManager.getInstance();
			organizations = organizationManager.getAllOrganizations();
		} 
		return organizations;
	}
	

	public Collection<Integer> getYears(){
		return CalendarUtil.getYears();
	}
}
