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
import au.edu.usyd.reviewer.server.util.DigitalSigner;

public class Reviewer {

	private static final Logger logger = LoggerFactory.getLogger(Reviewer.class);
	private static Configuration config;
	private static SessionFactory sessionFactory = null;
	private static AssignmentManager assignmentManager = null;
	private static DigitalSigner digitalSigner = null;
	private static EmailNotifier emailNotifier = null;

	static {
		try {
			config = new PropertiesConfiguration("reviewer.properties");
			for (Iterator<String> keys = config.getKeys(); keys.hasNext();) {
				String property = keys.next();
				logger.debug("Setting property: " + property + "=" + config.getString(property));
				if (String.valueOf(property).startsWith("system.")) {
					System.setProperty(StringUtils.substringAfter(property, "system."), config.getString(property));
				}
			}
		} catch (Exception e) {
			logger.error("Failed to load reviewer.properties", e);
		}
	}

	public static List<String> getAdminUsers() {
		return Arrays.asList(Reviewer.getProperty("reviewer.admin.users").split("\\."));
	}

	public static synchronized AssignmentManager getAssignmentManager() {
		if (assignmentManager == null) {
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
				assignmentManager = new AssignmentManager(assignmentRepository, assignmentDao, emailNotifier);
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
		return config.getString("reviewer.documents.home");
	}
	
	public static String getUploadsHome() {
		return config.getString("reviewer.uploads.home");
	}	

	public static String getGoogleDomain() {
		return config.getString("reviewer.google.domain");
	}

	public static String getGooglePassword() {
		return config.getString("reviewer.google.password");
	}
	
	public static String getGoogleUsername() {
		return config.getString("reviewer.google.username");
	}
	
	public static String getEmailPassword() {
		return config.getString("reviewer.email.password");
	}
	
	public static String getEmailUsername() {
		return config.getString("reviewer.email.username");
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
		return config.getString("reviewer.privatekey.path");
	}

	public static String getProperty(String property) {
		return config.getString(property);
	}

	public static String getPublicKeyPath() {
		return config.getString("reviewer.publickey.path");
	}

	public static String getEmptyFile() {
		return config.getString("reviewer.empty.file");
	}
	
	public static String getEmptyDocument() {
		return config.getString("reviewer.empty.document");
	}

	public static EmailNotifier getEmailNotifier() {
		return emailNotifier;
	}

	public static void setEmailNotifier(EmailNotifier emailNotifier) {
		Reviewer.emailNotifier = emailNotifier;
	}
	
	public static String getSMTPHost(){
		return config.getString("reviewer.smtp.host");
	}
	
	public static String getSMTPPort(){
		return config.getString("reviewer.smtp.port");
	}
	
	public static String getGlosserHost(){
		return config.getString("reviewer.glosser.host");
	}
	
	public static String getGlosserPort(){
		return config.getString("reviewer.glosser.port");
	}
}