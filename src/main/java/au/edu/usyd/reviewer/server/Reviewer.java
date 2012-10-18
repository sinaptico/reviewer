package au.edu.usyd.reviewer.server;

import java.util.Iterator;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.OrganizationProperty;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.server.util.DigitalSigner;

public class Reviewer {

	private static final Logger logger = LoggerFactory.getLogger(Reviewer.class);
	private static Configuration config;
	private static SessionFactory sessionFactory = null;
	private static AssignmentManager assignmentManager = null;
	private static DigitalSigner digitalSigner = null;
	private static EmailNotifier emailNotifier = null;
	private static Organization organization = null;
	private static AssignmentRepository assignmentRepository =null;
    
	static {
		try {
			config = new PropertiesConfiguration("reviewer.properties");
			for (Iterator<String> keys = config.getKeys(); keys.hasNext();) {
				String property = keys.next();
				logger.debug("Setting property: " + property + "=" + config.getString(property));
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Failed to load reviewer.properties", e);
		}

	}
	public static synchronized AssignmentManager getAssignmentManager() {
		assignmentManager = new AssignmentManager();
		return assignmentManager;
	}
	
	public static synchronized void initializeAssignmentManager(Organization anOrganization) throws Exception{
		
		if ((organization == null) || (assignmentManager == null) || (assignmentRepository == null) ||
			(assignmentManager != null && assignmentManager.getAssignmentRepository() == null) ||
			(organization != null && anOrganization != null && 
			(!organization.getId().equals(anOrganization.getId())) || !organization.getGoogleDomain().equals(anOrganization.getGoogleDomain()))){
			
			organization = anOrganization;
		
			try {
	 			String domain = organization.getGoogleDomain();
				String username = organization.getGoogleUsername();
				String password = organization.getDecryptedGooglePassword();			
				String emailUsername = organization.getEmailUsername();
				String emailPassword = organization.getDecryptedEmailPassword();
				String smtpHost = organization.getSMTPHost();
				String smtpPort = organization.getSMTPPort();
				
				setEmailNotifier(new EmailNotifier(emailUsername, emailPassword, smtpHost, smtpPort, domain));
				
				assignmentRepository = new AssignmentRepository(username, password, domain);
				
				assignmentManager.initialize(assignmentRepository, emailNotifier, organization);
				
			} catch (Exception e) {
					e.printStackTrace();
					if ( e instanceof MessageException){
						throw e;
					}
					throw new Exception(Constants.EXCEPTION_FAILED_INITIALIZE_ASSIGNMENT_MANAGER);
			}
		}
	}

	public static synchronized DigitalSigner getDigitalSigner() {
		if (digitalSigner == null) {
			try {
				digitalSigner = new DigitalSigner(organization.getPrivateKeyPath(), organization.getPublicKeyPath());
			} catch (Throwable e) {
				logger.error("Failed to initialise digital signer.", e);
			}
		}
		return digitalSigner;
	}

	
	
	public static synchronized SessionFactory getHibernateSessionFactory() {
		if (sessionFactory == null) {
			try {
				sessionFactory = new AnnotationConfiguration().configure().buildSessionFactory();
			} catch (Throwable e) {
				logger.error("Failed to initialise hibernate session factory.", e);
			}
		}
		return sessionFactory;
	}


	public static EmailNotifier getEmailNotifier() {
		return emailNotifier;
	}

	public static void setEmailNotifier(EmailNotifier emailNotifier) {
		Reviewer.emailNotifier = emailNotifier;
	}
	
	
	public static String getGlosserUrl(Long siteId, String docId){
		return "http://"+organization.getGlosserHost()+":"+organization.getGlosserPort()+"/glosser/siteauth.htm?siteId=" + siteId + "&docId=" + docId;
	}
	
	/**
	 * Set the logged user organization and initialize the System properties
	 * @param anOrganization
	 */
	public static void setOrganization(Organization anOrganization){
		organization = anOrganization;
		for (OrganizationProperty property : organization.getOrganizationProperties()){
			String propertyName = property.getProperty().getName();
			String value = property.getValue();
			logger.debug("Setting property: " + property + "=" + value);
			if (String.valueOf(propertyName).startsWith("system.")) {
				System.setProperty(StringUtils.substringAfter(propertyName, "system."), value);
			}
		}
	}
	
	public static String getDocumentsHome() {
		return config.getString(Constants.REVIEWER_DOCUMENTS_HOME);
	}
	
	public static String getGlosserHost() {
		return config.getString(Constants.REVIEWER_GLOSSER_PORT);
	}

	public static String getGlosserPort() {
		return config.getString(Constants.REVIEWER_GLOSSER_HOST);
	}
	
	public static String getSMTPHost() {
		return config.getString(Constants.REVIEWER_SMTP_HOST);
	}
	
	public static String getSMTPPort() {
		return config.getString(Constants.REVIEWER_SMTP_PORT);
	}

	public static String getUploadsHome() {
		return config.getString(Constants.REVIEWER_UPLOADS_HOME);
	}
	
	public static AssignmentRepository getAssignmentRepository(){
		return assignmentRepository;
	}
	
	public static String getReviewerLogosHome(){
		return config.getString(Constants.REVIEWER_LOGOS_HOME);
	}
	
	
}