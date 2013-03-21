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
import au.edu.usyd.reviewer.client.core.EmailCourse;
import au.edu.usyd.reviewer.client.core.ReviewingActivity;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;

public class EmailNotifier {

	private String username;
	private String password;
	private String smtpHost;
	private String smtpPort;
	private Properties properties;
	private Session mailSession;
	private Transport transport;
	// google organization domain
	private String domain = null;
	private String fromName = "Reviewer Assignment Tracker";
	private String fromAddress = "no-reply@"+domain;
	private DateFormat dateFormat = new SimpleDateFormat("E d MMM h:mma");
	private String reviewerDomain = null;
 
	public EmailNotifier(String username, String password, String smtpHost, String smtpPort, String domain,String reviewerDomain) throws NoSuchProviderException {
		this.username = username;
		this.password = password;
		this.smtpHost = smtpHost;
		this.smtpPort = smtpPort;
		this.domain = domain;
		this.reviewerDomain = reviewerDomain;
		
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
		content = content.replaceAll("@LecturerName@", to);
		content = content.replaceAll("@ActivityName@", activity.getName());
		content = content.replaceAll("@DeadlineName@", deadlineName);
		content = content.replaceAll("@ReviewerLink@", getReviewerLinkForUser());
		content = content.replaceAll("@FromName@", fromName);
		sendNotification(lecturer, subject, content);
	}
	
	public void sendReviewFinishNotification(User user, Course course, WritingActivity writingActivity, String deadlineName) throws MessagingException, UnsupportedEncodingException, MessageException {
		String subject = "[" + course.getName().toUpperCase() + "] " + writingActivity.getName();
		String to = user.getFirstname() + " " + user.getLastname();
		EmailCourse email = course.getEmail(Constants.EMAIL_STUDENT_REVIEW_FINISH); 
		String content = email.getMessage();
		content = content.replaceAll("@StudentName@", to);
		content = content.replaceAll("@ActivityName@", writingActivity.getName());
		content = content.replaceAll("@DeadlineName@", deadlineName);
		content = content.replaceAll("@ReviewerLink@", getReviewerLinkForUser());
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
		String deadlineDate = dateFormat.format(deadline.getFinishDate());
		String to = student.getFirstname() + " " + student.getLastname();
		String subject = "[" + course.getName().toUpperCase() + "] " + writingActivity.getName();
		EmailCourse email = course.getEmail(Constants.EMAIL_STUDENT_ACTIVITY_START);
		String content = email.getMessage();
		content = content.replaceAll("@StudentName@", to);
		content = content.replaceAll("@ActivityName@", writingActivity.getName());
		content = content.replaceAll("@ReviewerLink@", getReviewerLinkForUser());
		content = content.replaceAll("@DeadlineDate@", deadlineDate);
		content = content.replaceAll("@FromName@", fromName);
		sendNotification(student, subject, content);
	}

	public void sendStudentReviewStartNotification(User student, Course course, WritingActivity writingActivity, Deadline deadline) throws MessagingException, UnsupportedEncodingException, MessageException {		
		String deadlineDate = dateFormat.format(writingActivity.getReviewingActivities().get(0).getFinishDate());
		String to = student.getFirstname() + " " + student.getLastname();
		String subject = "[" + course.getName().toUpperCase() + "] " + writingActivity.getName();
		EmailCourse email = course.getEmail(Constants.EMAIL_STUDENT_REVIEW_START);
		String content = email.getMessage();
		content = content.replaceAll("@StudentName@", to);
		content = content.replaceAll("@ActivityName@", writingActivity.getName());
		content = content.replaceAll("@ReviewerLink@", getReviewerLinkForUser());
		content = content.replaceAll("@DeadlineDate@", deadlineDate);
		content = content.replaceAll("@FromName@", fromName);
		sendNotification(student, subject, content);
	}

	public void sendPasswordNotification(User user, Course course) throws MessagingException, UnsupportedEncodingException, MessageException {
		EmailCourse email = course.getEmail(Constants.EMAIL_PASSWORD_DETAILS);
		String content = email.getMessage();
		content = content.replaceAll("@UserName@", user.getFirstname()+" "+user.getLastname());
		content = content.replaceAll("@CourseName@", course.getName());
		content = content.replaceAll("@UserUsername@", user.getUsername());
		content = content.replaceAll("@Password@", user.getPassword());
		content = content.replaceAll("@iWriteLink@", getReviewerLinkForUser());
		this.sendNotification(user, "iWrite user details", content);
	}
	
	public void sendReviewEarlyFinishNotification(User user, Course course, ReviewingActivity reviewingActivity) throws MessagingException, UnsupportedEncodingException, MessageException {
		EmailCourse email = course.getEmail(Constants.EMAIL_STUDENT_RECEIVED_REVIEW);
		String content = email.getMessage();
		content = content.replaceAll("@UserName@", user.getFirstname()+" "+user.getLastname());
		content = content.replaceAll("@ActivityName@", reviewingActivity.getName());
		content = content.replaceAll("@ReviewerLink@", getReviewerLinkForUser());
		content = content.replaceAll("@FromName@", fromName);
		String subject = "[" + course.getName().toUpperCase() + "] " + reviewingActivity.getName();
		this.sendNotification(user, subject, content);
	}	
	
	private String getReviewerLinkForUser() {	
		return "http://"+reviewerDomain+"/reviewer/Assignments.html";
	}
	
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
}
