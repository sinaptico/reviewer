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

import com.google.gdata.data.docs.DocumentListEntry;

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
	
	public AssignmentManager() {
	}

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
	public void finishActivityDeadline(Course course, WritingActivity writingActivity, Deadline deadline) throws Exception{
		// check activity status
		if (deadline.getStatus() >= Deadline.STATUS_DEADLINE_FINISH) {
//			logger.info("Deadline has already finished.");
			return;
		}

		if(writingActivity.getDeadlines().get(writingActivity.getDeadlines().size()-1).equals(deadline)) {
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
		for (ReviewingActivity reviewingActivity : writingActivity.getReviewingActivities()) {
			if (deadline.equals(reviewingActivity.getStartDate())) {
				updateActivityReviews(course, writingActivity, reviewingActivity);
				reviewingActivity.setStatus(Activity.STATUS_START);
				reviewingActivity = assignmentDao.save(reviewingActivity);
			}
		}

		// update activity deadline status
		deadline.setStatus(Deadline.STATUS_DEADLINE_FINISH);
		deadline = assignmentDao.save(deadline);
		
		// schedule next activity deadline
		scheduleActivityDeadline(course, writingActivity);
		
		// send assessment finish notification to lecturers
		for (User lecturer : course.getLecturers()) {
			try {
				emailNotifier.sendLecturerDeadlineFinishNotification(lecturer, course, writingActivity, deadline.getName());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Failed to send assessment finish notification.", e);
			}
		}
		
		// send review start notification to students
		if (writingActivity.getEmailStudents()) {
			for (ReviewingActivity reviewingActivity : writingActivity.getReviewingActivities()) {
				if (deadline.equals(reviewingActivity.getStartDate()) && (reviewingActivity.getNumStudentReviewers() > 0)) {
					for (UserGroup studentGroup : course.getStudentGroups()) {
						if (writingActivity.getTutorial().equals(WritingActivity.TUTORIAL_ALL) || writingActivity.getTutorial().equals(studentGroup.getTutorial())) {
							for (User student : studentGroup.getUsers()) {
								try {
									emailNotifier.sendStudentReviewStartNotification(student, course, writingActivity, deadline);
								} catch (Exception e) {
									e.printStackTrace();
									logger.error("Failed to send review start notification.", e);
								}
							}
						}
					}
				}
			}
		}	
	}

	public void finishReviewingActivity(Course course, ReviewingActivity reviewingActivity, Deadline deadline) throws MessageException{
		// check activity status
		if (reviewingActivity.getStatus() >= Activity.STATUS_FINISH) {
//			logger.info("Review has already finished.");
			return;
		}

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
//		logger.info("Zip review files in: "+getDocumentsFolder(course.getId(), reviewingActivity.getId(), reviewingActivity.getStartDate().getId(), "", organization));
		String filePath = getDocumentsFolder(course.getId(), reviewingActivity.getId(), reviewingActivity.getStartDate().getId(), "", organization);
		File file = new File(filePath);
		
		if ( file.exists()) {
			for (File folder : file.listFiles()) {		                            
				if (folder != null && folder.isDirectory()) {
					FileUtil.zipFolder(folder, new File(folder.getAbsolutePath() + ".zip"));
				}
			}
		} else {
			
			MessageException me = new MessageException(Constants.EXCEPTION_ACTIVITY_NOT_FINISHED + " File " + filePath);
			me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
			throw me;
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

		// send review finish notification to lecturers
		for (User lecturer : course.getLecturers()) {
			try {
				emailNotifier.sendLecturerDeadlineFinishNotification(lecturer, course, reviewingActivity, reviewingActivity.getName());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Failed to send review finish notification.", e);
			}
		}

		//send review finish notifications to students	
		WritingActivity writingActivity = assignmentDao.loadWritingActivityWhereDeadline(deadline);
		if(writingActivity != null && writingActivity.getEmailStudents()){
			List<DocEntry> notifiedDocEntries = new ArrayList<DocEntry>();
			for (ReviewEntry reviewEntry : reviewingActivity.getEntries()) {
				if (!reviewEntry.isDeleted()){
					//Reviewed User
					User user = reviewEntry.getDocEntry().getOwner();
					if (!notifiedDocEntries.contains(reviewEntry.getDocEntry())){
						try {
							if (user != null){
								emailNotifier.sendReviewFinishNotification(user, course, writingActivity, deadline.getName());
								notifiedDocEntries.add(reviewEntry.getDocEntry());
							}else{ 
								//it's a document owned by a group
								Set<User> students = reviewEntry.getDocEntry().getOwnerGroup().getUsers();
								for (User userToNotify : students) {
									emailNotifier.sendReviewFinishNotification(userToNotify, course, writingActivity, deadline.getName());								
								}
								notifiedDocEntries.add(reviewEntry.getDocEntry());
							}
						} catch (Exception e) {
							e.printStackTrace();
							logger.error("Failed to send review finish notification.", e);
						}
					}
				}
			}
		}

		// read excel file and insert questions into DB
		if (reviewingActivity.getFormType() != null && reviewingActivity.getFormType().equals(ReviewingActivity.REVIEW_TYPE_QUESTION)) {
			String path = getDocumentsFolder(course.getId(), reviewingActivity.getId(), "aqg", "", organization);
			String filepath = path + Reviewer.getAggLoadExcelPath(); 
			QuestionUtil questionUtil = new QuestionUtil();
			try {
				questionUtil.readExcelInsertDB(filepath, course.getOrganization());
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Failed to read the excel.", e);
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

	public WritingActivity saveActivity(Course course, WritingActivity writingActivity) throws Exception {
		
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
	
	private void saveStudentUsers(Course course) throws Exception {
		Organization organization = course.getOrganization();
		for (UserGroup studentGroup : course.getStudentGroups()) {
			for (User student : studentGroup.getUsers()) {
				if (student.getOrganization() == null){
					student.setOrganization(organization);
				}
				
				if (student.getDomain() != null && student.getOrganization() != null && 
						!student.getOrganization().domainBelongsToEmailsDomain(student.getDomain())){
					throw new MessageException(Constants.EXCEPTION_STUDENTS_INVALID_DOMAIN);
				}
				student.addRole(Constants.ROLE_GUEST);
				
				if (student.getEmail() != null){
					//generate the username with the email
					student.getUsername();
				} else if (student.getFirstname() != null){
					// set the username with the firstname
					student.setUsername(student.getFirstname().toLowerCase());
				}
				// search student by email so it's no necessary get it by organization because the email is unique
				User user = userDao.getUserByEmail(student.getEmail());
				if (user == null) {
	
					if (!organization.isShibbolethEnabled()){
						// generate password to login in reviewer and send it by email
						  if (student.getPassword() == null){
							  student.setPassword(Long.toHexString(Double.doubleToLongBits(Math.random())));
						  }
						  emailNotifier.sendPasswordNotification(student, course);
						  student.setPassword(RealmBase.Digest(student.getPassword(), "MD5",null));
					} else {
						// student doesn't have a password to login in reviewer because uses shibboleth 
						student.setPassword(null);
					} 
					student = userDao.save(student);
				} else {
					student.setId(user.getId());
					student.setPassword(user.getPassword());
					if (student.getFirstname() == null){
						student.setFirstname(user.getFirstname());
					}
					if (student.getLastname() == null){
						student.setLastname(user.getLastname());
					}
				}
				if (!assignmentRepository.userExists(student.getGoogleAppsEmailUsername())){
					assignmentRepository.createUser(student,organization.getOrganizationPasswordNewUsers() + student.getUsername());
				}
			}
			studentGroup = assignmentDao.save(studentGroup);
		}	
	}
	
	private void saveLecturerUsers(Course course) throws Exception{
		Organization organization = course.getOrganization();
		for (User lecturer : course.getLecturers()) {
			if (lecturer.getOrganization() == null){
				lecturer.setOrganization(organization);
			}
			// if the domain of the lecturer's email belong to the organizations email domains ==> create the user in Google Apps.
			if (lecturer.getDomain() != null && lecturer.getOrganization() != null && 
			    !lecturer.getOrganization().domainBelongsToEmailsDomain(lecturer.getDomain())){	
				throw new MessageException(Constants.EXCEPTION_LECTURER_INVALID_DOMAIN);
			}
			
			// lecture doesn't exists in database
			if (lecturer.getEmail() != null){
				lecturer.getUsername();
			} else if (lecturer.getFirstname() != null){
				lecturer.setUsername(lecturer.getFirstname().toLowerCase());
			}
			
			// search lecture in database
			User user = userDao.getUserByEmail(lecturer.getEmail());
			if (user == null) {	
				if (!organization.isShibbolethEnabled()){
					// generate password to login in reviewer and send it by email
					if (lecturer.getPassword() == null){
						lecturer.setPassword(Long.toHexString(Double.doubleToLongBits(Math.random())));
					}
					emailNotifier.sendPasswordNotification(lecturer, course);
					lecturer.setPassword(RealmBase.Digest(lecturer.getPassword(), "MD5",null));
				} else {
					// lecture doesn't have a password to login in reviewer because uses shibboleth 
					lecturer.setPassword(null);
				} 
				lecturer.addRole(Constants.ROLE_STAFF);
			} else {
				lecturer.setId(user.getId());
				lecturer.setPassword(user.getPassword());
				lecturer.setRole_name(user.getRole_name());
				if (lecturer.getFirstname() == null){
					lecturer.setFirstname(user.getFirstname());
				}
				if (lecturer.getLastname() == null){
					lecturer.setLastname(user.getLastname());
				}
				if (!lecturer.isAdmin() && !lecturer.isStaff() && !lecturer.isSuperAdmin()){
					lecturer.addRole(Constants.ROLE_STAFF);
				}
			}
			lecturer = userDao.save(lecturer);
			// if the user doesn't exist in Google Apps then create it
			if (!assignmentRepository.userExists(lecturer.getGoogleAppsEmailUsername())){
				assignmentRepository.createUser(lecturer,organization.getOrganizationPasswordNewUsers()+lecturer.getUsername());
			}			
		}
	}
	private void saveTutorUsers(Course course) throws Exception {
		Organization organization = course.getOrganization();
		for (User tutor : course.getTutors()) {
			if (tutor.getOrganization() == null){
				tutor.setOrganization(organization);
			}
			
			// if the domain of the tutor's email belong to the organizations email domains ==> create the user in Google Apps.
			if (tutor.getDomain() != null && tutor.getOrganization() != null && 
					!tutor.getOrganization().domainBelongsToEmailsDomain(tutor.getDomain())){	
				throw new MessageException(Constants.EXCEPTION_TUTORS_INVALID_DOMAIN);
			}
			
			if (tutor.getEmail() != null){
				tutor.getUsername();
			} else if (tutor.getFirstname() != null){
				tutor.setUsername(tutor.getFirstname().toLowerCase());
			}

			// search tutor in database
			User user = userDao.getUserByEmail(tutor.getEmail());
			if (user == null) {
				
				// tutor doesn't exists in database
				if (!organization.isShibbolethEnabled()){
					// generate password to login in reviewer and send it by email
					if (tutor.getPassword() == null){
						tutor.setPassword(Long.toHexString(Double.doubleToLongBits(Math.random())));
					}
					emailNotifier.sendPasswordNotification(tutor, course);
					tutor.setPassword(RealmBase.Digest(tutor.getPassword(), "MD5",null));
				} else {
					// tutor doesn't have a password to login in reviewer because uses shibboleth 
					tutor.setPassword(null);
				} 
				
				tutor.addRole(Constants.ROLE_STAFF);
				
			} else {
				tutor.setId(user.getId());
				tutor.setPassword(user.getPassword());
				if (tutor.getFirstname() == null){
					tutor.setFirstname(user.getFirstname());
				}
				if (tutor.getLastname() == null){
					tutor.setLastname(user.getLastname());
				}
				if (!tutor.isAdmin() && !tutor.isStaff() && !tutor.isSuperAdmin()){
					tutor.addRole(Constants.ROLE_STAFF);
				}
			}
			tutor = userDao.save(tutor);
			// if the user doesn't exist in Google Apps then create it
			if (!assignmentRepository.userExists(tutor.getGoogleAppsEmailUsername())){
				assignmentRepository.createUser(tutor,organization.getOrganizationPasswordNewUsers()+tutor.getUsername());
			}			
		}

	}
	
	private void processActivitiesForNewUsers(Course course) throws Exception {
		
		// update activities and reviews
		for (WritingActivity writingActivity : course.getWritingActivities()) {
			// validate activity
			validateActivity(writingActivity);
			
			// create documents for new users
			if (writingActivity.getStatus() >= Activity.STATUS_START && writingActivity.getStatus() < Activity.STATUS_FINISH) {
				updateActivityDocuments(course, writingActivity);
			}
			// create reviews for new users
			for (ReviewingActivity reviewingActivity : writingActivity.getReviewingActivities()) {
				if (reviewingActivity.getStatus() == Activity.STATUS_START) {
					updateActivityReviews(course, writingActivity, reviewingActivity);
				}
			}
		}
	}
	
	private void validateActivity(WritingActivity writingActivity) throws MessageException{
		MessageException me = null;
		if (writingActivity.getId() != null && writingActivity.getStatus() == writingActivity.STATUS_FINISH){
			boolean allFinished = true;
			for (ReviewingActivity reviewingActivity : writingActivity.getReviewingActivities()){
				allFinished = allFinished && (reviewingActivity.getStatus() == reviewingActivity.STATUS_FINISH);
			}
			// if all the reviews of the activity finished then the activity can not be modified
			if (allFinished){
				me = new MessageException(Constants.EXCEPTION_ACTIVITY_FINISHED);
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
				throw me;
			}
		}
		
		int index = 0;
		// All the reviewing finish date must be greater than the corresponding writing activity deadline finish date
		for (ReviewingActivity reviewingActivity : writingActivity.getReviewingActivities()){
			if (reviewingActivity.getStartDate().getFinishDate() == null){
				me = new MessageException(Constants.EXCEPTION_NOT_ACTIVITY_FINISH_DATE);
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
	
	public Course saveCourse(Course course, User user) throws Exception {
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
		
		
		try{
			
			// Add emails to the course
			course = addEmails(course);
			
			//Set up folders and templates
			setUpFoldersAndTemplates(course);
			
			// save student users
			saveStudentUsers(course);
			
			// if the logged user is staff and course lecturers and tutors are empty then add the logged user as lecturer
			if (course.getLecturers().isEmpty() && course.getTutors().isEmpty() && user.isStaff()){
				course.getLecturers().add(user);
			}
			// save lecturer users
			saveLecturerUsers(course);
			
			// save tutor users
			saveTutorUsers(course);
			
			//if logged user is an admin and it's one of the lecturers or tutors then
			// remove if from lectures or tutors before update the course becuase they have permissions
			// if they are lecturer or tutors then Google will remove his/her permissions
			for(User lecturer : course.getLecturers()){
				if (lecturer.isAdmin() || lecturer.isSuperAdmin()){
					throw new MessageException(Constants.EXCEPTION_ADMIN_CAN_NO_BE_LECTURER);
				} 
			}
					
			for(User tutor : course.getTutors()){
				if (tutor.isAdmin() && tutor.isSuperAdmin()){
					throw new MessageException(Constants.EXCEPTION_ADMIN_CAN_NO_BE_TUTOR);
				}
			}
			
			
			// update course document permissions
			assignmentRepository.updateCourseDocumentPermissions(course, user);
			
			course.setDomainName(course.getOrganization().getGoogleDomain());
			
			// save course in DB
			course = courseDao.save(course);
	
			// update emails
			course = updateEmails(course);
			
			// for each activity create documents and reviewers for new users
			processActivitiesForNewUsers(course);
			
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
						logger.error("Error running acctivity " + activityId);
						e.printStackTrace();
					}	
				}
			}, writingActivity.getStartDate());
		} else if (writingActivity.getStatus() < Activity.STATUS_FINISH || !writingActivity.getReviewingActivities().isEmpty()) {
			for(final Deadline deadline : assignmentDao.loadWritingActivity(activityId).getDeadlines()) {
				final Long deadlineId = deadline.getId();
				if(deadline.getStatus() < Deadline.STATUS_DEADLINE_FINISH && deadline.getFinishDate() != null){
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							
							try {
								finishActivityDeadline(courseDao.loadCourse(courseId), assignmentDao.loadWritingActivity(activityId), assignmentDao.loadDeadline(deadlineId));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}, deadline.getFinishDate());
					break;
				} else {
					for(final ReviewingActivity reviewingActivity : assignmentDao.loadWritingActivity(activityId).getReviewingActivities()) {
						if(deadline.equals(reviewingActivity.getStartDate())) {
							if(reviewingActivity.getStatus() == Activity.STATUS_START && reviewingActivity.getFinishDate() != null){
								timer.schedule(new TimerTask() {
									@Override
									public void run() {
										try {
											finishReviewingActivity(courseDao.loadCourse(courseId), assignmentDao.loadReviewingActivity(reviewingActivity.getId()),deadline);
										} catch (MessageException e) {
											logger.error("Error running reviewing activity " + reviewingActivity.getId());
											e.printStackTrace();
										}
									}
								}, reviewingActivity.getFinishDate());
							} else if ( reviewingActivity.getStatus() == Activity.STATUS_NONE) {
								try{
									startReviewActivity(courseDao.loadCourse(courseId), writingActivity, assignmentDao.loadReviewingActivity(reviewingActivity.getId()),deadline);
								} catch(Exception e){
									logger.error("Error running reviewing activity " + reviewingActivity.getId());
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		}
	}

	public void startActivity(Course course, WritingActivity writingActivity) throws MessageException{

		// check activity status
		if (writingActivity.getStatus() >= Activity.STATUS_START) {
//			logger.info("Assessment has already started.");
			return;
		}

//		logger.info("Assigning documents: course=" + course.getName() + ", activity=" + writingActivity.getName());
		updateActivityDocuments(course, writingActivity);

		// update activity status
		writingActivity.setStatus(Activity.STATUS_START);		
		// update document entry domains
		Organization organization = course.getOrganization();
		String domainName = organization.getGoogleDomain();
		Set<DocEntry> docEntries = writingActivity.getEntries();
		for (DocEntry docEntry : docEntries) {
			docEntry.setDomainName(domainName);
		}		
		writingActivity = assignmentDao.save(writingActivity);

		// schedule next activity deadline
		scheduleActivityDeadline(course, writingActivity);

		// send assessment start notification to students
		if (writingActivity.getEmailStudents()) {
//			logger.info("Sending start assessment noficiation: course=" + course.getName() + ", activity=" + writingActivity.getName());
			for (UserGroup studentGroup : course.getStudentGroups()) {
				if (writingActivity.getTutorial().equals(WritingActivity.TUTORIAL_ALL) || writingActivity.getTutorial().equals(studentGroup.getTutorial())) {
					for (User student : studentGroup.getUsers()) {
						try {
							emailNotifier.sendStudentActivityStartNotification(student, course, writingActivity, writingActivity.getDeadlines().get(writingActivity.getDeadlines().size() - 1));
						} catch (Exception e) {
							e.printStackTrace();
							logger.error("Failed to send assessment start notification.", e);
						}
					}
				}
			}
		}
	}

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

	private void updateActivityDocuments(Course course, WritingActivity writingActivity) throws MessageException {
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
					logger.error("Failed to create document: " + newDocEntry.getTitle(), e);
				}
			}
		}
	}
	
	private void updateActivityReviews(Course course, WritingActivity writingActivity, ReviewingActivity reviewingActivity) throws Exception{
		synchronized (writingActivity.getFolderId().intern()) {
			// get documents to be reviewed
			List<DocEntry> docEntries = new ArrayList<DocEntry>();
			if (reviewingActivity.getStatus() < Activity.STATUS_START) {
				try {
					if (writingActivity.getExcludeEmptyDocsInReviews()){
						docEntries.addAll(selectNonEmptyEntries(writingActivity));
					}else{
						docEntries.addAll(writingActivity.getEntries());
					}
				} catch (Exception e) {
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

//			logger.info("Assigning student reviewers: course=" + course.getName() + ", activity=" + writingActivity.getName());
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
							reviewSetup.get(docEntry).add(lecturers.next());
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
							reviewSetup.get(docEntry).add(tutors.next());
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
							reviewSetup.get(docEntry).add(autoReviewers.next());
						}
					}
				}				
				
			}

			// create user reviews
			for (DocEntry docEntry : reviewSetup.keySet()) {
				for (User user : reviewSetup.get(docEntry)) {
//					logger.info("Assigning reviewer: reviewer=" + user.getUsername() + ", docid=" + docEntry.getId());
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
				}
			}
		}
	}
	
	private Set<DocEntry> selectNonEmptyEntries(WritingActivity writingActivity) throws Exception {
		Set<DocEntry> entries = new HashSet<DocEntry>();
		
		for (DocEntry entry : writingActivity.getEntries()) {
			Course course = courseDao.loadCourse(assignmentDao.loadCourseWhereWritingActivity(writingActivity).getId());
			Organization organization = course.getOrganization();
			String filename = FileUtil.escapeFilename(entry.getDocumentId()) + ".pdf";
			Long deadlineId;
			if (writingActivity.getCurrentDeadline() != null){
				deadlineId = writingActivity.getCurrentDeadline().getId();
			} else {
				deadlineId = writingActivity.getFinalDeadline().getId();
			}
			File file = new File(getDocumentsFolder(course.getId(), writingActivity.getId(),deadlineId , WritingActivity.TUTORIAL_ALL, organization) + "/" + filename);
			try{
				File empty = new File(Reviewer.getOrganizationsHome() + organization.getName()+ Reviewer.getDocumentsHome() + Reviewer.getEmptyDocument());
				if (empty.length() != file.length()){
					entries.add(entry);	
				}
			} catch (Exception e) {
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
		
		if (assignmentDao.loadReviewEntryWhereDocEntryAndOwner(docEntry, user) == null){
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
		Organization organization = course.getOrganization();
		for (User lecturer : lecturers) {
			lecturer.setOrganization(course.getOrganization());
				
			if (lecturer.getDomain() != null && lecturer.getOrganization() != null && 
				    !lecturer.getOrganization().domainBelongsToEmailsDomain(lecturer.getDomain())){	
				MessageException me = new MessageException(Constants.EXCEPTION_LECTURER_INVALID_DOMAIN);
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
				throw me;
			}
			// if the user doesn't exist in Google Apps then create it
			if (!assignmentRepository.userExists(lecturer.getGoogleAppsEmailUsername())){
				assignmentRepository.createUser(lecturer,organization.getOrganizationPasswordNewUsers()+lecturer.getUsername());
			}
			
			// search the lecturer in the database
			User user = userDao.getUserByEmail(lecturer.getEmail());
			if (lecturer.getEmail() != null){
				lecturer.getUsername();
			} else if (lecturer.getFirstname() != null){
				lecturer.setUsername(lecturer.getFirstname().toLowerCase());
			}
			if (user == null) {
				
				if (organization!= null){
					// if organization use shibboleth then set password to null, otherwise generate a new one an encrypt it
					if (!organization.isShibbolethEnabled()){
						if (lecturer.getPassword() == null){
							lecturer.setPassword(Long.toHexString(Double.doubleToLongBits(Math.random())));
						}
						emailNotifier.sendPasswordNotification(lecturer, course);
						lecturer.setPassword(RealmBase.Digest(lecturer.getPassword(), "MD5",null));
					} else {
						lecturer.setPassword(null);
					}
				}
				lecturer.addRole(Constants.ROLE_STAFF);
			}
			else {
				lecturer.setId(user.getId());
				lecturer.setPassword(user.getPassword());
				if (lecturer.getFirstname() == null){
					lecturer.setFirstname(user.getFirstname());
				}
				if (lecturer.getLastname() == null){
					lecturer.setLastname(user.getLastname());
				}
				if (!lecturer.isAdmin() && !lecturer.isStaff() && !lecturer.isSuperAdmin()){
					lecturer.addRole(Constants.ROLE_STAFF);
				}
			}	
			
			// save the lecturer in the database
			lecturer = userDao.save(lecturer);
			
			// add the lecturer to the course 
			course.getLecturers().add(lecturer);
		}
		
		// update course document permissions
		assignmentRepository.updateCourseDocumentPermissions(course, loggedUser);

		// save course in DB in order to save the relationshiop with the course
		course = courseDao.save(course);		
		
		// for each activity create documents and reviewers for new users
		processActivitiesForNewUsers(course);
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
		Organization organization = course.getOrganization();
		for (User tutor : course.getTutors()) {
			tutor.setOrganization(course.getOrganization());
			
			if (tutor.getDomain() != null && tutor.getOrganization() != null && 
				    !tutor.getOrganization().domainBelongsToEmailsDomain(tutor.getDomain())){	
				MessageException me = new MessageException(Constants.EXCEPTION_TUTORS_INVALID_DOMAIN);
				me.setStatusCode(Constants.HTTP_CODE_MESSAGE);
				throw me;
			}
			if (tutor.getEmail() != null){
				tutor.getUsername();
			} else if (tutor.getFirstname() != null){
				tutor.setUsername(tutor.getFirstname().toLowerCase());
			}
			// search the tutor in the database
			User user = userDao.getUserByEmail(tutor.getEmail());
			if (user == null) {
				
				//if organization use shibboleth then set password to null, otherwise generate a new one an encrypt it
				if (organization!= null ){
					if (!organization.isShibbolethEnabled()){
						if (tutor.getPassword() == null){
							tutor.setPassword(Long.toHexString(Double.doubleToLongBits(Math.random())));
						}
						emailNotifier.sendPasswordNotification(tutor, course);
						tutor.setPassword(RealmBase.Digest(tutor.getPassword(), "MD5",null));
					} else {
						tutor.setPassword(null);
					}
				}
				tutor.addRole(Constants.ROLE_STAFF);
			} else {
				tutor.setId(user.getId());
				tutor.setPassword(user.getPassword());
				if (tutor.getFirstname() == null){
					tutor.setFirstname(user.getFirstname());
				}
				if (tutor.getLastname() == null){
					tutor.setLastname(user.getLastname());
				}
				if (!tutor.isAdmin() && !tutor.isStaff() && !tutor.isSuperAdmin()){
					tutor.addRole(Constants.ROLE_STAFF);
				}
			}
			
			// save tutor into the database
			tutor = userDao.save(tutor);
			
			// if the user doesn't exist in Google Apps then create it
			if (!assignmentRepository.userExists(tutor.getGoogleAppsEmailUsername())){
				assignmentRepository.createUser(tutor,organization.getOrganizationPasswordNewUsers()+tutor.getUsername());
			}
			
			// add the tutor to the course 
			course.getTutors().add(tutor);
		}
		
		// update course document permissions
		assignmentRepository.updateCourseDocumentPermissions(course, loggedUser);
		
		// save course in DB
		course = courseDao.save(course);		
		
		// for each activity create documents and reviewers for new users
		processActivitiesForNewUsers(course);
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
		Organization organization = course.getOrganization();
		if (!course.getTutorials().contains(tutorial)) {
			throw new MessageException(Constants.EXCEPTION_INVALID_TUTORIAL + Constants.MESSAGE_STUDENTS_TUTORIAL);
		}
	
		for(User student: students){
			// set the organization
			if (student.getOrganization() == null){
				student.setOrganization(course.getOrganization());
			}	
			// Students have Guest role
			student.getRole_name().add(Constants.ROLE_GUEST);
			
			// check if student domanin is equals to the organization domain
			if (student.getDomain() != null && student.getOrganization() != null && 
					!student.getOrganization().domainBelongsToEmailsDomain(student.getDomain())){
				throw new MessageException(Constants.EXCEPTION_STUDENTS_INVALID_DOMAIN);
			}
			
			if (student.getEmail() != null){
				student.getUsername();
			} else if (student.getFirstname() != null){
				student.setUsername(student.getFirstname().toLowerCase());
			}
			
			// search student by email so it's no necessary get it by organization because the email is unique
			User user = userDao.getUserByEmail(student.getEmail());
			if (user == null) {
				//if organization use shibboleth then set password to null, otherwise generate a new one an encrypt it
				if (organization!= null){
					if (!organization.isShibbolethEnabled()){
						student.setPassword(Long.toHexString(Double.doubleToLongBits(Math.random())));
						// send email notification with password
						emailNotifier.sendPasswordNotification(student, course);
						// encrypt the password with MD5
						student.setPassword(RealmBase.Digest(student.getPassword(), "MD5",null));
					} else {
						student.setPassword(null);
					}
				}
			} else{
				student.setId(user.getId());
				student.setPassword(user.getPassword());
				if (student.getFirstname() == null){
					student.setFirstname(user.getFirstname());
				}
				if (student.getLastname() == null){
					student.setLastname(user.getLastname());
				}
			}
			// save the user in the database
			student = userDao.save(student);
			
			if (!assignmentRepository.userExists(student.getGoogleAppsEmailUsername())){
				assignmentRepository.createUser(student,organization.getOrganizationPasswordNewUsers() + student.getUsername());
			}
								
		
		}
		
		// create a new user group
		UserGroup studentGroup = new UserGroup();
		studentGroup.setTutorial(tutorial);
		studentGroup.setUsers(students);
		studentGroup.setName(group);
		
		//save the user group
		studentGroup = assignmentDao.save(studentGroup);
		
		//add the user group to the course
		course.getStudentGroups().add(studentGroup);
		
		//save the course in DB
		course = courseDao.save(course);
		
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
			if ( org != null && course != null && !course.hasEmails()){
				createEmail(org.getEmail(Constants.EMAIL_LECTURER_DEADLINE_FINISH),course);
				createEmail(org.getEmail(Constants.EMAIL_PASSWORD_DETAILS), course);
				createEmail(org.getEmail(Constants.EMAIL_STUDENT_ACTIVITY_START),course);
				createEmail(org.getEmail(Constants.EMAIL_STUDENT_RECEIVED_REVIEW),course);
				createEmail(org.getEmail(Constants.EMAIL_STUDENT_REVIEW_FINISH),course);
				createEmail(org.getEmail(Constants.EMAIL_STUDENT_REVIEW_START),course);
			}
				
		} catch(Exception e){
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
		reviewTemplate.noShareWith(userToShare);
		reviewTemplate = assignmentDao.save(reviewTemplate);
		return reviewTemplate;
	}

	public void startReviewActivity(Course course, WritingActivity writingActivity, ReviewingActivity reviewingActivity, Deadline deadline) throws Exception{
		// check reviewactivity status
		if (reviewingActivity.getStatus() >= Activity.STATUS_START) {
//			logger.info("Review has already finished.");
			return;
		}
		
		if (deadline.equals(reviewingActivity.getStartDate())) {
			updateActivityReviews(course, writingActivity, reviewingActivity);
			reviewingActivity.setStatus(Activity.STATUS_START);
			reviewingActivity = assignmentDao.save(reviewingActivity);
		}
		
		// send review start notification to students
		if (writingActivity.getEmailStudents()) {
			if (deadline.equals(reviewingActivity.getStartDate()) && (reviewingActivity.getNumStudentReviewers() > 0)) {
				for (UserGroup studentGroup : course.getStudentGroups()) {
					if (writingActivity.getTutorial().equals(WritingActivity.TUTORIAL_ALL) || writingActivity.getTutorial().equals(studentGroup.getTutorial())) {
						for (User student : studentGroup.getUsers()) {
							try {
								emailNotifier.sendStudentReviewStartNotification(student, course, writingActivity, deadline);
							} catch (Exception e) {
								e.printStackTrace();
								logger.error("Failed to send review start notification.", e);
							}
						}
					}
				}

			}
		}


	}

	
	
}