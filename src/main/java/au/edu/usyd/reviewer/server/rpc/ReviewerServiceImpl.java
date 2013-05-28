package au.edu.usyd.reviewer.server.rpc;


import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * Get logger user, its organization an initialize Reviewer with it
	 */
	protected void initialize() throws Exception{
		user = getLoggedUser();
		organization = user.getOrganization();	
		Reviewer.initializeAssignmentManager(organization);	
	}
	
	/**
	 * Return the logged user
	 * @return User
	 * @throws MessageException
	 */
	public User getLoggedUser() throws MessageException{
		try {			
			HttpServletRequest request = this.getThreadLocalRequest();
			request = this.getThreadLocalRequest();
			
			// Get user from session
			Object obj = request.getSession().getAttribute("user");
			if (obj != null){
				user = (User) obj;
			}
			
			// getEmail
			String email = getEmail(request);
			
			if (email == null && user == null){
				// ERROR we need the email o the user to continue. 
				logger.info("MARIELA - Error user null and email null");
				MessageException me = new MessageException(Constants.EXCEPTION_GET_LOGGED_USER);;
				me.setStatusCode(Constants.HTTP_CODE_LOGOUT);
				throw me;
			} else if (email != null && user != null && (user != null && user.getEmail() != null && user.getEmail().equals(email))){
				//user is logged ==> continue	
			} else {
				// user is null or user's email is different to the email obtained from request ==> get user from Database
				if (email != null && ((user == null) || (user != null && user.getEmail() != null && !user.getEmail().equals(email)))){
					UserDao userDao = UserDao.getInstance();
					user = userDao.getUserByEmail(email);
				} 
				
				// Get organization
				Organization organization = getOrganization(email);
												
				if (organization == null){
					// ERROR we need the organization to know if shibboleth property is enabled or not
					logger.info("Organization is null so we can not verify the shibboleth property");
					MessageException me = new MessageException(Constants.EXCEPTION_GET_LOGGED_USER);;
					me.setStatusCode(Constants.HTTP_CODE_LOGOUT);
					throw me;
				} else {
					// Verify if the organization is activated and deleted
					if (!organization.isActivated() ){
						MessageException me = new MessageException(Constants.EXCEPTION_ORGANIZATION_UNACTIVATED);;
						me.setStatusCode(Constants.HTTP_CODE_LOGOUT);
						throw me;
					} else if (organization.isDeleted()){
						organization = null;
						user = null;
						request.getSession().setAttribute("user", null);
						MessageException me = new MessageException(Constants.EXCEPTION_ORGANIZATION_DELETED);;
						me.setStatusCode(Constants.HTTP_CODE_LOGOUT);
						throw me;
					}
					
					// Check if shibboleth is enabled or not in the organization
					logger.info("MARIELA - shibbolethEnabled = " + organization.isShibbolethEnabled());	
					if (organization.isShibbolethEnabled()){
						if (user != null){
							// set user in session
							request.getSession().setAttribute("user", user);
						} else {	
							// create user
							user = createUser(request, email);
							
							if (user != null){
								logger.info("MARIELA - user added to the database " + user.getId());
							} else {
								logger.info("MARIELA - user created is null");
							}
							
							// set user in session
							request.getSession().setAttribute("user", user);
						}
					} else{
						// User comes from reviewer login page
						logger.info("MARIELA - shibboleth is not enabled, so we suppose the user comes from the reviewer login page");
						if (user != null){
							logger.info("MARIELA - user is not null");
							request.getSession().setAttribute("user", user);
						} else {
							MessageException me = new MessageException(Constants.EXCEPTION_INVALID_LOGIN);;
							me.setStatusCode(Constants.HTTP_CODE_LOGOUT);
							throw me;
						}
					}	
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
		if (user != null && organization == null){
			organization = user.getOrganization();
		}
		if (organization != null && organization.isShibbolethEnabled()){
			ConnectionUtil.logoutAAF(this.getThreadLocalRequest(), this.getThreadLocalResponse());
		} else {
			ConnectionUtil.logout(this.getThreadLocalRequest());
		}
		user = null;
		organization = null;
	}
	
	
	/*
	 * Check if the organization properties are OK to activate it or not.
	 * If the properties are OK then the organization is activated 
	 */
	public Organization checkOrganizationProperties(Organization anOrganization) throws Exception{
		if (isSuperAdmin()){
			anOrganization = organizationManager.activateOrganization(anOrganization);
			if (anOrganization != null && organization != null && anOrganization.getId() != null && anOrganization.getId().equals(organization.getId())){
				organization = anOrganization;
			}
		}
		return anOrganization;
	}
	
	/**
	 * Return a boolean indicating if mockedUser is an instructor or not of the course received as parameter
	 * @param course
	 * @return true mockedUser is instructor otherwise false
	 * @throws Exception
	 */
	protected boolean isCourseInstructor(Course course)throws Exception {
		User mockedUser = mockedUser = getMockedUser();
		return mockedUser == null ? false : course.getLecturers().contains(mockedUser) || 
											course.getTutors().contains(mockedUser) || 
											course.getSupervisors().contains(mockedUser); 
	}
	
	/**
	 * Get email from request
	 * @param request
	 * @return email obtained from request
	 */
	private String getEmail(HttpServletRequest request){
		// Get email from request
		String email = null;
		if (request.getUserPrincipal() != null) {
			// Get email from reviewer login page
			email = request.getUserPrincipal().getName();
		} else if (request.getAttribute("email") != null){
			// Get email from AAF IdP property
			email = (String) request.getAttribute("email");
		}
		logger.info("MARIELA - email " + email);
		return email;
	}
	
	/**
	 * Get organization from logged user or from the database using the domain of the email
	 * @param email email to get the domain
	 * @return Organization
	 * @throws MessageException
	 */
	private Organization getOrganization(String email) throws MessageException{
		Organization organization = null;
		if (user != null){
			// Get organization from user
			organization = user.getOrganization();
		}  else {
			// Get organization using the email domain
			int i = email.indexOf("@");
			String domain = email.substring(i+1,email.length());
			OrganizationManager organizationManager = OrganizationManager.getInstance();
			organization = organizationManager.getOrganizationByDomain(domain);
		}
		logger.info("MARIELA - organization!= null? " + (organization!= null));
		return organization;
	}
	
	/**
	 * Create a user in the database. This method should be called only the first time that a new user loggin in reviewer and organization use shibboleht (AAF login)
	 * @param request Request to obtain the givenName and the surname of the user
	 * @param email email of the user
	 * @return User
	 * @throws MessageException
	 */
	private User createUser(HttpServletRequest request, String email) throws MessageException{
		
		// add user into the database as a guest 
		logger.info("MARIELA - user doesn't exists in the database so he/she will be created as guest");
		String firstname = (String) request.getAttribute("givenName");
		logger.info("MARIELA - firstname " + firstname);
		String lastname = (String) request.getAttribute("surname");
		logger.info("MARIELA - lastname " + lastname);
		User newUser = new User();
		user.setFirstname(firstname);
		user.setLastname(lastname);
		user.setEmail(email);
		user.setOrganization(organization);
		user.addRole(Constants.ROLE_GUEST);
		
		newUser = userDao.save(newUser);
		return newUser;
	}
}
