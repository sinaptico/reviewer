package au.edu.usyd.reviewer.client.core.gwt;

import au.edu.usyd.reviewer.client.core.Review;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class ReviewWidget extends Composite {

	private final HorizontalPanel panel = new HorizontalPanel();
	private Review review;
	private String title;
	private boolean edit;

	public ReviewWidget(Review review, String title, boolean edit) {
		this.review = review;
		this.title = title;
		this.edit = edit;
		formatHTML();
		initWidget(panel);
	}

	private void formatHTML() {
		Anchor link = new Anchor();
		link.setHref("Review.html?"+(edit?"edit":"view")+"="+review.getId());
		link.setHTML("<img height='19px' src='images/review.png'></img><span>" + title + "</span>");
		//link.setTarget("_blank");
		link.setTitle("Open Review");
		
		panel.clear();
		panel.add(link);
	}
}
