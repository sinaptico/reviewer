package au.edu.usyd.reviewer.client.core.gwt;

import au.edu.usyd.reviewer.client.core.Review;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * <p>Class that extends Composite class and it is used to build links to {@link au.edu.usyd.reviewer.client.core.Review Reviews}.</p>
 */
public class ReviewWidget extends Composite {

	/** The panel where the link is placed. */
	private final HorizontalPanel panel = new HorizontalPanel();
	
	/** The review. */
	private Review review;
	
	/** The document title. */
	private String title;
	
	/** Boolean that defines if the review is open in edit/view mode. */
	private boolean edit;

	/**
	 * Instantiates a new review widget.
	 *
	 * @param review the review
	 * @param title the title
	 * @param edit the edit
	 */
	public ReviewWidget(Review review, String title, boolean edit) {
		this.review = review;
		this.title = title;
		this.edit = edit;
		formatHTML();
		initWidget(panel);
	}

	/**
	 * Method that builds the link with the id and title.
	 */
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
