package au.edu.usyd.reviewer.server.controller;


import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.StringUtil;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;

/**
 * Controller class for users. 
 * @author mdagraca
 */

@Controller
@RequestMapping("/User")
public class UserController extends ReviewerController{

	/**
	 * Return logged user
	 * @param request HttpServletRequest used to initialize the controller
	 * @return User logged user
	 * @throws MessageException message to the user
	 */
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody User getLoggedUser(HttpServletRequest request) throws MessageException{
		user = super.getUser(request);
		if (!(user != null && ( user.isAdmin() || user.isGuest() || user.isSuperAdmin())))
			throw new MessageException( Constants.EXCEPTION_PERMISSION_DENIED);
		else
			return user;
	}

	/**
	 * Mock the user received as parameter in memory
	 * @param request HttpServletRequest used to initialize the controller
	 * @param aUser user to mock
	 * @return Mocked user
	 * @throws MessageException
	 */
	@RequestMapping(value="/mocked", method = RequestMethod.POST)
	public @ResponseBody User mockUser(HttpServletRequest request, @RequestBody User aUser) throws MessageException {
		try{
			initialize(request);
			if (isAdminOrSuperAdmin()) {
				String email = null;
				if ( aUser.getEmail() != null && !StringUtil.isBlank(aUser.getEmail())){
					email = aUser.getEmail();
				} else if (aUser.getUsername() != null && !StringUtil.isBlank(aUser.getUsername())){
					email = aUser.getUsername() + "@" + organization.getGoogleDomain();
				} else {
					throw new MessageException(Constants.EXCEPTION_USERNAME_OR_EMAIL_NO_EXIST);
				}
				User mockedUser = userDao.getUserByEmail(email);
				if (mockedUser != null){
					logger.info("Mocking user: " + mockedUser.getEmail());
					request.getSession().setAttribute("mockedUser", mockedUser);
					return mockedUser;
				} else{
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
	
//	@RequestMapping(value="/login", method = RequestMethod.POST)
//	public @ResponseBody User login(HttpServletRequest request, @RequestParam("email") String email, @RequestParam("pqssword") String password ) throws MessageException{
//		try{
//			if (email != null && password != null) {
//				User user = userDao.getUserByEmail(email);
//				if (user != null){
//					if (user.getPassword() != null && user.getPassword().equals(password)){
//						request.getSession().setAttribute("user", user);
//						logger.debug("Loging in user: " + user.getEmail());
//						return user;
//					} else {
//						throw new MessageException(Constants.EXCEPTION_LOGIN_WRONG);
//					}
//				} else {
//					throw new MessageException(Constants.EXCEPTION_LOGIN_WRONG);
//				}
//			} else {
//				throw new MessageException(Constants.EXCEPTION_LOGIN_WRONG);
//			}
//		} catch(Exception e){
//			e.printStackTrace();
//			throw new MessageException(Constants.EXCEPTION_LOGIN_WRONG);
//		}
//	}
	
//	@RequestMapping(value="/logout", method = RequestMethod.POST)
//	public @ResponseBody void logout(HttpServletRequest request) throws MessageException{
//		try{
//			User user = super.getUser(request);
//			if ( user != null){
//				logger.debug("Logout in user: " + user.getEmail());
//				request.getSession().setAttribute("user", null);
//			}
//		} catch(Exception e){
//			e.printStackTrace();
//			throw new MessageException(Constants.EXCEPTION_LOGOUT);
//		}
//	}

	
}
