package au.edu.usyd.reviewer.client.review.form;

import au.edu.usyd.reviewer.client.core.Rating;

import com.google.gwt.user.client.ui.Composite;

public abstract class RatingForm<R extends Rating> extends Composite {
	protected R rating;

	public abstract R getRating();

	public abstract void setRating(R rating);
}
