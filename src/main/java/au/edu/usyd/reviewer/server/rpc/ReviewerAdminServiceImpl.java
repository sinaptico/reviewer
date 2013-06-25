package au.edu.usyd.reviewer.server.rpc;

import java.util.Collection;

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
import au.edu.usyd.reviewer.server.UserDao;

/**
 * Implementation of ReviewerService
 * It has methods to manage organizations, users and reviewer properties
 * @author mdagraca
 *
 */
@Service("reviewerAdminService")
public class ReviewerAdminServiceImpl extends ReviewerServiceImpl implements ReviewerAdminService {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static final long serialVersionUID = 1L;
	
	@Override
	public Organization saveOrganization(Organization organization) throws Exception {
		if (isSuperAdmin()){
			if (!StringUtil.isBlank(organization.getName())){
				return  organizationManager.saveOrganization(organization,true); 
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
		if (isSuperAdmin()){
			return organizationManager.getOrganization(organizationName);
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
	}
	
	public OrganizationProperty saveOrganizationProperty(OrganizationProperty organizationProperty) throws Exception {
		if (isSuperAdmin()){
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
	

	public Collection<User> getUsers(User user) throws Exception{
		if (isSuperAdmin()){
			return organizationManager.getUsers(user);
		} else {
			throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
		}
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

}
