package au.edu.usyd.reviewer.server.controller;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.server.AssignmentDao;
import au.edu.usyd.reviewer.server.AssignmentManager;
import au.edu.usyd.reviewer.server.CourseDao;
import au.edu.usyd.reviewer.server.Reviewer;
import au.edu.usyd.reviewer.server.UserDao;

public abstract class Controller {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected AssignmentManager assignmentManager = Reviewer.getAssignmentManager();
	protected AssignmentDao assignmentDao = new AssignmentDao(Reviewer.getHibernateSessionFactory());
	protected UserDao userDao = UserDao.getInstance();
	protected CourseDao courseDao = CourseDao.getInstance();
	
	// logged user
	protected User user = null;
	// logged user organization
	protected Organization organization = null;

	
	protected boolean isAdmin() {
		return user == null ? false : user.isAdmin();
	}
	
	protected boolean isSuperAdmin(){
		return user == null? false : user.isSuperAdmin();
	}
	
	protected boolean isAdminOrSuperAdmin(){
		return this.isAdmin() || this.isSuperAdmin();
	}
	
	protected boolean isCourseLecturer(Course course) {
		return user == null ? false : course.getLecturers().contains(user);
	}

   protected void initialize(HttpServletRequest request) throws Exception{
	    user = getUser(request);
		organization = user.getOrganization();	
		Reviewer.initializeAssignmentManager(organization);	
   }

   protected User getUser(HttpServletRequest request) throws MessageException{
		try {
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
		} catch (Exception e) {
			e.printStackTrace();
			if ( e instanceof MessageException){
				throw (MessageException) e;
			} else {
				throw new MessageException(Constants.EXCEPTION_GET_LOGGED_USER);
			}
		}
		return user;
   }
}
