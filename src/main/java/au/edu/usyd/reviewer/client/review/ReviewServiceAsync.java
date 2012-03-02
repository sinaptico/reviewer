package au.edu.usyd.reviewer.client.review;

import java.util.Collection;

import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.DocumentType;
import au.edu.usyd.reviewer.client.core.Grade;
import au.edu.usyd.reviewer.client.core.QuestionRating;
import au.edu.usyd.reviewer.client.core.Rating;
import au.edu.usyd.reviewer.client.core.Review;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ReviewServiceAsync {
	public void getUserRatingForEditing(Review review, AsyncCallback<Rating> asyncCallback);

	public void getUserReviewForEditing(long reviewEntryId, AsyncCallback<Course> asyncCallback);

	public void getUserReviewForViewing(long reviewId, AsyncCallback<Course> asyncCallback);
	
	public void getQuestionRating(String docId, AsyncCallback<QuestionRating> asyncCallback);

	public void submitGrades(Collection<Grade> grades, AsyncCallback<Collection<Grade>> asyncCallback);

	public <R extends Rating> void submitRating(R rating, Review review, AsyncCallback<R> asyncCallback);

	public <R extends Review> void saveReview(R review, AsyncCallback<R> asyncCallback);
	
	public <R extends Review> void submitReview(R review, AsyncCallback<R> asyncCallback);
	
	public void getDocumentTypes(String genre, AsyncCallback<Collection<DocumentType>> callback);
}
