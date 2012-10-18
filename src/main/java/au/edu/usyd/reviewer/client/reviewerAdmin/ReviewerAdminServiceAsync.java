package au.edu.usyd.reviewer.client.reviewerAdmin;

import java.util.Collection;

import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.OrganizationProperty;
import au.edu.usyd.reviewer.client.core.User;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * The Interface ReviewerAdminService (Asynchronous) used to manage the organizations, users and reviewer properties 
 * from the  Reviewer Administration module.
 * @author mdagraca
 *
 */
public interface ReviewerAdminServiceAsync {

	
	public void saveOrganization(Organization organization, AsyncCallback<Organization> callback);
	
	public void getOrganizations(String organizationName,AsyncCallback<Collection<Organization>> callback);
	
	public void getOrganization(String organizationName, AsyncCallback<Organization> callback);
	
	public void saveOrganizationProperty(OrganizationProperty organizationProperty, AsyncCallback<OrganizationProperty> callback);
	
	public void deleteOrganization(Organization organization, AsyncCallback<Organization> callback);
	
	public void deleteOrganizationProperties(Organization organization, AsyncCallback<Organization> callback);

	public void deleteOrganizationProperty(OrganizationProperty property, AsyncCallback<OrganizationProperty> callback);

	public void getUsers(User user, AsyncCallback<Collection<User>> callback);
	
	public void saveUser(User user, AsyncCallback<User> callback);
	
	public void getLoggedUser(AsyncCallback<User> callback);
}
