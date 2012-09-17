package au.edu.usyd.reviewer.server;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import au.edu.usyd.reviewer.client.core.Activity;
import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Deadline;
import au.edu.usyd.reviewer.client.core.ReviewingActivity;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.WritingActivity;

public class EmailNotifier {

	//private static final String SMTP_HOST = "smtp.gmail.com";
	//private static final String SMTP_PORT = "465";
//	private static final String SMTP_HOST = "smtp.usyd.edu.au";
//	private static final String SMTP_PORT = "25";	//576 //993(ssl)
	private String username;
	private String password;
	private String smtpHost;
	private String smtpPort;
	private Properties properties;
	private Session mailSession;
	private Transport transport;
	private String domain = null;
	private String fromName = "iWrite Assignment Tracker";
	private String fromAddress = "no-reply@"+domain;
	private DateFormat dateFormat = new SimpleDateFormat("E d MMM h:mma");

	private final String STUDENT_REVIEW_START_MESSAGE = "Dear %s," + "\n\nYou need to review the '%s' of one of your peers. Please visit %s to write and submit your review before the deadline on %s." + "\n\n" + fromName;
	private final String STUDENT_ACTIVITY_START_MESSAGE = "Dear %s," + "\n\nA Google document has been created for you to write your '%s'. Please visit %s to write and submit your document before the deadline on %s. " + "\n\n" + fromName;
	private final String LECTURER_DEADLINE_FINISH_MESSAGE = "Dear %s," + "\n\nThe '%s' %s assessment has finished " + "\nPlease go to %s to download the documents." + "\n\n" + fromName;
	private final String PASSWORD_DETAILS = "Dear %s," + "\n\nThe iWrite application for the course '%s' is now available for you." + "\nTo login, please go to http://iwrite.sydney.edu.au/reviewer/iWrite.html." + "\n\nUsername: '%s' \nPassword: '%s' " +"\n\n iWrite Assignment Tracker";
	private final String STUDENT_REVIEW_FINISH_MESSAGE = "Dear %s," + "\n\nThe '%s' %s assessment has finished " + "\nReviews are now available." + "\n\n" + fromName;
	private final String STUDENT_RECEIVED_REVIEW_MESSAGE = "Dear %s," + "\n\nYou have received feedback from the activity '%s'. " + "\nYou can go to %s to read it." + "\n\n" + fromName;
 
	public EmailNotifier(String username, String password, String smtpHost, String smtpPort, String domain) throws NoSuchProviderException {
		this.username = username;
		this.password = password;
		this.smtpHost = smtpHost;
		this.smtpPort = smtpPort;
		this.domain = domain;
		
		properties = new Properties();
		properties.put("mail.transport.protocol", "smtp");
		properties.put("mail.smtp.host", this.smtpHost);
		properties.put("mail.smtp.port", this.smtpPort);
		
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.socketFactory.port", smtpPort);
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.put("mail.smtp.socketFactory.fallback", "false");
		
		//properties.put("mail.smtp.host", SMTP_HOST);
		//properties.put("mail.smtp.port", SMTP_PORT);
		//properties.put("mail.smtp.auth", "false");
		//properties.put("mail.smtp.socketFactory.port", SMTP_PORT);
		//properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		//properties.put("mail.smtp.socketFactory.fallback", "false");
		mailSession = Session.getDefaultInstance(properties);
		transport = mailSession.getTransport();
	}

	public void close() throws MessagingException {
		transport.close();
	}

	public Properties getProperties() {
		return properties;
	}

	public void sendLecturerDeadlineFinishNotification(User lecturer, Course course, Activity activity, String deadlineName) throws MessagingException, UnsupportedEncodingException {
		String subject = "[" + course.getName().toUpperCase() + "] " + activity.getName();
		String to = lecturer.getFirstname() + " " + lecturer.getLastname();
		String content = String.format(LECTURER_DEADLINE_FINISH_MESSAGE, to, activity.getName(), deadlineName, getIwriteLinkForUser(lecturer));
		sendNotification(lecturer, subject, content);
	}
	
	public void sendReviewFinishNotification(User user, Course course, WritingActivity writingActivity, String deadlineName) throws MessagingException, UnsupportedEncodingException {
		String subject = "[" + course.getName().toUpperCase() + "] " + writingActivity.getName();
		String to = user.getFirstname() + " " + user.getLastname();
		String content = String.format(STUDENT_REVIEW_FINISH_MESSAGE, to, writingActivity.getName(), deadlineName, getIwriteLinkForUser(user));
		sendNotification(user, subject, content);
	}	

	public void sendNotification(User user, String subject, String content) throws MessagingException, UnsupportedEncodingException {
		if (!transport.isConnected()) {
			transport.connect(username, password);			
		}
		InternetAddress[] internetAddress = new InternetAddress[1];
		internetAddress[0] = new InternetAddress(user.getEmail());
		MimeMessage message = new MimeMessage(mailSession);
		message.setFrom(new InternetAddress(fromAddress, fromName));
		message.setContent(content, "text/plain");
		message.setSubject(subject);
		message.addRecipients(Message.RecipientType.TO, internetAddress);
		message.saveChanges();
		transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
	}

	public void sendStudentActivityStartNotification(User student, Course course, WritingActivity writingActivity, Deadline deadline) throws MessagingException, UnsupportedEncodingException {
		String deadlineDate = dateFormat.format(deadline.getFinishDate());
		String to = student.getFirstname() + " " + student.getLastname();
		String subject = "[" + course.getName().toUpperCase() + "] " + writingActivity.getName();
		String content = String.format(STUDENT_ACTIVITY_START_MESSAGE, to, writingActivity.getName(), getIwriteLinkForUser(student), deadlineDate);
		sendNotification(student, subject, content);
	}

	public void sendStudentReviewStartNotification(User student, Course course, WritingActivity writingActivity, Deadline deadline) throws MessagingException, UnsupportedEncodingException {		
		String deadlineDate = dateFormat.format(writingActivity.getReviewingActivities().get(0).getFinishDate());
		String to = student.getFirstname() + " " + student.getLastname();
		String subject = "[" + course.getName().toUpperCase() + "] " + writingActivity.getName();
		String content = String.format(STUDENT_REVIEW_START_MESSAGE, to, writingActivity.getName(), getIwriteLinkForUser(student), deadlineDate);
		sendNotification(student, subject, content);
	}

	public void sendPasswordNotification(User user, String courseName) throws MessagingException, UnsupportedEncodingException {
		String content = String.format(PASSWORD_DETAILS, user.getFirstname()+" "+user.getLastname(), courseName, user.getUsername(), user.getPassword());
		this.sendNotification(user, "iWrite user details", content);
	}
	
	public void sendReviewEarlyFinishNotification(User user, Course course, ReviewingActivity reviewingActivity) throws MessagingException, UnsupportedEncodingException {
		String content = String.format(STUDENT_RECEIVED_REVIEW_MESSAGE, user.getFirstname()+" "+user.getLastname(), reviewingActivity.getName(), getIwriteLinkForUser(user));
		String subject = "[" + course.getName().toUpperCase() + "] " + reviewingActivity.getName();
		this.sendNotification(user, subject, content);
	}	

	private String getIwriteLinkForUser(User user) {	
		return user.getWasmuser()?"http://"+domain+"/reviewer/Assignments.html":"http://"+domain+"/reviewer/iWrite.html";
	}
	
	public void setProperties(Properties properties) {
		this.properties = properties;
	}	
}
