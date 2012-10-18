package au.edu.usyd.reviewer.server;

import java.util.ArrayList;


import java.util.Collection;
import java.util.List;

import java.util.Set;


import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.OrganizationProperty;
import au.edu.usyd.reviewer.client.core.ReviewerProperty;
import au.edu.usyd.reviewer.client.core.User;
//import au.edu.usyd.reviewer.client.core.util.AESCipher;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.StringUtil;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;

/**
 * This class admins the organizations.
 * It's a singleton
 * @author mdagraca
 *
 */
public class OrganizationManager {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	// Organization dao
	private OrganizationDao organizationDao;
	private OrganizationPropertyDao organizationPropertyDao;
	private ReviewerPropertyDao propertyDao;
	private UserDao userDao;
	private CourseDao courseDao;
	
	private static OrganizationManager organizationManager = null;
	
	/***
	 * Constructor 
	 */
	private OrganizationManager(){
		// initialize Organization dao
		try {
			organizationDao = OrganizationDao.getInstance();
			organizationPropertyDao =  OrganizationPropertyDao.getInstance();
			propertyDao =  ReviewerPropertyDao.getInstance();
			userDao = UserDao.getInstance();
			courseDao = CourseDao.getInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Singleton. Get the only instance of OrganizationManager class
	 * @return OrganizationManager the only instance of OrganizationManager
	 */
	public static OrganizationManager getInstance(){
		if (organizationManager == null){
			organizationManager = new OrganizationManager();
		}
		return organizationManager;
	}
	
	
	/**
	 * This method calls organizationDao to save the organization received as parameter
	 * @param organization organization to save
	 */
	public Organization saveOrganization(Organization organization) throws Exception{
		Organization organizationSaved = null;
		Organization otherOrganization = getOrganization(organization.getName());
		if ( otherOrganization == null || (otherOrganization != null && otherOrganization.getId().equals(organization.getId()))){
			organizationSaved = organizationDao.save(organization);;
			if (!organizationSaved.hasProperties()){
				organization = addProperties(organization);
				saveOrganizationProperties(organization.getOrganizationProperties());
			} else {
				saveOrganizationProperties(organization.getOrganizationProperties());
			}
				
		} else {
			// there is an organization with the same name
			throw new MessageException(Constants.EXCEPTION_ORGANIZATION_EXISTS);
		}
		return organizationSaved; 
	}
	
	/**
	 * This method return the organization whose id is equal to the organization id received as parameter
	 * @param organizationId id of the organization to look for
	 * @return organization with id equals to the organization id received as parameter
	 */
	public Organization getOrganization(Long organizationId)throws MessageException{
		Organization organization = organizationDao.load(organizationId);
		return organization;
	}
	
	/**
	 * This method return the organization whose name is equal to the organization name received as parameter
	 * @param name name of the organization to look for
	 * @return organization with name equals to the organization name received as parameter
	 */
	public Organization getOrganization(String name) throws MessageException{
		Organization organization = organizationDao.load(name);
		return organization;
	}
	
	/**
	 * Return all the organization whose name start with the organization name received as parameter
	 * @param organizationName
	 * @return
	 */
	public List<Organization> getOrganizations(String organizationName)throws MessageException{
		List organizations = organizationDao.getOrganizations(organizationName);
		return organizations;
	}
	
	/**
	 * Gets the reviewer properties and add them to the organization
	 * @param organization organization owner of the properties
	 * @return organizaton with the reviewer properties
	 */
	private Organization addProperties(Organization organization)throws MessageException{
		ReviewerProperty aagInsertToExcelPath = propertyDao.load(Constants.AGG_INSERT_TO_EXCEL_PATH);
		organization.addProperty(aagInsertToExcelPath, null);
		
		ReviewerProperty aggLoadExcelPath = propertyDao.load(Constants.AGG_LOAD_EXCEL_PATH);
		organization.addProperty(aggLoadExcelPath, Constants.AGG_INSERT_TO_EXCEL_PATH_VALUE);
		
		ReviewerProperty reviewerAdminUsers = propertyDao.load(Constants.REVIEWER_ADMIN_USERS);
		organization.addProperty(reviewerAdminUsers,null);
		
		ReviewerProperty reviewerDocumentsHome = propertyDao.load(Constants.REVIEWER_DOCUMENTS_HOME);
		organization.addProperty(reviewerDocumentsHome, Reviewer.getDocumentsHome() + organization.getName() );
		
		ReviewerProperty reviewerEmailPassword = propertyDao.load(Constants.REVIEWER_EMAIL_PASSWORD);
		organization.addProperty(reviewerEmailPassword, null);
		
		ReviewerProperty reviewerEmailUsername = propertyDao.load(Constants.REVIEWER_EMAIL_USERNAME);
		organization.addProperty(reviewerEmailUsername, null);
		
		ReviewerProperty reviewerEmptyDocument = propertyDao.load(Constants.REVIEWER_EMPTY_DOCUMENT);
		organization.addProperty(reviewerEmptyDocument, Reviewer.getDocumentsHome() + organization.getName() + "/" + Constants.REVIEWER_EMPTY_DOCUMENT_FILENAME);
		
		ReviewerProperty reviewerEmptyFile = propertyDao.load(Constants.REVIEWER_EMPTY_FILE);
		organization.addProperty(reviewerEmptyFile,Reviewer.getDocumentsHome() +  organization.getName() + "/" + Constants.REVIEWER_EMPTY_DOCUMENT_FILENAME);
		
		ReviewerProperty reviewerGlosserHost = propertyDao.load(Constants.REVIEWER_GLOSSER_HOST);
		organization.addProperty(reviewerGlosserHost, Reviewer.getGlosserHost());
		
		ReviewerProperty reviewerGlosserPort = propertyDao.load(Constants.REVIEWER_GLOSSER_PORT);
		organization.addProperty(reviewerGlosserPort, Reviewer.getGlosserPort());
		
		ReviewerProperty reviewerGoogleDomain = propertyDao.load(Constants.REVIEWER_GOOGLE_DOMAIN);
		organization.addProperty(reviewerGoogleDomain,null);
		
		ReviewerProperty reviewerGooglePassword = propertyDao.load(Constants.REVIEWER_GOOGLE_PASSWORD);
		organization.addProperty(reviewerGooglePassword,null);
			
		ReviewerProperty reviewerGoogleUsername = propertyDao.load(Constants.REVIEWER_GOOGLE_USERNAME);
		organization.addProperty(reviewerGoogleUsername, null);
		
		ReviewerProperty reviewerPrivateKey = propertyDao.load(Constants.REVIEWER_PRIVATE_KEY);
		organization.addProperty(reviewerPrivateKey, null);
		
		ReviewerProperty reviewerPublicKey = propertyDao.load(Constants.REVIEWER_PUBLIC_KEY);
		organization.addProperty(reviewerPublicKey,null);
		
		ReviewerProperty reviewerSmtpHost = propertyDao.load(Constants.REVIEWER_SMTP_HOST);
		organization.addProperty(reviewerSmtpHost, Reviewer.getSMTPHost());
		
		ReviewerProperty reviewerSmtpPort = propertyDao.load(Constants.REVIEWER_SMTP_PORT);
		organization.addProperty(reviewerSmtpPort, Reviewer.getSMTPPort());
		
		ReviewerProperty reviewerUploadsHome = propertyDao.load(Constants.REVIEWER_UPLOADS_HOME);
		organization.addProperty(reviewerUploadsHome, Reviewer.getUploadsHome() + "/" + organization.getName());
		
		ReviewerProperty systemHttpProxyHost = propertyDao.load(Constants.SYSTEM_HTTP_PROXY_HOST);
		organization.addProperty(systemHttpProxyHost,null);
		
		ReviewerProperty systemHttpProxyPort = propertyDao.load(Constants.SYSTEM_HTTP_PROXY_PORT);
		organization.addProperty(systemHttpProxyPort,null);
		
		ReviewerProperty systemHttpProxySet = propertyDao.load(Constants.SYSTEM_HTTP_PROXY_SET);
		organization.addProperty(systemHttpProxySet, null);
		
		ReviewerProperty systemHttpsProxyHost = propertyDao.load(Constants.SYSTEM_HTTPS_PROXY_HOST);
		organization.addProperty(systemHttpsProxyHost, null);
		
		ReviewerProperty systemHttpsProxyPort = propertyDao.load(Constants.SYSTEM_HTTPS_PROXY_PORT);
		organization.addProperty(systemHttpsProxyPort, null);
		
		ReviewerProperty systemHttpsProxySet = propertyDao.load(Constants.SYSTEM_HTTPS_PROXY_SET);
		organization.addProperty(systemHttpsProxySet, null);
		
		ReviewerProperty reviewerLogosHome = propertyDao.load(Constants.ORGANIZATION_LOGO_HOME);
		organization.addProperty(reviewerLogosHome, Reviewer.getReviewerLogosHome() + "/" + organization.getName() + "/");
		
		ReviewerProperty organizationLogoFile = propertyDao.load(Constants.ORGANIZATION_LOGO_FILE);
		organization.addProperty(organizationLogoFile, null);
		
		return organization;
	}

	/** 
	 * Save the collection of organization properties
	 * @param properties collection of organization properties
	 * @throws MessageException message for the user
	 */
	private void saveOrganizationProperties(Set<OrganizationProperty> properties) throws Exception{
		for(OrganizationProperty organizationProperty : properties){			
			saveOrganizationProperty((organizationProperty));
		}
	}
	
	/**
	 * Save the organization property received as parameter
	 * @param organizationProperty organization property to save
	 * @throws MessageException message for the user
	 */
	public OrganizationProperty saveOrganizationProperty(OrganizationProperty organizationProperty) throws Exception{
//		if (organizationProperty != null && organizationProperty.getProperty() != null && organizationProperty.getProperty().isPassword()){
//			AESCipher cipherAES = AESCipher.getInstance();
//			String value = organizationProperty.getValue();
//			if ( value != null){
//				String encryptedValue = cipherAES.encrypt(value);
//				organizationProperty.setValue(encryptedValue);
//			}
//		}
		return organizationPropertyDao.save(organizationProperty);
	}
	
	
	/**
	 * An organization can be deleted if:
	 *      - there is not users and courses belong to this organization
	 * @param organization organization to delete
	 * @return organization deleted
	 * @throws MessageException message for the user
	 */
	public Organization deleteOrganization(Organization organization) throws MessageException{
		if (userDao.hasUsers(organization)){
			throw new MessageException(Constants.EXCEPTION_ORGANIZATION_HAS_USERS);
		} else if (courseDao.hasCourses(organization)){
			throw new MessageException(Constants.EXCEPTION_ORGANIZATION_HAS_COURSES);
		} else {
			// delete the properties belong to the organization
			deleteOrganizationProperties(organization);
			// delete the organization
			organizationDao.delete(organization);
			return organization;
		}
	}
	
	/**
	 * Delete all the organization properties belong to the organization received as parameter
	 * @param organization organization owner of the properties
	 * @return organization owner of the properties
	 * @throws MessageException message to the user
	 */
	public Organization deleteOrganizationProperties(Organization organization) throws MessageException {
		for(OrganizationProperty organizationProperty : organization.getOrganizationProperties()){
			organizationProperty = deleteOrganizationProperty(organizationProperty);
		}
		return organization;
	}
	
	/**
	 * Delete the organization property received as parameter
	 * @param property organization property to delete
	 * @return organization property deleted
	 * @throws MessageException message to the user
	 */
	public OrganizationProperty deleteOrganizationProperty(OrganizationProperty property) throws MessageException {
		organizationPropertyDao.delete(property);
		return property;
	}
	
	
	public Collection<User> getUsers(User user) throws Exception{
		Collection<User> users = new ArrayList<User>();
		if (!StringUtil.isBlank(user.getFirstname()) || !StringUtil.isBlank(user.getLastname())){
			users = userDao.geUsers(user);
		} else {
			throw new MessageException(Constants.EXCEPTION_FIELD_EMPTIES);
		}
		return users;
	}
	
	public Collection<User> getUsers(String firstName, String lastName, int startRow, int maxRows) throws Exception{
		Collection<User> users = new ArrayList<User>();
		if (!StringUtil.isBlank(firstName) || !StringUtil.isBlank(lastName)){
			users = userDao.geUsers(firstName, lastName, startRow, maxRows);
		} else {
			throw new MessageException(Constants.EXCEPTION_FIELD_EMPTIES);
		}
		return users;
	}
	
	
	public Collection<User> getUsers(Organization organization, String firstName, String lastName, int startRow, int maxRows) throws Exception{
		Collection<User> users = new ArrayList<User>();
		if (!StringUtil.isBlank(firstName) || !StringUtil.isBlank(lastName)){
			users = userDao.geUsers(organization, firstName, lastName, startRow, maxRows);
		} else {
			throw new MessageException(Constants.EXCEPTION_FIELD_EMPTIES);
		}
		return users;
	}
	
	public Collection<Organization> getOrganizations(){
		return organizationDao.getOrganizations();
	}
	
	
	/**
	 * This method calls userDao to save the user received as parameter
	 * @param user user to save
	 */
	public User saveUser(User user) throws Exception{
		User userSaved = null;
		User otherUser = userDao.getUserByEmail(user.getEmail());
		if ( otherUser == null || (otherUser != null && otherUser.getId().equals(user.getId()))){
			userSaved = userDao.save(user);;	
		} else {
			// there is a user with the same email
			throw new MessageException(Constants.EXCEPTION_USER_EXISTS);
		}
		return userSaved; 
	}
}
