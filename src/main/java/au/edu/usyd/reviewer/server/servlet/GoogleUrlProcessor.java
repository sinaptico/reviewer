package au.edu.usyd.reviewer.server.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.StringUtil;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.server.OrganizationManager;
import au.edu.usyd.reviewer.server.UserDao;
import au.edu.usyd.reviewer.server.oauth.GoogleAuthHelper;

/**
 * This class intercepts every link to Google to add a access_token as parameter
 * @author Marie
 *
 */
public class GoogleUrlProcessor extends HttpServlet {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static final long serialVersionUID = 1L;
	protected UserDao userDao = UserDao.getInstance();
	private User user;
	private Organization organization;
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		initialize(request,response);
		String redirect = request.getParameter("redirect");
		try{
			//get redirect url
			// The redirect url is the url of the google doc 
			if (redirect != null) {
				if (user!= null){
					GoogleAuthHelper helper = new GoogleAuthHelper();
					// verify if the tokes has expired and get a new one
					user = helper.refreshUserTokens(user);
//					String accessToken = user.getGoogleToken();
//					logger.error("Access Token " + accessToken);
					// add the access_token parameter to the url
//					redirect += "&access_token=" + accessToken;
//					response.setHeader("Authorization", "Bearer " + accessToken);
					response.setHeader("Content-Type", "application/http");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Failed to access to Google Drive" + e.getMessage());
		} finally{
			logger.error("redirect " + redirect);
			response.sendRedirect(redirect);
		}
	}
	
	
	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
    }

	private void initialize(HttpServletRequest request, HttpServletResponse response){
		// get the logged user
		user = getUser(request, response);
		if (user != null){
			organization = user.getOrganization();
		}
	}
	
	/**
	 * Get the logged user and if the logged user is an admin or supper admin get the mocked user
	 * @param request
	 * @param response
	 * @return
	 */
	private User getUser(HttpServletRequest request, HttpServletResponse response) { 
		try {
			user = getLoggedUser(request, response);
			if (user != null && user.isSuperAdmin() || user.isAdmin()) {
				User mockedUser = (User) request.getSession().getAttribute("mockedUser");
				if (mockedUser != null) {
					user = mockedUser;
					if ( mockedUser.getOrganization() == null){
						UserDao userDao = UserDao.getInstance();
						user = userDao.getUserByEmail(mockedUser.getEmail());
					}
				} 
			}
		} catch (MessageException e) {
			e.printStackTrace();
		}
		return user;
	}
	
	/*
	 * Get the logged user taking in condierating if the organization of the user uses shibboleth or not
	 */
	public User getLoggedUser(HttpServletRequest request,HttpServletResponse response) throws MessageException{
		try {			
			
			// Get user from session
			Object obj = request.getSession().getAttribute("user");
			if (obj != null){
				user = (User) obj;
			}
			
			// getEmail
			String email = getEmail(request,response);
			
			if (email == null && user == null){
				// ERROR we need the email o the user to continue. 
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
					logger.error("Organization is null for user " + email + " so we can not verify the shibboleth property");
					MessageException me = new MessageException(Constants.EXCEPTION_GET_LOGGED_USER);;
					me.setStatusCode(Constants.HTTP_CODE_LOGOUT);
					throw me;
				} else {
					// Verify if the organization is activated and deleted
					if (!organization.isActivated() ){
						logger.error("organization " + organization.getName() + " is no activated");
						MessageException me = new MessageException(Constants.EXCEPTION_ORGANIZATION_UNACTIVATED);;
						me.setStatusCode(Constants.HTTP_CODE_LOGOUT);
						throw me;
					} else if (organization.isDeleted()){
						organization = null;
						user = null;
						logger.error("organization " + organization.getName() + "  was deleted");
						request.getSession().setAttribute("user", null);
						MessageException me = new MessageException(Constants.EXCEPTION_ORGANIZATION_DELETED);;
						me.setStatusCode(Constants.HTTP_CODE_LOGOUT);
						throw me;
					}
					
					// Check if shibboleth is enabled or not in the organization	
					if (organization.isShibbolethEnabled()){
						if (user != null){
							// set user in session
							user.setOrganization(organization);
							if (StringUtil.isBlank(user.getFirstname()) || StringUtil.isBlank(user.getLastname())){
								String firstname = (String) request.getAttribute("givenName");
								String lastname = (String) request.getAttribute("surname");
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
								user = userDao.save(user);
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
		return user;
	}

	/**
	 * Create a user into the database wihte the information received from shibboleth (AAF)
	 * @param request
	 * @param email
	 * @param anOrganization
	 * @return
	 * @throws MessageException
	 */
	private User createUser(HttpServletRequest request, String email, Organization anOrganization) throws MessageException{
		User newUser = null;
		try{
			// add user into the database as a guest
			String firstname = (String) request.getAttribute("givenName");
			String lastname = (String) request.getAttribute("surname");
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
	 * Get the email of the user
	 * @param request
	 * @param response
	 * @return
	 */
	private String getEmail(HttpServletRequest request, HttpServletResponse response){
		// Get email from request
		String email = null;
		if (request.getUserPrincipal() != null) {
			// Get email from reviewer login page
			email = request.getUserPrincipal().getName();
		} else if (request.getAttribute("email") != null){
			// Get email from AAF IdP property
			email = (String) request.getAttribute("email");
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
}
