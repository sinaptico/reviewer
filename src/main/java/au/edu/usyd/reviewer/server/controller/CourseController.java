package au.edu.usyd.reviewer.server.controller;

import java.util.ArrayList;



import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
import au.edu.usyd.reviewer.server.OrganizationDao;

/**
 * Controller for courses. It has methods to retrieve, create, update and delete courses  
 * @author mdagraca
 */

@Controller
@RequestMapping("/")
public class CourseController extends ReviewerController {
	
	/**
	 * If the logged user is admin or guest and role is true this method returns the courses belong to the organization where the logged user is student, tutor or guest.  
	 * If the logged user is an admin and role is false this method returns the courses belong to the organization of the logged user
	 * If the logged user is super admin this method returns the courses of all the organizations
	 * @param request  HttpServletRequest to initialize the controller
	 * @param semester semester of the courses
	 * @param year  Integer year of the courses
	 * @param page Integer page to show (pagination)
	 * @param limit Integer quantity of courses per page (pagination)
	 * @param role boolean if role is true return the courses where the logged user is lecturer, tutor or student
	 * @return List of courses
	 * @throws MessageException message to the user
	 */
	@RequestMapping(value="courses", method=RequestMethod.GET)
	public @ResponseBody  List<Course> getCourses(HttpServletRequest request,@RequestParam(value="semester", required=false) Integer semester, 
			@RequestParam(value="year", required=false) Integer year, @RequestParam(value="page", required=false) Integer page,
			@RequestParam(value="limit", required=false) Integer limit, @RequestParam(value="role", required=false) boolean role) throws MessageException { 
		List<Course> courses = new ArrayList<Course>();
		// organization is the organization which the logged user belong to
		try{
			initialize(request);
			if (isAdmin()) { 
				if (role){ // returns all the courses belong to the organization where the logged user is student, lecturer or tutor
					courses = courseDao.loadCourses(semester, year, organization, user, limit, page);
				} else { // returns the courses belong to the user organization
					courses = courseDao.loadCourses(semester, year, organization,null, limit, page);
				}
			} else if (isSuperAdmin()){ // return the courses of all the organizations
				courses = courseDao.loadCourses(semester, year,null, null, limit, page);
			} else if (isGuest()){ // return the courses of the organization wher the logged user is student, lecturer or tutor
				courses = courseDao.loadCourses(semester, year, organization, user, limit, page);
			} else {	
				throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
			}
			for(Course course : courses){
				if (course != null){
					course.setAutomaticReviewers(new HashSet<User>());
					course.setLecturers(new HashSet<User>());
					course.setOrganization(null);
					course.setStudentGroups(new HashSet<UserGroup>());
					course.setSupervisors(new HashSet<User>());
					course.setTemplates(new HashSet<DocEntry>());
					course.setTutorials(new HashSet<String>());
					course.setTutors(new HashSet<User>());
					course.setWritingActivities(new HashSet<WritingActivity>());
				}
			}
		} catch( Exception e){
			if (e instanceof MessageException){
				throw (MessageException) e;
			} else {
				e.printStackTrace();
				throw new MessageException(Constants.EXCEPTION_GET_COURSES);
			}
		}
		return courses;
	}

	
	/**
	 * Returns the courses belong to a specific organization. This method only can be called from user with role SuperAdmin
	 * @param request HttpServletRequest to initialize the controller
	 * @param semester Integer semester of the courses
	 * @param year Integer year of the courses
	 * @param organizationId id of the organization owner of the courses
	 * @param page page to show in pagination
	 * @param limit quantity of courses per page
	 * @return List<Course> list of courses
	 * @throws MessageException message to the user
	 */
	@RequestMapping(value="organizations/{id}/courses", method = RequestMethod.GET)
	public @ResponseBody  List<Course> getCourses(HttpServletRequest request,@RequestParam(value="semester", required=false) Integer semester, 
			@RequestParam(value="year", required=false) Integer year, @PathVariable Long id, 
			@RequestParam(value="page", required=false) Integer page, 
			@RequestParam(value="limit", required=false) Integer limit) throws MessageException { 
		List<Course> courses = new ArrayList<Course>();
		try{
			initialize(request);
			if (isSuperAdmin() || (isAdmin() && organization.getId().equals(id))) {
				Organization organizationSelected = null;	
				if ( id == null){
					throw new MessageException(Constants.EXCEPTION_ORGANIZATION_NOT_FOUND);
				} else {
					organizationSelected = organizationDao.load(id);
					if (organizationSelected == null) {
						throw new MessageException(Constants.EXCEPTION_ORGANIZATION_NOT_FOUND);
					} else {
						courses = courseDao.loadCourses(semester, year, organizationSelected, null, limit, page);
					}
				}
			} else {
				throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
			}
			
			for(Course course : courses){
				if (course != null){
					course.setAutomaticReviewers(new HashSet<User>());
					course.setLecturers(new HashSet<User>());
					course.setOrganization(null);
					course.setStudentGroups(new HashSet<UserGroup>());
					course.setSupervisors(new HashSet<User>());
					course.setTemplates(new HashSet<DocEntry>());
					course.setTutorials(new HashSet<String>());
					course.setTutors(new HashSet<User>());
					course.setWritingActivities(new HashSet<WritingActivity>());
				}
			}
		} catch( Exception e){
			if (e instanceof MessageException){
				throw (MessageException) e;
			} else {
				e.printStackTrace();
				throw new MessageException(Constants.EXCEPTION_GET_COURSES);
			}
		}
		return courses;
	}
	
