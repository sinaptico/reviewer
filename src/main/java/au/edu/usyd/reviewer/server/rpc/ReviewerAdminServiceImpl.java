package au.edu.usyd.reviewer.server.rpc;


import java.security.Principal;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.OrganizationProperty;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.StringUtil;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.client.reviewerAdmin.ReviewerAdminService;
import au.edu.usyd.reviewer.server.OrganizationManager;
import au.edu.usyd.reviewer.server.UserDao;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
/**
 * Implementation of ReviewerService
 * It has methods to manage organizations, users and reviewer properties
 * @author mdagraca
 *
 */
@Service("reviewerAdminService")
public class ReviewerAdminServiceImpl extends RemoteServiceServlet implements ReviewerAdminService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static final long serialVersionUID = 1L;
	// logged user
	private User user = null;
	
	// OrganizationManager is the only class to interact with the database to manage users, organizations and prperties
	private OrganizationManager organizationManager = OrganizationManager.getInstance();
	
	@Override
	public Organization saveOrganization(Organization organization) throws Exception {
		if (isAdminOrSuperAdmin()){
			if (!StringUtil.isBlank(organization.getName())){
				return  organizationManager.saveOrganization(organization); 
			} else {
				throw new MessageException(Constants.EXCEPTION_ORGANIZATION_EMPTY);
			}
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
	}
	
	public Collection<Organization> getOrganizations(String organizationName) throws Exception{
		if (isSuperAdmin()){
			return organizationManager.getOrganizations(organizationName);
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
	}
	
	public Organization getOrganization(String organizationName) throws Exception{
		if (isAdminOrSuperAdmin()){
			return organizationManager.getOrganization(organizationName);
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
	}
	
	public OrganizationProperty saveOrganizationProperty(OrganizationProperty organizationProperty) throws Exception {
		if (isAdminOrSuperAdmin()){
			return organizationManager.saveOrganizationProperty(organizationProperty);
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
	}
	
	public Organization deleteOrganization(Organization organization) throws Exception{
		if (isSuperAdmin()){
			return organizationManager.deleteOrganization(organization);
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
	}
	
	public Organization deleteOrganizationProperties(Organization organization) throws Exception{
		if (isSuperAdmin()){
			return organizationManager.deleteOrganizationProperties(organization);
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
	}
	
	public OrganizationProperty deleteOrganizationProperty(OrganizationProperty property) throws Exception{
		if (isSuperAdmin()){
			return organizationManager.deleteOrganizationProperty(property);
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
	}

	public Collection<User> getUsers(User user) throws Exception{
		if (isAdminOrSuperAdmin()){
			return organizationManager.getUsers(user);
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
	}
	
	public Collection<User> getUsers(String firstName, String lastName,int startRow, int maxRows) throws Exception{
		if (isAdminOrSuperAdmin()){
			return organizationManager.getUsers(firstName, lastName, startRow, maxRows);
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
	}
	
	public Collection<User> getUsers(Organization organization, String firstName, String lastName, int startRow, int maxRows) throws Exception{
		if (isAdminOrSuperAdmin()){
			return organizationManager.getUsers(organization, firstName,lastName, startRow, maxRows);
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
	}
	
	private boolean isAdmin() {
		User user = getUser();
		return user == null ? false : user.isAdmin();
	}
		
	private boolean isSuperAdmin() {
		User user = getUser();
		return user == null ? false : user.isSuperAdmin();
	}
	
	private boolean isAdminOrSuperAdmin(){
		return this.isAdmin() || this.isSuperAdmin();
	}
	
	public User getUser() {
		
		try {
			HttpServletRequest request = this.getThreadLocalRequest();
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
		} catch (MessageException e) {
			e.printStackTrace();
		}
		return user;
	}

	public User saveUser(User aUser) throws Exception {
		if (isAdminOrSuperAdmin()){
			UserDao userDao = UserDao.getInstance();
			aUser = userDao.save(aUser);
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
		return aUser;
	}

	@Override
	public User getLoggedUser() throws Exception {
		return getUser();
	}
}
