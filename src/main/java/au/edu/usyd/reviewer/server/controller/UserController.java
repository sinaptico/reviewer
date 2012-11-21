package au.edu.usyd.reviewer.server.controller;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.OrganizationProperty;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.StringUtil;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.server.OrganizationDao;

/**
 * Controller class for users. 
 * @author mdagraca
 */

@Controller
@RequestMapping("/")
public class UserController extends ReviewerController{

	/**
	 * Mock the user received as parameter in memory
	 * @param request HttpServletRequest used to initialize the controller
	 * @param aUser user to mock
	 * @return Mocked user
	 * @throws MessageException
	 */
	@RequestMapping(value="users/mock/{email}", method = RequestMethod.POST)
	public @ResponseBody User mockUser(HttpServletRequest request, @PathVariable String email) throws MessageException {
		try{
			initialize(request);
			if (isAdminOrSuperAdmin()) {
				if (!StringUtil.isBlank(email)){
					User mockedUser = userDao.getUserByEmail(email);
					if (mockedUser != null){
						logger.info("Mocking user: " + mockedUser.getEmail());
						request.getSession().setAttribute("mockedUser", mockedUser);
						Organization anOrganization = mockedUser.getOrganization();
						anOrganization.setOrganizationProperties(new HashSet<OrganizationProperty>());
						mockedUser.setOrganization(anOrganization);
						return mockedUser;
					} else{
						throw new MessageException(Constants.EXCEPTION_USERNAME_OR_EMAIL_NO_EXIST);
					}
				} else {
					throw new MessageException(Constants.EXCEPTION_USERNAME_OR_EMAIL_NO_EXIST);
				}
			} else {
				throw new MessageException( Constants.EXCEPTION_PERMISSION_DENIED);
			}
		} catch(Exception e){
			if ( e instanceof MessageException){
				throw (MessageException) e;
			} else {
				e.printStackTrace();
				throw new MessageException(Constants.EXCEPTION_MOCKING_USER);
			}
		}
	}
	
	/**
	 * Return the user with id equals to userId
	 * @param request HttpServletRequest used to initialize the controller
	 * @return User user with id equals to userId
	 * @throws MessageException message to the user
	 */
	@RequestMapping(value="users/{userId}", method = RequestMethod.GET)
	public @ResponseBody User getUser(HttpServletRequest request, @PathVariable Long userId) throws MessageException{
		User resultUser = null;	
		try{
			
			initialize(request);
			if (isAdminOrSuperAdminOrGuest()){
				if (userId != null){
					resultUser = userDao.load(userId);
					if (resultUser != null){
						Organization anOrganization = resultUser.getOrganization();
						if (anOrganization!= null && organization.getId().equals(anOrganization.getId())){
							anOrganization.setOrganizationProperties(new HashSet<OrganizationProperty>());
							resultUser.setOrganization(anOrganization);
							return resultUser;
						} else {
							throw new MessageException( Constants.EXCEPTION_PERMISSION_DENIED);
						}
					} else {
						throw new MessageException(Constants.EXCEPTION_USER_NOT_FOUND);
					}
				} else{
					throw new MessageException(Constants.EXCEPTION_USER_NOT_FOUND);
				}
			}else
				throw new MessageException( Constants.EXCEPTION_PERMISSION_DENIED);

		}
		catch(Exception e){
			if ( e instanceof MessageException){
				throw (MessageException) e;
			} else {
				e.printStackTrace();
				throw new MessageException(Constants.EXCEPTION_GET_USER);
			}
		}
	}
	
	
	/**
	 * This method returns the logged user
	 * @param request HttpServletRequest used to initialize the controller
	 * @return User logged user
	 * @throws MessageException message to the user
	 */
	@RequestMapping(value="users/logged", method = RequestMethod.GET)
	public @ResponseBody User getLoggedUser(HttpServletRequest request) throws MessageException{
		try{
			initialize(request);
			if (this.isAdminOrSuperAdminOrGuest()){
				User aUser = user.clone();
				Organization anOrganization = aUser.getOrganization();
				anOrganization.setOrganizationProperties(new HashSet<OrganizationProperty>());
				aUser.setOrganization(anOrganization);
				return aUser;
			} else {
				throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
			}
		} catch( Exception e){
			if (e instanceof MessageException){
				throw (MessageException) e;
			} else {
				e.printStackTrace();
				throw new MessageException(Constants.EXCEPTION_GET_LOGGED_USER);
			}
		}
	}

	
	/**
	 * This method returns the mocked user
	 * @return User mocked user
	 * @throws MessageException message to the user
	 */
	@RequestMapping(value="users/mocked", method = RequestMethod.GET)
	public @ResponseBody User getMockedUser(HttpServletRequest request) throws MessageException{
		try{
			initialize(request);
			User mockedUser = super.getMockedUser(request);
			if (mockedUser != null && user != null && !mockedUser.equals(user)){
				Organization anOrganization = mockedUser.getOrganization();
				anOrganization.setOrganizationProperties(new HashSet<OrganizationProperty>());
				mockedUser.setOrganization(anOrganization);
				return mockedUser;
			} else {
				throw new MessageException(Constants.EXCEPTION_MOCKED_USER_NOT_FOUND);
			}
			
		} catch( Exception e){
			if (e instanceof MessageException){
				throw (MessageException) e;
			} else {
				e.printStackTrace();
				throw new MessageException(Constants.EXCEPTION_MOCKED_USER);
			}
		}
	}


//	/**
//	 * Return a list of user whose lastname start with the lastname received as parameter and belong to the organization with id equals to organizationId
//	 * @param request HttpServletRequest used to initialize the controller
//	 * @param lastname lastname of the users
//	 * @param organizationId id of the organization which users belong to
//	 * @return List<User> list of users
//	 * @throws MessageException message to the user
//	 */
//	@RequestMapping(value="users/{lastname}/{organizationId}", method = RequestMethod.GET)
//	public @ResponseBody  List<User> getUsers(HttpServletRequest request, @PathVariable String lastname, @PathVariable Long organizationId) throws MessageException { 
//		List<User> users = new ArrayList<User>();
//		try{
//			initialize(request);
//			if (isAdminOrSuperAdmin()) {
//				if (!StringUtils.isBlank(lastname)){
//					Organization anOrganization = null;
//					if (organizationId != null){
//						anOrganization = organizationDao.load(organizationId);
//					} else {
//						anOrganization = user.getOrganization();
//						if (anOrganization == null){
//							throw new MessageException(Constants.EXCEPTION_ORGANIZATION_NOT_FOUND);
//						}
//					}
//					
//					if (anOrganization != null){
//						anOrganization.setOrganizationProperties(new HashSet<OrganizationProperty>());
//					}
//					users = userDao.geUsers(lastname,organization);
//					for(User aUser:users){
//						if (aUser != null){			
//							aUser.setOrganization(anOrganization);
//						}
//					}
//				} else {
//					throw new MessageException(Constants.EXCEPTION_USER_NOT_FOUND);
//				}
//			} else {
//				throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
//			}
//		} catch( Exception e){
//			if (e instanceof MessageException){
//				throw (MessageException) e;
//			} else {
//				e.printStackTrace();
//				throw new MessageException(Constants.EXCEPTION_GET_USERS);
//			}
//		}
//		return users;
//	}

}
