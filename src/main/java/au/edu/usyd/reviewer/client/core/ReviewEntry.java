package au.edu.usyd.reviewer.client.core;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * <p>Class that extends {@link Entry Entry} class and it is used for review management, it contains
 * the owner, review ({@link Review Review}) and the link to the reviewed document (Google Doc).</>
 */
@Entity
public class ReviewEntry extends Entry {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The review. */
	@OneToOne
	protected Review review;
	
	/** The doc entry. */
	@ManyToOne
	protected DocEntry docEntry;
	
	/** The owner. */
	@ManyToOne
	protected User owner;

	/**
	 * Gets the doc entry.
	 *
	 * @return the doc entry
	 */
	public DocEntry getDocEntry() {
		return docEntry;
	}
	
	/**
	 * Gets the owner.
	 *
	 * @return the owner
	 */
	public User getOwner() {
		return owner;
	}
	
	/**
	 * Gets the review.
	 *
	 * @return the review
	 */
	public Review getReview() {
		return review;
	}
	
	/**
	 * Sets the doc entry.
	 *
	 * @param docEntry the new doc entry
	 */
	public void setDocEntry(DocEntry docEntry) {
		this.docEntry = docEntry;
	}

	/**
	 * Sets the owner.
	 *
	 * @param owner the new owner
	 */
	public void setOwner(User owner) {
		this.owner = owner;
	}

	/**
	 * Sets the review.
	 *
	 * @param review the new review
	 */
	public void setReview(Review review) {
		this.review = review;
	}
}
