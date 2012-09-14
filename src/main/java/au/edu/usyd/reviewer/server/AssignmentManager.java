package au.edu.usyd.reviewer.server;

//import glosser.app.doc.Doc;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
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

import com.google.gdata.data.MediaContent;
import com.google.gdata.data.OutOfLineContent;
import com.google.gdata.data.docs.DocumentEntry;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.RevisionEntry;
import com.google.gdata.data.media.MediaSource;

import au.edu.usyd.feedback.feedbacktracking.FeedbackTracking;
import au.edu.usyd.feedback.feedbacktracking.FeedbackTrackingDao;
import au.edu.usyd.feedback.server.FeedbackServiceImpl;
import au.edu.usyd.reviewer.client.core.Activity;
import au.edu.usyd.reviewer.client.core.Choice;
import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Deadline;
import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.Entry;
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
	private AssignmentDao assignmentDao;
	private UserDao userDao;
	private String documentsHome = "documents";
	private FeedbackServiceImpl feedBackService = new FeedbackServiceImpl();
	private FeedbackTrackingDao feedBackTrackingService = new FeedbackTrackingDao() ;		
	private Organization organization = null;
	
	public AssignmentManager(AssignmentRepository assignmentRepository, AssignmentDao assignmentDao, EmailNotifier emailNotifier, Organization organization) {
		this.assignmentRepository = assignmentRepository;
		this.assignmentDao = assignmentDao;
		this.emailNotifier = emailNotifier;	
		this.userDao = UserDao.getInstance();
		this.organization = organization;

		List<Course> courses = assignmentDao.loadCourses();
		for (Course course : courses) {
			for (WritingActivity writingActivity : course.getWritingActivities()) {
				scheduleActivityDeadline(course, writingActivity);
			}
		}
	}

	public void deleteActivity(WritingActivity writingActivity) throws Exception {
		Timer timer = activityTimers.get(writingActivity.getId());
		if (timer != null) {
			timer.cancel();
		}
		Course course = assignmentDao.loadCourse(assignmentDao.loadCourseWhereWritingActivity(writingActivity).getId());
		course.getWritingActivities().remove(writingActivity);
		assignmentDao.save(course);
		assignmentDao.delete(writingActivity);
		assignmentRepository.deleteActivity(writingActivity);
	}

	public void deleteCourse(Course course) throws Exception {
		for (WritingActivity writingActivity : course.getWritingActivities()) {
			Timer timer = activityTimers.get(writingActivity.getId());
			if (timer != null) {
				timer.cancel();
			}
		}
		assignmentDao.delete(course);
		assignmentRepository.deleteCourse(course);
	}

	private void downloadDocuments(Course course, WritingActivity writingActivity, Deadline deadline) {
		File activityFolder = new File(getDocumentsFolder(course.getId(), writingActivity.getId(), deadline.getId(), "all"));
		activityFolder.mkdirs();
		for (DocEntry docEntry : writingActivity.getEntries()) {
			UserGroup studentGroup = writingActivity.getGroups() ? docEntry.getOwnerGroup() : assignmentDao.loadUserGroupWhereUser(course, docEntry.getOwner());
			String filePath = activityFolder.getAbsolutePath() + "/" + FileUtil.escapeFilename(docEntry.getDocumentId()) + ".pdf";
			try {
				if (docEntry instanceof LogbookDocEntry) {
					// do nothing
				} else {
					// download PDF document
					if (docEntry.isLocalFile()){
							 if (docEntry.isUploaded()){
								 filePath = Reviewer.getUploadsHome()+"/"+docEntry.getFileName(); 
							 }else {
								 filePath = Reviewer.getEmptyFile();
								 
							 }						   
						}else{
							assignmentRepository.downloadDocumentFile(docEntry, filePath);
						}					
				}
				// copy document PDF to zip folder
				String filename = FileUtil.escapeFilename((writingActivity.getGroups() ? "Group " + docEntry.getOwnerGroup().getName() : docEntry.getOwner().getLastname() + ", " + docEntry.getOwner().getFirstname() + " (" + docEntry.getOwner().getUsername() + ")") + " - " + deadline.getName() + ".pdf");	
				String filePathZip = getDocumentsFolder(course.getId(), writingActivity.getId(), deadline.getId(), studentGroup.getTutorial()) + "/" + filename;
				FileUtil.copyFile(filePath, filePathZip);
				
				//Copy entry to "all" folder in case of an uploaded file
				if (docEntry.isLocalFile()){
					if (docEntry.getFileName() !=null) {
						filePathZip = getDocumentsFolder(course.getId(), writingActivity.getId(), deadline.getId(), "all") + "/file-" + docEntry.getFileName();
					}else {
						filePathZip = getDocumentsFolder(course.getId(), writingActivity.getId(), deadline.getId(), "all") + "/Empty file -" + docEntry.getTitle() + ".pdf";
					}
					FileUtil.copyFile(filePath, filePathZip);
				}
				
				docEntry.setDownloaded(true);
				assignmentDao.save(docEntry);
			} catch (Exception e) {
				logger.error("Failed to download document PDF. ", e);
			}
		}

		// zip PDF documents
		for (File folder : activityFolder.getParentFile().listFiles()) {
			if (folder.isDirectory()) {
				FileUtil.zipFolder(folder, new File(folder.getAbsolutePath() + ".zip"));
			}
		}
	}

	public void finishActivityDeadline(Course course, WritingActivity writingActivity, Deadline deadline) throws Exception{
		// check activity status
		if (deadline.getStatus() >= Deadline.STATUS_DEADLINE_FINISH) {
			logger.info("Deadline has already finished.");
			return;
		}

		if(writingActivity.getDeadlines().get(writingActivity.getDeadlines().size()-1).equals(deadline)) {
			// update document writer permissions to reader permissions for ALL
			// documents in activity folder
			assignmentRepository.lockActivityDocuments(writingActivity);
	
			// lock documents
			for (DocEntry docEntry : writingActivity.getEntries()) {
				docEntry.setLocked(true);
				assignmentDao.save(docEntry);
			}
	
			// update activity status
			writingActivity.setStatus(Activity.STATUS_FINISH);
			assignmentDao.save(writingActivity);
		}

		// download PDF documents
		downloadDocuments(course, writingActivity, deadline);

		// create reviews
		for (ReviewingActivity reviewingActivity : writingActivity.getReviewingActivities()) {
			if (deadline.equals(reviewingActivity.getStartDate())) {
				updateActivityReviews(course, writingActivity, reviewingActivity);
				reviewingActivity.setStatus(Activity.STATUS_START);
				assignmentDao.save(reviewingActivity);
			}
		}

		// update activity deadline status
		deadline.setStatus(Deadline.STATUS_DEADLINE_FINISH);
		assignmentDao.save(deadline);
		
		// schedule next activity deadline
		scheduleActivityDeadline(course, writingActivity);
		
		// send assessment finish notification to lecturers
		for (User lecturer : course.getLecturers()) {
			try {
				emailNotifier.sendLecturerDeadlineFinishNotification(lecturer, course, writingActivity, deadline.getName());
			} catch (Exception e) {
				logger.error("Failed to send assessment finish notification.", e);
			}
		}
		
		// send review start notification to students
		if (writingActivity.getEmailStudents()) {
			for (ReviewingActivity reviewingActivity : writingActivity.getReviewingActivities()) {
				if (deadline.equals(reviewingActivity.getStartDate()) && (reviewingActivity.getNumStudentReviewers() > 0)) {
					logger.info("Sending start review noficiation: course=" + course.getName() + ", activity=" + writingActivity.getName() +", NumStudentReviewers=" + reviewingActivity.getNumStudentReviewers());
					for (UserGroup studentGroup : course.getStudentGroups()) {
						if (writingActivity.getTutorial().equals(WritingActivity.TUTORIAL_ALL) || writingActivity.getTutorial().equals(studentGroup.getTutorial())) {
							for (User student : studentGroup.getUsers()) {
								try {
									emailNotifier.sendStudentReviewStartNotification(student, course, writingActivity, deadline);
								} catch (Exception e) {
									logger.error("Failed to send review start notification.", e);
								}
							}
						}
					}
					//break;
				}
			}
		}	
	}

	public void finishReviewingActivity(Course course, ReviewingActivity reviewingActivity, Deadline deadline) {
		// check activity status
		if (reviewingActivity.getStatus() >= Activity.STATUS_FINISH) {
			logger.info("Review has already finished.");
			return;
		}

		// download HTML reviews
		UserGroup studentGroup = null;
		for (ReviewEntry reviewEntry : reviewingActivity.getEntries()) {
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
			logger.info("Review File: "+getDocumentsFolder(course.getId(), reviewingActivity.getId(), reviewingActivity.getStartDate().getId(), studentGroup.getTutorial()) + "/" + FileUtil.escapeFilename(filename));
			File reviewFile = new File(getDocumentsFolder(course.getId(), reviewingActivity.getId(), reviewingActivity.getStartDate().getId(), studentGroup.getTutorial()) + "/" + FileUtil.escapeFilename(filename));
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
				logger.error("Failed to save review HTML", e);
			} finally {
				if (out != null) {
					out.flush();
					out.close();
				}
			}
		}

		// zip HTML reviews 
		logger.info("Zip review files in: "+getDocumentsFolder(course.getId(), reviewingActivity.getId(), reviewingActivity.getStartDate().getId(), ""));
		for (File folder : new File(getDocumentsFolder(course.getId(), reviewingActivity.getId(), reviewingActivity.getStartDate().getId(), "")).listFiles()) {		                            
			if (folder.isDirectory()) {
				FileUtil.zipFolder(folder, new File(folder.getAbsolutePath() + ".zip"));
			}
		}

		// update activity status
		reviewingActivity.setStatus(Activity.STATUS_FINISH);
		assignmentDao.save(reviewingActivity);
		
		// release reviews 
		for(ReviewEntry reviewEntry : reviewingActivity.getEntries()) {
			DocEntry docEntry = reviewEntry.getDocEntry();
			//Check if the review hasn't been released early
			if (!docEntry.getReviews().contains(reviewEntry.getReview())){
				docEntry.getReviews().add(reviewEntry.getReview());
				assignmentDao.save(docEntry);
			}
		}

		// send review finish notification to lecturers
		for (User lecturer : course.getLecturers()) {
			try {
				emailNotifier.sendLecturerDeadlineFinishNotification(lecturer, course, reviewingActivity, reviewingActivity.getName());
			} catch (Exception e) {
				logger.error("Failed to send review finish notification.", e);
			}
		}

		//send review finish notifications to students	
		WritingActivity writingActivity = assignmentDao.loadWritingActivityWhereDeadline(deadline);
		if(writingActivity != null && writingActivity.getEmailStudents()){
			List<DocEntry> notifiedDocEntries = new ArrayList<DocEntry>();
			for (ReviewEntry reviewEntry : reviewingActivity.getEntries()) {					
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
						logger.error("Failed to send review finish notification.", e);
					}
				}
			}
		}

		// read excel file and insert questions into DB
		if (reviewingActivity.getFormType() != null && reviewingActivity.getFormType().equals(ReviewingActivity.REVIEW_TYPE_QUESTION)) {
			String path = getDocumentsFolder(course.getId(), reviewingActivity.getId(), "aqg", "");
			String filepath = path + Reviewer.getProperty(Constants.AGG_LOAD_EXCEL_PATH);
			QuestionUtil questionUtil = new QuestionUtil();
			try {
				questionUtil.readExcelInsertDB(filepath, course.getOrganization());
			} catch (Exception e) {
				logger.error("Failed to read the excel.", e);
			}
		}
	}

	public AssignmentDao getAssignmentDao() {
		return assignmentDao;
	}

	public AssignmentRepository getAssignmentRepository() {
		return assignmentRepository;
	}

	public String getDocumentsFolder(long courseId, long activityId, long activityDeadlineId, String tutorial) {
		return String.format(documentsHome + "/%s/%s/%s/%s", courseId, activityId, activityDeadlineId, tutorial);
	}

	public String getDocumentsFolder(long courseId, long activityId, String activityDeadlineId, String tutorial) {
		return String.format(documentsHome + "/%s/%s/%s/%s", courseId, activityId, activityDeadlineId, tutorial);
	}

	public WritingActivity saveActivity(Course course, WritingActivity writingActivity) throws Exception {
		// check if status is valid
		if (writingActivity.getId() != null && writingActivity.getStatus() != assignmentDao.loadWritingActivity(writingActivity.getId()).getStatus()) {
			throw new Exception("Failed to save Activity: Invalid status");
		}

		// check if tutorial is valid
		if (!course.getTutorials().contains(writingActivity.getTutorial()) && !writingActivity.getTutorial().equals(WritingActivity.TUTORIAL_ALL)) {
			throw new Exception("Failed to save Activity: Invalid tutorial");
		}

		// create activity folder
		if (writingActivity.getFolderId() == null) {
			assignmentRepository.createActivity(course, writingActivity);
		}

		assignmentDao.save(writingActivity);
		course.getWritingActivities().add(writingActivity);
		assignmentDao.save(course);

		// schedule next activity deadline
		scheduleActivityDeadline(course, writingActivity);

		return writingActivity;
	}

	private void setUpFoldersAndTemplates(Course course) throws Exception{
		
		List<DocumentListEntry> templates = assignmentRepository.setUpFolders(course);

		course.getTemplates().clear();
		for (DocumentListEntry template : templates) {
			DocEntry tempDocEntry = assignmentDao.loadDocEntry(template.getResourceId());
			DocEntry templateEntry = new DocEntry();
			if (!(tempDocEntry == null)){			
				templateEntry = tempDocEntry;
			}
			templateEntry.setDocumentId(template.getResourceId());
			templateEntry.setTitle(template.getTitle().getPlainText());
			templateEntry.setDomainName(Reviewer.getGoogleDomain());
			course.getTemplates().add(templateEntry);
	    }
		
		// create course folder
		assignmentRepository.updateCourse(course);
	}
	
	private void saveStudentUsers(Course course) throws Exception {
		
		for (UserGroup studentGroup : course.getStudentGroups()) {
			for (User student : studentGroup.getUsers()) {
				if (!userDao.containsUser(student)) {
					assignmentRepository.createUser(student);
				}
			}
		}	
	}
	
	private void saveLecturerUsers(Course course) throws Exception{
		
		for (User lecturer : course.getLecturers()) {
			if (!userDao.containsUser(lecturer)) {
					assignmentRepository.createUser(lecturer);
			}
		}
	}
	
	private void saveTutorUsers(Course course) throws Exception {
		
		for (User tutor : course.getTutors()) {
			if (!userDao.containsUser(tutor)) {
					assignmentRepository.createUser(tutor);
			}
		}
	}
	
	private void saveLecturerDB(Course course) throws Exception{
		for(User lecturer : course.getLecturers()) {
			if(!userDao.containsUser(lecturer)){
				//send password notification if not a wasm user
				if (!lecturer.getWasmuser()){
					emailNotifier.sendPasswordNotification(lecturer, course.getName());
					lecturer.setPassword(RealmBase.Digest(lecturer.getPassword(), "MD5",null));
				}					
				lecturer.setOrganization(course.getOrganization());
				assignmentDao.save(lecturer);
			}
		}
	}
	
	private void saveTutorDB(Course course) throws Exception {
		for(User tutor : course.getTutors()) {
			if(!userDao.containsUser(tutor)){
				//send password notification if not a wasm user
				if (!tutor.getWasmuser()){					
					emailNotifier.sendPasswordNotification(tutor, course.getName());
					tutor.setPassword(RealmBase.Digest(tutor.getPassword(), "MD5",null));
				}				
				tutor.setOrganization(course.getOrganization());
				assignmentDao.save(tutor);	
			}				
		}
	}
	
	private void saveUserGroupDB(Course course)throws Exception{
		for(UserGroup studentGroup : course.getStudentGroups()) {
			for(User student : studentGroup.getUsers()) {
				if(!userDao.containsUser(student)){
					//send password notification if not a wasm user
					if (!student.getWasmuser()){
						emailNotifier.sendPasswordNotification(student, course.getName());
						student.setPassword(RealmBase.Digest(student.getPassword(), "MD5",null));
					}	
					student.setOrganization(course.getOrganization());
					assignmentDao.save(student);		
				}
			}
			assignmentDao.save(studentGroup);
		}
	}
	
	private void processActivitiesForNewUsers(Course course) throws Exception {
		
		for (WritingActivity writingActivity : course.getWritingActivities()) {
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
	
	public Course saveCourse(Course course) throws Exception {
		//Set up folders and templates
		setUpFoldersAndTemplates(course);
		
		///Google Users//////////////////////////////////////////////////////
		// save student users
		saveStudentUsers(course);
		
		// save lecturer users
		saveLecturerUsers(course);
		
		// save tutor users
		saveTutorUsers(course);
		
		///////////////////////////////////////////////////////////////////////		
		
		///Local DataBase//////////////////////////////////////////////////////
		// save lecture in DB
		saveLecturerDB(course);
		
		// save tutor in DB
		saveTutorDB(course);
		
		// save user group in DB
		saveUserGroupDB(course);
		
		// update course document permissions
		assignmentRepository.updateCourseDocumentPermissions(course);
		
		course.setDomainName(Reviewer.getGoogleDomain());
		
		// save course in DB
		assignmentDao.save(course);		
		///Local DataBase//////////////////////////////////////////////////////
		
		// for each activity create documents and reviewers for new users
		processActivitiesForNewUsers(course);
		
		return course;
	}

	private void scheduleActivityDeadline(Course course, WritingActivity writingActivity) {
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
					startActivity(assignmentDao.loadCourse(courseId), assignmentDao.loadWritingActivity(activityId));
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
								finishActivityDeadline(assignmentDao.loadCourse(courseId), assignmentDao.loadWritingActivity(activityId), assignmentDao.loadDeadline(deadlineId));
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
										finishReviewingActivity(assignmentDao.loadCourse(courseId), assignmentDao.loadReviewingActivity(reviewingActivity.getId()),deadline);
									}
								}, reviewingActivity.getFinishDate());
							}
						}
					}
				}
			}
		}
	}
	public void setDocumentsHome(String documentsHome) {
		this.documentsHome = documentsHome;
	}

	public void startActivity(Course course, WritingActivity writingActivity) {

		// check activity status
		if (writingActivity.getStatus() >= Activity.STATUS_START) {
			logger.info("Assessment has already started.");
			return;
		}

		logger.info("Assigning documents: course=" + course.getName() + ", activity=" + writingActivity.getName());
		updateActivityDocuments(course, writingActivity);

		// update activity status
		writingActivity.setStatus(Activity.STATUS_START);		
		// update document entry domains
		String domainName = Reviewer.getGoogleDomain();
		Set<DocEntry> docEntries = writingActivity.getEntries();
		for (DocEntry docEntry : docEntries) {
			docEntry.setDomainName(domainName);
		}		
		assignmentDao.save(writingActivity);

		// schedule next activity deadline
		scheduleActivityDeadline(course, writingActivity);

		// send assessment start notification to students
		if (writingActivity.getEmailStudents()) {
			logger.info("Sending start assessment noficiation: course=" + course.getName() + ", activity=" + writingActivity.getName());
			for (UserGroup studentGroup : course.getStudentGroups()) {
				if (writingActivity.getTutorial().equals(WritingActivity.TUTORIAL_ALL) || writingActivity.getTutorial().equals(studentGroup.getTutorial())) {
					for (User student : studentGroup.getUsers()) {
						try {
							emailNotifier.sendStudentActivityStartNotification(student, course, writingActivity, writingActivity.getDeadlines().get(writingActivity.getDeadlines().size() - 1));
						} catch (Exception e) {
							logger.error("Failed to send assessment start notification.", e);
						}
					}
				}
			}
		}
	}

	public <D extends DocEntry> D submitDocument(D docEntry) throws Exception {
		synchronized (docEntry.getDocumentId().intern()) {
			WritingActivity writingActivity = assignmentDao.loadWritingActivityWhereDocEntry(docEntry);
			Course course = assignmentDao.loadCourseWhereWritingActivity(writingActivity);
			
			File activityFolder = new File(this.getDocumentsFolder(course.getId(), writingActivity.getId(), writingActivity.getDeadlines().get(writingActivity.getDeadlines().size() - 1).getId(), WritingActivity.TUTORIAL_ALL));
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
						assignmentDao.save(newLogpageEntry);
					} catch (Exception e) {
						// unlock document
						logpageDocEntry.setLocked(false);
						assignmentRepository.updateDocument(logpageDocEntry);
						throw e;
					}

					// submit entry
					assignmentDao.save(logpageDocEntry);
					assignmentDao.save(logbookDocEntry);

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
						logger.error("Error merging PDFs", e);
					}

				} else {
					throw new Exception("Document has already been submitted.");
				}
			} else {
				if (writingActivity.getEarlySubmit()){
					docEntry.setEarlySubmitDate(new Date());
					assignmentDao.save(docEntry);
				}
				
				if (docEntry.isLocalFile()){
					FileUtil.copyFile(Reviewer.getUploadsHome()+"/"+docEntry.getFileName(), filePath);
				}else{
					assignmentRepository.downloadDocumentFile(docEntry, filePath);
				}
			}
			if (writingActivity.getStatus() >= Activity.STATUS_FINISH) {
				// copy document PDF to zip folder
				UserGroup studentGroup = writingActivity.getGroups() ? docEntry.getOwnerGroup() : assignmentDao.loadUserGroupWhereUser(course, docEntry.getOwner());
				File tutorialfolder = new File(this.getDocumentsFolder(course.getId(), writingActivity.getId(), writingActivity.getDeadlines().get(writingActivity.getDeadlines().size() - 1).getId(), studentGroup.getTutorial()));
				String filePathZip = tutorialfolder.getAbsolutePath() + "/" + FileUtil.escapeFilename((writingActivity.getGroups() ? "Group " + docEntry.getOwnerGroup().getName() : docEntry.getOwner().getLastname() + ", " + docEntry.getOwner().getFirstname() + " (" + docEntry.getOwner().getUsername() + ")") + " - Final.pdf");
				FileUtil.copyFile(filePath, filePathZip);
				FileUtil.zipFolder(tutorialfolder, new File(tutorialfolder.getAbsolutePath() + ".zip"));
				docEntry.setDownloaded(true);
			}
			return docEntry;
		}
	}

	private void updateActivityDocuments(Course course, WritingActivity writingActivity) {
		synchronized (writingActivity.getFolderId().intern()) {
			String domainName = Reviewer.getGoogleDomain();
			// remove documents for deleted users
			// NOTE documents will not be removed after a review has started
			if (writingActivity.getStatus() < Activity.STATUS_FINISH) {
				List<User> students = new ArrayList<User>();
				for (UserGroup studentGroup : course.getStudentGroups()) {
					for (User student : studentGroup.getUsers()) {
						students.add(student);
					}
				}
				for (Iterator<DocEntry> docEntries = writingActivity.getEntries().iterator(); docEntries.hasNext();) {
					DocEntry docEntry = docEntries.next();
					try{
						//update permission if any user has changed groups
						updateDocument(docEntry);
					} catch (Exception e) {
						logger.error("Failed to update document : " + docEntry.getTitle(), e);
					}
					if (writingActivity.getGroups() && !course.getStudentGroups().contains(docEntry.getOwnerGroup()) || !writingActivity.getGroups() && !students.contains(docEntry.getOwner())) {
						docEntries.remove();
						assignmentDao.save(writingActivity);
					}
				}
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
					assignmentDao.save(newDocEntry);
					writingActivity.getEntries().add(newDocEntry);
					assignmentDao.save(writingActivity);
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
					if(reviewEntry.getDocEntry() != null) {
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

			logger.info("Assigning student reviewers: course=" + course.getName() + ", activity=" + writingActivity.getName());
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
					logger.info("Assigning reviewer: reviewer=" + user.getUsername() + ", docid=" + docEntry.getId());
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
					assignmentDao.save(review);
					
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
					assignmentDao.save(reviewEntry);
					reviewingActivity.getEntries().add(reviewEntry);
					assignmentDao.save(reviewingActivity);
				}
			}
		}
	}
	
	private Set<DocEntry> selectNonEmptyEntries(WritingActivity writingActivity) throws Exception {
		Set<DocEntry> entries = new HashSet<DocEntry>();
		
		for (DocEntry entry : writingActivity.getEntries()) {
			Course course = assignmentDao.loadCourse(assignmentDao.loadCourseWhereWritingActivity(writingActivity).getId());
			File file = new File(getDocumentsFolder(course.getId(), writingActivity.getId(), writingActivity.getCurrentDeadline().getId(), WritingActivity.TUTORIAL_ALL) + "/" + FileUtil.escapeFilename(entry.getDocumentId()) + ".pdf");
			
			try{
				File empty = new File(Reviewer.getEmptyDocument());
				
				//Only add non empty entries
				if (empty.length() != file.length()){
					entries.add(entry);
				}
				
			} catch (Exception e) {
				logger.error("Error reading empty document.", e);					
			}										
		}
		return entries;
	}
	
	
	public <D extends DocEntry> D updateDocument(D docEntry) throws Exception {
		synchronized (docEntry.getDocumentId().intern()) {
			try {
				assignmentRepository.updateDocument(docEntry);
			} catch (Exception e) {
				logger.error("Error updating document permission.", e);
				throw e;
			}
			assignmentDao.save(docEntry);
		}

		// resubmit document if activity has finished
		if (docEntry.getDownloaded() && docEntry.getLocked() && !(docEntry instanceof LogbookDocEntry)) {
			try {
				this.submitDocument(docEntry);
			} catch (Exception e) {
				logger.error("Error submitting document.", e);
			}
		}
		return docEntry;
	}

	public ReviewTemplate saveReviewTemplate(ReviewTemplate reviewTemplate) throws Exception {
		ReviewTemplate reviewTemplateToSave = new ReviewTemplate();
		reviewTemplateToSave.setName(reviewTemplate.getName());
		reviewTemplateToSave.setDescription(reviewTemplate.getDescription());
		reviewTemplateToSave.setSections(reviewTemplate.getSections());		

		if (reviewTemplate.getId() !=null){
				deleteReviewTemplate(reviewTemplate);
		}

		for (Section section : reviewTemplateToSave.getSections()) {
			if (section.getType() != Section.OPEN_QUESTION) {
				for (Choice choice : section.getChoices()) {
					assignmentDao.save(choice);
				}
			}
			assignmentDao.save(section);
		}
		assignmentDao.save(reviewTemplateToSave);
		return reviewTemplateToSave;		
	}
	
	public void deleteReviewTemplate(ReviewTemplate reviewTemplate) throws Exception {
		
		if (!assignmentDao.isReviewTemplateInUse(reviewTemplate)){		
			reviewTemplate = assignmentDao.loadReviewTemplate(reviewTemplate.getId());
			for (Section section : reviewTemplate.getSections()) {
				if (section.getType() != Section.OPEN_QUESTION){
					for (Choice choice: section.getChoices()) {
						assignmentDao.delete(choice);
					}
				}		
				section.setChoices(null);
				assignmentDao.delete(section);
			}
			reviewTemplate.setSections(null);
			assignmentDao.delete(reviewTemplate);
		}else{
			throw new Exception("Review template already in use.");
		}
	}
	
	public String updateReviewDocEntry(String reviewEntryId, String newDocEntry) throws Exception {
		ReviewEntry reviewEntry =  assignmentDao.loadReviewEntry(Long.valueOf(reviewEntryId));
		DocEntry docEntry = assignmentDao.loadDocEntryWhereId(Long.valueOf(newDocEntry));
		
		if ( (docEntry.getOwner()!=null && reviewEntry.getOwner() == docEntry.getOwner()) 
				|| (docEntry.getOwner()==null && docEntry.getOwnerGroup().getUsers().contains(reviewEntry.getOwner()) )) {
			throw new Exception("Reviewer can't be owner of the document.");
		}else{ 
			reviewEntry.setDocEntry(docEntry);
			assignmentDao.save(reviewEntry);			
		}
		return docEntry.getTitle();
	}

	public void deleteReviewEntry(String reviewEntryId) throws Exception {
			ReviewEntry reviewEntry = assignmentDao.loadReviewEntry(Long.valueOf(reviewEntryId));
			assignmentDao.delete(reviewEntry);		
	}
	
	public ReviewEntry saveNewReviewEntry(String reviewingActivityId, String userId, String docEntryId, Organization organization) throws Exception{
		ReviewingActivity reviewingActivity = assignmentDao.loadReviewingActivity(Long.valueOf(reviewingActivityId));
		DocEntry docEntry = assignmentDao.loadDocEntryWhereId(Long.valueOf(docEntryId));
		// userId is the username
		User user = userDao.getUserByUsername(userId, organization);
		
		if (assignmentDao.loadReviewEntryWhereDocEntryAndOwner(docEntry, user) == null){
			ReviewEntry reviewEntry = new ReviewEntry();
			Review review = new Review();		
			assignmentDao.save(review);
			reviewEntry.setReview(review);
			
			reviewEntry.setDocEntry(docEntry);
			reviewEntry.setOwner(user);
			reviewEntry.setTitle(user.getLastname()+","+user.getFirstname());
			assignmentDao.save(reviewEntry);
			
			reviewingActivity.getEntries().add(reviewEntry);
			assignmentDao.save(reviewingActivity);
			
			return reviewEntry;			
		}else{
			throw new Exception("Review already assigned to user.");
		}
	}

}