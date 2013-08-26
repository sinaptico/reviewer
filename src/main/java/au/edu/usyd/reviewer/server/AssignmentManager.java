package au.edu.usyd.reviewer.server;

import java.io.File;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.catalina.realm.RealmBase;
import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.gdata.data.docs.DocumentListEntry;

import edu.emory.mathcs.backport.java.util.Arrays;

import au.edu.usyd.feedback.feedbacktracking.FeedbackTracking;
import au.edu.usyd.feedback.feedbacktracking.FeedbackTrackingDao;
import au.edu.usyd.feedback.server.FeedbackServiceImpl;
import au.edu.usyd.reviewer.client.core.Activity;
import au.edu.usyd.reviewer.client.core.Choice;
import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Deadline;
import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.EmailCourse;
import au.edu.usyd.reviewer.client.core.EmailOrganization;
import au.edu.usyd.reviewer.client.core.Grade;
import au.edu.usyd.reviewer.client.core.LogbookDocEntry;
import au.edu.usyd.reviewer.client.core.LogpageDocEntry;
import au.edu.usyd.reviewer.client.core.Organization;
import au.edu.usyd.reviewer.client.core.Question;
import au.edu.usyd.reviewer.client.core.QuestionReview;
import au.edu.usyd.reviewer.client.core.ReviewReply;
import au.edu.usyd.reviewer.client.core.ReviewTemplate;
import au.edu.usyd.reviewer.client.core.Review;
import au.edu.usyd.reviewer.client.core.ReviewEntry;
import au.edu.usyd.reviewer.client.core.ReviewingActivity;
import au.edu.usyd.reviewer.client.core.Section;
import au.edu.usyd.reviewer.client.core.TemplateReply;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.UserGroup;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.StringUtil;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.server.reviewstratergy.RandomReviewStratergy;
import au.edu.usyd.reviewer.server.reviewstratergy.ReviewStratergy;
import au.edu.usyd.reviewer.server.reviewstratergy.SpreadsheetReviewStratergy;
import au.edu.usyd.reviewer.server.util.FileUtil;
import au.edu.usyd.reviewer.server.util.QuestionUtil;

public class AssignmentManager {
	private final Logger logger = LoggerFactory.getLogger(AssignmentManager.class);
	private Map<Long, Timer> activityTimers = Collections.synchronizedMap(new HashMap<Long, Timer>());
	private AssignmentRepository assignmentRepository;
	private EmailNotifier emailNotifier;
	private AssignmentDao assignmentDao = new AssignmentDao(Reviewer.getHibernateSessionFactory());
	private UserDao userDao = UserDao.getInstance();
	private FeedbackServiceImpl feedBackService = new FeedbackServiceImpl();
	private FeedbackTrackingDao feedBackTrackingService = new FeedbackTrackingDao() ;		
	private CourseDao courseDao = CourseDao.getInstance();
	private OrganizationManager organizationManager = OrganizationManager.getInstance();
	private EmailDao emailDao = EmailDao.getInstance();
	private Map<Long, Timer> studentsTimers = Collections.synchronizedMap(new HashMap<Long, Timer>());
	private Map<Long, Timer> coursesTimers = Collections.synchronizedMap(new HashMap<Long, Timer>());
	
	public AssignmentManager() {
	}

	/**
	 * Initialice teh assinment repository, the email notifier and schedule all the activities de all the courses of the organization
	 * @param assignmentRepository
	 * @param emailNotifier
	 * @param organization
	 * @throws MessageException
	 */
	public void initialize(AssignmentRepository assignmentRepository, EmailNotifier emailNotifier, Organization organization) throws MessageException{
		this.assignmentRepository = assignmentRepository;
		this.emailNotifier = emailNotifier;		
		List<Course> courses = courseDao.loadCourses(organization);
		for (Course course : courses) {
			for (WritingActivity writingActivity : course.getWritingActivities()) {
				scheduleActivityDeadline(course, writingActivity);
			}
		}
	}
	
	/**
	 * This method sets the activity as deleted but it doesn't remove it from the database or Google
	 * The activity will be deleted if its status is FINISH
	 * @param writingActivity Writing activity to set as deleted
	 * @throws Exception
	 */
	public void deleteActivity(WritingActivity writingActivity) throws Exception {
		boolean allReviewing = true;
		if (writingActivity.getStatus() == WritingActivity.STATUS_FINISH){
			for(ReviewingActivity reviewing:writingActivity.getReviewingActivities()){
				allReviewing = allReviewing && (reviewing.getStatus() == WritingActivity.STATUS_FINISH);
			}
			if (allReviewing){
				writingActivity.setDeleted(true);
				writingActivity = assignmentDao.save(writingActivity);
			} else {
				throw new MessageException(Constants.EXCEPTION_DELETE_REVIEWING_ACTIVITY_NOT_FINISHED);
			}	 
		} else {
			throw new MessageException(Constants.EXCEPTION_DELETE_WRITING_ACTIVITY_NOT_FINISHED);
		}
	}

	/**
	 * This method sets course as deleted in the database. It doesn't remove it from the database or Google
	 * The course will be deleted if all its activities are finished
	 * @param course Course to set as deleted
	 * @throws Exception
	 */
	public void deleteCourse(Course course) throws Exception {
		
		// writing activities
		for (WritingActivity writingActivity : course.getWritingActivities()) {
			if (writingActivity.getStatus() == WritingActivity.STATUS_FINISH){
				deleteActivity(writingActivity);
			} else {
				throw new MessageException(Constants.EXCEPTION_DELETE_COURSE_NOT_FINISHED);
			}
		}	
		course.setDeleted(true);
		courseDao.save(course);
	}

	/**
	 * Download the documents of the students belongs to the activity and course received as parameter, from Google to the server
	 * @param course
	 * @param writingActivity
	 * @param deadline
	 */
	private void downloadDocuments(Course course, WritingActivity writingActivity, Deadline deadline) {
		Organization organization = course.getOrganization();
		File activityFolder = new File(getDocumentsFolder(course.getId(), writingActivity.getId(), deadline.getId(), "all", organization));
		activityFolder.mkdirs();
		for (DocEntry docEntry : writingActivity.getEntries()) {
			try {
				UserGroup studentGroup = writingActivity.getGroups() ? docEntry.getOwnerGroup() : assignmentDao.loadUserGroupWhereUser(course, docEntry.getOwner());
				String filePath = activityFolder.getAbsolutePath() + "/" + FileUtil.escapeFilename(docEntry.getDocumentId()) + ".pdf";
				
				if (docEntry instanceof LogbookDocEntry) {
					// do nothing
				} else {
					// download PDF document
					if (docEntry.isLocalFile()){
							 if (docEntry.isUploaded()){
								 filePath = Reviewer.getOrganizationsHome() + organization.getName()+ Reviewer.getUploadsHome() + docEntry.getFileName();
							 }else {
								 filePath = Reviewer.getOrganizationsHome() + organization.getName() + Reviewer.getUploadsHome() + Reviewer.getEmptyDocument();
								 
							 }						   
						}else{
							assignmentRepository.downloadDocumentFile(docEntry, filePath);
						}					
				}
				// copy document PDF to zip folder
				String filename = FileUtil.escapeFilename((writingActivity.getGroups() ? "Group " + docEntry.getOwnerGroup().getName() : docEntry.getOwner().getLastname() + ", " + docEntry.getOwner().getFirstname() + " (" + docEntry.getOwner().getUsername() + ")") + " - " + deadline.getName() + ".pdf");
				String filePathZip = getDocumentsFolder(course.getId(), writingActivity.getId(), deadline.getId(), studentGroup.getTutorial(), organization) + "/" + filename;
				FileUtil.copyFile(filePath, filePathZip);
				
				//Copy entry to "all" folder in case of an uploaded file
				if (docEntry.isLocalFile()){
					if (docEntry.getFileName() !=null) {
						filePathZip = getDocumentsFolder(course.getId(), writingActivity.getId(), deadline.getId(), "all", organization) + "/file-" + docEntry.getFileName();
					}else {
						filePathZip = getDocumentsFolder(course.getId(), writingActivity.getId(), deadline.getId(), "all", organization) + "/Empty file -" + docEntry.getTitle() + ".pdf";
					}
					FileUtil.copyFile(filePath, filePathZip);
				}
				
				docEntry.setDownloaded(true);
				docEntry = assignmentDao.save(docEntry);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Failed to download document PDF. ", e);
			}
		}
		
		if (activityFolder.getParentFile() != null){
			// zip PDF documents
			for (File folder : activityFolder.getParentFile().listFiles()) {
				if (folder.isDirectory()) {
					FileUtil.zipFolder(folder, new File(folder.getAbsolutePath() + ".zip"));
				}
			}
		}
	}
	/**
	 * Finish the deadline of the activity and course received as parameters. Download the documents belong to the activity, create the review task and
	 * schedule them and send emails to the lecturers
	 * @param course
	 * @param writingActivity
	 * @param deadline
	 * @throws Exception
	 */
	public void finishActivityDeadline(Course course, WritingActivity writingActivity, Deadline deadline) throws Exception{
		// check activity status
		if (deadline.getStatus() >= Deadline.STATUS_DEADLINE_FINISH) {
			return;
		}
		
		writingActivity.setSaving(true);
		writingActivity = assignmentDao.save(writingActivity);
		
		Deadline finalDeadline = writingActivity.getFinalDeadline();
		//if deadline is the final deadline then finish de activity
		if (finalDeadline != null && finalDeadline.equals(deadline)) {
			// update document writer permissions to reader permissions for ALL
			// documents in activity folder
			assignmentRepository.lockActivityDocuments(writingActivity);
	
			// lock documents
			for (DocEntry docEntry : writingActivity.getEntries()) {
				docEntry.setLocked(true);
				docEntry = assignmentDao.save(docEntry);
			}
			
			// update activity status
			writingActivity.setStatus(Activity.STATUS_FINISH);
			writingActivity = assignmentDao.save(writingActivity);
		}

		// download PDF documents
		downloadDocuments(course, writingActivity, deadline);

		
		// create reviews
		List<ReviewingActivity> reviewingActivities = new ArrayList<ReviewingActivity>();
		for (ReviewingActivity reviewingActivity : writingActivity.getReviewingActivities()) {
			if (deadline.equals(reviewingActivity.getStartDate())) {
				reviewingActivity = updateActivityReviews(course, writingActivity, reviewingActivity,deadline);
				reviewingActivity.setStatus(Activity.STATUS_START);
				reviewingActivity = assignmentDao.save(reviewingActivity);
			}
			reviewingActivities.add(reviewingActivity);
		}

		writingActivity.setReviewingActivities(reviewingActivities);
		// update activity deadline status
		deadline.setStatus(Deadline.STATUS_DEADLINE_FINISH);
		deadline = assignmentDao.save(deadline);
	
		for(Deadline oldDeadline: writingActivity.getDeadlines()){
			if (oldDeadline != null && oldDeadline.getId() != null &&
				oldDeadline.getId().equals(deadline.getId())){
				oldDeadline.setStatus(deadline.getStatus());
			}
		}
		
		// schedule next activity deadline
		scheduleActivityDeadline(course, writingActivity);
		
		if (writingActivity.getEmailStudents()){
			// send assessment finish notification to lecturers
			for (User lecturer : course.getLecturers()) {
				try {
					emailNotifier.sendLecturerDeadlineFinishNotification(lecturer, course, writingActivity, deadline.getName());
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("Failed to send assessment finish notification.", e);
				}
			}
			
			
			final WritingActivity wActivity = writingActivity.clone();
			final Course fCourse = course.clone();
			final Long activityId = writingActivity.getId();
			final Deadline fDeadline = deadline.clone();
	
			Timer timer = studentsTimers.get(activityId);
			if (timer == null){
				timer = new Timer();
			} 
			// send review start notification to students
			for (final ReviewingActivity reviewingActivity : writingActivity.getReviewingActivities()) {
				if (deadline.equals(reviewingActivity.getStartDate()) && (reviewingActivity.getNumStudentReviewers() > 0)) {
					// create task to send email notifications
					// if the task was cancelled then an illegal state exception appears so create a new timer and schedule the task again
					try {
						timer.schedule(new TimerTask(){
							@Override
							public void run() {
								sendReviwingActivityStartNotificationToStudents(fCourse, wActivity, reviewingActivity, fDeadline);	
							}
						}, deadline.getFinishDate());
					} catch(IllegalStateException ise) {
						ise.printStackTrace();
						timer = new Timer();
						timer.schedule(new TimerTask() {
							@Override
							public void run() {
								sendReviwingActivityStartNotificationToStudents(fCourse, wActivity, reviewingActivity, fDeadline);	
							}
						}, deadline.getFinishDate());
					}
				}
			}
			studentsTimers.put(activityId, timer);
		}
		
		writingActivity.setSaving(false);
		writingActivity = assignmentDao.save(writingActivity);
		
		if (writingActivity.getEmailStudents()){
			List<User> admins = organizationManager.getAdminUsers(course.getOrganization());
			// send notification to admin to inform that the start activity process has finished
			for(User admin: admins){
				try{
					emailNotifier.sendNotificationToAdmin(course,writingActivity, null, deadline,admin, Constants.EMAIL_ADMIN_ACTIVITY_DEADLINE_FINISHED);
				} catch(Exception e){
					e.printStackTrace();
					String message = "Failed to send notification of activity saved.";
					if ( admin != null ){
						message +="Admin: " + admin.getEmail();
					}
					logger.error(message,e);
				}
			}
		}
	}

