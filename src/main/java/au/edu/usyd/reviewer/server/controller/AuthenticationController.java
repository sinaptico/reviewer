package au.edu.usyd.reviewer.server.controller;


import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.StringUtil;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;

@Controller
@RequestMapping("/")
public class AuthenticationController extends au.edu.usyd.reviewer.server.controller.Controller{

	@RequestMapping(value="/user", method = RequestMethod.GET)
	public @ResponseBody User getLoggedUser(HttpServletRequest request) throws Exception{
		user = getUser(request);
		return user;
	}

	@RequestMapping(value="/user/", method = RequestMethod.POST)
	public @ResponseBody User mockUser(HttpServletRequest request, @RequestBody User aUser) throws Exception {
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
	}

}
