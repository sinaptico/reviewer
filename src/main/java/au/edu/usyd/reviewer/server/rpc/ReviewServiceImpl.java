package au.edu.usyd.reviewer.server.rpc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.usyd.feedback.feedbacktracking.FeedbackTracking;
import au.edu.usyd.feedback.feedbacktracking.FeedbackTrackingDao;
import au.edu.usyd.reviewer.client.core.Activity;
import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.Deadline;
import au.edu.usyd.reviewer.client.core.DocEntry;
import au.edu.usyd.reviewer.client.core.DocumentType;
import au.edu.usyd.reviewer.client.core.Grade;
import au.edu.usyd.reviewer.client.core.Question;
import au.edu.usyd.reviewer.client.core.QuestionRating;
import au.edu.usyd.reviewer.client.core.QuestionScore;
import au.edu.usyd.reviewer.client.core.Rating;
import au.edu.usyd.reviewer.client.core.Review;
import au.edu.usyd.reviewer.client.core.ReviewEntry;
import au.edu.usyd.reviewer.client.core.ReviewingActivity;
import au.edu.usyd.reviewer.client.core.User;
import au.edu.usyd.reviewer.client.core.WritingActivity;
import au.edu.usyd.reviewer.client.review.ReviewService;
import au.edu.usyd.reviewer.server.AssignmentDao;
import au.edu.usyd.reviewer.server.AssignmentManager;
import au.edu.usyd.reviewer.server.EmailNotifier;
import au.edu.usyd.reviewer.server.QuestionDao;
import au.edu.usyd.reviewer.server.Reviewer;
import au.edu.usyd.reviewer.server.util.CloneUtil;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ReviewServiceImpl extends RemoteServiceServlet implements ReviewService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private AssignmentManager assignmentManager = Reviewer.getAssignmentManager();
	private EmailNotifier emailNotifier = Reviewer.getEmailNotifier();
	private AssignmentDao assignmentDao = assignmentManager.getAssignmentDao();
	private FeedbackTrackingDao feedbackTrackingService = new FeedbackTrackingDao();

	@Override
	public Rating getUserRatingForEditing(Review review) throws Exception {
		Rating rating = assignmentDao.loadUserRatingForEditing(getUser(), review);
		if (rating == null) {
			throw new Exception("Rating not found");
		}
		return CloneUtil.clone(rating);
	}

	@Override
	public Course getUserReviewForEditing(long reviewId) throws Exception {
		Course course = assignmentDao.loadUserReviewForEditing(getUser(), reviewId);
		if (course == null) {
			throw new Exception("Review not found");
		}
		return CloneUtil.clone(course);
	}

	@Override
	public QuestionRating getQuestionRating(String docId) {
		if (docId == null || docId.trim() == "") {
			return null;
		}
		QuestionDao questionDao = new QuestionDao(Reviewer.getHibernateSessionFactory());

		List<Question> questionlist = questionDao.getQuestion(docId);
		if (questionlist.size() > 0) {
			if (questionDao.getScore(questionlist.get(0)).size() > 0) {
				return null;
			}
		}
		Random random = new Random();
		int size = questionlist.size();
		List<Question> randomlist = new ArrayList<Question>();
		for (int i = 0; i < size; i++) {
			int j = random.nextInt(questionlist.size());
			Question question = questionlist.remove(j);
			if (StringUtils.isNotBlank(question.getQuestion())) {
				randomlist.add(question);
			}
		}

		QuestionRating review = new QuestionRating();
		review.setTriggerQuestions(randomlist);

		try {
			return CloneUtil.clone(review);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public User getUser() throws Exception {
		User user = (User) this.getThreadLocalRequest().getSession().getAttribute("user");
		if (user == null) {
			throw new Exception("Your session has expired. Please login again.");
		} else {
			return user;
		}
	}

	public boolean isCourseInstructor(Course course) throws Exception {
		User user = getUser();
		return user == null ? false : course.getLecturers().contains(user) || course.getTutors().contains(user) || course.getSupervisors().contains(getUser());
	}

	@Override
	public Collection<Grade> submitGrades(Collection<Grade> grades) throws Exception {
		for(Grade grade : grades) {
			Deadline deadline = grade.getDeadline();
			Course course = assignmentDao.loadCourseWhereDeadline(deadline);
			if (isCourseInstructor(course)) {
				User user = grade.getUser();
				Grade currentGrade = assignmentDao.loadGrade(deadline, user);
				if(currentGrade != null) {
					currentGrade.setValue(grade.getValue());
				} else {
					currentGrade = grade;
				}
				assignmentDao.save(currentGrade);
				WritingActivity writingActivity = assignmentDao.loadWritingActivityWhereDeadline(deadline);
				writingActivity.getGrades().add(currentGrade);
				assignmentDao.save(writingActivity);
			} else {
				throw new Exception("Permission denied");
			}
		}
		return grades;
	}

	@Override
	public <R extends Rating> R submitRating(R rating, Review review) throws Exception {
		// check permission
		Rating userRating = assignmentDao.loadRating(rating.getId());
		if (userRating != null) {
			if (userRating.getOwner().equals(getUser())) {
				rating.setId(userRating.getId());
			} else {
				throw new Exception("Your session has expired. Please login again to submit your rating.");
			}
		}

		// save rating
		if (rating instanceof QuestionRating) {
			User user = assignmentDao.loadUser(getUser().getId());
			if (((QuestionRating) rating).getEvaluatorBackground().equals("Yes")) {
				user.setNativeSpeaker("Yes");
			} else {
				user.setNativeSpeaker("No");
			}

			QuestionDao questionDao = new QuestionDao(Reviewer.getHibernateSessionFactory());
			for (QuestionScore questionScore : ((QuestionRating) rating).getScores()) {
				questionDao.saveScoreAndDocOwner(questionScore, user);
			}
		} else {
			ReviewEntry reviewEntry = assignmentDao.loadReviewEntryWhereReview(review);
			rating.setEntry(reviewEntry);
			rating.setOwner(getUser());
			assignmentDao.save(rating);
		}

		return rating;
	}

	@Override
	public <R extends Review> R saveReview(R review) throws Exception {
		// check review deadline
		ReviewingActivity reviewingActivity = assignmentDao.loadReviewingActivityWhereReview(review);
		Course course = assignmentDao.loadCourseWhereReviewingActivity(reviewingActivity);
		if (reviewingActivity.getStatus() < Activity.STATUS_FINISH || isCourseInstructor(course)) {
			// check review owner
			ReviewEntry reviewEntry =  assignmentDao.loadReviewEntryWhereReview(review);
			if (reviewEntry != null && reviewEntry.getOwner().equals(getUser())) {
				review.setSaved(new Date());
				assignmentDao.save(review);
			} else {
				throw new Exception("Your session has expired. Please login again to save your review.");
			}
		} else {
			throw new Exception("The deadline has already passed.");
		}
		return CloneUtil.clone(review);
	}
	
	@Override
	public <R extends Review> R submitReview(R review) throws Exception {
		// check review deadline
		ReviewingActivity reviewingActivity = assignmentDao.loadReviewingActivityWhereReview(review);
		Course course = assignmentDao.loadCourseWhereReviewingActivity(reviewingActivity);
		if (reviewingActivity.getStatus() < Activity.STATUS_FINISH || isCourseInstructor(course)) {
			// check review owner
			ReviewEntry reviewEntry =  assignmentDao.loadReviewEntryWhereReview(review);
			if (reviewEntry != null && reviewEntry.getOwner().equals(getUser())) {
				review.setSaved(new Date());
				review.setEarlySubmitted(true);
				
				// release review
					DocEntry docEntry = reviewEntry.getDocEntry();
				//Check if the review hasn't been released early
				if (!docEntry.getReviews().contains(reviewEntry.getReview())){
					docEntry.getReviews().add(reviewEntry.getReview());
					assignmentDao.save(docEntry);
				}

				assignmentDao.save(review);

				try{
					if (docEntry.getOwner()!=null){
						emailNotifier.sendReviewEarlyFinishNotification(docEntry.getOwner(), course, reviewingActivity);					
					}else{
						if (docEntry.getOwnerGroup() != null){
							for (User user: docEntry.getOwnerGroup().getUsers()){
								emailNotifier.sendReviewEarlyFinishNotification(user, course, reviewingActivity);							
							}
						}
					}
				} catch (Exception e) {
					logger.error("Error Sending email notification. Error: "+e.getMessage()+" - ReviewingActivity: "+reviewingActivity.getName());
				}
				
			} else {
				throw new Exception("Your session has expired. Please login again to submit your review.");
			}
		} else {
			throw new Exception("The deadline has already passed.");
		}
		return CloneUtil.clone(review);
	}	
	
	@Override
	public Course getUserReviewForViewing(long reviewId) throws Exception {
		Course course = assignmentDao.loadReviewForViewing(getUser(), reviewId);
		if (course == null) {
			throw new Exception("Review not found");
		}
		
		Review review = assignmentDao.loadReview(reviewId);
		ReviewEntry entryReview = assignmentDao.loadReviewEntryWhereReview(review);
		DocEntry docEntry = assignmentDao.loadDocEntry(entryReview.getDocEntry().getDocumentId());
		
		WritingActivity writingActivity = assignmentDao.loadWritingActivityWhereDocEntry(docEntry);
		
		if (writingActivity.getTrackReviews()){
			/////// TRACKING FEEDBACK /////////////
			// fill tracking review 'read date' if user is the reviewed document owner
			// and it's the first time that views it 
			FeedbackTracking fedbackTrackingRecord = feedbackTrackingService.loadByFeedbackId(reviewId);
			   
			if(fedbackTrackingRecord !=null && fedbackTrackingRecord.getReadDate() == null){
				
				if (docEntry.getOwner()!=null){
					if (docEntry.getOwner().equals(getUser())){
						fedbackTrackingRecord.setReadDate(new Date());
						feedbackTrackingService.save(fedbackTrackingRecord);
					}
				}else{
					if (docEntry.getOwnerGroup() != null){
						if (docEntry.getOwnerGroup().getUsers().contains(getUser())){
							fedbackTrackingRecord.setReadDate(new Date());
							feedbackTrackingService.save(fedbackTrackingRecord);
						}
					}
				}			
			}
			/////// TRACKING FEEDBACK /////////////			
		}
		
		return CloneUtil.clone(course);
	}
	
	@Override	
	public Collection<DocumentType> getDocumentTypes() throws Exception {
		Collection<DocumentType> documentTypes = null;
		//if (isAdmin()) {
			documentTypes = assignmentDao.loadDocumentTypes();
		//}
		return CloneUtil.clone(documentTypes);
	}	
}
