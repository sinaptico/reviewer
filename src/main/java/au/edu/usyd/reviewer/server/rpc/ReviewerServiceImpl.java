package au.edu.usyd.reviewer.server.rpc;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.usyd.feedback.feedbacktracking.FeedbackTrackingDao;
import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.StringUtil;
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
import au.edu.usyd.reviewer.server.oauth.GoogleAuthHelper;
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
			HttpServletResponse response = this.getThreadLocalResponse();
			request = this.getThreadLocalRequest();
			
			// Get user from session
			Object obj = request.getSession().getAttribute("user");
			if (obj != null){
				user = (User) obj;
			}
			
			// getEmail
			String email = getEmail(request,response);
			
			if (email == null && user == null){
				// ERROR we need the email o the user to continue
				logger.error("email is null or user is null");
				logger.error("email " + email);
				logger.error("user " + user);
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
				organization = getOrganization(email, user);
												
				if (organization == null){
					// ERROR we need the organization to know if shibboleth property is enabled or not
					logger.error("Organization is null for user " + email);
					MessageException me = new MessageException(Constants.EXCEPTION_GET_LOGGED_USER);;
					me.setStatusCode(Constants.HTTP_CODE_LOGOUT);
					throw me;
				} else {
					// Verify if the organization is activated and deleted
					if (!organization.isActivated() ){
						logger.error("organization is no activated");
						MessageException me = new MessageException(Constants.EXCEPTION_ORGANIZATION_UNACTIVATED);;
						me.setStatusCode(Constants.HTTP_CODE_LOGOUT);
						throw me;
					} else if (organization.isDeleted()){
						organization = null;
						user = null;
						request.getSession().setAttribute("user", null);
						logger.error("organization was deleted");
						MessageException me = new MessageException(Constants.EXCEPTION_ORGANIZATION_DELETED);;
						me.setStatusCode(Constants.HTTP_CODE_LOGOUT);
						throw me;
					}
					
					// Check if shibboleth is enabled or not in the organization	
					if (organization.isShibbolethEnabled()){
						if (user != null){
							// set user in session
							user.setOrganization(organization);
//							logger.debug("Information received from IDP");
							String firstname = (String) request.getAttribute("givenName");
//							logger.debug("givenName " + firstname);
							String lastname = (String) request.getAttribute("surname");
//							logger.debug("surname " + lastname);
							if (StringUtil.isBlank(user.getFirstname()) || StringUtil.isBlank(user.getLastname()) ||
							 (firstname != null && !firstname.toLowerCase().equals(user.getFirstname())) || 
							 (lastname != null && !lastname.toLowerCase().equals(user.getLastname()))){								
								user.setFirstname(firstname);
								user.setLastname(lastname);
								try{
									user = userDao.save(user);
								} catch(Exception e){
									e.printStackTrace();
									logger.error("Error to save the user to update the firstname and lastname " + firstname + lastname);
								}
							}
							request.getSession().setAttribute("user", user);
						} else {	
							// create user
							user = createUser(request, email, organization);
							
							// set user in session
							request.getSession().setAttribute("user", user);
						}
					} else{
						// User comes from reviewer login page
						
						if (user != null){
							user.setOrganization(organization);
							request.getSession().setAttribute("user", user);
						} else {
							logger.error("user is null");
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
		if (user != null){
			user = user.clone();	
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
	
	private boolean isCourseLecturer(Course course){
		boolean result = true;
		if (user != null){
			for(User lecturer: course.getLecturers()){
				result |= lecturer.getId() != null && lecturer.getId().equals(user.getId());
				result |= lecturer.getEmail()!= null && lecturer.getEmail().equals(user.getEmail());
			}
		} else {
			result = false;
		}
		return result;
//		return user == null ? false : course!= null && course.getLecturers().contains(user);
	}
	
	protected boolean isGuestOrAdminOrSuperAdminOrStaff(){
		return this.isAdmin() || this.isSuperAdmin() || this.isGuest() || this.isStaff();
	}
	
	protected boolean isGuest(){
		return user == null? false : user.isGuest();
	}
	
	protected boolean isStaff(){
		return user == null? false : user.isStaff();
	}
	
	protected boolean isStaff(Course course){
		return (isCourseLecturer(course) && isStaff());
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
		ConnectionUtil.logout(this.getThreadLocalRequest());
		user = null;
		organization = null;
	}
	
	
	/*
	 * Check if the organization properties are OK to activate it or not.
	 * If the properties are OK then the organization is activated 
	 */
	public Organization checkOrganizationProperties(Organization anOrganization) throws Exception{
		if (isSuperAdmin()){
			anOrganization = organizationManager.activateOrganization(user, anOrganization);
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
	private String getEmail(HttpServletRequest request, HttpServletResponse response){
		// Get email from request
		String email = null;
		if (request.getUserPrincipal() != null) {
			// Get email from reviewer login page
			email = request.getUserPrincipal().getName();
//			logger.debug("email " + email);
		} else if (request.getAttribute("email") != null){
			// Get email from AAF IdP property
			email = (String) request.getAttribute("email");
//			logger.debug("Information received from IDP");
//			logger.debug("email " + email);
		}
		return email;
	}
	
	/**
	 * Get organization from logged user or from the database using the domain of the email
	 * @param email email to get the domain
	 * @return Organization
	 * @throws MessageException
	 */
	private Organization getOrganization(String email, User user) throws MessageException{
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
		
		return organization;
	}
	
	/**
	 * Create a user in the database. This method should be called only the first time that a new user loggin in reviewer and organization use shibboleht (AAF login)
	 * @param request Request to obtain the givenName and the surname of the user
	 * @param email email of the user
	 * @return User
	 * @throws MessageException
	 */
	private User createUser(HttpServletRequest request, String email, Organization anOrganization) throws MessageException{
		User newUser = null;
		try{
			// add user into the database as a guest
//			logger.debug("Information received from IDP");
			String firstname = (String) request.getAttribute("givenName");
//			logger.debug("givenName " + firstname);
			String lastname = (String) request.getAttribute("surname");
//			logger.debug("surname " + lastname);
			newUser = userDao.getUserByEmail(email);
			
			if (newUser == null){
				newUser = new User();
				newUser.setFirstname(firstname);
				newUser.setLastname(lastname);
				newUser.setEmail(email);
				newUser.setOrganization(anOrganization);
				newUser.addRole(Constants.ROLE_GUEST);
				newUser = userDao.save(newUser);
			}
		} catch(Exception e){
			logger.error("error creating user with the IDP information");
			e.printStackTrace();
			
		}
		return newUser;
	}
	
	/**
	 * This method returns the Url of Google authorization page of user to access to his/her data
	 * This page return the code of authorization used to obtain the user token 
	 * @param currentUrl this page will be used to come back to the application after the authorization 
	 * @return String Google Authorization page
	 * @throws Exception
	 */
	public String getGoogleAuthorizationUrl(String currentUrl) throws Exception {
		String url = null;
		
		try{
			GoogleAuthHelper helper = new GoogleAuthHelper();
			url = helper.getGoogleAuthorizationUrl(user, currentUrl);
			
		} catch(Exception e){
			e.printStackTrace();
		}
		return url;
	}
		

	/**
	 * This method obtain the token and refresh token of the user to access to his/her data without login
	 * @param code code obtained from Google Authorization page
	 * @param currentUrl url to come back
	 * @return user wiht his/her tokens
	 * @throws Exception
	 */
	public User getUserToken(String code, String currentUrl) throws Exception {
		try{
			HttpServletRequest request = this.getThreadLocalRequest();
			GoogleAuthHelper helper = new GoogleAuthHelper();
			// Get user informatino to obtain the token
			user = helper.getUserTokens(user, code, currentUrl);
		} catch(Exception e){
			e.printStackTrace();
		}
		return user;
	}
	
}
