package au.edu.usyd.reviewer.server.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.server.AssignmentManager;
import au.edu.usyd.reviewer.server.CourseManager;
import au.edu.usyd.reviewer.server.OrganizationManager;
import au.edu.usyd.reviewer.server.Reviewer;

/**
 * This class is the super class of all the controller.
 * It has the common methods used in all the controllers. For example, a method obtain the logged user, others to 
 * know if the logged use is an admin or a super admin.
 * @author mdagraca
 */
 
public abstract class ReviewerController {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected AssignmentManager assignmentManager = Reviewer.getAssignmentManager();
	protected OrganizationManager organizationManager = OrganizationManager.getInstance();
	protected CourseManager courseManager = CourseManager.getInstance();
	// logged user
	protected User user = null;
	// logged user organization
	protected Organization organization = null;

	/**
	 * Returns a boolean indicating if the logged user is an admin or not
	 * @return true if the logged user has an admin role otherwise false
	 */
	protected boolean isAdmin() {
		return user == null ? false : user.isAdmin();
	}
	
	/**
	 * Return a boolean indicating if the logged user is a super admin or not
	 * @return true if the logged user has a super admin role otherwise false
	 */
	protected boolean isSuperAdmin(){
		return user == null? false : user.isSuperAdmin();
	}
	
	protected boolean isGuest() {
		return user == null ? false : user.isGuest();
	}
	

	/**
	 * Return a boolean indicating if the logged user is an admin or a super admin
	 * @return true if the logged user has admin or super admin role.
	 */
	protected boolean isAdminOrSuperAdmin(){
		return this.isAdmin() || this.isSuperAdmin();
	}
	
	protected boolean isGuestOrAdmin(){
		return this.isAdmin() || this.isGuest();
	}
	
	/**
	 * Return a boolean indicating if the logged user is an admin or a super admin or a guest
	 * @return true if the logged user has admin or super admin role or a guest
	 */
	protected boolean isAdminOrSuperAdminOrGuest(){
		return this.isAdmin() || this.isSuperAdmin() || this.isGuest();
	}
	
	
	/**
	 * Return a boolean indicating if the logged user is an admin or  or a guest
	 * @return true if the logged user has admin or  or a guest
	 */
	protected boolean isAdminOrGuest(){
		return this.isAdmin() ||  this.isGuest();
	}
	
	/**
	 * Return a boolean indicatin if the loged user is a lecturer of the course received as parameter
	 * @param course course the validate the lecturers
	 * @return true if the logged user is one of the lectureres of the course
	 */
	protected boolean isCourseLecturer(Course course) {
		return user == null ? false : course != null && course.getLecturers().contains(user);
	}
	
	protected boolean isCourseInstructor(Course course) throws Exception {
		return user == null ? false : course.getLecturers().contains(user) || course.getTutors().contains(user) || course.getSupervisors().contains(user);
	}

	/**
	 * Initialize the controller. Obtain the logged user and initialize the Reviewer class
	 * @param request HttpServletRequest to obtain the logged user
	 * @throws Exception 
	 */
    protected void initialize(HttpServletRequest request) throws Exception{
	    user = getUser(request);
	   	organization = user.getOrganization();	
	   	Reviewer.initializeAssignmentManager(organization);
	   	assignmentManager = Reviewer.getAssignmentManager();
	   	courseManager.setAssignmentManager(assignmentManager);
   }

   /**
    * Return the logged user
    * @param request HttpServletRequest to obtain the logged user
    * @return User logged user
    * @throws MessageException
    */
   protected User getUser(HttpServletRequest request) throws MessageException{
		try {
			Object obj = request.getSession().getAttribute("user");	
			if (obj != null)
			{
				user = (User) obj;
			}
			Principal principal = request.getUserPrincipal();
			if (principal == null){
				user = new User();
			}
			else {
				if  (user == null){
					user = organizationManager.getUserByEmail(principal.getName());
					request.getSession().setAttribute("user", user);
				} else if (principal.getName() != null && !principal.getName().equals(user.getEmail())){
					user = organizationManager.getUserByEmail(principal.getName());
					request.getSession().setAttribute("user", user);
				}
			}
		} catch (Exception e) {
			MessageException me = null;
			e.printStackTrace();
			if ( e instanceof MessageException){
				me = (MessageException) e;
			} else {
				me = new MessageException(Constants.EXCEPTION_GET_LOGGED_USER);
			}
			if ( me.getStatusCode() == 0){
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
			}
			throw me;
		}
		return user;
   }
   
   /**
    * Return the mocked user. If this user doesn't exist then return logged user
    * @return User
    * @throws MessageException message to the user
    */
   protected User getMockedUser(HttpServletRequest request) throws MessageException{
	   User mockedUser = null;
	   MessageException me = null;
	   try{
		   if (isAdminOrSuperAdmin()){
				mockedUser = (User) request.getSession().getAttribute("mockedUser");
				if ( mockedUser != null && mockedUser.getOrganization() == null){
					mockedUser = organizationManager.getUserByEmail(mockedUser.getEmail());
					request.getSession().setAttribute("mockedUser", mockedUser);
				}
				else if ( mockedUser == null){
						mockedUser = user;
						request.getSession().setAttribute("mockedUser", mockedUser);
				}
		   } else { // Guest
			   mockedUser = user;
			   request.getSession().setAttribute("mockedUser", mockedUser);
			}
	   } catch(Exception e){
		   if (e instanceof MessageException){
			   me = (MessageException) e;
		   } else {
			   me = new MessageException(Constants.EXCEPTION_MOCKED_USER);
		   }
		   if ( me.getStatusCode() == 0){
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
		   }
		   throw me;
	   }
		return mockedUser;
	}
   
   
   @ExceptionHandler(MessageException.class)
   public @ResponseBody String handleException(MessageException ex, HttpServletResponse response) throws IOException{
	  response.setHeader("Content-Type", "application/json");
	  response.sendError(ex.getStatusCode(), ex.getMessage());
      return ex.getMessage();
   }

}
