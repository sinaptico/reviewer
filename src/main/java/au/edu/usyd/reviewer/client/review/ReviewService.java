package au.edu.usyd.reviewer.client.review;

import java.util.Collection;

import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.DocumentType;
import au.edu.usyd.reviewer.client.core.Grade;
import au.edu.usyd.reviewer.client.core.QuestionRating;
import au.edu.usyd.reviewer.client.core.Rating;
import au.edu.usyd.reviewer.client.core.Review;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("reviewService")
public interface ReviewService extends RemoteService {

	public Rating getUserRatingForEditing(Review review) throws Exception;

	public Course getUserReviewForEditing(long reviewEntryId) throws Exception;

	public Course getUserReviewForViewing(long reviewId) throws Exception;
	
	public QuestionRating getQuestionRating(String docId) throws Exception;

	public Collection<Grade> submitGrades(Collection<Grade> grades) throws Exception;

	public <R extends Rating> R submitRating(R rating, Review review) throws Exception;

	public <R extends Review> R saveReview(R review) throws Exception;
	
	public <R extends Review> R submitReview(R review) throws Exception;
	
	public Collection<DocumentType> getDocumentTypes() throws Exception;
}