	/**
	 * Save the course received as parameter into the database
	 * @param request HttpServletRequest used to initialize the controller
	 * @param course Course to insert or update into the database
	 * @return Course saved
	 * @throws MessageException message to the user
	 */
	@RequestMapping(value="courses",  method = RequestMethod.PUT)
	public @ResponseBody Course saveCourse(HttpServletRequest request, @RequestBody Course course) throws MessageException {
		try{
			initialize(request);
			if (isAdminOrSuperAdmin() || isCourseLecturer(courseDao.loadCourse(course.getId()))) {
				// Before save the course set its organization
				if (course.getOrganization() == null){
					course.setOrganization(organization);
				}
				course = assignmentManager.saveCourse(course, user);
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
			} else {
				throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof MessageException){
				throw (MessageException) e;
			} else {
				throw new MessageException(Constants.EXCEPTION_SAVE_COURSE);
			}
		}
	}
	
	/**
	 * Delete the course received as parameter from the database and Google Docs
	 * @param request HttpServletRequest to initialize the controller
	 * @param course course to delete
	 * @throws MessageException message to the user
	 */
	@RequestMapping(value="courses/{id}", method = RequestMethod.DELETE)
	public @ResponseBody void deleteCourse(HttpServletRequest request,@PathVariable Long id) throws MessageException {
		try{
			initialize(request);
			if (isAdminOrSuperAdmin()) {
				if (id == null){
					throw new MessageException(Constants.EXCEPTION_COURSE_NOT_FOUND);
				} else {
					Course course = courseDao.load(id);
					if (course != null){
						if ( course.getOrganization() != null && course.getOrganization().getId() != null && 
							 course.getOrganization().getId().equals(organization.getId())){
							assignmentManager.deleteCourse(course);
						} else {
							throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
						}
					} else {
						throw new MessageException(Constants.EXCEPTION_COURSE_NOT_FOUND);
					}
				}
			} else {
				throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof MessageException){
				throw (MessageException) e;
			} else {
				throw new MessageException(Constants.EXCEPTION_DELETE_COURSE);
			}
		}
	}

	/**
	 * Get the course with id equals to the id received as parameter
	 * @param request  HttpServletRequest to initialize the controller
	 * @param id id of the course to return
	 * @return Course course with id equals to id
	 * @throws MessageException message to the user
	 */
	@RequestMapping(value="courses/{id}",method = RequestMethod.GET)
	public @ResponseBody  Course getCourse(HttpServletRequest request,@PathVariable Long id, @RequestParam(value="include", required=false) String include, 
			@RequestParam(value="relationship", required=false) String relationship) throws MessageException { 
		Course course = null;
		try{
			initialize(request);
			if (id == null){
				throw new MessageException(Constants.EXCEPTION_COURSE_NOT_FOUND);
			} else { 
				course = courseDao.load(id);
				if (course !=  null){
					if ( course.getOrganization() != null && course.getOrganization().getId() != null && 
							 course.getOrganization().getId().equals(organization.getId())){
						course = prepareCourse(course, include, relationship);
						return course;
					} else{
						throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
					}
				} else {
					throw new MessageException(Constants.EXCEPTION_COURSE_NOT_FOUND);
				}
			} 
		} catch( Exception e){
			if (e instanceof MessageException){
				throw (MessageException) e;
			} else {
				e.printStackTrace();
				throw new MessageException(Constants.EXCEPTION_GET_COURSES);
			}
		}
	}
	
