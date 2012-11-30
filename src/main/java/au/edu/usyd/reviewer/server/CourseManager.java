package au.edu.usyd.reviewer.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
			                       Integer limit, Integer page) throws Exception{
		List<Course> courses = courseDao.loadCourses(semester, year, organization, user, limit, page);
		return courses;
	}
	
	
	public Course saveCourse(Course course, User loggedUser) throws Exception{
		course = assignmentManager.saveCourse(course, loggedUser);
		if (course != null){
			course.setAutomaticReviewers(new HashSet<User>());
			course.setLecturers(new HashSet<User>());
			Organization anOrganization = course.getOrganization();
			if (anOrganization != null){
				anOrganization.setOrganizationProperties(new HashSet<OrganizationProperty>());
			}
			course.setOrganization(anOrganization);
			course.setStudentGroups(new HashSet<UserGroup>());
			course.setSupervisors(new HashSet<User>());
			course.setTemplates(new HashSet<DocEntry>());
			course.setTutorials(new HashSet<String>());
			course.setTutors(new HashSet<User>());
			course.setWritingActivities(new HashSet<WritingActivity>());
		}
		return course;
	}
	
	public void setAssignmentManager(AssignmentManager manager){
		assignmentManager = manager;
	}
	
	public Course getCourse(Long courseId) throws Exception{
		return courseDao.load(courseId);
	}
	
//	/**
//	 * Prepare the course taking in the consideration the parameters include and relationships
//	 * @param course Course to return
//	 * @param include if include is equals to all then return all the information of the course including the relationshipss
//	 * @param relationships indicates which relationships is include. It's used to get a course with a specific relationships
//	 * @return Course course prepared to sent to the user interface
//	 * @throws MessageException
//	 */
//	public Course prepareCourse(Course course, String include, String relationships) throws MessageException{
//		
//		boolean tutors = !StringUtil.isBlank(relationships) && relationships.contains(Constants.TUTORS);
//		boolean lecturers = !StringUtil.isBlank(relationships) && relationships.contains(Constants.LECTURERS);
//		boolean students = !StringUtil.isBlank(relationships) && relationships.contains(Constants.STUDENTS);
//		boolean templates = !StringUtil.isBlank(relationships) && relationships.contains(Constants.TEMPLATES);
//		boolean activities = !StringUtil.isBlank(relationships) && relationships.contains(Constants.ACTIVITIES);
//
//		if (Constants.ALL.equals(include)){
//			tutors = true;
//			lecturers = true;
//			students = true;
//			templates = true;
//			activities = true;
//		} 
//		
//		course.setAutomaticReviewers(new HashSet<User>());
//		course.setSupervisors(new HashSet<User>());
//		
//		// Lecturers
//		if (lecturers){
//			for(User lecturer: course.getLecturers()){
//				if (lecturer != null){
//					lecturer.setOrganization(null);
//				}
//			}
//		} else {
//			course.setLecturers(new HashSet<User>());
//		}
//		
//		// Students
//		if (students){
//			for(UserGroup group : course.getStudentGroups()){
//				for(User student: group.getUsers()){
//					if (student != null){
//						student.setOrganization(null);
//					}
//				}
//			}
//		} else {
//			course.setStudentGroups(new HashSet<UserGroup>());
//		} 
//		
//		// Templates
//		if (templates){
//			for(DocEntry entry:course.getTemplates()){
//				if (entry != null){
//					User owner = entry.getOwner();
//					if (owner != null){
//						owner.setOrganization(null);
//					}
//					UserGroup ownerGroup = entry.getOwnerGroup();
//					if (ownerGroup != null){
//						for(User student :ownerGroup.getUsers()){
//							if (student != null){
//								student.setOrganization(null);
//							}
//						}
//					}
//					entry.setReviews(new HashSet<Review>());
//				}
//			}
//		} else {
//			course.setTemplates(new HashSet<DocEntry>());
//		}
//		
//		// Tutors
//		if (tutors){
//			for(User tutor : course.getTutors()){
//				if (tutor != null){
//					tutor.setOrganization(null);
//				}
//			}
//		} else {
//			course.setTutors(new HashSet<User>());
//		}
//		
//		// Activities
//		if (activities){
//			for(WritingActivity activity:course.getWritingActivities()){
//				if (activity != null){
//					activity.setDeadlines(new ArrayList<Deadline>());
//					activity.setEntries(new HashSet<DocEntry>());
//					activity.setGrades(new HashSet<Grade>());
//					activity.setReviewingActivities(new ArrayList<ReviewingActivity>());
//				}
//			}
//		} else {
//			course.setWritingActivities(new HashSet<WritingActivity>());
//		}
//		return course;
//	}
		

	public Course loadCourseWhereWritingActivity(WritingActivity writingActivity) throws MessageException{
		return courseDao.loadCourseWhereWritingActivity(writingActivity);
	}
}
