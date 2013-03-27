package au.edu.usyd.reviewer.server.rpc;


import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import au.edu.usyd.feedback.feedbacktracking.FeedbackTrackingDao;
import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.server.AssignmentDao;
import au.edu.usyd.reviewer.server.AssignmentManager;
import au.edu.usyd.reviewer.server.CourseDao;
import au.edu.usyd.reviewer.server.CourseManager;
import au.edu.usyd.reviewer.server.EmailNotifier;
import au.edu.usyd.reviewer.server.OrganizationDao;
import au.edu.usyd.reviewer.server.OrganizationManager;
import au.edu.usyd.reviewer.server.Reviewer;
import au.edu.usyd.reviewer.server.UserDao;
import au.edu.usyd.reviewer.server.util.ConnectionUtil;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ReviewerServiceImpl extends RemoteServiceServlet {

	private static final long serialVersionUID = 1L;
	// Manager classes
	protected AssignmentManager assignmentManager = Reviewer.getAssignmentManager();
	protected CourseManager courseManager = CourseManager.getInstance();
	protected OrganizationManager organizationManager = OrganizationManager.getInstance();
	protected EmailNotifier emailNotifier = null;
	
	// Dao classes
	protected AssignmentDao assignmentDao = new AssignmentDao(Reviewer.getHibernateSessionFactory());
	protected UserDao userDao = UserDao.getInstance();
	protected CourseDao courseDao = CourseDao.getInstance();
	protected OrganizationDao organizationDao = OrganizationDao.getInstance();
	protected FeedbackTrackingDao feedbackTrackingService = new FeedbackTrackingDao();

	// logged user
	protected User user = null;
	// logged user organization
	protected Organization organization = null;
	
	/**
	 * Get logger user, its organization an initialize Reviewer with it
	 */
	protected void initialize() throws Exception{
		user = getLoggedUser();
		organization = user.getOrganization();	
		Reviewer.initializeAssignmentManager(organization);	
	}
	
	public User getLoggedUser() throws MessageException{
		try {
			HttpServletRequest request = this.getThreadLocalRequest();
			request = this.getThreadLocalRequest();
			Object obj = request.getSession().getAttribute("user");
				
			if (obj != null){
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
			if (organization != null){
				if (!organization.isActivated() ){
					organization = organizationManager.activateOrganization(organization);
					if (!organization.isActivated()){
						MessageException me = new MessageException(Constants.EXCEPTION_ORGANIZATION_UNACTIVATED);;
						me.setStatusCode(Constants.HTTP_CODE_LOGOUT);
						throw me;
					}
				} else if (organization.isDeleted()){
					organization = null;
					user = null;
					request.getSession().setAttribute("user", null);
					MessageException me = new MessageException(Constants.EXCEPTION_ORGANIZATION_DELETED);;
					me.setStatusCode(Constants.HTTP_CODE_LOGOUT);
					throw me;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof MessageException){
				throw (MessageException)e;
			} else {
				throw new MessageException(Constants.EXCEPTION_GET_LOGGED_USER);
			}
		}
		return user;
	}

	public User getUser() throws MessageException{
		return getLoggedUser();
	}
	
	/** check user roles **/
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
	
	protected boolean isGuestOrAdminOrSuperAdmin(){
		return this.isAdmin() || this.isSuperAdmin() || isGuest();
	}
	
	protected boolean isGuest(){
		return user == null? false : user.isGuest();
	}
	


	protected User getMockedUser() throws MessageException{
		User mockedUser = null;
		try {
			HttpServletRequest request = this.getThreadLocalRequest();
			mockedUser = (User) request.getSession().getAttribute("mockedUser");
			 
			if ( mockedUser != null) {
				if (mockedUser.getOrganization() == null){
					mockedUser = userDao.getUserByEmail(mockedUser.getEmail());
					request.getSession().setAttribute("mockedUser", mockedUser);
				}
			}
			else {
				mockedUser = user;
				request.getSession().setAttribute("mockedUser", mockedUser);
			} 
		} catch (MessageException e) {
			e.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_USER_NOT_MOCKED);
		}
		return mockedUser;
	}
	

	public void logout() throws Exception{
		user = null;
		organization = null;
		ConnectionUtil.logout(this.getThreadLocalRequest());
	}
	
	

	public Organization checkOrganizationProperties(Organization anOrganization) throws Exception{
		if (isSuperAdmin()){
			anOrganization = organizationManager.activateOrganization(anOrganization);
			if (anOrganization != null && organization != null && anOrganization.getId() != null && anOrganization.getId().equals(organization.getId())){
				organization = anOrganization;
			}
		}
		return anOrganization;
	}
	
	protected boolean isCourseInstructor(Course course)throws Exception {
		User mockedUser = mockedUser = getMockedUser();
		return mockedUser == null ? false : course.getLecturers().contains(mockedUser) || 
											course.getTutors().contains(mockedUser) || 
											course.getSupervisors().contains(mockedUser); 
	}
}
