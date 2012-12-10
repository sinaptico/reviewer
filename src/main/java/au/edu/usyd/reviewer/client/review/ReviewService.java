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

/**
 * <p>The Interface ReviewService used to manage reviewing activities.</p>
 */
@RemoteServiceRelativePath("reviewService")
public interface ReviewService extends RemoteService {

	/**
	 * Gets the user rating for editing.
	 *
	 * @param review the review
	 * @return the user rating for editing
	 * @throws Exception the exception
	 */
	public Rating getUserRatingForEditing(Review review) throws Exception;

	/**
	 * Gets the user review for editing.
	 *
	 * @param reviewEntryId the review entry id
	 * @return the user review for editing
	 * @throws Exception the exception
	 */
	public Course getUserReviewForEditing(long reviewEntryId) throws Exception;

	/**
	 * Gets the user review for viewing.
	 *
	 * @param reviewId the review id
	 * @return the user review for viewing
	 * @throws Exception the exception
	 */
	public Course getUserReviewForViewing(long reviewId) throws Exception;
	
	/**
	 * Gets the question rating.
	 *
	 * @param docId the doc id
	 * @return the question rating
	 * @throws Exception the exception
	 */
	public QuestionRating getQuestionRating(String docId) throws Exception;

	/**
	 * Submit grades.
	 *
	 * @param grades the grades
	 * @return the collection
	 * @throws Exception the exception
	 */
	public Collection<Grade> submitGrades(Collection<Grade> grades) throws Exception;

	/**
	 * Submit rating.
	 *
	 * @param <R> the generic type
	 * @param rating the rating
	 * @param review the review
	 * @return the r
	 * @throws Exception the exception
	 */
	public <R extends Rating> R submitRating(R rating, Review review) throws Exception;

	/**
	 * Save review.
	 *
	 * @param <R> the generic type
	 * @param review the review
	 * @return the r
	 * @throws Exception the exception
	 */
	public <R extends Review> R saveReview(R review) throws Exception;
	
	/**
	 * Submit review.
	 *
	 * @param <R> the generic type
	 * @param review the review
	 * @return the r
	 * @throws Exception the exception
	 */
	public <R extends Review> R submitReview(R review) throws Exception;
	
	/**
	 * Gets the document types.
	 *
	 * @param genre the genre
	 * @return the document types
	 * @throws Exception the exception
	 */
	public Collection<DocumentType> getDocumentTypes(String genre) throws Exception;
	
	/**
	 * Returns the glosser url
	 * @param siteId site used in glosser
	 * @param docId doc id to access in glosser
	 * @return glosser url to access to document recieved as parameter
	 */
	public String getGlosserUrl(Long siteId, String docId);
}
