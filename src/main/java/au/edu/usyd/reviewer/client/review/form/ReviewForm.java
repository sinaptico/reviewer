package au.edu.usyd.reviewer.client.review.form;

import au.edu.usyd.reviewer.client.core.Review;

import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.user.client.ui.Composite;

public abstract class ReviewForm<E extends Review> extends Composite implements HasChangeHandlers {

	protected E review = null;
	protected boolean locked = false;

	public abstract E getReview();

	public boolean isLocked() {
		return locked;
	}

	public abstract boolean isModified();

	public abstract void setLocked(boolean locked);

	public abstract void setReview(E review);
}