	/**
	 * Send email to the students to notify them about the reviewing activity status. Send email to the admin users to notify them that the emails to the students
	 * were sent
	 * @param course
	 * @param writingActivity
	 * @param reviewingActivity
	 * @param deadline
	 */
	private void sendReviwingActivityStartNotificationToStudents(Course course, WritingActivity writingActivity, ReviewingActivity reviewingActivity, Deadline deadline){
		int iEmailsSent = 0;
		if (writingActivity.getEmailStudents()){
			for (UserGroup studentGroup : course.getStudentGroups()) {
				if (writingActivity.getTutorial().equals(WritingActivity.TUTORIAL_ALL) || writingActivity.getTutorial().equals(studentGroup.getTutorial())) {
					for (User student : studentGroup.getUsers()) {
						try {
							emailNotifier.sendStudentReviewStartNotification(student, course, writingActivity, deadline);
							iEmailsSent++;
						} catch (Exception e) {
							e.printStackTrace();
							String mesagge = "Failed to send review start notification.";
							if (student != null){
								mesagge += " Student: " + student.getEmail();
							}
							logger.error(mesagge, e);
						}
					}
				}
			}
			
			if (iEmailsSent > 0){
				List<User> admins = organizationManager.getAdminUsers(course.getOrganization());
				// send notification to admin to inform that the task finish
				for(User admin: admins){
					try{
						emailNotifier.sendReviewingNotificationToAdmin(course,writingActivity, reviewingActivity, admin, Constants.EMAIL_STUDENT_REVIEW_START);
					} catch(Exception e){
						e.printStackTrace();
						String message = "Failed to send notification of email sent.";
						if ( admin != null ){
							message +="Admin: " + admin.getEmail();
						}
						logger.error(message,e);
					}
				}
			}
		}
	}
	
