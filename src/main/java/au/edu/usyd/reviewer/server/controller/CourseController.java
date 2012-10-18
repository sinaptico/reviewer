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


@Controller
@RequestMapping("/Course")
public class CourseController extends au.edu.usyd.reviewer.server.controller.Controller{
	
	@RequestMapping(value="/getCourses/{semester}/{year}/{organizationId}", method = RequestMethod.GET)
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
			e.printStackTrace();
			if (e instanceof MessageException){
				throw (MessageException) e;
			} else {
				throw new MessageException(Constants.EXCEPTION_GET_COURSES_MESSAGE);
			}
		}
		return courses;
	}
	
	@RequestMapping(value="/saveCourse", method = RequestMethod.PUT)
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
				throw new MessageException(Constants.EXCEPTION_SAVE_COURSE_MESSAGE);
			}
		}
	}
	
	@RequestMapping(value="/deleteCourse", method = RequestMethod.DELETE)
	public @ResponseBody Course deleteCourse(HttpServletRequest request,@RequestBody Course course) throws Exception {
		initialize(request);
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

}
