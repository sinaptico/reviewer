package au.edu.usyd.reviewer.client.review;

import java.util.Collection;

import au.edu.usyd.reviewer.client.core.Course;
import au.edu.usyd.reviewer.client.core.DocumentType;
import au.edu.usyd.reviewer.client.core.Grade;
import au.edu.usyd.reviewer.client.core.QuestionRating;
import au.edu.usyd.reviewer.client.core.Rating;
import au.edu.usyd.reviewer.client.core.Review;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * <p>The Interface (Asynchronous) ReviewService used to manage reviewing activities.</p>
 */
public interface ReviewServiceAsync {
	
	/**
	 * Gets the user rating for editing.
	 *
	 * @param review the review
	 * @param asyncCallback the async callback	 * 
	 */
	public void getUserRatingForEditing(Review review, AsyncCallback<Rating> asyncCallback);

	/**
	 * Gets the user review for editing.
	 *
	 * @param reviewEntryId the review entry id
	 * @param asyncCallback the async callback	 * 
	 */
	public void getUserReviewForEditing(long reviewEntryId, AsyncCallback<Course> asyncCallback);

	/**
	 * Gets the user review for viewing.
	 *
	 * @param reviewId the review id
	 * @param asyncCallback the async callback	 * 
	 */
	public void getUserReviewForViewing(long reviewId, AsyncCallback<Course> asyncCallback);
	
	/**
	 * Gets the question rating.
	 *
	 * @param docId the doc id
	 * @param asyncCallback the async callback	 * 
	 */
	public void getQuestionRating(String docId, AsyncCallback<QuestionRating> asyncCallback);

	/**
	 * Submit grades.
	 *
	 * @param grades the grades
	 * @param asyncCallback the async callback
	 */
	public void submitGrades(Collection<Grade> grades, AsyncCallback<Collection<Grade>> asyncCallback);

	/**
	 * Submit rating.
	 *
	 * @param <R> the generic type
	 * @param rating the rating
	 * @param review the review
	 * @param asyncCallback the async callback
	 */
	public <R extends Rating> void submitRating(R rating, Review review, AsyncCallback<R> asyncCallback);

	/**
	 * Save review.
	 *
	 * @param <R> the generic type
	 * @param review the review
	 * @param asyncCallback the async callback
	 */
	public <R extends Review> void saveReview(R review, AsyncCallback<R> asyncCallback);
	
	/**
	 * Submit review.
	 *
	 * @param <R> the generic type
	 * @param review the review
	 * @param asyncCallback the async callback
	 */
	public <R extends Review> void submitReview(R review, AsyncCallback<R> asyncCallback);
	
	/**
	 * Gets the document types.
	 *
	 * @param genre the genre
	 * @param callback the callback	 * 
	 */
	public void getDocumentTypes(String genre, AsyncCallback<Collection<DocumentType>> callback);
}
