package au.edu.usyd.reviewer.server.controller;

import java.util.ArrayList;

import java.util.Map;


import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.server.util.ObjectConverter;

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
	 * @param include if it is equals to all then it returns all the relationships of the courses 
	 * @param relationships It indicates the relationships to be included in the courses. It can be tutors,lecturers, studentGroups,templates,writingActivities,organization
	 * @param role boolean if role is true return the courses where the logged user is lecturer, tutor or student 
	 * @param tasks It can be reviewing, writing. It's used to return course with the reviewing or writing task of the mocked or loggued user
	 * @return List of courses
	 * @throws MessageException message to the user
	 */
	@RequestMapping(value="courses", method=RequestMethod.GET)
	public @ResponseBody  List getCourses(HttpServletRequest request, 
			@RequestParam(value="semester", required=false) Integer semester, 
			@RequestParam(value="year", required=false) Integer year, 
			@RequestParam(value="page", required=false) Integer page,
			@RequestParam(value="limit", required=false) Integer limit,
			@RequestParam(value="role", required=false) boolean role, 
			@RequestParam(value="include", required=false) String include, 
			@RequestParam(value="relationships", required=false) String relationships,
			@RequestParam(value="tasks", required=false) String tasks,
			@RequestParam(value="finished", required=false) boolean finished) throws MessageException {
		MessageException me = null;
		try{
			
			initialize(request);
			List<Course> courses = new ArrayList<Course>();
			User aUser = null;
			if (role){
				aUser = super.getMockedUser(request);
			}
			if (isAdminOrGuest()) { 
				courses = courseManager.getCourses(semester, year, organization, aUser, limit, page, tasks,finished);
			} else if (isSuperAdmin()){ // return the courses of all the organizations
				courses = courseManager.getCourses(semester, year,null, aUser, limit, page, tasks, finished);
			} else {	
				me = new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
				me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
				throw me; 
			}
			List coursesList = ObjectConverter.convertCollectiontInList(courses, include, relationships,0);
			return coursesList;			
		} catch( Exception e){
			e.printStackTrace();
			if (e instanceof MessageException){
				me = (MessageException)e;
			} else {
				me = new MessageException(Constants.EXCEPTION_GET_COURSES);; 
			}
			if ( me.getStatusCode() == 0){
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
			}
			throw me;
		}
	}

	
	/**
	 * Returns the courses belong to a specific organization. This method only can be called from user with role SuperAdmin
	 * @param request HttpServletRequest to initialize the controller
	 * @param semester Integer semester of the courses
	 * @param year Integer year of the courses
	 * @param organizationId id of the organization owner of the courses
	 * @param page page to show in pagination
	 * @param limit quantity of courses per page
	 * @param include if it is equals to all then it returns all the relationships of the courses
	 * @param relationships It indicates the relationships to be included in the courses. It can be tutors,lecturers, studentGroups,templates,writingActivities,organization
	 * @param role boolean if role is true return the courses where the logged user is lecturer, tutor or student 
	 * @param tasks It can be reviewing, writing. It's used to return course with the reviewing or writing task of the mocked or loggued user
	 * @return List<Course> list of courses
	 * @throws MessageException message to the user
	 */
	@RequestMapping(value="organizations/{id}/courses", method = RequestMethod.GET)
	public @ResponseBody  List getCourses(HttpServletRequest request, 
					                      @PathVariable Long id,
									  	  @RequestParam(value="semester", required=false) Integer semester, 
									  	  @RequestParam(value="year", required=false) Integer year,  
									  	  @RequestParam(value="page", required=false) Integer page, 
									  	  @RequestParam(value="limit", required=false) Integer limit,
									  	  @RequestParam(value="include", required=false) String include, 
									  	  @RequestParam(value="relationships", required=false) String relationships,
									  	  @RequestParam(value="role", required=false) boolean role,
									  	  @RequestParam(value="tasks", required=false) String tasks,
									  	  @RequestParam(value="finished", required=false) boolean finished) throws MessageException {
		MessageException me = null;
		try{
			initialize(request);
			if (isSuperAdmin() || (isAdmin() && organization.getId().equals(id))) {
				Organization organizationSelected = null;	
				if ( id == null){
					me = new MessageException(Constants.EXCEPTION_ORGANIZATION_NOT_FOUND);
					me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
					throw me;
				} else {
					organizationSelected = organizationManager.getOrganization(id);
					if (organizationSelected == null) {
						me = new MessageException(Constants.EXCEPTION_ORGANIZATION_NOT_FOUND);
						me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
						throw me;
					} else {
						User aUser = null;
						if (role){
							aUser = super.getMockedUser(request);
						} else if (tasks != null && (tasks.contains(Constants.REVIEWING) || tasks.contains(Constants.WRITING))){
							aUser = super.getMockedUser(request);
						}
						
						List<Course> courses = courseManager.getCourses(semester, year, organizationSelected, aUser, limit, page,tasks,finished);
						List coursesList = ObjectConverter.convertCollectiontInList(courses, include,relationships,0);
						return coursesList;
					}
				}
			} else {
				me = new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
				me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
				throw me;
			}
		} catch( Exception e){
			if (e instanceof MessageException){
				me = (MessageException) e;
			} else {
				e.printStackTrace();
				me = new MessageException(Constants.EXCEPTION_GET_COURSES);
			}
			if (me.getStatusCode() == 0){
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
			}
			throw me;
		}
	}
	
	/**
	 * Create or update  the course received as parameter
	 * @param request HttpServletRequest used to initialize the controller
	 * @param course Course to insert or update into the database
	 * @return Course saved
	 * @throws MessageException message to the user
	 */
	@RequestMapping(value="courses",  method = RequestMethod.PUT)
	public @ResponseBody Map<String,Object> saveCourse(HttpServletRequest request, @RequestBody Course course) throws MessageException {
		MessageException me = null;
		try{
			initialize(request);
			if (isAdminOrSuperAdmin() || isCourseLecturer(courseManager.getCourse(course.getId()))) {
				course = courseManager.loadCourseRelationships(course,organization);
				// Before save the course set its organization
				course = courseManager.saveCourse(course, user);
				Map<String,Object> courseMap = ObjectConverter.convertObjectInMap(course, "", "",0);
				return courseMap;
			} else {
				me = new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
				me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
				throw me;
			}
		} catch (Exception e) {
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
	
	/**
	 * Delete the course received as parameter from the database and Google Docs
	 * @param request HttpServletRequest to initialize the controller
	 * @param Log id of the course to delete
	 * @throws MessageException message to the user
	 */
	@RequestMapping(value="courses/{id}", method = RequestMethod.DELETE)
	public @ResponseBody void deleteCourse(HttpServletRequest request,@PathVariable Long id) throws MessageException {
		MessageException me = null;
		try{
			initialize(request);
			if (isAdminOrSuperAdmin()) {
				if (id == null){
					me = new MessageException(Constants.EXCEPTION_COURSE_NOT_FOUND);
					me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
					throw me;
				} else {
					Course course = courseManager.getCourse(id);
					if (course != null){
						if (isSuperAdmin() || ( isAdmin() && course.getOrganization() != null && course.getOrganization().getId() != null && 
							 course.getOrganization().getId().equals(organization.getId()))){
							assignmentManager.deleteCourse(course);
						} else {
							me = new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
							me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
							throw me;
						}
					} else {
						me = new MessageException(Constants.EXCEPTION_COURSE_NOT_FOUND);
						me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
						throw me;
					}
				}
			} else {
				me = new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
				me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
				throw me;
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof MessageException){
				me = (MessageException) e;
			} else {
				me = new MessageException(Constants.EXCEPTION_DELETE_COURSE);
			}
			if ( me.getStatusCode() == 0){
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
			}
			throw me;
		}
	}

	/**
	 * Return the course with id equals to the {id}. 
	 * @param request  HttpServletRequest to initialize the controller
	 * @param id id of the course to return
	 * @param include if include is equals to all returns the course with all its relationships
	 * @param relationship relationships to return: tutors, lecturers, students, templates, activities, organization
	 * @return Course
	 * @throws MessageException
	 */
	@RequestMapping(value="courses/{id}",method = RequestMethod.GET)
	public @ResponseBody  Map getCourse(HttpServletRequest request,@PathVariable Long id,
			                            @RequestParam(value="include", required=false) String include, 
			                            @RequestParam(value="relationships", required=false) String relationships) throws MessageException  {
		Course course = null;
		MessageException me =null;
		try{
			initialize(request);

			if (id == null){
				me = new MessageException(Constants.EXCEPTION_COURSE_NOT_FOUND);
				me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
				throw me;
			} else { 
				course = courseManager.getCourse(id);
				if (course !=  null){
					if ( isSuperAdmin() ||( isAdminOrGuest() && course.getOrganization() != null && course.getOrganization().getId() != null && 
							 course.getOrganization().getId().equals(organization.getId()))){
						Map courseMap = ObjectConverter.convertObjectInMap(course, include,relationships,0);
						return courseMap;
					} else {
						me = new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
						me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
						throw me;
					}
				} else {
					me = new MessageException(Constants.EXCEPTION_COURSE_NOT_FOUND);
					me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
					throw me;
				}
			} 
		} catch( Exception e){
			if (e instanceof MessageException){
				me = (MessageException) e;
			} else {
				e.printStackTrace();
				me = new MessageException(Constants.EXCEPTION_GET_COURSES);
			}
			if ( me.getStatusCode() == 0){
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
			}
			throw me;
		}
	}
	
	
	/**
	 * Add a the list of lecturers to the course with id equals to {id}
	 * @param request HttpServletRequest to initialize the controller
	 * @param id id of the course which the lecturers belong to
	 * @param lecturers list of lecturers users
	 * @throws MessageException message to the user
	 */
	@RequestMapping(value="courses/{id}/lecturers/",method = RequestMethod.PUT)
	public @ResponseBody void addLecturer(HttpServletRequest request, @PathVariable Long id, @RequestBody List<User> lecturers)throws MessageException{
		MessageException me=null;
		try{
			initialize(request);
			if (isAdminOrSuperAdmin()){
				if ( id != null){
					Course course = courseManager.getCourse(id);
					if (course !=  null){
						if (organization.getId().equals(course.getOrganization().getId())){
							if (!lecturers.isEmpty()) {
								assignmentManager.saveLecturers(course,lecturers,user);
							} else {
								new MessageException(Constants.EXCEPTION_EMPTY_LECTURERS_LIST);
							}
						} else {
							me = new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
							me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
							throw me;
						}
					} else {
						me = new MessageException(Constants.EXCEPTION_COURSE_NOT_FOUND);
						me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
						throw me;
					}
				} else {
					me = new MessageException(Constants.EXCEPTION_COURSE_NOT_FOUND);
					me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
					throw me;
				}
			}  else{
				me = new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
				me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
				throw me;
			}
		} catch( Exception e){
			e.printStackTrace();
			if (e instanceof MessageException){
				me = (MessageException) e;
			} else {
				me = new MessageException(Constants.EXCEPTION_SAVE_LECTURERS);
			}
			if ( me.getStatusCode() == 0){
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
			}
			throw me;
		}
	}

	/**
	 * Add a new tutors to the course with id equals to {id}
	 * @param request HttpServletRequest to initialize the controller
	 * @param id id of the course which the tutors belong to
	 * @param tutors list of tutors users
	 * @throws MessageException message to the user
	 */
	@RequestMapping(value="courses/{id}/tutors", method = RequestMethod.PUT)
	public @ResponseBody void addTutor(HttpServletRequest request, @PathVariable Long id, @RequestBody List<User> tutors)throws MessageException{
		MessageException me = null;
		try{
			initialize(request);
			if (isAdminOrSuperAdmin()){
				if ( id != null){
					Course course = courseManager.getCourse(id);
					if (course !=  null){
						if (organization.getId().equals(course.getOrganization().getId())){
							if (!tutors.isEmpty()) {
								assignmentManager.saveTutors(course,tutors,user);
							} else {
								new MessageException(Constants.EXCEPTION_EMPTY_TUTORS_LIST);
							}
						} else {
							me = new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
							me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
							throw me;
						}
					} else {
						me = new MessageException(Constants.EXCEPTION_COURSE_NOT_FOUND);
						me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
						throw me;
					}
				} else {
					me = new MessageException(Constants.EXCEPTION_COURSE_NOT_FOUND);
					me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
					throw me;
				}
			}  else{
				me = new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
				me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
				throw me;
			}
		} catch( Exception e){
			e.printStackTrace();
			if (e instanceof MessageException){
				me = (MessageException) e;
			} else {
				me = new MessageException(Constants.EXCEPTION_SAVE_TUTORS);
			}
			
			if ( me.getStatusCode() == 0){
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
			}
			throw me;
		}
	}	
	
	/**
	 * This method create or update the writing activity received as parameter
	 * @param request HttpServletRequest to initialize the controller
	 * @param writingActivity writing activity to create or update
	 * @return WritingActivity writing activity created or updated
	 * @throws MessageException message to the user
	 */
	@RequestMapping(value="courses/{id}/activities",  method = RequestMethod.PUT)
	public @ResponseBody Map<String,Object> saveWritingActivity(HttpServletRequest request,  
									   		   @RequestBody WritingActivity writingActivity,
									   		   @PathVariable Long id) throws MessageException {
		MessageException me = null;
		try{
			initialize(request);
			if (writingActivity != null){
				if (id == null){
					me = new MessageException(Constants.EXCEPTION_COURSE_NOT_FOUND);
					me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
					throw me;
				} else {
					Course course = courseManager.getCourse(id);
					if (course != null){
						if (isAdminOrSuperAdmin() || isCourseLecturer(course)) {
							writingActivity = assignmentManager.loadWritingActivityRelationships(writingActivity);
							writingActivity = assignmentManager.saveActivity(course, writingActivity);
							Map<String,Object> activityMap = ObjectConverter.convertObjectInMap(writingActivity, "", "",0);
							return activityMap;
						} else {
							me = new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
							me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
							throw me;
						}
					} else {
						me = new MessageException(Constants.EXCEPTION_COURSE_NOT_FOUND);
						me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
						throw me;
					}
				}
			} else {
				me = new MessageException(Constants.EXCEPTION_WRITING_ACTIVITY_NOT_FOUND);
				me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
				throw me;
			}
		} catch(Exception e){
			e.printStackTrace();
			if (e instanceof MessageException){
				me = (MessageException) e;
			} else {	
				me = new MessageException(Constants.EXCEPTION_SAVE_WRITING_ACTIVITIES);
			}
			if ( me.getStatusCode() == 0){
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
			}
			throw me;
		}
	}	

}
