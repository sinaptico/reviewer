package au.edu.usyd.reviewer.server.controller;

import java.util.ArrayList;



import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.server.OrganizationDao;

/**
 * Controller for courses. It has to get, save and delete courses  
 * @author mdagraca
 */

@Controller
@RequestMapping("/Course")
public class CourseController extends ReviewerController {
	
	/**
	 * Return a list of courses belongs to the Organization with id equals to "organizationId", year equals "year" and semester equals to "semeter"
	 * @param request  HttpServletRequest to initialize the controller
	 * @param semester semester of the courses
	 * @param year  year of the courses
	 * @param organizationId organization id of the organization owner of the courses
	 * @return List of courses
	 * @throws MessageException message to the user
	 */
	@RequestMapping(value="/{semester}/{year}/{organizationId}", method = RequestMethod.GET)
	public @ResponseBody  List<Course> getCourses(HttpServletRequest request,@PathVariable Integer semester, 
			@PathVariable Integer year, @PathVariable Long organizationId) throws MessageException { 
		List<Course> courses = new ArrayList<Course>();
		try{
			initialize(request);
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
	@RequestMapping(method = RequestMethod.PUT)
	public @ResponseBody Course saveCourse(HttpServletRequest request, @RequestBody Course course) throws MessageException {
		try{
			initialize(request);
			if (isAdminOrSuperAdmin()|| isCourseLecturer(courseDao.loadCourse(course.getId()))) {
				// Before save the course set its organization
				if (course.getOrganization() == null){
					course.setOrganization(organization);
				}
				return assignmentManager.saveCourse(course, user);
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
	 * @return Course deleted
	 * @throws MessageException message to the user
	 */
	@RequestMapping(value="{courseId}",method = RequestMethod.DELETE)
	public @ResponseBody Course deleteCourse(HttpServletRequest request,@PathVariable Long courseId) throws MessageException {
		try{
			initialize(request);
			if (isAdminOrSuperAdmin()) {
				Course course = courseDao.load(courseId);
				if (course != null){
					assignmentManager.deleteCourse(course);
					return course;
				} else {
					throw new MessageException(Constants.EXCEPTION_COURSE_NOT_FOUND);
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

	@RequestMapping(value="/{courseId}", method = RequestMethod.GET)
	public @ResponseBody  Course getCourse(HttpServletRequest request,@PathVariable Long courseId) throws MessageException { 
		Course course = null;
		try{
			initialize(request);
			course = courseDao.load(courseId);
			if (course !=  null){
				return course;	
			} else {
				throw new MessageException(Constants.EXCEPTION_COURSE_NOT_FOUND);
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
}
