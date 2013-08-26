package au.edu.usyd.reviewer.server;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.usyd.reviewer.client.core.Activity;
import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Deadline;
import au.edu.usyd.reviewer.client.core.EmailCourse;
import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.ReviewingActivity;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.StringUtil;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.server.util.CalendarUtil;

public class EmailNotifier {

	private final Logger logger = LoggerFactory.getLogger(EmailNotifier.class);
	private String username;
	private String password;
	private String smtpHost;
	private String smtpPort;
	private Properties properties;
	private Session mailSession;
	private Transport transport;
	// google organization domain
	private String fromName = "Reviewer Assignment Tracker";
	private String fromAddress = null;
	private String reviewerDomain = null;
	private String timeZone = null;
 
	public EmailNotifier(String username, String password, String smtpHost, String smtpPort, String reviewerDomain, String fromAddress, String timeZone) throws NoSuchProviderException {
		this.username = username;
		this.password = password;
		this.smtpHost = smtpHost;
		this.smtpPort = smtpPort;
		this.reviewerDomain = reviewerDomain;
		this.fromAddress = fromAddress;
		this.timeZone = timeZone;
		
		properties = new Properties();
		properties.put("mail.transport.protocol", "smtp");
		properties.put("mail.smtp.host", this.smtpHost);
		properties.put("mail.smtp.port", this.smtpPort);
		
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.socketFactory.port", smtpPort);
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.put("mail.smtp.socketFactory.fallback", "false");
		
		mailSession = Session.getDefaultInstance(properties);
		transport = mailSession.getTransport();
	}

	public void close() throws MessagingException {
		transport.close();
	}

	public Properties getProperties() {
		return properties;
	}

	public void sendLecturerDeadlineFinishNotification(User lecturer, Course course, Activity activity, String deadlineName) throws MessagingException, UnsupportedEncodingException, MessageException {
		String subject = "[" + course.getName().toUpperCase() + "] " + activity.getName();
		String to = lecturer.getFirstname() + " " + lecturer.getLastname();
		EmailCourse email = course.getEmail(Constants.EMAIL_LECTURER_DEADLINE_FINISH);
		String content = email.getMessage();
		if (StringUtil.isBlank(to)){
			to="Lecturer";
		}
		content = content.replaceAll("@LecturerName@", to);
		content = content.replaceAll("@ActivityName@", activity.getName());
		content = content.replaceAll("@DeadlineName@", deadlineName);
		content = content.replaceAll("@ReviewerLink@", getReviewerLinkForUser(lecturer));
		content = content.replaceAll("@FromName@", fromName);
		sendNotification(lecturer, subject, content);
	}
	
