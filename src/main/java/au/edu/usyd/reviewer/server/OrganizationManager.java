package au.edu.usyd.reviewer.server;

import java.net.MalformedURLException;
import java.util.ArrayList;



import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import java.util.Set;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gdata.util.AuthenticationException;

import au.edu.usyd.reviewer.client.core.EmailOrganization;
import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.OrganizationProperty;
import au.edu.usyd.reviewer.client.core.ReviewerProperty;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.StringUtil;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.gdata.GoogleDocsServiceImpl;
import au.edu.usyd.reviewer.server.util.AESCipher;

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
	private EmailDao emailDao;
	
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
			emailDao = EmailDao.getInstance();
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
	public Organization saveOrganization(Organization organization, boolean includeAll) throws Exception{
		Organization otherOrganization = getOrganization(organization.getName());
		if ( otherOrganization == null || (otherOrganization != null && organization.getId() != null && otherOrganization.getId().equals(organization.getId()))){
			organization = organizationDao.save(organization);;
			if (!organization.hasProperties()){
				organization = addProperties(organization);
			}
			if (!organization.hasEmails()){
				organization = addEmails(organization);
			}
			saveOrganizationProperties(organization.getOrganizationProperties());	
		} else {
			// there is an organization with the same name
			throw new MessageException(Constants.EXCEPTION_ORGANIZATION_EXISTS);
		}
		if (!includeAll){
			organization.setOrganizationProperties(new HashSet<OrganizationProperty>());
		}
		return organization; 
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
		ReviewerProperty reviewerEmailPassword = propertyDao.load(Constants.REVIEWER_EMAIL_PASSWORD);
		organization.addProperty(reviewerEmailPassword, null);
		
		ReviewerProperty reviewerEmailUsername = propertyDao.load(Constants.REVIEWER_EMAIL_USERNAME);
		organization.addProperty(reviewerEmailUsername, null);
		
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
		
		ReviewerProperty reviewerSmtpHost = propertyDao.load(Constants.REVIEWER_SMTP_HOST);
		organization.addProperty(reviewerSmtpHost, Reviewer.getSMTPHost());
		
		ReviewerProperty reviewerSmtpPort = propertyDao.load(Constants.REVIEWER_SMTP_PORT);
		organization.addProperty(reviewerSmtpPort, Reviewer.getSMTPPort());
		
		ReviewerProperty organizationLogoFile = propertyDao.load(Constants.ORGANIZATION_LOGO_FILE);
		organization.addProperty(organizationLogoFile, null);
		
		ReviewerProperty organizationShibbolethEnabled = propertyDao.load(Constants.ORGANIZATION_SHIBBOLETH_ENABLED);
		organization.addProperty(organizationShibbolethEnabled, Constants.SHIBBOLETH_ENABLED_NO);
		
		ReviewerProperty organizationPasswordNewUsers = propertyDao.load(Constants.ORGANIZATION_PASSWORD_NEW_USERS);
		organization.addProperty(organizationPasswordNewUsers, Constants.NEW_USERS_PASSWORD_DEFAULT_VALUE);
		
		ReviewerProperty reviewerDomain = propertyDao.load(Constants.REVIEWER_DOMAIN);
		organization.addProperty(reviewerDomain, null);
		
		ReviewerProperty reviewerEmailNotificationDomain = propertyDao.load(Constants.REVIEWER_EMAIL_NOTIFICATION_DOMAIN);
		organization.addProperty(reviewerEmailNotificationDomain,null);
		
		ReviewerProperty organizationLinkToShowInAssignments = propertyDao.load(Constants.ORGANIZATION_LINK_TO_SHOW_IN_ASSIGNMENTS);
		organization.addProperty(organizationLinkToShowInAssignments,null);

		ReviewerProperty reviewerSupportEmail = propertyDao.load(Constants.REVIEWER_SUPPORT_EMAIL);
		organization.addProperty(reviewerSupportEmail,null);

		
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
		if (organizationProperty != null){
			ReviewerProperty property = organizationProperty.getProperty();
			if (property != null && property.getName() != null){
				String value = organizationProperty.getValue();
				try{
					Organization organization = organizationProperty.getOrganization();
					String username = null;
					
					if (property.getName().equals(Constants.REVIEWER_EMAIL_PASSWORD)){
						username =  organization.getPropertyValue(Constants.REVIEWER_EMAIL_USERNAME);
					} else if (property.getName().equals(Constants.REVIEWER_GOOGLE_PASSWORD)){
						username =  organization.getPropertyValue(Constants.REVIEWER_GOOGLE_USERNAME);
					}
					
					if (username != null){
						AESCipher aesCipher = AESCipher.getInstance();
						String decryptedValue = aesCipher.decrypt(value);
						GoogleDocsServiceImpl google = new GoogleDocsServiceImpl(username, decryptedValue);
						
					}
				} catch (Exception e) {
					try{
						AESCipher aescipher = AESCipher.getInstance();
						value = aescipher.encrypt(value);
						organizationProperty.setValue(value);
					} catch(Exception ex){
							
					}
				}
			}
			organizationProperty = organizationPropertyDao.save(organizationProperty);
		}
		return organizationProperty;
	}
	
	
	/**
	 * An organization can be deleted if:
	 *      - there is not users and courses belong to this organization
	 * @param organization organization to delete
	 * @return organization deleted
	 * @throws MessageException message for the user
	 */
	public Organization deleteOrganization(Organization organization) throws MessageException{
		organization.setDeleted(true);
		organizationDao.save(organization);
		return organization;
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
		
	
	
	public Collection<Organization> getOrganizations() throws MessageException{
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
	
	public List<Organization> getOrganizations(Integer page, Integer limit, String name) throws MessageException{
		return organizationDao.getOrganizations(page, limit, name);
	}
	
	
	public User getUserByEmail(String email) throws MessageException{
		return userDao.getUserByEmail(email);
	}
	
	
	public User getUser(Long userId) throws MessageException{
		return userDao.load(userId);
	}

	public List<User> getUsers(Organization organization,Integer page, Integer limit, String roles, boolean assigned) throws MessageException{
		return userDao.getUsers(organization,page,limit,roles, assigned);
	}
	
	
	public Organization addEmails(Organization organization) throws MessageException {
		try{
			organization = createEmail(Constants.EMAIL_LECTURER_DEADLINE_FINISH,Constants.EMAIL_LECTURER_DEADLINE_FINISH_MESSAGE, organization);
			organization = createEmail(Constants.EMAIL_PASSWORD_DETAILS,Constants.EMAIL_PASSWORD_DETAILS_MESSAGE, organization);
			organization = createEmail(Constants.EMAIL_STUDENT_ACTIVITY_START,Constants.EMAIL_STUDENT_ACTIVITY_START_MESSAGE, organization);
			organization = createEmail(Constants.EMAIL_STUDENT_RECEIVED_REVIEW,Constants.EMAIL_STUDENT_RECEIVED_REVIEW_MESSAGE, organization);
			organization = createEmail(Constants.EMAIL_STUDENT_REVIEW_FINISH,Constants.EMAIL_STUDENT_REVIEW_FINISH_MESSAGE, organization);
			organization = createEmail(Constants.EMAIL_STUDENT_REVIEW_START,Constants.EMAIL_STUDENT_REVIEW_START_MESSAGE, organization);
		} catch(Exception e){
			throw new MessageException(Constants.EXCEPTION_GENERATE_ORGANIZATION_EMAILS);
		}
		return organization;
	}
	
	private Organization createEmail(String emailName, String emailMessage, Organization organization) throws Exception {
		EmailOrganization email = new EmailOrganization();
		email.setName(emailName);
		email.setMessage(emailMessage);
		email.setOrganization(organization);
		email = emailDao.saveEmailOrganization(email, organization);
		organization.addEmail(email);
		return organization;
	}
	
	public boolean isOrganizationActivated(User loggedUser, Organization organization) throws MessageException {
		boolean isOrganizationActivated = true;
		
		//check if the properties are completed
		isOrganizationActivated = checkOrganizationProperties(organization);
		
		//checkGoogle conection
		isOrganizationActivated &=checkGoogleConnection(organization);
		
		//check SMTP connection
		isOrganizationActivated &=checkSMTPConnection(loggedUser, organization);
		
		return isOrganizationActivated;

	}
	
	private boolean  checkOrganizationProperties(Organization organization) throws MessageException {
		boolean propertiesOK = true;
		String message ="";
		String value = organization.getEmailUsername();
		if (StringUtil.isBlank(value)){
			message = Constants.REVIEWER_EMAIL_USERNAME;
		}
		
		value = organization.getEmailPassword();
		if (StringUtil.isBlank(value)){
			if (!StringUtil.isBlank(message)){
				message += "\n" + Constants.REVIEWER_EMAIL_PASSWORD;
			} else {
				message = Constants.REVIEWER_EMAIL_PASSWORD;
			}
		}
		
		value = organization.getGoogleUsername();
		if (StringUtil.isBlank(value)){
			if (!StringUtil.isBlank(message)){
				message += "\n" + Constants.REVIEWER_GOOGLE_USERNAME;
			} else {
				message = Constants.REVIEWER_GOOGLE_USERNAME;
			}
		}
		
		value = organization.getGooglePassword();
		if (StringUtil.isBlank(value)){
			if (!StringUtil.isBlank(message)){
				message += "\n" + Constants.REVIEWER_GOOGLE_PASSWORD;
			} else {
				message = Constants.REVIEWER_GOOGLE_PASSWORD;
			}
		}
		
		value = organization.getGoogleDomain();
		if (StringUtil.isBlank(value)){
			if (!StringUtil.isBlank(message)){
				message += "\n" + Constants.REVIEWER_GOOGLE_DOMAIN;
			} else {
				message = Constants.REVIEWER_GOOGLE_DOMAIN;
			}
		}
		
		value = organization.getSMTPHost();
		if (StringUtil.isBlank(value)){
			if (!StringUtil.isBlank(message)){
				message += "\n" + Constants.REVIEWER_SMTP_HOST;
			} else {
				message = Constants.REVIEWER_SMTP_HOST;
			}
		}
		
		value = organization.getSMTPPort();
		if (StringUtil.isBlank(value)){
			if (!StringUtil.isBlank(message)){
				message += "\n" + Constants.REVIEWER_GLOSSER_PORT;
			} else {
				message = Constants.REVIEWER_GLOSSER_PORT;
			}
		}
		
		value = organization.getOrganizationPasswordNewUsers();
		if (StringUtil.isBlank(value)){
			if (!StringUtil.isBlank(message)){
				message += "\n" + Constants.ORGANIZATION_PASSWORD_NEW_USERS;
			} else {
				message = Constants.ORGANIZATION_PASSWORD_NEW_USERS;
			}
		}
		
		
		value = organization.getReviewerDomain();
		if (StringUtil.isBlank(value)){
			if (!StringUtil.isBlank(message)){
				message += "\n" + Constants.REVIEWER_DOMAIN;
			} else {
				message = Constants.REVIEWER_DOMAIN;
			}
		}
		
		value = organization.getReviewerEmailNotificationDomain();
		if (StringUtil.isBlank(value)){
			if (!StringUtil.isBlank(message)){
				message += "\n" + Constants.REVIEWER_EMAIL_NOTIFICATION_DOMAIN;
			} else {
				message = Constants.REVIEWER_EMAIL_NOTIFICATION_DOMAIN;
			}
		}
		
		
		value = organization.getReviewerSupportEmail();
		if (StringUtil.isBlank(value)){
			if (!StringUtil.isBlank(message)){
				message += "\n" + Constants.REVIEWER_SUPPORT_EMAIL;
			} else {
				message = Constants.REVIEWER_SUPPORT_EMAIL;
			}
		}
		
		
		
		if (!StringUtil.isBlank(message)){
			propertiesOK = false;
			throw new MessageException(Constants.EXCEPTION_ORGANIZATION_PROPERTIES + "\n" + message);
		}
		return propertiesOK;
	}
	
	private boolean checkGoogleConnection(Organization organization) throws MessageException{
		boolean connectionOK = true;
		try{
			String username = organization.getGoogleUsername();
			String password = organization.getGooglePassword();
			AESCipher aesCipher = AESCipher.getInstance();
			String decryptedValue = aesCipher.decrypt(password);
			GoogleDocsServiceImpl google = new GoogleDocsServiceImpl(username, decryptedValue);
		} catch (Exception e) {
			connectionOK = false;
			e.printStackTrace();
			if (e instanceof AuthenticationException) {
				throw new MessageException(Constants.EXCEPTION_GOOGLE_AUTHENTICATION);
			} else if  (e instanceof MalformedURLException) {
				throw new MessageException(Constants.EXCEPTION_GOOGLE_URL_MALFORMED);
			} else {
				throw new MessageException(Constants.EXCEPTION_GOOGLE_CONNECTION);
			}
		}
		return connectionOK;
	}
	
	private boolean checkSMTPConnection(User loggedUser, Organization organization) throws MessageException {
		boolean connectionOK = true;
		try{
			String username = organization.getEmailUsername();
			String password = organization.getEmailPassword();
			String domain = organization.getGoogleDomain();
			String smtpHost = organization.getSMTPHost();
			String smtpPort = organization.getSMTPPort();
			String reviewerEmailNotificationDomain = organization.getReviewerEmailNotificationDomain();
			AESCipher aesCipher = AESCipher.getInstance();
			String decryptedValue = aesCipher.decrypt(password);
			EmailNotifier emailSender = new EmailNotifier(username, decryptedValue, smtpHost, smtpPort, domain,reviewerEmailNotificationDomain);
			emailSender.sendTestSMTPEmail(loggedUser);
		} catch (Exception e) {
			connectionOK = false;
			e.printStackTrace();
			throw new MessageException(Constants.EXCEPTION_SMTP_CONNECTION);
		}
		return connectionOK;
	}
	
	public Organization activateOrganization(User loggedUser, Organization anOrganization) throws MessageException {
		if (organizationManager.isOrganizationActivated(loggedUser,anOrganization)){
			if (!anOrganization.isActivated()){
				anOrganization.setActivated(true);
				anOrganization = organizationDao.save(anOrganization);
			}
		}
		return anOrganization;
	}
	
	/**
	 * This method return the organization whose domain is equal to the domain received as parameter
	 * @param domain domain of the organization to look for
	 * @return organization with domain equals to the domain received as parameter
	 */
	public Organization getOrganizationByDomain(String domain) throws MessageException{
		Organization organization = organizationDao.getOrganizationByDomain(domain);
		return organization;
	}
}