	/**
	 * Prepare the course taking in the consideration the parameters include and relationship
	 * @param course Course to return
	 * @param include if include is equals to all then return all the information of the course including the relationships
	 * @param relationship indicates which relationship is included. It's used to get a course with a specific relationships
	 * @return
	 * @throws MessageException
	 */
	private Course prepareCourse(Course course, String include, String relationship) throws MessageException{
		
		boolean tutors = !StringUtil.isBlank(relationship) && relationship.contains(Constants.TUTORS);
		boolean lecturers = !StringUtil.isBlank(relationship) && relationship.contains(Constants.LECTURERS);
		boolean students = !StringUtil.isBlank(relationship) && relationship.contains(Constants.STUDENTS);
		boolean templates = !StringUtil.isBlank(relationship) && relationship.contains(Constants.TEMPLATES);
		boolean activities = !StringUtil.isBlank(relationship) && relationship.contains(Constants.ACTIVITIES);
		boolean organizationOwner = !StringUtil.isBlank(relationship) && relationship.contains(Constants.ORGANIZATION);

		
		if (!StringUtil.isBlank(include) && include.equals(Constants.ALL)){
			tutors = true;
			lecturers = true;
			students = true;
			templates = true;
			activities = true;
			organizationOwner = true;
		} 
		
		course.setAutomaticReviewers(new HashSet<User>());
		course.setSupervisors(new HashSet<User>());
		
		// organization
		if (organizationOwner){
			Organization anOrganization = course.getOrganization();
			if (anOrganization != null){
				anOrganization.setOrganizationProperties(new HashSet<OrganizationProperty>());
			}
			course.setOrganization(anOrganization);
		} else {
			course.setOrganization(null);
		}

		// Lecturers
		if (lecturers){
			for(User lecturer: course.getLecturers()){
				if (lecturer != null){
					lecturer.setOrganization(null);
				}
			}
		} else {
			course.setLecturers(new HashSet<User>());
		}
		
		// Students
		if (students){
			for(UserGroup group : course.getStudentGroups()){
				for(User student: group.getUsers()){
					if (student != null){
						student.setOrganization(null);
					}
				}
			}
		} else {
			course.setStudentGroups(new HashSet<UserGroup>());
		} 
		
		// Templates
		if (templates){
			for(DocEntry entry:course.getTemplates()){
				if (entry != null){
					User owner = entry.getOwner();
					if (owner != null){
						owner.setOrganization(null);
					}
					UserGroup ownerGroup = entry.getOwnerGroup();
					if (ownerGroup != null){
						for(User student :ownerGroup.getUsers()){
							if (student != null){
								student.setOrganization(null);
							}
						}
					}
					entry.setReviews(new HashSet<Review>());
				}
			}
		} else {
			course.setTemplates(new HashSet<DocEntry>());
		}
		
		// Tutors
		if (tutors){
			for(User tutor : course.getTutors()){
				if (tutor != null){
					tutor.setOrganization(null);
				}
			}
		} else {
			course.setTutors(new HashSet<User>());
		}
		
		// Activities
		if (activities){
			for(WritingActivity activity:course.getWritingActivities()){
				if (activity != null){
					activity.setDeadlines(new ArrayList<Deadline>());
					activity.setEntries(new HashSet<DocEntry>());
					activity.setGrades(new HashSet<Grade>());
					activity.setReviewingActivities(new ArrayList<ReviewingActivity>());
				}
			}
		} else {
			course.setWritingActivities(new HashSet<WritingActivity>());
		}
		return course;
	}
		
	
	/**
	 * Add a the list oflecturers to the course with id equals to courseId
	 * @param request HttpServletRequest to initialize the controller
	 * @param id id of the course which the lecturer belongs to
	 * @return User new reviewer
	 * @throws MessageException message to the user
	 */
	@RequestMapping(value="courses/{id}/lecturers/",method = RequestMethod.PUT)
	public @ResponseBody void addLecturer(HttpServletRequest request, @PathVariable Long id, @RequestBody List<User> lecturers)throws MessageException{
		try{
			initialize(request);
			if (isAdminOrSuperAdmin()){
				if ( id != null){
					Course course = courseDao.load(id);
					if (course !=  null){
						if (organization.getId().equals(course.getOrganization().getId())){
							if (!lecturers.isEmpty()) {
								lecturer.setOrganization(course.getOrganization());
								assignmentManager.saveLecturers(course,lecturers,user);
							} else {
								new MessageException(Constants.EXCEPTION_EMPTY_LECTURERS_LIST);
							}
						} else {
							throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
						}
					} else {
						throw new MessageException(Constants.EXCEPTION_COURSE_NOT_FOUND);
					}
				} else {
					throw new MessageException(Constants.EXCEPTION_COURSE_NOT_FOUND);
				}
			}  else{
				throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
			}
		} catch( Exception e){
			e.printStackTrace();
			if (e instanceof MessageException){
				throw (MessageException) e;
			} else {
				throw new MessageException(Constants.EXCEPTION_SAVE_LECTURER);
			}
		}
	}

