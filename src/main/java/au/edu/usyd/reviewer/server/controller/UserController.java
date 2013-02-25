package au.edu.usyd.reviewer.server.controller;


import java.util.HashSet;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gwt.core.client.JsonUtils;

import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.OrganizationProperty;
import au.edu.usyd.reviewer.client.core.ReviewerProperty;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.StringUtil;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.server.util.ObjectConverter;

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
	@RequestMapping(value="users/mock", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> mockUser(HttpServletRequest request, 
													@RequestParam(value="username", required=true) String username) throws MessageException {
		MessageException me = null;
		try{
			initialize(request);
			if (isAdminOrSuperAdmin()) {
				if (!StringUtil.isBlank(username)){
					String email = username + "@" + organization.getGoogleDomain();
					User mockedUser = organizationManager.getUserByEmail(email);
					if (mockedUser != null){
//						logger.info("Mocking user: " + mockedUser.getEmail());
						request.getSession().setAttribute("mockedUser", mockedUser);
						Organization anOrganization = mockedUser.getOrganization();
						anOrganization.setOrganizationProperties(new HashSet<OrganizationProperty>());
						mockedUser.setOrganization(anOrganization);
						Map<String,Object> mockedUserMap = ObjectConverter.convertObjectInMap(mockedUser,"","",0);
						return mockedUserMap;
					} else{
						me= new MessageException(Constants.EXCEPTION_USERNAME_NO_EXIST);
						me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
						throw me;
					}
				} else {
					me = new MessageException(Constants.EXCEPTION_USERNAME_NO_EXIST);
					me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
					throw me;
				}
			} else {
				me = new MessageException( Constants.EXCEPTION_PERMISSION_DENIED);
				me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
				throw me;
			}
		} catch(Exception e){
			if ( e instanceof MessageException){
				me = (MessageException) e;
			} else {
				e.printStackTrace();
				me = new MessageException(Constants.EXCEPTION_MOCKING_USER);
			}
			if ( me.getStatusCode() == 0){
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
		    }
		    throw me;
		}
	}
	
	/**
	 * Return the user with id equals to {id}
	 * @param request HttpServletRequest used to initialize the controller
	 * @return User user with id equals to userId
	 * @throws MessageException message to the user
	 */
	@RequestMapping(value="users/{id}", method = RequestMethod.GET)
	public @ResponseBody Map<String,Object> getUser(HttpServletRequest request, @PathVariable Long id, 
													@RequestParam(value="include", required=false) String include) throws MessageException{
		User resultUser = null;	
		MessageException me = null;
		try{
			initialize(request);
			if (isAdminOrSuperAdmin()){
				if (id != null){
					resultUser = organizationManager.getUser(id);
					if (isAdmin() && resultUser != null && resultUser.getOrganization() != null && 
							!user.getOrganization().getId().equals(resultUser.getOrganization().getId())){
						me = new MessageException( Constants.EXCEPTION_PERMISSION_DENIED);
						me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
						throw me;
					}
				} else{
					me = new MessageException(Constants.EXCEPTION_USER_NOT_FOUND);
					me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
					throw me;
				}
			} else if (isGuest() && user.getId().equals(id)){
				resultUser = user.clone();
			} else{
				me = new MessageException( Constants.EXCEPTION_PERMISSION_DENIED);
				me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
				throw me;
			}
			if (resultUser != null){
				Organization anOrganization = resultUser.getOrganization();
				if (anOrganization!= null && organization.getId().equals(anOrganization.getId())){
					for(OrganizationProperty organizationProperty : anOrganization.getOrganizationProperties()){
						if (organizationProperty != null){
							organizationProperty.setOrganization(null);
							ReviewerProperty reviewerProperty = organizationProperty.getProperty();
							reviewerProperty.setOrganizationProperties(new HashSet<OrganizationProperty>());
						}
					}
					resultUser.setOrganization(anOrganization);
					Map<String,Object> userMap = ObjectConverter.convertObjectInMap(resultUser, include,"",0);
					return userMap;
				} else {
					me = new MessageException( Constants.EXCEPTION_PERMISSION_DENIED);
					me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
					throw me;
				}
			} else {
				me = new MessageException(Constants.EXCEPTION_USER_NOT_FOUND);
				me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
				throw me;
			}
		}
		catch(Exception e){
			e.printStackTrace();
			if ( e instanceof MessageException){
				me = (MessageException) e;
			} else {
				me = new MessageException(Constants.EXCEPTION_GET_USER);
			}
			if ( me.getStatusCode() == 0){
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
		    }
			throw me;
		}
	}
	
	
	/**
	 * This method returns the logged user
	 * @param request HttpServletRequest used to initialize the controller
	 * @return User logged user
	 * @throws MessageException message to the user
	 */
	@RequestMapping(value="users/logged", method = RequestMethod.GET)
	public @ResponseBody Map<String,Object> getLoggedUser(HttpServletRequest request,
													 @RequestParam(value="include", required=false) String include) throws MessageException{
		MessageException me = null;
		try{
			initialize(request);
			if (isAdminOrSuperAdminOrGuest()){
				User aUser = user.clone();
				Organization anOrganization = aUser.getOrganization();
				for(OrganizationProperty organizationProperty : anOrganization.getOrganizationProperties()){
					if (organizationProperty != null){
						organizationProperty.setOrganization(null);
						ReviewerProperty reviewerProperty = organizationProperty.getProperty();
						reviewerProperty.setOrganizationProperties(new HashSet<OrganizationProperty>());
					}
				}
				aUser.setOrganization(anOrganization);
				Map<String, Object> userMap = ObjectConverter.convertObjectInMap(aUser,include,"",0);
				return userMap;
			} else {
				me = new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
				me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
				throw me;
			}
		} catch( Exception e){
			if (e instanceof MessageException){
				me = (MessageException) e;
			} else {
				e.printStackTrace();
				me = new MessageException(Constants.EXCEPTION_GET_LOGGED_USER);
			}
			if ( me.getStatusCode() == 0){
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
		    }
		    throw me;
		}
	}

	
	/**
	 * This method returns the mocked user
	 * @return User mocked user
	 * @throws MessageException message to the user
	 */
	@RequestMapping(value="users/mocked", method = RequestMethod.GET)
	public @ResponseBody Map<String,Object> getMockedUserMap(HttpServletRequest request,
															@RequestParam(value="include", required=false) String include) throws MessageException{
		MessageException me = null;
		try{
			initialize(request);
			if (isAdminOrSuperAdmin()){
				User mockedUser = super.getMockedUser(request);
				if (mockedUser != null && user != null && !mockedUser.equals(user)){
					Organization anOrganization = mockedUser.getOrganization();
					for(OrganizationProperty organizationProperty : anOrganization.getOrganizationProperties()){
						if (organizationProperty != null){
							organizationProperty.setOrganization(null);
							ReviewerProperty reviewerProperty = organizationProperty.getProperty();
							reviewerProperty.setOrganizationProperties(new HashSet<OrganizationProperty>());
						}
					}
					Map<String,Object> userMap = ObjectConverter.convertObjectInMap(mockedUser,include,"",0);
					return userMap;
				} else {
					me = new MessageException(Constants.EXCEPTION_MOCKED_USER_NOT_FOUND);
					me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
					throw me;
				}
			} else {
				me = new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
				me.setStatusCode(Constants.HTTP_CODE_FORBIDDEN);
				throw me;
			}
			
		} catch( Exception e){
			if (e instanceof MessageException){
				me = (MessageException) e;
			} else {
				e.printStackTrace();
				me = new MessageException(Constants.EXCEPTION_MOCKED_USER);
			}
			if ( me.getStatusCode() == 0){
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
		    }
		    throw me;
		}
		
	}
}
