package au.edu.usyd.reviewer.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Deadline;
import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.Grade;
import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.OrganizationProperty;
import au.edu.usyd.reviewer.client.core.Review;
import au.edu.usyd.reviewer.client.core.ReviewingActivity;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.UserGroup;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.StringUtil;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.server.util.ObjectConverter;

public class CourseManager {
	
private static CourseManager courseManager = null;
	
	private CourseDao courseDao = CourseDao.getInstance();
	private AssignmentManager assignmentManager = null;
	private OrganizationManager organizationManager = OrganizationManager.getInstance();
	private UserDao userDao = UserDao.getInstance();
	
	/***
	 * Constructor 
	 */
	private CourseManager(){
	}

	/**
	 * Singleton. Get the only instance of CourseManager class
	 * @return courseManager the only instance of CourseManager
	 */
	public static CourseManager getInstance(){
		if (courseManager == null){
			courseManager = new CourseManager();
		}
		return courseManager;
	}
	
	/**
	 * Return the courses of the organization taking in consideration the filters semester, year and user.
	 * Use pagination with page and limit.
	 * @param semester semester of the courses
	 * @param year year of the course
	 * @param organization organization owner of the courses
	 * @param user this user could be a lecturer, a tutor or a student of the course. 
	 * @param limit quantity of courses per page
	 * @param page number to page to return
	 * @return List of courses
	 * @throws MessageException message to the user
	 */
	public List<Course> getCourses(Integer semester, Integer year, Organization organization, User user, 
			                       Integer limit, Integer page, String tasks, boolean finished) throws Exception{
		List<Course> courses = courseDao.loadCourses(semester, year, organization, user, limit, page, tasks, finished);
		return courses;
	}
	
	/**
	 * Create or update the course received as parameter
	 * @param course to save
	 * @param loggedUser logged user
	 * @return Course Saved course
	 * @throws Exception
	 */
	public Course saveCourse(Course course, User loggedUser) throws Exception{
		course = assignmentManager.saveCourse(course, loggedUser);
		return course;
	}
	
	/**
	 * Set the assignment manager in the course manager
	 * @param manager assignment manager
	 */
	public void setAssignmentManager(AssignmentManager manager){
		assignmentManager = manager;
	}
	
	/**
	 * Get the course with id equals to the id received as parameter
	 * @param courseId id of the course to get
	 * @return Course 
	 * @throws Exception
	 */
	public Course getCourse(Long courseId) throws Exception{
		return courseDao.load(courseId);
	}
	
	/**
	 *  Get the course owner of the writing activity received as parameter
	 * @param writingActivity writing activity to look for the course
	 * @return Course owner of the writing activity
	 * @throws MessageException
	 */
	public Course loadCourseWhereWritingActivity(WritingActivity writingActivity) throws MessageException{
		return courseDao.loadCourseWhereWritingActivity(writingActivity);
	}
	
	/**
	 * Return a course with all its relationships
	 * @param course course without relationships (the objects have only the id)
	 * @param organization organization of the logged user
	 * @return Course with all its relationships 
	 * @throws MessageException message to the logged user
	 */
	public Course loadCourseRelationships(Course course, Organization organization) throws MessageException{
		MessageException me = null;
		try{
			// Set organization
			if (course.getOrganization() == null){
				course.setOrganization(organization);
			} else {
				Organization anOrganization = organizationManager.getOrganization(course.getOrganization().getId());
				if (anOrganization != null){
					course.setOrganization(anOrganization);
				} else {
					me = new MessageException(Constants.EXCEPTION_ORGANIZATION_NOT_FOUND);
					me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
					throw me;
				}
			}
			
			// load lecturers
			Set<User> lecturers = new HashSet<User>();
			for(User lecturer :course.getLecturers()){
				if (lecturer != null && lecturer.getId() != null){
					lecturer = userDao.load(lecturer.getId());
				} else if (lecturer.getId() == null && lecturer.getEmail() != null){
					lecturer = userDao.getUserByEmail(lecturer.getEmail());
				}
				lecturers.add(lecturer);
			}
			course.setLecturers(lecturers);
			
			// load tutors
			Set<User> tutors = new HashSet<User>();
			for(User tutor :course.getTutors()){
				if (tutor != null && tutor.getId() != null){
					tutor = userDao.load(tutor.getId());
				} else if ( tutor.getId() == null && tutor.getEmail() != null) {
					tutor = userDao.getUserByEmail(tutor.getEmail());
				}
				tutors.add(tutor);
			}
			course.setTutors(tutors);
			
			// load student Groups
			Set<UserGroup> groups = new HashSet<UserGroup>();
			for(UserGroup group :course.getStudentGroups()){
				Set<User> students = new HashSet<User>();
				if(group != null && group.getId()!= null){
					group = assignmentManager.loadUserGroup(group.getId());
					for(User student:group.getUsers()){
						if(student != null && student.getId() != null){
							student = userDao.load(student.getId());
						}
						students.add(student);
					}
				}
				groups.add(group);
			}
			course.setStudentGroups(groups);
			
			// load templates
			Set<DocEntry> entries = new HashSet<DocEntry>();
			for(DocEntry entry :course.getTemplates()){
				if (entry!= null && entry.getId() != null){
					entry = assignmentManager.loadDocEntry(entry.getId());
				}
				entries.add(entry);
			}
			course.setTemplates(entries);
			
			// load activities
			Set<WritingActivity> activities = new HashSet<WritingActivity>();
			for (WritingActivity activity : course.getWritingActivities()){
				if (activity != null && activity.getId() != null){
					activity = assignmentManager.loadWritingActivity(activity.getId());
				}
				activities.add(activity);
			}
			course.setWritingActivities(activities);
			
			return course;
		} catch(Exception e){
			e.printStackTrace();
			if (e instanceof MessageException){
				me = (MessageException) e;
			} else {
				me = new MessageException(Constants.EXCEPTION_SAVE_COURSE);
			}
			if ( me.getStatusCode() == 0){
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
			}
			throw me;
		}
	}

	public void deleteCourse(Course course) throws Exception {
		course = loadCourseRelationships(course,course.getOrganization());
		assignmentManager.deleteCourse(course);
		
	}
}
