package au.edu.usyd.reviewer.client.review.form;

import au.edu.usyd.reviewer.client.core.Rating;

import com.google.gwt.user.client.ui.Composite;

/**
 * Abstract Class use as a base for Ratings forms.
 *
 * @param <R> the generic type
 */
public abstract class RatingForm<R extends Rating> extends Composite {
	
	/** The rating. */
	protected R rating;

	/**
	 * Gets the rating.
	 *
	 * @return the rating
	 */
	public abstract R getRating();

	/**
	 * Sets the rating.
	 *
	 * @param rating the new rating
	 */
	public abstract void setRating(R rating);
}
