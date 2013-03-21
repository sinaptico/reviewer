package au.edu.usyd.reviewer.client.reviewerAdmin;

import java.util.Collection;


import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.OrganizationProperty;
import au.edu.usyd.reviewer.client.core.User;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
/**
 * This class has the methods to manage users, organizations and reviewer properties.
 * The implementations of these methods are in the server side
 * @author mdagraca
 *
 */
@RemoteServiceRelativePath("reviewerAdminService")
public interface ReviewerAdminService extends RemoteService {
	
	public Organization saveOrganization(Organization organization) throws Exception;
	
	public Collection<Organization> getOrganizations(String organizationName) throws Exception;

	public Organization getOrganization(String organizationName) throws Exception;
	
	public OrganizationProperty saveOrganizationProperty(OrganizationProperty organizationProperty) throws Exception;
	
	public Organization deleteOrganization(Organization organization) throws Exception;
	
	public Collection<User> getUsers(User user) throws Exception;
	
	public User saveUser(User user) throws Exception;
	
	public User getLoggedUser() throws Exception;

	public Organization checkOrganizationProperties(Organization organization) throws Exception;
	
	public void logout() throws Exception;
}
