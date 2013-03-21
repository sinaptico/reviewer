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
import au.edu.usyd.reviewer.client.core.util.Constants;
import au.edu.usyd.reviewer.client.core.util.exception.MessageException;
import au.edu.usyd.reviewer.client.review.ReviewService;
import au.edu.usyd.reviewer.server.QuestionDao;
import au.edu.usyd.reviewer.server.Reviewer;

public class ReviewServiceImpl extends ReviewerServiceImpl implements ReviewService {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public Rating getUserRatingForEditing(Review review) throws Exception {
		initialize();
		Rating rating = assignmentDao.loadUserRatingForEditing(getMockedUser(), review);
		if (rating == null) {
			throw new MessageException(Constants.EXCEPTION_RATING_NOT_FOUND);
		}
		return rating;
	}

	@Override
	public Course getUserReviewForEditing(long reviewId) throws Exception {
		initialize();
		Course course = assignmentDao.loadUserReviewForEditing(getMockedUser(), reviewId);
		
		if (course == null) {
			throw new MessageException(Constants.EXCEPTION_REVIEW_NOT_FOUND);
		}
		return course;
	}

	@Override
	public QuestionRating getQuestionRating(String docId) throws Exception {
		initialize();
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
			return review;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}



	@Override
	public Collection<Grade> submitGrades(Collection<Grade> grades) throws Exception {
		initialize();
		for(Grade grade : grades) {
			Deadline deadline = grade.getDeadline();
			Course course = assignmentDao.loadCourseWhereDeadline(deadline);
			if (isCourseInstructor(course)) {
				User userGrade = grade.getUser();
				Grade currentGrade = assignmentDao.loadGrade(deadline, userGrade);
				if(currentGrade != null) {
					currentGrade.setValue(grade.getValue());
				} else {
					currentGrade = grade;
				}
				currentGrade = assignmentDao.save(currentGrade);
				WritingActivity writingActivity = assignmentDao.loadWritingActivityWhereDeadline(deadline);
				writingActivity.getGrades().add(currentGrade);
				writingActivity = assignmentDao.save(writingActivity);
			} else {
				throw new MessageException(Constants.EXCEPTION_PERMISSION_DENIED);
			}
		}
		return grades;
	}

	@Override
	public <R extends Rating> R submitRating(R rating, Review review) throws Exception {
		initialize();
		User loggedUser = getMockedUser();
		// check permission
		Rating userRating = assignmentDao.loadRating(rating.getId());
		if (userRating != null) {
			if (userRating.getOwner().equals(loggedUser)) {
				rating.setId(userRating.getId());
			} else {
				throw new MessageException(Constants.EXCEPTION_SESSION_EXPIRED_SUBMIT_RATING);
			}
		}

		// save rating
		if (rating instanceof QuestionRating) {
			if (((QuestionRating) rating).getEvaluatorBackground().equals("Yes")) {
				loggedUser.setNativeSpeaker("Yes");
			} else {
				loggedUser.setNativeSpeaker("No");
			}

			QuestionDao questionDao = new QuestionDao(Reviewer.getHibernateSessionFactory());
			for (QuestionScore questionScore : ((QuestionRating) rating).getScores()) {
				questionDao.saveScoreAndDocOwner(questionScore, loggedUser);
			}
		} else {
			ReviewEntry reviewEntry = assignmentDao.loadReviewEntryWhereReview(review);
			rating.setEntry(reviewEntry);
			rating.setOwner(loggedUser);
			rating = assignmentDao.save(rating);
		}

		return rating;
	}

	@Override
	public <R extends Review> R saveReview(R review) throws Exception {
		initialize();
		// check review deadline
		ReviewingActivity reviewingActivity = assignmentDao.loadReviewingActivityWhereReview(review);
		Course course = assignmentDao.loadCourseWhereReviewingActivity(reviewingActivity);
		if (reviewingActivity.getStatus() < Activity.STATUS_FINISH || isCourseInstructor(course)) {
			// check review owner
			ReviewEntry reviewEntry =  assignmentDao.loadReviewEntryWhereReview(review);
			if (reviewEntry != null && reviewEntry.getOwner().equals(getMockedUser())) {
				review.setSaved(new Date());
				review = assignmentDao.save(review);
			} else {
				throw new MessageException(Constants.EXCEPTION_SESSION_EXPIRED_SAVE_REVIEW);
			}
		} else {
			throw new MessageException(Constants.EXCEPTION_DEADLINE_ALREADY_PASSED);
		}
		R newReview = (R) new Review();
		if (review != null){
			newReview = (R) review.clone();
		}
		return newReview;
	}
	
	@Override
	public <R extends Review> R submitReview(R review) throws Exception {
		initialize();
		// check review deadline
		ReviewingActivity reviewingActivity = assignmentDao.loadReviewingActivityWhereReview(review);
		Course course = assignmentDao.loadCourseWhereReviewingActivity(reviewingActivity);
		if (reviewingActivity.getStatus() < Activity.STATUS_FINISH || isCourseInstructor(course)) {
			// check review owner
			ReviewEntry reviewEntry =  assignmentDao.loadReviewEntryWhereReview(review);
			if (reviewEntry != null && reviewEntry.getOwner().equals(getMockedUser())) {
				review.setSaved(new Date());
				review.setEarlySubmitted(true);
				// release review
					DocEntry docEntry = reviewEntry.getDocEntry();
				//Check if the review hasn't been released early
				if (!docEntry.getReviews().contains(reviewEntry.getReview())){
					docEntry.getReviews().add(reviewEntry.getReview());
					docEntry = assignmentDao.save(docEntry);
					
				}
				review = assignmentDao.save(review);
				try{
					emailNotifier = Reviewer.getEmailNotifier();
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
					e.printStackTrace();
					logger.error("Error Sending email notification. Error: "+e.getMessage()+" - ReviewingActivity: "+reviewingActivity.getName());
				}
				
			} else {
				throw new MessageException(Constants.EXCEPTION_SESSION_EXPIRED_SUBMIT_REVIEW);
			}
		} else {
			throw new MessageException(Constants.EXCEPTION_DEADLINE_ALREADY_PASSED);
		}
		R newReview = (R) new Review();
		if (review != null){
			newReview = (R)(review.clone());
		}
		return newReview;
	}	
	
	@Override
	public Course getUserReviewForViewing(long reviewId) throws Exception {
		initialize();
		Course course = assignmentDao.loadReviewForViewing(getMockedUser(), reviewId);
		if (course == null) {
			throw new MessageException(Constants.EXCEPTION_REVIEW_NOT_FOUND);
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
					if (docEntry.getOwner().equals(getMockedUser())){
						fedbackTrackingRecord.setReadDate(new Date());
						feedbackTrackingService.save(fedbackTrackingRecord);
					}
				}else{
					if (docEntry.getOwnerGroup() != null){
						if (docEntry.getOwnerGroup().getUsers().contains(getMockedUser())){
							fedbackTrackingRecord.setReadDate(new Date());
							feedbackTrackingService.save(fedbackTrackingRecord);
						}
					}
				}			
			}
			/////// TRACKING FEEDBACK /////////////			
		}
		
		return course;
	}
	
	@Override	
	public Collection<DocumentType> getDocumentTypes(String genre) throws Exception {
		initialize();
		Collection<DocumentType> documentTypes = null;
		//if (isAdmin()) {
			documentTypes = assignmentDao.loadDocumentTypes(genre);
		//}
		return documentTypes;
	}
		
	public String getGlosserUrl(Long siteId, String docId) {
		return Reviewer.getGlosserUrl(siteId, docId);
	}

}