	public void sendReviewFinishNotification(User user, Course course, WritingActivity writingActivity, String deadlineName) throws MessagingException, UnsupportedEncodingException, MessageException {
		String subject = "[" + course.getName().toUpperCase() + "] " + writingActivity.getName();
		String to = user.getFirstname() + " " + user.getLastname();
		EmailCourse email = course.getEmail(Constants.EMAIL_STUDENT_REVIEW_FINISH); 
		String content = email.getMessage();
		if (StringUtil.isBlank(to)){
			to="Student";
		}
		content = content.replaceAll("@StudentName@", to);
		content = content.replaceAll("@ActivityName@", writingActivity.getName());
		content = content.replaceAll("@DeadlineName@", deadlineName);
		content = content.replaceAll("@ReviewerLink@", getReviewerLinkForUser(user));
		content = content.replaceAll("@FromName@", fromName);
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

	public void sendStudentActivityStartNotification(User student, Course course, WritingActivity writingActivity, Deadline deadline) throws MessagingException, UnsupportedEncodingException, MessageException {
		String deadlineDate = CalendarUtil.convertDateWithTimeZone(deadline.getFinishDate(), timeZone);
		String to = student.getFirstname() + " " + student.getLastname();
		String subject = "[" + course.getName().toUpperCase() + "] " + writingActivity.getName();
		EmailCourse email = course.getEmail(Constants.EMAIL_STUDENT_ACTIVITY_START);
		String content = email.getMessage();
		if (StringUtil.isBlank(to)){
			to="Student";
		}
		content = content.replaceAll("@StudentName@", to);
		content = content.replaceAll("@ActivityName@", writingActivity.getName());
		content = content.replaceAll("@ReviewerLink@", getReviewerLinkForUser(student));
		content = content.replaceAll("@DeadlineDate@", deadlineDate);
		content = content.replaceAll("@FromName@", fromName);
		sendNotification(student, subject, content);
	}

	public void sendStudentReviewStartNotification(User student, Course course, WritingActivity writingActivity, Deadline deadline) throws MessagingException, UnsupportedEncodingException, MessageException {		
		String deadlineDate = CalendarUtil.convertDateWithTimeZone(writingActivity.getReviewingActivities().get(0).getFinishDate(), timeZone);
		String to = student.getFirstname() + " " + student.getLastname();
		String subject = "[" + course.getName().toUpperCase() + "] " + writingActivity.getName();
		EmailCourse email = course.getEmail(Constants.EMAIL_STUDENT_REVIEW_START);
		String content = email.getMessage();
		if (StringUtil.isBlank(to)){
			to="Student";
		}
		content = content.replaceAll("@StudentName@", to);
		content = content.replaceAll("@ActivityName@", writingActivity.getName());
		content = content.replaceAll("@ReviewerLink@", getReviewerLinkForUser(student));
		content = content.replaceAll("@DeadlineDate@", deadlineDate);
		content = content.replaceAll("@FromName@", fromName);
		sendNotification(student, subject, content);
	}

	public void sendPasswordNotification(User user, Course course) throws MessagingException, UnsupportedEncodingException, MessageException {
		EmailCourse email = course.getEmail(Constants.EMAIL_PASSWORD_DETAILS);
		String content = email.getMessage();
		String to = user.getFirstname()+" "+user.getLastname();
		if (StringUtil.isBlank(to)){
			to="User";
		}
		content = content.replaceAll("@UserName@", to);
		content = content.replaceAll("@CourseName@", course.getName());
		content = content.replaceAll("@UserUsername@", user.getEmail());
		content = content.replaceAll("@Password@", user.getPassword());
		content = content.replaceAll("@iWriteLink@", getReviewerLinkForUser(user));
		content = content.replaceAll("@FromName@", fromName);
		this.sendNotification(user, "iWrite user details", content);
	}
	
	public void sendReviewEarlyFinishNotification(User user, Course course, ReviewingActivity reviewingActivity) throws MessagingException, UnsupportedEncodingException, MessageException {
		EmailCourse email = course.getEmail(Constants.EMAIL_STUDENT_RECEIVED_REVIEW);
		String content = email.getMessage();
		String to = user.getFirstname()+" "+user.getLastname();
		if (StringUtil.isBlank(to)){
			to="User";
		}
		content = content.replaceAll("@UserName@", to);
		content = content.replaceAll("@ActivityName@", reviewingActivity.getName());
		content = content.replaceAll("@ReviewerLink@", getReviewerLinkForUser(user));
		content = content.replaceAll("@FromName@", fromName);
		String subject = "[" + course.getName().toUpperCase() + "] " + reviewingActivity.getName();
		this.sendNotification(user, subject, content);
	}	
	
	private String getReviewerLinkForUser(User user) {	
		String url = "";
		if (user != null && user.getOrganization() != null && user.getOrganization().isShibbolethEnabled()){
			url ="http://"+reviewerDomain+"/assignments.htm";
		} else {
			url = "https://"+reviewerDomain+"/Assignments.html";
		}
		return url;
	}

	public void sendActivityNotificationToAdmin(Course course, WritingActivity writingActivity, User admin, String title) throws MessagingException, UnsupportedEncodingException, MessageException {
		String to = admin.getFirstname() + " " + admin.getLastname(); 
		String subject = "[" + course.getName().toUpperCase() + "] " + writingActivity.getName();
		EmailCourse email = course.getEmail(Constants.EMAIL_ACTIVITY_NOTIFICATIONS_SENT);
		String content = email.getMessage();
		if (StringUtil.isBlank(to)){
			to="Admin";
		}
		content = content.replaceAll("@UserName@", to);
		content = content.replaceAll("@EmailName@", title);
		content = content.replaceAll("@CourseName@", course.getName());
		content = content.replaceAll("@ActivityName@", writingActivity.getName());
		content = content.replaceAll("@FromName@", fromName);
		sendNotification(admin, subject, content);
	}
	
	public void sendReviewingNotificationToAdmin(Course course, WritingActivity writingActivity, ReviewingActivity reviewingActivity, User admin, String title) throws MessagingException, UnsupportedEncodingException, MessageException {
		String to = admin.getFirstname() + " " + admin.getLastname(); 
		String subject = "[" + course.getName().toUpperCase() + "] " + writingActivity.getName();
		EmailCourse email = course.getEmail(Constants.EMAIL_REVIEWING_ACTIVITY_NOTIFICATIONS_SENT);
		String content = email.getMessage();
		if (StringUtil.isBlank(to)){
			to="Admin";
		}
		content = content.replaceAll("@UserName@", to);
		content = content.replaceAll("@EmailName@", title);
		content = content.replaceAll("@CourseName@", course.getName());
		content = content.replaceAll("@ActivityName@", writingActivity.getName());
		content = content.replaceAll("@ReviewingActivityName@", reviewingActivity.getName());
		content = content.replaceAll("@FromName@", fromName);
		sendNotification(admin, subject, content);
	}
	
	
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public void sendTestSMTPEmail(User user) throws MessagingException, UnsupportedEncodingException, MessageException {
		String content = Constants.EMAIL_TEST_MESSAGE;
		this.sendNotification(user, "Test email from reviewer", content);
	}

	
	public void sendSaveCourseFinishedNotificationToAdmin(Course course, User admin, String title) throws MessagingException, UnsupportedEncodingException, MessageException {
		String to = admin.getFirstname() + " " + admin.getLastname(); 
		String subject = "[" + course.getName().toUpperCase() + "] ";
		EmailCourse email = course.getEmail(Constants.EMAIL_SAVE_COURSE_FINISHED);
		String content = email.getMessage();
		if (StringUtil.isBlank(to)){
			to="Admin";
		}
		content = content.replaceAll("@UserName@", to);
		content = content.replaceAll("@EmailName@", title);
		content = content.replaceAll("@CourseName@", course.getName());
		content = content.replaceAll("@FromName@", fromName);
		sendNotification(admin, subject, content);
	}
	
	
	public void sendNotificationToAdmin(Course course, WritingActivity writingActivity, ReviewingActivity reviewingActivity, Deadline deadline, User admin, String title) throws MessagingException, UnsupportedEncodingException, MessageException {
		String to = admin.getFirstname() + " " + admin.getLastname(); 
		String subject = "[" + course.getName().toUpperCase() + "] " + writingActivity.getName();
		EmailCourse email = course.getEmail(title);
		String content = email.getMessage();
		if (StringUtil.isBlank(to)){
			to="Admin";
		}
		content = content.replaceAll("@UserName@", to);
		content = content.replaceAll("@EmailName@", title);
		content = content.replaceAll("@CourseName@", course.getName());
		if (writingActivity != null){
			content = content.replaceAll("@ActivityName@", writingActivity.getName());
		}
		if (reviewingActivity != null){
			content = content.replaceAll("@ReviewingActivityName@", reviewingActivity.getName());
		}
		if (deadline != null){
			content = content.replaceAll("@DeadlineName@", deadline.getName());
		}
		content = content.replaceAll("@FromName@", fromName);
		sendNotification(admin, subject, content);
	}
}
