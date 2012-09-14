package au.edu.usyd.reviewer.server;

import java.util.Arrays;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.usyd.feedback.feedbacktracking.FeedbackTrackingDao;
import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.OrganizationProperty;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.server.util.DigitalSigner;

public class Reviewer {

	private static final Logger logger = LoggerFactory.getLogger(Reviewer.class);
//	private static Configuration config;
	private static SessionFactory sessionFactory = null;
	private static AssignmentManager assignmentManager = null;
	private static DigitalSigner digitalSigner = null;
	private static EmailNotifier emailNotifier = null;
	private static Organization organization = null;
	private static OrganizationManager organizationManager = null;
    
	static {
		try {
//			config = new PropertiesConfiguration("reviewer.properties");
//			for (Iterator<String> keys = config.getKeys(); keys.hasNext();) {
			for (OrganizationProperty property : organization.getOrganizationProperties()){
//				String property = keys.next();
				String propertyName = property.getProperty().getName();
				String value = property.getValue();
//				logger.debug("Setting property: " + property + "=" + config.getString(property));
				logger.debug("Setting property: " + property + "=" + value);
				if (String.valueOf(propertyName).startsWith("system.")) {
					System.setProperty(StringUtils.substringAfter(propertyName, "system."), value);
				}
			}
		} catch (Exception e) {
			logger.error("Failed to load reviewer.properties", e);
		}
	}

//	public static List<String> getAdminUsers() {
//		return Arrays.asList(Reviewer.getProperty("reviewer.admin.users").split("\\."));
//	}

	public static synchronized AssignmentManager getAssignmentManager(Organization anOrganization) {
		if (assignmentManager == null) {
			setOrganization(organization);
			String domain = Reviewer.getGoogleDomain();
			String username = Reviewer.getGoogleUsername();
			String password = Reviewer.getGooglePassword();
			String documentsHome = Reviewer.getDocumentsHome();
			String emailUsername = Reviewer.getEmailUsername();
			String emailPassword = Reviewer.getEmailPassword();
			String smtpHost = Reviewer.getSMTPHost();
			String smtpPort = Reviewer.getSMTPPort();
			

			try {
				AssignmentDao assignmentDao = new AssignmentDao(Reviewer.getHibernateSessionFactory());
				AssignmentRepository assignmentRepository = new AssignmentRepository(username, password, domain);
				setEmailNotifier(new EmailNotifier(emailUsername, emailPassword, smtpHost, smtpPort));				 
				assignmentManager = new AssignmentManager(assignmentRepository, assignmentDao, emailNotifier, anOrganization);
				assignmentManager.setDocumentsHome(documentsHome);
			} catch (Exception e) {
				logger.error("Failed to initialise assignment manager", e);
			}
		}
		return assignmentManager;
	}

	public static synchronized DigitalSigner getDigitalSigner() {
		if (digitalSigner == null) {
			try {
				digitalSigner = new DigitalSigner(getPrivateKeyPath(), getPublicKeyPath());
			} catch (Throwable e) {
				logger.error("Failed to initialise digital signer.", e);
			}
		}
		return digitalSigner;
	}

	public static String getDocumentsHome() {
		String value = organization.getPropertyValue(Constants.REVIEWER_DOCUMENTS_HOME);
//		return config.getString("reviewer.documents.home");
		return value;
	}
	
	public static String getUploadsHome() {
//		return config.getString("reviewer.uploads.home");
		String value = organization.getPropertyValue(Constants.REVIEWER_UPLOADS_HOME);
		return value;
	}	

	public static String getGoogleDomain() {
		String value = organization.getPropertyValue(Constants.REVIEWER_GOOGLE_DOMAIN);
		return value;
//		return config.getString("reviewer.google.domain");
	}

	public static String getGooglePassword() {
//		return config.getString("reviewer.google.password");
		String value = organization.getPropertyValue(Constants.REVIEWER_GOOGLE_PASSWORD);
		return value;
	}
	
	public static String getGoogleUsername() {
//		return config.getString("reviewer.google.username");
		String value = organization.getPropertyValue(Constants.REVIEWER_GOOGLE_USERNAME);
		return value;
	}
	
	public static String getEmailPassword() {
//		return config.getString("reviewer.email.password");
		String value = organization.getPropertyValue(Constants.REVIEWER_EMAIL_PASSWORD);
		return value;
	}
	
	public static String getEmailUsername() {
//		return config.getString("reviewer.email.username");
		String value = organization.getPropertyValue(Constants.REVIEWER_EMAIL_USERNAME);
		return value;
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

	public static String getPrivateKeyPath() {
//		return config.getString("reviewer.privatekey.path");
		String value = organization.getPropertyValue(Constants.REVIEWER_PRIVATE_KEY);
		return value;
	}

	public static String getProperty(String property) {
		String value = organization.getPropertyValue(property);
		return value;
//		return config.getString(property);
	}

	public static String getPublicKeyPath() {
//		return config.getString("reviewer.publickey.path");
		String value = organization.getPropertyValue(Constants.REVIEWER_PUBLIC_KEY);
		return value;
	}

	public static String getEmptyFile() {
//		return config.getString("reviewer.empty.file");
		String value = organization.getPropertyValue(Constants.REVIEWER_EMPTY_FILE);
		return value;
	}
	
	public static String getEmptyDocument() {
//		return config.getString("reviewer.empty.document");
		String value = organization.getPropertyValue(Constants.REVIEWER_EMPTY_DOCUMENT);
		return value;
	}

	public static EmailNotifier getEmailNotifier() {
		return emailNotifier;
	}

	public static void setEmailNotifier(EmailNotifier emailNotifier) {
		Reviewer.emailNotifier = emailNotifier;
	}
	
	public static String getSMTPHost(){
//		return config.getString("reviewer.smtp.host");
		String value = organization.getPropertyValue(Constants.REVIEWER_SMTP_HOST);
		return value;
	}
	
	public static String getSMTPPort(){
//		return config.getString("reviewer.smtp.port");
		String value = organization.getPropertyValue(Constants.REVIEWER_SMTP_PORT);
		return value;
	}
	
	public static String getGlosserHost(){
//		return config.getString("reviewer.glosser.host");
		String value = organization.getPropertyValue(Constants.REVIEWER_GLOSSER_HOST);
		return value;
	}
	
	public static String getGlosserPort(){
//		return config.getString("reviewer.glosser.port");
		String value = organization.getPropertyValue(Constants.REVIEWER_GLOSSER_PORT);
		return value;
	}
	
	public static String getGlosserUrl(Long siteId, String docId){
		return "http://"+getGlosserHost()+":"+getGlosserPort()+"/glosser/siteauth.htm?siteId=" + siteId + "&docId=" + docId;
	}
	
	private static void setOrganization(Organization anOrganization){
		if (organization == null){
			organization = anOrganization;
		}
	}
}