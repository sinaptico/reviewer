package au.edu.usyd.reviewer.client.review.form;

import au.edu.usyd.reviewer.client.core.Review;

import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.user.client.ui.Composite;

/**
 * Abstract Class use as a base for Review forms.
 *
 * @param <E> the element type
 */
public abstract class ReviewForm<E extends Review> extends Composite implements HasChangeHandlers {

	/** The review. */
	protected E review = null;
	
	/** The locked. */
	protected boolean locked = false;

	/**
	 * Gets the review.
	 *
	 * @return the review
	 */
	public abstract E getReview();

	/**
	 * Checks if is locked.
	 *
	 * @return true, if is locked
	 */
	public boolean isLocked() {
		return locked;
	}

	/**
	 * Checks if is modified.
	 *
	 * @return true, if is modified
	 */
	public abstract boolean isModified();

	/**
	 * Sets the locked.
	 *
	 * @param locked the new locked
	 */
	public abstract void setLocked(boolean locked);

	/**
	 * Sets the review.
	 *
	 * @param review the new review
	 */
	public abstract void setReview(E review);
}
