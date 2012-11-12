package au.edu.usyd.reviewer.server.controller;


import java.util.ArrayList;
import java.util.Calendar;
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
	@RequestMapping(value="user/mock/{email}", method = RequestMethod.POST)
	public @ResponseBody User mockUser(HttpServletRequest request, @PathVariable String email) throws MessageException {
		try{
			initialize(request);
			if (isAdminOrSuperAdmin()) {
				if (!StringUtil.isBlank(email)){
					User mockedUser = userDao.getUserByEmail(email);
					if (mockedUser != null){
						logger.info("Mocking user: " + mockedUser.getEmail());
						request.getSession().setAttribute("mockedUser", mockedUser);
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
	@RequestMapping(value="user/{userId}", method = RequestMethod.GET)
	public @ResponseBody User getUser(HttpServletRequest request, @PathVariable Long userId) throws MessageException{
		User resultUser = null;	
		try{
			
			initialize(request);
			if (isAdminOrSuperAdminOrGuest()){
				if (userId != null){
					resultUser = userDao.load(userId);
					if (resultUser != null){
						return resultUser;
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
	
	@RequestMapping(value="users/{lastname}/{organizationId}", method = RequestMethod.GET)
	public @ResponseBody  List<User> getUsers(HttpServletRequest request, @PathVariable String lastname, @PathVariable Long organizationId) throws MessageException { 
		List<User> users = new ArrayList<User>();
		try{
			initialize(request);
			if (isAdminOrSuperAdmin()) {
				if (!StringUtils.isBlank(lastname)){
					Organization organization = null;
					if (organizationId != null){
						organization = organizationDao.load(organizationId);
					} else {
						organization = user.getOrganization();
						if (organization == null){
							throw new MessageException(Constants.EXCEPTION_ORGANIZATION_NOT_FOUND);
						}
					}
					users = userDao.geUsers(lastname,organization);
				} else {
					throw new MessageException(Constants.EXCEPTION_USER_NOT_FOUND);
				}
			} else {
				throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
			}
		} catch( Exception e){
			if (e instanceof MessageException){
				throw (MessageException) e;
			} else {
				e.printStackTrace();
				throw new MessageException(Constants.EXCEPTION_GET_USERS);
			}
		}
		return users;
	}
	
	@RequestMapping(value="user/logged", method = RequestMethod.GET)
	public @ResponseBody User getLoggedUser(HttpServletRequest request) throws MessageException{
		try{
			initialize(request);
			if (this.isAdminOrSuperAdminOrGuest()){
				return user;
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

	
	@RequestMapping(value="user/mocked", method = RequestMethod.GET)
	public @ResponseBody User getMockedUser(HttpServletRequest request) throws MessageException{
		try{
			initialize(request);
			User mockedUser = super.getMockedUser(request);
			if (mockedUser != null && user != null && !mockedUser.equals(user)){
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

	
}
