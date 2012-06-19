package au.edu.usyd.reviewer.client.core;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 * <p>Class used to save the relation between a student/tutor/lecturer with a Review template and its answers ({@link ReviewReply ReviewReplies}).</p>
 */
@Entity
public class ReviewTemplateEntry extends Entry {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The review reply. */
	@OneToOne
	protected TemplateReply reviewReply;
	
	/** The review template. */
	@ManyToOne
	protected ReviewTemplate reviewTemplate;
	
	/** The owner. */
	@ManyToOne
	protected User owner;
	
	
	/**
	 * Gets the review reply.
	 *
	 * @return the review reply
	 */
	public TemplateReply getReviewReply() {
		return reviewReply;
	}
	
	/**
	 * Sets the review reply.
	 *
	 * @param reviewReply the new review reply
	 */
	public void setReviewReply(TemplateReply reviewReply) {
		this.reviewReply = reviewReply;
	}
	
	/**
	 * Gets the review template.
	 *
	 * @return the review template
	 */
	public ReviewTemplate getReviewTemplate() {
		return reviewTemplate;
	}
	
	/**
	 * Sets the review template.
	 *
	 * @param reviewTemplate the new review template
	 */
	public void setReviewTemplate(ReviewTemplate reviewTemplate) {
		this.reviewTemplate = reviewTemplate;
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
	 * Sets the owner.
	 *
	 * @param owner the new owner
	 */
	public void setOwner(User owner) {
		this.owner = owner;
	}

	
	
	
}