	/**
	 * Finish a reviewing activity. Download the reviews documents from Google to the servers, zip these documents and send email notifications to the students
	 * @param course
	 * @param reviewingActivity
	 * @param deadline
	 * @throws MessageException
	 */
	public void finishReviewingActivity(Course course, ReviewingActivity reviewingActivity, Deadline deadline) throws MessageException{
		// check activity status
		if (reviewingActivity.getStatus() >= Activity.STATUS_FINISH) {
			return;
		}

		reviewingActivity.setSaving(true);
		reviewingActivity = assignmentDao.save(reviewingActivity);
		
		Organization organization = course.getOrganization();
		
		// download HTML reviews
		UserGroup studentGroup = null;
		for (ReviewEntry reviewEntry : reviewingActivity.getEntries()) {
			if (!reviewEntry.isDeleted()){
				DocEntry docEntry = reviewEntry.getDocEntry();
						
				WritingActivity writingActivity = assignmentDao.loadWritingActivityWhereDocEntry(docEntry); 
				
				if (writingActivity.getTutorial().equalsIgnoreCase(WritingActivity.TUTORIAL_ALL)){
					//Load Group from Reviewer User
					studentGroup = assignmentDao.loadUserGroupWhereUser(course, reviewEntry.getOwner());
					
					//If null, the reviewer is a Tutor, Lecturer or Automatic reviewer with no studentGroup			
					if (studentGroup == null) {
						//Load student group from student to review
						studentGroup = assignmentDao.loadUserGroupWhereUser(course, reviewEntry.getDocEntry().getOwner());
					}
					
					//If null, the document is owned by a group
					User firstStudentFromGroup=null;
					if (studentGroup == null) {
						Set<User> students = reviewEntry.getDocEntry().getOwnerGroup().getUsers();
						for (User user : students) {
							firstStudentFromGroup = user;
						}				
						studentGroup = assignmentDao.loadUserGroupWhereUser(course, firstStudentFromGroup);
					}			
					
					if (studentGroup == null) {
						continue;
					}				
				}else{
					 	
					studentGroup = new UserGroup();
					studentGroup.setTutorial(writingActivity.getTutorial());				
				}	
				String filename = reviewEntry.getOwner().getLastname() + ", " + reviewEntry.getOwner().getFirstname() + " (" + reviewEntry.getOwner().getUsername() + ") - reviewed - " + (docEntry.getOwnerGroup() != null ? "Group " + docEntry.getOwnerGroup().getName() : docEntry.getOwner().getLastname() + ", " + docEntry.getOwner().getFirstname() + " (" + docEntry.getOwner().getId() + ")") + ".html";
				File reviewFile = new File(getDocumentsFolder(course.getId(), reviewingActivity.getId(), reviewingActivity.getStartDate().getId(), studentGroup.getTutorial(), organization) + "/" + FileUtil.escapeFilename(filename));
				reviewFile.getParentFile().mkdirs();
		    	PrintWriter out = null;
				try {
					out = new PrintWriter(new FileWriter(reviewFile));
					out.print(reviewEntry.getReview().getContent());
					if (reviewingActivity.getFormType().equals(ReviewingActivity.REVIEW_TYPE_TEMPLATE)) {
						List<TemplateReply> templateReplies = ((ReviewReply) reviewEntry.getReview()).getTemplateReplies();
						for (TemplateReply tempReply: templateReplies){
							out.print(tempReply.getSection().getText());						
							out.print(tempReply.getText());
						}						
					}
				} catch (IOException e) {
					e.printStackTrace();
					logger.error("Failed to save review HTML", e);
				} finally {
					if (out != null) {
						out.flush();
						out.close();
					}
				}
			}
		}

		// zip HTML reviews 
		String filePath = getDocumentsFolder(course.getId(), reviewingActivity.getId(), reviewingActivity.getStartDate().getId(), "", organization);
		File file = new File(filePath);
		
		if ( file.exists()) {
			for (File folder : file.listFiles()) {		                            
				if (folder != null && folder.isDirectory()) {
					FileUtil.zipFolder(folder, new File(folder.getAbsolutePath() + ".zip"));
				}
			}
		} 
		else {	
			logger.error(Constants.EXCEPTION_ACTIVITY_NOT_FINISHED + " File " + filePath);
		}

		// update activity status
		reviewingActivity.setStatus(Activity.STATUS_FINISH);
		reviewingActivity = assignmentDao.save(reviewingActivity);
		
		// release reviews 
		for(ReviewEntry reviewEntry : reviewingActivity.getEntries()) {
			if (!reviewEntry.isDeleted()){
				DocEntry docEntry = reviewEntry.getDocEntry();
				//Check if the review hasn't been released early
				if (!docEntry.getReviews().contains(reviewEntry.getReview())){
					docEntry.getReviews().add(reviewEntry.getReview());
					docEntry = assignmentDao.save(docEntry);
				}
			}
		}

		WritingActivity writingActivity = assignmentDao.loadWritingActivityWhereDeadline(deadline);
		if (writingActivity.getEmailStudents()){
			// send review finish notification to lecturers
			for (User lecturer : course.getLecturers()) {
				try {
					emailNotifier.sendLecturerDeadlineFinishNotification(lecturer, course, reviewingActivity, reviewingActivity.getName());
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("Failed to send review finish notification.", e);
				}
			}
	
			final WritingActivity wActivity = writingActivity.clone();
			final Course fCourse = course.clone();
			final Deadline fDeadline = deadline.clone();
			final Long activityId = writingActivity.getId();
			Timer timer = studentsTimers.get(activityId);
			if (timer == null){
				timer = new Timer();
			}
			final ReviewingActivity rActivity = reviewingActivity.clone();
			//send review finish notifications to students	
			if(writingActivity != null && writingActivity.getEmailStudents()){		
				// create task to send email notifications
				try{
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							sendReviewingActivityFinishNotificationToStudents(fCourse, wActivity, rActivity, fDeadline);	
						}
					}, deadline.getFinishDate());
				} catch(IllegalStateException ise) {
					ise.printStackTrace();
					timer = new Timer();
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							sendReviewingActivityFinishNotificationToStudents(fCourse, wActivity, rActivity, fDeadline);	
						}
					}, deadline.getFinishDate());
				}
				studentsTimers.put(activityId, timer);
			}
		}

		// read excel file and insert questions into DB
		if (reviewingActivity.getFormType() != null && reviewingActivity.getFormType().equals(ReviewingActivity.REVIEW_TYPE_QUESTION)) {
			try {
				String path = getDocumentsFolder(course.getId(), reviewingActivity.getId(), "aqg", "", organization);
				String filepath = path + Reviewer.getAggLoadExcelPath(); 
				QuestionUtil questionUtil = new QuestionUtil();
				questionUtil.readExcelInsertDB(filepath, course.getOrganization());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Failed to read the excel.", e);
			}
		}
		
		reviewingActivity.setSaving(false);
		reviewingActivity = assignmentDao.save(reviewingActivity);
		if (writingActivity.getEmailStudents()){
			List<User> admins = organizationManager.getAdminUsers(course.getOrganization());
			// send notification to admin to inform that the start activity process has finished
			for(User admin: admins){
				try{
					emailNotifier.sendNotificationToAdmin(course,writingActivity, reviewingActivity, null, admin, Constants.EMAIL_ADMIN_REVIEWING_ACTIVITY_FINISHED);
				} catch(Exception e){
					e.printStackTrace();
					String message = "Failed to send notification of activity saved.";
					if ( admin != null ){
						message +="Admin: " + admin.getEmail();
					}
					logger.error(message,e);
				}
			}
		}
	}

	
	/**
	 * Send reviewing finish email notifications to the students and then notifications to the admin users to notify them about this process 
	 * @param course
	 * @param writingActivity
	 * @param reviewingActivity
	 * @param deadline
	 */
	private void sendReviewingActivityFinishNotificationToStudents(Course course, WritingActivity writingActivity, ReviewingActivity reviewingActivity, Deadline deadline){
		if (writingActivity.getEmailStudents()){
			List<DocEntry> notifiedDocEntries = new ArrayList<DocEntry>();
			int iEmailsSent =0;
			for (ReviewEntry reviewEntry : reviewingActivity.getEntries()) {
				if (!reviewEntry.isDeleted()){
					//Reviewed User
					User user = reviewEntry.getDocEntry().getOwner();
					if (!notifiedDocEntries.contains(reviewEntry.getDocEntry())){
						try {
							if (user != null){
								emailNotifier.sendReviewFinishNotification(user, course, writingActivity, deadline.getName());
							}else{ 
								//it's a document owned by a group
								Set<User> students = reviewEntry.getDocEntry().getOwnerGroup().getUsers();
								for (User userToNotify : students) {
									emailNotifier.sendReviewFinishNotification(userToNotify, course, writingActivity, deadline.getName());								
								}
							}
							notifiedDocEntries.add(reviewEntry.getDocEntry());
							iEmailsSent++;
							
						} catch (Exception e) {
							e.printStackTrace();
							logger.error("Failed to send review finish notification.", e);
						}
					}
				}
			}
			
			if (iEmailsSent > 0){
				List<User> admins = organizationManager.getAdminUsers(course.getOrganization());
				// send notification to admin to inform that the task finish
				for(User admin: admins){
					try{
						emailNotifier.sendReviewingNotificationToAdmin(course,writingActivity, reviewingActivity,admin, Constants.EMAIL_STUDENT_REVIEW_FINISH);
					} catch(Exception e){
						e.printStackTrace();
						String message = "Failed to send notification of email sent.";
						if ( admin != null ){
							message +="Admin: " + admin.getEmail();
						}
						logger.error(message,e);
					}
				}
			}
		}
	}
	
	
	public AssignmentDao getAssignmentDao() {
		if ( assignmentDao == null){
			assignmentDao = new AssignmentDao(Reviewer.getHibernateSessionFactory());
		}
		return assignmentDao;
	}

	public AssignmentRepository getAssignmentRepository() {
		return assignmentRepository;
	}

	public String getDocumentsFolder(long courseId, long activityId, long activityDeadlineId, String tutorial, Organization organization) {
		return String.format(Reviewer.getOrganizationsHome() + FileUtil.replaceBlanks(organization.getName())+ "/" + Reviewer.getDocumentsHome() + "/%s/%s/%s/%s",courseId, activityId, activityDeadlineId, tutorial);
	}

	public String getDocumentsFolder(long courseId, long activityId, String activityDeadlineId, String tutorial, Organization organization) {
		return String.format(Reviewer.getOrganizationsHome() + FileUtil.replaceBlanks(organization.getName())+ "/" + Reviewer.getDocumentsHome() + "/%s/%s/%s/%s", courseId, activityId, activityDeadlineId, tutorial);
	}

	/**
	 * Validate the activity received as parameters, create or update in the database and schedule it
	 * @param course
	 * @param writingActivity
	 * @return
	 * @throws Exception
	 */
	public WritingActivity saveActivity(Course course, WritingActivity writingActivity) throws Exception {
		
		if (course.isSaving() || writingActivity.isSaving() || someReviewingActivityIsSaving(writingActivity)){
			MessageException me = new MessageException(Constants.EXCEPTION_ACTIVITY_OR_COURSE_SAVING);
			me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
			throw me;
		}
		
		// check if status is valid
		validateActivity(writingActivity);

		// check if tutorial is valid
		if (!course.getTutorials().contains(writingActivity.getTutorial()) && !writingActivity.getTutorial().equals(WritingActivity.TUTORIAL_ALL)) {
			MessageException me = new MessageException(Constants.EXCEPTION_INVALID_TUTORIAL + Constants.MESSAGE_RELOAD_COURSES);
			me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
			throw me;
		}

		// create activity folder
		if (writingActivity.getFolderId() == null) {
			assignmentRepository.createActivity(course, writingActivity);
		} else {
			assignmentRepository.updateActivityFolderName(writingActivity);
		}

		writingActivity = assignmentDao.save(writingActivity);
		
		course.getWritingActivities().add(writingActivity);
		courseDao.save(course);

		// schedule next activity deadline
		scheduleActivityDeadline(course, writingActivity);

		if (writingActivity != null){
			writingActivity = writingActivity.clone();
		}
		return writingActivity;
	}
	
	/**
	 * Create or update the folder belongs to the course in Google and set it in the course
	 * @param course
	 * @throws Exception
	 */
	private void setUpFoldersAndTemplates(Course course) throws Exception{
		
		List<DocumentListEntry> templates = assignmentRepository.setUpFolders(course);

		course.getTemplates().clear();
		Organization organization = course.getOrganization();
		for (DocumentListEntry template : templates) {
			DocEntry tempDocEntry = assignmentDao.loadDocEntry(template.getResourceId());
			DocEntry templateEntry = new DocEntry();
			if (!(tempDocEntry == null)){			
				templateEntry = tempDocEntry;
			}
			templateEntry.setDocumentId(template.getResourceId());
			templateEntry.setTitle(template.getTitle().getPlainText());
			templateEntry.setDomainName(organization.getGoogleDomain());
			course.getTemplates().add(templateEntry);
	    }
		
		// create course folder
		assignmentRepository.updateCourse(course);
	}

	
	/**
	 * For each student belongs to the course this method does the following task:
	 * - create or update the student in the datbase
	 * - if the student was created then send email notification with hi/her password in reviewer
	 * - if the student doesn't exist in Google them create him/her 
	 * @param course
	 * @throws Exception
	 */
	private void saveStudentUsers(Course course) throws Exception {
		Organization organization = course.getOrganization();	
		for (UserGroup studentGroup : course.getStudentGroups()) {
			for (User student : studentGroup.getUsers()) {
				try{
					if (student.getOrganization() == null){
						student.setOrganization(organization);
					}
					
					student.addRole(Constants.ROLE_GUEST);
					
					//generate the username with the email
					student.getUsername();
		
					// search student by email so it's no necessary get it by organization because the email is unique
					User user = userDao.getUserByEmail(student.getEmail());
					if (user == null) {
						// student doesn't exist into the database
						if (StringUtil.isBlank(student.getFirstname())){
							logger.error(Constants.EXCEPTION_STUDENT_FIRSTNAME_EMPTY + "\nStudent: " + student.getEmail());
						}
						if(StringUtil.isBlank(student.getLastname())){
							logger.error(Constants.EXCEPTION_STUDENT_LASTNAME_EMPTY + "\nStudent: " + student.getEmail());
						}
						if (!organization.isShibbolethEnabled()){
							// generate password to login in reviewer and send it by email
							  if (student.getPassword() == null){
								  student.setPassword(Long.toHexString(Double.doubleToLongBits(Math.random())));
							  }
							  try{
								  emailNotifier.sendPasswordNotification(student, course);
								  student.setPassword(RealmBase.Digest(student.getPassword(), "MD5",null));
							  } catch(Exception e){
								  logger.error("Failed to send email notification with password to " + student.getEmail());
								  e.printStackTrace();
							  }
						} else {
							// student doesn't have a password to login in reviewer because his organization uses shibboleth 
							student.setPassword(null);
						} 
						student = userDao.save(student);
					} else {
						student.setId(user.getId());
						student.setPassword(user.getPassword());
						if (StringUtil.isBlank(student.getFirstname())){
							if (StringUtil.isBlank(user.getFirstname())){
								logger.error(Constants.EXCEPTION_STUDENT_FIRSTNAME_EMPTY + "\nStudent: " + student.getEmail()); 
							} else {						
								student.setFirstname(user.getFirstname());
							}
						}
						if(StringUtil.isBlank(student.getLastname())){
							if(StringUtil.isBlank(user.getLastname())){
								logger.error(Constants.EXCEPTION_STUDENT_LASTNAME_EMPTY + "\nStudent: " + student.getEmail());
							} else {
								student.setLastname(user.getLastname());
							}
							
						}
					}
					// check if the studet exists in Google Apps
					if (!assignmentRepository.userExists(student.getGoogleAppsEmailUsername())){
						// create the student in Google Apps
						assignmentRepository.createUser(student,organization.getOrganizationPasswordNewUsers() + student.getUsername());
					}
				} catch(Exception e){
					e.printStackTrace();
					String email = "";
					if (student != null && student.getEmail()!= null){
						email = "\nEmail: " + student.getEmail();
					}
					logger.error("Failed to save the student." + email);
				}
			}
			try{
				studentGroup = assignmentDao.save(studentGroup);
			} catch(Exception e){
				String usergroup = "";
				if ( studentGroup != null && studentGroup.getId() != null ){
					usergroup = "\nUser group id: " + studentGroup.getId();
				}
				logger.error("Failed to save a user group" + usergroup);
			}
				
		}	
	}
		
	/**
	 * For each lecturer of the course this method does the followig task:
	 * - create or update the lecturer in the database
	 * - if lecturer was created then send email notification with his/her password of reviewer
	 * - if the lecturer doesn't exist in Google them create him/her
	 * @param course
	 * @throws Exception
	 */
	private void saveLecturerUsers(Course course) throws Exception{
		Organization organization = course.getOrganization();
		for (User lecturer : course.getLecturers()) {
			String message = (lecturer.getEmail()!= null)?"Lecturer: " + lecturer.getEmail() +"\n":"";
			if (lecturer.getOrganization() == null){
				lecturer.setOrganization(organization);
			}
			// if the domain of the lecturer's email belong to the organizations email domains ==> create the user in Google Apps.
			if (lecturer.getDomain() != null && lecturer.getOrganization() != null && 
			    !lecturer.getOrganization().domainBelongsToEmailsDomain(lecturer.getDomain())){	
				throw new MessageException(message + Constants.EXCEPTION_LECTURER_INVALID_DOMAIN);
			}
			
			// lecture doesn't exists in database
			if (lecturer.getEmail() != null){
				lecturer.getUsername();
			} else {
				throw new MessageException(message + Constants.EXCEPTION_LECTURER_EMAIL_EMPTY);
			}
			
			// search lecture in database
			User user = userDao.getUserByEmail(lecturer.getEmail());
			if (user == null) {	
				if (StringUtil.isBlank(lecturer.getFirstname())){
					throw new MessageException(message + Constants.EXCEPTION_LECTURER_FIRSTNAME_EMPTY + "Lecturer: " + lecturer.getEmail());
				}
				if(StringUtil.isBlank(lecturer.getLastname())){
					throw new MessageException(message + Constants.EXCEPTION_LECTURER_LASTNAME_EMPTY + "Lecturer: " + lecturer.getEmail());
				}
				if (!organization.isShibbolethEnabled()){
					// generate password to login in reviewer and send it by email
					if (lecturer.getPassword() == null){
						lecturer.setPassword(Long.toHexString(Double.doubleToLongBits(Math.random())));
					}
					try{
						emailNotifier.sendPasswordNotification(lecturer, course);
						lecturer.setPassword(RealmBase.Digest(lecturer.getPassword(), "MD5",null));
					} catch(Exception e){
						logger.error("Failed to send email notification with password to " + lecturer.getEmail());
					}
				} else {
					// lecture doesn't have a password to login in reviewer because uses shibboleth 
					lecturer.setPassword(null);
				} 
				lecturer.addRole(Constants.ROLE_STAFF);
			} else {
				lecturer.setId(user.getId());
				lecturer.setPassword(user.getPassword());
				lecturer.setRole_name(user.getRole_name());
				if (StringUtil.isBlank(lecturer.getFirstname())){
					if (StringUtil.isBlank(user.getFirstname())){
						throw new MessageException(message + Constants.EXCEPTION_LECTURER_FIRSTNAME_EMPTY + "Lecturer: " + lecturer.getEmail());
					} else {
						lecturer.setFirstname(user.getFirstname());
					}
				}
				if(StringUtil.isBlank(lecturer.getLastname())){
					if(StringUtil.isBlank(user.getLastname())){
						throw new MessageException(message + Constants.EXCEPTION_LECTURER_LASTNAME_EMPTY + "Lecturer: " + lecturer.getEmail());
					} else {
						lecturer.setLastname(user.getLastname());
					}
				}
				if (!lecturer.isAdmin() && !lecturer.isStaff() && !lecturer.isSuperAdmin()){
					lecturer.addRole(Constants.ROLE_STAFF);
				}
			}
			// if the lecturer is Admin and is the same user is used to access to Google, then remove them from lecturers
			// otherwise Google will delete his/hers permissions and will give an exception
			if ((lecturer.isAdmin() || lecturer.isSuperAdmin()) && 
					(lecturer.getEmail() != null && lecturer.getEmail().equalsIgnoreCase(organization.getGoogleUsername()))){
					throw new MessageException(message + Constants.EXCEPTION_ADMIN_CAN_NO_BE_LECTURER_OR_TUTOR + "Lecturer: " + lecturer.getEmail());
			} 
			lecturer = userDao.save(lecturer);
			// if the user doesn't exist in Google Apps then create it
			if (!assignmentRepository.userExists(lecturer.getGoogleAppsEmailUsername())){
				assignmentRepository.createUser(lecturer,organization.getOrganizationPasswordNewUsers()+lecturer.getUsername());
			}			
		}
	}
	
	/**
	 * For each tutor of the course this method does the followig task:
	 * - create or update the tutor in the database
	 * - if tutor was created then send email notification with his/her password of reviewer
	 * - if the tutor doesn't exist in Google them create him/her
	 * @param course
	 * @throws Exception
	 */
	private void saveTutorUsers(Course course) throws Exception {
		Organization organization = course.getOrganization();
		for (User tutor : course.getTutors()) {
			String message = ( tutor.getEmail() != null)?"Tutor: " + tutor.getEmail() + "\n":"";
			
			if (tutor.getOrganization() == null){
				tutor.setOrganization(organization);
			}
			
			// if the domain of the tutor's email belong to the organizations email domains ==> create the user in Google Apps.
			if (tutor.getDomain() != null && tutor.getOrganization() != null && 
					!tutor.getOrganization().domainBelongsToEmailsDomain(tutor.getDomain())){	
				throw new MessageException(message + Constants.EXCEPTION_TUTORS_INVALID_DOMAIN);
			}
			
			if (tutor.getEmail() != null){
				tutor.getUsername();
			} else {
				throw new MessageException(message + Constants.EXCEPTION_TUTOR_EMAIL_EMPTY);
			}

			// search tutor in database
			User user = userDao.getUserByEmail(tutor.getEmail());
			if (user == null) {
				if (StringUtil.isBlank(tutor.getFirstname())){
					throw new MessageException(message + Constants.EXCEPTION_TUTOR_FIRSTNAME_EMPTY + "Tutor: " + tutor.getEmail());
				}
				if(StringUtil.isBlank(tutor.getLastname())){
					throw new MessageException(message + Constants.EXCEPTION_TUTOR_LASTNAME_EMPTY  + "Tutor: " + tutor.getEmail());
				}
				// tutor doesn't exists in database
				if (!organization.isShibbolethEnabled()){
					// generate password to login in reviewer and send it by email
					if (tutor.getPassword() == null){
						tutor.setPassword(Long.toHexString(Double.doubleToLongBits(Math.random())));
					}
					try{
						emailNotifier.sendPasswordNotification(tutor, course);
						tutor.setPassword(RealmBase.Digest(tutor.getPassword(), "MD5",null));
					} catch(Exception e){
						logger.error("Failed to send email notification with password to " + tutor.getEmail());
					}
				} else {
					// tutor doesn't have a password to login in reviewer because uses shibboleth 
					tutor.setPassword(null);
				} 
				
				tutor.addRole(Constants.ROLE_STAFF);
				
			} else {
				tutor.setId(user.getId());
				tutor.setPassword(user.getPassword());
				tutor.setRole_name(user.getRole_name());
				
				if (StringUtil.isBlank(tutor.getFirstname())){
					if (StringUtil.isBlank(user.getFirstname())){
						throw new MessageException(message + Constants.EXCEPTION_TUTOR_FIRSTNAME_EMPTY  + "Tutor: " + tutor.getEmail());
					} else {
						tutor.setFirstname(user.getFirstname());
					}
				}
				if(StringUtil.isBlank(tutor.getLastname())){
					if(StringUtil.isBlank(user.getLastname())){
						throw new MessageException(message + Constants.EXCEPTION_TUTOR_LASTNAME_EMPTY  + "Tutor: " + tutor.getEmail());
					} else {
						tutor.setLastname(user.getLastname());
					}
				}
				if (!tutor.isAdmin() && !tutor.isStaff() && !tutor.isSuperAdmin()){
					tutor.addRole(Constants.ROLE_STAFF);
				}
			}
			
			// if the tutor is Admin and is the same user is used to access to Google, then remove them from tutors
			// otherwise Google will delete his/hers permissions and will give an exception
			if ((tutor.isAdmin() || tutor.isSuperAdmin()) && 
			   (tutor.getEmail() != null && tutor.getEmail().equalsIgnoreCase(organization.getGoogleUsername()))){
				throw new MessageException(message + Constants.EXCEPTION_ADMIN_CAN_NO_BE_LECTURER_OR_TUTOR  + "Tutor: " + tutor.getEmail());
			}
			tutor = userDao.save(tutor);
			// if the user doesn't exist in Google Apps then create it
			if (!assignmentRepository.userExists(tutor.getGoogleAppsEmailUsername())){
				assignmentRepository.createUser(tutor,organization.getOrganizationPasswordNewUsers()+tutor.getUsername());
			}			
		}

	}
	
	/**
	 * Create activities and revies for the new students belong to the course 
	 * @param course
	 * @throws Exception
	 */
	private void processActivitiesForNewUsers(Course course) throws Exception {
		
		// update activities and reviews
		for (WritingActivity writingActivity : course.getWritingActivities()) {
			// validate activity
			validateActivity(writingActivity);
			
			// create documents for new users
			if (writingActivity.getStatus() >= Activity.STATUS_START && writingActivity.getStatus() < Activity.STATUS_FINISH) {
				writingActivity = updateActivityDocuments(course, writingActivity);
			}
			// create reviews for new users
			for (ReviewingActivity reviewingActivity : writingActivity.getReviewingActivities()) {
				if (reviewingActivity.getStatus() == Activity.STATUS_START) {
					reviewingActivity = updateActivityReviews(course, writingActivity, reviewingActivity, null);
				}
			}
		}
	}
	
	/**
	 * Validate a writing activity.
	 * @param writingActivity
	 * @throws MessageException
	 */
	private void validateActivity(WritingActivity writingActivity) throws MessageException{
		MessageException me = null;		
		int index = 0;
		// All the reviewing finish date must be greater than the corresponding writing activity deadline finish date
		for (ReviewingActivity reviewingActivity : writingActivity.getReviewingActivities()){
			
			// All the review activity start deadline must belong to the deadlines of the writing activity
			// This validation detects if a deadline of the writing activity was deleted by it's still used in a reviewing activity
			if (!writingActivity.getDeadlines().contains(reviewingActivity.getStartDate())){
				me = new MessageException(Constants.EXCEPTION_NOT_REVIEWING_ACTIVITY_START_DEADLINE + reviewingActivity.getName());
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
				throw me;
			}
			
			if (reviewingActivity.getStartDate() != null && reviewingActivity.getStartDate().getFinishDate() == null){
				me = new MessageException(Constants.EXCEPTION_NOT_ACTIVITY_FINISH_DATE);
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
				throw me;
			}
			// finish date of a review template can not be empty
			if (reviewingActivity.getFinishDate() == null){
						me = new MessageException(Constants.EXCEPTION_EMPTY_REVIEWING_ACTIVITY_FINISH_DATE  + reviewingActivity.getName());
						me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
						throw me;
			}
			
			if (reviewingActivity.getFinishDate() != null && reviewingActivity.getStartDate()!= null && 
				reviewingActivity.getFinishDate().before(reviewingActivity.getStartDate().getFinishDate()) ||
				reviewingActivity.getFinishDate().equals(reviewingActivity.getStartDate().getFinishDate())){
					me = new MessageException(Constants.EXCEPTION_WRONG_REVIEWING_ACTIVITY_FINISH_DATE  + reviewingActivity.getName());
					me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
					throw me;
			}
		}
		
		// verify if activity start date is before to all the deadlines finish dates 
		for(Deadline deadline : writingActivity.getDeadlines()){
			if (writingActivity.getStartDate() != null && deadline != null && 
				deadline.getFinishDate() != null && writingActivity.getStartDate().after(deadline.getFinishDate())){
				me = new MessageException(Constants.EXCEPTION_ACTIVITY_START_AFTER_DEADLINE  + deadline.getName());
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
				throw me;
			}
		}
		// It happens when the activity is updated by the schedule and the logged used is showing an old version on the screen
		if (writingActivity.getId() != null && writingActivity.getStatus() != assignmentDao.loadWritingActivity(writingActivity.getId()).getStatus()) {
			me = new MessageException(Constants.EXCEPTION_INVALID_STATUS);
			me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
			throw me;
		}

	}
	
	/** 
	 * Save course implies:
	 * - Validate the course, its lecturers, tutors and students
	 * - If the course doesn't have email templates then create them
	 * - Create or update the folder of the course in Google
	 * - Create or update the lecturers, tutors and students belong to the course
	 * - Process activities for new students. It's create or update the activity and its documents and set the in the database and Google for each student.
	 * - The last task is send email notifications to the admin users to notify them that this process has finished
	 * @param course
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public Course saveCourse(Course course, User user) throws Exception {
		
		if (course.isSaving() || someWritingActivityIsSaving(course)){
			MessageException me = new MessageException(Constants.EXCEPTION_ACTIVITY_OR_COURSE_SAVING);
			me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
			throw me;
		}
		
		// Validate the course
		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		// you can not create courses for a old semester (not current one) or years before to the current one
		if ((course.getSemester() == 1 && month > 7 && course.getYear() <= year) || 
			(course.getYear() < year )){
			throw new MessageException(Constants.EXCEPTION_WRONG_SEMESTER);
		}
		
		if (StringUtil.isBlank(course.getName())){
			throw new MessageException(Constants.EXCEPTION_EMPTY_COURSE_NAME);
		}
		boolean hasTutorials = false;
		for (String tutorial: course.getTutorials()){
			hasTutorials |= !StringUtil.isBlank(tutorial);
		}
		if (!hasTutorials){
			throw new MessageException(Constants.EXCEPTION_EMPTY_COURSE_TUTORIALS);
		}
		
		// validate students
		validateStudents(course);
		
		try{
			
			// Add emails to the course
			course = addEmails(course);
			
			//Set up folders and templates
			setUpFoldersAndTemplates(course);
			
			// If logged user is staff and he/she doesn't belong to the lecturers of the course 
			// then add him/her to the lectures of the course
			if (user.isStaff() && !isCourseLecturer(course,user)){
				course.getLecturers().add(user);
			}
			
			// save lecturer users
			saveLecturerUsers(course);
			
			// save tutor users
			saveTutorUsers(course);
			
			// update course document permissions for instructors
			assignmentRepository.updateCourseDocumentPermissions(course, user);
			
			// set course domain
			course.setDomainName(course.getOrganization().getGoogleDomain());
			
			// The students will be saved in the timer so take them out from the course to save it and then set them again in the course
			Set<UserGroup> userGroups = course.getStudentGroups();
			course.setStudentGroups(new HashSet<UserGroup>());
			
			course.setSaving(true);
			// save course in DB
			course = courseDao.save(course);
			
			// update emails
			course = updateEmails(course);
			
			course.setStudentGroups(userGroups);
			
			Timer timer = coursesTimers.get(course.getId());
			if (timer == null){
				timer = new Timer();
			}
			
			final Course courseTimer = course.clone();
			try{
				// create task to save the students, process activities for new users and send email notification to admin users
				timer.schedule(new TimerTask() {
					@Override
						public void run() {
						 	try{
								// save student users
								saveStudentUsers(courseTimer);
								
								// save course in DB
								courseDao.save(courseTimer);
								
								
								// for each activity create documents and reviewers for new users
								processActivitiesForNewUsers(courseTimer);
								
								courseTimer.setSaving(false);
								// save course in DB
								courseDao.save(courseTimer);
								
								// Get the admin users of the organization
								List<User> admins = organizationManager.getAdminUsers(courseTimer.getOrganization());
								// send notification to admin to inform that the save course process has finished
								for(User admin: admins){
									emailNotifier.sendSaveCourseFinishedNotificationToAdmin(courseTimer,  admin, Constants.EMAIL_SAVE_COURSE_FINISHED);
								}
						 	} catch(Exception e) {
						 		logger.error("Failed to save the students or process teh activities for new users");
						 		e.printStackTrace();
						 	}
						}
				}, new Date());		
				coursesTimers.put(course.getId(), timer);
			} catch(IllegalStateException ise) {
				ise.printStackTrace();
				timer = new Timer();
				// create task to save the students, process activities for new users and send email notification to admin users
				timer.schedule(new TimerTask() {
					@Override
						public void run() {
						 	try{
								// save student users
								saveStudentUsers(courseTimer);
								
								// save course in DB
								courseDao.save(courseTimer);
								
								
								// for each activity create documents and reviewers for new users
								processActivitiesForNewUsers(courseTimer);
								
								// Get the admin users of the organization
								List<User> admins = organizationManager.getAdminUsers(courseTimer.getOrganization());
								// send notification to admin to inform that the save course process has finished
								for(User admin: admins){
									emailNotifier.sendSaveCourseFinishedNotificationToAdmin(courseTimer,  admin, Constants.EMAIL_SAVE_COURSE_FINISHED);
								}
						 	} catch(Exception e) {
						 		logger.error("Failed to save the students or process teh activities for new users");
						 		e.printStackTrace();
						 	}
						}
				}, new Date());		
				coursesTimers.put(course.getId(), timer);
			}
			
			
		} catch(Exception e){
			e.printStackTrace();
			// rollback TODO COMPLETE ROLLBACK
			if (course != null && course.getId() == null){
				// delete orphans emails, whose organizationId and courseId are equals to null
				emailDao.deleteOrphanEmails();
			}
			if (e instanceof MessageException){
				throw (MessageException)e;
			} else {
				throw new MessageException(Constants.EXCEPTION_SAVE_COURSE);
			}
		}
		return course;
	}

	/**
	 * Schedule activity deadline. Check the state of the activity and schedule it in different process taking in 
	 * consideration its state
	 * @param course
	 * @param writingActivity
	 * @throws MessageException
	 */
	private void scheduleActivityDeadline(Course course, WritingActivity writingActivity) throws MessageException{
		final Long courseId = course.getId();
		final Long activityId = writingActivity.getId();

		// cancel previously scheduled Activity deadline
		Timer timer = activityTimers.get(activityId);
		if (timer != null) {
			timer.cancel();
		}
		timer = new Timer();
		activityTimers.put(activityId, timer);
		
		// schedule new Activity deadline
		if (writingActivity.getStatus() < Activity.STATUS_START && writingActivity.getStartDate() != null) {
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					try {
						startActivity(courseDao.loadCourse(courseId), assignmentDao.loadWritingActivity(activityId));
					} catch (MessageException e) {
						logger.error("Error starting activity " + activityId);
						e.printStackTrace();
					}	
				}
			}, writingActivity.getStartDate());
		} else if (writingActivity.getStatus() < Activity.STATUS_FINISH || !writingActivity.getReviewingActivities().isEmpty()) {
			for(final Deadline deadline : assignmentDao.loadWritingActivity(activityId).getDeadlines()) {
				if ( deadline.getStatus() < Deadline.STATUS_DEADLINE_START){
					if (writingActivity != null && writingActivity.getStartDate() != null){
						timer.schedule(new TimerTask() {
							@Override
							public void run() {			
								try {
									startDeadlines(courseDao.loadCourse(courseId), assignmentDao.loadWritingActivity(activityId), deadline);
								} catch (Exception e) {
									logger.error("Error starting deadline " + activityId);
									e.printStackTrace();
								}
							}
						}, writingActivity.getStartDate());
					} else {
						if (writingActivity != null){
							logger.error("Could not start deadlines of writing activity " + writingActivity.getName() + " because it has null startDate");
						} else {
							logger.error("Could not start deadlines of writing activity becuase it is null");
						}
					}
				} else if(deadline.getStatus() < Deadline.STATUS_DEADLINE_FINISH && deadline.getFinishDate() != null){
					timer.schedule(new TimerTask() {
						@Override
						public void run() {			
							try {
								finishActivityDeadline(courseDao.loadCourse(courseId), assignmentDao.loadWritingActivity(activityId), deadline);
							} catch (Exception e) {
								logger.error("Error finishing  activity deadline " + activityId);
								e.printStackTrace();
							}
						}
					}, deadline.getFinishDate());
//					break;
				} else {
					for(final ReviewingActivity reviewingActivity : assignmentDao.loadWritingActivity(activityId).getReviewingActivities()) {
						if(deadline.equals(reviewingActivity.getStartDate())) {
							if(reviewingActivity.getStatus() == Activity.STATUS_START && reviewingActivity.getFinishDate() != null){
								timer.schedule(new TimerTask() {
									@Override
									public void run() {
										try {
											finishReviewingActivity(courseDao.loadCourse(courseId), assignmentDao.loadReviewingActivity(reviewingActivity.getId()),deadline);
										} catch (Exception e) {
											logger.error("Error finishing reviewing activity " + reviewingActivity.getId());
											e.printStackTrace();
										}
									}
								}, reviewingActivity.getFinishDate());
							} else if(reviewingActivity.getStatus() < Activity.STATUS_START && reviewingActivity.getFinishDate() != null){
								timer.schedule(new TimerTask() {
									@Override
									public void run() {
										try {
											startReviewingActivity(courseDao.loadCourse(courseId), assignmentDao.loadWritingActivity(activityId), assignmentDao.loadReviewingActivity(reviewingActivity.getId()),deadline);
										} catch (Exception e) {
											logger.error("Error starting reviewing activity " + reviewingActivity.getId());
											e.printStackTrace();
										}
									}
								}, deadline.getFinishDate());
							}  
						}
					}
				}
			}
		}
	}

	
	/**
	 * Start an activity. It implies update the documents related with the activity, start the deadlines belong to the actiivty,
	 * schedule these deadlines and send email notifications
	 * @param course
	 * @param writingActivity
	 * @throws MessageException
	 */
	public void startActivity(Course course, WritingActivity writingActivity) throws MessageException{

		
		// check activity status
		if (writingActivity.getStatus() >= Activity.STATUS_START) {
			return;
		}

		writingActivity.setSaving(true);
		writingActivity = assignmentDao.save(writingActivity);
		
		writingActivity = updateActivityDocuments(course, writingActivity);

		// update activity status
		writingActivity.setStatus(Activity.STATUS_START);		
		
		// update document entry domains
		Organization organization = course.getOrganization();
		String domainName = organization.getGoogleDomain();
		Set<DocEntry> docEntries = writingActivity.getEntries();
		for (DocEntry docEntry : docEntries) {
			docEntry.setDomainName(domainName);
		}
		
		// save activity 
		writingActivity = assignmentDao.save(writingActivity);
		
		// start all deadlines
		for(Deadline deadline: writingActivity.getDeadlines()){
			deadline.setStatus(Deadline.STATUS_DEADLINE_START);
			deadline = assignmentDao.save(deadline);
		}
		
		scheduleActivityDeadline(course, writingActivity);
		
		
		final WritingActivity wActivity = writingActivity.clone();
		final Course fCourse = course.clone();
		final Long activityId = writingActivity.getId();
		Timer timer = studentsTimers.get(activityId);
		if (timer == null){
			timer = new Timer();
		}
		try{
			// create task to send email notifications 
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					sendActivityStartNotificationToStudents(fCourse, wActivity);	
				}
			}, writingActivity.getStartDate());
		} catch(IllegalStateException ise) {
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					sendActivityStartNotificationToStudents(fCourse, wActivity);	
				}
			}, writingActivity.getStartDate());
		}
		studentsTimers.put(activityId, timer);
		
		if (writingActivity.getEmailStudents()){
			List<User> admins = organizationManager.getAdminUsers(course.getOrganization());
			// send notification to admin to inform that the start activity process has finished
			for(User admin: admins){
				try{
					emailNotifier.sendNotificationToAdmin(course,writingActivity, null,null, admin, Constants.EMAIL_ADMIN_ACTIVITY_STARTED);
				} catch(Exception e){
					e.printStackTrace();
					String message = "Failed to send notification of activity saved.";
					if ( admin != null ){
						message +="Admin: " + admin.getEmail();
					}
					logger.error(message,e);
				}
			}
		}
		writingActivity.setSaving(false);
		writingActivity = assignmentDao.save(writingActivity);
	}
	
	/**
	 * Send notifications to the students to inform them about the start of the activity. After this, send email notifications to
	 * the admin users to inform them that this task has finished 
	 * @param course
	 * @param writingActivity
	 */
	private void sendActivityStartNotificationToStudents(Course course, WritingActivity writingActivity){
		if (writingActivity.getEmailStudents()){
			// get final deadline   
			Deadline finalDeadline = writingActivity.getFinalDeadline();
			if (finalDeadline != null){
				// send assessment start notification to students
				if (writingActivity.getEmailStudents()) {
					int iEmailsSent = 0;
					for (UserGroup studentGroup : course.getStudentGroups()) {
						if (writingActivity.getTutorial().equals(WritingActivity.TUTORIAL_ALL) || writingActivity.getTutorial().equals(studentGroup.getTutorial())) {
							for (User student : studentGroup.getUsers()) {
								try {
									emailNotifier.sendStudentActivityStartNotification(student, course, writingActivity, finalDeadline);
									iEmailsSent++;
								} catch (Exception e) {
									e.printStackTrace();
									logger.error("Failed to send assessment start notification.", e);
									if ( student != null ){
										logger.error("Start notification not sent to " + student.getEmail());
									}
								}
							}
						}
					}
					if (iEmailsSent > 0){
						List<User> admins = organizationManager.getAdminUsers(course.getOrganization());
						// send notification to admin to inform that the task finish
						for(User admin: admins){
							try{
								emailNotifier.sendActivityNotificationToAdmin(course,writingActivity, admin, Constants.EMAIL_STUDENT_ACTIVITY_START);
							} catch(Exception e){
								e.printStackTrace();
								String message = "Failed to send notification of email sent.";
								if ( admin != null ){
									message +="Admin: " + admin.getEmail();
								}
								logger.error(message,e);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Start deadline of the activity. It implies update the documents of the activity, change the state of the deadline and schedule it
	 * @param course
	 * @param writingActivity
	 * @param deadline
	 * @throws MessageException
	 */
	public void startDeadlines(Course course, WritingActivity writingActivity,Deadline deadline) throws MessageException{

		writingActivity.setSaving(true);
		writingActivity = assignmentDao.save(writingActivity);
		
		writingActivity = updateActivityDocuments(course, writingActivity);

		// update document entry domains
		Organization organization = course.getOrganization();
		String domainName = organization.getGoogleDomain();
		Set<DocEntry> docEntries = writingActivity.getEntries();
		for (DocEntry docEntry : docEntries) {
			docEntry.setDomainName(domainName);
		}
		
		// save activity 
		writingActivity = assignmentDao.save(writingActivity);
		
		// start the deadline and save it
		deadline.setStatus(Deadline.STATUS_DEADLINE_START);
		deadline = assignmentDao.save(deadline);
		
		for(Deadline oldDeadline: writingActivity.getDeadlines()){
			if (oldDeadline != null && oldDeadline.getId() != null &&
				oldDeadline.getId().equals(deadline.getId())){
				oldDeadline.setStatus(deadline.getStatus());
			}
		}
		
		// schedule the activity deadlines
		scheduleActivityDeadline(course, writingActivity);
		writingActivity.setSaving(false);
		writingActivity = assignmentDao.save(writingActivity);
		
		if (writingActivity.getEmailStudents()){
			List<User> admins = organizationManager.getAdminUsers(course.getOrganization());
			// send notification to admin to inform that the start activity process has finished
			for(User admin: admins){
				try{
					emailNotifier.sendNotificationToAdmin(course,writingActivity,null,deadline, admin, Constants.EMAIL_ADMIN_ACTIVITY_DEADLINE_STARTED);
				} catch(Exception e){
					e.printStackTrace();
					String message = "Failed to send notification of activity saved.";
					if ( admin != null ){
						message +="Admin: " + admin.getEmail();
					}
					logger.error(message,e);
				}
			}
		}
	}

	/**
	 * Submit a document implies download the document as PDF from Google to the servers and lock the document in Google 
	 * @param docEntry
	 * @return
	 * @throws Exception
	 */
	public DocEntry submitDocument(DocEntry docEntry) throws Exception {
		synchronized (docEntry.getDocumentId().intern()) {
			WritingActivity writingActivity = assignmentDao.loadWritingActivityWhereDocEntry(docEntry);
			Course course = assignmentDao.loadCourseWhereWritingActivity(writingActivity);
			Organization organization = course.getOrganization();
			File activityFolder = new File(this.getDocumentsFolder(course.getId(), writingActivity.getId(), writingActivity.getDeadlines().get(writingActivity.getDeadlines().size() - 1).getId(), WritingActivity.TUTORIAL_ALL, organization));
			activityFolder.mkdirs();
			String filePath = activityFolder.getAbsolutePath() + "/" + FileUtil.escapeFilename(docEntry.getDocumentId()) + ".pdf";
				
			if (docEntry instanceof LogbookDocEntry) {
				LogbookDocEntry logbookDocEntry = (LogbookDocEntry) docEntry;
				LogpageDocEntry logpageDocEntry = logbookDocEntry.getPages().get(logbookDocEntry.getPages().size() - 1);
				if (logpageDocEntry.getSubmitted() == null) {
					try {
						// lock document
						logpageDocEntry.setLocked(true);
						logpageDocEntry.setSubmitted(new Date());
						assignmentRepository.updateDocument(logpageDocEntry);

						// document download pdf
						assignmentRepository.downloadDocumentFile(logpageDocEntry, activityFolder.getAbsolutePath() + "/" + FileUtil.escapeFilename(logpageDocEntry.getDocumentId()) + ".pdf");
						logpageDocEntry.setDownloaded(true);

						// create new document
						LogpageDocEntry newLogpageEntry = new LogpageDocEntry();
						newLogpageEntry.setTitle("Entry " + (logbookDocEntry.getPages().size() + 1));
						logbookDocEntry.getPages().add(newLogpageEntry);
						assignmentRepository.createDocument(writingActivity, logbookDocEntry, course);
						newLogpageEntry = assignmentDao.save(newLogpageEntry);
					} catch (Exception e) {
						// unlock document
						e.printStackTrace();
						logpageDocEntry.setLocked(false);
						assignmentRepository.updateDocument(logpageDocEntry);
						throw e;
					}

					// submit entry
					logpageDocEntry = assignmentDao.save(logpageDocEntry);
					logbookDocEntry = assignmentDao.save(logbookDocEntry);

					// merge document pdf
					try {
						PDFMergerUtility pdfMerger = new PDFMergerUtility();
						for (LogpageDocEntry source : logbookDocEntry.getPages()) {
							if (source.getLocked()) {
								String sourceFilename = activityFolder.getAbsolutePath() + "/" + FileUtil.escapeFilename(source.getDocumentId()) + ".pdf";
								String targetFilename = activityFolder.getAbsolutePath() + "/" + FileUtil.escapeFilename(source.getDocumentId()) + "-annotated.pdf";
								PDDocument doc = null;
								try {
									doc = PDDocument.load(sourceFilename);
									List<PDPage> pages = doc.getDocumentCatalog().getAllPages();
									PDFont font = PDType1Font.TIMES_ROMAN;
									float headerFontSize = 10.0f;
									for (int i = 0; i < pages.size(); i++) {
										// pdf header
										DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss z");
										String date = dateFormat.format(source.getSubmitted());
										String header = String.format("%s %s, %s, %s - Page %s of %s", logbookDocEntry.getOwner().getFirstname(), logbookDocEntry.getOwner().getLastname(), date, source.getTitle(), i + 1, pages.size());
										PDPage page = pages.get(i);
										PDRectangle pageSize = page.findMediaBox();
										float stringWidth = font.getStringWidth(header);
										float x = (pageSize.getWidth() - (stringWidth * headerFontSize) / 1000f) - headerFontSize - 15;
										float y = pageSize.getHeight() - headerFontSize - 15;

										// draw pdf header
										PDPageContentStream contentStream = new PDPageContentStream(doc, page, true, true);
										contentStream.beginText();
										contentStream.setFont(font, headerFontSize);
										contentStream.moveTextPositionByAmount(x, y);
										contentStream.drawString(header);
										contentStream.endText();
										contentStream.close();
									}
									doc.save(targetFilename);
									pdfMerger.addSource(targetFilename);
								} catch (Exception e) {
									e.printStackTrace();
									logger.error("Error adding PDF header", e);
								} finally {
									if (doc != null) {
										doc.close();
									}
								}
							}
						}

						// merge PDFs
						pdfMerger.setDestinationFileName(activityFolder.getAbsolutePath() + "/" + FileUtil.escapeFilename(logbookDocEntry.getDocumentId()) + ".pdf");
						pdfMerger.mergeDocuments();

						// add document metadata
						PDDocument doc = PDDocument.load(pdfMerger.getDestinationFileName());
						PDDocumentInformation docInfo = doc.getDocumentInformation();
						docInfo.setTitle(course.getName() + " - " + writingActivity.getName());
						docInfo.setAuthor(logbookDocEntry.getOwner().getLastname() + ", " + logbookDocEntry.getOwner().getFirstname());
						doc.setDocumentInformation(docInfo);
						doc.save(pdfMerger.getDestinationFileName());
						doc.close();
					} catch (Exception e) {
						e.printStackTrace();
						logger.error("Error merging PDFs", e);
					}

				} else {
					throw new MessageException(Constants.EXCEPTION_DOCUMENT_ALREADY_SUBMITTED);
				}
			} else {
				if (writingActivity.getEarlySubmit()){
					docEntry.setEarlySubmitDate(new Date());
					docEntry = assignmentDao.save(docEntry);
				}
				
				if (docEntry.isLocalFile()){
					FileUtil.copyFile(Reviewer.getOrganizationsHome() + organization.getName()+ Reviewer.getUploadsHome() + docEntry.getFileName(), filePath);
				}else{
					assignmentRepository.downloadDocumentFile(docEntry, filePath);
				}
			}
			if (writingActivity.getStatus() >= Activity.STATUS_FINISH) {
				// copy document PDF to zip folder
				UserGroup studentGroup = writingActivity.getGroups() ? docEntry.getOwnerGroup() : assignmentDao.loadUserGroupWhereUser(course, docEntry.getOwner());
				File tutorialfolder = new File(this.getDocumentsFolder(course.getId(), writingActivity.getId(), writingActivity.getDeadlines().get(writingActivity.getDeadlines().size() - 1).getId(), studentGroup.getTutorial(), organization));
				String filename = FileUtil.escapeFilename((writingActivity.getGroups() ? "Group " + docEntry.getOwnerGroup().getName() : docEntry.getOwner().getLastname() + ", " + docEntry.getOwner().getFirstname() + " (" + docEntry.getOwner().getUsername() + ")" + " - Final.pdf"));
				String filePathZip = tutorialfolder.getAbsolutePath() + "/" + filename;
				FileUtil.copyFile(filePath, filePathZip);
				FileUtil.zipFolder(tutorialfolder, new File(tutorialfolder.getAbsolutePath() + ".zip"));
				docEntry.setDownloaded(true);
			}
			return docEntry;
		}
	}

	/**
	 * Update the permissions of documents belong to the activity, update the activity in the database, assign documents to the student and
	 * if it is necessary create new documents in Google
	 * @param course
	 * @param writingActivity
	 * @throws MessageException
	 */
	private WritingActivity updateActivityDocuments(Course course, WritingActivity writingActivity) throws MessageException {
		synchronized (writingActivity.getFolderId().intern()) {
			Organization organization = course.getOrganization();
			String domainName = organization.getGoogleDomain();
			// remove documents for deleted users
			// NOTE documents will not be removed after a review has started
			if (writingActivity.getStatus() < Activity.STATUS_FINISH) {
				List<User> students = new ArrayList<User>();
				for (UserGroup studentGroup : course.getStudentGroups()) {
					for (User student : studentGroup.getUsers()) {
						students.add(student);
					}
				}
				Set<DocEntry> savedDocEntries = new HashSet<DocEntry>();
				for (Iterator<DocEntry> docEntries = writingActivity.getEntries().iterator(); docEntries.hasNext();) {
					DocEntry docEntry = docEntries.next();
					try{
						//update permission if any user has changed groups
						docEntry = updateDocument(docEntry);
						savedDocEntries.add(docEntry);
					} catch (Exception e) {
						logger.error("Failed to update document : " + docEntry.getTitle(), e);
					}
					if (writingActivity.getGroups() && !course.getStudentGroups().contains(docEntry.getOwnerGroup()) || !writingActivity.getGroups() && !students.contains(docEntry.getOwner())) {
						savedDocEntries.remove(docEntry);
						docEntries.remove();
						writingActivity = assignmentDao.save(writingActivity);
					}
				}
				writingActivity.setEntries(savedDocEntries);
			}			
				

			// assign documents for new users
			List<DocEntry> newDocEntries = new ArrayList<DocEntry>();
			for (UserGroup studentGroup : course.getStudentGroups()) {
				// check student tutorial
				if (writingActivity.getTutorial().equals(studentGroup.getTutorial()) || writingActivity.getTutorial().equals(WritingActivity.TUTORIAL_ALL)) {	
					if (writingActivity.getGroups()) {
						// check student group name and size
						if (!StringUtils.isEmpty(studentGroup.getName()) && studentGroup.getUsers().size() > 0) {
							// check if student group already has a document
							DocEntry docEntry = assignmentDao.loadDocEntryWhereOwnerGroup(writingActivity, studentGroup);
							if (docEntry == null) {
								if (writingActivity.getDocumentType().equals(WritingActivity.DOCUMENT_TYPE_LOGBOOK)) {
									LogpageDocEntry logbookEntry = new LogpageDocEntry();
									logbookEntry.setTitle("Entry 1");
									LogbookDocEntry logbookDocEntry = new LogbookDocEntry();
									logbookDocEntry.getPages().add(logbookEntry);
									docEntry = logbookDocEntry;
								} else {
									docEntry = new DocEntry();
								}
								docEntry.setTitle(writingActivity.getName() + " - Group " + studentGroup.getName());
								docEntry.setOwnerGroup(studentGroup);
								docEntry.setDomainName(domainName);
								newDocEntries.add(docEntry);
							}
						}
					} else {
						  for (User student : studentGroup.getUsers()) {
							DocEntry docEntry = assignmentDao.loadDocEntryWhereUser(writingActivity, student);
							if (docEntry == null) {
								if (writingActivity.getDocumentType().equals(WritingActivity.DOCUMENT_TYPE_LOGBOOK)) {
									LogpageDocEntry logbookEntry = new LogpageDocEntry();
									logbookEntry.setTitle("Entry 1");
									LogbookDocEntry logbookDocEntry = new LogbookDocEntry();
									logbookDocEntry.getPages().add(logbookEntry);
									docEntry = logbookDocEntry;
								} else {
									docEntry = new DocEntry();
								}
								docEntry.setTitle(writingActivity.getName() + " - " + student.getLastname() + ", " + student.getFirstname());
								docEntry.setOwner(student);
								docEntry.setDomainName(domainName);
								newDocEntries.add(docEntry);
							}
						  }
					}
				}
			}

			// create new documents
			for (DocEntry newDocEntry : newDocEntries) {
				try {
					assignmentRepository.createDocument(writingActivity, newDocEntry, course);
					newDocEntry = assignmentDao.save(newDocEntry);
					writingActivity.getEntries().add(newDocEntry);
					writingActivity = assignmentDao.save(writingActivity);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("Failed to create document: " + newDocEntry.getTitle(), e);
				}
			}
		}
		return writingActivity;
	}
	
	/**
	 * Update a review activity. Get the documents to be reviewed, get the users (lecturers, tutors or students) to review the documents and
	 * assign  the documents to them  
	 * @param course
	 * @param writingActivity
	 * @param reviewingActivity
	 * @param currentDeadline
	 * @throws Exception
	 */
	private ReviewingActivity updateActivityReviews(Course course, WritingActivity writingActivity, ReviewingActivity reviewingActivity, Deadline currentDeadline) throws Exception{
		synchronized (writingActivity.getFolderId().intern()) {
			// get documents to be reviewed
			List<DocEntry> docEntries = new ArrayList<DocEntry>();
			if (reviewingActivity.getStatus() < Activity.STATUS_START) {
				try {
					if (writingActivity.getExcludeEmptyDocsInReviews()){
						docEntries.addAll(selectNonEmptyEntries(writingActivity, currentDeadline));
					}else{
						docEntries.addAll(writingActivity.getEntries());
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("Error loading revisions.", e);					
				}	
			} else {
				for (ReviewEntry reviewEntry : reviewingActivity.getEntries()) {
					if(!reviewEntry.isDeleted() && reviewEntry.getDocEntry() != null) {
						docEntries.add(reviewEntry.getDocEntry());
					}
				}
			}
			// get students to review documents
			List<User> students = new ArrayList<User>();
			for (UserGroup studentGroup : course.getStudentGroups()) {
				// check student tutorial
				if (writingActivity.getTutorial().equals(studentGroup.getTutorial()) || writingActivity.getTutorial().equals(WritingActivity.TUTORIAL_ALL)) {
					for (User student : studentGroup.getUsers()) {
						students.add(student);
					}
				}
			}

			ReviewStratergy reviewStratergy = null;
			if (reviewingActivity.getAllocationStrategy().equals(ReviewingActivity.REVIEW_STRATEGY_RANDOM)) {
				reviewStratergy = new RandomReviewStratergy(reviewingActivity, docEntries, students);
			} else if (reviewingActivity.getAllocationStrategy().equals(ReviewingActivity.REVIEW_STRATEGY_SPREADSHEET)) {
				reviewStratergy = new SpreadsheetReviewStratergy(course, writingActivity, assignmentDao, assignmentRepository);
			}

			// assign student reviewers
			Map<DocEntry, Set<User>> reviewSetup = null;
			if (reviewStratergy != null) {
				reviewSetup = reviewStratergy.allocateReviews();
			} else {
				reviewSetup = new HashMap<DocEntry, Set<User>>();
				for (DocEntry docEntry : docEntries) {
					reviewSetup.put(docEntry, new HashSet<User>());
				}
			}

			if (reviewingActivity.getStatus() < Activity.STATUS_START) {
				// assign lecturer reviewers
				if (course.getLecturers().size() > 0) {
					Iterator<User> lecturers = course.getLecturers().iterator();
					for (DocEntry docEntry : reviewSetup.keySet()) {
						for (int i = 0; i < reviewingActivity.getNumLecturerReviewers(); i++) {
							if (!lecturers.hasNext()) {
								lecturers = course.getLecturers().iterator();
							}
							if (lecturers.hasNext()){
								User lecturer = lecturers.next();
								// lecturer can not review his/her activities
								if (lecturer != null && docEntry != null & docEntry.getOwner() != null && !lecturer.equals(docEntry.getOwner())){
									reviewSetup.get(docEntry).add(lecturer);
								} 
							}
							
						}
					}
				}

				// assign tutor reviewers
				if (course.getTutors().size() > 0) {
					Iterator<User> tutors = course.getTutors().iterator();
					for (DocEntry docEntry : reviewSetup.keySet()) {
						for (int i = 0; i < reviewingActivity.getNumTutorReviewers(); i++) {
							if (!tutors.hasNext()) {
								tutors = course.getTutors().iterator();
							}
							if (tutors.hasNext()){
								User tutor = tutors.next();
								// tutor can not review his/her activities
								if (tutor != null && docEntry != null & docEntry.getOwner() != null && !tutor.equals(docEntry.getOwner())){
									reviewSetup.get(docEntry).add(tutor);
								} 
							}
						}
					}
				}
				
				// assign Automatic reviewers
				if (course.getAutomaticReviewers().size() > 0) {
					Iterator<User> autoReviewers= course.getAutomaticReviewers().iterator();
					for (DocEntry docEntry : reviewSetup.keySet()) {
						for (int i = 0; i < reviewingActivity.getNumAutomaticReviewers(); i++) {
							if (!autoReviewers.hasNext()) {
								autoReviewers = course.getAutomaticReviewers().iterator();
							}
							User autoReviewUser = autoReviewers.next();
							if (autoReviewUser != null && docEntry != null & docEntry.getOwner() != null && !autoReviewUser.equals(docEntry.getOwner())){
								reviewSetup.get(docEntry).add(autoReviewUser);
							}
						}
					}
				}				
				
			}

			// create user reviews
			for (DocEntry docEntry : reviewSetup.keySet()) {
				docEntry = assignmentDao.save(docEntry);
				for (User user : reviewSetup.get(docEntry)) {
//					boolean existsReview = false;
//					// check that user has not already been assigned to review this document for the same deadline
//					for(ReviewEntry reviewEntry:assignmentDao.loadReviewEntryWhereDocEntryAndOwner(docEntry, user)){
//						DocEntry docEntryReviewer = reviewEntry.getDocEntry();
//						// if the reviewEntry has a docEntry for the same document and it's in the same
//						// folder, it means that the deadline is the same so not create a new review for this user
//						if (docEntryReviewer != null && docEntryReviewer.getDocumentId() != null &&  
//							docEntryReviewer.getDocumentId().equals(docEntry.getDocumentId()) &&
//							docEntryIsEmpty(course, writingActivity,docEntryReviewer)){
//								existsReview = true;
//								break;
//						}
//					}
//					if (!existsReview){
						Review review;
						if (reviewingActivity.getFormType().equals(ReviewingActivity.REVIEW_TYPE_QUESTION)) {
							QuestionReview questionReview = new QuestionReview();
							Question question1 = new Question();
							question1.setOwner(user);
							question1.setDocId(docEntry.getDocumentId());
							Question question2 = new Question();
							question2.setOwner(user);
							question2.setDocId(docEntry.getDocumentId());
							Question question3 = new Question();
							question3.setOwner(user);
							question3.setDocId(docEntry.getDocumentId());
							Question question4 = new Question();
							question4.setOwner(user);
							question4.setDocId(docEntry.getDocumentId());
							Question question5 = new Question();
							question5.setOwner(user);
							question5.setDocId(docEntry.getDocumentId());
							questionReview.getQuestions().add(question1);
							questionReview.getQuestions().add(question2);
							questionReview.getQuestions().add(question3);
							questionReview.getQuestions().add(question4);
							questionReview.getQuestions().add(question5);
							review = questionReview;
						} else {
							
							if (reviewingActivity.getFormType().equals(ReviewingActivity.REVIEW_TYPE_TEMPLATE)) {
								ReviewReply reviewReply = new ReviewReply();
								ReviewTemplate templateToReply = assignmentDao.loadReviewTemplate(reviewingActivity.getReviewTemplateId());
								
								for (Section section : templateToReply.getSections()){
									TemplateReply answer = new TemplateReply();
									answer.setReviewTemplate(templateToReply);
									answer.setSection(section);	
									answer.setText("");
									
									if (!section.getTool().equalsIgnoreCase("none")){
										int siteId = Integer.valueOf(String.valueOf(writingActivity.getGlosserSite()));
										String docId = docEntry.getDocumentId();
										String toolType = section.getTool();
										String feedBack = "";
										try {
											feedBack = feedBackService.getAutomatedFeedback(siteId,docId,toolType);
										} catch (Exception e) {
											logger.error("Error getting automatic feedback.", e +"Document id: "+docId+" Tooltype: "+toolType);
										}
										answer.setText(feedBack);
									}
									
									reviewReply.getTemplateReplies().add(answer);
								}
								
								review = reviewReply;
							}else{
								review = new Review();
							}
						}
						review.setFeedbackTemplateType(reviewingActivity.getFeedbackTemplateType());
						review = assignmentDao.save(review);
						
						// Tracking monitor for study
						if (writingActivity.getTrackReviews()){
							FeedbackTracking feedbackTracking = new FeedbackTracking();
							feedbackTracking.setFeedbackId(review.getId());
							feedBackTrackingService.save(feedbackTracking);						
						}
						
						ReviewEntry reviewEntry = new ReviewEntry();
						reviewEntry.setDocEntry(docEntry);
						reviewEntry.setOwner(user);
						reviewEntry.setTitle(user.getLastname() + "," + user.getFirstname());
						reviewEntry.setReview(review);
						reviewEntry.setDeleted(false);
						reviewEntry = assignmentDao.save(reviewEntry);
						reviewingActivity.getEntries().add(reviewEntry);
						reviewingActivity = assignmentDao.save(reviewingActivity);
//					}
				}
			}
		}
		return reviewingActivity;
	}
	
	private String getDocumentsFolder( Course course, long activityId, long activityDeadlineId, String tutorial)  {
		//
		Organization organization = course.getOrganization();
		String documentsHome = Reviewer.getOrganizationsHome() + organization.getName()+ Reviewer.getDocumentsHome(); 
		return String.format(documentsHome + "/%s/%s/%s/%s", course.getId(), activityId, activityDeadlineId, tutorial);
	}
	
	private boolean docEntryIsEmpty(Course course, WritingActivity writingActivity, DocEntry docEntry) {	
		try{
			String folder = null;
			if (assignmentRepository != null && writingActivity.getCurrentDeadline()!= null){
				folder = getDocumentsFolder(course,writingActivity.getId(),writingActivity.getCurrentDeadline().getId(),WritingActivity.TUTORIAL_ALL);
			}
			if (folder != null){
				File file = new File(folder + "/" + FileUtil.escapeFilename(docEntry.getDocumentId()) + ".pdf");
				Organization organization = course.getOrganization();
				File empty = new File(Reviewer.getOrganizationsHome() + organization.getName()+ Reviewer.getDocumentsHome() + Reviewer.getEmptyDocument());
				if (empty.length() == file.length()){return true;}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error reading empty document.", e);					
		}	

		return false;
	}

	private Set<DocEntry> selectNonEmptyEntries(WritingActivity writingActivity, Deadline deadline) throws Exception {
		Set<DocEntry> entries = new HashSet<DocEntry>();
		
		for (DocEntry entry : writingActivity.getEntries()) {
			Course course = courseDao.loadCourse(assignmentDao.loadCourseWhereWritingActivity(writingActivity).getId());
			Organization organization = course.getOrganization();
			String filename = FileUtil.escapeFilename(entry.getDocumentId()) + ".pdf";
			Long deadlineId;
			if (deadline != null){
				deadlineId = deadline.getId();
			} else {
				if (writingActivity.getCurrentDeadline() != null){
					deadlineId = writingActivity.getCurrentDeadline().getId();
				} else {
					deadlineId = writingActivity.getFinalDeadline().getId();
				}
			}
			File file = new File(getDocumentsFolder(course.getId(), writingActivity.getId(),deadlineId , WritingActivity.TUTORIAL_ALL, organization) + "/" + filename);
			try{
				File empty = new File(Reviewer.getOrganizationsHome() + organization.getName()+ Reviewer.getDocumentsHome() + Reviewer.getEmptyDocument());
				if (empty.length() != file.length()){
					entries.add(entry);	
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error reading empty document.", e);					
			}										
		}
		return entries;
	}
	
	
	public DocEntry  updateDocument(DocEntry docEntry) throws Exception {
		synchronized (docEntry.getDocumentId().intern()) {
			try {
				docEntry = assignmentRepository.updateDocument(docEntry);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error updating document permission.", e);
				throw e;
			}
			docEntry = assignmentDao.save(docEntry);
		}

		// resubmit document if activity has finished
		if (docEntry.getDownloaded() && docEntry.getLocked() && !(docEntry instanceof LogbookDocEntry)) {
			try {
				docEntry = submitDocument(docEntry);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error submitting document.", e);
			}
		}
		return docEntry;
	}

	public ReviewTemplate saveReviewTemplate(ReviewTemplate reviewTemplate, User loggedUser) throws Exception {

		for (Section section : reviewTemplate.getSections()) {
			if (section.getType() != Section.OPEN_QUESTION) {
				for (Choice choice : section.getChoices()) {
					choice = assignmentDao.save(choice);
				}
			}
			section = assignmentDao.save(section);
		}
		reviewTemplate.setOwner(loggedUser);
		reviewTemplate = assignmentDao.save(reviewTemplate);
		if (reviewTemplate != null){
			reviewTemplate = reviewTemplate.clone();
		}
		return reviewTemplate;		
	}
	
	/**
	 * This method set the review templates as deleted. It doesn't revove it from the database or Google.
	 * The review template will be deleted if it's not being used
	 * @param reviewTemplate review template to set as deleted
	 * @throws Exception
	 */
	public void deleteReviewTemplate(ReviewTemplate reviewTemplate) throws Exception {
		// Load the review template with all its relationships
		reviewTemplate = loadReviewTemplateRelationships(reviewTemplate,reviewTemplate.getOrganization());
		if (assignmentDao.isReviewTemplateInUse(reviewTemplate))
		{
			throw new MessageException(Constants.EXCEPTION_DELETE_REVIEW_TEMPLATE_IN_USE);
		} else {
			reviewTemplate.setDeleted(true);
			reviewTemplate = assignmentDao.save(reviewTemplate);
		}
	}
	
	public String updateReviewDocEntry(String reviewEntryId, String newDocEntry) throws Exception {
		ReviewEntry reviewEntry =  assignmentDao.loadReviewEntry(Long.valueOf(reviewEntryId));
		if (reviewEntry == null){
			throw new MessageException(Constants.EXCEPTION_REVIEW_ENTRY_NOT_FOUND);
		}
		DocEntry docEntry = assignmentDao.loadDocEntryWhereId(Long.valueOf(newDocEntry));
		
		if ( (docEntry.getOwner()!=null && reviewEntry.getOwner() == docEntry.getOwner()) 
				|| (docEntry.getOwner()==null && docEntry.getOwnerGroup().getUsers().contains(reviewEntry.getOwner()) )) {
			throw new MessageException(Constants.EXCEPTION_REVIEWER_NOT_DOCUMENT_OWNER);
		}else{ 
			reviewEntry.setDocEntry(docEntry);
			reviewEntry = assignmentDao.save(reviewEntry);			
		}
		return docEntry.getTitle();
	}

	public void deleteReviewEntry(String reviewEntryId) throws Exception {
			ReviewEntry reviewEntry = assignmentDao.loadReviewEntry(Long.valueOf(reviewEntryId));
			if (reviewEntry == null){
				throw new MessageException(Constants.EXCEPTION_REVIEW_ENTRY_NOT_FOUND);
			}
			reviewEntry.setDeleted(true);
			reviewEntry = assignmentDao.save(reviewEntry);	
	}
	
	public ReviewEntry saveNewReviewEntry(String reviewingActivityId, String userId, String docEntryId, Organization organization) throws Exception{
		ReviewingActivity reviewingActivity = assignmentDao.loadReviewingActivity(Long.valueOf(reviewingActivityId));
		DocEntry docEntry = assignmentDao.loadDocEntryWhereId(Long.valueOf(docEntryId));
		// userId is the username
		User user = userDao.getUserByUsername(userId, organization);
		
		if (assignmentDao.loadReviewEntryWhereDocEntryAndOwner(docEntry, user, reviewingActivity) == null){
			ReviewEntry reviewEntry = new ReviewEntry();
			Review review = new Review();		
			review = assignmentDao.save(review);
			reviewEntry.setReview(review);
			
			reviewEntry.setDocEntry(docEntry);
			reviewEntry.setOwner(user);
			reviewEntry.setTitle(user.getLastname()+","+user.getFirstname());
			reviewEntry.setDeleted(false);
			reviewEntry = assignmentDao.save(reviewEntry);
			
			reviewingActivity.getEntries().add(reviewEntry);
			reviewingActivity = assignmentDao.save(reviewingActivity);
			
			return reviewEntry;			
		}else{
			throw new MessageException(Constants.EXCEPTION_REVIEW_ALREADY_ASSIGNED);
		}
	}

	/**
	 * Create or update the lecturers in the database, in Google Docs and in the course
	 * This method is only used in Rest API
	 * @param course Course where the user will be lecturer
	 * @param lecturer list of users
	 * @param loggedUser logged user used in Google Apps
	 * @throws Exception
	 */
	public void saveLecturers(Course course, List<User> lecturers, User loggedUser) throws Exception {
	}
	
	
	/**
	 * Create or update a tutors in the database, in Google Apps, assign permissions to the documents in GoogleDocs and add him/her to the course
	 * This method is only used in Rest API
	 * @param course course where the user will be tutor
	 * @param tutor List of users representing the tutors
	 * @param loggedUser loggedUser used to add permissions in GoogleDoc
	 * @throws Exception
	 */
	public void saveTutors(Course course, List<User> tutors, User loggedUser) throws Exception {
	}
		
	/**
	 * Get a writing activity
	 * @param writingActivityId
	 * @return
	 * @throws MessageException
	 */
	public WritingActivity loadWritingActivity(long writingActivityId)throws MessageException {
		return assignmentDao.loadWritingActivity(writingActivityId);
	}
	

	/**
	 * Return a doc entry with id equals to the id received as parameter
	 * @param id Long id of the doc entry to look for
	 * @return DocEntry with id equals to the id received as parameter
	 * @throws Exception 
	 */
	public DocEntry loadDocEntry(Long id) throws Exception{
		return assignmentDao.loadDocEntryWhereId(id);
	}
	
	/**
	 * Return the writing activity with all its relationships
	 * This method is only used in Rest API
	 * @param activity writing activity with relationships object only with theirs ids
	 * @return WritingActivity writing activity with its relationships
	 * @throws MessageException
	 */
	public WritingActivity loadWritingActivityRelationships(WritingActivity activity)throws MessageException {
		MessageException me = null;
		try{
			// load deadlines
			List<Deadline> deadlines = new ArrayList<Deadline>();
			for(Deadline deadline: activity.getDeadlines()){
				if (deadline != null && deadline.getId() != null){
					deadline = assignmentDao.loadDeadline(deadline.getId());
				}
				deadlines.add(deadline);
			}
			activity.setDeadlines(deadlines);
			
			// load entries
			Set<DocEntry> entries = new HashSet<DocEntry>();
			for(DocEntry entry:activity.getEntries()){
				if (entry != null && entry.getId() != null){
					entry = loadDocEntry(entry.getId());
				}
				entries.add(entry);
			}
			activity.setEntries(entries);
			
			// load grades
			Set<Grade> grades = new HashSet<Grade>();
			for(Grade grade:activity.getGrades()){
				if (grade != null && grade.getId() != null){
					grade = assignmentDao.loadGrade(grade.getId());
				}
				grades.add(grade);
			}
			activity.setGrades(grades);
			
			// load reviewing activities
			List<ReviewingActivity> reviewingActivities = new ArrayList<ReviewingActivity>();
			for(ReviewingActivity reviewingActivity :activity.getReviewingActivities()){
				if (reviewingActivity != null && reviewingActivity.getId() != null){
					reviewingActivity = assignmentDao.loadReviewingActivity(reviewingActivity.getId());
				}
				reviewingActivities.add(reviewingActivity);
			}
			activity.setReviewingActivities(reviewingActivities);
			
			return activity.clone();
		} catch(Exception e){
			e.printStackTrace();
			if (e instanceof MessageException){
				me = (MessageException) e;
			} else {
				me = new MessageException(Constants.EXCEPTION_SAVE_WRITING_ACTIVITIES);
			}
			if ( me.getStatusCode() == 0){
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
			}
			throw me;
		}
	}
	
	public UserGroup loadUserGroup(Long id) throws  MessageException {
		return assignmentDao.loadUserGroup(id);
	}
	
	public ReviewTemplate loadReviewTemplate(Long id) throws MessageException {
		return assignmentDao.loadReviewTemplate(id);
	}
	
	public ReviewingActivity loadReviewingActivity(Long id) throws MessageException {
		return assignmentDao.loadReviewingActivity(id);
	}
	
	public List<ReviewTemplate> loadReviewTemplates(Organization organization, Integer page, Integer limit) throws MessageException{
		return assignmentDao.loadReviewTemplates(organization, page, limit);
	}
	
	/**
	 * Return a review template with all its relationships
	 * @param ReviewTemplate review template without relationships (the objects have only the id)
	 * @param organization organization of the logged user
	 * @return ReviewTemplate review template with all its relationships 
	 * @throws MessageException message to the logged user
	 */
	public ReviewTemplate loadReviewTemplateRelationships(ReviewTemplate reviewTemplate, Organization organization) throws MessageException{
		MessageException me = null;
		try{
			// Set organization
			if (reviewTemplate.getOrganization() == null){
				reviewTemplate.setOrganization(organization);
			} else {
				Organization anOrganization = organizationManager.getOrganization(reviewTemplate.getOrganization().getId());
				if (anOrganization != null){
					reviewTemplate.setOrganization(anOrganization);
				} else {
					me = new MessageException(Constants.EXCEPTION_ORGANIZATION_NOT_FOUND);
					me.setStatusCode(Constants.HTTP_CODE_NOT_FOUND);
					throw me;
				}
			}
			
			// load sections
			List<Section> sections = new ArrayList<Section>();
			for(Section section :reviewTemplate.getSections()){
				if (section != null && section.getId() != null){
					section = assignmentDao.loadSection(section.getId());
				}
				sections.add(section);
			}
			
			reviewTemplate.setSections(sections);
			return reviewTemplate;
		} catch(Exception e){
			e.printStackTrace();
			if (e instanceof MessageException){
				me = (MessageException) e;
			} else {
				me = new MessageException(Constants.EXCEPTION_SAVE_COURSE);
			}
			if ( me.getStatusCode() == 0){
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
			}
			throw me;
		}
	}
	
	/**
	 * Save students in a student group and add it to the course
	 * @param course course where the users will be students
	 * @param students students to add to the course
	 * @param group number of the group of students
	 * @param tutorial it must be equals to the course tutorial
	 * @throws Exception
	 */
	public void saveStudentsGroup(Course course, Set<User> students, String group, String tutorial) throws Exception {
		
	}
	
	public Course loadCourseWhereDeadline(Deadline deadline) throws MessageException{
		return assignmentDao.loadCourseWhereDeadline(deadline);
	}

	public Grade loadGrade(Deadline deadline, User user) throws MessageException{
		return assignmentDao.loadGrade(deadline, user);
	}
	
	public Grade saveGrade(Grade grade) throws Exception {
		return grade = assignmentDao.save(grade);
	}
	
	public  WritingActivity loadWritingActivityWhereDeadline(Deadline deadline) throws Exception {
		return assignmentDao.loadWritingActivityWhereDeadline(deadline);
	}
	
	public WritingActivity saveWritingActivity(WritingActivity activity) throws Exception{
		return activity = assignmentDao.save(activity);
	}
	
	/**
	 * This method generates the emails for the course and adds them to it
	 * @param course course owner of the emails
	 * @return course with emails
	 */
	private Course addEmails(Course course) throws MessageException{
		
		try{
			Organization org = course.getOrganization();
			if ( org != null && course != null){
				if (course.getEmail(Constants.EMAIL_LECTURER_DEADLINE_FINISH) == null){
					createEmail(org.getEmail(Constants.EMAIL_LECTURER_DEADLINE_FINISH),course);
				}
				if (course.getEmail(Constants.EMAIL_PASSWORD_DETAILS) == null){
					createEmail(org.getEmail(Constants.EMAIL_PASSWORD_DETAILS), course);
				}
				if (course.getEmail(Constants.EMAIL_STUDENT_ACTIVITY_START) == null){
					createEmail(org.getEmail(Constants.EMAIL_STUDENT_ACTIVITY_START),course);
				}
				if (course.getEmail(Constants.EMAIL_STUDENT_RECEIVED_REVIEW) == null){
					createEmail(org.getEmail(Constants.EMAIL_STUDENT_RECEIVED_REVIEW),course);
				}
				if (course.getEmail(Constants.EMAIL_STUDENT_REVIEW_FINISH) == null){
					createEmail(org.getEmail(Constants.EMAIL_STUDENT_REVIEW_FINISH),course);
				}
				if (course.getEmail(Constants.EMAIL_STUDENT_REVIEW_START) == null){
					createEmail(org.getEmail(Constants.EMAIL_STUDENT_REVIEW_START),course);
				}
				if (course.getEmail(Constants.EMAIL_ACTIVITY_NOTIFICATIONS_SENT) == null){
					createEmail(org.getEmail(Constants.EMAIL_ACTIVITY_NOTIFICATIONS_SENT),course);
				}
				if (course.getEmail(Constants.EMAIL_REVIEWING_ACTIVITY_NOTIFICATIONS_SENT) == null){
					createEmail(org.getEmail(Constants.EMAIL_REVIEWING_ACTIVITY_NOTIFICATIONS_SENT),course);
				}
				if (course.getEmail(Constants.EMAIL_SAVE_COURSE_FINISHED) == null){
					createEmail(org.getEmail(Constants.EMAIL_SAVE_COURSE_FINISHED),course);
				}
				if (course.getEmail(Constants.EMAIL_ADMIN_ACTIVITY_DEADLINE_FINISHED) == null){
					createEmail(org.getEmail(Constants.EMAIL_ADMIN_ACTIVITY_DEADLINE_FINISHED),course);
				}
				if (course.getEmail(Constants.EMAIL_ADMIN_REVIEWING_ACTIVITY_FINISHED) == null){
					createEmail(org.getEmail(Constants.EMAIL_ADMIN_REVIEWING_ACTIVITY_FINISHED),course);
				}
				if (course.getEmail(Constants.EMAIL_ADMIN_ACTIVITY_STARTED) == null){
					createEmail(org.getEmail(Constants.EMAIL_ADMIN_ACTIVITY_STARTED),course);
				}
				if (course.getEmail(Constants.EMAIL_ADMIN_ACTIVITY_DEADLINE_STARTED) == null){
					createEmail(org.getEmail(Constants.EMAIL_ADMIN_ACTIVITY_DEADLINE_STARTED),course);
				}
				if (course.getEmail(Constants.EMAIL_ADMIN_REVIEWING_ACTIVITY_DEADLINE_STARTED) == null){
					createEmail(org.getEmail(Constants.EMAIL_ADMIN_REVIEWING_ACTIVITY_DEADLINE_STARTED),course);
				}
			}
				
		} catch(Exception e){
			e.printStackTrace();
			if (e instanceof MessageException){
				throw (MessageException) e;
			} else {
				e.printStackTrace();
				throw new MessageException(Constants.EXCEPTION_GENERATE_COURSE_EMAILS);
			}
		}		
		return course;
	}
	
	private void createEmail(EmailOrganization emailOrganization, Course course) throws Exception{
		EmailCourse emailCourse = new EmailCourse();
		emailCourse.setName(emailOrganization.getName());
		emailCourse.setMessage(emailOrganization.getMessage());
		emailCourse = emailDao.saveEmailCourse(emailCourse, course);
		course.addEmail(emailCourse);
	}
	
	private Course updateEmails(Course course) throws MessageException{
		Set<EmailCourse> emails = new HashSet<EmailCourse>();
		for(EmailCourse email: course.getEmails()){
			if (email.getCourse() == null || (email.getCourse() != null && email.getCourse().getId() == null)){
				email.setCourse(course);
				email = emailDao.saveEmailCourse(email, course);
			}
			emails.add(email);
		}
		course.setEmails(emails);
		return course;
		
	}
	
	
	public List<ReviewTemplate> loadReviewTemplates(Organization organization, User loggedUser) throws MessageException{
		return assignmentDao.loadReviewTemplates(organization, loggedUser);
	}
	
	public Course loadCourseWhereWritingActivity(WritingActivity writingActivity) throws MessageException {
		return assignmentDao.loadCourseWhereWritingActivity(writingActivity);
	}
	
	public ReviewTemplate shareReviewTemplateWith(ReviewTemplate reviewTemplate, String email) throws MessageException{
		
		// get email domain
		String domain = null;
		if (email !=  null){
			int i = email.indexOf("@");
			domain = email.substring(i+1,email.length());
			if (domain != null){
				domain = domain.toLowerCase();
			}
		}
		if (reviewTemplate.getOrganization()!= null && domain!= null && reviewTemplate.getOrganization().domainBelongsToEmailsDomain(domain)){
			User userToShare = userDao.getUserByEmail(email);
			if (userToShare == null){
				throw new MessageException(Constants.EXCEPTION_USER_NOT_FOUND);
			}
			reviewTemplate.sharedWith(userToShare);
			reviewTemplate = assignmentDao.save(reviewTemplate);
		} else {
			throw new MessageException(Constants.EXCEPTION_WRONG_ORGANIZATION_DOMAIN);
		}
		return reviewTemplate;
		
	}
	
	public ReviewTemplate noShareReviewTemplateWith(ReviewTemplate reviewTemplate, String email) throws MessageException{
		User userToShare = userDao.getUserByEmail(email);
		if (userToShare == null){
			throw new MessageException(Constants.EXCEPTION_USER_NOT_FOUND);
		}
		if (!assignmentDao.reviewTemplateInUse(reviewTemplate, userToShare)){
			reviewTemplate.noShareWith(userToShare);
			reviewTemplate = assignmentDao.save(reviewTemplate);
		} else {
			throw new MessageException(Constants.EXCEPTION_REVIEW_TEMPLATE_USED_BY_USER);
		}
		return reviewTemplate;
	}

	public void startReviewingActivity(Course course, WritingActivity writingActivity, ReviewingActivity reviewingActivity, Deadline deadline) throws Exception{
		// check reviewactivity status
		if (reviewingActivity.getStatus() >= Activity.STATUS_START) {
			return;
		}
		
		reviewingActivity.setSaving(true);
		reviewingActivity = assignmentDao.save(reviewingActivity);
		
		if (deadline.equals(reviewingActivity.getStartDate())) {
			reviewingActivity = updateActivityReviews(course, writingActivity, reviewingActivity, deadline);
			reviewingActivity.setStatus(Activity.STATUS_START);
			reviewingActivity = assignmentDao.save(reviewingActivity);
		}
		
		// send review start notification to students
		if (writingActivity.getEmailStudents()) {
			if (deadline.equals(reviewingActivity.getStartDate()) && (reviewingActivity.getNumStudentReviewers() > 0)) {				
				final WritingActivity wActivity = writingActivity.clone();
				final Course fCourse = course.clone();
				final Long activityId = writingActivity.getId();
				final Deadline fDeadline = deadline.clone();
				final ReviewingActivity rActivity = reviewingActivity.clone();
				Timer timer = studentsTimers.get(activityId);
				if (timer == null){
					timer = new Timer();
				}
				try{
					// create task to send email notifications 
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							sendReviwingActivityStartNotificationToStudents(fCourse, wActivity, rActivity, fDeadline);	
						}
					}, deadline.getFinishDate());
				} catch(IllegalStateException ise) {
					ise.printStackTrace();
					timer = new Timer();
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							sendReviwingActivityStartNotificationToStudents(fCourse, wActivity, rActivity, fDeadline);	
						}
					}, deadline.getFinishDate());
				}
				studentsTimers.put(activityId, timer);
			}
		}
		reviewingActivity.setSaving(false);
		reviewingActivity = assignmentDao.save(reviewingActivity);
		
		if (writingActivity.getEmailStudents()){
			List<User> admins = organizationManager.getAdminUsers(course.getOrganization());
			// send notification to admin to inform that the start activity process has finished
			for(User admin: admins){
				try{
					emailNotifier.sendNotificationToAdmin(course,writingActivity, reviewingActivity,null,admin, Constants.EMAIL_ADMIN_REVIEWING_ACTIVITY_DEADLINE_STARTED);
				} catch(Exception e){
					e.printStackTrace();
					String message = "Failed to send notification of activity saved.";
					if ( admin != null ){
						message +="Admin: " + admin.getEmail();
					}
					logger.error(message,e);
				}
			}
		}
	}

	/**
	 * This method returns a boolean indicating if the user received as parameter is lecturer of the course received as parameter
	 * @param course course to verify the lectures
	 * @param user user check if is a lecturer or not. The user is a lecturer if he/she has the same id or email of one of the lecturers of the course
	 * @return true if user is lecture of the course otherwise false
	 */
	private boolean isCourseLecturer(Course course, User user){
		boolean result = false;
		if (user != null && !course.getLecturers().isEmpty()){
			for(User lecturer: course.getLecturers()){
				result |= lecturer.getId() != null && lecturer.getId().equals(user.getId());
				result |= lecturer.getEmail()!= null && lecturer.getEmail().equalsIgnoreCase(user.getEmail());
			}
		} 
		return result;
	}

	private void validateStudents(Course course) throws MessageException {		
		for( UserGroup studentGroup: course.getStudentGroups()){
			for(User student : studentGroup.getUsers()){
				if (student.getDomain() != null && student.getOrganization() != null && 
						!student.getOrganization().domainBelongsToEmailsDomain(student.getDomain())){
					throw new MessageException(Constants.EXCEPTION_STUDENTS_INVALID_DOMAIN + "\nWrong domain: " + student.getDomain());
				}
				
				if (student.getEmail() == null){
					throw new MessageException(Constants.EXCEPTION_STUDENT_EMAIL_EMPTY);
				}
			}
		}
	}
	
	private boolean someReviewingActivityIsSaving(WritingActivity writingActivity ){
		boolean saving = false;
		try{
			for(ReviewingActivity reviewingActivity: writingActivity.getReviewingActivities()){
				if (reviewingActivity.isSaving()){
					saving = true;
					break;
				}
			}
		} catch(Exception e){
			
		}
		return saving;
	}
	
	private boolean someWritingActivityIsSaving(Course course ){
		boolean saving = false;
		try{
			for(WritingActivity writingActivity: course.getWritingActivities()){
				if (writingActivity.isSaving() || someReviewingActivityIsSaving(writingActivity)){
					saving = true;
					break;
				}
			}
		} catch(Exception e){
			
		}
		return saving;
	}
	
}