	//	/**
//	 * Add a new tutor to the course with id equals to courseId
//	 * @param request HttpServletRequest to initialize the controller
//	 * @param courseId id of the course which the tutor belongs to
//	 * @return User new tutor
//	 * @throws MessageException message to the user
//	 */
//	@RequestMapping(value="courses/{courseId}/tutor", method = RequestMethod.PUT)
//	public @ResponseBody User addTutor(HttpServletRequest request, @PathVariable Long courseId, @RequestBody User tutor)throws MessageException{
//		try{
//			initialize(request);
//			if ( courseId != null){
//				Course course = courseDao.load(courseId);
//				if (course !=  null){
//					if (tutor != null) {
//						tutor.setOrganization(course.getOrganization());
//						assignmentManager.saveTutor(course,tutor,user);
//						Organization anOrganization = tutor.getOrganization();
//						if (anOrganization != null) {
//							anOrganization.setOrganizationProperties(new HashSet<OrganizationProperty>());
//							tutor.setOrganization(anOrganization);
//						}
//					} else {
//						new MessageException(Constants.EXCEPTION_INVALID_LECTURER);
//					}
//				} else {
//					throw new MessageException(Constants.EXCEPTION_COURSE_NOT_FOUND);
//				}
//			} else {
//				throw new MessageException(Constants.EXCEPTION_COURSE_NOT_FOUND);
//			}
//		} catch( Exception e){
//			if (e instanceof MessageException){
//				throw (MessageException) e;
//			} else {
//				e.printStackTrace();
//				throw new MessageException(Constants.EXCEPTION_SAVE_TUTOR);
//			}
//		}
//		return tutor;
//	}
//	
//	/**
//	 * Add a student group to the course with id equals to courseId
//	 * @param request HttpServletRequest to initialize the controller
//	 * @param courseId id of the course
//	 * @return UserGroup saved user group
//	 * @throws MessageException message of the user
//	 */
//	@RequestMapping(value="courses/{courseId}/studentgroup", method = RequestMethod.GET)
//	public @ResponseBody UserGroup addStudentGroups(HttpServletRequest request, @PathVariable Long courseId, @RequestBody UserGroup userGroup )throws MessageException{
//		try{
//			initialize(request);
//			if ( courseId != null){
//				Course course = courseDao.load(courseId);
//				if (course !=  null){
//					// VER CON FRANCO COMO SE VANA AGREGAR LOS ESTUDIANTES, DE A 1 O DE A MUCHOS? AMBOS?
//				} else {
//					throw new MessageException(Constants.EXCEPTION_COURSE_NOT_FOUND);
//				}
//			} else {
//				throw new MessageException(Constants.EXCEPTION_COURSE_NOT_FOUND);
//			}
//		} catch( Exception e){
//			if (e instanceof MessageException){
//				throw (MessageException) e;
//			} else {
//				e.printStackTrace();
//				throw new MessageException(Constants.EXCEPTION_SAVE_USER_GROUP);
//			}
//		}
//		return userGroup;
//	}

	}
